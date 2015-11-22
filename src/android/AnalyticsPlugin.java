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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class AnalyticsPlugin extends CordovaPlugin {

    private static final String TAG = "AnalyticsPlugin";
    private Analytics analytics;
    private String writeKey;

    @Override protected void pluginInitialize() {
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
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
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
        analytics.with(cordova.getActivity().getApplicationContext()).identify(
            args.optString(0),
            makeTraitsFromJSON(args.optJSONObject(1)),
            null // passing options is deprecated
        );
    }

    private void group(JSONArray args) {
        analytics.with(cordova.getActivity().getApplicationContext()).group(
            args.optString(0),
            makeTraitsFromJSON(args.optJSONObject(1)),
            null // passing options is deprecated
        );
    }

    private void track(JSONArray args) {
        analytics.with(cordova.getActivity().getApplicationContext()).track(
            args.optString(0),
            makePropertiesFromJSON(args.optJSONObject(1)),
            null // passing options is deprecated
        );
    }

    private void screen(JSONArray args) {
        analytics.with(cordova.getActivity().getApplicationContext()).screen(
            args.optString(0),
            args.optString(1),
            makePropertiesFromJSON(args.optJSONObject(2)),
            null // passing options is deprecated
        );
    }

    private void alias(JSONArray args) {
        analytics.with(cordova.getActivity().getApplicationContext()).alias(
            args.optString(0),
            null // passing options is deprecated
        );
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
        Map<String, Object> traitMap = mapFromJSON(json);

        if (traitMap != null) {
            if (traitMap.get("address") != null) {
                traitMap.put("address", new Address((Map<String, Object>) traitMap.get("address")));
            }

            traits.putAll(traitMap);
        }

        return traits;
    }

    private Properties makePropertiesFromJSON(JSONObject json) {
        Properties properties = new Properties();
        Map<String, Object> propertiesMap = mapFromJSON(json);

        if (propertiesMap != null) {
            List<Map<String, Object>> rawProducts = (List<Map<String, Object>>) propertiesMap.get("products");

            if (rawProducts != null) {
                List<Product> products = new ArrayList<Product>();

                for (Map<String, Object> rawProduct : rawProducts) {
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

    private static Map<String, Object> mapFromJSON(JSONObject jsonObject) {
        if (jsonObject == null) {
            return null;
        }
        Map<String, Object> map = new HashMap<String, Object>();
        Iterator<String> keysIter = jsonObject.keys();
        while (keysIter.hasNext()) {
            String key = keysIter.next();
            Object value = getObject(jsonObject.opt(key));
            if (value != null) {
                map.put(key, value);
            }
        }
        return map;
    }

    private static List<Object> listFromJSON(JSONArray jsonArray) {
        List<Object> list = new ArrayList<Object>();
        for (int i = 0, count = jsonArray.length(); i < count; i++) {
            Object value = getObject(jsonArray.opt(i));
            if (value != null) {
                list.add(value);
            }
        }
        return list;
    }

    private static Object getObject(Object value) {
        if (value instanceof JSONObject) {
            value = mapFromJSON((JSONObject) value);
        } else if (value instanceof JSONArray) {
            value = listFromJSON((JSONArray) value);
        }
        return value;
    }
}