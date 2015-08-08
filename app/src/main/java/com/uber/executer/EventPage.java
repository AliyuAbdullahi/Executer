package com.uber.executer;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.uber.executer.models.Calendar;

public class EventPage extends AppCompatActivity {

  Context ctx;
  String[] taxiTypes;
  ListView eventList;
  Double longitude;
  Double latitude;
  EditText pickupLocation;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate (savedInstanceState);
    setContentView (R.layout.activity_event_page);

    taxiTypes = new String[]{"UBER X", "UBER BLACK", "UBER SUN"};
    eventList = (ListView) findViewById(R.id.events);
    eventList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Dialog dialog = new Dialog(EventPage.this);
        dialog.setTitle ("Event");
        dialog.setContentView(R.layout.event_details);
        Calendar calendar = Vars.calendars[position];
        ((TextView) dialog.findViewById(R.id.summary)).setText(calendar.summary);
        ((TextView) dialog.findViewById(R.id.destination)).setText(calendar.location);
        ((TextView) dialog.findViewById(R.id.status)).setText(calendar.status);
        try {
          ((TextView) dialog.findViewById(R.id.start_time)).setText(Vars.dateToRelativeString(calendar.start));
          ((TextView) dialog.findViewById(R.id.end_time)).setText(Vars.dateToRelativeString(calendar.end));
        }
        catch (Exception e) {

        }
        Spinner taxiType = (Spinner) dialog.findViewById(R.id.taxi_type);
        taxiType.setAdapter(new ArrayAdapter<String> (EventPage.this, android.R.layout.simple_spinner_dropdown_item, taxiTypes));
        taxiType.setOnItemSelectedListener (new AdapterView.OnItemSelectedListener () {
          @Override
          public void onItemSelected (AdapterView<?> parent, View view, int position, long id) {
            String taxiType = (String)parent.getItemAtPosition (position);
            //make a post with volley to request for taxi

            //get Location name with url using volley
            RequestQueue requestQueue = Volley.newRequestQueue (EventPage.this);
            StringRequest requestLocation = new StringRequest (Request.Method.GET, "url", new Response.Listener<String> () {
              @Override
              public void onResponse (String response) {

              }
            }, new Response.ErrorListener () {
              @Override
              public void onErrorResponse (VolleyError error) {

              }
            });
          }

          @Override
          public void onNothingSelected (AdapterView<?> parent) {

          }
        });
        //getLocation();
        dialog.show();
      }
    });

    eventList.setAdapter (new EventAdapter (this, Vars.calendars));
  }


  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate (R.menu.menu_event_page, menu);
    return true;
  }
  //turn on GPS
  public void turnGPSOn()
  {
    Intent intent = new Intent("android.location.GPS_ENABLED_CHANGE");
    intent.putExtra("enabled", true);
    this.ctx.sendBroadcast (intent);

    String provider = Settings.Secure.getString(ctx.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
    if(!provider.contains("gps")){ //if gps is disabled
      final Intent poke = new Intent();
      poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
      poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
      poke.setData(Uri.parse ("3"));
      this.ctx.sendBroadcast(poke);


    }
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();

    //noinspection SimplifiableIfStatement
    if (id == R.id.action_settings) {
      return true;
    }

    return super.onOptionsItemSelected(item);
  }



  public class EventAdapter extends BaseAdapter {
    private Context context;

    private Calendar[] calendars;

    public EventAdapter(Context context, Calendar[] calendars) {
      this.context = context;
      this.calendars = calendars;
    }

    @Override
    public int getCount() {
      return calendars.length;
    }

    @Override
    public Object getItem(int position) {
      return calendars[position];
    }

    @Override
    public long getItemId(int position) {
      return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      View row = null;
      Calendar calendar = calendars[position];
      if (convertView == null) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        row = inflater.inflate(R.layout.event_page_adapter, parent, false);
      } else {
        row = convertView;
      }
      ((TextView) row.findViewById(R.id.summary)).setText(calendar.summary);
      ((TextView) row.findViewById(R.id.location)).setText(calendar.location);
      try {
        ((TextView) row.findViewById(R.id.start)).setText(Vars.dateToRelativeString(calendar.start));
        ((TextView) row.findViewById(R.id.end)).setText(Vars.dateToRelativeString(calendar.end));
      }
      catch (Exception e) {

      }
      return row;
    }
  }
}
