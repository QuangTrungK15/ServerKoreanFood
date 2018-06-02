package com.example.dell.serverkoreanfood.Model;

/**
 * Created by dell on 18/01/2018.
 */

public class User {



    private String Name;
    private String Password;
    private String Phone;
    private String isStaff;

    public User() {
    }

    

    public User(String name, String password) {
        this.Name = name;
        this.Password = password;
        this.isStaff = "false";
    }


    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }


    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getIsStaff() {
        return isStaff;
    }

    public void setIsStaff(String isStaff) {
        this.isStaff = isStaff;
    }
}
