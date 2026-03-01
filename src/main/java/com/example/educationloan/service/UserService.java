package com.example.educationloan.service;

import com.example.educationloan.entity.Role;
import com.example.educationloan.entity.User;
import com.example.educationloan.entity.UserRole;
import com.example.educationloan.enumconstant.RoleEnum;
import com.example.educationloan.exception.DuplicateResourceException;
import com.example.educationloan.exception.ResourceNotFoundException;
import com.example.educationloan.exception.RoleNotFoundException;
import com.example.educationloan.repository.RoleRepository;
import com.example.educationloan.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    // uses findByIdWithRoles to eagerly fetch userRoles — prevents LazyInitializationException
    @Transactional(readOnly = true)
    public User getUserById(Long id) {
        return userRepository.findByIdWithRoles(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
        log.info("User with id {} deleted successfully", id);
    }

    @Transactional(readOnly = true)
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));
    }

    @Transactional(readOnly = true)
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }

    // fetches user with roles eagerly before calling toUserDTO()
    @Transactional
    public User assignRoleToUser(Long userId, RoleEnum roleName) {
        User user = userRepository.findByIdWithRoles(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new RoleNotFoundException("Role not found with name: " + roleName));

        boolean alreadyHasRole = user.getUserRoles().stream()
                .anyMatch(userRole -> userRole.getRole().equals(role));

        if (!alreadyHasRole) {
            user.addRole(role, "system");
            userRepository.save(user);
            log.info("Role {} assigned to user {}", roleName, user.getUsername());
        } else {
            log.info("User {} already has role {}", user.getUsername(), roleName);
        }

        return userRepository.findByIdWithRoles(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
    }

    @Transactional
    public void removeRoleFromUser(Long userId, RoleEnum roleName) {
        User user = userRepository.findByIdWithRoles(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with name: " + roleName));

        UserRole userRoleToRemove = user.getUserRoles().stream()
                .filter(userRole -> userRole.getRole().equals(role))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + userId + " does not have role: " + roleName));

        user.getUserRoles().remove(userRoleToRemove);
        role.getUserRoles().remove(userRoleToRemove);
        userRepository.save(user);
        log.info("Role {} removed from user {}", roleName, user.getUsername());
    }

    @Transactional(readOnly = true)
    public boolean isUserAdmin(Long userId) {
        User user = getUserById(userId);
        return user.getUserRoles().stream().map(UserRole::getRole)
                .anyMatch(role -> role.getName().equals(RoleEnum.ADMIN));
    }

    @Transactional(readOnly = true)
    public boolean isUserEmployee(Long userId) {
        User user = getUserById(userId);
        return user.getUserRoles().stream().map(UserRole::getRole)
                .anyMatch(role -> role.getName().equals(RoleEnum.EMPLOYEE));
    }

    @Transactional(readOnly = true)
    public boolean doesUserHaveRole(Long id, RoleEnum roleName) {
        User user = getUserById(id);
        return user.getUserRoles().stream().map(UserRole::getRole)
                .anyMatch(role -> role.getName() == roleName);
    }

    @Transactional
    public void activateUser(Long id) {
        User user = getUserById(id);
        user.setIsActive(true);
        userRepository.save(user);
        log.info("User {} with email {} activated successfully", user.getUsername(), user.getEmail());
    }

    @Transactional
    public void deactivateUser(Long id) {
        User user = getUserById(id);
        user.setIsActive(false);
        userRepository.save(user);
        log.info("User {} with email {} deactivated successfully", user.getUsername(), user.getEmail());
    }

    @Transactional
    public void verifyEmail(Long id) {
        User user = getUserById(id);
        user.setIsEmailVerified(true);
        userRepository.save(user);
        log.info("User {} with emailId {} verified successfully", user.getUsername(), user.getEmail());
    }

    @Transactional
    public void updatePassword(Long id, String newPassword) {
        User user = getUserById(id);
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        log.info("Password updated successfully for user {} with email {}", user.getUsername(), user.getEmail());
    }

    @Transactional
    public User createUser(String username, String email, String password, String firstName, String lastName) {
        if (userRepository.existsByUsername(username)) {
            throw new DuplicateResourceException("Username already exists: " + username);
        }
        if (userRepository.existsByEmail(email)) {
            throw new DuplicateResourceException("Email already exists: " + email);
        }

        Role defaultRole = roleRepository.findByName(RoleEnum.USER)
                .orElseThrow(() -> new ResourceNotFoundException("Default role not found: " + RoleEnum.USER));

        User user = User.builder()
                .username(username)
                .email(email)
                .password(passwordEncoder.encode(password))
                .firstName(firstName)
                .lastName(lastName)
                .isActive(true)
                .isEmailVerified(false)
                .build();

        user.addRole(defaultRole, "system");
        userRepository.save(user);
        log.info("User {} with email {} created successfully", username, email);

        // Re-fetch with roles eagerly after save
        return userRepository.findByIdWithRoles(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found after creation"));
    }

    @Transactional(readOnly = true)
    public List<User> filterUsersByRoleAndStatus(RoleEnum roleName, Boolean isActive) {
        return userRepository.findByRoleAndStatus(roleName, isActive);
    }

    @Transactional
    public User updateUser(Long id, String username, String email, String password, String firstName, String lastName) {
        User user = getUserById(id);

        Optional.ofNullable(username).filter(u -> !u.isBlank()).ifPresent(uname -> {
            if (userRepository.existsByUsername(uname) && !user.getUsername().equals(uname)) {
                throw new RuntimeException("Username already exists: " + uname);
            }
            user.setUsername(uname);
        });

        Optional.ofNullable(email).filter(e -> !e.isBlank()).ifPresent(e -> {
            if (userRepository.existsByEmail(e) && !user.getEmail().equals(e)) {
                throw new RuntimeException("Email already exists: " + e);
            }
            user.setEmail(e);
        });

        Optional.ofNullable(password).filter(p -> !p.isBlank()).map(passwordEncoder::encode).ifPresent(user::setPassword);
        Optional.ofNullable(firstName).filter(fn -> !fn.isBlank()).ifPresent(user::setFirstName);
        Optional.ofNullable(lastName).filter(ln -> !ln.isBlank()).ifPresent(user::setLastName);
        userRepository.save(user);
        log.info("User {} updated successfully", user.getUsername());
        return user;
    }

    @Transactional
    public User patchUser(Long id, String username, String email, String password, String firstName, String lastName) {
        User existingUser = getUserById(id);
        Optional.ofNullable(username).ifPresent(existingUser::setUsername);
        Optional.ofNullable(email).ifPresent(existingUser::setEmail);
        Optional.ofNullable(password).filter(p -> !p.isBlank()).map(passwordEncoder::encode).ifPresent(existingUser::setPassword);
        Optional.ofNullable(firstName).ifPresent(existingUser::setFirstName);
        Optional.ofNullable(lastName).ifPresent(existingUser::setLastName);
        return userRepository.save(existingUser);
    }

    public Optional<User> getUserById1(Long userId) {
        return userRepository.findByIdWithRoles(userId);
    }

    public List<Role> getRolesByUserId(Long userId) {
        return roleRepository.findRolesByUserId(userId);
    }

    // fetches user with roles eagerly before assigning multiple roles----------------------------
    @Transactional
    public User assignRolesUser1(Long userId, List<RoleEnum> roleNames) {
        User user = userRepository.findByIdWithRoles(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        for (RoleEnum roleName : roleNames) {
            Role role = roleRepository.findByName(roleName)
                    .orElseThrow(() -> new RoleNotFoundException("Role not found with name: " + roleName));

            boolean alreadyHasRole = user.getUserRoles().stream()
                    .anyMatch(userRole -> userRole.getRole().equals(role));

            if (!alreadyHasRole) {
                user.addRole(role, "system");
                log.info("Role {} assigned to user {}", roleName, user.getUsername());
            } else {
                log.info("User {} already has role {}", user.getUsername(), roleName);
            }
        }

        userRepository.save(user);

        //Re-fetch after save to return fresh data with all roles-------------------------------
        return userRepository.findByIdWithRoles(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
    }
}