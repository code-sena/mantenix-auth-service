package com.mantenix.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/** Data to create a user. Same rules as RegisterRequest in 07-api/contracts/openapi/auth-service.yaml. */
public record CreateUserRequest(

        @Schema(example = "owner@example.com")
        @NotBlank
        @Email
        @Size(max = 255)
        String email,

        @Schema(description = "At least 8 characters, one uppercase letter and one number", example = "SecurePass123")
        @NotBlank
        @Size(min = 8, max = 100)
        @Pattern(regexp = "^(?=.*[A-Z])(?=.*\\d).+$",
                message = "must contain at least one uppercase letter and one number")
        String password,

        @Schema(example = "Jane Doe")
        @NotBlank
        @Size(max = 100)
        String name
) {

    /** Never print the password in logs. */
    @Override
    public String toString() {
        return "CreateUserRequest[email=" + email + ", name=" + name + ", password=****]";
    }
}
