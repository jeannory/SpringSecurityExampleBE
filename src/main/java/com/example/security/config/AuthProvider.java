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

    public Token getRefreshToken(String email) {
        logger.info("Method getRefreshToken");
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

    public Token validateConnection(Credential credential) {
        logger.info("Method validateConnection");
        try {
            final User user = validateUser(credential.getEmail());
            if (credential.getSha3Password().equals(user.getPassword())) {
                try {
                    final String jwt = generateJwt(credential.getEmail());
                    final Token token = new Token();
                    token.setToken(jwt);
                    return token;
                } catch (CustomJoseException ex) {
                    logger.error(ex.getMessage());
                    return null;
                }
            }
        } catch (CustomTokenException ex) {
            logger.error(ex.getMessage());
            return null;
        }
        return null;
    }

    private User validateUser(String email) {
        final User user = userRepository.selectMyUserByEmail(email);
        if (user == null || email.isEmpty() || !isValidEmail(email)) {
            throw new CustomTokenException("email not valid or user not found");
        }
        return user;
    }

    private String generateJwt(String email) {
        logger.info("Method generateJwt");
        try {
            List<Role> roles = roleRepository.findByUsersEmail(email);
            if (roles.isEmpty()) {
                throw new CustomTokenException("token must contain at least 1 role");
            }
            List<String> rolesString = roles.stream().map(
                    role -> {
                        return role.getName();
                    }).collect(Collectors.toCollection(ArrayList::new));
            final int kidRandom = generateRandmoKid();
            final RsaJsonWebKey rsaJsonWebKey = (RsaJsonWebKey) singletonBean.getJsonWebKeys().get(kidRandom);
            /**
             * Create the Claims, which will be the content of the jwt
             */
            final JwtClaims jwtClaims = new JwtClaims();
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
            final JsonWebSignature jsonWebSignature = new JsonWebSignature();
            jsonWebSignature.setPayload(jwtClaims.toJson());
            jsonWebSignature.setKeyIdHeaderValue(rsaJsonWebKey.getKeyId());
            jsonWebSignature.setKey(rsaJsonWebKey.getPrivateKey());
            jsonWebSignature.setAlgorithmHeaderValue(AlgorithmIdentifiers.RSA_USING_SHA256);
            return jsonWebSignature.getCompactSerialization();
        } catch (JoseException ex) {
            throw new CustomJoseException("CustomJoseException - Failed to generate token");
        } catch (IndexOutOfBoundsException ex) {
            throw new CustomJoseException("singletonBean.jsonWebKeys is null or empty");
        }
    }

}
