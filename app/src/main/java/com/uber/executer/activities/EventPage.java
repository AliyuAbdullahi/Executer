package com.uber.executer.activities;

import android.graphics.Typeface;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.uber.executer.Singletons.Vars;
import com.uber.executer.fragments.BookedEventFragment;
import com.uber.executer.fragments.EventPageFragment;
import com.uber.executer.fragments.NavFragment;
import com.uber.executer.R;

import it.neokree.materialtabs.MaterialTab;
import it.neokree.materialtabs.MaterialTabHost;
import it.neokree.materialtabs.MaterialTabListener;

public class EventPage extends AppCompatActivity implements MaterialTabListener {
  MaterialTabHost tabHost;
  ViewPager pager;
  Toolbar toolbar;
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate (savedInstanceState);
    setContentView (R.layout.activity_event_page);
    pager = (ViewPager)findViewById (R.id.pager);
    tabHost = (MaterialTabHost) this.findViewById (R.id.materialTabHost);
  //  Toast.makeText (getApplicationContext (), Vars.user.response.google_token.toString (),Toast.LENGTH_LONG).show ();
    toolbar = (Toolbar)findViewById (R.id.toolbar);
    setSupportActionBar (toolbar);
    TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
    mTitle.setText ("EXECUTER");
    Typeface tf = Typeface.createFromAsset (getAssets (),"MuseoSans-300.otf");
    mTitle.setTypeface (tf);
    getSupportActionBar ().setDisplayHomeAsUpEnabled (false);
    getSupportActionBar ().setDisplayShowHomeEnabled (false);
    getSupportActionBar ().setDisplayShowTitleEnabled (false);

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
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu_event_page, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();

    //noinspection SimplifiableIfStatement
    if (id == R.id.action_settings) {
      Toast.makeText (getApplicationContext (),"Empty for now",Toast.LENGTH_SHORT).show ();
      return true;
    }

    return super.onOptionsItemSelected(item);
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
          return new EventPageFragment ();
        case 1:
          return new BookedEventFragment ();
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
      return getResources ().getStringArray (R.array.tabs)[position];
    }
  }
}
