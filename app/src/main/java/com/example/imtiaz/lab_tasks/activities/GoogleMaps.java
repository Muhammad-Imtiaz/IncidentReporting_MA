package com.example.imtiaz.lab_tasks.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import com.example.imtiaz.lab_tasks.R;
import com.example.imtiaz.lab_tasks.models.PlaceInfo;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class GoogleMaps extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private static final String TAG = "GoogleMaps";

    private LocationManager locationManager;
    private Location location;
    private LatLng latLngCurrent, latLngDestination;

    // constants
    private static final int ERROR_DIALOGE_REQUEST = 9001;
    private static final float DEFAULT_ZOOM = 10f;
    public static final int LOCATION_PERMISSION_REQUEST_CODE = 1122;
    private boolean mLocationPermissionGranted;
    private String incidentType;

    //firebase
    private DatabaseReference mMapRef;

    private PlaceInfo mPlace;
    private final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_maps);
        //getting data from create post
        Intent intent = getIntent();
        incidentType = intent.getStringExtra("type");

        if (isServicesOK()){
            if(isLocationEnabled()){
                getLocationPermission();
                if (mLocationPermissionGranted){
                    initializeMap();
                }
            }else {
                showSettingAlert();
            }

        }
    }

    private void initializeMap(){
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        getLocation();
        fetchingNearestLocations(incidentType);
        onMarkerClicked();
    }

    private boolean isLocationEnabled(){
        LocationManager locationManager = (LocationManager) getSystemService(this.LOCATION_SERVICE);
        if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && !locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            return false;
        }
        return true;
    }
    

    public void showSettingAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("GPS setting!");
        alertDialog.setMessage("GPS is not enabled, Do you want to go to settings menu? ");
        alertDialog.setPositiveButton("Setting", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        });
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                startActivity(new Intent(GoogleMaps.this, CreatePost.class));
            }
        });
        alertDialog.show();
    }

    private boolean isServicesOK() {
        Log.i(TAG, "isServicesOK: Checking google services version");
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(GoogleMaps.this);
        if (available == ConnectionResult.SUCCESS) {
            Log.d(TAG, "isServicesOK: Google services are working fine");
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            // an error occured but we can resolve it
            Log.d(TAG, "isServicesOK: an error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(GoogleMaps.this, available, ERROR_DIALOGE_REQUEST);
            dialog.show();
        } else {
            Toast.makeText(GoogleMaps.this, "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    public void getLocationPermission(){
        Log.i(TAG, "getLocationPermission: getting location permissions");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION};
        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            Log.d(TAG, "getLocationPermission: all permission granted");
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.i(TAG, "onRequestPermissionsResult: called");
        mLocationPermissionGranted = false;
        switch (requestCode){
            case LOCATION_PERMISSION_REQUEST_CODE:{
                if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED ){
                    Log.i(TAG, "onRequestPermissionsResult: permission failed");
                    return;
                }
            }
            Log.i(TAG, "onRequestPermissionsResult: permission granted");
            mLocationPermissionGranted = true;
        }
    }

    @SuppressLint("MissingPermission")
    public void getLocation() {
        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location != null) {
            latLngCurrent = new LatLng(location.getLatitude(), location.getLongitude());
            Log.d("Latlang", latLngCurrent+"");
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngCurrent, DEFAULT_ZOOM));
            mMap.addMarker(new MarkerOptions().position(latLngCurrent).title("My Location"));
        }
    }

    private void fetchingNearestLocations(String authorty){
        /*final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("fetching your device nearest locations");
        progressDialog.show();*/
        Log.i(TAG, "fetchingNearestLocations: " + authorty);
        mMapRef = FirebaseDatabase.getInstance().getReference("Authorties");
        switch (authorty){
            case "Accident":
                incidentType = "Hospitals";
                break;
            case "Fire":
                incidentType = "Firebargade";
                break;
            case "Earthquak":
                incidentType = "Earthquak";
                break;
            case "Flood":
                incidentType = "Flood";
                break;
            default:
                break;
        }
        mMapRef.child(incidentType).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot mDatasnapshot: dataSnapshot.getChildren()) {
                    String name = mDatasnapshot.child("name").getValue().toString();
                    String lat = mDatasnapshot.child("lat").getValue().toString();
                    String lng = mDatasnapshot.child("lng").getValue().toString();
                    double plat = new Double(lat);
                    double plng = new Double(lng);
                    LatLng latLng = new LatLng(plat, plng);
                    mPlace = new PlaceInfo(name, latLng);
                    //progressDialog.dismiss();
                    showMarkersOnMap(mPlace);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void showMarkersOnMap(PlaceInfo place) {
        Log.i(TAG, "showMarkersOnMap: Showing markers on map");

        MarkerOptions options = new MarkerOptions()
                .position(place.getLatLng())
                .title(place.getName());
        mMap.addMarker(options);
        showDirections(latLngCurrent, place.getLatLng());
    }

    private void showDirections(LatLng latLngCurrent, LatLng latLng) {
        PolylineOptions options = new PolylineOptions().width(5).color(Color.BLUE).geodesic(true);
        options.add(latLngCurrent);
        options.add(latLng);
        mMap.addPolyline(options);
    }

    public void onMarkerClicked() {
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(final Marker marker) {
                marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                latLngDestination = marker.getPosition();
                AlertDialog.Builder builder = new AlertDialog.Builder(GoogleMaps.this);
                builder.setTitle("Confirm");
                builder.setMessage("Confirm Location?");
                builder.setCancelable(false);
                builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String title = marker.getTitle();
                        sendData(title, latLngDestination.latitude, latLngDestination.longitude);
                    }
                });


                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    }
                });
                builder.create();
                builder.show();
                return true;
            }
        });
    }

    public void sendData(String title, double latitude, double longitude) {
        Intent intent = new Intent(GoogleMaps.this, CreatePost.class);
        intent.putExtra("Title", title);
        intent.putExtra("Lat", latitude);
        intent.putExtra("Long", longitude);
        setResult(RESULT_OK, intent);
        finish();
    }


}

