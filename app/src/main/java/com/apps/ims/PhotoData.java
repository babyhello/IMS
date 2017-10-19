package com.apps.ims;

/**
 * Created by androids on 2016/10/25.
 */
public class PhotoData {

    public static String photo_background;

    public static String photo_enddate;

    public static String photo_sdate;

    public static String photo_path;

    public static String photo_ID;

    public static String photo_item;

    public static String photo_downloadPath;

    public PhotoData() {


    }

    public PhotoData(String photo_background, String photo_enddate, String photo_sdate, String photo_path,String Photo_ID,String photo_downloadPath,String Photo_item) {
        this.photo_background = photo_background;

        this.photo_enddate = photo_enddate;

        this.photo_sdate = photo_sdate;

        this.photo_path = photo_path;

        this.photo_ID = Photo_ID;

        this.photo_item = Photo_item;

        this.photo_downloadPath = photo_downloadPath;
    }

}
