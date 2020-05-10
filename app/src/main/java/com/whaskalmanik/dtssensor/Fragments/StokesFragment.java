package com.whaskalmanik.dtssensor.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.whaskalmanik.dtssensor.R;
import com.whaskalmanik.dtssensor.Files.ExtractedFile;

import java.util.ArrayList;

public class StokesFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_stokes, container, false);
        return rootView;
    }

    public static StokesFragment newInstance()
    {
        StokesFragment fragment = new StokesFragment();
        return fragment;
    }
}