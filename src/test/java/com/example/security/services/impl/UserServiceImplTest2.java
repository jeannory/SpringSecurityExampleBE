package com.example.security.services.impl;

import com.example.security.converter.SuperModelMapper;
import com.example.security.dtos.UserDTO;
import com.example.security.entities.Role;
import com.example.security.entities.User;
import com.example.security.enums.Gender;
import com.example.security.enums.Status;
import com.example.security.repositories.UserRepository;
import com.example.security.utils.BuilderUtils1;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.*;

public class UserServiceImplTest2 {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleServiceImpl roleService;

    @Mock
    private SuperModelMapper superModelMapper;

    @Mock
    org.springframework.security.core.userdetails.User userDetailMock;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void test_loadUserByUsername_with_all_parameters_valid_should_return_values() {
        //given
        Set<Role> roles1 = BuilderUtils1.buildRoles(Arrays.asList(Arrays.asList("1","USER"), Arrays.asList("2","COOKER"), Arrays.asList("3","ADMIN")));
        List<Role> roles2 = new ArrayList<>(roles1);
        final User user = BuilderUtils1.buildUser(1L, "jean@jean.com", "1234", Gender.Monsieur, "Jean", "Leroy", "0101010101",
                "9 rue du roi", "75018", "Paris", "9ème étage", Status.ACTIVE);
        user.setRoles(roles1);
        Mockito.when(userRepository.selectMyUserByEmail(Mockito.eq("jean@jean.com"))).thenReturn(user);
        Mockito.when(roleService.findByUsersEmail(Mockito.eq("jean@jean.com"))).thenReturn(new ArrayList<>(roles2));
        final Set<GrantedAuthority> simpleGrantedAuthorities = BuilderUtils1.buildAuthorities(Arrays.asList("ROLE_ADMIN", "ROLE_COOKER", "ROLE_USER"));
        Mockito.when(userDetailMock.getUsername()).thenReturn(user.getEmail());
        Mockito.when(userDetailMock.getPassword()).thenReturn(user.getPassword());
        Mockito.when(userDetailMock.getAuthorities()).thenReturn(simpleGrantedAuthorities);

        //when
        final UserDetails userDetails = userService.loadUserByUsername("jean@jean.com");

        //then
        Assert.assertEquals("jean@jean.com", userDetails.getUsername());
        Assert.assertEquals("1234", userDetails.getPassword());
        Assert.assertEquals(BuilderUtils1.buildAuthorities(Arrays.asList("ADMIN", "COOKER", "USER")), userDetails.getAuthorities());
    }

    @Test
    public void test_loadUserByUsername_without_id_should_return_null() throws UsernameNotFoundException {
        //given
        final User user = BuilderUtils1.buildUser(null, "jean@jean.com", "1234", Gender.Monsieur, "Jean", "Leroy", "0101010101",
                "9 rue du roi", "75018", "Paris", "9ème étage", Status.ACTIVE, Arrays.asList(Arrays.asList("1","USER"), Arrays.asList("2","COOKER"), Arrays.asList("3","ADMIN")));
        Mockito.when(userRepository.selectMyUserByEmail(Mockito.eq("jean@jean.com"))).thenReturn(user);

        //when
        final UserDetails userDetails = userService.loadUserByUsername("jean@jean.com");

        //then
        Assert.assertNull("return null", userDetails);
    }

    @Test
    public void test_loadUserByUsername_without_email_should_return_null() throws UsernameNotFoundException {
        //given
        final User user = BuilderUtils1.buildUser(1L, null, "1234", Gender.Monsieur, "Jean", "Leroy", "0101010101",
                "9 rue du roi", "75018", "Paris", "9ème étage", Status.ACTIVE, Arrays.asList(Arrays.asList("1","USER"), Arrays.asList("2","COOKER"), Arrays.asList("3","ADMIN")));
        Mockito.when(userRepository.selectMyUserByEmail(Mockito.eq("jean@jean.com"))).thenReturn(user);

        //when
        final UserDetails userDetails = userService.loadUserByUsername("jean@jean.com");

        //then
        Assert.assertNull("return null", userDetails);
    }

    @Test
    public void test_loadUserByUsername_without_password_should_return_null() throws UsernameNotFoundException {
        //given
        final User user = BuilderUtils1.buildUser(1L, "jean@jean.com", null, Gender.Monsieur, "Jean", "Leroy", "0101010101",
                "9 rue du roi", "75018", "Paris", "9ème étage", Status.ACTIVE, Arrays.asList(Arrays.asList("1","USER"), Arrays.asList("2","COOKER"), Arrays.asList("3","ADMIN")));
        Mockito.when(userRepository.selectMyUserByEmail(Mockito.eq("jean@jean.com"))).thenReturn(user);

        //when
        final UserDetails userDetails = userService.loadUserByUsername("jean@jean.com");

        //then
        Assert.assertNull("return null", userDetails);
    }

    //toDo see Method
//    @Test
    public void test_loadUserByUsername_without_roles_should_return_null() throws UsernameNotFoundException {
        //given
        final User user = BuilderUtils1.buildUser(1L, "jean@jean.com", "1234", Gender.Monsieur, "Jean", "Leroy", "0101010101",
                "9 rue du roi", "75018", "Paris", "9ème étage", Status.ACTIVE);
        Mockito.when(userRepository.selectMyUserByEmail(Mockito.eq("jean@jean.com"))).thenReturn(user);

        //when
        final UserDetails userDetails = userService.loadUserByUsername("jean@jean.com");

        //then
        Assert.assertNull("return null", userDetails);
    }

    @Test
    public void test_loadUserByUsername_without_user_should_return_null() throws UsernameNotFoundException {
        //given
        Mockito.when(userRepository.selectMyUserByEmail(Mockito.eq("jean@jean.com"))).thenThrow(new UsernameNotFoundException("Invalid user"));

        //when
        UserDetails userDetails = userService.loadUserByUsername("jean@jean.com");

        //then
        Assert.assertNull("return null", userDetails);
    }

    @Test
    public void test_loadUserByUsername_when_user_not_found_should_return_null() throws Exception {
        //given
        Mockito.when(userRepository.selectMyUserByEmail(Mockito.eq("jean@jean.com"))).thenReturn(null);

        //when
        final UserDetails userDetails = userService.loadUserByUsername("jean@jean.com");

        //then
        Assert.assertNull("return null", userDetails);
    }

    @Test
    public void test_findUserDTOByEmail_with_all_parameters_valid_should_return_result()  {
        //given
        final User user = BuilderUtils1.buildUser(1L, "jean@jean.com", "1234", Gender.Monsieur, "Jean", "Leroy", "0101010101",
                "9 rue du roi", "75018", "Paris", "9ème étage", Status.ACTIVE, Arrays.asList(Arrays.asList("1","USER"), Arrays.asList("2","COOKER"), Arrays.asList("3","ADMIN")));
        Mockito.when(userRepository.findByEmail(Mockito.eq("jean@jean.com"))).thenReturn(user);
        final Optional<UserDTO> userDTO = Optional.of(BuilderUtils1.buildUserDTO(1L, "jean@jean.com", "1234", Gender.Monsieur, "Jean", "Leroy", "0101010101",
                "9 rue du roi", "75018", "Paris", "9ème étage", null, "ADMIN, COOKER, USER", Status.ACTIVE));
        Mockito.when(superModelMapper.convertToDTO(user)).thenReturn(userDTO);

        //when
        final UserDTO result = userService.findUserDTOByEmail("jean@jean.com");

        //then
        Assert.assertEquals("jean@jean.com", result.getEmail());
        Assert.assertEquals("1234", result.getPassword());
        Assert.assertEquals("9 rue du roi", result.getAddress());
        Assert.assertEquals("0101010101", result.getPhoneNumber());
        Assert.assertEquals("ADMIN, COOKER, USER", result.getFlattenRoles());
    }

    @Test
    public void test_findUserDTOByEmail_with_user_not_found(){
        //given
        final User user = null;
        Mockito.when(userRepository.findByEmail(Mockito.eq("jean@jean.com"))).thenReturn(user);
        final Optional<UserDTO> userDTO = Optional.of(BuilderUtils1.buildUserDTO(1L, "jean@jean.com", "1234", Gender.Monsieur, "Jean", "Leroy", "0101010101",
                "9 rue du roi", "75018", "Paris", "9ème étage", null, "ADMIN, COOKER, USER", Status.ACTIVE));
        Mockito.when(superModelMapper.convertToDTO(user)).thenReturn(userDTO);

        //when
        final UserDTO result = userService.findUserDTOByEmail("jean@jean.com");

        //then
        Assert.assertNull("return null", result);
    }

    @Test
    public void test_findUserDTOByEmail_with_conversion_return_null()  {
        //given
        final User user = BuilderUtils1.buildUser(1L, "jean@jean.com", "1234", Gender.Monsieur, "Jean", "Leroy", "0101010101",
                "9 rue du roi", "75018", "Paris", "9ème étage", Status.ACTIVE, Arrays.asList(Arrays.asList("1","USER"), Arrays.asList("2","COOKER"), Arrays.asList("3","ADMIN")));
        Mockito.when(userRepository.findByEmail(Mockito.eq("jean@jean.com"))).thenReturn(user);
        final Optional<UserDTO> userDTO = Optional.empty();
        Mockito.when(superModelMapper.convertToDTO(user)).thenReturn(userDTO);

        //when
        final UserDTO result = userService.findUserDTOByEmail("jean@jean.com");

        //then
        Assert.assertNull("return null", result);
    }

    @Test
    public void test_findUserDTOByEmail_when_convertToDTO_return_optional_empty_should_return_null() {
        //given
        final User user = BuilderUtils1.buildUser(1L, "jean@jean.com", "1234", Gender.Monsieur, "Jean", "Leroy", "0101010101",
                "9 rue du roi", "75018", "Paris", "9ème étage", Status.ACTIVE, Arrays.asList(Arrays.asList("1","USER"), Arrays.asList("2","COOKER"), Arrays.asList("3","ADMIN")));
        Mockito.when(userRepository.findByEmail(Mockito.eq("jean@jean.com"))).thenReturn(user);
        Mockito.when(superModelMapper.convertToDTO(user)).thenReturn(Optional.empty());

        //when
        final UserDTO result = userService.findUserDTOByEmail("jean@jean.com");

        //then
        Assert.assertNull("return null", result);
    }

}
