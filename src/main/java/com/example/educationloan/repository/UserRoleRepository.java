package com.example.educationloan.repository;


import com.example.educationloan.entity.Role;
import com.example.educationloan.entity.User;
import com.example.educationloan.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Long> {

    List<UserRole> findByUser(User user);
    List<UserRole> findByRole(Role role);
    boolean existsByUserAndRole(User user, Role role);
}
