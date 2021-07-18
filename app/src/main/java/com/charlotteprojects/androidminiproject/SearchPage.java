package com.charlotteprojects.androidminiproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
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
                        MainActivity.itemShopNameList.clear();
                        MainActivity.itemLatitudeList.clear();
                        MainActivity.itemLongitudeList.clear();

                        if (task.isSuccessful()) {
                            int index = 0;
                            for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {

                                String json = document.getData().toString();

                                String[] array = json.split(",");

                                // Set the data to item list and display on the log
                                if(array.length > 0){
                                    String itemImage = array[0].substring(7);
                                    String itemPrice = array[1].substring(7);
                                    String itemName = array[2].substring(6);
                                    String itemEmail = array[3].substring(7, array[3].length()-1);

                                    Log.i(MainActivity.TAG, itemImage);

                                    MainActivity.itemPriceList.add(itemPrice);
                                    MainActivity.itemNameList.add(itemName);
                                    MainActivity.itemImageURL.add(itemImage);
                                    MainActivity.itemEmailList.add(itemEmail);

                                    // check the item shop name with user list

                                    for(int j = 0; j < MainActivity.userList.size(); j++){
                                        // Check which Email is same
                                        if(MainActivity.userList.get(j).userEmail.equals(MainActivity.itemEmailList.get(index))){
                                            MainActivity.itemShopNameList.add(MainActivity.userList.get(j).shopName);

                                            if(MainActivity.userList.get(j).latitude.isEmpty())
                                                MainActivity.itemLatitudeList.add("1024");
                                            else
                                                MainActivity.itemLatitudeList.add(MainActivity.userList.get(j).latitude);

                                            if(MainActivity.userList.get(j).longitude.isEmpty())
                                                MainActivity.itemLongitudeList.add("1024");
                                            else
                                                MainActivity.itemLongitudeList.add(MainActivity.userList.get(j).longitude);
                                        }
                                    }

                                    Log.i(MainActivity.TAG,
                                            "[" + MainActivity.itemNameList.get(index)+"] is $ : " +
                                                    MainActivity.itemPriceList.get(index) + ", Email :"+
                                                    MainActivity.itemEmailList.get(index) + ", Shop Name : " +
                                                    MainActivity.itemShopNameList.get(index) + ", geo : " +
                                                    MainActivity.itemLatitudeList.get(index) + " : " +
                                                    MainActivity.itemLongitudeList.get(index),
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
                    intent.putExtra(MainActivity.SEARCH_WORD, keyWord);
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
            item.put("shopName", MainActivity.itemShopNameList.get(i));
            item.put("image",R.drawable.construction2);
            myList.add(item);
        }

        SimpleAdapter adapter = new SimpleAdapter(
                this,
                myList,
                R.layout.item_page,
                new String[]{"name","price","shopName","image"},
                new int[]{R.id.list_name, R.id.list_price, R.id.list_shopName, R.id.list_image}
        );

        listView.setAdapter(adapter);

        // Add ListView On Listener
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(SearchPage.this, ItemPage.class);

                intent.putExtra(MainActivity.ITEM_NAME, MainActivity.itemNameList.get(position));
                intent.putExtra(MainActivity.ITEM_PRICE, MainActivity.itemPriceList.get(position));
                intent.putExtra(MainActivity.ADDRESS_LATITUDE, MainActivity.itemLatitudeList.get(position));
                intent.putExtra(MainActivity.ADDRESS_LONGITUDE, MainActivity.itemLongitudeList.get(position));
                intent.putExtra(MainActivity.SHOP_NAME, MainActivity.itemShopNameList.get(position));
                intent.putExtra(MainActivity.ITEM_URL, MainActivity.itemImageURL.get(position));

                startActivity(intent);

            }
        });

    }
}