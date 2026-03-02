package com.example.educationloan.controller;

import com.example.educationloan.dto.UserDTO;
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

import static com.example.educationloan.dto.UserDTO.toUserDTO;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    /*
    1.controller to create user-----------------------------------------------------------------------------------------
    */
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<UserDTO>> createUser1(@RequestBody User user) {
        User createdUser = userService.createUser(user.getEmail(), user.getPassword(), user.getFirstName(), user.getLastName());
        UserDTO response = toUserDTO(createdUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(true,"User created successfully",response));
    }

/*
2.read the  user by id---------------------------------------------------------------------------------------------------
 */
    @GetMapping("/get/{id}")
    public ResponseEntity<ApiResponse<UserDTO>> getUser(@PathVariable Long id) {
        User user = userService.getUserById(id);
        UserDTO response = toUserDTO(user);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(true,"User Read successfully",response));
    }

    /* 3.assign multiple roles to the existing user if same role is not exist--------------------------------------------------------------------------------------
     */
    @PostMapping("assign/{userId}/roles")
    public ResponseEntity<ApiResponse<UserDTO>> assignRoles(@PathVariable Long userId, @RequestBody List<RoleEnum> roleNames) {
        User updatedUser = userService.assignRolesUser1(userId, roleNames);
        UserDTO response = toUserDTO(updatedUser);
        return ResponseEntity.ok(new ApiResponse<>(true,"Roles assigned to user successfully",response));
    }
    /*
    *Get all users------------------------------------------------------------------------------------------------------
     */
    @GetMapping("/getAll")
    public ResponseEntity<ApiResponse<List<UserDTO>>> getAllUsers() {
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
                        .build())
                .collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(true,"All User Read Successfully",response));
    }


    // UPDATE-----------------------------------------------------------------------------------------------------------
    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse<UserDTO>> updateUser(@PathVariable Long id, @RequestBody User user) {
        User updatedUser = userService.updateUser(id, user.getUsername(), user.getEmail(), user.getPassword(), user.getFirstName(), user.getLastName());
        UserDTO response = UserDTO.builder()
                .id(updatedUser.getId())
                .username(updatedUser.getUsername())
                .email(updatedUser.getEmail())
                .firstName(updatedUser.getFirstName())
                .lastName(updatedUser.getLastName())
                .isActive(updatedUser.getIsActive())
                .isEmailVerified(updatedUser.getIsEmailVerified())
                .build();
        return ResponseEntity.ok(new ApiResponse<>(true,"User Updated Successfully ",response));
    }


    /*
    * @RequestParam Long id → expects DELETE /delete?id=123
    * @PathVariable Long id → expects DELETE /delete/123
    * */
    // DELETE-----------------------------------------------------------------------------------------------------------
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse<Long>> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(new ApiResponse<>(true,"User deleted successfully",id));
    }

    // PATCH - Partial Update ------------------------------------------------------------------------------------------
    @PatchMapping("/patch/{id}")
    public ResponseEntity<ApiResponse<UserDTO>> patchUser(@PathVariable Long id, @RequestBody User user) {
        User patchedUser = userService.patchUser(id, user.getUsername(), user.getEmail(), user.getPassword(), user.getFirstName(), user.getLastName());

        UserDTO response = UserDTO.builder()
                .id(patchedUser.getId())
                .username(patchedUser.getUsername())
                .email(patchedUser.getEmail())
                .firstName(patchedUser.getFirstName())
                .lastName(patchedUser.getLastName())
                .isActive(patchedUser.getIsActive())
                .isEmailVerified(patchedUser.getIsEmailVerified())
                .build();
        return ResponseEntity.ok(new ApiResponse<>(true, "User patched successfully", response));
    }
}





