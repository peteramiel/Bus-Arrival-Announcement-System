package plm.busarrivalannouncementsystem;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CreateBusStops extends WizardBaseActivity implements OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener,View.OnClickListener {
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(this, "Map is Ready", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onMapReady: map is ready");
        mMap = googleMap;

        if (mLocationPermissionsGranted) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "onMapReady: permission denied");
                return;
            }
            mMap.setMyLocationEnabled(true);
            Log.d(TAG, "onMapReady: location enabled");
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
            init();
            getDeviceLocation();
        }
//         Setting a click event handler for the map
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(final LatLng latLng) {
                setNewMarkerInformation(latLng);
            }
        });
    }

    private String TAG = "CreateBusStops";
    private static final String FINE_LOCATION = android.Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = android.Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 16f;
    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(
            new LatLng(4.409981, 129.084164), new LatLng(20.227815, 114.686922));
    //widgets
    private AutoCompleteTextView mSearchText;
    EditText popupNewMarkerNameEditText;
    TextView popupNewMarkerLatLngTextView;
    Dialog popupNewMarker;
    //vars
    private Boolean mLocationPermissionsGranted = false;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private PlaceAutocompleteAdapter mPlaceAutocompleteAdapter;
    private GoogleApiClient mGoogleApiClient;
    private String routeName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_bus_stops);
        setContentView(R.layout.activity_maps);
        displayDrawer();
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            routeName = extras.getString("routeName");
            // and get whatever type user account id is
        }

        mSearchText = (AutoCompleteTextView) findViewById(R.id.input_search);
        getLocationPermission();
//        getNewMarkersFromFirebase();
//        getTerminalsFromFirebase();
    }

    private void addNewMarker(LatLng latLng, String newStopName) {
        // Creating a marker
        MarkerOptions markerOptions = new MarkerOptions();

        // Setting the position and title for the marker
        markerOptions.position(latLng);
        markerOptions.title(newStopName);

        // Clears the previously touched position
//        mMap.clear();

        Log.d(TAG, "addNewMarker: adding stop "+ latLng.toString());

        // Placing a marker on the touched position
        mMap.addMarker(markerOptions);
        Log.d(TAG, "addNewMarker: added stop "+ latLng.toString());

    }
    private void addNewTerminalMarker(LatLng latLng, String newStopName) {

        // Creating a marker
        MarkerOptions markerOptions = new MarkerOptions();

        // Setting the position and title for the marker
        markerOptions.position(latLng);
        markerOptions.title(newStopName);
        markerOptions.icon(BitmapDescriptorFactory
                .defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
        // Clears the previously touched position
//        mMap.clear();

        Log.d(TAG, "addNewMarker: adding stop "+ latLng.toString());

        // Placing a marker on the touched position
        mMap.addMarker(markerOptions);
        Log.d(TAG, "addNewMarker: added stop "+ latLng.toString());

    }

    private void saveNewMarkerToFireBase(LatLng latLng, String newStopName){
        //Save to Firebase
        FirebaseUser user = mAuth.getCurrentUser();
        String userId = user.getUid();
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("users");
        myRef.child(userId).child("routes").child(routeName).child("stops").child(newStopName).child("lat").setValue(latLng.latitude);
        myRef.child(userId).child("routes").child(routeName).child("stops").child(newStopName).child("long").setValue(latLng.longitude);
        myRef.child(userId).child("routes").child(routeName).child("stops").child(newStopName).child("name").setValue(newStopName);
    }


    private void init() {
        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();

        mPlaceAutocompleteAdapter = new PlaceAutocompleteAdapter(this, mGoogleApiClient,
                LAT_LNG_BOUNDS, null);

        mSearchText.setAdapter(mPlaceAutocompleteAdapter);

        mSearchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || keyEvent.getAction() == KeyEvent.ACTION_DOWN
                        || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER) {

                    //execute our method for searching
                    geoLocate();
                }

                return false;
            }
        });
    }


    @Override
    public void onClick(View view) {

    }

    private void getDeviceLocation() {
        Log.d(TAG, "getDeviceLocation: getting the devices current location");

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try {
            if (mLocationPermissionsGranted) {

                final Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "onComplete: found location!");
                            Location currentLocation = (Location) task.getResult();
                            moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
                                    DEFAULT_ZOOM);
                            LatLng mLocation=new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());


                        } else {
                            Log.d(TAG, "onComplete: current location is null");
                            Toast.makeText(CreateBusStops.this, "unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage());
        }
    }
    private void setNewMarkerInformation(final LatLng latLng) {

        popupNewMarker = new Dialog(CreateBusStops.this);
        popupNewMarker.setContentView(R.layout.popup_new_stop);
        popupNewMarker.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupNewMarker.show();
        popupNewMarkerNameEditText = popupNewMarker.findViewById(R.id.newStopNameEditText);
        popupNewMarkerLatLngTextView = popupNewMarker.findViewById(R.id.latLongTextView);
        String markerLatLng = "Latitude: " + latLng.latitude + " & Longitude: " + latLng.longitude;
        popupNewMarkerLatLngTextView.setText(markerLatLng);
        Button popupNewMarkerButton = popupNewMarker.findViewById(R.id.savePlaceButton);
        popupNewMarkerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNewMarker(latLng, popupNewMarkerNameEditText.getText().toString());
                saveNewMarkerToFireBase(latLng, popupNewMarkerNameEditText.getText().toString());
                // Animating to the touched position
                mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                popupNewMarker.dismiss();
            }
        });

    }

    private void geoLocate() {
        Log.d(TAG, "geoLocate: geolocating");

        String searchString = mSearchText.getText().toString();

        Geocoder geocoder = new Geocoder(CreateBusStops.this);
        List<Address> list = new ArrayList<>();
        try {
            list = geocoder.getFromLocationName(searchString, 1);
        } catch (IOException e) {
            Log.e(TAG, "geoLocate: IOException: " + e.getMessage());
        }

        if (list.size() > 0) {
            Address address = list.get(0);

            Log.d(TAG, "geoLocate: found a location: " + address.toString());
            //Toast.makeText(this, address.toString(), Toast.LENGTH_SHORT).show();
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(address.getLatitude(), address.getLongitude()), DEFAULT_ZOOM));
        }
    }

    private void moveCamera(LatLng latLng, float zoom) {
        Log.d(TAG, "moveCamera: moving the camera to: lat: " + latLng.latitude + ", lng: " + latLng.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

    private void initMap() {
        Log.d(TAG, "initMap: initializing map");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(CreateBusStops.this);
    }

    private void getLocationPermission() {
        Log.d(TAG, "getLocationPermission: getting location permissions");
        String[] permissions = {android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "getLocationPermission: FINE LOCATION GRANTED");
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "getLocationPermission: FINE AND COARSE LOCATION GRANTED");
                mLocationPermissionsGranted = true;
                initMap();
            } else {
                ActivityCompat.requestPermissions(this,
                        permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
                Log.d(TAG, "getLocationPermission: COARSE LOCATION DENIED");
            }
        } else {
            ActivityCompat.requestPermissions(this,
                    permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
            Log.d(TAG, "getLocationPermission: FINE LOCATION DENIED");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: called.");
        mLocationPermissionsGranted = false;

        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            mLocationPermissionsGranted = false;
                            Log.d(TAG, "onRequestPermissionsResult: permission failed");
                            return;
                        }
                    }
                    Log.d(TAG, "onRequestPermissionsResult: permission granted");
                    mLocationPermissionsGranted = true;
                    //initialize our map
                    initMap();
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, HomeActivity.class));
        finish();
        super.onBackPressed();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


}
