package com.example.security.services.impl;

import com.example.security.converter.SuperModelMapper;
import com.example.security.entities.Role;
import com.example.security.repositories.RoleRepository;
import com.example.security.repositories.UserRepository;
import com.example.security.services.IRoleService;
import com.example.security.services.IUserService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.Whitebox;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

import static com.example.security.contants.Constants.*;
import static org.junit.Assert.*;

public class RoleServiceImplTest {

    private IRoleService roleService;
    private RoleRepository roleRepository;
    private SuperModelMapper superModelMapper;
    private UserRepository userRepository;
    private IUserService userService;

    @Before
    public void setUp() throws Exception {
        this.roleService = new RoleServiceImpl();
        roleRepository = Mockito.mock(RoleRepository.class);
        Whitebox.setInternalState(roleService,"roleRepository", roleRepository);
    }


    @Test
    public void test_findByUsersEmail_when_paramters_ok_should_return_result() {
        //given
        final Role role1 = Mockito.mock(Role.class);
        Mockito.when(role1.getName()).thenReturn("USER");
        Mockito.when(role1.getId()).thenReturn(1L);
        final Role role2 = Mockito.mock(Role.class);
        Mockito.when(role2.getName()).thenReturn("MANAGER");
        Mockito.when(role2.getId()).thenReturn(2L);
        final Role role3 = Mockito.mock(Role.class);
        Mockito.when(role3.getName()).thenReturn("ADMIN");
        Mockito.when(role3.getId()).thenReturn(3L);
        Mockito.when(roleRepository.findByUsersEmail("jean@jean.com")).thenReturn(Arrays.asList(role1, role2, role3));

        //when
        final List<Role> results = roleService.findByUsersEmail("jean@jean.com");

        //then
        Assert.assertEquals("USER", results.get(0).getName());
        Assert.assertEquals("MANAGER", results.get(1).getName());
        Assert.assertEquals("ADMIN", results.get(2).getName());
    }

    @Test
    public void test_findByUsersEmail_when_paramters_when_collection_is_empty() {
        //given
        Mockito.when(roleRepository.findByUsersEmail("jean@jean.com")).thenReturn(Collections.emptyList());

        //when
        final List<Role> results = roleService.findByUsersEmail("jean@jean.com");

        //then
        Assert.assertEquals(Collections.emptyList(), results);
    }

    @Test
    public void test_findByUsersEmail_when_paramters_when_collection_is_empty_bis() {
        //given
        Mockito.when(roleRepository.findByUsersEmail("jean@jean.com")).thenReturn(new ArrayList<>());

        //when
        final List<Role> results = roleService.findByUsersEmail("jean@jean.com");

        //then
        Assert.assertEquals(Collections.emptyList(), results);
    }

    @Test
    public void test_findByUsersEmail_when_paramters_when_collection_is_null() {
        //given
        Mockito.when(roleRepository.findByUsersEmail("jean@jean.com")).thenReturn(null);

        //when
        final List<Role> results = roleService.findByUsersEmail("jean@jean.com");

        //then
        Assert.assertEquals(Collections.emptyList(), results);
    }

    @Test
    public void test_getUserRole_when_parameters_ok_should_return_result() {
        //given
        final Role role = Mockito.mock(Role.class);
        Mockito.when(role.getName()).thenReturn("USER");
        Mockito.when(role.getId()).thenReturn(1L);
        Mockito.when(roleRepository.findByName(USER)).thenReturn(role);

        //when
        final Role result = roleService.getUserRole();

        //then
        Assert.assertEquals("USER", result.getName());
    }

    @Test
    public void test_getUserRole_when_role_is_null() {
        //given
        Mockito.when(roleRepository.findByName(USER)).thenReturn(null);

        //when
        final Role result = roleService.getUserRole();

        //then
        Assert.assertNull("return null", result);
    }

    @Test
    public void test_getManagerRole_when_parameters_ok_should_return_result() {
        //given
        final Role role = Mockito.mock(Role.class);
        Mockito.when(role.getName()).thenReturn("MANAGER");
        Mockito.when(role.getId()).thenReturn(2L);
        Mockito.when(roleRepository.findByName(MANAGER)).thenReturn(role);

        //when
        final Role result = roleService.getManagerRole();

        //then
        Assert.assertEquals("MANAGER", result.getName());
    }

    @Test
    public void test_getManagerRole_when_role_is_null() {
        //given
        Mockito.when(roleRepository.findByName(MANAGER)).thenReturn(null);

        //when
        final Role result = roleService.getManagerRole();

        //then
        Assert.assertNull("return null", result);
    }

    @Test
    public void test_getAdminRole_when_parameters_ok_should_return_result() {
        //given
        final Role role = Mockito.mock(Role.class);
        Mockito.when(role.getName()).thenReturn("ADMIN");
        Mockito.when(role.getId()).thenReturn(3L);
        Mockito.when(roleRepository.findByName(ADMIN)).thenReturn(role);

        //when
        final Role result = roleService.getAdminRole();

        //then
        Assert.assertEquals("ADMIN", result.getName());
    }

    @Test
    public void test_getAdminRole_when_role_is_null() {
        //given
        Mockito.when(roleRepository.findByName(ADMIN)).thenReturn(null);

        //when
        final Role result = roleService.getAdminRole();

        //then
        Assert.assertNull("return null", result);
    }

    @Test
    public void test_getUserRoleSet_when_all_parameters_valid() {
        //given
        final Role role = Mockito.mock(Role.class);
        Mockito.when(role.getName()).thenReturn("USER");
        Mockito.when(role.getId()).thenReturn(1L);
        Mockito.when(roleRepository.findByName(USER)).thenReturn(role);

        //when
        Set<Role> results = roleService.getUserRoleSet();

        //then
        Assert.assertEquals("USER", new ArrayList<>(results).get(0).getName());
    }

    @Test
    public void test_getUserRoleSet_when_role_is_null() {
        //given
        Mockito.when(roleRepository.findByName(USER)).thenReturn(null);

        //when
        Set<Role> results = roleService.getUserRoleSet();

        //then
        Assert.assertTrue(results.isEmpty());
    }

    @Test
    public void test_getManagerRoleSet_when_parameters_is_valid() {
        //given
        final Role role = Mockito.mock(Role.class);
        Mockito.when(role.getName()).thenReturn("MANAGER");
        Mockito.when(role.getId()).thenReturn(2L);
        Mockito.when(roleRepository.findByName(MANAGER)).thenReturn(role);

        //when
        Set<Role> results = roleService.getManagerRoleSet();

        //then
        Assert.assertEquals("MANAGER", new ArrayList<>(results).get(0).getName());
    }

    @Test
    public void test_getManagerRoleSet_when_roles_is_null() {
        //given
        Mockito.when(roleRepository.findByName(MANAGER)).thenReturn(null);

        //when
        Set<Role> results = roleService.getManagerRoleSet();

        //then
        Assert.assertTrue(results.isEmpty());
    }

    @Test
    public void test_getAdminRoleDTOSet_when_all_parameters_valid() {
        //given
        final Role role = Mockito.mock(Role.class);
        Mockito.when(role.getName()).thenReturn("ADMIN");
        Mockito.when(role.getId()).thenReturn(3L);
        Mockito.when(roleRepository.findByName(ADMIN)).thenReturn(role);

        //when
        Set<Role> results = roleService.getAdminRoleSet();

        //then
        Assert.assertEquals("ADMIN", new ArrayList<>(results).get(0).getName());
    }

    @Test
    public void test_getAdminRoleDTOSet_when_roles_is_null() {
        //given
        Mockito.when(roleRepository.findByName(ADMIN)).thenReturn(null);

        //when
        Set<Role> results = roleService.getAdminRoleSet();

        //then
        Assert.assertTrue(results.isEmpty());
    }

    @Test
    public void getRoleDtosSet() {
    }

    @Test
    public void getRoleDtosList() {
    }



    @Test
    public void putUserRoles() {
    }
}