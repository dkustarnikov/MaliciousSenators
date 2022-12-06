package com.malicioussenators;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.widget.Button;

import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    Button submitApplicationRouteButton;
    Button selectWinnerRouteButton;
    Button displayModuleButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        submitApplicationRouteButton = (Button) findViewById(R.id.submitApplicationScreenButton);
        selectWinnerRouteButton = (Button) findViewById(R.id.pickWinnerScreenButton);
        displayModuleButton = (Button) findViewById(R.id.displayModuleButton);

        submitApplicationRouteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =  new Intent(MainActivity.this, SubmitApplicationActivity.class);
                startActivity(intent);
            }
        });

        selectWinnerRouteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =  new Intent(MainActivity.this, SelectWinnerApplication.class);
                startActivity(intent);
            }
        });

        displayModuleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =  new Intent(MainActivity.this, DisplayModuleActivity.class);
                startActivity(intent);
            }
        });


        FirebaseApp.initializeApp(this);


    }
}