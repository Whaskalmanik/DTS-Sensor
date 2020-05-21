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
import com.whaskalmanik.dtssensor.Files.DocumentsLoader;
import com.whaskalmanik.dtssensor.Graph.Graph;
import com.whaskalmanik.dtssensor.R;
import com.whaskalmanik.dtssensor.Files.ExtractedFile;

import java.util.ArrayList;
import java.util.List;


public class TemperatureFragment extends Fragment {
    private LineChart chart;
    private float value;
    private Graph graph;
    private ArrayList<ExtractedFile> files = new ArrayList<>();
    DocumentsLoader documentsLoader;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View rootView =inflater.inflate(R.layout.fragment_temperature,container,false);
        chart =rootView.findViewById(R.id.chart);

        Bundle arguments = getArguments();
        documentsLoader = new DocumentsLoader(rootView.getContext());

        files = documentsLoader.parseDataFromFiles();
        if (arguments != null)
        {
            value = arguments.getFloat("xValue");
            if(value==Float.MIN_VALUE)
            {
                value=files.get(0).getLength().get(0);
            }
            //files = arguments.getParcelableArrayList("data");
            //files=null;
        }
        graph = new Graph(chart,files,rootView.getContext());
        graph.createTemperatureGraph(value);
        return  rootView;

    }
    @Override
    public void onResume()
    {
        super.onResume();
        if(files!=null)
        {
            graph.createTemperatureGraph(value);
        }
    }

    public static TemperatureFragment newInstance(ArrayList<ExtractedFile> files,float value)
    {
        TemperatureFragment fragment = new TemperatureFragment();
        Bundle args = new Bundle();
        args.putFloat("xValue",value);
        args.putParcelableArrayList("data",files);
        fragment.setArguments(args);
        return fragment;
    }

}

