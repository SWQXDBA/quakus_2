package com.example.ci;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class Usage1 {
    @Inject
    Component1 component1;

    public Component1 getComponent1() {
        return component1;
    }
}
