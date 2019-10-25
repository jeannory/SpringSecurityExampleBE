package com.example.security.models;

import com.example.security.tools.ITools;

public class Credential implements ITools {

    private String email;
    private String password;

    public Credential() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSha3Password(){
        return getStringSha3(password);
    }

}
