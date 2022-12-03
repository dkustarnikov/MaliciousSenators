package com.malicioussenators.models;

public class Application {
    String firstName;
    String studentNumber;

    public Application() {
    }

    public Application(String firstName, String studentNumber) {
        this.firstName = firstName;
        this.studentNumber = studentNumber;
    }

    public String getStudentNumber() {
        return studentNumber;
    }

    public void setStudentNumber(String studentNumber) {
        this.studentNumber = studentNumber;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
}
