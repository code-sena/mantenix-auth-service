package com.mantenix.auth.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * A system role. The rows are created by Flyway (V2__insert_default_roles.sql);
 * the API only reads them.
 */
@Entity
@Table(name = "roles")
public class Role {

    @Id
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true, length = 50)
    private RoleName name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "created_at", nullable = false, updatable = false, insertable = false)
    private OffsetDateTime createdAt;

    protected Role() {
    }

    public Role(UUID id, RoleName name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public UUID getId() { return id; }
    public RoleName getName() { return name; }
    public String getDescription() { return description; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
}
