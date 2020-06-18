package com.whaskalmanik.dtssensor.utils;

import com.whaskalmanik.dtssensor.files.ExtractedFile;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class Utils
{
    public static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
    public static final DateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm:ss",Locale.US);
    public static final DateFormat DATETIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.US);
    public static final DateFormat DATETIME_FORMAT_LIST = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.US);
    public static final DateFormat DATE_FORMAT_LIST = new SimpleDateFormat("dd.MM.yyyy",Locale.US);

    public static final int CONNECTION_TIME_OUT_MS = 5000;
    public static final int SOCKET_TIME_OUT_MS = 5000;
    public static final int SERVER_SELECTION_TIMEOUT_MS = 5000;

    public static boolean deleteRecursive(final File fileOrDirectory) {
        if (fileOrDirectory.isDirectory()) {
            for (File child : fileOrDirectory.listFiles()) {
                deleteRecursive(child);
            }
        }
        return fileOrDirectory.delete();
    }
    public static boolean isDataValid(List<ExtractedFile> data) {
        return data != null && !data.isEmpty() && !data.get(0).getEntries().isEmpty();
    }
    public static float roundFloat(float f, int places) {

        BigDecimal bigDecimal = new BigDecimal(Float.toString(f));
        bigDecimal = bigDecimal.setScale(places, RoundingMode.HALF_UP);
        return bigDecimal.floatValue();
    }
    public static float getDataMaxTemperature(final Collection<ExtractedFile> data)
    {
        return data.stream().map(ExtractedFile::getMaximumTemperature).max(Float::compareTo).get();
    }
    public static float getDataMinTemperature(final Collection<ExtractedFile> data)
    {
        return data.stream().map(ExtractedFile::getMinimumTemperature).min(Float::compareTo).get();
    }

}
