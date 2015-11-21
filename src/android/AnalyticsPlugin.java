package com.segment.analytics.cordova;

import android.util.Log;

import com.segment.analytics.Analytics;
import com.segment.analytics.Analytics.LogLevel;
import com.segment.analytics.Properties;
import com.segment.analytics.Properties.Product;
import com.segment.analytics.StatsSnapshot;
import com.segment.analytics.Traits;
import com.segment.analytics.Traits.Address;

import org.apache.cordova.BuildConfig;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class AnalyticsPlugin extends CordovaPlugin {

    private static final String TAG = "AnalyticsPlugin";
    private final Analytics analytics;
    private final String writeKey;

    public AnalyticsPlugin () {
        String writeKeyPreferenceName;
        LogLevel logLevel;

        if(BuildConfig.DEBUG) {
            writeKeyPreferenceName = "analytics_debug_write_key";
            logLevel = LogLevel.VERBOSE;
        } else {
            writeKeyPreferenceName = "analytics_write_key";
            logLevel = LogLevel.NONE;
        }

        writeKey = this.preferences.getString(writeKeyPreferenceName, null);

        if (writeKey == null || "".equals(writeKey)) {
            analytics = null;
            Log.e(TAG, "Invalid write key: " + writeKey);
        } else {
            analytics = new Analytics.Builder(
                    cordova.getActivity().getApplicationContext(),
                    writeKey
            ).logLevel(logLevel).build();

            Analytics.setSingletonInstance(analytics);
        }
    }

    @Override
    public boolean execute(final String action, final JSONArray args, final CallbackContext callbackContext) throws JSONException {
        if (analytics == null) {
            Log.e(TAG, "Error initializing");
            return false;
        }

        if ("identify".equals(action)) {
            identify(args);
            return true;
        } else if ("group".equals(action)) {
            group(args);
            return true;
        } else if ("track".equals(action)) {
            track(args);
            return true;
        } else if ("screen".equals(action)) {
            screen(args);
            return true;
        } else if ("alias".equals(action)) {
            alias(args);
            return true;
        } else if ("reset".equals(action)) {
            reset();
            return true;
        } else if ("flush".equals(action)) {
            flush();
            return true;
        } else if ("getSnapshot".equals(action)) {
            getSnapshot(callbackContext);
            return true;
        }

        return false;
    }

    private void identify(JSONArray args) {
        try{
            analytics.with(cordova.getActivity().getApplicationContext()).identify(
                args.getString(0),
                makeTraitsFromJSON(args.getJSONObject(1)),
                null // passing options is deprecated
            );
        } catch(JSONException e) {
            e.printStackTrace();
        }
    }

    private void group(JSONArray args) {
        try{
            analytics.with(cordova.getActivity().getApplicationContext()).group(
                    args.getString(0),
                    makeTraitsFromJSON(args.getJSONObject(1)),
                    null // passing options is deprecated
            );
        } catch(JSONException e) {
            e.printStackTrace();
        }
    }

    private void track(JSONArray args) {
        try{
            analytics.with(cordova.getActivity().getApplicationContext()).track(
                    args.getString(0),
                    makePropertiesFromJSON(args.getJSONObject(1)),
                    null // passing options is deprecated
            );
        } catch(JSONException e) {
            e.printStackTrace();
        }
    }

    private void screen(JSONArray args) {
        try{
            analytics.with(cordova.getActivity().getApplicationContext()).screen(
                    args.getString(0),
                    args.getString(1),
                    makePropertiesFromJSON(args.getJSONObject(2)),
                    null // passing options is deprecated
            );
        } catch(JSONException e) {
            e.printStackTrace();
        }
    }

    private void alias(JSONArray args) {
        try{
            analytics.with(cordova.getActivity().getApplicationContext()).alias(
                    args.getString(0),
                    null // passing options is deprecated
            );
        } catch(JSONException e) {
            e.printStackTrace();
        }
    }

    private void reset() {
        analytics.with(cordova.getActivity().getApplicationContext()).reset();
    }

    private void flush() {
        analytics.with(cordova.getActivity().getApplicationContext()).flush();
    }

    private void getSnapshot(CallbackContext callbackContext) {
        StatsSnapshot snapshot = analytics.with(cordova.getActivity().getApplicationContext()).getSnapshot();
        JSONObject snapshotJSON = new JSONObject();

        try {
            snapshotJSON.put("timestamp", snapshot.timestamp);
            snapshotJSON.put("flushCount", snapshot.flushCount);
            snapshotJSON.put("flushEventCount", snapshot.flushEventCount);
            snapshotJSON.put("integrationOperationCount", snapshot.integrationOperationCount);
            snapshotJSON.put("integrationOperationDuration", snapshot.integrationOperationDuration);
            snapshotJSON.put("integrationOperationAverageDuration", snapshot.integrationOperationAverageDuration);
            snapshotJSON.put("integrationOperationDurationByIntegration", new JSONObject(snapshot.integrationOperationDurationByIntegration));

            PluginResult r = new PluginResult(PluginResult.Status.OK, snapshotJSON);
            r.setKeepCallback(false);
            callbackContext.sendPluginResult(r);
        } catch(JSONException e) {
            e.printStackTrace();
            return;
        }
    }

    private Traits makeTraitsFromJSON(JSONObject json) {
        Traits traits = new Traits();
        ConcurrentHashMap<String, Object> traitMap = jsonToMap(json);

        if (traitMap != null) {
            if (traitMap.get("address") != null) {
                traitMap.put("address", new Address((ConcurrentHashMap<String, Object>) traitMap.get("address")));
            }

            traits.putAll(traitMap);
        }

        return traits;
    }

    private Properties makePropertiesFromJSON(JSONObject json) {
        Properties properties = new Properties();
        ConcurrentHashMap<String, Object> propertiesMap = jsonToMap(json);

        if (propertiesMap != null) {
            List<ConcurrentHashMap<String, Object>> rawProducts = (List<ConcurrentHashMap<String, Object>>) propertiesMap.get("products");

            if (rawProducts != null) {
                List<Product> products = new ArrayList<Product>();

                for (ConcurrentHashMap<String, Object> rawProduct : rawProducts) {
                    Product product = new Product(
                        (String) rawProduct.get("id"),
                        (String) rawProduct.get("sku"),
                        (Double) rawProduct.get("price")
                    );

                    product.putAll(rawProduct);
                    products.add(product);
                }

                propertiesMap.put("products", products.toArray(new Product[products.size()]));
            }

            properties.putAll(propertiesMap);
        }

        return properties;
    }

    public static ConcurrentHashMap<String, Object> jsonToMap(JSONObject json) {
        ConcurrentHashMap<String, Object> retMap = new ConcurrentHashMap<String, Object>();

        try {
            if (json != JSONObject.NULL) {
                retMap = toMap(json);
            }
        } catch(JSONException e) {
            e.printStackTrace();
        }

        return retMap;
    }

    public static ConcurrentHashMap<String, Object> toMap(JSONObject object) throws JSONException {
        ConcurrentHashMap<String, Object> map = new ConcurrentHashMap<String, Object>();

        Iterator<String> keysItr = object.keys();
        while(keysItr.hasNext()) {
            String key = keysItr.next();
            Object value = object.get(key);

            if(value instanceof JSONArray) {
                value = toList((JSONArray) value);
            }

            else if(value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }
            map.put(key, value);
        }
        return map;
    }

    public static List<Object> toList(JSONArray array) throws JSONException {
        List<Object> list = new ArrayList<Object>();
        for(int i = 0; i < array.length(); i++) {
            Object value = array.get(i);
            if(value instanceof JSONArray) {
                value = toList((JSONArray) value);
            }

            else if(value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }
            list.add(value);
        }
        return list;
    }
}