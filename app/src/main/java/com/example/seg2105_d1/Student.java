package com.example.seg2105_d1;

public class Student extends User{
    private String programOfStudy;

    public Student(String programOfStudy, String firstName, String lastName, String emailAddressUsername, String accountPassword, String phoneNumber) {
        super(firstName, lastName, emailAddressUsername, accountPassword, phoneNumber);
        this.programOfStudy = programOfStudy;
    }

    public String getProgramOfStudy() { return this.programOfStudy; }

    public void setProgramOfStudy(String programOfStudy) { this.programOfStudy = programOfStudy; }

    @Override
    public void register(User u) {
        if(!checkDuplicates(u.getEmailAddressUsername())) {
            return;
        }

        userList.add(u);
        //show success message
    }
}
