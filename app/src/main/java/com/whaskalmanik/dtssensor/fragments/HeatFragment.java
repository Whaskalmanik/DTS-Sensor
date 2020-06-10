package com.whaskalmanik.dtssensor.fragments;


import android.os.Bundle;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.whaskalmanik.dtssensor.files.DocumentsLoader;
import com.whaskalmanik.dtssensor.files.ExtractedFile;
import com.whaskalmanik.dtssensor.graph.HeatGraph;
import com.whaskalmanik.dtssensor.preferences.Preferences;
import com.whaskalmanik.dtssensor.R;

import java.util.ArrayList;

public class HeatFragment extends Fragment {
    private ArrayList<ExtractedFile> data = new ArrayList<>();

    private ImageView heatGraph;
    private TextView lengthStart;
    private TextView lengthEnd;
    private TextView tempMin;
    private TextView tempMax;
    private TextView timeStart;
    private TextView timeEnd;
    private TextView paddingText;
    private ImageView bar;
    private TextView middleTemperature;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_heat, container, false);
        findViewElements(rootView);
        DocumentsLoader documentsLoader = new DocumentsLoader(rootView.getContext());
        data = documentsLoader.parseDataFromFiles();

        HeatGraph graph= new HeatGraph(data,heatGraph,bar);
        graph.createGraph();
        setTextView();
        return rootView;
    }

    public void findViewElements(View rootView)
    {
        heatGraph = rootView.findViewById(R.id.imageView);
        lengthStart = rootView.findViewById(R.id.lenghtStart);
        lengthEnd = rootView.findViewById(R.id.lenghtEnd);
        tempMin = rootView.findViewById(R.id.minTmp);
        tempMax = rootView.findViewById(R.id.maxTmp);
        timeEnd = rootView.findViewById(R.id.timeEnd);
        timeStart = rootView.findViewById(R.id.timeStart);
        paddingText = rootView.findViewById(R.id.paddingText);
        middleTemperature = rootView.findViewById(R.id.middleTmp);
        bar = rootView.findViewById(R.id.imageBar);
    }
    private void setTextView()
    {
        int startingTime = 0;
        int endingTime = startingTime+data.size()*10;
        int heat_max = Preferences.getHeatMax();
        int heat_min = Preferences.getHeatMin();
        int midPoint=(heat_max+heat_min)/2;

        tempMin.setText(heat_min +" °C");
        tempMax.setText(heat_max +" °C");

        lengthEnd.setText(data.get(0).getMaximumLength()+" m");
        lengthStart.setText(data.get(0).getMinimumLength()+" m");

        timeEnd.setText(endingTime + " s");
        timeStart.setText(startingTime +" s");

        paddingText.setText(endingTime+" s");
        middleTemperature.setText(midPoint+" °C");
    }


    public static HeatFragment newInstance()
    {
        return new HeatFragment();
    }
}