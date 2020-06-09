package com.whaskalmanik.dtssensor.Graph;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.whaskalmanik.dtssensor.Files.ExtractedFile;
import com.whaskalmanik.dtssensor.Preferences.Preferences;
import com.whaskalmanik.dtssensor.R;

import java.util.ArrayList;
import java.util.List;

public class HeatGraph {

    private ArrayList<ExtractedFile> data;
    private ImageView heatGraph;
    private TextView lengthStart;
    private TextView lengthEnd;
    private TextView tempMin;
    private TextView tempMax;
    private TextView timeStart;
    private TextView timeEnd;
    private TextView paddingText;
    private ImageView bar;
    private TextView middleTemperature;
    private int heat_min;
    private int heat_max;


    public HeatGraph(ArrayList<ExtractedFile> data, View rootView)
    {
        this.data = data;
        heatGraph = (ImageView) rootView.findViewById(R.id.imageView);
        lengthStart = (TextView) rootView.findViewById(R.id.lenghtStart);
        lengthEnd = (TextView) rootView.findViewById(R.id.lenghtEnd);
        tempMin = (TextView) rootView.findViewById(R.id.minTmp);
        tempMax = (TextView) rootView.findViewById(R.id.maxTmp);
        timeEnd = (TextView) rootView.findViewById(R.id.timeEnd);
        timeStart = (TextView) rootView.findViewById(R.id.timeStart);
        paddingText = (TextView) rootView.findViewById(R.id.paddingText);
        middleTemperature = (TextView) rootView.findViewById(R.id.middleTmp);
        bar = (ImageView) rootView.findViewById(R.id.imageBar);

        heat_min = Preferences.getHeatMin();
        heat_max = Preferences.getHeatMax();

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

    private int getHeatColor(float temp) {
        float t = Math.min(Math.max((temp - heat_min) / (heat_max - heat_min), 0f), 1f);
        return interpolateColor(Color.BLUE,Color.RED,t);
    }

    public void createBar()
    {
        int width=heat_max-heat_min;
        Bitmap bitmap = Bitmap.createBitmap(width, 10, Bitmap.Config.ARGB_8888);
        for(int i = 0;i<width;i++)
        {
            for(int j = 0;j<10;j++)
            {
                bitmap.setPixel(i, j, getHeatColor(i+heat_min));
            }
        }
        bar.setImageBitmap(bitmap);
        bar.setScaleType(ImageView.ScaleType.FIT_XY);
    }
    private void setTextView(float min, float max,int start, int end)
    {
        tempMin.setText(heat_min +" °C");
        tempMax.setText(heat_max +" °C");
        lengthEnd.setText(max+" m");
        lengthStart.setText(min+" m");
        timeEnd.setText(end+" s");
        timeStart.setText(start+" s");
        paddingText.setText(end+" s");
        int midPoint=(heat_max+heat_min)/2;
        middleTemperature.setText((midPoint)+" °C");
    }


    public void createGraph()
    {
        if(data==null||data.isEmpty()||data.get(0).getEntries().isEmpty())
        {
            setTextView(0,0,0,0);
            return;
        }

        int startingTime = 0;

        int endingTime = startingTime+data.size()*10;
        float minLength = data.get(0).getMinimumLength();
        float maxLength = data.get(0).getMaximumLength();
        int lengthCount = data.get(0).getLength().size();

        setTextView(minLength,maxLength,startingTime,endingTime);

        Bitmap bitmap = Bitmap.createBitmap(lengthCount, data.size(), Bitmap.Config.ARGB_8888);

        for(int i=0;i<data.size();i++)
        {
            List<ExtractedFile.Entry> entries = data.get(i).getEntries();

            for(int j=0;j<entries.size();j++)
            {
                bitmap.setPixel(j, data.size()-1-i, getHeatColor((float)entries.get(j).getTemp()));
            }
        }
        heatGraph.setImageBitmap(bitmap);
        heatGraph.setScaleType(ImageView.ScaleType.FIT_XY);
        createBar();
    }

}
