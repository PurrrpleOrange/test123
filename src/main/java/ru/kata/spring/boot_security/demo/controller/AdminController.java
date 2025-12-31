package ru.kata.spring.boot_security.demo.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.repository.RoleRepository;
import ru.kata.spring.boot_security.demo.service.UserService;
import ru.kata.spring.boot_security.demo.service.UserServiceImpl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final RoleRepository roleRepository;
    private final UserService userServiceImpl;
    private final PasswordEncoder passwordEncoder;

    public AdminController(UserService userServiceImpl,
                           RoleRepository roleRepository,
                           PasswordEncoder passwordEncoder) {
        this.userServiceImpl = userServiceImpl;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public String admin(Model model, Authentication authentication) {
        fillHeader(model, authentication);
        model.addAttribute("users", userServiceImpl.findAll());
        model.addAttribute("allRoles", roleRepository.findAll());
        return "users-list";
    }

    @GetMapping("/edit")
    public String edit(@RequestParam("id") Long id, Model model, Authentication authentication) {
        fillHeader(model, authentication);
        model.addAttribute("user", userServiceImpl.findById(id));
        model.addAttribute("allRoles", roleRepository.findAll());
        return "user-form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute("user") User user,
                       @RequestParam(value = "roleIds", required = false) List<Long> roleIds,
                       @RequestParam(value = "rawPassword", required = false) String rawPassword) {

        if (user.getUsername() == null || user.getUsername().isBlank()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }

        Set<Role> roles = new HashSet<>();
        if (roleIds != null) {
            roles.addAll(roleRepository.findAllById(roleIds));
        }
        user.setRoles(roles);

        boolean creating = (user.getId() == null);

        if (rawPassword != null && !rawPassword.isBlank()) {
            user.setPassword(passwordEncoder.encode(rawPassword));
        } else if (!creating) {
            User fromDb = userServiceImpl.findById(user.getId());
            user.setPassword(fromDb.getPassword());
        } else {
            throw new IllegalArgumentException("Password cannot be empty for new user");
        }

        userServiceImpl.save(user);
        return "redirect:/admin";
    }


    @PostMapping("/delete")
    public String delete(@RequestParam("id") Long id) {
        userServiceImpl.deleteById(id);
        return "redirect:/admin";
    }

    private void fillHeader(Model model, Authentication authentication) {
        if (authentication == null) {
            model.addAttribute("currentEmail", "");
            model.addAttribute("currentRoles", "");
            return;
        }
        model.addAttribute("currentEmail", authentication.getName());
        model.addAttribute("currentRoles", authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" ")));
    }
}