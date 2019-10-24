package com.example.security.converter;

import com.example.security.dtos.UserDTO;
import com.example.security.entities.User;
import com.example.security.enums.Gender;
import com.example.security.enums.Status;
import com.example.security.singleton.SingletonBean;
import com.example.security.utils.BuilderUtils1;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.Whitebox;
import org.modelmapper.ModelMapper;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class UserUserDTOConverterTest {

    private UserUserDTOConverter userDTOConverter;
    private SingletonBean singletonBean;

    @Before
    public void setUp() throws Exception {
        this.userDTOConverter = Mockito.spy(new UserUserDTOConverter());
        singletonBean = Mockito.mock(SingletonBean.class);
        Whitebox.setInternalState(userDTOConverter, "singletonBean", singletonBean);
    }

    @Test
    public void test_convertToUserDTO_when_all_parameters_valid_should_return_result(){
        //given
        final User user = BuilderUtils1.buildUser(1L, "jean@jean.com", "1234", Gender.Monsieur, "Jean", "Leroy", "0101010101",
                "9 rue du roi", "75018", "Paris", "9ème étage", Status.ACTIVE, Arrays.asList(Arrays.asList("1","USER"), Arrays.asList("2","MANAGER"), Arrays.asList("3","ADMIN")));
        Mockito.when(singletonBean.getModelMapper()).thenReturn(new ModelMapper());

        //then
        final Optional<UserDTO> result = userDTOConverter.convertToUserDTO(user);

        //then
        Assert.assertEquals("jean@jean.com", result.get().getEmail());
        Assert.assertEquals("USER, MANAGER, ADMIN", result.get().getFlattenRoles());
    }

    @Test
    public void test_convertToUserDTO_when_user_is_null_valid_should_optional_empty(){
        //given
        final User user = null;
        Mockito.when(singletonBean.getModelMapper()).thenReturn(new ModelMapper());

        //then
        final Optional<UserDTO> result = userDTOConverter.convertToUserDTO(user);

        //then
        Assert.assertEquals(Optional.empty(), result);
    }

    @Test
    public void test_convertToUserDTO_when_roles_is_empty_valid_should_optional_empty(){
        //given
        final User user = BuilderUtils1.buildUser(1L, "jean@jean.com", "1234", Gender.Monsieur, "Jean", "Leroy", "0101010101",
                "9 rue du roi", "75018", "Paris", "9ème étage", Status.ACTIVE, Collections.emptyList());
        Mockito.when(singletonBean.getModelMapper()).thenReturn(new ModelMapper());

        //then
        final Optional<UserDTO> result = userDTOConverter.convertToUserDTO(user);

        //then
        Assert.assertEquals(Optional.empty(), result);
    }

    @Test
    public void test_convertToUserDTOs_when_all_parameters_valid_should_return_results(){
        //given
        final User user1 = BuilderUtils1.buildUser(1L, "jean@jean.com", "1234", Gender.Monsieur, "Jean", "Leroy", "0101010101",
                "9 rue du roi", "75018", "Paris", "9ème étage", Status.ACTIVE, Arrays.asList(Arrays.asList("1","USER"), Arrays.asList("2","MANAGER"), Arrays.asList("3","ADMIN")));
        final User user2 = BuilderUtils1.buildUser(2L, "jeanne@jean.com", "1234", Gender.Monsieur, "Jean", "Leroy", "0101010101",
                "9 rue du roi", "75018", "Paris", "9ème étage", Status.ACTIVE, Arrays.asList(Arrays.asList("1","USER"), Arrays.asList("2","MANAGER")));
        final List<User> users = Arrays.asList(user1, user2);
        Mockito.when(singletonBean.getModelMapper()).thenReturn(new ModelMapper());

        //then
        final List<UserDTO> results = userDTOConverter.convertToUserDTOs(users);

        //then
        Assert.assertEquals(2, results.size());
        Assert.assertEquals("jean@jean.com", results.get(0).getEmail());
        Assert.assertEquals("jeanne@jean.com", results.get(1).getEmail());
        Assert.assertEquals("USER, MANAGER, ADMIN", results.get(0).getFlattenRoles());
        Assert.assertEquals("USER, MANAGER", results.get(1).getFlattenRoles());
    }

    @Test
    public void test_convertToUserDTOs_when_1_user_is_null_should_return_results_with_1_user(){
        //given
        final User user1 = BuilderUtils1.buildUser(1L, "jean@jean.com", "1234", Gender.Monsieur, "Jean", "Leroy", "0101010101",
                "9 rue du roi", "75018", "Paris", "9ème étage", Status.ACTIVE, Arrays.asList(Arrays.asList("1","USER"), Arrays.asList("2","MANAGER"), Arrays.asList("3","ADMIN")));
        final User user2 = null;
        final List<User> users = Arrays.asList(user1, user2);
        Mockito.when(singletonBean.getModelMapper()).thenReturn(new ModelMapper());

        //then
        final List<UserDTO> results = userDTOConverter.convertToUserDTOs(users);

        //then
        Assert.assertEquals(1, results.size());
        Assert.assertEquals("jean@jean.com", results.get(0).getEmail());
        Assert.assertEquals("USER, MANAGER, ADMIN", results.get(0).getFlattenRoles());
    }

    @Test
    public void test_convertToUserDTOs_when_convertToUserDTO_return_empty_should_return_empty_list(){
        //given
        final User user1 = BuilderUtils1.buildUser(1L, "jean@jean.com", "1234", Gender.Monsieur, "Jean", "Leroy", "0101010101",
                "9 rue du roi", "75018", "Paris", "9ème étage", Status.ACTIVE, Arrays.asList(Arrays.asList("1","USER"), Arrays.asList("2","MANAGER"), Arrays.asList("3","ADMIN")));
        final User user2 = null;
        final List<User> users = Arrays.asList(user1, user2);
        Mockito.when(singletonBean.getModelMapper()).thenReturn(new ModelMapper());
        Mockito.when(userDTOConverter.convertToUserDTO(Mockito.any(User.class))).thenReturn(Optional.empty());

        //then
        final List<UserDTO> results = userDTOConverter.convertToUserDTOs(users);

        //then
        Assert.assertEquals(Collections.emptyList(), results);
    }
}