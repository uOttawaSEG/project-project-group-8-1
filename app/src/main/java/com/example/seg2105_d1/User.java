package com.example.seg2105_d1;

import java.util.ArrayList;

public abstract class User {
    private String firstName, lastName, emailAddressUsername, accountPassword, phoneNumber;

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
    public abstract void register(User u);

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

    public String getEmailAddressUsername() {
        return this.emailAddressUsername;
    }

    public void setEmailAddressUsername(String emailAddressUsername) {
        this.emailAddressUsername = emailAddressUsername;
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
        this.phoneNumber = phoneNumber;
    }

    /**
     * Checks if there are other Users with the same email address in the database when the register()
     * method is called.
     *
     * @param emailAddressUsername
     * @return isADuplicate
     */
    protected boolean checkDuplicates(String emailAddressUsername) {
        for (User user:userList){
            if(emailAddressUsername.equals(user.getEmailAddressUsername())){
                return true;
            }
        }
        return false;
    } //dummy method

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

