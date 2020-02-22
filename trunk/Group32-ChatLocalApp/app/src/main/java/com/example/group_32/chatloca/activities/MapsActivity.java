package com.example.group_32.chatloca.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
//import com.example.group_32.chatloca.DBHelper;
import com.example.group_32.chatloca.R;
import com.example.group_32.chatloca.adapters.PlaceAutocompleteAdapter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener,
        OnMapReadyCallback,LocationListener,GoogleMap.OnMarkerClickListener,RoutingListener {

    private static final String TAG = "MapsActivity";

    private final static int REQUEST_CHECK_SETTINGS_GPS=0x1;
    private static final int DEFAULT_ZOOM = 15;


    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(
            new LatLng(-40, -168), new LatLng(71, 136));

    private GoogleMap mMap;
    private PlaceAutocompleteAdapter mPlaceAutocompleteAdapter;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private final LatLng mDefaultLocation =  new LatLng(10.8231131, 106.7573822);
    private  boolean mLocationPermissionGranted = false;

    // last-known location
    private Location mLastKnownLocation;

    // Keys for storing activity state.
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";
    private static final String KEY_MEETING_LOCATION = "meeting_location";
    private LatLng mLocationMeeting;
    private  CameraPosition mCameraPosition;

    private Button btSetLocation,btCancel;
    private AutoCompleteTextView mSearchText;
    private ImageView mGps, mInfo, mSearch,mRoute;

    private GoogleApiClient mGoogleApiClient;

    //db
    private FirebaseDatabase db = FirebaseDatabase.getInstance();

    // ConversationId
    private String mConverId;
    private String mIsMessageActi;

    // market point
    private Marker mMarkerMeeting;

    // show address location
    // show address location
    Geocoder geocoder;

    // route
    private List<Polyline> polylines;
    private static final int[] COLORS = new int[]{R.color.blue,R.color.colorPrimary,R.color.colorPrimaryDark,R.color.com_facebook_blue,R.color.primary_dark_material_light};

//    private DBHelper localDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState != null){
            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
            mLocationMeeting = savedInstanceState.getParcelable(KEY_MEETING_LOCATION);
            Log.d(TAG, "GET THIS LOCATION "+ mLastKnownLocation.getLongitude()+ " "+ mLastKnownLocation.getLatitude());

        }
        setContentView(R.layout.activity_maps);
        polylines = new ArrayList<>();

        btSetLocation = findViewById(R.id.Map_btn_set_location);
        btCancel = findViewById(R.id.Map_btn_set_cancel);
        mGps = findViewById(R.id.ic_gps);
        mSearchText = findViewById(R.id.input_search);
        mSearch = findViewById(R.id.ic_magnify);
        mRoute  = findViewById(R.id.ic_route);
        mInfo = findViewById(R.id.meeting_info);



        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, 0,this )
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();
        mGoogleApiClient.connect();

        initMap();
        getLocationPermission();
    }

    @Override
    protected void onStart(){
        super.onStart();
        Intent intent = getIntent();
        mConverId = intent.getStringExtra("mConverId");
        mIsMessageActi = intent.getStringExtra("mIsMessageActi");
        if (mIsMessageActi != null)
            btSetLocation.setVisibility(View.GONE);
    }
    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Log.d(TAG, "MAP IS READY");

        if(mLocationPermissionGranted) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            init();
            getDeviceLocation();
        }
    }
    private  void init(){
        Log.d(TAG, "init: initializing");

        mPlaceAutocompleteAdapter = new PlaceAutocompleteAdapter(this, mGoogleApiClient,
                LAT_LNG_BOUNDS, null);

        mSearchText.setOnItemClickListener(mAutocompleteClickListener);
        mSearchText.setAdapter(mPlaceAutocompleteAdapter);

        // Construct a FusedLocationProviderClient.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));

        geocoder = new Geocoder(this, Locale.getDefault());

        mGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: clicked gps icon");
                getDeviceLocation();
            }
        });

        showMarkerMeetingFromDb();

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                Log.d(TAG, "New click from listener: " + latLng.toString());
                markerMeeting(latLng);

            }
        });

        btSetLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mLocationMeeting != null) {
                    Log.d(TAG, "onCLick: send data back");

                    try {
                        List<Address> addresses = geocoder.getFromLocation(mLocationMeeting.latitude, mLocationMeeting.longitude, 1);
                        Intent intent = new Intent();
                        intent.putExtra("mLongitude", mLocationMeeting.longitude);
                        intent.putExtra("mLatitude", mLocationMeeting.latitude);
                        if (addresses.size() > 0) {
                            intent.putExtra("addressName", addresses.get(0).getAddressLine(0));
                        }
                        setResult(Activity.RESULT_OK, intent);
                    } catch (IOException e) {
                        Log.e(TAG, "onClick getLocation: NullPointerException: " + e.getMessage());
                    }
                    finish();
                }
            }
        });
        mRoute.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                if(mLocationMeeting!= null && mLastKnownLocation != null){
                    setRoute();
                }
            }
        });
        mSearchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || keyEvent.getAction() == KeyEvent.ACTION_DOWN
                        || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER){

                    //execute our method for searching
                    geoLocate();
                }

                return false;
            }
        });
        mSearch.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Log.d(TAG, "search ADDRESS");
                if(!mSearchText.getText().toString().equals("")){
                    geoLocate();

                }
            }
        });
        mInfo.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Log.d(TAG, "onClick: Info Meeting");
                if(mLocationMeeting!= null)
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mLocationMeeting, mMap.getCameraPosition().zoom));
                else{
                    Toast.makeText(MapsActivity.this, "Not location meeting found",
                            Toast.LENGTH_SHORT).show();
                }
                try{
                    if(mMarkerMeeting.isInfoWindowShown()){
                        mMarkerMeeting.hideInfoWindow();
                    }else{
                        Log.d(TAG, "onClick: place info: " + mLocationMeeting.toString());
                        mMarkerMeeting.showInfoWindow();
                    }
                }catch (NullPointerException e){
                    Log.e(TAG, "onClick: NullPointerException: " + e.getMessage() );
                }

            }
        });
        btCancel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Log.d(TAG, "onCLick: Cancel");
                finish();
            }
        });

    }


    @Override
    public void onConnected(Bundle bundle) {
        getLocationPermission();
    }
    @Override
    public void onConnectionSuspended(int i) {
        //Do whatever you need
        //You can display a message here
    }
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        //You can display a message here
    }
    /**
     * Gets the current location of the device, and positions the map's camera.
     */

    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        Log.d(TAG, "onGetDeviceLocation");
        try {
            if (mLocationPermissionGranted) {


                LocationRequest mlocationRequest = new LocationRequest();
                mlocationRequest.setInterval(60 * 60 * 1000);
                mlocationRequest.setFastestInterval(120 * 1000);
                mlocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY );
                LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                        .addLocationRequest(mlocationRequest);
                builder.setAlwaysShow(true);

                PendingResult result = LocationServices.SettingsApi
                                .checkLocationSettings(mGoogleApiClient, builder.build());
                result.setResultCallback(new ResultCallback() {
                    @Override
                    public void onResult(@NonNull Result result) {
                        final Status status = result.getStatus();
                        switch (status.getStatusCode()) {
                            case LocationSettingsStatusCodes.SUCCESS:
                                Log.d(TAG, "PERMISION: NO 1");
                                // All location settings are satisfied.
                                // You can initialize location requests here.
                                int permissionLocation = ContextCompat
                                        .checkSelfPermission(MapsActivity.this,
                                                android.Manifest.permission.ACCESS_FINE_LOCATION);
                                if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
                                    final Task<Location> task= mFusedLocationProviderClient.getLastLocation();
                                    task.addOnSuccessListener(MapsActivity.this, new OnSuccessListener<Location>() {
                                        @Override
                                        public void onSuccess(Location location) {
                                            if(task.isSuccessful()){
                                                Log.d(TAG, "get Current location SUCCESS");
                                                mLastKnownLocation = task.getResult();
                                                if(mLastKnownLocation !=null) {
                                                    Log.d(TAG,"mLASTKNOWN NOT EQUAL NULL");
                                                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastKnownLocation.getLatitude(),
                                                            mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                                                }else
                                                    Log.d(TAG, "mLASTKNOWN IS EQUAL NULL");
                                            }else{
                                                Log.d(TAG, "Current location is null. Using defaults.");
                                                Log.e(TAG, "Exception: %s", task.getException());
                                            }
                                        }
                                    });
                                }
                                break;
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                Log.d(TAG, "PERMISION: NO 2");
                                // Location settings are not satisfied.
                                // But could be fixed by showing the user a dialog.
                                try {
                                    // Show the dialog by calling startResolutionForResult(),
                                    // and check the result in onActivityResult().
                                    // Ask to turn on GPS automatically
                                    status.startResolutionForResult(MapsActivity.this,
                                            REQUEST_CHECK_SETTINGS_GPS);
                                } catch (IntentSender.SendIntentException e) {
                                    // Ignore the error.
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                Log.d(TAG, "PERMISION: NO 3");
                                // Location settings are not satisfied. However, we have no way to fix the
                                // settings so we won't show the dialog.
                                //finish();
                                // to do: when not permission gps get db and show lastknowlocation
                                break;
                        }
                    }
                });
/*                if(mLastKnownLocation!= null) {
                    Log.d(TAG, "MLASTKNOWNLOCATION IS NULL SO");
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastKnownLocation.getLatitude(),
                            mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                }*/
            }
            else { // to do: when not permission gps get db and show lastknowlocation
                Log.d(TAG, "PERMISION: NULL");
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }


    /**
     * Saves the state of the map when the activity is paused.
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mMap != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, mMap.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, mLastKnownLocation);
            outState.putParcelable(KEY_MEETING_LOCATION, mLocationMeeting);
            if(mLastKnownLocation != null)
                Log.d(TAG, "SAVE THIS LOCATION "+ mLastKnownLocation.getLongitude()+ " "+ mLastKnownLocation.getLatitude());
            super.onSaveInstanceState(outState);
        }
        Log.d(TAG, "mMAP IS NULL");
    }

    private void initMap(){
        Log.d(TAG, "initMap: initializing map");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(MapsActivity.this);
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
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
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
//                    getDeviceLocation();
                    Log.d(TAG, "RESULT SOMETHING: TRUE");
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        Log.d(TAG, "RETURN NOW");
                        return;
                    }
                    mMap.setMyLocationEnabled(true);
                    mMap.getUiSettings().setMyLocationButtonEnabled(false);
                    init();
                    getDeviceLocation();
                }
                else
                    Log.d(TAG, "RESULT SOMETHING: NULL");
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {// nothing with location change
        mLastKnownLocation = location;
        if (mLastKnownLocation != null) {/*
            Double latitude=mLastKnownLocation.getLatitude();
            Double longitude=mLastKnownLocation.getLongitude();
            mtvLocation.setText("Location: "+ latitude.toString() + ","+ longitude.toString());*/

            //Or Do whatever you want with your location
        }
    }


    private  void geoLocate(){

        Log.d(TAG, "geoLocate: geolocating");

        String searchString = mSearchText.getText().toString();

        List<Address> list = new ArrayList<>();
        try{
            list = geocoder.getFromLocationName(searchString, 1);

            if(list.size() > 0){
                Address address = list.get(0);
                Log.d(TAG, "geoLocate: found a location: " + address.toString());
                //Toast.makeText(this, address.toString(), Toast.LENGTH_SHORT).show();

                mLocationMeeting = new LatLng(address.getLatitude(), address.getLongitude());
                markerMeeting(mLocationMeeting);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mLocationMeeting, DEFAULT_ZOOM));
            }
        }catch (IOException e){
            Log.e(TAG, "geoLocate: IOException: " + e.getMessage() );
        }

    }

    private void setRoute(){
        Routing routing = new Routing.Builder()
                .travelMode(Routing.TravelMode.DRIVING)
                .withListener(this)
                .waypoints(new LatLng(mLastKnownLocation.getLatitude(),mLastKnownLocation.getLongitude()),mLocationMeeting)
                //       .key()
                .build();
        routing.execute();
    }
    private void showMarkerMeetingFromDb(){
        db.getReference("meeting").child(mConverId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild("latitude") && dataSnapshot.hasChild("longitude")){
                    markerMeeting(new LatLng ((double) dataSnapshot.child("latitude").getValue(), (double) dataSnapshot.child("longitude").getValue()));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
    private void markerMeeting(LatLng latLng){
        mLocationMeeting = latLng;
        Log.d(TAG, "new click "+ latLng.toString());

        List<Address> addresses = new ArrayList<>();
        try {
            if(mMarkerMeeting != null){
                mMarkerMeeting.remove();
            }
            mMarkerMeeting = mMap.addMarker(new MarkerOptions().position(latLng));
            mMarkerMeeting.setTag(latLng);
            mMarkerMeeting.setTitle("meeting position");
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude,1);
            if(addresses.size()>0)
                mMarkerMeeting.setSnippet("Address: " + addresses.get(0).getAddressLine(0) + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {

        mMarkerMeeting.showInfoWindow();

        // Return false to indicate that we have not consumed the event and that we wish
        // for the default behavior to occur (which is for the camera to move such that the
        // marker is centered and for the marker's info window to open, if it has one).
        return false;
    }



    @Override
    public void onRoutingStart() {
        // The Routing Request starts
    }
    @Override
    public void onRoutingCancelled() {
        Log.i(TAG, "Routing was cancelled.");
    }
    @Override
    public void onRoutingFailure(RouteException e) {
        // The Routing request failed
//        progressDialog.dismiss();
        if(e != null) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }else {
            Toast.makeText(this, "Something went wrong, Try again", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void  onRoutingSuccess(ArrayList<Route> route,int shortestRouteIndex){
       //        progressDialog.dismiss();
        CameraUpdate center = CameraUpdateFactory.newLatLng(mLocationMeeting);
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(16);

        mMap.moveCamera(center);


        if(polylines.size()>0) {
            for (Polyline poly : polylines) {
                poly.remove();
            }
        }

        polylines = new ArrayList<>();
        //add route(s) to the map.
        for (int i = 0; i <route.size(); i++) {

            //In case of more than 5 alternative routes
            int colorIndex = i % COLORS.length;

            PolylineOptions polyOptions = new PolylineOptions();
            polyOptions.color(getResources().getColor(COLORS[colorIndex]));
            polyOptions.width(10 + i * 3);
            polyOptions.addAll(route.get(i).getPoints());
            Polyline polyline = mMap.addPolyline(polyOptions);
            polylines.add(polyline);

            Toast.makeText(getApplicationContext(),"Route "+ (i+1) +": distance - "+ route.get(i).getDistanceValue()+": duration - "+ route.get(i).getDurationValue(),Toast.LENGTH_SHORT).show();
        }

    }
    private AdapterView.OnItemClickListener mAutocompleteClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            hideSoftKeyboard();

            final AutocompletePrediction item = mPlaceAutocompleteAdapter.getItem(i);
            final String placeId = item.getPlaceId();

            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
        }
    };
    private void hideSoftKeyboard(){
        Log.d(TAG, "hide KEYBOARD not work");
        // Check if no view has focus:
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(@NonNull PlaceBuffer places) {
            if(!places.getStatus().isSuccess()){
                Log.d(TAG, "onResult: Place query did not complete successfully: " + places.getStatus().toString());
                places.release();
                return;
            }
            Place place = places.get(0);

            try{
                Log.d(TAG, "onResult: name: " + place.getName());
                Log.d(TAG, "onResult: address: " + place.getAddress());
                Log.d(TAG, "onResult: id:" + place.getId());
                Log.d(TAG, "onResult: latlng: " + place.getLatLng());
                Log.d(TAG, "onResult: rating: " + place.getRating());
                Log.d(TAG, "onResult: phone number: " + place.getPhoneNumber());
                Log.d(TAG, "onResult: website uri: " + place.getWebsiteUri());

                Log.d(TAG, "onResult: place: " + place.toString());
            }catch (NullPointerException e){
                Log.e(TAG, "onResult: NullPointerException: " + e.getMessage() );
            }
            mLocationMeeting = new LatLng(place.getViewport().getCenter().latitude,
                    place.getViewport().getCenter().longitude);
            markerMeeting(mLocationMeeting);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mLocationMeeting, DEFAULT_ZOOM));
            places.release();
        }
    };
}
