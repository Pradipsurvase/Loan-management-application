package com.example.educationloan.security;

import com.example.educationloan.entity.Role;
import com.example.educationloan.entity.User;
import com.example.educationloan.enumconstant.RoleEnum;
import com.example.educationloan.repository.RoleRepository;
import com.example.educationloan.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {


    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;


    /*
     *get user by username
     */
    @Transactional(readOnly = true)
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found with username: " + username));
    }

    /*
     * Get user by email
     */
    @Transactional(readOnly = true)
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found with email: " + email));
    }

    /*
     * Get user by ID
     */
    @Transactional(readOnly = true)
    public User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    /*
     * assign role to user
     * */
    @Transactional
    public void assignRoleToUser(Long Id, RoleEnum roleName) {
        User user = getUserById(Id);
        Role role = roleRepository.findByName(roleName).orElseThrow(() -> new RuntimeException("Role not found with name: " + roleName));
        if(!user.getRoles().contains(role)) {
            user.getRoles().add(role);
            role.getUsers().add(user);
            userRepository.save(user);
            log.info("Role {} assigned to user {}", roleName, user.getUsername());
        }else{
            log.info("User {} already has role {}", user.getUsername(), roleName);
        }
    }
    /*
    * remove role from user
    * */
    @Transactional
    public void removeRoleFromUSer(Long Id,RoleEnum roleName){
        User user=getUserById(Id);
        Role role=roleRepository.findByName(roleName).orElseThrow(()->new RuntimeException("Role not found with name: "+roleName));
        if(user.getRoles().contains(role)){
            user.getRoles().remove(role);
            role.getUsers().remove(user);
            userRepository.save(user);
            log.info("Role {} removed from user {}", roleName, user.getUsername());
        }else {
            log.info("User {} does not have role {}", user.getUsername(), roleName);
        }
    }

    /*
    * check if user has  admin role
     *
    */
    @Transactional(readOnly = true)
    public boolean isUserAdmin(Long Id){
        User user=getUserById(Id);
      /*
       Role role=roleRepository.findByName(RoleEnum.ADMIN).orElseThrow(()->new RuntimeException("Role not found with name: "+RoleEnum.ADMIN));
       return user.getRoles().contains(role);
      */
        return user.getRoles().stream().anyMatch(role->role.getName()==RoleEnum.ADMIN);
    }

    /*
    * check if user is  employee
    * */
    @Transactional(readOnly = true)
    public boolean isUserEmployee(Long id){
        User user=getUserById(id);
        return user.getRoles().stream().anyMatch(role->role.getName()==RoleEnum.EMPLOYEE);
    }

    /*
    * check if the user has a specific role
    * */
    @Transactional(readOnly = true)
    public boolean doesUserHaveRole(Long id,RoleEnum roleName){
        User user=getUserById(id);
        return user.getRoles().stream().anyMatch(role->role.getName()==roleName);
    }

    /*
    * Activate user
     * */
    @Transactional
    public void activateUser(Long id){
        User user=getUserById(id);
        user.setIsActive(true);
        userRepository.save(user);
        log.info("User {} with email {} activated successfully", user.getUsername(), user.getEmail());
    }
    /*
     *Deactivate user
     */
    @Transactional
    public void deactivateUser(Long id) {
        User user = getUserById(id);
        user.setIsActive(false);
        userRepository.save(user);
        log.info("User {}  with email {} deactivated successfully", user.getUsername(),user.getEmail());
    }

    @Transactional
    public void verifyEmail(Long id){
        User user=getUserById(id);
        user.setIsEmailVerified(true);
        userRepository.save(user);
        log.info("User {} with emailId {} verified successfully", user.getUsername(),user.getEmail());
    }

    /*
    * update the password for user
    * */
    @Transactional
    public void updatePassword(Long id,String newPassword){
        User user=getUserById(id);
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        log.info("Password updated successfully for user {} with email {}", user.getUsername(),user.getEmail());
    }

    /*
    *user creation
    * */
    @Transactional
public User createUser(String username, String email, String password, String firstName, String lastName) {
    if (userRepository.existsByUsername(username)) {
        throw new RuntimeException("Username is already exist: " + username);
    }
    if (userRepository.existsByEmail(email)) {
        throw new RuntimeException("Email is already in use: " + email);
    }
    User user = User.builder()
            .username(username)
            .email(email)
            .password(passwordEncoder.encode(password))
            .firstName(firstName)
            .lastName(lastName)
            .isActive(true)
            .isEmailVerified(false)
            .build();
    userRepository.save(user);
    log.info("User {} with email {} created successfully", username, email);
    return user;
}

/*
* filter users by role and active status
* */
    @Transactional(readOnly = true)
 public List<User> filterUsersByRoleAndStatus(RoleEnum roleName, Boolean isActive) {
        return userRepository.findByRoleAndStatus(roleName, isActive);
    }


    /*
    * @Transactional(readOnly = true)
public List<User> filterUsersByRoleAndStatusCursor(RoleEnum roleName,Boolean isActive,Long cursorId,int pageSize) {
           Pageable pageable = PageRequest.of(0, pageSize, Sort.by("id").ascending());
           return userRepository.findByRoleAndStatusAfterCursor(roleName, isActive, cursorId, pageable);
    }
    */








}
