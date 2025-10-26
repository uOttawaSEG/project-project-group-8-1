package com.example.seg2105_d1.Model;

public class Administrator extends User{
    @Override
    public String getUserType() {
        return "ADMIN";
    }
}
