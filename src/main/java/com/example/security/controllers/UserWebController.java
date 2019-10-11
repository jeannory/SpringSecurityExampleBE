package com.example.security.controllers;

import com.example.security.config.AuthProvider;
import com.example.security.dtos.RoleDTO;
import com.example.security.dtos.UserDTO;
import com.example.security.enums.Gender;
import com.example.security.exceptions.CustomConverterException;
import com.example.security.models.Credential;
import com.example.security.models.Token;
import com.example.security.services.IRoleService;
import com.example.security.services.IUserService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import static com.example.security.contants.Constants.*;
import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping(PRE_PATH + USER_CONTROLLER)
public class UserWebController extends SuperController {

    private final static Logger logger = Logger.getLogger(UserWebController.class);
    @Autowired
    private IUserService userService;
    @Autowired
    private IRoleService roleService;
    @Autowired
    private AuthProvider authProvider;

    //http://localhost:8080/api/UserWebController/getDataTest
    @RequestMapping(path = "/getDataTest", method = RequestMethod.GET)
    public String getDataTest(){
            if(userService.getDataTest()){
                return "success";
            }
            else{
                throw new ResponseStatusException(
                        HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error"
                );
            }
    }

    //http://localhost:8080/api/UserWebController/validateConnection
    @RequestMapping(path = "/validateConnection", method = RequestMethod.POST)
    public Token validateConnection(@RequestBody Credential credential) throws Exception {
            return validateToken(credential);
    }

    private Token validateToken(Credential credential) {
        Token token = authProvider.validateConnection(credential);
        if (token == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "Unauthorized"
            );
        }
        return token;
    }

    //http://localhost:8080/api/UserWebController/getUserDto?email=jean@jean.com
    @Secured({AUTHORITY_PREFIX+USER, AUTHORITY_PREFIX+MANAGER, AUTHORITY_PREFIX+ADMIN})
    @RequestMapping(path = "/getUserDto", method = RequestMethod.GET)
    public UserDTO getUserDto(@RequestParam("email") String email) {
            validateThisUser(email);
            final UserDTO userDTO = userService.findUserDTOByEmail(email);
            if(userDTO==null){
                throw new ResponseStatusException(
                        HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error"
                );
            }
            userDTO.setPassword(null);
            return userDTO;
    }

    //http://localhost:8080/api/UserWebController/registerUser
    @RequestMapping(value = "/registerUser", method = RequestMethod.POST)
    public Token registerUser(@RequestBody UserDTO userDTOEntry) throws CustomConverterException{
            Token token = userService.generateUser(userDTOEntry);
        if(token==null){
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error"
            );
        }
        return token;
    }

    //http://localhost:8080/api/UserWebController/getRoleDtos?email=jean@jean.com
    @Secured({AUTHORITY_PREFIX+USER, AUTHORITY_PREFIX+MANAGER, AUTHORITY_PREFIX+ADMIN})
    @RequestMapping(path = "/getRoleDtos", method = RequestMethod.GET)
    public List<RoleDTO> getRoleDtos(@RequestParam("email") String email) {
            validateThisUser(email);
            return roleService.getRoleDtosList(email);
    }

    //http://localhost:8080/api/UserWebController/setUser
    @Secured({AUTHORITY_PREFIX + USER, AUTHORITY_PREFIX + MANAGER, AUTHORITY_PREFIX + ADMIN})
    @RequestMapping(path = "/setUser", method = RequestMethod.PUT)
    public UserDTO setUser(@RequestBody UserDTO userDTOEntry) {
            validateThisUser(userDTOEntry.getEmail());
        final UserDTO userDTO = userService.setUser(userDTOEntry);
        if(userDTO==null){
            logger.info("end-point setUser failed");
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error"
            );
        }
        logger.info("end-point setUser succeed");
        return userDTO;
    }

    //http://localhost:8080/api/UserWebController/getUsers
    @Secured({AUTHORITY_PREFIX + ADMIN})
    @RequestMapping(path = "/getUsers", method = RequestMethod.GET)
    public List<UserDTO> getUsers() throws CustomConverterException{
        List<UserDTO> userDTOS = userService.getUsers();
        if(userDTOS.isEmpty()||userDTOS==null){
            logger.info("end-point getUsers failed");
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error"
            );
        }
        userDTOS.forEach(u -> {
            u.setPassword(null);
        });
        logger.info("end-point getUsers succeed");
        return userDTOS;
    }

    //http://localhost:8080/api/UserWebController/getRoles
    @Secured({AUTHORITY_PREFIX+ADMIN})
    @RequestMapping(path = "/getRoles", method = RequestMethod.GET)
    public List<RoleDTO> getRoles() {
        return roleService.getAdminRoleDTOS();
    }

    //http://localhost:8080/api/UserWebController/putUserRoles
    @Secured({AUTHORITY_PREFIX+ADMIN})
    @RequestMapping(path = "/putUserRoles", method = RequestMethod.PUT)
    public List<UserDTO> putUserRoles(@RequestParam("email") String email, @RequestBody List<RoleDTO> roleDTOS) throws CustomConverterException {
        List<UserDTO> userDTOS = roleService.putUserRoles(email, roleDTOS);
        if(userDTOS.isEmpty()){
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error"
            );
        }
        return userDTOS;
    }

    //http://localhost:8080/api/UserWebController/getGenders
    @RequestMapping(path = "/getGenders", method = RequestMethod.GET)
    public List<Gender> getGenders() {
        return userService.getGenders();
    }

    //http://localhost:8080/api/UserWebController/changeUserSatus
    @Secured({AUTHORITY_PREFIX+ADMIN})
    @RequestMapping(path = "/changeUserSatus", method = RequestMethod.PUT)
    public List<UserDTO> changeUserSatus(@RequestBody UserDTO userDTO) throws CustomConverterException {
        return userService.changeUserSatus(userDTO);
    }

    //http://localhost:8080/api/UserWebController/refreshToken
    @RequestMapping(path = "/refreshToken", method = RequestMethod.POST)
    public Token refreshToken(@RequestBody Token token)  {
        token = authProvider.validateRefreshToken(token);
        if(token == null){
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error"
            );
        }
        return token;
    }
}
