package com.example.json_rest_service;

import io.smallrye.mutiny.Uni;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.HashSet;
import java.util.Set;

@ApplicationScoped
@Path("/json")
public class JsonController {

    Set<Fruit> fruits = new HashSet<>();

    public JsonController() {
        fruits.add(new Fruit("苹果"));
    }


    @PUT
    @Path("/{name}")
    public Response add(@PathParam("name") String name){
        fruits.add(new Fruit(name));
        return Response.ok("ok").build();
    }
    @GET
    @Path("/{name}")
    public Response get(@PathParam("name") String name){
        final Fruit fruit = fruits.stream().filter(f -> f.name.equals(name)).findFirst().get();
        return Response.ok(fruit).build();
    }
    @GET
    @Path("/2/{name}")
    public Fruit get2(@PathParam("name") String name){
        return fruits.stream().filter(f -> f.name.equals(name)).findFirst().get();
    }

    @GET
    @Path("/3/{name}")
    public Uni<Fruit> get3(@PathParam("name") String name){
        return Uni.createFrom().item(fruits.stream().filter(f -> f.name.equals(name)).findFirst().get());

    }
}
