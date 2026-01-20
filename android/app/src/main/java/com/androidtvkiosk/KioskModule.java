package com.androidtvkiosk;

import android.view.View;
import android.view.WindowManager;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Promise;

public class KioskModule extends ReactContextBaseJavaModule {
    private static final String TAG = "KioskModule";

    KioskModule(ReactApplicationContext context) {
        super(context);
    }

    @Override
    public String getName() {
        return "KioskModule";
    }

    @ReactMethod
    public void enableKioskMode() {
        final android.app.Activity activity = getCurrentActivity();
        if (activity != null) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    View decorView = activity.getWindow().getDecorView();

                    // Hide navigation bar and status bar
                    int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

                    decorView.setSystemUiVisibility(uiOptions);

                    // Keep screen on
                    activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                }
            });
        }
    }

    @ReactMethod
    public void preventExit(Promise promise) {
        // This is handled in MainActivity
        promise.resolve(true);
    }
}
