package com.example.security.services.impl;

import com.example.security.converter.SuperModelMapper;
import com.example.security.dtos.UserDTO;
import com.example.security.entities.Role;
import com.example.security.entities.User;
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
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.*;

import static com.example.security.contants.Constants.AUTHORITY_PREFIX;

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
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void test_loadUserByUsername_should_return_values_when_has_all_parameters() throws Exception {
        //given
        User user = new User();
        user.setId(1L);
        user.setEmail("jean@jean.com");
        user.setPassword("****");
        Role role1 = new Role();
        role1.setName("ROLE_USER");
        Role role2 = new Role();
        role2.setName("ROLE_MANAGER");
        Role role3 = new Role();
        role3.setName("ROLE_ADMIN");
        Set<Role> roleSet = new HashSet(Arrays.asList(role1, role2, role3));
        user.setRoles(roleSet);
        List<Role> roleList = new ArrayList(Arrays.asList(role1, role2, role3));
        Set<SimpleGrantedAuthority> simpleGrantedAuthorities = new HashSet<>();
        roleList.forEach(
                role -> simpleGrantedAuthorities.add(new SimpleGrantedAuthority(AUTHORITY_PREFIX + role.getName())));
        Mockito.when(userRepository.selectMyUserByEmail(Mockito.eq("jean@jean.com"))).thenReturn(user);
        //when
        UserDetails userDetails = userService.loadUserByUsername("jean@jean.com");

        //then
        Assert.assertNotNull("The object you enter return not null", userDetails);
        Assert.assertEquals("jean@jean.com", userDetails.getUsername());
        Assert.assertEquals("****", userDetails.getPassword());
        Assert.assertTrue(simpleGrantedAuthorities.size() == roleSet.size());
        Assert.assertNotNull("The object you enter return not null", userDetails.getAuthorities());
    }

    @Test
    public void test_loadUserByUsername_should_return_null_throw_exception_without_id() throws UsernameNotFoundException {
        //given
        User user = new User();
        user.setEmail("jean@jean.com");
        user.setPassword("****");
        Role role1 = new Role();
        role1.setName("ROLE_USER");
        Role role2 = new Role();
        role2.setName("ROLE_MANAGER");
        Role role3 = new Role();
        role3.setName("ROLE_ADMIN");
        Set<Role> roleSet = new HashSet(Arrays.asList(role1, role2, role3));
        user.setRoles(roleSet);
        Mockito.when(userRepository.selectMyUserByEmail(Mockito.eq("jean@jean.com"))).thenReturn(user);

        //when
        UserDetails userDetails = userService.loadUserByUsername("jean@jean.com");

        //then
        Assert.assertNull(userDetails);
    }

    @Test
    public void test_loadUserByUsername_should_return_null_throw_exception_without_email() throws UsernameNotFoundException {
        //given
        User user = new User();
        user.setId(1L);
        user.setPassword("****");
        Role role1 = new Role();
        role1.setName("ROLE_USER");
        Role role2 = new Role();
        role2.setName("ROLE_MANAGER");
        Role role3 = new Role();
        role3.setName("ROLE_ADMIN");
        Set<Role> roleSet = new HashSet(Arrays.asList(role1, role2, role3));
        user.setRoles(roleSet);
        //condition in private method manageSelectMyUserByEmailException
        Mockito.when(userRepository.selectMyUserByEmail(Mockito.eq("jean@jean.com"))).thenReturn(user);

        //when
        UserDetails userDetails = userService.loadUserByUsername("jean@jean.com");

        //then
        Assert.assertNull(userDetails);
    }

    @Test
    public void test_loadUserByUsername_should_return_null_throw_exception_without_password() throws UsernameNotFoundException {
        //given
        User user = new User();
        user.setId(1L);
        user.setEmail("jean@jean.com");
        Role role1 = new Role();
        role1.setName("ROLE_USER");
        Role role2 = new Role();
        role2.setName("ROLE_MANAGER");
        Role role3 = new Role();
        role3.setName("ROLE_ADMIN");
        Set<Role> roleSet = new HashSet(Arrays.asList(role1, role2, role3));
        user.setRoles(roleSet);
        //condition in private method manageSelectMyUserByEmailException
        Mockito.when(userRepository.selectMyUserByEmail(Mockito.eq("jean@jean.com"))).thenReturn(user);

        //when
        UserDetails userDetails = userService.loadUserByUsername("jean@jean.com");

        //then
        Assert.assertNull(userDetails);
    }


    @Test
    public void test_loadUserByUsername_should_return_null_throw_exception_without_roles() throws UsernameNotFoundException {
        //given
        User user = new User();
        user.setId(1L);
        user.setEmail("jean@jean.com");
        user.setPassword("****");
        Mockito.when(userRepository.selectMyUserByEmail(Mockito.eq("jean@jean.com"))).thenReturn(user);

        //when
        UserDetails userDetails = userService.loadUserByUsername("jean@jean.com");

        //then
        Assert.assertNull(userDetails);
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
    public void test_changeUserSatus_should_return_value_when_all_parameters_valid() throws CustomConverterException {
        //given
        UserDTO userDTOEntry = new UserDTO();
        userDTOEntry.setId(1L);
        userDTOEntry.setEmail("jean@jean.com");
        userDTOEntry.setStatus(Status.INACTIVE);

        Optional<User> userMockito = Optional.of(new User());
        userMockito.get().setId(1L);
        userMockito.get().setEmail("jean@jean.com");
        userMockito.get().setStatus(Status.INACTIVE);
        Mockito.when(userRepository.findById(Mockito.eq(userDTOEntry.getId()))).thenReturn(userMockito);

        User user1 = new User();
        user1.setId(1L);
        user1.setEmail("jean@jean.com");
        user1.setStatus(Status.INACTIVE);

        User user2 = new User();
        user2.setId(2L);
        user2.setEmail("jeanne@jeanne.com");
        user2.setStatus(Status.INACTIVE);

        List<User> userMockitos = new ArrayList<>();
        userMockitos.add(user1);
        userMockitos.add(user2);
        Mockito.when(userRepository.findAll()).thenReturn(userMockitos);

        List<UserDTO> userDTOMockitos = new ArrayList<>();
        UserDTO userDTO1 = new UserDTO();
        userDTO1.setId(1L);
        userDTO1.setEmail("jean@jean.com");
        userDTO1.setStatus(Status.ACTIVE);

        UserDTO userDTO2 = new UserDTO();
        userDTO2.setId(2L);
        userDTO2.setEmail("jeanne@jeanne.com");
        userDTO2.setStatus(Status.INACTIVE);

        userDTOMockitos.add(userDTO1);
        userDTOMockitos.add(userDTO2);

        Mockito.when(superModelMapper.convertToDTOs(Mockito.eq(userMockitos))).thenReturn(Optional.of(userDTOMockitos));

        //when
        List<UserDTO> result = userService.changeUserSatus(userDTOEntry);

        //then
        result.forEach(r->{
            if(r.getEmail().equals(userDTOEntry.getEmail())){
                Assert.assertNotEquals(r.getStatus(),userDTOEntry.getStatus());
            }
        });
    }

/**
 @Test public void test_loadUserByUsername_throws_exception_when_user_is_null() throws Exception {

 //given
 User user = null;
 user.setId(1L);
 user.setEmail("jean@jean.com");
 user.setPassword("****");
 Set<SimpleGrantedAuthority> simpleGrantedAuthorities = new HashSet<>();
 Mockito.when(userRepository.selectMyUserByEmail("jean@jean.com")).thenThrow(new UsernameNotFoundException("Invalid username or password."));

 UserServiceImpl mockPrivateMethodExample = new UserServiceImpl();
 UserServiceImpl spy = PowerMockito.spy(mockPrivateMethodExample);
 PowerMockito.doReturn(simpleGrantedAuthorities).when(spy, "getAuthority");
 Set<SimpleGrantedAuthority> simpleGrantedAuthoritiesMock = (Set<SimpleGrantedAuthority>) spy.selectMyUserByEmail("jean@jean.com");
 //when
 UserDetails userDetails = userService.loadUserByUsername("jean@jean.com");

 //then
 Assert.assertNull("return null", userDetails);
 Assert.assertNull("return null", simpleGrantedAuthoritiesMock);
 }
 **/
}
