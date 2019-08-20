package com.example.security.repositories;

import com.example.security.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.List;

@CrossOrigin("*")
public interface RoleRepository extends JpaRepository<Role, Long> {

    Role findByName(String name);
    List<Role> findByUsersEmail(String userEmail);
}
