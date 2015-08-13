package com.uber.executer;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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

import java.util.ArrayList;
import java.util.List;

public class BookedEvents extends AppCompatActivity {
  ListView bookedEventList;
 private List<BookedEvent> bookedEvents = new ArrayList<> ();


  @Override
  protected void onCreate (Bundle savedInstanceState) {
    super.onCreate (savedInstanceState);
    setContentView (R.layout.activity_booked_events);
    bookedEventList = (ListView)findViewById (R.id.listView);
    RequestQueue queue = Volley.newRequestQueue (this);
    final StringRequest request = new StringRequest (Request.Method.GET,
            "http://andelahack.herokuapp.com/users/35c2856e-068b-43ba-94d3-3840af926b36/requests",
            new Response.Listener<String> () {
      BookedEvent currentEvent;
      @Override
      public void onResponse (String response) {
        Toast.makeText (getApplicationContext (), response.toString (),Toast.LENGTH_LONG).show ();
        try {
          JSONArray results = new JSONArray (response);
          for(int i=0; i<results.length (); i++){
            JSONObject currentResult = results.getJSONObject (i);
            currentEvent.setPickUpLocation (currentResult.getString ("pickUpLocation"));
            currentEvent.setEventLocation (currentResult.getString ("destination"));
            currentEvent.setEventTime (currentResult.getString ("time"));
            currentEvent.setEventName (currentResult.getString ("summary"));
          }
        } catch (JSONException e) {
          e.printStackTrace ();
        }
        bookedEvents.add (currentEvent);
      }
    }, new Response.ErrorListener () {
      @Override
      public void onErrorResponse (VolleyError error) {

      }
    });
    queue.add (request);
    myAdapter myadapter = new myAdapter (this,bookedEvents);
    bookedEventList.setAdapter (myadapter);
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
  public class myAdapter extends BaseAdapter{
    List<BookedEvent> bookedEvents = new ArrayList<> ();
    Context context;
    public myAdapter(Context context, List<BookedEvent> bookedEvents){
      this.bookedEvents = bookedEvents;
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
      BookedEvent currentBookedEvent = bookedEvents.get (position);
      View row = null;
      if(convertView == null){
        LayoutInflater inflater = (LayoutInflater)getSystemService (Context.LAYOUT_INFLATER_SERVICE);
        row = inflater.inflate (R.layout.booked_event_listview_adapter, parent, false);
        TextView textView = (TextView)row.findViewById (R.id.event_time);
        TextView summar = (TextView)row.findViewById (R.id.event_summary);
        textView.setText (currentBookedEvent.getEventTime ());
        summar.setText (currentBookedEvent.getEventName ());
      }
      else {
        row = convertView;
      }
      return row;
    }
  }
}
