package com.deedee.ddp.ddp;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.AutocompletePredictionBuffer;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.SphericalUtil;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;


public class MainActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    public enum appStatus {
        NORNAL,
        CONFIRM,
        PICKUP,
        DROPOFF;
    }

    appStatus appState;

    private static final String API_KEY = "AIzaSyCmPTaiGOOsgaNp6tQJ91vbTSNjttvl5kk";
    private GoogleApiClient mGoogleApiClient;


    JSONObject confirmJson;
    JSONObject pickupJson;
    JSONObject dropoffJson;

    HashMap<String, JSONObject> advOrders = new HashMap<String, JSONObject>();

    HashMap<String, PendingIntent> pendingIntents = new HashMap<String, PendingIntent>();
    private PendingIntent pendingIntent;

    MqttAndroidClient mqttClient;
    String acceptedDriverID;


    FrameLayout arrivalTimeFrame;
    FrameLayout noteToDriverFrame;
    FrameLayout bottomFrame;

    TextView driverMobileText;
    TextView licensePlateNumber;
    TextView driverNameText;

    String[] carTypeCode = {"1", "2", "2", "3", "7"};
    String[] carTypeDetail = {"Sedan", "SUV", "Lux car","Van", "Tuk Tuk"};

    String selectedCarCode;
    String selectedPrice;


    String memberID;
    String memberFirstname;
    String memberLastname;
    String memberMobile;
    String promotionCode = "NULL";

    String userPhoneNumber = "";

    String OrderID;
    String advOrderID;

    GoogleCloudMessaging gcm;
    String regid;
    String PROJECT_NUMBER = "216681423972";   //"290951438564";


    private GoogleMap mMap;
    SupportMapFragment mMapFragment;

    LocationManager locationManager;
    boolean firstRun = true;
    int mapCount = 0;

    GridLayout fromButton;
    GridLayout toButton;

    Button callButton;
    Button confirmButton;
    Button cancelButton;
    Button cancelMiddleButton;
    ImageButton dateTimeSelectionButton;

    ImageButton historyButton;
    Button backFromHistoryButton;
    LinearLayout historyView;
    ListView historyListView;

    Button backFromSearch;
    TextView deedeePhuketTextView;

    Button sedanButton;
    Button suvButton;
    Button luxcarButton;
    Button vanButton;
    Button tuktukButton;

    Button bookmarkButton;
    JSONArray bookmarkJSONArray;
    ArrayList<String> bookmarkNames = new ArrayList<String>();

    TextView fromTextView;
    TextView fromAddressTextView;
    TextView toTextView;
    TextView toAddressTextView;

    TextView arrivalTime;
    TextView arrivalDistance;

    TextView detailCarType;
    TextView noteToDriver;


    constants cc = new constants();
    public final String address = cc.address;
    public final String newHost = "http://" + address + ":3000";
    public final String mqttHost = "tcp://" + address + ":1883";
    public final String landMarkUrl = newHost + "/api/landmark";
    public final String addUser = newHost + "/api/member";
    public final String memberSignin = newHost + "/api/startup";
    public final String callTaxiUrl = newHost + "/api/order";
    public final String rating = newHost + "/api/rating";
    public final String calprice = newHost + "/api/pricing";
    public final String advBooking = newHost + "/api/advBooking";
    String removeOrder = newHost + "/api/removeOrder";
    String getAdvOrder = newHost + "/api/getAdvOrder";
    //public final static String tracking = newHost + "/api/track";

    //public final static String mainHost = "http://pdnmobileserver.azurewebsites.net";




    FrameLayout searchFrame;
    LinearLayout fromToFrame;
    LinearLayout searchTableView;
    LinearLayout driverDetail;
    SearchView search;
    ListView searchListView;


    Location currentLoction;


    String isFirstRide = "false";
    Boolean isConfirm = false;

    @Override
    public void onConnected(Bundle bundle) {
        Log.d("HTTP", "GOOLE CLIENT OK");
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d("HTTP", "GOOLE CLIENT FAIL");

    }


    public class bookmarkLocDetail {
        public String name;
        public String phoneNUmber;
        public Location location;
    }

    public class locDetail {
        public String name;
        public String address;
        public Location location;
        public double distance;
        public int CategoryId;
        public String PhoneNumber = "N/A";
    }

    ArrayList<locDetail> locAll = new ArrayList<locDetail>();
    ArrayList<placeItem> placeList = new ArrayList<placeItem>();

    locDetail fromWhere = new locDetail();
    locDetail toWhere = new locDetail();
    boolean fromOrTo;


    ProgressDialog dialog;
    ProgressDialog waitForTaxiDialog;

    Timer timer;



    String pickupDateTime;
    Boolean isDestinationSet = false;
    Boolean isPickedUp = false;
    Boolean isUpdateMap = false;


    TextView pricetextView;


    int taxiRouteColor = Color.RED;
    int destinationRouteColor = Color.GREEN;


    int mYear, mMonth, mDay, mHour, mMinute;


    Handler handler=  new Handler();
    Runnable timeoutRunable = new Runnable() {
        public void run() {
            // do something
            if(waitForTaxiDialog != null) {
                waitForTaxiDialog.dismiss();
            }
            handler.removeCallbacks(timeoutRunable);
            final RequestQueue queue = Volley.newRequestQueue(getBaseContext());
            StringRequest putRequest = new StringRequest(Request.Method.PUT, removeOrder,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            // response
                            Log.d("HTTP", response);
                            try {

                                JSONObject obj1 = new JSONObject(response);

                                if (obj1.getString("Status").equals("SUCCESS")) {
                                    Log.d("HTTP", "remove order OK");
                                }
                            } catch (Throwable t) {
                                Log.e("HTTP", "Could not parse malformed JSON: \"" + response + "\"");
                            }


                            //Log.d("Response", response);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // error
                            Log.d("HTTP", error.toString());
                        }
                    }
            ) {

                @Override
                protected Map<String, String> getParams() {

                    Map<String, String> params = new HashMap<String, String>();
                    params.put("Id", OrderID);
                    Log.d("HTTP",params.toString());

                    return params;
                }

            };

            queue.add(putRequest);


            new AlertDialog.Builder(new ContextThemeWrapper(MainActivity.this, android.R.style.Theme_DeviceDefault_Light_Dialog))
                    .setTitle("Timeout")
                    .setMessage("No respond in time.")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            JSONObject data = new JSONObject();
                            try {
                                data.put("MessageType", 300);
                                String payload = data.toString();
                                byte[] encodedPayload = new byte[0];
                                try {
                                    encodedPayload = payload.getBytes("UTF-8");
                                    MqttMessage message = new MqttMessage(encodedPayload);
                                    mqttClient.publish(acceptedDriverID, message);


                                    Log.d("HTTP", "Cancel OK");

                                    callButton.setEnabled(false);
                                    callButton.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
                                    confirmButton.setVisibility(View.VISIBLE);
                                    noteToDriverFrame.setVisibility(View.VISIBLE);
                                    arrivalTimeFrame.setVisibility(View.VISIBLE);
                                    bottomFrame.setVisibility(View.VISIBLE);
                                    driverDetail.setVisibility(View.GONE);
                                    mMap.clear();



                                } catch (UnsupportedEncodingException | MqttException e) {
                                    e.printStackTrace();
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    })
                    .show().setCanceledOnTouchOutside(false);;
        }
    };



    //region Save states
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("confirmJson", confirmJson.toString());
        outState.putBoolean("isConfirm", isConfirm);

        outState.putSerializable("appState", appState);



    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        String confirmJsonString = savedInstanceState.getString("confirmJson");
        appState  = (appStatus) savedInstanceState.getSerializable("appState");
        isConfirm = savedInstanceState.getBoolean("isConfirm");
        try {
            confirmJson = new JSONObject(confirmJsonString);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    //endregion


    @Override
    public void onResume() {
        super.onResume();


        if(appState == appStatus.CONFIRM) {
            showConfirmState(confirmJson);
        } else if(appState == appStatus.PICKUP) {
            showPickupState(confirmJson);
        } else if(appState == appStatus.DROPOFF){
            showDropoffState(confirmJson);
        }


        Bundle extras = getIntent().getExtras();
        if (extras != null) {

            if(waitForTaxiDialog !=null)
            {
                waitForTaxiDialog = null;
                timerDelayRemoveDialog(cc.timeout, waitForTaxiDialog);
            }

            //waitForTaxiDialog = ProgressDialog.show(MainActivity.this, "", "Waiting for taxi...", true);


        }

    }


    @Override
    public void onStop(){
        super.onStop();

        final RequestQueue queue = Volley.newRequestQueue(getBaseContext());
        StringRequest putRequest = new StringRequest(Request.Method.PUT, removeOrder,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("HTTP", response);
                        try {

                            JSONObject obj1 = new JSONObject(response);

                            if (obj1.getString("Status").equals("SUCCESS")) {
                                Log.d("HTTP", "remove order OK");
                            }
                        } catch (Throwable t) {
                            Log.e("HTTP", "Could not parse malformed JSON: \"" + response + "\"");
                        }


                        //Log.d("Response", response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("HTTP", error.toString());
                    }
                }
        ) {

            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<String, String>();
                params.put("Id", OrderID);
                Log.d("HTTP",params.toString());

                return params;
            }

        };

        queue.add(putRequest);


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mGoogleApiClient.disconnect();
        if (waitForTaxiDialog != null) {
            waitForTaxiDialog.dismiss();
            waitForTaxiDialog = null;
        }

        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
    }

    //region ******************************** MQTT ********************************************
    //**********************************************************************************************
    //              MQTT
    //**********************************************************************************************

    private MqttAndroidClient mqttSetup(String memberID){

        final String ddID = memberID;
        String clientId = MqttClient.generateClientId();
        final MqttAndroidClient client =
                new MqttAndroidClient(this.getApplicationContext(), mqttHost,
                        clientId);

        client.setCallback(new MqttCallback() {

            @Override
            public void connectionLost(Throwable cause) { //Called when the client lost the connection to the broker

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                if (topic.equals(ddID)) {

                    JSONObject messJson = new JSONObject(message.toString());

                    int mType = messJson.getInt("MessageType");

                    Log.d("HTTP", "mType = " + mType);

                    if (mType == 100){
                        //order confirm

                        confirmJson = messJson;
                        appState = appStatus.CONFIRM;

                        showConfirmState(confirmJson);




                    } else if (mType == 200) {

                        //Pickup

                        pickupJson = messJson;
                        appState = appStatus.PICKUP;
                        showPickupState(confirmJson);


                    } else if (mType == 300) {

                        //Drop off
                        dropoffJson = messJson;
                        appState = appStatus.DROPOFF;
                        showDropoffState(confirmJson);

                    } else if (mType == 400) {

                        //Driver update location

                        LatLng fromLatLng = new LatLng(messJson.getDouble("lat"), messJson.getDouble("lon"));
                        if(isPickedUp == false) {
                            LatLng toLatLng = new LatLng(fromWhere.location.getLatitude(), fromWhere.location.getLongitude());
                            updateMap(fromLatLng, toLatLng, 0, taxiRouteColor);
                        } else {
                            LatLng toLatLng = new LatLng(toWhere.location.getLatitude(), toWhere.location.getLongitude());
                            updateMap(fromLatLng, toLatLng, 0, destinationRouteColor);
                        }



                    } else if (mType == 500){
                        //Driver cancel
                        final View view = findViewById(android.R.id.content);
                        ContextThemeWrapper ctw = new ContextThemeWrapper(view.getContext(), R.style.Theme_AppCompat_Light);
                        AlertDialog.Builder builder = new AlertDialog.Builder(ctw);
                        builder.setTitle("Driver cancel");


                        // Set up the buttons
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                mMap.clear();
                                callButton.setEnabled(false);
                                callButton.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
                                confirmButton.setVisibility(View.VISIBLE);
                                noteToDriverFrame.setVisibility(View.VISIBLE);
                                arrivalTimeFrame.setVisibility(View.VISIBLE);
                                bottomFrame.setVisibility(View.VISIBLE);
                                cancelButton.setVisibility(View.VISIBLE);
                                cancelMiddleButton.setVisibility(View.GONE);
                                driverDetail.setVisibility(View.GONE);


                            }
                        });

                        final AlertDialog dialog = builder.create();
                        dialog.setCanceledOnTouchOutside(false);
                        dialog.show();



                    } else if(mType == 600) {

                        //Set alarm


                        final String advOrderID = messJson.getString("orderID");

                        final JSONObject acceptedAdvOrder = advOrders.get(advOrderID);


                        Log.d("HTTP", acceptedAdvOrder.toString());

                        waitForTaxiDialog.dismiss();
                        handler.removeCallbacks(timeoutRunable);

                        //Driver cancel
                        final View view = findViewById(android.R.id.content);
                        ContextThemeWrapper ctw = new ContextThemeWrapper(view.getContext(), R.style.Theme_AppCompat_Light);
                        AlertDialog.Builder builder = new AlertDialog.Builder(ctw);
                        builder.setTitle("Driver confirm advance booking");


                        // Set up the buttons
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {


                                //set alarm
                                try {
                                    long pickupTime = acceptedAdvOrder.getLong("PickupDateTime") - 30*1000;


                                    Intent myIntent = new Intent(MainActivity.this, alarmReceiver.class);
                                    final int _id = (int) System.currentTimeMillis();
                                    pendingIntent = PendingIntent.getBroadcast(MainActivity.this, _id, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                                    myIntent.putExtra("orderID", advOrderID); //data to pass
                                    Log.d("HTTP","ADV Order ID = " +  advOrderID);
                                    pendingIntents.put(advOrderID, pendingIntent);

                                    AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                                    alarmManager.set(AlarmManager.RTC_WAKEUP, pickupTime, pendingIntent);
                                    // alarmManager.set(AlarmManager.RTC, pickupTime, pendingIntent);


                                    Log.d("HTTP", "Time = " + pickupTime);
                                } catch (Throwable t) {
                                    Log.e("HTTP", "Could not parse malformed JSON");
                                }


                            }
                        });

                        final AlertDialog dialog = builder.create();
                        dialog.setCanceledOnTouchOutside(false);
                        dialog.show();


                    }
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {//Called when a outgoing publish is complete
            }
        });

        try {
            IMqttToken token = client.connect();
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // We are connected
                    Log.d("HTTP", "onSuccess");



                    int qos = 1;
                    try {
                        IMqttToken subToken = client.subscribe(ddID, qos);
                        subToken.setActionCallback(new IMqttActionListener() {
                            @Override
                            public void onSuccess(IMqttToken asyncActionToken) {
                                // The message was published
                            }



                            @Override
                            public void onFailure(IMqttToken asyncActionToken,
                                                  Throwable exception) {
                                // The subscription could not be performed, maybe the user was not
                                // authorized to subscribe on the specified topic e.g. using wildcards

                            }
                        });
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }





                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Something went wrong e.g. connection timeout or firewall problems
                    Log.d("HTTP", "onFailure");

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }

        return client;

    }
    //endregion


    private void showConfirmState(JSONObject messJson){
        isUpdateMap = false;



        detailCarType.setText(carTypeDetail[Integer.parseInt(selectedCarCode) - 1]);
        String driverImage = null;
        try {
            driverImage = "http://" + address + ":8080/" +  messJson.getString("acceptedDriverID") + ".png";
            String driverName = messJson.getString("DriverName");
            String driverLicensePlate = messJson.getString("DriverLicensePlate");
            String driverMobile = messJson.getString("DriverMobile");
            driverMobileText.setText("Mobile : " + driverMobile);
            licensePlateNumber.setText(driverLicensePlate);
            driverNameText.setText(driverName);
            acceptedDriverID = messJson.getString("acceptedDriverID");
        } catch (JSONException e) {
            e.printStackTrace();
        }



        Log.d("HTTP", driverImage);

        new DownloadImageTask((ImageView) findViewById(R.id.driverImage))
                .execute(driverImage);


        driverDetail.setVisibility(View.VISIBLE);
        noteToDriver.setText("Note to driver :");
        confirmButton.setVisibility(View.VISIBLE);
        cancelMiddleButton.setVisibility(View.GONE);
        cancelButton.setVisibility(View.VISIBLE);
        waitForTaxiDialog.dismiss();
        handler.removeCallbacks(timeoutRunable);

        if(isConfirm == true) {
            cancelButton.setVisibility(View.GONE);
            cancelMiddleButton.setVisibility(View.VISIBLE);
            confirmButton.setVisibility(View.GONE);
        }




    }


    private void showPickupState(JSONObject messJson){


        detailCarType.setText(carTypeDetail[Integer.parseInt(selectedCarCode) - 1]);
        String driverImage = null;
        try {
            driverImage = "http://" + address + ":8080/" +  messJson.getString("acceptedDriverID") + ".png";
            String driverName = messJson.getString("DriverName");
            String driverLicensePlate = messJson.getString("DriverLicensePlate");
            String driverMobile = messJson.getString("DriverMobile");
            driverMobileText.setText("Mobile : " + driverMobile);
            licensePlateNumber.setText(driverLicensePlate);
            driverNameText.setText(driverName);
            acceptedDriverID = messJson.getString("acceptedDriverID");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mMap.clear();
        isPickedUp = true;
        isUpdateMap = false;

                        /* if (timer != null) {
                            timer.cancel();
                        }*/




        callButton.setEnabled(false);
        confirmButton.setVisibility(View.GONE);
        noteToDriverFrame.setVisibility(View.GONE);
        arrivalTimeFrame.setVisibility(View.GONE);
        bottomFrame.setVisibility(View.GONE);

        LatLng fromLatLng = new LatLng(fromWhere.location.getLatitude(), fromWhere.location.getLongitude());
        LatLng toLatLng = new LatLng(toWhere.location.getLatitude(), toWhere.location.getLongitude());

        updateMap(fromLatLng, toLatLng, 1, destinationRouteColor);
        Log.d("HTTP", "Pick up");
    }


    private void showDropoffState(JSONObject messJson){

        isConfirm = false;
        detailCarType.setText(carTypeDetail[Integer.parseInt(selectedCarCode) - 1]);
        String driverImage = null;
        try {
            driverImage = "http://" + address + ":8080/" +  messJson.getString("acceptedDriverID") + ".png";
            String driverName = messJson.getString("DriverName");
            String driverLicensePlate = messJson.getString("DriverLicensePlate");
            String driverMobile = messJson.getString("DriverMobile");
            driverMobileText.setText("Mobile : " + driverMobile);
            licensePlateNumber.setText(driverLicensePlate);
            driverNameText.setText(driverName);
            acceptedDriverID = messJson.getString("acceptedDriverID");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        appState = appStatus.NORNAL;

        mMap.clear();


        isPickedUp = false;
        isUpdateMap = false;

        final View view = findViewById(android.R.id.content);


        ContextThemeWrapper ctw = new ContextThemeWrapper(view.getContext(), R.style.Theme_AppCompat_Light);
        AlertDialog.Builder builder = new AlertDialog.Builder(ctw);
        builder.setTitle("Arrive");

               /* TextView mTitle = (TextView)findViewById(android.R.id.title);
                mTitle.setTextColor(Color.GREEN);*/
               /* int x = Resources.getSystem().getIdentifier("titleDivider", "id", "android");
                View titleDivider = findViewById(x);
                titleDivider.setBackgroundColor(getResources().getColor(R.color.button_material_light));*/


        LinearLayout layout = new LinearLayout(view.getContext());
        layout.setOrientation(LinearLayout.VERTICAL);

        // Set up the input
        final EditText input = new EditText(view.getContext());
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setHint("Comment");

        // Set up the input

        final RatingBar rr = new RatingBar(view.getContext());
        LayerDrawable stars = (LayerDrawable) rr.getProgressDrawable();
        stars.getDrawable(2).setColorFilter(Color.rgb(248, 68, 110), PorterDuff.Mode.SRC_ATOP);


        rr.setMax(5);
        rr.setNumStars(5);
        rr.setStepSize(0.5f);
        rr.setRating(4.0f);
        rr.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT));


        layout.addView(rr);
        layout.addView(input);


        builder.setView(layout);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                final RequestQueue queue = Volley.newRequestQueue(getBaseContext());
                StringRequest putRequest = new StringRequest(Request.Method.POST, rating,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                // response
                                Log.d("HTTP", response);
                                try {

                                    JSONObject obj = new JSONObject(response);

                                    if (obj.getString("Status").equals("SUCCESS")) {
                                        mMap.clear();

                                        Log.d("HTTP", "Rating OK");
                                        callButton.setEnabled(false);
                                        callButton.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
                                        confirmButton.setVisibility(View.VISIBLE);
                                        noteToDriverFrame.setVisibility(View.VISIBLE);
                                        arrivalTimeFrame.setVisibility(View.VISIBLE);
                                        bottomFrame.setVisibility(View.VISIBLE);
                                        cancelButton.setVisibility(View.VISIBLE);
                                        cancelMiddleButton.setVisibility(View.GONE);
                                        driverDetail.setVisibility(View.GONE);

                                        try {
                                            InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                            inputManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                                        } catch (Exception e) {
                                        }

                                    }

                                } catch (Throwable t) {
                                    Log.e("My App", "Could not parse malformed JSON: \"" + response + "\"");
                                }


                                //Log.d("Response", response);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                // error
                                Log.d("HTTP", error.toString());
                            }
                        }
                ) {

                    @Override
                    protected Map<String, String> getParams() {

                        Map<String, String> params = new HashMap<String, String>();

                        params.put("Action", "1");
                        params.put("DriverID", acceptedDriverID);
                        params.put("RatingValue", String.valueOf(rr.getRating()));
                        params.put("Comment", input.getText().toString());


                        return params;
                    }

                };

                queue.add(putRequest);


            }
        });

        final AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

    }







    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        isConfirm = false;

        appState = appStatus.NORNAL;

        registerReceiver(localAlarmReceiver, new IntentFilter(alarmReceiver.NOTIFICATION));

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
      /*  if(dialog !=null)
        {
            dialog = null;
        }*/
        dialog = ProgressDialog.show(MainActivity.this, "", "Loading. Please wait...", true);
        dialog.setCanceledOnTouchOutside(false);

        getRegId();





        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .enableAutoManage(this, 0 /* clientId */, this)
                .addApi(Places.GEO_DATA_API)
                .addApi(LocationServices.API)
                .build();


        mGoogleApiClient.connect();



        mMapFragment = (SupportMapFragment) (getSupportFragmentManager()
                .findFragmentById(R.id.map));

        arrivalTimeFrame = (FrameLayout) findViewById(R.id.arrivalTimeFrame);
        noteToDriverFrame = (FrameLayout) findViewById(R.id.noteToDriverFrame);
        bottomFrame = (FrameLayout) findViewById(R.id.buttomFrame);
        driverMobileText = (TextView) findViewById(R.id.driverMobileNumber);

        licensePlateNumber = (TextView) findViewById(R.id.licensePlateNumber);
        driverNameText = (TextView) findViewById(R.id.driverName);

        driverDetail = (LinearLayout) findViewById(R.id.driverDetail);
        driverDetail.setVisibility(View.GONE);


        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        final String currentDateAndTime = sdf.format(new Date());
        pickupDateTime = currentDateAndTime;

        selectedCarCode = "1";
        pricetextView = (TextView) findViewById(R.id.priceTextView);

        arrivalTime = (TextView) findViewById(R.id.arrivalTime);
        arrivalDistance = (TextView) findViewById(R.id.arrivalDistance);

        detailCarType = (TextView) findViewById(R.id.cartypeText);
        noteToDriver = (TextView) findViewById(R.id.noteToDriver);

        deedeePhuketTextView = (TextView) findViewById(R.id.deedeeText);
        backFromSearch = (Button) findViewById(R.id.backFromSearch);

        final RequestQueue queue = Volley.newRequestQueue(this);

        fromTextView = (TextView) findViewById(R.id.fromTextView);
        fromAddressTextView = (TextView) findViewById(R.id.fromAddressTextView);
        toTextView = (TextView) findViewById(R.id.toTextView);
        toAddressTextView = (TextView) findViewById(R.id.toAddressTextView);

        //region History button
        //******************************************************************************************
        //                              History buttons
        //******************************************************************************************
        historyListView = (ListView) findViewById(R.id.historyListView);
        historyView = (LinearLayout) findViewById(R.id.historyView);
        backFromHistoryButton = (Button) findViewById(R.id.backFromHistoryButton);
        backFromHistoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                historyView.setVisibility(View.GONE);
            }

        });


        historyButton = (ImageButton) findViewById(R.id.historyButton);
        historyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                historyView.setVisibility(View.VISIBLE);
                final RequestQueue queue = Volley.newRequestQueue(getBaseContext());
                StringRequest putRequest = new StringRequest(Request.Method.PUT, callTaxiUrl,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                // response
                                Log.d("HTTP", response);
                                try {

                                    JSONObject obj = new JSONObject(response);

                                    if (obj.getString("Status").equals("SUCCESS")) {
                                        Log.d("HTTP", "History OK");
                                        Log.d("HTTP", obj.toString());

                                        JSONArray orderDetailArray = new JSONArray(obj.getString("Orders"));
                                        ArrayList<historyItem> historyList = new ArrayList<historyItem>();


                                        for (int i = 0; i < orderDetailArray.length(); i++) {

                                            JSONObject orderi = orderDetailArray.getJSONObject(i);

                                            String fromName = orderi.getString("FromWhere");
                                            String toName = orderi.getString("ToWhere");
                                            String price = orderi.getString("Fare");
                                            String driverID = orderi.getString("DriverId");
                                            String driverName = orderi.getString("DriverFirstName") + " " + orderi.getString("DriverLastName") ;
                                            long dateTimeU = orderi.getLong("timestamp");


                                            Date date = new Date(dateTimeU*1000L); // *1000 is to convert minutes to milliseconds
                                            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM, yyyy HH:mm"); // the format of your date


                                            historyItem itm = new historyItem(sdf.format(date),
                                                    fromName + " to " + toName,
                                                    "THB : " + price,
                                                    driverName,
                                                    "",
                                                    driverID,
                                                    "");



                                            historyList.add(i, itm);

                                            // Log.d("My App", place.getString("LocationName"));

                                        }

                                        historyItemAdapter arrayAdapter2 = new historyItemAdapter(getApplicationContext(), android.R.layout.select_dialog_singlechoice, historyList);
                                        historyListView.setAdapter(arrayAdapter2);




                                    }

                                } catch (Throwable t) {
                                    Log.e("My App", "Could not parse malformed JSON: \"" + response + "\"");
                                }


                                //Log.d("Response", response);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                // error
                                Log.d("HTTP", error.toString());
                            }
                        }
                ) {

                    @Override
                    protected Map<String, String> getParams() {

                        Map<String, String> params = new HashMap<String, String>();

                        params.put("Action", "12");
                        params.put("Member", memberID);
                        Log.d("HTTP", params.toString());
                        return params;
                    }

                };

                queue.add(putRequest);
            }

        });
        //endregion

        //region  Date time selection buttons
        //******************************************************************************************
        //                              Date time selection buttons
        //******************************************************************************************




        dateTimeSelectionButton = (ImageButton) findViewById(R.id.dateTimeSelectionButton);

        dateTimeSelectionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Calendar calendar = Calendar.getInstance();
                mDay = calendar.get(Calendar.DAY_OF_MONTH);
                mMonth = calendar.get(Calendar.MONTH) + 1;
                mYear = calendar.get(Calendar.YEAR);
                mMinute = calendar.get(Calendar.MINUTE);
                ;
                mHour = calendar.get(Calendar.HOUR);
                ;


                Log.d("HTTP", "Day = " + mDay);
                Log.d("HTTP", "Mount = " + mMonth);
                Log.d("HTTP", "Year = " + mYear);
                Log.d("HTTP", "Min = " + mMinute);
                Log.d("HTTP", "Hour = " + mHour);

                //LatLng toLatLng1 = new LatLng(toWhere.location.getLatitude(), toWhere.location.getLongitude());

                if (toWhere.location != null) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext())
                            .setTitle("Pickup date and time")
                            .setPositiveButton("Done",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog,
                                                            int which) {

                                            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                                            //Date date = (Date)formatter.parse(str_date);

                                            String s = String.format("%4d-%02d-%02d %02d:%02d", mYear, mMonth, mDay, mHour, mMinute);


                                            //System.out.println("Today is " +date.getTime());

                                            try {
                                                final Date date = (Date) formatter.parse(s);
                                                Log.d("HTTP", s);
                                                Log.d("HTTP", "Today is " + date.getTime());


                                                LatLng fromLatLng = new LatLng(fromWhere.location.getLatitude(), fromWhere.location.getLongitude());

                                                LatLng toLatLng = new LatLng(toWhere.location.getLatitude(), toWhere.location.getLongitude());


                                                updateMap(fromLatLng, toLatLng, 1, destinationRouteColor);

                                                final RequestQueue queue = Volley.newRequestQueue(getBaseContext());

                                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                                                final String currentDateAndTime = sdf.format(new Date());
                                                pickupDateTime = currentDateAndTime;
                                                Log.d("HTTP", currentDateAndTime);

                                                StringRequest putRequest = new StringRequest(Request.Method.PUT, advBooking,
                                                        new Response.Listener<String>() {
                                                            @Override
                                                            public void onResponse(String response) {
                                                                // response
                                                                Log.d("HTTP", response);
                                                                try {

                                                                    JSONObject obj = new JSONObject(response);

                                                                    if (obj.getString("Status").equals("SUCCESS")) {

                                                                        JSONObject order = obj.getJSONObject("SubmittedOrder");

                                                                        Log.d("HTTP", "Order OK");
                                                                        Log.d("HTTP", order.getString("Id"));

                                                                        //OrderID = order.getString("Id");


                                                                        advOrderID = order.getString("Id");
                                                                        advOrders.put(order.getString("Id"), order);

                                                                        if(waitForTaxiDialog !=null)
                                                                        {
                                                                            waitForTaxiDialog = null;
                                                                        }

                                                                        waitForTaxiDialog = ProgressDialog.show(MainActivity.this, "", "Waiting for taxi...", true);
                                                                        timerDelayRemoveDialog(cc.timeout, waitForTaxiDialog);
                                                                        // waitForTaxiDialog.show();
                                                                    }

                                                                } catch (Throwable t) {
                                                                    Log.e("My App", "Could not parse malformed JSON: \"" + response + "\"");
                                                                }


                                                                //Log.d("Response", response);
                                                            }
                                                        },
                                                        new Response.ErrorListener() {
                                                            @Override
                                                            public void onErrorResponse(VolleyError error) {
                                                                // error
                                                                Log.d("HTTP", error.toString());
                                                            }
                                                        }
                                                ) {

                                                    @Override
                                                    protected Map<String, String> getParams() {

                                                        Map<String, String> params = new HashMap<String, String>();

                                                        long orderDateTime = date.getTime();

                                                        params.put("Action", "10");
                                                        params.put("OrderDateTime", String.valueOf(orderDateTime));
                                                        params.put("PickupDateTime", String.valueOf(orderDateTime));
                                                        params.put("BaselinePrice", "100");
                                                        params.put("Incentive", "10");
                                                        params.put("TotalPrice", selectedPrice);
                                                        params.put("FromName", fromWhere.name);
                                                        params.put("FromPhoneNumber", fromWhere.PhoneNumber);

                                                        params.put("FromAddress", fromWhere.address);
                                                        params.put("ToName", toWhere.name);
                                                        params.put("ToPhoneNumber", toWhere.PhoneNumber);

                                                        params.put("ToAddress", toWhere.address);
                                                        params.put("FromLat", String.valueOf(fromWhere.location.getLatitude()));
                                                        params.put("FromLng", String.valueOf(fromWhere.location.getLongitude()));
                                                        params.put("ToLat", String.valueOf(toWhere.location.getLatitude()));
                                                        params.put("ToLng", String.valueOf(toWhere.location.getLongitude()));
                                                        params.put("IsSelfRide", "0");
                                                        params.put("SelfRideString", "0");
                                                        params.put("TimePeriodCode", "0");
                                                        params.put("Member", memberID);
                                                        params.put("PassengerName", memberFirstname + memberLastname);
                                                        params.put("PassengerMobile", memberMobile);
                                                        params.put("OrderTimeType", "1");
                                                        params.put("PassengerMg", "");
                                                        params.put("AdditionalLocationString", " ");
                                                        params.put("CarTypeCode", selectedCarCode);
                                                        params.put("Discount", "0");

                                                        Log.d("HTTP", params.toString());

                                                        return params;
                                                    }

                                                };

                                                queue.add(putRequest);


                                            } catch (ParseException e) {
                                                e.printStackTrace();
                                            }


                                            dialog.dismiss();
                                        }
                                    });
                    final FrameLayout frameView = new FrameLayout(view.getContext());
                    builder.setView(frameView);

                    final AlertDialog alertDialog = builder.create();
                    alertDialog.setCanceledOnTouchOutside(false);
                    LayoutInflater inflater = alertDialog.getLayoutInflater();
                    inflater.inflate(R.layout.date_time_picker, frameView);
                    alertDialog.setCanceledOnTouchOutside(false);
                    alertDialog.show();


                    TimePicker tp = (TimePicker) alertDialog.findViewById(R.id.timePicker1);
                    //tp.setOnTimeChangedListener(myOnTimechangedListener);
                    if (tp == null) {
                        Log.d("HTTP", "tp null");
                    } else {
                        tp.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {

                            @Override
                            public void onTimeChanged(TimePicker arg0, int arg1, int arg2) {
                                // TODO Auto-generated method stub
                                // tv.setText(""+ arg1+" : "+ arg2);
                                // Log.d("HTTP", ""+ arg1+" : "+ arg2);

                                mMinute = arg2;
                                mHour = arg1;
                            }
                        });
                    }


                    DatePicker dp = (DatePicker) alertDialog.findViewById(R.id.datePicker1);

                    dp.init(dp.getYear(), dp.getMonth(), dp.getDayOfMonth(), new DatePicker.OnDateChangedListener() {
                        public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            mYear = year;
                            mMonth = monthOfYear + 1;
                            mDay = dayOfMonth;
                            Log.d("HTTP", year + "/" + mMonth + "/" + dayOfMonth);
                        }
                    });


                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                    builder.setTitle("Please select you destination.");


                    // Set up the buttons
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {




                        }
                    });

                    final AlertDialog dialog = builder.create();
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.show();

                }
            }

        });






        //endregion

        //region car selection buttons
        //******************************************************************************************
        //                              car selection buttons
        //******************************************************************************************

        final Drawable topSedan = getResources().getDrawable(R.drawable.sedan);
        final Drawable topSedanActive = getResources().getDrawable(R.drawable.sedan_active);
        final Drawable topSuv = getResources().getDrawable(R.drawable.suv);
        final Drawable topSuvActive = getResources().getDrawable(R.drawable.suv_active);
        final Drawable topLux = getResources().getDrawable(R.drawable.lux);
        final Drawable topLuxActive = getResources().getDrawable(R.drawable.lux_sedan_active);
        final Drawable topVan = getResources().getDrawable(R.drawable.van);
        final Drawable topVanActive = getResources().getDrawable(R.drawable.van_active);
        final Drawable topTuktuk = getResources().getDrawable(R.drawable.tuktuk);
        final Drawable topTuktukActive = getResources().getDrawable(R.drawable.tuktuk_active);

        sedanButton = (Button) findViewById(R.id.sedanButton);
        suvButton = (Button) findViewById(R.id.suvButton);
        luxcarButton = (Button) findViewById(R.id.luxcarButton);
        vanButton = (Button) findViewById(R.id.vanVutton);
        tuktukButton = (Button) findViewById(R.id.tuktukButton);

        Drawable top = getResources().getDrawable(R.drawable.sedan_active);
        sedanButton.setCompoundDrawablesWithIntrinsicBounds(null, top, null, null);
        sedanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedCarCode = carTypeCode[0];
                sedanButton.setCompoundDrawablesWithIntrinsicBounds(null, topSedanActive, null, null);
                suvButton.setCompoundDrawablesWithIntrinsicBounds(null, topSuv, null, null);
                luxcarButton.setCompoundDrawablesWithIntrinsicBounds(null, topLux, null, null);
                vanButton.setCompoundDrawablesWithIntrinsicBounds(null, topVan, null, null);
                tuktukButton.setCompoundDrawablesWithIntrinsicBounds(null, topTuktuk, null, null);

                if(isDestinationSet == true) {
                    calPrice();
                }
            }
        });

        suvButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedCarCode = carTypeCode[1];
                sedanButton.setCompoundDrawablesWithIntrinsicBounds(null, topSedan, null, null);
                suvButton.setCompoundDrawablesWithIntrinsicBounds(null, topSuvActive, null, null);
                luxcarButton.setCompoundDrawablesWithIntrinsicBounds(null, topLux, null, null);
                vanButton.setCompoundDrawablesWithIntrinsicBounds(null, topVan, null, null);
                tuktukButton.setCompoundDrawablesWithIntrinsicBounds(null, topTuktuk, null, null);
                if(isDestinationSet == true) {
                    calPrice();
                }
            }
        });

        luxcarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedCarCode = carTypeCode[2];
                sedanButton.setCompoundDrawablesWithIntrinsicBounds(null, topSedan, null, null);
                suvButton.setCompoundDrawablesWithIntrinsicBounds(null, topSuv, null, null);
                luxcarButton.setCompoundDrawablesWithIntrinsicBounds(null, topLuxActive, null, null);
                vanButton.setCompoundDrawablesWithIntrinsicBounds(null, topVan, null, null);
                tuktukButton.setCompoundDrawablesWithIntrinsicBounds(null, topTuktuk, null, null);
                if(isDestinationSet == true) {
                    calPrice();
                }
            }
        });

        vanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedCarCode = carTypeCode[3];
                sedanButton.setCompoundDrawablesWithIntrinsicBounds(null, topSedan, null, null);
                suvButton.setCompoundDrawablesWithIntrinsicBounds(null, topSuv, null, null);
                luxcarButton.setCompoundDrawablesWithIntrinsicBounds(null, topLux, null, null);
                vanButton.setCompoundDrawablesWithIntrinsicBounds(null, topVanActive, null, null);
                tuktukButton.setCompoundDrawablesWithIntrinsicBounds(null, topTuktuk, null, null);
                if(isDestinationSet == true) {
                    calPrice();
                }
            }
        });

        tuktukButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedCarCode = carTypeCode[4];
                sedanButton.setCompoundDrawablesWithIntrinsicBounds(null, topSedan, null, null);
                suvButton.setCompoundDrawablesWithIntrinsicBounds(null, topSuv, null, null);
                luxcarButton.setCompoundDrawablesWithIntrinsicBounds(null, topLux, null, null);
                vanButton.setCompoundDrawablesWithIntrinsicBounds(null, topVan, null, null);
                tuktukButton.setCompoundDrawablesWithIntrinsicBounds(null, topTuktukActive, null, null);
                if(isDestinationSet == true) {
                    calPrice();
                }
            }
        });
        //endregion

        //region cancel button
        //******************************************************************************************
        //                              cancel button
        //******************************************************************************************

        View.OnClickListener cClick = new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                JSONObject data = new JSONObject();
                try {
                    data.put("MessageType", 300);
                    String payload = data.toString();
                    byte[] encodedPayload = new byte[0];
                    try {
                        encodedPayload = payload.getBytes("UTF-8");
                        MqttMessage message = new MqttMessage(encodedPayload);
                        mqttClient.publish(acceptedDriverID, message);


                        Log.d("HTTP", "Cancel OK");

                        callButton.setEnabled(false);
                        callButton.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
                        confirmButton.setVisibility(View.VISIBLE);
                        noteToDriverFrame.setVisibility(View.VISIBLE);
                        arrivalTimeFrame.setVisibility(View.VISIBLE);
                        bottomFrame.setVisibility(View.VISIBLE);
                        driverDetail.setVisibility(View.GONE);
                        mMap.clear();



                    } catch (UnsupportedEncodingException | MqttException e) {
                        e.printStackTrace();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        };

        cancelMiddleButton = (Button) findViewById(R.id.cancelButtonMiddle);
        cancelMiddleButton.setOnClickListener(cClick);

        cancelButton = (Button) findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(cClick);
        //endregion

        //region Confirm button
        //******************************************************************************************
        //                              Confirm button
        //******************************************************************************************

        confirmButton = (Button) findViewById(R.id.confirmTaxi);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final View view1 = view;
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setTitle("Confirm order");


                LinearLayout layout = new LinearLayout(view.getContext());
                layout.setOrientation(LinearLayout.VERTICAL);

                // Set up the input
                final EditText input = new EditText(view.getContext());
                // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                input.setHint("Note to driver");

                // Set up the input
                final EditText input2 = new EditText(view.getContext());
                // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                input2.setInputType(InputType.TYPE_CLASS_NUMBER);


                layout.addView(input2);
                layout.addView(input);

                builder.setView(layout);

                // Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogIn, int which) {


                        try {
                            InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            inputManager.hideSoftInputFromWindow(view1.getWindowToken(), 0);
                        } catch (Exception e) {
                        }


                        JSONObject data = new JSONObject();
                        try {
                            data.put("MessageType", 200);
                            data.put("TelNumber", input2.getText());
                            data.put("MessageToDriver", input.getText());
                            data.put("promotionCode", promotionCode);
                            String payload = data.toString();
                            byte[] encodedPayload = new byte[0];
                            try {
                                encodedPayload = payload.getBytes("UTF-8");
                                MqttMessage message = new MqttMessage(encodedPayload);
                                mqttClient.publish(acceptedDriverID, message);


                                Log.d("HTTP", "Confirm OK");

                                isUpdateMap = false;

                                /*

                                updateLocationHandler.removeCallbacks(runnable);
                                updateLocationHandler.postDelayed(runnable, 2000);*/

                                               /*
                                               myTimerTask = new MyTimerTask();
                                               timer = new Timer();
                                               timer.schedule(myTimerTask, 0, 10000);*/

                                isConfirm = true;

                                cancelButton.setVisibility(View.GONE);
                                cancelMiddleButton.setVisibility(View.VISIBLE);
                                confirmButton.setVisibility(View.GONE);

                                noteToDriver.setText("Note to driver : " + input.getText().toString());
                            } catch (UnsupportedEncodingException | MqttException e) {
                                e.printStackTrace();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogIn, int which) {
                        dialogIn.cancel();
                    }
                });

                final AlertDialog dialogC = builder.create();

                dialogC.setCanceledOnTouchOutside(false);
                dialogC.show();

                //dialogC.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);


                if(userPhoneNumber.isEmpty() == true) {
                    input2.setHint("Phone number");
                    dialogC.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);

                } else {
                    input2.setText(userPhoneNumber);
                    dialogC.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                }


                input2.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        // Check if edittext is empty
                        if (TextUtils.isEmpty(editable)) {
                            // Disable ok button
                            dialogC.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                        } else {
                            // Something into edit text. Enable the button.
                            dialogC.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                            userPhoneNumber = input2.getText().toString();
                        }
                    }
                });


            }
        });
        //endregion

        //region Call button
        //******************************************************************************************
        //                              Call button
        //******************************************************************************************


        callButton = (Button) findViewById(R.id.callTaxi);
        callButton.setEnabled(false);
        callButton.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LatLng fromLatLng;
                LatLng toLatLng;

                Log.d("HTTP", "Call button is pressed!!!!!");

                final SharedPreferences prefl = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

                String isFirstRideLocal = prefl.getString("isFirstRide", "true");

                Log.d("HTTP","Is first ride -------> " + isFirstRideLocal);

                if (isFirstRideLocal.equalsIgnoreCase("true")) {

                    isFirstRide = "true";
                    SharedPreferences.Editor editor = prefl.edit();
                    editor.putString("isFirstRide", "false");
                    editor.commit();

                } else {

                    isFirstRide = "false";
                }



                if(fromWhere == null) {
                    fromLatLng = new LatLng(7.88045,98.3922504);
                    AlertDialog GPSalertDialog = new AlertDialog.Builder(MainActivity.this).create();
                    GPSalertDialog.setTitle("GPS Problem");
                    GPSalertDialog.setMessage("Please check your GPS signal.");
                    GPSalertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface gdialog, int which) {
                                    gdialog.dismiss();
                                }
                            });
                    GPSalertDialog.show();

                } else {
                    if(fromWhere.location == null) {
                        fromLatLng = new LatLng(7.88045,98.3922504);
                        AlertDialog GPSalertDialog = new AlertDialog.Builder(MainActivity.this).create();
                        GPSalertDialog.setTitle("GPS Problem");
                        GPSalertDialog.setMessage("Please check your GPS signal.");
                        GPSalertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface gdialog, int which) {
                                        gdialog.dismiss();
                                    }
                                });
                        GPSalertDialog.show();

                    } else {
                        fromLatLng = new LatLng(fromWhere.location.getLatitude(), fromWhere.location.getLongitude());


                        if(toWhere == null) {
                            toLatLng = new LatLng(7.88045,98.3922504);

                        } else {
                            toLatLng = new LatLng(toWhere.location.getLatitude(), toWhere.location.getLongitude());
                            updateMap(fromLatLng, toLatLng, 1, destinationRouteColor);

                            final RequestQueue queue = Volley.newRequestQueue(getBaseContext());

                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                            final String currentDateAndTime = sdf.format(new Date());
                            pickupDateTime = currentDateAndTime;
                            Log.d("HTTP", currentDateAndTime);

                            StringRequest putRequest = new StringRequest(Request.Method.PUT, callTaxiUrl,
                                    new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            // response
                                            Log.d("HTTP", response);
                                            try {

                                                JSONObject obj = new JSONObject(response);

                                                if (obj.getString("Status").equals("SUCCESS")) {

                                                    JSONObject order = obj.getJSONObject("SubmittedOrder");

                                                    Log.d("HTTP", "Order OK");
                                                    Log.d("HTTP", order.getString("Id"));

                                                    OrderID = order.getString("Id");

                                                    if(waitForTaxiDialog !=null)
                                                    {
                                                        waitForTaxiDialog = null;
                                                    }

                                                    waitForTaxiDialog = ProgressDialog.show(MainActivity.this, "", "Waiting for taxi...", true);
                                                    timerDelayRemoveDialog(cc.timeout, waitForTaxiDialog);
                                                    // waitForTaxiDialog.show();
                                                }

                                            } catch (Throwable t) {
                                                Log.e("My App", "Could not parse malformed JSON: \"" + response + "\"");
                                            }


                                            //Log.d("Response", response);
                                        }
                                    },
                                    new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            // error
                                            Log.d("HTTP", error.toString());
                                        }
                                    }
                            ) {

                                @Override
                                protected Map<String, String> getParams() {

                                    Map<String, String> params = new HashMap<String, String>();

                                    params.put("Action", "10");
                                    params.put("OrderDateTime", currentDateAndTime);
                                    params.put("PickupDateTime", currentDateAndTime);
                                    params.put("BaselinePrice", "100");
                                    params.put("Incentive", "10");
                                    params.put("TotalPrice", selectedPrice);
                                    params.put("FromName", fromWhere.name);
                                    params.put("FromAddress", fromWhere.address);
                                    params.put("FromPhoneNumber", fromWhere.PhoneNumber);

                                    params.put("ToName", toWhere.name);
                                    params.put("ToAddress", toWhere.address);
                                    params.put("ToPhoneNumber", toWhere.PhoneNumber);

                                    params.put("FromLat", String.valueOf(fromWhere.location.getLatitude()));
                                    params.put("FromLng", String.valueOf(fromWhere.location.getLongitude()));
                                    params.put("ToLat", String.valueOf(toWhere.location.getLatitude()));
                                    params.put("ToLng", String.valueOf(toWhere.location.getLongitude()));
                                    params.put("IsSelfRide", "0");
                                    params.put("SelfRideString", "0");
                                    params.put("TimePeriodCode", "0");
                                    params.put("Member", memberID);
                                    params.put("PassengerName", memberFirstname + memberLastname);
                                    params.put("PassengerMobile", memberMobile);
                                    params.put("OrderTimeType", "1");
                                    params.put("PassengerMg", "");
                                    params.put("AdditionalLocationString", " ");
                                    params.put("CarTypeCode", selectedCarCode);
                                    params.put("Discount", "0");
                                    params.put("isFirstRide", isFirstRide);

                                    Log.d("HTTP", params.toString());

                                    return params;
                                }

                            };

                            queue.add(putRequest);

                        }

                    }

                }






            }
        });
        //endregion

        //region Bookmark button
        //******************************************************************************************
        //                              Bookmark button
        //******************************************************************************************

        updateBookmark();


        bookmarkButton = (Button) findViewById(R.id.bookmarkBotton);
        bookmarkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setTitle("Bookmark current location");


                LinearLayout layout = new LinearLayout(view.getContext());
                layout.setOrientation(LinearLayout.VERTICAL);

                // Set up the input
                final EditText input = new EditText(view.getContext());
                // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                input.setHint("Place name");


                layout.addView(input);
                // Set up the input
                final EditText input2 = new EditText(view.getContext());
                // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                input2.setInputType(InputType.TYPE_CLASS_NUMBER);
                input2.setHint("Phone number");
                layout.addView(input2);

                builder.setView(layout);

                // Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String placeName = input.getText().toString();
                        String phoneNumber = input2.getText().toString();
                        double currentLat = 0;
                        double currentLng = 0;

                        if(currentLoction == null) {
                            AlertDialog GPSalertDialog = new AlertDialog.Builder(MainActivity.this).create();
                            GPSalertDialog.setTitle("GPS Problem");
                            GPSalertDialog.setMessage("Please check your GPS signal.");
                            GPSalertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface gdialog, int which) {
                                            gdialog.dismiss();
                                        }
                                    });
                            GPSalertDialog.show();


                        } else {
                            currentLat = currentLoction.getLatitude();
                            currentLng = currentLoction.getLongitude();

                            JSONObject object = new JSONObject();
                            try {
                                object.put("placeName", placeName);
                                object.put("phoneNumber", phoneNumber);
                                object.put("Lat", currentLat);
                                object.put("Lng", currentLng);
                                object.put("CategoryId", 13);

                                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());


                                try {
                                    JSONArray jsonArray2 = new JSONArray(prefs.getString("bookmark", "[]"));
                                    //  for (int i = 0; i < jsonArray2.length(); i++) {
                                    //      Log.d("HTTP", jsonArray2.getJSONObject(i)+"");
                                    //  }
                                    jsonArray2.put(object);
                                    SharedPreferences.Editor editor = prefs.edit();
                                    editor.putString("bookmark", jsonArray2.toString());
                                    editor.commit();


                                } catch (Exception e) {
                                    JSONArray jsonArray = new JSONArray();
                                    jsonArray.put(object);
                                    SharedPreferences.Editor editor = prefs.edit();
                                    editor.putString("bookmark", jsonArray.toString());
                                    editor.commit();
                                    e.printStackTrace();
                                }

                                Log.d("HTTP", "json created");

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                            updateBookmark();
                            Log.d("HTTP", "Bookmark OK");
                        }





                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                final AlertDialog dialog = builder.create();
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();

                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);

                input.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        // Check if edittext is empty
                        if (TextUtils.isEmpty(editable)) {
                            // Disable ok button
                            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                        } else {
                            // Something into edit text. Enable the button.
                            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                        }
                    }
                });

            }

        });
        //endregion

        //region Place search view
        //******************************************************************************************
        //                              Place search view
        //******************************************************************************************

        backFromSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fromToFrame.setVisibility(View.VISIBLE);
                search.setVisibility(View.GONE);
                searchTableView.setVisibility(View.GONE);
                backFromSearch.setVisibility(View.GONE);
                deedeePhuketTextView.setText("DEE DEE PHUKET");
            }
        });


        searchListView = (ListView) findViewById(R.id.locationListView);

        searchListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                // ListView Clicked item index
                int itemPosition = position;

                // ListView Clicked item value
                // String  itemValue    = (String) searchListView.getItemAtPosition(position);

                // Show Alert
                //Toast.makeText(getApplicationContext(),
                //        "Position :" + itemPosition, Toast.LENGTH_LONG)
                //        .show();


                if (fromOrTo == true) {
                    fromWhere = locAll.get(itemPosition);
                    fromTextView.setText(fromWhere.name);
                    fromAddressTextView.setText(fromWhere.address);
                    if(isDestinationSet == true) {
                        calPrice();
                    }
                } else {
                    toWhere = locAll.get(itemPosition);
                    toTextView.setText(toWhere.name);
                    toAddressTextView.setText(toWhere.address);
                    ImageView toIm = (ImageView) findViewById(R.id.toimageView);
                    toIm.setImageResource(R.drawable.destination_active);
                    isDestinationSet = true;
                    callButton.setEnabled(true);
                    callButton.getBackground().setColorFilter(null);
                    calPrice();
                }

                fromToFrame.setVisibility(View.VISIBLE);
                search.setVisibility(View.GONE);
                searchTableView.setVisibility(View.GONE);
                backFromSearch.setVisibility(View.GONE);
                deedeePhuketTextView.setText("DEE DEE PHUKET");

            }

        });


        searchTableView = (LinearLayout) findViewById(R.id.searchTableView);
        searchTableView.setVisibility(View.GONE);

        search = (SearchView) findViewById(R.id.placeSearchView);
        searchFrame = (FrameLayout) findViewById(R.id.multiView);
        fromToFrame = (LinearLayout) findViewById(R.id.fromToFrame);
        search.setIconifiedByDefault(false);
        search.setQueryHint("SearchView");
        search.setVisibility(View.GONE);


        //***setOnQueryTextFocusChangeListener***
        search.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                // TODO Auto-generated method stub

                //Toast.makeText(getBaseContext(), String.valueOf(hasFocus),
                //        Toast.LENGTH_SHORT).show();
            }
        });

        //***setOnQueryTextListener***
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                // TODO Auto-generated method stub

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // TODO Auto-generated method stub

                //Toast.makeText(getBaseContext(), newText, Toast.LENGTH_SHORT).show();
                locAll.clear();
                placeList.clear();
                int placeIx = 0;
                final String sText = newText;




                //Local bookmark place search

                if (newText.length() > 0) {
                    for (int i = 0; i < bookmarkNames.size(); i++) {
                        Log.d("HTTP", bookmarkNames.get(i));
                        if (bookmarkNames.get(i).toLowerCase().startsWith(newText.toLowerCase()) == true) {
                            locDetail eachLoc = new locDetail();
                            try {
                                JSONObject pp = bookmarkJSONArray.getJSONObject(i);
                                eachLoc.name = pp.getString("placeName");
                                eachLoc.address = "";
                                eachLoc.CategoryId = pp.getInt("CategoryId");
                                Location tl = new Location("service Provider");
                                tl.setLatitude(pp.getDouble("Lat"));
                                tl.setLongitude(pp.getDouble("Lng"));
                                eachLoc.location = tl;
                                eachLoc.distance = 0.0;
                                locAll.add(placeIx, eachLoc);

                                placeItem itm = new placeItem(eachLoc.name, eachLoc.CategoryId);
                                placeList.add(placeIx, itm);

                                placeIx++;
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }
                }


                //Predefined place search
                final int placeOff = placeIx;

                final ArrayList<locDetail> GlocAll = new ArrayList<locDetail>();


                StringRequest putRequest = new StringRequest(Request.Method.PUT, landMarkUrl,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                // response
                                try {

                                    JSONObject obj = new JSONObject(response);
                                    JSONArray places = obj.getJSONArray("SearchResults");

                                    Log.d("HTTP", obj.toString());

                                    if (obj.getString("Status").equals("SUCCESS")) {


                                        for (int i = 0; i < places.length(); i++) {

                                            JSONObject place = places.getJSONObject(i);
                                            locDetail eachLoc = new locDetail();
                                            eachLoc.name = place.getString("LocationName");
                                            eachLoc.address = place.getString("LocationAddress");
                                            eachLoc.CategoryId = place.getInt("CategoryId");
                                            if(place.getString("Phone").equals("null")) {
                                                eachLoc.PhoneNumber = "N/A";
                                            } else {
                                                eachLoc.PhoneNumber = place.getString("Phone");
                                            }

                                            Location tl = new Location("service Provider");
                                            tl.setLatitude(place.getDouble("Lat"));
                                            tl.setLongitude(place.getDouble("Lng"));

                                            eachLoc.location = tl;
                                            eachLoc.distance = 0.0;

                                            locAll.add(placeOff + i, eachLoc);

                                            placeItem itm = new placeItem(eachLoc.name, eachLoc.CategoryId);
                                            placeList.add(placeOff + i, itm);

                                            // Log.d("My App", place.getString("LocationName"));

                                        }

                                        Log.d("HTTP", "Start google -------------->");


                                       // ArrayList<PlaceAutocomplete> aaa = getPredictions(sText);


                                        LatLng cll = new LatLng(currentLoction.getLatitude(), currentLoction.getLongitude());

                                        double RADIUS = 1000.0;

                                        /*LatLngBounds latLngBounds = new LatLngBounds.Builder().
                                                include(SphericalUtil.computeOffset(cll, RADIUS, 0)).
                                                include(SphericalUtil.computeOffset(cll, RADIUS, 90)).
                                                include(SphericalUtil.computeOffset(cll, RADIUS, 180)).
                                                include(SphericalUtil.computeOffset(cll, RADIUS, 270)).build();*/


                                        LatLng SW = new LatLng(7.743651, 98.193054);
                                        LatLng NE = new LatLng(8.219646, 98.495178);

                                        LatLngBounds latLngBounds = new LatLngBounds(SW,NE);

                                        List<Integer> autocompleteFilter = new ArrayList<Integer>();
                                        //autocompleteFilter.add(Place.TYPE_LOCALITY);
                                        autocompleteFilter.add(Place.TYPE_GEOCODE);



                                        AutocompleteFilter ff =  AutocompleteFilter.create(autocompleteFilter);





                                        Places.GeoDataApi.getAutocompletePredictions( mGoogleApiClient, sText, latLngBounds, ff )
                                                .setResultCallback (
                                                        new ResultCallback<AutocompletePredictionBuffer>() {
                                                            @Override
                                                            public void onResult( AutocompletePredictionBuffer buffer ) {

                                                                if( buffer == null )
                                                                    return;

                                                                if( buffer.getStatus().isSuccess() ) {
                                                                    int iix = 0;
                                                                    for( AutocompletePrediction prediction : buffer ) {
                                                                        final int findex = iix;
                                                                        //Add as a new item to avoid IllegalArgumentsException when buffer is released


                                                                        final PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi.getPlaceById(mGoogleApiClient, prediction.getPlaceId());



                                                                        placeResult.setResultCallback(new ResultCallback<PlaceBuffer>() {
                                                                            @Override
                                                                            public void onResult(PlaceBuffer places) {
                                                                                if (!places.getStatus().isSuccess()) {
                                                                                    places.release();

                                                                                } else {
                                                                                    if (places.getCount() > 0) {
                                                                                        final Place myPlace = places.get(0);
                                                                                        LatLng queriedLocation = myPlace.getLatLng();

                                                                                        locDetail eachLoc = new locDetail();

                                                                                        eachLoc.name = myPlace.getName().toString();
                                                                                        eachLoc.address = myPlace.getAddress().toString();
                                                                                        eachLoc.CategoryId = 13;
                                                                                        Location tl = new Location("service Provider");
                                                                                        tl.setLatitude(queriedLocation.latitude);
                                                                                        tl.setLongitude(queriedLocation.longitude);
                                                                                        eachLoc.location = tl;
                                                                                        eachLoc.distance = 0.0;

                                                                                        Log.d("HTTP", "address = " + eachLoc.address);

                                                                                        String s1 = eachLoc.address;
                                                                                        String s2 = "phuket";

                                                                                        if (Pattern.compile(Pattern.quote(s2), Pattern.CASE_INSENSITIVE).matcher(s1).find() == true) {

                                                                                            GlocAll.add(eachLoc);
                                                                                            Log.d("HTTP", "G Loc = " + eachLoc.location.toString());

                                                                                            places.close();


                                                                                            locAll.add(locAll.size(), eachLoc);

                                                                                            placeItem itm = new placeItem(eachLoc.name, eachLoc.CategoryId);
                                                                                            placeList.add(placeList.size(), itm);

                                                                                            placeItemAdapter arrayAdapter2 = new placeItemAdapter(getApplicationContext(), android.R.layout.select_dialog_singlechoice, placeList);
                                                                                            searchListView.setAdapter(arrayAdapter2);
                                                                                        }

                                                                                    }
                                                                                }
                                                                            }
                                                                        },5,TimeUnit.SECONDS);


                                                                        iix++;

                                                                    }

                                                                    placeItemAdapter arrayAdapter2 = new placeItemAdapter(getApplicationContext(), android.R.layout.select_dialog_singlechoice, placeList);

                                                                    searchListView.setAdapter(arrayAdapter2);

                                                                    //Log.d("My App", obj.toString());



                                                                }

                                                                //Prevent memory leak by releasing buffer
                                                                buffer.release();
                                                            }
                                                        }, 60, TimeUnit.SECONDS );




                                    }


                                   // placeItemAdapter arrayAdapter2 = new placeItemAdapter(getApplicationContext(), android.R.layout.select_dialog_singlechoice, placeList);
                                   // searchListView.setAdapter(arrayAdapter2);

                                    Log.d("HTTP", "END search -------------->");


                                } catch (Throwable t) {
                                    Log.e("My App", "Could not parse malformed JSON: \"" + response + "\"");
                                }


                                //Log.d("Response", response);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                // error
                                //Log.d("Error.Response", response);
                            }
                        }
                ) {

                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("Action", "4");
                        params.put("Keyword", sText);

                        return params;
                    }

                };

                queue.add(putRequest);



                return false;
            }
        });

        search.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {

                fromToFrame.setVisibility(View.VISIBLE);
                search.setVisibility(View.GONE);
                searchTableView.setVisibility(View.INVISIBLE);
                return false;
            }
        });
        //endregion

        //region Place search buttons
        //******************************************************************************************
        //                              Place search buttons
        //******************************************************************************************
        fromButton = (GridLayout) findViewById(R.id.fromButton);
        fromButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Http", "Async task !!!!!!!!!!!!!!!!!!");

                backFromSearch.setVisibility(View.VISIBLE);
                deedeePhuketTextView.setText("Search for location");

                fromOrTo = true;
                placeList.clear();
                placeItemAdapter arrayAdapter2 = new placeItemAdapter(getApplicationContext(), android.R.layout.select_dialog_singlechoice, placeList);
                searchListView.setAdapter(arrayAdapter2);

                fromToFrame.setVisibility(View.GONE);
                search.setQuery("", false);
                search.clearFocus();
                search.setVisibility(View.VISIBLE);
                searchTableView.setVisibility(View.VISIBLE);

            }
        });

        toButton = (GridLayout) findViewById(R.id.toButton);
        toButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                backFromSearch.setVisibility(View.VISIBLE);
                deedeePhuketTextView.setText("Search for location");
                fromOrTo = false;
                placeList.clear();
                placeItemAdapter arrayAdapter2 = new placeItemAdapter(getApplicationContext(), android.R.layout.select_dialog_singlechoice, placeList);
                searchListView.setAdapter(arrayAdapter2);

                fromToFrame.setVisibility(View.GONE);
                search.setQuery("", false);
                search.clearFocus();
                search.setVisibility(View.VISIBLE);
                searchTableView.setVisibility(View.VISIBLE);


                Log.d("Http", "Async task !!!!!!!!!eeee!!!!!!");

            }
        });
        //endregion

    }


    //region ************************ Update bookmark for bookmark button **************************
    /********************************************************************************
     * Update string
     ********************************************************************************/
    public void updateBookmark() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        try {
            bookmarkJSONArray = new JSONArray(prefs.getString("bookmark", "[]"));
            for (int i = 0; i < bookmarkJSONArray.length(); i++) {
                Log.d("HTTP", bookmarkJSONArray.getJSONObject(i) + "");
                bookmarkNames.add(i, bookmarkJSONArray.getJSONObject(i).getString("placeName"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        for (int i = 0; i < bookmarkNames.size(); i++) {
            Log.d("HTTP", bookmarkNames.get(i));
        }

    }


    class PlaceAutocomplete {

        public CharSequence placeId;
        public CharSequence description;

        PlaceAutocomplete(CharSequence placeId, CharSequence description) {
            this.placeId = placeId;
            this.description = description;
        }

        @Override
        public String toString() {
            return description.toString();
        }
    }


    private ArrayList<PlaceAutocomplete> getPredictions(String constraint) {

        LatLng cll = new LatLng(currentLoction.getLatitude(), currentLoction.getLongitude());

        double RADIUS = 20000.0;

        LatLngBounds latLngBounds = new LatLngBounds.Builder().
                include(SphericalUtil.computeOffset(cll, RADIUS, 0)).
                include(SphericalUtil.computeOffset(cll, RADIUS, 90)).
                include(SphericalUtil.computeOffset(cll, RADIUS, 180)).
                include(SphericalUtil.computeOffset(cll, RADIUS, 270)).build();
        if (mGoogleApiClient != null) {


            if(mGoogleApiClient.isConnected()){
                Log.d("HTTP","GOOGLE API CONNECTED");
            } else {
                Log.d("HTTP","GOOGLE API NOT CONNECTED");
            }




            Log.d("HTTP", "Executing autocomplete query for: " + constraint);
            PendingResult<AutocompletePredictionBuffer> results =
                    Places.GeoDataApi
                            .getAutocompletePredictions(mGoogleApiClient, constraint,
                                    latLngBounds, null);
            // Wait for predictions, set the timeout.
            AutocompletePredictionBuffer autocompletePredictions = results
                    .await(1, TimeUnit.SECONDS);
            final Status status = autocompletePredictions.getStatus();
            if (!status.isSuccess()) {

                Log.e("HTTP", "Error getting place predictions: " + status
                        .toString());
                autocompletePredictions.release();
                return null;
            }

            Log.i("HTTP", "Query completed. Received " + autocompletePredictions.getCount()
                    + " predictions.");
            Iterator<AutocompletePrediction> iterator = autocompletePredictions.iterator();
            ArrayList resultList = new ArrayList<>(autocompletePredictions.getCount());
            while (iterator.hasNext()) {
                AutocompletePrediction prediction = iterator.next();
                resultList.add(new PlaceAutocomplete(prediction.getPlaceId(),
                        prediction.getDescription()));
            }
            // Buffer release
            autocompletePredictions.release();
            return resultList;



        }
        Log.e("HTTP", "Google API client is not connected.");
        return null;
    }






    public String getPlaceAutoCompleteUrl(String input) {
        StringBuilder urlString = new StringBuilder();
        urlString.append("https://maps.googleapis.com/maps/api/place/autocomplete/json");
        urlString.append("?input=");
        try {
            urlString.append(URLEncoder.encode(input, "utf8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        urlString.append("&location=");

        urlString.append(currentLoction.getLatitude() + "," + currentLoction.getLongitude()); // append lat long of current location to show nearby results.
        urlString.append("&types=establishment");
        urlString.append("&radius=5000&amp;language=en");
        urlString.append("&key=" + API_KEY);
        return urlString.toString();
    }



    @Override
    public  void onPause() {
        super.onPause();
        try {

            unregisterReceiver(localAlarmReceiver);

        } catch(IllegalArgumentException e) {

        }




    }

    //endregion



    private BroadcastReceiver localAlarmReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            final String alarmOrderID = intent.getStringExtra("orderID");

            Log.d("HTTP", "Alarm Triggered in Main activity " + alarmOrderID);







            final RequestQueue queue = Volley.newRequestQueue(getBaseContext());
            StringRequest putRequest = new StringRequest(Request.Method.PUT, getAdvOrder,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            // response
                            Log.d("HTTP", "ADV ORDER ***************************************************");
                            try {

                                JSONObject obj = new JSONObject(response);

                                if (obj.getString("Status").equals("SUCCESS")) {
                                    JSONObject advOrder = obj.getJSONObject("advOrder");

                                    Log.d("HTTP", advOrder.toString());

                                    if(waitForTaxiDialog !=null)
                                    {
                                        waitForTaxiDialog = null;
                                    }

                                    waitForTaxiDialog = ProgressDialog.show(MainActivity.this, "", "Waiting for taxi...", true);

                                    timerDelayRemoveDialog(cc.timeout, waitForTaxiDialog);

                                }

                            } catch (Throwable t) {
                                Log.e("HTTP", "Could not parse malformed JSON: \"" + response + "\"");
                            }


                            //Log.d("Response", response);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // error
                            Log.d("HTTP", error.toString());
                        }
                    }
            ) {

                @Override
                protected Map<String, String> getParams() {

                    Map<String, String> params = new HashMap<String, String>();

                    params.put("Id", alarmOrderID);


                    return params;
                }

            };

            queue.add(putRequest);


        }
    };



    //region ************************************ On Map ready and Location update *****************
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

        // Add a marker in Sydney and move the camera
        //LatLng sydney = new LatLng(-34, 151);
        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        // Enabling MyLocation Layer of Google Map
        googleMap.setMyLocationEnabled(true);

        // Getting LocationManager object from System Service LOCATION_SERVICE
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        // Creating a criteria object to retrieve provider
        Criteria criteria = new Criteria();

        // Getting the name of the best provider
        String provider = locationManager.getBestProvider(criteria, true);

        // Getting Current Location
        Location location = locationManager.getLastKnownLocation(provider);

        if (location != null) {
            onLocationChanged(location);
        }
        if (firstRun == true) {
            locationManager.requestLocationUpdates(provider, 1000, 0, this);
        }

    }

    @Override
    public void onLocationChanged(Location location) {


        // Getting latitude of the current location
        double latitude = location.getLatitude();

        // Getting longitude of the current location
        double longitude = location.getLongitude();

        // Creating a LatLng object for the current location
        LatLng latLng = new LatLng(latitude, longitude);
        Log.d("HTTP", latLng.toString());
        // Showing the current location in Google Map





        currentLoction = location;

        fromWhere.name = "Current location";
        fromWhere.CategoryId = 13;
        //fromWhere.address = "";
        fromWhere.location = location;

        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        String result = null;
        try {
            List<Address> list = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (list != null && list.size() > 0) {
                Address address = list.get(0);
                // sending back first address line and locality
                result = address.getAddressLine(0) + ", " + address.getLocality();
                fromWhere.address = result;
                if (firstRun == true) {
                    fromAddressTextView.setText(result);
                }
                Log.d("HTTP", result);
            }
        } catch (IOException e) {
            Log.e("HTTP", "Impossible to connect to Geocoder", e);
        }


        if (isPickedUp == true){
            LatLng fromLatLng = new LatLng(latitude, longitude);

            LatLng toLatLng = new LatLng(toWhere.location.getLatitude(), toWhere.location.getLongitude());

            updateMap(fromLatLng, toLatLng, 0, destinationRouteColor);
        }

        if (firstRun == true) {

            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

            // Zoom in the Google Map
            mMap.animateCamera(CameraUpdateFactory.zoomTo(15));

            mapCount++;
            if (mapCount > 1) {
                firstRun = false;
                Criteria criteria = new Criteria();
                String provider = locationManager.getBestProvider(criteria, true);
                locationManager.removeUpdates(this);
                locationManager.requestLocationUpdates(provider, 5000, 100, this);
            }


        }



    }

    @Override
    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub
    }
    //endregion

    //region ***************************** Driver signin and signout *******************************
    public void login(final String gcmID) {

        Log.d("HTTP", "Login & regsiter");

        final RequestQueue queue = Volley.newRequestQueue(this);

        final SharedPreferences prefl = PreferenceManager.getDefaultSharedPreferences(getBaseContext());




        String didRegister = prefl.getString("didRegister", "false");

        if (didRegister.equalsIgnoreCase("true")) {
            Log.d("HTTP", "Login!!");
            loginAll(gcmID);
        } else {

            Log.d("HTTP", "Registaer");
            final String firstname = randomString(6);
            final String lastname = randomString(6);
            final String email = firstname + '.' + lastname + "@gmail.com";
            final String password = "123456";


            StringRequest postRequest = new StringRequest(Request.Method.POST, addUser,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            // response
                            Log.d("HTTP", response);
                            try {
                                JSONObject obj = new JSONObject(response);

                                if (obj.getString("Status").equals("SUCCESS")) {
                                    SharedPreferences.Editor editor = prefl.edit();
                                    editor.putString("didRegister", "true");
                                    editor.putString("email", email);
                                    editor.commit();
                                    Log.d("HTTP", "Registration complete");
                                    //Log.d("HTTP", response);
                                    loginAll(gcmID);



                                    dialog.dismiss();


                                    //Show a diaglog for promotion code

                                    showEnterPromotionCodeDialog();



                                }
                            } catch (Throwable t) {
                                Log.e("My App", "Could not parse malformed JSON: \"" + response + "\"");
                            }
                        }


                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // error
                            Log.d("Error.Response", error.toString());
                        }
                    }
            ) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("FirstName", firstname);
                    params.put("LastName", lastname);
                    params.put("Email", email);
                    params.put("Mobile", "1234567890");
                    params.put("GCMRegistrationID", gcmID);

                    return params;
                }
            };
            queue.add(postRequest);


        }
    }

    public void loginAll(final String gcmID){

        Log.d("HTTP", "Login & regsiter");

        final RequestQueue queue = Volley.newRequestQueue(this);

        final SharedPreferences prefl = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        promotionCode = prefl.getString("promotionCode","NULL");

        StringRequest putRequest = new StringRequest(Request.Method.PUT, memberSignin,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        try {

                            JSONObject obj = new JSONObject(response);
                            Log.d("HTTP", response);
                            if (obj.getString("Status").equals("SUCCESS")) {
                                JSONObject mm = obj.getJSONObject("AuthenMember");
                                memberFirstname = mm.getString("FirstName");
                                memberLastname = mm.getString("LastName");
                                memberID = mm.getString("Id");
                                memberMobile = mm.getString("Mobile");


                                GCMClientManager pushClientManager = new GCMClientManager(MainActivity.this, PROJECT_NUMBER);
                                pushClientManager.registerIfNeeded(new GCMClientManager.RegistrationCompletedHandler() {
                                    @Override
                                    public void onSuccess(String registrationId, boolean isNewRegistration) {

                                        Log.d("HTTP", "Reg id = " + registrationId);
                                        //send this registrationId to your server
                                    }

                                    @Override
                                    public void onFailure(String ex) {
                                        super.onFailure(ex);
                                    }
                                });

                                mqttClient = mqttSetup(memberID);

                                Log.d("HTTP", "Login OK");
                                dialog.dismiss();
                            }

                        } catch (Throwable t) {
                            Log.e("My App", "Could not parse malformed JSON: \"" + response + "\"");
                        }


                        //Log.d("Response", response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        //Log.d("Error.Response", response);
                    }
                }
        ) {

            @Override
            protected Map<String, String> getParams() {


                String email = prefl.getString("email", "");


                Map<String, String> params = new HashMap<String, String>();
                params.put("Email", email);
                params.put("GCMRegistrationID", gcmID);

                return params;
            }

        };

        queue.add(putRequest);

    }

    public void getRegId() {

        //dialog.show();
        final Context context = this;

        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }
                    regid = gcm.register(PROJECT_NUMBER);

                    msg = "Device registered, registration ID=" + regid;
                    Log.i("HTTP", msg);
                    login(regid);

                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();

                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                //etRegId.setText(msg + "\n");
            }
        }.execute(null, null, null);
    }
    //endregion


    static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    static Random rnd = new Random();

    String randomString(int len) {
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++)
            sb.append(AB.charAt(rnd.nextInt(AB.length())));
        return sb.toString();
    }


    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }


    private static class updateMapParameters {
        LatLng fromLatLng;
        LatLng toLatLng;
        int mode;
        int color;
        String url;

        updateMapParameters(LatLng fromLatLng, LatLng toLatLng, int mode, int color, String url) {
            this.fromLatLng = fromLatLng;
            this.toLatLng = toLatLng;
            this.mode = mode;
            this.color = color;
            this.url = url;
        }
    }


    private void updateMap(LatLng fromLatLng, LatLng toLatLng, int mode, int color){
        // Getting URL to the Google Directions API

        mMap.clear();

        String url = getDirectionsUrl(fromLatLng, toLatLng);

        DownloadTask downloadTask = new DownloadTask();

        updateMapParameters p1 = new updateMapParameters(fromLatLng, toLatLng, mode, color, url);
        // Start downloading json data from Google Directions API
        downloadTask.execute(p1);

    }

    private String getDirectionsUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;

        Log.d("HTTP", url);

        return url;
    }


    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            Log.d("HTTP", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    // Fetches data from url passed
    private class DownloadTask extends AsyncTask<updateMapParameters, Void, String> {

        // Downloading data in non-ui thread

        LatLng fromLatLng;
        LatLng toLatLng;
        int mode;
        int routeColor;
        String url;
        Boolean isUpdateFrameNeeded = false;

        @Override
        protected String doInBackground(updateMapParameters... p1) {

            // For storing data from web service
            String data = "";

            fromLatLng =  p1[0].fromLatLng;
            toLatLng = p1[0].toLatLng;
            mode = p1[0].mode;
            routeColor = p1[0].color;

            try {
                // Fetching the data from web service

                data = downloadUrl(p1[0].url);

            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);
        }



        /**
         * A class to parse the Google Places in JSON format
         */
        private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

            // Parsing the data in non-ui thread
            @Override
            protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

                JSONObject jObject;
                List<List<HashMap<String, String>>> routes = null;

                try {
                    jObject = new JSONObject(jsonData[0]);
                    DirectionsJSONParser parser = new DirectionsJSONParser();

                    // Starts parsing data
                    routes = parser.parse(jObject);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return routes;
            }

            // Executes in UI thread, after the parsing process
            @Override
            protected void onPostExecute(List<List<HashMap<String, String>>> result) {
                ArrayList<LatLng> points = null;
                PolylineOptions lineOptions = null;
                MarkerOptions markerOptions = new MarkerOptions();
                String distance = "";
                String duration = "";

                if (result.size() < 1) {
                    Toast.makeText(getBaseContext(), "No Points", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Traversing through all the routes
                for (int i = 0; i < result.size(); i++) {
                    points = new ArrayList<LatLng>();
                    lineOptions = new PolylineOptions();

                    // Fetching i-th route
                    List<HashMap<String, String>> path = result.get(i);

                    // Fetching all the points in i-th route
                    for (int j = 0; j < path.size(); j++) {
                        HashMap<String, String> point = path.get(j);

                        if (j == 0) {    // Get distance from the list
                            distance = (String) point.get("distance");
                            continue;
                        } else if (j == 1) { // Get duration from the list
                            duration = (String) point.get("duration");
                            continue;
                        }

                        double lat = Double.parseDouble(point.get("lat"));
                        double lng = Double.parseDouble(point.get("lng"));
                        LatLng position = new LatLng(lat, lng);

                        points.add(position);
                    }

                    // Adding all the points in the route to LineOptions
                    lineOptions.addAll(points);
                    lineOptions.width(10);

                    lineOptions.color(routeColor);
                }

                Log.d("HTTP", "Distance:" + distance + ", Duration:" + duration);



                // Drawing polyline in the Google Map for the i-th route
                mMap.addPolyline(lineOptions);


                LatLng from1 = points.get(0); //new LatLng(fromWhere.location.getLatitude(), fromWhere.location.getLongitude());
                LatLng to1 = points.get(points.size()-1); //new LatLng(toWhere.location.getLatitude(), toWhere.location.getLongitude());

                if(mode == 0) { //Taxi route
                    arrivalTime.setText(duration);
                    arrivalDistance.setText(distance);
                    mMap.addMarker(new MarkerOptions()
                            .position(from1)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.driver_pin)));
                    mMap.addMarker(new MarkerOptions()
                            .position(to1)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.customer_pin)));
                } else { //Destination route
                    mMap.addMarker(new MarkerOptions()
                            .position(from1)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.driver_pin)));
                    mMap.addMarker(new MarkerOptions()
                            .position(to1)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.destination_pin)));
                }

                if(isUpdateMap == false) {
                    LatLngBounds.Builder builder = new LatLngBounds.Builder();

                    builder.include(from1);
                    builder.include(to1);

                    LatLngBounds bounds = builder.build();

//                    ViewGroup.LayoutParams params = mMapFragment.getView().getLayoutParams();

                    int padding = 100; // offset from edges of the map in pixels

                    CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);

                    mMap.animateCamera(cu);
                    isUpdateMap = true;
                    //mMap.moveCamera(cu);
                }


            }
        }



    }



    void calPrice() {

        final RequestQueue queue = Volley.newRequestQueue(getBaseContext());
        StringRequest putRequest = new StringRequest(Request.Method.POST, calprice,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("HTTP", response);
                        try {

                            JSONObject obj = new JSONObject(response);

                            if (obj.getString("Status").equals("SUCCESS")) {

                                Log.d("HTTP", "Calprice OK");
                                selectedPrice = obj.getString("SelectedPrice");
                                TextView dtv = (TextView) findViewById(R.id.detialPriceText);
                                dtv.setText("THB : " + obj.getString("SelectedPrice"));
                                pricetextView.setText("THB : " + obj.getString("SelectedPrice"));
                            }

                        } catch (Throwable t) {
                            Log.e("My App", "Could not parse malformed JSON: \"" + response + "\"");
                        }


                        //Log.d("Response", response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("HTTP", error.toString());
                    }
                }
        ) {

            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<String, String>();

                params.put("StartLat", String.valueOf(fromWhere.location.getLatitude()));
                params.put("StartLng", String.valueOf(fromWhere.location.getLongitude()));

                params.put("EndLat", String.valueOf(toWhere.location.getLatitude()));
                params.put("EndLng", String.valueOf(toWhere.location.getLongitude()));
                params.put("SelectedCarType", selectedCarCode);
                params.put("PickupDateTime", pickupDateTime);
                params.put("AdditionalLocationString", "");



                Log.d("HTTP", params.toString());

                return params;
            }

        };

        queue.add(putRequest);

    }

    public void timerDelayRemoveDialog(long time, final Dialog d){
        handler.postDelayed(timeoutRunable, cc.timeout);
    }


    private  void showEnterPromotionCodeDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Please enter the promotion code");


        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        // Set up the input
        final EditText input = new EditText(this);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setHint("Promotion Code");



        layout.addView(input);

        builder.setView(layout);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


                try {
                    InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow(input.getWindowToken(), 0);
                } catch (Exception e) {
                }





            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        final AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();


        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Check if edittext is empty
                if (TextUtils.isEmpty(editable)) {
                    // Disable ok button
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                } else {
                    // Something into edit text. Enable the button.
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                    promotionCode = input.getText().toString();


                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());



                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString("promotionCode", promotionCode);
                        editor.commit();
                }
            }
        });

    }


}
