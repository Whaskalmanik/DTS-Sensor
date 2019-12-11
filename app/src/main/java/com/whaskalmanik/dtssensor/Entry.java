package com.whaskalmanik.dtssensor;

import java.io.Serializable;

public class Entry implements Serializable {
    public final String delka;
    public final String teplota;
    public final String stokes;
    public final String antistokes;

    Entry(String delka,String teplota, String stokes, String antistokes) {
        this.delka=delka;
        this.teplota=teplota;
        this.stokes=stokes;
        this.antistokes=antistokes;
    }
}

