package com.whaskalmanik.dtssensor.Utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import com.whaskalmanik.dtssensor.Preference.SynchronizationPref;
import com.whaskalmanik.dtssensor.Utils.ExtractedFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

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
    private ExtractedFile parseLines(List<String> lines)
    {
        ExtractedFile file=null;
        String date=null;
        String time=null;
        List<Float> length=new ArrayList<>();
        List<Float> temperature=new ArrayList<>();
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
                length.add(Float.parseFloat(words[0]));
                temperature.add(Float.parseFloat(words[1]));
                Log.d("FileParser",words[0] + " " + words[1]);
            }
            catch (NumberFormatException nfe){}
            file=new ExtractedFile(date,time,length,temperature);
        }
        Log.d("FileParser","=========NEW FILE=========");
        return file;
    }

    public ArrayList<ExtractedFile> extractFiles()
    {
        ArrayList<ExtractedFile> files = new ArrayList<>();
        for (String item:assetsList)
        {
            List<String> lines = readLines(item);
            files.add(parseLines(lines));
        }
        return files;
    }

    public ExtractedFile extractFile(String path)
    {
        List<String> lines = readLines(path);
        return parseLines(lines);
    }
}
