package com.example.educationloan.enumconstant;

import lombok.Getter;

@Getter
public enum RoleEnum {

    USER("User role for loan applicants"),
    EMPLOYEE("Employee role for bank staff"),
    ADMIN("Admin role for system administrators");

    private final String description;

    RoleEnum(String description) {
        this.description = description;
    }

}