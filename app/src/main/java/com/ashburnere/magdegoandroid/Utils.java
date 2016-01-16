package com.ashburnere.magdegoandroid;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

/**
 * Created by erik on 15.01.16.
 */
public class Utils {

    public static void showAboutDialog(Activity parent) {
        AlertDialog alertDialog = new AlertDialog.Builder(parent).create();
        alertDialog.setTitle("Über");
        alertDialog.setMessage("MagdeGo für Android (Testversion)\nKontakt: ashburnere@gmail.com");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }
}
