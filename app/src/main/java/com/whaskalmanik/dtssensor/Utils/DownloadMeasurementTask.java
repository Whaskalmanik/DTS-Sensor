package com.whaskalmanik.dtssensor.Utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.mongodb.DBCursor;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.whaskalmanik.dtssensor.Preferences.Preferences;

import org.bson.Document;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class DownloadMeasurementTask extends AsyncTask<Void,Void,Integer> {

    private Context context;
    String collectionName;
    ProgressDialog dialog;
    private String ip;
    private int port;
    private String databaseName;
    private MongoClient mongoClient;
    ArrayList<Document> documents;
    Exception exception;

    public DownloadMeasurementTask(Context context, String collectionName)
    {
        this.collectionName = collectionName;
        this.context = context;
        ip = Preferences.getIP();
        port = Preferences.getPort();
        databaseName = Preferences.getDatabaseName();
        documents = new ArrayList<>();
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
            Document doc;
            mongoClient = new MongoClient(ip,port);
            MongoDatabase database = mongoClient.getDatabase(databaseName);
            MongoCollection<Document> collection = database.getCollection(collectionName);
            MongoCursor<Document> cursor= collection.find().iterator();
            try
            {
                while(cursor.hasNext())
                {
                    doc = cursor.next();
                    documents.add(doc);
                }
            }
            catch (Exception e)
            {
                this.exception = e;
            }
            finally
            {
                cursor.close();
            }
            return 0;
        }
        catch (Exception e)
        {
            this.exception = e;
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
        for(int i=0;i<documents.size();i++)
        {
            String tmp = documents.get(i).toJson();
            File file = new File(context.getFilesDir(),collectionName+"_"+i);
            if(!file.exists()) {
                try {
                    if (file.createNewFile()) {
                        fileWriter = new FileWriter(file.getAbsoluteFile());
                        bufferedWriter = new BufferedWriter(fileWriter);
                        bufferedWriter.write(tmp);
                        bufferedWriter.close();
                    }

                } catch (IOException e) {
                    Log.d("Exceptions", e.getMessage());
                }
            }
        }
    }
}