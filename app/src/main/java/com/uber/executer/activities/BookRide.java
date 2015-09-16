package com.uber.executer.activities;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
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

import java.text.ParseException;
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
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.picasso.Picasso;
import com.uber.executer.Singletons.MyApp;
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
  GoogleApiClient mGoogleApiClient;
  TextView eventTitle;
  TextView startTimeValueOfEvent;
  TextView endTimeValueOfEvent;
  EditText pickUpLocation;
  EditText eventDestination;
  Spinner spinnerForUberType;
  ArrayAdapter<String> adapter;

  long time;
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

    TextView toolbarTitle = (TextView)findViewById (R.id.toolbar_title);
    toolbarTitle.setText ("executer");
    toolbarTitle.setAllCaps (false);
    Typeface tf = null;
    try{
      tf = Typeface.createFromAsset (getAssets (),"MuseoSans_900.otf");
    }
    catch (Exception e){
      e.printStackTrace ();
    }
    toolbarTitle.setTypeface (tf);
    com.pkmmte.view.CircularImageView avatar = (com.pkmmte.view.CircularImageView)findViewById (R.id.myAvartar);
    try {
      Picasso.with (BookRide.this).load (Vars.user.response.picture)
              .error (R.drawable.logoone).placeholder (R.drawable.logoone)
              .into (avatar);
    }catch (Exception e){
      e.printStackTrace ();
    }

    //get data from EventPageFragment and populate the view
    Intent gotten = getIntent ();
    final String end = gotten.getStringExtra ("end");
    final String location = gotten.getStringExtra ("location");
    final String summary = gotten.getStringExtra ("summary");
    final String timing = gotten.getStringExtra ("startTime");

    final String dataTime = gotten.getStringExtra ("startOrigin");

    eventTitle.setText (summary);
    eventDestination.setText (location);

    String timeFormatter = timing.split("\\+")[0];
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    try {
      Date date = sdf.parse (timeFormatter);
      SimpleDateFormat dt = new SimpleDateFormat("EEE, MMM d, yyyy");
      startTimeValueOfEvent.setText (dt.format (date));
    } catch (ParseException e) {
      e.printStackTrace ();
    }


    String timeFormatter2 = end.split("\\+")[0];
    SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    try {
      Date date2 = sdf.parse (timeFormatter2);
      SimpleDateFormat dt2 = new SimpleDateFormat("EEE, MMM d, yyyy");
      endTimeValueOfEvent.setText (dt2.format (date2));
    } catch (ParseException e) {
      e.printStackTrace ();
    }

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
    spinnerForUberType.setAdapter (adapter);

    bookARide = (Button)findViewById (R.id.bookeRideNow);
    bookARide.setOnClickListener (new View.OnClickListener () {
      @Override
      public void onClick (View v) {
        if(isOnline ()){

          //get pickup location text in string format
         if(pickUpLocation.getText ().toString ().equals ("")){
           Toast.makeText (getApplicationContext (), "Please Enter a valid location!", Toast.LENGTH_SHORT).show ();
           if(eventDestination.getText ().toString ().equals ("")){
             Toast.makeText (getApplicationContext (), "Please Enter a valid destination", Toast.LENGTH_SHORT).show ();
           }

         }

         else {
           RequestQueue queue = Volley.newRequestQueue (getApplicationContext ());
           StringRequest stringRequest = new StringRequest (Request.Method.POST,
                   "http://andelahack.herokuapp.com/users/"+ Vars.user.response.uuid+"/requests",
                   new Response.Listener<String> () {
                     @Override
                     public void onResponse (String response) {
                       String reminderTime = null;
                       String address = null;
                       String productString = null;
                       String id = null;
                       String productId = null;
                       String returnedSummary = null;

                       try {
                         JSONObject object = new JSONObject (response);
                         JSONObject objectResponse = object.getJSONObject ("response");
                         Log.e ("obectResponse:", objectResponse+"");
                         JSONObject product = objectResponse.getJSONObject ("product");
                         JSONObject estimate = objectResponse.getJSONObject ("estimates");
                         productString = product.getString ("type");
                         productId = objectResponse.getString ("id");
                         JSONObject destinationObj = objectResponse.getJSONObject ("destination");
                         address = destinationObj.getString ("address");
                         returnedSummary = objectResponse.getString ("summary");
                         Log.e("Id: ",productId+"");
                         reminderTime = estimate.getString ("reminder");
                         Log.e ("reminder", reminderTime);
                       } catch (JSONException e) {
                         e.printStackTrace ();
                       }

                       try {
                         String timeFormatter = reminderTime.split("\\+")[0];
                         SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                         Date date = sdf.parse (timeFormatter);
                         time = date.getTime ();
                       } catch (ParseException e) {
                         e.printStackTrace ();
                       }

                       Intent myIntent = new Intent(BookRide.this, AlarmReciever.class);
                       myIntent.putExtra ("type",productString);
                       myIntent.putExtra ("start",dataTime);
                       myIntent.putExtra ("reminder", reminderTime);
                       myIntent.putExtra ("destination",address);
                       myIntent.putExtra ("id", productId);
                       myIntent.putExtra ("summary", returnedSummary);

                       PendingIntent pendingIntent = PendingIntent.getBroadcast(BookRide.this,
                               0, myIntent, 0);

                       Log.e ("formatted", time + "");

                       AlarmManager alarmManager = (AlarmManager)getApplicationContext (). getSystemService (Context.ALARM_SERVICE);

            /*
             * The following sets the Alarm in the specific time by getting the long
             * value of the alarm date time which is in calendar object by calling
             * the getTimeInMillis(). Since Alarm supports only long value , we're
             * using this method.
             */

//                       alarmManager.set (AlarmManager.RTC, System.currentTimeMillis (),
//                               pendingIntent);

              alarmManager.set (AlarmManager.RTC, time,
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
               params.put ("location",pickUpLocation.getText ().toString ());
               params.put("summary", summary);
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
      Toast.makeText (getApplicationContext (),"Check your internet connection", Toast.LENGTH_SHORT).show ();
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
  protected void onStart () {
    super.onStart ();

  }

  @Override
  public void onBackPressed () {
    Intent i = new Intent (BookRide.this, EventPage.class);
    startActivity (i);
    this.finish();
  }

  @Override
    public boolean onOptionsItemSelected (MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId ();

    //noinspection SimplifiableIfStatement
    if (id == R.id.logout) {
      revokeAccess ();
      Vars.clearDB (getApplicationContext ());
      Intent intent = new Intent (BookRide.this, LoginActivity.class);
      startActivity (intent);
      Toast.makeText (getApplicationContext (), "You have logged out", Toast.LENGTH_SHORT).show ();
      return true;
    }
    if (id == R.id.changeAccount) {
      revokeAccess ();
      Intent i = new Intent (BookRide.this, MainActivity.class);
      startActivity (i);

    }
    return true;
  }
  public void revokeAccess(){
    if (MyApp.mGoogleApiClient.isConnected()) {
      Plus.AccountApi.clearDefaultAccount(MyApp.mGoogleApiClient);
      MyApp.mGoogleApiClient.disconnect();
      MyApp.mGoogleApiClient.connect();
    }
  }
  private void toastMessage(String message){
    Toast.makeText (this, message, Toast.LENGTH_SHORT).show ();
  }
}
