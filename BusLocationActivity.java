package com.example.laxman.bussuvidha;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.LocationCallback;
import com.github.glomadrian.materialanimatedswitch.MaterialAnimatedSwitch;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class BusLocationActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, View.OnClickListener,
        LocationListener
{

    private GoogleMap mMap;

    private static final int MY_PERMISSION_REQUEST_CODE= 7000;
    private static final int PLAY_SERVICE_RES_REQUEST= 7001;
    private static final int BUS_COUNT= 10;

    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;

    private static int UPDATE_INTERVAL=2000;
    private static int FASTEST_INTERVAL=1000;
    private static int DISPLACEMENT=10;

    DatabaseReference dref;
    GeoFire geoFire;

    Marker mCurrent,mCurrent1;
    MaterialAnimatedSwitch location_switch;
    SupportMapFragment mapFragment;
    FirebaseAuth firebaseAuth;
    private  Button btnLogout,btnBack;
    private  Button btnBusInfo;
    private TextView textViewLati, textViewLongi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_location);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //Init View
        btnBusInfo = (Button) findViewById(R.id.btnBusInfo);
        btnBack = (Button) findViewById(R.id.btnBack);
        btnLogout = (Button) findViewById(R.id.btnLogout);
        textViewLati = (TextView) findViewById(R.id.textViewLati);
        textViewLongi = (TextView) findViewById(R.id.textViewLongi);

        location_switch=(MaterialAnimatedSwitch)findViewById((R.id.location_switch));
        location_switch.setOnCheckedChangeListener(new MaterialAnimatedSwitch.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(boolean isOnline) {
                if(isOnline)
                {
                    startLocationUpdates();
                    displayLocation();
                    Toast.makeText(getApplicationContext(),"You are online",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    stopLocalUpdates();
                    mCurrent1.remove();
                    Toast.makeText(getApplicationContext(),"You are offline",Toast.LENGTH_SHORT).show();
                }
            }
        });
        firebaseAuth = FirebaseAuth.getInstance();
        btnLogout.setOnClickListener(this);
        btnBack.setOnClickListener(this);
        btnBusInfo.setOnClickListener(this);
        dref= FirebaseDatabase.getInstance().getReference("Drivers");
        geoFire= new GeoFire(dref);
        setUpLocation();
    }




    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode)
        {
            case MY_PERMISSION_REQUEST_CODE:
                if(grantResults.length>0 && grantResults[0]== PackageManager.PERMISSION_GRANTED)
                {
                    if(checkPlayServices())
                    {
                        buildGoogleApiClient();
                        createLocationRequest();
                        if(location_switch.isChecked())
                            displayLocation();
                    }
                }
        }
    }

    private void setUpLocation() {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                )
        {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
            },MY_PERMISSION_REQUEST_CODE);
        }
        else
        {
            if(checkPlayServices())
            {
                buildGoogleApiClient();
                createLocationRequest();
                if(location_switch.isChecked())
                    displayLocation();
            }
        }
    }

    private void createLocationRequest() {
        mLocationRequest= new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
    }

    private void buildGoogleApiClient() {
        mGoogleApiClient= new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    private boolean checkPlayServices() {
        int resultCode= GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if(resultCode != ConnectionResult.SUCCESS)
        {
            if(GooglePlayServicesUtil.isUserRecoverableError(resultCode))
                GooglePlayServicesUtil.getErrorDialog(resultCode,this,PLAY_SERVICE_RES_REQUEST).show();
            else {
                Toast.makeText(this, "This device is not supported", Toast.LENGTH_SHORT).show();
                finish();
            }
            return false;
        }
        return true;
    }

    private void stopLocalUpdates() {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                )
        {
            return;
        }
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient,this);
    }



    @Override
    protected void onStart() {
        super.onStart();
        dref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String key=FirebaseAuth.getInstance().getCurrentUser().getUid();
                for(DataSnapshot ds : dataSnapshot.getChildren()) {

                    Log.d("BusLocationActivity:",FirebaseAuth.getInstance().getCurrentUser().getUid()+" \nKey: "+ds.getKey());

                    if(!FirebaseAuth.getInstance().getCurrentUser().getUid().equals(ds.getKey())){
                        key=ds.getKey();
                        if(ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                                ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                                ) {
                            return;
                        }
                        geoFire.getLocation(key, new LocationCallback() {
                            @Override
                            public void onLocationResult(String key, GeoLocation location) {
                                if (location != null) {
                                    Log.d("BusLocation:","Latitude:"+location.latitude);
                                    System.out.println(String.format("The location for key %s is [%f,%f]", key, location.latitude, location.longitude));
                                   // Toast.makeText(getApplicationContext(),"User:"+key,Toast.LENGTH_SHORT).show();

                                    mCurrent=mMap.addMarker(new MarkerOptions()
                                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.bus8))
                                            .position(new LatLng(location.latitude,location.longitude))
                                            .title("Bus Information")
                                            .snippet(" Key: " +key +"\n Latitude: "+ location.latitude+"\n Longitude"+ location.longitude+"\n No.of people in bus: "+BUS_COUNT));

                                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.latitude, location.longitude),15.0f));
                                    mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(getApplicationContext()));


                                    //trying to add rotating markar
//                                    Location prevLoc = ... ;
//                                    Location newLoc = ... ;
//                                    float bearing = prevLoc.bearingTo(newLoc) ;
//                                             .anchor(0.5f, 0.5f)
//                                            .rotation(bearing)
//                                            .flat(true));
//


                                    rotateMarker(mCurrent,360,mMap);

                                } else {
                                    System.out.println(String.format("There is no location for key %s in GeoFire", key));
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                System.err.println("There was an error getting the GeoFire location: " + databaseError);
                            }
                        });

                    }
//                    else{
//                        key=ds.getKey();
//                        if(ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
//                                ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
//                                )
//                        {
//                            return;
//                        }
//                        mLastLocation= LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
//                        if(mLastLocation!=null)
//                        {
//                            if(location_switch.isChecked())
//                            {
//                                final double latitude= mLastLocation.getLatitude();
//                                final double longitude= mLastLocation.getLongitude();
//                                textViewLongi.setText("Latitude:  "+Double.toString(longitude));
//                                textViewLati.setText("Longitute: "+Double.toString(latitude));
//                                //Toast.makeText(getApplicationContext(),"User:"+key,Toast.LENGTH_SHORT).show();
//                                mCurrent=mMap.addMarker(new MarkerOptions()
//                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_current_location))
//                                        .position(new LatLng(latitude,longitude))
//                                        .title("Bus Information")
//                                        .snippet(" Key: " +key +"\n Latitude: "+ latitude+"\n Longitude"+ longitude+"\n No.of people in bus: "+BUS_COUNT));
//
//                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude),15.0f));
//                                mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(getApplicationContext()));
//
//                                rotateMarker(mCurrent,360,mMap);
//
//                            }
//                        }
//                        else
//                        {
//                            Log.d("ERROR","Cannot get your location");
//                        }
//

//                    }

                }



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void displayLocation() {
        String key=FirebaseAuth.getInstance().getCurrentUser().getUid();
        if(ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                )
        {
            return;
        }
        mLastLocation= LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if(mLastLocation!=null)
        {
            if(location_switch.isChecked())
            {
                final double latitude= mLastLocation.getLatitude();
                final double longitude= mLastLocation.getLongitude();
                textViewLongi.setText("Latitude:  "+Double.toString(longitude));
                textViewLati.setText("Longitute: "+Double.toString(latitude));
                //Toast.makeText(getApplicationContext(),"User:"+key,Toast.LENGTH_SHORT).show();
                if(mCurrent1!=null)
                {
                    mCurrent1.remove();
                }
                mCurrent1=mMap.addMarker(new MarkerOptions()
                        //.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_current_location))
                        .position(new LatLng(latitude,longitude))
                        .title("Your current location")
                        .snippet("Latitude: "+ latitude+"\n Longitude"+ longitude));

                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude),15.0f));
                mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(getApplicationContext()));

                rotateMarker(mCurrent1,0,mMap);

            }
        }
        else
        {
            Log.d("ERROR","Cannot get your location");
        }

    }

    private void rotateMarker(final Marker mCurrent, final float i, GoogleMap mMap) {
        final Handler handler= new Handler();
        final long start= SystemClock.uptimeMillis();
        final float startRotation = mCurrent.getRotation();
        final long duration= 1500;

        final Interpolator interpolator= new LinearInterpolator();
        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed =SystemClock.uptimeMillis()-start;
                float t=interpolator.getInterpolation((float)elapsed/duration);
                float rot= t*i+(1-t)*startRotation;
                mCurrent.setRotation(-rot>180?rot/2:rot);
                if(t<1.0)
                {
                    handler.postDelayed(this,16);
                }
            }
        });

    }

    private void startLocationUpdates() {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                )
        {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,mLocationRequest,this);

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        displayLocation();
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation=location;
        displayLocation();
    }

    @Override
    public void onClick(View v) {
        if(v == btnLogout){
            firebaseAuth.signOut();
            startActivity(new Intent(this,LoginActivity.class));
            Toast.makeText(this,"Logout Sucessful",Toast.LENGTH_SHORT).show();
            finish();
        }
        if(v == btnBusInfo){
            startActivity(new Intent(this,BusInformationActivity.class));
            Toast.makeText(this,"Bus Information is ready...",Toast.LENGTH_SHORT).show();
            //finish();
        }
        if(v == btnBack){
            startActivity(new Intent(this,HomeActivity.class));
            Toast.makeText(this,"Open home page successfully",Toast.LENGTH_SHORT).show();
            finish();
        }
    }

}
