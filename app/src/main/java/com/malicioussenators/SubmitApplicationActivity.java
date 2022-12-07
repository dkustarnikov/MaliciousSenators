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

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class SubmitApplicationActivity extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public Application searchStudentInfo(String StudentNum) {
        Application studentInfo = new Application();
        db.collection("RegistrarDataStore")
                .whereEqualTo("studentNumber", StudentNum)
                .get().addOnCompleteListener(task -> {
                    List<DocumentSnapshot> documents = task.getResult().getDocuments();
                    if (documents.size() == 0){
                        studentInfo.setFirstName("No luck");
                        return;
                    }
                    else {
                        studentInfo.setEmpty(false);
                        studentInfo.setFirstName(documents.get(0).get("firstName").toString());
                        studentInfo.setLastName(documents.get(0).get("lastName").toString());
                        studentInfo.setZipCode(documents.get(0).get("zipCode").toString());
                        studentInfo.setDoB(documents.get(0).get("DoB").toString());
                        studentInfo.setPhoneNum(documents.get(0).get("phoneNumber").toString());
                        studentInfo.seteMail(documents.get(0).get("eMail").toString());
                        studentInfo.setStudentNumber(StudentNum);
                    }
                });

        /*db.collection("RegistrarDataStore")
                .whereEqualTo("studentNumber", StudentNum).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        List<DocumentSnapshot> documents = task.getResult().getDocuments();
                        if (documents.size() == 0){
                            studentInfo.setFirstName("No luck");
                            return;
                        }
                        else {
                            studentInfo.setEmpty(false);
                            studentInfo.setFirstName(documents.get(0).get("firstName").toString());
                            studentInfo.setLastName(documents.get(0).get("lastName").toString());
                            studentInfo.setZipCode(documents.get(0).get("zipCode").toString());
                            studentInfo.setDoB(documents.get(0).get("DoB").toString());
                            studentInfo.setPhoneNum(documents.get(0).get("phoneNumber").toString());
                            studentInfo.seteMail(documents.get(0).get("eMail").toString());
                            studentInfo.setStudentNumber(StudentNum);
                        }
                    }
                });*/
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Change the image back here
            }
        }, 1000); // 1 sec delay
        return studentInfo;
    }

    Application retrieveStudentInfo(Application studentInfo) {
        db.collection("RegistrarDataStore")
                .whereEqualTo("studentNumber", studentInfo.getStudentNumber()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        List<DocumentSnapshot> documents = task.getResult().getDocuments();
                        if (documents.size() == 0){
                            studentInfo.setEmpty(true);
                            return;
                        }
                        else {
                            studentInfo.setEmpty(false);
                            studentInfo.setGender(documents.get(0).get("gender").toString());
                            studentInfo.setAcademicStatus(documents.get(0).get("academicStatus").toString());
                            studentInfo.setCumulativeGPA(documents.get(0).get("cumulativeGPA").toString());
                            studentInfo.setRecentCreditHours(documents.get(0).get("recentCreditHours").toString());
                        }
                    }
                });
        return studentInfo;
    }

    void storeStudentInfo(Application studentInfo, boolean Eligible, String reasons) {
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
        student.put("eligibility", Eligible);
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
                errorTextView.setText("Testing this");
                String studentNumber = studentNumEditText.getText().toString();
                String firstName = firstNameEditText.getText().toString();
                String lastName = lastNameEditText.getText().toString();
                String zipCode = zipCodeEditText.getText().toString();
                String DoB = dobEditText.getText().toString();
                String phoneNumber = phoneNumberEditText.getText().toString();

                Application studentInfo = searchStudentInfo(studentNumber);
                if(studentInfo.isEmpty()) {
                    errorTextView.setText("No Student Number Match - please reenter data");
                    errorTextView.setText(studentInfo.getFirstName());
                    return;
                }

                boolean infoNoMatch = false;
                boolean contactNoMatch = false;
                String infoError = "Data field(s) do not match Registrar Data Store:";
                String contactError = "";
                if(firstName != studentInfo.getFirstName()) {
                    infoError = infoError + " first name";
                    infoNoMatch = true;
                }
                if(lastName != studentInfo.getLastName()) {
                    infoError = infoError + " last name";
                    infoNoMatch = true;
                }
                if(DoB != studentInfo.getDoB()) {
                    infoError = infoError + " date of birth";
                    infoNoMatch = true;
                }
                if(lastName != studentInfo.getLastName()) {
                    infoError = infoError + " last name";
                    infoNoMatch = true;
                }
                if(zipCode != studentInfo.getZipCode()) {
                    contactError = contactError + "Zip code";
                    contactNoMatch = true;
                }
                if(phoneNumber != studentInfo.getPhoneNum()) {
                    contactError = contactError + " phone number";
                    contactNoMatch = true;
                }

                if(infoNoMatch) {
                    infoError = infoError + "-Please reenter data";
                    errorTextView.setText(infoError);
                    return;
                }
                else {
                    errorTextView.setText("success!");
                    return;
                }


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