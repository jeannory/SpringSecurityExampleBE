package com.example.security.services.impl;

import com.example.security.converter.SuperModelMapper;
import com.example.security.dtos.UserDTO;
import com.example.security.entities.User;
import com.example.security.enums.Gender;
import com.example.security.enums.Status;
import com.example.security.exceptions.CustomConverterException;
import com.example.security.repositories.RoleRepository;
import com.example.security.repositories.UserRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.*;

@PrepareForTest({UserServiceImpl.class})
public class UserServiceImplTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private SuperModelMapper superModelMapper;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    //@test without @InjectMock
    //loadUserByUsername(String email) == new User(String userName, String password, Set<GrantedAuthority> authorities)
    @Test
    public void test_loadUserByUsername_should_return_values_when_has_all_parameters() throws Exception {
        //given
        final User user = BuilderUtils.buildUser(1L, "jean@jean.com", "1234", Gender.Monsieur, "Jean", "Leroy", "0101010101",
                "9 rue du roi", "75018", "Paris", "9ème étage", Status.ACTIVE, Arrays.asList("ADMIN", "COOKER", "USER"));
        final Set<GrantedAuthority> simpleGrantedAuthorities = BuilderUtils.buildAuthorities(Arrays.asList("ROLE_ADMIN", "ROLE_COOKER", "ROLE_USER"));
        final org.springframework.security.core.userdetails.User userDetail = BuilderUtils.buildUserDetails(user.getEmail(), user.getPassword(), simpleGrantedAuthorities);
        final UserServiceImpl userService1 = Mockito.mock(UserServiceImpl.class);
        Mockito.when(userService1.loadUserByUsername("jean@jean.com")).thenReturn(userDetail);

        //when
        final UserDetails userDetails = userService1.loadUserByUsername("jean@jean.com");

        //then
        Assert.assertEquals("jean@jean.com", userDetails.getUsername());
        Assert.assertEquals("1234", userDetails.getPassword());
        Assert.assertEquals(BuilderUtils.buildAuthorities(Arrays.asList("ROLE_ADMIN", "ROLE_COOKER", "ROLE_USER")), userDetails.getAuthorities());
    }

    @Test
    public void test_loadUserByUsername_should_return_null_throw_exception_without_id() throws UsernameNotFoundException {
        //given
        final User user = BuilderUtils.buildUser(null, "jean@jean.com", "1234", Gender.Monsieur, "Jean", "Leroy", "0101010101",
                "9 rue du roi", "75018", "Paris", "9ème étage", Status.ACTIVE, Arrays.asList("ADMIN", "COOKER", "USER"));
        Mockito.when(userRepository.selectMyUserByEmail(Mockito.eq("jean@jean.com"))).thenReturn(user);

        //when
        final UserDetails userDetails = userService.loadUserByUsername("jean@jean.com");

        //then
        Assert.assertNull("return null", userDetails);
    }

    @Test
    public void test_loadUserByUsername_should_return_null_throw_exception_without_email() throws UsernameNotFoundException {
        //given
        final User user = BuilderUtils.buildUser(1L, null, "1234", Gender.Monsieur, "Jean", "Leroy", "0101010101",
                "9 rue du roi", "75018", "Paris", "9ème étage", Status.ACTIVE, Arrays.asList("ADMIN", "COOKER", "USER"));
        Mockito.when(userRepository.selectMyUserByEmail(Mockito.eq("jean@jean.com"))).thenReturn(user);

        //when
        final UserDetails userDetails = userService.loadUserByUsername("jean@jean.com");

        //then
        Assert.assertNull("return null", userDetails);
    }

    @Test
    public void test_loadUserByUsername_should_return_null_throw_exception_without_password() throws UsernameNotFoundException {
        //given
        final User user = BuilderUtils.buildUser(1L, "jean@jean.com", null, Gender.Monsieur, "Jean", "Leroy", "0101010101",
                "9 rue du roi", "75018", "Paris", "9ème étage", Status.ACTIVE, Arrays.asList("ADMIN", "COOKER", "USER"));
        Mockito.when(userRepository.selectMyUserByEmail(Mockito.eq("jean@jean.com"))).thenReturn(user);

        //when
        final UserDetails userDetails = userService.loadUserByUsername("jean@jean.com");

        //then
        Assert.assertNull("return null", userDetails);
    }


    @Test
    public void test_loadUserByUsername_should_return_null_throw_exception_without_roles() throws UsernameNotFoundException {
        //given
        final User user = BuilderUtils.buildUser(1L, "jean@jean.com", "1234", Gender.Monsieur, "Jean", "Leroy", "0101010101",
                "9 rue du roi", "75018", "Paris", "9ème étage", Status.ACTIVE);
        Mockito.when(userRepository.selectMyUserByEmail(Mockito.eq("jean@jean.com"))).thenReturn(user);

        //when
        final UserDetails userDetails = userService.loadUserByUsername("jean@jean.com");

        //then
        Assert.assertNull("return null", userDetails);
    }

    @Test
    public void test_loadUserByUsername_should_return_null_throw_exception_without_user() throws UsernameNotFoundException {
        //given
        Mockito.when(userRepository.selectMyUserByEmail(Mockito.eq("jean@jean.com"))).thenThrow(new UsernameNotFoundException("Invalid user"));

        //when
        UserDetails userDetails = userService.loadUserByUsername("jean@jean.com");

        //then
        Assert.assertNull("return null", userDetails);
    }

    @Test
    public void test_loadUserByUsername_when_user_not_found() throws Exception {
        //given
        Mockito.when(userRepository.selectMyUserByEmail(Mockito.eq("jean@jean.com"))).thenReturn(null);

        //when
        final UserDetails userDetails = userService.loadUserByUsername("jean@jean.com");

        //then
        Assert.assertNull("return null", userDetails);
    }

    //method and test should be upgrade because space could'nt be null
    @Test
    public void test_changeUserSatus_should_return_value_when_all_parameters_valid() throws CustomConverterException {
        //given
        final UserDTO userDTO = BuilderUtils.buildUserDTO(1L, "jean@jean.com", "1234", Gender.Monsieur, "Jean", "Leroy", "0101010101",
                "9 rue du roi", "75018", "Paris", "9ème étage", null, "USER, MANAGER, ADMIN", Status.ACTIVE);

        final User user = BuilderUtils.buildUser(1L, "jean@jean.com", "1234", Gender.Monsieur, "Jean", "Leroy", "0101010101",
                "9 rue du roi", "75018", "Paris", "9ème étage", Status.ACTIVE, Arrays.asList("ADMIN", "MANAGER", "USER"));
        Mockito.when(userRepository.findById(Mockito.eq(userDTO.getId()))).thenReturn(Optional.of(user));
        user.setStatus(Status.INACTIVE);
        List<User> users = Arrays.asList(
                user,
                BuilderUtils.buildUser(2L, "jeanne@jeanne.com", "1234", Gender.Monsieur, "Jeanne", "Leroy", "0101010101",
                        "9 rue du roi", "75018", "Paris", "9ème étage", Status.ACTIVE, Arrays.asList("ADMIN", "MANAGER", "USER")));
        Mockito.when(userRepository.findAll()).thenReturn(users);
        userDTO.setStatus(Status.INACTIVE);
        List<UserDTO> userDTOS = Arrays.asList(
                userDTO,
                BuilderUtils.buildUserDTO(2L, "jeanne@jeanne.com", "1234", Gender.Monsieur, "Jeanne", "Leroy", "0101010101",
                        "9 rue du roi", "75018", "Paris", "9ème étage", null, "USER, MANAGER, ADMIN", Status.ACTIVE)
        );
        Mockito.when(superModelMapper.convertToDTOs(Mockito.eq(users))).thenReturn(Optional.of(userDTOS));

        //when
        List<UserDTO> result = userService.changeUserSatus(userDTO);

        //then
        Assert.assertEquals(Status.INACTIVE, result.get(0).getStatus());
        Assert.assertEquals(Status.ACTIVE, result.get(1).getStatus());
    }
}
