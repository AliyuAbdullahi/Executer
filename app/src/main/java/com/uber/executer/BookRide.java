package com.uber.executer;

import android.app.ActionBar;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.uber.executer.models.Calendar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class BookRide extends AppCompatActivity {
  DrawerLayout drawerLayout;
  TextView myCurrentLocation;
  TextView clickImage;
  TextView startTime;
  TextView showBookedEvents;
  EditText pickUpLocation;
  JSONObject locationObject;
  String pickup;
  Dialog dialog;
  static final String UBER_BLACK = "UBER BLACK";
  static final String UBER_X = "UBER X";
  static final String UBER_TAXI = "UBER TAXI";
  LinearLayout cars;
  EditText destination;
  Toolbar toolbar;
  Button bookARide;
  TableLayout tableLayout;
  Button chooseAride;
  TableRow row;
  ImageView uberx;
  ImageView uberBlack;
  ImageView ubertaxi;
  TextView carType;
  Animation animation;
  @Override
  protected void onCreate (Bundle savedInstanceState) {
    super.onCreate (savedInstanceState);

    //set up layout
    setContentView (R.layout.activity_book_ride);
    //prevent editText from gaining focus on lunch of activity
//    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

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
    drawerLayout = (DrawerLayout)findViewById (R.id.layoutdrawer);
    NavFragment navigation = (NavFragment)getSupportFragmentManager ().findFragmentById (R.id.navigation_drawer);
    navigation.setUp (R.id.navigation_drawer, drawerLayout);


    //get data from EventPageFragment and populate the view
    Intent gotten = getIntent ();
    final double longitude = gotten.getDoubleExtra ("longitude",0);
    final double latitude = gotten.getDoubleExtra ("latitude",0);
    final String location = gotten.getStringExtra ("location");
    final String summary = gotten.getStringExtra ("summary");
    final String timing = gotten.getStringExtra ("startTime");
    final String myLocation = gotten.getStringExtra ("currentLocation");
    TextView eventsummary = (TextView)findViewById (R.id.event_title_text);
    TextView eventLocation = (TextView)findViewById (R.id.event_location);
    eventsummary.setText (summary);
    eventLocation.setText (location);


    //make cars layout invisible
    cars = (LinearLayout)findViewById (R.id.cars);
    cars.setVisibility (View.INVISIBLE);

    //get instance of the tablelayout
    tableLayout = (TableLayout)findViewById (R.id.table);
    row = (TableRow)tableLayout.findViewById (R.id.uber_types);
    pickUpLocation = (EditText)tableLayout.findViewById (R.id.pick_up);
    //set click image notification text invisible
    clickImage = (TextView)findViewById (R.id.click_to_select);
    clickImage.setVisibility (View.INVISIBLE);
    //get event start time
    startTime = (TextView)tableLayout.findViewById (R.id.event_start_time);
    myCurrentLocation = (TextView)tableLayout.findViewById (R.id.my_current_location);
    myCurrentLocation.setText (myLocation);

    startTime.setText (timing);

    //set up drawer layout
    drawerLayout = (DrawerLayout)findViewById (R.id.drawerlayout);

    row.setVisibility (View.INVISIBLE);
    chooseAride = (Button)tableLayout.findViewById (R.id.choose_a_ride);
    carType = (TextView)tableLayout.findViewById (R.id.uber_type_here);
    destination = (EditText)tableLayout.findViewById (R.id.event_location);
    destination.setText (location);
    // book a ride
    bookARide = (Button)findViewById (R.id.book_a_ride);
    bookARide.setVisibility(View.INVISIBLE);
    bookARide.setOnClickListener (new View.OnClickListener () {
      @Override
      public void onClick (View v) {
        if(isOnline ()){
          try{
            locationObject = new JSONObject ();
            locationObject.put ("longitude",longitude);
            locationObject.put("latitude",latitude);
            locationObject.put("address",myLocation);
          }
          catch(JSONException e){
            e.printStackTrace ();
          }
          //get pickup location text in string format
          pickup = pickUpLocation.getText ().toString ();

          final String myDestination = destination.getText ().toString ();
          //get the car type
          final String car = carType.getText ().toString ();
          // get the current time of request
          long time = new Date ().getTime ();
          DateFormat formatter = new SimpleDateFormat ("HH:mm:ss:SSS");
          String formattedTime = formatter.format (time);

          //get user location
          //get user destination
          //get user longitude
          //get user latitude

          RequestQueue queue = Volley.newRequestQueue (getApplicationContext ());
          StringRequest stringRequest = new StringRequest (Request.Method.POST, "http://andelahack.herokuapp.com/users/"+Vars.user.response.uuid+"/requests", new Response.Listener<String> () {
            @Override
            public void onResponse (String response) {
              Toast.makeText (getApplicationContext (), "Success!",Toast.LENGTH_LONG).show ();

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
              params.put("uberType",car);
              params.put("location", locationObject.toString ());
              params.put ("destination", myDestination);
              params.put("pickUpLocation", pickup);
              params.put("summary",summary);
              params.put("startTime",timing);


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
          queue.add (stringRequest);
          pickUpLocation.setText ("");
        }
        else {
          Toast.makeText (BookRide.this, "Enable network connection", Toast.LENGTH_SHORT).show ();
        }

      }
    });


    //setup taxies
    uberBlack = (ImageView)findViewById (R.id.uber_black_one);
    uberx = (ImageView)findViewById (R.id.uber_x);
    ubertaxi = (ImageView)findViewById (R.id.uber_taxi);


    //setup listeners for taxies
    uberBlack.setOnClickListener (new View.OnClickListener () {
      @Override
      public void onClick (View v) {
        choose(UBER_BLACK);
        zoomIn (v);


      }
    });


    uberBlack.setOnLongClickListener (new View.OnLongClickListener () {
      @Override
      public boolean onLongClick (View v) {
        dialog = new Dialog (BookRide.this);
        dialog.setContentView (R.layout.car_detail_dialog);
        Toolbar toolbar = (Toolbar) dialog.findViewById (R.id.toolbar);
        dialog.setTitle ("");
        ImageView carImage = (ImageView) dialog.findViewById (R.id.uber_black_detail_car);
        carImage.setImageResource (R.drawable.uber_black);
        TextView carTitle = (TextView) dialog.findViewById (R.id.car_title);
        carTitle.setText ("UBER BLACK");
        TextView carProperty = (TextView) dialog.findViewById (R.id.car_property);
        carProperty.setText (R.string.uber_black_details);
        Button gotIt = (Button) dialog.findViewById (R.id.gotit);
        gotIt.setOnClickListener (new View.OnClickListener () {
          @Override
          public void onClick (View v) {
            dialog.hide ();
          }
        });
        dialog.show ();
        return true;
      }
    });


    uberx.setOnClickListener (new View.OnClickListener () {
      @Override
      public void onClick (View v) {
        choose (UBER_X);
        zoomIn (v);

      }
    });


    uberx.setOnLongClickListener (new View.OnLongClickListener () {
      @Override
      public boolean onLongClick (View v) {
        dialog = new Dialog (BookRide.this);
        dialog.setContentView (R.layout.car_detail_dialog);
        Toolbar toolbar = (Toolbar) dialog.findViewById (R.id.toolbar);
        dialog.setTitle ("");
        ImageView carImage = (ImageView) dialog.findViewById (R.id.uber_black_detail_car);
        carImage.setImageResource (R.drawable.new_uber);
        TextView carTitle = (TextView) dialog.findViewById (R.id.car_title);
        carTitle.setText ("UBER X");
        TextView carProperty = (TextView) dialog.findViewById (R.id.car_property);
        carProperty.setText (R.string.uber_x_details);
        Button gotIt = (Button) dialog.findViewById (R.id.gotit);
        gotIt.setOnClickListener (new View.OnClickListener () {
          @Override
          public void onClick (View v) {
            dialog.hide ();
          }
        });
        dialog.show ();
        return true;
      }
    });


    ubertaxi.setOnClickListener (new View.OnClickListener () {
      @Override
      public void onClick (View v) {
        choose (UBER_TAXI);
        zoomIn (v);
      }
    });


    ubertaxi.setOnLongClickListener (new View.OnLongClickListener () {
      @Override
      public boolean onLongClick (View v) {
        dialog = new Dialog (BookRide.this);
        dialog.setContentView (R.layout.car_detail_dialog);
        Toolbar toolbar = (Toolbar) dialog.findViewById (R.id.toolbar);
        dialog.setTitle ("");
        ImageView carImage = (ImageView) dialog.findViewById (R.id.uber_black_detail_car);
        carImage.setImageResource (R.drawable.taxi);
        TextView carTitle = (TextView) dialog.findViewById (R.id.car_title);
        carTitle.setText ("UBER TAXI");
        TextView carProperty = (TextView) dialog.findViewById (R.id.car_property);
        carProperty.setText (R.string.uber_taxi_details);
        Button gotIt = (Button) dialog.findViewById (R.id.gotit);
        gotIt.setOnClickListener (new View.OnClickListener () {
          @Override
          public void onClick (View v) {
            dialog.hide ();
          }
        });
        dialog.show ();
        return true;
      }
    });


    chooseAride.setOnClickListener (new View.OnClickListener () {
      @Override
      public void onClick (View v) {
        cars.setVisibility (View.VISIBLE);
        chooseAride.setVisibility (View.INVISIBLE);
        bookARide.setVisibility (View.VISIBLE);
        clickImage.setVisibility (View.VISIBLE);


      }
    });
  }

  //check if there is network
  public boolean isOnline() {
    ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService (Context.CONNECTIVITY_SERVICE);
    NetworkInfo info = connectivityManager.getActiveNetworkInfo();
    if (info != null && info.isConnectedOrConnecting()) {
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
      return true;
    }

    return super.onOptionsItemSelected (item);
  }

  public void choose(String type){
    row.setVisibility (View.VISIBLE);
    carType.setText (type);
  }
  public void zoomIn(View view){
    Animation animation =  AnimationUtils.loadAnimation (getApplicationContext (), R.anim.zoom_out);
    view.setAnimation (animation);
    animation.start ();
  }
  private void toastMessage(String message){
    Toast.makeText (this, message, Toast.LENGTH_SHORT).show ();
  }

}
