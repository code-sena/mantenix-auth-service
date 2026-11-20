package com.mantenix.auth.dto;

import com.mantenix.auth.model.RoleName;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.Set;

/** The complete set of roles the user must have (it replaces the current roles). */
public record UpdateRolesRequest(

        @ArraySchema(schema = @Schema(implementation = RoleName.class), arraySchema = @Schema(example = "[\"USER\", \"MECHANIC\"]"))
        @NotEmpty
        Set<@NotNull RoleName> roles
) {
}
