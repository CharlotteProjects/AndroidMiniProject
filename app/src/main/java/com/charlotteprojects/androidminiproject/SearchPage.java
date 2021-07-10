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

/*
價錢參考的網 :
https://www.hangtatproducts.com.hk/?catcode=cat_1005&page=productcat
 */

public class SearchPage extends AppCompatActivity {


    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private EditText editInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_page);

        //region Firebase get item data and set the List<String>
        firestore.collection("item")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull @org.jetbrains.annotations.NotNull Task<QuerySnapshot> task) {

                        // Clear the list because every time in this page will get again
                        MainActivity.itemNameList.clear();
                        MainActivity.itemPriceList.clear();

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
                                            MainActivity.itemNameList.get(index)+" is $ : " + MainActivity.itemPriceList.get(index),
                                            task.getException()
                                    );
                                    index++;
                                }
                            }

                            SetItemListView();
                        } else {
                        Log.e(MainActivity.TAG, "Error Can not get data.", task.getException());
                    }
                }
            });

        //endregion

        //region Set the Search Button
        editInput = findViewById(R.id.search_editText);
        Button buttonSearch = findViewById(R.id.search_button_search);
        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputString = editInput.getText().toString();
                if(!inputString.equals("")){
                    Toast.makeText(SearchPage.this,inputString, Toast.LENGTH_SHORT).show();
                } else{
                    Toast.makeText(SearchPage.this,R.string.search_toast, Toast.LENGTH_SHORT).show();
                }
            }
        });
        //endregion
    }

    // After Get Firebase Data, then create item list view
    private void SetItemListView(){
        ListView listView = (ListView) findViewById(R.id.search_listview);

        List<HashMap<String, Object>> myList = new ArrayList<HashMap<String, Object>>();

        for(int i = 0;i < MainActivity.itemNameList.size() ; i++){
            HashMap<String, Object> item = new HashMap<String, Object>();
            item.put("name", MainActivity.itemNameList.get(i));
            item.put("price", MainActivity.itemPriceList.get(i));
            // item.put("image",imageAddress[i]);
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