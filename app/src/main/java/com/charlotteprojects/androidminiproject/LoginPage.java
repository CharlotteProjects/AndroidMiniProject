package com.charlotteprojects.androidminiproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        //region Set Login Button and Function
        EditText editID = (EditText) findViewById(R.id.login_edit_id);
        EditText editPW = (EditText) findViewById(R.id.login_edit_pw);

        Button buttonLogin = (Button) findViewById(R.id.login_button_login);
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ID = editID.getText().toString();
                String PW = editPW.getText().toString();

                if(ID.isEmpty() || PW.isEmpty()){

                    Toast.makeText(LoginPage.this,R.string.toast_login,Toast.LENGTH_LONG).show();

                } else {

                    //! If Building must be hide
                    Log.i(MainActivity.TAG, "Input ID : "+ ID + ", PW : "+ PW);

                    if(MainActivity.LoginAccount(ID, PW)){
                        Toast.makeText(LoginPage.this,R.string.toast_loginSuccess,Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(LoginPage.this,R.string.toast_loginFail,Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        //endregion

        //region Set link to Register Page
        Button buttonRegister = (Button) findViewById(R.id.login_button_registered);
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginPage.this, RegisterPage.class);
                startActivity(intent);
            }
        });
        //endregion
    }
}