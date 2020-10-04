package com.media.clouds.app.features.media.utils;

import android.content.Context;

import com.media.clouds.app.security.EncoDecode;
import com.media.clouds.app.utils.FormatterImpl;
import com.media.clouds.app.utils.KeyConstants;

import org.json.JSONObject;

import java.nio.charset.StandardCharsets;

/**s
 * ContentDataLayer.class
 * This class handles
 */
public class ContentDataLayer {

    private JSONObject content;

    /**
     * Private constructor.
     * @param content audio content.
     */
    private ContentDataLayer(JSONObject content) {
        this.content = content;
    }

    /**
     * Initializes content data.
     * @param contentJson audio content.
     * @return class instance.
     * @throws Exception JSON Exception.
     */
    public static  synchronized ContentDataLayer init(String contentJson) throws Exception {
        JSONObject obj = new JSONObject(contentJson);
        return new ContentDataLayer(obj);
    }

    /**
     * Gets content ID.
     * @return content ID string.
     * @throws Exception JSON Exception.
     */
    public String getId() throws Exception {
        String id = content.getString(KeyConstants.ID);
        if (!id.isEmpty()) {
            return id;
        }
        return null;
    }

    /**
     * Gets content title.
     * @return content title.
     * @throws Exception JSON Exception.
     */
    public String getTitle() throws Exception {
        String title = content.getString(KeyConstants.NAME);
        if (!title.isEmpty()) {
            return title;
        }
        return "-";
    }

    /**
     * Gets banner image URL.
     * @param context application context.
     * @return banner image URL.
     * @throws Exception JSON Exception.
     */
    public String getBannerLink(Context context) throws Exception {
        String bannerUrl = content.getString(KeyConstants.BANNER_LINK);
        if (!bannerUrl.isEmpty()) {
            byte[] bytes = EncoDecode.getInstance(context).decodeBase64(bannerUrl);
            return new String(bytes, StandardCharsets.UTF_8);
        }
        return null;
    }

    /**
     * Gets content URL.
     * @param context application context.
     * @return content URL.
     * @throws Exception JSON Exception.
     */
    public String getContentLink(Context context) throws Exception {
        String bannerUrl = content.getString(KeyConstants.CONTENT_LINK);
        if (!bannerUrl.isEmpty()) {
            byte[] bytes = EncoDecode.getInstance(context).decodeBase64(bannerUrl);
            return new String(bytes, StandardCharsets.UTF_8);
        }
        return null;
    }

    /**
     * Get content price.
     * @return content price string.
     * @throws Exception JSON Exception.
     */
    public String getPrice() throws Exception {
        String price = content.getString(KeyConstants.CONTENT_PRICE);
        if (!price.isEmpty()) {
            return FormatterImpl.getInstance()
                    .formatNumberAddCommas(Double.parseDouble(price));
        }
        return "0.00";
    }

    /**
     * Gets author name for data map.
     * @return author name string.
     * @throws Exception JSON Exception.
     */
    public String getAuthorName() throws Exception {
        String authorName = content.getString(KeyConstants.CREATOR_NAME);
        if (!authorName.isEmpty()) {
            return authorName;
        }
        return "-";
    }

    /**
     * Gets content purchase status.
     * @return purchase status.
     * @throws Exception JSON Exception.
     */
    public boolean getPurchaseStatus() throws Exception {
        return content.getBoolean(KeyConstants.IS_BOUGHT);
    }
}
