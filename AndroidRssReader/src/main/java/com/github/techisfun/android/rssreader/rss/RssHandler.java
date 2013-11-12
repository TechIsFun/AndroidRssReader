package com.github.techisfun.android.rssreader.rss;

import com.github.techisfun.android.rssreader.model.RssArrayList;
import com.github.techisfun.android.rssreader.model.RssItem;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Created by admin on 04/10/13.
 */
class RssHandler extends DefaultHandler {

    private static final String ELEM_ITEM = "item";

    private static final String ELEM_TITLE = "title";

    private static final String ELEM_LINK = "link";

    private static final String ELEM_DESCRIPTION = "description";

    private static final String ELEM_PUBDATE = "pubDate";

    private RssArrayList rssItems = new RssArrayList();

    private RssItem currentItem;

    private boolean parsingTitle;

    private boolean parsingLink;

    private boolean parsingDescription;

    private boolean parsingPubDate;

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if (ELEM_ITEM.equalsIgnoreCase(qName)) {
            currentItem = new RssItem();
        } else if (ELEM_TITLE.equalsIgnoreCase(qName)) {
            parsingTitle = true;
        } else if (ELEM_LINK.equalsIgnoreCase(qName)) {
            parsingLink = true;
        } else if (ELEM_DESCRIPTION.equalsIgnoreCase(qName)) {
            parsingDescription = true;
        } else if (ELEM_PUBDATE.equalsIgnoreCase(qName)) {
            parsingPubDate = true;
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (ELEM_ITEM.equalsIgnoreCase(qName)) {
            rssItems.add(currentItem);
            currentItem = null;
        } else if (ELEM_TITLE.equalsIgnoreCase(qName)) {
            parsingTitle = false;
        } else if (ELEM_LINK.equalsIgnoreCase(qName)) {
            parsingLink = false;
        } else if (ELEM_DESCRIPTION.equalsIgnoreCase(qName)) {
            parsingDescription = false;
        } else if (ELEM_PUBDATE.equalsIgnoreCase(qName)) {
            parsingPubDate = false;
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if (currentItem == null) {
            return;
        }

        String data = new String(ch, start, length);

        if (parsingTitle) {
            currentItem.setTitle(data);
        } else if (parsingLink) {
            currentItem.appendToLink(data);
        } else if (parsingDescription) {
            currentItem.appendDescription(data);
        } else if (parsingPubDate) {
            currentItem.setPubDate(data);
        }
    }

    public RssArrayList getItems() {
        return rssItems;
    }
}
