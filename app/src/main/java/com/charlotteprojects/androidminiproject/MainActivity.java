package com.charlotteprojects.androidminiproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
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

    private boolean doubleTap = false;

    // For Debug
    public static final String TAG = "DebugLog";

    // For Message
    public static final String SEARCH_WORD = "searchWord";
    public static final String ITEM_NAME = "itemName";
    public static final String ITEM_PRICE = "itemPrice";
    public static final String ADDRESS_LATITUDE = "addressLatitude";
    public static final String ADDRESS_LONGITUDE = "addressLongitude";
    public static final String SHOP_NAME = "shopName";
    public static final String ITEM_URL = "itemURL";

    // Init Firebase
    public static FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    public static FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    public static FirebaseAuth firebaseAuth  = FirebaseAuth.getInstance();
    public static FirebaseUser firebaseUser;

    // List for store the firebase data
    public static List<String> itemNameList = new ArrayList<>();
    public static List<String> itemPriceList = new ArrayList<>();
    public static List<String> itemImageURL = new ArrayList<>();
    public static List<String> itemEmailList = new ArrayList<>();
    public static List<String> itemShopNameList = new ArrayList<>();
    public static List<String> itemLatitudeList = new ArrayList<>();
    public static List<String> itemLongitudeList = new ArrayList<>();
    public static List<User> userList = new ArrayList<>();

    // Store user data
    public static User myProfile;

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
            MainActivity.GetMyProfileFromFirebase();
        }

        //endregion

        //region download the user List when loading Main page
        userList.clear();
        firebaseDatabase.getReference().child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for(DataSnapshot ds : snapshot.getChildren()) {
                    userList.add(ds.getValue(User.class));
                }
                Log.d(TAG, "Get a User List success.");
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

        //endregion

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(@NotNull InitializationStatus initializationStatus) {
                Log.i(MainActivity.TAG,"Ads init completed");
            }
        });
    }

    //Double Click Exit
    @Override
    public void onBackPressed(){
        if(doubleTap){
            super.onBackPressed();
        } else {
            Toast.makeText(
                    this,
                    R.string.toast_exit,
                    Toast.LENGTH_SHORT).show();

            doubleTap = true;

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    doubleTap = false;
                }
            }, 500);
        }
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
                    // When on one login
                    intent = new Intent(MainActivity.this, LoginPage.class);
                } else {
                    // When some one login
                    intent = new Intent(MainActivity.this, ManagerPage.class);
                }
                startActivity(intent);
                break;

                // Logout account
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

    // Upload the user profile to Firebase
    public static void UploadMyProfile(){
        MainActivity.firebaseDatabase.getReference("Users")
                .child(MainActivity.firebaseUser.getUid())
                .setValue(MainActivity.myProfile).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<Void> task) {
                if(task.isSuccessful())
                    Log.i(MainActivity.TAG,"Upload user profile success !");
                else
                    Log.e(MainActivity.TAG,"Set data Failed !", task.getException());
            }
        });
    }

    // Download the user profile from Firebase
    public static void GetMyProfileFromFirebase(){
        MainActivity.firebaseDatabase.getReference("Users")
                .child(MainActivity.firebaseUser.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        MainActivity.myProfile = snapshot.getValue(User.class);
                        Log.i(MainActivity.TAG,"Get User Profile Success");
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {
                    }
                });
    }
}
