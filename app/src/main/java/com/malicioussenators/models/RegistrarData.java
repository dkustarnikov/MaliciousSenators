package com.malicioussenators.models;

public class RegistrarData {
    private String studentName;
    private Double cumulativeGPA;
    private Double currentSemesterGPA;
    private String academicStatus;
    private String gender;

    public RegistrarData() {
    }

    public RegistrarData(String studentName, Double cumulativeGPA) {
        this.studentName = studentName;
        this.cumulativeGPA = cumulativeGPA;
        this.currentSemesterGPA = currentSemesterGPA;
    }

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

    public String getStudentName() {
        return studentName;
    }
    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getAcademicStatus() { return academicStatus; }
    public void setAcademicStatus(String academicStatus) { this.academicStatus = academicStatus; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
}
