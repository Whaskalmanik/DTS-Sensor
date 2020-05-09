package com.whaskalmanik.dtssensor.Utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.common.collect.Lists;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.whaskalmanik.dtssensor.Preferences.Preferences;

import org.bson.Document;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class DownloadMeasurementTask extends AsyncTask<Void,Void,Integer> {

    private Context context;
    String collectionName;
    ProgressDialog dialog;
    private String ip;
    private int port;
    private String databaseName;
    private MongoClient mongoClient;
    Document myDoc;

    public DownloadMeasurementTask(Context context, String collectionName)
    {
        this.collectionName = collectionName;
        this.context = context;
        ip = Preferences.getIP();
        port = Preferences.getPort();
        databaseName = Preferences.getDatabaseName();
    }

    @Override
    protected void onPreExecute()
    {
        dialog = ProgressDialog.show(context, "",
                "Downloading. Please wait...", true);
    }

    @Override
    protected Integer doInBackground(Void... voids) {
        try {
            mongoClient = new MongoClient(ip,port);
            MongoDatabase database = mongoClient.getDatabase(databaseName);
            MongoCollection<Document> collection = database.getCollection(collectionName);
            myDoc = collection.find().first();
            return 0;
        } catch (Exception e) {
            //this.exception = e;
        }
        finally {
            mongoClient.close();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Integer result) {
        dialog.cancel();
        FileWriter fileWriter;
        BufferedWriter bufferedWriter;
        String tmp = myDoc.toJson();
        File file = new File(context.getFilesDir(),collectionName);
        if(!file.exists())
        {
            try
            {
                if(file.createNewFile())
                {
                    fileWriter = new FileWriter(file.getAbsoluteFile());
                    bufferedWriter = new BufferedWriter(fileWriter);
                    bufferedWriter.write(tmp);
                    bufferedWriter.close();
                }

            }
            catch (IOException e) {
                Log.d("Exceptions",e.getMessage());
            }

        }
    }
}
