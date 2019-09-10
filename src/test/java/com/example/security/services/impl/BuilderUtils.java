package com.example.security.services.impl;

import com.example.security.dtos.UserDTO;
import com.example.security.entities.Role;
import com.example.security.entities.User;
import com.example.security.enums.Gender;
import com.example.security.enums.Status;
import org.jose4j.jwk.JsonWebKey;
import org.jose4j.jwk.RsaJsonWebKey;
import org.jose4j.jwk.RsaJwkGenerator;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.lang.JoseException;
import org.mockito.Mockito;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.example.security.contants.Constants.AUTHORITIES_KEY;
import static com.example.security.contants.Constants.DOMAIN;

public class BuilderUtils {

    public static User buildUser(
            Long id, String email, String password, Gender gender, String firstName, String lastName, String phoneNumber,
            String adress, String zip, String city, String deliveryInformation, Status status) {
        final User user = new User();
        user.setId(id);
        user.setEmail(email);
        user.setPassword(password);
        user.setGender(gender);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setPhoneNumber(phoneNumber);
        user.setAdress(adress);
        user.setZip(zip);
        user.setCity(city);
        user.setDeliveryInformation(deliveryInformation);
        user.setStatus(status);
        return user;
    }

    public static User buildUser(
            Long id, String email, String password, Gender gender, String firstName, String lastName, String phoneNumber,
            String adress, String zip, String city, String deliveryInformation, Status status, List<List<String>> strings
    ) {
        final User user = buildUser(id, email, password, gender, firstName, lastName, phoneNumber, adress, zip, city, deliveryInformation, status);
        user.setRoles(buildRoles(strings));
        return user;
    }


    public static HashSet<GrantedAuthority> buildAuthorities(List<String> strings) {
        return strings.stream().map(str -> {
                    return new SimpleGrantedAuthority("ROLE_" + str);
                }
        ).collect(Collectors.toCollection(HashSet::new));
    }

    public static UserDTO buildUserDTO(
            Long id, String email, String password, Gender gender, String firstName, String lastName, String phoneNumber, String adress,
            String zip, String city, String deliveryInformation, Long SpaceId, String flattenRoles, Status status) {

        UserDTO userDTO = new UserDTO();
        userDTO.setId(id);
        userDTO.setEmail(email);
        userDTO.setPassword(password);
        userDTO.setGender(gender);
        userDTO.setFirstName(firstName);
        userDTO.setLastName(lastName);
        userDTO.setPhoneNumber(phoneNumber);
        userDTO.setAdress(adress);
        userDTO.setZip(zip);
        userDTO.setCity(city);
        userDTO.setDeliveryInformation(deliveryInformation);
        userDTO.setSpaceId(SpaceId);
        userDTO.setFlattenRoles(flattenRoles);
        userDTO.setStatus(status);
        return userDTO;
    }

    public static org.springframework.security.core.userdetails.User buildUserDetails(String userName, String password, Set<GrantedAuthority> authorities) {
        org.springframework.security.core.userdetails.User userDetails = Mockito.mock(org.springframework.security.core.userdetails.User.class);
        Mockito.when(userDetails.getUsername()).thenReturn(userName);
        Mockito.when(userDetails.getPassword()).thenReturn(password);
        Mockito.when(userDetails.getAuthorities()).thenReturn(authorities);
        return userDetails;
    }

    public static Set<Role> buildRoles(List<List<String>> strings) {
        return strings.stream().map(str -> {
                    Role role = buildRole(str);
                    return role;
                }
        ).collect(Collectors.toCollection(HashSet::new));
    }

    private static Role buildRole(List<String> strings) {
        final Role role = new Role();
        if(strings.get(0)!=null) {
            role.setId(Long.valueOf(strings.get(0)));
        }
        role.setName(strings.get(1));
        return role;
    }

    public static JsonWebKey buildJsonWebKey(int kid)throws JoseException{
        final JsonWebKey jsonWebKey = RsaJwkGenerator.generateJwk(2048);
        jsonWebKey.setKeyId(String.valueOf(kid));
        return jsonWebKey;
    }

    public static JsonWebSignature buildJsonWebSignature
            (
            String email,
            List<String> rolesString,
            int kid,
            RsaJsonWebKey rsaJsonWebKey
            ) throws JoseException {

        final JwtClaims jwtClaims = new JwtClaims();
        jwtClaims.setIssuer(DOMAIN);
        jwtClaims.setExpirationTimeMinutesInTheFuture(120);
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

        return jsonWebSignature;
    }
}
