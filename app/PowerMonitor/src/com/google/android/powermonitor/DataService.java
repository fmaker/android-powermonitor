/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.android.powermonitor;

import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbManager;
import android.os.IBinder;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Observer;
import java.util.Random;


/**
 * @author ffrank@google.com (Frank Maker)
 *
 */
public class DataService extends Service implements Runnable{
  // Simulation
  public final static String SIMULATE = "SIMULATE";
  Thread mSimThread;

  private static final String ACTION_USB_PERMISSION = "com.google.android.PowerMonitor.action.USB_PERMISSION";
  private static final String TAG = "DataService";

  static PowerMonitorData mData = new PowerMonitorData();
  boolean mBroadcastData = false;
  private UsbManager mUsbManager;
  private PendingIntent mPermissionIntent;
  private BroadcastReceiver mUsbReceiver;
  private Object mInputStream;
  private Object mOutputStream;
  private ParcelFileDescriptor mFileDescriptor;
  private UsbAccessory mAccessory;

  @Override
  public void onCreate() {
    super.onCreate();

    mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
    /*mPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(
            ACTION_USB_PERMISSION), 0);
    IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
    filter.addAction(UsbManager.ACTION_USB_ACCESSORY_DETACHED);
    registerReceiver(mUsbReceiver, filter);*/

    //Intent intent = getIntent();
    if (mInputStream != null && mOutputStream != null) {
        return;
    }

    UsbAccessory[] accessories = mUsbManager.getAccessoryList();
    UsbAccessory accessory = (accessories == null ? null : accessories[0]);
    if (accessory != null) {
        if (mUsbManager.hasPermission(accessory)) {
            openAccessory(accessory);
        } else {
            synchronized (mUsbReceiver) {
                /*if (!mPermissionRequestPending) {
                    mUsbManager.requestPermission(accessory,
                            mPermissionIntent);
                    mPermissionRequestPending = true;
                }*/
            }
        }
    } else {
        Log.d(TAG, "mAccessory is null");
    }

    /*if (getLastNonConfigurationInstance() != null) {
        mAccessory = (UsbAccessory) getLastNonConfigurationInstance();
        openAccessory(mAccessory);
    }*/

  }



  private void openAccessory(UsbAccessory accessory) {
      mFileDescriptor = mUsbManager.openAccessory(accessory);
      if (mFileDescriptor != null) {
          mAccessory = accessory;
          FileDescriptor fd = mFileDescriptor.getFileDescriptor();
          mInputStream = new BufferedInputStream(new FileInputStream(fd));
          mOutputStream = new FileOutputStream(fd);
          Thread thread = new Thread(null, this, "PowerMonitor");
          thread.start();
          Log.d(TAG, "accessory opened");
      } else {
          Log.d(TAG, "accessory open fail");
      }
  }

  public static void addPowerDataObserver(Observer o){
    mData.addObserver(o);
  }

  public static void removePowerDataObserver(Observer o){
    mData.deleteObserver(o);
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {

    // Simulating
    if(intent.getBooleanExtra(SIMULATE, false)){
      simulateData();
    }
    // ADK
    else{

    }

    return super.onStartCommand(intent, flags, startId);
  }

  @Override
  public void onDestroy() {
    mBroadcastData = false;
    super.onDestroy();

  }

  @Override
  public IBinder onBind(Intent arg0) {
    return null;

  }

  private void simulateData(){

    mSimThread = new Thread(){
      long offset = 0;
      long timestamp;
      float voltage,current;
      Random mRandomGenerator = new Random();

      private static final float MAX_CURRENT = 1.0F;
      private static final float MAX_VOLTAGE = 4.2F;
      private static final float MIN_VOLTAGE = 3.6F;
      private static final long PERIOD_MS = 100;

      @Override
      public void run() {
        mBroadcastData = true;

        while(mBroadcastData){
          if(offset == 0)
            offset = System.currentTimeMillis();
          timestamp = System.currentTimeMillis() - offset;
          voltage = mRandomGenerator.nextFloat() *
            (MAX_VOLTAGE-MIN_VOLTAGE)+MIN_VOLTAGE;
          current = mRandomGenerator.nextFloat() * MAX_CURRENT;

          // Get ready to send data
          mData.setTimestamp(timestamp);
          mData.setCurrent(current);
          mData.setVoltage(voltage);

          // Send out new data point
          mData.notifyObservers();

          try {
            sleep(PERIOD_MS);
          } catch (InterruptedException e) {
            // No big deal just keep going
          }
        }

        // Remove observers
        mData.deleteObservers();
        mData = null;
      }

    };

    mSimThread.start();
  }



  /* (non-Javadoc)
   * @see java.lang.Runnable#run()
   */
  @Override
  public void run() {
    // TODO(ffrank): Auto-generated method stub

  }

}
