package com.example.security.config;

import com.example.security.entities.Role;
import com.example.security.entities.User;
import com.example.security.enums.Gender;
import com.example.security.enums.Status;
import com.example.security.exceptions.CustomJwtException;
import com.example.security.models.Credential;
import com.example.security.models.Token;
import com.example.security.repositories.RoleRepository;
import com.example.security.repositories.UserRepository;
import com.example.security.services.impl.BuilderUtils;
import com.example.security.singleton.SingletonBean;
import com.example.security.tools.ITools;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jose4j.jwk.JsonWebKey;
import org.jose4j.jwk.JsonWebKeySet;
import org.jose4j.jwk.RsaJsonWebKey;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.lang.JoseException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class AuthProviderTest {

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
        String hashPassword = iTools.getStringSha3(credential.getPassword());
        final User user = BuilderUtils.buildUser(1L, "jean@jean.com", hashPassword, Gender.Monsieur, "Jean", "Leroy", "0101010101",
                "9 rue du roi", "75018", "Paris", "9ème étage", Status.ACTIVE, Collections.singletonList(Arrays.asList("1", "USER")));
        Mockito.when(userRepository.selectMyUserByEmail(Mockito.eq(credential.getEmail()))).thenReturn(user);
        final List<Role> roles1 = user.getRoles().stream().collect(Collectors.toCollection(ArrayList::new));
        Mockito.when(roleRepository.findByUsersEmail("jean@jean.com")).thenReturn(roles1);
        List<String> rolesString = roles1.stream().map(
                role -> {
                    return role.getName();
                }).collect(Collectors.toCollection(ArrayList::new));
        int kidRandom = iTools.generateRandmoKid();
        List<JsonWebKey> jsonWebKeys = Arrays.asList(
                BuilderUtils.buildJsonWebKey(0),
                BuilderUtils.buildJsonWebKey(1),
                BuilderUtils.buildJsonWebKey(2)
                );
        Mockito.when(singletonBean.getJsonWebKeys()).thenReturn(jsonWebKeys);

        //when
        final Token result = authProvider.validateConnection(credential);
        final String kid1 = getStringFromJwtNode(result.getToken(), 0, "kid");
        final String subject1 = getStringFromJwtNode(result.getToken(), 1, "sub");
        final String issuer1 = getStringFromJwtNode(result.getToken(), 1, "iss");

        //then
        Assert.assertTrue(Integer.valueOf(kid1)<=2);
        Assert.assertEquals("jean@jean.com", subject1);
        Assert.assertEquals("cuisine.com", issuer1);

        //manually create a new jsonWebSignature
        final JsonWebSignature jsonWebSignature = BuilderUtils.buildJsonWebSignature("jean@jean.com",
                rolesString, kidRandom, (RsaJsonWebKey) jsonWebKeys.get(kidRandom));
        final String subject2 = getStringFromJwtNode(jsonWebSignature.getCompactSerialization(), 1, "sub");
        Assert.assertEquals(subject2, subject1);

    }

    private String getStringFromJwtNode(String token, int indice, String nodeName) {
        try {
            String[] tokenTab = token.split("\\.");
            String headerEncoded = tokenTab[indice];
            byte[] decodeBytesHeader = Base64.getUrlDecoder().decode(headerEncoded);
            String decodeHeader = new String(decodeBytesHeader);
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode;
            rootNode = objectMapper.readValue(decodeHeader, JsonNode.class);
            JsonNode node = rootNode.path(nodeName);
            String kid = node.asText();
            return kid;
        } catch (Exception ex) {
            throw new CustomJwtException("failed to get infos from the token");
        }
    }

}