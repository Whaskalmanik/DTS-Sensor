package com.whaskalmanik.dtssensor.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.health.ServiceHealthStats;
import android.preference.PreferenceManager;
import android.util.Log;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

import java.net.UnknownHostException;
import java.util.Set;

public class MeasurementLoadingTask extends AsyncTask<String,Void,Long> {
    private Exception exception;
    private String ip;
    private int port;
    private MongoClient mongoClient;
    private Context context;

    public MeasurementLoadingTask(Context context)
    {
        this.context=context;

    }

    @Override
    protected void onPreExecute()
    {
        //Udělat popup s loadingem
    }

    @Override
    protected Long doInBackground(String... values) {
        try {
            //ip = PreferenceManager.(context).getString()
            //mongoClient = new MongoClient(ip,port);
            //DB database= mongoClient.getDB("DTS");
            //database.getCollectionNames();
            //Uložit do listu názvy
            // return collection.count();
        } catch (Exception e) {
            this.exception = e;
        }
        finally {
            mongoClient.close();
        }
        return null;
    }

    protected void onPostExecute(Long count) {
        if(exception != null)
        {
            Log.d("MongoLog",exception.getMessage());
        }
        else
        {
            Log.d("MongoLog",count.toString());
        }

        // Zrušit popup
        // Dát do listboxu ve fragmentu obsah listu s názvy
    }
}
