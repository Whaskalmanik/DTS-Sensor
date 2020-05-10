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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.whaskalmanik.dtssensor.Files.DocumentsLoader;
import com.whaskalmanik.dtssensor.R;
import com.whaskalmanik.dtssensor.Files.ExtractedFile;
import com.whaskalmanik.dtssensor.Graph.Graph;

import java.util.ArrayList;


public class RealTimeFragment extends Fragment {
    public FragmentRealTimeListener listener;
    LineChart chart;
    TextView max;
    Graph graph;
    int selectedIndex=0;

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

        DocumentsLoader documentsLoader = new DocumentsLoader(rootView.getContext());
        files=documentsLoader.parseDataFromFiles();
        graph = new Graph(chart,files,rootView.getContext());

        ArrayList<String> nameList= new ArrayList<>();

        for(int i =0; i< files.size();i++)
        {
            nameList.add(files.get(i).getDate()+ " "+files.get(i).getTime());
        }

        Spinner spinner = (Spinner) rootView.findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(rootView.getContext(), android.R.layout.simple_spinner_item, nameList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                graph.createRealTimeGraph(position);
            }
            public void onNothingSelected(AdapterView<?> parent)
            {
                graph.createRealTimeGraph(0);
            }
        });


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
        if(files!=null)
        {
            graph.createRealTimeGraph(selectedIndex);
        }
    }
    public static RealTimeFragment newInstance()
    {
        RealTimeFragment fragment = new RealTimeFragment();
        return fragment;
    }

}
