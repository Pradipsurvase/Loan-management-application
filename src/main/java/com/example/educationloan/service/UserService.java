package com.example.educationloan.service;

import com.example.educationloan.entity.Role;
import com.example.educationloan.entity.User;
import com.example.educationloan.enumconstant.RoleEnum;
import com.example.educationloan.exception.DuplicateResourceException;
import com.example.educationloan.exception.ResourceNotFoundException;
import com.example.educationloan.repository.RoleRepository;
import com.example.educationloan.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {


    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    /*
     * Get user by ID
     */
    @Transactional(readOnly = true)
    public User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    /*
    * get all users
    * */

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }


    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found with id: " + id);
        }else{
            userRepository.deleteById(id);
            log.info("User with id {} deleted successfully", id);
        }

    }


    /*
     *get user by username
     */
    @Transactional(readOnly = true)
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));
    }




    /*
     * Get user by email
     */
    @Transactional(readOnly = true)
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
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
        Role role=roleRepository.findByName(roleName).orElseThrow(()->new ResourceNotFoundException("Role not found with name: "+roleName));
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
        return user.getRoles().stream().anyMatch(role->role.getName().equals(RoleEnum.ADMIN));
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
        throw new DuplicateResourceException("Username is already exists: " + username);
    }
    if (userRepository.existsByEmail(email)) {
        throw new DuplicateResourceException("Email already exists: " + email);
    }
    Role defaultRole = roleRepository.findByName(RoleEnum.USER).orElseThrow(() -> new ResourceNotFoundException("Default role not found: " + RoleEnum.USER));
    User user = User.builder()
            .username(username)
            .email(email)
            .password(passwordEncoder.encode(password))
            .firstName(firstName)
            .lastName(lastName)
            .createdAt(LocalDateTime.now())
            .isActive(true)
            .isEmailVerified(false)
            .build();
        user.getRoles().add(defaultRole);
        userRepository.save(user);

    log.info("User {} with email {} created successfully", username, email);
    return user;
}

/*
* filter users by role and active status
**/
    @Transactional(readOnly = true)
 public List<User> filterUsersByRoleAndStatus(RoleEnum roleName, Boolean isActive) {
        return userRepository.findByRoleAndStatus(roleName, isActive);
    }

    @Transactional
    public User updateUser(Long id, String username, String email, String password, String firstName, String lastName) {
        User user = getUserById(id);

        Optional.ofNullable(username).filter(u -> !u.isBlank()).ifPresent(uname -> {
                    boolean usernameTaken = userRepository.existsByUsername(uname) && !user.getUsername().equals(uname);
                    if (usernameTaken) {
                        throw new RuntimeException("Username already exists: " + uname);
                    }
                    user.setUsername(uname);
                });

        Optional.ofNullable(email).filter(e -> !e.isBlank()).ifPresent(e -> {
                    boolean emailTaken = userRepository.existsByEmail(e) && !user.getEmail().equals(e);
                    if (emailTaken) {
                        throw new RuntimeException("Email already exists: " + e);
                    }
                    user.setEmail(e);
                });

        Optional.ofNullable(password).filter(p -> !p.isBlank()).map(passwordEncoder::encode).ifPresent(user::setPassword);
        Optional.ofNullable(firstName).filter(fn -> !fn.isBlank()).ifPresent(user::setFirstName);
        Optional.ofNullable(lastName).filter(ln -> !ln.isBlank()).ifPresent(user::setLastName);
        userRepository.save(user);
        log.info("User {} updated successfully", user.getUsername());
        return user;
    }

    public User patchUser(Long id, String username, String email, String password, String firstName, String lastName) {
        User existingUser = getUserById(id);
        Optional.ofNullable(username).ifPresent(existingUser::setUsername);
        Optional.ofNullable(email).ifPresent(existingUser::setEmail);
        Optional.ofNullable(password).filter(p -> !p.isBlank()).map(passwordEncoder::encode).ifPresent(existingUser::setPassword);
        Optional.ofNullable(firstName).ifPresent(existingUser::setFirstName);
        Optional.ofNullable(lastName).ifPresent(existingUser::setLastName);
        return userRepository.save(existingUser);
    }

        /*
    * @Transactional(readOnly = true)
public List<User> filterUsersByRoleAndStatusCursor(RoleEnum roleName,Boolean isActive,Long cursorId,int pageSize) {
           Pageable pageable = PageRequest.of(0, pageSize, Sort.by("id").ascending());
           return userRepository.findByRoleAndStatusAfterCursor(roleName, isActive, cursorId, pageable);
    }
    */

}
