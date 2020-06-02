/*
 * Copyright 2020 Author @NMD [Next Mobile Development]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nmd.screenshot;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaActionSound;
import android.media.MediaScannerConnection;
import android.os.Build.VERSION;
import android.os.Environment;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Timer;
import java.util.TimerTask;


@SuppressWarnings("unused")
public class Screenshot {

    private Activity activity;
    private Context context;
    private static final String LOG_TAG = "Screenshot";
    private static final String CHANNEL_ID = "Screenshot_Channel_ID";
    private Bitmap bitmapBackup;
    private LinearLayout linearLayout;
    private ImageView imageView;
    private MediaActionSound takeSound;
    private OnResultListener onResultListener;
    private final Handler androidUIHandler = new Handler();
    private NotificationManagerCompat mNotificationManagerCompat;
    private PackageManager packageManager;

    private String fileName;
    private boolean preview;
    private boolean notification;
    private String notificationTitle;
    private String notificationShareTitle;
    private boolean notificationBigStyle;
    private boolean notificationButton;
    private String filePathBackup;
    private String fileNameBackup;
    private float dimAmount;
    private String currentPackageName;
    private int readPermission;
    private int writePermission;

    public Screenshot(Context context) {
        this.context = context;
        this.activity = (Activity) context;
        initialize();
    }

    private void initialize() {
        this.fileName = "Screenshot.png";
        this.preview = true;
        this.notification = true;
        this.notificationTitle = "Screenshot..";
        this.notificationShareTitle = "Share";
        this.notificationBigStyle = false;
        this.notificationButton = true;
        this.dimAmount = 0.5f;
        this.mNotificationManagerCompat = NotificationManagerCompat.from(this.context);
        this.packageManager = this.context.getPackageManager();
        this.currentPackageName = this.context.getPackageName().toString();
        this.readPermission = packageManager.checkPermission("android.permission.READ_EXTERNAL_STORAGE", this.currentPackageName);
        this.writePermission = packageManager.checkPermission("android.permission.WRITE_EXTERNAL_STORAGE", this.currentPackageName);
        if (VERSION.SDK_INT >= 16) {
            takeSound = new MediaActionSound();
            takeSound.load(MediaActionSound.SHUTTER_CLICK);
        }
        Log.d(LOG_TAG, "Screenshot Created");
    }

    public void takeScreenshot() {
        View view = activity.getWindow().getDecorView().getRootView();
        take(view);
    }

    public void takeScreenshotFromView(View view) {
        take(view);
    }

    private void take(final View view) {
        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                androidUIHandler.post(new Runnable() {
                    public void run() {
                        view.setDrawingCacheEnabled(true);
                        view.buildDrawingCache(true);
                        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
                        bitmapBackup = bitmap;
                        view.setDrawingCacheEnabled(false);

                        saveUtil(bitmap);
                        MediaScannerConnection.scanFile(context, new String[]{filePathBackup}, new String[]{"image/*"}, null);
                        timer.cancel();
                    }
                });
            }
        }, 75);
    }

    public boolean arePermissionsGranted() {
        return (this.readPermission == PackageManager.PERMISSION_GRANTED &&
                this.writePermission == PackageManager.PERMISSION_GRANTED);
    }

    public boolean isReadPermissionGranted() {
        return (this.readPermission == PackageManager.PERMISSION_GRANTED);
    }

    public boolean isWritePermissionGranted() {
        return (writePermission == PackageManager.PERMISSION_GRANTED);
    }

    public interface OnResultListener {
        void result(boolean success, String filePath, Bitmap bitmap);
    }

    public void setCallback(OnResultListener listener) {
        this.onResultListener = listener;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return this.fileName;
    }

    private void saveUtil(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, byteArrayOutputStream);

        FileOutputStream fileOutputStream;
        File image = null;
        try {
            image = new File(Environment.getExternalStorageDirectory() + "/" + fileName);
            fileOutputStream = new FileOutputStream(image);
            fileOutputStream.write(byteArrayOutputStream.toByteArray());
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (Exception e) {
            Log.e(LOG_TAG, "" + e);
            if (onResultListener != null) {
                onResultListener.result(false, "" + e, null);
            }
        } finally {
            filePathBackup = (image != null) ? image.getAbsolutePath() : "There was a problem";
            fileNameBackup = (image != null) ? image.getName() : "There was a problem";
            //image is null should never happen
            if (VERSION.SDK_INT >= 16) {
                takeSound.play(MediaActionSound.SHUTTER_CLICK);
            }
            if (this.preview) {
                preview();
            }
            if (this.notification) {
                newNotification();
            }
            if (onResultListener != null) {
                onResultListener.result(true, filePathBackup, bitmapBackup);
            }
        }
    }

    public void showPreview(boolean enabled) {
        this.preview = enabled;
    }

    public boolean showPreview() {
        return this.preview;
    }

    public void showNotification(boolean enabled) {
        this.notification = enabled;
    }

    public boolean showNotification() {
        return this.notification;
    }

    public void notificationTitle(String title) {
        this.notificationTitle = title;
    }

    public String notificationTitle() {
        return this.notificationTitle;
    }

    public void notificationShareTitle(String title) {
        this.notificationShareTitle = title;
    }

    public String notificationShareTitle() {
        return this.notificationShareTitle;
    }

    public void notificationBigStyle(boolean enabled) {
        this.notificationBigStyle = enabled;
    }

    public boolean notificationBigStyle() {
        return this.notificationBigStyle;
    }

    public void notificationShareButton(boolean enabled) {
        this.notificationButton = enabled;
    }

    public boolean notificationShareButton() {
        return this.notificationButton;
    }

    public void allowScreenshots(boolean enabled) {
        if (enabled) {
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_SECURE);
        } else {
            activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        }
    }

    public void setDimAmount(float amount) {
        this.dimAmount = amount;
    }

    public float getDimAmount() {
        return this.dimAmount;
    }

    private void preview() {
        AlertDialog.Builder alert = new AlertDialog.Builder(activity);

        linearLayout = new LinearLayout(context);
        imageView = new ImageView(activity);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

        if (display.getRotation() == Surface.ROTATION_0 || display.getRotation() == Surface.ROTATION_180) {
            //for portrait mode
            linearLayout.setPadding(15, 19, 15, 19);
        } else {
            //for landscape mode
            linearLayout.setPadding(27, 0, 27, 0);
        }
        linearLayout.setBackgroundColor(-1);//white
        imageView.setImageBitmap(bitmapBackup);

        linearLayout.addView(imageView);
        alert.setView(linearLayout);
        alert.setCancelable(false);

        final AlertDialog dialog = alert.create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setDimAmount(this.dimAmount);
        }

        dialog.show();

        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);

        int height = (int) (metrics.heightPixels * 0.86);
        int width = (int) (metrics.widthPixels * 0.85);

        dialog.getWindow().setLayout(width, height);

        final Timer t = new Timer();
        t.schedule(new TimerTask() {
            public void run() {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                    t.cancel();
                }
            }
        }, 1250);
    }

    private void newNotification() {
        int ID = (int) System.currentTimeMillis();

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle(notificationTitle)
                .setContentText(fileNameBackup)
                .setSmallIcon(android.R.drawable.ic_menu_gallery)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setChannelId(CHANNEL_ID)
                .setAutoCancel(true);

        if (VERSION.SDK_INT >= 16) {
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_STREAM, android.net.Uri.parse(filePathBackup));
            shareIntent.putExtra("EXTRA_DETAILS_ID", ID);
            shareIntent.setType("image/*");

            if (notificationButton) {
                //Action buttons depend on expanded notifications, which are only available in Android 4.1 and later.
                PendingIntent detailsPendingIntent = PendingIntent.getActivity(context, ID, shareIntent, PendingIntent.FLAG_CANCEL_CURRENT);
                notificationBuilder.addAction(android.R.drawable.ic_menu_share, notificationShareTitle, detailsPendingIntent);
                //notificationBuilder.cancel(ID);
            }
        }

        if (notificationBigStyle) {
            notificationBuilder.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(bitmapBackup));
        }

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(android.net.Uri.parse(filePathBackup), "image/*");
        notificationBuilder.setContentIntent(PendingIntent.getActivity(context, ID, intent, 0));

        mNotificationManagerCompat.notify(0, notificationBuilder.build());
    }
}