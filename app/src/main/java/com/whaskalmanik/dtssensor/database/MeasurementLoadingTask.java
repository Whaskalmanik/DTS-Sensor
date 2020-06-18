package com.whaskalmanik.dtssensor.database;

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

import com.whaskalmanik.dtssensor.files.DocumentsLoader;
import com.whaskalmanik.dtssensor.files.ExtractedFile;
import com.whaskalmanik.dtssensor.preferences.Preferences;
import com.whaskalmanik.dtssensor.utils.EntryAdapter;
import com.whaskalmanik.dtssensor.utils.ListEntry;
import com.whaskalmanik.dtssensor.utils.Utils;

import org.w3c.dom.Document;

import java.text.ParseException;
import java.util.ArrayList;
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
    private ProgressDialog dialog;
    private MongoClientOptions options;

    public MeasurementLoadingTask(Context context, ListView lv) {
        this.context = context;
        this.lv = lv;
        ip = Preferences.getIP();
        port = Preferences.getPort();
        databaseName = Preferences.getDatabaseName();

        MongoClientOptions.Builder optionsBuilder = MongoClientOptions.builder();
        optionsBuilder.connectTimeout(Utils.CONNECTION_TIME_OUT_MS);
        optionsBuilder.socketTimeout(Utils.SOCKET_TIME_OUT_MS);
        optionsBuilder.serverSelectionTimeout(Utils.SERVER_SELECTION_TIMEOUT_MS);
        options = optionsBuilder.build();
    }

    @Override
    protected void onPreExecute() {
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
    private ListEntry getEntry(String value) {
        int index = value.indexOf('_');
        String timestamp = value.substring(index+1);
        try {
            Date date = Utils.DATETIME_FORMAT_LIST.parse(timestamp);
            return new ListEntry(value,Utils.DATE_FORMAT_LIST.format(date),Utils.TIME_FORMAT.format(date));
        } catch (ParseException e) {
            exception = e;
            return null;
        }
    }


    @Override
    protected void onPostExecute(Integer result) {
        if(dialog!=null) {
            try {
                dialog.dismiss();
            }
            catch (final IllegalArgumentException e) {
                Log.d("Exception",exception.getMessage());
            }
            finally {
                dialog = null;
            }
        }
        if (result == null) {
            Toast.makeText(context, "Connection failed", Toast.LENGTH_LONG).show();
            Preferences.setSelectedValue(null);
            return;
        }
        if(collectionNames.isEmpty()) {
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

            DownloadMeasurementTask task = new DownloadMeasurementTask(context, temp.identifier, true);
            task.execute();
        });
    }
}
