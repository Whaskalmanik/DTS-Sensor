package com.whaskalmanik.dtssensor.Fragments;

import android.accessibilityservice.AccessibilityService;
import android.content.Context;
import android.content.res.Configuration;
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
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.whaskalmanik.dtssensor.Files.DocumentsLoader;
import com.whaskalmanik.dtssensor.R;
import com.whaskalmanik.dtssensor.Files.ExtractedFile;
import com.whaskalmanik.dtssensor.Graph.Graph;

import java.nio.channels.FileLock;
import java.util.ArrayList;
import java.util.Collections;


public class RealTimeFragment extends Fragment {

    private static final String SELECTED_INDEX = "index";
    private static final String SELECTED_VALUE = "value";

    public FragmentRealTimeListener listener;

    private LineChart chart;
    private Graph graph;
    private TextView selectedTemperature;
    private TextView minValue;
    private TextView maxValue;

    private Spinner spinner;
    private Context context;
    private DocumentsLoader documentsLoader;
    private Highlight high;

    private int selectedIndex=0;
    private float selectedValue = Float.MIN_VALUE;

    ArrayList<ExtractedFile> files = new ArrayList<>();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_realtime,container,false);
        context = rootView.getContext();

        if (savedInstanceState != null) {
            selectedValue = savedInstanceState.getFloat(SELECTED_VALUE);
            selectedIndex = savedInstanceState.getInt(SELECTED_INDEX);
        }
        selectedTemperature = (TextView) rootView.findViewById(R.id.selectedTemperature);
        minValue = (TextView) rootView.findViewById(R.id.minValue);
        maxValue = (TextView) rootView.findViewById(R.id.maxValue);
        spinner = (Spinner) rootView.findViewById(R.id.spinner);

        chart = rootView.findViewById(R.id.chart);

        receiveData();
        setSpinner();
        setGraph();


        return  rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putInt(SELECTED_INDEX,selectedIndex);
        outState.putFloat(SELECTED_VALUE,selectedValue);
    }

    public interface FragmentRealTimeListener {
        void onValueSent(float number);
    }


    public void receiveData()
    {
        Bundle arguments = getArguments();
        documentsLoader = new DocumentsLoader(context);

        files = documentsLoader.parseDataFromFiles();
    }

    public void setSpinner()
    {
        ArrayList<String> nameList = new ArrayList<>();
        for(int i = 0; i < files.size(); i++)
        {
            nameList.add(files.get(i).getDate() + " " + files.get(i).getTime());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, nameList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                graph.createRealTimeGraph(position);
                if(selectedValue!=Float.MIN_VALUE)
                {
                    graph.higlightValue(selectedValue);
                }
            }
            public void onNothingSelected(AdapterView<?> parent)
            {
                //graph.createRealTimeGraph(0);
            }
        });
    }

    public void setGraph()
    {
        graph = new Graph(chart,files,context);

        if(selectedValue == Float.MIN_VALUE)
        {
            setInformation(0, View.GONE);
        }
        else
        {
            setInformation(selectedValue, View.VISIBLE);

        }

        chart.setOnChartValueSelectedListener(new OnChartValueSelectedListener()
        {

            @Override
            public void onValueSelected(Entry e, Highlight h)
            {
                selectedValue = e.getX();
                listener.onValueSent(selectedValue);
                setInformation(selectedValue,View.VISIBLE);
            }
            @Override
            public void onNothingSelected()
            {

            }
        });
    }

    private void setInformation(float selectedX, int visibility)
    {
        selectedTemperature.setText("Selected: "+selectedX+" m");
        selectedTemperature.setVisibility(visibility);
        minValue.setText("Min: "+ Collections.min(files.get(selectedIndex).getTemperature())+" °C");
        maxValue.setText("Max: "+ Collections.max(files.get(selectedIndex).getTemperature())+" °C");

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

    public static RealTimeFragment newInstance()
    {
        RealTimeFragment fragment = new RealTimeFragment();
        return fragment;
    }

}
