package com.example.security.dtos;

public abstract class SuperDTO{

    private Long id;

    public SuperDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public abstract Class getEntityClass();
}
