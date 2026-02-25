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
}
