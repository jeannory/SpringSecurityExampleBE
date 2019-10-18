package com.example.security.repositories;

import com.example.security.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.CrossOrigin;

@CrossOrigin("*")
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
    @Query(value = "select * from cuisine_user u where u.email ilike :paramEmail", nativeQuery=true)
    User selectMyUserByEmail(@Param("paramEmail") String email);
}
