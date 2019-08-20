package com.example.security.dtos;

import com.example.security.entities.Space;

public class SpaceDTO extends SuperDTO {

    private String name;
    private String userEmail;

    public SpaceDTO() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    @Override
    public Class getEntityClass() {
        return Space.class;
    }
}
