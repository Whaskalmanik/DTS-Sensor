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
import com.whaskalmanik.dtssensor.Files.DocumentsLoader;
import com.whaskalmanik.dtssensor.Graph.TemperatureGraph;
import com.whaskalmanik.dtssensor.R;
import com.whaskalmanik.dtssensor.Files.ExtractedFile;

import java.util.ArrayList;


public class TemperatureFragment extends Fragment {
    private float value;
    private TemperatureGraph graph;
    private ArrayList<ExtractedFile> data = new ArrayList<>();
    DocumentsLoader documentsLoader;

    TextView selected;
    TextView minValue;
    TextView maxValue;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View rootView =inflater.inflate(R.layout.fragment_temperature,container,false);
        LineChart chart = rootView.findViewById(R.id.chart);

        selected = rootView.findViewById(R.id.selectedTemperatureTemp);
        maxValue = rootView.findViewById(R.id.maxValueTemp);
        minValue = rootView.findViewById(R.id.minValueTemp);


        documentsLoader = new DocumentsLoader(rootView.getContext());

        data = documentsLoader.parseDataFromFiles();

        loadArguments();
        graph = new TemperatureGraph(chart, data);
        graph.createGraph(value);
        setInformation();
        return  rootView;

    }
    @Override
    public void onResume()
    {
        super.onResume();
        if (data == null || data.isEmpty()||data.get(0).getEntries().isEmpty()) {
            return;
        }
        graph.createGraph(value);
    }

    private void loadArguments()
    {
        if (data == null || data.isEmpty()||data.get(0).getEntries().isEmpty()) {
            return;
        }
        Bundle arguments = getArguments();
        if (arguments != null)
        {
            value = arguments.getFloat("xValue");
        }

        if(value==Float.MIN_VALUE)
        {
            value=data.get(0).getLength().get(0);
        }
    }

    private void setInformation()
    {
        if (data == null || data.isEmpty() || data.get(0).getEntries().isEmpty()) {
            selected.setText("Length: " + 0 + " m ");
            minValue.setText("Min: "+ 0 + " °C");
            maxValue.setText("Max: "+ 0 + " °C");

            return;
        }

        selected.setText("Length: " + value + " m ");

        int lengthIndex = data.get(0).getLength().indexOf(value);

        double maxTemp = data.stream().map(x -> x.getData().get(lengthIndex).getTemp()).max(Double::compare).get();
        double minTemp = data.stream().map(x -> x.getData().get(lengthIndex).getTemp()).min(Double::compare).get();

        minValue.setText("Min: "+ minTemp + " °C");
        maxValue.setText("Max: "+ maxTemp + " °C");

    }

    public static TemperatureFragment newInstance(float value)
    {
        TemperatureFragment fragment = new TemperatureFragment();
        Bundle args = new Bundle();
        args.putFloat("xValue",value);
        fragment.setArguments(args);
        return fragment;
    }

}