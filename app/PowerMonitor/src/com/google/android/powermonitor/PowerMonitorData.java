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

import java.util.Observable;

/**
 * @author ffrank@google.com (Frank Maker)
 *
 */
public class PowerMonitorData extends Observable{
  // Store raw values
  private static long offset = 0;
  private long timestamp;
  private double current;
  private double voltage;

  public void setTimestamp(long timestamp){
    if(offset == 0)
      offset = timestamp;
    this.timestamp = timestamp - offset;

  }

  public void setCurrent(float current){
    setChanged();
    this.current = current;

  }

  public void setVoltage(float voltage){
    setChanged();
    this.voltage = voltage;

  }

  public float getTimestamp(){
    return timestamp;

  }

  public double getCurrent(){
    return current;

  }

  public double getVoltage(){
    return voltage;

  }

  public double getPower(){
    return current*voltage;

  }
}
