package com.uber.executer.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.uber.executer.R;
import com.uber.executer.Singletons.Vars;
import com.uber.executer.activities.BookRide;
import com.uber.executer.activities.MainActivity;
import com.uber.executer.models.Calendar;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.Locale;

/**
 * Created by aliyuolalekan on 8/10/15.
 */
public class EventPageFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener{
  private static final String TAG = "Connect";
  String summary;
  private IntentFilter filter;
  ListView eventList;
  String coolTime;
  String myStreet;
  protected  GoogleApiClient googleApiClient;
  protected LocationRequest locationRequest;
  double longitude;
  double latitude;
  Calendar[] calendars = MainActivity.calendars;


  @Nullable
  @Override
  public View onCreateView (final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    super.onCreateView (inflater, container, savedInstanceState);
    View view = inflater.inflate (R.layout.events, container, false);

    final LocationManager manager = (LocationManager)getActivity ().getSystemService (Context.LOCATION_SERVICE);

//    if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ){
//      Toast.makeText(getActivity (), "GPS is disabled!", Toast.LENGTH_LONG).show();
//      startActivity (new Intent (android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
//      Toast.makeText (getActivity (),"Turn on GPS",Toast.LENGTH_SHORT).show ();
//    }
//    else
//      Toast.makeText(getActivity (), "GPS is enabled!", Toast.LENGTH_LONG).show();
    eventList = (ListView)view. findViewById(R.id.events);
    eventList.setAdapter (new EventAdapter (getActivity (), calendars));
    eventList.setOnItemClickListener (new AdapterView.OnItemClickListener () {
      @Override
      public void onItemClick (AdapterView<?> parent, View view, int position, long id) {

        Calendar calendar = (Calendar) parent.getItemAtPosition (position);
        try {
          coolTime = Vars.dateToRelativeString ((calendar.start).toString ());
        } catch (ParseException e) {
          e.printStackTrace ();
        }

        Geocoder geocoder = new Geocoder(getActivity (), Locale.getDefault ());
        List<Address> addresses = null;
        try {
          addresses = geocoder.getFromLocation(latitude, longitude, 1);
        } catch (IOException e) {
          e.printStackTrace ();
        }
        String cityName = addresses.get(0).getAddressLine(0);
        String stateName = addresses.get(0).getAddressLine(1);
        String countryName = addresses.get(0).getAddressLine(2);
        Intent intent = new Intent (getActivity (), BookRide.class);
        intent.putExtra ("summary", calendar.getSummary ());
        intent.putExtra ("location", calendar.getLocation ());
        intent.putExtra ("startTime", coolTime);
        intent.putExtra ("currentLocation", cityName+"/"+stateName);
        intent.putExtra ("longitude",longitude);
        intent.putExtra ("latitude",latitude);
        startActivity (intent);
      }
    });
    return view;
  }
  public void onStart(){
    super.onStart ();
    googleApiClient.connect ();
  }
  public void onStop(){
    super.onStop ();
    if(googleApiClient.isConnected ()){
      googleApiClient.disconnect ();
    }
  }
  protected synchronized void buidGoogleApiClient(){
    googleApiClient = new GoogleApiClient.Builder (getActivity ())
            .addConnectionCallbacks (this)
            .addOnConnectionFailedListener (this)
            .addApi (LocationServices.API).build ();
  }

  @Override
  public void onCreate (@Nullable Bundle savedInstanceState) {
    super.onCreate (savedInstanceState);
    buidGoogleApiClient ();
      }

  @Override
  public void onConnected (Bundle bundle) {
    locationRequest = LocationRequest.create ();
    locationRequest.setPriority (LocationRequest.PRIORITY_HIGH_ACCURACY);
    locationRequest.setInterval (1000);
    LocationServices.FusedLocationApi.requestLocationUpdates (googleApiClient, locationRequest, this);

  }

  @Override
  public void onConnectionSuspended (int i) {
    Log.i (TAG, "Connection suspended");
    googleApiClient.connect();
  }

  @Override
  public void onLocationChanged (Location location) {
//    Toast.makeText (getActivity (), String.valueOf (location.getLatitude ())+" "+String.valueOf (location.getLongitude ()),Toast.LENGTH_LONG).show ();
    longitude = location.getLongitude ();
    latitude = location.getLatitude ();

  }
  public double getLatitude(double latitude){
    this.latitude = latitude;
    return latitude;
  }
  public double getLongitude(double longitude){
    this.longitude = longitude;
    return longitude;
  }

  @Override
  public void onConnectionFailed (ConnectionResult connectionResult) {
    Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + connectionResult.getErrorCode());
  }


  public class EventAdapter extends BaseAdapter {
    private Context context;

    public Calendar[] calendars;

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
      summary = calendar.summary;
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
      }
      catch (Exception e) {

      }
      return row;
    }
  }
}
