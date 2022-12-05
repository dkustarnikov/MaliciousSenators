package com.malicioussenators;

import static com.malicioussenators.CONSTANTS.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.security.KeyStore;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DisplayModuleActivity extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_module);

        readFromDataStore("RegistrarDataStore");
    }

    Map<String, Object> readFromDataStore(String dataStoreName) {
        //Check if the dataStoreName is one of the good ones

        FirebaseApp.initializeApp(this);
        DatabaseReference mDatabase;
        mDatabase = FirebaseDatabase.getInstance().getReference();

        Map<String, Object> returnMap = new HashMap<String, Object>();

        Log.e("TAG", "Inside the readFromDataStore");

        db.collection(dataStoreName)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                List<DocumentSnapshot> documents = task.getResult().getDocuments();
                for (DocumentSnapshot document : documents) {
                    returnMap.put(document.getId(), document.getData());
                }
            }
        });

        return returnMap;
    }
}