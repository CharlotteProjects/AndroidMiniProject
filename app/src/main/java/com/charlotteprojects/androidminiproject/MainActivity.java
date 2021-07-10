package com.charlotteprojects.androidminiproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    // For Debug
    public static final String TAG = "DebugLog";

    // List for store the firebase data
    public static List<String> itemNameList = new ArrayList<>();
    public static List<String> itemPriceList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //region Set up the search page
        Button buttonStart = findViewById(R.id.main_button_start);
        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SearchPage.class);
                startActivity(intent);
            }
        });
        //endregion

    }
}
