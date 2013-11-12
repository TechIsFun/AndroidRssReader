package com.github.techisfun.android.rssreader;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.Window;
import com.github.techisfun.android.rssreader.model.RssArrayList;
import com.github.techisfun.android.rssreader.model.RssItem;
import com.github.techisfun.android.rssreader.robospice.RssArrayListRequest;
import com.github.techisfun.android.rssreader.robospice.RssSpiceService;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends SherlockActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final String CACHE_KEY = "cache-key";

    SpiceManager spiceManager = new SpiceManager(RssSpiceService.class);

    private List<RssItem> rssItems = new ArrayList<RssItem>(0);

    private RssArrayAdapter rssItemAdapter;

    private ListView listView;

    private String cacheKey;

    private NetworkStateReceiver networkStateReceiver = new NetworkStateReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setSupportProgressBarIndeterminateVisibility(false);

        listView = (ListView) findViewById(android.R.id.list);
        rssItemAdapter = new RssArrayAdapter();
        listView.setAdapter(rssItemAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, RssItemActivity.class);
                intent.putExtra(Constants.BUNDLE_RSS_ITEM, rssItems.get(position));
                startActivity(intent);
            }
        });

        String url = getRssUrl();
        getSupportActionBar().setSubtitle(url);
        loadRss(url);
    }

    @Override
    protected void onStart() {
        super.onStart();

        spiceManager.start(this);

        // register receiver
        IntentFilter filter = new IntentFilter();
        filter.addAction(android.net.ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkStateReceiver, filter);
    }

    @Override
    protected void onStop() {
        spiceManager.shouldStop();

        if (networkStateReceiver != null) {
            unregisterReceiver(networkStateReceiver);
        }

        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getSupportMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                startActivity(new Intent(this, RssPreferenceActivity.class));
                break;
            case R.id.action_refresh:
                loadRss(getRssUrl());
                break;
            default:
                break;
        }
        return super.onMenuItemSelected(featureId, item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(CACHE_KEY, cacheKey);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        String ck = savedInstanceState.getString(CACHE_KEY);
        if (ck != null) {
            cacheKey = ck;
            spiceManager.addListenerIfPending(RssArrayList.class, cacheKey, new RssRequestListener());
            spiceManager.getFromCache(RssArrayList.class, cacheKey, DurationInMillis.ONE_MINUTE, new RssRequestListener());
        }
    }

    /**
     * Do remote request
     *
     * @param url
     */
    protected void loadRss(String url) {
        setSupportProgressBarIndeterminateVisibility(true);

        RssArrayListRequest request = new RssArrayListRequest(url);
        cacheKey = request.getCacheKey();
        spiceManager.execute(request, cacheKey, DurationInMillis.ONE_MINUTE, new RssRequestListener());
    }

    /**
     * Get the rss url
     */
    protected String getRssUrl() {
        return PreferenceManager.getDefaultSharedPreferences(this).getString("pref_rss_url", Constants.DEFAULT_RSS_URL);
    }

    private class RssRequestListener implements RequestListener<RssArrayList> {

        @Override
        public void onRequestFailure(SpiceException e) {
            Log.d(TAG, "got exception", e);
            setSupportProgressBarIndeterminateVisibility(false);

            Toast.makeText(getApplicationContext(), getString(R.string.feed_error, getRssUrl()), Toast.LENGTH_LONG).show();
        }

        @Override
        public void onRequestSuccess(RssArrayList rssItems) {
            setSupportProgressBarIndeterminateVisibility(false);

            if (rssItems == null) {
                return;
            }

            rssItemAdapter.clear();

            // rssItemAdapter.addAll(rssItems) requires API Level 11
            for (RssItem item : rssItems) {
                rssItemAdapter.add(item);
            }

            rssItemAdapter.notifyDataSetChanged();
        }
    }

    class RssArrayAdapter extends ArrayAdapter<RssItem> {

        private LayoutInflater mInflater;

        public RssArrayAdapter() {
            super(getApplicationContext(), R.layout.list_item, rssItems);

            mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            TextView descriptionTw;
            TextView titleTw;
            TextView pubDateTw;

            if (convertView == null) {
                view = mInflater.inflate(R.layout.list_item, parent, false);
            } else {
                view = convertView;
            }

            titleTw = (TextView) view.findViewById(R.id.rss_item_title);
            descriptionTw = (TextView) view.findViewById(R.id.rss_item_description);
            pubDateTw = (TextView) view.findViewById(R.id.rss_item_date);

            RssItem item = getItem(position);

            titleTw.setText(item.getTitle());
            pubDateTw.setText(item.getPubDate());

            //CharSequence description = Html.fromHtml(item.getDescription(), new NullImageGetter(), new TagHandlerImpl());
            CharSequence description = Html.fromHtml(item.getDescription());
            descriptionTw.setText(description);

            return view;
        }
    }

    public class NetworkStateReceiver extends BroadcastReceiver {

        public void onReceive(Context context, Intent intent) {
            if (intent.getExtras() != null) {
                NetworkInfo ni = (NetworkInfo) intent.getExtras().get(ConnectivityManager.EXTRA_NETWORK_INFO);

                if (ni != null && ni.getState() == NetworkInfo.State.CONNECTED) {
                    loadRss(getRssUrl());
                }
            }

            if (intent.getExtras().getBoolean(ConnectivityManager.EXTRA_NO_CONNECTIVITY, Boolean.FALSE)) {
                Toast.makeText(getApplicationContext(), getString(R.string.feed_error, getRssUrl()), Toast.LENGTH_LONG).show();
            }
        }
    }

}
