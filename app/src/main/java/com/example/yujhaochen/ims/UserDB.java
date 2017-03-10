package com.example.yujhaochen.ims;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

// 資料功能類別
public class UserDB {
    // 表格名稱
    public static final String TABLE_NAME = "UserData";

    // 編號表格欄位名稱，固定不變
    public static final String KEY_ID = "_id";

    // 其它表格欄位名稱
    public static final String Font_COLUMN = "_fontsize";
    public static final String Account_COLUMN = "_Account";
    public static final String Password_COLUMN = "_Password";
    public static final String WorkID_COLUMN = "_WorkID";
    public static final String Name_COLUMN = "_Name";
    public static final String Dept_COLUMN = "_Dept";
    public static final String Phone_COLUMN = "_Phone";
    public static final String EName_COLUMN = "_EName";
    public static final String LastTab_COLUMN = "_LastTab";
    // 使用上面宣告的變數建立表格的SQL指令
    public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "( " +

            "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +

            "_fontsize VARCHAR(10), " +

            "_Account VARCHAR(100)," +

            "_Password VARCHAR(100)," +

            "_WorkID VARCHAR(30)," +

            "_Name VARCHAR(100)," +

            "_Dept VARCHAR(20)," +

            "_Phone VARCHAR(20)," +

            "_EName VARCHAR(50)," +

            "_LastTab VARCHAR(50)" +
            ");";


    // 資料庫物件
    private SQLiteDatabase db;

    // 建構子，一般的應用都不需要修改
    public UserDB(Context context) {
        db = DBHelper.getDatabase(context);
    }

    // 關閉資料庫，一般的應用都不需要修改
    public void close() {
        db.close();
    }

    // 新增參數指定的物件
    public UserData insert(UserData item) {

        // 建立準備新增資料的ContentValues物件
        ContentValues cv = new ContentValues();


        // 加入ContentValues物件包裝的新增資料
        // 第一個參數是欄位名稱， 第二個參數是欄位的資料
        cv.put(Font_COLUMN,"0");
        cv.put(Account_COLUMN, item.Account);
        cv.put(Password_COLUMN, item.Password);
        cv.put(WorkID_COLUMN, item.WorkID);
        cv.put(Name_COLUMN, item.Name);
        cv.put(Dept_COLUMN, item.Dept);
        cv.put(Phone_COLUMN, item.Phone);
        cv.put(EName_COLUMN, item.EName);
        cv.put(LastTab_COLUMN, item.LastTab);
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
    public boolean update(UserData item) {
        // 建立準備修改資料的ContentValues物件
        ContentValues cv = new ContentValues();

        // 加入ContentValues物件包裝的修改資料
        // 第一個參數是欄位名稱， 第二個參數是欄位的資料
        cv.put(Font_COLUMN,"0");
        cv.put(Account_COLUMN, item.Account);
        cv.put(Password_COLUMN, item.Password);
        cv.put(WorkID_COLUMN, item.WorkID);
        cv.put(Name_COLUMN, item.Name);
        cv.put(Dept_COLUMN, item.Dept);
        cv.put(Phone_COLUMN, item.Phone);
        cv.put(EName_COLUMN, item.EName);
        cv.put(LastTab_COLUMN, item.LastTab);
        // 設定修改資料的條件為編號
        // 格式為「欄位名稱＝資料」
        String where = "_WorkID" + "=" + item.WorkID;

        // 執行修改資料並回傳修改的資料數量是否成功
        return db.update(TABLE_NAME, cv, where, null) > 0;
    }

    // 修改參數指定的物件
    public boolean UpdateLastTab(String TabName,String WorkID) {
        // 建立準備修改資料的ContentValues物件
        ContentValues cv = new ContentValues();

        // 加入ContentValues物件包裝的修改資料
        // 第一個參數是欄位名稱， 第二個參數是欄位的資料

        cv.put(LastTab_COLUMN, TabName);
        // 設定修改資料的條件為編號
        // 格式為「欄位名稱＝資料」
        String where = "_WorkID" + "=" + WorkID;

        // 執行修改資料並回傳修改的資料數量是否成功
        return db.update(TABLE_NAME, cv, where, null) > 0;
    }

    // 刪除參數指定編號的資料
    public boolean delete(long id){
        // 設定條件為編號，格式為「欄位名稱=資料」
        String where = KEY_ID + "=" + id;
        // 刪除指定編號資料並回傳刪除是否成功
        return db.delete(TABLE_NAME, where , null) > 0;
    }

    // 讀取所有記事資料
    public List<UserData> getAll() {
        List<UserData> result = new ArrayList<>();
        Cursor cursor = db.query(
                TABLE_NAME, null, null, null, null, null, null, null);

        while (cursor.moveToNext()) {
            result.add(getRecord(cursor));
        }

        cursor.close();
        return result;
    }

    // 取得指定編號的資料物件
    public UserData get(String WorkID) {
        // 準備回傳結果用的物件
        UserData item = new UserData();
        // 使用編號為查詢條件
        String where = "_WorkID" + "=" + WorkID;
        // 執行查詢
        Cursor result = db.rawQuery("SELECT _fontsize,_Account,_Password,_WorkID,_Name,_Dept,_Phone,_EName,_LastTab FROM "+ TABLE_NAME + where, null);

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
    public UserData getRecord(Cursor cursor) {
//        _fontsize,_Account,_Password,_WorkID,_Name,_Dept,_Phone,_EName




        String Account = cursor.getString(cursor.getColumnIndex("_Account"));
        String Password = cursor.getString(cursor.getColumnIndex("_Password"));
        String WorkID = cursor.getString(cursor.getColumnIndex("_WorkID"));
        String Name = cursor.getString(cursor.getColumnIndex("_Name"));
        String Dept = cursor.getString(cursor.getColumnIndex("_Dept"));
        String Phone = cursor.getString(cursor.getColumnIndex("_Phone"));
        String EName = cursor.getString(cursor.getColumnIndex("_EName"));
        String LastTab = cursor.getString(cursor.getColumnIndex("_LastTab"));

        UserData result = new UserData(Account,Password,WorkID,Name,Dept,Phone,EName,LastTab);
        // 回傳結果
        return result;
    }

    // 取得資料數量
    public int getCount() {
        int result = 0;
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_NAME, null);

        if (cursor.moveToNext()) {
            result = cursor.getInt(0);
        }

        return result;
    }


}