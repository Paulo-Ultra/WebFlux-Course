package br.com.pauloultra.webfluxcourse.model.request;

public record UserRequest(
        String name,
        String email,
        String password) {}
