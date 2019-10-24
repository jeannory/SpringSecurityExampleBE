package com.example.security.converter;

import com.example.security.dtos.UserDTO;
import com.example.security.entities.Role;
import com.example.security.entities.User;
import com.example.security.singleton.SingletonBean;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class UserUserDTOConverter {

    private final static Logger logger = Logger.getLogger(UserUserDTOConverter.class);
    @Autowired
    private SingletonBean singletonBean;

    public Optional<UserDTO> convertToUserDTO(User user) {
        logger.info("Method convertToUserDTO");
        if(user==null||user.getRoles().isEmpty()){
            return Optional.empty();
        }
            final UserDTO userDTO = singletonBean.getModelMapper().map(user, UserDTO.class);
            userDTO.setPassword(null);
            userDTO.setFlattenRoles(buildFlattenRoles(new ArrayList<>(user.getRoles())));
            return Optional.of(userDTO);
    }

    public List<UserDTO> convertToUserDTOs(List<User> users) {
        logger.info("Method convertToUserDTOs");
        return users.stream().map(user -> {
                final Optional<UserDTO> userDTO = convertToUserDTO(user);
                try {
                    return userDTO.get();
                }catch(NoSuchElementException ex){
                    logger.error(ex.getMessage());
                    return null;
            }
        }).collect(Collectors.toList()).stream().filter(Objects::nonNull).collect(Collectors.toList());
    }

    private String buildFlattenRoles(List<Role> roles) {
        logger.info("Method buildFlattenRoles");
        final List<String> list = roles.stream().sorted(Comparator.comparing(Role::getId)).map(Role::getName).collect(Collectors.toList());
        final String flattenRoles = list.stream().map(Object::toString).collect(Collectors.joining(", "));
        return flattenRoles;
    }
}
