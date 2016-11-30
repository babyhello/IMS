package com.example.yujhaochen.ims;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Welcome extends Activity {


    private Context _Context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);


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
