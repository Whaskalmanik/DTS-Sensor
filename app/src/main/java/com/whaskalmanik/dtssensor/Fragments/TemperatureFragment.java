package com.whaskalmanik.dtssensor.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.whaskalmanik.dtssensor.R;


public class TemperatureFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView =inflater.inflate(R.layout.fragment_temperature,container,false);
        //GraphView graph=rootView.findViewById(R.id.graphTemperature);
        if (getArguments() != null) {
            double[] delka= getArguments().getDoubleArray("delka");
            double[] temperature=getArguments().getDoubleArray("teplota");
            //setGraph(graph,delka,temperature);
        }

        return rootView;
    }

    public static TemperatureFragment newInstance(double[] delka,double[] temperature)
    {
        TemperatureFragment fragment=new TemperatureFragment();
        Bundle args=new Bundle();
        args.putDoubleArray("delka",delka);
        args.putDoubleArray("teplota",temperature);
        fragment.setArguments(args);
        return fragment;
    }
/*
    public void setGraph(GraphView graph,double[] delka,double[] temperature)
    {
        LineGraphSeries<DataPoint> series= new LineGraphSeries();
        for(int i=0;i<delka.length;i++)
        {
            DataPoint data=new DataPoint(delka[i],temperature[i]);
            series.appendData(data,true,delka.length);
        }
        graph.addSeries(series);
    }
*/



}
