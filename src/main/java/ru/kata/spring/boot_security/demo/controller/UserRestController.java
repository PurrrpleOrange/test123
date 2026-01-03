package ru.kata.spring.boot_security.demo.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.dto.RoleResponse;
import ru.kata.spring.boot_security.demo.dto.UserRequest;
import ru.kata.spring.boot_security.demo.dto.UserResponse;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class UserRestController {

    private final UserService userService;
    private final RoleService roleService;

    public UserRestController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping("/users")
    public List<UserResponse> allUsers() {
        return userService.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @GetMapping("/users/{id}")
    public UserResponse userById(@PathVariable Long id) {
        return toResponse(userService.findById(id));
    }

    @GetMapping("/users/current")
    public UserResponse currentUser(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return toResponse(userService.findById(user.getId()));
    }

    @GetMapping("/roles")
    public List<RoleResponse> allRoles() {
        return roleService.findAll().stream()
                .map(role -> new RoleResponse(role.getId(), role.getName()))
                .collect(Collectors.toList());
    }

    @PostMapping("/users")
    public ResponseEntity<UserResponse> create(@RequestBody UserRequest request) {
        User user = toEntity(request);
        userService.saveUser(user, request.getRoleIds(), request.getRawPassword());
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(user));
    }

    @PutMapping("/users/{id}")
    public UserResponse update(@PathVariable Long id,
                               @RequestBody UserRequest request,
                               Authentication authentication) {
        User user = toEntity(request);
        user.setId(id);
        userService.saveUser(user, request.getRoleIds(), request.getRawPassword());
        User updatedUser = userService.findById(id);
        refreshAuthenticationIfCurrentUser(updatedUser, authentication);
        return toResponse(updatedUser);
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private UserResponse toResponse(User user) {
        Set<RoleResponse> roles = user.getRoles().stream()
                .map(role -> new RoleResponse(role.getId(), role.getName()))
                .collect(Collectors.toSet());
        return new UserResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getAge(),
                user.getUsername(),
                user.getEmail(),
                roles
        );
    }

    private User toEntity(UserRequest request) {
        User user = new User();
        user.setId(request.getId());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setAge(request.getAge());
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        return user;
    }

    private void refreshAuthenticationIfCurrentUser(User updatedUser, Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof User)) {
            return;
        }
        User currentUser = (User) authentication.getPrincipal();
        if (!updatedUser.getId().equals(currentUser.getId())) {
            return;
        }
        UsernamePasswordAuthenticationToken newAuth = new UsernamePasswordAuthenticationToken(
                updatedUser,
                authentication.getCredentials(),
                updatedUser.getAuthorities()
        );
        newAuth.setDetails(authentication.getDetails());
        SecurityContextHolder.getContext().setAuthentication(newAuth);
    }
}
