package com.whaskalmanik.dtssensor.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.anychart.anychart.AnyChart;
import com.anychart.anychart.AnyChartView;
import com.anychart.anychart.DataEntry;
import com.anychart.anychart.HeatDataEntry;
import com.anychart.anychart.HeatMap;
import com.anychart.anychart.Interactivity;
import com.anychart.anychart.OrdinalColor;
import com.anychart.anychart.SelectionMode;
import com.anychart.anychart.SolidFill;
import com.whaskalmanik.dtssensor.Files.DocumentsLoader;
import com.whaskalmanik.dtssensor.Files.ExtractedFile;
import com.whaskalmanik.dtssensor.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HeatFragment extends Fragment {
    private ArrayList<ExtractedFile> files = new ArrayList<>();
    String[] colors= new String[]{"#ff6666", "#ff5544", "#ff4422"};
    DocumentsLoader documentsLoader;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_heat, container, false);

        //DocumentsLoader documentsLoader = new DocumentsLoader(rootView.getContext());
        //files = documentsLoader.parseDataFromFiles();

        Bundle arguments = getArguments();
        documentsLoader = new DocumentsLoader(rootView.getContext());

        files = documentsLoader.parseDataFromFiles();
        if (arguments != null)
        {
        }
        AnyChartView anyChartView = rootView.findViewById(R.id.charHeat);
        anyChartView.setProgressBar(rootView.findViewById(R.id.progress_bar));
        HeatMap riskMap = AnyChart.heatMap();
        riskMap.setStroke("0 #fff");
        riskMap.setFill((new SolidFill("#545f69", 1d)));
        List<DataEntry> data = new ArrayList<>();



        for(int i=0;i<files.size();i++)
        {
            List<ExtractedFile.Entry> entries = files.get(i).getEntries();
            for(int j=0;j<entries.size();j++)
            {
                data.add(new CustomHeatDataEntry(new Float(entries.get(j).getLength()).toString(),
                            files.get(i).getTime(),
                            Math.round((float)entries.get(j).getTemp()),
                            getColorString(Math.round((float)entries.get(j).getTemp()))));
            }
        }

        riskMap.setData(data);
        riskMap.draw(true);
        anyChartView.setChart(riskMap);
        return rootView;
    }
    private String getColorString(int temp)
    {
        if(temp < 25)
        {
            return "#90caf9";
        }
        else if(temp < 50)
        {
            return "#ffb74d";
        }
        else if(temp <75)
        {
            return "#ef6c00";
        }
        else if(temp<101)
        {
            return "#d84315";
        }
        return "#d84315";
    }
    public static HeatFragment newInstance(ArrayList<ExtractedFile> files)
    {
        HeatFragment fragment = new HeatFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList("data",files);
        fragment.setArguments(args);
        return fragment;
    }

    private class CustomHeatDataEntry extends HeatDataEntry {
        CustomHeatDataEntry(String x, String y, Integer heat, String fill) {
            super(x, y, heat);
            setValue("fill", fill);
        }
    }
}