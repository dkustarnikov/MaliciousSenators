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
    String gender;
    String academicStatus;
    String cumulativeGPA;
    String recentCreditHours;

    public Application() {
        empty = true;
        firstName = "";
        lastName = "";
        studentNumber = "";
        zipCode = "";
        DoB = "";
        phoneNum = "";
        eMail = "";
        gender = "";
        academicStatus = "";
        cumulativeGPA = "";
        recentCreditHours = "";
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

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setAcademicStatus(String academicStatus) {
        this.academicStatus = academicStatus;
    }

    public void setCumulativeGPA(String cumulativeGPA) {
        this.cumulativeGPA = cumulativeGPA;
    }

    public void setRecentCreditHours(String recentCreditHours) {
        this.recentCreditHours = recentCreditHours;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getStudentNumber() {
        return studentNumber;
    }

    public String getZipCode() {
        return zipCode;
    }

    public String getDoB() {
        return DoB;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public String geteMail() {
        return eMail;
    }

    public String getGender() {
        return gender;
    }

    public String getAcademicStatus() {
        return academicStatus;
    }

    public String getCumulativeGPA() {
        return cumulativeGPA;
    }

    public String getRecentCreditHours() {
        return recentCreditHours;
    }
}
