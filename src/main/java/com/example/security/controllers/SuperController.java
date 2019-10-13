package com.example.security.controllers;

import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.server.ResponseStatusException;

public class SuperController{

    private final static Logger logger = Logger.getLogger(SuperController.class);

    private UserDetails getSecurityContextHolder() {
        logger.info("Method getSecurityContextHolder");
        final UsernamePasswordAuthenticationToken authentication = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        return (UserDetails) authentication.getPrincipal();
    }

    public String getEmailUser(){
        logger.info("Method getEmailUser");
        return getSecurityContextHolder().getUsername();
    }

    /**
     * for each @Secure ROLE_USER && ROLE_MANAGER only themself can access
     * for all @Secure ROLE_ADMIN can access
     */
    public void validateThisUser(String emailEntry) {
        logger.info("Method validateThisUser");
        try {
            final UserDetails userDetails = getSecurityContextHolder();
            boolean authorization = false;
            if(userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))){
                authorization = true;
            }else {
                if (userDetails.getUsername().equals(emailEntry)) {
                    authorization = true;
                }
            }
                if(authorization==false){
                    logger.error("Forbidden");
                    throw new ResponseStatusException(
                            HttpStatus.FORBIDDEN, "Forbidden"
                    );
                }
        } catch (NullPointerException ex) {
            logger.error("Forbidden");
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "Forbidden"
            );
        }
    }

}
