package com.malicioussenators;

import android.content.Context;
import android.util.Log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataHelper {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public DataHelper(FirebaseFirestore db) {
        this.db = db;
    }

    public DataHelper() {
    }

    public FirebaseFirestore getDb() {
        return db;
    }

    public void setDb(FirebaseFirestore db) {
        this.db = db;
    }

    public void addObjectToDataStore(String dataStoreName, Object object) {
        db.collection(dataStoreName).add(object);
    }

    public Map<String, Object> getAllAsMapFromDataStore(String dataStoreName) {
        //Check if the dataStoreName is one of the good ones
        Map<String, Object> returnMap = new HashMap<String, Object>();
        FirebaseFirestore methodDb = FirebaseFirestore.getInstance();

        methodDb.collection(dataStoreName)
                .get().addOnCompleteListener(task -> {
            List<DocumentSnapshot> documents = task.getResult().getDocuments();
            for (DocumentSnapshot document : documents) {
                returnMap.put(document.getId(), document.getData());
            }
        });
        return returnMap;
    }

    public ArrayList<String> convertMapToArrayOfJsons(Map<String, Object> map) {
        ArrayList<String> returnArray = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();
        for (Map.Entry<String, Object> stringObjectEntry : map.entrySet()) {
            try {
                String json = objectMapper.writeValueAsString(stringObjectEntry.getValue());
                returnArray.add(json);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
        return returnArray;
    }

    //This method will create a text file and name it whatever the parameter is
    public void createTextFile(String fileName, Context context) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(fileName, Context.MODE_PRIVATE));
//            outputStreamWriter.append("This is some new data");
            outputStreamWriter.close();

        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    //This method writes to a text file whatever data is provided (it appends data, doesn't overwrite it)
    public void writeToTextFile(String fileName, String data, Context context) {
        try {
            Path path = Paths.get("/data/user/0/com.malicioussenators/files/" + fileName);
            Files.write(path, data.getBytes(), StandardOpenOption.APPEND);
            Files.write(path, "\n".getBytes(), StandardOpenOption.APPEND);
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

}
