package com.malicioussenators.models;

public class AccountingData {
    private String studentNumber;
    private int tuitionPaid;

    public AccountingData() {
    }

    public String getStudentNumber() { return studentNumber; }
    public void setStudentNumber(String studentNumber) { this.studentNumber = studentNumber; }

    public int getTuitionPaid() { return tuitionPaid; }
    public void setTuitionPaid(int tuitionPaid) { this.tuitionPaid = tuitionPaid; }
}
