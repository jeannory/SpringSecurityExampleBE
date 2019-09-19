package com.example.security.services.impl;

import com.example.security.converter.SuperModelMapper;
import com.example.security.dtos.RoleDTO;
import com.example.security.dtos.UserDTO;
import com.example.security.entities.Role;
import com.example.security.entities.User;
import com.example.security.enums.Gender;
import com.example.security.enums.Status;
import com.example.security.repositories.RoleRepository;
import com.example.security.repositories.UserRepository;
import com.example.security.services.IRoleService;
import com.example.security.services.IUserService;
import com.example.security.utils.BuilderUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.Whitebox;

import java.util.*;

import static com.example.security.contants.Constants.*;

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
        superModelMapper = Mockito.mock(SuperModelMapper.class);
        userRepository = Mockito.mock(UserRepository.class);
        userService = Mockito.mock(UserServiceImpl.class);
        Whitebox.setInternalState(roleService,"roleRepository", roleRepository);
        Whitebox.setInternalState(roleService,"superModelMapper", superModelMapper);
        Whitebox.setInternalState(roleService,"userRepository", userRepository);
        Whitebox.setInternalState(roleService,"userService", userService);
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

    //a refaire
    @Test
    public void test_getAdminRoleSet_when_all_parameters_valid() {
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
    public void test_getAdminRoleSet_when_roles_is_null() {
        //given
        Mockito.when(roleRepository.findByName(ADMIN)).thenReturn(null);

        //when
        Set<Role> results = roleService.getAdminRoleSet();

        //then
        Assert.assertTrue(results.isEmpty());
    }


    @Test
    public void test_getRoleDtosList_when_all_parameters_valid_with_real_objects() {
        //given
        final Role role1 = new Role();
        role1.setName("USER");
        role1.setId(1L);
        final Role role2 = new Role();
        role2.setName("MANAGER");
        role2.setId(2L);
        final Role role3 = new Role();
        role3.setName("ADMIN");
        role3.setId(3L);
        List<Role> roles = Arrays.asList(role1, role2, role3);
        Mockito.when(roleRepository.findByUsersEmail(Mockito.anyString())).thenReturn(roles);
        final RoleDTO roleDTO1 = new RoleDTO();
        roleDTO1.setName("USER");
        roleDTO1.setId(1L);
        final RoleDTO roleDTO2 = new RoleDTO();
        roleDTO2.setName("MANAGER");
        roleDTO2.setId(2L);
        final RoleDTO roleDTO3 = new RoleDTO();
        roleDTO3.setName("ADMIN");
        roleDTO3.setId(3L);
        List<RoleDTO> roleDTOS = Arrays.asList(roleDTO1, roleDTO2, roleDTO3);
        Mockito.when(superModelMapper.convertToDTOs(Mockito.anyList())).thenReturn(roleDTOS);

        //when
        List<RoleDTO> results = roleService.getRoleDtosList(Mockito.anyString());

        //then
        Assert.assertTrue(3==results.size());
        Assert.assertTrue(1L==results.get(0).getId());
        Assert.assertEquals("USER", results.get(0).getName());
        Assert.assertTrue(2L==results.get(1).getId());
        Assert.assertEquals("MANAGER", results.get(1).getName());
        Assert.assertTrue(3L==results.get(2).getId());
        Assert.assertEquals("ADMIN", results.get(2).getName());
    }

    @Test
    public void test_getRoleDtosList_when_all_parameters_valid_with_mocks() {
        //given
        final Role role1 = Mockito.mock(Role.class);
        Mockito.when(role1.getName()).thenReturn("USER");
        Mockito.when(role1.getId()).thenReturn(1L);
        final Role role2 = Mockito.mock(Role.class);
        Mockito.when(role1.getName()).thenReturn("MANAGER");
        Mockito.when(role1.getId()).thenReturn(2L);
        final Role role3 = Mockito.mock(Role.class);
        Mockito.when(role1.getName()).thenReturn("ADMIN");
        Mockito.when(role1.getId()).thenReturn(3L);
        List<Role> roles = Arrays.asList(role1, role2, role3);
        Mockito.when(roleRepository.findByUsersEmail(Mockito.anyString())).thenReturn(roles);
        final RoleDTO roleDTO1 = Mockito.mock(RoleDTO.class);
        Mockito.when(roleDTO1.getName()).thenReturn("USER");
        Mockito.when(roleDTO1.getId()).thenReturn(1L);
        final RoleDTO roleDTO2 = Mockito.mock(RoleDTO.class);
        Mockito.when(roleDTO2.getName()).thenReturn("MANAGER");
        Mockito.when(roleDTO2.getId()).thenReturn(2L);
        final RoleDTO roleDTO3 = Mockito.mock(RoleDTO.class);
        Mockito.when(roleDTO3.getName()).thenReturn("ADMIN");
        Mockito.when(roleDTO3.getId()).thenReturn(3L);
        List<RoleDTO> roleDTOS = Arrays.asList(roleDTO1, roleDTO2, roleDTO3);
        Mockito.when(superModelMapper.convertToDTOs(Mockito.anyList())).thenReturn(roleDTOS);

        //when
        List<RoleDTO> results = roleService.getRoleDtosList(Mockito.anyString());

        //then
        Assert.assertTrue(3==results.size());
        Assert.assertTrue(1L==results.get(0).getId());
        Assert.assertEquals("USER", results.get(0).getName());
        Assert.assertTrue(2L==results.get(1).getId());
        Assert.assertEquals("MANAGER", results.get(1).getName());
        Assert.assertTrue(3L==results.get(2).getId());
        Assert.assertEquals("ADMIN", results.get(2).getName());
    }

    @Test
    public void test_getRoleDtosList_when_all_parameters_valid_with_mocks_bis() {
        //given
        final Role role1 = Mockito.mock(Role.class);
        Mockito.doCallRealMethod().when(role1).setName(Mockito.anyString());
        Mockito.doCallRealMethod().when(role1).setId(Mockito.anyLong());
        role1.setName("USER");
        role1.setId(1L);
        Mockito.doCallRealMethod().when(role1).getName();
        Mockito.doCallRealMethod().when(role1).getId();

        final Role role2 = Mockito.mock(Role.class);
        Mockito.doCallRealMethod().when(role2).setName(Mockito.anyString());
        Mockito.doCallRealMethod().when(role2).setId(Mockito.anyLong());
        role2.setName("MANAGER");
        role2.setId(2L);
        Mockito.doCallRealMethod().when(role2).getName();
        Mockito.doCallRealMethod().when(role2).getId();

        final Role role3 = Mockito.mock(Role.class);
        Mockito.doCallRealMethod().when(role3).setName(Mockito.anyString());
        Mockito.doCallRealMethod().when(role3).setId(Mockito.anyLong());
        role3.setName("ADMIN");
        role3.setId(3L);
        Mockito.doCallRealMethod().when(role3).getName();
        Mockito.doCallRealMethod().when(role3).getId();

        List<Role> roles = Arrays.asList(role1, role2, role3);
        Mockito.when(roleRepository.findByUsersEmail(Mockito.anyString())).thenReturn(roles);
        final RoleDTO roleDTO1 = Mockito.mock(RoleDTO.class);
        Mockito.doCallRealMethod().when(roleDTO1).setName(Mockito.anyString());
        Mockito.doCallRealMethod().when(roleDTO1).setId(Mockito.anyLong());
        roleDTO1.setName("USER");
        roleDTO1.setId(1L);
        Mockito.doCallRealMethod().when(roleDTO1).getName();
        Mockito.doCallRealMethod().when(roleDTO1).getId();

        final RoleDTO roleDTO2 = Mockito.mock(RoleDTO.class);
        Mockito.doCallRealMethod().when(roleDTO2).setName(Mockito.anyString());
        Mockito.doCallRealMethod().when(roleDTO2).setId(Mockito.anyLong());
        roleDTO2.setName("MANAGER");
        roleDTO2.setId(2L);
        Mockito.doCallRealMethod().when(roleDTO2).getName();
        Mockito.doCallRealMethod().when(roleDTO2).getId();

        final RoleDTO roleDTO3 = Mockito.mock(RoleDTO.class);
        Mockito.doCallRealMethod().when(roleDTO3).setName(Mockito.anyString());
        Mockito.doCallRealMethod().when(roleDTO3).setId(Mockito.anyLong());
        roleDTO3.setName("ADMIN");
        roleDTO3.setId(3L);
        Mockito.doCallRealMethod().when(roleDTO3).getName();
        Mockito.doCallRealMethod().when(roleDTO3).getId();

        List<RoleDTO> roleDTOS = Arrays.asList(roleDTO1, roleDTO2, roleDTO3);
        Mockito.when(superModelMapper.convertToDTOs(Mockito.anyList())).thenReturn(roleDTOS);

        //when
        List<RoleDTO> results = roleService.getRoleDtosList(Mockito.anyString());

        //then
        Assert.assertTrue(3==results.size());
        Assert.assertTrue(1L==results.get(0).getId());
        Assert.assertEquals("USER", results.get(0).getName());
        Assert.assertTrue(2L==results.get(1).getId());
        Assert.assertEquals("MANAGER", results.get(1).getName());
        Assert.assertTrue(3L==results.get(2).getId());
        Assert.assertEquals("ADMIN", results.get(2).getName());
    }

    /**
     *In this test see how a spy works
     *
     *
    **/
    @Test
    public void test_getRoleDtosList_when_all_parameters_valid_with_spy() {
        //given
        final Role role1 = Mockito.spy(new Role());
        Mockito.when(role1.getName()).thenReturn("USER");
        role1.setName("this setter is erased by the mock even if it s declared after the mock");
        role1.setId(1L);
        Mockito.verify(role1).setId(Mockito.anyLong());
        Mockito.verify(role1).setId(1L);
        final Role role2 = Mockito.spy(new Role());
        Mockito.when(role1.getName()).thenReturn("MANAGER");
        role2.setName("this setter is erased by the mock even if it s declared after the mock");
        role2.setId(2L);
        final Role role3 = Mockito.spy(new Role());
        Mockito.when(role1.getName()).thenReturn("ADMIN");
        role3.setName("this setter is erased by the mock even if it s declared after the mock");
        role3.setId(3L);
        List<Role> roles = Arrays.asList(role1, role2, role3);
        Mockito.when(roleRepository.findByUsersEmail(Mockito.anyString())).thenReturn(roles);
        final RoleDTO roleDTO1 = Mockito.spy(new RoleDTO());
        roleDTO1.setName("USER");
        Mockito.when(roleDTO1.getId()).thenReturn(1L);
        final RoleDTO roleDTO2 = Mockito.spy(new RoleDTO());
        roleDTO2.setName("MANAGER");
        Mockito.when(roleDTO2.getId()).thenReturn(2L);
        final RoleDTO roleDTO3 = Mockito.spy(new RoleDTO());
        roleDTO3.setName("ADMIN");
        Mockito.when(roleDTO3.getId()).thenReturn(3L);
        List<RoleDTO> roleDTOS = Arrays.asList(roleDTO1, roleDTO2, roleDTO3);
        Mockito.when(superModelMapper.convertToDTOs(Mockito.anyList())).thenReturn(roleDTOS);

        //when
        List<RoleDTO> results = roleService.getRoleDtosList(Mockito.anyString());

        //then
        Mockito.verify(roleDTO1, Mockito.times(0)).getId();
        Assert.assertTrue(3==results.size());
        Assert.assertTrue(1L==results.get(0).getId());
        Assert.assertEquals("USER", results.get(0).getName());
        Mockito.verify(roleDTO1, Mockito.times(1)).getId();
        Assert.assertTrue(2L==results.get(1).getId());
        Assert.assertEquals("MANAGER", results.get(1).getName());
        Assert.assertTrue(3L==results.get(2).getId());
        Assert.assertEquals("ADMIN", results.get(2).getName());
    }

    @Test
    public void test_getAdminRoleDTOS_when_all_parameters_valid(){
        //given
        final RoleDTO roleDTO1 = Mockito.mock(RoleDTO.class);
        Mockito.when(roleDTO1.getName()).thenReturn("USER");
        Mockito.when(roleDTO1.getId()).thenReturn(1L);
        final RoleDTO roleDTO2 = Mockito.mock(RoleDTO.class);
        Mockito.when(roleDTO2.getName()).thenReturn("MANAGER");
        Mockito.when(roleDTO2.getId()).thenReturn(2L);
        final RoleDTO roleDTO3 = Mockito.mock(RoleDTO.class);
        Mockito.when(roleDTO3.getName()).thenReturn("ADMIN");
        Mockito.when(roleDTO3.getId()).thenReturn(3L);
        List<RoleDTO> roleDTOS = Arrays.asList(roleDTO1, roleDTO2, roleDTO3);

        Mockito.when(superModelMapper.convertToDTOs(Mockito.anyList())).thenReturn(roleDTOS);

        //when
        List<RoleDTO> results= roleService.getAdminRoleDTOS();

        Assert.assertTrue(3==results.size());
        Assert.assertTrue(1L==results.get(0).getId());
        Assert.assertEquals("USER", results.get(0).getName());
        Assert.assertTrue(2L==results.get(1).getId());
        Assert.assertEquals("MANAGER", results.get(1).getName());
        Assert.assertTrue(3L==results.get(2).getId());
        Assert.assertEquals("ADMIN", results.get(2).getName());
    }

    @Test
    public void test_getAdminRoleDTOS_when_list_of_role_is_empty(){
        //given
        final RoleDTO roleDTO1 = Mockito.mock(RoleDTO.class);
        Mockito.when(roleDTO1.getName()).thenReturn("USER");
        Mockito.when(roleDTO1.getId()).thenReturn(1L);
        final RoleDTO roleDTO2 = Mockito.mock(RoleDTO.class);
        Mockito.when(roleDTO2.getName()).thenReturn("MANAGER");
        Mockito.when(roleDTO2.getId()).thenReturn(2L);
        final RoleDTO roleDTO3 = Mockito.mock(RoleDTO.class);
        Mockito.when(roleDTO3.getName()).thenReturn("ADMIN");
        Mockito.when(roleDTO3.getId()).thenReturn(3L);
        List<RoleDTO> roleDTOS = Arrays.asList(roleDTO1, roleDTO2, roleDTO3);
        Mockito.when(superModelMapper.convertToDTOs(Collections.emptyList())).thenReturn(roleDTOS);

        //when
        List<RoleDTO> results= roleService.getAdminRoleDTOS();

        Assert.assertTrue(results.isEmpty());
    }

    @Test
    public void test_getAdminRoleDTOS_when_list_of_roleDTO_is_empty(){
        //given
        Mockito.when(superModelMapper.convertToDTOs(Mockito.anyList())).thenReturn(Collections.emptyList());

        //when
        List<RoleDTO> results= roleService.getAdminRoleDTOS();

        //then
        Assert.assertTrue(results.isEmpty());
    }

    @Test
    public void test_putUserRoles_when_all_parameters_valid() {
        //given
        final User user = Mockito.spy(BuilderUtils.buildUser(1L, "jean@jean.com", "1234", Gender.Monsieur, "Jean", "Leroy", "0101010101",
                "9 rue du roi", "75018", "Paris", "9ème étage", Status.ACTIVE, Arrays.asList(Arrays.asList("1","User"), Arrays.asList("2","MANAGER"))));
        Mockito.when(userRepository.findByEmail(Mockito.anyString())).thenReturn(user);
        Set<Role> roles = BuilderUtils.buildRoles(Arrays.asList(Arrays.asList("1","USER"), Arrays.asList("2","MANAGER"), Arrays.asList("3","ADMIN")));
        Mockito.when(superModelMapper.convertToEntities(Mockito.anyList())).thenReturn(new ArrayList(roles));
        user.setRoles(roles);
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(user);

        final Role role1 = Mockito.mock(Role.class);
        Mockito.when(role1.getName()).thenReturn("USER");
        Mockito.when(role1.getId()).thenReturn(1L);
        final Role role2 = Mockito.mock(Role.class);
        Mockito.when(role2.getName()).thenReturn("MANAGER");
        Mockito.when(role2.getId()).thenReturn(2L);
        final Role role3 = Mockito.mock(Role.class);
        Mockito.when(role3.getName()).thenReturn("ADMIN");
        Mockito.when(role3.getId()).thenReturn(3L);
        Mockito.when(roleRepository.findByUsersEmail(Mockito.anyString())).thenReturn(Arrays.asList(role1, role2, role3));

        final UserDTO userDTO1 = BuilderUtils.buildUserDTO(1L, "jean@jean.com", "1234", Gender.Monsieur, "Jean", "Leroy", "0101010101",
                "9 rue du roi", "75018", "Paris", "9ème étage", null, "USER, MANAGER, ADMIN", Status.ACTIVE);
        final UserDTO userDTO2 = BuilderUtils.buildUserDTO(2L, "jeanne@jeanne.com", "1234", Gender.Madame, "jeanne", "jeanne", "0102030405",
                "9 rue de la reine", "95000", "Cergy", "rdc", null, "USER, MANAGER", Status.INACTIVE);
        Mockito.when(userService.getUsers()).thenReturn(Arrays.asList(userDTO1, userDTO2));

        //when
        List<UserDTO> results = roleService.putUserRoles("any string",Mockito.anyList());

        //then
        Assert.assertTrue(2==results.size());
        Assert.assertEquals("jean@jean.com", results.get(0).getEmail());
        Assert.assertEquals("USER, MANAGER, ADMIN", results.get(0).getFlattenRoles());
        Assert.assertEquals("jeanne@jeanne.com", results.get(1).getEmail());
        Assert.assertEquals("USER, MANAGER", results.get(1).getFlattenRoles());
    }

    @Test
    public void test_putUserRoles_when_save_user_failed() {
        //given
        final User user = Mockito.spy(BuilderUtils.buildUser(1L, "jean@jean.com", "1234", Gender.Monsieur, "Jean", "Leroy", "0101010101",
                "9 rue du roi", "75018", "Paris", "9ème étage", Status.ACTIVE, Arrays.asList(Arrays.asList("1","User"), Arrays.asList("2","MANAGER"))));
        Mockito.when(userRepository.findByEmail(Mockito.anyString())).thenReturn(user);
        Set<Role> roles = BuilderUtils.buildRoles(Arrays.asList(Arrays.asList("1","USER"), Arrays.asList("2","MANAGER"), Arrays.asList("3","ADMIN")));
        Mockito.when(superModelMapper.convertToEntities(Mockito.anyList())).thenReturn(new ArrayList(roles));

        //** userRepository.save(user) failed because user still 've got 2 roles **
        Mockito.when(user.getRoles()).thenReturn(BuilderUtils.buildRoles(Arrays.asList(Arrays.asList("1","User"), Arrays.asList("2","MANAGER"))));
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(user);

        final Role role1 = Mockito.mock(Role.class);
        Mockito.when(role1.getName()).thenReturn("USER");
        Mockito.when(role1.getId()).thenReturn(1L);
        final Role role2 = Mockito.mock(Role.class);
        Mockito.when(role2.getName()).thenReturn("MANAGER");
        Mockito.when(role2.getId()).thenReturn(2L);
        final Role role3 = Mockito.mock(Role.class);
        Mockito.when(role3.getName()).thenReturn("ADMIN");
        Mockito.when(role3.getId()).thenReturn(3L);
        Mockito.when(roleRepository.findByUsersEmail(Mockito.anyString())).thenReturn(Arrays.asList(role1, role2, role3));

        final UserDTO userDTO1 = BuilderUtils.buildUserDTO(1L, "jean@jean.com", "1234", Gender.Monsieur, "Jean", "Leroy", "0101010101",
                "9 rue du roi", "75018", "Paris", "9ème étage", null, "USER, MANAGER, ADMIN", Status.ACTIVE);
        final UserDTO userDTO2 = BuilderUtils.buildUserDTO(2L, "jeanne@jeanne.com", "1234", Gender.Madame, "jeanne", "jeanne", "0102030405",
                "9 rue de la reine", "95000", "Cergy", "rdc", null, "USER, MANAGER", Status.INACTIVE);
        Mockito.when(userService.getUsers()).thenReturn(Arrays.asList(userDTO1, userDTO2));

        //when
        List<UserDTO> results = roleService.putUserRoles("any string",Mockito.anyList());

        //then
        Assert.assertEquals(Collections.emptyList(),results);
    }

}
