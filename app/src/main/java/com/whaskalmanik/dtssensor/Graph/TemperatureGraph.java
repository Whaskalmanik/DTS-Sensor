package com.whaskalmanik.dtssensor.Graph;

import android.content.Context;
import android.graphics.Color;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.whaskalmanik.dtssensor.Files.ExtractedFile;
import com.whaskalmanik.dtssensor.Preferences.Preferences;

import java.util.ArrayList;
import java.util.List;

public class TemperatureGraph {

    private LineChart graph;
    private ArrayList<ExtractedFile> data;
    private Preferences markers;
    private Context context;

    private LineDataSet dataSetData;
    private LineDataSet dataSetWarning;
    private LineDataSet dataSetCritical;

    public TemperatureGraph(LineChart graph, ArrayList<ExtractedFile> data, Context context)
    {
        this.graph = graph;
        this.data = data;
        this.context = context;
    }

    private void setStyle(int color, LineDataSet dataSet)
    {
        dataSet.setColor(color);
        dataSet.setCircleColor(color);
        dataSet.setLineWidth((float) 2.0);
        dataSet.setDrawCircles(false);
    }
    private void fillDataSet(float value)
    {
        if (!Float.isNaN(value) && data != null && !data.isEmpty())
        {
            List<Entry> entries = new ArrayList<>();
            int lengthIndex = data.get(0).getLength().indexOf(value);
            for(int i = 0;i < data.size();i++)
            {
                entries.add(new Entry(i, (float)data.get(i).getEntries().get(lengthIndex).getTemp()));
            }
            dataSetData = new LineDataSet(entries, data.get(0).getDate());
            dataSetData.setHighlightEnabled(false);
            setStyle(Color.BLUE,dataSetData);
        }
    }

    private void fillDataSetMarker()
    {
        List<Entry> entries = new ArrayList<>();
        List<Entry> entries2 = new ArrayList<>();
        entries.add(new Entry(0,Preferences.getWarningTemp()));
        entries.add(new Entry(data.size()-1,Preferences.getWarningTemp()));
        dataSetWarning = new LineDataSet(entries,"Warning marker");

        entries2.add(new Entry(0,Preferences.getCriticalTemp()));
        entries2.add(new Entry(data.size()-1,Preferences.getCriticalTemp()));
        dataSetCritical = new LineDataSet(entries2,"Critical marker");

        setStyle(Color.parseColor("#CAB11B"),dataSetWarning);
        setStyle(Color.RED, dataSetCritical);
    }

    public void createGraph(float value)
    {
        if(data!=null)
        {
            fillDataSet(value);
            LineData linedata;
            if(Preferences.areMarkersEnabled())
            {
                fillDataSetMarker();
                linedata = new LineData(dataSetData, dataSetCritical, dataSetWarning);

            }
            else
            {
                linedata= new LineData(dataSetData);
            }
            linedata.setHighlightEnabled(true);
            graph.setData(linedata);
            graph.getDescription().setEnabled(false);
            graph.invalidate();
        }
    }
}
