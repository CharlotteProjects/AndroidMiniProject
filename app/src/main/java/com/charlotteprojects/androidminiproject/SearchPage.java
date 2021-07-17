package com.charlotteprojects.androidminiproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.OnCompleteListener;
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

    public static final String SEARCH_WORD = "0001";

    private EditText editInput;

    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_page);

        progressBar = (ProgressBar) findViewById(R.id.search_progressBar);

        //region Firebase get item data and set the List<String>
        MainActivity.firestore.collection("item")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull @org.jetbrains.annotations.NotNull Task<QuerySnapshot> task) {

                        // Clear the list because every time in this page will get again
                        MainActivity.itemNameList.clear();
                        MainActivity.itemPriceList.clear();
                        MainActivity.itemEmailList.clear();

                        if (task.isSuccessful()) {
                            int index = 0;
                            for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {

                                String json = document.getData().toString();

                                String[] array = json.split(",");

                                // Set the data to item list and display on the log
                                if(array.length > 0){
                                    String itemPrice = array[0].substring(7);
                                    String itemName = array[1].substring(6);
                                    String itemEmail = array[2].substring(7, array[2].length()-1);

                                    MainActivity.itemPriceList.add(itemPrice);
                                    MainActivity.itemNameList.add(itemName);
                                    MainActivity.itemEmailList.add(itemEmail);

                                    Log.i(MainActivity.TAG,
                                            "[" + MainActivity.itemNameList.get(index)+"] is $ : " +
                                                    MainActivity.itemPriceList.get(index) + ", Email :"+
                                                    MainActivity.itemEmailList.get(index),
                                            task.getException()
                                    );
                                    index++;
                                }
                            }

                            SetItemListView();
                        } else {
                            Log.e(MainActivity.TAG, "Error Can not get data.", task.getException());
                        }
                        progressBar.setVisibility(View.GONE);
                }
            });

        //endregion

        //region Set the Search Button
        editInput = findViewById(R.id.search_editText);
        Button buttonSearch = findViewById(R.id.search_button_search);
        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String keyWord = editInput.getText().toString();
                if(keyWord.isEmpty()){
                    Toast.makeText(SearchPage.this,R.string.search_toast, Toast.LENGTH_SHORT).show();
                } else{
                    Intent intent = new Intent(SearchPage.this, ItemListPage.class);
                    intent.putExtra(SEARCH_WORD, keyWord);
                    startActivity(intent);
                    Log.i(MainActivity.TAG,"User input : " + keyWord );
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
                new int[]{R.id.list_name, R.id.list_price}
        );

        listView.setAdapter(adapter);
    }
}