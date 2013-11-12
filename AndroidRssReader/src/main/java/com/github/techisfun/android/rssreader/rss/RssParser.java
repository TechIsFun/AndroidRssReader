package com.github.techisfun.android.rssreader.rss;

import android.util.Xml;

import com.github.techisfun.android.rssreader.model.RssArrayList;
import com.github.techisfun.android.rssreader.model.RssItem;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by James on 10/5/13.
 */
public class RssParser {

    private static final String ns = null;
    public static final String ITEM = "item";
    public static final String RSS = "rss";
    public static final String SUMMARY = "description";
    public static final String CHANNEL = "channel";
    public static final String TITLE = "title";
    public static final String LINK = "link";
    public static final String MEDIA = "media:thumbnail";

    public RssArrayList parse(InputStream in) throws XmlPullParserException, IOException {
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

    private RssArrayList readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
        RssArrayList entries = new RssArrayList();

        parser.require(XmlPullParser.START_TAG, ns, RSS);
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals(CHANNEL)) {
                continue;
            }
            if (name.equals(ITEM)) {
                entries.add(readEntry(parser));
            } else {
                skip(parser);
            }
        }
        return entries;
    }


    private RssItem readEntry(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, ITEM);
        String title = null;
        String summary = null;
        String link = null;
        String media = null;
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals(TITLE)) {
                title = readTitle(parser);
            } else if (name.equals(SUMMARY)) {
                summary = readSummary(parser);
            } else if (name.equals(LINK)) {
                link = readLink(parser);
            } else if (name.equals(MEDIA)) {
                media = readMedia(parser);
            } else {
                skip(parser);
            }
        }

        RssItem item = new RssItem();
        item.setTitle(title);
        item.appendDescription(summary);
        item.appendToLink(link);
        item.setMedia(media);

        return item;
    }

    private String readTitle(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, TITLE);
        String title = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, TITLE);
        return title;
    }

    private String readLink(XmlPullParser parser) throws IOException, XmlPullParserException {
        String link = "";
        parser.require(XmlPullParser.START_TAG, ns, LINK);
        String tag = parser.getName();
        String relType = parser.getAttributeValue(null, "rel");
        if (tag.equals(LINK)) {
            if ("alternate".equals(relType)) {
                link = parser.getAttributeValue(null, "href");
                parser.nextTag();
            } else {
                link = readText(parser);
            }
        }
        parser.require(XmlPullParser.END_TAG, ns, LINK);
        return link;
    }

    private String readSummary(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, SUMMARY);
        String summary = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, SUMMARY);
        return summary;
    }

    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    private String readMedia(XmlPullParser parser) throws IOException, XmlPullParserException {
        String media = "";
        parser.require(XmlPullParser.START_TAG, ns, MEDIA);
        String tag = parser.getName();
        if (tag.equals(MEDIA)) {
            media = parser.getAttributeValue(null, "url");
            parser.nextTag();
        }
        //parser.require(XmlPullParser.END_TAG, ns, LINK);
        return media;
    }

    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }

}
