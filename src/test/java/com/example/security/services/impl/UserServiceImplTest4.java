package com.example.security.services.impl;

import com.example.security.converter.SuperModelMapper;
import com.example.security.dtos.UserDTO;
import com.example.security.entities.User;
import com.example.security.enums.Gender;
import com.example.security.enums.Status;
import com.example.security.exceptions.CustomConverterException;
import com.example.security.exceptions.CustomTransactionalException;
import com.example.security.repositories.UserRepository;
import com.example.security.services.IUserService;
import com.example.security.utils.BuilderUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.Whitebox;

import java.util.Arrays;
import java.util.Optional;

public class UserServiceImplTest4 {

    private IUserService userService;
    private UserRepository userRepository;
    private SuperModelMapper superModelMapper;

    @Before
    public void setUp() throws Exception {
        this.userService = new UserServiceImpl();
        userRepository = Mockito.mock(UserRepository.class);
        superModelMapper = Mockito.mock(SuperModelMapper.class);
        Whitebox.setInternalState(userService, "userRepository", userRepository);
        Whitebox.setInternalState(userService, "superModelMapper", superModelMapper);
    }

    @Test
    public void test_setUser_when_all_parameters_valid(){
        //given
        final UserDTO userDTO = Mockito.spy(BuilderUtils.buildUserDTO(1L, "jean@jean.com", "1234", Gender.Madame, "Jeanne", "Leroy", "0101010101",
                "9 rue du roi", "75018", "Paris", "9ème étage", null, "USER, MANAGER, ADMIN", Status.ACTIVE));

        final User user = Mockito.spy(BuilderUtils.buildUser(1L, "jean@jean.com", "1234", Gender.Monsieur, "Jean", "Leroy", "0101010101",
                "9 rue du roi", "75018", "Paris", "9ème étage", Status.ACTIVE, Arrays.asList(Arrays.asList("1","User"), Arrays.asList("2","MANAGER"), Arrays.asList("3","ADMIN"))));

        Mockito.when(userRepository.findByEmail(Mockito.anyString())).thenReturn(user);

        user.setGender(userDTO.getGender());
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setPhoneNumber((userDTO.getPhoneNumber()));
        user.setAdress(userDTO.getAdress());
        user.setZip(userDTO.getZip());
        user.setCity(userDTO.getCity());
        user.setDeliveryInformation(userDTO.getDeliveryInformation());

        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(user);

        Mockito.when(superModelMapper.convertToDTO(Mockito.any(User.class))).thenReturn(Optional.of(userDTO));

        //when
        UserDTO result = userService.setUser(userDTO);

        //then
        Assert.assertEquals(userDTO.getId(), result.getId());
        Assert.assertEquals(userDTO.getEmail(), result.getEmail());
        Assert.assertEquals(userDTO.getPhoneNumber(), result.getPhoneNumber());
        Assert.assertEquals(userDTO.getLastName(), result.getLastName());
        Assert.assertEquals(userDTO.getGender(), result.getGender());
        Assert.assertEquals(user.getGender(), result.getGender());
        Assert.assertEquals(user.getGender(), result.getGender());
    }

    @Test
    public void test_setUser_when_user_is_null(){
        //given
        final UserDTO userDTO = Mockito.spy(BuilderUtils.buildUserDTO(1L, "jean@jean.com", "1234", Gender.Madame, "Jeanne", "Leroy", "0101010101",
                "9 rue du roi", "75018", "Paris", "9ème étage", null, "USER, MANAGER, ADMIN", Status.ACTIVE));

        final User user = null;

        Mockito.when(userRepository.findByEmail(Mockito.anyString())).thenReturn(user);
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(user);

        Mockito.when(superModelMapper.convertToDTO(Mockito.any(User.class))).thenReturn(Optional.of(userDTO));

        //when
        UserDTO result = userService.setUser(userDTO);

        //then
        Assert.assertNull("return null", result);
    }


    @Test
    public void test_setUser_when_save_user_failed(){
        //given
        final UserDTO userDTO = Mockito.spy(BuilderUtils.buildUserDTO(1L, "jean@jean.com", "1234", Gender.Madame, "Jeanne", "Leroy", "0101010101",
                "9 rue du roi", "75018", "Paris", "9ème étage", null, "USER, MANAGER, ADMIN", Status.ACTIVE));

        final User user = Mockito.spy(BuilderUtils.buildUser(1L, "jean@jean.com", "1234", Gender.Monsieur, "Jean", "Leroy", "0101010101",
                "9 rue du roi", "75018", "Paris", "9ème étage", Status.ACTIVE, Arrays.asList(Arrays.asList("1","User"), Arrays.asList("2","MANAGER"), Arrays.asList("3","ADMIN"))));

        Mockito.when(userRepository.findByEmail(Mockito.anyString())).thenReturn(user);

        user.setGender(userDTO.getGender());
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setPhoneNumber((userDTO.getPhoneNumber()));
        user.setAdress(userDTO.getAdress());
        user.setZip(userDTO.getZip());
        user.setCity(userDTO.getCity());
        user.setDeliveryInformation(userDTO.getDeliveryInformation());

        Mockito.when(userRepository.save(Mockito.any(User.class))).thenThrow(new CustomTransactionalException());

        //when
        UserDTO result = userService.setUser(userDTO);

        //then
        Assert.assertNull("return null", result);
    }

    @Test
    public void test_setUser_when_convertToDTO_failed(){
        //given
        final UserDTO userDTO = Mockito.spy(BuilderUtils.buildUserDTO(1L, "jean@jean.com", "1234", Gender.Madame, "Jeanne", "Leroy", "0101010101",
                "9 rue du roi", "75018", "Paris", "9ème étage", null, "USER, MANAGER, ADMIN", Status.ACTIVE));

        final User user = Mockito.spy(BuilderUtils.buildUser(1L, "jean@jean.com", "1234", Gender.Monsieur, "Jean", "Leroy", "0101010101",
                "9 rue du roi", "75018", "Paris", "9ème étage", Status.ACTIVE, Arrays.asList(Arrays.asList("1","User"), Arrays.asList("2","MANAGER"), Arrays.asList("3","ADMIN"))));

        Mockito.when(userRepository.findByEmail(Mockito.anyString())).thenReturn(user);

        user.setGender(userDTO.getGender());
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setPhoneNumber((userDTO.getPhoneNumber()));
        user.setAdress(userDTO.getAdress());
        user.setZip(userDTO.getZip());
        user.setCity(userDTO.getCity());
        user.setDeliveryInformation(userDTO.getDeliveryInformation());

        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(user);

        Mockito.when(superModelMapper.convertToDTO(Mockito.any(User.class))).thenThrow(new CustomConverterException());

        //when
        UserDTO result = userService.setUser(userDTO);

        //then
        Assert.assertNull("return null", result);
    }

}
