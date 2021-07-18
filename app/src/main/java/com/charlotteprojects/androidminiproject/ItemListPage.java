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
    List<String> tempName = new ArrayList<>();
    List<String> tempPrice = new ArrayList<>();
    List<String> tempLatitude = new ArrayList<>();
    List<String> tempLongitude = new ArrayList<>();
    List<String> tempShopName = new ArrayList<>();
    List<String> tempImageURL = new ArrayList<>();

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
                item.put("image",R.drawable.construction2);
                tempName.add(MainActivity.itemNameList.get(i));
                tempPrice.add(MainActivity.itemPriceList.get(i));
                tempLatitude.add(MainActivity.itemLatitudeList.get(i));
                tempLongitude.add(MainActivity.itemLongitudeList.get(i));
                tempShopName.add(MainActivity.itemShopNameList.get(i));
                tempImageURL.add(MainActivity.itemImageURL.get(i));

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
                new String[]{"name","price","shopName","image"},
                new int[]{R.id.list_name, R.id.list_price, R.id.list_shopName,R.id.list_image}
        );

        listView.setAdapter(adapter);

        // Add ListView Listener
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(ItemListPage.this, ItemPage.class);

                intent.putExtra(MainActivity.ITEM_NAME, tempName.get(position));
                intent.putExtra(MainActivity.ITEM_PRICE, tempPrice.get(position));
                intent.putExtra(MainActivity.ADDRESS_LATITUDE, tempLatitude.get(position));
                intent.putExtra(MainActivity.ADDRESS_LONGITUDE, tempLongitude.get(position));
                intent.putExtra(MainActivity.SHOP_NAME, tempShopName.get(position));
                intent.putExtra(MainActivity.ITEM_URL, tempImageURL.get(position));

                startActivity(intent);

            }
        });
    }
}