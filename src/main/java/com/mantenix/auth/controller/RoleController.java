package com.mantenix.auth.controller;

import com.mantenix.auth.dto.RoleResponse;
import com.mantenix.auth.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** Read-only catalog of roles. The roles are created by the Flyway migration V2. */
@RestController
@RequestMapping("/roles")
@Tag(name = "Roles", description = "Catalog of system roles")
public class RoleController {

    private final UserService userService;

    public RoleController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @Operation(summary = "List the system roles")
    public List<RoleResponse> findAll() {
        return userService.findAllRoles();
    }
}
