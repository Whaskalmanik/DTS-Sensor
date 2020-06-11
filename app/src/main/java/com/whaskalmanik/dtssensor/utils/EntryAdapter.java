package com.whaskalmanik.dtssensor.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.whaskalmanik.dtssensor.preferences.Preferences;
import com.whaskalmanik.dtssensor.R;

import java.io.File;
import java.util.List;

public class EntryAdapter extends ArrayAdapter<ListEntry> {
    private Context context;

    public EntryAdapter(Context context, List<ListEntry> users) {
        super(context, 0, users);
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ListEntry entry = getItem(position);
        if(entry==null) {
            throw new NullPointerException();
        }
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_list, parent, false);
        }
        // Lookup view for data population

        TextView tvName =  convertView.findViewById(R.id.tvName);
        ImageView tvImage = convertView.findViewById(R.id.tvImage);
        TextView tvDate = convertView.findViewById(R.id.tvDate);

        // Populate the data into the template view using the data object
        String temp = Preferences.getSelectedValue();
        File file = new File(context.getFilesDir(),entry.identifier+"_"+0);
        if(temp!=null) {
            entry.selected = entry.identifier.equals(temp);
        }
        entry.downloaded = file.exists();
        tvName.setText(entry.name);
        tvDate.setText(entry.date);
        decideImage(entry.selected, entry.downloaded, tvImage);
        // Return the completed view to render on screen
        return convertView;
    }

    private void decideImage(boolean selected, boolean downloaded, ImageView image) {
        if(selected) {
            image.setImageDrawable(getContext().getDrawable(R.drawable.ic_save_green_24dp));
        }
        else if(downloaded) {
            image.setImageDrawable(getContext().getDrawable(R.drawable.ic_save_yellow_24dp));
        }
        else {
            image.setImageDrawable(getContext().getDrawable(R.drawable.ic_save_black_24dp));
        }
    }
}
