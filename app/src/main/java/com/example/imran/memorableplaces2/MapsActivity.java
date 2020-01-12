package com.example.imran.memorableplaces2;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener {

    LocationManager locationManager;
    LocationListener locationListener;
    private GoogleMap mMap;


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER, 3000, 0, locationListener);
                Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                setLocationOnRequest(lastKnownLocation, "Current Location");
                mMap.setMyLocationEnabled(true);
            }
        }
    }



    public void setLocationOnRequest(Location location, String string ) {
        if (location != null) {
            LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
            mMap.clear();
            mMap.addMarker(new MarkerOptions().position(currentLocation).title(string));



            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 12));
        } else {
            Toast.makeText(this, "No Location Was found!", Toast.LENGTH_SHORT).show();
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnMapLongClickListener(this);

        Intent intent = getIntent();
        if (intent.getIntExtra("itemNumber", 0) == 0) {

            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    setLocationOnRequest(location, "Current Location");
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            };

            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 0, locationListener);
                Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                setLocationOnRequest(lastKnownLocation, "Current Location");
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }

        } else {
            Location locationSet = new Location(LocationManager.GPS_PROVIDER);
            locationSet.setLatitude(MainActivity.savedLocation.get(intent.getIntExtra("itemNumber", 0)).latitude);
            locationSet.setLongitude(MainActivity.savedLocation.get(intent.getIntExtra("itemNumber", 0)).longitude);

            setLocationOnRequest(locationSet, MainActivity.savedPlaces.get(intent.getIntExtra("itemNumber", 0)));
        }


    }

    @Override
    public void onMapLongClick(LatLng latLng) {

        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        String address = "";

        try{
            List<Address> listAddress = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);

            if(listAddress != null && listAddress.size() > 0){
                if (listAddress.get(0).getThoroughfare() != null){
                    if(listAddress.get(0).getSubThoroughfare() != null){
                        address += listAddress.get(0).getSubThoroughfare() + " ";
                    }

                    address += listAddress.get(0).getThoroughfare();
                }
            }

        } catch (Exception e){
            e.printStackTrace();
        }

        if(address.equals("")){
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
            address += sdf.format(new Date());
        }

        mMap.addMarker(new MarkerOptions().position(latLng).title(address));

        MainActivity.savedPlaces.add(address);
        MainActivity.savedLocation.add(latLng);

        MainActivity.arrayAdapter.notifyDataSetChanged();
    }


}
