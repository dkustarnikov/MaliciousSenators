package com.malicioussenators.models;

public class RegistrarData {
    private String studentName;
    private Double cumulativeGPA;

    public RegistrarData() {
    }

    public RegistrarData(String studentName, Double cumulativeGPA) {
        this.studentName = studentName;
        this.cumulativeGPA = cumulativeGPA;
    }

    public Double getCumulativeGPA() {
        return cumulativeGPA;
    }

    public void setCumulativeGPA(Double cumulativeGPA) {
        this.cumulativeGPA = cumulativeGPA;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }
}
