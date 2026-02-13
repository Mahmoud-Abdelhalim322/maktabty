package com.maktabty.ktaby.services;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.maktabty.ktaby.entities.Role;
import com.maktabty.ktaby.entities.User;
import com.maktabty.ktaby.repositories.RoleRepository;
import com.maktabty.ktaby.repositories.UserRepository;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    
    }
    public User loginUser(User user){
        User existingUser = userRepository.findByUsername(user.getUsername());
        if (existingUser != null && passwordEncoder.matches(user.getPassword(), existingUser.getPassword())) {
            return existingUser;
        }
        throw new IllegalArgumentException("Invalid username or password");
        
    }

    public User registerNewUser(User user) {
        logger.info("Registering new user: {}", user.getUsername());
        if (user.getUsername() != null && userRepository.findByUsername(user.getUsername()) != null) {
            throw new IllegalArgumentException("Username already exists");
        }
        if (user.getEmail() != null && userRepository.findByEmail(user.getEmail()) != null) {
            throw new IllegalArgumentException("Email already exists");
        }
        String pw = user.getPassword() == null ? "" : user.getPassword();
        if (pw.length() < 8 || pw.length() > 16) {
            throw new IllegalArgumentException("Password must be between 8 and 16 characters");
        }
        user.setPassword(passwordEncoder.encode(pw));
        Role memberRole = roleRepository.findByName("ROLE_MEMBER");
        if (memberRole == null) {
            memberRole = new Role("ROLE_MEMBER");
            roleRepository.save(memberRole);
        }
        user.setRoles(Set.of(memberRole));
        User savedUser = userRepository.save(user);
        logger.info("User registered successfully: {}", savedUser.getUsername());
        return savedUser;
    }
}
