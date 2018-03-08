/*
 * Copyright 2018 Author @NMD [Next Mobile Development]
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

package com.nmd.android.support;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import android.graphics.Bitmap;

import android.media.MediaActionSound;
import android.media.MediaScannerConnection;

import android.os.AsyncTask;
import android.os.Environment;
import android.os.Build.VERSION;
import android.os.Handler;

import android.util.DisplayMetrics;
import android.util.Log;

import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import android.widget.LinearLayout;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.System;
import java.util.Timer;
import java.util.TimerTask;

import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.BigPictureStyle;
import android.support.v4.app.NotificationCompat.Builder;
import android.support.v4.app.NotificationManagerCompat;


@SuppressWarnings("unused")
public class Screenshot {

    private Activity activity;
    private Context context;
    private static final String LOG_TAG = "Screenshot";
    private static final String CHANNEL_ID = "Screenshot_Channel_ID";
    private Bitmap bitmapBackup;
    private LinearLayout layout;
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
      this.activity = (Activity)context;
      Initialize();
    }
    
    private void Initialize() {
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
      Log.d(LOG_TAG,"Screenshot Created");
    }

    public void TakeScreenshot() {
      View view = activity.getWindow().getDecorView().getRootView();
      Take(view);
    }
    
    public void TakeScreenshotFromView(View view) {
      Take(view);
    }
    
    private void Take(View view) {
        final Timer t = new Timer();
        t.schedule(new TimerTask() {
            public void run() {
            	androidUIHandler.post(new Runnable() {
                    public void run() {
                    view.setDrawingCacheEnabled(true);
                    view.buildDrawingCache(true);
                    Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
                    bitmapBackup = bitmap;
                    view.setDrawingCacheEnabled(false);
                        
                    SaveUtil(bitmap);
                    MediaScannerConnection.scanFile(context, new String[] { filePathBackup }, new String[] { "image/*" }, null);
                    t.cancel();
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
      String currentPackageName = this.context.getPackageName().toString();
      int readPermission = packageManager.checkPermission("android.permission.READ_EXTERNAL_STORAGE", currentPackageName);
      return (this.readPermission == PackageManager.PERMISSION_GRANTED);
    }
    
    public boolean isWritePermissionGranted() {
      String currentPackageName = this.context.getPackageName().toString();
      int writePermission = packageManager.checkPermission("android.permission.WRITE_EXTERNAL_STORAGE", currentPackageName);
      return (writePermission == PackageManager.PERMISSION_GRANTED);
    }
    
    public interface OnResultListener {
      public void result(boolean success, String filePath, Bitmap bitmap);
    }
   
    public void setCallback(OnResultListener listener) {
      this.onResultListener = listener;
    }
    
    public void setFileName(String name) {
      this.fileName = name;
    }

    public String getFileName() {
      return this.fileName;
    }

    private void SaveUtil(Bitmap bmOut) {
    ByteArrayOutputStream ostream = new ByteArrayOutputStream();
    bmOut.compress(Bitmap.CompressFormat.PNG, 0 , ostream);
    
    FileOutputStream fostream = null;
    File image = null;
        try {
            image = new File(Environment.getExternalStorageDirectory()+"/"+fileName);
            fostream = new FileOutputStream(image);
            fostream.write(ostream.toByteArray());
            fostream.flush();
            fostream.close();
        } catch (FileNotFoundException e) {
            Log.e(LOG_TAG, e.getMessage());
            if (onResultListener != null) {
                onResultListener.result(false, e.getMessage(), null);
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, e.getMessage());
            if (onResultListener != null) {
                onResultListener.result(false, e.getMessage(), null);
            }
        }
    filePathBackup = (image != null) ? image.getAbsolutePath() : "There was a problem";
    fileNameBackup = (image != null) ? image.getName() : "There was a problem";
    //image is null should never happen
        if (this.preview) {
            Preview();
        }
        if (VERSION.SDK_INT >= 16) {
        	takeSound.play(MediaActionSound.SHUTTER_CLICK);
        }
        if (this.notification) {
        	NewNotification();
        }  
        if (onResultListener != null) {
            onResultListener.result(true, filePathBackup, bitmapBackup);
        }
    }

    public void ShowPreview(boolean enabled) {
      this.preview = enabled;
    }
	
    public boolean ShowPreview() {
    return this.preview;
    }
	
    public void ShowNotification(boolean enabled) {
      this.notification = enabled;
    }
	
    public boolean ShowNotification() {
    return this.notification;
    }
	
    public void NotificationTitle(String title){
      this.notificationTitle = title;   	
    }
	
    public String NotificationTitle() {
    return this.notificationTitle;
    }
	
    public void NotificationShareTitle(String title){
      this.notificationShareTitle = title;   	
    }
	
    public String NotificationShareTitle() {
    return this.notificationShareTitle;
    }
	
    public void NotificationBigStyle(boolean enabled){
      this.notificationBigStyle = enabled;   	
    }
	
    public boolean NotificationBigStyle() {
    return this.notificationBigStyle;
    }
    
    public void NotificationShareButton(boolean enabled){
      this.notificationButton = enabled;   	
    }
    	
    public boolean NotificationShareButton() {
      return this.notificationButton;
    }
	
    public void AllowScreenshots(boolean enabled){
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
    
    private void Preview() {
    AlertDialog.Builder alert = new AlertDialog.Builder(activity);

    layout = new LinearLayout(context);
    imageView = new ImageView(activity);
    layout.setOrientation(LinearLayout.VERTICAL);
    Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        if (display.getRotation() == 0 || display.getRotation() == 2) {
            //for portrait mode
        	layout.setPadding(15, 19, 15, 19);
        } else {
        	//for landscape mode
            layout.setPadding(27, 0, 27, 0);
        }
    layout.setBackgroundColor(-1);//white
    imageView.setImageBitmap(bitmapBackup);
	
    layout.addView(imageView);
    alert.setView(layout);
	
    final AlertDialog dialog = alert.create();
    dialog.getWindow().setDimAmount(this.dimAmount);
        
    dialog.show();
	
    DisplayMetrics metrics = new DisplayMetrics();
    activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);

    int height = (int) (metrics.heightPixels*0.86);
    int width = (int) (metrics.widthPixels*0.85);

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
    
    private void NewNotification() {
      int ID = (int) System.currentTimeMillis();
    	
      NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, "CHANNEL_ID")
      // support v4 version 26.1.0
      //.setChannelId(String.valueOf(System.currentTimeMillis()))
      .setContentTitle(notificationTitle)
      .setContentText(fileNameBackup)
      .setSmallIcon(android.R.drawable.ic_menu_gallery)
      .setPriority(NotificationCompat.PRIORITY_DEFAULT)
      .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
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