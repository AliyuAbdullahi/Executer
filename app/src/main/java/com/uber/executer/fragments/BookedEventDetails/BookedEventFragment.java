package com.uber.executer.fragments.BookedEventDetails;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.uber.executer.R;
import com.uber.executer.activities.EventBookedDetails;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by aliyuolalekan on 9/12/15.
 */
public class BookedEventFragment extends Fragment {
  //Fragment to show the details of the booked event
  @Nullable
  @Override
  public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    super.onCreateView (inflater, container, savedInstanceState);
    View view = inflater.inflate (R.layout.booked_detail_fragment_layout, container, false);
    EventBookedDetails bookedDetails = (EventBookedDetails)getActivity ();
    TextView to = (TextView) view.findViewById (R.id.to);
    TextView product = (TextView) view.findViewById (R.id.product);
    TextView reminder = (TextView) view.findViewById (R.id.reminder);

    to.setText (bookedDetails.getLocationTo ());
    product.setText (bookedDetails.getType ());

    String timeFormatter2 = bookedDetails.getReminder ().split("\\+")[0];
    SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    try {
      Date date2 = sdf2.parse (timeFormatter2);
      SimpleDateFormat dt2 = new SimpleDateFormat("yyyy-mm-dd hh:mm");
      reminder.setText (dt2.format (date2));
    } catch (ParseException e) {
      e.printStackTrace ();
    }
    return view;
  }
}
