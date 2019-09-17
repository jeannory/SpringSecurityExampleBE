package com.example.security.services;

import com.example.security.dtos.RoleDTO;
import com.example.security.dtos.UserDTO;
import com.example.security.entities.Role;
import com.example.security.exceptions.CustomConverterException;

import java.util.List;
import java.util.Set;

public interface IRoleService {
    List<Role> findByUsersEmail(String userEmail);
    Role getUserRole();
    Role getManagerRole();
    Role getAdminRole();
    Set<Role> getUserRoleSet();
    Set<Role> getManagerRoleSet();
    Set<Role> getAdminRoleSet();
    List<RoleDTO> getRoleDtosList(String userEmail);
    List<RoleDTO> getAdminRoleDTOSet();
    List<UserDTO> putUserRoles(String email, List<RoleDTO> roleDTOS) throws CustomConverterException;
}
