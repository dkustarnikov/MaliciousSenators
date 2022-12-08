package com.malicioussenators;

import static com.malicioussenators.CONSTANTS.APPLICANTS_DATA_STORE;
import static com.malicioussenators.CONSTANTS.REGISTRAR_DATA_STORE;

import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.malicioussenators.models.Application;
import com.malicioussenators.models.RegistrarData;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class SelectWinnerApplication extends AppCompatActivity {

    Button readyButton, steadyButton, pickWinnerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_winner_application);

        readyButton = findViewById(R.id.readyButton);
        steadyButton = findViewById(R.id.steadyButton);
        pickWinnerButton = findViewById(R.id.pickWinnerButton);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        HashMap<String, Application> applicantsDataStoreMap = new HashMap<>();
        HashMap<String, RegistrarData> tempRegistrarsDataStoreMap = new HashMap<>();
        HashMap<String, RegistrarData> registrarsDataStoreMap = new HashMap<>();


        readyButton.setOnClickListener(view -> {
            db.collection(APPLICANTS_DATA_STORE)
                    .get().addOnCompleteListener(task -> {
                List<DocumentSnapshot> applicationDocuments = task.getResult().getDocuments();
                for (DocumentSnapshot doc : applicationDocuments) {
                    applicantsDataStoreMap.put(doc.get("studentNumber").toString(), doc.toObject(Application.class));
                }
                steadyButton.setEnabled(true);
                readyButton.setEnabled(false);
            });
        });

        steadyButton.setOnClickListener(view -> {
            db.collection(REGISTRAR_DATA_STORE)
                    .get().addOnCompleteListener(task -> {
                List<DocumentSnapshot> applicationDocuments = task.getResult().getDocuments();
                for (DocumentSnapshot doc : applicationDocuments) {
                    tempRegistrarsDataStoreMap.put(doc.get("studentNumber").toString(), doc.toObject(RegistrarData.class));
                }
                steadyButton.setEnabled(false);
                pickWinnerButton.setEnabled(true);
            });
        });

        pickWinnerButton.setOnClickListener(view -> {
            for (Map.Entry entry : tempRegistrarsDataStoreMap.entrySet()) {
                String studentNumber = entry.getKey().toString();
                if (applicantsDataStoreMap.containsKey(studentNumber)) {
                    registrarsDataStoreMap.put(studentNumber, (RegistrarData) entry.getValue());
                }
            }

            //At this point you have all the data
//            Logic
            Collection<RegistrarData> dataList = registrarsDataStoreMap.values();
            List<RegistrarData> dataList2 = dataList.stream().collect(Collectors.toList());
            Collections.sort(dataList2, new Comparator<RegistrarData>(){
                public int compare(RegistrarData s1, RegistrarData s2) {
                    if(s1.getCumulativeGPA() < s2.getCumulativeGPA()) {
                        return 1;
                    }
                    else if(s1.getCumulativeGPA() > s2.getCumulativeGPA()) {
                        return -1;
                    }
                    else {
                        return 0;
                    }
                }
            });

            double highestCumulativeGPA = dataList2.get(0).getCumulativeGPA();
            tempRegistrarsDataStoreMap.clear();
            for (Map.Entry entry : registrarsDataStoreMap.entrySet()) {
                String studentNumber = entry.getKey().toString();
                RegistrarData s1 = (RegistrarData) entry.getValue();
                if (s1.getCumulativeGPA() == highestCumulativeGPA) {
                    tempRegistrarsDataStoreMap.put(studentNumber, s1);
                }
            }

            System.out.println(1);

        });


        //Ready
        //Steady
        //Pick Winner


//        db.collection(APPLICANTS_DATA_STORE)
//                .get()
//                .addOnCompleteListener(task -> {
//                    List<DocumentSnapshot> applicationDocuments = task.getResult().getDocuments();
//                    for (DocumentSnapshot doc : applicationDocuments) {
//                        applicantsDataStoreMap.put(doc.get("studentNumber").toString(), doc.toObject(Application.class));
//                        //At this point we have the studentNumber
//                        String studentNumber = doc.get("studentNumber").toString();
//                        //So we just pull a user with that studentNumber from Registrar and store it
//
//                        db.collection(REGISTRAR_DATA_STORE)
//                                .whereEqualTo("studentNumber", studentNumber)
//                                .get().addOnCompleteListener(task12 -> {
//                            List<DocumentSnapshot> registrarsDocuments = task12.getResult().getDocuments();
//                            if (!registrarsDocuments.isEmpty()) {
//                                tempRegistrarsDataStoreMap.put(studentNumber, registrarsDocuments.get(0).toObject(RegistrarData.class));
//                            }
//                            System.out.println(1);
//                        });
//                        //At this point we have all applicants
//                        //                      all applicants' registrar data
//                    }
//                });
//
//
//        System.out.println("The final result");
//
//
//        System.out.println(1);


/*
        //Get all registrar entries ordered by gpa

        //Get all applicants entries

        HashMap<String, Object> Applicants = db.collection().orderBy("gpa")

        YourGuy = new Guy();
        HashMap<String, Object> Applicants -> String is the studentNumber, object is all information;
        List<Object> registrarEntries;

        for (entry : registrarEntries) {
            if (Applicants.get(entry.studentNumber)) {
                YourGuy = entry; //YourGuy is the person with the higherst gpa in applicants
            }
        }
        for (entry in registrarEntries) {
            for (applicant in Applicants) {
                if (entry.studentNumber == applicant.studentNumber) {
                    YourGuys = entry or applicant (whatever you need to find)
                    break;
                }
            }
        }
*/


    }
}