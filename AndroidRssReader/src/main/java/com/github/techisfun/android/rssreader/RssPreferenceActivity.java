package com.github.techisfun.android.rssreader;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

/**
 * Created by James on 10/5/13.
 */
public class RssPreferenceActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.prefs);
    }

    /*
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        String url = PreferenceManager.getDefaultSharedPreferences(this).getString("pref_rss_url", Constants.DEFAULT_RSS_URL);
        Preference prefRssUrl = findPreference("pref_rss_url");
        prefRssUrl.setSummary(url);

    }
    */
}
