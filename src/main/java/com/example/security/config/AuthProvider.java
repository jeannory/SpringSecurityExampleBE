package com.example.security.config;

import com.example.security.entities.Role;
import com.example.security.entities.User;
import com.example.security.exceptions.CustomJoseException;
import com.example.security.exceptions.CustomTokenException;
import com.example.security.models.Credential;
import com.example.security.models.Token;
import com.example.security.repositories.RoleRepository;
import com.example.security.repositories.UserRepository;
import com.example.security.singleton.SingletonBean;
import com.example.security.tools.ITools;
import org.apache.log4j.Logger;
import org.jose4j.jwk.RsaJsonWebKey;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.lang.JoseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.security.contants.Constants.AUTHORITIES_KEY;
import static com.example.security.contants.Constants.DOMAIN;

@Component
public class AuthProvider implements ITools {

    private final static Logger logger = Logger.getLogger(AuthProvider.class);
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    SingletonBean singletonBean;

    public Token validateConnection(Credential credential) {
        String credentialSha3 = getStringSha3(credential.getPassword());
        User user = userRepository.selectMyUserByEmail(credential.getEmail());
        if (user == null) {
            return null;
        } else if (credentialSha3.equals(user.getPassword())) {
            try {
                String jwt = generateJwt(credential.getEmail());
                Token token = new Token();
                token.setToken(jwt);
                logger.info("Method validateConnection succeed");
                return token;
            } catch (CustomJoseException ex) {
                logger.error(ex.getMessage());
                return null;
            } catch (CustomTokenException ex) {
                logger.error(ex.getMessage());
                return null;
            }
        }
        return null;
    }

    public Token getRefreshToken(String email){
            try {
                final Token token = new Token();
                token.setToken(generateJwt(email));
                logger.info("refreshToken new : " + token.getToken());
                return token;
            } catch (CustomJoseException ex) {
                logger.error(ex.getMessage());
                return null;
            } catch (CustomTokenException ex) {
                logger.error(ex.getMessage());
                return null;
            }
    }

    private String generateJwt(String email) {
        try {
            List<Role> roles = roleRepository.findByUsersEmail(email);
            if (roles.isEmpty() || roles == null) {
                throw new CustomTokenException("email cannot be empty && token must contain at least 1 role");
            }
            List<String> rolesString = roles.stream().map(
                    role -> {
                        return role.getName();
                    }).collect(Collectors.toCollection(ArrayList::new));
            Integer kidRandom = generateRandmoKid();
            RsaJsonWebKey rsaJsonWebKey = (RsaJsonWebKey) singletonBean.getJsonWebKeys().get(kidRandom);
            /**
             * Create the Claims, which will be the content of the jwt
             */
            JwtClaims jwtClaims = new JwtClaims();
            jwtClaims.setIssuer(DOMAIN);
            /**
            *UI request for refresh token 30 min before its expiration
            *setExpirationTimeMinutesInTheFuture to 29 UI will always request for a new token
            *setExpirationTimeMinutesInTheFuture to 120 UI will request for a new token after 90 min
             */
            jwtClaims.setExpirationTimeMinutesInTheFuture(120);
//            jwtClaims.setExpirationTimeMinutesInTheFuture(29);
            jwtClaims.setGeneratedJwtId();
            jwtClaims.setIssuedAtToNow();
            jwtClaims.setNotBeforeMinutesInThePast(2);
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
        } catch (NullPointerException ex) {
            throw new CustomTokenException("all objects must be initialized to build jsonWebSignature");
        }
    }

}
