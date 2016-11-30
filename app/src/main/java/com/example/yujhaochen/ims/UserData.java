package com.example.yujhaochen.ims;

/**
 * Created by yujhaochen on 2016/10/25.
 */
public class UserData {

    public static String Account;

    public static String Password;

    public static String WorkID;

    public static String Name;

    public static String Dept;

    public static String Phone;

    public static String EName;

    public UserData()
    {


    }

    public UserData(String Account,String Password,String WorkID,String Name,String Dept,String Phone,String EName)
    {
        this.Account = Account;

        this.Password = Password;

        this.WorkID = WorkID;

        this.Name = Name;

        this.Dept = Dept;

        this.Phone = Phone;

        this.EName = EName;
    }

}
