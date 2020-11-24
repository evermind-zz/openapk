package com.dkanada.openapk.utils;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import androidx.core.content.ContextCompat;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;

import com.dkanada.openapk.App;
import com.dkanada.openapk.R;
import com.dkanada.openapk.models.AppItem;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class OtherUtils {
    // get the name of the extracted app
    public static String getAPKFilename(AppItem appItem) {
        AppPreferences appPreferences = App.getAppPreferences();
        switch (appPreferences.getFilename()) {
            case "0":
                return appItem.getPackageName() + ".apk";
            case "1":
                return appItem.getPackageLabel() + ".apk";
            default:
                return appItem.getPackageName() + ".apk";
        }
    }

    // open google play if installed otherwise open browser
    public static void goToGooglePlay(Context context, PackageInfo packageInfo) {
        try {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packageInfo.packageName)));
        } catch (ActivityNotFoundException e) {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + packageInfo.packageName)));
        }
    }

    // save app name to clipboard
    public static void saveClipboard(Context context, PackageInfo packageInfo) {
        ClipData clipData;
        clipData = ClipData.newPlainText("text", packageInfo.packageName);

        ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        clipboardManager.setPrimaryClip(clipData);
    }

    // get version number for this app
    public static String getAppVersionName(Context context) {
        String res = "0.0.0";
        try {
            res = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    // get version code for this app
    public static int getAppVersionCode(Context context) {
        int res = 0;
        try {
            res = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    // get intent to share app
    public static Intent getShareIntent(File file) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
        intent.setType("application/vnd.android.package-archive");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    public static boolean checkPermissions(Context context) {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    public static void requestPermissions(Context context) {
        Activity activity = (Activity) context;
        if (!checkPermissions(context)) {
            activity.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, AppPreferences.CODE_PERMISSION);
        }
    }

    public static int dark(int color, double factor) {
        int a = Color.alpha(color);
        int r = Color.red(color);
        int g = Color.green(color);
        int b = Color.blue(color);
        return Color.argb(a, Math.max((int) (r * factor), 0), Math.max((int) (g * factor), 0), Math.max((int) (b * factor), 0));
    }

    // set the toolbar title with any string
    public static void setToolbarTitle(Context context, String title) {
        Activity activity = (Activity) context;
        Toolbar toolbar = (Toolbar) activity.findViewById(R.id.toolbar);
        toolbar.setTitle(title);
    }

    // update the state of the favorite icon
    public static void updateAppFavoriteIcon(Context context, MenuItem menuItem, PackageInfo packageInfo) {
        /*if (App.getAppPreferences().getFavoriteList().contains(packageInfo.packageName)) {
            menuItem.setIcon(context.getResources().getDrawable(R.drawable.ic_star));
        } else {
            menuItem.setIcon(context.getResources().getDrawable(R.drawable.ic_star_border));
        }*/
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap = Bitmap.createBitmap(150, 150, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    public static String formatDate(long date) {
        return new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a", Locale.US).format(date);
    }

    public static String formatSize(long size) {
        if (size < 1024) {
            return Long.toString(size) + "B";
        } else if (size < 1048576) {
            size = size / 1024;
            return Long.toString(size) + "KB";
        } else if (size < 1073741824) {
            size = size / 1048576;
            return Long.toString(size) + "MB";
        } else {
            size = size / 1073741824;
            return Long.toString(size) + "GB";
        }
    }
}
