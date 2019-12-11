package com.whaskalmanik.dtssensor.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.whaskalmanik.dtssensor.Entry;
import com.whaskalmanik.dtssensor.R;

import java.util.List;

public class AntistokesFragment extends Fragment {



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView =inflater.inflate(R.layout.fragment_anitstokes,container,false);
        GraphView graph=rootView.findViewById(R.id.graphAntistokes);
        if (getArguments() != null) {
            double[] delka=getArguments().getDoubleArray("delka");
            double[] antistokes=getArguments().getDoubleArray("antistokes");
            setGraph(graph,delka,antistokes);
        }
        return  rootView;
    }

    public static AntistokesFragment newInstance(double[] delka,double[] antistokes)
    {
        AntistokesFragment fragment=new AntistokesFragment();
        Bundle args=new Bundle();
        args.putDoubleArray("delka",delka);
        args.putDoubleArray("antistokes",antistokes);
        fragment.setArguments(args);
        return fragment;
    }

    public void setGraph(GraphView graph,double[] delka,double[] antistokes)
    {
        LineGraphSeries<DataPoint> series= new LineGraphSeries();
        for(int i=0;i<delka.length;i++)
        {
            DataPoint data=new DataPoint(delka[i],antistokes[i]);
            series.appendData(data,true,delka.length);
        }
        graph.addSeries(series);
    }
}
