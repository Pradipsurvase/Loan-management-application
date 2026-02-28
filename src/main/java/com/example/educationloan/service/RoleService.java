package com.example.educationloan.service;

import com.example.educationloan.entity.Role;
import com.example.educationloan.entity.User;
import com.example.educationloan.enumconstant.RoleEnum;
import com.example.educationloan.exception.DuplicateResourceException;
import com.example.educationloan.exception.ResourceNotFoundException;
import com.example.educationloan.repository.RoleRepository;
import com.example.educationloan.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

import static com.example.educationloan.enumconstant.RoleEnum.USER;

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
    @Transactional(readOnly = true)
    public Optional<Role> getByRoleName(RoleEnum roleName) {
        return roleRepository.findByName(roleName);
    }

    /*
     * Get all roles-------------------------------------------------------------------------
     * */
    @Transactional(readOnly = true)
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    /*
    *get all roles assigned to specific user------------------------------------------------
    * */
    @Transactional(readOnly = true)
    public List<Role> getRolesByUserId(Long userId) {
        User user = userService.getUserById(userId);
        return user.getRoles().stream().toList();
    }

    /*
     * create new role or get role------------------------------------------------------------
     * */
    @Transactional
    public Role createOrGetRole(RoleEnum name) {
        return roleRepository.findByName(name).orElseGet(() -> {
            Role newRole = Role.builder()
                    .name(name)
                  //  .description("Default description for " + name)
                    .build();
            Role savedRole = roleRepository.save(newRole);
            log.info("Created new role with name {} and id {}", savedRole.getName(), savedRole.getRoleId());
            return savedRole;
        });
    }

    /*
    *remove role from the specific user-----------------------------------------------------
    * */
    @Transactional
    public void removeRoleFromUser(Long userId, RoleEnum roleName) {

        Role role = roleRepository.findByName(roleName).orElseThrow(() -> new RuntimeException("Role not found with name: " + roleName));
        User user = userService.getUserById(userId);
        if(!user.getRoles().remove(role)) {
            throw new RuntimeException("User with id " + userId + " does not have role: " + roleName);
        }
        userRepository.save(user);
        log.info("Removed role {} from user with id {}", roleName, userId);


    }

    /*
    *update role details of the specific user---------------------------------------------
    * */
    @Transactional
    public Role updateRole(Long id, RoleEnum name) {
        Role role = roleRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + id));
         if(name!=null){
             roleRepository.findByName(name).ifPresent(existingRole -> {
                 if (!existingRole.getRoleId().equals(id)) {
                     throw new DuplicateResourceException("Role name already exists: " + name);
                 }
             });
             role.setName(name);
         }

         return roleRepository.save(role);
    }

        /*
        *fetch all user who has the user role--------------------------------------------
        * */
     @Transactional(readOnly = true)
    public List<User> getUserWithUserRole() {
        return userRepository.findByRoles_Name(USER.name());
    }

    /*
    *fetch all the user with the specific role-----------------------------------------------
    * */
    @Transactional(readOnly = true)
    public List<User> getUsersByRoleName(RoleEnum roleName) {
        return userRepository.findBySpecificRoleName(roleName);
    }

    /*update role of the specific user*/
    @Transactional
    public User updateUserRole(Long userId, RoleEnum newRoleName) {

        //fetch the user by id
        User user=userRepository.findById(userId).orElseThrow(()->new ResourceNotFoundException("User not found with id: "+userId));

        //fetch thr role by its enum name
        Role role=roleRepository.findByName(newRoleName).orElseThrow(()->new ResourceNotFoundException("Role not found with name: "+newRoleName));

        //clear existing roles and assign the new role
        user.getRoles().clear();
        user.getRoles().add(role);
        return userRepository.save(user);

    }

    public List<Role> getRolesByUserId1(Long userId) {
        return roleRepository.findRolesByUserId(userId);
    }

    /*update role assign to the specific user*/
    public User updateUserRole1(Long userId, RoleEnum newRoleName) {
        User user = userService.getUserById(userId);
        Role newRole = roleRepository.findByName(newRoleName).orElseThrow(() -> new RuntimeException("Role not found with name: " + newRoleName));
        user.getRoles().clear(); // Clear existing roles
        user.getRoles().add(newRole); // Assign the new role
        User updatedUser = userRepository.save(user);
        log.info("Updated role for user with id {}: new role name {}", userId, newRoleName);
        return updatedUser;
    }

}

