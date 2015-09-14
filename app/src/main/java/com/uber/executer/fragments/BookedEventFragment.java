package com.uber.executer.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.gson.JsonObject;
import com.uber.executer.R;
import com.uber.executer.Singletons.Vars;
import com.uber.executer.activities.EventBookedDetails;
import com.uber.executer.models.BookedEventData;
import com.uber.executer.models.Calendar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by aliyuolalekan on 9/12/15.
 */
public class BookedEventFragment extends Fragment {
  EventBooked eventBooked;
  ArrayList<EventBooked> eventBookedArrayList = new ArrayList<EventBooked> ();
  ArrayAdapter<String> adapter;
  Dialog dialog;
  ArrayList<String> bookedEventss = new ArrayList<String> ();
  ArrayList<String> summary;
  ListView bookedEventList;
  TextView noevent;
  BookedEventData[] bookedEvents;

  //check internet connectivity

  @Nullable
  @Override
  public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    super.onCreateView (inflater, container, savedInstanceState);
    View view = inflater.inflate (R.layout.booked_events_new_layout, container, false);
    final String[] dataSet = new String[]{"monday", "tuesday", "wednesday"};
    bookedEventList = (ListView) view.findViewById (R.id.listOfItemsBooked);
    bookedEventList.setAdapter (new ArrayAdapter<String> (getActivity ()
            ,R.layout.booked_event_listview_adapter,R.id.event_summary,dataSet));
    try {
      RequestQueue queue = Volley.newRequestQueue (getActivity ());
      final StringRequest request = new StringRequest (Request.Method.GET,
              "http://andelahack.herokuapp.com/users/" + Vars.user.response.uuid + "/requests",
              new Response.Listener<String> () {
                @Override
                public void onResponse (String response) {
                  try {
                    JSONObject result = new JSONObject (response);
                    JSONArray resultValues = result.getJSONArray ("response");
                    for (int i = 0; i < resultValues.length (); i++) {
                      JSONObject currentObject = resultValues.getJSONObject (i);
                      JSONObject product = currentObject.getJSONObject ("product");
                      JSONObject estimate = currentObject.getJSONObject ("estimates");
                      JSONObject destination = currentObject.getJSONObject ("destination");
                      bookedEventss.add (destination.getString ("address"));
                      eventBooked = new EventBooked ();
                      eventBooked.reminder = estimate.getString ("reminder");
                      eventBooked.product = product.getString ("type");
                      eventBooked.summary = destination.getString ("address");
                      eventBooked.starts = currentObject.getString ("startTime");
                      eventBookedArrayList.add (eventBooked);
                    }

                    final MyNewAdapter adapterNew = new MyNewAdapter (getActivity (), eventBookedArrayList);
                    bookedEventList.setAdapter (adapterNew);
                    bookedEventList.setOnItemClickListener (new AdapterView.OnItemClickListener () {
                      @Override
                      public void onItemClick (AdapterView<?> parent, View view, int position, long id) {
                        Intent eventDetails = new Intent (getActivity (), EventBookedDetails.class);
                        eventDetails.putExtra ("start", eventBookedArrayList.get (position).starts);
                        eventDetails.putExtra ("destination", eventBookedArrayList.get (position).summary);
                        eventDetails.putExtra ("reminder", eventBookedArrayList.get (position).reminder);
                        eventDetails.putExtra ("type", eventBookedArrayList.get (position).product);
                        eventDetails.putExtra ("id", eventBookedArrayList.get (position).ids);
                        startActivity (eventDetails);
                      }
                    });

                    bookedEventList.setOnItemLongClickListener (new AdapterView.OnItemLongClickListener () {
                      @Override
                      public boolean onItemLongClick (AdapterView<?> parent, View view, final int position, long id) {
                        final Dialog dialog = new Dialog (getActivity ());
                        dialog.setContentView (R.layout.delete_item);
                        TextView deleteTitle = (TextView) dialog.findViewById (R.id.titleDelete);
                        final CheckBox sureDelete = (CheckBox) dialog.findViewById (R.id.yesIamSureCheckBox);
                        Button yesDelete = (Button) dialog.findViewById (R.id.deleteItemNow);
                        Button noDontDelete = (Button) dialog.findViewById (R.id.noDontnow);
                        deleteTitle.setText ("Deleting " + eventBookedArrayList.get (position).summary + " event?");

                        //delete item
                        yesDelete.setOnClickListener (new View.OnClickListener () {
                          @Override
                          public void onClick (View v) {
                            if (sureDelete.isChecked () && isOnline ()) {
                              RequestQueue queue1 = Volley.newRequestQueue (getActivity ());
                              StringRequest request1 = new StringRequest (Request.Method.DELETE,
                                      "http://andelahack.herokuapp.com/users/" + Vars.user.response.uuid
                                              + "/requests/" + eventBookedArrayList.get (position).ids,
                                      new Response.Listener<String> () {
                                        @Override
                                        public void onResponse (String response) {
                                          Toast.makeText (getActivity (), "Item deleted successfully",
                                                  Toast.LENGTH_SHORT).show ();
                                          eventBookedArrayList.remove (position);
                                          adapterNew.notifyDataSetChanged ();
                                          dialog.hide ();
                                        }
                                      }, new Response.ErrorListener () {
                                @Override
                                public void onErrorResponse (VolleyError error) {
                                  Toast.makeText (getActivity (), "Please check you internet connection",
                                          Toast.LENGTH_SHORT).show ();

                                }
                              });

                              int socketTimeout = 30000;//30 seconds - change to what you want
                              RetryPolicy policy = new DefaultRetryPolicy (socketTimeout, DefaultRetryPolicy
                                      .DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                              request1.setRetryPolicy (policy);
                              queue1.add (request1);

                            } else {
                              Toast.makeText (getActivity (), "Please confirm by checking the checkbox", Toast.LENGTH_SHORT).show ();
                            }

                          }
                        });

                        //hide dialog
                        noDontDelete.setOnClickListener (new View.OnClickListener () {
                          @Override
                          public void onClick (View v) {
                            dialog.hide ();
                          }
                        });
                        dialog.show ();
                        return true;
                      }
                    });
                    // bookedEventList.setAdapter (new ArrayAdapter<String> (getActivity (), R.layout.booked_event_listview_adapter, R.id.event_summary, bookedEventss));

//                  GsonBuilder gsonBuilder = new GsonBuilder();
//                  Gson gson = gsonBuilder.create();
//
//                  bookedEvents = gson.fromJson (String.valueOf (resultValues), BookedEventData[].class);
//                  for(BookedEventData eventData: bookedEvents){
//                    Log.e ("start", eventData.startTime);
//                  }
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
    catch (Exception e){
      e.printStackTrace ();
    }
    return view;
  }


  public void setUp (View view) {

  }

  //check if network is available
  public boolean isOnline () {
    ConnectivityManager connectivityManager = (ConnectivityManager) getActivity ()
            .getSystemService (Context.CONNECTIVITY_SERVICE);
    NetworkInfo info = connectivityManager.getActiveNetworkInfo ();
    if (info != null && info.isConnected ()) {
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
    public String destination;
    public String product;
    public String type;
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
        e.printStackTrace ();
      }
      return view;
    }
  }
}
