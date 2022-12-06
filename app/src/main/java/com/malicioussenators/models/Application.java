package com.malicioussenators.models;

public class Application {
    boolean empty;
    String firstName;
    String lastName;
    String studentNumber;
    String zipCode;
    String DoB;
    String phoneNum;
    String eMail;

    public Application() {
        empty = true;
        firstName = "";
        lastName = "";
        studentNumber = "";
        zipCode = "";
        DoB = "";
        phoneNum = "";
        eMail = "";
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void seteMail(String eMail) {
        this.eMail = eMail;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public void setDoB(String doB) {
        this.DoB = doB;
    }

    public void setStudentNumber(String studentNumber) {
        this.studentNumber = studentNumber;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public boolean isEmpty() {
        return empty;
    }

    public void setEmpty(boolean empty) {
        this.empty = empty;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
}
