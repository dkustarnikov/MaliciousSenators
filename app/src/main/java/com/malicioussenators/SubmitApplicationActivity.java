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

    public void MainLogic1() {
        //call the data store and wait for a response
        db.collection("RegistrarDataStore")
                .whereEqualTo("studentNumber", studentInfo.getStudentNumber()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        //When the response comes continue with main logic
                        List<DocumentSnapshot> documents = task.getResult().getDocuments();
                        if (documents.size() == 0){
                            errorTextView.setText("No Student Number Match - please reenter data");
                            return;
                        }
                        else {
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
                                contactError = contactError + "Zip code";
                                contactNoMatch = true;
                            }
                            if(!studentInfo.getPhoneNum().equals(documents.get(0).get("phoneNumber").toString())) {
                                contactError = contactError + " phone number";
                                contactNoMatch = true;
                            }

                            if(infoNoMatch) {
                                infoError = infoError + "-Please reenter data";
                                errorTextView.setText(infoError);
                                return;
                            }

                            studentInfo.setGender(documents.get(0).get("gender").toString());
                            studentInfo.setAcademicStatus(documents.get(0).get("academicStatus").toString());
                            studentInfo.setCumulativeGPA((double) documents.get(0).getData().get("cumulativeGPA"));
                            studentInfo.setRecentCreditHours((int) documents.get(0).getData().get("recentCreditHours"));
                            if(contactNoMatch) {
                                contactError = contactError + " do not match Registrar Data Store. Update to new value(s)? (y/n)";
                                errorTextView.setText(infoError);
                            }
                            MainLogic2();
                        }
                    }
                });
    }

    public void MainLogic2() {
        if(studentInfo.getRecentCreditHours() <= 0) {
            errorTextView.setText("Cannot apply - not currently enrolled");
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
        int diffYear = currentDate.getYear() - Integer.parseInt(dob[-1]);
        int diffMonth = currentDate.getMonthValue() - Integer.parseInt(dob[0]);
        int diffDay = currentDate.getDayOfMonth() - Integer.parseInt(dob[1]);
        int age = ((diffYear * 365) + (diffMonth * 31) + diffDay)/365;
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
        student.put("recentCreditHours", studentInfo.getRecentCreditHours());
        student.put("eligibility", eligible);
        student.put("reasons", reasons);

        db.collection("ApplicantsDataStore")
                .add(student)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("Tag","DocumentSnapshot added with ID");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Tag","Error adding document", e);
                    }
                });
    }

    EditText firstNameEditText, lastNameEditText, zipCodeEditText, dobEditText, emailEditText, phoneNumberEditText, studentNumEditText;
    TextView errorTextView;
    Button submitButton;

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

        submitButton = findViewById(R.id.submitApplicationButton);

        errorTextView = findViewById(R.id.errorTextView);
        errorTextView.setText("");

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                studentInfo = new Application();
                errorTextView.setText("");
                studentInfo.setFirstName(firstNameEditText.getText().toString());
                studentInfo.setLastName(lastNameEditText.getText().toString());
                studentInfo.setZipCode(zipCodeEditText.getText().toString());
                studentInfo.setDoB(dobEditText.getText().toString());
                studentInfo.setPhoneNum(phoneNumberEditText.getText().toString());
                studentInfo.seteMail(emailEditText.getText().toString());
                studentInfo.setStudentNumber(studentNumEditText.getText().toString());

                MainLogic1();


//                Application app = new Application(firstName);



//                db.collection("RegistrarDataStore").addTask<QuerySnapshot> snapshotTask= (app);

//                db.collection("RegistrarDataStore")
//                        .get()
//                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                            @Override
//                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                                if (task.isSuccessful()) {
//                                    for (QueryDocumentSnapshot document : task.getResult()) {
//                                        Log.d("TAG", document.getId() + " => " + document.getData());
//                                        Log.d("firebase", String.valueOf(task.getResult().getDocuments()));
//
//                                        List<DocumentSnapshot> documents = task.getResult().getDocuments();
//                                        String firstNameFromDB = (String) documents.get(0).get("firstName");
//
//                                        for (DocumentSnapshot doc : documents) {
//                                            Log.e("TAG", doc.get("born").toString());
//                                            Log.e("TAG", doc.getData().toString());
//                                        }
//
//                                        Log.d("1", "1");
//                                    }
//                                } else {
//                                    Log.w("TAG", "Error getting documents.", task.getException());
//                                }
//                            }
//                        });
            }
        });

        /*
         * First name
         * Last name
         * Zip code
         * DOB
         * Email
         * Phone number
         * Student Number
         */

//        \d{2}-\d{2}-\d{2} == dd-mm-yy

    }
}