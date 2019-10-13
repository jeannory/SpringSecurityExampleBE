package com.example.security.config;

import com.example.security.exceptions.CustomInvalidJwtException;
import com.example.security.exceptions.CustomJwtException;
import com.example.security.exceptions.CustomMalformedClaimException;
import com.example.security.models.TokenUtility;
import com.example.security.singleton.SingletonBean;
import com.example.security.tools.ITools;
import com.fasterxml.jackson.databind.JsonNode;
import org.apache.log4j.Logger;
import org.jose4j.jwk.JsonWebKey;
import org.jose4j.jwk.JsonWebKeySet;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.MalformedClaimException;
import org.jose4j.jwt.NumericDate;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.io.Serializable;
import java.util.Base64;
import java.util.List;

import static com.example.security.contants.Constants.AUTHORITIES_KEY;

@Component
public class TokenUtilityProvider implements ITools, Serializable {

    private final static Logger logger = Logger.getLogger(TokenUtilityProvider.class);
    @Autowired
    SingletonBean singletonBean;
    private static TokenUtility tokenUtility;

    public TokenUtilityProvider() {
    }

    public TokenUtility getTokenUtility(String token){
        logger.info("Method getTokenUtility");
        try {
            tokenUtility = validateToken(token);
        } catch (CustomMalformedClaimException ex) {
            logger.error(ex.getMessage());
            tokenUtility.setValidateToken(false);
        } catch (CustomInvalidJwtException ex) {
            logger.error(ex.getMessage());
            tokenUtility.setValidateToken(false);
        } catch (CustomJwtException ex) {
            /**
             * if exception in :
             * -TokenUtility.validateToken
             * -TokenUtilityProvider.validateJsonWebKey
             * -TokenUtilityProvider.getStringFromJwtNode
             */
            logger.error(ex.getMessage());
            tokenUtility.setValidateToken(false);
        }
        logger.info("Method getTokenUtility succeed");
        return tokenUtility;
    }

    private TokenUtility validateToken(String token) {
        logger.info("Method validateToken");
        tokenUtility = new TokenUtility();
        try {
            final String kid = getStringFromJwtNode(token, 0, "kid");
            final String issuer = getStringFromJwtNode(token, 1, "iss");
            /**
             * not in use
             */
            final String exp = getStringFromJwtNode(token, 1, "exp");
            final JsonWebKey jsonWebKey = validateJsonWebKey(kid);
            tokenUtility.setKid(Integer.valueOf(kid));
            final JwtConsumer jwtConsumer = new JwtConsumerBuilder()
                    .setRequireExpirationTime()
                    .setAllowedClockSkewInSeconds(120)
                    .setRequireSubject()
                    .setExpectedIssuer(issuer)
                    .setVerificationKey(jsonWebKey.getKey())
                    .build();
            final JwtClaims jwtClaims = jwtConsumer.processToClaims(token);
            if (jwtClaims == null) {
                throw new CustomJwtException("token is invalid");
            }
            else{
                tokenUtility.setEmail(jwtClaims.getSubject());
                List<String> roleStrings = jwtClaims.getStringListClaimValue(AUTHORITIES_KEY);
                tokenUtility.setRoles(roleStrings);
                /**
                 * jwt validation succeeded
                 */
                tokenUtility.setValidateToken(true);
                return tokenUtility;
            }
        } catch (MalformedClaimException ex) {
            throw new CustomMalformedClaimException("claim is malFormed");
        } catch (InvalidJwtException ex) {
            throw new CustomInvalidJwtException("jwt is invalid");
        } catch (NullPointerException ex) {
            throw new CustomJwtException("token is invalid");
        }
    }

    /**
     * to calculate expiration of jwt
     */
    private Long millisecondsLeft(String exp) {
        logger.info("Method millisecondsLeft");
        final NumericDate jwtNumericDate = NumericDate.fromSeconds(Long.valueOf(exp));
        final NumericDate numericDateNow = NumericDate.now();
        final Long millisecondsLeft = jwtNumericDate.getValueInMillis() - numericDateNow.getValueInMillis();
        return millisecondsLeft;
    }

    private String getStringFromJwtNode(String token, int indice, String nodeName) {
        logger.info("Method getStringFromJwtNode");
        try {
            final String[] tokenTab = token.split("\\.");
            final String headerEncoded = tokenTab[indice];
            final byte[] decodeBytesHeader = Base64.getUrlDecoder().decode(headerEncoded);
            final String decodeHeader = new String(decodeBytesHeader);
            final JsonNode rootNode = singletonBean.getObjectMapper().readValue(decodeHeader, JsonNode.class);
            final JsonNode node = rootNode.path(nodeName);
            final String kid = node.asText();
            return kid;
        } catch (IOException ex) {
            throw new CustomJwtException("failed to get infos from the token");
        } catch (NullPointerException ex) {
            throw new CustomJwtException("failed to get infos from the token");
        }
    }

    private JsonWebKey validateJsonWebKey(String kid) {
        logger.info("Method validateJsonWebKey");
        try {
            /**
             * get jsonWebKey by kid
             */
            final JsonWebKeySet jsonWebKeySet = new JsonWebKeySet(singletonBean.getJsonWebKeys());
            final JsonWebKey jsonWebKey = jsonWebKeySet.findJsonWebKey(kid, null, null, null);
            return jsonWebKey;
        } catch (NullPointerException ex) {
            throw new CustomJwtException("failed to created JsonWebKeySet");
        }
    }
}
