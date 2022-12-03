package com.malicioussenators;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.malicioussenators.models.Application;

import java.util.List;

public class SubmitApplicationActivity extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    String doNothing() {
        return "nothing";
    }

    static Application convertDocumentToApplication(DocumentSnapshot doc) {
        Application app = new Application();

        app.setFirstName(doc.get("firstName").toString());
        app.setStudentNumber(doc.get("studentNumber").toString());

        return app;
    }

    Application getApplicationById(String id) {
        final Application[] app = new Application[1];
        db.collection("RegistrarDataStore")
                .whereEqualTo("studentNumber", id).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        List<DocumentSnapshot> documents = task.getResult().getDocuments();
                        convertDocumentToApplication(documents.get(0));
//                        app[0] = task.getResult().getDocuments().get(0).convertDocumentToApplication(Application.class);
                    }
                });


        return app[0];
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

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String firstName = firstNameEditText.getText().toString();

                errorTextView.setTextColor(Color.RED);
                errorTextView.setText(firstName);

                getApplicationById("12");

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