package com.example.json_rest_service;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class Fruit {
   public String name;

    public Fruit() {
    }

    public Fruit(String name) {
        this.name = name;
    }
}
