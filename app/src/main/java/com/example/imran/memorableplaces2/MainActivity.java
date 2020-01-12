package com.example.imran.memorableplaces2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    static ArrayList<String> savedPlaces = new ArrayList<>();
    static ArrayList<LatLng> savedLocation = new ArrayList<>();
    static ArrayAdapter<String> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView listView = findViewById(R.id.listOfPlaces);
        savedPlaces.add("Add a place....");
        savedLocation.add(new LatLng(0, 0));

        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, savedPlaces);
        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                intent.putExtra("itemNumber", position);
                startActivity(intent);
            }
        });

    }
}
