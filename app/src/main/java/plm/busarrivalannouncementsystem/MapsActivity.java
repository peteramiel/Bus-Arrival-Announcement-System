package plm.busarrivalannouncementsystem;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class MapsActivity extends WizardBaseActivity implements OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener {

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

        }
//         Setting a click event handler for the map
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(final LatLng latLng) {
                setNewMarkerInformation(latLng);
            }
        });
    }

    private String TAG = "MapsActivity";
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
    List<String> markerNameArrayList;
    List<LatLng> markerLatLongArrayList;
    LocationManager locationManager;
    private Dialog popupNoBluetooth;
    //bluetooth
    private final String DEVICE_ADDRESS = "00:18:E4:34:E4:0F"; //MAC Address of Bluetooth Module
    private final UUID PORT_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
    private BluetoothDevice device;
    private BluetoothSocket socket;
    private OutputStream outputStream;
    private BluetoothAdapter mBluetoothAdapter;
    private String message;
    private String prev;
    MediaPlayer ring;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        displayDrawer();
        mSearchText = (AutoCompleteTextView) findViewById(R.id.input_search);
        getLocationPermission();
        Intent intent = getIntent();
        routeName = intent.getStringExtra("routeName");

        Toast.makeText(getApplicationContext(), routeName, Toast.LENGTH_SHORT).show();
        Log.d(TAG, "routeName: " + routeName);
        markerNameArrayList = new ArrayList<>();
        markerLatLongArrayList = new ArrayList<>();
        getNewMarkersFromFirebase();
        getTerminalsFromFirebase();
        locationManager = (LocationManager) this.getSystemService(this.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            getLocationPermission();
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                1000,
                3, locationListenerGPS);

        popupNoBluetooth = new Dialog(MapsActivity.this);
        popupNoBluetooth.setContentView(R.layout.popup_no_bluetooth);
        Button popupNoBluetoothButton = popupNoBluetooth.findViewById(R.id.retryConnectionButton);
        popupNoBluetoothButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MapsActivity.this, ConnectBluetoothModule.class));
                finish();
            }
        });
        if (bluetoothEnabled()) {
            Log.d(TAG, "bluetoothEnabled: true");

            if (bTconnect()) {
                Log.d(TAG, "bTconnect: true;");
            }
            else{
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    Objects.requireNonNull(popupNoBluetooth.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    Log.d(TAG, "BLuetooth Adapter == null");
                }
            }
        }else{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                Objects.requireNonNull(popupNoBluetooth.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                Log.d(TAG, "BLuetooth Adapter == null");
            }
        }
        ring= MediaPlayer.create(MapsActivity.this,R.raw.bus_stops);
    }

    LocationListener locationListenerGPS = new LocationListener() {
        @Override
        public void onLocationChanged(android.location.Location location) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            getNearMarker(new LatLng(location.getLatitude(), location.getLongitude()));
            String msg = "New Latitude: " + latitude + "New Longitude: " + longitude;
            Log.d(TAG, msg);
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

    private void checkBluetooth() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            // Device doesn't support Bluetooth
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                Objects.requireNonNull(popupNoBluetooth.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                Log.d(TAG, "BLuetooth Adapter == null");
            }
            popupNoBluetooth.show();
        }

        if (!mBluetoothAdapter.isEnabled()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                Objects.requireNonNull(popupNoBluetooth.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                Log.d(TAG, "BLuetooth Adapter != enabled");
            }
            popupNoBluetooth.show();
        }
        Set<BluetoothDevice> bondedDevices = mBluetoothAdapter.getBondedDevices();

        if (bondedDevices.isEmpty()) //Checks for paired bluetooth devices
        {
            Log.d(TAG, "bondedDevices: empty");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                Objects.requireNonNull(popupNoBluetooth.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            }
            popupNoBluetooth.show();
        } else {
            for (BluetoothDevice iterator : bondedDevices) {
                if (iterator.getAddress().equals(DEVICE_ADDRESS)) {
                    device = iterator;
                    Log.d(TAG, "finding device: true");
                    break;
                }
                Log.d(TAG, "bluetoothDevices: " + iterator.getAddress());
            }
            boolean connected = true;

            try {
                socket = device.createRfcommSocketToServiceRecord(PORT_UUID); //Creates a socket to handle the outgoing connection
                socket.connect();
                Log.d(TAG, "createRfcommSocket: true");
                Toast.makeText(getApplicationContext(),
                        "Connection to bluetooth device successful", Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                e.printStackTrace();
                connected = false;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    Objects.requireNonNull(popupNoBluetooth.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                }
                popupNoBluetooth.show();
            }

            if (connected) {
                try {
                    outputStream = socket.getOutputStream(); //gets the output stream of the socket
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            Log.d(TAG, "bluetoothConnect:" + connected);
        }
    }

    public boolean bluetoothEnabled() {
        boolean found = false;
        Log.d(TAG, "bluetoothEnabled: starting");
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            // Device doesn't support Bluetooth
            Log.d(TAG, "bluetoothAdapter: null");
            Toast.makeText(getApplicationContext(), "Device doesn't support bluetooth", Toast.LENGTH_SHORT).show();
            found = false;
        }

        if (!mBluetoothAdapter.isEnabled()) {
            Log.d(TAG, "bluetoothAdapter: disabled");
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 0);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Set<BluetoothDevice> bondedDevices = mBluetoothAdapter.getBondedDevices();

        if (bondedDevices.isEmpty()) //Checks for paired bluetooth devices
        {
            Log.d(TAG, "bondedDevices: empty");
            Toast.makeText(getApplicationContext(), "Please pair the device first", Toast.LENGTH_SHORT).show();
        } else {
            for (BluetoothDevice iterator : bondedDevices) {
                if (iterator.getAddress().equals(DEVICE_ADDRESS)) {
                    device = iterator;
                    Log.d(TAG, "finding device: true");
                    found = true;
                    break;
                }
                Log.d(TAG, "bluetoothDevices: " + iterator.getAddress());
            }
        }
        Log.d(TAG, "bluetoothEnabled: " + found);
        return found;
    }

    public boolean bTconnect() {
        boolean connected = true;

        try {
            socket = device.createRfcommSocketToServiceRecord(PORT_UUID); //Creates a socket to handle the outgoing connection
            socket.connect();
            Log.d(TAG, "createRfcommSocket: true");
            Toast.makeText(getApplicationContext(),
                    "Connection to bluetooth device successful", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
            connected = false;
        }

        if (connected) {
            try {
                outputStream = socket.getOutputStream(); //gets the output stream of the socket
                Log.d(TAG, "socket: get input output stream");
//                beginListenForData();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Log.d(TAG, "bluetoothConnect:" + connected);
        return connected;

    }

    private void getNearMarker(LatLng latLng) {
        Log.d(TAG, "getNearMarker: " + latLng.latitude + "," + latLng.longitude);
        //compare the current location to the markers in the arraylist
        for (LatLng marker : markerLatLongArrayList) {
            float[] distance = new float[1];
            Log.d(TAG, "getNearMarker: markerArrayList" + marker.latitude + "," + marker.longitude);
            Log.d(TAG, "getNearMarker: markerCurrent" + latLng.latitude + "," + latLng.longitude);
            Location.distanceBetween(marker.latitude, marker.longitude, latLng.latitude, latLng.longitude, distance);
            // distance[0] is now the distance between these lat/lons in meters
            Log.d(TAG, "getNearMarker: distance = " + distance[0]);
            if (distance[0] < 10.0) {
                int index = markerLatLongArrayList.indexOf(marker);
                if (prev != markerNameArrayList.get(index)) {
                    sendMessage(markerNameArrayList.get(index));
                    prev = markerNameArrayList.get(index);
                    Toast.makeText(getApplicationContext(), markerNameArrayList.get(index), Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "getNearMarker: sending message");
                    playSound();
                }
            }
        }
    }

    private void playSound() {
        try {
            ring.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
                            LatLng mLocation = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                            getNearMarker(mLocation);

                        } else {
                            Log.d(TAG, "onComplete: current location is null");
                            Toast.makeText(MapsActivity.this, "unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage());
        }
    }

    private void addNewMarker(LatLng latLng, String newStopName) {
        // Creating a marker
        MarkerOptions markerOptions = new MarkerOptions();

        // Setting the position and title for the marker
        markerOptions.position(latLng);
        markerOptions.title(newStopName);

        // Clears the previously touched position
//        mMap.clear();

        Log.d(TAG, "addNewMarker: adding stop " + latLng.toString());

        // Placing a marker on the touched position
        mMap.addMarker(markerOptions);
        Log.d(TAG, "addNewMarker: added stop " + latLng.toString());

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

        Log.d(TAG, "addNewMarker: adding stop " + latLng.toString());

        // Placing a marker on the touched position
        mMap.addMarker(markerOptions);
        Log.d(TAG, "addNewMarker: added stop " + latLng.toString());

    }

    private void saveNewMarkerToFireBase(LatLng latLng, String newStopName) {
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
        getDeviceLocation();
    }

    private void getNewMarkersFromFirebase() {
        Log.d(TAG, "getNewMarkersFromDatabase: starting void");

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        final String userId = user.getUid();
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("users/" + userId + "/routes/" + routeName + "/stops");
        Log.d(TAG, "getNewMarkersFromDatabase: myRef " + myRef.toString());
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                markerLatLongArrayList.clear();
                markerNameArrayList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if (ds.child("lat").getValue(Double.class) != null && ds.child("long").getValue(Double.class) != null) {
                        LatLng latLng = new LatLng(ds.child("lat").getValue(Double.class),
                                ds.child("long").getValue(Double.class));
                        String newStopName = ds.child("name").getValue(String.class);
                        markerNameArrayList.add(newStopName);
                        markerLatLongArrayList.add(latLng);
                        Log.d(TAG, "getNewMarkersFromDatabase: adding marker to the ArrayList: " + latLng.toString() + " name:" + newStopName);
                        addNewMarker(latLng, newStopName);
                        Log.d(TAG, "getNewMarkersFromDatabase: passing information latlng: " + latLng.toString() + " name:" + newStopName);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void getTerminalsFromFirebase() {
        Log.d(TAG, "getTerminalsFromFirebase: staring void");

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        final String userId = user.getUid();
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("users/" + userId + "/routes/" + routeName + "/terminals");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if (ds.child("lat").getValue(Double.class) != null && ds.child("long").getValue(Double.class) != null) {
                        LatLng latLng = new LatLng(ds.child("lat").getValue(Double.class),
                                ds.child("long").getValue(Double.class));
                        String newStopName = ds.child("name").getValue(String.class);
                        markerNameArrayList.add(newStopName);
                        markerLatLongArrayList.add(latLng);
                        Log.d(TAG, "getTerminalsFromFirebase: adding marker to the ArrayList: " + latLng.toString() + " name:" + newStopName);
                        addNewTerminalMarker(latLng, newStopName);
                        Log.d(TAG, "getTerminalsFromFirebase: passing information latlng: " + latLng.toString() + " name:" + newStopName);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void setNewMarkerInformation(final LatLng latLng) {

        popupNewMarker = new Dialog(MapsActivity.this);
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

        Geocoder geocoder = new Geocoder(MapsActivity.this);
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
        mapFragment.getMapAsync(MapsActivity.this);
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

    //BLUUETOOTH

    private void sendMessage(String mess) {

        try {
            message = mess;
            Log.d(TAG, "message: connected");
            outputStream.write(message.getBytes()); //transmits the value of command to the bluetooth module
            Log.d(TAG, "sending message: " + message);
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}