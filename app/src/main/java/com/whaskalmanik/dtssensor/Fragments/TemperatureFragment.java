package com.whaskalmanik.dtssensor.Fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.whaskalmanik.dtssensor.Files.DocumentsLoader;
import com.whaskalmanik.dtssensor.Graph.Graph;
import com.whaskalmanik.dtssensor.Graph.TemperatureGraph;
import com.whaskalmanik.dtssensor.R;
import com.whaskalmanik.dtssensor.Files.ExtractedFile;

import java.util.ArrayList;
import java.util.List;


public class TemperatureFragment extends Fragment {
    private LineChart chart;
    private float value;
    private TemperatureGraph graph;
    private int index;
    private ArrayList<ExtractedFile> files = new ArrayList<>();
    DocumentsLoader documentsLoader;

    TextView selected;
    TextView minValue;
    TextView maxValue;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View rootView =inflater.inflate(R.layout.fragment_temperature,container,false);
        chart = rootView.findViewById(R.id.chart);

        selected = rootView.findViewById(R.id.selectedTemperatureTemp);
        maxValue = rootView.findViewById(R.id.maxValueTemp);
        minValue = rootView.findViewById(R.id.minValueTemp);

        Bundle arguments = getArguments();
        documentsLoader = new DocumentsLoader(rootView.getContext());

        files = documentsLoader.parseDataFromFiles();
        if (arguments != null)
        {
            value = arguments.getFloat("xValue");
            index = arguments.getInt("index");
            if(value==Float.MIN_VALUE)
            {
                value=files.get(0).getLength().get(0);
            }
            //files = arguments.getParcelableArrayList("data");
            //files = null;
        }
        graph = new TemperatureGraph(chart,files,rootView.getContext());
        graph.createGraph(value);
        setInformation();
        return  rootView;

    }
    @Override
    public void onResume()
    {
        super.onResume();
        if(files!=null)
        {
            graph.createGraph(value);
        }
    }

    private void setInformation()
    {
        selected.setText("Selected: " + value+ " m ");
    }


    public static TemperatureFragment newInstance(float value,int index)
    {
        TemperatureFragment fragment = new TemperatureFragment();
        Bundle args = new Bundle();
        args.putFloat("xValue",value);
        args.putInt("index",index);
        fragment.setArguments(args);
        return fragment;
    }

}