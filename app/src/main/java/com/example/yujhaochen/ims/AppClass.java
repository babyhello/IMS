package com.example.yujhaochen.ims;

import android.accessibilityservice.GestureDescription;
import android.app.AlertDialog;
import android.content.Context;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import 	java.util.Date;
/**
 * Created by yujhaochen on 2016/10/25.
 */
public class AppClass {

    public static void AlertMessage (String AlertMessage, Context Context)
    {
        AlertDialog.Builder MyAlertDialog = new AlertDialog.Builder(Context);

        MyAlertDialog.setMessage(AlertMessage);

        MyAlertDialog.show();
    }

    public static String ConvertDateString(String Date)
    {


        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = format.parse(Date);

            format = new SimpleDateFormat("yyyy-MM-dd");

            Date = format.format(date.getTime());

        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return Date;
    }

    public static int PriorityImage(String PriorityString)
    {
        int PriorityImage = 0;

        switch (PriorityString)
        {
            case "1":
                PriorityImage = R.mipmap.projectlist_ic_issue_priop1;

                return PriorityImage;

            case "2":
                PriorityImage = R.mipmap.projectlist_ic_issue_priop2;

                return PriorityImage;

            case "3":
                PriorityImage = R.mipmap.projectlist_ic_issue_priop3;

                return PriorityImage;

        }

        return PriorityImage;
    }
}
