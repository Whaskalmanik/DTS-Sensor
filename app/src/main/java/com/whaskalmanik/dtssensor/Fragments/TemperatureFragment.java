package com.whaskalmanik.dtssensor.Fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.whaskalmanik.dtssensor.R;
import com.whaskalmanik.dtssensor.Files.ExtractedFile;

import java.util.ArrayList;
import java.util.List;


public class TemperatureFragment extends Fragment {
    LineChart chart;
    float value;
    ArrayList<ExtractedFile> files = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View rootView =inflater.inflate(R.layout.fragment_temperature,container,false);
        chart =rootView.findViewById(R.id.chart);

        if (getArguments() != null)
        {
            files = getArguments().getParcelableArrayList("data");
            value = getArguments().getFloat("xValue");
        }
        createGraph();
        return  rootView;


    }

    public static TemperatureFragment newInstance(ArrayList<ExtractedFile> files,float value)
    {
        TemperatureFragment fragment = new TemperatureFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList("data",files);
        args.putFloat("xValue",value);
        fragment.setArguments(args);
        return fragment;
    }

    public void createGraph()
    {
        if(files!=null)
        {
            List<Entry> entries = new ArrayList<>();
            for(int i = 0;i < files.size();i++)
            {
                for(int j =0;j<files.get(i).getLength().size();j++)
                {
                    if(files.get(i).getLength().get(j)==value)
                    {
                        entries.add(new Entry(i,files.get(i).getTemperature().get(j)));
                    }
                }
            }
            LineDataSet dataSet = new LineDataSet(entries, "Data");
            LineData lineData = new LineData(dataSet);
            chart.setData(lineData);
            chart.invalidate();
        }
    }
}

