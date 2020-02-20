package com.whaskalmanik.dtssensor.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.whaskalmanik.dtssensor.R;

public class StokesFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_stokes, container, false);
        // GraphView graph=rootView.findViewById(R.id.graphStokes);
        if (getArguments() != null) {
            double[] delka = getArguments().getDoubleArray("delka");
            double[] antistokes = getArguments().getDoubleArray("stokes");
            //    setGraph(graph,delka,antistokes);
        }
        return rootView;
    }

    public static StokesFragment newInstance(double[] delka, double[] stokes) {
        StokesFragment fragment = new StokesFragment();
        Bundle args = new Bundle();
        args.putDoubleArray("delka", delka);
        args.putDoubleArray("stokes", stokes);
        fragment.setArguments(args);
        return fragment;
    }
/*
    public void setGraph(GraphView graph,double[] delka,double[] stokes)
    {
        LineGraphSeries<DataPoint> series= new LineGraphSeries();
        for(int i=0;i<delka.length;i++)
        {
            DataPoint data=new DataPoint(delka[i],stokes[i]);
            series.appendData(data,true,delka.length);
        }
        graph.addSeries(series);
    }
    */

}