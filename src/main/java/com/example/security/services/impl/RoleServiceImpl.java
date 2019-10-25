package com.example.security.services.impl;

import com.example.security.converter.SuperModelMapper;
import com.example.security.dtos.RoleDTO;
import com.example.security.dtos.UserDTO;
import com.example.security.entities.Role;
import com.example.security.entities.User;
import com.example.security.exceptions.CustomTransactionalException;
import com.example.security.repositories.RoleRepository;
import com.example.security.repositories.UserRepository;
import com.example.security.services.IRoleService;
import com.example.security.services.IUserService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.annotation.ApplicationScope;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.example.security.contants.Constants.*;

@ApplicationScope
@Service
public class RoleServiceImpl implements IRoleService {

    private final static Logger logger = Logger.getLogger(RoleServiceImpl.class);
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private SuperModelMapper superModelMapper;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private IUserService userService;

    public RoleServiceImpl() {
    }

    @Override
    public List<Role> findByUsersEmail(String userEmail) {
        logger.info("Method findByUsersEmail");
        List<Role> role= roleRepository.findByUsersEmail(userEmail);
        if(role==null){
            return Collections.emptyList();
        }
        return role;
    }

    @Override
    public Role getUserRole() {
        logger.info("Method getUserRole");
        try {
            return roleRepository.findByName(USER);
        }catch(NullPointerException ex){
            logger.error(ex.getMessage());
            return null;
        }
    }

    @Override
    public Role getManagerRole() {
        logger.info("Method getManagerRole");
        try{
        return roleRepository.findByName(MANAGER);
        }catch(NullPointerException ex){
            logger.error(ex.getMessage());
            return null;
        }
    }

    @Override
    public Role getAdminRole() {
        logger.info("Method getAdminRole");
        try{
        return roleRepository.findByName(ADMIN);
        }catch(NullPointerException ex){
            logger.error(ex.getMessage());
            return null;
        }
    }

    @Override
    public Set<Role> getUserRoleSet() {
        logger.info("Method getUserRoleSet");
        return Stream.of(getUserRole()).collect(Collectors.toCollection(HashSet::new)).stream().filter(Objects::nonNull).collect(Collectors.toSet());
    }

    @Override
    public Set<Role> getManagerRoleSet() {
        logger.info("Method getManagerRoleSet");
        return Stream.of(getUserRole(), getManagerRole()).collect(Collectors.toCollection(HashSet::new)).stream().filter(Objects::nonNull).collect(Collectors.toSet());
    }

    @Override
    public Set<Role> getAdminRoleSet() {
        logger.info("Method getAdminRoleSet");
        return Stream.of(getUserRole(), getManagerRole(), getAdminRole()).collect(Collectors.toCollection(HashSet::new)).stream().filter(Objects::nonNull).collect(Collectors.toSet());
    }

    @Override
    public List<RoleDTO> getRoleDtosList(String userEmail){
        logger.info("Method getRoleDtosList");
        return superModelMapper.convertToDTOs(findByUsersEmail(userEmail));
    }

    @Override
    public List<RoleDTO> getAdminRoleDTOS() {
        logger.info("Method getAdminRoleDTOS");
        return superModelMapper.convertToDTOs(Stream.of(getUserRole(), getManagerRole(), getAdminRole()).collect(Collectors.toList()));
    }

    @Override
    //catch CustomTransactionalException in this method
    public List<UserDTO> putUserRoles(final String email, final List<RoleDTO> roleDTOS) {
        logger.info("Method putUserRoles");
            try {
                testPutUserRolesTransaction(email, roleDTOS);
                return userService.getUsers();
            }catch(CustomTransactionalException ex){
                logger.error(ex.getMessage());
                return Collections.emptyList();
            }
        }

        // Do not catch CustomTransactionalException in this method
        @Transactional(propagation = Propagation.REQUIRES_NEW,
                rollbackFor = CustomTransactionalException.class)
        private void testPutUserRolesTransaction(final String email, final List<RoleDTO> roleDTOS) throws CustomTransactionalException{
            validatePutUserRolesTransaction(email, roleDTOS);
        }

        // MANDATORY: Transaction must be created before.
        @Transactional(propagation = Propagation.MANDATORY)
        private void validatePutUserRolesTransaction(final String email, final List<RoleDTO> roleDTOS){
            try{
                final User user = userRepository.findByEmail(email);
                List<Role> roles = superModelMapper.convertToEntities(roleDTOS);
                user.setRoles(new HashSet<>(roles));
                userRepository.save(user);
            } catch (Exception ex) {
                throw new CustomTransactionalException("save user failed");
            }
    }

}
