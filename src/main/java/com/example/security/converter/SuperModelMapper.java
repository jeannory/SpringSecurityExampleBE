package com.example.security.converter;

import com.example.security.dtos.SuperDTO;
import com.example.security.entities.SuperEntity;
import com.example.security.exceptions.CustomConverterException;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class SuperModelMapper<E extends SuperEntity, D extends SuperDTO> {

    private static ModelMapper modelMapper;

    public Optional<D> convertToDTO(E entity) throws CustomConverterException {
        modelMapper = new ModelMapper();
        D d = modelMapper.map(entity, (Type) entity.getDTOClass());
        if(d==null){
            throw new CustomConverterException("Conversion failed");
        }
        return Optional.of(d);
    }

    public Optional<E> convertToEntity(D dto) throws CustomConverterException {
        modelMapper = new ModelMapper();
            E e = modelMapper.map(dto, (Type) dto.getEntityClass());
            if (e == null) {
                throw new CustomConverterException("Conversion failed");
            }
        return Optional.of(e);
    }

    public Optional<List<D>> convertToDTOs(List<E> entities) throws CustomConverterException {
        List<D> dtos = new ArrayList<>();
        entities.forEach(entity -> {
            try{
                Optional<D> d = convertToDTO(entity);
                dtos.add(convertToDTO(entity).get());
            } catch (CustomConverterException ex) {
                ex.printStackTrace();
            }

        });
        return Optional.of(dtos);
    }

    public Optional<List<E>> convertToEntities(List<D> dtos) throws CustomConverterException {
        List<E> entities = new ArrayList<>();
        dtos.forEach(dto -> {
            try{
                E e = convertToEntity(dto).get();
                entities.add(e);
            } catch (CustomConverterException ex) {
                ex.printStackTrace();
            }

        });
        return Optional.of(entities);
    }
}
