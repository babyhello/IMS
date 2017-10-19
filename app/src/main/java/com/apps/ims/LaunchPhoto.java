package com.apps.ims;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

// 資料功能類別
public class LaunchPhoto {
    // 表格名稱
    public static final String TABLE_NAME = "LaunchPhoto";

    // 編號表格欄位名稱，固定不變
    public static final String KEY_ID = "_id";

    // 其它表格欄位名稱
    public static final String photo_background_COLUMN = "_photo_background";
    public static final String photo_enddate_COLUMN = "_photo_enddate";
    public static final String photo_sdate_COLUMN = "_photo_sdate";
    public static final String photo_path_COLUMN = "_photo_path";
    public static final String photo_item_COLUMN = "_photo_item";
    public static final String photo_downloadPath_COLUMN = "_photo_downloadPath";
    public static final String photo_id_COLUMN = "_photo_id";
    // 使用上面宣告的變數建立表格的SQL指令
    public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "( " +

            "_photo_id INTEGER PRIMARY KEY AUTOINCREMENT, " +

            "_photo_background VARCHAR(20), " +

            "_photo_enddate VARCHAR(100)," +

            "_photo_sdate VARCHAR(100)," +

            "_photo_path VARCHAR(300)," +

            "_photo_downloadPath VARCHAR(300)," +

            "_photo_item VARCHAR(300)" +

            ");";


    // 資料庫物件
    private SQLiteDatabase db;

    // 建構子，一般的應用都不需要修改
    public LaunchPhoto(Context context) {
        db = DBHelper.getDatabase(context);
    }

    // 關閉資料庫，一般的應用都不需要修改
    public void close() {
        db.close();
    }

    // 新增參數指定的物件
    public PhotoData insert(PhotoData item) {

        // 建立準備新增資料的ContentValues物件
        ContentValues cv = new ContentValues();


        // 加入ContentValues物件包裝的新增資料
        // 第一個參數是欄位名稱， 第二個參數是欄位的資料
        cv.put(photo_background_COLUMN, item.photo_background);
        cv.put(photo_enddate_COLUMN, item.photo_enddate);
        cv.put(photo_sdate_COLUMN, item.photo_sdate);
        cv.put(photo_path_COLUMN, item.photo_path);
        cv.put(photo_item_COLUMN, item.photo_item);
        cv.put(photo_downloadPath_COLUMN, item.photo_downloadPath);
        // 新增一筆資料並取得編號
        // 第一個參數是表格名稱
        // 第二個參數是沒有指定欄位值的預設值
        // 第三個參數是包裝新增資料的ContentValues物件
        long id = db.insert(TABLE_NAME, null, cv);

        // 設定編號
        //item.setId(id);
        // 回傳結果
        return item;
    }

    // 修改參數指定的物件
    public boolean update(PhotoData item) {
        // 建立準備修改資料的ContentValues物件
        ContentValues cv = new ContentValues();

        // 加入ContentValues物件包裝的修改資料
        // 第一個參數是欄位名稱， 第二個參數是欄位的資料

        cv.put(photo_background_COLUMN, item.photo_background);
        cv.put(photo_enddate_COLUMN, item.photo_enddate);
        cv.put(photo_sdate_COLUMN, item.photo_sdate);
        cv.put(photo_path_COLUMN, item.photo_path);
        cv.put(photo_item_COLUMN, item.photo_item);
        cv.put(photo_downloadPath_COLUMN, item.photo_downloadPath);
        // 設定修改資料的條件為編號
        // 格式為「欄位名稱＝資料」
        String where = "_id" + "=" + item.photo_ID;

        // 執行修改資料並回傳修改的資料數量是否成功
        return db.update(TABLE_NAME, cv, where, null) > 0;
    }

//    // 修改參數指定的物件
//    public boolean UpdateLastTab(String TabName, String WorkID) {
//        // 建立準備修改資料的ContentValues物件
//        ContentValues cv = new ContentValues();
//
//        // 加入ContentValues物件包裝的修改資料
//        // 第一個參數是欄位名稱， 第二個參數是欄位的資料
//
//        cv.put(LastTab_COLUMN, TabName);
//        // 設定修改資料的條件為編號
//        // 格式為「欄位名稱＝資料」
//        String where = "_WorkID" + "=" + WorkID;
//
//        // 執行修改資料並回傳修改的資料數量是否成功
//        return db.update(TABLE_NAME, cv, where, null) > 0;
//    }

    // 刪除參數指定編號的資料
    public boolean delete(long id) {
        // 設定條件為編號，格式為「欄位名稱=資料」
        String where = KEY_ID + "=" + id;
        // 刪除指定編號資料並回傳刪除是否成功
        return db.delete(TABLE_NAME, where, null) > 0;
    }

    // 讀取所有記事資料
    public List<PhotoData> getAll() {
        List<PhotoData> result = new ArrayList<>();
        Cursor cursor = db.query(
                TABLE_NAME, null, null, null, null, null, null, null);

        while (cursor.moveToNext()) {
            result.add(getRecord(cursor));
        }

        cursor.close();
        return result;
    }

    // 讀取所有記事資料
    public List<PhotoData> getAll(String Date) {
        List<PhotoData> result = new ArrayList<>();
        String where = "_photo_sdate" + " <" +"'" +Date + "' and _photo_enddate >'" +Date + "'" ;
        Cursor cursor = db.query
        (TABLE_NAME,		//資料表名稱
                null,	//欄位名稱
                where, // WHERE
                null, // WHERE 的參數
                null, // GROUP BY
                null, // HAVING
                null  // ORDOR BY
		);
        while (cursor.moveToNext()) {
            result.add(getRecord(cursor));
        }

        Log.w("eeeeeeeeeeeeeeeeeeeee",String.valueOf(result.size()));

        cursor.close();
        return result;
    }

    // 取得指定編號的資料物件
    public PhotoData get(String Date) {
        // 準備回傳結果用的物件
        PhotoData item = new PhotoData();
        // 使用編號為查詢條件
        String where = "_photo_update" + "<" +"'" +Date + "' and _photo_enddate >'" +Date + "'" ;
        // 執行查詢
        Cursor result = db.rawQuery("SELECT _photo_id,_photo_background,_photo_path,_photo_update,_photo_enddate,_photo_downloadPath,_photo_item FROM " + TABLE_NAME + where, null);

        // 如果有查詢結果
        if (result.moveToFirst()) {
            // 讀取包裝一筆資料的物件
            item = getRecord(result);
        }

        // 關閉Cursor物件
        result.close();
        // 回傳結果
        return item;
    }

    // 把Cursor目前的資料包裝為物件
    public PhotoData getRecord(Cursor cursor) {
//        _fontsize,_Account,_Password,_WorkID,_Name,_Dept,_Phone,_EName
//        public static final String photo_background_COLUMN = "_photo_background";
//        public static final String photo_enddate_COLUMN = "_photo_enddate";
//        public static final String photo_sdate_COLUMN = "_photo_update";
//        public static final String photo_path_COLUMN = "_photo_path";
//        public static final String photo_id_COLUMN = "_photo_id";

        String photo_background = cursor.getString(cursor.getColumnIndex("_photo_background"));
        String photo_enddate = cursor.getString(cursor.getColumnIndex("_photo_enddate"));
        String photo_sdate = cursor.getString(cursor.getColumnIndex("_photo_sdate"));
        String photo_path = cursor.getString(cursor.getColumnIndex("_photo_path"));
        String photo_id = cursor.getString(cursor.getColumnIndex("_photo_id"));
        String photo_item = cursor.getString(cursor.getColumnIndex("_photo_item"));
        String photo_downloadPath = cursor.getString(cursor.getColumnIndex("_photo_downloadPath"));

        PhotoData result = new PhotoData(photo_background,photo_enddate,photo_sdate,photo_path,photo_id,photo_downloadPath,photo_item);
        // 回傳結果
        return result;
    }

    // 取得資料數量
    public int getCount() {
        int result = 0;
        Cursor cursor = db.rawQuery("SELECT  COUNT(*)  FROM " + TABLE_NAME, null);

        if (cursor.moveToNext()) {
            result = cursor.getInt(0);
        }

        return result;
    }


}