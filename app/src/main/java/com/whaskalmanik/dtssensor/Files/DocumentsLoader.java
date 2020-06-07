package com.whaskalmanik.dtssensor.Files;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.whaskalmanik.dtssensor.Preferences.Preferences;

import org.apache.commons.io.FileUtils;
import org.bson.Document;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;


public class DocumentsLoader {
    private String selectedFile;
    private Context context;
    private ArrayList<File> files;
    private ArrayList<ExtractedFile> extractedFiles;

    public DocumentsLoader(Context context)
    {
        this.context=context;
        selectedFile = Preferences.getSelectedValue();
        files = new ArrayList<>();
        extractedFiles = new ArrayList<>();
    }

    private void getSelectedFiles() {
        if(selectedFile!=null) {
            File dir = context.getFilesDir();
            File[] tmp = dir.listFiles();
            for (File file : tmp) {
                if (file.getName().startsWith(selectedFile)) {
                    files.add(file);
                    Log.d("Files", file.getName());
                }
            }
        }
    }
    public ArrayList<ExtractedFile> parseDataFromFiles()
    {
        getSelectedFiles();
        if(files==null) {
            return null;
        }
        String response = null;
        for (File file:files) {
            if(!file.exists()) {
                continue;
            }
            try
            {
                response = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
                Log.d("String",response);
                Gson gson = new Gson();
                ExtractedFile extractedFile = gson.fromJson(response, ExtractedFile.class);
                extractedFiles.add(extractedFile);

            }
            catch (Exception ex)
            {
                Log.d("Exceptions",ex.getMessage());
                return null;
            }
        }
        return extractedFiles;

    }
}
