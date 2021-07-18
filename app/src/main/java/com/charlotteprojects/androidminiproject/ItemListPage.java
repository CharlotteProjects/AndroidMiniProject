package com.charlotteprojects.androidminiproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ItemListPage extends AppCompatActivity {

    private ProgressBar progressBar;
    List<String> tempLatitude = new ArrayList<>();
    List<String> tempLongitude = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list_page);

        progressBar = (ProgressBar) findViewById(R.id.itemList_progressBar);

        // Get the keyword from search page
        Intent intent = getIntent();
        String keyWord = intent.getStringExtra(MainActivity.SEARCH_WORD);
        Log.i(MainActivity.TAG,"Get the keyWord : " + keyWord);

        // Set the keyword at TextView
        TextView textSearchWord = findViewById(R.id.itemList_searchWord);
        textSearchWord.setText(keyWord);

        // Create the ListView by key word
        SetItemListView(keyWord);

        progressBar.setVisibility(View.GONE);
    }

    // After Get Firebase Data, then create item list view
    private void SetItemListView(String keyword){

        ListView listView = (ListView) findViewById(R.id.itemList_listview);
        TextView text_found = (TextView) findViewById(R.id.itemList_found);

        List<HashMap<String, Object>> myList = new ArrayList<HashMap<String, Object>>();

        for(int i = 0; i < MainActivity.itemNameList.size() ; i++){
            HashMap<String, Object> item = new HashMap<String, Object>();

            // Check the which item match with keyword
            if(MainActivity.itemNameList.get(i).contains(keyword)){
                Log.i(MainActivity.TAG,"["+MainActivity.itemNameList.get(i) + "] is match !");

                item.put("name", MainActivity.itemNameList.get(i));
                item.put("price", MainActivity.itemPriceList.get(i));
                item.put("shopName", MainActivity.itemShopNameList.get(i));
                tempLatitude.add(MainActivity.itemLatitudeList.get(i));
                tempLongitude.add(MainActivity.itemLongitudeList.get(i));

                // item.put("image",imageAddress[i]);
                myList.add(item);

                // Set the Text
                text_found.setText(R.string.itemList_found);
            }
        }

        // If do not match
        if(text_found.getText().toString().isEmpty())
            text_found.setText(R.string.itemList_notFound);

        SimpleAdapter adapter = new SimpleAdapter(
                this,
                myList,
                R.layout.item_page,
                new String[]{"name","price","shopName"},
                new int[]{R.id.list_name, R.id.list_price, R.id.list_shopName}
        );

        listView.setAdapter(adapter);

        // Add ListView Listener
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if(MainActivity.itemLatitudeList.get(position).equals("1024") || MainActivity.itemLongitudeList.get(position).equals("1024")){
                    Toast.makeText(ItemListPage.this,R.string.toast_noAddress,Toast.LENGTH_LONG).show();
                    Log.i(MainActivity.TAG,"No latitude & longitude");
                } else {
                    Intent intent = new Intent(ItemListPage.this, MyShopAddress.class);

                    intent.putExtra(MainActivity.ADDRESS_LATITUDE, MainActivity.itemLatitudeList.get(position));
                    intent.putExtra(MainActivity.ADDRESS_LONGITUDE, MainActivity.itemLongitudeList.get(position));
                    intent.putExtra(MainActivity.SHOP_NAME, MainActivity.itemShopNameList.get(position));

                    startActivity(intent);
                }
            }
        });
    }
}