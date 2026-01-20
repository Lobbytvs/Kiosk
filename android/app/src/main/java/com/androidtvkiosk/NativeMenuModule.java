package com.androidtvkiosk;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.text.InputType;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ArrayAdapter;
import android.graphics.Color;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Promise;

public class NativeMenuModule extends ReactContextBaseJavaModule {
    private static final String PREFS_NAME = "KioskPrefs";
    private static final String KEY_URL = "kiosk_url";
    private Promise pendingPromise;

    NativeMenuModule(ReactApplicationContext context) {
        super(context);
    }

    @Override
    public String getName() {
        return "NativeMenuModule";
    }

    @ReactMethod
    public void showMenu(final Promise promise) {
        final android.app.Activity activity = getCurrentActivity();
        if (activity == null) {
            promise.reject("ERROR", "Activity is null");
            return;
        }

        pendingPromise = promise;

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final String[] items = {"⚙️ Settings", "🚪 Exit App", "✕ Close Menu"};

                AlertDialog.Builder builder = new AlertDialog.Builder(activity, android.R.style.Theme_DeviceDefault_Dialog_Alert);
                builder.setTitle("Kiosk Menu");

                // Use setSingleChoiceItems for better TV navigation
                builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                        switch (which) {
                            case 0: // Settings
                                showSettingsDialog(activity);
                                break;
                            case 1: // Exit
                                activity.finishAndRemoveTask();
                                if (pendingPromise != null) {
                                    pendingPromise.resolve("exit");
                                    pendingPromise = null;
                                }
                                break;
                            case 2: // Close
                                if (pendingPromise != null) {
                                    pendingPromise.resolve("close");
                                    pendingPromise = null;
                                }
                                break;
                        }
                    }
                });

                builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        if (pendingPromise != null) {
                            pendingPromise.resolve("close");
                            pendingPromise = null;
                        }
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.getListView().setFocusable(true);
                dialog.getListView().setFocusableInTouchMode(false);
                dialog.show();
            }
        });
    }

    private void showSettingsDialog(final android.app.Activity activity) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(activity, android.R.style.Theme_DeviceDefault_Dialog_Alert);
                builder.setTitle("Change Kiosk URL");

                // Create EditText for URL input
                final EditText input = new EditText(activity);
                input.setInputType(InputType.TYPE_TEXT_VARIATION_URI);
                input.setTextColor(Color.WHITE);
                input.setHintTextColor(Color.GRAY);
                input.setHint("https://example.com");
                input.setFocusable(true);
                input.setFocusableInTouchMode(true);

                // Load current URL
                SharedPreferences prefs = activity.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                String currentUrl = prefs.getString(KEY_URL, "https://example.com");
                input.setText(currentUrl);
                input.setSelection(input.getText().length()); // Move cursor to end

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

                builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String url = input.getText().toString().trim();
                        if (url.isEmpty()) {
                            if (pendingPromise != null) {
                                pendingPromise.reject("ERROR", "URL cannot be empty");
                                pendingPromise = null;
                            }
                            return;
                        }
                        if (!url.startsWith("http://") && !url.startsWith("https://")) {
                            if (pendingPromise != null) {
                                pendingPromise.reject("ERROR", "URL must start with http:// or https://");
                                pendingPromise = null;
                            }
                            return;
                        }

                        // Save URL
                        SharedPreferences prefs = activity.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString(KEY_URL, url);
                        editor.apply();

                        if (pendingPromise != null) {
                            pendingPromise.resolve(url);
                            pendingPromise = null;
                        }
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        if (pendingPromise != null) {
                            pendingPromise.resolve("cancel");
                            pendingPromise = null;
                        }
                    }
                });

                builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        if (pendingPromise != null) {
                            pendingPromise.resolve("cancel");
                            pendingPromise = null;
                        }
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();

                // Request focus on EditText after dialog shows
                input.requestFocus();
            }
        });
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
