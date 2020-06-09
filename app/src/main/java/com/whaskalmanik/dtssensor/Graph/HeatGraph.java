package com.whaskalmanik.dtssensor.Graph;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import com.whaskalmanik.dtssensor.Files.ExtractedFile;
import com.whaskalmanik.dtssensor.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HeatGraph {

    private ArrayList<ExtractedFile> data;
    private ImageView imageView;
    private TextView lengthStart;
    private TextView lengthEnd;
    private TextView tempMin;
    private TextView tempMax;
    private TextView timeStart;
    private TextView timeEnd;
    private LinearLayout layout;
    private TableLayout table;

    private static final float MIN_TEMP = 20;
    private static final float MAX_TEMP = 30;
    private static final DateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm:ss");

    public HeatGraph(ArrayList<ExtractedFile> data, View rootView)
    {
        this.data = data;
        imageView = (ImageView) rootView.findViewById(R.id.imageView);
        lengthStart = (TextView) rootView.findViewById(R.id.lenghtStart);
        lengthEnd = (TextView) rootView.findViewById(R.id.lenghtEnd);
        tempMin = (TextView) rootView.findViewById(R.id.minTmp);
        tempMax = (TextView) rootView.findViewById(R.id.maxTmp);
        timeEnd = (TextView) rootView.findViewById(R.id.timeEnd);
        timeStart = (TextView) rootView.findViewById(R.id.timeStart);
        layout = (LinearLayout)rootView.findViewById(R.id.layoutForPadding);
        table = (TableLayout) rootView.findViewById(R.id.tableLayout);
      //  bar = (ImageView) rootView.findViewById(R.id.imageViewBar);
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
        float t = Math.min(Math.max((temp - MIN_TEMP) / (MAX_TEMP - MIN_TEMP), 0f), 1f);
        return interpolateColor(Color.BLUE,Color.RED,t);
    }

    public void createBar(ImageView image)
    {
        Bitmap bitmap = Bitmap.createBitmap(100, 10, Bitmap.Config.ARGB_8888);
        for(int i = 0;i<100;i++)
        {
            for(int j = 0;j<10;j++)
            {
                bitmap.setPixel(i, j, getHeatColor(i/100));
            }
        }
        image.setImageBitmap(bitmap);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
    }
    private void setTextView(float min, float max,int start, int end)
    {
        tempMin.setText(MIN_TEMP+" °C");
        tempMax.setText(MAX_TEMP+" °C");
        lengthEnd.setText(min+" m");
        lengthStart.setText(max+" m");
        timeEnd.setText(end+" s");
        timeStart.setText(start+" s");
        int number =layout.getWidth();
        table.setPadding(layout.getWidth(),0,0,0);

    }


    public void createGraph()
    {
        if (data==null) {
            Log.d("HeatGraph","Data in heat RealTimeGraph are null");
            return;
        }

        int startingTime = 0;
        int endingTime = startingTime+data.size()*10;
        float minLength = data.get(0).getMinimumLenght();
        float maxLength = data.get(0).getMaximumLenght();
        int lengthCount = data.get(0).getLength().size();

        setTextView(minLength,maxLength,startingTime,endingTime);

        Bitmap bitmap = Bitmap.createBitmap(lengthCount, data.size(), Bitmap.Config.ARGB_8888);

        for(int i=0;i<data.size();i++)
        {
            List<ExtractedFile.Entry> entries = data.get(i).getEntries();

            for(int j=0;j<entries.size();j++)
            {
                bitmap.setPixel(j, i, getHeatColor((float)entries.get(j).getTemp()));
            }
        }
        imageView.setImageBitmap(bitmap);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
    }

}
