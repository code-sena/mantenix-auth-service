package com.mantenix.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Data to update a user's profile. Password and roles have their own operations. */
public record UpdateUserRequest(

        @Schema(example = "owner@example.com")
        @NotBlank
        @Email
        @Size(max = 255)
        String email,

        @Schema(example = "Jane Doe")
        @NotBlank
        @Size(max = 100)
        String name
) {
}
