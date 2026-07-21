package io.talgat;

import java.io.Serializable;

public class TransactionEvent implements Serializable {

    public String transactionId;
    public TransactionStatus status;
    public long eventTime;

    public TransactionEvent() {
    }

    public TransactionEvent(
            String transactionId,
            TransactionStatus status,
            long eventTime
    ) {
        this.transactionId = transactionId;
        this.status = status;
        this.eventTime = eventTime;
    }

    @Override
    public String toString() {
        return "TransactionEvent{" +
                "transactionId='" + transactionId + '\'' +
                ", status=" + status +
                ", eventTime=" + Util.formatTime(eventTime) +
                '}';
    }
}
