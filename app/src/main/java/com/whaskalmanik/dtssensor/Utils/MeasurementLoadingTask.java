package com.whaskalmanik.dtssensor.Utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.health.ServiceHealthStats;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.google.common.collect.Lists;
import com.mongodb.CommandResult;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;
import com.whaskalmanik.dtssensor.Preferences.Preferences;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class MeasurementLoadingTask extends AsyncTask<Void,Void,Integer> {
    private Exception exception;
    private String ip;
    private int port;
    private String databaseName;
    private MongoClient mongoClient;
    private Context context;
    private List<String> collectionNames;
    private ListView lv;
    ProgressDialog dialog;

    public MeasurementLoadingTask(Context context, ListView lv)
    {
        this.context=context;
        this.lv=lv;
        ip = Preferences.getIP();
        port = Preferences.getPort();
        databaseName = Preferences.getDatabaseName();
    }

    @Override
    protected void onPreExecute()
    {
        dialog = ProgressDialog.show(context, "",
            "Loading. Please wait...", true);
    }

    @Override
    protected Integer doInBackground(Void... voids) {
        try {

            mongoClient = new MongoClient(ip,port);
            List<String> dbNames = Lists.newArrayList(mongoClient.listDatabaseNames());
            MongoDatabase database = mongoClient.getDatabase(databaseName);
            collectionNames = Lists.newArrayList(database.listCollectionNames());
            return 0;
        } catch (Exception e) {
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
        SharedPreferences pref= context.getSharedPreferences("SelectedPreferences",0);
        SharedPreferences.Editor editor = pref.edit();
        if(exception != null)
        {
            Toast.makeText(context,exception.getMessage(),Toast.LENGTH_LONG).show();
        }
        if(result==0)
        {
            Toast.makeText(context,"Connection established",Toast.LENGTH_LONG).show();

            List<ListEntry> listEntries = collectionNames.stream().map(x -> new ListEntry(x, "test")).collect(Collectors.toList());

            EntryAdapter adapter = new EntryAdapter(context,listEntries);
            lv.setAdapter(adapter);
            lv.setOnItemClickListener((adapterView, view, i, l) -> {
                ListEntry temp = listEntries.get(i);
                editor.putString("selected",temp.name);
                editor.commit();
                adapter.notifyDataSetChanged();
                DownloadMeasurementTask task = new DownloadMeasurementTask(context,temp.name);
                //todo Trida pro naceni dat kolekce
                task.execute();
                Toast.makeText(context,temp.name,Toast.LENGTH_SHORT).show();
            });
        }

    }
}
