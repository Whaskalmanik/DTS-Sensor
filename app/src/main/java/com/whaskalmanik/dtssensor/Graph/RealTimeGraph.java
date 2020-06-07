package com.whaskalmanik.dtssensor.Graph;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.ChartHighlighter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.whaskalmanik.dtssensor.Preferences.Preferences;
import com.whaskalmanik.dtssensor.Files.ExtractedFile;
import com.whaskalmanik.dtssensor.R;
import com.whaskalmanik.dtssensor.Utils.NotificationHelper;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class RealTimeGraph
{
    private LineChart graph;
    private ArrayList<ExtractedFile> data;
    private Context context;


    private LineDataSet dataSetData;
    private LineDataSet dataSetWarning;
    private LineDataSet dataSetCritical;

    private NotificationHelper notifications;
    boolean popped=false;


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
        if(Preferences.areMarkersEnabled())
        {
            if (!popped&&yValue >= Preferences.getWarningTemp())
            {
                notifications.popWarning();
                popped=true;
            }
            else if (!popped&&yValue >= Preferences.getCriticalTemp())
            {
                notifications.popCritical();
                popped=true;
            }
        }
    }

    private void fillDataSet(int selectedIndex)
    {
        List<Entry> entriesForData = new ArrayList<>();;
        entriesForData.clear();

        if(data != null && !data.isEmpty())
        {
            List<Float> lengths = data.get(selectedIndex).getLength();
            List<Float> temperatures = data.get(selectedIndex).getTemperature();
            for(int i = 0;i < lengths.size() ;i++)
            {
                entriesForData.add(new Entry(lengths.get(i),temperatures.get(i)));
                notificationsCheck(temperatures.get(i));
            }
            dataSetData = new LineDataSet(entriesForData, data.get(selectedIndex).getDate());
            dataSetData.setHighlightLineWidth(1.5f);
            dataSetData.setHighLightColor(context.getResources().getColor(R.color.colorYellow));
            setStyle(Color.BLUE,dataSetData,2.0f);
        }
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

    public void createGraph(int selectedIndex)
    {
        if (data==null)
        {
            Log.d("RealTimeGraph:" ,"Data are null when graph is being created!");
            return;
        }
        fillDataSet(selectedIndex);
        if (Preferences.areMarkersEnabled()) {
            fillDataForMarker(selectedIndex);
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
                return super.getAxisLabel(roundFloat(value,2), axis) +" °C";
            }
        });
        graph.getAxisRight().setDrawLabels(false);
        graph.invalidate();

    }
    private static float roundFloat(float f, int places) {

        BigDecimal bigDecimal = new BigDecimal(Float.toString(f));
        bigDecimal = bigDecimal.setScale(places, RoundingMode.HALF_UP);
        return bigDecimal.floatValue();
    }


    public void highlightValue(float value)
    {
        graph.highlightValue(value,0,true);
    }
}

