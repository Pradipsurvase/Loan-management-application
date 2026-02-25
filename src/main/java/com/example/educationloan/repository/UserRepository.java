package com.example.educationloan.repository;

import com.example.educationloan.entity.User;
import com.example.educationloan.enumconstant.RoleEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    //custom query to find users by role and status
    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.name = :roleName AND u.isActive = :isActive")
    List<User> findByRoleAndStatus(@Param("roleName") RoleEnum roleName, @Param("isActive") Boolean isActive);


    //for pagination with cursor-based approach
    /*
    @Query("SELECT u FROM User u JOIN u.roles r " + "WHERE r.name = :roleName AND u.isActive = :isActive " + "AND (:cursorId IS NULL OR u.id > :cursorId) " + "ORDER BY u.id ASC")
    List<User> findByRoleAndStatusAfterCursor(@Param("roleName") RoleEnum roleName, @Param("isActive") Boolean isActive, @Param("cursorId") Long cursorId, Pageable pageable);

    * */


}
