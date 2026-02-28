package com.example.educationloan.entity;



import com.example.educationloan.enumconstant.RoleEnum;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(
        name = "roles",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_role_name", columnNames = "role_name")
        },
        indexes = {
                @Index(name = "idx_role_name", columnList = "role_name")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long roleId;

    @Enumerated(EnumType.STRING)
    @Column(name="role_name",nullable = false, length = 50, unique = true)
    private RoleEnum name;


    @Builder.Default
    @ManyToMany(mappedBy = "roles", fetch = FetchType.LAZY)
   // @JsonBackReference
    @JsonIgnore              // prevents recursion
    private Set<User> users = new HashSet<>();
}
