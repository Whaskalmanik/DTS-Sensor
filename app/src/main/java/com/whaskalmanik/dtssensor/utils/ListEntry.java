package com.whaskalmanik.dtssensor.utils;

public class ListEntry {
    public String name;
    public boolean downloaded;
    public String date;
    public boolean selected;
    public String identifier;

    public ListEntry(String identifier, String name, String date) {
        this.name = name;
        this.date = date;
        this.identifier = identifier;
        this.downloaded = false;
        this.selected = false;
    }
}
