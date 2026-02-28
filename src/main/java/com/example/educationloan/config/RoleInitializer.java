package com.example.educationloan.config;


import com.example.educationloan.enumconstant.RoleEnum;
import com.example.educationloan.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RoleInitializer implements CommandLineRunner {
    private final RoleService roleService;


    @Override
    public void run(String... args) {
        /* ask user to which role to create using the switch choice*/
        RoleEnum[] rolesToCreate = {RoleEnum.ADMIN, RoleEnum.USER, RoleEnum.MANAGER, RoleEnum.EMPLOYEE};
        for (RoleEnum roleEnum : rolesToCreate) {
            roleService.createOrGetRole(roleEnum);
        }
    }
}
