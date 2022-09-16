package com.example.filter;

import io.smallrye.mutiny.Uni;
import org.jboss.resteasy.reactive.server.ServerRequestFilter;
import org.jboss.resteasy.reactive.server.ServerResponseFilter;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;


public class Filters {
    @ServerRequestFilter
    public void preFilter(ContainerRequestContext context){
        System.out.println("preFilter");
        context.setProperty("pro","666");
    }
    @ServerRequestFilter
    public Uni<Response> preFilter2(ContainerRequestContext context){
        System.out.println("preFilter2");
       return Uni.createFrom().item(Response.ok("123").build());
    }
    @ServerResponseFilter
    public void afterFilter(ContainerResponseContext context){
       context.setEntity("q213");
    }
}
