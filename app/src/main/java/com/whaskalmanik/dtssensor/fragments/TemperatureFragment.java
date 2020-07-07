package com.whaskalmanik.dtssensor.fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.whaskalmanik.dtssensor.files.DocumentsLoader;
import com.whaskalmanik.dtssensor.graph.TemperatureGraph;
import com.whaskalmanik.dtssensor.R;
import com.whaskalmanik.dtssensor.files.ExtractedFile;
import com.whaskalmanik.dtssensor.utils.Utils;

import java.util.ArrayList;


public class TemperatureFragment extends Fragment {
    static private float value;
    private TemperatureGraph graph;
    private ArrayList<ExtractedFile> data = new ArrayList<>();

    private TextView selected;
    private TextView minValue;
    private TextView maxValue;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView =inflater.inflate(R.layout.fragment_temperature,container,false);
        LineChart chart = rootView.findViewById(R.id.chart);

        selected = rootView.findViewById(R.id.selectedTemperatureTemp);
        maxValue = rootView.findViewById(R.id.maxValueTemp);
        minValue = rootView.findViewById(R.id.minValueTemp);

        DocumentsLoader documentsLoader = new DocumentsLoader(rootView.getContext());

        data = documentsLoader.parseDataFromFiles();

        loadArguments();
        graph = new TemperatureGraph(chart, data);
        graph.createGraph(value);
        setInformation();
        return  rootView;

    }
    @Override
    public void onResume() {
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
        if (arguments != null) {
            value = arguments.getFloat("xValue");
        }

        if(value==Float.MIN_VALUE) {
            value=data.get(0).getLength().get(0);
        }
    }

    private void setInformation() {
        if(!Utils.isDataValid(data)) {
            selected.setVisibility(View.GONE);
            maxValue.setVisibility(View.GONE);
            minValue.setVisibility(View.GONE);
            Toast.makeText(getContext(), "Graph offset is out of range", Toast.LENGTH_SHORT).show();
            return;
        }

        selected.setText("Length: " + value + " m ");

        int lengthIndex = data.get(0).getLength().indexOf(value);

        double maxTemp = data.stream().map(x -> x.getData().get(lengthIndex).getTemp()).max(Double::compare).get();
        double minTemp = data.stream().map(x -> x.getData().get(lengthIndex).getTemp()).min(Double::compare).get();

        minValue.setText("Min: "+ minTemp + " °C");
        maxValue.setText("Max: "+ maxTemp + " °C");
    }

    public static TemperatureFragment newInstance(float value) {
        TemperatureFragment fragment = new TemperatureFragment();
        Bundle args = new Bundle();
        args.putFloat("xValue",value);
        fragment.setArguments(args);
        return fragment;
    }

}