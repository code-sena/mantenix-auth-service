package com.mantenix.auth.service;

import com.mantenix.auth.dto.CreateUserRequest;
import com.mantenix.auth.dto.UpdateRolesRequest;
import com.mantenix.auth.dto.UserResponse;
import com.mantenix.auth.exception.BusinessRuleException;
import com.mantenix.auth.exception.ResourceNotFoundException;
import com.mantenix.auth.model.Role;
import com.mantenix.auth.model.RoleName;
import com.mantenix.auth.model.User;
import com.mantenix.auth.repository.RoleRepository;
import com.mantenix.auth.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** Unit tests for the business rules of UserService (no database needed). */
class UserServiceTest {

    private static final Role USER_ROLE = new Role(UUID.randomUUID(), RoleName.USER, "Vehicle owner");
    private static final Role MECHANIC_ROLE = new Role(UUID.randomUUID(), RoleName.MECHANIC, "Repair shop");

    // Cost 4 only to keep the test fast; the application uses 12 (PasswordConfig).
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(4);

    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private UserService service;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        roleRepository = mock(RoleRepository.class);
        service = new UserService(userRepository, roleRepository, passwordEncoder);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(userRepository.saveAndFlush(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(roleRepository.findByName(RoleName.USER)).thenReturn(Optional.of(USER_ROLE));
    }

    @Test
    void createHashesThePasswordNormalizesTheEmailAndAssignsTheUserRole() {
        when(userRepository.existsByEmail("owner@example.com")).thenReturn(false);

        UserResponse response = service.create(new CreateUserRequest(" Owner@Example.com ", "SecurePass123", "Jane Doe"));

        ArgumentCaptor<User> saved = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(saved.capture());
        assertThat(saved.getValue().getPasswordHash()).isNotEqualTo("SecurePass123");
        assertThat(passwordEncoder.matches("SecurePass123", saved.getValue().getPasswordHash())).isTrue();
        assertThat(response.email()).isEqualTo("owner@example.com");
        assertThat(response.roles()).containsExactly(RoleName.USER);
    }

    @Test
    void createRejectsAnEmailThatIsAlreadyRegistered() {
        when(userRepository.existsByEmail("owner@example.com")).thenReturn(true);

        assertThatThrownBy(() -> service.create(new CreateUserRequest("owner@example.com", "SecurePass123", "Jane")))
                .isInstanceOf(BusinessRuleException.class)
                .extracting("code").isEqualTo("EMAIL_ALREADY_EXISTS");
        verify(userRepository, never()).save(any());
    }

    @Test
    void updateRolesReplacesTheCurrentRoles() {
        UUID id = UUID.randomUUID();
        User user = new User();
        user.setId(id);
        user.setRoles(new java.util.HashSet<>(Set.of(USER_ROLE)));
        when(userRepository.findByIdAndDeletedAtIsNull(id)).thenReturn(Optional.of(user));
        when(roleRepository.findByNameIn(Set.of(RoleName.MECHANIC))).thenReturn(List.of(MECHANIC_ROLE));

        UserResponse response = service.updateRoles(id, new UpdateRolesRequest(Set.of(RoleName.MECHANIC)));

        assertThat(response.roles()).containsExactly(RoleName.MECHANIC);
    }

    @Test
    void deleteIsASoftDelete() {
        UUID id = UUID.randomUUID();
        User user = new User();
        user.setId(id);
        when(userRepository.findByIdAndDeletedAtIsNull(id)).thenReturn(Optional.of(user));

        service.delete(id);

        assertThat(user.getDeletedAt()).isNotNull();
        verify(userRepository, never()).delete(any());
    }

    @Test
    void findByIdFailsWhenTheUserDoesNotExistOrWasDeleted() {
        UUID id = UUID.randomUUID();
        when(userRepository.findByIdAndDeletedAtIsNull(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(id))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
