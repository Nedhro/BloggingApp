package com.prodev.bloggingservice.log;


import com.prodev.bloggingservice.model.Role;
import com.prodev.bloggingservice.model.User;
import com.prodev.bloggingservice.model.enums.Status;
import com.prodev.bloggingservice.repository.RoleRepository;
import com.prodev.bloggingservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import java.util.Arrays;

@Component
public class InitialDataLoader implements ApplicationListener<ContextRefreshedEvent> {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    boolean alreadySetup = false;
    @Value("${auth.adminuser}")
    private String adminUser;
    @Value("${auth.adminpassword}")
    private String adminPassword;

    public InitialDataLoader(RoleRepository roleRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;

    }

    @Override
    @Transactional
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        if (alreadySetup)
            return;
        String adminRoleName = "Admin";
        String userRoleName = "Blogger";
        Role role =null;
        Role userRole = null;
        int roleExist = roleRepository.countByRoleName(adminRoleName);
        int userRoleExist = roleRepository.countByRoleName(userRoleName);
        if (roleExist == 1 && userRoleExist == 1) {
            role = roleRepository.findByRoleName(adminRoleName);
            userRole = roleRepository.findByRoleName(userRoleName);
        } else {
            role = new Role();
            role.setRoleName(adminRoleName);
            role.setTitle("Admin Role");
            roleRepository.save(role);
            userRole = new Role();
            userRole.setTitle("Blogger Role");
            userRole.setRoleName(userRoleName);
            roleRepository.save(userRole);
        }
        User user = userRepository.findByUsername(adminUser);
        if (user == null) {
            user = new User();
            user.setFullName("Super Admin");
            user.setUsername(adminUser);
            user.setPassword(passwordEncoder.encode(adminPassword));
            user.setEmail("admin@gmail.com");
            user.setEnabled(true);
            user.setStatus(Status.APPROVED);
        }
        user.setRoleList(Arrays.asList(role, userRole));
        userRepository.save(user);

        alreadySetup = true;
    }
}
