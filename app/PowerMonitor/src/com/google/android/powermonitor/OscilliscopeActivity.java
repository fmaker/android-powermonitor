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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.tools.FitZoom;

public class OscilliscopeActivity extends Activity implements OnClickListener {
    private static boolean mSimulate = false;

    CheckBox mPowerCheckBox, mVoltageCheckBox, mCurrentCheckBox;

    Intent mDataServiceIntent;
    XYGraphDriver mXyGraphDriver;
    private GraphicalView mChartView;
    private XYMultipleSeriesDataset mDataset = new XYMultipleSeriesDataset();
    private XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.oscope);

        // Get view refs
        mPowerCheckBox = (CheckBox) findViewById(R.id.powerCheckBox);
        mVoltageCheckBox = (CheckBox) findViewById(R.id.voltageCheckBox);
        mCurrentCheckBox = (CheckBox) findViewById(R.id.currentCheckBox);

        // Fix checkbox padding
        final float LEFT_CB_PADDING = 10.0F;
        final CheckBox[] mCheckBoxes = {mPowerCheckBox, mVoltageCheckBox,
                                  mCurrentCheckBox};
        final float scale = getResources().getDisplayMetrics().density;
        for(CheckBox cb : mCheckBoxes){
          cb.setPadding(cb.getPaddingLeft() + (int)(LEFT_CB_PADDING * scale),
            cb.getPaddingTop(),
            cb.getPaddingRight(),
            cb.getPaddingBottom());
        }

        // By default only show power
        mPowerCheckBox.setChecked(true);
        initButtons();
        initGraph();
        DataService.addPowerDataObserver(mXyGraphDriver);

        mDataServiceIntent = new Intent(this,DataService.class);

        // Start data service if simulating
        if(mSimulate){
          mDataServiceIntent.putExtra(DataService.SIMULATE, mSimulate);
          startService(mDataServiceIntent);
        }
    }

    private void initGraph(){
      LinearLayout layout = (LinearLayout) findViewById(R.id.graphLayout);
      mChartView = ChartFactory.getLineChartView(this, mDataset, mRenderer);
      mXyGraphDriver = new XYGraphDriver(mChartView, mDataset, mRenderer);
      layout.addView(mChartView, new LayoutParams(LayoutParams.FILL_PARENT,
        LayoutParams.FILL_PARENT));
    }

    private void initButtons(){
      Button mSDCardDumpButton;

      mSDCardDumpButton = (Button) findViewById(R.id.dumpSDButton);
      mSDCardDumpButton.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
      super.onResume();
    }

    @Override
    protected void onPause() {
      super.onPause();
    }

    @Override
    protected void onDestroy() {
      stopService(mDataServiceIntent);
      super.onDestroy();
    }

    @Override
    public void onClick(View v) {
      switch(v.getId()){
        case R.id.dumpSDButton:
          break;
      }
    }


}
