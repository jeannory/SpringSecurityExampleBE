package com.example.security.utils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MvcResult;
import javax.annotation.PostConstruct;

@Component
public class BuilderUtils2{

    private ObjectMapper objectMapper;

    @PostConstruct
    private void initControllerUtils(){
        objectMapper = new ObjectMapper();
    }

    public String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public <T> T fromJsonResult(MvcResult mvcResult, Class<T> tClass) throws Exception {
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        return this.objectMapper.readValue(mvcResult.getResponse().getContentAsString(), tClass);
    }
}
