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
import com.example.security.exceptions.CustomConverterException;
import com.example.security.models.Credential;
import com.example.security.models.Token;
import com.example.security.repositories.RoleRepository;
import com.example.security.repositories.SpaceRepository;
import com.example.security.repositories.UserRepository;
import com.example.security.services.IRoleService;
import com.example.security.services.IUserService;
import com.example.security.tools.ITools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static com.example.security.contants.Constants.*;

import static com.example.security.contants.Constants.AUTHORITY_PREFIX;

@Service(value = "userService")
public class UserServiceImpl implements UserDetailsService, IUserService, ITools {

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


    //toDo mak builder
    @Override
    public void getDataTest() {
        Role userRole = new Role();
        userRole.setName(USER);
        Role cookerRole = new Role();
        cookerRole.setName(MANAGER);
        Role adminRole = new Role();
        adminRole.setName(ADMIN);

        roleRepository.save(userRole);
        roleRepository.save(cookerRole);
        roleRepository.save(adminRole);

        Set<Role> userRoles = roleService.getUserRoleSet();
        Set<Role> managerRoles = roleService.getManagerRoleSet();
        Set<Role> adminRoles = roleService.getAdminRoleSet();

        User user1 = new User();
        user1.setEmail("jean@jean.com");
        user1.setGender(Gender.Monsieur);
        user1.setFirstName("Jean");
        user1.setLastName("Leroi");
        user1.setPassword(getStringSha3("0000"));
        user1.setRoles((Set<Role>)adminRoles);
        user1.setPhoneNumber("0606060606");
        user1.setAdress("3 rue du Roi");
        user1.setZip("95000");
        user1.setCity("Cergy");
        user1.setDeliveryInformation("2ème étage");
        User user2 = new User();
        user2.setEmail("jeanne@jeanne.com");
        user2.setPassword(getStringSha3("0000"));
        user2.setRoles((Set<Role>)managerRoles);
        User user3 = new User();
        user3.setEmail("john@john.com");
        user3.setPassword(getStringSha3("0000"));
        user3.setRoles((Set<Role>)userRoles);
        User user4 = new User();
        user4.setEmail("johny@johny.com");
        user4.setPassword(getStringSha3("0000"));
        user4.setRoles((Set<Role>)userRoles);
        user1.setStatus(Status.ACTIVE);
        user2.setStatus(Status.ACTIVE);
        user3.setStatus(Status.ACTIVE);
        user4.setStatus(Status.INACTIVE);
        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);
        userRepository.save(user4);

        Space space1 = new Space();
        space1.setName("Jean@jean.com space");
        space1.setUser(user1);
        spaceRepository.save(space1);

        Space space2 = new Space();
        space2.setName("jeanne@jeanne.com space");
        space2.setUser(user2);
        spaceRepository.save(space2);

        Space space3 = new Space();
        space3.setName("john@john.com space");
        space3.setUser(user3);
        spaceRepository.save(space3);

        Space space4 = new Space();
        space4.setName("johny@johny.com space");
        space4.setUser(user4);
        spaceRepository.save(space4);
    }

    //@Secure needs to redefine loadUserByUsername(String username)
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            User user = manageSelectMyUserByEmailException(username);
            Set<SimpleGrantedAuthority> simpleGrantedAuthorities = roleService.findByUsersEmail(user.getEmail()).stream().map(role-> {
                return new SimpleGrantedAuthority(AUTHORITY_PREFIX + role.getName());
            }).collect(Collectors.toCollection(HashSet::new));
            return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), simpleGrantedAuthorities);
        } catch (UsernameNotFoundException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    //only use with method loadUserByUsername
    private User manageSelectMyUserByEmailException(String username) {
        User user = userRepository.selectMyUserByEmail(username);
        if (
                user == null ||
                        user.getId() == null ||
                        user.getEmail() == null ||
                        user.getPassword() == null ||
                        user.getRoles() == null
        ) {
            throw new UsernameNotFoundException("Invalid user");
        }
        return user;
    }

    @Override
    public UserDTO findUserDTOByEmail(String email) {
        try {
            User user = userRepository.findByEmail(email);
            if(user==null){
                return null;
            }
            return (UserDTO) superModelMapper.convertToDTO(user).get();
        } catch (CustomConverterException ex) {
            ex.printStackTrace();
            return null;
        }catch(NoSuchElementException ex) {
            ex.printStackTrace();
            return null;
        }
    }


    @Transactional
    @Override
    public Token generateUser(UserDTO userDTOEntry) throws CustomConverterException {
        Set<Role> roles = roleService.getUserRoleSet();
//        if(
//                userDTOEntry==null ||
//                        userDTOEntry.getEmail()==null ||
//                        userDTOEntry.getPassword()==null ||
//                        userDTOEntry.getFirstName()==null ||
//                        userDTOEntry.getLastName()==null ||
//                        roles==null ||
//                        roles.isEmpty()
//        ){
//            return null;
//        }
        userDTOEntry.setPassword(getStringSha3(userDTOEntry.getPassword()));
        User user = (User) (superModelMapper.convertToEntity(userDTOEntry)).get();
        user.setRoles(roles);
        user.setStatus(Status.ACTIVE);
        userRepository.save(user);
        Space space = new Space();
        space.setName("Espace de " + user.getEmail());
        space.setUser(user);
        spaceRepository.save(space);
        //check if @transactionnal failed
//        if(user.getId()==null||space.getId()==null){
//            return null;
//        }else{
        Credential credential = new Credential();
        credential.setEmail(userDTOEntry.getEmail());
        credential.setPassword(userDTOEntry.getPassword());
        return authProvider.validateConnection(credential);
//        }
    }

    @Override
    @Transactional
    public UserDTO setUser(UserDTO userDTO) {
        User user = userRepository.findByEmail(userDTO.getEmail());
        user.setGender(userDTO.getGender());
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setPhoneNumber((userDTO.getPhoneNumber()));
        user.setAdress(userDTO.getAdress());
        user.setZip(userDTO.getZip());
        user.setCity(userDTO.getCity());
        user.setDeliveryInformation(userDTO.getDeliveryInformation());
        userRepository.save(user);
        try {
            UserDTO userDTOReturn = (UserDTO) superModelMapper.convertToDTO(user).get();
            return userDTOReturn;
        } catch (CustomConverterException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    @Override
    public List<UserDTO> getUsers() {
        List<UserDTO> userDTOS = (List<UserDTO>) superModelMapper.convertToDTOs(userRepository.findAll()).get();
        userDTOS.forEach(u -> {
            u.setFlattenRoles(buildFlattenRoles(roleRepository.findByUsersEmail(u.getEmail())));
        });
        return userDTOS;
    }

    @Override
    public List<Gender> getGenders() {
        return Arrays.asList(Gender.Monsieur, Gender.Madame);
    }

    private String buildFlattenRoles(List<Role> roles) {
        try {
            Optional<List<String>> list = Optional.of(roles.stream().map(Role::getName).collect(Collectors.toList()));
            String flattenRoles = list.get().stream().map(Object::toString).collect(Collectors.joining(", "));
            return flattenRoles;
        } catch (Exception ex) {
            //toDo
        }
        return null;
    }

    @Transactional
    @Override
    public List<UserDTO> changeUserSatus(UserDTO userDTO) {
        Optional<User> user = userRepository.findById(userDTO.getId());
        if (userDTO.getStatus() == Status.ACTIVE) {
            user.get().setStatus(Status.INACTIVE);
        } else {
            user.get().setStatus(Status.ACTIVE);
        }
        userRepository.save(user.get());
        return (List<UserDTO>) superModelMapper.convertToDTOs(userRepository.findAll()).get();
    }
}