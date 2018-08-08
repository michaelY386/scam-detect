package edu.cmu.eps.scams.permissions;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

/**
 * Created by jeremy on 3/10/2018.
 * The class wraps around the permissions API in android.
 */

public class PermissionsFacade {

    private static final String TAG = "PermissionsFacade";

    public static boolean isPermissionGranted(Activity activity, String permission) {
        boolean result = false;
        Log.d(TAG, String.format("Request to check for permission: %s", permission));
        if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, String.format("Permission is not granted: %s", permission));
            result = false;
        } else {
            Log.d(TAG, String.format("Permission is granted: %s", permission));
            result = true;
        }
        return result;
    }

    public static void requestPermission(Activity activity, String[] permissions, int requestCode) {
        StringBuilder permissionString = new StringBuilder();
        for (String permission : permissions) {
            permissionString.append(permission);
            permissionString.append(", ");
        }
        Log.d(TAG, String.format("Requesting user for permissions: %s", permissionString.toString()));
        ActivityCompat.requestPermissions(activity, permissions, requestCode);
    }
}
