package com.mantenix.auth.repository;

import com.mantenix.auth.model.Role;
import com.mantenix.auth.model.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RoleRepository extends JpaRepository<Role, UUID> {

    Optional<Role> findByName(RoleName name);

    List<Role> findByNameIn(Collection<RoleName> names);

    List<Role> findAllByOrderByNameAsc();
}
