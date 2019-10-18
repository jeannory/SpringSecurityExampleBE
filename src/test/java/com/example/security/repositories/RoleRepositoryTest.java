package com.example.security.repositories;

import com.example.security.entities.Role;
import com.example.security.entities.User;
import com.example.security.enums.Gender;
import com.example.security.enums.Status;
import com.example.security.utils.BuilderUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;

@RunWith(SpringRunner.class)
@DataJpaTest
public class RoleRepositoryTest {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void test_findByName_when_all_parameters_valid_should_return_result() {
        //given
        final Role role1 = new Role();
        role1.setName("USER");
        final Role role2 = new Role();
        role2.setName("MANAGER");
        final Role role3 = new Role();
        role3.setName("ADMIN");
        final Set<Role> roles  = new HashSet<>(Arrays.asList(role1, role2, role3));
        roles.forEach(role -> {
            roleRepository.save(role);
        });

        //when
        final Role result = roleRepository.findByName("USER");

        //then
        Assert.assertEquals("USER", result.getName());
    }

    @Test
    public void test_findByName_when_role_not_found__should_return_null() {
        //given && when
        final Role result = roleRepository.findByName("USER");

        //then
        Assert.assertNull("return null", result);
    }

    @Test
    public void test_findByUsersEmail_when_all_parameters_valid_should_return_results() {
        //given
        final Role role1 = new Role();
        role1.setName("USER");
        final Role role2 = new Role();
        role2.setName("MANAGER");
        final Role role3 = new Role();
        role3.setName("ADMIN");
        final Set<Role> roles  = new HashSet<>(Arrays.asList(role1, role2, role3));
        roles.forEach(role -> {
            roleRepository.save(role);
        });
        final User user = BuilderUtils.buildUser("jean@jean.com", "1234", Gender.Monsieur, "Jean", "Leroy", "0101010101",
                "9 rue du roi", "75018", "Paris", "9ème étage", Status.ACTIVE);
        user.setRoles(roles);
        userRepository.save(user);

        //when
        final List<Role> results = roleRepository.findByUsersEmail("jean@jean.com");

        //then
        Assert.assertEquals(3, results.size());
    }

    @Test
    public void test_findByUsersEmail_when_user_no_have_roles_should_return_emptyList() {
        //given
        final User user = BuilderUtils.buildUser("jean@jean.com", "1234", Gender.Monsieur, "Jean", "Leroy", "0101010101",
                "9 rue du roi", "75018", "Paris", "9ème étage", Status.ACTIVE);
        userRepository.save(user);

        //when
        final List<Role> results = roleRepository.findByUsersEmail("jean@jean.com");

        //then
        Assert.assertEquals(Collections.emptyList(), results);
    }

    @Test
    public void test_findByUsersEmail_when_user_no_found_should_return_emptyList() {
        //given && when
        final List<Role> results = roleRepository.findByUsersEmail("jean@jean.com");

        //then
        Assert.assertEquals(Collections.emptyList(), results);
    }
}