package com.example.security.services.impl;

import com.example.security.converter.SuperModelMapper;
import com.example.security.dtos.UserDTO;
import com.example.security.entities.User;
import com.example.security.enums.Gender;
import com.example.security.enums.Status;
//import com.example.security.exceptions.CustomConverterException;
import com.example.security.repositories.UserRepository;
import com.example.security.utils.BuilderUtils1;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class UserServiceImplTestX {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SuperModelMapper superModelMapper;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    //method and test should be upgrade because space could'nt be null
    @Test
    public void test_changeUserSatus_should_return_value_when_all_parameters_valid()  {
        //given
        final UserDTO userDTO = BuilderUtils1.buildUserDTO(1L, "jean@jean.com", "1234", Gender.Monsieur, "Jean", "Leroy", "0101010101",
                "9 rue du roi", "75018", "Paris", "9ème étage", null, "USER, MANAGER, ADMIN", Status.ACTIVE);

        final User user = BuilderUtils1.buildUser(1L, "jean@jean.com", "1234", Gender.Monsieur, "Jean", "Leroy", "0101010101",
                "9 rue du roi", "75018", "Paris", "9ème étage", Status.ACTIVE, Arrays.asList(Arrays.asList("1","USER"), Arrays.asList("2","COOKER"), Arrays.asList("3","ADMIN")));
        Mockito.when(userRepository.findById(Mockito.eq(userDTO.getId()))).thenReturn(Optional.of(user));
        user.setStatus(Status.INACTIVE);
        List<User> users = Arrays.asList(
                user,
                BuilderUtils1.buildUser(2L, "jeanne@jeanne.com", "1234", Gender.Monsieur, "Jeanne", "Leroy", "0101010101",
                        "9 rue du roi", "75018", "Paris", "9ème étage", Status.ACTIVE, Arrays.asList(Arrays.asList("1","USER"), Arrays.asList("2","COOKER"), Arrays.asList("3","ADMIN"))));
        Mockito.when(userRepository.findAll()).thenReturn(users);
        userDTO.setStatus(Status.INACTIVE);
        List<UserDTO> userDTOS = Arrays.asList(
                userDTO,
                BuilderUtils1.buildUserDTO(2L, "jeanne@jeanne.com", "1234", Gender.Monsieur, "Jeanne", "Leroy", "0101010101",
                        "9 rue du roi", "75018", "Paris", "9ème étage", null, "USER, MANAGER, ADMIN", Status.ACTIVE)
        );
        Mockito.when(superModelMapper.convertToDTOs(Mockito.eq(users))).thenReturn(userDTOS);

        //when
        List<UserDTO> result = userService.changeUserSatus(userDTO);

        //then
        Assert.assertEquals(Status.INACTIVE, result.get(0).getStatus());
        Assert.assertEquals(Status.ACTIVE, result.get(1).getStatus());
    }
}
