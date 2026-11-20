package com.mantenix.auth.service;

import com.mantenix.auth.dto.CreateUserRequest;
import com.mantenix.auth.dto.RoleResponse;
import com.mantenix.auth.dto.UpdateRolesRequest;
import com.mantenix.auth.dto.UpdateUserRequest;
import com.mantenix.auth.dto.UserResponse;
import com.mantenix.auth.exception.BusinessRuleException;
import com.mantenix.auth.exception.ResourceNotFoundException;
import com.mantenix.auth.model.Role;
import com.mantenix.auth.model.RoleName;
import com.mantenix.auth.model.User;
import com.mantenix.auth.repository.RoleRepository;
import com.mantenix.auth.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

/** Business logic for users and roles. Controllers only call this class; they never touch the repositories. */
@Service
public class UserService {

    /** Role given to every new user: a vehicle owner. */
    static final RoleName DEFAULT_ROLE = RoleName.USER;

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UserResponse create(CreateUserRequest request) {
        String email = normalizeEmail(request.email());
        if (userRepository.existsByEmail(email)) {
            throw new BusinessRuleException("EMAIL_ALREADY_EXISTS", "The email is already registered");
        }

        User user = new User();
        user.setEmail(email);
        user.setName(request.name().trim());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setRoles(new HashSet<>(Set.of(getRole(DEFAULT_ROLE))));

        return UserResponse.from(userRepository.save(user));
    }

    @Transactional(readOnly = true)
    public List<UserResponse> findAll(RoleName role) {
        List<User> users = (role == null)
                ? userRepository.findAllByDeletedAtIsNullOrderByCreatedAtAsc()
                : userRepository.findByRoles_NameAndDeletedAtIsNullOrderByCreatedAtAsc(role);
        return users.stream().map(UserResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public UserResponse findById(UUID id) {
        return UserResponse.from(getUser(id));
    }

    @Transactional
    public UserResponse update(UUID id, UpdateUserRequest request) {
        User user = getUser(id);
        String email = normalizeEmail(request.email());
        if (userRepository.existsByEmailAndIdNot(email, id)) {
            throw new BusinessRuleException("EMAIL_ALREADY_EXISTS", "The email is already registered");
        }
        user.setEmail(email);
        user.setName(request.name().trim());
        return UserResponse.from(userRepository.saveAndFlush(user));
    }

    /** Replaces all the roles of the user with the given ones. */
    @Transactional
    public UserResponse updateRoles(UUID id, UpdateRolesRequest request) {
        User user = getUser(id);
        List<Role> roles = roleRepository.findByNameIn(request.roles());
        if (roles.size() != request.roles().size()) {
            throw new IllegalStateException("Roles missing in the database; check migration V2");
        }
        user.setRoles(new HashSet<>(roles));
        return UserResponse.from(userRepository.saveAndFlush(user));
    }

    /** Soft delete (06-data/models.md): the row stays, with deleted_at filled in. */
    @Transactional
    public void delete(UUID id) {
        User user = getUser(id);
        user.setDeletedAt(OffsetDateTime.now(ZoneOffset.UTC));
        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public List<RoleResponse> findAllRoles() {
        return roleRepository.findAllByOrderByNameAsc().stream().map(RoleResponse::from).toList();
    }

    private User getUser(UUID id) {
        return userRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("User " + id + " not found"));
    }

    private Role getRole(RoleName name) {
        return roleRepository.findByName(name)
                .orElseThrow(() -> new IllegalStateException("Role " + name + " missing in the database; check migration V2"));
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
