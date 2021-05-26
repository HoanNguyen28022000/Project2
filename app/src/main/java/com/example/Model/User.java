package com.example.Model;

import com.example.chatroom.R;
import com.google.firebase.auth.FirebaseAuth;

public class User {


    String userID;
    String avtResource;
    String username;
    String phone;
    String email;
    String address;

    public  User() {
        super();
    }
    public User(String userName, String phone, String email, String address) {
        this.userID= FirebaseAuth.getInstance().getCurrentUser().getUid();
        this.username = userName;
        this.phone = phone;
        this.email = email;
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAvtResource() {
        return avtResource;
    }

    public void setAvtResource(String avtResource) {
        this.avtResource = avtResource;
    }

    public String getUserName() {
        return username;
    }

    public void setUserName(String userName) {
        this.username = userName;
    }
}

