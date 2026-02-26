package com.example.educationloan.service;

import com.example.educationloan.entity.Role;
import com.example.educationloan.entity.User;
import com.example.educationloan.enumconstant.RoleEnum;
import com.example.educationloan.repository.RoleRepository;
import com.example.educationloan.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;
    private final UserService userService;
    private final UserRepository userRepository;

    /*
     * Get role by name----------------------------------------------------------------
     * */
    public Optional<Role> getRoleByName(RoleEnum roleName) {
        return roleRepository.findByName(roleName);
    }

    /*
     * Get all roles-------------------------------------------------------------------------
     * */
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }


    /*
    *get all roles assigned to specific user------------------------------------------------
    * */
    public List<Role> getRolesByUserId(Long userId) {
        User user = userService.getUserById(userId);
        return user.getRoles().stream().toList();
    }


    /*
     * create new role or get role------------------------------------------------------------
     * */
    public Role createOrGetRole(RoleEnum name) {
        return roleRepository.findByName(name).orElseGet(() -> {
            Role newRole = Role.builder()
                    .name(name)
                    .description("Default description for " + name)
                    .build();
            Role savedRole = roleRepository.save(newRole);
            log.info("Created new role with name {} and id {}", savedRole.getName(), savedRole.getId());
            return savedRole;
        });
    }


    /*
    *remove role from the specific user-----------------------------------------------------
    * */
    public void removeRoleFromUser(Long userId, RoleEnum roleName) {
        Role role = roleRepository.findByName(roleName).orElseThrow(() -> new RuntimeException("Role not found with name: " + roleName));
        // Assuming you have a method in UserService to get user by ID
        User user = userService.getUserById(userId);
        if (user.getRoles().contains(role)) {
            user.getRoles().remove(role);
            userRepository.save(user);
            log.info("Removed role {} from user with id {}", roleName, userId);
        } else {
            log.warn("User with id {} does not have role {}", userId, roleName);
        }
    }


    /*
    *update role details of the specific user---------------------------------------------
    * */
    public Role updateRole(Long id, RoleEnum name, String description) {
        Role role = roleRepository.findById(id).orElseThrow(() -> new RuntimeException("Role not found with id: " + id));
         if(name!=null){
             roleRepository.findByName(name).ifPresent(existingRole -> {
                 if (!existingRole.getId().equals(id)) {
                     throw new RuntimeException("Role name already exists: " + name);
                 }
             });
             role.setName(name);
         }
            if(description!=null){
                role.setDescription(description);
            }
        Role updatedRole = roleRepository.save(role);
        log.info("Updated role with id {}: new name {}, new description {}", updatedRole.getId(), updatedRole.getName(), updatedRole.getDescription());
        return updatedRole;
        }

        /*
        *fetch all user who has the user role--------------------------------------------
        * */

    public List<User> getUserWithUserRole() {
        return userRepository.findBy_UserRoleName(RoleEnum.USER);
    }

    /*
    *fetch all the user with the specific role-----------------------------------------------
    * */
    public List<User> getUsersByRoleName(RoleEnum roleName) {
        return userRepository.findBy_SpecificRoleName(roleName);
    }

}

