package com.example.security.config;

import com.example.security.entities.Role;
import com.example.security.exceptions.CustomJoseException;
import com.example.security.exceptions.CustomTokenException;
import com.example.security.models.Token;
import com.example.security.models.TokenUtility;
import com.example.security.repositories.RoleRepository;
import com.example.security.singleton.SingletonBean;
import com.example.security.utils.BuilderUtils;
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

public class AuthProviderTest2 {

    private AuthProvider authProvider;
    private TokenUtilityProvider tokenUtilityProvider;
    private RoleRepository roleRepository;
    private SingletonBean singletonBean;

    @Before
    public void setUp() throws Exception{
        this.authProvider = new AuthProvider();
        tokenUtilityProvider = Mockito.mock(TokenUtilityProvider.class);
        roleRepository = Mockito.mock(RoleRepository.class);
        singletonBean = Mockito.mock(SingletonBean.class);
        Whitebox.setInternalState(authProvider,"tokenUtilityProvider", tokenUtilityProvider);
        Whitebox.setInternalState(authProvider, "roleRepository", roleRepository);
        Whitebox.setInternalState(authProvider, "singletonBean", singletonBean);
    }

    @Test
    public void test_validateRefreshToken_when_all_parameters_valid() throws JoseException {
        //given
        final TokenUtility tokenUtility = Mockito.spy(new TokenUtility());
        Mockito.when(tokenUtility.getEmail()).thenReturn("email");
        Mockito.when(tokenUtility.isValidateToken()).thenReturn(true);
        Mockito.when(tokenUtilityProvider.getTokenUtility(Mockito.anyString())).thenReturn(tokenUtility);
        List<Role> roles = Arrays.asList(
                BuilderUtils.buildRole(Arrays.asList("1", "USER")),
                BuilderUtils.buildRole(Arrays.asList("2", "MANAGER")),
                BuilderUtils.buildRole(Arrays.asList("3", "ADMIN")));
        Mockito.when(roleRepository.findByUsersEmail(Mockito.anyString())).thenReturn(roles);
        List<JsonWebKey> jsonWebKeys = Arrays.asList(
                BuilderUtils.buildJsonWebKey(0),
                BuilderUtils.buildJsonWebKey(1),
                BuilderUtils.buildJsonWebKey(2)
        );
        Mockito.when(singletonBean.getJsonWebKeys()).thenReturn(jsonWebKeys);
        final Token token = Mockito.mock(Token.class);
        Mockito.when(token.getToken()).thenReturn("token old");

        //when
        final Token result = authProvider.validateRefreshToken(token);

        //then
        Assert.assertNotNull("return not null", result.getToken());
        /**
         * the refresh token is always different than the old one
         */
        Assert.assertNotEquals(token.getToken(), result.getToken());
        /**
         * the refresh token has a new expiration which is always after now
         */
        final String expiration = BuilderUtils.getStringFromJwtNode(result.getToken(), 1, "exp");
        final NumericDate jwtNumericDate = NumericDate.fromSeconds(Long.valueOf(expiration));
        final NumericDate numericDateNow = NumericDate.now();
        Assert.assertTrue(jwtNumericDate.isOnOrAfter(numericDateNow));
    }

    @Test
    public void test_validateRefreshToken_when_token_is_null() throws JoseException {
        //given
        final TokenUtility tokenUtility = Mockito.spy(new TokenUtility());
        Mockito.when(tokenUtility.getEmail()).thenReturn("email");
        Mockito.when(tokenUtility.isValidateToken()).thenReturn(true);
        Mockito.when(tokenUtilityProvider.getTokenUtility(Mockito.anyString())).thenReturn(tokenUtility);
        List<Role> roles = Arrays.asList(
                BuilderUtils.buildRole(Arrays.asList("1", "USER")),
                BuilderUtils.buildRole(Arrays.asList("2", "MANAGER")),
                BuilderUtils.buildRole(Arrays.asList("3", "ADMIN")));
        Mockito.when(roleRepository.findByUsersEmail(Mockito.anyString())).thenReturn(roles);
        List<JsonWebKey> jsonWebKeys = Arrays.asList(
                BuilderUtils.buildJsonWebKey(0),
                BuilderUtils.buildJsonWebKey(1),
                BuilderUtils.buildJsonWebKey(2)
        );
        Mockito.when(singletonBean.getJsonWebKeys()).thenReturn(jsonWebKeys);
        final Token token = Mockito.mock(Token.class);
        Mockito.when(token.getToken()).thenReturn("token old");

        //when
        final Token result = authProvider.validateRefreshToken(null);

        //then
        Assert.assertNull("return null", result);
    }

    @Test
    public void test_validateRefreshToken_when_isValidateToken_is_false() throws JoseException {
        //given
        final TokenUtility tokenUtility = Mockito.spy(new TokenUtility());
        Mockito.when(tokenUtility.getEmail()).thenReturn("email");
        Mockito.when(tokenUtility.isValidateToken()).thenReturn(false);
        Mockito.when(tokenUtilityProvider.getTokenUtility(Mockito.anyString())).thenReturn(tokenUtility);
        final Token token = Mockito.mock(Token.class);
        Mockito.when(token.getToken()).thenReturn("token old");

        //when
        final Token result = authProvider.validateRefreshToken(token);

        //then
        Assert.assertNull("return null", result);
    }

    @Test
    public void test_validateRefreshToken_when_throws_CustomJoseException() throws JoseException {
        //given
        final TokenUtility tokenUtility = Mockito.spy(new TokenUtility());
        Mockito.when(tokenUtility.getEmail()).thenReturn("email");
        Mockito.when(tokenUtility.isValidateToken()).thenReturn(true);
        Mockito.when(tokenUtilityProvider.getTokenUtility(Mockito.anyString())).thenReturn(tokenUtility);
        List<Role> roles = Arrays.asList(
                BuilderUtils.buildRole(Arrays.asList("1", "USER")),
                BuilderUtils.buildRole(Arrays.asList("2", "MANAGER")),
                BuilderUtils.buildRole(Arrays.asList("3", "ADMIN")));
        Mockito.when(roleRepository.findByUsersEmail(Mockito.anyString())).thenReturn(roles);
        List<JsonWebKey> jsonWebKeys = Arrays.asList(
                BuilderUtils.buildJsonWebKey(0),
                BuilderUtils.buildJsonWebKey(1),
                BuilderUtils.buildJsonWebKey(2)
        );
        Mockito.when(singletonBean.getJsonWebKeys()).thenReturn(jsonWebKeys);
        final Token token = Mockito.mock(Token.class);
        Mockito.when(token.getToken()).thenReturn("token old");

        //when && then
        Mockito.when(authProvider.validateRefreshToken(token)).thenThrow(new CustomJoseException("Failed to generate token")).thenReturn(null);
    }

    @Test
    public void test_validateRefreshToken_when_throws_CustomTokenException() throws JoseException {
        //given
        final TokenUtility tokenUtility = Mockito.spy(new TokenUtility());
        Mockito.when(tokenUtility.getEmail()).thenReturn("email");
        Mockito.when(tokenUtility.isValidateToken()).thenReturn(true);
        Mockito.when(tokenUtilityProvider.getTokenUtility(Mockito.anyString())).thenReturn(tokenUtility);
        List<Role> roles = Arrays.asList(
                BuilderUtils.buildRole(Arrays.asList("1", "USER")),
                BuilderUtils.buildRole(Arrays.asList("2", "MANAGER")),
                BuilderUtils.buildRole(Arrays.asList("3", "ADMIN")));
        Mockito.when(roleRepository.findByUsersEmail(Mockito.anyString())).thenReturn(roles);
        List<JsonWebKey> jsonWebKeys = Arrays.asList(
                BuilderUtils.buildJsonWebKey(0),
                BuilderUtils.buildJsonWebKey(1),
                BuilderUtils.buildJsonWebKey(2)
        );
        Mockito.when(singletonBean.getJsonWebKeys()).thenReturn(jsonWebKeys);
        final Token token = Mockito.mock(Token.class);
        Mockito.when(token.getToken()).thenReturn("token old");

        //when && then
        Mockito.when(authProvider.validateRefreshToken(token)).thenThrow(new CustomTokenException("all objects must be initialized to build jsonWebSignature")).thenReturn(null);
    }

    @Test
    public void test_validateRefreshToken_when_role_is_empty_then_throw_CustomTokenException() throws JoseException {
        //given
        final TokenUtility tokenUtility = Mockito.spy(new TokenUtility());
        Mockito.when(tokenUtility.getEmail()).thenReturn("email");
        Mockito.when(tokenUtility.isValidateToken()).thenReturn(true);
        Mockito.when(tokenUtilityProvider.getTokenUtility(Mockito.anyString())).thenReturn(tokenUtility);
        Mockito.when(roleRepository.findByUsersEmail(Mockito.anyString())).thenReturn(Collections.emptyList());
        final Token token = Mockito.mock(Token.class);
        Mockito.when(token.getToken()).thenReturn("token old");

        //when
        final Token result = authProvider.validateRefreshToken(token);

        //then
        Assert.assertNull("return null", result);
    }
}
