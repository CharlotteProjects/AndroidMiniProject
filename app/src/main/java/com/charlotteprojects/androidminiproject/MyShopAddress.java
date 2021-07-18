package com.charlotteprojects.androidminiproject;

import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.charlotteprojects.androidminiproject.databinding.ActivityMyShopAddressBinding;

public class MyShopAddress extends FragmentActivity implements OnMapReadyCallback {

    private double latitude = 0 ;
    private double longitude = 0;
    private String shopName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            Intent intent = getIntent();

            String stlatitude = intent.getStringExtra(MainActivity.ADDRESS_LATITUDE);
            String stlongitude = intent.getStringExtra(MainActivity.ADDRESS_LONGITUDE);
            shopName = intent.getStringExtra(MainActivity.SHOP_NAME);

            latitude = Double.parseDouble(stlatitude);
            longitude = Double.parseDouble(stlongitude);

        } catch (Exception e){
            Log.i(MainActivity.TAG,"No latitude & longitude");
        }

        com.charlotteprojects.androidminiproject.databinding.ActivityMyShopAddressBinding binding = ActivityMyShopAddressBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        LatLng position;

        // Add a marker in Sydney and move the camera
        if(latitude == 0 && longitude == 0){
            if(MainActivity.myProfile.latitude.equals("") || MainActivity.myProfile.longitude.equals("")){
                Toast.makeText(MyShopAddress.this,R.string.toast_noAddress,Toast.LENGTH_LONG).show();
                return;
            }
            position = new LatLng(Double.parseDouble(MainActivity.myProfile.latitude), Double.parseDouble(MainActivity.myProfile.longitude));
            googleMap.addMarker(new MarkerOptions().position(position).title(MainActivity.myProfile.shopName));
        } else {
            position = new LatLng(latitude, longitude);
            googleMap.addMarker(new MarkerOptions().position(position).title(shopName));
        }
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 19));
    }
}