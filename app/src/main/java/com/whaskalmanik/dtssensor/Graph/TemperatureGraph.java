package com.whaskalmanik.dtssensor.Graph;

import android.content.Context;
import android.graphics.Color;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.whaskalmanik.dtssensor.Files.ExtractedFile;
import com.whaskalmanik.dtssensor.Preferences.Preferences;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class TemperatureGraph {

    private LineChart graph;
    private ArrayList<ExtractedFile> data;

    private LineDataSet dataSetData;
    private LineDataSet dataSetWarning;
    private LineDataSet dataSetCritical;

    public TemperatureGraph(LineChart graph, ArrayList<ExtractedFile> data)
    {
        this.graph = graph;
        this.data = data;
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

    public void createGraph(float value) {

        if (data == null  || data.isEmpty())
        {
            return;
        }
        fillDataSet(value);
        LineData linedata;
        if (Preferences.areMarkersEnabled()) {
            fillDataSetMarker();
            linedata = new LineData(dataSetData, dataSetCritical, dataSetWarning);

        } else {
            linedata = new LineData(dataSetData);
        }
        linedata.setValueFormatter(new ValueFormatter() {
            @Override
            public String getPointLabel(Entry entry) {
                return super.getPointLabel(entry)+" °C";
            }
        });
        linedata.setHighlightEnabled(false);
        graph.setData(linedata);
        DecimalFormat dformat = new DecimalFormat();
        dformat.setMaximumFractionDigits(2);
        graph.getXAxis().setValueFormatter(new ValueFormatter() {
            @Override
            public String getAxisLabel(float value, AxisBase axis) {
                return super.getAxisLabel(roundFloat(value*10,2), axis) +" s";
            }
        });
        graph.getAxisLeft().setValueFormatter(new ValueFormatter() {
            @Override
            public String getAxisLabel(float value, AxisBase axis) {
                return super.getAxisLabel(roundFloat(value,2), axis) +" °C";
            }
        });

        graph.getAxisRight().setDrawLabels(false);
        graph.getDescription().setEnabled(false);
        graph.invalidate();

    }
    private static float roundFloat(float f, int places) {

        BigDecimal bigDecimal = new BigDecimal(Float.toString(f));
        bigDecimal = bigDecimal.setScale(places, RoundingMode.HALF_UP);
        return bigDecimal.floatValue();
    }
}
