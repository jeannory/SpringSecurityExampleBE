package com.example.security.services;

import com.example.security.dtos.UserDTO;
import com.example.security.enums.Gender;
import com.example.security.models.Token;

import java.util.List;

public interface IUserService {

    boolean getDataTest();

    UserDTO findUserDTOByEmail(String email);

    Token generateUser(UserDTO userDTOEntry);

    UserDTO modifyUser(UserDTO userDTO);

    List<UserDTO> getUsers();

    List<Gender> getGenders();

    List<UserDTO> changeUserSatus(UserDTO userDTO);
}
