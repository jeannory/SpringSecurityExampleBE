package com.example.security.entities;

import com.example.security.dtos.SpaceDTO;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "cuisine_space")
public class Space extends SuperEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Space() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public Class getDTOClass() {
        return SpaceDTO.class;
    }
}
