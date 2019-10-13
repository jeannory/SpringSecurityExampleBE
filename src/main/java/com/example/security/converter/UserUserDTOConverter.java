package com.example.security.converter;

import com.example.security.dtos.UserDTO;
import com.example.security.entities.Role;
import com.example.security.entities.User;
import com.example.security.singleton.SingletonBean;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class UserUserDTOConverter {

    private final static Logger logger = Logger.getLogger(UserUserDTOConverter.class);
    @Autowired
    private SingletonBean singletonBean;

    public Optional<UserDTO> convertToUserDTO(User user) {
        logger.info("Method convertToUserDTO");
            final UserDTO userDTO = singletonBean.getModelMapper().map(user, UserDTO.class);
            userDTO.setFlattenRoles(buildFlattenRoles(new ArrayList<>(user.getRoles())));
            return Optional.of(userDTO);
    }

    public List<UserDTO> convertToUserDTOs(List<User> users) {
        logger.info("Method convertToUserDTOs");
        return users.stream().map(user -> {
                final Optional<UserDTO> userDTO = convertToUserDTO(user);
                return userDTO.get();
        }).collect(Collectors.toList());
    }

    private String buildFlattenRoles(List<Role> roles) {
        logger.info("Method buildFlattenRoles");
        final Optional<List<String>> list = Optional.of(roles.stream().map(Role::getName).collect(Collectors.toList()));
        final String flattenRoles = list.get().stream().map(Object::toString).collect(Collectors.joining(", "));
        return flattenRoles;
    }
}
