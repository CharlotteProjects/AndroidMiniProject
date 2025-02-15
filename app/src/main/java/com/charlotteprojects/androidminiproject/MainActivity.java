package com.charlotteprojects.androidminiproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
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
import java.util.Locale;
import java.util.Objects;

// This is a Singleton , for other page get set Firebase data
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private boolean doubleTap = false;

    // For Debug
    public static final String TAG = "DebugLog";

    //region For Message
    public static final String SEARCH_WORD = "searchWord";
    public static final String ITEM_NAME = "itemName";
    public static final String ITEM_PRICE = "itemPrice";
    public static final String ADDRESS_LATITUDE = "addressLatitude";
    public static final String ADDRESS_LONGITUDE = "addressLongitude";
    public static final String SHOP_NAME = "shopName";
    public static final String ITEM_URL = "itemURL";
    //endregion

    //region Init Firebase
    public static FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    public static FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    public static FirebaseAuth firebaseAuth  = FirebaseAuth.getInstance();
    public static FirebaseUser firebaseUser;
    //endregion

    //region List for store the firebase data
    public static List<String> itemNameList = new ArrayList<>();
    public static List<String> itemPriceList = new ArrayList<>();
    public static List<String> itemImageURL = new ArrayList<>();
    public static List<String> itemEmailList = new ArrayList<>();
    public static List<String> itemShopNameList = new ArrayList<>();
    public static List<String> itemLatitudeList = new ArrayList<>();
    public static List<String> itemLongitudeList = new ArrayList<>();
    public static List<User> userList = new ArrayList<>();
    //endregion

    // Store user data
    public static User myProfile;

    private Button buttonStart, buttonLogin, buttonLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // set language
        int i = getSharedPreferences("WhereShop",MODE_PRIVATE).getInt("language",0);
        if(i != 0)
            SetLanguage(Locale.TRADITIONAL_CHINESE,false);
        else
            SetLanguage(Locale.ENGLISH,false);

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

        // init Ads
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(@NotNull InitializationStatus initializationStatus) {
                Log.i(MainActivity.TAG,"Ads init completed");
            }
        });

        // init Website
        TextView link = (TextView) findViewById(R.id.main_text);
        String linkText = "<a href='https://charlotteprojects.github.io/'>Website</a>";
        link.setText(Html.fromHtml(linkText));
        link.setMovementMethod(LinkMovementMethod.getInstance());

        // init ImageButton
        ImageButton imageButton = (ImageButton) findViewById(R.id.main_imageButton);
        imageButton.setOnClickListener(this);

        ImageButton imageButtonEmail = (ImageButton) findViewById(R.id.main_imageButtonEmail);
        imageButtonEmail.setOnClickListener(this);

        ImageButton imageButtonLanguage = (ImageButton) findViewById(R.id.main_imageButtonLanguage);
        imageButtonLanguage.setOnClickListener(this);
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

            case R.id.main_imageButton:

                break;
            case R.id.main_imageButtonEmail:
                Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);

                emailIntent.setType("plain/text");

                emailIntent.putExtra(
                        android.content.Intent.EXTRA_EMAIL,
                        new String[] { "Admin@WhereShop.com" });

                emailIntent.putExtra(
                        android.content.Intent.EXTRA_SUBJECT,
                        "Email Subject");

                emailIntent.putExtra(
                        android.content.Intent.EXTRA_TEXT,
                        "Your Content");


                startActivity(Intent.createChooser(
                        emailIntent,
                        "Send mail..."));

                break;
            case R.id.main_imageButtonLanguage:

                AlertDialog.Builder dialog_login = new AlertDialog.Builder(MainActivity.this);
                dialog_login.setTitle(R.string.alertDialog_message);
                dialog_login.setMessage(R.string.alertDialog_Language);
                dialog_login.setPositiveButton("中文",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        // save
                        getSharedPreferences("WhereShop", MODE_PRIVATE).edit().putInt("language", 1).apply();

                        SetLanguage(Locale.TRADITIONAL_CHINESE,true);
                    }
                });

                dialog_login.setNegativeButton("English",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        // save
                        getSharedPreferences("WhereShop", MODE_PRIVATE).edit().putInt("language", 0).apply();

                        SetLanguage(Locale.ENGLISH, true);
                    }
                });
                dialog_login.show();

                break;
        }
    }

    // Set the System Language
    private void SetLanguage(Locale localeLanguage, boolean refresh){
        Resources resources = getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        Configuration config = resources.getConfiguration();
        config.locale = localeLanguage;
        resources.updateConfiguration(config, dm);

        // refresh Activity Layout
        if(refresh){
            Intent intent = new Intent(MainActivity.this, MainActivity.class);
            finish();
            startActivity(intent);
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
