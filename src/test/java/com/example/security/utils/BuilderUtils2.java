package com.example.security.utils;

import com.example.security.tools.ITools;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MvcResult;
import javax.annotation.PostConstruct;

@Component
public class BuilderUtils2 implements ITools {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    private ObjectMapper objectMapper;

    @PostConstruct
    private void initControllerUtils(){
        objectMapper = new ObjectMapper();
    }

    public void dataTestBuilder(){
        final String sha3Password = getStringSha3("0000");
        jdbcTemplate.execute(
                "delete from cuisine_users_roles; ");
        jdbcTemplate.execute(
                "delete from cuisine_role; ");
        jdbcTemplate.execute(
                "delete from cuisine_space; ");
        jdbcTemplate.execute(
                "delete from cuisine_user; ");
        jdbcTemplate.execute(
                "insert into cuisine_role(id, name) values (1001, 'USER'); " +
                        "insert into cuisine_role(id, name) values (1002, 'MANAGER'); " +
                        "insert into cuisine_role(id, name) values (1003, 'ADMIN'); "
        );
        jdbcTemplate.execute(
                "insert into cuisine_user(id, email, password) values (1001, 'jean@jean.com', '" + sha3Password + "'); " +
                        "insert into cuisine_user(id, email, password) values (1002, 'johny@johny.com', '" + sha3Password + "'); " +
                        "insert into cuisine_user(id, email, password) values (1003, 'jeanne@jeanne.com', '" + sha3Password + "'); "
        );
        jdbcTemplate.execute(
                "insert into cuisine_users_roles(cuisine_user_id, cuisine_role_id) values (1001,1001); " +
                        "insert into cuisine_users_roles(cuisine_user_id, cuisine_role_id) values (1001,1002); " +
                        "insert into cuisine_users_roles(cuisine_user_id, cuisine_role_id) values (1001,1003); " +
                        "insert into cuisine_users_roles(cuisine_user_id, cuisine_role_id) values (1002,1001); " +
                        "insert into cuisine_users_roles(cuisine_user_id, cuisine_role_id) values (1002,1002); " +
                        "insert into cuisine_users_roles(cuisine_user_id, cuisine_role_id) values (1003,1001); "
        );
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
