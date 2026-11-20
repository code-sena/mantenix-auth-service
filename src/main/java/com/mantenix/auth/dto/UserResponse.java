package com.mantenix.auth.dto;

import com.mantenix.auth.model.Role;
import com.mantenix.auth.model.RoleName;
import com.mantenix.auth.model.User;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

/** Data the API returns. Never includes the password hash or other security fields. */
public record UserResponse(
        UUID id,
        String email,
        String name,
        boolean emailVerified,
        List<RoleName> roles,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {

    public static UserResponse from(User user) {
        List<RoleName> roles = user.getRoles().stream()
                .map(Role::getName)
                .sorted()
                .toList();
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.isEmailVerified(),
                roles,
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}
