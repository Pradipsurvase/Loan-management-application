package com.example.educationloan.config;


import com.example.educationloan.enumconstant.RoleEnum;
import com.example.educationloan.service.RoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class RoleInitializer implements CommandLineRunner {

    private static final String BLUE  = "\u001B[34m";
    private static final String RESET = "\u001B[0m";

    private final RoleService roleService;
    @Override
    public void run(String... args) {

        log.info(BLUE +"Available roles are loading in roles database.........................."+RESET);
        RoleEnum[] rolesToCreate = {RoleEnum.ADMIN, RoleEnum.USER, RoleEnum.MANAGER, RoleEnum.EMPLOYEE, RoleEnum.CASHIER, RoleEnum.AUDITOR, RoleEnum.SUPPORT, RoleEnum.ANALYST, RoleEnum.DEVELOPER, RoleEnum.TESTER, RoleEnum.CONSULTANT, RoleEnum.INTERN, RoleEnum.CONTRACTOR, RoleEnum.SUPERVISOR, RoleEnum.DIRECTOR, RoleEnum.EXECUTIVE, RoleEnum.OWNER, RoleEnum.GUEST, RoleEnum.MEMBER, RoleEnum.PARTNER, RoleEnum.VENDOR};
        for (RoleEnum roleEnum : rolesToCreate) {
            roleService.createOrGetRole(roleEnum);
        }
        log.info(BLUE + "all Available roles are Successfully loaded in the roles database..........!!!" + RESET);
    }
}
