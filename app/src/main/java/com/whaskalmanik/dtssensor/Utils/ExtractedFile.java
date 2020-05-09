package com.whaskalmanik.dtssensor.Utils;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class ExtractedFile implements Parcelable {
    private String date;
    private String time;
    private List<Float> length;
    private List<Float> temperature;

    ExtractedFile(String date, String time, List<Float> length, List<Float> temperature)
    {
        this.date=date;
        this.time=time;
        this.length=length;
        this.temperature=temperature;
    }

    protected ExtractedFile(Parcel in)
    {
        date = in.readString();
        time = in.readString();
        length = in.readArrayList(null);
        temperature = in.readArrayList(null);
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
        return length;
    }

    public List<Float> getTemperature()
    {
        return temperature;
    }

    public String getDate()
    {
        return date;
    }

    public String getTime()
    {
        return time;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(date);
        dest.writeString(time);
        dest.writeList(length);
        dest.writeList(temperature);
    }
}