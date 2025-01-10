package com.example.babybuy.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.babybuy.Database.Database;
import com.example.babybuy.Model.ProductDataModel;
import com.example.babybuy.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Map extends AppCompatActivity implements OnMapReadyCallback {

    SupportMapFragment smf;
    FusedLocationProviderClient client;
    GoogleMap mgoogleMap;
    MapView mMapVIew;
    Geocoder geocoder;
    Double lat, lng, productlat, productlng;
    NetworkInfo networkInfo;
    ConnectivityManager manager;
    List<Address> address;
    String selectedaddress, newitem = "";
    Database db;
    int procatidlist;
    Spinner spinner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);


        // getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        smf = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.google_map);
        client = LocationServices
                .getFusedLocationProviderClient(this);

        smf.getMapAsync(this);
        // getmylocation();

        spinner = findViewById(R.id.productformapview);
        try {
            loadSpinnerData();
        } catch (Exception ex) {
            Toast.makeText(this, ex.getMessage().toString(), Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mgoogleMap = googleMap;
    }


    public void checkConnection() {
        manager = (ConnectivityManager) getApplicationContext().getSystemService(CONNECTIVITY_SERVICE);
        networkInfo = manager.getActiveNetworkInfo();
    }


    private void GetAddress(double mlat, double mlng) throws IOException {
        geocoder = new Geocoder(Map.this, Locale.getDefault());
        if (mlat != 0) {
            address = geocoder.getFromLocation(mlat, mlng, 1);
            if (address != null) {
                String mAddress = address.get(0).getAddressLine(0);
                String city = address.get(0).getLocality();
                String state = address.get(0).getAdminArea();
                String Country = address.get(0).getCountryName();
                String postalCode = address.get(0).getPostalCode();
                String knownName = address.get(0).getFeatureName();

                String productaddress = newitem + " " + mAddress;

                selectedaddress = mAddress;

                if (mAddress != null) {
                    MarkerOptions markerOptions = new MarkerOptions();
                    LatLng lating = new LatLng(mlat, mlng);
                    markerOptions.position(lating).title(productaddress);
                    mgoogleMap.addMarker(markerOptions).showInfoWindow();
                    mgoogleMap.animateCamera(CameraUpdateFactory
                            .newLatLngZoom(lating, 17));

                } else {
                    Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
        }
    }


    private void loadSpinnerData() {
        // database handler
        Database db = new Database(getApplicationContext());
        // Spinner Drop down elements
        ArrayList<ProductDataModel> lables = db.productfetchdataformap();
        String[] nameList = new String[lables.size()];

        for (int i = 0; i < lables.size(); i++) {
            nameList[i] = lables.get(i).getProductname(); //create array of name
        }

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, nameList);

        // Drop down layout style - list view with radio button
        dataAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mgoogleMap.clear();
                newitem = spinner.getSelectedItem().toString();
                productlat = lables.get(position).getProductlat();
                productlng = lables.get(position).getProductlong();

                if (productlng != 0.0) {
                    try {
                        GetAddress(productlat, productlng);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }


                //Toast.makeText(Map.this, newitem, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
}