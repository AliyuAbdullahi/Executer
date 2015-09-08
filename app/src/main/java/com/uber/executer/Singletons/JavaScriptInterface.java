package com.uber.executer.Singletons;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.uber.executer.Singletons.Vars;
import com.uber.executer.activities.EventPage;
import com.uber.executer.activities.MainActivity;
import com.uber.executer.context.AppContext;
import com.uber.executer.models.Calendar;
import com.uber.executer.models.User;


/**
 * Created by goodson on 7/8/15.
 */
public class JavaScriptInterface {
    AppContext context;
    public JavaScriptInterface (Activity c, WebView v) {
        mc = c;
    }

    private Activity mc;

    @JavascriptInterface
    public void getJSONString(final String json) {

        Vars.user = new Gson().fromJson(json, User.class);
        if (Vars.user.response.uuid != null) {
            Vars.saveDB("user", json, mc);
            mc.finish();
            mc.startActivity(new Intent(mc, MainActivity.class));
        }
    }

    @JavascriptInterface
    public void getJSONCalendarString(final String json) {

       // Vars.calendars = new Gson().fromJson(json, Calendar[].class);
        new Vars.MakeHTTPRequest(mc, "user/" + Vars.user.response.uuid + "/calendar", new String[]{"calendar"}, new String[]
                {json}, Vars
                .POST) {
            @Override
            protected void done(String result) {
                super.done(result);
                if (Vars.calendars.length > -1) {
                    Vars.saveDB("calendar", json, mc);
                    Log.v ("result",json.toString ());
                    mc.finish();
                    mc.startActivity(new Intent(mc, EventPage.class));
                }
                else{
                    Toast.makeText (AppContext.getAppContext (),"You do not have any Calendar Event",Toast.LENGTH_LONG).show ();
                }

            }

            @Override
            protected void error(int code, String result, int viewId) {
                super.error (code, result, viewId);
                Vars.Toaster("Error sending calendar", mc, 0);
            }
        };

    }

}
