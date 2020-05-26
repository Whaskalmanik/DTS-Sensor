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
import com.whaskalmanik.dtssensor.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class HeatFragment extends Fragment {
    private ArrayList<ExtractedFile> files = new ArrayList<>();
    String[] colors= new String[]{"#ff6666", "#ff5544", "#ff4422"};
    DocumentsLoader documentsLoader;
    SurfaceView surfaceView;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_heat, container, false);

        DocumentsLoader documentsLoader = new DocumentsLoader(rootView.getContext());
        files = documentsLoader.parseDataFromFiles();
        ImageView imageView = rootView.findViewById(R.id.imageView);

        if (files.isEmpty()) {
            //Prázdný graf TODO
           // return;
        }

        Date startingTime = files.get(0).getTimestamp();
        Date endingTime = files.get(files.size() - 1).getTimestamp();
        float minLength = files.get(0).getLength().stream().min(Float::compareTo).get();
        float maxLength = files.get(0).getLength().stream().max(Float::compareTo).get();
        int lengthCount = files.get(0).getLength().size();

        Bitmap bitmap = Bitmap.createBitmap(lengthCount, files.size(), Bitmap.Config.ARGB_8888);

        for(int i=0;i<files.size();i++)
        {
            List<ExtractedFile.Entry> entries = files.get(i).getEntries();
            Date timestamp = files.get(i).getTimestamp();
            float seconds = (timestamp.getTime() - startingTime.getTime()) / 1000f;
            for(int j=0;j<entries.size();j++)
            {
                bitmap.setPixel(j, i, getHeatColor((float)entries.get(j).getTemp()));
                //canvas.drawPoint((float)entries.get(j).getLength()*10, seconds, getColorPaint((int)entries.get(j).getTemp()));
            }
        }
        imageView.setImageBitmap(bitmap);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);

        List<DataEntry> data = new ArrayList<>();

        return rootView;
    }
    private String getColorString(int temp)
    {
        if(temp < 25)
        {
            return "#90caf9";
        }
        else if(temp < 50)
        {
            return "#ffb74d";
        }
        else if(temp <75)
        {
            return "#ef6c00";
        }
        else if(temp<101)
        {
            return "#d84315";
        }
        return "#d84315";
    }

    private Paint getColorPaint(int temp) {
        Paint p = new Paint();
        p.setColor(Color.parseColor(getColorString(temp)));
        return  p;
    }

    private int getHeatColor(float temp) {
        float minTemp = 20;
        float maxTemp = 30;
        float t = Math.min(Math.max((temp - minTemp) / (maxTemp - minTemp), 0f), 1f);
        return interpolateColor(Color.GREEN,Color.RED,t);
    }

    private float interpolate(float a, float b, float proportion) {
        return (a + ((b - a) * proportion));
    }

    private int interpolateColor(int a, int b, float proportion) {

        if (proportion > 1 || proportion < 0) {
            throw new IllegalArgumentException("proportion must be [0 - 1]");
        }
        float[] hsva = new float[3];
        float[] hsvb = new float[3];
        float[] hsv_output = new float[3];

        Color.colorToHSV(a, hsva);
        Color.colorToHSV(b, hsvb);
        for (int i = 0; i < 3; i++) {
            hsv_output[i] = interpolate(hsva[i], hsvb[i], proportion);
        }

        int alpha_a = Color.alpha(a);
        int alpha_b = Color.alpha(b);
        float alpha_output = interpolate(alpha_a, alpha_b, proportion);

        return Color.HSVToColor((int) alpha_output, hsv_output);
    }

    public static HeatFragment newInstance(ArrayList<ExtractedFile> files)
    {
        HeatFragment fragment = new HeatFragment();
        return fragment;
    }
}