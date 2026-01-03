package ru.kata.spring.boot_security.demo.dto;

import java.util.Set;

public class UserResponse {
    private final Long id;
    private final String firstName;
    private final String lastName;
    private final Integer age;
    private final String username;
    private final String email;
    private final Set<RoleResponse> roles;

    public UserResponse(Long id,
                        String firstName,
                        String lastName,
                        Integer age,
                        String username,
                        String email,
                        Set<RoleResponse> roles) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.username = username;
        this.email = email;
        this.roles = roles;
    }

    public Long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public Integer getAge() {
        return age;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public Set<RoleResponse> getRoles() {
        return roles;
    }
}
