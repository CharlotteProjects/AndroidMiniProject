package com.charlotteprojects.androidminiproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

import org.jetbrains.annotations.NotNull;

public class LoginPage extends AppCompatActivity implements View.OnClickListener{

    private EditText editID, editPW;
    private ProgressBar progressBar;

    private AlertDialog.Builder dialog_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        progressBar = (ProgressBar) findViewById(R.id.login_progressBar);
        progressBar.setVisibility(View.GONE);

        //region init Button onClick function

        editID = (EditText) findViewById(R.id.login_edit_id);
        editPW = (EditText) findViewById(R.id.login_edit_pw);

        Button buttonLogin = (Button) findViewById(R.id.login_button_login);
        buttonLogin.setOnClickListener(this);

        Button buttonRegister = (Button) findViewById(R.id.login_button_registered);
        buttonRegister.setOnClickListener(this);

        Button buttonForgot = (Button) findViewById(R.id.login_button_forgot);
        buttonForgot.setOnClickListener(this);

        //endregion

        //region init AlertDialog
        dialog_login= new AlertDialog.Builder(LoginPage.this);
        dialog_login.setTitle(R.string.alertDialog_loginSuccess);
        dialog_login.setMessage(R.string.alertDialog_loginWelcome);
        dialog_login.setPositiveButton("OK",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                Intent intent = new Intent(LoginPage.this, ManagerPage.class);
                startActivity(intent);
            }
        });

        //endregion

    }

    // Set the OnClickListener to each button
    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()){
            case R.id.login_button_login:
                String email = editID.getText().toString();
                String password = editPW.getText().toString();

                // Check Email
                if(email.isEmpty()){
                    editID.setError(getResources().getString(R.string.toast_registerEmail));
                    editID.requestFocus();
                    Toast.makeText(LoginPage.this,R.string.toast_registerEmail,Toast.LENGTH_LONG).show();
                    return;
                }

                // Check Password
                if(password.isEmpty()){
                    editPW.setError(getResources().getString(R.string.toast_registerPW));
                    editID.requestFocus();
                    Toast.makeText(LoginPage.this,R.string.toast_registerPW,Toast.LENGTH_LONG).show();
                    return;
                }

                Log.i(MainActivity.TAG, "Input ID : "+ email);

                progressBar.setVisibility(View.VISIBLE);

                // Login Account
                MainActivity.firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            MainActivity.firebaseUser = MainActivity.firebaseAuth.getCurrentUser();

                            assert MainActivity.firebaseUser != null;
                            Log.i(MainActivity.TAG,"Login success, ID : "+ MainActivity.firebaseUser.getUid() + ", Email : "+ MainActivity.firebaseUser.getEmail());
                            progressBar.setVisibility(View.GONE);
                            dialog_login.show();

                        } else {
                            Log.i(MainActivity.TAG,"Login Fail");
                            Toast.makeText(LoginPage.this,R.string.toast_loginFail,Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
                break;

            case R.id.login_button_registered:
                intent = new Intent(LoginPage.this, RegisterPage.class);
                startActivity(intent);
                break;
            case R.id.login_button_forgot:
                intent = new Intent(LoginPage.this, ForgotPage.class);
                startActivity(intent);
                break;
        }
    }
}