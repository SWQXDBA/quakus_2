package com.example;

import com.example.ci.Usage1;
import com.example.ci.Usage2;
import org.eclipse.microprofile.config.inject.ConfigProperties;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Optional;

@Path("/greeting")
public class ExampleResource {

    @ConfigProperty(name = "greeting.message")
    String message;

    @ConfigProperty(name = "greeting.suffix", defaultValue="!")
    String suffix;

    @ConfigProperty(name = "greeting.name", defaultValue="!")
    Optional<String> name;

    @Inject
    Usage1 usage1;
    @Inject
    Usage2 usage2;
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        usage1.getComponent1().doSomething();
        usage2.getComponent1().doSomething();
          return message + " " + name.orElse("world") + suffix;
    }
}