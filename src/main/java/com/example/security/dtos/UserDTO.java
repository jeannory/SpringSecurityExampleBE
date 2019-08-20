package com.example.security.dtos;

import com.example.security.entities.User;
import com.example.security.enums.Gender;
import com.example.security.enums.Status;

public class UserDTO extends SuperDTO{

    private String email;
    private String password;
    private Gender gender;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String adress;
    private String zip;
    private String city;
    private String deliveryInformation;
    private Long SpaceId;
    private String flattenRoles;
    private Status status;

    public UserDTO() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    //Don't return the password
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAdress() {
        return adress;
    }

    public void setAdress(String adress) {
        this.adress = adress;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDeliveryInformation() {
        return deliveryInformation;
    }

    public void setDeliveryInformation(String deliveryInformation) {
        this.deliveryInformation = deliveryInformation;
    }

    public Long getSpaceId() {
        return SpaceId;
    }

    public void setSpaceId(Long spaceId) {
        SpaceId = spaceId;
    }

    public String getFlattenRoles() {
        return flattenRoles;
    }

    public void setFlattenRoles(String flattenRoles) {
        this.flattenRoles = flattenRoles;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public Class getEntityClass() {
        return User.class;
    }
}
