package com.uber.executer;

/**
 * Created by goodson on 3/28/15.
 */

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.Window;
import android.webkit.GeolocationPermissions;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.uber.executer.models.Calendar;
import com.uber.executer.models.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class Vars {
    public enum HttpMethods {
        GET, POST, PUT, DELETE
    }

    public static final int GET = 1;
    public static final int POST = 2;
    // public static final int PUT = 3;
    public static final int DELETE = 4;
    public static final String KEY_STATUS_CODE = "status_code";
    public static final String CHARSET = "UTF-8";
    public static final String KEY_ERROR = "error";
    public static final String KEY_TRANS_ID = "transaction_id";
    public static final String KEY_REFERS = "refers";
    public static final String KEY_AIRTIME = "airtime";
    public static final String KEY_USER = "user";
    public static final String KEY_REFERRER = "referrer";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_HEADER_TOKEN = "X-Yj-Token";
    public static final String KEY_HEADER_USER = "X-Yj-User";
    public static final String KEY_ID = "id";
    public static User user;
    //  public static final String KEY_JSON = "json";
    public static final String KEY_QUERY = "query";
    public static final String KEY_ENABLED = "enabled";
    public static final String KEY_UID = "user_id";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_PLT = "platform";
    public static final String KEY_NAME = "name";
    public static final String KEY_ADDRESS = "address";
    public static final String KEY_TOKEN = "token";
    public static final String KEY_SECRET = "secret";
    public static final String KEY_USN = "username";
    public static final String KEY_EXPIRY = "expiry";
    public static final String KEY_IMG = "picture";
    public static final String KEY_IMAGES = "images";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_IMAGE_SM = "image_sm";
    public static final String KEY_LOGO = "logo";
    public static final String KEY_LOCATION = "location";
    public static final String KEY_LNG = "longitude";
    public static final String KEY_EMAIL_NOTIFY = "email_notify";
    public static final String KEY_NOTIFICATIONS = "notifications";
    public static Calendar[] calendars;
    public static final String KEY_LAT = "latitude";
    //  public static final String KEY_LOCATION_MAP = "location_map";

    public static final String KEY_CARD_ID = "card_id";
    public static final String KEY_BANK_ID = "bank_id";

    public static final String KEY_PHONE = "phone";
    public static final String KEY_ACC_NO = "acc_no";
    public static final String KEY_ACC_NAME = "acc_name";
    public static final String KEY_ACC_TYPE = "acc_type";
    public static final String KEY_CAT = "category";
    public static boolean isLoginLoading = false;
    public static String API_TOKEN = "";
    public static String locale = "en-US";
    public static String[] platforms = new String[]{"", "Twitter", "LinkedIn", "Google", "Facebook"};
    public static User currentUser = null;


    public static int socialNetworkID = 0;
    public static String BROADCAST_ACTION = "com.trivoda.jara.VIEW_ACTION";
    public static String BASE_URL = "https://andelahack.herokuapp.com/";
    //public static String BASE_URL = "http://192.168.57.1/projects/your-jara/app/";
    public static String API_PATH = "";
    private static String myPrefs = "trvYJ001";
    public static String VersionName = "";
    public static String VersionCode = "";

    private static Dialog loading = null;



    public static String getAPI_TOKEN(Context context) {
        locale = context.getResources().getConfiguration().locale.toString();
        return API_TOKEN.isEmpty() ? Vars.getDB(context, Vars.KEY_HEADER_TOKEN, "") : API_TOKEN;
    }


    public static long airtime(int modified_at, int card_duration) {

        card_duration = card_duration * 86400;
        int expiration = modified_at + card_duration;
        double result = (new Date()).getTime() / 1000;
        return expiration - Math.round(result);
    }

    public static void saveUser(User user) {


    }


//  public static JSONObject getJSONObjectString(String jsonString) {
//    try {
//      return new JSONObject(jsonString);
//    } catch (JSONException jse) {
//      jse.printStackTrace();
//      return null;
//    }
//  }

//  public static float round(float d, int decimalPlace) {
//    BigDecimal bd = new BigDecimal(Float.toString(d));
//    bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
//    return bd.floatValue();
//  }

    public static String toCurrency(float value) {
        DecimalFormat formatter = new DecimalFormat("#,###.00");
        return formatter.format(value);
    }

    public static int percentageDiscount(float a, float b) {
        return Math.round(((a - b) / a) * 100);
    }


    public static int getInt(Object a) {
        return Integer.parseInt(a.toString());
    }



    public static String dateToRelativeString(String date) throws ParseException {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-mm-dd");
            Date date1 = format.parse(date);
            return date1.toString();
        }
        catch (Exception e0) {

        }
        return  date;
    }




//    public static AutoSpanRecyclerView recyclerView = null;
//
//    public static void PopulateOfferList(final Activity activity, final View view, final com.trivoda.jara.model.Offer[] offers, final int recyclerViewId) {
//        activity.runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                if (offers == null || offers.length == 0) {
//                    Vars.showCenterMessage(activity, view, R.id.mainView, activity.getString(R.string.no_content) + " " + activity.getString(R.string.title_offers).toLowerCase(), null);
//                } else {
//                    recyclerView = (AutoSpanRecyclerView) view.findViewById(recyclerViewId);
//                    recyclerView.setHasFixedSize(true);
//                    recyclerView.setLayoutManager(new GridLayoutManager(activity, 2));
//                    recyclerView.setAdapter(new OfferAdapter(offers, activity));
//                }
//            }
//        });
//    }


    public static void ShowNativeMessage(final Activity activity, final String msg, final String title, final DialogInterface.OnClickListener positiveClick) {
        activity.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                Builder dg = new Builder(activity);
                dg.setIcon(R.mipmap.ic_launcher).setTitle(title != null ? title : activity.getString(R.string.app_name))
                        .setMessage(msg).setCancelable(true).setPositiveButton("Retry", positiveClick)
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                dg.show();
            }
        });

    }



    public static void isLoading(final Activity context, final boolean b) {
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (loading == null) {
                        loading = new Dialog(context);
                        //loading.setTitle(context.getString(R.string.title_loading));
                        loading.setCancelable(false);
                        loading.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        loading.setContentView(R.layout.dialog_loading);
                    }

                    if (loading != null) {
                        try {
                            if (b && !loading.isShowing()) {
                                loading.show();
                            } else {
                                loading.cancel();
                                loading = null;
                            }
                        } catch (Exception e0) {
                            e0.printStackTrace();
                        }

                    }
                } catch (Exception e0) {
                    e0.printStackTrace();
                }
            }
        });
    }



    public static boolean isOldUser(Activity activity, boolean isOld) {

        String sharedKey = "app_first_launch";
        if (isOld) {
            SharedPreferences sp = PreferenceManager
                    .getDefaultSharedPreferences(activity);
            sp.edit().putBoolean(sharedKey, true).apply();
            return true;
        }
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(activity);
        return sp.getBoolean(sharedKey, false);
    }

    public static void init(Context context) {
//        currentUser = getCurrentUser();
        API_TOKEN = getAPI_TOKEN(context);
    }

    public static String getDB(Context context, String id, String init) {
        return context.getSharedPreferences(myPrefs, Activity.MODE_PRIVATE).getString(id, init);
    }

    public static WebView popUpWebView(WebView webView, Activity self) {
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int progress) {

            }

            public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
                callback.invoke(origin, true, false);
            }

        });
        if (Build.VERSION.SDK_INT >= 16) {
            Class<?> clazz = webView.getSettings().getClass();
            Method method;
            try {
                method = clazz.getMethod("setAllowUniversalAccessFromFileURLs", boolean.class);
                if (method != null) {
                    try {
                        method.invoke(webView.getSettings(), true);
                    } catch (Exception e) {

                    }
                }
            } catch (Exception e) {

            }

        }

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setBlockNetworkImage(false);
        webView.getSettings().setAppCacheEnabled(true);
        webView.getSettings().setBlockNetworkLoads(false);
        webView.getSettings().setAllowFileAccess(true);
        webView.playSoundEffect(SoundEffectConstants.CLICK);
        webView.getSettings().setDatabaseEnabled(true);
        webView.getSettings().setGeolocationEnabled(true);

        webView.addJavascriptInterface(new JavaScriptInterface(self,webView),"uber");
        webView.requestFocus(View.FOCUS_DOWN);
        webView.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_UP:
                        if (!v.hasFocus()) {
                            v.requestFocus();
                        }
                        break;
                }
                return false;
            }
        });
        return webView;
    }
    public static int getDB(Context context, String id, int init) {
        return context.getSharedPreferences(myPrefs, Activity.MODE_PRIVATE).getInt(id, init);
    }

    public static void saveDB(String id, String value, Context context) {
        try {
            context.getSharedPreferences(myPrefs, Activity.MODE_PRIVATE).edit().putString(id, value).apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void saveDB(String id, int value, Context context) {
        try {
            context.getSharedPreferences(myPrefs, Activity.MODE_PRIVATE).edit().putInt(id, value).apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void clearDB(Context context) {
        try {
            Vars.currentUser = null;

        } catch (SQLiteException sqlError) {
            sqlError.printStackTrace();
        }
        context.getSharedPreferences(myPrefs, Activity.MODE_PRIVATE).edit().clear().apply();
    }

    public static abstract class GetBitmapFromURL {

        public GetBitmapFromURL(String bitmapURL, final Context context) {
            final Target target = new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    done(bitmap);
                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {

                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {
                    //done(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_plusone_small_off_client));
                }
            };
            Picasso.with(context).load(bitmapURL).centerInside().into(target);
        }

        protected abstract void done(Bitmap bitmap);
    }

    public static void Toaster(final String msg, final Activity activity, int duration) {
        final int toastDuration = duration == 0 ? Toast.LENGTH_LONG : duration;
        try {
            activity.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    Toast.makeText(activity, msg, toastDuration).show();
                }
            });
        } catch (NullPointerException npe) {
            npe.printStackTrace();
        }
    }

    public static void LaunchActivity(Activity activity, JSONObject jsonObject, String[] keys, Class<?> activityClass) {
        Intent i = new Intent();
        try {
            for (String key : keys) {
                if (jsonObject.has(key)) {
                    i.putExtra(key, jsonObject.getString(key));
                }
            }
            i.setClass(activity, activityClass);
            activity.startActivity(i);
        } catch (JSONException jse) {
            jse.printStackTrace();
        }

    }

    public static abstract class MakeHTTPRequest {

        private Activity activity;
        private View view;
        private View.OnClickListener clickListener = null;

        public MakeHTTPRequest(Activity activity, String apiRoute, int method) {
            this.activity = activity;
            makeRequest(apiRoute, null, null, method, -1);
        }

        public MakeHTTPRequest(Activity activity, View view, String apiRoute, int method) {
            this.activity = activity;
            this.view = view;
            makeRequest(apiRoute, null, null, method, -1);
        }

        public MakeHTTPRequest(Activity activity, View view, String apiRoute, int method, View.OnClickListener clickListener) {
            this.activity = activity;
            this.clickListener = clickListener;
            this.view = view;
            makeRequest(apiRoute, null, null, method, -1);
        }

        public MakeHTTPRequest(Activity activity, String apiRoute, int method, View.OnClickListener clickListener) {
            this.activity = activity;
            this.clickListener = clickListener;
            makeRequest(apiRoute, null, null, method, -1);
        }

        public MakeHTTPRequest(Activity activity, String apiRoute, int method, int viewId) {
            this.activity = activity;
            makeRequest(apiRoute, null, null, method, viewId);
        }

        public MakeHTTPRequest(Activity activity, String apiRoute, String[] keys, String[] values, int method, int viewId) {
            this.activity = activity;
            makeRequest(apiRoute, keys, values, method, viewId);
        }

        public MakeHTTPRequest(Activity activity, String apiRoute, String[] keys, String[] values, int method) {
            this.activity = activity;
            makeRequest(apiRoute, keys, values, method, -1);
        }

        private void makeRequest(final String apiRoute, final String[] keys, final String[] values, final int method, final int viewId) {
            //  Vars.isCenterLoading(activity, view, true, viewId);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        final Object[] result = HttpRequest(apiRoute, keys, values, HttpMethods.values ()[method - 1], activity);
                        if ((Boolean) result[1]) {
                            //Vars.isCenterLoading(activity, view, false, viewId);
                            done(result[0].toString());
                            done(getInt(result[2]), result[0].toString());
                        } else {
                            Log.e("Error String", result[0].toString() + "-" + result[1].toString() + "-" + result[2].toString());
                            error(getInt(result[2]), result[0].toString(), viewId);
                        }
                    } catch (IllegalArgumentException iae) {
                        iae.printStackTrace();
                    }
                }
            }).start();
        }

        protected void done(String result) {

        }

        protected void done(int statusCode, String result) {

        }

        protected void error(int code, String result, int viewId) {
            //Vars.showCenterMessage(activity, view, viewId, "(#" + code + ") " + result, clickListener);
            if (clickListener == null && activity != null) {
                Vars.Toaster("#(" + code + ") " + activity.getString(R.string.error_connection), activity, 0);
            }
        }
    }


    public static String Shorten_String(String text, int len) {
        if (text == null) {
            return "";
        }
        text = text.trim();
        if (text.length() >= len) {
            return text.substring(0, len - 3).trim() + "...";
        } else {
            return text;
        }
    }

    private static Object[] HttpRequest(String urlSuffix, String[] keys, String[] values, HttpMethods method, Activity activity) {
        String apiRoute = BASE_URL + API_PATH + urlSuffix;
        return httpRequest(apiRoute, keys, values, method, activity);

    }

    public static Object[] httpRequest(String urlString, String[] keys, String[] values,
                                       HttpMethods
                                               method, Activity activity) {
        int responseCode = 0;
        boolean complete = false;
        StringBuilder response = new StringBuilder();
        try {
            URL url = new URL(urlString);
            Log.e("API_ROUTE", urlString);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod(method.name());


            con.setRequestProperty(KEY_HEADER_TOKEN, API_TOKEN);
//            if (currentUser != null) {
//                con.setRequestProperty(KEY_HEADER_USER, String.valueOf(currentUser._id));
//            }
            con.setRequestProperty("Locale", locale);

            switch (method) {
                case POST:
                case PUT:
                    con.setRequestProperty("X-Yj-Authorization", "E2C88102-43E0-48D8-95C9-C43A455793B9");
                    con.setDoOutput(true);
                    DataOutputStream wr = new DataOutputStream(con.getOutputStream());
                    String urlParameters = "";
                    if (keys != null) {
                        for (int i = 0; i < keys.length; i++) {
                            urlParameters += (i == 0 ? "" : "&") + keys[i] + "=" + values[i];
                        }
                    }
                    wr.writeBytes(urlParameters);
                    wr.flush();
                    wr.close();

                    String token = con.getHeaderField(KEY_HEADER_TOKEN);
                    if (token != null && !token.isEmpty()) {
                        API_TOKEN = token;
                        Log.i(KEY_HEADER_TOKEN, token);
                        Vars.saveDB(KEY_HEADER_TOKEN, token, activity);
                    }
                    break;
            }
            responseCode = con.getResponseCode();
            complete = responseCode >= 200 && responseCode <= 206;
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

        } catch (MalformedURLException mur) {
            response.append("Error: ");
            response.append(mur.getMessage());
        } catch (IOException ioe) {
            if (activity != null) {
                response.append(activity.getString(R.string.error_connection));
            } else {
                response.append("DE:Fehler mit netzwerk\n\nEN:Error with network");
            }
        }
        return new Object[]{
                response.toString(),
                complete,
                responseCode
        };
    }
}