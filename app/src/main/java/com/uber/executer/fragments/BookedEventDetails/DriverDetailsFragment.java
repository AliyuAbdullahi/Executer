package com.uber.executer.fragments.BookedEventDetails;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.uber.executer.R;

/**
 * Created by aliyuolalekan on 9/12/15.
 */
public class DriverDetailsFragment extends Fragment {
  @Nullable
  @Override
  public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    super.onCreateView (inflater, container, savedInstanceState);
    View view = inflater.inflate (R.layout.driver_detail_fragment, container, false);
    return view;
  }
}
