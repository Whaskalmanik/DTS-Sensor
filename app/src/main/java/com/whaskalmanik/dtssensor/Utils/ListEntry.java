package com.whaskalmanik.dtssensor.Utils;

public class ListEntry {
    public String name;
    public boolean downloaded;
    public String date;
    public boolean selected;

    public ListEntry(String name, String date) {
        this.name = name;
        this.date = date;
        this.downloaded = false;
        this.selected = false;
    }
    public void select()
    {
        this.selected = true;
    }

    public void unselect()
    {
        this.selected = false;
    }

}
