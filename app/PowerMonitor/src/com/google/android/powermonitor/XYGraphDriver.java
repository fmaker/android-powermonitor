/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.android.powermonitor;

import android.util.Log;

import org.achartengine.GraphicalView;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.util.Observable;
import java.util.Observer;

/**
 * @author ffrank@google.com (Frank Maker)
 *
 */
public class XYGraphDriver implements Observer {
  private static final String TAG = "XYGraphDriver";
  
  GraphicalView mChartView;
  XYMultipleSeriesDataset mDataset;
  XYMultipleSeriesRenderer mRenderer;
  
  double gridRange[] = {0.0, 10.0, 0.0, 5.0};
  double panLimits[] = {0, Double.POSITIVE_INFINITY, 0, 5.0};
  final float AXIS_TEXT_SIZE = 20.0F, VALUES_TEXT_SIZE = AXIS_TEXT_SIZE * 0.75F;

  public XYGraphDriver(GraphicalView chartView, XYMultipleSeriesDataset dataset, XYMultipleSeriesRenderer renderer){
    mChartView = chartView;
    mDataset = dataset;
    mRenderer = renderer;

    initChartView();
    initRenderer();
  }
  
  private void initChartView() {
  }

  private void initRenderer(){
    /*mRenderer.setInitialRange(gridRange);
    mRenderer.setPanEnabled(true, false);
    mRenderer.setPanLimits(panLimits);
    mRenderer.setYTitle("Power (mW)");
    mRenderer.setXTitle("Time (s)");
    mRenderer.setChartValuesTextSize(VALUES_TEXT_SIZE);
    mRenderer.setAxisTitleTextSize(AXIS_TEXT_SIZE);*/
  }
  
  
  
  private XYSeries getSeries(String seriesName){
    for(XYSeries s : mDataset.getSeries()){
      if(s.getTitle().equals(seriesName))
        return s;
    }
    return null;
  }
  
  public void add(String seriesName, double x, double y){
    XYSeries s;
    
    if(mDataset.getSeriesCount() == 0){
      mDataset.addSeries(new XYSeries(seriesName));
    }
    if(mRenderer.getSeriesRendererCount() == 0){
      mRenderer.addSeriesRenderer(new XYSeriesRenderer());
    }
    
    s = getSeries(seriesName);
    if(s != null){
      s.add(x, y);
      mChartView.repaint();
    }
  }

  /* (non-Javadoc)
   * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
   */
  @Override
  public void update(Observable observable, Object data) {
    PowerMonitorData d = (PowerMonitorData) observable;
    add("Power",d.getTimestamp(),d.getPower());
  }

  
}
