package com.example.security.converter;

import com.example.security.dtos.SuperDTO;
import com.example.security.entities.SuperEntity;
import com.example.security.singleton.SingletonBean;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class SuperModelMapper<E extends SuperEntity, D extends SuperDTO> {

    private final static Logger logger = Logger.getLogger(SuperModelMapper.class);
    @Autowired
    private SingletonBean singletonBean;

    public Optional<D> convertToDTO(E entity) {
        logger.info("Method convertToDTO");
        if (entity == null) {
            return Optional.empty();
        }
        final D dto = singletonBean.getModelMapper().map(entity, (Type) entity.getDTOClass());
        return Optional.of(dto);
    }

    public Optional<E> convertToEntity(D dto) {
        logger.info("Method convertToEntity");
        if (dto == null) {
            return Optional.empty();
        }
        final E entity = singletonBean.getModelMapper().map(dto, (Type) dto.getEntityClass());
        return Optional.of(entity);
    }

    public List<D> convertToDTOs(List<E> entities) {
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

    public List<E> convertToEntities(List<D> dtos) {
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
