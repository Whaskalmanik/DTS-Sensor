package com.whaskalmanik.dtssensor.files;

import android.content.Context;
import android.preference.Preference;
import android.util.Log;

import com.google.gson.Gson;
import com.whaskalmanik.dtssensor.preferences.Preferences;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class DocumentsLoader {
    private String selectedFile;
    private Context context;
    private ArrayList<File> files;
    private ArrayList<ExtractedFile> extractedFiles;

    public DocumentsLoader(Context context) {
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
    public ArrayList<ExtractedFile> parseDataFromFiles() {
        getSelectedFiles();
        if(files==null) {
            return null;
        }
        String response;
        for (File file:files) {
            if(!file.exists()) {
                continue;
            }
            try {
                response = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
                Log.d("String",response);
                Gson gson = new Gson();
                ExtractedFile extractedFile = gson.fromJson(response, ExtractedFile.class);
                float offsetMin = Preferences.getGraphOffsetMin();
                float offsetMax = Preferences.getGraphOffsetMax();
                if(offsetMin != 0.0f || offsetMax != extractedFile.getMaximumLength())
                {
                    List<ExtractedFile.Entry> entries = extractedFile.getEntries().stream().filter(x -> x.getLength() >= offsetMin &&  x.getLength() <= offsetMax).collect(Collectors.toList());
                    extractedFile.setEntries(entries);
                }
                extractedFiles.add(extractedFile);
            }
            catch (Exception ex) {
                Log.d("Exceptions",ex.getMessage());
                return null;
            }
        }
        return extractedFiles.stream().sorted(new ExtractedFile.TimestampComparator()).collect(Collectors.toCollection(ArrayList::new));
    }
}
