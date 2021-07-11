package com.charlotteprojects.androidminiproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Application;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Objects;

public class LoginPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        //region Set Login
        EditText editID = (EditText) findViewById(R.id.login_edit_id);
        EditText editPW = (EditText) findViewById(R.id.login_edit_pw);

        Button buttonLogin = (Button) findViewById(R.id.login_button_login);
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ID = editID.getText().toString();
                String PW = editPW.getText().toString();

                if(ID.isEmpty() || PW.isEmpty()){

                    Toast.makeText(LoginPage.this,R.string.toast_login,Toast.LENGTH_SHORT).show();

                } else {

                    //! If Building must be hide
                    Log.i(MainActivity.TAG, "Input ID : "+ ID + ", PW : "+ PW);

                    if(MainActivity.LoginAccount(ID, PW)){
                        Toast.makeText(LoginPage.this,R.string.toast_loginSuccess,Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(LoginPage.this,R.string.toast_loginFail,Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        //endregion

    }
}