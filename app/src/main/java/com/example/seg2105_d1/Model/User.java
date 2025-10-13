package com.example.seg2105_d1.Model;

import java.util.ArrayList;
import java.util.regex.Pattern;

public abstract class User {
    private String firstName;
    private String lastName;
    private String emailAddressUsername;
    private String accountPassword;
    private String phoneNumber;

    /**
     * Constructor for the user
     */
    public User() {
        this.firstName = null;
        this.lastName = null;
        this.emailAddressUsername = null;
        this.accountPassword = null;
        this.phoneNumber = null;
    }
    public User(String firstName, String lastName, String emailAddressUsername, String accountPassword, String phoneNumber){
        this.setFirstName(firstName);
        this.setLastName(lastName);
        this.setEmailAddressUsername(emailAddressUsername);
        this.setAccountPassword(accountPassword);
        this.setPhoneNumber(phoneNumber);
    }

    public String getFirstName() {
        return this.firstName;
    }

    public void setFirstName(String firstName) {
        if(firstName.isEmpty()){
            throw new IllegalArgumentException("empty firstname");
        }else{
            this.firstName = firstName;
        }

    }

    public String getLastName() {
        return this.lastName;
    }

    public void setLastName(String lastName) {
        if(lastName.isEmpty()){
            throw new IllegalArgumentException("empty lastname");
        }else{
            this.lastName = lastName;
        }
    }

    public String getEmailAddressUsername() {return this.emailAddressUsername;}

    public void setEmailAddressUsername(String emailAddressUsername) {

        if(emailAddressUsername.isEmpty()){
            throw new IllegalArgumentException("empty email");
        }else{
            //regex setup to verify valid email address
            String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@" + "(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
            Pattern emailPattern = Pattern.compile(emailRegex);

            if(emailPattern.matcher(emailAddressUsername).matches()) {
                this.emailAddressUsername = emailAddressUsername;
            } else {
                throw new IllegalArgumentException("invalid email");
            }
        }
    }

    public String getAccountPassword() {
        return this.accountPassword;
    }

    public void setAccountPassword(String accountPassword) {
        if(accountPassword.isEmpty()){
            throw new IllegalArgumentException("empty password");
        }else{
            this.accountPassword = accountPassword;
        }
    }

    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {

        if(phoneNumber.isEmpty()){
            throw new IllegalArgumentException("empty phone number");
        }else{
            if(phoneNumber.length()==10){
                for(char c : phoneNumber.toCharArray()){
                    if(!Character.isDigit(c)){
                        throw new IllegalArgumentException("invalid phone number");
                    }
                }
                this.phoneNumber = phoneNumber;
            }else{
                throw new IllegalArgumentException("invalid phone number");
            }
        }
    }
}

