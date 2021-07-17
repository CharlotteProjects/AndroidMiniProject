package com.charlotteprojects.androidminiproject;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ManagerPage extends AppCompatActivity implements View.OnClickListener {

    private EditText edit_latitude, edit_longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_page);

        // init the Name
        TextView textUserName = (TextView) findViewById(R.id.manage_text_userName);
        TextView textShopName = (TextView) findViewById(R.id.manage_text_shopName);

        textUserName.setText(MainActivity.myProfile.userName);
        textShopName.setText(MainActivity.myProfile.shopName);

        edit_latitude = (EditText) findViewById(R.id.manage_edit_X);
        edit_longitude = (EditText) findViewById(R.id.manage_edit_Y);

        Button buttonAddAddress = (Button) findViewById(R.id.manage_button_addAddress);
        buttonAddAddress.setOnClickListener(this);

        Button buttonMyAddress = (Button) findViewById(R.id.manage_button_myAddress);
        buttonMyAddress.setOnClickListener(this);

        Button buttonAddItem =(Button) findViewById(R.id.manage_button_addItem);
        buttonAddItem.setOnClickListener(this);
    }

    // init OnClick Button
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.manage_button_addAddress:

                String latitude = edit_latitude.getText().toString();
                String longitude = edit_longitude.getText().toString();

                // Check the input is empty or not
                if(latitude.isEmpty() || longitude.isEmpty() ||
                        Double.parseDouble(latitude) > 180 || Double.parseDouble(latitude) < -180  ||
                        Double.parseDouble(longitude) > 180 || Double.parseDouble(longitude) < -180 ){
                    Toast.makeText(ManagerPage.this,R.string.toast_manage_geo,Toast.LENGTH_LONG).show();
                    edit_latitude.setSelected(false);
                    edit_longitude.setSelected(false);
                    return;
                }

                MainActivity.myProfile.SetAddress(latitude, longitude);

                MainActivity.UploadMyProfile();

                AlertDialog.Builder dialog= new AlertDialog.Builder(ManagerPage.this);
                dialog.setTitle(R.string.alertDialog_message);
                dialog.setMessage(R.string.alertDialog_address);
                dialog.setPositiveButton("OK",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                    }
                });
                dialog.show();

                break;

            case R.id.manage_button_myAddress:

                if(MainActivity.myProfile.latitude.isEmpty() || MainActivity.myProfile.longitude.isEmpty()){
                    Toast.makeText(ManagerPage.this,R.string.toast_manage_myAddressEmpty,Toast.LENGTH_LONG).show();
                    return;
                } else {
                    Intent intent = new Intent(ManagerPage.this, MyShopAddress.class);
                    startActivity(intent);
                }
                break;

            case R.id.manage_button_addItem:
                Intent intent = new Intent(ManagerPage.this, AddItemPage.class);
                startActivity(intent);
                break;
        }
    }
}