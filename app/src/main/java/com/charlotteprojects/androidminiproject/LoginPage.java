package com.charlotteprojects.androidminiproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginPage extends AppCompatActivity implements View.OnClickListener{

    private EditText editID, editPW;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

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

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.login_button_login:
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
                break;
            case R.id.login_button_registered:
                Intent intent = new Intent(LoginPage.this, RegisterPage.class);
                startActivity(intent);
                break;
        }
    }
}