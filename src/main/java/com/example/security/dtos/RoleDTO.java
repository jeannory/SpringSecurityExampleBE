package com.example.security.dtos;

import com.example.security.entities.Role;

public class RoleDTO extends SuperDTO{

    private String name;

    public RoleDTO() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Class getEntityClass() {
        return Role.class;
    }
}
