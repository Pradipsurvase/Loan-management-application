package com.example.educationloan.controller;

import com.example.educationloan.entity.Role;
import com.example.educationloan.enumconstant.RoleEnum;
import com.example.educationloan.response.ApiResponse;
import com.example.educationloan.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RoleController {
    private final RoleService roleService;

    /*
    * get role by name--------------------------------------------------------------------------------------
    * */
    @GetMapping("/{roleName}")
    public ResponseEntity<ApiResponse<Role>> getRoleByName(@PathVariable RoleEnum role) {
        return roleService.getByRoleName(role).map(roleObj->ResponseEntity.ok(new ApiResponse<>(true,"User Role Found",roleObj))).
                              orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(false,"User Role Not Found",null)));
    }

    /*
    *Get all roles
    * */
    @GetMapping("/getRoles")
    public ResponseEntity<ApiResponse<?>> getAllRoles() {
        List<Role> role=roleService.getAllRoles();
        return ResponseEntity.ok(new ApiResponse<>(true,"All roles fetched successfully",role));
    }

    /*
    *Get all roles assigned to a specific user
    * */
    @GetMapping("/byUserId/{userId}")
    public ResponseEntity<ApiResponse<?>> getRolesByUserId(@PathVariable Long userId) {
        List<Role> roles = roleService.getRolesByUserId(userId);
        return ResponseEntity.ok(new ApiResponse<>(true,"Roles for user with id " + userId + " fetched successfully",roles));
    }

    /*create a new role if not exist or get existing role*/
    @GetMapping("/createRole/{roleName}")
    public ResponseEntity<ApiResponse<Role>> createOrGetRole(@PathVariable RoleEnum roleName) {
        return ResponseEntity.ok(new ApiResponse<>(true,"Role created or retrieved successfully",roleService.createOrGetRole(roleName)));
    }

    /*remove role from a specific user*/
    @DeleteMapping("/removeRole/{userId}/{roleName}")
    public ResponseEntity<ApiResponse<?>> removeRoleFromUser(@PathVariable Long userId, @PathVariable RoleEnum roleName) {
            roleService.removeRoleFromUser(userId, roleName);
            return ResponseEntity.ok(new ApiResponse<>(true,"Role " + roleName + " removed from user with id " + userId,null));
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

    /*update the role of the specific user id*/
    @PutMapping("/updateUserRole/{userId}/{newRoleName}")
    public ResponseEntity<ApiResponse<Role>> updateUserRole(@PathVariable Long userId, @PathVariable RoleEnum newRoleName) {
        Role updatedRole=roleService.updateUserRole(userId, newRoleName);
        return ResponseEntity.ok(new ApiResponse<>(true,"User role updated successfully for user with id:"+ userId,updatedRole));
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

}
