package com.whaskalmanik.dtssensor.Fragments;

import android.animation.ArgbEvaluator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.anychart.anychart.AnyChart;
import com.anychart.anychart.AnyChartView;
import com.anychart.anychart.DataEntry;
import com.anychart.anychart.HeatDataEntry;
import com.anychart.anychart.HeatMap;
import com.anychart.anychart.Interactivity;
import com.anychart.anychart.OrdinalColor;
import com.anychart.anychart.SelectionMode;
import com.anychart.anychart.SolidFill;
import com.whaskalmanik.dtssensor.Files.DocumentsLoader;
import com.whaskalmanik.dtssensor.Files.ExtractedFile;
import com.whaskalmanik.dtssensor.Graph.HeatGraph;
import com.whaskalmanik.dtssensor.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class HeatFragment extends Fragment {
    private ArrayList<ExtractedFile> files = new ArrayList<>();
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_heat, container, false);

        DocumentsLoader documentsLoader = new DocumentsLoader(rootView.getContext());
        files = documentsLoader.parseDataFromFiles();

        ImageView imageView = rootView.findViewById(R.id.imageView);
        TextView lengthStart= rootView.findViewById(R.id.lenghtStart);
        TextView lengthEnd= rootView.findViewById(R.id.lenghtEnd);

        HeatGraph graph= new HeatGraph(files,imageView,lengthStart,lengthEnd);
        graph.createGraph();
        return rootView;
    }

    public static HeatFragment newInstance(ArrayList<ExtractedFile> files)
    {
        HeatFragment fragment = new HeatFragment();
        return fragment;
    }
}