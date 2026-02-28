package com.example.educationloan.dto;


import lombok.*;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRoleDTO {
    private Long id;
    private Long userId;
    private Long roleId;

    public UserRoleDTO toUserRoleDTO(Long userId, Long roleId) {
        return UserRoleDTO.builder()
                .userId(userId)
                .roleId(roleId)
                .build();
    }
}
