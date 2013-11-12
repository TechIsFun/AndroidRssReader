package com.github.techisfun.android.rssreader;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.github.techisfun.android.rssreader.model.RssItem;
import com.github.techisfun.android.rssreader.robospice.RssSpiceService;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.octo.android.robospice.request.simple.BitmapRequest;

import java.io.File;

/**
 * Created by James on 10/5/13.
 */
public class RssItemActivity extends SherlockActivity {

    private static final String TAG = RssItemActivity.class.getSimpleName();

    private SpiceManager spiceManager = new SpiceManager(RssSpiceService.class);

    private TextView titleTw;
    private TextView descriptionTw;
    private TextView pubDateTw;
    private Button openInBrowserButton;
    private ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rss_item);

        titleTw = (TextView) findViewById(R.id.rss_item_title);
        descriptionTw = (TextView) findViewById(R.id.rss_item_description);
        pubDateTw = (TextView) findViewById(R.id.rss_item_date);
        image = (ImageView) findViewById(R.id.rss_item_image);
        openInBrowserButton = (Button) findViewById(R.id.open_in_browser_button);

        final RssItem item = getIntent().getParcelableExtra(Constants.BUNDLE_RSS_ITEM);

        getSupportActionBar().setTitle(item.getTitle());

        titleTw.setText(item.getTitle());
        descriptionTw.setText(Html.fromHtml(item.getDescription()));
        pubDateTw.setText(item.getPubDate());

        openInBrowserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                Uri uri = Uri.parse(item.getLink());
                i.setData(uri);
                startActivity(i);
            }
        });

        String imageLink = item.getMedia();
        if (imageLink != null) {
            File tempFile = new File(getCacheDir(), "THUMB_IMAGE_TEMP_" + imageLink.hashCode());
            spiceManager.execute(new BitmapRequest(imageLink, tempFile), "" + imageLink.hashCode(), DurationInMillis.ONE_HOUR, new RequestListener<Bitmap>() {
                @Override
                public void onRequestFailure(SpiceException e) {
                    Log.d(TAG, "got exception", e);
                    image.setVisibility(View.GONE);
                }

                @Override
                public void onRequestSuccess(Bitmap bitmap) {
                    image.setVisibility(View.VISIBLE);
                    image.setImageBitmap(bitmap);
                }
            });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        spiceManager.start(this);
    }

    @Override
    protected void onStop() {
        spiceManager.shouldStop();

        super.onStop();
    }
}
