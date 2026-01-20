package com.androidtvkiosk;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.BaseActivityEventListener;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Promise;

public class NativeMenuModule extends ReactContextBaseJavaModule {
    private static final String PREFS_NAME = "KioskPrefs";
    private static final String KEY_URL = "kiosk_url";
    private static final int MENU_REQUEST_CODE = 1001;
    private Promise pendingPromise;

    private final ActivityEventListener activityEventListener = new BaseActivityEventListener() {
        @Override
        public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
            if (requestCode == MENU_REQUEST_CODE && pendingPromise != null) {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    String result = data.getStringExtra(ComposeMenuActivity.EXTRA_RESULT);
                    if ("edit_url".equals(result)) {
                        // For now, resolve with "edit_url" - could be expanded later
                        pendingPromise.resolve("edit_url");
                    } else {
                        pendingPromise.resolve("close");
                    }
                } else {
                    pendingPromise.resolve("close");
                }
                pendingPromise = null;
            }
        }
    };

    NativeMenuModule(ReactApplicationContext context) {
        super(context);
        context.addActivityEventListener(activityEventListener);
    }

    @Override
    public String getName() {
        return "NativeMenuModule";
    }

    @ReactMethod
    public void showMenu(final Promise promise) {
        final Activity activity = getCurrentActivity();
        if (activity == null) {
            promise.reject("ERROR", "Activity is null");
            return;
        }

        pendingPromise = promise;

        Intent intent = new Intent(activity, ComposeMenuActivity.class);
        intent.putExtra(ComposeMenuActivity.EXTRA_MENU_TYPE, ComposeMenuActivity.MENU_TYPE_MAIN);
        activity.startActivityForResult(intent, MENU_REQUEST_CODE);
    }

    @ReactMethod
    public void getStoredUrl(Promise promise) {
        try {
            Context context = getReactApplicationContext();
            SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            String url = prefs.getString(KEY_URL, "https://example.com");
            promise.resolve(url);
        } catch (Exception e) {
            promise.reject("ERROR", e.getMessage());
        }
    }
}
