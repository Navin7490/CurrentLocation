package com.example.currentlocation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements LocationListener {

    TextView tvcountry,tvstate,tvcity,tvpincode,tvaddress;
    LocationManager locationManager;
    String district,taluka;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        grantedPermission();

        tvcountry=findViewById(R.id.Tv_Country);
        tvstate=findViewById(R.id.Tv_State);
        tvcity=findViewById(R.id.Tv_City);
        tvpincode=findViewById(R.id.Tv_Pincode);
        tvaddress=findViewById(R.id.Tv_Address);

        checkLocationIsEnabledOrNot(); // this will redirect us to the location setting

        getLocation();


    }

    private void getLocation() {

        try {
            locationManager= (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,500,5,(LocationListener) this);
        }catch (SecurityException e){
            e.printStackTrace();
        }
    }

    private void checkLocationIsEnabledOrNot() {

        LocationManager lm= (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean gpsEnabled=false;
        boolean networkEnabled=false;

        try {
            gpsEnabled=lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        }catch (Exception e){
            e.printStackTrace();
        }

        try {
            networkEnabled=lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        }catch (Exception e){
            e.printStackTrace();
        }

        if (!gpsEnabled && !networkEnabled){


            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Enable GPS Service")
                    .setCancelable(false)
                    .setPositiveButton("Enable", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // this intent redirect us to the location setting , if GPS is disabled this dialog will be show
                            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        }
                    }).setNegativeButton("Cancel",null)
                    .show();
        }
    }

    private void grantedPermission() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED

        && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED)
        {
           ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
           Manifest.permission.ACCESS_COARSE_LOCATION},100);
        }
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {


        try {
            Geocoder geocoder=new Geocoder(getApplicationContext(), Locale.getDefault());

            List<Address> addresses=geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);

            tvcountry.setText(addresses.get(0).getCountryName());
            tvstate.setText(addresses.get(0).getAdminArea());
            tvcity.setText(addresses.get(0).getLocality());
            tvpincode.setText(addresses.get(0).getPostalCode());
            tvaddress.setText(addresses.get(0).getAddressLine(0));
             district=addresses.get(0).getSubAdminArea();


            Toast.makeText(this, "District :"+district, Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {

    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {

    }
}