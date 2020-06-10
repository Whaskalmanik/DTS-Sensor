package com.whaskalmanik.dtssensor.Database;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.google.common.collect.Lists;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;

import com.whaskalmanik.dtssensor.Preferences.Preferences;
import com.whaskalmanik.dtssensor.Utils.EntryAdapter;
import com.whaskalmanik.dtssensor.Utils.ListEntry;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

public class MeasurementLoadingTask extends AsyncTask<Void,Void,Integer> {

    private static final int CONNECTION_TIME_OUT_MS =5000;
    private static final int SOCKET_TIME_OUT_MS =5000;
    private static final int SERVER_SELECTION_TIMEOUT_MS = 5000;

    private static final DateFormat DATETIME_FORMAT = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.US);
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy",Locale.US);
    private static final DateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm:ss",Locale.US);

    private Exception exception;
    private String ip;
    private int port;
    private String databaseName;
    private MongoClient mongoClient;
    private Context context;
    private List<String> collectionNames;
    private ListView lv;
    private ProgressDialog dialog;
    private MongoClientOptions options;




    public MeasurementLoadingTask(Context context, ListView lv)
    {
        this.context = context;
        this.lv = lv;
        ip = Preferences.getIP();
        port = Preferences.getPort();
        databaseName = Preferences.getDatabaseName();

        MongoClientOptions.Builder optionsBuilder = MongoClientOptions.builder();
        optionsBuilder.connectTimeout(CONNECTION_TIME_OUT_MS);
        optionsBuilder.socketTimeout(SOCKET_TIME_OUT_MS);
        optionsBuilder.serverSelectionTimeout(SERVER_SELECTION_TIMEOUT_MS);
        options = optionsBuilder.build();
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
            mongoClient = new MongoClient(new ServerAddress(ip,port),options);
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
            exception = e;
            return null;
        }
    }


    @Override
    protected void onPostExecute(Integer result) {
        dialog.cancel();
        if (result == null)
        {
            Toast.makeText(context, "Connection failed", Toast.LENGTH_LONG).show();
            Log.d("ListView",exception.getMessage());
            return;
        }
        if(collectionNames.isEmpty())
        {
            Toast.makeText(context,"Database is empty or doesn't exists",Toast.LENGTH_LONG).show();
            Preferences.setSelectedValue(null);
            return;
        }
        List<ListEntry> listEntries = collectionNames.stream().map(this::getEntry).filter(Objects::nonNull).collect(Collectors.toList());
        EntryAdapter adapter = new EntryAdapter(context, listEntries);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener((adapterView, view, i, l) -> {
            ListEntry temp = listEntries.get(i);
            Preferences.setSelectedValue(temp.identifier);
            adapter.notifyDataSetChanged();
            DownloadMeasurementTask task = new DownloadMeasurementTask(context, temp.identifier,true);
            task.execute();
            Toast.makeText(context, temp.identifier, Toast.LENGTH_SHORT).show();
        });

    }

}
