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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

// This is a Singleton , for other page get set Firebase data
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    // For Debug
    public static final String TAG = "DebugLog";

    // Init Firebase
    public static FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    public static FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    public static FirebaseAuth firebaseAuth  = FirebaseAuth.getInstance();
    public static FirebaseUser firebaseUser;

    // List for store the firebase data
    public static List<String> itemNameList = new ArrayList<>();
    public static List<String> itemPriceList = new ArrayList<>();

    // Store user data
    public static User myProfile;

    // Login ID & PW
    /*
    private static List<String> UserIDList = new ArrayList<>();
    private static List<String> loginIDList = new ArrayList<>();
    private static List<String> loginPWList = new ArrayList<>();
    private static List<String> UserNameList = new ArrayList<>();
    private static String myId;
    private static boolean isLogined = false;

     */

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

            // When have login record then init the user profile
            MainActivity.firebaseDatabase.getReference("Users")
                    .child(MainActivity.firebaseUser.getUid())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                            MainActivity.myProfile = snapshot.getValue(User.class);
                            Log.i(MainActivity.TAG,"Get User Profile Success at Start app");
                        }

                        @Override
                        public void onCancelled(@NonNull @NotNull DatabaseError error) {

                        }
                    });
        }

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
}
