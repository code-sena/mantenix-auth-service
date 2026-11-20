package com.mantenix.auth.dto;

import com.mantenix.auth.model.Role;
import com.mantenix.auth.model.RoleName;

public record RoleResponse(RoleName name, String description) {

    public static RoleResponse from(Role role) {
        return new RoleResponse(role.getName(), role.getDescription());
    }
}
