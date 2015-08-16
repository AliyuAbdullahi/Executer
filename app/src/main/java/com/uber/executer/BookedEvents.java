package com.uber.executer;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.uber.executer.models.BookedEventData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BookedEvents extends AppCompatActivity {
  Toolbar toolbar;
  ArrayAdapter<String> adapter;
  Dialog dialog;
  ListView bookedEventList;
  private ArrayList<String> bookedEvents = new ArrayList<String> ();
  private ArrayList<String> location = new ArrayList<String>();
  private ArrayList<BookedEventData> events = new ArrayList<BookedEventData> ();



  @Override
  protected void onCreate (Bundle savedInstanceState) {
    super.onCreate (savedInstanceState);
    setContentView (R.layout.activity_booked_events);
    toolbar = (Toolbar)findViewById (R.id.toolbar);
    setSupportActionBar (toolbar);
    getSupportActionBar().setDisplayShowTitleEnabled (false);
    TextView toolbarTitle = (TextView)findViewById (R.id.toolbar_title);
    toolbarTitle.setText ("EXECUTER");
    Typeface tf = Typeface.createFromAsset (getAssets (),"MuseoSans-300.otf");
    toolbarTitle.setTypeface (tf);
    bookedEventList = (ListView)findViewById (R.id.bookedlist);


    RequestQueue queue = Volley.newRequestQueue (getApplicationContext ());
    final StringRequest request = new StringRequest (Request.Method.GET,
            "http://andelahack.herokuapp.com/users/"+Vars.user.response.uuid+"/requests",
            new Response.Listener<String> () {

              @Override
              public void onResponse (String response) {
                try {
                  JSONObject result = new JSONObject (response);
                  JSONArray array = result.getJSONArray ("response");
                  for(int i=0; i<array.length (); i++){
                    BookedEventData current = new BookedEventData ();
                    JSONObject currentResult = array.getJSONObject (i);
                    current.setSummary (currentResult.getString ("summary"));
                    current.setEventDestination (currentResult.getString ("destination"));
                    current.setEventTime (currentResult.getString ("startTime"));
                    current.setPickUpLocation (currentResult.getString ("pickUpLocation"));
                    current.setId (currentResult.getString ("id"));
                    current.setUberType (currentResult.getString ("uberType"));
                    events.add (current);
                    location.add (currentResult.getString ("pickUpLocation"));
                    bookedEvents.add (currentResult.getString ("summary"));

                  }
                  adapter = new ArrayAdapter<String> (getApplicationContext (),R.layout.booked_event_listview_adapter,R.id.event_summary,bookedEvents);

                bookedEventList.setAdapter (adapter);

                 bookedEventList.setOnItemClickListener (new AdapterView.OnItemClickListener () {
                   @Override
                   public void onItemClick (AdapterView<?> parent, View view, final int position, final long id) {

                     Toast.makeText (getApplicationContext (),events.get (position).getId (),Toast.LENGTH_LONG).show ();
                     dialog = new Dialog (BookedEvents.this);
                     dialog.setContentView (R.layout.dialog_delete_item);
                     dialog.setTitle (bookedEvents.get (position));
                     Button delete = (Button)dialog.findViewById (R.id.delete_booked_event);
                     TextView location = (TextView)dialog.findViewById (R.id.location_reciept);
                     TextView timeRciept = (TextView)dialog.findViewById (R.id.time_reciept);
                     TextView uberTypeReciept = (TextView)dialog.findViewById (R.id.uber_type_reciept);
                     TextView destinationReciept = (TextView)dialog.findViewById (R.id.destination_reciept);
                     location.setText (events.get (position).getEventLocation ());
                     uberTypeReciept.setText (events.get (position).getUberType ());
                     timeRciept.setText (events.get (position).getEventTime ());
                     destinationReciept.setText (events.get (position).getEventDestination ());

                     Button ok = (Button)dialog.findViewById (R.id.okay);
                     ok.setOnClickListener (new View.OnClickListener () {
                       @Override
                       public void onClick (View v) {
                         dialog.hide ();
                       }
                     });


                     delete.setOnClickListener (new View.OnClickListener () {
                       @Override
                       public void onClick (View v) {
                         if (isOnline ()) {
                           RequestQueue requestQueue = Volley.newRequestQueue (BookedEvents.this);
                           StringRequest request1 = new StringRequest (Request.Method.DELETE,
                                   "http://andelahack.herokuapp.com/users/" + Vars.user.response.uuid + "/requests/" + events.get (position).getId (),
                                   new Response.Listener<String> () {
                                     @Override
                                     public void onResponse (String response) {
                                       Toast.makeText (getApplicationContext (), response, Toast.LENGTH_SHORT).show ();

                                     }
                                   }, new Response.ErrorListener () {
                             @Override
                             public void onErrorResponse (VolleyError error) {
                               Toast.makeText (getApplicationContext (), "Error: " + error, Toast.LENGTH_SHORT).show ();
                             }
                           });
                           requestQueue.add (request1);
                           dialog.hide ();
                         }
                         bookedEvents.remove (position);
                         adapter.notifyDataSetChanged ();

                       }
                     });
                     dialog.show ();
                   }

                 });
                } catch (JSONException e) {
                  e.printStackTrace ();
                }

              }
            }, new Response.ErrorListener () {
      @Override
      public void onErrorResponse (VolleyError error) {

      }
    });
    queue.add (request);

  }

  //check if network is available
  public boolean isOnline() {
    ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
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
    getMenuInflater ().inflate (R.menu.menu_booked_events, menu);
    return true;
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


  class myAdapter extends BaseAdapter {
    private Context context;
    private myAdapter(Context context){
      this.context = context;
    }

    @Override
    public int getCount () {
      return bookedEvents.size ();
    }

    @Override
    public Object getItem (int position) {
      return bookedEvents.get (position);
    }

    @Override
    public long getItemId (int position) {
      return position;
    }

    @Override
    public View getView (int position, View convertView, ViewGroup parent) {
      View row = null;
      if(convertView == null){
        LayoutInflater inflater = (LayoutInflater)context.getSystemService (Context.LAYOUT_INFLATER_SERVICE);
        row = inflater.inflate (R.layout.booked_event_listview_adapter, parent, false);

      }
      else {
        row = convertView;
      }
      TextView summary = (TextView)row.findViewById(R.id.event_summary);
      BookedEventData currentEvent = events.get (position);
      summary.setText (currentEvent.getSummary ());
      return null;
    }
  }
}
