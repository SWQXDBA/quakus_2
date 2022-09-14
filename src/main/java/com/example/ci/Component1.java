package com.example.ci;

import javax.enterprise.context.Dependent;

@Dependent
public class Component1 {
    public Component1() {

    }
    public void doSomething(){
        System.out.println("doSomething"+this);
    }
}
