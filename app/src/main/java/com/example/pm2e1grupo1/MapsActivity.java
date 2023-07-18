package com.example.pm2e1grupo1;

import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps2);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in San Pedro Sula and move the camera
        LatLng sanPedroSula = new LatLng(15.4997, -88.0250);
        mMap.addMarker(new MarkerOptions().position(sanPedroSula).title("Marker in San Pedro Sula"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sanPedroSula,15f));
        mMap.setOnMarkerClickListener(marker -> {

            double latitud = marker.getPosition().latitude;
            double longitud = marker.getPosition().longitude;

            Uri gmmIntentUri = Uri.parse("geo:" + latitud + "," + longitud);

            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");

            if (mapIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(mapIntent);
            } else {
                Toast.makeText(this, "No se encontró Google Maps en tu dispositivo", Toast.LENGTH_SHORT).show();
            }

            return true;
        });

        double latitud = getIntent().getDoubleExtra("latitud", 0);
        double longitud = getIntent().getDoubleExtra("longitud", 0);

        LatLng ubicacion = new LatLng(latitud,longitud);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ubicacion,15f));

        //Posicion en Carro
        String destination = "San Pedro Sula";
        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + destination);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapIntent);
        } else {
            Toast.makeText(this, "No se encontró Google Maps en tu dispositivo", Toast.LENGTH_SHORT).show();
        }
    }
}
