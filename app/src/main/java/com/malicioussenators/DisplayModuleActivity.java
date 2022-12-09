package com.malicioussenators;

import static com.malicioussenators.CONSTANTS.ACCOUNTING_DATA_STORE;
import static com.malicioussenators.CONSTANTS.APPLICANTS_DATA_STORE;
import static com.malicioussenators.CONSTANTS.AWARDED_DATA_STORE;
import static com.malicioussenators.CONSTANTS.REGISTRAR_DATA_STORE;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//Program Name: DisplayModuleActivity
//Programmer Name: Dmitry Kustarnikov
//Description: Display activity that completed test cases 3 and 5
//Date Created: 12/5/2022

public class DisplayModuleActivity extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    Button extractAllDataButton, displayApplicantsButton;
    EditText studentNumberEditText;
    LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_module);

        extractAllDataButton = (Button) findViewById(R.id.extractAllDataButton);
        displayApplicantsButton = (Button) findViewById(R.id.showApplicantsButton);
        studentNumberEditText = (EditText) findViewById(R.id.displayStudentNumberEditText);
        linearLayout = (LinearLayout) findViewById(R.id.displayLinearLayout);

        DataHelper dh = new DataHelper();

        displayApplicantsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                linearLayout.removeAllViews();
                String fileName = Timestamp.now().toDate().toString() + "_" + APPLICANTS_DATA_STORE + ".txt";

                //If there is something in the edit text, search for that
                String studentNum = studentNumberEditText.getText().toString();
                Map<String, Object> tempMap = new HashMap<>();
                if (!studentNum.isEmpty()) {


                    db.collection(APPLICANTS_DATA_STORE)
                            .whereEqualTo("studentNumber", studentNum)
                            .get().addOnCompleteListener(task -> {
                        List<DocumentSnapshot> documents = task.getResult().getDocuments();
                        for (DocumentSnapshot document : documents) {
                            tempMap.put(document.getId(), document.getData());
                        }
                        //At this point we have Map<String, Object> that is populated
                        //Convert the data to a list of Jsons
                        ArrayList<String> dataAsList = dh.convertMapToArrayOfJsons(tempMap);
                        //Write each object into the file
                        if (dataAsList.isEmpty()) {
                            //Here we just create one TextView and put it in the layout
                            String data = "No Student with such StudentNumber is found";
                            TextView[] t=new TextView[1];
                            LinearLayout.LayoutParams dim=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            t[0]=new TextView(DisplayModuleActivity.this);
                            t[0].setLayoutParams(dim);
                            t[0].setText(data);
                            linearLayout.addView(t[0]);
                        }
                        else {
                            TextView[] t=new TextView[dataAsList.size()]; //This technically should have only 1 entry, but what if not?
                            for (int i = 0; i < dataAsList.size(); i++) {
                                String data = dataAsList.get(i);
                                LinearLayout.LayoutParams dim = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                t[i] = new TextView(DisplayModuleActivity.this);
                                t[i].setLayoutParams(dim);
                                t[i].setText(data);
                                linearLayout.addView(t[i]);
                            }
                        }

                    });
                } else {
                    db.collection(APPLICANTS_DATA_STORE)
                            .get().addOnCompleteListener(task -> {
                        List<DocumentSnapshot> documents = task.getResult().getDocuments();
                        for (DocumentSnapshot document : documents) {
                            tempMap.put(document.getId(), document.getData());
                        }
                        //At this point we have Map<String, Object> that is populated
                        //Convert the data to a list of Jsons
                        ArrayList<String> dataAsList = dh.convertMapToArrayOfJsons(tempMap);
                        //Write each object into the file
                        TextView[] t = new TextView[dataAsList.size()];
                        TextView textView = new TextView(getApplicationContext());
                        LinearLayout.LayoutParams dim=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        for (int i = 0; i < dataAsList.size(); i++) {
                            String data = dataAsList.get(i);
                            t[i]=new TextView(DisplayModuleActivity.this);
                            t[i].setLayoutParams(dim);
                            t[i].setText(data);
                            linearLayout.addView(t[i]);
//
//                            textView.setText(data);
//                            linearLayout.addView(textView);
//                            displayApplicantsTextView.setText(data);
                        }
                    });
                }
                //Otherwise, just display all the values
            }
        });

        //Show Applicants button onclick
        extractAllDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Because by using this button we are extracting ALL information, we go through the list of ALL DataStores
                ArrayList<String> dataStores = new ArrayList<>();

                dataStores.add(REGISTRAR_DATA_STORE);
                dataStores.add(AWARDED_DATA_STORE);
                dataStores.add(ACCOUNTING_DATA_STORE);
                dataStores.add(APPLICANTS_DATA_STORE);

                //This is going to be the timestamp for file creation.
                String currentTime = Timestamp.now().toDate().toString();

                for (String dataStore : dataStores) {
                    //Create the file
                    String fileName = dataStore + "_" + currentTime + ".txt";
                    dh.createTextFile(fileName, DisplayModuleActivity.this);


                    //Get all the data and store it in the tempMap
                    Map<String, Object> tempMap = new HashMap<>();
                    db.collection(dataStore)
                            .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            //Unfortunately I have to do all the logic in OnComplete because otherwise it gets skipped until completion
                            List<DocumentSnapshot> documents = task.getResult().getDocuments();
                            for (DocumentSnapshot document : documents) {
                                tempMap.put(document.getId(), document.getData());
                            }
                            //At this point we have Map<String, Object> that is populated
                            //Convert the data to a list of Jsons
                            ArrayList<String> dataAsList = dh.convertMapToArrayOfJsons(tempMap);
                            //Write each object into the file
                            for (String data : dataAsList) {
                                dh.writeToTextFile(fileName, data, DisplayModuleActivity.this);
                            }
                        }
                    }).addOnFailureListener(e -> {
                        Toast.makeText(getApplicationContext(), dataStore + " datastore is not found", Toast.LENGTH_LONG).show();
                    });
                }

                Toast.makeText(getApplicationContext(), "Data has been extracted", Toast.LENGTH_LONG).show();
            }
        });
    }
}