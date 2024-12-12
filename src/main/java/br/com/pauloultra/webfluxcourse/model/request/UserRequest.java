package br.com.pauloultra.webfluxcourse.model.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserRequest(
        @Size(min = 3, max = 50, message = "must be between and 50 characters")
        @NotBlank(message = "must not be null or empty")
        String name,

        @Email(message = "invalid email")
        String email,

        @Size(min = 3, max = 20, message = "must be between and 20 characters")
        @NotBlank(message = "must not be null or empty")
        String password) {}
