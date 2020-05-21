package com.whaskalmanik.dtssensor.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.whaskalmanik.dtssensor.R;
import com.whaskalmanik.dtssensor.Utils.ListEntry;
import com.whaskalmanik.dtssensor.Database.MeasurementLoadingTask;

import java.util.ArrayList;


public class MeasurementsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View rootView =inflater.inflate(R.layout.fragment_measurements,container,false);
        Bundle arguments = getArguments();
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
