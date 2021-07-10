package com.charlotteprojects.androidminiproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

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

        //region Firebase get account data
        MainActivity.firestore.collection("account")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull @org.jetbrains.annotations.NotNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {
                            int index = 0;
                            for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {

                                String json = document.getData().toString();

                                String[] array = json.split(",");

                                // Set the data the item list and display to log
                                if(array.length > 0){
                                    String itemName = array[1].substring(6, array[1].length()-1);
                                    String itemPrice = array[0].substring(7);

                                    MainActivity.itemNameList.add(itemName);
                                    MainActivity.itemPriceList.add(itemPrice);

                                    Log.i(MainActivity.TAG,
                                            "[" + MainActivity.itemNameList.get(index)+"] is $ : " + MainActivity.itemPriceList.get(index),
                                            task.getException()
                                    );
                                    index++;
                                }
                            }

                        } else {
                            Log.e(MainActivity.TAG, "Error Can not get account data.", task.getException());
                        }
                    }
                });

        //endregion
    }
}