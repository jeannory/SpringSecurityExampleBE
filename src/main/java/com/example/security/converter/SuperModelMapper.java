package com.example.security.converter;

import com.example.security.dtos.SuperDTO;
import com.example.security.entities.SuperEntity;
import com.example.security.exceptions.CustomConverterException;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

//if conversion failed should return null
//already manage for list
//need to be manage for simple object in service
@Service
public class SuperModelMapper<E extends SuperEntity, D extends SuperDTO> {

    private static ModelMapper modelMapper;

    public Optional<D> convertToDTO(E entity) throws CustomConverterException {
        modelMapper = new ModelMapper();
        try {
            D dto = modelMapper.map(entity, (Type) entity.getDTOClass());
            return Optional.of(dto);
        } catch (Exception ex) {
            throw new CustomConverterException("Conversion failed");
        }
    }

    public Optional<E> convertToEntity(D dto) throws CustomConverterException {
        modelMapper = new ModelMapper();
        try {
            E entiy = modelMapper.map(dto, (Type) dto.getEntityClass());
            return Optional.of(entiy);
        } catch (Exception ex) {
            throw new CustomConverterException("Conversion failed");
        }
    }

    public Optional<List<D>> convertToDTOs(List<E> entities) {
        return Optional.of(
                entities.stream().map(entity -> {
                    try {
                        Optional<D> dto = convertToDTO(entity);
                        return dto.get();
                    } catch (CustomConverterException ex) {
                        ex.printStackTrace();
                        return null;
                    }
                }).collect(Collectors.toList()));
    }

    public Optional<List<E>> convertToEntities(List<D> dtos) {
        return Optional.of(
                dtos.stream().map(dto -> {
                    try {
                        Optional<E> entity = convertToEntity(dto);
                        return entity.get();
                    } catch (CustomConverterException ex) {
                        ex.printStackTrace();
                        return null;
                    }
                }).collect(Collectors.toList()));
    }
}
