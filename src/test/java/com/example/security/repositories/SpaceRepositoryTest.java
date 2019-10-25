package com.example.security.repositories;

import com.example.security.entities.Space;
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
import org.springframework.test.context.junit4.SpringRunner;
import java.util.Optional;

//https://www.javaguides.net/2018/09/spring-data-jpa-repository-testing-using-spring-boot-datajpatest.html
@RunWith(SpringRunner.class)
@DataJpaTest
public class SpaceRepositoryTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SpaceRepository spaceRepository;

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void test_findByUserEmail_when_all_parameters_valid_should_return_result() {
        //given
        final User user = BuilderUtils1.buildUser("jean@jean.com", "1234", Gender.Monsieur, "Jean", "Leroy", "0101010101",
                "9 rue du roi", "75018", "Paris", "9ème étage", Status.ACTIVE);
        userRepository.save(user);
        final Space space = new Space();
        space.setName("jean space");
        space.setUser(user);
        spaceRepository.save(space);

        //when
        Optional<Space> result = spaceRepository.findByUserEmail("jean@jean.com");

        //then
        Assert.assertEquals("jean@jean.com", result.get().getUser().getEmail());
    }

    @Test
    public void test_findByUserEmail_when_no_user_found_should_return_optional_empty() {
        //given
        Optional<Space> result = spaceRepository.findByUserEmail("jean@jean.com");

        Assert.assertEquals(Optional.empty(), result);
    }
}