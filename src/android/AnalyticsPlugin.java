package com.segment.analytics.cordova;

//import java.util.Map;
//import java.util.List;
//import java.util.HashMap;
//import java.util.Iterator;
//import java.util.ArrayList;
//import java.util.concurrent.Executors;
//import java.util.concurrent.ScheduledExecutorService;
//import java.util.concurrent.TimeUnit;
//
//import org.apache.cordova.CallbackContext;
//import org.apache.cordova.CordovaPlugin;
//import org.apache.cordova.PluginResult;
//import org.json.JSONObject;
//import org.json.JSONArray;
//import org.json.JSONException;
//
//import com.segment.analytics;
//
//import android.content.Context;
//import android.util.Log;

public class AnalyticsPlugin extends CordovaPlugin {

//    private static final String TAG = "AnalyticsPlugin";
//    private static CallbackContext context;
//    private static boolean measureSessionDuration;
//    private final Analytics analytics = null;
//    private final String writeKey;
//
//    public AnalyticsPlugin () {
//        writeKey = this.preferences.getString("analytics_write_key", null);
//
//        if (writeKey == null || "".equals(writeKey)) {
//            Log.e(TAG, "Invalid analytics_write_key: " + writeKey);
//        } else {
//            analytics = new Analytics.Builder(
//                    cordova.getActivity().getApplicationContext(),
//                    writeKey
//            ).build();
//            Analytics.setSingletonInstance(analytics);
//        }
//    }
//
//    @Override
//    public boolean execute(final String action, final JSONArray args, final CallbackContext callbackContext) throws JSONException {
//        if (analytics == null) {
//            Log.e(TAG, "Error initializing");
//            return false;
//        }
//
//        if("setCurrencyCode".equals(action))
//        {
//            methodReference = Analytics::setCurrencyCode;
//            return true;
//        }
//        else if("setCustomerUserId".equals(action))
//        {
//            setCustomerUserId(args, callbackContext);
//            return true;
//        }
//        else if("setUserEmails".equals(action))
//        {
//            setUserEmails(args, callbackContext);
//            return true;
//        }
//        else if("setDeviceTrackingDisabled".equals(action))
//        {
//            setDeviceTrackingDisabled(args);
//            return true;
//        }
//        else if("setMeasureSessionDuration".equals(action))
//        {
//            setMeasureSessionDuration(args);
//            return true;
//        }
//        else if("getAppsFlyerUID".equals(action))
//        {
//            getAppsFlyerUID(args, callbackContext);
//            return true;
//        }
//        else if("trackEvent".equals(action))
//        {
//            trackEvent(args);
//            return true;
//        }
//        else if("initSdk".equals(action))
//        {
//            cordova.getThreadPool().execute(new Runnable() {
//                @Override
//                public void run() {
//                    initSdk(args, callbackContext);
//                }
//            });
//            return true;
//        }
//        return false;
//    }
//
//    @Override
//    public void onResume(boolean multitasking) {
//        super.onResume(multitasking);
//
//        if (measureSessionDuration) {
//            AppsFlyerLib.onActivityResume(cordova.getActivity());
//        }
//    }
//
//    @Override
//    public void onPause(boolean multitasking) {
//        super.onPause(multitasking);
//
//        if (measureSessionDuration) {
//            AppsFlyerLib.onActivityPause(cordova.getActivity());
//        }
//    }
//
//    private void initSdk(JSONArray parameters, final CallbackContext callbackContext) {
//        context = callbackContext;
//        String devKey = null;
//        try
//        {
//            if (parameters.length() > 0) {
//                devKey = parameters.getString(0);
//            }
//
//            if (parameters.length() > 1) {
//                measureSessionDuration = parameters.getBoolean(1);
//            }
//
//            if(devKey != null){
//                AppsFlyerLib.setAppsFlyerKey(devKey);
//                initListener(callbackContext);
//            }
//        }
//        catch (JSONException e)
//        {
//            e.printStackTrace();
//            Log.e(TAG, "error on initSdk", e);
//            callbackContext.error(e.getMessage());
//            return;
//        }
//
//        AppsFlyerLib.registerConversionListener(cordova.getActivity().getApplicationContext(), new AppsFlyerConversionListener() {
//
//            @Override
//            public void onAppOpenAttribution(Map<String, String> arg0) {
//                // TODO Auto-generated method stub
//
//            }
//
//            @Override
//            public void onAttributionFailure(String errorMessage) {
//                //Added this to avoid compilation failure
//            }
//
//            @Override
//            public void onInstallConversionDataLoaded(Map<String, String> conversionData) {
//                final JSONObject message = new JSONObject(conversionData);
//                if (context != null) {
//
//                    cordova.getActivity().runOnUiThread(
//                            new Runnable() {
//                                @Override
//                                public void run() {
//                                    PluginResult result = new PluginResult(PluginResult.Status.OK, message);
//                                    result.setKeepCallback(true);
//                                    context.sendPluginResult(result);
//                                }
//                            }
//                    );
//
//                } else {
//                    Log.w(TAG, "onInstallConversionDataLoaded: context is null");
//                }
//            }
//
//            @Override
//            public void onInstallConversionFailure(String arg0) {
//                // TODO Auto-generated method stub
//                Log.w(TAG, "onInstallConversionFailure: " + arg0);
//                context.error("onInstallConversionFailure: " + arg0);
//            }
//
//        });
//
//    }
//
//    private void initListener(final CallbackContext callbackContext) {
//        Runnable task = new Runnable() {
//            public void run() {
//                AppsFlyerLib.sendTracking(cordova.getActivity().getApplicationContext());
//            }
//        };
//        ScheduledExecutorService worker = Executors.newSingleThreadScheduledExecutor();
//        worker.schedule(task, 500, TimeUnit.MILLISECONDS);
//
//        PluginResult result = new PluginResult(PluginResult.Status.NO_RESULT);
//        result.setKeepCallback(true);
//        callbackContext.sendPluginResult(result);
//    }
//
//    private void trackEvent(JSONArray parameters) {
//        String eventName = null;
//        JSONObject eventValueJSON = null;
//        Map<String, Object> eventValue = null;
//
//        try
//        {
//            eventName = parameters.getString(0);
//
//            if (parameters.length() > 1) {
//                eventValueJSON = parameters.getJSONObject(1);
//            }
//
//            if (eventValueJSON != null) {
//                eventValue = toMap(eventValueJSON);
//            }
//        }
//        catch (JSONException e)
//        {
//            e.printStackTrace();
//            return;
//        }
//        if(eventName == null || eventName.length()==0)
//        {
//            return;
//        }
//        Context c = this.cordova.getActivity().getApplicationContext();
//        AppsFlyerLib.trackEvent(c, eventName, eventValue);
//    }
//
//    public static Map<String, Object> toMap(JSONObject object) throws JSONException {
//        Map<String, Object> map = new HashMap<String, Object>();
//
//        Iterator<String> keysItr = object.keys();
//        while(keysItr.hasNext()) {
//            String key = keysItr.next();
//            Object value = object.get(key);
//            map.put(key, value);
//        }
//
//        return map;
//    }
//
//
//    private void setCurrencyCode(JSONArray parameters)
//    {
//        String currencyId=null;
//        try
//        {
//            currencyId = parameters.getString(0);
//        }
//        catch (JSONException e)
//        {
//            e.printStackTrace();
//            return;
//        }
//        if(currencyId == null || currencyId.length()==0)
//        {
//            return;
//        }
//        AppsFlyerLib.setCurrencyCode(currencyId);
//
//    }
//
//    private void setMeasureSessionDuration(JSONArray parameters)
//    {
//        try
//        {
//            measureSessionDuration = parameters.getBoolean(0);
//        }
//        catch (JSONException e)
//        {
//            e.printStackTrace();
//            return;
//        }
//    }
//
//    private void setCustomerUserId(JSONArray parameters, CallbackContext callbackContext)
//    {
//        try
//        {
//            String customeUserId = null;
//
//            if (parameters.length() > 0) {
//                customeUserId = parameters.getString(0);
//            }
//
//            AppsFlyerLib.setCustomerUserId(customeUserId);
//            PluginResult r = new PluginResult(PluginResult.Status.OK);
//            r.setKeepCallback(false);
//            callbackContext.sendPluginResult(r);
//        }
//        catch (JSONException e)
//        {
//            e.printStackTrace();
//            return;
//        }
//    }
//
//    private void setDeviceTrackingDisabled(JSONArray parameters)
//    {
//        try
//        {
//            AppsFlyerLib.setDeviceTrackingDisabled(parameters.getBoolean(0));
//        }
//        catch (JSONException e)
//        {
//            e.printStackTrace();
//            return;
//        }
//    }
//
//    private void setUserEmails(JSONArray parameters, CallbackContext callbackContext)
//    {
//        try
//        {
//            JSONArray emailsJSON = parameters.getJSONArray(0);
//            EmailsCryptType cryptMethod = null;
//            String cryptMethodValue = "";
//
//            if (parameters.length() > 1) {
//                cryptMethodValue = parameters.getString(1);
//            }
//
//            List<String> emails = new ArrayList<String>();
//            for (int i=0; i < emailsJSON.length(); i++) {
//                emails.add(emailsJSON.getString(i));
//            }
//
//            String[] emailVarargs = emails.toArray(new String[emails.size()]);
//
//            if ("MD5".equals(cryptMethodValue)) {
//                cryptMethod = EmailsCryptType.MD5;
//            } else if ("SHA1".equals(cryptMethodValue)) {
//                cryptMethod = EmailsCryptType.SHA1;
//            } else {
//                cryptMethod = EmailsCryptType.NONE;
//            }
//
//            AppsFlyerLib.setUserEmails(cryptMethod, emailVarargs);
//
//            PluginResult r = new PluginResult(PluginResult.Status.OK);
//            r.setKeepCallback(false);
//            callbackContext.sendPluginResult(r);
//        }
//        catch (JSONException e)
//        {
//            e.printStackTrace();
//            return;
//        }
//    }
//
//    private void getAppsFlyerUID(JSONArray parameters, CallbackContext callbackContext)
//    {
//        String id = AppsFlyerLib.getAppsFlyerUID(cordova.getActivity().getApplicationContext());
//        PluginResult r = new PluginResult(PluginResult.Status.OK, id);
//        r.setKeepCallback(false);
//        callbackContext.sendPluginResult(r);
//    }
//
//    @Override
//    public void onDestroy() {
//        context = null;
//
//        super.onDestroy();
//    }
}