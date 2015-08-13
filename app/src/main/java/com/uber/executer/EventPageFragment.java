package com.uber.executer;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.uber.executer.models.Calendar;

import java.text.ParseException;
import java.util.List;

/**
 * Created by aliyuolalekan on 8/10/15.
 */
public class EventPageFragment extends Fragment {
  String summary;
  ListView eventList;
  String coolTime;
  @Nullable
  @Override
  public View onCreateView (final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    super.onCreateView (inflater, container, savedInstanceState);
    View view = inflater.inflate (R.layout.events, container, false);
    eventList = (ListView)view. findViewById(R.id.events);
    eventList.setAdapter (new EventAdapter (getActivity (), Vars.calendars));
    eventList.setOnItemClickListener (new AdapterView.OnItemClickListener () {
      @Override
      public void onItemClick (AdapterView<?> parent, View view, int position, long id) {

        Calendar calendar = (Calendar)parent.getItemAtPosition (position);
        try {
          coolTime = Vars.dateToRelativeString ((calendar.start).toString ());
        } catch (ParseException e) {
          e.printStackTrace ();
        }
        Intent intent = new Intent (getActivity (), BookRide.class);
        intent.putExtra ("summary", calendar.getSummary ());
        intent.putExtra ("location",calendar.getLocation ());
        intent.putExtra ("startTime", coolTime);
        startActivity (intent);
      }
    });
    return view;
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
