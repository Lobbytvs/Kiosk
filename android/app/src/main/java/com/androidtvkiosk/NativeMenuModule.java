package com.androidtvkiosk;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.InputType;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.graphics.Color;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Promise;

public class NativeMenuModule extends ReactContextBaseJavaModule {
    private static final String PREFS_NAME = "KioskPrefs";
    private static final String KEY_URL = "kiosk_url";

    NativeMenuModule(ReactApplicationContext context) {
        super(context);
    }

    @Override
    public String getName() {
        return "NativeMenuModule";
    }

    @ReactMethod
    public void showMenu(Promise promise) {
        final android.app.Activity activity = getCurrentActivity();
        if (activity == null) {
            promise.reject("ERROR", "Activity is null");
            return;
        }

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setTitle("Kiosk Menu");
                builder.setItems(new String[]{"Settings", "Exit App", "Close Menu"},
                    (dialog, which) -> {
                        switch (which) {
                            case 0: // Settings
                                showSettingsDialog(activity, promise);
                                break;
                            case 1: // Exit
                                activity.finishAndRemoveTask();
                                promise.resolve("exit");
                                break;
                            case 2: // Close
                                dialog.dismiss();
                                promise.resolve("close");
                                break;
                        }
                    });
                builder.setOnCancelListener(dialog -> promise.resolve("close"));
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    private void showSettingsDialog(android.app.Activity activity, Promise promise) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Change Kiosk URL");

        // Create EditText for URL input
        final EditText input = new EditText(activity);
        input.setInputType(InputType.TYPE_TEXT_VARIATION_URI);
        input.setTextColor(Color.WHITE);
        input.setHintTextColor(Color.GRAY);
        input.setHint("https://example.com");

        // Load current URL
        SharedPreferences prefs = activity.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String currentUrl = prefs.getString(KEY_URL, "https://example.com");
        input.setText(currentUrl);

        // Add padding
        LinearLayout container = new LinearLayout(activity);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(50, 20, 50, 20);
        input.setLayoutParams(params);
        container.addView(input);

        builder.setView(container);

        builder.setPositiveButton("Save", (dialog, which) -> {
            String url = input.getText().toString().trim();
            if (url.isEmpty()) {
                promise.reject("ERROR", "URL cannot be empty");
                return;
            }
            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                promise.reject("ERROR", "URL must start with http:// or https://");
                return;
            }

            // Save URL
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(KEY_URL, url);
            editor.apply();

            promise.resolve(url);
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> {
            dialog.cancel();
            promise.resolve("cancel");
        });

        AlertDialog dialog = builder.create();
        dialog.show();
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
