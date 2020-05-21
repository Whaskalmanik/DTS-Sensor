package com.whaskalmanik.dtssensor.Database;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.google.common.collect.Lists;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.whaskalmanik.dtssensor.Files.PeriodicTask;
import com.whaskalmanik.dtssensor.Preferences.Preferences;
import com.whaskalmanik.dtssensor.Utils.EntryAdapter;
import com.whaskalmanik.dtssensor.Utils.ListEntry;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Objects;
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
    PeriodicTask watcher;

    static final DateFormat DATETIME_FORMAT = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
    static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy");
    static final DateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm:ss");


    public MeasurementLoadingTask(Context context, ListView lv)
    {
        this.context=context;
        this.lv=lv;
        ip = Preferences.getIP();
        port = Preferences.getPort();
        databaseName = Preferences.getDatabaseName();
        watcher= new PeriodicTask(context);
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
            return null;
        }
        finally {
            mongoClient.close();
        }
    }
    private ListEntry getEntry(String value)
    {
        int index = value.indexOf('_');
        String timestamp = value.substring(index+1);
        try {
            Date date = DATETIME_FORMAT.parse(timestamp);
            return new ListEntry(value,DATE_FORMAT.format(date),TIME_FORMAT.format(date));
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }


    @Override
    protected void onPostExecute(Integer result) {
        dialog.cancel();
        SharedPreferences pref = context.getSharedPreferences("SelectedPreferences", 0);
        SharedPreferences.Editor editor = pref.edit();
        if (result == null)
        {
            Log.d("exception",exception.getMessage());
            return;
        }
        Toast.makeText(context, "Connection established", Toast.LENGTH_LONG).show();


        List<ListEntry> listEntries = collectionNames.stream().map(this::getEntry).filter(Objects::nonNull).collect(Collectors.toList());
        EntryAdapter adapter = new EntryAdapter(context, listEntries);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener((adapterView, view, i, l) -> {
            ListEntry temp = listEntries.get(i);
            editor.putString("selected", temp.identifier);
            editor.commit();
            adapter.notifyDataSetChanged();
            DownloadMeasurementTask task = new DownloadMeasurementTask(context, temp.identifier,true);
            task.execute();
            Toast.makeText(context, temp.identifier, Toast.LENGTH_SHORT).show();
        });

    }

}
