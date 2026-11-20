package com.mantenix.auth.repository;

import com.mantenix.auth.model.RoleName;
import com.mantenix.auth.model.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** Every query ignores soft-deleted users (deleted_at IS NOT NULL). */
public interface UserRepository extends JpaRepository<User, UUID> {

    @EntityGraph(attributePaths = "roles")
    Optional<User> findByIdAndDeletedAtIsNull(UUID id);

    @EntityGraph(attributePaths = "roles")
    List<User> findAllByDeletedAtIsNullOrderByCreatedAtAsc();

    @EntityGraph(attributePaths = "roles")
    List<User> findByRoles_NameAndDeletedAtIsNullOrderByCreatedAtAsc(RoleName role);

    /** Includes deleted users: the email column is UNIQUE in the database. */
    boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(String email, UUID id);
}
