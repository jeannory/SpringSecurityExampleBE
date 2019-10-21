package com.example.security.services.impl;

import com.example.security.converter.SuperModelMapper;
import com.example.security.converter.UserUserDTOConverter;
import com.example.security.dtos.UserDTO;
import com.example.security.entities.User;
import com.example.security.enums.Gender;
import com.example.security.enums.Status;
import com.example.security.exceptions.CustomTransactionalException;
import com.example.security.repositories.UserRepository;
import com.example.security.services.IUserService;
import com.example.security.utils.BuilderUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.Whitebox;

import java.util.*;

public class UserServiceImplTest4 {

    private IUserService userService;
    private UserRepository userRepository;
    private SuperModelMapper superModelMapper;
    private UserUserDTOConverter userDTOConverter;

    @Before
    public void setUp() throws Exception {
        this.userService = new UserServiceImpl();
        userRepository = Mockito.mock(UserRepository.class);
        superModelMapper = Mockito.mock(SuperModelMapper.class);
        userDTOConverter = Mockito.mock(UserUserDTOConverter.class);
        Whitebox.setInternalState(userService, "userRepository", userRepository);
        Whitebox.setInternalState(userService, "superModelMapper", superModelMapper);
        Whitebox.setInternalState(userService, "userDTOConverter", userDTOConverter);
    }

    @Test
    public void test_setUser_when_all_parameters_valid() {
        //given
        final UserDTO userDTO = Mockito.spy(BuilderUtils.buildUserDTO(1L, "jean@jean.com", "1234", Gender.Madame, "Jeanne", "Leroy", "0101010101",
                "9 rue du roi", "75018", "Paris", "9ème étage", null, "USER, MANAGER, ADMIN", Status.ACTIVE));

        final User user = Mockito.spy(BuilderUtils.buildUser(1L, "jean@jean.com", "1234", Gender.Monsieur, "Jean", "Leroy", "0101010101",
                "9 rue du roi", "75018", "Paris", "9ème étage", Status.ACTIVE, Arrays.asList(Arrays.asList("1", "User"), Arrays.asList("2", "MANAGER"), Arrays.asList("3", "ADMIN"))));

        Mockito.when(userRepository.findByEmail(Mockito.anyString())).thenReturn(user);

        user.setGender(userDTO.getGender());
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setPhoneNumber((userDTO.getPhoneNumber()));
        user.setAddress(userDTO.getAddress());
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
    public void test_setUser_when_user_is_null() {
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
    public void test_setUser_when_save_user_failed() {
        //given
        final UserDTO userDTO = Mockito.spy(BuilderUtils.buildUserDTO(1L, "jean@jean.com", "1234", Gender.Madame, "Jeanne", "Leroy", "0101010101",
                "9 rue du roi", "75018", "Paris", "9ème étage", null, "USER, MANAGER, ADMIN", Status.ACTIVE));

        final User user = Mockito.spy(BuilderUtils.buildUser(1L, "jean@jean.com", "1234", Gender.Monsieur, "Jean", "Leroy", "0101010101",
                "9 rue du roi", "75018", "Paris", "9ème étage", Status.ACTIVE, Arrays.asList(Arrays.asList("1", "User"), Arrays.asList("2", "MANAGER"), Arrays.asList("3", "ADMIN"))));

        Mockito.when(userRepository.findByEmail(Mockito.anyString())).thenReturn(user);
        user.setGender(userDTO.getGender());
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setPhoneNumber((userDTO.getPhoneNumber()));
        user.setAddress(userDTO.getAddress());
        user.setZip(userDTO.getZip());
        user.setCity(userDTO.getCity());
        user.setDeliveryInformation(userDTO.getDeliveryInformation());
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenThrow(new CustomTransactionalException());

        //when
        final UserDTO result = userService.setUser(userDTO);

        //then
        Assert.assertNull("return null", result);
    }

    @Test
    public void test_setUser_when_convertToDTO_failed() {
        //given
        final UserDTO userDTO = Mockito.spy(BuilderUtils.buildUserDTO(1L, "jean@jean.com", "1234", Gender.Madame, "Jeanne", "Leroy", "0101010101",
                "9 rue du roi", "75018", "Paris", "9ème étage", null, "USER, MANAGER, ADMIN", Status.ACTIVE));

        final User user = Mockito.spy(BuilderUtils.buildUser(1L, "jean@jean.com", "1234", Gender.Monsieur, "Jean", "Leroy", "0101010101",
                "9 rue du roi", "75018", "Paris", "9ème étage", Status.ACTIVE, Arrays.asList(Arrays.asList("1", "User"), Arrays.asList("2", "MANAGER"), Arrays.asList("3", "ADMIN"))));

        Mockito.when(userRepository.findByEmail(Mockito.anyString())).thenReturn(user);
        user.setGender(userDTO.getGender());
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setPhoneNumber((userDTO.getPhoneNumber()));
        user.setAddress(userDTO.getAddress());
        user.setZip(userDTO.getZip());
        user.setCity(userDTO.getCity());
        user.setDeliveryInformation(userDTO.getDeliveryInformation());
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(user);
        Mockito.when(superModelMapper.convertToDTO(Mockito.any(User.class))).thenReturn(Optional.empty());

        //when
        final UserDTO result = userService.setUser(userDTO);

        //then
        Assert.assertNull("return null", result);
    }

    @Test
    public void test_getUsers_when_all_parameters_valid() {
        //given
        final User user1 = Mockito.spy(BuilderUtils.buildUser(null, "jean@jean.com", "1234", Gender.Monsieur, "Jean", "Leroy", "0101010101",
                "9 rue du roi", "75015", "Paris", "9ème étage", Status.ACTIVE, Arrays.asList(Arrays.asList("1", "USER"), Arrays.asList("2", "COOKER"), Arrays.asList("3", "ADMIN"))));
        final User user2 = Mockito.spy(BuilderUtils.buildUser(null, "jeanne@jean.com", "4567", Gender.Madame, "Jeanne", "Leroy", "0909090909",
                "9 rue du roi", "75015", "Paris", "9ème étage", Status.ACTIVE, Arrays.asList(Arrays.asList("1", "USER"), Arrays.asList("2", "COOKER"), Arrays.asList("3", "ADMIN"))));
        final User user3 = Mockito.spy(BuilderUtils.buildUser(null, "franck@franck.com", "0000", Gender.Monsieur, "franck", "francky", "0102030405",
                "9 rue du fou", "75018", "Paris", "RDC", Status.ACTIVE, Collections.singletonList(Arrays.asList("1", "USER"))));
        final List<User> users = new ArrayList<>(Arrays.asList(user1, user2, user3));
        Mockito.when(userRepository.findAll()).thenReturn(users);
        final UserDTO userDTO1 = BuilderUtils.buildUserDTO(1L, "jean@jean.com", "1234", Gender.Monsieur, "Jean", "Leroy", "0101010101",
                "9 rue du roi", "75015", "Paris", "9ème étage", null, "ADMIN, COOKER, USER", Status.ACTIVE);
        final UserDTO userDTO2 = BuilderUtils.buildUserDTO(2L, "jeanne@jean.com", "4567", Gender.Madame, "Jean", "Leroy", "0909090909",
                "9 rue du roi", "75015", "Paris", "9ème étage", null, "ADMIN, COOKER, USER", Status.ACTIVE);
        final UserDTO userDTO3 = BuilderUtils.buildUserDTO(3L, "franck@franck.com", "0000", Gender.Monsieur, "franck", "francky", "0102030405",
                "9 rue du fou", "75018", "Paris", "RDC", null, "USER", Status.ACTIVE);
        final List<UserDTO> userDTOS = new ArrayList(Arrays.asList(userDTO1, userDTO2, userDTO3));
        Mockito.when(userDTOConverter.convertToUserDTOs(Mockito.anyList())).thenReturn(userDTOS);

        //when
        final List<UserDTO> results = userService.getUsers();

        //then
        Assert.assertEquals(3, results.size());
        Assert.assertEquals("jean@jean.com", results.get(0).getEmail());
        Assert.assertEquals("ADMIN, COOKER, USER", results.get(0).getFlattenRoles());
        Assert.assertEquals("jeanne@jean.com", results.get(1).getEmail());
        Assert.assertEquals("ADMIN, COOKER, USER", results.get(1).getFlattenRoles());
        Assert.assertEquals("franck@franck.com", results.get(2).getEmail());
        Assert.assertEquals("USER", results.get(2).getFlattenRoles());
    }

    @Test
    public void test_getUsers_when_users_is_empty_return_emptyList() {
        //given
        Mockito.when(userRepository.findAll()).thenReturn(Collections.emptyList());

        //when
        final List<UserDTO> results = userService.getUsers();

        //then
        Assert.assertEquals(results, Collections.emptyList());
    }

    @Test
    public void test_getUsers_when_users_is_null_throws_NPE_return_emptyList() {
        //given
        Mockito.when(userRepository.findAll()).thenReturn(null);

        //when
        final List<UserDTO> results = userService.getUsers();

        //then
        Assert.assertEquals(results, Collections.emptyList());
    }

    @Test
    public void test_getUsers_when_convertToUserDTOs_return_emptyList() {
        //given
        final User user1 = Mockito.spy(BuilderUtils.buildUser(null, "jean@jean.com", "1234", Gender.Monsieur, "Jean", "Leroy", "0101010101",
                "9 rue du roi", "75015", "Paris", "9ème étage", Status.ACTIVE, Arrays.asList(Arrays.asList("1", "USER"), Arrays.asList("2", "COOKER"), Arrays.asList("3", "ADMIN"))));
        final User user2 = Mockito.spy(BuilderUtils.buildUser(null, "jeanne@jean.com", "4567", Gender.Madame, "Jeanne", "Leroy", "0909090909",
                "9 rue du roi", "75015", "Paris", "9ème étage", Status.ACTIVE, Arrays.asList(Arrays.asList("1", "USER"), Arrays.asList("2", "COOKER"), Arrays.asList("3", "ADMIN"))));
        final User user3 = Mockito.spy(BuilderUtils.buildUser(null, "franck@franck.com", "0000", Gender.Monsieur, "franck", "francky", "0102030405",
                "9 rue du fou", "75018", "Paris", "RDC", Status.ACTIVE, Collections.singletonList(Arrays.asList("1", "USER"))));
        final List<User> users = new ArrayList<>(Arrays.asList(user1, user2, user3));
        Mockito.when(userRepository.findAll()).thenReturn(users);
        Mockito.when(userDTOConverter.convertToUserDTOs(Mockito.anyList())).thenReturn(Collections.emptyList());

        //when
        final List<UserDTO> results = userService.getUsers();

        //then
        Assert.assertEquals(results, Collections.emptyList());
    }

}
