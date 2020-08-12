package com.miggy.mekaniko;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
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
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class HistorySingleActivity extends AppCompatActivity {
    private String rideId, currentUserId, customerId, driverId, userDriverOrCustomer, userID;

    private TextView rideLocation;
    private TextView rideDistance;
    private TextView rideDate;
    private TextView userName;
    private TextView userPhone;
    private TextView paymStatuss;

    private ImageView userImage;

    private RatingBar mRatingBar;

    private Button mPay, mPayC;

    private Boolean customerPaid = false;

    private DatabaseReference historyRideInfoDb;

    private DatabaseReference userDb, MechanicDb;

    private LatLng destinationLatLng, pickupLatLng;
    private String distance;
    private Double ridePrice;
    FirebaseAuth auth;
    private GoogleMap mMap;
    private SupportMapFragment mMapFragment;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_single);

        Intent intent = new Intent(this, PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        startService(intent);



        rideId = getIntent().getExtras().getString("rideId");




        paymStatuss = (TextView) findViewById(R.id.paymStatuss);
        rideDistance = (TextView) findViewById(R.id.rideDistance);
        rideDate = (TextView) findViewById(R.id.rideDate);

        userName = (TextView) findViewById(R.id.userName);
        userPhone = (TextView) findViewById(R.id.userPhone);


        userImage = (ImageView) findViewById(R.id.userImage);

        mRatingBar = (RatingBar) findViewById(R.id.ratingBar);

        mPay = (Button) findViewById(R.id.pay);
        mPayC = (Button) findViewById(R.id.paycash);

        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        auth = FirebaseAuth.getInstance();
        userID = auth.getCurrentUser().getUid();
        userDb = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(userID).child("Paystatus");
        MechanicDb = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child("PendingBalance");

        historyRideInfoDb = FirebaseDatabase.getInstance().getReference().child("history").child(rideId);
        getRideInformation();
    }

    private void getRideInformation() {
        historyRideInfoDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        if (child.getKey().equals("customer")) {
                            customerId = child.getValue().toString();
                            if (!customerId.equals(currentUserId)) {
                                userDriverOrCustomer = "Drivers";
                                getUserInformation("Customers", customerId);
                            }
                        }
                        if (child.getKey().equals("driver")) {
                            driverId = child.getValue().toString();
                            if (!driverId.equals(currentUserId)) {
                                userDriverOrCustomer = "Customers";
                                getUserInformation("Drivers", driverId);
                                displayCustomerRelatedObjects();
                            }
                        }
                        if (child.getKey().equals("timestamp")) {
                            rideDate.setText(getDate(Long.valueOf(child.getValue().toString())));
                        }
                        if (child.getKey().equals("rating")) {
                            mRatingBar.setRating(Integer.valueOf(child.getValue().toString()));

                        }
                        if (child.getKey().equals("customerPaid")) {
                            customerPaid = true;
                            if(customerPaid == true){
                                paymStatuss.setText("payed");
                            }
                        }
                        if (child.getKey().equals("distance")) {
                            distance = child.getValue().toString();
                            rideDistance.setText(distance.substring(0, Math.min(distance.length(), 5)) + " km");
                            ridePrice = Double.valueOf(distance) + 500;

                        }
                        if (child.getKey().equals("destination")) {
                            rideLocation.setText(child.getValue().toString());
                        }
                        if (child.getKey().equals("location")) {
                            pickupLatLng = new LatLng(Double.valueOf(child.child("from").child("lat").getValue().toString()), Double.valueOf(child.child("from").child("lng").getValue().toString()));
                            destinationLatLng = new LatLng(Double.valueOf(child.child("to").child("lat").getValue().toString()), Double.valueOf(child.child("to").child("lng").getValue().toString()));
                            if (destinationLatLng != new LatLng(0, 0)) {

                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }


    private void displayCustomerRelatedObjects() {
        mRatingBar.setVisibility(View.VISIBLE);
        mPay.setVisibility(View.VISIBLE);
        mPayC.setVisibility(View.VISIBLE);
        mRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                historyRideInfoDb.child("rating").setValue(rating);
                DatabaseReference mDriverRatingDb = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(driverId).child("rating");
                mDriverRatingDb.child(rideId).setValue(rating);
            }
        });
        if (customerPaid) {
            mPay.setEnabled(false);
            mPayC.setEnabled(false);
        } else {
            mPay.setEnabled(true);
            mPayC.setEnabled(true);
        }
        mPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                payPalPayment();
            }
        });

        mPayC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference mDriverPendingstatus = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(driverId).child("PendingBalance");
                mDriverPendingstatus.setValue("unpayed");
                mPay.setEnabled(false);
                mPayC.setEnabled(false);
                Toast.makeText(getApplicationContext(), "Payment successful", Toast.LENGTH_LONG).show();
                historyRideInfoDb.child("customerPaid").setValue(true);
                userDb.child("Paystatus").setValue("payed");
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
                            historyRideInfoDb.child("customerPaid").setValue(true);
                            mPay.setEnabled(false);
                            userDb.child("Paystatus").setValue("payed");


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

    private void getUserInformation(String otherUserDriverOrCustomer, String otherUserId) {
        DatabaseReference mOtherUserDB = FirebaseDatabase.getInstance().getReference().child("Users").child(otherUserDriverOrCustomer).child(otherUserId);
        mOtherUserDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if (map.get("name") != null) {
                        userName.setText(map.get("name").toString());
                    }
                    if (map.get("phone") != null) {
                        userPhone.setText(map.get("phone").toString());
                    }

                    if (map.get("profileImageUrl") != null) {
                        Glide.with(getApplication()).load(map.get("profileImageUrl").toString()).into(userImage);
                    }

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private String getDate(Long timestamp) {
        Calendar cal = Calendar.getInstance(Locale.getDefault());
        cal.setTimeInMillis(timestamp * 1000);
        String date = DateFormat.format("MM-dd-yyyy hh:mm", cal).toString();
        return date;
    }

}