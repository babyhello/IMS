package com.example.yujhaochen.ims;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static java.security.AccessController.getContext;

public class Welcome extends Activity {


    private Context _Context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        String token = FirebaseInstanceId.getInstance().getToken();
        Log.d("FCM", "Token:"+token);

        FirebaseMessaging.getInstance().subscribeToTopic("dogs");
        //這裡來檢測版本是否需要更新
        _Context = this;

        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                // TODO Auto-generated method stub

                UserDB UserDB = new UserDB(Welcome.this);

                //如果進來程式有資料的話就不用再登入
                if (UserDB.getCount() > 0)
                {

                    UserData UserData = new UserData();

                    UserData = UserDB.getAll().get(0);

                    Intent intent = new Intent(Welcome.this, MainTab.class);

                    startActivity(intent);

                    finish();
                }
                else
                {
                    Intent intent = new Intent(Welcome.this, LoginAccount.class);
                    startActivity(intent);
                    Welcome.this.finish();

                }



            }
        }, 2000);//两秒后跳转到另一个页面


    }




}
