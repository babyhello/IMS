package com.apps.ims;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

public class Welcome extends Activity {


    private Context _Context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.apps.ims.R.layout.activity_welcome);


//        test

        FirebaseMessaging.getInstance().subscribeToTopic("dogs");
        //這裡來檢測版本是否需要更新
        _Context = this;

        String token = FirebaseInstanceId.getInstance().getToken();

        //Log.w("Token",token);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub

                UserDB UserDB = new UserDB(Welcome.this);

                //如果進來程式有資料的話就不用再登入
                if (UserDB.getCount() > 0) {

                    UserData UserData = new UserData();

                    UserData = UserDB.getAll().get(0);

                    Intent intent = new Intent(Welcome.this, MainTab.class);

                    startActivity(intent);

                    finish();
                } else {
                    Intent intent = new Intent(Welcome.this, LoginAccount.class);
                    startActivity(intent);
                    Welcome.this.finish();

                }


            }
        }, 2000);//两秒后跳转到另一个页面


    }


}
