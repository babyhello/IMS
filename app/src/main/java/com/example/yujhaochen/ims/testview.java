package com.example.yujhaochen.ims;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class testview extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testview);

        UserDB UserDB = new UserDB(getApplicationContext());

        UserData UserData = new UserData("Markycchen","a22835518","10015667","陳俞兆","設計品質驗證三部三課","2842","Markycchen","");

        UserDB.insert(UserData);

        Button button = (Button) findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                GetData();

            }
        });

    }

    private void GetData()
    {
        UserDB UserDB = new UserDB(this);

        //如果進來程式有資料的話就不用再登入
        if (UserDB.getCount() > 0)
        {
            System.out.println(UserDB.getAll().get(0).WorkID);

        }
    }
}
