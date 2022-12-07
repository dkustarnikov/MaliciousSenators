package com.malicioussenators.models;

public class Application {
    String firstName;
    String lastName;
    String studentNumber;
    String zipCode;
    String DoB;
    String phoneNum;
    String eMail;
    String gender;
    String academicStatus;
    double cumulativeGPA;
    int recentCreditHours;

    public Application() {
        firstName = "";
        lastName = "";
        studentNumber = "";
        zipCode = "";
        DoB = "";
        phoneNum = "";
        eMail = "";
        gender = "";
        academicStatus = "";
        cumulativeGPA = 0.0;
        recentCreditHours = 0;
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

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setAcademicStatus(String academicStatus) {
        this.academicStatus = academicStatus;
    }

    public void setCumulativeGPA(double cumulativeGPA) {
        this.cumulativeGPA = cumulativeGPA;
    }

    public void setRecentCreditHours(int recentCreditHours) {
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

    public double getCumulativeGPA() {
        return cumulativeGPA;
    }

    public int getRecentCreditHours() {
        return recentCreditHours;
    }
}
