package com.whaskalmanik.dtssensor.Fragments;


import android.os.Bundle;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.whaskalmanik.dtssensor.Files.DocumentsLoader;
import com.whaskalmanik.dtssensor.Files.ExtractedFile;
import com.whaskalmanik.dtssensor.Graph.HeatGraph;
import com.whaskalmanik.dtssensor.R;

import java.util.ArrayList;

public class HeatFragment extends Fragment {
    private ArrayList<ExtractedFile> files = new ArrayList<>();
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_heat, container, false);

        DocumentsLoader documentsLoader = new DocumentsLoader(rootView.getContext());
        files = documentsLoader.parseDataFromFiles();

        HeatGraph graph= new HeatGraph(files,rootView);
        graph.createGraph();
        return rootView;
    }

    public static HeatFragment newInstance()
    {
        HeatFragment fragment = new HeatFragment();
        return fragment;
    }
}