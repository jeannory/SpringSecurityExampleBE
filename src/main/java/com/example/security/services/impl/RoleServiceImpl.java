package com.example.security.services.impl;

import com.example.security.converter.SuperModelMapper;
import com.example.security.dtos.RoleDTO;
import com.example.security.dtos.UserDTO;
import com.example.security.entities.Role;
import com.example.security.entities.User;
import com.example.security.exceptions.CustomConverterException;
import com.example.security.repositories.RoleRepository;
import com.example.security.repositories.UserRepository;
import com.example.security.services.IRoleService;
import com.example.security.services.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.annotation.ApplicationScope;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.example.security.contants.Constants.*;

@ApplicationScope
@Service
public class RoleServiceImpl implements IRoleService {

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
        List<Role> role= roleRepository.findByUsersEmail(userEmail);
        if(role==null){
            return Collections.emptyList();
        }
        return role;
    }

    @Override
    public Role getUserRole() {
        try {
            return roleRepository.findByName(USER);
        }catch(NullPointerException ex){
            ex.printStackTrace();
            return null;
        }
    }

    @Override
    public Role getManagerRole() {
        try{
        return roleRepository.findByName(MANAGER);
        }catch(NullPointerException ex){
            ex.printStackTrace();
            return null;
        }
    }

    @Override
    public Role getAdminRole() {
        try{
        return roleRepository.findByName(ADMIN);
        }catch(NullPointerException ex){
            ex.printStackTrace();
            return null;
        }
    }

    @Override
    public Set<Role> getUserRoleSet() {
        return Stream.of(getUserRole()).collect(Collectors.toCollection(HashSet::new)).stream().filter(Objects::nonNull).collect(Collectors.toSet());
    }

    @Override
    public Set<Role> getManagerRoleSet() {
        return Stream.of(getUserRole(), getManagerRole()).collect(Collectors.toCollection(HashSet::new)).stream().filter(Objects::nonNull).collect(Collectors.toSet());
    }

    @Override
    public Set<Role> getAdminRoleSet() {
        return Stream.of(getUserRole(), getManagerRole(), getAdminRole()).collect(Collectors.toCollection(HashSet::new)).stream().filter(Objects::nonNull).collect(Collectors.toSet());
    }

    @Override
    public List<RoleDTO> getRoleDtosList(String userEmail){
        return superModelMapper.convertToDTOs(findByUsersEmail(userEmail));
    }

    @Override
    public List<RoleDTO> getAdminRoleDTOS() {
        return superModelMapper.convertToDTOs(Stream.of(getUserRole(), getManagerRole(), getAdminRole()).collect(Collectors.toList()));
    }

    @Transactional
    @Override
    public List<UserDTO> putUserRoles(String email, List<RoleDTO> roleDTOS) throws CustomConverterException{
        User user = userRepository.findByEmail(email);
        List<Role> roles = superModelMapper.convertToEntities(roleDTOS);
        user.setRoles(new HashSet<>(roles));
        user = userRepository.save(user);
        if(user.getRoles().size()==roles.size()){
            return userService.getUsers();
        }
        return Collections.emptyList();
    }
}
