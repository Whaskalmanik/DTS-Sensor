package com.whaskalmanik.dtssensor.graph;

import android.content.Context;
import android.graphics.Color;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.whaskalmanik.dtssensor.preferences.Preferences;
import com.whaskalmanik.dtssensor.files.ExtractedFile;
import com.whaskalmanik.dtssensor.utils.NotificationHelper;
import com.whaskalmanik.dtssensor.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class RealTimeGraph
{
    private LineChart graph;
    private ArrayList<ExtractedFile> data;

    private LineDataSet dataSetData;
    private LineDataSet dataSetWarning;
    private LineDataSet dataSetCritical;

    private NotificationHelper notifications;
    private boolean popped = false;


    public RealTimeGraph(LineChart graph, ArrayList<ExtractedFile> data, Context context) {
        this.graph = graph;
        this.data = data;
        notifications = new NotificationHelper(context);
    }

    private void setStyle(int color, LineDataSet dataSet) {
        dataSet.setColor(color);
        dataSet.setCircleColor(color);
        dataSet.setLineWidth(2.0f);
        dataSet.setDrawCircles(false);
    }

    private void notificationsCheck(float length,float temp) {
        if(!Preferences.areMarkersEnabled() || popped) {
            return;
        }

        if (temp>= Preferences.getWarningTemp()) {
            notifications.popWarning(temp,length);
            popped = true;
        }

        else if (temp >= Preferences.getCriticalTemp()) {
            notifications.popCritical(temp,length);
            popped = true;
        }
    }

    private void fillDataSet(int selectedIndex) {
        List<Entry> entriesForData = new ArrayList<>();
        entriesForData.clear();

        if(!Utils.isDataValid(data)) {
            return;
        }
        List<Float> lengths = data.get(selectedIndex).getLength();
        List<Float> temperatures = data.get(selectedIndex).getTemperature();
        for(int i = 0;i < lengths.size() ;i++) {
            float length = lengths.get(i);
            float temperature = temperatures.get(i);
            entriesForData.add(new Entry(length,temperature));
            notificationsCheck(length,temperature);
        }
        dataSetData = new LineDataSet(entriesForData, data.get(selectedIndex).getDate());
        dataSetData.setHighlightLineWidth(1.5f);
        dataSetData.setHighLightColor(Color.parseColor("#F05837"));
        setStyle(Color.BLUE,dataSetData);
    }

    private void fillDataForMarker() {
        List<Entry> entries = new ArrayList<>();
        List<Entry> entries2 = new ArrayList<>();

        entries.add(new Entry(data.get(0).getMinimumLength(),Preferences.getWarningTemp()));
        entries.add(new Entry(data.get(0).getMaximumLength(),Preferences.getWarningTemp()));
        dataSetWarning = new LineDataSet(entries,"Warning marker");

        entries2.add(new Entry(data.get(0).getMinimumLength(),Preferences.getCriticalTemp()));
        entries2.add(new Entry(data.get(0).getMaximumLength(),Preferences.getCriticalTemp()));
        dataSetCritical = new LineDataSet(entries2,"Critical marker");

        setStyle(Color.parseColor("#CAB11B"),dataSetWarning);
        setStyle(Color.RED, dataSetCritical);
    }

    public void createGraph(int selectedIndex) {
        if(data==null||data.isEmpty()||data.get(0).getEntries().isEmpty()) {
            return;
        }
        fillDataSet(selectedIndex);
        if (Preferences.areMarkersEnabled()) {
            fillDataForMarker();
            LineData linedata = new LineData(dataSetData, dataSetCritical, dataSetWarning);
            graph.setData(linedata);
        } else {
            LineData lineData = new LineData(dataSetData);
            lineData.setValueFormatter(new ValueFormatter() {
                @Override
                public String getPointLabel(Entry entry) {
                    return super.getPointLabel(entry)+" °C";
                }
            });
            graph.setData(lineData);
        }
        graph.getDescription().setEnabled(false);
        graph.getXAxis().setValueFormatter(new ValueFormatter() {
            @Override
            public String getAxisLabel(float value, AxisBase axis) {
                return super.getAxisLabel(value, axis) +" m";
            }
        });
        graph.getAxisLeft().setValueFormatter(new ValueFormatter() {
            @Override
            public String getAxisLabel(float value, AxisBase axis) {
                return super.getAxisLabel(Utils.roundFloat(value,2), axis) +" °C";
            }
        });
        graph.getAxisRight().setDrawLabels(false);
        graph.invalidate();

    }

    public void highlightValue(float value)
    {
        graph.highlightValue(value,0,true);
    }
}

