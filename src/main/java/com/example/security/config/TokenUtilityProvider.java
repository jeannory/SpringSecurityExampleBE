package com.example.security.config;

import com.example.security.exceptions.CustomInvalidJwtException;
import com.example.security.exceptions.CustomJwtException;
import com.example.security.exceptions.CustomMalformedClaimException;
import com.example.security.models.TokenUtility;
import com.example.security.singleton.SingletonBean;
import com.example.security.tools.ITools;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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

//https://www.devglan.com/spring-security/jwt-role-based-authorization
@Component
public class TokenUtilityProvider implements ITools, Serializable {

    @Autowired
    SingletonBean singletonBean;

    private static TokenUtility tokenUtility;

    public TokenUtilityProvider() {
    }

    public TokenUtility getTokenUtility(String token){
        try {
            tokenUtility = validateToken(token);
        } catch (CustomMalformedClaimException ex) {
            ex.printStackTrace();
            tokenUtility.setValidateToken(false);
        } catch (CustomInvalidJwtException ex) {
            ex.printStackTrace();
            tokenUtility.setValidateToken(false);
        } catch (CustomJwtException ex) {
            //if exception in TokenUtility.validateToken, TokenUtilityProvider.validateJsonWebKey, TokenUtilityProvider.getStringFromJwtNode
            ex.printStackTrace();
            tokenUtility.setValidateToken(false);
        }
        return tokenUtility;
    }

    private TokenUtility validateToken(String token) {
        tokenUtility = new TokenUtility();
        try {
            String kid = getStringFromJwtNode(token, 0, "kid");
            String issuer = getStringFromJwtNode(token, 1, "iss");
            //not using
            String exp = getStringFromJwtNode(token, 1, "exp");
            JsonWebKey jsonWebKey = validateJsonWebKey(kid);
            tokenUtility.setKid(Integer.valueOf(kid));
            JwtConsumer jwtConsumer = new JwtConsumerBuilder()
                    .setRequireExpirationTime()
                    .setAllowedClockSkewInSeconds(120)
                    .setRequireSubject()
                    .setExpectedIssuer(issuer)
                    .setVerificationKey(jsonWebKey.getKey())
                    .build();
            JwtClaims jwtClaims = jwtConsumer.processToClaims(token);
            if (jwtClaims == null) {
                throw new CustomJwtException("token is invalid");
            }
            else{
                tokenUtility.setEmail(jwtClaims.getSubject());
                List<String> roleStrings = jwtClaims.getStringListClaimValue(AUTHORITIES_KEY);
                tokenUtility.setRoles(roleStrings);
                //JWT validation succeeded
                tokenUtility.setValidateToken(true);//verified
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

    //not using
    private Long millisecondsLeft(String exp) {
        NumericDate jwtNumericDate = NumericDate.fromSeconds(Long.valueOf(exp));
        NumericDate numericDateNow = NumericDate.now();
        Long millisecondsLeft = jwtNumericDate.getValueInMillis() - numericDateNow.getValueInMillis();
        return millisecondsLeft;
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
        } catch (IOException ex) {
            throw new CustomJwtException("failed to get infos from the token");
        } catch (NullPointerException ex) {
            throw new CustomJwtException("failed to get infos from the token");
        }catch(Exception ex){
            throw new CustomJwtException("failed to get infos from the token");
        }
    }

    private JsonWebKey validateJsonWebKey(String kid) {
        try {
            //Get jsonWebKey by kid
            JsonWebKeySet jsonWebKeySet = new JsonWebKeySet(singletonBean.getJsonWebKeys());
            JsonWebKey jsonWebKey = jsonWebKeySet.findJsonWebKey(kid, null, null, null);
            return jsonWebKey;
        } catch (NullPointerException ex) {
            ex.printStackTrace();
            throw new CustomJwtException("failed to created JsonWebKeySet");
        }
    }
}
