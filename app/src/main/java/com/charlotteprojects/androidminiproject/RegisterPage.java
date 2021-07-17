package com.charlotteprojects.androidminiproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

import org.jetbrains.annotations.NotNull;

public class RegisterPage extends AppCompatActivity {

    private ProgressBar progressBar;

    private AlertDialog.Builder dialog_register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_page);

        progressBar = (ProgressBar) findViewById(R.id.register_progressBar);
        progressBar.setVisibility(View.GONE);

        EditText editEmail = (EditText) findViewById(R.id.register_edit_email);
        EditText editPW =  (EditText) findViewById(R.id.register_edit_pw);
        EditText editPWConfirm = (EditText) findViewById(R.id.register_edit_pwAgain);
        EditText editUserName = (EditText) findViewById(R.id.register_edit_userName);
        EditText editShopName = (EditText) findViewById(R.id.register_edit_shopName);

        Button buttonConfirm = (Button) findViewById(R.id.register_button_register);
        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //region Get the input String & Check empty
                String email = editEmail.getText().toString();
                String PW = editPW.getText().toString();
                String PWConfirm = editPWConfirm.getText().toString();
                String userName = editUserName.getText().toString();
                String shopName = editShopName.getText().toString();

                // Check the input String is not empty and correct email
                if(email.isEmpty()){
                    editEmail.setError(getResources().getString(R.string.toast_registerEmail));
                    editEmail.requestFocus();
                    Toast.makeText(RegisterPage.this,R.string.toast_registerEmail,Toast.LENGTH_LONG).show();
                    return;
                } else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    editEmail.setError(getResources().getString(R.string.toast_registerCorrectEmail));
                    editEmail.requestFocus();
                    Toast.makeText(RegisterPage.this,R.string.toast_registerCorrectEmail,Toast.LENGTH_LONG).show();
                    return;
                }

                // Check the email is used or not
                /*
                if(MainActivity.CheckEmailHaveBeSignUp(email)){
                    Toast.makeText(RegisterPage.this,R.string.toast_registerEmail,Toast.LENGTH_LONG).show();
                    return;
                }

                 */

                // Check the Confirm PW is empty or not match
                if(PW.isEmpty()){
                    editPW.setError(getResources().getString(R.string.toast_registerPW));
                    editPW.requestFocus();
                    Toast.makeText(RegisterPage.this,R.string.toast_registerPW,Toast.LENGTH_LONG).show();
                    return;
                } else if(PW.length() < 6){
                    editPW.setError(getResources().getString(R.string.toast_registerPWSix));
                    editPW.requestFocus();
                    Toast.makeText(RegisterPage.this,R.string.toast_registerPWSix,Toast.LENGTH_LONG).show();
                    return;
                }

                // Check the Confirm PW is empty or not match
                if(PWConfirm.isEmpty() ||
                !PW.equals(PWConfirm)){
                    editPWConfirm.setError(getResources().getString(R.string.toast_registerCorrectEmail));
                    editPWConfirm.requestFocus();
                    Toast.makeText(RegisterPage.this,R.string.toast_registerCorrectEmail,Toast.LENGTH_LONG).show();
                    return;
                }

                if(userName.isEmpty()){
                    editUserName.setError(getResources().getString(R.string.toast_registerShopName));
                    editUserName.requestFocus();
                    Toast.makeText(RegisterPage.this,R.string.toast_registerUserName,Toast.LENGTH_LONG).show();
                    return;
                }

                if(shopName.isEmpty()){
                    editShopName.setError(getResources().getString(R.string.toast_registerShopName));
                    editShopName.requestFocus();
                    Toast.makeText(RegisterPage.this,R.string.toast_registerShopName,Toast.LENGTH_LONG).show();
                    return;
                }

                //endregion

                // Create Firebase Account by Email
                progressBar.setVisibility(View.VISIBLE);
                MainActivity.firebaseAuth.createUserWithEmailAndPassword(email, PW)
                        .addOnCompleteListener(RegisterPage.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {

                                    MainActivity.firebaseUser = MainActivity.firebaseAuth.getCurrentUser();

                                    assert MainActivity.firebaseUser != null;
                                    Log.i(MainActivity.TAG, "Create user with Email : success, ID : " + MainActivity.firebaseUser.getUid());

                                    MainActivity.myProfile = new User(userName, email, shopName);
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

                                    MainActivity.firebaseUser.sendEmailVerification();
                                    Log.i(MainActivity.TAG,"Sent a confirm Email to user.");

                                    dialog_register.show();
                                } else {
                                    Log.e(MainActivity.TAG, "Create user with Email : failure", task.getException());
                                    Toast.makeText(RegisterPage.this,R.string.toast_registerFail,Toast.LENGTH_LONG).show();
                                }
                                progressBar.setVisibility(View.GONE);
                            }
                        });
            }
        });


        //region init AlertDialog

        dialog_register = new AlertDialog.Builder(RegisterPage.this);
        dialog_register.setTitle(R.string.alertDialog_registerSuccess);
        dialog_register.setMessage(R.string.alertDialog_registerWelcome);
        dialog_register.setPositiveButton("OK",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                Intent intent = new Intent(RegisterPage.this, ManagerPage.class);
                startActivity(intent);
            }
        });

        //endregion
    }
}