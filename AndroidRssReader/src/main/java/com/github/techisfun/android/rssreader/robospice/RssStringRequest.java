package com.github.techisfun.android.rssreader.robospice;

import com.octo.android.robospice.request.springandroid.SpringAndroidSpiceRequest;

/**
 * Created by admin on 04/10/13.
 */
public class RssStringRequest extends SpringAndroidSpiceRequest<String> {

    private String url;

    public RssStringRequest(String url) {
        super(String.class);

        this.url = url;
    }

    @Override
    public String loadDataFromNetwork() throws Exception {
        return getRestTemplate().getForObject(url, String.class);
    }

    public String getCacheKey() {
        return "rss-request-" + url;
    }
}
