package com.whaskalmanik.dtssensor.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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

    private static final String SELECTED_INDEX = "index";
    private static final String SELECTED_VALUE = "value";
    private static final String LAST_INDEX = "lastIndex";

    public FragmentRealTimeListener listener;

    private LineChart chart;
    private Graph graph;
    private TextView selected;
    private TextView minValue;
    private TextView maxValue;

    private Spinner spinner;
    private Context context;
    private DocumentsLoader documentsLoader;

    private int indexInSpinner=0;
    private float selectedLength = Float.MIN_VALUE;
    private float selectedTemperature = Float.MIN_VALUE;
    private int lastIndex;

    ArrayList<ExtractedFile> files = new ArrayList<>();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_realtime,container,false);
        context = rootView.getContext();

        Bundle arguments = getArguments();

        if (arguments != null) {
           lastIndex = arguments.getInt(LAST_INDEX,Integer.MIN_VALUE);
        }

        if (savedInstanceState != null) {
            selectedLength = savedInstanceState.getFloat(SELECTED_VALUE);
            indexInSpinner = savedInstanceState.getInt(SELECTED_INDEX);
        }
        selected = (TextView) rootView.findViewById(R.id.selectedTemperature);
        minValue = (TextView) rootView.findViewById(R.id.minValue);
        maxValue = (TextView) rootView.findViewById(R.id.maxValue);
        spinner = (Spinner) rootView.findViewById(R.id.spinner);

        chart = rootView.findViewById(R.id.chart);

        documentsLoader = new DocumentsLoader(context);
        files = documentsLoader.parseDataFromFiles();

        setSpinner();
        setGraph();

        return  rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putInt(SELECTED_INDEX, indexInSpinner);
        outState.putFloat(SELECTED_VALUE, selectedLength);
    }

    public interface FragmentRealTimeListener {
        void onValueSent(float number,int indexInSpinner);

        void onValueSent();
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
                graph.createGraph(position);
                indexInSpinner = position;
                if(selectedLength !=Float.MIN_VALUE)
                {
                    graph.higlightValue(selectedLength);
                }
            }
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });
        if(lastIndex != Integer.MIN_VALUE)
        {
            spinner.setSelection(lastIndex);
        }
    }

    public void setGraph()
    {
        graph = new Graph(chart,files,context);

        if(selectedLength == Float.MIN_VALUE)
        {
            setInformation(0,0, View.GONE);
        }
        else
        {
            setInformation(selectedLength,selectedTemperature, View.VISIBLE);

        }

        chart.setOnChartValueSelectedListener(new OnChartValueSelectedListener()
        {

            @Override
            public void onValueSelected(Entry e, Highlight h)
            {
                selectedLength = e.getX();
                selectedTemperature = e.getY();
                listener.onValueSent(selectedLength,indexInSpinner);
                setInformation(selectedLength,selectedTemperature,View.VISIBLE);
            }
            @Override
            public void onNothingSelected()
            {

            }
        });
    }

    private void setInformation(float selectedX,float selectedY, int visibility)
    {
        selected.setText("Selected: " + selectedX + " m " + selectedY + " °C");
        selected.setVisibility(visibility);
        minValue.setText("Min: "+ files.get(indexInSpinner).getMinimumTemperature()+" °C");
        maxValue.setText("Max: "+ files.get(indexInSpinner).getMaximumTemperature()+" °C");

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

    public static RealTimeFragment newInstance(int lastIndex)
    {
        RealTimeFragment fragment = new RealTimeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

}
