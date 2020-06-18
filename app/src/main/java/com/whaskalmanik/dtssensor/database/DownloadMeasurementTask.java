package com.whaskalmanik.dtssensor.database;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientSettings;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.whaskalmanik.dtssensor.preferences.Preferences;
import com.whaskalmanik.dtssensor.files.ExtractedFile;
import com.whaskalmanik.dtssensor.utils.Command;

import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;


import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;


public class DownloadMeasurementTask extends AsyncTask<Void,Void,Integer> {

    private final static CodecRegistry pojoCodecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
            fromProviders(PojoCodecProvider.builder().automatic(true).build()));


    private Context context;
    private String collectionName;
    private ProgressDialog dialog;
    private String ip;
    private int port;
    private String databaseName;
    private MongoClient mongoClient;
    private ArrayList<ExtractedFile> extractedFiles;
    private Exception exception;
    private int notCachedIndex = 0;
    private boolean showDialog;
    private Command callback;
    private static int lastIndex;
    private static MongoClientOptions options;

    public DownloadMeasurementTask(Context context, String collectionName,boolean showDialog) {
        this.collectionName = collectionName;
        this.context = context;
        ip = Preferences.getIP();
        port = Preferences.getPort();
        databaseName = Preferences.getDatabaseName();
        extractedFiles = new ArrayList<>();
        this.showDialog=showDialog;

        MongoClientOptions.Builder optionsBuilder = MongoClientOptions.builder();
        options = optionsBuilder.build();
    }

    public void setCallback(final Command callback) {
        this.callback = callback;
    }

    public static int getLastIndex()
    {
        return lastIndex;
    }

    @Override
    protected void onPreExecute() {
        if(showDialog) {
            dialog = ProgressDialog.show(context, "",
                    "Downloading. Please wait...", true);
        }
    }

    @Override
    protected Integer doInBackground(Void... voids) {

        try {
            mongoClient = new MongoClient(new ServerAddress(ip,port),options);
            MongoDatabase database = mongoClient.getDatabase(databaseName).withCodecRegistry(pojoCodecRegistry);
            MongoCollection<ExtractedFile> stronglyTyped = database.getCollection(collectionName, ExtractedFile.class);
            long count = stronglyTyped.countDocuments();

            notCachedIndex = 0;
            while (new File(context.getDataDir(),collectionName+"_"+notCachedIndex).exists()) {
                notCachedIndex++;
            }
            if(count > notCachedIndex) {
                MongoCursor<ExtractedFile> cursor = stronglyTyped.find().skip(notCachedIndex).iterator();
                try {
                    while (cursor.hasNext()) {
                        ExtractedFile extractedFile = cursor.next();
                        extractedFiles.add(extractedFile);
                    }
                } catch (Exception e) {
                    this.exception = e;
                } finally {
                    cursor.close();
                }
                return 0;
            }
        } catch (Exception e) {
            this.exception = e;
        } finally {
            mongoClient.close();
        }
        return null;
    }
    @Override
    protected void onPostExecute(Integer result) {
        if(exception != null) {
            Log.d("Exception",exception.getMessage());
            return;
        }

        BufferedWriter bufferedWriter;
        Gson gson = new Gson();

        for(int i = notCachedIndex;i<extractedFiles.size();i++) {
            String tmp = gson.toJson(extractedFiles.get(i));
            File file = new File(context.getFilesDir(),collectionName+"_"+i);
            lastIndex = i;
            if(!file.exists()) {
                try {
                    if (file.createNewFile()) {
                        try (FileWriter fileWriter = new FileWriter(file.getAbsoluteFile())) {
                            bufferedWriter = new BufferedWriter(fileWriter);
                            bufferedWriter.write(tmp);
                            bufferedWriter.close();

                        }
                    }
                    else {
                        Toast.makeText(context, "File unable to create", Toast.LENGTH_SHORT).show();
                    }
                }
                catch (IOException e) {
                    Log.d("Exceptions", e.getMessage());
                }
            }
        }
        if(showDialog) {
            try {
                dialog.cancel();
            }
            catch (final IllegalArgumentException e) {
                Log.d("Exceptions", e.getMessage());
            }
            finally {
                dialog=null;
            }

        }
        if (callback != null) {
            callback.apply();
        }

    }
}
