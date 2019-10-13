package com.example.security.converter;

import com.example.security.dtos.SuperDTO;
import com.example.security.entities.SuperEntity;
import com.example.security.exceptions.CustomConverterException;
import com.example.security.singleton.SingletonBean;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SuperModelMapper<E extends SuperEntity, D extends SuperDTO> {

    private final static Logger logger = Logger.getLogger(SuperModelMapper.class);
    @Autowired
    private SingletonBean singletonBean;

    public Optional<D> convertToDTO(E entity1) {
        logger.info("Method convertToDTO");
        try {
            final E entity2 = validateEntity(entity1);
            final D dto = singletonBean.getModelMapper().map(entity2, (Type) entity2.getDTOClass());
            return Optional.of(dto);
        } catch (CustomConverterException ex) {
            logger.error(ex.getMessage());
            return null;
        }
    }

    private E validateEntity(E entity) {
        logger.info("Method validateEntity");
        if (entity == null) {
            throw new CustomConverterException("Entity cannot be null");
        }
        return entity;
    }

    public Optional<E> convertToEntity(D dto1) {
        logger.info("Method convertToEntity");
        try {
            final D dto2 = validateDTO(dto1);
            final E entiy = singletonBean.getModelMapper().map(dto2, (Type) dto2.getEntityClass());
            return Optional.of(entiy);
        } catch (CustomConverterException ex) {
            logger.error(ex.getMessage());
            return null;
        }
    }

    private D validateDTO(D dto) {
        logger.info("Method validateDTO");
        if (dto == null) {
            throw new CustomConverterException("Dto cannot be null");
        }
        return dto;
    }

    public List<D> convertToDTOs(List<E> entities) {
        logger.info("Method convertToDTOs");
        return entities.stream().map(entity -> {
            try {
                final Optional<D> dto = convertToDTO(entity);
                return dto.get();
            } catch (CustomConverterException ex) {
                logger.error(ex.getMessage());
                return null;
            }
        }).collect(Collectors.toList()).stream().filter(Objects::nonNull).collect(Collectors.toList());
    }

    public List<E> convertToEntities(List<D> dtos) {
        logger.info("Method convertToEntities");
        return dtos.stream().map(dto -> {
            try {
                final Optional<E> entity = convertToEntity(dto);
                return entity.get();
            } catch (CustomConverterException ex) {
                logger.error(ex.getMessage());
                return null;
            }
        }).collect(Collectors.toList()).stream().filter(Objects::nonNull).collect(Collectors.toList());
    }
}
