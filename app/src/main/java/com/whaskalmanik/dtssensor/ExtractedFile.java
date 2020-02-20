package com.whaskalmanik.dtssensor;

import java.util.List;

public class ExtractedFile {
    private String date;
    private String time;
    private List<Double> length;
    private List<Double> temperature;

    ExtractedFile(String date, String time, List<Double> length, List<Double> temperature)
    {
        this.date=date;
        this.time=time;
        this.length=length;
        this.temperature=temperature;
    }

    public List<Double> getLength()
    {
        return length;
    }

    public List<Double> getTemperature()
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
}
