package com.example.security.services;

import com.example.security.dtos.UserDTO;
import com.example.security.entities.User;
import com.example.security.enums.Gender;
import com.example.security.exceptions.CustomConverterException;
import com.example.security.models.Credential;
import com.example.security.models.Token;
import com.example.security.models.TokenUtility;
import org.jose4j.jwt.MalformedClaimException;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.lang.JoseException;

import java.io.IOException;
import java.util.List;

public interface IUserService {

    void getDataTest();

    UserDTO findUserDTOByEmail(String email);

    Token generateUser(UserDTO userDTOEntry)throws CustomConverterException;

    UserDTO setUser(UserDTO userDTO);

    List<UserDTO> getUsers() throws CustomConverterException;

    List<Gender> getGenders();

    List<UserDTO> changeUserSatus(UserDTO userDTO);
}
