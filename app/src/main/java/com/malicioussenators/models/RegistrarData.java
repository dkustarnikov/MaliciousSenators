package com.malicioussenators.models;

public class RegistrarData {
    private String firstName;
    private String lastName;
    private Double cumulativeGPA;
    private Double currentSemesterGPA;
    private String academicStatus;
    private String gender;
    private String studentNumber;

    public RegistrarData() {
    }

    public RegistrarData(Double cumulativeGPA) {
        this.cumulativeGPA = cumulativeGPA;
    }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public Double getCumulativeGPA() {
        return cumulativeGPA;
    }
    public void setCumulativeGPA(Double cumulativeGPA) {
        this.cumulativeGPA = cumulativeGPA;
    }

    public Double getCurrentSemesterGPA() {
        return currentSemesterGPA;
    }
    public void setCurrentSemesterGPA(Double currentSemesterGPA) { this.currentSemesterGPA = currentSemesterGPA; }

    public String getAcademicStatus() { return academicStatus; }
    public void setAcademicStatus(String academicStatus) { this.academicStatus = academicStatus; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getStudentNumber() { return studentNumber; }
    public void setStudentNumber(String studentNumber) { this.studentNumber = studentNumber; }
}
