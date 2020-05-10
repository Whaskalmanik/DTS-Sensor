package com.whaskalmanik.dtssensor.Graph;

import android.content.Context;
import android.graphics.Color;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.whaskalmanik.dtssensor.Preferences.Preferences;
import com.whaskalmanik.dtssensor.Files.ExtractedFile;
import com.whaskalmanik.dtssensor.Utils.NotificationHelper;

import java.util.ArrayList;
import java.util.List;

public class Graph
{
    private LineChart graph;
    private ArrayList<ExtractedFile> data;
    private Preferences markers;
    private Context context;


    private LineDataSet dataSetData;
    private LineDataSet dataSetWarning;
    private LineDataSet dataSetCritical;

    private NotificationHelper notifications;


    public Graph(LineChart graph, ArrayList<ExtractedFile> data, Context context)
    {
        this.graph = graph;
        this.data = data;
        this.context = context;
        notifications = new NotificationHelper(context);

    }

    private void setStyle(int color,LineDataSet dataSet,float lineWidth)
    {
        dataSet.setColor(color);
        dataSet.setCircleColor(color);
        dataSet.setLineWidth(lineWidth);
        dataSet.setDrawCircles(false);
    }
    private void notificationsCheck(float yValue)
    {
        if(Preferences.areMarkersEnabled())
        {
            if (yValue>= Preferences.getWarningTemp())
            {
                notifications.popWarning();
            }
            else if (yValue>=Preferences.getCriticalTemp())
            {
                notifications.popCritical();
            }

        }
    }

    private void fillDataSet(int selectedIndex)
    {
        List<Entry> entriesForData = new ArrayList<>();;
        entriesForData.clear();

        for(int i = 0;i < data.get(selectedIndex).getLength().size();i++)
        {
            entriesForData.add(new Entry(data.get(selectedIndex).getLength().get(i),data.get(selectedIndex).getTemperature().get(i)));
            notificationsCheck(data.get(0).getTemperature().get(i));
        }
        dataSetData = new LineDataSet(entriesForData, data.get(selectedIndex).getDate().toString());
        setStyle(Color.BLUE,dataSetData,2.0f);
    }

    private void fillDataForMarker(int selectedIndex)
    {
        List<Entry> entries = new ArrayList<>();
        List<Entry> entries2 = new ArrayList<>();

        entries.add(new Entry(0,Preferences.getWarningTemp()));
        entries.add(new Entry(data.get(selectedIndex).getLength().size(),Preferences.getWarningTemp()));
        dataSetWarning = new LineDataSet(entries,"Warning marker");

        entries2.add(new Entry(0,Preferences.getCriticalTemp()));
        entries2.add(new Entry(data.get(selectedIndex).getTemperature().size(),Preferences.getCriticalTemp()));
        dataSetCritical = new LineDataSet(entries2,"Critical marker");

        setStyle(Color.parseColor("#CAB11B"),dataSetWarning,2.0f);
        setStyle(Color.RED, dataSetCritical,2.0f);
    }

    public void createRealTimeGraph(int selectedIndex)
    {
        if(data!=null) {
            fillDataSet(selectedIndex);
            if (Preferences.areMarkersEnabled()) {
                fillDataForMarker(selectedIndex);
                LineData linedata = new LineData(dataSetData, dataSetCritical, dataSetWarning);
                graph.setData(linedata);
                graph.getDescription().setEnabled(false);
            } else {
                LineData lineData = new LineData(dataSetData);
                graph.setData(lineData);
            }
            graph.invalidate();
        }
        else
        {
            Toast.makeText(context,"Data null",Toast.LENGTH_LONG);
        }
    }

    private void fillDataSetTemperatures(float value)
    {
        List<Entry> entries = new ArrayList<>();
        for(int i = 0;i < data.size();i++)
        {
            for(int j =0;j<data.get(i).getLength().size();j++)
            {
                if(data.get(i).getLength().get(j)==value)
                {
                    entries.add(new Entry(i,data.get(i).getTemperature().get(j)));

                }
            }
        }
        dataSetData = new LineDataSet(entries, data.get(0).getDate());
        setStyle(Color.BLUE,dataSetData,2.0f);
    }

    private void fillDataSetTemperaturesMarker()
    {
        List<Entry> entries = new ArrayList<>();
        List<Entry> entries2 = new ArrayList<>();
        entries.add(new Entry(0,Preferences.getWarningTemp()));
        entries.add(new Entry(data.size()-1,Preferences.getWarningTemp()));
        dataSetWarning = new LineDataSet(entries,"Warning marker");

        entries2.add(new Entry(0,Preferences.getCriticalTemp()));
        entries2.add(new Entry(data.size()-1,Preferences.getCriticalTemp()));
        dataSetCritical = new LineDataSet(entries2,"Critical marker");

        setStyle(Color.parseColor("#CAB11B"),dataSetWarning,2.0f);
        setStyle(Color.RED, dataSetCritical,2.0f);
    }

    public void createTemperatureGraph(float value)
    {
        if(data!=null)
        {
            fillDataSetTemperatures(value);
            LineData linedata;
            if(Preferences.areMarkersEnabled())
            {
                fillDataSetTemperaturesMarker();
                linedata = new LineData(dataSetData, dataSetCritical, dataSetWarning);
            }
            else
            {
                linedata= new LineData(dataSetData);

            }
            graph.setData(linedata);
            graph.getDescription().setEnabled(false);
            graph.invalidate();
        }
    }
}
