package com.example.seg2105_d1.Mapper;

public class UserPoJo {

    private String firstName;
    private String lastName;
    private String emailAddressUsername;
    private String accountPassword;
    private String phoneNumber;
    private String registrationStatus;
    private String role;

    public UserPoJo(){

    }

    public String getAccountPassword() {
        return accountPassword;
    }

    public String getEmailAddressUsername() {
        return emailAddressUsername;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getRegistrationStatus() {
        return registrationStatus;
    }

    public String getRole() {
        return role;
    }

    public void setAccountPassword(String accountPassword) {
        this.accountPassword = accountPassword;
    }

    public void setEmailAddressUsername(String emailAddressUsername) {
        this.emailAddressUsername = emailAddressUsername;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setRegistrationStatus(String registrationStatus) {
        this.registrationStatus = registrationStatus;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
