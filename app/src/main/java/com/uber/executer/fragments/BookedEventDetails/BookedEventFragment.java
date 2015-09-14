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

/**
 * Created by aliyuolalekan on 9/12/15.
 */
public class BookedEventFragment extends Fragment {

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
    reminder.setText (bookedDetails.getReminder ());
    return view;
  }
}
