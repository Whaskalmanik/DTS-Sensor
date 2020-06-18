package com.whaskalmanik.dtssensor.graph;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.preference.Preference;
import android.widget.ImageView;
import android.widget.Toast;

import com.whaskalmanik.dtssensor.files.ExtractedFile;
import com.whaskalmanik.dtssensor.preferences.Preferences;
import com.whaskalmanik.dtssensor.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class HeatGraph {

    private ArrayList<ExtractedFile> data;
    private ImageView heatImage;
    private ImageView barImage;
    private int heat_min;
    private int heat_max;


    public HeatGraph(ArrayList<ExtractedFile> data,ImageView heatImage, ImageView barImage) {
        this.data = data;
        this.heatImage = heatImage;
        this.barImage = barImage;
        if(Preferences.isDataOverrided())
        {
            heat_max = Preferences.getHeatMax();
            heat_min = Preferences.getHeatMin();
        }
        else {
            heat_max = (int) Utils.getDataMaxTemperature(data);
            heat_min = (int) Utils.getDataMinTemperature(data);
        }
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
        if(heat_max<heat_min)
        {
            return Color.BLUE;
        }
        float t = Math.min(Math.max((temp - heat_min) / (heat_max - heat_min), 0f), 1f);
        return interpolateColor(Color.BLUE,Color.RED,t);
    }

    private void createBar() {
        int width=heat_max-heat_min;
        if(width<=0)
        {
            return;
        }
        Bitmap bitmap = Bitmap.createBitmap(width, 10, Bitmap.Config.ARGB_8888);
        for(int i = 0;i<width;i++) {
            for(int j = 0;j<10;j++) {
                bitmap.setPixel(i, j, getHeatColor(i+heat_min));
            }
        }
        barImage.setImageBitmap(bitmap);
        barImage.setScaleType(ImageView.ScaleType.FIT_XY);
    }


    public void createGraph() {
        if(!Utils.isDataValid(data)) {
            return;
        }
        int lengthCount = data.get(0).getLength().size();

        Bitmap bitmap = Bitmap.createBitmap(lengthCount, data.size(), Bitmap.Config.ARGB_8888);

        for(int i=0;i<data.size();i++) {
            List<ExtractedFile.Entry> entries = data.get(i).getEntries();

            for(int j=0;j<entries.size();j++) {
                bitmap.setPixel(j, data.size()-1-i, getHeatColor((float)entries.get(j).getTemp()));
            }
        }
        heatImage.setImageBitmap(bitmap);
        heatImage.setScaleType(ImageView.ScaleType.FIT_XY);
        createBar();
    }
}
