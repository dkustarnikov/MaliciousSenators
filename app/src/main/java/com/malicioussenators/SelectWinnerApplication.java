package com.malicioussenators;

import static com.malicioussenators.CONSTANTS.APPLICANTS_DATA_STORE;
import static com.malicioussenators.CONSTANTS.AWARDED_DATA_STORE;
import static com.malicioussenators.CONSTANTS.REGISTRAR_DATA_STORE;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;


public class SelectWinnerApplication extends AppCompatActivity {

    Button readyButton, steadyButton, pickWinnerButton;

    //Linking the voting system
    Button showTuitionPaidStudent1Button, castVoteStudent1Button,
            showTuitionPaidStudent2Button, castVoteStudent2Button,
            endVotingButton;
    TextView studentName1VotingTextView, student1TuitionPaidTextView, votingResultsStudent1,
            studentName2VotingTextView, student2TuitionPaidTextView, votingResultsStudent2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_winner_application);

        AtomicInteger votesForStudentOne = new AtomicInteger();
        AtomicInteger votesForStudentTwo = new AtomicInteger();

        readyButton = findViewById(R.id.readyButton);
        steadyButton = findViewById(R.id.steadyButton);
        pickWinnerButton = findViewById(R.id.pickWinnerButton);

        showTuitionPaidStudent1Button = findViewById(R.id.showTuitionPaidStudent1Button);
        castVoteStudent1Button = findViewById(R.id.castVoteStudent1Button);
        showTuitionPaidStudent2Button = findViewById(R.id.showTuitionPaidStudent2Button);
        castVoteStudent2Button = findViewById(R.id.castVoteStudent2Button);
        endVotingButton = findViewById(R.id.endVotingButton);

        studentName1VotingTextView = findViewById(R.id.studentName1VotingTextView);
        student2TuitionPaidTextView = findViewById(R.id.student2TuitionPaidTextView);
        student1TuitionPaidTextView = findViewById(R.id.student1TuitionPaidTextView);
        votingResultsStudent1 = findViewById(R.id.votingResultsStudent1);
        studentName2VotingTextView = findViewById(R.id.studentName2VotingTextView);
        votingResultsStudent2 = findViewById(R.id.votingResultsStudent2);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        HashMap<String, Application> applicantsDataStoreMap = new HashMap<>();
        AtomicReference<HashMap<String, RegistrarData>> tempRegistrarsDataStoreMap = new AtomicReference<>(new HashMap<>());
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
                    tempRegistrarsDataStoreMap.get().put(doc.get("studentNumber").toString(), doc.toObject(RegistrarData.class));
                }
                steadyButton.setEnabled(false);
                pickWinnerButton.setEnabled(true);
            });
        });

        pickWinnerButton.setOnClickListener(view -> {
            for (Map.Entry entry : tempRegistrarsDataStoreMap.get().entrySet()) {
                String studentNumber = entry.getKey().toString();
                if (applicantsDataStoreMap.containsKey(studentNumber)) {
                    registrarsDataStoreMap.put(studentNumber, (RegistrarData) entry.getValue());
                }
            }

            //At this point you have all the data
//            Logic
            Collection<RegistrarData> dataList = registrarsDataStoreMap.values();
            List<RegistrarData> dataList2 = dataList.stream().collect(Collectors.toList());
            Collections.sort(dataList2, new Comparator<RegistrarData>() {
                public int compare(RegistrarData s1, RegistrarData s2) {
                    if (s1.getCumulativeGPA() < s2.getCumulativeGPA()) {
                        return 1;
                    } else if (s1.getCumulativeGPA() > s2.getCumulativeGPA()) {
                        return -1;
                    } else {
                        return 0;
                    }
                }
            });

            double highestCumulativeGPA = dataList2.get(0).getCumulativeGPA();
            tempRegistrarsDataStoreMap.get().clear();
            for (Map.Entry entry : registrarsDataStoreMap.entrySet()) {
                String studentNumber = entry.getKey().toString();
                RegistrarData s1 = (RegistrarData) entry.getValue();
                if (s1.getCumulativeGPA() == highestCumulativeGPA) {
                    tempRegistrarsDataStoreMap.get().put(studentNumber, s1);
                }
            }
            dataList.clear();
            dataList2.clear();
            //handle tie in cumulativeGPA
            if (tempRegistrarsDataStoreMap.get().size() != 1) {
                dataList = tempRegistrarsDataStoreMap.get().values();
                dataList2 = dataList.stream().collect(Collectors.toList());
                Collections.sort(dataList2, new Comparator<RegistrarData>() {
                    public int compare(RegistrarData s1, RegistrarData s2) {
                        if (s1.getCurrentSemesterGPA() < s2.getCurrentSemesterGPA()) {
                            return 1;
                        } else if (s1.getCurrentSemesterGPA() > s2.getCurrentSemesterGPA()) {
                            return -1;
                        } else {
                            return 0;
                        }
                    }
                });

                double highestCurrentSemesterGPA = dataList2.get(0).getCurrentSemesterGPA();
                registrarsDataStoreMap.clear();
                for (Map.Entry entry : tempRegistrarsDataStoreMap.get().entrySet()) {
                    String studentNumber = entry.getKey().toString();
                    RegistrarData s1 = (RegistrarData) entry.getValue();
                    if (s1.getCurrentSemesterGPA() == highestCurrentSemesterGPA) {
                        registrarsDataStoreMap.put(studentNumber, s1);
                    }
                }
            } else {
                //winner selected based on highest cumulativeGPA
                //store winner in awarded data store
                //create emails
                Toast.makeText(this, "Highest cumulativeGPA winner is selected", Toast.LENGTH_LONG).show();
//                RegistrarData[] winnerArr = tempRegistrarsDataStoreMap.get().entrySet().toArray();

                RegistrarData winner = new RegistrarData();
                for (Map.Entry entry : tempRegistrarsDataStoreMap.get().entrySet()) {
                    winner = (RegistrarData) entry.getValue();
                }

                //Now we add the winner to AwardedDataStore
                db.collection(AWARDED_DATA_STORE).add(winner);

                //TODO

            }
            //handle tie in currentsemesterGPA
            if (registrarsDataStoreMap.size() != 1) {
                tempRegistrarsDataStoreMap.get().clear();
                for (Map.Entry entry : registrarsDataStoreMap.entrySet()) {
                    String studentNumber = entry.getKey().toString();
                    RegistrarData s1 = (RegistrarData) entry.getValue();
                    if (s1.getAcademicStatus().equals("Junior")) {
                        tempRegistrarsDataStoreMap.get().put(studentNumber, s1);
                    }
                }
                if (tempRegistrarsDataStoreMap.get().size() == 0) { //None are juniors
                    for (Map.Entry entry : registrarsDataStoreMap.entrySet()) {
                        String studentNumber = entry.getKey().toString();
                        RegistrarData s1 = (RegistrarData) entry.getValue();
                        tempRegistrarsDataStoreMap.get().put(studentNumber, s1);
                    }
                }
            } else {
                //winner selected based on highest currentSemesterGPA
                //store winner in awarded data store
                //create emails
                //TODO: sd
                Toast.makeText(this, "Highest currentSemesterGPA winner is selected", Toast.LENGTH_LONG).show();


            }
            System.out.println(1);
            //handle tie in academicStatus
            if (tempRegistrarsDataStoreMap.get().size() != 1) {
                registrarsDataStoreMap.clear();
                for (Map.Entry entry : tempRegistrarsDataStoreMap.get().entrySet()) {
                    String studentNumber = entry.getKey().toString();
                    RegistrarData s1 = (RegistrarData) entry.getValue();
                    if (s1.getGender().equals("Female")) {
                        registrarsDataStoreMap.put(studentNumber, s1);
                    }
                }
                if (registrarsDataStoreMap.size() == 0) { //none are female
                    for (Map.Entry entry : tempRegistrarsDataStoreMap.get().entrySet()) {
                        String studentNumber = entry.getKey().toString();
                        RegistrarData s1 = (RegistrarData) entry.getValue();
                        registrarsDataStoreMap.put(studentNumber, s1);
                    }
                }
            } else {
                //winner selected based on being a Junior
                //store winner in awarded data store
                //create emails
                //TODO: sd
                Toast.makeText(this, "Junior winner is selected", Toast.LENGTH_LONG).show();

//                db.collection(AWARDED_DATA_STORE).add()


            }
            //handle tie in gender
            if (registrarsDataStoreMap.size() != 1) {
                //voting system
                //Display the voting system

                votesForStudentOne.set(0);
                votesForStudentTwo.set(0);

                String studentOneName = "John";
                String studentTwoName = "Dmitry";

                String studentOneTuitionPaid = "$0";
                String studentTwoTuitionPaid = "$5000";

                studentName1VotingTextView.setText(studentOneName);
                studentName2VotingTextView.setText(studentTwoName);

                student1TuitionPaidTextView.setText(studentOneTuitionPaid);
                student2TuitionPaidTextView.setText(studentTwoTuitionPaid);


                showTuitionPaidStudent1Button.setVisibility(View.VISIBLE);
                castVoteStudent1Button.setVisibility(View.VISIBLE);
                showTuitionPaidStudent2Button.setVisibility(View.VISIBLE);
                castVoteStudent2Button.setVisibility(View.VISIBLE);
                endVotingButton.setVisibility(View.VISIBLE);
                studentName1VotingTextView.setVisibility(View.VISIBLE);
                votingResultsStudent1.setVisibility(View.VISIBLE);
                studentName2VotingTextView.setVisibility(View.VISIBLE);
                votingResultsStudent2.setVisibility(View.VISIBLE);




            } else {
                //winner selected based on being a female
                //store winner in awarded data store
                //create emails
                //TODO: sd
                Toast.makeText(this, "Female winner is selected", Toast.LENGTH_LONG).show();

            }
        });

        endVotingButton.setOnClickListener(view -> {
            //TODO: The logic for after voting ends
            if (votesForStudentOne.intValue() > votesForStudentTwo.intValue()) {
                Toast.makeText(this, "Winner is studentOne", Toast.LENGTH_LONG).show();



            }
            else if (votesForStudentOne.intValue() > votesForStudentTwo.intValue()) {
                Toast.makeText(this, "Winner is studentTwo", Toast.LENGTH_LONG).show();




            }
            else {
                Toast.makeText(this, "One more vote needed", Toast.LENGTH_LONG).show();

            }
        });

        showTuitionPaidStudent1Button.setOnClickListener(view -> {
            student1TuitionPaidTextView.setVisibility(View.VISIBLE);
        });

        showTuitionPaidStudent2Button.setOnClickListener(view -> student2TuitionPaidTextView.setVisibility(View.VISIBLE));

        castVoteStudent1Button.setOnClickListener(view -> {
            votesForStudentOne.set(votesForStudentOne.get() + 1);
            votingResultsStudent1.setText(String.valueOf(votesForStudentOne.get()));
        });

        castVoteStudent2Button.setOnClickListener(view -> {
            votesForStudentTwo.set(votesForStudentTwo.get() + 1);
            votingResultsStudent2.setText(String.valueOf(votesForStudentTwo.get()));
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