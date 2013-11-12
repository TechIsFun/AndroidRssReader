package com.github.techisfun.android.rssreader.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by admin on 04/10/13.
 */
public class RssItem implements Parcelable {

    private String title;

    private StringBuilder link = new StringBuilder();

    private String pubDate;

    private StringBuilder description = new StringBuilder();

    private String media;

    public RssItem() {
        // nothing
    }

    public RssItem(Parcel in) {
        title = in.readString();
        link = new StringBuilder(in.readString());
        pubDate = in.readString();
        description = new StringBuilder(in.readString());
        media = in.readString();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link.toString();
    }

    public void appendToLink(String value) {
        link.append(value);
    }

    public void setPubDate(String pubDate) {
        this.pubDate = pubDate;
    }

    public String getPubDate() {
        return pubDate;
    }

    public String getDescription() {
        return description.toString();
    }

    public void appendDescription(String value) {
        description.append(value);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("RssItem{");
        sb.append("title='").append(title).append('\'');
        sb.append(", link='").append(link).append('\'');
        sb.append(", pubDate='").append(pubDate).append('\'');
        sb.append(", description=").append(description);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(title);
        parcel.writeString(link.toString());
        parcel.writeString(pubDate);
        parcel.writeString(description.toString());
        parcel.writeString(media);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public RssItem createFromParcel(Parcel in) {
            return new RssItem(in);
        }

        public RssItem[] newArray(int size) {
            return new RssItem[size];
        }
    };

    public String getMedia() {
        return media;
    }

    public void setMedia(String media) {
        this.media = media;
    }
}
