package com.miggy.mekaniko;

import android.*;
import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
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
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.vision.text.Text;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MechanicMapActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener, RoutingListener, NavigationView.OnNavigationItemSelectedListener {

    private GoogleMap mMap;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    LocationRequest mLocationRequest;

    private Button  mRideStatus, mBalancePay;

    private Switch mWorkingSwitch;

    private int status = 0;

    private double ridePrice;

    private String customerId = "" , destination, mMbalance;

    private LatLng destinationLatLng;

    private float rideDistance;

    private Boolean isLoggingOut = false;

    private SupportMapFragment mapFragment;

    private LinearLayout mCustomerInfo;

    private ImageView mCustomerProfileImage, displayImage;

    private TextView mCustomerName, mCustomerPhone, mCustomerDestination, mCustomerCar, mVerification, mCustomerProblem;

    private FirebaseAuth mAuth;

    private DrawerLayout dl;
    private ActionBarDrawerToggle abdt;
    private String userID, displayNameField, displayEmailField, displayImageField;

    private TextView displayName, displayEmail;
    private NavigationView navigationView;
    private boolean firstLoad = true;
    private DatabaseReference mCustomerDatabase, mMechanicDatabase, displayDatabase, validationDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mechanic_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        polylines = new ArrayList<>();
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MechanicMapActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
        }else{
            mapFragment.getMapAsync(this);
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        dl = findViewById(R.id.dl);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, dl, toolbar,
                R.string.open, R.string.close);
        dl.addDrawerListener(toggle);
        toggle.syncState();
        View headerView = navigationView.getHeaderView(0);
        destinationLatLng = new LatLng(0.0,0.0);

        ridePrice = 200;

        mCustomerInfo = (LinearLayout) findViewById(R.id.customerInfo);

        mCustomerProfileImage = (ImageView) findViewById(R.id.customerProfileImage);

        mCustomerName = (TextView) findViewById(R.id.customerName);

        displayName = (TextView) headerView.findViewById(R.id.display_username);
        displayEmail = (TextView) headerView.findViewById(R.id.display_email);
        displayImage = (ImageView) headerView.findViewById(R.id.image_display);

        mCustomerPhone = (TextView) findViewById(R.id.customerPhone);
        mCustomerCar = (TextView) findViewById(R.id.customerCar);
        mCustomerProblem = (TextView) findViewById(R.id.customerProblem);

        mBalancePay = (Button) findViewById(R.id.Balancepay);

        mVerification = (TextView) findViewById(R.id.Verified);
        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getCurrentUser().getUid();

        displayDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(userID);

        mCustomerDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(userID);
        mCustomerDestination = (TextView) findViewById(R.id.customerDestination);


        mMechanicDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(userID).child("PendingBalance");
        getUserInfo();
        mWorkingSwitch = (Switch) findViewById(R.id.workingSwitch);
        mBalancePay.setVisibility(View.GONE);
        mWorkingSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    connectDriver();
                }else{
                    disconnectDriver();
                    endRide();
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("mechsAvailable");
                    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    GeoFire geoFire = new GeoFire(ref);
                    geoFire.removeLocation(userId);
                }
            }
        });

        mBalancePay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                payPalPayment();
            }
        });


        mRideStatus = (Button) findViewById(R.id.rideStatus);
        mRideStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (status){

                    case 1:
                        status=2;

                        if(destinationLatLng.latitude!=0.0 && destinationLatLng.longitude!=0.0){
                            getRouteToMarker(destinationLatLng);
                        }

                        mRideStatus.setText("end service");
                        break;

                    case 2:
                        recordRide();
                        endRide();
                        break;


                }
            }
        });
        validationDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(userID);
        Validation();
        Verification();
        MechanicBalanceCheck();
        getAssignedCustomer();
        getmechinfo();
    }

    private void Validation() {
        validationDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();

                    if (map.get("verified") != null) {

                        Toast.makeText(MechanicMapActivity.this, "Account is Verified!", Toast.LENGTH_SHORT).show();
                        mWorkingSwitch.setEnabled(true);
                        navigationView.setVisibility(View.VISIBLE);

                    }else{
                        Toast.makeText(MechanicMapActivity.this, "Account is not Verified Please wait", Toast.LENGTH_SHORT).show();
                        mWorkingSwitch.setEnabled(false);

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void getmechinfo() {
        displayDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();

                    if (map.get("username") != null) {
                        displayNameField = map.get("username").toString();
                        displayName.setText(displayNameField);
                    }
                    if (map.get("email") != null) {
                        displayEmailField = map.get("email").toString();
                        displayEmail.setText(displayEmailField);
                    }
                    if (map.get("profileImageUrl") != null) {
                        displayImageField = map.get("profileImageUrl").toString();
                        Glide.with(getApplication()).load(displayImageField).into(displayImage);
                    }
                }else{
                    Toast.makeText(MechanicMapActivity.this, "no info", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void MechanicBalanceCheck() {
        displayDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0){
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if (map.get("PendingBalance")!=null) {
                        Toast.makeText(MechanicMapActivity.this, "Balance Pending Please settle balance", Toast.LENGTH_SHORT).show();
                        mBalancePay.setVisibility(View.VISIBLE);

                        mWorkingSwitch.setEnabled(false);
                    }else {
                        mBalancePay.setVisibility(View.GONE);


                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void getUserInfo(){
        mCustomerDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0){
                    FirebaseAuth.getInstance().signOut();
                    Toast.makeText(getApplicationContext(), "Not Authorized to log in", Toast.LENGTH_LONG).show();
                    Intent intentssss = new Intent(MechanicMapActivity.this, MainActivity.class);
                    startActivity(intentssss);
                    finish();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private int PAYPAL_REQUEST_CODE = 1;
    private static PayPalConfiguration config = new PayPalConfiguration()
            .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)
            .clientId(PayPalConfig.PAYPAL_CLIENT_ID);

    private void payPalPayment() {
        PayPalPayment payment = new PayPalPayment(new BigDecimal(ridePrice), "PHP", "Service",
                PayPalPayment.PAYMENT_INTENT_SALE);

        Intent intent = new Intent(this, PaymentActivity.class);

        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);

        startActivityForResult(intent, PAYPAL_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PAYPAL_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                PaymentConfirmation confirm = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if (confirm != null) {
                    try {
                        JSONObject jsonObj = new JSONObject(confirm.toJSONObject().toString());

                        String paymentResponse = jsonObj.getJSONObject("response").getString("state");

                        if (paymentResponse.equals("approved")) {
                            Toast.makeText(getApplicationContext(), "Payment successful", Toast.LENGTH_LONG).show();
                            mMechanicDatabase.setValue(null);
                            mBalancePay.setVisibility(View.GONE);



                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            } else {
                Toast.makeText(getApplicationContext(), "Payment unsuccessful", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        stopService(new Intent(this, PayPalService.class));
        super.onDestroy();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){

            case R.id.nav_setting:
                Intent intent = new Intent(MechanicMapActivity.this, MechanicSettingsActivity.class);
                startActivity(intent);
                break;

            case R.id.nav_history:
                Intent intents = new Intent(MechanicMapActivity.this, HistoryActivity.class);
                intents.putExtra("customerOrDriver", "Drivers");
                startActivity(intents);
                break;

            case R.id.nav_chat:
                Intent intentss = new Intent(MechanicMapActivity.this, MechChatActivity.class);
                startActivity(intentss);
                break;

            case R.id.logout:
                disconnectDriver();
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("mechsAvailable");
                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                GeoFire geoFire = new GeoFire(ref);
                geoFire.removeLocation(userId);
                FirebaseAuth.getInstance().signOut();
                Intent intentsss = new Intent(MechanicMapActivity.this, MainActivity.class);
                startActivity(intentsss);
                finish();
                break;
        }

        return true;
    }
    public void onBackPressed() {
        if (dl.isDrawerOpen(GravityCompat.START)) {
            dl.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return abdt.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }
    private void Verification() {
        final FirebaseUser user = mAuth.getCurrentUser();
        if (user.isEmailVerified()) {


            mVerification.setVisibility(View.GONE);
        } else {
            mVerification.setText("Email Not Verified (Click to Verify)");


            mVerification.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(MechanicMapActivity.this, "Verification Email Sent", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });

        }
    }

    private void getAssignedCustomerDestination() {
        String driverId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference assignedCustomerRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(driverId).child("customerRequest").child("destination");
        assignedCustomerRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if(map.get("destination")!=null){
                        destination = map.get("destination").toString();
                        mCustomerDestination.setText("Destination: " + destination);
                    }
                    else{
                        mCustomerDestination.setText("Destination: --");
                    }

                    Double destinationLat = 0.0;
                    Double destinationLng = 0.0;
                    if(map.get("destinationLat") != null){
                        destinationLat = Double.valueOf(map.get("destinationLat").toString());
                    }
                    if(map.get("destinationLng") != null){
                        destinationLng = Double.valueOf(map.get("destinationLng").toString());
                        destinationLatLng = new LatLng(destinationLat, destinationLng);
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void getAssignedCustomer() {
        String driverId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference assignedCustomerRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(driverId).child("customerRequest").child("customerRideId");
        assignedCustomerRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    status = 1;
                    customerId = dataSnapshot.getValue().toString();
                    getAssignedCustomerPickupLocation();
                    getAssignedCustomerDestination();
                    getAssignedCustomerInfo();
                } else {
                    endRide();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    Marker pickupMarker;
    private DatabaseReference assignedCustomerPickupLocationRef;
    private ValueEventListener assignedCustomerPickupLocationRefListener;

    private void getAssignedCustomerPickupLocation() {
        assignedCustomerPickupLocationRef = FirebaseDatabase.getInstance().getReference().child("customerRequest").child(customerId).child("l");
        assignedCustomerPickupLocationRefListener = assignedCustomerPickupLocationRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && !customerId.equals("")){
                    List<Object> map = (List<Object>) dataSnapshot.getValue();
                    double locationLat = 0;
                    double locationLng = 0;
                    if(map.get(0) != null){
                        locationLat = Double.parseDouble(map.get(0).toString());
                    }
                    if(map.get(1) != null){
                        locationLng = Double.parseDouble(map.get(1).toString());
                    }
                    LatLng pickupLatLng = new LatLng(locationLat,locationLng);
                    pickupMarker = mMap.addMarker(new MarkerOptions().position(pickupLatLng).title("pickup location").icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_pickup)));
                    getRouteToMarker(pickupLatLng);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void getRouteToMarker(LatLng pickupLatLng) {
        Routing routing = new Routing.Builder()
                .travelMode(AbstractRouting.TravelMode.DRIVING)
                .withListener(this)
                .alternativeRoutes(false)
                .waypoints(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), pickupLatLng)
                .key("AIzaSyCuXrm4aQhDqGypxTvuxA-BDye2zRIfy7g")
                .build();
        routing.execute();
    }


    private void getAssignedCustomerInfo(){
        mCustomerInfo.setVisibility(View.VISIBLE);
        DatabaseReference mCustomerDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(customerId);
        mCustomerDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0){
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if(map.get("name")!=null){

                        mCustomerName.setText( map.get("name").toString());
                    }
                    if(map.get("phone")!=null){
                        mCustomerPhone.setText(map.get("phone").toString());
                    }
                    if(map.get("car")!=null){
                        mCustomerCar.setText(map.get("car").toString());
                    }
                    if(map.get("problem")!=null){
                        mCustomerProblem.setText(map.get("problem").toString());
                    }

                    if(map.get("profileImageUrl")!=null){

                        Glide.with(getApplication()).load(map.get("profileImageUrl").toString()).into(mCustomerProfileImage);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void endRide (){
        mRideStatus.setText("picked customer");
        erasePolylines();



        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference driverRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(userId).child("customerRequest");
        driverRef.removeValue();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("customerRequest");
        GeoFire geoFire = new GeoFire(ref);
        geoFire.removeLocation(customerId);
        customerId = "";
        rideDistance = 0;




        if(pickupMarker != null){
            pickupMarker.remove();
        }
        if (assignedCustomerPickupLocationRefListener != null) {
            assignedCustomerPickupLocationRef.removeEventListener(assignedCustomerPickupLocationRefListener);
        }
        mCustomerInfo.setVisibility(View.GONE);
        mCustomerName.setText("");
        mCustomerPhone.setText("");
        mCustomerDestination.setText("Destination--");
        mCustomerProfileImage.setImageResource(R.mipmap.ic_default_user);

    }

    private void recordRide(){
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference driverRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(userId).child("history");
        DatabaseReference customerRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(customerId).child("history");
        DatabaseReference historyRef = FirebaseDatabase.getInstance().getReference().child("history");
        String requestId = historyRef.push().getKey();
        driverRef.child(requestId).setValue(true);
        customerRef.child(requestId).setValue(true);

        HashMap map = new HashMap();
        map.put("driver", userId);
        map.put("customer", customerId);
        map.put("rating", 0);
        map.put("timestamp", getCurrentTimestamp());
        map.put("destination", destination);
        map.put("distance", rideDistance);
        historyRef.child(requestId).updateChildren(map);


    }

    private Long getCurrentTimestamp() {
        Long timestamp = System.currentTimeMillis()/1000;
        return timestamp;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        buildGoogleApiClient();
        mMap.setMyLocationEnabled(true);
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        if (getApplicationContext() != null) {

            if(!customerId.equals("")){
                rideDistance += mLastLocation.distanceTo(location)/1000;
            }
            if (firstLoad) {
                mMap.animateCamera( CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(),location.getLongitude()) , 11) );
                firstLoad = false;
            }
            mLastLocation = location;



            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            DatabaseReference refAvailable = FirebaseDatabase.getInstance().getReference("mechsAvailable");
            DatabaseReference refWorking = FirebaseDatabase.getInstance().getReference("mechsWorking");
            GeoFire geoFireAvailable = new GeoFire(refAvailable);
            GeoFire geoFireWorking = new GeoFire(refWorking);

            switch (customerId) {
                case "":
                    geoFireWorking.removeLocation(userId);
                    geoFireAvailable.setLocation(userId, new GeoLocation(location.getLatitude(), location.getLongitude()));
                    break;

                default:
                    geoFireAvailable.removeLocation(userId);
                    geoFireWorking.setLocation(userId, new GeoLocation(location.getLatitude(), location.getLongitude()));
                    break;
            }


        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);


    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    private void connectDriver(){
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MechanicMapActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }


    private void disconnectDriver() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("mechsAvailable");

        GeoFire geoFire = new GeoFire(ref);
        geoFire.removeLocation(userId);
        DatabaseReference refWorking = FirebaseDatabase.getInstance().getReference("mechsWorking");

        GeoFire geoFireWorking = new GeoFire(refWorking);
        geoFireWorking.removeLocation(userId);


    }

    final int LOCATION_REQUEST_CODE = 1;
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch(requestCode){
            case LOCATION_REQUEST_CODE:{
                if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    mapFragment.getMapAsync(this);
                } else{
                    Toast.makeText(getApplicationContext(), "Please provide the permission", Toast.LENGTH_LONG).show();
                }
                break;
            }
        }
    }



    private List<Polyline> polylines;
    private static final int[] COLORS = new int[]{R.color.primary_dark_material_light};
    @Override
    public void onRoutingFailure(RouteException e) {
        if(e != null) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }else {
            Toast.makeText(this, "Something went wrong, Try again", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onRoutingStart() {
    }
    @Override
    public void onRoutingSuccess(ArrayList<Route> route, int shortestRouteIndex) {
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
    @Override
    public void onRoutingCancelled() {
    }
    private void erasePolylines(){
        for(Polyline line : polylines){
            line.remove();
        }
        polylines.clear();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
