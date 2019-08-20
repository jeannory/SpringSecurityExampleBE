package com.example.security.config;

import com.example.security.entities.Role;
import com.example.security.entities.User;
import com.example.security.exceptions.CustomAuthException;
import com.example.security.exceptions.CustomJoseException;
import com.example.security.models.Credential;
import com.example.security.models.Token;
import com.example.security.repositories.RoleRepository;
import com.example.security.repositories.UserRepository;
import com.example.security.singleton.SingletonBean;
import com.example.security.tools.ITools;
import org.jose4j.jwk.RsaJsonWebKey;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.lang.JoseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static com.example.security.contants.Constants.AUTHORITIES_KEY;
import static com.example.security.contants.Constants.DOMAIN;

@Component
public class AuthProvider implements ITools {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    SingletonBean singletonBean;

    public Token validateConnection(Credential credential) {
        try {
            Token token = null;
            if (validateCredential(credential)) {
                String jwt = generateJwt(credential.getEmail());
                System.out.println("jwt : " + jwt);
                token = new Token();
                token.setToken(jwt);
            }
            return token;
        } catch (CustomAuthException ex) {
            ex.printStackTrace();
            return null;
        } catch (CustomJoseException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    private boolean validateCredential(Credential credential) {
        try {
            Boolean connection = false;
            String credentialSha3 = getStringSha3(credential.getPassword());
            User user = userRepository.selectMyUserByEmail(credential.getEmail());
            if (credentialSha3.equals(user.getPassword())) {
                connection = true;
            }
            if (connection == false) {
                throw new CustomAuthException("Invalid credentials");
            }
            return connection;
        } catch (NullPointerException ex) {
            throw new CustomAuthException("Invalid user");
        }
    }

    private String generateJwt(String email) {
        try {
            List<Role> roles = null;
            List<String> rolesString = new ArrayList<>();
            try {
                roles = roleRepository.findByUsersEmail(email);
                roles.forEach(r -> {
                    rolesString.add(r.getName());
                });
            } catch (Exception ex) {
                //toDo
            }
            int kidRandom = generateRandmoKid();
            RsaJsonWebKey rsaJsonWebKey = (RsaJsonWebKey) singletonBean.getJsonWebKeys().get(kidRandom);
            // Create the Claims, which will be the content of the JWT
            JwtClaims jwtClaims = new JwtClaims();
            jwtClaims.setIssuer(DOMAIN);
            jwtClaims.setExpirationTimeMinutesInTheFuture(120);
            jwtClaims.setGeneratedJwtId();
            jwtClaims.setIssuedAtToNow();
            jwtClaims.setNotBeforeMinutesInThePast(2);// time before which the token is not yet valid (2 minutes ago)
            jwtClaims.setSubject(email);
            jwtClaims.setStringListClaim(AUTHORITIES_KEY, rolesString);
            JsonWebSignature jsonWebSignature = new JsonWebSignature();
            jsonWebSignature.setPayload(jwtClaims.toJson());
            jsonWebSignature.setKeyIdHeaderValue(rsaJsonWebKey.getKeyId());
            jsonWebSignature.setKey(rsaJsonWebKey.getPrivateKey());
            jsonWebSignature.setAlgorithmHeaderValue(AlgorithmIdentifiers.RSA_USING_SHA256);
            return jsonWebSignature.getCompactSerialization();
        } catch (JoseException ex) {
            throw new CustomJoseException("Failed to generate token");
        }
    }

}
