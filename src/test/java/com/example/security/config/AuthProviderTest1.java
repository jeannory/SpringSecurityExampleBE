package com.example.security.config;

import com.example.security.entities.Role;
import com.example.security.entities.User;
import com.example.security.enums.Gender;
import com.example.security.enums.Status;
import com.example.security.models.Credential;
import com.example.security.models.Token;
import com.example.security.repositories.RoleRepository;
import com.example.security.repositories.UserRepository;
import com.example.security.utils.BuilderUtils1;
import com.example.security.singleton.SingletonBean;
import com.example.security.tools.ITools;
import org.jose4j.jwk.JsonWebKey;
import org.jose4j.jwk.RsaJsonWebKey;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.lang.JoseException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.stream.Collectors;

public class AuthProviderTest1 {

    @InjectMocks
    private AuthProvider authProvider;

    @Autowired
    private ITools iTools = new AuthProvider();

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private SingletonBean singletonBean;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void test_validateConnection_when_all_parameters_valid() throws JoseException {
        //given
        Credential credential = new Credential();
        credential.setEmail("jean@jean.com");
        credential.setPassword("1234");
        String hashPassword = iTools.getStringSha3("1234");
        final User user = BuilderUtils1.buildUser(1L, "jean@jean.com", hashPassword, Gender.Monsieur, "Jean", "Leroy", "0101010101",
                "9 rue du roi", "75018", "Paris", "9ème étage", Status.ACTIVE, Collections.singletonList(Arrays.asList("1", "USER")));
        Mockito.when(userRepository.selectMyUserByEmail(Mockito.eq(credential.getEmail()))).thenReturn(user);
        List<Role> roles1 = user.getRoles().stream().collect(Collectors.toCollection(ArrayList::new));
        Mockito.when(roleRepository.findByUsersEmail("jean@jean.com")).thenReturn(roles1);
        List<String> rolesString = roles1.stream().map(
                role -> {
                    return role.getName();
                }).collect(Collectors.toCollection(ArrayList::new));

        int kidRandom = 0;
        List<JsonWebKey> jsonWebKeys = Arrays.asList(
                BuilderUtils1.buildJsonWebKey(0),
                BuilderUtils1.buildJsonWebKey(1),
                BuilderUtils1.buildJsonWebKey(2)
                );
        Mockito.when(singletonBean.getJsonWebKeys()).thenReturn(jsonWebKeys);

        //when
        final Token result = authProvider.validateConnection(credential);
        final String token1 = result.getToken();
        final String kid1 = BuilderUtils1.getStringFromJwtNode(result.getToken(), 0, "kid");
        final String subject1 = BuilderUtils1.getStringFromJwtNode(result.getToken(), 1, "sub");
        final String issuer1 = BuilderUtils1.getStringFromJwtNode(result.getToken(), 1, "iss");
        final String tokenId1 = BuilderUtils1.getStringFromJwtNode(result.getToken(), 1, "jti");
        final String expiration1 = BuilderUtils1.getStringFromJwtNode(result.getToken(), 1, "exp");

        //manually create a new jsonWebSignature to compare 2 tokens
        final JsonWebSignature jsonWebSignature = BuilderUtils1.buildJsonWebSignature("jean@jean.com",
                rolesString, kidRandom, (RsaJsonWebKey) jsonWebKeys.get(kidRandom));
        final String token2 = jsonWebSignature.getCompactSerialization();
        final String kid2 = BuilderUtils1.getStringFromJwtNode(token2, 0, "kid");
        final String subject2 = BuilderUtils1.getStringFromJwtNode(token2, 1, "sub");
        final String issuer2 = BuilderUtils1.getStringFromJwtNode(token2, 1, "iss");
        final String tokenId2 = BuilderUtils1.getStringFromJwtNode(token2, 1, "jti");
        final String expiration2 = BuilderUtils1.getStringFromJwtNode(token2, 1, "exp");

        //then
        Assert.assertTrue(Integer.valueOf(kid1)<=2);
        //With 2 token build separately
        //the kid is random and might be different
        //tokens was generate with an offset the expiration time might be different
        //even if everything is similar (expiration time, kid, ...) the tokenId is always unique and for 2 tokens
        Assert.assertNotEquals(token2, token1);

        Assert.assertEquals("jean@jean.com", subject1);
        Assert.assertEquals("cuisine.com", issuer1);
        Assert.assertEquals(subject2, subject1);
        Assert.assertEquals(issuer2, issuer1);
        //unique id of tokens
        Assert.assertNotEquals(tokenId2, tokenId1);

        if(kid2.equals(kid1)&&expiration2.equals(expiration1)){
            Assert.assertEquals(kid2, kid1);
            Assert.assertEquals(expiration2, expiration1);
            System.out.println("test_validateConnection kid and exp are equals !!!!!!");
        }

    }

    @Test
    public void test_validateConnection_when_all_credential_not_matching_should_return_null() throws JoseException {
        //given
        Credential credential = new Credential();
        credential.setEmail("jean@jean.com");
        credential.setPassword("1234");
        String hashPassword = iTools.getStringSha3(credential.getPassword());
        final User user = BuilderUtils1.buildUser(1L, "jean@jean.com", "anotherPassword", Gender.Monsieur, "Jean", "Leroy", "0101010101",
                "9 rue du roi", "75018", "Paris", "9ème étage", Status.ACTIVE, Collections.singletonList(Arrays.asList("1", "USER")));
        Mockito.when(userRepository.selectMyUserByEmail(Mockito.eq(credential.getEmail()))).thenReturn(user);

        //when
        final Token result = authProvider.validateConnection(credential);

        //then
        Assert.assertNull("return null", result);
    }

    @Test
    public void test_validateConnection_when_when_user_not_found_should_return_null() throws JoseException {
        //given
        Credential credential = new Credential();
        credential.setEmail("jean@jean.com");
        credential.setPassword("1234");
        final User user = null;
        Mockito.when(userRepository.selectMyUserByEmail(Mockito.eq(credential.getEmail()))).thenReturn(user);

        //when
        final Token result = authProvider.validateConnection(credential);

        //then
        Assert.assertNull("return null", result);
    }

    @Test
    public void test_validateConnection_when_user_Roles_is_empty_return_null() throws JoseException {
        //given
        Credential credential = new Credential();
        credential.setEmail("jean@jean.com");
        credential.setPassword("1234");
        String hashPassword = iTools.getStringSha3(credential.getPassword());
        final User user = BuilderUtils1.buildUser(1L, "jean@jean.com", hashPassword, Gender.Monsieur, "Jean", "Leroy", "0101010101",
                "9 rue du roi", "75018", "Paris", "9ème étage", Status.ACTIVE, Collections.singletonList(Arrays.asList("1", "USER")));
        Mockito.when(userRepository.selectMyUserByEmail(Mockito.eq(credential.getEmail()))).thenReturn(user);
        Mockito.when(roleRepository.findByUsersEmail("jean@jean.com")).thenReturn(Collections.emptyList());

        //when
        final Token result = authProvider.validateConnection(credential);

        //then
        Assert.assertNull("return null", result);

    }

    @Test
    public void test_validateConnection_when_user_Roles_is_empty_return_null_bis() throws JoseException {
        //given
        Credential credential = new Credential();
        credential.setEmail("jean@jean.com");
        credential.setPassword("1234");
        String hashPassword = iTools.getStringSha3(credential.getPassword());
        final User user = BuilderUtils1.buildUser(1L, "jean@jean.com", hashPassword, Gender.Monsieur, "Jean", "Leroy", "0101010101",
                "9 rue du roi", "75018", "Paris", "9ème étage", Status.ACTIVE, Collections.singletonList(Arrays.asList("1", "USER")));
        Mockito.when(userRepository.selectMyUserByEmail(Mockito.eq(credential.getEmail()))).thenReturn(user);
        List<Role> roles1 = new ArrayList<>();
        Mockito.when(roleRepository.findByUsersEmail("jean@jean.com")).thenReturn(roles1);

        //when
        final Token result = authProvider.validateConnection(credential);

        //then
        Assert.assertNull("return null", result);

    }

    @Test
    public void test_validateConnection_when_user_Roles_is_null_return_null() throws JoseException {
        //given
        Credential credential = new Credential();
        credential.setEmail("jean@jean.com");
        credential.setPassword("1234");
        String hashPassword = iTools.getStringSha3(credential.getPassword());
        final User user = BuilderUtils1.buildUser(1L, "jean@jean.com", hashPassword, Gender.Monsieur, "Jean", "Leroy", "0101010101",
                "9 rue du roi", "75018", "Paris", "9ème étage", Status.ACTIVE, Collections.singletonList(Arrays.asList("1", "USER")));
        Mockito.when(userRepository.selectMyUserByEmail(Mockito.eq(credential.getEmail()))).thenReturn(user);
        List<Role> roles1 = null;
        Mockito.when(roleRepository.findByUsersEmail("jean@jean.com")).thenReturn(roles1);

        //when
        final Token result = authProvider.validateConnection(credential);

        //then
        Assert.assertNull("return null", result);

    }

}
