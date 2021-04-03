package com.prodev.bloggingservice.controller;

import com.prodev.bloggingservice.annotations.ApiController;
import com.prodev.bloggingservice.model.Role;
import com.prodev.bloggingservice.model.User;
import com.prodev.bloggingservice.model.dto.Response;
import com.prodev.bloggingservice.repository.RoleRepository;
import com.prodev.bloggingservice.service.UserService;
import com.prodev.bloggingservice.util.UrlConstraints;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Arrays;

@ApiController
@RequestMapping(value = UrlConstraints.UserManagement.ROOT)
public class UserController {
    private final UserService userService;
    private final RoleRepository roleRepository;

    public UserController(UserService userService, RoleRepository roleRepository) {
        this.userService = userService;
        this.roleRepository = roleRepository;
    }

    @PostMapping
    public Response saveUser(@RequestBody User user) {
        Role role = roleRepository.findByRoleName("Blogger");
        user.setRoleList(Arrays.asList(role));
        return userService.save(user);
    }

}
