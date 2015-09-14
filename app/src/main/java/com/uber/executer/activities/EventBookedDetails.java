package com.uber.executer.activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.plus.Plus;
import com.uber.executer.R;
import com.uber.executer.Singletons.MyApp;
import com.uber.executer.Singletons.Vars;
import com.uber.executer.fragments.BookedEventDetails.DriverDetailsFragment;
import com.uber.executer.fragments.BookedEventFragment;
import com.uber.executer.fragments.EventPageFragment;

import it.neokree.materialtabs.MaterialTab;
import it.neokree.materialtabs.MaterialTabHost;
import it.neokree.materialtabs.MaterialTabListener;

public class EventBookedDetails extends AppCompatActivity implements MaterialTabListener {
  MaterialTabHost tabHost;
  ViewPager pager;
  Toolbar toolbar;
  public String starts;
  public String type;
  public String summary;
  public String locationTo;
  public String reminder;
  TextView titleOfevent;
  TextView startTimeValue;
  TextView ends;
  @Override
  protected void onCreate (Bundle savedInstanceState) {
    super.onCreate (savedInstanceState);
    setContentView (R.layout.activity_event_booked_details);
    pager = (ViewPager)findViewById (R.id.pagerForBookedEventDetail);
    titleOfevent = (TextView)findViewById (R.id.titleOfEvent);
    tabHost = (MaterialTabHost) this.findViewById (R.id.materialTabHostOne);
    toolbar = (Toolbar)findViewById (R.id.toolbar);
    setSupportActionBar (toolbar);
    TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
    mTitle.setText ("executer");
    mTitle.setAllCaps (false);
    Typeface tf = Typeface.createFromAsset (getAssets (),"MuseoSans-300.otf");
    mTitle.setTypeface (tf);
    getSupportActionBar ().setDisplayHomeAsUpEnabled (false);
    getSupportActionBar ().setDisplayShowHomeEnabled (false);
    getSupportActionBar ().setDisplayShowTitleEnabled (false);
    Intent got = getIntent ();

    type = got.getStringExtra ("type");
    starts = got.getStringExtra ("start");
    reminder = got.getStringExtra ("reminder");
    summary = got.getStringExtra ("destination");
    startTimeValue = (TextView)findViewById (R.id.startTimeValueofEvent);
    startTimeValue.setText (starts);
    titleOfevent.setText (summary);
   //locationTo = got.getStringExtra ("destination");


    Bundle bundle = new Bundle();
    bundle.putString("starts", starts);
    bundle.putString ("type", type);
    bundle.putString ("destination", summary);
    bundle.putString ("reminder", reminder);
// set Fragmentclass Arguments
    com.uber.executer.fragments.BookedEventDetails.BookedEventFragment fragobj
            = new com.uber.executer.fragments.BookedEventDetails.BookedEventFragment ();
    fragobj.setArguments (bundle);




    MyPagerAdapter pagerAdapter = new MyPagerAdapter (getSupportFragmentManager ());
    pager.setAdapter (pagerAdapter);
    pager.setOnPageChangeListener (new ViewPager.SimpleOnPageChangeListener () {
      @Override
      public void onPageSelected (int position) {
        super.onPageSelected (position);
        tabHost.setSelectedNavigationItem (position);
      }
    });
    for (int i = 0; i < pagerAdapter.getCount (); i++){
      tabHost.addTab (tabHost.newTab ().setText (pagerAdapter
              .getPageTitle (i))
              .setTabListener (this));
    }

  }

  @Override
  public boolean onCreateOptionsMenu (Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater ().inflate (R.menu.menu_event_booked_details, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected (MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId ();

    //noinspection SimplifiableIfStatement
    if (id == R.id.logout) {
      Vars.clearDB (getApplicationContext ());
      Intent intent = new Intent (EventBookedDetails.this, LoginActivity.class);
      startActivity (intent);
      Toast.makeText (getApplicationContext (), "You have logged out", Toast.LENGTH_SHORT).show ();
      return true;
    }
    if (id == R.id.changeAccount) {
      revokeAccess ();
      Intent i = new Intent (EventBookedDetails.this, MainActivity.class);
      startActivity (i);

    }
    return true;
  }
  public void revokeAccess(){
    if (MyApp.mGoogleApiClient.isConnected()) {
      Plus.AccountApi.clearDefaultAccount(MyApp.mGoogleApiClient);
      MyApp.mGoogleApiClient.disconnect();
      MyApp.mGoogleApiClient.connect();
    }
  }

  @Override
  public void onTabSelected (MaterialTab materialTab) {
    pager.setCurrentItem (materialTab.getPosition ());
  }

  @Override
  public void onTabReselected (MaterialTab materialTab) {

  }

  @Override
  public void onTabUnselected (MaterialTab materialTab) {

  }
  public class MyPagerAdapter extends FragmentPagerAdapter {

    public MyPagerAdapter (FragmentManager fm) {
      super (fm);
    }

    @Override
    public Fragment getItem (int position) {
      switch (position){
        case 0:
          return new com.uber.executer.fragments.BookedEventDetails.BookedEventFragment ();
        case 1:
          return new DriverDetailsFragment ();
        default:
          break;
      }
      return null;
    }

    @Override
    public int getCount () {
      return 2;
    }

    @Override
    public CharSequence getPageTitle (int position) {
      return getResources ().getStringArray (R.array.details_events)[position];
    }
  }


  public String getStarts () {
    Intent here = getIntent ();
    starts = here.getStringExtra ("starts");
    return starts;
  }

  public String getType () {
    Intent here = getIntent ();
    type = here.getStringExtra ("type");
    return type;
  }

  public String getLocationTo () {
    Intent here = getIntent ();
    locationTo = here.getStringExtra ("destination");
    return locationTo;
  }

  public String getReminder () {
    Intent here = getIntent ();
    reminder = here.getStringExtra ("reminder");
    return reminder;
  }
}
