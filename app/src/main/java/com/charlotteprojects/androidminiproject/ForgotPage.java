package com.charlotteprojects.androidminiproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.jetbrains.annotations.NotNull;

public class ForgotPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_page);

        TextView textTitle = (TextView) findViewById(R.id.forgot_text_title);
        TextView textContent = (TextView) findViewById(R.id.forgot_text_content);

        EditText editEmail = (EditText) findViewById(R.id.forgot_edit);

        Button buttonSend = (Button) findViewById(R.id.forgot_button);
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editEmail.getText().toString();

                if(email.isEmpty()){
                    editEmail.setError(getResources().getString(R.string.toast_registerEmail));
                    editEmail.requestFocus();
                    Toast.makeText(ForgotPage.this,R.string.toast_registerEmail,Toast.LENGTH_LONG).show();
                    return;
                }

                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    editEmail.setError(getResources().getString(R.string.toast_registerCorrectEmail));
                    editEmail.requestFocus();
                    Toast.makeText(ForgotPage.this, R.string.toast_registerCorrectEmail, Toast.LENGTH_LONG).show();
                    return;
                }

                MainActivity.firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Log.i(MainActivity.TAG,"Send a email to " + email + " Reset password.");
                            textTitle.setText(R.string.forget_text_titleRe);
                            String content = getResources().getString(R.string.forgot_text_contenteRe) +
                                    email + getResources().getString(R.string.forgot_text_contenteRe2);
                            textContent.setText(content);
                        }
                    }
                });
            }
        });
    }
}