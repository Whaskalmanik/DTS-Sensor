package com.whaskalmanik.dtssensor.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.whaskalmanik.dtssensor.R;
import com.whaskalmanik.dtssensor.database.MeasurementLoadingTask;


public class MeasurementsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView =inflater.inflate(R.layout.fragment_measurements,container,false);
        ListView lv = rootView.findViewById(R.id.measurement_list);
        MeasurementLoadingTask task = new MeasurementLoadingTask(getContext(),lv);
        task.execute();
        return  rootView;
    }

    public static MeasurementsFragment newInstance() {
        return new MeasurementsFragment();
    }

}
