package com.uber.executer;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.internal.widget.AdapterViewCompat;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.uber.executer.models.BookedEvent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class BookedEvents extends AppCompatActivity {
  Toolbar toolbar;
  Dialog dialog;
  ListView bookedEventList;
  private ArrayList<String> bookedEvents = new ArrayList<String> ();
  private ArrayList<String> time = new ArrayList<String> ();
  private ArrayAdapter<String> adapter;
  private ArrayList<String> location = new ArrayList<String>();
  private ArrayList<BookedEvent> events = new ArrayList<BookedEvent> ();


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
    final BookedEvent current = new BookedEvent ();

    RequestQueue queue = Volley.newRequestQueue (getApplicationContext ());
    final StringRequest request = new StringRequest (Request.Method.GET,
            "http://andelahack.herokuapp.com/users/35c2856e-068b-43ba-94d3-3840af926b36/requests",
            new Response.Listener<String> () {

              @Override
              public void onResponse (String response) {
                try {
                  JSONObject result = new JSONObject (response);
                  JSONArray array = result.getJSONArray ("response");
                  for(int i=0; i<array.length (); i++){
                    JSONObject currentResult = array.getJSONObject (i);
                    current.setSummary (currentResult.getString ("summary"));
                    current.setEventTime (currentResult.getString ("startTime"));
                    current.getPickUpLocation (currentResult.getString ("pickUpLocation"));
                    events.add (current);
                    location.add (currentResult.getString ("pickUpLocation"));
                    bookedEvents.add (currentResult.getString ("summary"));
                  }
                  adapter = new ArrayAdapter<String> (getApplicationContext (),R.layout.booked_event_listview_adapter,R.id.event_summary,bookedEvents);
                  bookedEventList.setAdapter (adapter);
                 bookedEventList.setOnItemClickListener (new AdapterView.OnItemClickListener () {
                   @Override
                   public void onItemClick (AdapterView<?> parent, View view, final int position, long id) {
                     Toast.makeText (getApplicationContext (),location.get (position),Toast.LENGTH_LONG).show ();
                     dialog = new Dialog (BookedEvents.this);
                     dialog.setContentView (R.layout.dialog_delete_item);
                     dialog.setTitle ("");
                     Button delete = (Button)dialog.findViewById (R.id.delete_booked_event);
                     delete.setOnClickListener (new View.OnClickListener () {
                       @Override
                       public void onClick (View v) {
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
    ArrayList<BookedEvent> bookedEvents;
    public myAdapter(Context context, ArrayList<BookedEvent> bookedEvents){
      this.context = context;
      this.bookedEvents = bookedEvents;
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
      BookedEvent currentEvent = bookedEvents.get (position);
      summary.setText (currentEvent.getSummary ());
      return null;
    }
  }
}
