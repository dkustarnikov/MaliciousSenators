package com.malicioussenators;

import android.content.Context;
import android.util.Log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Map;

//Program Name: DataHelper
//Programmer Name: Dmitry Kustarnikov
//Description: This is a helper class to work with some data
//Date Created: 12/5/2022


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


    //Description: Converts a map into an array of Jsons
    //Pre-condition: Map<String, Object>
    //Post-condition: ArrayList<String> - Each entry is a Json-formatted string
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

    //Description: Create a text file
    //Pre-condition: Filename and the activity context
    //Post-condition: A text file is created

    //This method will create a text file and name it whatever the parameter is
    public void createTextFile(String fileName, Context context) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(fileName, Context.MODE_PRIVATE));
            outputStreamWriter.close();

        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    //Description: Writes to a file any data provided (A line at a time)
    //Pre-condition: a file with fileName exists
    //Post-condition: data is written to the given file

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
