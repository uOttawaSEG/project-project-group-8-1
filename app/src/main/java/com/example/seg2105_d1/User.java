package com.example.seg2105_d1;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public abstract class User {
    private String firstName, lastName, emailAddressUsername, accountPassword, phoneNumber;
    private DatabaseReference mDatabase;
    public static ArrayList<User> userList = new ArrayList<User>();

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

    /**
     * Takes in information about the person registering, then creates an account of type User for them
     *
     *
     * @param u
     */
    public abstract void register(User u, RegisterCallback r);

    public String getFirstName() {
        return this.firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmailAddressUsername() {return this.emailAddressUsername;}

    public void setEmailAddressUsername(String emailAddressUsername) {
        //regex setup to verify valid email address
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@" + "(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern emailPattern = Pattern.compile(emailRegex);

        if(emailPattern.matcher(emailAddressUsername).matches()) {
            this.emailAddressUsername = emailAddressUsername;
        } else {
            throw new IllegalArgumentException("Input is not an email address.");
        }
    }

    public String getAccountPassword() {
        return this.accountPassword;
    }

    public void setAccountPassword(String accountPassword) {
        this.accountPassword = accountPassword;
    }

    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {

        if(phoneNumber!=null){
            if(phoneNumber.length()==10){
                for(char c : phoneNumber.toCharArray()){
                    if(!Character.isDigit(c)){
                        throw new IllegalArgumentException("Phone number contains non-digit characters.");
                    }
                }
                this.phoneNumber = phoneNumber;
                return;
            }
        } else {
            throw new IllegalArgumentException("Please enter a phone number.");
        }
        throw new IllegalArgumentException("Input is not a phone number.");

    }

    /**
     * Checks if the login information is correct, then returns the user that which corresponds to the
     * login information. If either the email address or the password is not correct, the program will throw an exception
     * to be handled by the main.
     *
     * @param emailAddressUsername
     * @param accountPassword
     * @return user
     * @throws IncorrectLoginException
     */
    public static User login(String emailAddressUsername, String accountPassword) throws IncorrectLoginException {

        for (User user : userList) {
            if (emailAddressUsername.equals(user.getEmailAddressUsername())) {
                if (accountPassword.equals(user.getAccountPassword())) {
                    return user;
                }
            }
        }
        throw new IncorrectLoginException("Username or Password Incorrect.");

    }
}

