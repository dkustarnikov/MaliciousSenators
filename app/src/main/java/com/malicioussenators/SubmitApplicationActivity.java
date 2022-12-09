//Program Name:Module 2 Malicious Senators
//Programmer Name: Kyle Name
//Description: Allows users to submit an application
//Date Created: 12/9/2022

package com.malicioussenators;

import static com.malicioussenators.CONSTANTS.APPLICANTS_DATA_STORE;
import static com.malicioussenators.CONSTANTS.REGISTRAR_DATA_STORE;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.malicioussenators.models.Application;

import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class SubmitApplicationActivity extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    Application studentInfo;
    DocumentReference studentDoc;
    static String Registrar_Data_Store = "RegistrarDataStore";
    static String Applicant_Data_Store = "ApplicantsDataStore";

    public void MainLogic1() {
        //call the data store and wait for a response
        db.collection(Registrar_Data_Store)
                .whereEqualTo("studentNumber", studentInfo.getStudentNumber()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        //When the response comes continue with main logic
                        List<DocumentSnapshot> documents = task.getResult().getDocuments();
                        if (documents.size() == 0){
                            errorTextView.setText("No Student Number Match - please reenter data");
                            return;
                        }
                        boolean infoNoMatch = false;
                        boolean contactNoMatch = false;
                        String infoError = "Data field(s) do not match Registrar Data Store:";
                        String contactError = "";
                        if(!studentInfo.getFirstName().equals(documents.get(0).get("firstName").toString())) {
                            infoError = infoError + " first name";
                            infoNoMatch = true;
                        }
                        if(!studentInfo.getLastName().equals(documents.get(0).get("lastName").toString())) {
                            infoError = infoError + " last name";
                            infoNoMatch = true;
                        }
                        if(!studentInfo.getDoB().equals(documents.get(0).get("DoB").toString())) {
                            infoError = infoError + " date of birth";
                            infoNoMatch = true;
                        }
                        if(!studentInfo.getZipCode().equals(documents.get(0).get("zipCode").toString())) {
                            infoError = infoError + " zip code";
                            infoNoMatch = true;
                        }
                        if(!studentInfo.geteMail().equals(documents.get(0).get("eMail").toString())) {
                            contactError = contactError + "e-mail";
                            contactNoMatch = true;
                        }
                        if(!studentInfo.getPhoneNum().equals(documents.get(0).get("phoneNumber").toString())) {
                            contactError = contactError + " phone number";
                            contactNoMatch = true;
                        }

                        if(infoNoMatch) {
                            infoError = infoError + " - Please reenter data";
                            errorTextView.setText(infoError);
                            return;
                        }

                        studentInfo.setGender(documents.get(0).get("gender").toString());
                        studentInfo.setAcademicStatus(documents.get(0).get("academicStatus").toString());
                        Number GPA = (Number) documents.get(0).getData().get("cumulativeGPA");
                        Number latestGPA = (Number) documents.get(0).getData().get("currentSemesterGPA");
                        Number credit = (Number) documents.get(0).getData().get("recentCreditHours");
                        studentInfo.setCumulativeGPA(GPA.doubleValue());
                        studentInfo.setLatestGPA(latestGPA.doubleValue());
                        studentInfo.setRecentCreditHours(credit.longValue());
                        if(contactNoMatch) {
                            contactError = contactError + " do not match Registrar Data Store. Update to new value(s)? (y/n)";
                            errorTextView.setText(contactError);
                            studentDoc = documents.get(0).getReference();
                            //enable user error response
                            contactButton.setEnabled(true);
                            contactErrorEditText.setFocusableInTouchMode(true);
                            //disable normal user response
                            firstNameEditText.setFocusable(false);
                            lastNameEditText.setFocusable(false);
                            zipCodeEditText.setFocusable(false);
                            dobEditText.setFocusable(false);
                            emailEditText.setFocusable(false);
                            phoneNumberEditText.setFocusable(false);
                            studentNumEditText.setFocusable(false);
                            submitButton.setEnabled(false);
                            return;
                        }
                        MainLogic2();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    errorTextView.setText("Registrar Office Data Store is unreachable");
                    }
                });
    }

    public void MainLogic2() {
        if(studentInfo.getRecentCreditHours() <= 0) {
            errorTextView.setText("Cannot apply - not enrolled in latest semester");
            return;
        }

        boolean eligible = true;
        String reasons = "";
        if(studentInfo.getCumulativeGPA() < 3.2) {
            eligible = false;
            reasons = reasons + "GPA";
        }
        if(studentInfo.getRecentCreditHours() < 12) {
            eligible = false;
            reasons = reasons + " credit hours during latest semester";
        }
        //calculate applicant's age
        String[] dob = studentInfo.getDoB().split("-");
        LocalDate currentDate = LocalDate.now();
        long diffYear = currentDate.getYear() - Long.parseLong(dob[2]);
        long diffMonth = currentDate.getMonthValue() - Long.parseLong(dob[0]);
        long diffDay = currentDate.getDayOfMonth() - Long.parseLong(dob[1]);
        long age = ((diffYear * 365) + (diffMonth * 31) + diffDay)/365;
        if(age < 23) {
            eligible = false;
            reasons = reasons + " age";
        }
        //Store in applicants data store
        Map<String, Object> student = new HashMap<>();
        student.put("firstName", studentInfo.getFirstName());
        student.put("lastName", studentInfo.getLastName());
        student.put("studentNumber", studentInfo.getStudentNumber());
        student.put("zipCode", studentInfo.getZipCode());
        student.put("Dob", studentInfo.getDoB());
        student.put("phoneNum", studentInfo.getPhoneNum());
        student.put("eMail", studentInfo.geteMail());
        student.put("gender", studentInfo.getGender());
        student.put("academicStatus", studentInfo.getAcademicStatus());
        student.put("cumulativeGPA", studentInfo.getCumulativeGPA());
        student.put("currentSemesterGPA", studentInfo.getLatestGPA());
        student.put("recentCreditHours", studentInfo.getRecentCreditHours());
        student.put("eligibility", eligible);
        student.put("reasons", reasons);

        db.collection(Applicant_Data_Store)
                .add(student)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        errorTextView.setText("Your application has been submitted");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        errorTextView.setText("Applicant Data Store is unreachable");
                    }
                });
    }

    EditText firstNameEditText, lastNameEditText, zipCodeEditText, dobEditText, emailEditText, phoneNumberEditText, studentNumEditText, contactErrorEditText;
    TextView errorTextView;
    Button submitButton, contactButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_application);

        firstNameEditText = (EditText) findViewById(R.id.firstNameEditText);
        lastNameEditText = findViewById(R.id.lastNameEditText);
        zipCodeEditText = findViewById(R.id.zipCodeEditText);
        dobEditText = findViewById(R.id.dobEditText);
        emailEditText = findViewById(R.id.emailEditText);
        phoneNumberEditText = findViewById(R.id.phoneNumberEditText);
        studentNumEditText = findViewById(R.id.studentNumberEditText);
        contactErrorEditText = findViewById(R.id.contactErrorEditText);

        submitButton = findViewById(R.id.submitApplicationButton);
        contactButton = findViewById(R.id.contactButton);

        errorTextView = findViewById(R.id.errorTextView);
        studentInfo = new Application();
        errorTextView.setText("");
        contactButton.setEnabled(false);
        contactErrorEditText.setFocusable(false);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                errorTextView.setText("");
                studentInfo.setFirstName(firstNameEditText.getText().toString());
                studentInfo.setLastName(lastNameEditText.getText().toString());
                studentInfo.setZipCode(zipCodeEditText.getText().toString());
                studentInfo.setDoB(dobEditText.getText().toString());
                studentInfo.setPhoneNum(phoneNumberEditText.getText().toString());
                studentInfo.seteMail(emailEditText.getText().toString());
                studentInfo.setStudentNumber(studentNumEditText.getText().toString());

                MainLogic1();
            }
        });

        contactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                errorTextView.setText("");
                String Answer = contactErrorEditText.getText().toString();
                if(Answer.equals("Y") || Answer.equals("y")) {
                    studentDoc.update("eMail", studentInfo.geteMail());
                    studentDoc.update("phoneNumber", studentInfo.getPhoneNum());
                    //disable user error response
                    contactButton.setEnabled(false);
                    contactErrorEditText.setFocusable(false);
                    //enable normal user response
                    firstNameEditText.setFocusableInTouchMode(true);
                    lastNameEditText.setFocusableInTouchMode(true);
                    zipCodeEditText.setFocusableInTouchMode(true);
                    dobEditText.setFocusableInTouchMode(true);
                    emailEditText.setFocusableInTouchMode(true);
                    phoneNumberEditText.setFocusableInTouchMode(true);
                    studentNumEditText.setFocusableInTouchMode(true);
                    submitButton.setEnabled(true);
                    MainLogic2();
                } else if (Answer.equals("N") || Answer.equals("n")) {
                    errorTextView.setText("Please reenter data");
                    //disable user error response
                    contactButton.setEnabled(false);
                    contactErrorEditText.setFocusable(false);
                    //enable normal user response
                    firstNameEditText.setFocusableInTouchMode(true);
                    lastNameEditText.setFocusableInTouchMode(true);
                    zipCodeEditText.setFocusableInTouchMode(true);
                    dobEditText.setFocusableInTouchMode(true);
                    emailEditText.setFocusableInTouchMode(true);
                    phoneNumberEditText.setFocusableInTouchMode(true);
                    studentNumEditText.setFocusableInTouchMode(true);
                    submitButton.setEnabled(true);
                } else {
                    errorTextView.setText("Invalid response. Update contact information to new value(s) (y/n)?");
                }
            }
        });
    }
}