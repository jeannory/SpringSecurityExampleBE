package com.example.security.repositories;

import com.example.security.entities.Role;
import com.example.security.entities.User;
import com.example.security.enums.Gender;
import com.example.security.enums.Status;
import com.example.security.utils.BuilderUtils1;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@RunWith(SpringRunner.class)
@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;

    @Before
    public void setUp() throws Exception {
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    @Test
    public void test_findById_when_user_exist_should_return_user(){
        //given
        final User user = BuilderUtils1.buildUser("jean@jean.com", "1234", Gender.Monsieur, "Jean", "Leroy",
                "0101010101", "9 rue du roi", "75018", "Paris", "9ème étage", Status.ACTIVE);
        userRepository.save(user);

        //when
        final Optional<User> result = userRepository.findById(1L);

        //then
        //incrementing start at 1
        Assert.assertTrue(1L == result.get().getId());
        Assert.assertEquals("jean@jean.com", result.get().getEmail());
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    @Test
    public void test_findById_when_user_no_exist_should_return_optional_empty(){
        //given & when
        final Optional<User> result = userRepository.findById(1L);

        //then
        Assert.assertEquals(Optional.empty(), result);
    }

    @Test
    public void test_findAll_when_users_exist_should_return_list(){
        //given
        final User user1 = BuilderUtils1.buildUser("1-jean@gmail.com", "1234", Gender.Monsieur, "Jean", "Leroy",
                "0101010101", "9 rue du roi", "75018", "Paris", "9ème étage", Status.ACTIVE);
        final User user2 = BuilderUtils1.buildUser("2-jean@gmail.com", "1234", Gender.Monsieur, "Jean", "Leroy",
                "0101010101", "9 rue du roi", "75018", "Paris", "9ème étage", Status.ACTIVE);
        final User user3 = BuilderUtils1.buildUser("3-jean@gmail.com", "1234", Gender.Monsieur, "Jean", "Leroy",
                "0101010101", "9 rue du roi", "75018", "Paris", "9ème étage", Status.ACTIVE);
        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);

        //when
        final List<User> results = userRepository.findAll();

        //then
        Assert.assertEquals(3,results.size());
        /**
         * all emails have been founded
         */
        AtomicInteger emailsFound = new AtomicInteger();
        results.forEach(
                user->{
                    if(user.getEmail().equals("1-jean@gmail.com")){
                        emailsFound.addAndGet(1);
                    }
                    else if(user.getEmail().equals("2-jean@gmail.com")){
                        emailsFound.addAndGet(1);
                    }
                    else if(user.getEmail().equals("3-jean@gmail.com")){
                        emailsFound.addAndGet(1);
                    }
                }
        );
        Assert.assertEquals(3, emailsFound.get());
    }

    @Test
    public void test_findAll_when_users_no_exist_should_return_emptyList(){
        //given & when
        final List<User> results = userRepository.findAll();

        //then
        Assert.assertEquals(0, results.size());
        Assert.assertEquals(Collections.emptyList(), results);
    }

    @Test
    public void test_findByEmail_when_all_parameters_valid_should_return_result() {
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
        final User user = BuilderUtils1.buildUser("jean@jean.com", "1234", Gender.Monsieur, "Jean", "Leroy",
                "0101010101", "9 rue du roi", "75018", "Paris", "9ème étage", Status.ACTIVE);
        user.setRoles(roles);
        userRepository.save(user);

        //when
        final User result = userRepository.findByEmail("jean@jean.com");

        //then
        Assert.assertEquals("jean@jean.com", result.getEmail());
        Assert.assertEquals(3, result.getRoles().size());
    }

    @Test
    public void test_findByEmail_when_user_no_have_roles_should_return_user() {
        //given
        final User user = BuilderUtils1.buildUser("jean@jean.com", "1234", Gender.Monsieur, "Jean", "Leroy",
                "0101010101", "9 rue du roi", "75018", "Paris", "9ème étage", Status.ACTIVE);
        userRepository.save(user);

        //when
        final User result = userRepository.findByEmail("jean@jean.com");

        //then
        Assert.assertEquals("jean@jean.com", result.getEmail());
        Assert.assertEquals(null, result.getRoles());
    }

    @Test
    public void test_findByEmail_when_no_user_found_should_return_null() {
        //given && when
        final User result = userRepository.findByEmail("jean@jean.com");

        //then
        Assert.assertNull("return null", result);
    }

    @Test
    public void test_selectMyUserByEmail_when_all_parameters_valid_should_return_result() {
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
        final User user = BuilderUtils1.buildUser("jean@jean.com", "1234", Gender.Monsieur, "Jean", "Leroy", "0101010101",
                "9 rue du roi", "75018", "Paris", "9ème étage", Status.ACTIVE);
        user.setRoles(roles);
        userRepository.save(user);

        //when
        final User result = userRepository.selectMyUserByEmail("jean@jean.com");

        //then
        Assert.assertEquals("jean@jean.com", result.getEmail());
        Assert.assertEquals(3, result.getRoles().size());
    }

    @Test
    public void test_selectMyUserByEmail_when_user_no_have_roles_should_return_user() {
        //given
        final User user = BuilderUtils1.buildUser("jean@jean.com", "1234", Gender.Monsieur, "Jean", "Leroy", "0101010101",
                "9 rue du roi", "75018", "Paris", "9ème étage", Status.ACTIVE);
        userRepository.save(user);

        //when
        final User result = userRepository.selectMyUserByEmail("jean@jean.com");

        //then
        Assert.assertEquals("jean@jean.com", result.getEmail());
        Assert.assertEquals(null, result.getRoles());
    }

    @Test
    public void test_selectMyUserByEmail_when_no_user_found_should_return_null() {
        //given && when
        final User result = userRepository.selectMyUserByEmail("jean@jean.com");

        //then
        Assert.assertNull("return null", result);
    }
}