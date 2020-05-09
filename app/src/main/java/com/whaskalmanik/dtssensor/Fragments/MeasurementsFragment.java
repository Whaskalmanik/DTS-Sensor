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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.whaskalmanik.dtssensor.Graph.RealTimeGraph;
import com.whaskalmanik.dtssensor.R;
import com.whaskalmanik.dtssensor.Utils.EntryAdapter;
import com.whaskalmanik.dtssensor.Utils.ExtractedFile;
import com.whaskalmanik.dtssensor.Utils.ListEntry;
import com.whaskalmanik.dtssensor.Utils.MeasurementLoadingTask;

import java.time.Duration;
import java.util.ArrayList;


public class MeasurementsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View rootView =inflater.inflate(R.layout.fragment_measurements,container,false);
        Bundle arguments = getArguments();
        ArrayList<ListEntry> arrayOfUsers = new ArrayList<ListEntry>();
        ListView lv = (ListView)rootView.findViewById(R.id.measurement_llist);
        MeasurementLoadingTask task=new MeasurementLoadingTask(getContext(),lv);
        task.execute();
        if (arguments != null)
        {

        }
        return  rootView;
    }
    public static MeasurementsFragment newInstance()
    {
        MeasurementsFragment fragment = new MeasurementsFragment();
        return fragment;
    }

}
