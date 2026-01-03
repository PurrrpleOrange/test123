package ru.kata.spring.boot_security.demo.dto;

public class RoleResponse {
    private final Long id;
    private final String name;

    public RoleResponse(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
