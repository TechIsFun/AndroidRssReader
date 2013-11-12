package com.github.techisfun.android.rssreader.robospice;

import com.github.techisfun.android.rssreader.model.RssArrayList;
import com.github.techisfun.android.rssreader.rss.RssParser;
import com.octo.android.robospice.request.springandroid.SpringAndroidSpiceRequest;

import java.io.ByteArrayInputStream;

/**
 * Created by admin on 04/10/13.
 */
public class RssArrayListRequest extends SpringAndroidSpiceRequest<RssArrayList> {

    private String url;

    public RssArrayListRequest(String url) {
        super(RssArrayList.class);

        this.url = url;
    }

    @Override
    public RssArrayList loadDataFromNetwork() throws Exception {
        String xml = getRestTemplate().getForObject(url, String.class);

        return new RssParser().parse(new ByteArrayInputStream(xml.getBytes()));
    }

    public String getCacheKey() {
        return "rss-request-" + url;
    }
}
