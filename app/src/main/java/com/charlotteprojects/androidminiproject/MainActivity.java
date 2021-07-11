package com.charlotteprojects.androidminiproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    // For Debug
    public static final String TAG = "DebugLog";

    // Init Firebase
    public static FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    // List for store the firebase data
    public static List<String> itemNameList = new ArrayList<>();
    public static List<String> itemPriceList = new ArrayList<>();

    // Login ID & PW
    private List<String> UserIDList = new ArrayList<>();
    private List<String> loginIDList = new ArrayList<>();
    private List<String> loginPWList = new ArrayList<>();
    private List<String> UserNameList = new ArrayList<>();
    private boolean isLogined = false;

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

        //region Set up the search page
        Button buttonLogin = findViewById(R.id.main_button_login);
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LoginPage.class);
                startActivity(intent);
            }
        });
        //endregion


        //region Firebase get account data because program need to check login when start app
        firestore.collection("account")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull @org.jetbrains.annotations.NotNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {
                            int index = 0;
                            for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {

                                String json = document.getData().toString();
                                String[] array = json.split(",");

                                // Set the data the item list and display to log
                                if(array.length > 0){
                                    UserIDList.add(document.getId());
                                    loginPWList.add(array[0].substring(4));
                                    UserNameList.add(array[1].substring(6));
                                    loginIDList.add(array[2].substring(4, array[2].length()-1));

                                    // For Check the data from Firebase
                                    //! When building App must be hide
                                    Log.i(MainActivity.TAG,
                                            UserIDList.get(index) + ", ID : " +
                                                    loginIDList.get(index) + ", PW : " +
                                                    loginPWList.get(index) + ", Name : " +
                                                    UserNameList.get(index),
                                            task.getException()
                                    );
                                    index++;
                                }
                            }

                        } else {
                            Log.e(MainActivity.TAG, "Error Can not get account data.", task.getException());
                        }
                    }
                });

        //endregion

    }

    // Input
    public Boolean LoginAccount(String id, String pw){
        return false;
    }
}
