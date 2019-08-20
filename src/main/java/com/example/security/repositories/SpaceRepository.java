package com.example.security.repositories;

import com.example.security.entities.Space;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.Optional;

@CrossOrigin("*")
public interface SpaceRepository extends JpaRepository<Space, Long> {
    Optional<Space> findByUserEmail(String email);
}
