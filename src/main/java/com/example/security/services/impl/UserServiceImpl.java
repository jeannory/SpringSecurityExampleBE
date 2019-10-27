package com.example.security.services.impl;

import com.example.security.config.AuthProvider;
import com.example.security.config.TokenUtilityProvider;
import com.example.security.converter.SuperModelMapper;
import com.example.security.dtos.UserDTO;
import com.example.security.entities.Role;
import com.example.security.entities.Space;
import com.example.security.entities.User;
import com.example.security.enums.Gender;
import com.example.security.enums.Status;
import com.example.security.exceptions.CustomTransactionalException;
import com.example.security.models.Credential;
import com.example.security.models.Token;
import com.example.security.repositories.RoleRepository;
import com.example.security.repositories.SpaceRepository;
import com.example.security.repositories.UserRepository;
import com.example.security.services.IRoleService;
import com.example.security.services.IUserService;
import com.example.security.tools.ITools;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import java.util.stream.Collectors;

import static com.example.security.contants.Constants.*;
import static com.example.security.contants.Constants.AUTHORITY_PREFIX;

@Service(value = "userService")
public class UserServiceImpl implements UserDetailsService, IUserService, ITools {

    private final static Logger logger = Logger.getLogger(UserServiceImpl.class);
    @Autowired
    private TokenUtilityProvider tokenUtilityProvider;
    @Autowired
    private AuthProvider authProvider;
    @Autowired
    private SuperModelMapper superModelMapper;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private SpaceRepository spaceRepository;
    @Autowired
    private IRoleService roleService;

    @Override
    //catch CustomTransactionalException in this method
    public boolean getDataTest(){
        logger.info("Method getDataTest");
        try {
            testGetDataTest();
        }catch (CustomTransactionalException ex){
            logger.error(ex.getMessage());
            return false;
        }
        return true;
    }

    // Do not catch CustomTransactionalException in this method
    @Transactional(propagation = Propagation.REQUIRES_NEW,
            rollbackFor = CustomTransactionalException.class)
    private void testGetDataTest() throws CustomTransactionalException{
        validateGetDataTest();
    }

    // MANDATORY: Transaction must be created before.
    @Transactional(propagation = Propagation.MANDATORY )
    private void validateGetDataTest(){
        try {
            /**
            no persistence of userTest when rollback
             */
            final User userTest = new User();
            userTest.setEmail("userTest@test.com");
            userTest.setPassword(getStringSha3("userTest"));
            userRepository.save(userTest);

            final Role userRole = new Role();
            userRole.setName(USER);
            final Role managerRole = new Role();
            managerRole.setName(MANAGER);
            final Role adminRole = new Role();
            adminRole.setName(ADMIN);

            roleRepository.save(userRole);
            roleRepository.save(managerRole);
            roleRepository.save(adminRole);

            final Set<Role> userRoles = roleService.getUserRoleSet();
            final Set<Role> managerRoles = roleService.getManagerRoleSet();
            final Set<Role> adminRoles = roleService.getAdminRoleSet();

            final User user1 = new User();
            user1.setEmail("jean@jean.com");
            user1.setGender(Gender.Monsieur);
            user1.setFirstName("Jean");
            user1.setLastName("Leroi");
            user1.setPassword(getStringSha3("0000"));
            user1.setRoles(adminRoles);
            user1.setPhoneNumber("0606060606");
            user1.setAddress("3 rue du Roi");
            user1.setZip("95000");
            user1.setCity("Cergy");
            user1.setDeliveryInformation("2ème étage");
            final User user2 = new User();
            user2.setEmail("jeanne@jeanne.com");
            user2.setPassword(getStringSha3("0000"));
            user2.setRoles(managerRoles);
            final User user3 = new User();
            user3.setEmail("john@john.com");
            user3.setPassword(getStringSha3("0000"));
            user3.setRoles(userRoles);
            final User user4 = new User();
            user4.setEmail("johny@johny.com");
            user4.setPassword(getStringSha3("0000"));
            user4.setRoles(userRoles);
            user1.setStatus(Status.ACTIVE);
            user2.setStatus(Status.ACTIVE);
            user3.setStatus(Status.ACTIVE);
            user4.setStatus(Status.INACTIVE);

            userRepository.save(user1);
            userRepository.save(user2);
            userRepository.save(user3);
            userRepository.save(user4);

            final Space space1 = new Space();
            space1.setName("Jean@jean.com space");
            space1.setUser(user1);
            spaceRepository.save(space1);

            final Space space2 = new Space();
            space2.setName("jeanne@jeanne.com space");
            space2.setUser(user2);
            spaceRepository.save(space2);

            final Space space3 = new Space();
            space3.setName("john@john.com space");
            space3.setUser(user3);
            spaceRepository.save(space3);

            final Space space4 = new Space();
            space4.setName("johny@johny.com space");
            space4.setUser(user4);
            spaceRepository.save(space4);
        }catch(Exception ex){
            throw new CustomTransactionalException("data tests failed");
        }
    }

    /**
     * with Spring security
     * @Secure needs to redefine loadUserByUsername(String username)
     * abstract method of UserDetailsService
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.info("Method loadUserByUsername");
        try {
            final User user = manageSelectMyUserByEmailException(username);
            Set<SimpleGrantedAuthority> simpleGrantedAuthorities = roleService.findByUsersEmail(user.getEmail()).stream().map(role -> {
                return new SimpleGrantedAuthority(AUTHORITY_PREFIX + role.getName());
            }).collect(Collectors.toCollection(HashSet::new));
            logger.info("Method loadUserByUsername succeed");
            return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), simpleGrantedAuthorities);
        } catch (UsernameNotFoundException ex) {
            logger.error(ex.getMessage());
            return null;
        }
    }

    private User manageSelectMyUserByEmailException(String username) {
        logger.info("Method manageSelectMyUserByEmailException");
        final User user = userRepository.selectMyUserByEmail(username);
        if (
                user == null ||
                        user.getId() == null ||
                        user.getEmail() == null ||
                        user.getPassword() == null
                        //toDo to handle
                        //Unable to evaluate the expression Method threw 'org.hibernate.LazyInitializationException' exception.
                        //user.getRoles() == null
        ) {
            throw new UsernameNotFoundException("Invalid user");
        }
        return user;
    }

    @Override
    public UserDTO findUserDTOByEmail(String email) {
        logger.info("Method findUserDTOByEmail");
        try {
            final User user = userRepository.findByEmail(email);
            if (user == null) {
                return null;
            }
            return (UserDTO) superModelMapper.convertToDTO(user).get();
        } catch (NoSuchElementException ex) {
            logger.error(ex.getMessage());
            return null;
        }
    }

    //catch CustomTransactionalException in this method
    @Override
    public Token generateUser(UserDTO userDTOEntry) {
        logger.info("Method generateUser");
        try {
            return testGenerateUser(userDTOEntry);
        } catch (CustomTransactionalException ex) {
            logger.error(ex.getMessage());
            return null;
        }
    }

    // Do not catch CustomTransactionalException in this method
    @Transactional(propagation = Propagation.REQUIRES_NEW,
    rollbackFor = CustomTransactionalException.class)
    private Token testGenerateUser(final UserDTO userDTOEntry) throws CustomTransactionalException{
        logger.info("Method testGenerateUser");
            return validateGenerateUser(userDTOEntry);
    }

    // MANDATORY: Transaction must be created before.
    @Transactional(propagation = Propagation.MANDATORY)
    private Token validateGenerateUser(UserDTO userDTO){
        logger.info("Method validateGenerateUser");
        try {
            Set<Role> roles = roleService.getUserRoleSet();
            if (
                    userDTO == null ||
                            userDTO.getEmail() == null ||
                            userDTO.getPassword() == null ||
                            userDTO.getFirstName() == null ||
                            userDTO.getLastName() == null ||
                            roles == null || roles.isEmpty()
            ) {
                throw new CustomTransactionalException();
            }
            final User user = (User) (superModelMapper.convertToEntity(userDTO)).get();
            user.setRoles(roles);
            user.setStatus(Status.ACTIVE);
            userRepository.save(user);
            final Space space = new Space();
            space.setName("Espace de " + user.getEmail());
            space.setUser(user);
            spaceRepository.save(space);
            final Credential credential = new Credential();
            credential.setEmail(userDTO.getEmail());
            credential.setPassword(userDTO.getPassword());
            return authProvider.validateConnection(credential);
        }catch(Exception ex){
            throw new CustomTransactionalException("persistence failed");
        }
    }

    @Override
//    @Transactional(rollbackFor = CustomTransactionalException.class)
    //toDo @Transactional
    public UserDTO modifyUser(UserDTO userDTO) {
        logger.info("Method setUser");
        final User user = userRepository.findByEmail(userDTO.getEmail());
        if (user == null) {
            return null;
        }
        try {
            user.setGender(userDTO.getGender());
            user.setFirstName(userDTO.getFirstName());
            user.setLastName(userDTO.getLastName());
            user.setPhoneNumber((userDTO.getPhoneNumber()));
            user.setAddress(userDTO.getAddress());
            user.setZip(userDTO.getZip());
            user.setCity(userDTO.getCity());
            user.setDeliveryInformation(userDTO.getDeliveryInformation());
            userRepository.save(user);
        } catch (CustomTransactionalException ex) {
            logger.error(ex.getMessage());
            return null;
        }
        try {
            final UserDTO userDTOReturn = (UserDTO) superModelMapper.convertToDTO(user).get();
            return userDTOReturn;
        }catch(NoSuchElementException ex){
            logger.error(ex.getMessage());
            return null;
        }
    }

    @Override
    public List<UserDTO> getUsers() {
        logger.info("Method getUsers");
        try {
            final List<User> users = userRepository.findAll();
            if (users.isEmpty()) {
                return Collections.emptyList();
            }
            return superModelMapper.convertToDTOs(users);
        }catch(NullPointerException ex){
            logger.error(ex.getMessage());
            return Collections.emptyList();
        }
    }

    @Override
    public List<Gender> getGenders() {
        logger.info("Method getGenders");
        return Arrays.asList(Gender.Monsieur, Gender.Madame);
    }

    @Override
//    @Transactional(rollbackFor = CustomTransactionalException.class)
    //toDo @Transactional
    public List<UserDTO> changeUserSatus(UserDTO userDTO) {
        logger.info("Method changeUserSatus");
        try {
            Optional<User> user = userRepository.findById(userDTO.getId());
            if (userDTO.getStatus() == Status.ACTIVE) {
                user.get().setStatus(Status.INACTIVE);
            } else {
                user.get().setStatus(Status.ACTIVE);
            }
            userRepository.save(user.get());
            return (List<UserDTO>) superModelMapper.convertToDTOs(userRepository.findAll());
        }catch(CustomTransactionalException ex){
            logger.error(ex.getMessage());
            return Collections.emptyList();
        }
    }
}
