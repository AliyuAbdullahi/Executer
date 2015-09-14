package com.uber.executer.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
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
  private boolean mGoogleLoginClicked;
  private ConnectionResult mGoogleConnectionResult;

  private ImageButton mGoogleLoginButton;
  private String TAG = "google login";
  private ProgressDialog mAuthProgressDialog;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate (savedInstanceState);
    setContentView (R.layout.activity_main);
    welcome = (TextView)findViewById (R.id.welcomeTextView);
    welcome.setText ("Welcome " + Vars.user.response.first_name + " "+Vars.user.response.last_name);
    avatar = (com.pkmmte.view.CircularImageView)findViewById (R.id.avatar);
    Picasso.with (this).load (Vars.user.response.picture)
            .error (R.drawable.logoone).placeholder (R.drawable.logoone)
            .into (avatar);
    init ();
  }

  private void init() {

    mAuthProgressDialog = new ProgressDialog(this);
    mAuthProgressDialog.setMessage("Loading...");
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
    mAuthProgressDialog.show ();
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
          Log.v("token", token);
          RequestQueue queue = Volley.newRequestQueue (MainActivity.this);
          final StringRequest request = new StringRequest (Request.Method.POST, "http://andelahack.herokuapp.com/"+Vars.user.response.uuid+"/calendar/ ", new Response.Listener<String> () {
            @Override
            public void onResponse (String response) {

              try {
                JSONObject result = new JSONObject (response);
                JSONArray resultValues = result.getJSONArray ("response");
                GsonBuilder gsonBuilder = new GsonBuilder();
                Gson gson = gsonBuilder.create();
                calendars = gson.fromJson (String.valueOf (resultValues), Calendar[].class);
                Intent intent = new Intent (MainActivity.this, EventPage.class);
                startActivity (intent);
                overridePendingTransition( R.anim.slide_in_right,R.anim.slide_out_left);
              } catch (JSONException e) {
                e.printStackTrace ();
              }

            }
          }, new Response.ErrorListener () {
            @Override
            public void onErrorResponse (VolleyError error) {
              Toast.makeText (getApplicationContext (), error+"",Toast.LENGTH_LONG).show ();

            }
          }){
            @Override
            protected Map<String, String> getParams () throws AuthFailureError {
              super.getParams ();
              Map<String,String> params = new HashMap<String, String> ();
              params.put("refreshToken","");
              params.put("accessToken",token);
              return params;

            }

            @Override
            public Map<String, String> getHeaders () throws AuthFailureError {
              super.getHeaders ();
              Map<String,String> params = new HashMap<String, String>();
              params.put("Content-Type","application/x-www-form-urlencoded");
              return params;
            }
          };

          int socketTimeout = 30000;//30 seconds - change to what you want
          RetryPolicy policy = new DefaultRetryPolicy (socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
          request.setRetryPolicy(policy);

          queue.add (request);
        } else if (errorMessage != null) {

        }
      }
    };
    task.execute();
  }

  public GoogleApiClient buildApiClient(){
    return new GoogleApiClient.Builder(this)
            .addConnectionCallbacks (this)
            .addOnConnectionFailedListener (this)
            .addApi (Plus.API)
            .addScope (Plus.SCOPE_PLUS_LOGIN)
            .build ();
  }
  @Override
  public void onConnected(final Bundle bundle) {
    MyApp.mGoogleApiClient = this.mGoogleApiClient;
    loginAndGetToken ();
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
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.google_plus_button:
        mGoogleLoginClicked = true;
        if (!mGoogleApiClient.isConnecting()) {
          if (mGoogleConnectionResult != null) {
            resolveSignInError();
          } else if (mGoogleApiClient.isConnected()) {
            loginAndGetToken();
          } else {
            Log.d(TAG, "Trying to connect to Google API");
            mGoogleApiClient.connect();
          }
        }
        break;
    }
  }
}