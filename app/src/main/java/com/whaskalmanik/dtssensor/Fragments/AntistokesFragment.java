package com.whaskalmanik.dtssensor.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.whaskalmanik.dtssensor.R;
import com.whaskalmanik.dtssensor.Utils.ExtractedFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class AntistokesFragment extends Fragment {
    LineChart chart;
    TextView max;

    ArrayList<ExtractedFile> files = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        final int step = 1;
        int maxi = 0;
        final int min = 1;
        View rootView =inflater.inflate(R.layout.fragment_anitstokes,container,false);

        chart =rootView.findViewById(R.id.temperatureChart);
        SeekBar seekBar = rootView.findViewById(R.id.zoomTemperature);
        max = rootView.findViewById(R.id.maxValueAntistokes);

        if (getArguments() != null)
        {
            files = getArguments().getParcelableArrayList("data");
            maxi=files.get(0).getLength().size();
        }
        seekBar.setMax( (maxi - min) / step );
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                int value = min + (progress * step);
                changeAxis(value);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar)
            {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar)
            {

            }
        });
        createGraph(chart);
        return  rootView;
    }

    private void changeAxis(int progress)
    {
        Random ran = new Random();
        List<Entry> entries = new ArrayList<>();
        for(int i=0;i<files.get(0).getLength().size();i++)
        {
            entries.add(new Entry(files.get(0).getLength().get(i),files.get(0).getTemperature().get(i)));
        }
        setMaxValue(entries);
        LineDataSet dataSet = new LineDataSet(entries, "Data");
        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);
        XAxis axis = chart.getXAxis();
        axis.setAxisMaximum(progress);
        chart.invalidate();
    }
    private void setMaxValue(List<Entry> values)
    {
        float tmp = 0;
        for (int i = 0;i<values.size();i++)
        {
            float expectedMax = values.get(i).getY();
            if(findMax(tmp, expectedMax))
            {
                tmp = expectedMax;
            }
        }
        max.setText(String.valueOf(tmp));
    }

    private boolean findMax(float value, float expectedMax)
    {
        if(expectedMax > value)
        {
            return true;
        }
        return false;
    }

    public static AntistokesFragment newInstance(ArrayList<ExtractedFile> files)
    {
        AntistokesFragment fragment = new AntistokesFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList("data",files);
        fragment.setArguments(args);
        return fragment;
    }
    public void createGraph(LineChart chart)
    {
        Random ran = new Random();
        List<Entry> entries = new ArrayList<>();
        for(int i = 0;i < files.get(0).getLength().size();i++)
        {
            entries.add(new Entry(files.get(0).getLength().get(i),files.get(0).getTemperature().get(i)));
        }
        setMaxValue(entries);
        LineDataSet dataSet = new LineDataSet(entries, "Data");
        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);
        chart.invalidate();
    }
}
