package com.example.security.controllers;

import org.springframework.http.HttpStatus;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.server.ResponseStatusException;


public class SuperController{

    private static UserDetails userDetails;

    private void getSecurityContextHolder() {
        UsernamePasswordAuthenticationToken authentication = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        userDetails = (UserDetails) authentication.getPrincipal();
    }

    public String getUserEmail() {
        getSecurityContextHolder();
        try {
            return userDetails.getUsername();
        } catch (NullPointerException ex) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "Forbidden"
            );
        }
    }

    //for each @Secure ROLE_USER && ROLE_MANAGER only themself can access || all ROLE_ADMIN
    public boolean validateThisUser(String emailEntry) {
        try {
            getSecurityContextHolder();
            boolean authorization = false;
            if(userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))){
                authorization = true;
            }else {
                if (userDetails.getUsername().equals(emailEntry)) {
                    authorization = true;
                }
            }
                if(authorization==false){
                    throw new ResponseStatusException(
                            HttpStatus.FORBIDDEN, "Forbidden"
                    );
                }
            return authorization;
        } catch (NullPointerException ex) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "Forbidden"
            );
        }
    }

}
