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
        Set<Role> userRoleSet = new HashSet(Collections.singletonList(getUserRole()));
        return userRoleSet;
    }

    @Override
    public Set<Role> getManagerRoleSet() {
        Set<Role> managerRoleSet = new HashSet(Arrays.asList(getUserRole(), getManagerRole()));
        return managerRoleSet;
    }

    @Override
    public Set<Role> getAdminRoleSet() {
        Set<Role> adminRoleSet = new HashSet(Arrays.asList(getUserRole(), getManagerRole(), getAdminRole()));
        return adminRoleSet;
    }

    @Override
    public Set<RoleDTO> getRoleDtosSet(String userEmail){
        List<Role> roles = findByUsersEmail(userEmail);
        try {
            Set<RoleDTO> roleDTOS = new HashSet(Arrays.asList(superModelMapper.convertToDTOs(roles)));
            return roleDTOS;
        } catch (CustomConverterException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    @Override
    public List<RoleDTO> getRoleDtosList(String userEmail){
        List<Role> roles = findByUsersEmail(userEmail);
        try {
            List<RoleDTO> roleDTOS = (List<RoleDTO>) superModelMapper.convertToDTOs(roles).get();
            return roleDTOS;
        } catch (CustomConverterException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    @Override
    public List<RoleDTO> getAdminRoleDTOSet() {
        try {

            List<RoleDTO> adminRoleDTOSet = (List<RoleDTO>) (superModelMapper.convertToDTOs(new ArrayList(Arrays.asList(getUserRole(), getManagerRole(), getAdminRole()))).get());
            return adminRoleDTOSet;
        } catch (CustomConverterException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    @Transactional
    @Override
    public List<UserDTO> putUserRoles(String email, List<RoleDTO> roleDTOS) throws CustomConverterException {
        User user = userRepository.findByEmail(email);
        user.setRoles(new HashSet<Role>((List<Role>) superModelMapper.convertToEntities(roleDTOS).get()));
        userRepository.save(user);
        return userService.getUsers();
    }
}
