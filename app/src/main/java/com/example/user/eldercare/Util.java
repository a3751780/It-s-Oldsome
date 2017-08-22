package com.example.user.eldercare;

import android.app.Activity;
import android.app.AlertDialog;


public class Util {

    private Util()
    {
    }

    public static void showExceptionAlert(Activity activity, String operation, Exception ex) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        builder
                .setMessage("Exception: " + ex.getLocalizedMessage())
                .setTitle(operation + " failed")
                .setPositiveButton("OK", null)
                .show();
    }
}
