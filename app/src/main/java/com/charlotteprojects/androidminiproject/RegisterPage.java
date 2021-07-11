package com.charlotteprojects.androidminiproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class RegisterPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_page);

        //region Init Register UI and Function
        // init UI and onClick Listener
        EditText editEmail = (EditText) findViewById(R.id.register_edit_email);
        EditText editPW =  (EditText) findViewById(R.id.register_edit_pw);
        EditText editPWConfirm = (EditText) findViewById(R.id.register_edit_pwAgain);
        EditText editShopName = (EditText) findViewById(R.id.register_edit_name);
        LinearLayout linearLayoutRegisterGroup = (LinearLayout) findViewById(R.id.register_registerGroup);
        TextView textSuccsee = (TextView) findViewById(R.id.register_text_success);

        Button buttonConfirm = (Button) findViewById(R.id.register_button_register);
        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the input String
                String email = editEmail.getText().toString();
                String PW = editPW.getText().toString();
                String PWConfirm = editPWConfirm.getText().toString();
                String shopName = editShopName.getText().toString();

                // Check the input String is not empty.
                if(email.isEmpty()){
                    Toast.makeText(RegisterPage.this,R.string.toast_inputEmail,Toast.LENGTH_LONG).show();
                    return;
                }

                // Check the email is used or not
                if(MainActivity.CheckEmailHaveBeSignUp(email)){
                    Toast.makeText(RegisterPage.this,R.string.toast_emailHaveBeSignUp,Toast.LENGTH_LONG).show();
                    return;
                }
                if(PW.isEmpty()){
                    Toast.makeText(RegisterPage.this,R.string.toast_inputPW,Toast.LENGTH_LONG).show();
                    return;
                }

                // Check the Confirm PW is empty or not match
                if(PWConfirm.isEmpty() ||
                !PW.equals(PWConfirm)){
                    Toast.makeText(RegisterPage.this,R.string.toast_inputPWConfirm,Toast.LENGTH_LONG).show();
                    return;
                }
                if(shopName.isEmpty()){
                    Toast.makeText(RegisterPage.this,R.string.toast_inputShopName,Toast.LENGTH_LONG).show();
                    return;
                }

                Map<String, String> userData = new HashMap<>();
                userData.put("id", email);
                userData.put("name", shopName);
                userData.put("pw", PW);

                MainActivity.firestore.collection("account")
                        .add(userData)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                MainActivity.SetMyId(documentReference.getId());

                                // Hide the EditText Selected
                                editEmail.setSelected(false);
                                editPW.setSelected(false);
                                editPWConfirm.setSelected(false);
                                editShopName.setSelected(false);

                                // Hide the Keyboard
                                View view = RegisterPage.this.getCurrentFocus();
                                if(view != null){
                                    InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                                    inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(),0);
                                }

                                linearLayoutRegisterGroup.setVisibility(View.GONE);
                                buttonConfirm.setVisibility(View.GONE);
                                textSuccsee.setText(R.string.toast_registerSuccess);

                                Log.i(MainActivity.TAG,
                                        "email : "+ email +
                                        ", pw : " + PW +
                                        ", Shop Name : " + shopName +
                                        ", ID : " + documentReference.getId());

                                // After second go to next Manager Page
                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent intent = new Intent(RegisterPage.this, ManagerPage.class);
                                        startActivity(intent);
                                    }
                                }, 5000);
                            }
                        })

                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e(MainActivity.TAG,"Upload Firebase Error : ", e);
                            }
                        });
            }
        });

        //endregion
    }
}