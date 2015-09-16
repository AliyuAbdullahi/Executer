package com.uber.executer.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.Plus;
import com.google.api.services.calendar.CalendarScopes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.picasso.Picasso;
import com.uber.executer.R;
import com.uber.executer.Singletons.MyApp;
import com.uber.executer.Singletons.Vars;
import com.uber.executer.models.Calendar;
import com.uber.executer.models.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Handler;

import static android.app.ProgressDialog.*;


public class MainActivity extends Activity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {
  Calendar calendar;
  com.pkmmte.view.CircularImageView avatar;
  public static final int RC_GOOGLE_LOGIN = 1;
  TextView welcome;
  private GoogleApiClient mGoogleApiClient;
  public  static Calendar[] calendars;
  private boolean mGoogleIntentInProgress;
  public static final String PREFS_NAME = "MyPrefsFile";
  public ImageView myAnimation;
  private static final String SHARED_PREFERENCE_NAME = "mySharedPreference" ;
  private boolean fromSavedInstanceState;
  private boolean activityIsIdentified;
  public static final String ACTIVITY_CAUGHT_RED_HANDED = "i_know_about_you_already";

  private boolean mGoogleLoginClicked;
  private ConnectionResult mGoogleConnectionResult;

  private ImageButton mGoogleLoginButton;
  private String TAG = "google login";
  private ProgressDialog mAuthProgressDialog;

  @Override
  protected void onStart () {
    super.onStart ();
    SharedPreferences settings = getSharedPreferences(MainActivity.PREFS_NAME, 0);
//Get "hasLoggedIn" value. If the value doesn't exist yet false is returned
    boolean hasLoggedIn = settings.getBoolean("hasLoggedIn", false);

    if(hasLoggedIn)
    {
      Intent intent = new Intent (MainActivity.this, EventPage.class);
      startActivity (intent);
      MainActivity.this.finish();
      //Go directly to main activity.
    }
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate (savedInstanceState);

    activityIsIdentified = Boolean.valueOf (readFromPreference (getApplicationContext ()
            ,ACTIVITY_CAUGHT_RED_HANDED,"false"));

    if (getIntent().getBooleanExtra("EXIT", false)) {
      this.moveTaskToBack(true);
    }
    if(savedInstanceState != null){
      fromSavedInstanceState = true;
    }

    setContentView (R.layout.activity_main);
    myAnimation = (ImageView)findViewById (R.id.progressAnimation);
    myAnimation.setVisibility (View.INVISIBLE);
    welcome = (TextView)findViewById (R.id.welcomeTextView);
    try{
      welcome.setText ("Welcome " + Vars.user.response.first_name + " "+Vars.user.response.last_name);
    }
    catch (Exception e)
    {
      e.printStackTrace ();
    }

    //Use picasso library to load user image into imageview
    avatar = (com.pkmmte.view.CircularImageView)findViewById (R.id.avatar);
try {
  Picasso.with (this).load (Vars.user.response.picture)
          .error (R.drawable.logoone).placeholder (R.drawable.logoone)
          .into (avatar);
}catch (Exception e){
  e.printStackTrace ();
}
    init ();
  }

  private void init() {
    //Initialise the google api and setup connection

    mAuthProgressDialog = new ProgressDialog(this);
    mAuthProgressDialog.setMessage ("Loading...");
    mAuthProgressDialog.setCancelable (false);

    mGoogleLoginButton = (ImageButton) findViewById(R.id.google_plus_button);
    mGoogleLoginButton.setOnClickListener (this);

    mGoogleApiClient = new GoogleApiClient.Builder(this)
            .addConnectionCallbacks (this)
            .addOnConnectionFailedListener (this)
            .addApi (Plus.API)
            .addScope (Plus.SCOPE_PLUS_LOGIN)
            .build ();
  }

  private void resolveSignInError() {
    if (mGoogleConnectionResult.hasResolution()) {
      try {
        mGoogleIntentInProgress = true;
        mGoogleConnectionResult.startResolutionForResult(this, RC_GOOGLE_LOGIN);
      } catch (IntentSender.SendIntentException e) {
        mGoogleIntentInProgress = false;
        mGoogleApiClient.connect();
      }
    }
  }

  private void loginAndGetToken() {
    try{
      mAuthProgressDialog.show ();
    }
    catch (Exception e){
      e.printStackTrace ();
    }

    AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>() {
      String errorMessage = null;

      @Override
      protected String doInBackground(Void... params) {
        String token = null;

        try {
          String scope = String.format("oauth2:%s", CalendarScopes.CALENDAR_READONLY);
          token = GoogleAuthUtil.getToken (MainActivity.this, Plus.AccountApi.getAccountName (mGoogleApiClient), scope);
        } catch (IOException transientEx) {
          Log.e(TAG, "Error authenticating with Google: " + transientEx);
          errorMessage = "Network error: " + transientEx.getMessage();
        } catch (UserRecoverableAuthException e) {
          Log.w(TAG, "Recoverable Google OAuth error: " + e.toString());
                    /* We probably need to ask for permissions, so start the intent if there is none pending */
          if (!mGoogleIntentInProgress) {
            mGoogleIntentInProgress = true;
            Intent recover = e.getIntent();
            startActivityForResult(recover, RC_GOOGLE_LOGIN);
          }
        } catch (GoogleAuthException authEx) {
          Log.e(TAG, "Error authenticating with Google: " + authEx.getMessage(), authEx);
          errorMessage = "Error authenticating with Google: " + authEx.getMessage();
        }
        return token;
      }

      @Override
      protected void onPostExecute(final String token) {
        mGoogleLoginClicked = false;
        mAuthProgressDialog.hide();
        if (token != null) {
          Log.v ("token", token);
          try{
          RequestQueue queue = Volley.newRequestQueue (MainActivity.this);
          final StringRequest request = new StringRequest (Request.Method.POST,
                  "http://andelahack.herokuapp.com/" + Vars.user.response.uuid + "/calendar/ ",
                  new Response.Listener<String> () {
                    @Override
                    public void onResponse (String response) {

                      try {
                        JSONObject result = new JSONObject (response);
                        JSONArray resultValues = result.getJSONArray ("response");
                        GsonBuilder gsonBuilder = new GsonBuilder ();
                        Gson gson = gsonBuilder.create ();
                        calendars = gson.fromJson (String.valueOf (resultValues), Calendar[].class);
                        Intent intent = new Intent (MainActivity.this, EventPage.class);
                        startActivity (intent);
                        overridePendingTransition (R.anim.slide_in_right, R.anim.slide_out_left);
                        finish ();
                      } catch (JSONException e) {
                        e.printStackTrace ();
                      }

                    }
                  }, new Response.ErrorListener () {
            @Override
            public void onErrorResponse (VolleyError error) {
              Toast.makeText (getApplicationContext (), error + "", Toast.LENGTH_LONG).show ();

            }
          }) {
            @Override
            protected Map<String, String> getParams () throws AuthFailureError {
              super.getParams ();
              Map<String, String> params = new HashMap<String, String> ();
              params.put ("refreshToken", "");
              params.put ("accessToken", token);
              return params;
            }

            @Override
            public Map<String, String> getHeaders () throws AuthFailureError {
              super.getHeaders ();
              Map<String, String> params = new HashMap<String, String> ();
              params.put ("Content-Type", "application/x-www-form-urlencoded");
              return params;
            }
          };

          int socketTimeout = 30000;//30 seconds - change to what you want
          RetryPolicy policy = new DefaultRetryPolicy (socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
          request.setRetryPolicy (policy);

          queue.add (request);
        }catch(Exception e){
          e.printStackTrace ();
        }
        } else if (errorMessage != null) {
          Toast.makeText (getApplicationContext (),errorMessage,Toast.LENGTH_SHORT).show ();
        }
      }
    };
    task.execute ();
  }

  @Override
  public void onConnected(final Bundle bundle) {
    MyApp.mGoogleApiClient = this.mGoogleApiClient;
     try {
       loginAndGetToken ();
     }catch (Exception e){
       e.printStackTrace ();
     }
  }

  @Override
  public void onConnectionFailed(ConnectionResult result) {
            /* Store the ConnectionResult so that we can use it later when the user clicks on the Google+ login button */
    mGoogleConnectionResult = result;

    if (mGoogleLoginClicked) {
      resolveSignInError();
    } else {
      Log.e(TAG, result.toString());
    }
  }

  @Override
  public void onConnectionSuspended(int i) {
    // ignore
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (resultCode != RESULT_OK) {
      mGoogleLoginClicked = false;
    }
    mGoogleIntentInProgress = false;
    if(data != null)
      System.out.println("data" + data);

    if (!mGoogleApiClient.isConnecting()) {
      mGoogleApiClient.connect();
    }
  }
  public void signOut(){
    Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
    // Our sample has caches no user data from Google+, however we
    // would normally register a callback on revokeAccessAndDisconnect
    // to delete user data so that we comply with Google developer
    // policies.
    Plus.AccountApi.revokeAccessAndDisconnect(mGoogleApiClient);
    mGoogleApiClient.connect();
  }

  @Override
  public void onBackPressed () {
    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    intent.putExtra ("EXIT", true);
    startActivity (intent);
  }

  @Override
  public void onClick(View v) {
      //
    switch (v.getId()) {
      case R.id.google_plus_button:
        mGoogleLoginClicked = true;
        myAnimation.setVisibility (View.VISIBLE);
        ((AnimationDrawable) myAnimation.getBackground()).start ();

        if (!mGoogleApiClient.isConnecting()) {
          if (mGoogleConnectionResult != null) {
            resolveSignInError();
          } else if (mGoogleApiClient.isConnected()) {
            final ProgressDialog dialog = ProgressDialog.show(this, "", "Syncing...",
                    true);
            dialog.show ();
            android.os.Handler handler = new android.os.Handler ();
            handler.postDelayed (new Runnable () {
              public void run () {
                dialog.dismiss ();
                Toast.makeText (getApplicationContext (), "Google is Connected!", Toast.LENGTH_SHORT).show ();
                loginAndGetToken ();
              }
            }, 2000);

          } else {

            Log.d(TAG, "Trying to connect to Google API");
            mGoogleApiClient.connect();
          }
        }
        break;
    }

  }
  public void saveToPreference(Context context, String preferenceName, String preferenceValue){
    SharedPreferences sharedPreferences = context.getSharedPreferences (SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
    SharedPreferences.Editor editor = sharedPreferences.edit ();
    editor.putString (preferenceName, preferenceValue);
    editor.apply ();
  }
  public String readFromPreference (Context context, String preferenceName, String preferenceValue){
    SharedPreferences sharedPreferences = context.getSharedPreferences (SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
    return sharedPreferences.getString (preferenceName, preferenceValue);
  }
}