package com.malicioussenators.models;

public class AwardedData {
    private String studentNumber;
    private String firstName;
    private String lastName;
    private String awardCriteria;
    private int awardedAmount;

    public AwardedData() {
    }

    public String getStudentNumber() { return studentNumber; }
    public void setStudentNumber(String studentNumber) { this.studentNumber = studentNumber; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getAwardCriteria() { return awardCriteria; }
    public void setAwardCriteria(String awardCriteria) { this.awardCriteria = awardCriteria; }

    public int getAwardedAmount() { return awardedAmount; }
    public void setAwardedAmount(int awardedAmount) { this.awardedAmount = awardedAmount; }
}

