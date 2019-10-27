package com.example.security.controllers;

import com.example.security.entities.User;
import com.example.security.models.Credential;
import com.example.security.models.Token;
import com.example.security.repositories.UserRepository;
import com.example.security.tools.ITools;
import com.example.security.utils.BuilderUtils2;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import static com.example.security.contants.Constants.PRE_PATH;
import static com.example.security.contants.Constants.USER_CONTROLLER;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

/**
 * Initialize spring context at each tests
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class UserWebControllerTest1 implements ITools {

    private static final String BASE_URL = PRE_PATH + USER_CONTROLLER;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private BuilderUtils2 builderUtils2;
    @Autowired
    private UserRepository userRepository;

    @Test
    public void test_getDataTest_should_return_status_200_with_success_and_with_results() throws Exception {
        //given
        jdbcTemplate.execute(
                "delete from cuisine_users_roles; ");
        jdbcTemplate.execute(
                "delete from cuisine_role; ");
        jdbcTemplate.execute(
                "delete from cuisine_space; ");
        jdbcTemplate.execute(
                "delete from cuisine_user; ");
        //when
        final ResultActions resultActions = mockMvc.perform(get(BASE_URL + "/getDataTest").accept(MediaType.APPLICATION_JSON));

        //then
        final MvcResult mvcResult = resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$", is("success")))
                .andReturn();
        final String result = mvcResult.getResponse().getContentAsString();
        Assert.assertEquals("success", result);
        /**
         * check persistence of objects in IUserService.getDataTest()
         */
        final Credential credential = new Credential();
        credential.setEmail("jean@jean.com");
        credential.setPassword("0000");
        /**
         * userTest cannot be retrieved with getUsers because userTest has no roles (UserUserDTOConverter condition)
         */
        final MvcResult mvcResult2 = invokeGetUsers(credential)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(4)))
                .andExpect(jsonPath("$[0].email", is("jean@jean.com")))
                .andExpect(jsonPath("$[1].email", is("jeanne@jeanne.com")))
                .andExpect(jsonPath("$[2].email", is("john@john.com")))
                .andExpect(jsonPath("$[3].email", is("johny@johny.com")))
                .andExpect(jsonPath("$[0].id", is(2)))
                .andExpect(jsonPath("$[1].id", is(3)))
                .andExpect(jsonPath("$[2].id", is(4)))
                .andExpect(jsonPath("$[3].id", is(5)))
                .andReturn();
        /**
         * userTest cannot be retrieved with getUser because userTest has no roles (UserUserDTOConverter condition)
         */
        final ResultActions resultActions3 = invokeGetUser(credential, "userTest@test.com");
        final MvcResult mvcResult3 = resultActions3
                .andExpect(status().isNotFound())
                .andReturn();
        /**
         * userTest exist in memory test (or database) and the entity can be retrieve with userRepository.selectMyUserByEmail
         */
        final User user = userRepository.selectMyUserByEmail("userTest@test.com");
        Assert.assertEquals("userTest@test.com", user.getEmail());
    }

    /**
     * when getDataTest then throw sql exception constraint of unique column + constraint of duplicate key
     * rollback should works
     */
    @Test
    public void test_getDataTest_transactional_failed_should_status_500_with_no_new_persistence() throws Exception {
        //given
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
                "insert into cuisine_role(id, name) values (1, 'USER'); " +
                        "insert into cuisine_role(id, name) values (2, 'MANAGER'); " +
                        "insert into cuisine_role(id, name) values (3, 'ADMIN'); "
        );
        jdbcTemplate.execute(
                "delete from cuisine_user; " +
                        "insert into cuisine_user(id, email, password) values (1, 'jean@jean.com', '" + sha3Password + "'); ");
        jdbcTemplate.execute(
                "insert into cuisine_users_roles(cuisine_user_id, cuisine_role_id) values (1,1); " +
                        "insert into cuisine_users_roles(cuisine_user_id, cuisine_role_id) values (1,2); " +
                        "insert into cuisine_users_roles(cuisine_user_id, cuisine_role_id) values (1,3); "
        );
        //when
        final ResultActions resultActions = mockMvc.perform(get(BASE_URL + "/getDataTest").accept(MediaType.APPLICATION_JSON));

        //then
        final MvcResult mvcResult = resultActions.andExpect(status().isInternalServerError())
                .andReturn();
        /**
         * check persistence of objects in IUserService.getDataTest()
         *  only jean@jean.com has been persisted on db (or memory test)
         */
        final Credential credential = new Credential();
        credential.setEmail("jean@jean.com");
        credential.setPassword("0000");
        final MvcResult mvcResult2 = invokeGetUsers(credential)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].email", is("jean@jean.com")))
                .andReturn();
        final User user = userRepository.selectMyUserByEmail("userTest@test.com");
        Assert.assertNull("return null", user);
    }

    /**
     * Invoke end-points
     */
    private ResultActions invokeGetUser(final Credential credential, final String email) throws Exception{
        final String token = getAccessToken(credential);
        return mockMvc.perform(get(BASE_URL + "/getUser")
                .param("email",email)
                .header("Authorization", "Bearer " + token)
                .accept(MediaType.APPLICATION_JSON)
        );
    }

    private ResultActions invokeGetUsers(final Credential credential) throws Exception {
        final String token = getAccessToken(credential);
        return mockMvc.perform(get(BASE_URL + "/getUsers")
                .header("Authorization", "Bearer " + token)
                .accept(MediaType.APPLICATION_JSON));
    }

    private String getAccessToken(final Credential credential) throws Exception {
        final MvcResult mvcResult = invokeValidateConnection(credential)
                .andReturn();
        final Token token = builderUtils2.fromJsonResult(mvcResult, Token.class);
        return token.getToken();
    }

    private ResultActions invokeValidateConnection(final Credential credential) throws Exception {
        return mockMvc.perform(post(BASE_URL + "/validateConnection")
                .content(builderUtils2.asJsonString(credential))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
        );
    }

}
