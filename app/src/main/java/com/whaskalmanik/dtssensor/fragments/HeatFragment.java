package com.whaskalmanik.dtssensor.fragments;


import android.os.Bundle;

import android.preference.Preference;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.whaskalmanik.dtssensor.files.DocumentsLoader;
import com.whaskalmanik.dtssensor.files.ExtractedFile;
import com.whaskalmanik.dtssensor.graph.HeatGraph;
import com.whaskalmanik.dtssensor.preferences.Preferences;
import com.whaskalmanik.dtssensor.R;
import com.whaskalmanik.dtssensor.utils.Utils;

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
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_heat, container, false);
        findViewElements(rootView);
        DocumentsLoader documentsLoader = new DocumentsLoader(rootView.getContext());
        data = documentsLoader.parseDataFromFiles();

        if(!Utils.isDataValid(data))
        {
            Toast.makeText(getContext(), "Graph offset is out of range", Toast.LENGTH_SHORT).show();
        }

        HeatGraph graph = new HeatGraph(data, heatGraph, bar);
        graph.createGraph();
        setTextView();
        return rootView;
    }

    public void findViewElements(View rootView) {
        heatGraph = rootView.findViewById(R.id.imageView);
        lengthStart = rootView.findViewById(R.id.lengthStart);
        lengthEnd = rootView.findViewById(R.id.lengthEnd);
        tempMin = rootView.findViewById(R.id.minTmp);
        tempMax = rootView.findViewById(R.id.maxTmp);
        timeEnd = rootView.findViewById(R.id.timeEnd);
        timeStart = rootView.findViewById(R.id.timeStart);
        paddingText = rootView.findViewById(R.id.paddingText);
        middleTemperature = rootView.findViewById(R.id.middleTmp);
        bar = rootView.findViewById(R.id.imageBar);
    }
    private void setTextView() {
        String degrees = getResources().getString(R.string.degree_format);
        String meters = getResources().getString(R.string.metres_format);
        String seconds = getResources().getString(R.string.seconds_format);

        int startingTime = 0;
        int endingTime = 0;
        int heat_max = 0;
        int heat_min = 0;
        int midPoint = 0;
        float minLength = 0;
        float maxLength = 0;

        if(Utils.isDataValid(data)) {
            endingTime = startingTime + data.size() * 10;
            if(Preferences.isDataOverrided())
            {
                heat_max = Preferences.getHeatMax();
                heat_min = Preferences.getHeatMin();
            }
            else {
                heat_max = (int) Utils.getDataMaxTemperature(data);
                heat_min = (int) Utils.getDataMinTemperature(data);
            }
            midPoint = (heat_max+heat_min) / 2;
            minLength = data.get(0).getMinimumLength();
            maxLength = data.get(0).getMaximumLength();

        }
        lengthStart.setText(String.format(meters,minLength));
        lengthEnd.setText(String.format(meters,maxLength));

        tempMin.setText(String.format(degrees,heat_min));
        tempMax.setText(String.format(degrees,heat_max));

        timeStart.setText(String.format(seconds,startingTime));
        timeEnd.setText(String.format(seconds,endingTime));

        paddingText.setText(String.format(seconds,endingTime));
        middleTemperature.setText(String.format(degrees,midPoint));
    }


    public static HeatFragment newInstance()
    {
        return new HeatFragment();
    }
}