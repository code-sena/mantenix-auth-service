package com.mantenix.auth.controller;

import com.mantenix.auth.dto.CreateUserRequest;
import com.mantenix.auth.dto.UpdateRolesRequest;
import com.mantenix.auth.dto.UpdateUserRequest;
import com.mantenix.auth.dto.UserResponse;
import com.mantenix.auth.model.RoleName;
import com.mantenix.auth.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/** REST endpoints for users. Receives and returns DTOs only. */
@RestController
@RequestMapping("/users")
@Tag(name = "Users", description = "User accounts and their roles (access control)")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a user", description = "The password is stored as a bcrypt hash. New users get the USER role")
    public UserResponse create(@Valid @RequestBody CreateUserRequest request) {
        return userService.create(request);
    }

    @GetMapping
    @Operation(summary = "List active users", description = "Optionally filter by role")
    public List<UserResponse> findAll(@RequestParam(required = false) RoleName role) {
        return userService.findAll(role);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a user by id")
    public UserResponse findById(@PathVariable UUID id) {
        return userService.findById(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a user's email and name")
    public UserResponse update(@PathVariable UUID id, @Valid @RequestBody UpdateUserRequest request) {
        return userService.update(id, request);
    }

    @PutMapping("/{id}/roles")
    @Operation(summary = "Replace the roles of a user", description = "Sends the complete list of roles the user must have")
    public UserResponse updateRoles(@PathVariable UUID id, @Valid @RequestBody UpdateRolesRequest request) {
        return userService.updateRoles(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a user", description = "Soft delete: the record is kept with deleted_at filled in")
    public void delete(@PathVariable UUID id) {
        userService.delete(id);
    }
}
