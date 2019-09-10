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
    public Role findByName(String name) {
        return roleRepository.findByName(name);
    }

    @Override
    public List<Role> findByUsersEmail(String userEmail) {
        return roleRepository.findByUsersEmail(userEmail);

    }

    public Role getUserRole() {
        return roleRepository.findByName(USER);
    }

    public Role getManagerRole() {
        return roleRepository.findByName(MANAGER);
    }

    public Role getAdminRole() {
        return roleRepository.findByName(ADMIN);
    }

    @Override
    public Set<Role> getUserRoleSet() {
        return Stream.of(getUserRole()).collect(Collectors.toCollection(HashSet::new));
    }

    @Override
    public Set<Role> getManagerRoleSet() {
        return Stream.of(getUserRole(), getManagerRole()).collect(Collectors.toCollection(HashSet::new));
    }

    @Override
    public Set<Role> getAdminRoleSet() {
        return Stream.of(getUserRole(), getManagerRole(), getAdminRole()).collect(Collectors.toCollection(HashSet::new));
    }

    @Override
    public Set<RoleDTO> getRoleDtosSet(String userEmail){
        return ((List<RoleDTO>) superModelMapper.convertToDTOs(findByUsersEmail(userEmail)).get()).stream().collect(Collectors.toCollection(HashSet::new));
    }

    @Override
    public List<RoleDTO> getRoleDtosList(String userEmail){
        return (List<RoleDTO>) superModelMapper.convertToDTOs(findByUsersEmail(userEmail)).get();
    }

    @Override
    public List<RoleDTO> getAdminRoleDTOSet() {
        return (List<RoleDTO>) superModelMapper.convertToDTOs(Stream.of(getUserRole(), getManagerRole(), getAdminRole()).collect(Collectors.toList())).get();
    }

    @Transactional
    @Override
    public List<UserDTO> putUserRoles(String email, List<RoleDTO> roleDTOS) throws CustomConverterException{
        User user = userRepository.findByEmail(email);
        user.setRoles(new HashSet<>((List<Role>) superModelMapper.convertToEntities(roleDTOS).get()));
        userRepository.save(user);
        return userService.getUsers();
    }
}
