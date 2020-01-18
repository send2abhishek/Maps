package com.attra.googlemap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Dialog;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;

import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int PERMISSIONS_REQUEST_ENABLE_GPS = 101;
    private static final String TAG = "MyMap";
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 102;
    private static final int ERROR_DIALOG_REQUEST = 103;
    private boolean mLocationPermissionGranted = false;
    private EditText searchtext;
    private ImageView ic_magnify;
    private GoogleMap googleMap;
    private ImageView current_location;
    private PlacesClient placesClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        initAutoCompleteFragment();

    }

    private void initAutoCompleteFragment() {

        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME));

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {

                Toast.makeText(MainActivity.this,""+place.getName(),Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(@NonNull Status status) {

            }
        });

    }

    private void init(){

        Places.initialize(this,getString(R.string.map_api_key));
        placesClient=Places.createClient(this);

//        searchtext=findViewById(R.id.ic_edittext);
//
//        ic_magnify=findViewById(R.id.ic_search);
        current_location=findViewById(R.id.current_loc);
        current_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCurrentLocation1(googleMap);
            }
        });

//        ic_magnify.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(searchtext.getText().length()>0){
//                    geolocate();
//                }
//            }
//        });

//        searchtext.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//
//                if(actionId == EditorInfo.IME_ACTION_SEARCH
//                                || actionId ==EditorInfo.IME_ACTION_DONE
//                                || event.getAction()==KeyEvent.ACTION_DOWN
//                                || event.getAction()==KeyEvent.KEYCODE_ENTER){
//
//                    geolocate();
//
//
//                }
//                return false;
//            }
//        });
    }

//    private void  geolocate(){
//
//
//
//        String textserch=searchtext.getText().toString();
//        Geocoder geocoder=new Geocoder(this);
//        List<Address> addresslist=new ArrayList<>();
//        try {
//            addresslist=geocoder.getFromLocationName(textserch,1);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        if(addresslist.size() >0){
//
//            Address address=addresslist.get(0);
//            Log.d(TAG, "geolocate: " + address.toString());
//            moveCamera(googleMap,new LatLng(address.getLatitude(),address.getLongitude()),16f,address.getAddressLine(0));
//
//        }
//
//        AutocompleteSessionToken token=AutocompleteSessionToken.newInstance();
//        FindAutocompletePredictionsRequest request=FindAutocompletePredictionsRequest.builder()
//                                    .setTypeFilter(TypeFilter.ADDRESS)
//                                    .setSessionToken(token)
//                                    .setQuery("Pat")
//                                    .build();
////        PlacesClient placesClient= Places.createClient(this);
////        placesClient.findAutocompletePredictions(request).addOnSuccessListener((response) -> {
////            for (AutocompletePrediction prediction : response.getAutocompletePredictions()) {
////                Log.i(TAG, "Place prediction result " + prediction.getPlaceId());
////                Log.i(TAG, "Place prediction result" + prediction.getPrimaryText(null).toString());
////            }
////        }).addOnFailureListener((exception) -> {
////            if (exception instanceof ApiException) {
////                ApiException apiException = (ApiException) exception;
////                Log.e(TAG, "Place not found: " + apiException.getStatusCode());
////            }
////        });
//
//    }

    private void callMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (checkMapServices()) {
            //Toast.makeText(this, "All set and done", Toast.LENGTH_SHORT).show();
            callMap();

        }
    }

    private boolean checkMapServices() {
        if (isServicesOK()) {
            if (isMapsEnabled()) {
                return true;
            }
        }
        return false;
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("This application requires GPS to work properly, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        Intent enableGpsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(enableGpsIntent, PERMISSIONS_REQUEST_ENABLE_GPS);
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    public boolean isMapsEnabled() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
            return false;
        }
        return true;
    }

    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
            // getChatrooms();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    //checks for google play service in device
    public boolean isServicesOK() {
        Log.d(TAG, "isServicesOK: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MainActivity.this);

        if (available == ConnectionResult.SUCCESS) {
            //everything is fine and the user can make map requests
            Log.d(TAG, "isServicesOK: Google Play Services is working");
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            //an error occured but we can resolve it
            Log.d(TAG, "isServicesOK: an error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        } else {
            Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: called.");
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ENABLE_GPS: {
                if (mLocationPermissionGranted) {
                    //getChatrooms();


                } else {
                    getLocationPermission();
                }
            }
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        this.googleMap=googleMap;
        //googleMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                Toast.makeText(MainActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        googleMap.setMyLocationEnabled(true);

        getCurrentLocation1(googleMap);

    }

    private void getCurrentLocation1(final GoogleMap googleMap) {

        FusedLocationProviderClient locationProviderClient = LocationServices.getFusedLocationProviderClient(this);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }

        // return current Location of the user
        Task location = locationProviderClient.getLastLocation();
        location.addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {


                if(task.isSuccessful()){


                    Location currentloc=(Location)task.getResult();
                    Log.d(TAG, "found location -  " + currentloc.toString());
                    moveCamera(googleMap,new LatLng(currentloc.getLatitude(),currentloc.getLongitude()),16f,"Private Location");
                }

                else {
                    Log.d(TAG, "onComplete: Location not found");
                }

            }
        });

    }

    private void moveCamera(GoogleMap googleMap, LatLng latLng, float zoom,String place){
        Log.d(TAG, "move camera called");
        if(googleMap!=null){

           //googleMap.animateCamera(CameraUpdateFactory.zoomTo(zoom));
            // move the camera with specified longitude and latitude with zoom level
            //googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,zoom));

            // move the camera with specified longitude and latitude without zoom level
           // googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

            // best way to move camera, move the camera with specified longitude and latitude without zoom level,tilt
            // and bearing(angle)
            googleMap.moveCamera(CameraUpdateFactory
                    .newCameraPosition(new CameraPosition(latLng,zoom,45,0)));


            //googleMap.addMarker(new MarkerOptions().position(latLng).title("Attra Infotech"));

            googleMap.addMarker(new MarkerOptions().position(latLng).title(place));

        }

    }
}
