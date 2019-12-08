package com.whaskalmanik.dtssensor;

import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class XMLParser {
    private static final String ns = null;

    public List<Entry> parse(InputStream in) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            return readFeed(parser);
        } finally {
            in.close();
        }
    }

    private List<Entry> readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
        List<Entry> entries = new ArrayList<Entry>();

        parser.require(XmlPullParser.START_TAG, ns, "kalibrace");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            Log.d("XML","Name = " + name);
            // Starts by looking for the entry tag
            if (name.equals("mer")) {
                entries.add(readEntry(parser));

            } else {
                parser.next();
            }
        }
        return entries;
    }



    // Parses the contents of an entry. If it encounters a title, summary, or link tag, hands them
    // off
    // to their respective &quot;read&quot; methods for processing. Otherwise, skips the tag.

    private Entry readEntry(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "mer");
        String delka = null;
        String teplota= null;
        String stokes = null;
        String antistokes= null;
        Log.d("readEntry","START");

        String name = parser.getName();

        for (int i = 0; i < parser.getAttributeCount(); i++) {
            Log.d("attr","i = " + i + " name = " + parser.getAttributeName(i) + " value = " + parser.getAttributeValue(i));

            if (parser.getAttributeName(i).equals("delka")) {
                delka = parser.getAttributeValue(i);
            }
            if (parser.getAttributeName(i).equals("teplota")) {
                teplota = parser.getAttributeValue(i);
            }

            if (parser.getAttributeName(i).equals("stokes")) {
                stokes = parser.getAttributeValue(i);
            }

            if (parser.getAttributeName(i).equals("anti-stokes")) {
                antistokes = parser.getAttributeValue(i);
            }

        }

        parser.next();

        return new Entry(delka,teplota,stokes,antistokes);
    }

}
