package com.example.yujhaochen.ims;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class ProjectInfo extends Activity {

    private String ModelID;

    private String ModelName;

    private String CloseRate;

    public static Project_Item Project_Item;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_project_info);

        DisplayMetrics metrics = getResources().getDisplayMetrics();

        int screenWidth = (int) (metrics.widthPixels * 0.8);

        int screenHeight = (int) (metrics.heightPixels * 0.7);

        getWindow().setLayout(screenWidth, screenHeight);


        if (Project_Item != null)
        {

            ImageView Img_ProjectInfo = (ImageView)findViewById(R.id.Img_ProjectInfo);

            TextView txt_ProjectInfo_Name  = (TextView)findViewById(R.id.txt_ProjectInfo_Name);

            TextView txt_ProjectInfo_Rate  = (TextView)findViewById(R.id.txt_ProjectInfo_Rate);

            txt_ProjectInfo_Name.setText(Project_Item.GetName());

            txt_ProjectInfo_Rate.setText(Project_Item.GetCloseRate());

            GetServiceData.GetImageByImageLoad(Project_Item.GetImage(),Img_ProjectInfo,R.mipmap.project_default,R.mipmap.project_default);
        }


        ImageView Img_Issue_List = (ImageView)findViewById(R.id.Img_Issue_List);

        Img_Issue_List.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ItemClick(0);
            }
        });

        ImageView Img_Spec = (ImageView)findViewById(R.id.Img_Spec);

        Img_Spec.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ItemClick(1);
            }
        });

        ImageView Img_Member = (ImageView)findViewById(R.id.Img_Member);

        Img_Member.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ItemClick(2);
            }
        });
    }
    
    private void ItemClick(int Position)
    {
        if (Project_Item != null)
        {
            Bundle bundle = new Bundle();

            bundle.putString("ModelID", Project_Item.GetModelID());

            bundle.putString("ModelName", Project_Item.GetName());

            Intent intent = new Intent();

            switch (Position)
            {
                case 0:
                    intent = new Intent(this, IssueList.class);
                    break;
                case 1:
                    intent = new Intent(this, ProjectSpec.class);
                    break;
                case 2:
                    intent = new Intent(this, Project_Member.class);
                    break;
            }

            intent.putExtras(bundle);

            startActivity(intent);
        }

    }

    private void SpecClick()
    {
        // 建立啟動另一個Activity元件需要的Intent物件
        // 建構式的第一個參數：「this」
        // 建構式的第二個參數：「Activity元件類別名稱.class」
        Intent intent = new Intent(this, ProjectSpec.class);
        // 呼叫「startActivity」，參數為一個建立好的Intent物件
        // 這行敘述執行以後，如果沒有任何錯誤，就會啟動指定的元件
        startActivity(intent);

    }

    private void MemberClick()
    {
        // 建立啟動另一個Activity元件需要的Intent物件
        // 建構式的第一個參數：「this」
        // 建構式的第二個參數：「Activity元件類別名稱.class」
        Intent intent = new Intent(this, Project_Member.class);
        // 呼叫「startActivity」，參數為一個建立好的Intent物件
        // 這行敘述執行以後，如果沒有任何錯誤，就會啟動指定的元件
        startActivity(intent);

    }

}
