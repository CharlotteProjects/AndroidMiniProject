package com.charlotteprojects.androidminiproject;

import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.charlotteprojects.androidminiproject.databinding.ActivityMyShopAddressBinding;

public class MyShopAddress extends FragmentActivity implements OnMapReadyCallback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

        // Add a marker in Sydney and move the camera
        LatLng position = new LatLng(Double.parseDouble(MainActivity.myProfile.latitude), Double.parseDouble(MainActivity.myProfile.longitude));

        googleMap.addMarker(new MarkerOptions().position(position).title(MainActivity.myProfile.shopName));

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 18));
    }
}