package com.example.security.controllers;

import com.example.security.dtos.RoleDTO;
import com.example.security.dtos.UserDTO;
import com.example.security.entities.User;
import com.example.security.enums.Gender;
import com.example.security.models.Credential;
import com.example.security.models.Token;
import com.example.security.repositories.UserRepository;
import com.example.security.tools.ITools;
import com.example.security.utils.BuilderUtils1;
import com.example.security.utils.BuilderUtils2;
import org.junit.Assert;
import org.junit.Before;
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
import java.util.Arrays;
import java.util.List;

import static com.example.security.contants.Constants.PRE_PATH;
import static com.example.security.contants.Constants.USER_CONTROLLER;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * https://docs.spring.io/spring-boot/docs/current/reference/html/spring-boot-features.html#boot-features-testing
 * Initialize spring context before the class
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class UserWebControllerTest2 implements ITools {

    private static final String BASE_URL = PRE_PATH + USER_CONTROLLER;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private BuilderUtils2 builderUtils2;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Before
    public void setUp() throws Exception {
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

    /**
     * tests of end-points
     */
    @Test
    public void test_validateConnection_when_credential_valid_should_return_status_ok_with_Token() throws Exception {
        //given
        final Credential credential = new Credential();
        credential.setEmail("jean@jean.com");
        credential.setPassword("0000");

        //when && then
        final MvcResult mvcResult = invokeValidateConnection(credential)
                .andExpect(status().isOk())
                .andReturn();
        final Token result = builderUtils2.fromJsonResult(mvcResult, Token.class);
        System.out.println(result.getToken());
        Assert.assertFalse(result.getToken().isEmpty());
        final String sub = BuilderUtils1.getStringFromJwtNode(result.getToken(), 1, "sub");
        Assert.assertEquals("jean@jean.com", sub);
        final String roles = BuilderUtils1.getStringFromJwtNode(result.getToken(), 1, "roles");
        Assert.assertEquals("[\"USER\",\"MANAGER\",\"ADMIN\"]", roles);
    }

    @Test
    public void test_validateConnection_when_credential_not_valid_should_return_status_unauthorized_401() throws Exception {
        //given
        final Credential credential = new Credential();
        credential.setEmail("jean@jean.com");
        credential.setPassword("wrong password");

        //when && then
        final MvcResult mvcResult = invokeValidateConnection(credential)
                .andExpect(status().isUnauthorized())
                .andReturn();
    }

    @Test
    public void test_validateConnection_when_credential_email_no_exist_should_return_status_unauthorized_401() throws Exception {
        //given
        final Credential credential = new Credential();
        credential.setEmail("EmailNotOnDatabase@gmail.com");
        credential.setPassword("wrong password");

        //when && then
        final MvcResult mvcResult = invokeValidateConnection(credential)
                .andExpect(status().isUnauthorized())
                .andReturn();
    }

    @Test
    public void test_validateConnection_when_credential_email_is_empty_should_return_status_404() throws Exception {
        //given
        final Credential credential = new Credential();
        credential.setEmail("");
        credential.setPassword("1234");

        //when && then
        final MvcResult mvcResult = invokeValidateConnection(credential)
                .andExpect(status().isNotFound())
                .andReturn();
    }

    @Test
    public void test_validateConnection_when_credential_password_is_empty_should_return_status_404() throws Exception {
        //given
        final Credential credential = new Credential();
        credential.setEmail("jean@jean.com");
        credential.setPassword("");

        //when && then
        final MvcResult mvcResult = invokeValidateConnection(credential)
                .andExpect(status().isNotFound())
                .andReturn();
    }

    @Test
    public void test_validateConnection_when_credential_email_is_not_valid_email_should_return_status_404() throws Exception {
        //given
        final Credential credential = new Credential();
        credential.setEmail("this is not an email");
        credential.setPassword("1234");

        //when && then
        final MvcResult mvcResult = invokeValidateConnection(credential)
                .andExpect(status().isNotFound())
                .andReturn();
    }

    @Test
    public void test_getUser_when_jwt_role_is_ADMIN_should_return_himself_with_status_ok() throws Exception{
        //given
        final Credential credential = new Credential();
        credential.setEmail("jean@jean.com");
        credential.setPassword("0000");
        final ResultActions resultActions = invokeGetUser(credential, "jean@jean.com");

        //when && then
        final MvcResult mvcResult = resultActions
                .andExpect(status().isOk())
                .andReturn();
        final UserDTO result = builderUtils2.fromJsonResult(mvcResult, UserDTO.class);
        Assert.assertTrue(result.getId()==1001L);
        Assert.assertEquals("jean@jean.com", result.getEmail());
        Assert.assertEquals("USER, MANAGER, ADMIN", result.getFlattenRoles());
        Assert.assertNull("userDTO password is always null", result.getPassword());
    }

    @Test
    public void test_getUser_when_jwt_role_ADMIN_should_return_other_UserDTO_with_status_ok() throws Exception{
        //given
        final Credential credential = new Credential();
        credential.setEmail("jean@jean.com");
        credential.setPassword("0000");
        final ResultActions resultActions = invokeGetUser(credential, "jeanne@jeanne.com");

        //when && then
        final MvcResult mvcResult = resultActions
                .andExpect(status().isOk())
                .andReturn();
        final UserDTO result = builderUtils2.fromJsonResult(mvcResult, UserDTO.class);
        Assert.assertTrue(result.getId()==1003L);
        Assert.assertEquals("jeanne@jeanne.com", result.getEmail());
        Assert.assertEquals("USER", result.getFlattenRoles());
        Assert.assertNull("userDTO password is always null", result.getPassword());
    }

    @Test
    public void test_getUser_when_jwt_role_MANAGER_should_return_himself_with_status_ok() throws Exception{
        //given
        final Credential credential = new Credential();
        credential.setEmail("johny@johny.com");
        credential.setPassword("0000");
        final ResultActions resultActions = invokeGetUser(credential, "johny@johny.com");

        //when && then
        final MvcResult mvcResult = resultActions
                .andExpect(status().isOk())
                .andReturn();
        final UserDTO result = builderUtils2.fromJsonResult(mvcResult, UserDTO.class);
        Assert.assertTrue(result.getId()==1002L);
        Assert.assertEquals("johny@johny.com", result.getEmail());
        Assert.assertEquals("USER, MANAGER", result.getFlattenRoles());
        Assert.assertNull("userDTO password is always null", result.getPassword());
    }

    @Test
    public void test_getUser_when_jwt_role_MANAGER_should_return_status_forbidden_for_other_UserDTO() throws Exception{
        //given
        final Credential credential = new Credential();
        credential.setEmail("johny@johny.com");
        credential.setPassword("0000");
        final ResultActions resultActions = invokeGetUser(credential, "jeanne@jeanne.com");

        //when && then
        final MvcResult mvcResult = resultActions
                .andExpect(status().isForbidden())
                .andReturn();
    }

    @Test
    public void test_getUser_when_jwt_role_ADMIN_with_userDTO_not_found_should_return_status_404() throws Exception{
        //given
        final Credential credential = new Credential();
        credential.setEmail("jean@jean.com");
        credential.setPassword("0000");
        final ResultActions resultActions = invokeGetUser(credential, "not found user");

        //when && then
        final MvcResult mvcResult = resultActions
                .andExpect(status().isNotFound())
                .andReturn();
    }

    @Test
    public void test_getRoles_when_has_role_ADMIN_should_return_status_ok_with_roles() throws Exception {
        //given
        final Credential credential = new Credential();
        credential.setEmail("jean@jean.com");
        credential.setPassword("0000");

        //when && then
        final MvcResult mvcResult = invokeGetRoles(credential)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].name", is("USER")))
                .andExpect(jsonPath("$[1].name", is("MANAGER")))
                .andExpect(jsonPath("$[2].name", is("ADMIN")))
                .andReturn();
        final RoleDTO[] roles = builderUtils2.fromJsonResult(mvcResult, RoleDTO[].class);
        Assert.assertEquals("USER", roles[0].getName());
        Assert.assertEquals("MANAGER", roles[1].getName());
        Assert.assertEquals("ADMIN", roles[2].getName());
    }

    @Test
    public void test_getRoles_when_has_role_MANAGER_should_return_status_forbidden() throws Exception {
        //given
        final Credential credential = new Credential();
        credential.setEmail("johny@johny.com");
        credential.setPassword("0000");

        //when && then
        final MvcResult mvcResult = invokeGetRoles(credential)
                .andExpect(status().isForbidden())
                .andReturn();
    }

    @Test
    public void test_getRoles_when_has_role_USER_should_return_status_forbidden() throws Exception {
        //given
        final Credential credential = new Credential();
        credential.setEmail("jeanne@jeanne.com");
        credential.setPassword("0000");

        //when && then
        final MvcResult mvcResult = invokeGetRoles(credential)
                .andExpect(status().isForbidden())
                .andReturn();
    }

    @Test
    public void test_getRoles_when_no_token_on_header_should_return_forbidden() throws Exception {
        //given
        final ResultActions resultActions = mockMvc.perform(get(BASE_URL + "/getRoles")
                .accept(MediaType.APPLICATION_JSON));

        //when && then
        final MvcResult mvcResult = resultActions
                .andExpect(status().isForbidden())
                .andReturn();
    }

    @Test
    public void test_registerUser_when_parameters_valid_should_return_token() throws Exception {
        //given
        final UserDTO userDTO = new UserDTO();
        userDTO.setEmail("john11@john.com");
        userDTO.setPassword("0000");
        userDTO.setFirstName("john");
        userDTO.setLastName("john");
        userDTO.setGender(Gender.Monsieur);

        //when && then
        final MvcResult mvcResult = invokeRegisterUser(userDTO)
                .andExpect(status().isOk())
                .andReturn();
        final Token result = builderUtils2.fromJsonResult(mvcResult, Token.class);
        Assert.assertTrue(!result.getToken().isEmpty());
        //decode token
        final String sub = BuilderUtils1.getStringFromJwtNode(result.getToken(), 1, "sub");
        Assert.assertEquals("john11@john.com", sub);
        //user found on db
        User user = userRepository.findByEmail("john11@john.com");
        Assert.assertEquals("john11@john.com", user.getEmail());
    }

    @Test
    public void test_registerUser_when_email_is_missing_should_return_status_404() throws Exception {
        //given
        final UserDTO userDTO = new UserDTO();
        userDTO.setPassword("0000");
        userDTO.setFirstName("firstName never persisted");
        userDTO.setLastName("lastName never persisted");
        userDTO.setGender(Gender.Monsieur);

        //when && then
        final MvcResult mvcResult = invokeRegisterUser(userDTO)
                .andExpect(status().isNotFound())
                .andReturn();
        //user not found on db (transactional works)
        List<User> users = userRepository.findAll();
        users.forEach(user->{
            if(user.getFirstName()!=null){
                Assert.assertTrue(!user.getFirstName().equals("firstName never persisted"));
            }
            if(user.getLastName()!=null){
                Assert.assertTrue(!user.getLastName().equals("lastName never persisted"));
            }
        });
    }

    @Test
    public void test_registerUser_when_password_is_missing_should_return_status_404() throws Exception {
        //given
        final UserDTO userDTO = new UserDTO();
        userDTO.setEmail("john11@john.com");
        userDTO.setFirstName("john");
        userDTO.setLastName("john");
        userDTO.setGender(Gender.Monsieur);

        //when && then
        final MvcResult mvcResult = invokeRegisterUser(userDTO)
                .andExpect(status().isNotFound())
                .andReturn();
        //user not found on db (transactional works)
        User user = userRepository.findByEmail("john11@john.com");
        Assert.assertNull("return null", user);
    }

    @Test
    public void test_registerUser_when_firstName_is_missing_should_return_status_404() throws Exception {
        //given
        final UserDTO userDTO = new UserDTO();
        userDTO.setEmail("john11@john.com");
        userDTO.setPassword("0000");
        userDTO.setLastName("john");
        userDTO.setGender(Gender.Monsieur);

        //when && then
        final MvcResult mvcResult = invokeRegisterUser(userDTO)
                .andExpect(status().isNotFound())
                .andReturn();
        //user not found on db (transactional works)
        User user = userRepository.findByEmail("john11@john.com");
        Assert.assertNull("return null", user);
    }

    @Test
    public void test_registerUser_when_lastName_is_missing_should_return_status_404() throws Exception {
        //given
        final UserDTO userDTO = new UserDTO();
        userDTO.setEmail("john11@john.com");
        userDTO.setPassword("0000");
        userDTO.setFirstName("john");
        userDTO.setGender(Gender.Monsieur);

        //when && then
        final MvcResult mvcResult = invokeRegisterUser(userDTO)
                .andExpect(status().isNotFound())
                .andReturn();
        //user not found on db (transactional works)
        User user = userRepository.findByEmail("john11@john.com");
        Assert.assertNull("return null", user);
    }

    //toDo to reorganize && unit test cases toDo
    @Test
    public void test_setUser_when_all_parameters_valid_should_return_status_200_with_result() throws Exception{
        //given
        final UserDTO userDTO = new UserDTO();
        userDTO.setEmail("jeanne@jeanne.com");
        userDTO.setFirstName("new jeanne");
        userDTO.setLastName("new jeanne");
        userDTO.setGender(Gender.Madame);
        final Credential credential = new Credential();
        credential.setEmail("jeanne@jeanne.com");
        credential.setPassword("0000");
        final String token = getAccessToken(credential);
        final ResultActions resultActions = mockMvc.perform(put(BASE_URL+"/setUser")
            .content(builderUtils2.asJsonString(userDTO))
                .header("Authorization", "Bearer " + token)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
        );

        //when && then
        final MvcResult mvcResult = resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("firstName", is("new jeanne")))
                .andExpect(jsonPath("lastName", is("new jeanne")))
                .andReturn();
    }

    //toDo unit test cases toDo
    @Test
    public void test_getUsers_when_has_ADMIN_role_should_return_status_ok_with_results() throws Exception {
        //given
        final Credential credential = new Credential();
        credential.setEmail("jean@jean.com");
        credential.setPassword("0000");

        //when && then
        final MvcResult mvcResult = invokeGetUsers(credential)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].email", is("jean@jean.com")))
                .andExpect(jsonPath("$[1].email", is("johny@johny.com")))
                .andExpect(jsonPath("$[2].email", is("jeanne@jeanne.com")))
                .andReturn();
        final UserDTO[] userDTOS = builderUtils2.fromJsonResult(mvcResult, UserDTO[].class);
        Assert.assertEquals("jean@jean.com", userDTOS[0].getEmail());
        Assert.assertEquals("johny@johny.com", userDTOS[1].getEmail());
        Assert.assertEquals("jeanne@jeanne.com", userDTOS[2].getEmail());
    }

    //toDo to reorganize && unit test cases toDo
    @Test
    public void test_getUserRoles_when_has_ADMIN_should_return_status_ok_with_results() throws Exception{
        //given
        final Credential credential = new Credential();
        credential.setEmail("jean@jean.com");
        credential.setPassword("0000");
        final String token = getAccessToken(credential);
        final ResultActions resultActions = mockMvc.perform(get(BASE_URL+"/getUserRoles")
        .header("Authorization", "Bearer " + token)
                .param("email", "jean@jean.com")
                .accept(MediaType.APPLICATION_JSON)
        );

        //when && then
        final MvcResult mvcResult = resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$",hasSize(3)))
                .andExpect(jsonPath("$[0].name", is("USER")))
                .andExpect(jsonPath("$[1].name", is("MANAGER")))
                .andExpect(jsonPath("$[2].name", is("ADMIN")))
                .andReturn();
    }

    //toDo to reorganize && unit test cases toDo
    @Test
    public void test_putUserRoles_when_has_ADMIN_should_return_status_ok_with_results() throws Exception {
        //given
        final Credential credential = new Credential();
        credential.setEmail("jean@jean.com");
        credential.setPassword("0000");
        credential.setPassword("0000");
        final ResultActions resultActions1 = invokeGetUser(credential, "jeanne@jeanne.com");
        final MvcResult mvcResult1 = resultActions1
                .andExpect(status().isOk())
                .andExpect(jsonPath("email",is("jeanne@jeanne.com")))
                //jeanne@jeanne.com roles
                .andExpect(jsonPath("flattenRoles",is("USER")))
                .andReturn();
        List<RoleDTO> roleDTOS = Arrays.asList(
                BuilderUtils1.buildRoleDTO(Arrays.asList("1001","USER")),
                BuilderUtils1.buildRoleDTO(Arrays.asList("1002","MANAGER")),
                BuilderUtils1.buildRoleDTO(Arrays.asList("1003","ADMIN"))
        );
        final String token = getAccessToken(credential);

        //when
        final ResultActions resultActions2 = mockMvc.perform(put(BASE_URL+"/putUserRoles")
                .content(builderUtils2.asJsonString(roleDTOS))
                .param("email", "jeanne@jeanne.com")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
        );

        //then
        final MvcResult mvcResult2 = resultActions2
                .andExpect(status().isOk())
                .andExpect(jsonPath("$",hasSize(3)))
                .andExpect(jsonPath("$[0].email",is("jean@jean.com")))
                .andExpect(jsonPath("$[0].password",isEmptyOrNullString()))
                .andExpect(jsonPath("$[1].email",is("johny@johny.com")))
                .andExpect(jsonPath("$[1].password",isEmptyOrNullString()))
                .andExpect(jsonPath("$[2].email",is("jeanne@jeanne.com")))
                .andExpect(jsonPath("$[2].password",isEmptyOrNullString()))
                //jeanne@jeanne.com roles have changed
                .andExpect(jsonPath("$[2].flattenRoles",is("USER, MANAGER, ADMIN")))
                .andReturn();
    }

    @Test
    public void test_putUserRoles_when_has_ADMIN_when_ids_roles_is_missing() throws Exception {
        //given
        final Credential credential = new Credential();
        credential.setEmail("jean@jean.com");
        credential.setPassword("0000");
        final String token = getAccessToken(credential);

        //when
        final ResultActions resultActions = mockMvc.perform(put(BASE_URL+"/putUserRoles")
                        .content("[{\"name\":\"USER\"},{\"name\":\"MANAGER\"},{\"name\":\"ADMIN\"}]")
                        .param("email", "jeanne@jeanne.com")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
        );

        //then
        //return 500
        final MvcResult mvcResult = resultActions
                .andExpect(status().isInternalServerError())
                .andReturn();
        //jeanne@jeanne.com roles haven't changed
        final ResultActions resultActions1 = invokeGetUser(credential, "jeanne@jeanne.com");
        final MvcResult mvcResult1 = resultActions1
                .andExpect(status().isOk())
                .andExpect(jsonPath("email",is("jeanne@jeanne.com")))
                .andExpect(jsonPath("flattenRoles",is("USER")))
                .andReturn();
    }

    /**
     * Invoke end-points
     */
    private ResultActions invokeValidateConnection(final Credential credential) throws Exception {
        return mockMvc.perform(post(BASE_URL + "/validateConnection")
                .content(builderUtils2.asJsonString(credential))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
        );
    }

    private ResultActions invokeGetUser(final Credential credential, final String email) throws Exception{
        final String token = getAccessToken(credential);
        return mockMvc.perform(get(BASE_URL + "/getUser")
                .param("email",email)
                .header("Authorization", "Bearer " + token)
                .accept(MediaType.APPLICATION_JSON)
        );
    }

    private ResultActions invokeGetRoles(final Credential credential) throws Exception {
        final String token = getAccessToken(credential);
        return mockMvc.perform(get(BASE_URL + "/getRoles")
                .header("Authorization", "Bearer " + token)
                .accept(MediaType.APPLICATION_JSON));
    }

    private ResultActions invokeRegisterUser(final UserDTO userDTO) throws Exception{
        return mockMvc.perform(post(BASE_URL + "/registerUser")
                .content(builderUtils2.asJsonString(userDTO))
                .contentType(MediaType.APPLICATION_JSON)
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
}
