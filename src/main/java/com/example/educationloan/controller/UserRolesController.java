package com.example.educationloan.controller;

import com.example.educationloan.dto.AssignRoleDTO;
import com.example.educationloan.dto.UserRoleDTO;
import com.example.educationloan.entity.Role;
import com.example.educationloan.entity.User;
import com.example.educationloan.entity.UserRole;
import com.example.educationloan.response.ApiResponse;
import com.example.educationloan.service.UserRoleService;
import com.example.educationloan.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/user_roles")
@RequiredArgsConstructor
public class UserRolesController {

    private final UserService userService;
    private final UserRoleService userRoleService;


    // Fetch all UserRole mappings for a given user id--------------------------------------------------
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<UserRoleDTO>>> getUserRolesByUserId(@PathVariable Long userId) {
        List<UserRole> userRoles = userRoleService.getUserRolesByUserId(userId);
        List<UserRoleDTO> response = userRoles.stream().map(UserRoleDTO::fromEntity).toList();
        return ResponseEntity.ok(new ApiResponse<>(true, "User roles fetched successfully", response));
    }

    // Fetch all UserRole mappings for a given role-------------------------------------------------
    @GetMapping("/role/{roleId}")
    public ResponseEntity<ApiResponse<List<UserRoleDTO>>> getUserRolesByRoleId(@PathVariable Long roleId) {
        List<UserRole> userRoles = userRoleService.getUserRolesByRoleId(roleId);
        List<UserRoleDTO> response = userRoles.stream().map(UserRoleDTO::fromEntity).toList();
        return ResponseEntity.ok(new ApiResponse<>(true, "Role mappings fetched successfully", response));
    }

    //pass/assign the role in the request body-----------------------------------------------------------
    @PostMapping("/assign")
    public ResponseEntity<ApiResponse<UserRoleDTO>> assignRoleToUser(@RequestBody AssignRoleDTO request) {
        User user = userService.getUserById(request.getUserId());
        Role role = userService.getRoleById(request.getRoleId());
        UserRole userRole = userRoleService.giveRoleToUser(user, role, request.getAssignedBy());
        return ResponseEntity.ok(new ApiResponse<>(true, "Role assigned successfully", UserRoleDTO.fromEntity(userRole)));
    }

    //remove the particular role from the user using the roleId---------------------------------------------------
    @DeleteMapping("/role/{userRoleId}")
    public ResponseEntity<ApiResponse<Void>> removeUserRole(@PathVariable Long userRoleId) {
        userService.removeUserRole(userRoleId);
        return ResponseEntity.ok(new ApiResponse<>(true, "User role removed successfully", null));
    }

}
