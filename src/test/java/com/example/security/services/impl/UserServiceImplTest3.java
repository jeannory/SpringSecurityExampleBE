package com.example.security.services.impl;

import com.example.security.config.AuthProvider;
import com.example.security.converter.SuperModelMapper;
import com.example.security.dtos.UserDTO;
import com.example.security.entities.Role;
import com.example.security.entities.Space;
import com.example.security.entities.User;
import com.example.security.enums.Gender;
import com.example.security.exceptions.CustomJoseException;
import com.example.security.exceptions.CustomTokenException;
import com.example.security.models.Credential;
import com.example.security.models.Token;
import com.example.security.repositories.RoleRepository;
import com.example.security.repositories.SpaceRepository;
import com.example.security.repositories.UserRepository;
import com.example.security.services.IRoleService;
import com.example.security.services.IUserService;
import com.example.security.singleton.SingletonBean;
import com.example.security.tools.ITools;
import com.example.security.utils.BuilderUtils;
import org.jose4j.jwk.JsonWebKey;
import org.jose4j.jwk.RsaJsonWebKey;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.lang.JoseException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.Whitebox;

import java.util.*;

import static com.example.security.contants.Constants.USER;

public class UserServiceImplTest3 implements ITools {

    private IUserService userService;
    private IRoleService roleService;
    private SuperModelMapper superModelMapper;
    private UserRepository userRepository;
    private SpaceRepository spaceRepository;
    private AuthProvider authProvider;

    @Before
    public void setUp() throws Exception {
        this.userService = new UserServiceImpl();
        roleService = Mockito.mock(RoleServiceImpl.class);
        superModelMapper = Mockito.mock(SuperModelMapper.class);
        userRepository = Mockito.mock(UserRepository.class);
        spaceRepository = Mockito.mock(SpaceRepository.class);
        authProvider = Mockito.mock(AuthProvider.class);
        Whitebox.setInternalState(userService, "roleService", roleService);
        Whitebox.setInternalState(userService, "superModelMapper", superModelMapper);
        Whitebox.setInternalState(userService, "userRepository", userRepository);
        Whitebox.setInternalState(userService, "spaceRepository", spaceRepository);
        Whitebox.setInternalState(userService, "authProvider", authProvider);
    }

    @Test
    public void test_generateUser_when_all_parameters_valid(){
        //given
        final Role userRole = Mockito.spy(new Role());
        userRole.setId(1L);
        userRole.setName(USER);
        Set<Role> userRoles = new HashSet<>(Collections.singletonList(userRole));

        Mockito.when(roleService.getUserRoleSet()).thenReturn(userRoles);

        final User user1 = Mockito.spy(new User());
        user1.setId(1L);
        user1.setEmail("jean@jean.com");
        user1.setGender(Gender.Monsieur);
        user1.setFirstName("Jean");
        user1.setLastName("Leroi");
        user1.setPassword(getStringSha3("0000"));
        user1.setRoles(userRoles);
        user1.setPhoneNumber("0606060606");
        user1.setAdress("3 rue du Roi");
        user1.setZip("95000");
        user1.setCity("Cergy");
        user1.setDeliveryInformation("2ème étage");

        Mockito.when(superModelMapper.convertToEntity(Mockito.any(UserDTO.class))).thenReturn(Optional.of(user1));
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(user1);

        final Space space = Mockito.spy(new Space());
        space.setId(1L);
        space.setName("Espace de " + user1.getEmail());
        space.setUser(user1);

        Mockito.when(spaceRepository.save(Mockito.any(Space.class))).thenReturn(space);

        final Token token = Mockito.spy(new Token());
        token.setToken("fake token");

        Mockito.when(authProvider.validateConnection(Mockito.any(Credential.class))).thenReturn(token);

        final UserDTO userDTO = Mockito.spy(new UserDTO());
        userDTO.setId(1L);
        userDTO.setEmail("jean@jean.com");
        userDTO.setGender(Gender.Monsieur);
        userDTO.setFirstName("Jean");
        userDTO.setLastName("Leroi");
        userDTO.setPassword(getStringSha3("0000"));
        userDTO.setFlattenRoles("USER");
        userDTO.setPhoneNumber("0606060606");
        userDTO.setAdress("3 rue du Roi");
        userDTO.setZip("95000");
        userDTO.setCity("Cergy");
        userDTO.setDeliveryInformation("2ème étage");

        //when
        final Token result = userService.generateUser(userDTO);

        //then
        Assert.assertEquals("fake token", result.getToken());
    }

    @Test
    public void test_generateUser_when_throws_CustomJoseException() throws JoseException{
        //given
        final Role userRole = Mockito.spy(new Role());
        userRole.setId(1L);
        userRole.setName(USER);
        Set<Role> userRoles = new HashSet<>(Collections.singletonList(userRole));

        Mockito.when(roleService.getUserRoleSet()).thenReturn(userRoles);

        final User user1 = Mockito.spy(new User());
        user1.setId(1L);
        user1.setEmail("jean@jean.com");
        user1.setGender(Gender.Monsieur);
        user1.setFirstName("Jean");
        user1.setLastName("Leroi");
        user1.setPassword(getStringSha3("0000"));
        user1.setRoles(userRoles);
        user1.setPhoneNumber("0606060606");
        user1.setAdress("3 rue du Roi");
        user1.setZip("95000");
        user1.setCity("Cergy");
        user1.setDeliveryInformation("2ème étage");

        Mockito.when(superModelMapper.convertToEntity(Mockito.any(UserDTO.class))).thenReturn(Optional.of(user1));
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(user1);

        final Space space = Mockito.spy(new Space());
        space.setId(1L);
        space.setName("Espace de " + user1.getEmail());
        space.setUser(user1);

        Mockito.when(spaceRepository.save(Mockito.any(Space.class))).thenReturn(space);

        List<String> rolesString = Collections.singletonList("USER");

        List<JsonWebKey> jsonWebKeys = Arrays.asList(
                BuilderUtils.buildJsonWebKey(0),
                BuilderUtils.buildJsonWebKey(1),
                BuilderUtils.buildJsonWebKey(2)
        );

        final SingletonBean singletonBean = Mockito.mock(SingletonBean.class);
        Mockito.when(singletonBean.getJsonWebKeys()).thenReturn(jsonWebKeys);

        final JsonWebSignature jsonWebSignature = Mockito.spy(BuilderUtils.buildJsonWebSignature("jean@jean.com",
                rolesString, 0, (RsaJsonWebKey) jsonWebKeys.get(0)));
        final String token2 = jsonWebSignature.getCompactSerialization();

        Mockito.when(jsonWebSignature.getCompactSerialization()).thenThrow(new CustomJoseException("Failed to generate token"));

        final UserDTO userDTO = Mockito.spy(new UserDTO());
        userDTO.setId(1L);
        userDTO.setEmail("jean@jean.com");
        userDTO.setGender(Gender.Monsieur);
        userDTO.setFirstName("Jean");
        userDTO.setLastName("Leroi");
        userDTO.setPassword(getStringSha3("0000"));
        userDTO.setFlattenRoles("USER");
        userDTO.setPhoneNumber("0606060606");
        userDTO.setAdress("3 rue du Roi");
        userDTO.setZip("95000");
        userDTO.setCity("Cergy");
        userDTO.setDeliveryInformation("2ème étage");

        //when
        final Token result = userService.generateUser(userDTO);

        //then
        Assert.assertNull("return null", result);
    }

    @Test
    public void test_generateUser_when_throws_CustomTokenException(){
        //given
        final Role userRole = Mockito.spy(new Role());
        userRole.setId(1L);
        userRole.setName(USER);
        Set<Role> userRoles = new HashSet<>(Collections.singletonList(userRole));

        Mockito.when(roleService.getUserRoleSet()).thenReturn(userRoles);

        final User user1 = Mockito.spy(new User());
        user1.setId(1L);
        user1.setEmail("jean@jean.com");
        user1.setGender(Gender.Monsieur);
        user1.setFirstName("Jean");
        user1.setLastName("Leroi");
        user1.setPassword(getStringSha3("0000"));
        user1.setRoles(userRoles);
        user1.setPhoneNumber("0606060606");
        user1.setAdress("3 rue du Roi");
        user1.setZip("95000");
        user1.setCity("Cergy");
        user1.setDeliveryInformation("2ème étage");

        Mockito.when(superModelMapper.convertToEntity(Mockito.any(UserDTO.class))).thenReturn(Optional.of(user1));
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(user1);

        final Space space = Mockito.spy(new Space());
        space.setId(1L);
        space.setName("Espace de " + user1.getEmail());
        space.setUser(user1);

        Mockito.when(spaceRepository.save(Mockito.any(Space.class))).thenReturn(space);
        RoleRepository roleRepository = Mockito.mock(RoleRepository.class);
        Mockito.when(roleRepository.findByUsersEmail(Mockito.anyString())).thenThrow(new CustomTokenException("token must contain at least 1 role"));

        final UserDTO userDTO = Mockito.spy(new UserDTO());
        userDTO.setId(1L);
        userDTO.setEmail("jean@jean.com");
        userDTO.setGender(Gender.Monsieur);
        userDTO.setFirstName("Jean");
        userDTO.setLastName("Leroi");
        userDTO.setPassword(getStringSha3("0000"));
        userDTO.setFlattenRoles("USER");
        userDTO.setPhoneNumber("0606060606");
        userDTO.setAdress("3 rue du Roi");
        userDTO.setZip("95000");
        userDTO.setCity("Cergy");
        userDTO.setDeliveryInformation("2ème étage");

        //when
        final Token result = userService.generateUser(userDTO);

        //then
        Assert.assertNull("return null", result);
    }

    @Test
    public void test_generateUser_when_userDTO_is_null(){
        //given
        final UserDTO userDTO = null;

        //when
        final Token result = userService.generateUser(userDTO);

        //then
        Assert.assertNull("return null", result);
    }

    @Test
    public void test_generateUser_when_userDTO_email_is_null(){
        //given
        final UserDTO userDTO = Mockito.spy(new UserDTO());
        userDTO.setId(1L);
        userDTO.setGender(Gender.Monsieur);
        userDTO.setFirstName("Jean");
        userDTO.setLastName("Leroi");
        userDTO.setPassword(getStringSha3("0000"));
        userDTO.setFlattenRoles("USER");
        userDTO.setPhoneNumber("0606060606");
        userDTO.setAdress("3 rue du Roi");
        userDTO.setZip("95000");
        userDTO.setCity("Cergy");
        userDTO.setDeliveryInformation("2ème étage");

        //when
        final Token result = userService.generateUser(userDTO);

        //then
        Assert.assertNull("return null", result);
    }

    @Test
    public void test_generateUser_when_roles_is_empty(){
        //given
        final Role userRole = Mockito.spy(new Role());
        userRole.setId(1L);
        userRole.setName(USER);
        Set<Role> userRoles = Collections.emptySet();

        Mockito.when(roleService.getUserRoleSet()).thenReturn(userRoles);

        final User user1 = Mockito.spy(new User());
        user1.setId(1L);
        user1.setEmail("jean@jean.com");
        user1.setGender(Gender.Monsieur);
        user1.setFirstName("Jean");
        user1.setLastName("Leroi");
        user1.setPassword(getStringSha3("0000"));
        user1.setRoles(userRoles);
        user1.setPhoneNumber("0606060606");
        user1.setAdress("3 rue du Roi");
        user1.setZip("95000");
        user1.setCity("Cergy");
        user1.setDeliveryInformation("2ème étage");

        Mockito.when(superModelMapper.convertToEntity(Mockito.any(UserDTO.class))).thenReturn(Optional.of(user1));
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(user1);

        final Space space = Mockito.spy(new Space());
        space.setId(1L);
        space.setName("Espace de " + user1.getEmail());
        space.setUser(user1);

        Mockito.when(spaceRepository.save(Mockito.any(Space.class))).thenReturn(space);

        final Token token = Mockito.spy(new Token());
        token.setToken("fake token");

        Mockito.when(authProvider.validateConnection(Mockito.any(Credential.class))).thenReturn(token);

        final UserDTO userDTO = Mockito.spy(new UserDTO());
        userDTO.setId(1L);
        userDTO.setEmail("jean@jean.com");
        userDTO.setGender(Gender.Monsieur);
        userDTO.setFirstName("Jean");
        userDTO.setLastName("Leroi");
        userDTO.setPassword(getStringSha3("0000"));
        userDTO.setFlattenRoles("USER");
        userDTO.setPhoneNumber("0606060606");
        userDTO.setAdress("3 rue du Roi");
        userDTO.setZip("95000");
        userDTO.setCity("Cergy");
        userDTO.setDeliveryInformation("2ème étage");

        //when
        final Token result = userService.generateUser(userDTO);

        //then
        Assert.assertNull("return null", result);
    }

    @Test
    public void test_generateUser_when_user_save_failed(){
        //given
        final Role userRole = Mockito.spy(new Role());
        userRole.setId(1L);
        userRole.setName(USER);
        Set<Role> userRoles = new HashSet<>(Collections.singletonList(userRole));

        Mockito.when(roleService.getUserRoleSet()).thenReturn(userRoles);

        final User user1 = Mockito.spy(new User());
//        user1.setId(1L);
        user1.setEmail("jean@jean.com");
        user1.setGender(Gender.Monsieur);
        user1.setFirstName("Jean");
        user1.setLastName("Leroi");
        user1.setPassword(getStringSha3("0000"));
        user1.setRoles(userRoles);
        user1.setPhoneNumber("0606060606");
        user1.setAdress("3 rue du Roi");
        user1.setZip("95000");
        user1.setCity("Cergy");
        user1.setDeliveryInformation("2ème étage");

        Mockito.when(superModelMapper.convertToEntity(Mockito.any(UserDTO.class))).thenReturn(Optional.of(user1));
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(user1);

        final Space space = Mockito.spy(new Space());
        space.setId(1L);
        space.setName("Espace de " + user1.getEmail());
        space.setUser(user1);

        Mockito.when(spaceRepository.save(Mockito.any(Space.class))).thenReturn(space);

        final Token token = Mockito.spy(new Token());
        token.setToken("fake token");

        Mockito.when(authProvider.validateConnection(Mockito.any(Credential.class))).thenReturn(token);

        final UserDTO userDTO = Mockito.spy(new UserDTO());
        userDTO.setId(1L);
        userDTO.setEmail("jean@jean.com");
        userDTO.setGender(Gender.Monsieur);
        userDTO.setFirstName("Jean");
        userDTO.setLastName("Leroi");
        userDTO.setPassword(getStringSha3("0000"));
        userDTO.setFlattenRoles("USER");
        userDTO.setPhoneNumber("0606060606");
        userDTO.setAdress("3 rue du Roi");
        userDTO.setZip("95000");
        userDTO.setCity("Cergy");
        userDTO.setDeliveryInformation("2ème étage");

        //when
        final Token result = userService.generateUser(userDTO);

        //then
        Assert.assertNull("return null", result);
    }

    @Test
    public void test_generateUser_when_space_save_failed(){
        //given
        final Role userRole = Mockito.spy(new Role());
        userRole.setId(1L);
        userRole.setName(USER);
        Set<Role> userRoles = new HashSet<>(Collections.singletonList(userRole));

        Mockito.when(roleService.getUserRoleSet()).thenReturn(userRoles);

        final User user1 = Mockito.spy(new User());
        user1.setId(1L);
        user1.setEmail("jean@jean.com");
        user1.setGender(Gender.Monsieur);
        user1.setFirstName("Jean");
        user1.setLastName("Leroi");
        user1.setPassword(getStringSha3("0000"));
        user1.setRoles(userRoles);
        user1.setPhoneNumber("0606060606");
        user1.setAdress("3 rue du Roi");
        user1.setZip("95000");
        user1.setCity("Cergy");
        user1.setDeliveryInformation("2ème étage");

        Mockito.when(superModelMapper.convertToEntity(Mockito.any(UserDTO.class))).thenReturn(Optional.of(user1));
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(user1);

        final Space space = Mockito.spy(new Space());
//        space.setId(1L);
        space.setName("Espace de " + user1.getEmail());
        space.setUser(user1);

        Mockito.when(spaceRepository.save(Mockito.any(Space.class))).thenReturn(space);

        final Token token = Mockito.spy(new Token());
        token.setToken("fake token");

        Mockito.when(authProvider.validateConnection(Mockito.any(Credential.class))).thenReturn(token);

        final UserDTO userDTO = Mockito.spy(new UserDTO());
        userDTO.setId(1L);
        userDTO.setEmail("jean@jean.com");
        userDTO.setGender(Gender.Monsieur);
        userDTO.setFirstName("Jean");
        userDTO.setLastName("Leroi");
        userDTO.setPassword(getStringSha3("0000"));
        userDTO.setFlattenRoles("USER");
        userDTO.setPhoneNumber("0606060606");
        userDTO.setAdress("3 rue du Roi");
        userDTO.setZip("95000");
        userDTO.setCity("Cergy");
        userDTO.setDeliveryInformation("2ème étage");

        //when
        final Token result = userService.generateUser(userDTO);

        //then
        Assert.assertNull("return null", result);
    }
}
