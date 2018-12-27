package com.talgat;

public class Generic {
    A<Integer> a = new A<>();
}

class A<T extends Number> {

}

class B { }
class C extends B {}
