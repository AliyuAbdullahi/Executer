package com.uber.executer.activities;

import android.graphics.Typeface;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.uber.executer.fragments.NavFragment;
import com.uber.executer.R;

public class EventPage extends AppCompatActivity {
  private View containerView;
  int count;
  DrawerLayout drawerLayout;
  Toolbar toolbar;
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate (savedInstanceState);
    count = 0;
    setContentView (R.layout.activity_event_page);
    toolbar = (Toolbar)findViewById (R.id.toolbar);
    setSupportActionBar (toolbar);
    TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
    mTitle.setText ("EXECUTER");
    Typeface tf = Typeface.createFromAsset (getAssets (),"MuseoSans-300.otf");
    mTitle.setTypeface (tf);
    drawerLayout = (DrawerLayout)findViewById (R.id.drawerlayout);
    getSupportActionBar ().setDisplayHomeAsUpEnabled (true);
    getSupportActionBar ().setDisplayShowHomeEnabled (true);
    getSupportActionBar ().setDisplayShowTitleEnabled (false);
    NavFragment navigation = (NavFragment)getSupportFragmentManager ().findFragmentById (R.id.navigation_drawer);
    navigation.setUp(R.id.navigation_drawer,drawerLayout);
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
  public void closeDrawer(){
    containerView = findViewById (R.id.navigation_drawer);
    if(drawerLayout.isDrawerOpen (containerView))
      drawerLayout.closeDrawer (containerView);
  }

  @Override
  public void onBackPressed () {
    containerView = findViewById (R.id.navigation_drawer);
    if(drawerLayout.isDrawerOpen (containerView)){
      closeDrawer ();
    }
    else{
      count = count +1;
      if(count == 1){
        Toast.makeText (getApplicationContext (),"Press back button again to exit",Toast.LENGTH_SHORT).show ();
      }
      if(count > 1){
        finishFromChild (getParent ());
      }
    }

  }
}
