package com.whaskalmanik.dtssensor.Graph;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.whaskalmanik.dtssensor.Files.ExtractedFile;
import com.whaskalmanik.dtssensor.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HeatGraph {

    private ArrayList<ExtractedFile> data;
    private ImageView imageView;
    TextView lenghtStart;
    TextView lenghtEnd;

    public HeatGraph(ArrayList<ExtractedFile> data, ImageView imageView, TextView lenghtStart, TextView lenghtEnd)
    {
        this.data=data;
        this.imageView=imageView;
        this.lenghtEnd=lenghtEnd;
        this.lenghtStart=lenghtStart;
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
        float minTemp = 20;
        float maxTemp = 30;
        float t = Math.min(Math.max((temp - minTemp) / (maxTemp - minTemp), 0f), 1f);
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


    public void createGraph()
    {
        if (data==null) {
            Log.d("HeatGraph","Data in heat RealTimeGraph are null");
            return;
        }

        Date startingTime = data.get(0).getTimestamp();
        Date endingTime = data.get(data.size() - 1).getTimestamp();
        float minLength = data.get(0).getMinimumLenght();
        float maxLength = data.get(0).getMaximumLenght();
        int lengthCount = data.get(0).getLength().size();

        lenghtStart.setText(minLength+" m");
        lenghtEnd.setText(maxLength+" m");

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
