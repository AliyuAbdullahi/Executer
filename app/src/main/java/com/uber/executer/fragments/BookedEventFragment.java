package com.uber.executer.fragments;

import android.app.Dialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.uber.executer.R;
import com.uber.executer.Singletons.Vars;
import com.uber.executer.models.BookedEventData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by aliyuolalekan on 9/12/15.
 */
public class BookedEventFragment extends Fragment {
  ArrayList<EventBooked> eventBookedArrayList;
  ArrayAdapter<String> adapter;
  Dialog dialog;
  private ArrayList<String> bookedEvents = new ArrayList<String> ();
  ListView listView;
  TextView noevent;

  @Nullable
  @Override
  public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    super.onCreateView (inflater, container, savedInstanceState);
    View view = inflater.inflate (R.layout.trip_detail_fragment_layout, container, false);
    setUp (view);
    return view;
  }

  public void setUp (View view) {
    listView = (ListView) view.findViewById (R.id.events);

    RequestQueue queue = Volley.newRequestQueue (getActivity ());
    final StringRequest request = new StringRequest (Request.Method.GET,
            "http://andelahack.herokuapp.com/users/" + Vars.user.response.uuid + "/requests",
            new Response.Listener<String> () {
              @Override
              public void onResponse (String response) {
                try {
                  JSONObject result = new JSONObject (response);
                  final JSONArray array = result.getJSONArray ("response");
                  for (int i = 0; i < array.length (); i++) {


                  }
                  //  adapter = new ArrayAdapter<String> (getActivity (), R.layout.booked_event_listview_adapter, R.id.event_summary, bookedEvents);
                 MyNewAdapter newAdapter = new MyNewAdapter (getActivity (), eventBookedArrayList);
                  if (listView == null)
                    noevent.setVisibility (View.VISIBLE);
                  listView.setAdapter (newAdapter);
                  listView.setOnItemClickListener (new AdapterView.OnItemClickListener () {
                    @Override
                    public void onItemClick (AdapterView<?> parent, View view, final int position, final long id) {

                      dialog = new Dialog (getActivity ());
                      dialog.setContentView (R.layout.dialog_delete_item);
                      dialog.setTitle (bookedEvents.get (position));
                      Button delete = (Button) dialog.findViewById (R.id.delete_booked_event);
                      TextView timeRciept = (TextView) dialog.findViewById (R.id.time_reciept);

                      Button ok = (Button) dialog.findViewById (R.id.okay);
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
                            RequestQueue requestQueue = Volley.newRequestQueue (getActivity ());
                            StringRequest request1 = new StringRequest (Request.Method.DELETE,
                                    "http://andelahack.herokuapp.com/users/" + Vars.user.response.uuid + "/requests/" + eventBookedArrayList.get (position).ids,
                                    new Response.Listener<String> () {
                                      @Override
                                      public void onResponse (String response) {
                                        Toast.makeText (getActivity (), "Deleted Successfully", Toast.LENGTH_SHORT).show ();

                                      }
                                    }, new Response.ErrorListener () {
                              @Override
                              public void onErrorResponse (VolleyError error) {
                                Toast.makeText (getActivity (), "Error: " + error, Toast.LENGTH_SHORT).show ();
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
  public boolean isOnline () {
    ConnectivityManager connectivityManager = (ConnectivityManager) getActivity ().getSystemService (Context.CONNECTIVITY_SERVICE);
    NetworkInfo info = connectivityManager.getActiveNetworkInfo ();
    if (info != null && info.isConnectedOrConnecting ()) {
      return true;
    } else {
      return false;
    }

  }
  public class EventBooked{
    public String summary;
    public String starts;
    public String ends;
    public String ids;
    public String reminder;

  }
  public class MyNewAdapter extends BaseAdapter{
    ArrayList<EventBooked> eventBookeds;
    Context context;
    public MyNewAdapter(Context context, ArrayList<EventBooked> eventBookeds){
      this.eventBookeds = eventBookeds;
      this.context = context;

    }

    @Override
    public int getCount () {
      return eventBookeds.size ();
    }

    @Override
    public Object getItem (int position) {
      return eventBookeds.get (position);
    }

    @Override
    public long getItemId (int position) {
      return position;
    }

    @Override
    public View getView (int position, View convertView, ViewGroup parent) {
      EventBooked eventBooked = eventBookeds.get (position);
      View view = null;
      if (convertView == null) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.booked_event_layout, parent, false);
      } else {
        view = convertView;
      }
      ((TextView) view.findViewById(R.id.summary)).setText(eventBooked.summary);
      try {
        ((TextView) view.findViewById(R.id.start)).setText(Vars.dateToRelativeString(eventBooked.starts));
      }
      catch (Exception e) {

      }

      return view;
    }
  }
}
