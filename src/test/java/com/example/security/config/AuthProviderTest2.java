package com.example.security.config;

import com.example.security.entities.Role;
import com.example.security.entities.User;
import com.example.security.enums.Gender;
import com.example.security.enums.Status;
import com.example.security.exceptions.CustomJoseException;
import com.example.security.exceptions.CustomTokenException;
import com.example.security.models.Credential;
import com.example.security.models.Token;
import com.example.security.repositories.RoleRepository;
import com.example.security.repositories.UserRepository;
import com.example.security.singleton.SingletonBean;
import com.example.security.tools.ITools;
import com.example.security.utils.BuilderUtils1;
import org.jose4j.jwk.JsonWebKey;
import org.jose4j.jwt.NumericDate;
import org.jose4j.lang.JoseException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.Whitebox;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class AuthProviderTest2 implements ITools {

    private AuthProvider authProvider;
    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private SingletonBean singletonBean;

    @Before
    public void setUp() throws Exception {
        this.authProvider = new AuthProvider();
        userRepository = Mockito.mock(UserRepository.class);
        roleRepository = Mockito.mock(RoleRepository.class);
        singletonBean = Mockito.mock(SingletonBean.class);
        Whitebox.setInternalState(authProvider, "userRepository", userRepository);
        Whitebox.setInternalState(authProvider, "roleRepository", roleRepository);
        Whitebox.setInternalState(authProvider, "singletonBean", singletonBean);
    }

    @Test
    public void test_validateConnection_when_all_parameters_valid() throws JoseException {
        //given
        final Credential credential = Mockito.spy(new Credential());
        credential.setEmail("jean@jean.com");
        credential.setPassword("1234");
        final String hashPassword = getStringSha3("1234");
        final User user = BuilderUtils1.buildUser(1L, "jean@jean.com", hashPassword, Gender.Monsieur, "Jean", "Leroy", "0101010101",
                "9 rue du roi", "75018", "Paris", "9ème étage", Status.ACTIVE, Collections.singletonList(Arrays.asList("1", "USER")));
        Mockito.when(userRepository.selectMyUserByEmail(Mockito.eq(credential.getEmail()))).thenReturn(user);
        List<Role> roles = Arrays.asList(
                BuilderUtils1.buildRole(Arrays.asList("1", "USER")),
                BuilderUtils1.buildRole(Arrays.asList("2", "MANAGER")),
                BuilderUtils1.buildRole(Arrays.asList("3", "ADMIN")));
        Mockito.when(roleRepository.findByUsersEmail(Mockito.anyString())).thenReturn(roles);
        List<JsonWebKey> jsonWebKeys = Arrays.asList(
                BuilderUtils1.buildJsonWebKey(0),
                BuilderUtils1.buildJsonWebKey(1),
                BuilderUtils1.buildJsonWebKey(2)
        );
        Mockito.when(singletonBean.getJsonWebKeys()).thenReturn(jsonWebKeys);

        //when
        final Token result = authProvider.validateConnection(credential);

        //then
        Assert.assertNotNull("return not null", result.getToken());
        /**
         * the token has a new expiration which is always after now
         */
        final String expiration = BuilderUtils1.getStringFromJwtNode(result.getToken(), 1, "exp");
        final NumericDate jwtNumericDate = NumericDate.fromSeconds(Long.valueOf(expiration));
        final NumericDate numericDateNow = NumericDate.now();
        Assert.assertTrue(jwtNumericDate.isOnOrAfter(numericDateNow));
    }

    @Test
    public void test_validateConnection_when_throws_CustomJoseException_should_return_null() throws JoseException {
        //given
        final Credential credential = Mockito.spy(new Credential());
        credential.setEmail("jean@jean.com");
        credential.setPassword("1234");
        final String hashPassword = getStringSha3(credential.getPassword());
        final User user = BuilderUtils1.buildUser(1L, "jean@jean.com", hashPassword, Gender.Monsieur, "Jean", "Leroy", "0101010101",
                "9 rue du roi", "75018", "Paris", "9ème étage", Status.ACTIVE, Collections.singletonList(Arrays.asList("1", "USER")));
        Mockito.when(userRepository.selectMyUserByEmail(Mockito.eq(credential.getEmail()))).thenReturn(user);
        List<Role> roles = Arrays.asList(
                BuilderUtils1.buildRole(Arrays.asList("1", "USER")),
                BuilderUtils1.buildRole(Arrays.asList("2", "MANAGER")),
                BuilderUtils1.buildRole(Arrays.asList("3", "ADMIN")));
        Mockito.when(roleRepository.findByUsersEmail(Mockito.anyString())).thenReturn(roles);
        List<JsonWebKey> jsonWebKeys = Arrays.asList(
                BuilderUtils1.buildJsonWebKey(0),
                BuilderUtils1.buildJsonWebKey(1),
                BuilderUtils1.buildJsonWebKey(2)
        );
        Mockito.when(singletonBean.getJsonWebKeys()).thenReturn(jsonWebKeys);

        //when && then
        Mockito.when(authProvider.validateConnection(credential)).thenThrow(new CustomJoseException("Failed to generate token")).thenReturn(null);
    }

    @Test
    public void test_validateRefreshToken_when_throws_CustomTokenException() throws JoseException {
        //given
        final Credential credential = Mockito.spy(new Credential());
        credential.setEmail("jean@jean.com");
        credential.setPassword("1234");
        final String hashPassword = getStringSha3(credential.getPassword());
        final User user = BuilderUtils1.buildUser(1L, "jean@jean.com", hashPassword, Gender.Monsieur, "Jean", "Leroy", "0101010101",
                "9 rue du roi", "75018", "Paris", "9ème étage", Status.ACTIVE, Collections.singletonList(Arrays.asList("1", "USER")));
        Mockito.when(userRepository.selectMyUserByEmail(Mockito.eq(credential.getEmail()))).thenReturn(user);
        List<Role> roles = Arrays.asList(
                BuilderUtils1.buildRole(Arrays.asList("1", "USER")),
                BuilderUtils1.buildRole(Arrays.asList("2", "MANAGER")),
                BuilderUtils1.buildRole(Arrays.asList("3", "ADMIN")));
        Mockito.when(roleRepository.findByUsersEmail(Mockito.anyString())).thenReturn(roles);
        List<JsonWebKey> jsonWebKeys = Arrays.asList(
                BuilderUtils1.buildJsonWebKey(0),
                BuilderUtils1.buildJsonWebKey(1),
                BuilderUtils1.buildJsonWebKey(2)
        );
        Mockito.when(singletonBean.getJsonWebKeys()).thenReturn(jsonWebKeys);

        //when && then
        Mockito.when(authProvider.validateConnection(credential)).thenThrow(new CustomTokenException("all objects must be initialized to build jsonWebSignature")).thenReturn(null);
    }

    @Test
    public void test_validateRefreshToken_when_role_is_empty_then_throw_CustomTokenException() throws JoseException {
        //given
        final Credential credential = Mockito.spy(new Credential());
        credential.setEmail("jean@jean.com");
        credential.setPassword("1234");
        final String hashPassword = getStringSha3(credential.getPassword());
        final User user = BuilderUtils1.buildUser(1L, "jean@jean.com", hashPassword, Gender.Monsieur, "Jean", "Leroy", "0101010101",
                "9 rue du roi", "75018", "Paris", "9ème étage", Status.ACTIVE, Collections.singletonList(Arrays.asList("1", "USER")));
        Mockito.when(userRepository.selectMyUserByEmail(Mockito.eq(credential.getEmail()))).thenReturn(user);
        Mockito.when(roleRepository.findByUsersEmail(Mockito.anyString())).thenReturn(Collections.emptyList());

        //when
        final Token result = authProvider.validateConnection(credential);

        //then
        Assert.assertNull("return null", result);
    }

    @Test
    public void test_getRefreshToken_when_all_parameters_valid() throws JoseException{
        //given
        List<Role> roles = Arrays.asList(
                BuilderUtils1.buildRole(Arrays.asList("1", "USER")),
                BuilderUtils1.buildRole(Arrays.asList("2", "MANAGER")),
                BuilderUtils1.buildRole(Arrays.asList("3", "ADMIN")));
        Mockito.when(roleRepository.findByUsersEmail(Mockito.anyString())).thenReturn(roles);
        List<JsonWebKey> jsonWebKeys = Arrays.asList(
                BuilderUtils1.buildJsonWebKey(0),
                BuilderUtils1.buildJsonWebKey(1),
                BuilderUtils1.buildJsonWebKey(2)
        );
        Mockito.when(singletonBean.getJsonWebKeys()).thenReturn(jsonWebKeys);

        //when
        Token result = authProvider.getRefreshToken(Mockito.anyString());

        //then
        Assert.assertNotNull("return not null", result.getToken());
        /**
         * the token has a new expiration which is always after now
         */
        final String expiration = BuilderUtils1.getStringFromJwtNode(result.getToken(), 1, "exp");
        final NumericDate jwtNumericDate = NumericDate.fromSeconds(Long.valueOf(expiration));
        final NumericDate numericDateNow = NumericDate.now();
        Assert.assertTrue(jwtNumericDate.isOnOrAfter(numericDateNow));
    }

    @Test
    public void test_getRefreshToken_when_email_is_null_then_throws_CustomTokenException() throws JoseException{
        //given && when
        Token result = authProvider.getRefreshToken(null);

        //then
        Assert.assertNull("return null", result);
    }

    @Test
    public void test_getRefreshToken_when_roles_is_empty_then_throws_CustomTokenException() throws JoseException{
        //given
        List<Role> roles = Arrays.asList(
                BuilderUtils1.buildRole(Arrays.asList("1", "USER")),
                BuilderUtils1.buildRole(Arrays.asList("2", "MANAGER")),
                BuilderUtils1.buildRole(Arrays.asList("3", "ADMIN")));
        Mockito.when(roleRepository.findByUsersEmail(Mockito.anyString())).thenReturn(Collections.emptyList());

        //when
        Token result = authProvider.getRefreshToken(null);

        //then
        Assert.assertNull("return null", result);
    }

    @Test
    public void test_getRefreshToken_when_throws_CustomJoseException_should_return_null() throws JoseException{
        //given
        List<Role> roles = Arrays.asList(
                BuilderUtils1.buildRole(Arrays.asList("1", "USER")),
                BuilderUtils1.buildRole(Arrays.asList("2", "MANAGER")),
                BuilderUtils1.buildRole(Arrays.asList("3", "ADMIN")));
        Mockito.when(roleRepository.findByUsersEmail(Mockito.anyString())).thenReturn(roles);
        List<JsonWebKey> jsonWebKeys = Arrays.asList(
                BuilderUtils1.buildJsonWebKey(0),
                BuilderUtils1.buildJsonWebKey(1),
                BuilderUtils1.buildJsonWebKey(2)
        );
        Mockito.when(singletonBean.getJsonWebKeys()).thenReturn(jsonWebKeys);

        //when && then
        Mockito.when(authProvider.getRefreshToken(Mockito.anyString())).thenThrow(new CustomJoseException("Failed to generate token")).thenReturn(null);;
    }
}
