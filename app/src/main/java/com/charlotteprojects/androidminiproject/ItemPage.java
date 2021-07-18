package com.charlotteprojects.androidminiproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import org.jetbrains.annotations.NotNull;

import java.io.InputStream;

public class ItemPage extends AppCompatActivity {

    public static ProgressBar progressBar;

    private String stItemName, stItemPrice, stlatitude, stlongitude, shopName, imageURL;

    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_page);

        //region get data from intent

        Intent intent = getIntent();

        stItemName = intent.getStringExtra(MainActivity.ITEM_NAME);
        stItemPrice = intent.getStringExtra(MainActivity.ITEM_PRICE);
        stlatitude = intent.getStringExtra(MainActivity.ADDRESS_LATITUDE);
        stlongitude = intent.getStringExtra(MainActivity.ADDRESS_LONGITUDE);
        shopName = intent.getStringExtra(MainActivity.SHOP_NAME);
        imageURL = intent.getStringExtra(MainActivity.ITEM_URL);

        Log.i(MainActivity.TAG,"Loading Image : " + imageURL);

        //endregion

        //region init UI : TextView and Image
        progressBar = (ProgressBar) findViewById(R.id.item_progressBar);
        TextView textName = (TextView) findViewById(R.id.item_name);
        textName.setText(stItemName);

        TextView textPrice = (TextView) findViewById(R.id.item_price);
        textPrice.setText(stItemPrice);

        TextView textShopName = (TextView) findViewById(R.id.item_shopName);
        textShopName.setText(shopName);

        //endregion

        //region set the button to google map
        ImageButton imageButton = (ImageButton) findViewById(R.id.item_imageButton);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(stlatitude.equals("1024") || stlongitude.equals("1024")){

                    Toast.makeText(ItemPage.this,R.string.toast_noAddress,Toast.LENGTH_LONG).show();
                    Log.i(MainActivity.TAG,"No latitude & longitude");

                } else {
                    Intent intent = new Intent(ItemPage.this, MyShopAddress.class);

                    intent.putExtra(MainActivity.ADDRESS_LATITUDE, stlatitude);
                    intent.putExtra(MainActivity.ADDRESS_LONGITUDE, stlongitude);
                    intent.putExtra(MainActivity.SHOP_NAME, shopName);

                    startActivity(intent);
                }
            }
        });

        //endregion

        if(imageURL.equals("-")){
            ImageView imageView = (ImageView) findViewById(R.id.item_image);
            imageView.setImageResource(R.drawable.construction2);
            progressBar.setVisibility(View.GONE);
        }else {
            new DownloadImageFromInternet((ImageView) findViewById(R.id.item_image))
                    .execute(imageURL);
        }

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    // Loading Image
    private class DownloadImageFromInternet extends AsyncTask<String, Void, Bitmap> {
        ImageView imageView;

        public DownloadImageFromInternet(ImageView imageView) {
            this.imageView = imageView;
        }

        protected Bitmap doInBackground(String... urls) {
            String imageURL=urls[0];
            Bitmap bimage=null;
            try {
                InputStream in = new java.net.URL(imageURL).openStream();
                bimage = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error Message", e.getMessage());
                e.printStackTrace();
            }
            return bimage;
        }

        protected void onPostExecute(Bitmap result) {
            ItemPage.progressBar.setVisibility(View.GONE);
            imageView.setImageBitmap(result);
        }
    }
}