package com.example.group_32.chatloca;

import android.media.Image;

import java.util.Date;

/**
 * Created by dath on 4/8/18.
 */

public class User {
    private String userName;
    private String firstName;
    private String lastName;
    private String gender;
    private String email;
    private String phone;
    private String address;
    private String dateofbirth;
    private String password;
    private String nameOfUser;

    public User(){}

    public User(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    public User(String userName, String firstName, String lastName, String gender, String email, String phone, String address, String dateofbirth) {
        this.userName = userName;
        this.firstName = firstName;
        this.lastName = lastName;
        this.nameOfUser = firstName + " " + lastName;
        this.gender = gender;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.dateofbirth = dateofbirth;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public boolean checkUser(User newUser){
        if (this.userName == newUser.userName&& this.password == newUser.password)
            return true;
        return false;
    }

    public String getDateofbirth() {
        return dateofbirth;
    }

    public void setDateofbirth(String dateofbirth) {
        this.dateofbirth = dateofbirth;
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

    public String getNameOfUser() {
        return nameOfUser;
    }

    public void setNameOfUser(String nameOfUser) {
        this.nameOfUser = nameOfUser;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    public boolean isEqual(User nUser){
        if (nUser.userName == this.userName && nUser.password == this.password)
            return true;
        return false;
    }

}
