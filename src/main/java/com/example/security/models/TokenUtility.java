package com.example.security.models;

import java.util.List;

public class TokenUtility {

    private int kid;
    private String email;
    private boolean validateToken;
    private List<String> roles;

    public TokenUtility() {
    }

    public int getKid() {
        return kid;
    }

    public void setKid(int kid) {
        this.kid = kid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isValidateToken() {
        return validateToken;
    }

    public void setValidateToken(boolean validateToken) {
        this.validateToken = validateToken;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

}
