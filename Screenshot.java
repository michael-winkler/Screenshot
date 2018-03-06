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
import android.app.Notification;
import android.app.Notification.Style;
import android.app.NotificationManager;
import android.app.PendingIntent;

import android.content.Context;
import android.content.Intent;

import android.graphics.Bitmap;

import android.media.MediaScannerConnection;

import android.os.AsyncTask;
import android.os.Environment;
import android.os.Build.VERSION;
import android.os.Handler;

import android.util.DisplayMetrics;
import android.util.Log;

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


@SuppressWarnings("unused")
public class Screenshot {

    private Activity activity;
    private Context context;
    private static final String LOG_TAG = "Screenshot";
    private String fileName;
    private Bitmap bitmapBackup;
    private LinearLayout layout;
    private ImageView imageView;
    private boolean preview;
    private boolean notification;
    private String notificationTitle;
    private String notificationShareTitle;
    private boolean notificationBigStyle;
    private boolean notificationButton;
    private String filePathBackup;
    private String fileNameBackup;
    private OnResultListener onResultListener;
    private final Handler androidUIHandler = new Handler();

    
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
      Log.d(LOG_TAG,"Screenshot Created");
    }

    public void TakeScreenshot() {
      androidUIHandler.post(new Runnable() {
        public void run() {
        Take();
        }
      }); 
    }

    private void Take() {
      View view = activity.getWindow().getDecorView().getRootView();
      view.setDrawingCacheEnabled(true);
      Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
      bitmapBackup = bitmap;
      view.setDrawingCacheEnabled(false);
      
      SaveUtil(bitmap);
      MediaScannerConnection.scanFile(context, new String[] { filePathBackup }, new String[] { "image/*" }, null);
    }

    public interface OnResultListener {
      public void result(boolean success, String result);
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
            Log.e(LOG_TAG, e.getLocalizedMessage());
            if (onResultListener != null) {
                onResultListener.result(false, e.getLocalizedMessage().toString());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, e.getLocalizedMessage());
            if (onResultListener != null) {
                onResultListener.result(false, e.getLocalizedMessage().toString());
            }
        }
    filePathBackup = (image != null) ? image.getAbsolutePath() : "There was a problem";
    fileNameBackup = (image != null) ? image.getName() : "There was a problem";
    //image is null should never happen
        if (this.preview) {
          Preview();
        }
        if (this.notification) {
          Notification();
        }  
        if (onResultListener != null) {
          onResultListener.result(true, filePathBackup);
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
	
    private void Preview() {
    AlertDialog.Builder alert = new AlertDialog.Builder(activity);

    layout = new LinearLayout(context);
    imageView = new ImageView(activity);
    layout.setOrientation(LinearLayout.VERTICAL);
    layout.setPadding(15, 19, 15, 19);
    layout.setBackgroundColor(-1);//white
    imageView.setImageBitmap(bitmapBackup);
	
    layout.addView(imageView);
    alert.setView(layout);
	
    final AlertDialog dialog = alert.create();
        if (VERSION.SDK_INT >= 21) {
            dialog.getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        }

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
	
    @SuppressWarnings("deprecation")
	private void Notification() {
	Notification.Builder builder = new Notification.Builder(context);
    builder.setSmallIcon(android.R.drawable.ic_menu_gallery);
    //https://developer.android.com/reference/android/R.drawable.html#ic_menu_gallery
    int ID = (int) System.currentTimeMillis();

    if (VERSION.SDK_INT >= 16) {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, android.net.Uri.parse(filePathBackup));
        shareIntent.putExtra("EXTRA_DETAILS_ID", ID);
        shareIntent.setType("image/*");

            if (notificationButton) {
                PendingIntent detailsPendingIntent = PendingIntent.getActivity(context, ID, shareIntent, PendingIntent.FLAG_CANCEL_CURRENT);
                builder.addAction(android.R.drawable.ic_menu_share, notificationShareTitle, detailsPendingIntent);
                //notificationManager.cancel(ID);
            }
        }
	
    builder.setAutoCancel(true);
    builder.setContentTitle(notificationTitle);
    builder.setContentText(fileNameBackup);
	
    if (VERSION.SDK_INT >= 16 && notificationBigStyle) {
        Notification.BigPictureStyle bigPictureStyle = new Notification.BigPictureStyle();
        bigPictureStyle.setBigContentTitle(notificationTitle);
        bigPictureStyle.setSummaryText(fileNameBackup);
        bigPictureStyle.bigPicture(bitmapBackup);
        builder.setStyle(bigPictureStyle);
	}
	
    Intent intent = new Intent();
    intent.setAction(Intent.ACTION_VIEW);
    intent.setDataAndType(android.net.Uri.parse(filePathBackup), "image/*");
    builder.setContentIntent(PendingIntent.getActivity(context, ID, intent, 0));
	
    Notification build = builder.build();
    NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    build.flags |= Notification.FLAG_AUTO_CANCEL;
    notificationManager.notify(0, build);
    }


}