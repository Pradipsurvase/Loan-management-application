package com.example.educationloan.dto;



import com.example.educationloan.enumconstant.RoleEnum;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleDTO {
    private Long id;
    private RoleEnum name;
    private String description;
}
