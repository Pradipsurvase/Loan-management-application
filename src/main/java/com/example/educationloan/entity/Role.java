package com.example.educationloan.entity;


import com.example.educationloan.enumconstant.RoleEnum;
import jakarta.persistence.*;
import lombok.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(
        name = "roles",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_role_name", columnNames = "name")
        },
        indexes = {
                @Index(name = "idx_name", columnList = "name")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private RoleEnum name;

    @Column(length = 500)
    private String description;

    @ManyToMany(mappedBy = "roles")
    private Set<User> users = new HashSet<>();
}