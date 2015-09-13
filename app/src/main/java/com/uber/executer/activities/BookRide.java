package com.uber.executer.activities;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.uber.executer.fragments.NavFragment;
import com.uber.executer.R;
import com.uber.executer.Singletons.Vars;
import com.uber.executer.models.Calendar;
import com.uber.executer.models.Events;
import com.uber.executer.services.AlarmReciever;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class BookRide extends AppCompatActivity {
  Events[] eventses;

  TextView eventTitle;
  TextView startTimeValueOfEvent;
  TextView endTimeValueOfEvent;
  EditText pickUpLocation;
  EditText eventDestination;
  Spinner spinnerForUberType;
  ArrayAdapter<String> adapter;

  JSONObject locationObject;
  static final String UBER_BLACK = "UberBLACK";
  static final String UBER_X = "uberX";
  static final String UBER_TAXI = "UberSUV";

  Toolbar toolbar;
  Button bookARide;
  NotificationManager manager;
  FragmentManager fm = getSupportFragmentManager();
  @Override
  protected void onCreate (Bundle savedInstanceState) {
    super.onCreate (savedInstanceState);

    //set up layout
    setContentView (R.layout.activity_book_ride);
    initField ();
    //prevent editText from gaining focus on lunch of activity
//    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

    manager = (NotificationManager)getSystemService (Context.NOTIFICATION_SERVICE);


    //set up toolbar
    toolbar = (Toolbar)findViewById (R.id.toolbar);
    setSupportActionBar (toolbar);
    getSupportActionBar ().setDisplayHomeAsUpEnabled (true);
    getSupportActionBar ().setDisplayShowHomeEnabled (true);
    getSupportActionBar ().setDisplayShowTitleEnabled (false);
    TextView toolbarTitle = (TextView)findViewById (R.id.toolbar_title);
    toolbarTitle.setText ("EXECUTER");
    Typeface tf = Typeface.createFromAsset (getAssets (),"MuseoSans-300.otf");
    toolbarTitle.setTypeface (tf);

    //get data from EventPageFragment and populate the view
    Intent gotten = getIntent ();
    final String end = gotten.getStringExtra ("end");
    final double longitude = gotten.getDoubleExtra ("longitude", 0);
    final double latitude = gotten.getDoubleExtra ("latitude", 0);
    final String location = gotten.getStringExtra ("location");
    final String summary = gotten.getStringExtra ("summary");
    final String timing = gotten.getStringExtra ("startTime");
    final String myLocation = gotten.getStringExtra ("currentLocation");
    final String dataTime = gotten.getStringExtra ("startOrigin");

    eventTitle.setText (summary);
    eventDestination.setText (location);
    startTimeValueOfEvent.setText (timing);
    endTimeValueOfEvent.setText (end);
    spinnerForUberType = (Spinner)findViewById (R.id.spinner_for_uber_type);



    List<String> list;

    list = new ArrayList<String> ();
    for(String item: getResources ().getStringArray (R.array.taxies)){
      list.add(item);
    }
//    ArrayAdapter<CharSequence> adapters = ArrayAdapter.createFromResource (this, R.array.taxies, R.layout.simple_spinner_dropdown_item);
//    adapters.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
//    spinnerForUberType.setAdapter (adapters);
    adapter = new ArrayAdapter<String>(getApplicationContext(),
            android.R.layout.simple_spinner_item, list);
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    spinnerForUberType.setAdapter(adapter);


    bookARide = (Button)findViewById (R.id.bookeRideNow);
    bookARide.setOnClickListener (new View.OnClickListener () {
      @Override
      public void onClick (View v) {

        if(isOnline ()){
          try{
            locationObject = new JSONObject ();
            locationObject.put ("latitude",latitude);
            locationObject.put("longitude",longitude);
          }
          catch(JSONException e){
            e.printStackTrace ();
          }
          //get pickup location text in string format

          RequestQueue queue = Volley.newRequestQueue (getApplicationContext ());
          StringRequest stringRequest = new StringRequest (Request.Method.POST, "http://andelahack.herokuapp.com/users/"+ Vars.user.response.uuid+"/requests", new Response.Listener<String> () {
            @Override
            public void onResponse (String response) {
              String reminderTime;
              try {
                JSONObject object = new JSONObject (response);
                JSONObject objectResponse = object.getJSONObject ("response");
                Log.e ("Response:", response);
                Toast.makeText (getApplicationContext (),response,Toast.LENGTH_LONG).show ();
                JSONObject estimate = objectResponse.getJSONObject ("estimates");
                Toast.makeText (getApplicationContext (),estimate+"",Toast.LENGTH_LONG).show ();
                Log.e("Estimates: ",estimate+"");
                reminderTime = estimate.getString ("reminder");
                Log.e ("reminder", reminderTime);
              } catch (JSONException e) {
                e.printStackTrace ();
              }

              Intent myIntent = new Intent(BookRide.this, AlarmReciever.class);

              PendingIntent pendingIntent = PendingIntent.getBroadcast(BookRide.this,
                    0, myIntent, 0);

            AlarmManager alarmManager = (AlarmManager)getApplicationContext (). getSystemService (Context.ALARM_SERVICE);

            /*
             * The following sets the Alarm in the specific time by getting the long
             * value of the alarm date time which is in calendar object by calling
             * the getTimeInMillis(). Since Alarm supports only long value , we're
             * using this method.
             */

            alarmManager.set (AlarmManager.RTC, System.currentTimeMillis (),
                    pendingIntent);


            }
          }, new Response.ErrorListener () {
            @Override
            public void onErrorResponse (VolleyError error) {
              Toast.makeText (getApplicationContext (), "Error: " +error,Toast.LENGTH_LONG).show ();
            }
          }){
            @Override
            protected Map<String, String> getParams () throws AuthFailureError {
              super.getParams ();
              Map<String,String> params = new HashMap<String, String> ();
              params.put("destination",eventDestination.getText ().toString ());
              params.put("startTime",dataTime);
              params.put("productType",spinnerForUberType.getSelectedItem ().toString ());
              params.put("location", locationObject.toString ());
              return params;
            }

            @Override
            public Map<String, String> getHeaders () throws AuthFailureError {
              super.getHeaders ();
              Map<String,String> params = new HashMap<String, String>();
              params.put("Content-Type","application/x-www-form-urlencoded");
              return params;
            }
          };
          int socketTimeout = 30000;//30 seconds - change to what you want
          RetryPolicy policy = new DefaultRetryPolicy (socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
          stringRequest.setRetryPolicy(policy);

          queue.add (stringRequest);
        }
        else {
          Toast.makeText (BookRide.this, "Enable network connection", Toast.LENGTH_SHORT).show ();
        }
      }
    });
    //setup taxies


  }
  public void initField(){
    eventTitle = (TextView)findViewById (R.id.titleOfEvent);
    startTimeValueOfEvent = (TextView)findViewById (R.id.startTimeValueofEvent);
    endTimeValueOfEvent = (TextView)findViewById (R.id.endTimeValueOfEvent);
    pickUpLocation = (EditText)findViewById (R.id.pick_up_location);
    eventDestination = (EditText)findViewById (R.id.event_destination_);
    bookARide = (Button)findViewById (R.id.bookeRideNow);
  }

  //check if there is network
  public boolean isOnline() {
    ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService (Context.CONNECTIVITY_SERVICE);
    NetworkInfo info = connectivityManager.getActiveNetworkInfo();
    if (info != null && info.isConnectedOrConnecting ()) {
      return true;
    } else {
      return false;
    }

  }

  @Override
  public boolean onCreateOptionsMenu (Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater ().inflate (R.menu.menu_book_ride, menu);
    return true;
  }
  public Calendar getCalendarAtPosion(Calendar[] calendars, int position){
    return calendars[position];
  }


  @Override
  public boolean onOptionsItemSelected (MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId ();

    //noinspection SimplifiableIfStatement
    if (id == R.id.action_settings) {
      Toast.makeText (getApplicationContext (),"Empty for now",Toast.LENGTH_SHORT).show ();
      return true;
    }

    return super.onOptionsItemSelected (item);
  }

  private void toastMessage(String message){
    Toast.makeText (this, message, Toast.LENGTH_SHORT).show ();
  }


}
