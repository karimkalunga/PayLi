package com.media.clouds.app.dal;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "downloads")
public class DownloadedMedia {
    @PrimaryKey
    private int id;

    @ColumnInfo(name = "title")
    private String title;

    @ColumnInfo(name = "creator_name")
    private String creatorName;

    @ColumnInfo(name = "amount")
    private String amount;

    @ColumnInfo(name = "media_type")
    private String mediaType;

    @ColumnInfo(name = "description")
    private String description;

    @ColumnInfo(name = "media_url")
    private String mediaUrl;

    @ColumnInfo(name = "banner_url")
    private String bannerUrl;

    @ColumnInfo(name = "subscription_start_date")
    private String subscriptionStartDate;

    @ColumnInfo(name = "subscription_end_date")
    private String subscriptionEndDate;

    public DownloadedMedia(
            int id,
            String title,
            String creatorName,
            String amount,
            String mediaType,
            String description,
            String mediaUrl,
            String bannerUrl,
            String subscriptionStartDate,
            String subscriptionEndDate) {
        this.id = id;
        this.title = title;
        this.creatorName = creatorName;
        this.amount = amount;
        this.mediaType = mediaType;
        this.description = description;
        this.mediaType = mediaType;
        this.mediaUrl = mediaUrl;
        this.bannerUrl = bannerUrl;
        this.subscriptionStartDate = subscriptionStartDate;
        this.subscriptionEndDate = subscriptionEndDate;
    }

    public int getId() { return id; }

    public String getTitle() { return title; }

    public String getCreatorName() { return creatorName; }

    public String getAmount() { return amount; }

    public String getMediaType() { return mediaType; }

    public String getDescription() { return description; }

    public String getMediaUrl() { return mediaUrl; }

    public String getBannerUrl() { return bannerUrl; }

    public String getSubscriptionStartDate() { return subscriptionStartDate; }

    public String getSubscriptionEndDate() { return subscriptionEndDate; }
}
