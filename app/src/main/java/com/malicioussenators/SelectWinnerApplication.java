package com.malicioussenators;

import static com.malicioussenators.CONSTANTS.ACCOUNTING_DATA_STORE;
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
import com.malicioussenators.models.AccountingData;
import com.malicioussenators.models.Application;
import com.malicioussenators.models.AwardedData;
import com.malicioussenators.models.RegistrarData;

import java.util.ArrayList;
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

    Button readyButton, steadyButton, goButton, pickWinnerButton;

    //Linking the voting system
    Button showTuitionPaidStudent1Button, castVoteStudent1Button,
            showTuitionPaidStudent2Button, castVoteStudent2Button,
            endVotingButton;
    TextView studentName1VotingTextView, student1TuitionPaidTextView, votingResultsStudent1,
            studentName2VotingTextView, student2TuitionPaidTextView, votingResultsStudent2;

    DataHelper dh = new DataHelper();

    public AwardedData getAwardData(RegistrarData winnerInfo, String awardCriteria, int awardAmount) {
        AwardedData winner = new AwardedData();
        winner.setStudentNumber(winnerInfo.getStudentNumber());
        winner.setFirstName(winnerInfo.getFirstName());
        winner.setLastName(winnerInfo.getLastName());
        winner.setAwardCriteria(awardCriteria);
        winner.setAwardedAmount(awardAmount);
        return winner;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_winner_application);

        AtomicInteger votesForStudentOne = new AtomicInteger();
        AtomicInteger votesForStudentTwo = new AtomicInteger();

        readyButton = findViewById(R.id.readyButton);
        steadyButton = findViewById(R.id.steadyButton);
        pickWinnerButton = findViewById(R.id.pickWinnerButton);
        goButton = findViewById(R.id.goButton);

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
        HashMap<String, AccountingData> accountingDataHashMap = new HashMap<>();


        String emailsFileName = "emails.txt";
        dh.createTextFile(emailsFileName, SelectWinnerApplication.this);


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
                goButton.setEnabled(true);
            });
        });

        goButton.setOnClickListener(view -> {
            db.collection(ACCOUNTING_DATA_STORE)
                    .get().addOnCompleteListener(task -> {
                List<DocumentSnapshot> applicationDocuments = task.getResult().getDocuments();
                for (DocumentSnapshot doc : applicationDocuments) {
                    accountingDataHashMap.put(doc.get("studentNumber").toString(), doc.toObject(AccountingData.class));
                }
                goButton.setEnabled(false);
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

                RegistrarData winnerRegistrar = new RegistrarData();
                for (Map.Entry entry : tempRegistrarsDataStoreMap.get().entrySet()) {
                    winnerRegistrar = (RegistrarData) entry.getValue();
                }
                int awardAmount = accountingDataHashMap.get(winnerRegistrar.getStudentNumber()).getTuitionPaid();
                AwardedData winner =  getAwardData(winnerRegistrar, "Cummulative GPA", awardAmount);

                //Now we add the winner to AwardedDataStore
                db.collection(AWARDED_DATA_STORE).add(winner);

                Collection<Application> applicants = applicantsDataStoreMap.values();
                List<Application> applicantsList  = new ArrayList<>(applicants);

                for (Application app: applicantsList) {
                    String email = app.geteMail();
                    String studentNumber = app.getStudentNumber();

                    String data;
                    if (winner.getStudentNumber().equals(studentNumber)) {
                        //We write it to a file
                        data = email + " - Awarded";
                    }
                    else {
                        //We write it to a file
                        data = email + " - Not awarded";
                    }
                    dh.writeToTextFile(emailsFileName, data, SelectWinnerApplication.this);

                }

            }
//            System.out.println(1);
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

                RegistrarData winnerRegistrar = new RegistrarData();
                for (Map.Entry entry : tempRegistrarsDataStoreMap.get().entrySet()) {
                    winnerRegistrar = (RegistrarData) entry.getValue();
                }
                int awardAmount = accountingDataHashMap.get(winnerRegistrar.getStudentNumber()).getTuitionPaid();
                AwardedData winner =  getAwardData(winnerRegistrar, "Cummulative GPA, Latest Semester GPA", awardAmount);

                //Now we add the winner to AwardedDataStore
                db.collection(AWARDED_DATA_STORE).add(winner);

                Collection<Application> applicants = applicantsDataStoreMap.values();
                List<Application> applicantsList  = new ArrayList<>(applicants);

                for (Application app: applicantsList) {
                    String email = app.geteMail();
                    String studentNumber = app.getStudentNumber();

                    String data;
                    if (winner.getStudentNumber().equals(studentNumber)) {
                        //We write it to a file
                        data = email + " - Awarded";
                    }
                    else {
                        //We write it to a file
                        data = email + " - Not awarded";
                    }
                    dh.writeToTextFile(emailsFileName, data, SelectWinnerApplication.this);

                }

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

                RegistrarData winnerRegistrar = new RegistrarData();
                for (Map.Entry entry : tempRegistrarsDataStoreMap.get().entrySet()) {
                    winnerRegistrar = (RegistrarData) entry.getValue();
                }
                int awardAmount = accountingDataHashMap.get(winnerRegistrar.getStudentNumber()).getTuitionPaid();
                AwardedData winner =  getAwardData(winnerRegistrar, "Cummulative GPA, Latest Semester GPA, Junior", awardAmount);

                //Now we add the winner to AwardedDataStore
                db.collection(AWARDED_DATA_STORE).add(winner);

                Collection<Application> applicants = applicantsDataStoreMap.values();
                List<Application> applicantsList  = new ArrayList<>(applicants);

                for (Application app: applicantsList) {
                    String email = app.geteMail();
                    String studentNumber = app.getStudentNumber();

                    String data;
                    if (winner.getStudentNumber().equals(studentNumber)) {
                        //We write it to a file
                        data = email + " - Awarded";
                    }
                    else {
                        //We write it to a file
                        data = email + " - Not awarded";
                    }
                    dh.writeToTextFile(emailsFileName, data, SelectWinnerApplication.this);

                }


            }
            System.out.println(1);
            //handle tie in gender
            if (registrarsDataStoreMap.size() != 1) {
                //voting system
                //Display the voting system
                Collection<RegistrarData> tempList = registrarsDataStoreMap.values();
                List<RegistrarData> nomineeList  = tempList.stream().collect(Collectors.toList());

                votesForStudentOne.set(0);
                votesForStudentTwo.set(0);

                String studentOneName = nomineeList.get(0).getFirstName() + ' ' + nomineeList.get(0).getLastName();
                String studentTwoName = nomineeList.get(1).getFirstName() + ' ' + nomineeList.get(1).getLastName();

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

                RegistrarData winnerRegistrar = new RegistrarData();
                for (Map.Entry entry : tempRegistrarsDataStoreMap.get().entrySet()) {
                    winnerRegistrar = (RegistrarData) entry.getValue();
                }
                int awardAmount = accountingDataHashMap.get(winnerRegistrar.getStudentNumber()).getTuitionPaid();
                AwardedData winner =  getAwardData(winnerRegistrar, "Cummulative GPA, Latest Semester GPA, Junior, Female", awardAmount);

                //Now we add the winner to AwardedDataStore
                db.collection(AWARDED_DATA_STORE).add(winner);

                Collection<Application> applicants = applicantsDataStoreMap.values();
                List<Application> applicantsList  = new ArrayList<>(applicants);

                for (Application app: applicantsList) {
                    String email = app.geteMail();
                    String studentNumber = app.getStudentNumber();

                    String data;
                    if (winner.getStudentNumber().equals(studentNumber)) {
                        //We write it to a file
                        data = email + " - Awarded";
                    }
                    else {
                        //We write it to a file
                        data = email + " - Not awarded";
                    }
                    dh.writeToTextFile(emailsFileName, data, SelectWinnerApplication.this);

                }

            }
        });

        endVotingButton.setOnClickListener(view -> {
            Collection<RegistrarData> tempList = registrarsDataStoreMap.values();
            List<RegistrarData> nomineeList  = tempList.stream().collect(Collectors.toList());
            AwardedData winner = new AwardedData();
            if (votesForStudentOne.intValue() > votesForStudentTwo.intValue()) {
                Toast.makeText(this, "Winner is studentOne", Toast.LENGTH_LONG).show();
                int awardAmount = accountingDataHashMap.get(nomineeList.get(0).getStudentNumber()).getTuitionPaid();
                winner =  getAwardData(nomineeList.get(0), "Cummulative GPA, Latest Semester GPA, Junior, Interview", awardAmount);
                db.collection(AWARDED_DATA_STORE).add(winner);

                //we need to:
                //Go through all applicants, and if its the winner, let them know, if not, let them know
                Collection<Application> applicants = applicantsDataStoreMap.values();
                List<Application> applicantsList  = new ArrayList<>(applicants);

                for (Application app: applicantsList) {
                    String email = app.geteMail();
                    String studentNumber = app.getStudentNumber();

                    String data;
                    if (winner.getStudentNumber().equals(studentNumber)) {
                        //We write it to a file
                        data = email + " - Awarded";
                    }
                    else {
                        //We write it to a file
                        data = email + " - Not awarded";
                    }
                    dh.writeToTextFile(emailsFileName, data, SelectWinnerApplication.this);

                }

            }
            else if (votesForStudentOne.intValue() < votesForStudentTwo.intValue()) {
                Toast.makeText(this, "Winner is studentTwo", Toast.LENGTH_LONG).show();
                int awardAmount = accountingDataHashMap.get(nomineeList.get(1).getStudentNumber()).getTuitionPaid();
                winner =  getAwardData(nomineeList.get(1), "Cummulative GPA, Latest Semester GPA, Junior, Interview", awardAmount);
                db.collection(AWARDED_DATA_STORE).add(winner);

                //we need to:
                //Go through all applicants, and if its the winner, let them know, if not, let them know
                Collection<Application> applicants = applicantsDataStoreMap.values();
                List<Application> applicantsList  = new ArrayList<>(applicants);

                for (Application app: applicantsList) {
                    String email = app.geteMail();
                    String studentNumber = app.getStudentNumber();

                    String data;
                    if (winner.getStudentNumber().equals(studentNumber)) {
                        //We write it to a file
                        data = email + " - Awarded";
                    }
                    else {
                        //We write it to a file
                        data = email + " - Not awarded";
                    }
                    dh.writeToTextFile(emailsFileName, data, SelectWinnerApplication.this);

                }
            }
            else {
                Toast.makeText(this, "One more vote needed", Toast.LENGTH_LONG).show();
            }



        });

        showTuitionPaidStudent1Button.setOnClickListener(view -> {
            student1TuitionPaidTextView.setVisibility(View.VISIBLE);
            Collection<RegistrarData> tempList = registrarsDataStoreMap.values();
            List<RegistrarData> nomineeList  = tempList.stream().collect(Collectors.toList());

            int tuitionPaid = accountingDataHashMap.get(nomineeList.get(0).getStudentNumber()).getTuitionPaid();
            student1TuitionPaidTextView.setText('$' + String.valueOf(tuitionPaid));
        });


        showTuitionPaidStudent2Button.setOnClickListener(view -> {
            student2TuitionPaidTextView.setVisibility(View.VISIBLE);
            Collection<RegistrarData> tempList = registrarsDataStoreMap.values();
            List<RegistrarData> nomineeList  = tempList.stream().collect(Collectors.toList());

            int tuitionPaid = accountingDataHashMap.get(nomineeList.get(1).getStudentNumber()).getTuitionPaid();
            student2TuitionPaidTextView.setText('$' + String.valueOf(tuitionPaid));
        });

        castVoteStudent1Button.setOnClickListener(view -> {
            votesForStudentOne.set(votesForStudentOne.get() + 1);
            votingResultsStudent1.setText(String.valueOf(votesForStudentOne.get()));
        });

        castVoteStudent2Button.setOnClickListener(view -> {
            votesForStudentTwo.set(votesForStudentTwo.get() + 1);
            votingResultsStudent2.setText(String.valueOf(votesForStudentTwo.get()));
        });

    }
}