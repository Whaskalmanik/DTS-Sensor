package com.whaskalmanik.dtssensor.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.whaskalmanik.dtssensor.R;
import com.whaskalmanik.dtssensor.Utils.ExtractedFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class RealTimeFragment extends Fragment {
    public FragmentRealTimeListener listener;
    LineChart chart;
    TextView max;

    ArrayList<ExtractedFile> files = new ArrayList<>();

    public interface FragmentRealTimeListener {
        void onValueSent(float number);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View rootView =inflater.inflate(R.layout.fragment_realtime,container,false);
        chart =rootView.findViewById(R.id.chart);

        if (getArguments() != null)
        {
            files = getArguments().getParcelableArrayList("data");
        }

        createGraph(chart);
        chart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {

            @Override
            public void onValueSelected(Entry e, Highlight h) {
                Log.d("Fragment","Y = "+e.getY() + " X = "+ e.getX());
                listener.onValueSent(e.getX());
            }

            @Override
            public void onNothingSelected() {
            }
        });
        return  rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof FragmentRealTimeListener)
        {
            listener=(FragmentRealTimeListener)context;
        }
        else
        {
            throw new RuntimeException(context.toString() + "Must implement FragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener=null;
    }

    public static RealTimeFragment newInstance(ArrayList<ExtractedFile> files)
    {
        RealTimeFragment fragment = new RealTimeFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList("data",files);
        fragment.setArguments(args);
        return fragment;
    }

    public void createGraph(LineChart chart)
    {
        List<Entry> entries = new ArrayList<>();
        for(int i = 0;i < files.get(6).getLength().size();i++)
        {
            entries.add(new Entry(files.get(0).getLength().get(i),files.get(0).getTemperature().get(i)));
        }
        LineDataSet dataSet = new LineDataSet(entries, "Data");
        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);
        chart.invalidate();
    }
}
