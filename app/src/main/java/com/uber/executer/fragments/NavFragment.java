package com.uber.executer.fragments;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.uber.executer.R;
import com.uber.executer.activities.BookedEvents;

/**
 * Created by aliyuolalekan on 8/12/15.
 */
public class NavFragment extends Fragment {
  private View containerView;
  private ListView listView;
  private myAdapter mYAdapter;
  private static final String PREF_FILE_NAME = "myPreference" ;
  public static final String KEY_USER_LEARNED_DRAWER = "user_learned_drawer";
  public boolean fragmentIsSeen;
  private ImageView uberhot;
  private boolean fromSavedState;
  public ActionBarDrawerToggle drawertoggle;
  private DrawerLayout drawerLayout;
  public NavFragment(){

  }

  @Override
  public void onCreate (@Nullable Bundle savedInstanceState) {
    super.onCreate (savedInstanceState);
    fragmentIsSeen = Boolean.valueOf (readFromPreference(getActivity (), KEY_USER_LEARNED_DRAWER,"false"));
    if(savedInstanceState != null){
      fromSavedState = true;
    }
    setHasOptionsMenu (true);
  }
  @Override
  public boolean onOptionsItemSelected (MenuItem item) {
    if (drawertoggle.onOptionsItemSelected(item)) {
      return true;
    }
    return super.onOptionsItemSelected (item);
  }

  @Nullable
  @Override
  public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    super.onCreateView (inflater, container, savedInstanceState);
    View view = inflater.inflate (R.layout.navigation_layout, container, false);

    listView = (ListView)view.findViewById (R.id.nav_list);
    mYAdapter = new myAdapter (getActivity ());
    listView.setAdapter (mYAdapter);
    listView.setOnItemClickListener (new AdapterView.OnItemClickListener () {
      @Override
      public void onItemClick (AdapterView<?> parent, View view, int position, long id) {
       String itemElement = (String)listView.getItemAtPosition (position);
        switch (itemElement){
          case "Booked Events":
            Intent intent = new Intent (getActivity (), BookedEvents.class);
            startActivity (intent);
        }
      }
    });
    return view;
  }
  public void closeDrawer(){
    containerView = getActivity ().findViewById (R.id.navigation_drawer);
    if(drawerLayout.isDrawerOpen (containerView))
    drawerLayout.closeDrawer (containerView);
  }
  public void openDrawer(){
    containerView = getActivity ().findViewById (R.id.navigation_drawer);
    if(!drawerLayout.isDrawerOpen (containerView)){
      drawerLayout.openDrawer (containerView);
    }
  }

  public void setUp (int fragmentId, DrawerLayout layout) {
    containerView = getActivity ().findViewById (R.id.navigation_drawer);
    drawerLayout = layout;
    drawertoggle = new ActionBarDrawerToggle (getActivity (),drawerLayout,R.string.drawer_opened,R.string.drawer_closed){
      @Override
      public void onDrawerOpened (View drawerView) {
        super.onDrawerOpened (drawerView);
        if(!fragmentIsSeen){
          fragmentIsSeen = true;
          saveToPreference (getActivity (),KEY_USER_LEARNED_DRAWER,fragmentIsSeen + "");
        }
        getActivity ().invalidateOptionsMenu ();
      }

      @Override
      public void onDrawerClosed (View drawerView) {
        super.onDrawerClosed (drawerView);
        getActivity ().invalidateOptionsMenu ();
      }

      @TargetApi(Build.VERSION_CODES.LOLLIPOP)
      @Override
      public void onDrawerSlide (View drawerView, float slideOffset) {
        super.onDrawerSlide (drawerView, slideOffset);

      }
    };
    if(!fragmentIsSeen && !fromSavedState){
      drawerLayout.openDrawer (containerView);

    }

    drawerLayout.setDrawerListener (drawertoggle);
    drawerLayout.post (new Runnable () {
      @Override
      public void run () {
        drawertoggle.syncState ();
      }
    });
  }



  public static void saveToPreference(Context context, String preferenceName, String preferenceValue){
    SharedPreferences sharedPreference = context.getSharedPreferences (PREF_FILE_NAME, Context.MODE_PRIVATE);
    SharedPreferences.Editor editor = sharedPreference.edit ();
    editor.putString (preferenceName, preferenceValue);
    editor.apply ();
  }


  private static String readFromPreference (Context context, String prefFileName, String defaultValue) {
    SharedPreferences sharedpreference = context.getSharedPreferences (PREF_FILE_NAME, Context.MODE_PRIVATE);
    return sharedpreference.getString (prefFileName, defaultValue);
  }
  public class myAdapter extends BaseAdapter{
    Context context;
    String[] content;
    int[] images = {R.drawable.ic_event_black_48dp,R.drawable.ic_help_outline_black_48dp};
    public myAdapter(Context context){
      this.context = context;
      content = new String[] {"Booked Events","Help"};
    }
    @Override
    public int getCount () {
      return content.length;
    }

    @Override
    public Object getItem (int position) {
      return content[position];
    }

    @Override
    public long getItemId (int position) {
      return position;
    }

    @Override
    public View getView (int position, View convertView, ViewGroup parent) {
      View row = null;
      if(convertView == null){
        LayoutInflater inflater = (LayoutInflater)context.getSystemService (Context.LAYOUT_INFLATER_SERVICE);
        row = inflater.inflate (R.layout.adapter_for_navigation_list, parent, false);
      }
      else {
        row = convertView;
      }
      TextView text = (TextView)row.findViewById (R.id.text_for_navigation_drawer);
      ImageView image = (ImageView)row.findViewById (R.id.image_icon_navigation);
      text.setText (content[position]);
      image.setImageResource (images[position]);
      return row;
    }
  }
}
