package com.example.security.converter;

import com.example.security.dtos.SuperDTO;
import com.example.security.entities.SuperEntity;
import com.example.security.exceptions.CustomConverterException;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SuperModelMapper<E extends SuperEntity, D extends SuperDTO> {

    private static ModelMapper modelMapper;

    public Optional<D> convertToDTO(E entity1) {
        modelMapper = new ModelMapper();
        try {
            E entity2 = validateEntity(entity1);
            D dto = modelMapper.map(entity2, (Type) entity2.getDTOClass());
            return Optional.of(dto);
        } catch (CustomConverterException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    private E validateEntity(E entity) {
        if (entity == null) {
            throw new CustomConverterException("Entity cannot be null");
        }
        return entity;
    }

    public Optional<E> convertToEntity(D dto1) {
        modelMapper = new ModelMapper();
        try {
            D dto2 = validateDTO(dto1);
            E entiy = modelMapper.map(dto2, (Type) dto2.getEntityClass());
            return Optional.of(entiy);
        } catch (CustomConverterException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    private D validateDTO(D dto) {
        if (dto == null) {
            throw new CustomConverterException("Dto cannot be null");
        }
        return dto;
    }

    public List<D> convertToDTOs(List<E> entities) {
        return entities.stream().map(entity -> {
            try {
                Optional<D> dto = convertToDTO(entity);
                return dto.get();
            } catch (CustomConverterException ex) {
                ex.printStackTrace();
                return null;
            }
        }).collect(Collectors.toList()).stream().filter(Objects::nonNull).collect(Collectors.toList());
    }

    public List<E> convertToEntities(List<D> dtos) {
        return dtos.stream().map(dto -> {
            try {
                Optional<E> entity = convertToEntity(dto);
                return entity.get();
            } catch (CustomConverterException ex) {
                ex.printStackTrace();
                return null;
            }
        }).collect(Collectors.toList()).stream().filter(Objects::nonNull).collect(Collectors.toList());
    }
}
