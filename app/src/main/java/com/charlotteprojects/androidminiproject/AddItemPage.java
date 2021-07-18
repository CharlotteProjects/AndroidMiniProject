package com.charlotteprojects.androidminiproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;

import java.util.HashMap;
import java.util.Map;

public class AddItemPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item_page);

        EditText editItemName = (EditText) findViewById(R.id.addItem_edit_name);
        EditText editItemPrice = (EditText) findViewById(R.id.addItem_edit_price);

        Button buttonAdd = (Button) findViewById(R.id.addItem_button_add);
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String itemName = editItemName.getText().toString();
                String itemPrice = editItemPrice.getText().toString();

                // Check the input is empty or not
                if(itemName.isEmpty() || itemPrice.isEmpty() || Double.parseDouble(itemPrice) <= 0){
                    Toast.makeText(AddItemPage.this,R.string.toast_addItem_input,Toast.LENGTH_LONG).show();
                    editItemName.setSelected(false);
                    editItemPrice.setSelected(false);
                    return;
                }

                Map<String, Object> user = new HashMap<>();
                user.put("name", itemName);
                user.put("price", itemPrice);
                user.put("email", MainActivity.myProfile.userEmail);
                user.put("image", "-");

                // upload the new item to Firestore
                MainActivity.firestore.collection("item")
                        .add(user)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Log.i(MainActivity.TAG,
                                        "Add a new item : [" + itemName +"] is $ : " +
                                                itemPrice + ", Email :"+
                                                MainActivity.myProfile.userEmail);

                                // Display an AlertDialog
                                AlertDialog.Builder dialog= new AlertDialog.Builder(AddItemPage.this);
                                dialog.setTitle(R.string.alertDialog_message);

                                String st = getResources().getString(R.string.alertDialog_addItem) + itemName + "\n $ : " + itemPrice;
                                dialog.setMessage(st);
                                dialog.setPositiveButton("OK",new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface arg0, int arg1) {
                                        AddItemPage.this.onBackPressed();
                                    }
                                });
                                dialog.show();
                            }
                        })

                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(MainActivity.TAG, "Error to upload new item", e);
                            }
                        });
            }
        });
    }
}