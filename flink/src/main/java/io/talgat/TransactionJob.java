package io.talgat;

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.OpenContext;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.util.Collector;

import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static io.talgat.Util.formatTime;
import static io.talgat.Util.formatWatermark;

public class TransactionJob {

    private static final long CREATED_WAIT_TIMEOUT_MS =
            Duration.ofMinutes(5).toMillis();

    public static void main(String[] args) throws Exception {

        StreamExecutionEnvironment env =
                StreamExecutionEnvironment.getExecutionEnvironment();

        env.setParallelism(3);

        DataStream<TransactionEvent> source = env.fromData(
                event("tx-1", TransactionStatus.CANCELLED, "2026-07-17T10:04:00"),
                event("tx-2", TransactionStatus.CREATED,   "2026-07-17T10:02:00"),
                event("tx-2", TransactionStatus.COMPLETED, "2026-07-17T10:03:00"),
                event("tx-1", TransactionStatus.CREATED,   "2026-07-17T10:01:00"),
                event("tx-3", TransactionStatus.CANCELLED, "2026-07-17T10:04:30"),
                event("tx-4", TransactionStatus.CREATED,   "2026-07-17T10:10:00")
        );

        WatermarkStrategy<TransactionEvent> watermarkStrategy =
                WatermarkStrategy.<TransactionEvent>forBoundedOutOfOrderness(
                                Duration.ofSeconds(30)
                        ).withTimestampAssigner(
                                (transactionEvent, previousTimestamp) ->
                                        transactionEvent.eventTime
                        );

        DataStream<TransactionEvent> withWatermarks =
                source.assignTimestampsAndWatermarks(watermarkStrategy);

        DataStream<String> results = withWatermarks
                .keyBy(event -> event.transactionId)
                .process(new TransactionProcessFunction())
                .name("transaction-state-machine")
                .setParallelism(3);

        results.print();

        env.execute("Out-of-order transaction processing");
    }


    private static TransactionEvent event(String transactionId, TransactionStatus status, String eventTime) {
        long timestamp = LocalDateTime
                .parse(eventTime)
                .toInstant(ZoneOffset.UTC)
                .toEpochMilli();

        return new TransactionEvent(transactionId, status, timestamp);
    }

    public static class TransactionProcessFunction extends KeyedProcessFunction<String, TransactionEvent, String> {

        private transient ValueState<TransactionEvent> createdState;
        private transient ValueState<TransactionEvent> terminalState;
        private transient ValueState<Long> timerState;

        @Override
        public void open(OpenContext openContext) throws Exception {

            ValueStateDescriptor<TransactionEvent> createdDescriptor =
                    new ValueStateDescriptor<>(
                            "created-event",
                            TransactionEvent.class
                    );

            ValueStateDescriptor<TransactionEvent> terminalDescriptor =
                    new ValueStateDescriptor<>(
                            "terminal-event",
                            TransactionEvent.class
                    );

            ValueStateDescriptor<Long> timerDescriptor =
                    new ValueStateDescriptor<>(
                            "waiting-timer",
                            Long.class
                    );

            createdState = getRuntimeContext().getState(createdDescriptor);

            terminalState = getRuntimeContext().getState(terminalDescriptor);

            timerState = getRuntimeContext().getState(timerDescriptor);
        }

        @Override
        public void processElement(TransactionEvent event, Context context, Collector<String> output) throws Exception {

            int subtaskIndex = getRuntimeContext().getTaskInfo().getIndexOfThisSubtask();

            output.collect(
                    "RECEIVED"
                            + " | subtask=" + subtaskIndex
                            + " | key=" + context.getCurrentKey()
                            + " | status=" + event.status
                            + " | eventTime=" + formatTime(event.eventTime)
                            + " | watermark="
                            + formatWatermark(
                            context.timerService().currentWatermark()
                    )
            );

            switch (event.status) {
                case CREATED -> handleCreated(event, context, output);
                case COMPLETED, CANCELLED -> handleTerminal(event, context, output);
            }
        }

        private void handleCreated(TransactionEvent created, Context context, Collector<String> output) throws Exception {

            createdState.update(created);

            TransactionEvent terminal = terminalState.value();

            if (terminal != null) {
                emitCompletedTransaction(created, terminal, output);

                deleteTimer(context);

                clearAllState();
            } else {
                output.collect(
                        "WAITING"
                                + " | transaction=" + created.transactionId
                                + " | CREATED got"
                                + " | wait for COMPLETED or CANCELLED"
                );
            }
        }

        private void handleTerminal(TransactionEvent terminal, Context context, Collector<String> output) throws Exception {

            TransactionEvent created = createdState.value();

            if (created != null) {
                emitCompletedTransaction(created, terminal, output);
                deleteTimer(context);
                clearAllState();
            } else {
                terminalState.update(terminal);
                long timerTimestamp = terminal.eventTime + CREATED_WAIT_TIMEOUT_MS;

                Long oldTimerTimestamp = timerState.value();

                if (oldTimerTimestamp != null) {
                    context.timerService()
                            .deleteEventTimeTimer(oldTimerTimestamp);
                }

                context.timerService()
                        .registerEventTimeTimer(timerTimestamp);

                timerState.update(timerTimestamp);

                output.collect(
                        "WAITING"
                                + " | transaction=" + terminal.transactionId
                                + " | " + terminal.status
                                + " appear before CREATED"
                                + " | wait for CREATED until eventTime="
                                + formatTime(timerTimestamp)
                );
            }
        }

        @Override
        public void onTimer(long timestamp, OnTimerContext context, Collector<String> output) throws Exception {
            TransactionEvent created = createdState.value();
            TransactionEvent terminal = terminalState.value();
            Long expectedTimer = timerState.value();

            if (expectedTimer == null || expectedTimer != timestamp) {
                return;
            }

            if (created == null && terminal != null) {
                output.collect(
                        "TIMEOUT"
                                + " | transaction="
                                + context.getCurrentKey()
                                + " | get=" + terminal.status
                                + " | CREATED not appear"
                                + " | timer=" + formatTime(timestamp)
                                + " | watermark reach timer timestamp"
                );
            }

            clearAllState();
        }

        private void emitCompletedTransaction(TransactionEvent created, TransactionEvent terminal, Collector<String> output) {

            output.collect(
                    "RESULT"
                            + " | transaction=" + created.transactionId
                            + " | CREATED=" + formatTime(created.eventTime)
                            + " | finalStatus=" + terminal.status
                            + " | finalTime=" + formatTime(terminal.eventTime)
            );
        }


        private void deleteTimer(Context context) throws Exception {
            Long timerTimestamp = timerState.value();

            if (timerTimestamp != null) {
                context.timerService()
                        .deleteEventTimeTimer(timerTimestamp);

                timerState.clear();
            }
        }

        private void clearAllState() throws Exception {
            createdState.clear();
            terminalState.clear();
            timerState.clear();
        }
    }
}
