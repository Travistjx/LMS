package com.app.lms.web;

import com.app.lms.entity.Gender;
import com.app.lms.entity.Role;

import java.time.LocalDate;
import java.util.Collection;

public class MemberDto {

    private Long member_id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;

    private Collection<RoleDto> roles;

    private LocalDate birthday;

    private Gender gender;

    private String addressOne;

    private String addressTwo;

    private String postalCode;

    private String libraryCard;

    public MemberDto(){

    }

    public MemberDto(Long member_id, String firstName, String lastName, String email, String password, Collection<RoleDto> roles, LocalDate birthday, Gender gender, String addressOne, String addressTwo, String postalCode, String libraryCard) {
        this.member_id = member_id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.roles = roles;
        this.birthday = birthday;
        this.gender = gender;
        this.addressOne = addressOne;
        this.addressTwo = addressTwo;
        this.postalCode = postalCode;
        this.libraryCard = libraryCard;
    }

    public Collection<RoleDto> getRoles() {
        return roles;
    }

    public void setRoles(Collection<RoleDto> roles) {
        this.roles = roles;
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

    public Long getMember_id() {
        return member_id;
    }

    public void setMember_id(Long member_id) {
        this.member_id = member_id;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public String getAddressOne() {
        return addressOne;
    }

    public void setAddressOne(String addressOne) {
        this.addressOne = addressOne;
    }

    public String getAddressTwo() {
        return addressTwo;
    }

    public void setAddressTwo(String addressTwo) {
        this.addressTwo = addressTwo;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getLibraryCard() {
        return libraryCard;
    }

    public void setLibraryCard(String libraryCard) {
        this.libraryCard = libraryCard;
    }
}
