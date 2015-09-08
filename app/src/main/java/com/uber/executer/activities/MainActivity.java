package com.uber.executer.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.Plus;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.uber.executer.R;
import com.uber.executer.Singletons.Vars;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

        @Override
        protected void onCreate(Bundle savedInstanceState) {
          super.onCreate(savedInstanceState);
          setContentView(R.layout.activity_main);
          //startActivity(new Intent(this, EventPage.class));

        }

private WebView webView;
private Dialog authenticationDialog;

        public void authDialog(View view) {
          try {
            if (authenticationDialog == null) {
              authenticationDialog = new Dialog(this);
              authenticationDialog.setCancelable(true);
              authenticationDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
              authenticationDialog.setContentView(R.layout.dialog_webview);
            }

            if (authenticationDialog != null) {
              try {
                if (!authenticationDialog.isShowing()) {
                  authenticationDialog.show();
                  uberAuth();
                } else {
                  authenticationDialog.cancel();
                  authenticationDialog = null;
                }
              } catch (Exception e0) {
                e0.printStackTrace();
              }

            }
          } catch (Exception e0) {
            e0.printStackTrace();
          }
        }

        private void uberHackAuth(String string) {
          Vars.user.response.google_token = string;
          JsonObject user = new Gson().toJsonTree(Vars.user).getAsJsonObject();
          Vars.saveDB("user", user.toString(), this);
        }

        private void uberAuth() {
          final Activity self = this;
          webView = (WebView) authenticationDialog.findViewById(R.id.authWebView);
          webView.setWebViewClient(new WebViewClient() {

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
              super.onReceivedError(view, errorCode, description, failingUrl);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {

              super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
              super.onPageFinished(view, url);

              if (url.contains("/calendar") && !url.contains("calendar/callback")) {
                uberHackAuth(new Date().toString());
                Log.d("yes", url);
                webView.loadUrl("javascript:uber.getJSONCalendarString(document.body.innerText);");
                authenticationDialog.hide();
              }
            }
          });
          webView = Vars.popUpWebView(webView, this);
          webView.loadUrl("https://accounts.google.com/o/oauth2/auth?access_type=offline&scope=https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fcalendar.readonly&response_type=code&client_id=453264479059-fc56k30fdhl07leahq5n489ct7ifk7md.apps.googleusercontent.com&redirect_uri=https%3A%2F%2Fandelahack.herokuapp.com%2Fcalendar%2Fcallback");

        }

        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
          // Inflate the menu; this adds items to the action bar if it is present.
          getMenuInflater().inflate(R.menu.menu_main, menu);
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
            return true;
          }

          return super.onOptionsItemSelected(item);
        }

}
