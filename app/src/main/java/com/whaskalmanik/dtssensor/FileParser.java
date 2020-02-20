package com.whaskalmanik.dtssensor;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class FileParser{
    private Context context;
    private String[] assetsList;

    public FileParser(Context c)
    {
        this.context = c;
        this.assetsList = listAssets();
    }

    private String[] listAssets()
    {
        String [] list;
        try {
            list = context.getAssets().list("Data");
        } catch (IOException e) {
            return null;
        }
        return list;
    }

    private List<String> readLines(String path)
    {
        List<String> mLines = new ArrayList<>();
        try
        {
            String line;
            AssetManager am = context.getAssets();
            InputStream is = am.open("Data/"+path);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));

            while ((line = reader.readLine()) != null)
            {
                mLines.add(line);
            }
        }

        catch (IOException e)
        {
            e.printStackTrace();
            return null;
        }
        return mLines;
    }
    private void parseLines(List<String> lines)
    {
        String date=null;
        String time=null;
        List<Double> length=new ArrayList<>();
        List<Double> temperature=new ArrayList<>();
        for (String line:lines) {
            if(line.startsWith("date"))
            {
                date=line.substring(5);
                Log.d("FileParser",date);
                continue;
            }
            if(line.startsWith("time"))
            {
                time=line.substring(5);
                Log.d("FileParser",time);
                continue;
            }
            if(line.startsWith("-"))
            {
                continue;
            }
            String[] words = line.split("\t");
            try
            {
                length.add(Double.parseDouble(words[0]));
                temperature.add(Double.parseDouble(words[1]));
                Log.d("FileParser",words[0] + " " + words[1]);
            }
            catch (NumberFormatException nfe){}
        }
        Log.d("FileParser","=========NEW FILE=========");
    }

    public void extractFile()
    {
        for (String item:assetsList)
        {
            List<String> lines = readLines(item);
            parseLines(lines);
        }
    }
}
