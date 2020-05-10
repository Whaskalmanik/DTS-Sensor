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
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.whaskalmanik.dtssensor.Files.DocumentsLoader;
import com.whaskalmanik.dtssensor.R;
import com.whaskalmanik.dtssensor.Files.ExtractedFile;
import com.whaskalmanik.dtssensor.Graph.RealTimeGraph;

import java.util.ArrayList;


public class RealTimeFragment extends Fragment {
    public FragmentRealTimeListener listener;
    LineChart chart;
    TextView max;
    RealTimeGraph realTimeGraph;

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
        Bundle arguments = getArguments();

        if (arguments != null)
        {
            files = arguments.getParcelableArrayList("data");
        }

        realTimeGraph = new RealTimeGraph(chart,files,rootView.getContext());
        realTimeGraph.createGraph();
        DocumentsLoader documentsLoader = new DocumentsLoader(rootView.getContext());
        documentsLoader.getSelectedFiles();
/*
        Spinner spin = (Spinner) rootView.findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(rootView.getContext(), android.R.layout.simple_spinner_item,);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin.setAdapter(adapter);
*/
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
    @Override
    public void onResume()
    {
        super.onResume();
        realTimeGraph.createGraph();
    }
    public static RealTimeFragment newInstance(ArrayList<ExtractedFile> files)
    {
        RealTimeFragment fragment = new RealTimeFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList("data",files);
        fragment.setArguments(args);
        return fragment;
    }

}
