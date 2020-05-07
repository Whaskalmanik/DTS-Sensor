package com.whaskalmanik.dtssensor.Graph;

import android.content.Context;
import android.graphics.Color;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.whaskalmanik.dtssensor.Preferences.Preferences;
import com.whaskalmanik.dtssensor.Utils.ExtractedFile;
import com.whaskalmanik.dtssensor.Utils.NotificationHelper;

import java.util.ArrayList;
import java.util.List;

public class RealTimeGraph
{
    private LineChart graph;
    private ArrayList<ExtractedFile> data;
    private Preferences markers;
    private Context context;

    private LineDataSet dataSetData;
    private LineDataSet dataSetWarning;
    private LineDataSet dataSetCritical;

    private NotificationHelper notifications;

    private boolean poppedWarning;
    private boolean poppedCritical;

    public RealTimeGraph(LineChart graph, ArrayList<ExtractedFile> data, Context context)
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

        if (yValue>= Preferences.getWarningTemp()&&!poppedWarning)
        {
            notifications.popWarning();
            poppedWarning=true;
        }
        else if (yValue>=Preferences.getCriticalTemp()&&!poppedCritical)
        {
            notifications.popCritical();
            poppedCritical=true;
        }
        else
        {
            return;
        }
    }

    private void fillDataSet()
    {
        poppedCritical=false;
        poppedWarning=false;
        List<Entry> entriesForData = new ArrayList<>();;
        entriesForData.clear();
        for(int i = 0;i < data.get(6).getLength().size();i++)
        {
            entriesForData.add(new Entry(data.get(5).getLength().get(i),data.get(5).getTemperature().get(i)));
            notificationsCheck(data.get(5).getTemperature().get(i));
        }
        dataSetData = new LineDataSet(entriesForData, data.get(5).getDate());
        setStyle(Color.BLUE,dataSetData,2.0f);

    }
    private void fillDataForMarker()
    {
        List<Entry> entries = new ArrayList<>();
        List<Entry> entries2 = new ArrayList<>();

        entries.add(new Entry(0,Preferences.getWarningTemp()));
        entries.add(new Entry(data.get(5).getLength().size()+1,Preferences.getWarningTemp()));
        dataSetWarning = new LineDataSet(entries,"Warning marker");

        entries2.add(new Entry(0,Preferences.getCriticalTemp()));
        entries2.add(new Entry(data.get(5).getLength().size()+1,Preferences.getCriticalTemp()));
        dataSetCritical = new LineDataSet(entries2,"Critical marker");

        setStyle(Color.parseColor("#CAB11B"),dataSetWarning,2.0f);
        setStyle(Color.RED, dataSetCritical,2.0f);
    }

    public void createGraph()
    {
        fillDataSet();
        if(Preferences.areMarkersEnabled())
        {
            fillDataForMarker();
            LineData linedata = new LineData(dataSetData, dataSetCritical, dataSetWarning);
            graph.setData(linedata);
            graph.getDescription().setEnabled(false);
        }
        else
        {
            LineData lineData = new LineData(dataSetData);
            graph.setData(lineData);
        }
        graph.invalidate();
    }
}
