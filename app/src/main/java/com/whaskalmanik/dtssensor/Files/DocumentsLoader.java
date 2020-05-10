package com.whaskalmanik.dtssensor.Files;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;

import org.bson.Document;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;


public class DocumentsLoader {
    private String selectedFile;
    private Context context;
    ArrayList<File> files;
    ArrayList<ExtractedFile> extractedFiles;

    public DocumentsLoader(Context context)
    {
        this.context=context;
        SharedPreferences selectedPreferences = context.getSharedPreferences("SelectedPreferences", 0);
        selectedFile = selectedPreferences.getString("selected",null);
        files = new ArrayList<>();
        extractedFiles = new ArrayList<>();
    }
    public void getSelectedFiles() {
        if(selectedFile!=null) {
            File dir = context.getFilesDir();
            File[] tmp = dir.listFiles();
            for (File file : tmp) {
                if (file.getName().startsWith(selectedFile)) {
                    files.add(file);
                    Log.d("Files", file.getName());
                }
            }
            parseDataFromFiles();
        }
    }
    private void parseDataFromFiles()
    {
        if(files!=null)
        {
            String responce=null;
            for (File file:files) {
                File tempFile=new File(context.getFilesDir(),file.getName());
                if(tempFile.exists())
                {
                    try
                    {
                        String line;
                        FileReader fileReader = new FileReader(file);
                        BufferedReader bufferedReader = new BufferedReader(fileReader);
                        StringBuilder stringBuilder = new StringBuilder();
                        while ((line = bufferedReader.readLine()) != null){
                            stringBuilder.append(line);
                        }
                        bufferedReader.close();
                        responce=stringBuilder.toString();
                        Log.d("String",responce);
                        Gson gson = new Gson();
                        ExtractedFile extractedFile = gson.fromJson(responce, ExtractedFile.class);
                        extractedFiles.add(extractedFile);

                    }
                    catch (Exception ex)
                    {
                        Log.d("Exceptions",ex.getMessage());
                    }
                }
            }
        }
    }
}
