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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

// This is a Singleton , for other page get set Firebase data
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    // For Debug
    public static final String TAG = "DebugLog";

    // Init Firebase
    public static FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    public static FirebaseAuth firebaseAuth  = FirebaseAuth.getInstance();
    public static FirebaseUser firebaseUser;

    // List for store the firebase data
    public static List<String> itemNameList = new ArrayList<>();
    public static List<String> itemPriceList = new ArrayList<>();

    // Login ID & PW
    private static List<String> UserIDList = new ArrayList<>();
    private static List<String> loginIDList = new ArrayList<>();
    private static List<String> loginPWList = new ArrayList<>();
    private static List<String> UserNameList = new ArrayList<>();
    private static String myId;
    private static boolean isLogined = false;

    private Button buttonStart, buttonLogin, buttonLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseUser = MainActivity.firebaseAuth.getCurrentUser();

        //region init Button onClick and display
        buttonStart = findViewById(R.id.main_button_start);
        buttonStart.setOnClickListener(this);

        buttonLogin = findViewById(R.id.main_button_login);
        buttonLogin.setOnClickListener(this);

        buttonLogout = findViewById(R.id.main_button_logout);
        buttonLogout.setOnClickListener(this);

        if(firebaseUser == null){
            Log.i(TAG, "No one login.");
            buttonLogin.setText(getResources().getString(R.string.main_button_login));
            buttonLogout.setVisibility(View.GONE);
        } else {
            Log.i(TAG, "Had login, ID : " + firebaseUser.getUid() + ", Email : " + firebaseUser.getEmail());
            buttonLogin.setText(getResources().getString(R.string.main_button_manager));
            buttonLogout.setVisibility(View.VISIBLE);
        }

        //endregion

        //region Firebase get account data, because program need to check login when start app
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

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()){
            case R.id.main_button_start:
                intent = new Intent(MainActivity.this, SearchPage.class);
                startActivity(intent);
                break;

            case R.id.main_button_login:
                if(firebaseUser == null){
                    intent = new Intent(MainActivity.this, LoginPage.class);
                } else {
                    intent = new Intent(MainActivity.this, ManagerPage.class);
                }
                startActivity(intent);
                break;

            case R.id.main_button_logout:
                FirebaseAuth.getInstance().signOut();
                Log.i(TAG,firebaseUser.getEmail() + " had logout...");
                firebaseUser = null;

                // hide Logout Button
                buttonLogin.setText(getResources().getString(R.string.main_button_login));
                buttonLogout.setVisibility(View.GONE);

                break;
        }
    }

    // Set my sign in ID
    public static void SetMyId(String id){
        myId = id;
    }

    // Login Function Check the ID and PW
    public static Boolean LoginAccount(String inputID, String inputPW){

        // Check the ID & PW
        for(int i = 0; i < loginIDList.size(); i++){
            if(inputID.equals(loginIDList.get(i)) && inputPW.equals(loginPWList.get(i))){
                isLogined = true;
                Log.i(TAG,"The user id is "+ UserIDList.get(i));
                return true;
            }
        }
        Log.i(TAG,"Login Fail.");
        isLogined = false;
        return false;
    }

    // For user sign up check email
    public static Boolean CheckEmailHaveBeSignUp(String email){
            for(String id : loginIDList){
                if(id.equals(email))
                    return true;
            }
            return false;
    }
}
