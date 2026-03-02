package com.example.educationloan.controller;

import com.example.educationloan.dto.RoleDTO;
import com.example.educationloan.dto.UserDTO;
import com.example.educationloan.entity.Role;
import com.example.educationloan.entity.User;
import com.example.educationloan.entity.UserRole;
import com.example.educationloan.enumconstant.RoleEnum;
import com.example.educationloan.response.ApiResponse;
import com.example.educationloan.service.RoleService;
import com.example.educationloan.service.UserRoleService;
import com.example.educationloan.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import static com.example.educationloan.dto.UserDTO.toUserDTO;
@RestController
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
public class RoleController {
    private final RoleService roleService;
    private final UserService userService;
    private final UserRoleService userRoleService;

    //1.Get all roles assigned to a specific user-----------------------------------------------------------------------
    @GetMapping("/byUserId/{userId}")
    public ResponseEntity<ApiResponse<?>> getRolesByUserId(@PathVariable Long userId) {
        Optional<User> userOptional = userService.getUserById1(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            UserDTO userDTO = toUserDTO(user);
            // Fetch UserRole mappings for this user
            List<UserRole> userRoles = userRoleService.getUserRolesByUserId(userId);
            // Extract Role objects from UserRole
            List<RoleDTO> roleDTOs = userRoles.stream().map(UserRole::getRole)
                                              .map(role -> new RoleDTO(role.getRoleId(), role.getName())).toList();
            // Attach roles to the UserDTO
            userDTO.setRoles(new HashSet<>(roleDTOs));
            return ResponseEntity.ok(new ApiResponse<>(true, "Roles for user with id " + userId + " fetched successfully", userDTO));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(false, "User with id " + userId + " not found", null));
        }
    }
    // 2.add or assign  new role to the existing user,if same role is not exist-----------------------------------------
    @PostMapping("/assignRole/{userId}/{roleName}")
    public ResponseEntity<ApiResponse<UserDTO>> assignRoleUser(@PathVariable Long userId, @PathVariable RoleEnum roleName) {
        User updatedUserRole=userService.assignRoleToUser(userId, roleName);
        UserDTO userDTO = toUserDTO(updatedUserRole);
        return ResponseEntity.ok(new ApiResponse<>(true,"Role "+ roleName+" assigned to user with id "+userId+" successfully",userDTO));
    }

    //3.update the role assign to the specific user---------------------------------------------------------------------
    @PutMapping("/updateUserRole/{userId}/{oldRole}/{newRole}")
    public ResponseEntity<ApiResponse<UserDTO>> updateUserRole(@PathVariable Long userId, @PathVariable RoleEnum oldRole,@PathVariable RoleEnum newRole) {
        User updatedUserRole=roleService.updateUserRole(userId, oldRole,newRole);
        UserDTO userDTO = toUserDTO(updatedUserRole);
        return ResponseEntity.ok(new ApiResponse<>(true,"User role updated successfully for user with id:"+ userId,userDTO));
    }

    //4.remove role from a specific user--------------------------------------------------------------------------------
    @DeleteMapping("/removeRole/{userId}/{roleName}")
    public ResponseEntity<ApiResponse<UserDTO>> removeRoleFromUser(@PathVariable Long userId, @PathVariable RoleEnum roleName) {
            User updatedUser=roleService.removeRoleFromUser(userId, roleName);
            UserDTO userDTO=toUserDTO(updatedUser);
            return ResponseEntity.ok(new ApiResponse<>(true,"Role " + roleName + " removed from user with id " + userId,userDTO));
    }


    //5.get role by name------------------------------------------------------------------------------------------------

    @GetMapping("/{roleName}")
    public ResponseEntity<ApiResponse<Role>> getRoleByName(@PathVariable RoleEnum role) {
        return roleService.getByRoleName(role).map(roleObj->ResponseEntity.ok(new ApiResponse<>(true,"User Role Found",roleObj))).
                orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(false,"User Role Not Found",null)));
    }


    //Get all roles----------------------------------------------------------------------------------------------------

    @GetMapping("/getRoles")
    public ResponseEntity<ApiResponse<?>> getAllRoles() {
        List<Role> role=roleService.getAllRoles();
        return ResponseEntity.ok(new ApiResponse<>(true,"All roles fetched successfully",role));
    }

    //create a new role if not exist or get existing role---------------------------------------------------------------
    @PostMapping("/createRole/{roleName}")
    public ResponseEntity<ApiResponse<Role>> createOrGetRole(@PathVariable RoleEnum roleName) {
        return ResponseEntity.ok(new ApiResponse<>(true,"Role created or retrieved successfully",roleService.createOrGetRole(roleName)));
    }




    /* fetch all user who have User role*/
    @GetMapping("/usersWithRole")
    public ResponseEntity<ApiResponse<?>> getUsersWithRole() {
        return ResponseEntity.ok(new ApiResponse<>(true,"fetch all user with user role",roleService.getUserWithUserRole()));
    }

    /*fetch all user with specific role*/
    @GetMapping("/usersWithRole/{roleName}")
    public ResponseEntity<ApiResponse<?>> getUsersWithSpecificRole(@PathVariable RoleEnum roleName) {
        return ResponseEntity.ok(new ApiResponse<>(true,"fetch all user with role: "+roleName,roleService.getUsersByRoleName(roleName)));
    }


    //-----------------------------------------------------------------------------------------------------------------------------
    // Create or get a role by name
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<Role>> createRole(@RequestParam RoleEnum role) {
        Role createdRole = roleService.createOrGetRole(role);
        return ResponseEntity.ok(new ApiResponse<>(true, "Role Created successfully", createdRole));
    }

    // Get all roles
    @GetMapping
    public ResponseEntity<ApiResponse<List<Role>>> getAllRoles1() {
        List<Role> roles = roleService.getAllRoles();
        return ResponseEntity.ok(new ApiResponse<>(true, "All roles fetched successfully", roles));
    }

    // Get roles assigned to a specific user
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<Role>>> getRolesByUser(@PathVariable Long userId) {
        List<Role> roles = roleService.getRolesByUserId(userId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Roles for user " + userId + " fetched successfully", roles));
    }


    @PostMapping("/{userId}/role/{roleName}")
    public ResponseEntity<ApiResponse<UserDTO>> addRole(@PathVariable Long userId, @PathVariable RoleEnum roleName) {

        User updatedUser = userService.assignRolesUser1(userId, List.of(roleName));
        UserDTO response = toUserDTO(updatedUser);

        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(true, "Role added successfully", response));
    }



}
