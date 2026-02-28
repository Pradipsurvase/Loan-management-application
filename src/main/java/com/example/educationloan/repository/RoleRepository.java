package com.example.educationloan.repository;





import com.example.educationloan.entity.Role;
import com.example.educationloan.enumconstant.RoleEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByName(RoleEnum name);


    @Query("SELECT r FROM User u JOIN u.roles r WHERE u.id = :userId")
    List<Role> findRolesByUserId(@Param("userId") Long userId);

    boolean existsByName(RoleEnum name);
}
