package com.media.clouds.app.features.media.payment;

public class ContentPurchaseImpl {

    private static ContentPurchaseImpl instance = null;
    private String contentId, userId;

    private ContentPurchaseImpl(String contentId, String userId) {
        this.contentId = contentId;
        this.userId = userId;
    }

    public static ContentPurchaseImpl getInstance(String contentId, String userId) {
        return new ContentPurchaseImpl(contentId, userId);
    }

    public void purchase() {

    }
}
