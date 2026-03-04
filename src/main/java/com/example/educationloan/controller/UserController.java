package com.example.educationloan.controller;

import com.example.educationloan.dto.PasswordUpdateDTO;
import com.example.educationloan.dto.RoleDTO;
import com.example.educationloan.dto.UpdateUserDTO;
import com.example.educationloan.dto.UserDTO;
import com.example.educationloan.entity.Role;
import com.example.educationloan.entity.User;
import com.example.educationloan.enumconstant.RoleEnum;
import com.example.educationloan.response.ApiResponse;
import com.example.educationloan.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.educationloan.dto.UpdateUserDTO.toUpdateUserDTO;
import static com.example.educationloan.dto.UserDTO.toUserDTO;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    //1.controller to create/register the new  user---------------------------------------------------------------------
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<UserDTO>> createUser1(@RequestBody User user) {
        User createdUser = userService.createUser(user.getEmail(), user.getPassword(), user.getFirstName(), user.getLastName());
        UserDTO response = toUserDTO(createdUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(true,"User created successfully",response));
    }

  // 2.read the  user by id---------------------------------------------------------------------------------------------
    @GetMapping("/get/{id}")
    public ResponseEntity<ApiResponse<UserDTO>> getUser(@PathVariable Long id) {
        User user = userService.getUserById(id);
        UserDTO response = toUserDTO(user);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(true,"User Read successfully",response));
    }

    //3.assign multiple roles to the existing user if same role is not exist--------------------------------------------
    @PostMapping("assign/{userId}/roles")
    public ResponseEntity<ApiResponse<UserDTO>> assignRoles(@PathVariable Long userId, @RequestBody List<RoleEnum> roleNames) {
        User updatedUser = userService.assignRolesUser1(userId, roleNames);
        UserDTO response = toUserDTO(updatedUser);
        return ResponseEntity.ok(new ApiResponse<>(true,"Roles assigned to user successfully",response));
    }

    //4.Get all users---------------------------------------------------------------------------------------------------
    @GetMapping("/getAll")
    public ResponseEntity<ApiResponse<List<UserDTO>>> getAllUsers() {
        //this logic can be used to show a which roles is assigned to particular user in api response
        //List<UserDTO> response = userService.getAllUsers() .stream() .map(UserDTO::toUserDTO) .toList();

        //this custom user to dto mapping is used for i don't want to show that which roles is assigned to the specific user
        List<User> users = userService.getAllUsers();
        List<UserDTO> response = users.stream()
                .map(user -> UserDTO.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .email(user.getEmail())
                        .firstName(user.getFirstName())
                        .lastName(user.getLastName())
                        .isActive(user.getIsActive())
                        .isEmailVerified(user.getIsEmailVerified())
                        .createdAt(user.getCreatedAt())
                        .updatedAt(user.getUpdatedAt())
                        .build())
                .collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(true,"All User Read Successfully",response));
    }


    //5.UPDATE--firstname--lastname--password-for-email-update-enter-different-email---------------------------------------------------------------------------------------------------
    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse<UpdateUserDTO>> updateUser(@PathVariable Long id, @RequestBody UpdateUserDTO user) {
        User updatedUser = userService.updateUser(id, null, user.getEmail(), user.getPassword(), user.getFirstName(), user.getLastName());
        UpdateUserDTO response = toUpdateUserDTO(updatedUser);
        return ResponseEntity.ok(new ApiResponse<>(true,"User Updated Successfully ",response));
    }


    //6.DELETE-----------------------------------------------------------------------------------------------------------
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse<Long>> deleteUser(@PathVariable Long id) {
      boolean deleted=  userService.deleteUser(id);
        if (!deleted) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, "User not found", id));
        }
        return ResponseEntity.ok(new ApiResponse<>(true,"User deleted successfully",id));
    }

    //7.PATCH - Partial Update ------------------------------------------------------------------------------------------
    @PatchMapping("/patch/{id}")
    public ResponseEntity<ApiResponse<UserDTO>> patchUser(@PathVariable Long id, @RequestBody User user) {
        User patchedUser = userService.patchUser(id, user.getUsername(), user.getEmail(), user.getPassword(), user.getFirstName(), user.getLastName());
        user.setIsActive(true);
        return ResponseEntity.ok(new ApiResponse<>(true, "User patched successfully", toUserDTO(patchedUser)));
    }


    @GetMapping("/username/{username}")
    public ResponseEntity<ApiResponse<UserDTO>> getUserByUsername(@PathVariable String username) {
        User user = userService.getUserByUsername(username);
        UserDTO response = UserDTO.toUserDTO(user);
        return ResponseEntity.ok( new ApiResponse<>(true, "User fetched successfully", response) );
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<ApiResponse<UserDTO>> getUserByEmail(@PathVariable String email) {
        User user = userService.getUserByEmail(email);
        UserDTO response = UserDTO.toUserDTO(user);
        return ResponseEntity.ok( new ApiResponse<>(true, "User fetched successfully", response) );
    }


    @GetMapping("/getByRoleAndStatus")
    public ResponseEntity<ApiResponse<List<UserDTO>>> filterUsersByRoleAndStatus(@RequestParam RoleEnum roleName, @RequestParam Boolean isActive) {
        List<User> users = userService.filterUsersByRoleAndStatus(roleName, isActive);
        List<UserDTO> response = users.stream().map(UserDTO::toUserDTO).toList();
        return ResponseEntity.ok(new ApiResponse<>(true, "Filtered users fetched successfully", response));
        }


    @GetMapping("/{id}/isAdmin")
    public ResponseEntity<ApiResponse<Boolean>> isUserAdmin(@PathVariable Long id) {
        boolean isAdmin = userService.isUserAdmin(id);
        return ResponseEntity.ok( new ApiResponse<>(true, "Admin check completed", isAdmin) );
    }


    @GetMapping("/{id}/isEmployee")
    public ResponseEntity<ApiResponse<Boolean>> isUserEmployee(@PathVariable Long id) {
        boolean isEmployee = userService.isUserEmployee(id);
        return ResponseEntity.ok( new ApiResponse<>(true, "Employee check completed", isEmployee) );
    }

    //get a data of particular user id to check if the user has given role
    @GetMapping("/{id}/hasRole/{roleName}")
    public ResponseEntity<ApiResponse<Boolean>> doesUserHaveRole( @PathVariable Long id, @PathVariable RoleEnum roleName) {
        boolean hasRole = userService.doesUserHaveRole(id, roleName);
        return ResponseEntity.ok( new ApiResponse<>(true, "Role check completed", hasRole) );
    }


    //activate the user based on the id
    @PutMapping("/{id}/activate")
    public ResponseEntity<ApiResponse<Long>> activateUser(@PathVariable Long id) {
        userService.activateUser(id);
        return ResponseEntity.ok( new ApiResponse<>(true, "User activated successfully", id) );
    }

    //deactivate the user based on the id
    @PutMapping("/{id}/deactivate")
    public ResponseEntity<ApiResponse<Long>> deactivateUser(@PathVariable Long id) {
        userService.deactivateUser(id);
        return ResponseEntity.ok( new ApiResponse<>(true, "User deactivated successfully", id) );
    }

    //verify email flag to true by the admin is email is verified-------------------------------------------------------
    @PutMapping("/{id}/verifyEmail")
    public ResponseEntity<ApiResponse<Long>> verifyEmail(@PathVariable Long id) {
        userService.verifyEmail(id);
        return ResponseEntity.ok( new ApiResponse<>(true, "User email verified successfully", id) );
    }

    //update the password by the user
    @PutMapping("/{id}/updatePassword")
    public ResponseEntity<ApiResponse<Long>> updatePassword( @PathVariable Long id, @RequestBody PasswordUpdateDTO request) {
        userService.updatePassword(id, request.getNewPassword());
        return ResponseEntity.ok( new ApiResponse<>(true, "Password updated successfully", id) );
    }

    // Remove a role from a user based on the id
    @DeleteMapping("/{id}/roles/{roleName}")
    public ResponseEntity<ApiResponse<Long>> removeRoleFromUser( @PathVariable Long id, @PathVariable RoleEnum roleName) {
        userService.removeRoleFromUser(id, roleName);
        return ResponseEntity.ok( new ApiResponse<>(true, "Role removed successfully", id) );
    }


    // Get all roles for a user
    @GetMapping("/{id}/roles") public ResponseEntity<ApiResponse<List<RoleDTO>>> getRolesByUserId(@PathVariable Long id) {
        List<Role> roles = userService.getRolesByUserId(id);
        List<RoleDTO> response = roles.stream().map(role -> new RoleDTO(role.getRoleId(), role.getName())) .toList();
        return ResponseEntity.ok( new ApiResponse<>(true, "Roles fetched successfully", response) );
    }



}





