package com.cs160.finalproj.slientDisco.support.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;

import com.cs160.finalproj.slientDisco.R;
import com.cs160.finalproj.slientDisco.support.Constants;

public class AppUtils {

    public static void checkReadStoragePermission(@NonNull Activity activity) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                Manifest.permission.READ_EXTERNAL_STORAGE)) {
            openSettingsDialog(activity);
        } else {
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    Constants.REQUESTS.READ_EXTERNAL_STORAGE_REQUEST);
        }
    }

    private static void openSettingsDialog(@NonNull final Context context) {
        new AlertDialog.Builder(context)
                .setTitle(R.string.permission_error_dialog_title)
                .setMessage(R.string.permission_error_dialog_message)
                .setNegativeButton(R.string.permission_error_dialog_negative_button_text, (dialog, which) -> System.exit(Constants.REQUESTS.APPLICATION_EXIT_CODE))
                .setPositiveButton(R.string.permission_error_dialog_positive_button_text, (dialog, which) -> {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", context.getPackageName(), null);
                    intent.setData(uri);
                    context.startActivity(intent);
                })
                .setCancelable(false)
                .create()
                .show();
    }

}
