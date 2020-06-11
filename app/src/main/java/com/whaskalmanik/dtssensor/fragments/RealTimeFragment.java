package com.whaskalmanik.dtssensor.fragments;

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
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.whaskalmanik.dtssensor.files.DocumentsLoader;
import com.whaskalmanik.dtssensor.R;
import com.whaskalmanik.dtssensor.files.ExtractedFile;
import com.whaskalmanik.dtssensor.graph.RealTimeGraph;
import com.whaskalmanik.dtssensor.preferences.Preferences;
import com.whaskalmanik.dtssensor.utils.Utils;

import java.util.ArrayList;


public class RealTimeFragment extends Fragment {

    private static final String LAST_INDEX = "last_index";

    public FragmentRealTimeListener listener;

    private LineChart chart;
    private RealTimeGraph realTimeGraph;
    private TextView selected;
    private TextView minValue;
    private TextView maxValue;

    private Spinner spinner;
    private Context context;

    private static int indexInSpinner=0;
    private static float selectedLength = Float.MIN_VALUE;
    private static float selectedTemperature = Float.MIN_VALUE;
    private int lastIndex;


    private ArrayList<ExtractedFile> data = new ArrayList<>();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_realtime,container,false);
        context = rootView.getContext();

        Bundle arguments = getArguments();

        if (arguments != null) {
           lastIndex = arguments.getInt(LAST_INDEX,Integer.MIN_VALUE);
        }

        selected = rootView.findViewById(R.id.selectedTemperature);
        minValue = rootView.findViewById(R.id.minValue);
        maxValue = rootView.findViewById(R.id.maxValue);
        spinner = rootView.findViewById(R.id.spinner);

        chart = rootView.findViewById(R.id.chart);

        DocumentsLoader documentsLoader = new DocumentsLoader(context);
        data = documentsLoader.parseDataFromFiles();

        setSpinner();
        setGraph();

        return  rootView;
    }

    public interface FragmentRealTimeListener {
        void onValueSent(float valueX);
    }

    private void setSpinner() {
        if(!Utils.isDataValid(data)) {
            return;
        }
        ArrayList<String> nameList = new ArrayList<>();
        for(int i = 0; i < data.size(); i++) {
            nameList.add(data.get(i).getDate() + " " + data.get(i).getTime());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, nameList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                realTimeGraph.createGraph(position);
                indexInSpinner = position;
                if(selectedLength != Float.MIN_VALUE)
                {
                    realTimeGraph.highlightValue(selectedLength);
                }
            }
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        if(lastIndex != Integer.MIN_VALUE && Preferences.isSynchronizationEnabled()) {
            spinner.setSelection(lastIndex);
        }
        else {
            spinner.setSelection(indexInSpinner);
        }
    }

    private void setGraph()
    {
        if(data==null||data.isEmpty()||data.get(0).getEntries().isEmpty()) {
            setInformation(0,0,View.GONE);
            return;
        }
        realTimeGraph = new RealTimeGraph(chart, data,context);

        if(selectedLength == Float.MIN_VALUE) {
            setInformation(0,0, View.GONE);
        }
        else {
            setInformation(selectedLength,selectedTemperature, View.VISIBLE);
        }
        chart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {

            @Override
            public void onValueSelected(Entry e, Highlight h) {
                selectedLength = e.getX();
                selectedTemperature = e.getY();
                listener.onValueSent(selectedLength);
                setInformation(selectedLength,selectedTemperature,View.VISIBLE);
            }
            @Override
            public void onNothingSelected() {

            }
        });
    }

    private void setInformation(float selectedX,float selectedY, int visibility) {
        if(!Utils.isDataValid(data)) {
            selected.setVisibility(visibility);
            minValue.setVisibility(visibility);
            maxValue.setVisibility(visibility);
            Toast.makeText(getContext(), "Graph offset is out of range", Toast.LENGTH_SHORT).show();
            return;
        }
        selected.setText("Selected: " + selectedX + " m " + selectedY + " °C");
        selected.setVisibility(visibility);
        minValue.setText("Min: "+ data.get(indexInSpinner).getMinimumTemperature()+" °C");
        maxValue.setText("Max: "+ data.get(indexInSpinner).getMaximumTemperature()+" °C");
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof FragmentRealTimeListener) {
            listener=(FragmentRealTimeListener)context;
        }
        else {
            throw new RuntimeException(context.toString() + "Must implement FragmentListener");
        }
    }


    @Override
    public void onDetach() {
        super.onDetach();
        listener=null;
    }

    public static RealTimeFragment newInstance(int lastIndex) {
        RealTimeFragment fragment = new RealTimeFragment();
        Bundle args = new Bundle();
        args.putInt(LAST_INDEX,lastIndex);
        fragment.setArguments(args);
        return fragment;
    }

}
