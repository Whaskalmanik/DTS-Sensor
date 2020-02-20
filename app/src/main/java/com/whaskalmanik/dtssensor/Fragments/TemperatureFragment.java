package com.whaskalmanik.dtssensor.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.whaskalmanik.dtssensor.R;
import com.whaskalmanik.dtssensor.Utils.ExtractedFile;

import java.util.ArrayList;

public class TemperatureFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View rootView =inflater.inflate(R.layout.fragment_temperature,container,false);
        if (getArguments() != null)
        {
            ArrayList<ExtractedFile> files=getArguments().getParcelableArrayList("data");
        }
        return rootView;
    }

    public static TemperatureFragment newInstance(ArrayList<ExtractedFile> files)
    {
        TemperatureFragment fragment=new TemperatureFragment();
        Bundle args=new Bundle();
        args.putParcelableArrayList("data",files);
        fragment.setArguments(args);
        return fragment;
    }




}
