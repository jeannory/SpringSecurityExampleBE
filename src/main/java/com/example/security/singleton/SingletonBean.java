package com.example.security.singleton;

import org.apache.log4j.Logger;
import org.jose4j.jwk.JsonWebKey;
import org.jose4j.jwk.RsaJwkGenerator;
import org.jose4j.lang.JoseException;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
@Scope("singleton")
public class SingletonBean {

    private final static Logger logger = Logger.getLogger(SingletonBean.class);
    private static List<JsonWebKey> jsonWebKeys;
    private static ModelMapper modelMapper;

    public SingletonBean() {
        logger.info("**********");
        logger.info("Constructor SingletonBean begin");
        List<Integer> listInt = Arrays.asList(1, 2, 3);
        jsonWebKeys = listInt.stream().map(i -> {
            try {
                JsonWebKey jsonWebKey = RsaJwkGenerator.generateJwk(2048);
                jsonWebKey.setKeyId(String.valueOf(i));
                logger.info("JsonWebKeys number : " + i + " generate");
                return jsonWebKey;
            } catch (JoseException e) {
                e.printStackTrace();
                return null;
            }
        }).collect(Collectors.toCollection(ArrayList::new)).stream().filter(Objects::nonNull).collect(Collectors.toList());
        modelMapper = new ModelMapper();
        logger.info("Constructor SingletonBean end");
        logger.info("**********");
    }

    public List<JsonWebKey> getJsonWebKeys() {
        return jsonWebKeys;
    }

    public ModelMapper getModelMapper() {
        return modelMapper;
    }
}
