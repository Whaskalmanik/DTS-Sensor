package com.whaskalmanik.dtssensor.Files;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;


import org.bson.Document;
import org.bson.types.ObjectId;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class ExtractedFile implements Parcelable {
    public static final class Entry
    {
        private double length;
        private double temp;

        public Entry() {}

        public Entry(double length, double temp)
        {
            this.length = length;
            this.temp = temp;
        }

        public double getLength() {
            return length;
        }

        public void setLength(double length) {
            this.length = length;
        }

        public double getTemp() {
            return temp;
        }

        public void setTemp(double temp) {
            this.temp = temp;
        }
    }

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private static final DateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm:ss");
    private static final DateFormat DATETIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private Date timestamp;
    private ArrayList<Entry> data;


    public ExtractedFile() {}

    protected ExtractedFile(Parcel in)
    {
        String date = in.readString();
        String time = in.readString();
        try {
            DATETIME_FORMAT.parse(date + " " + time);
        }
        catch (ParseException e) {

            Log.d("Exceptions",e.getMessage());
        }

        ArrayList lengths = in.readArrayList(null);
        ArrayList temperatures = in.readArrayList(null);

        data = IntStream.range(0, Math.min(lengths.size(), temperatures.size()))
                .mapToObj(x -> new Entry((Float)lengths.get(x), (Float)temperatures.get(x)))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public static final Creator<ExtractedFile> CREATOR = new Creator<ExtractedFile>()
    {
        @Override
        public ExtractedFile createFromParcel(Parcel in)
        {
            return new ExtractedFile(in);
        }

        @Override
        public ExtractedFile[] newArray(int size)
        {
            return new ExtractedFile[size];
        }

    };

    public List<Float> getLength()
    {
        return data.stream().map(x -> Float.valueOf((float)x.length)).collect(Collectors.toList());
    }

    public List<Float> getTemperature()
    {
        return data.stream().map(x -> Float.valueOf((float)x.temp)).collect(Collectors.toList());
    }

    public List<Entry> getEntries()
    {
        return Collections.unmodifiableList(data);
    }

    public String getDate()
    {
        return DATE_FORMAT.format(timestamp);
    }

    public String getTime()
    {
        return TIME_FORMAT.format(timestamp);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(getDate());
        dest.writeString(getTime());
        dest.writeList(getLength());
        dest.writeList(getTemperature());
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(final Date timestamp) {
        this.timestamp = timestamp;
    }

    public ArrayList<Entry> getData() {
        return data;
    }

    public void setData(final ArrayList<Entry> data) {
        this.data = data;
    }

    public float getMaximumLenght()
    {
        return Collections.max(getLength());
    }
    public float getMaximumTemperature()
    {
        return Collections.max(getTemperature());
    }
    public float getMinimumLenght()
    {
        return Collections.min(getLength());
    }
    public float getMinimumTemperature()
    {
        return Collections.min(getTemperature());
    }
}
