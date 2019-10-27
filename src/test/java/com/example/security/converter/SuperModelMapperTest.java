package com.example.security.converter;

import com.example.security.dtos.RoleDTO;
import com.example.security.dtos.SpaceDTO;
import com.example.security.dtos.SuperDTO;
import com.example.security.dtos.UserDTO;
import com.example.security.entities.Role;
import com.example.security.entities.Space;
import com.example.security.entities.SuperEntity;
import com.example.security.entities.User;
import com.example.security.enums.Gender;
import com.example.security.enums.Status;
import com.example.security.singleton.SingletonBean;
import com.example.security.tools.ITools;
import com.example.security.utils.BuilderUtils1;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.Whitebox;
import org.modelmapper.ModelMapper;

import java.util.*;

public class SuperModelMapperTest implements ITools {

    private SuperModelMapper superModelMapper;
    private SingletonBean singletonBean;

    @Before
    public void setUp() throws Exception {
        this.superModelMapper = Mockito.spy(new SuperModelMapper());
        singletonBean = Mockito.mock(SingletonBean.class);
        Whitebox.setInternalState(superModelMapper, "singletonBean", singletonBean);
    }

    @Test
    public void test_convertToDTO_when_all_parameters_valid_should_return_result(){
        //given
        final Role role = new Role();
        role.setId(1L);
        role.setName("ADMIN");
        Mockito.when(singletonBean.getModelMapper()).thenReturn(new ModelMapper());

        //when
        final Optional<RoleDTO> result = superModelMapper.convertToDTO(role);

        //then
        Assert.assertEquals("ADMIN", result.get().getName());
    }

    @Test
    public void test_convertToDTO_when_entity_is_null_should_return_optional_empty(){
        //given
        final Role role = null;
        Mockito.when(singletonBean.getModelMapper()).thenReturn(new ModelMapper());

        //when
        final Optional<RoleDTO> result = superModelMapper.convertToDTO(role);

        //then
        Assert.assertEquals(Optional.empty(), result);
    }

    @Test
    public void test_convertToDTO_when_entity_is_user_and_all_parameters_valid_should_return_result(){
        //given
        final User user = BuilderUtils1.buildUser(1L, "jean@jean.com", "1234", Gender.Monsieur, "Jean", "Leroy", "0101010101",
                "9 rue du roi", "75018", "Paris", "9ème étage", Status.ACTIVE, Arrays.asList(Arrays.asList("1","USER"), Arrays.asList("2","MANAGER"), Arrays.asList("3","ADMIN")));
        Mockito.when(singletonBean.getModelMapper()).thenReturn(new ModelMapper());

        //then
        final Optional<UserDTO> result = superModelMapper.convertToDTO(user);

        //then
        Assert.assertEquals("jean@jean.com", result.get().getEmail());
        Assert.assertEquals("USER, MANAGER, ADMIN", result.get().getFlattenRoles());
    }

    @Test
    public void test_convertToDTO_when_entity_is_user_and_user_is_null_should_optional_empty(){
        //given
        final User user = null;
        Mockito.when(singletonBean.getModelMapper()).thenReturn(new ModelMapper());

        //then
        final Optional<UserDTO> result = superModelMapper.convertToDTO(user);

        //then
        Assert.assertEquals(Optional.empty(), result);
    }

    @Test
    public void test_convertToDTO_when_entity_is_user_and_roles_is_empty_should_optional_empty(){
        //given
        final User user = BuilderUtils1.buildUser(1L, "jean@jean.com", "1234", Gender.Monsieur, "Jean", "Leroy", "0101010101",
                "9 rue du roi", "75018", "Paris", "9ème étage", Status.ACTIVE, Collections.emptyList());
        Mockito.when(singletonBean.getModelMapper()).thenReturn(new ModelMapper());

        //then
        final Optional<UserDTO> result = superModelMapper.convertToDTO(user);

        //then
        Assert.assertEquals(Optional.empty(), result);
    }

    @Test
    public void test_convertToEntity_when_dto_is_RoleDTO_should_return_Role(){
        //given
        final RoleDTO roleDTO = new RoleDTO();
        roleDTO.setId(1L);
        roleDTO.setName("ADMIN");
        Mockito.when(singletonBean.getModelMapper()).thenReturn(new ModelMapper());

        //when
        final Optional<Role> result = superModelMapper.convertToEntity(roleDTO);

        //then
        Assert.assertEquals("ADMIN", result.get().getName());
        Assert.assertEquals(Role.class, result.get().getClass());
    }

    @Test
    public void test_convertToEntity_when_RoleDTO_is_null_should_return_optional_empty(){
        //given
        final RoleDTO roleDTO = null;
        Mockito.when(singletonBean.getModelMapper()).thenReturn(new ModelMapper());

        //when
        final Optional<Role> result = superModelMapper.convertToEntity(roleDTO);

        //then
        Assert.assertEquals(Optional.empty(), result);
    }

    @Test
    public void test_convertToEntity_when_dto_is_SpaceDTO_should_return_Space(){
        //given
        final SpaceDTO spaceDTO = new SpaceDTO();
        spaceDTO.setName("Espace de Jean");
        spaceDTO.setUserEmail("jean@gmail.com");
        Mockito.when(singletonBean.getModelMapper()).thenReturn(new ModelMapper());

        //when
        final Optional<Space> result = superModelMapper.convertToEntity(spaceDTO);

        //then
        Assert.assertEquals("Espace de Jean", result.get().getName());
        Assert.assertEquals("jean@gmail.com", result.get().getUser().getEmail());
        Assert.assertEquals(Space.class, result.get().getClass());
    }

    @Test
    public void test_convertToEntity_when_SpaceDTO_is_null_should_return_optional_empty(){
        //given
        final SpaceDTO spaceDTO = null;
        Mockito.when(singletonBean.getModelMapper()).thenReturn(new ModelMapper());

        //when
        final Optional<Space> result = superModelMapper.convertToEntity(spaceDTO);

        //then
        Assert.assertEquals(Optional.empty(), result);
    }

    @Test
    public void test_convertToEntity_when_dto_is_UserDTO_should_return_User_with_password_crypted(){
        //given
        final UserDTO userDTO = BuilderUtils1.buildUserDTO(
                1L, "jean@gmail.com", "1234", Gender.Monsieur, "Jean", "Leroy",
                "0101010101", "9 rue du roi", "75018", "Paris", "9ème étage",
                null, "ADMIN, COOKER, USER", Status.ACTIVE);
        Mockito.when(singletonBean.getModelMapper()).thenReturn(new ModelMapper());

        //when
        final Optional<User> result = superModelMapper.convertToEntity(userDTO);

        //then
        Assert.assertEquals("jean@gmail.com", result.get().getEmail());
        Assert.assertEquals(User.class, result.get().getClass());
        final String sha3Password = getStringSha3("1234");
        Assert.assertEquals(sha3Password, result.get().getPassword());
        Assert.assertNull("roles are null", result.get().getRoles());
    }

    @Test
    public void test_convertToEntity_when_UserDTO_is_null_should_return_optional_empty(){
        //given
        final UserDTO userDTO = null;
        Mockito.when(singletonBean.getModelMapper()).thenReturn(new ModelMapper());

        //when
        final Optional<User> result = superModelMapper.convertToEntity(userDTO);

        //then
        Assert.assertEquals(Optional.empty(), result);
    }

    @Test
    public void test_convertToDTOs_when_all_parameters_valid_should_return_results() {
        //given
        final Role role1 = new Role();
        role1.setId(1L);
        role1.setName("ADMIN");
        final Role role2 = new Role();
        role2.setId(2L);
        role2.setName("MANAGER");
        final List<SuperEntity> entities = Arrays.asList(role1, role2);
        Mockito.when(singletonBean.getModelMapper()).thenReturn(new ModelMapper());

        //when
        final List<RoleDTO> results = superModelMapper.convertToDTOs(entities);

        //then
        Assert.assertEquals(2, results.size());
        Assert.assertEquals("ADMIN", results.get(0).getName());
        Assert.assertEquals("MANAGER", results.get(1).getName());
    }

    @Test
    public void test_convertToDTOs_when_1_entity_is_null_should_return_list_without_null_entity() {
        //given
        final Role role1 = null;
        final Role role2 = new Role();
        role2.setId(2L);
        role2.setName("MANAGER");
        final List<SuperEntity> entities = Arrays.asList(role1, role2);

        //when
        Mockito.when(singletonBean.getModelMapper()).thenReturn(new ModelMapper());
        final List<RoleDTO> results = superModelMapper.convertToDTOs(entities);

        //then
        Assert.assertEquals(1, results.size());
        Assert.assertEquals("MANAGER", results.get(0).getName());
    }

    @Test
    public void test_convertToDTOs_when_convertToDTO_return_empty_should_return_empty_list() {
        //given
        final Role role1 = new Role();
        role1.setId(1L);
        role1.setName("ADMIN");
        final Role role2 = new Role();
        role2.setId(2L);
        role2.setName("MANAGER");
        final List<SuperEntity> entities = Arrays.asList(role1, role2);
        Mockito.when(singletonBean.getModelMapper()).thenReturn(new ModelMapper());
        Mockito.when(superModelMapper.convertToDTO(Mockito.any(SuperEntity.class))).thenReturn(Optional.empty());

        //when
        final List<RoleDTO> results = superModelMapper.convertToDTOs(entities);

        //then
        Assert.assertEquals(Collections.emptyList(), results);
    }

    @Test
    public void test_convertToDTOs_when_entities_are_users_and_all_parameters_are_valid_should_return_results(){
        //given
        final User user1 = BuilderUtils1.buildUser(1L, "jean@jean.com", "1234", Gender.Monsieur, "Jean", "Leroy", "0101010101",
                "9 rue du roi", "75018", "Paris", "9ème étage", Status.ACTIVE, Arrays.asList(Arrays.asList("1","USER"), Arrays.asList("2","MANAGER"), Arrays.asList("3","ADMIN")));
        final User user2 = BuilderUtils1.buildUser(2L, "jeanne@jean.com", "1234", Gender.Monsieur, "Jean", "Leroy", "0101010101",
                "9 rue du roi", "75018", "Paris", "9ème étage", Status.ACTIVE, Arrays.asList(Arrays.asList("1","USER"), Arrays.asList("2","MANAGER")));
        final List<User> users = Arrays.asList(user1, user2);
        Mockito.when(singletonBean.getModelMapper()).thenReturn(new ModelMapper());

        //then
        final List<UserDTO> results = superModelMapper.convertToDTOs(users);

        //then
        Assert.assertEquals(2, results.size());
        Assert.assertEquals("jean@jean.com", results.get(0).getEmail());
        Assert.assertEquals("jeanne@jean.com", results.get(1).getEmail());
        Assert.assertEquals("USER, MANAGER, ADMIN", results.get(0).getFlattenRoles());
        Assert.assertEquals("USER, MANAGER", results.get(1).getFlattenRoles());
    }

    @Test
    public void test_convertToDTOs_when_entities_are_users_and_1_user_is_null_should_return_results_with_1_user(){
        //given
        final User user1 = BuilderUtils1.buildUser(1L, "jean@jean.com", "1234", Gender.Monsieur, "Jean", "Leroy", "0101010101",
                "9 rue du roi", "75018", "Paris", "9ème étage", Status.ACTIVE, Arrays.asList(Arrays.asList("1","USER"), Arrays.asList("2","MANAGER"), Arrays.asList("3","ADMIN")));
        final User user2 = null;
        final List<User> users = Arrays.asList(user1, user2);
        Mockito.when(singletonBean.getModelMapper()).thenReturn(new ModelMapper());

        //then
        final List<UserDTO> results = superModelMapper.convertToDTOs(users);

        //then
        Assert.assertEquals(1, results.size());
        Assert.assertEquals("jean@jean.com", results.get(0).getEmail());
        Assert.assertEquals("USER, MANAGER, ADMIN", results.get(0).getFlattenRoles());
    }

    @Test
    public void test_convertToDTOs_when_entities_are_users_and_convertToDTO_return_empty_should_return_empty_list(){
        //given
        final User user1 = BuilderUtils1.buildUser(1L, "jean@jean.com", "1234", Gender.Monsieur, "Jean", "Leroy", "0101010101",
                "9 rue du roi", "75018", "Paris", "9ème étage", Status.ACTIVE, Arrays.asList(Arrays.asList("1","USER"), Arrays.asList("2","MANAGER"), Arrays.asList("3","ADMIN")));
        final User user2 = null;
        final List<User> users = Arrays.asList(user1, user2);
        Mockito.when(singletonBean.getModelMapper()).thenReturn(new ModelMapper());
        Mockito.when(superModelMapper.convertToDTO(Mockito.any(User.class))).thenReturn(Optional.empty());

        //then
        final List<UserDTO> results = superModelMapper.convertToDTOs(users);

        //then
        Assert.assertEquals(Collections.emptyList(), results);
    }

    @Test
    public void test_convertToEntities_when_all_parameters_valid_should_return_results(){
        //given
        final RoleDTO roleDTO1 = new RoleDTO();
        roleDTO1.setId(1L);
        roleDTO1.setName("USER");
        final RoleDTO roleDTO2 = new RoleDTO();
        roleDTO2.setId(2L);
        roleDTO2.setName("MANAGER");
        final List<RoleDTO> roleDTOS = Arrays.asList(roleDTO1, roleDTO2);
        Mockito.when(singletonBean.getModelMapper()).thenReturn(new ModelMapper());

        //when
        final List<Role> results = superModelMapper.convertToEntities(roleDTOS);

        //then
        Assert.assertEquals(2, results.size());
        Assert.assertEquals("USER", results.get(0).getName());
        Assert.assertEquals("MANAGER", results.get(1).getName());
    }

    @Test
    public void test_convertToEntities_when_1_dto_is_null_should_return_list_without_empty_dto(){
        //given
        final RoleDTO roleDTO1 = new RoleDTO();
        roleDTO1.setId(1L);
        roleDTO1.setName("USER");
        final RoleDTO roleDTO2 = null;
        final List<RoleDTO> roleDTOS = Arrays.asList(roleDTO1, roleDTO2);
        Mockito.when(singletonBean.getModelMapper()).thenReturn(new ModelMapper());

        //when
        final List<Role> results = superModelMapper.convertToEntities(roleDTOS);

        //then
        Assert.assertEquals(1, results.size());
        Assert.assertEquals("USER", results.get(0).getName());
    }

    @Test
    public void test_convertToEntities_when_convertToEntity_return_empty_should_return_empty_list(){
        //given
        final RoleDTO roleDTO1 = new RoleDTO();
        roleDTO1.setId(1L);
        roleDTO1.setName("USER");
        final RoleDTO roleDTO2 = new RoleDTO();
        roleDTO2.setId(2L);
        roleDTO2.setName("MANAGER");
        final List<RoleDTO> roleDTOS = Arrays.asList(roleDTO1, roleDTO2);
        Mockito.when(singletonBean.getModelMapper()).thenReturn(new ModelMapper());
        Mockito.when(superModelMapper.convertToEntity(Mockito.any(SuperDTO.class))).thenReturn(Optional.empty());

        //when
        final List<Role> results = superModelMapper.convertToEntities(roleDTOS);

        //then
        Assert.assertEquals(Collections.emptyList(), results);
    }

}