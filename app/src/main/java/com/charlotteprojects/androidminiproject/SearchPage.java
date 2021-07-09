package com.charlotteprojects.androidminiproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Objects;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SearchPage extends AppCompatActivity {

    private static final String TAG = "SearchPage";
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    private EditText editInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_page);

        //init the container
        editInput = findViewById(R.id.search_editText);
        Button buttonSearch = findViewById(R.id.search_button_search);

        firestore.collection("item")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull @org.jetbrains.annotations.NotNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                Log.d(TAG, document.getId() + " is " + document.getData());
                            }
                        } else {
                        Log.e(TAG, "Error getting documents.", task.getException());
                    }
                }
            });


        // Set the Search Button
        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputString = editInput.getText().toString();
                if(!inputString.equals("")){
                    Log.e("SearchPage", "User input " + inputString);
                } else{
                    Toast.makeText(SearchPage.this,R.string.search_toast, Toast.LENGTH_SHORT).show();
                }
            }
        });

        LoadingItemList();
    }

    private void LoadingItemList(){
        ListView listView = (ListView) findViewById(R.id.search_listview);

        String[] itemName = {"物件一","物件二","物件三","物件四","物件五","物件六"};
        String[] itemPrice = {"100","120","160","80","60","180"};
        List<HashMap<String, Object>> myList = new ArrayList<HashMap<String, Object>>();


        for(int i = 0;i < itemName.length ; i++){
            HashMap<String, Object> item = new HashMap<String, Object>();
            item.put("name", itemName[i]);
            item.put("price", itemPrice[i]);
            //item.put("image",imageAddress[i]);
            myList.add(item);
        }

        SimpleAdapter adapter = new SimpleAdapter(
                this,
                myList,
                R.layout.item_page,
                new String[]{"name","price"},
                new int[]{R.id.list_text, R.id.list_price}
        );

        listView.setAdapter(adapter);
    }
}