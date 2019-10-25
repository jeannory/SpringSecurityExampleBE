package com.example.security.converter;

import com.example.security.dtos.RoleDTO;
import com.example.security.dtos.SuperDTO;
import com.example.security.entities.Role;
import com.example.security.entities.SuperEntity;
import com.example.security.singleton.SingletonBean;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.Whitebox;
import org.modelmapper.ModelMapper;

import java.util.*;

public class SuperModelMapperTest1 {

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
    public void test_convertToEntity_when_all_parameters_valid_should_return_result(){
        //given
        final RoleDTO roleDTO = new RoleDTO();
        roleDTO.setId(1L);
        roleDTO.setName("ADMIN");
        Mockito.when(singletonBean.getModelMapper()).thenReturn(new ModelMapper());

        //when
        final Optional<Role> result = superModelMapper.convertToEntity(roleDTO);

        //then
        Assert.assertEquals("ADMIN", result.get().getName());
    }

    @Test
    public void test_convertToEntity_when_dto_is_null_should_return_optional_empty(){
        //given
        final RoleDTO roleDTO = null;
        Mockito.when(singletonBean.getModelMapper()).thenReturn(new ModelMapper());

        //when
        final Optional<Role> result = superModelMapper.convertToEntity(roleDTO);

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