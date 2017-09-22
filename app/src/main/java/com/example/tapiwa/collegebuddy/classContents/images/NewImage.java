package com.example.tapiwa.collegebuddy.classContents.images;


/**
 * Created by tapiwa on 7/3/17.
 */

public class NewImage {


    private String tag;
    private String full_image_uri;
    private String timeUploaded;
    private String thumb_uri;
    private String image_key;

    public NewImage() {
    }


    public NewImage(String tag, String full_image_uri, String thumb_uri, String timeUploaded, String imageKey) {
        this.tag = tag;
        this.full_image_uri = full_image_uri;
        this.timeUploaded = timeUploaded;
        this.thumb_uri = thumb_uri;
        this.image_key = imageKey;
    }


    public String getThumb_uri() {
        return thumb_uri;
    }

    public String getImage_key() {
        return image_key;
    }

    public String getTimeUploaded() {
        return timeUploaded;
    }

    public String getTag() {
        return tag;
    }

    public String getFull_image_uri() {
        return full_image_uri;
    }
}
