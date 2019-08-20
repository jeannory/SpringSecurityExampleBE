package com.example.security.entities;


import com.example.security.dtos.RoleDTO;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "cuisine_role",uniqueConstraints={@UniqueConstraint(columnNames = "name")})
public class Role extends SuperEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;

    @ManyToMany(fetch = FetchType.LAZY,
            cascade = {
                    CascadeType.PERSIST,
                    CascadeType.MERGE
            },
            mappedBy = "roles")
    private Set<User> users;

    public Role() {
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

    public void setUsers(Set<User> users) {
        this.users = users;
    }

    @Override
    public Class getDTOClass(){
        return RoleDTO.class;
    }

}
