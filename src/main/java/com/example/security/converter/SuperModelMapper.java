package com.example.security.converter;

import com.example.security.dtos.SuperDTO;
import com.example.security.dtos.UserDTO;
import com.example.security.entities.Role;
import com.example.security.entities.SuperEntity;
import com.example.security.entities.User;
import com.example.security.singleton.SingletonBean;
import com.example.security.tools.ITools;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class SuperModelMapper<E extends SuperEntity, D extends SuperDTO>  implements ITools {

    private final static Logger logger = Logger.getLogger(SuperModelMapper.class);
    @Autowired
    private SingletonBean singletonBean;

    public Optional<D> convertToDTO(final E entity) {
        logger.info("Method convertToDTO");
        if (entity == null) {
            return Optional.empty();
        }
        D dto = singletonBean.getModelMapper().map(entity, (Type) entity.getDTOClass());

        if(entity.getClass().equals(User.class)){
            dto = (D) convertToUserDTO(entity, dto);
            if(dto==null){
                return Optional.empty();
            }
        }
        return Optional.of(dto);
    }

    private UserDTO convertToUserDTO(final E entity, final D dto) {
        logger.info("convertToUserDTO");
        final User user = (User) entity;
        if(user.getRoles().isEmpty()){
            return null;
        }
        final UserDTO userDTO = (UserDTO) dto;
        userDTO.setPassword(null);
        userDTO.setFlattenRoles(buildFlattenRoles(new ArrayList<>(user.getRoles())));
        return userDTO;
    }

    private String buildFlattenRoles(final List<Role> roles) {
        logger.info("Method buildFlattenRoles");
        final List<String> list = roles.stream().sorted(Comparator.comparing(Role::getId)).map(Role::getName).collect(Collectors.toList());
        final String flattenRoles = list.stream().map(Object::toString).collect(Collectors.joining(", "));
        return flattenRoles;
    }

    public Optional<E> convertToEntity(final D dto) {
        logger.info("Method convertToEntity");
            if (dto == null) {
                return Optional.empty();
            }
            E entity = singletonBean.getModelMapper().map(dto, (Type) dto.getEntityClass());
            if (dto.getClass().equals(UserDTO.class)) {
                entity = convertToUser(entity);
            }
            return Optional.of(entity);
    }

    //hash password for persistence or authentication
    private E convertToUser(final E entity){
        logger.info("Method convertToUser");
        final User user = (User) entity;
        user.setPassword(getStringSha3(user.getPassword()));
        return  (E) user;
    }

    public List<D> convertToDTOs(final List<E> entities) {
        logger.info("Method convertToDTOs");
        return entities.stream().map(entity -> {
            final Optional<D> dto = convertToDTO(entity);
            try {
                return dto.get();
            } catch (NoSuchElementException ex) {
                logger.error(ex.getMessage());
                return null;
            }
        }).collect(Collectors.toList()).stream().filter(Objects::nonNull).collect(Collectors.toList());
    }

    public List<E> convertToEntities(final List<D> dtos) {
        logger.info("Method convertToEntities");
        return dtos.stream().map(dto -> {
            final Optional<E> entity = convertToEntity(dto);
            try{
            return entity.get();
            } catch (NoSuchElementException ex) {
                logger.error(ex.getMessage());
                return null;
            }
        }).collect(Collectors.toList()).stream().filter(Objects::nonNull).collect(Collectors.toList());
    }
}
