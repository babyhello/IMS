package com.example.yujhaochen.ims;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.example.yujhaochen.ims.R.id.Img_Star;

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

        int screenHeight = (int) (metrics.heightPixels * 0.78);

        getWindow().setLayout(screenWidth, screenHeight);




        if (Project_Item != null && !UserData.WorkID.matches("")) {

            Find_Model_Detail(Project_Item.GetModelID(),UserData.WorkID);

        }


        final ImageView Img_Star = (ImageView) findViewById(R.id.Img_Star);

        Img_Star.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                if (UserData.WorkID != "")
                {
                    project_expandtable.ResumeFlag = true;

                    FavoritClick(UserData.WorkID,"",Project_Item.ModelID);


                    if (Img_Star.getDrawable().getConstantState() == getResources().getDrawable( R.mipmap.btn_star_nor).getConstantState())
                    {
                        Img_Star.setImageResource(R.mipmap.btn_star_sel);
                    }
                    else
                    {
                        Img_Star.setImageResource(R.mipmap.btn_star_nor);
                    }
                }
            }
        });

        ImageView Img_Issue_List = (ImageView) findViewById(R.id.Img_Issue_List);

        Img_Issue_List.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ItemClick(0);
            }
        });

        ImageView Img_Spec = (ImageView) findViewById(R.id.Img_Spec);

        Img_Spec.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ItemClick(1);
            }
        });

        ImageView Img_Member = (ImageView) findViewById(R.id.Img_Member);

        Img_Member.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ItemClick(2);
            }
        });

        ImageView Img_New_Issue = (ImageView) findViewById(R.id.Img_New_Issue);

        Img_New_Issue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                ItemClick(3);
            }
        });
    }

    private void Get_Model_Detail(String ModelID,String WorkID) {

        RequestQueue mQueue = Volley.newRequestQueue(this);

        String Path = GetServiceData.ServicePath + "/Find_Model_Detail?ModelID=" + ModelID + "&WorkID=" + WorkID;

        GetServiceData.getString(Path, mQueue, new GetServiceData.VolleyCallback() {
            @Override
            public void onSuccess(JSONObject result) {


                ModelData_Detail(result);
            }
        });

    }

    private void ModelData_Detail(JSONObject result) {



        try {

            JSONArray UserArray = new JSONArray(result.getString("Key"));

            if (UserArray.length() > 0) {



                JSONObject IssueData = UserArray.getJSONObject(0);

                String P1 = IssueData.getString("P1");

                String ModelID = String.valueOf(IssueData.getInt("ModelID"));

                String ModelPic = IssueData.getString("ModelPic");

                boolean Model_Favorit = IssueData.getBoolean("Model_Favorit");

                Double CloseRate = IssueData.getDouble("CloseRate");

                String CurrentStage = IssueData.getString("CurrentStage");

                String MarketName = IssueData.getString("MarketName");

                //final ImageView Img_ProjectInfo = (ImageView) findViewById(R.id.Img_ProjectInfo);

                final ImageView Img_Project_Pic_Large = (ImageView) findViewById(R.id.Img_Project_Pic_Large);

                //final LinearLayout Rel_Project_Layout = (LinearLayout) findViewById(R.id.Rel_Project_Layout);

                TextView txt_ProjectInfo_Name = (TextView) findViewById(R.id.txt_ProjectInfo_Name);

                TextView txt_ProjectInfo_Rate = (TextView) findViewById(R.id.txt_ProjectInfo_Rate);

                TextView txt_Model_Stage = (TextView) findViewById(R.id.txt_Model_Stage);

                TextView txt_Model_MarketName = (TextView) findViewById(R.id.txt_Model_MarketName);

                TextView txt_Model_Priority = (TextView) findViewById(R.id.txt_Model_Priority);

                if (!MarketName.contains("null"))
                {
                    txt_Model_MarketName.setText(MarketName);
                }

                if (!P1.contains("null"))
                {
                    txt_Model_Priority.setText(P1);
                }

                txt_Model_Stage.setText(CurrentStage);

                txt_ProjectInfo_Name.setText(Project_Item.GetName());

                try {

                    txt_ProjectInfo_Rate.setText(String.format("%.0f", CloseRate * 100)+ "%");

                    if (CloseRate * 100 < 80)
                    {
                        txt_ProjectInfo_Rate.setTextColor(getResources().getColor(R.color.ProjectInfo_WarningColor));
                    }


                } catch (NumberFormatException e) {
                    txt_ProjectInfo_Rate.setText("0%");
                }

                ImageView Img_Star = (ImageView) findViewById(R.id.Img_Star);

                if (!Model_Favorit)
                {
                    Img_Star.setImageResource(R.mipmap.btn_star_nor);
                }
                else

                {
                    Img_Star.setImageResource(R.mipmap.btn_star_sel);
                }


                try
                {
                    Glide.with(ProjectInfo.this)
                            .load(GetServiceData.ServicePath + "/Get_File?FileName=" + ModelPic)
                            .asBitmap()
                            .into(new SimpleTarget<Bitmap>(300, 300) {
                                @Override
                                public void onResourceReady(Bitmap bitmap, GlideAnimation anim) {

                                    //BitmapDrawable ob = new BitmapDrawable(getResources(), AppClass.roundCornerImage(bitmap,0));
                                    //Img_ProjectInfo.setBackground(ob);

                                    BitmapDrawable ob = new BitmapDrawable(getResources(),bitmap);
                                    Img_Project_Pic_Large.setImageBitmap(ob.getBitmap());
                                    //Img_Project_Pic_Large.setBackground(ob);
                                    //Rel_Project_Layout.setBackground(ob);


                                }
                            });
                }
                catch(Exception ex)
                {
                    Log.w("PrintMessage", ex.toString());
                }



            }
        } catch (JSONException ex) {

            Log.e("CloseRate", String.valueOf("TestColse"));

            Log.w("Exceptionxxxxxxxxxx",ex.toString());

        }


    }


    private void FavoritClick(String F_Keyin,String F_Owner,String F_PM_ID) {

        RequestQueue mQueue = Volley.newRequestQueue(ProjectInfo.this);

        String Path = GetServiceData.ServicePath + "/Insert_Favorit_Model" + "?F_Keyin=" + F_Keyin + "&F_Owner=" + F_Owner + "&F_PM_ID=" + F_PM_ID;

        GetServiceData.getString(Path, mQueue, new GetServiceData.VolleyCallback() {
            @Override
            public void onSuccess(JSONObject result) {


                try {


                    JSONArray UserArray = new JSONArray(result.getString("Key"));

                    if (UserArray.length() > 0) {

                        JSONObject IssueData = UserArray.getJSONObject(0);

                        String Favorit_Model = String.valueOf(IssueData.getInt("Favorit_Model"));

                        ImageView Img_Star = (ImageView) findViewById(R.id.Img_Star);

//                        AppClass.makeTextAndShow(ProjectInfo.this,Favorit_Model,2);
//
//                        if (Favorit_Model.equals("0"))
//                        {
//                            Img_Star.setImageResource(R.mipmap.btn_star_nor);
//                        }
//                        else
//
//                        {
//                            Img_Star.setImageResource(R.mipmap.btn_star_sel);
//                        }


                    }
                } catch (JSONException ex) {

                    Log.w("exception",ex.toString());
                }

            }
        });

    }



//    http://wtsc.msi.com.tw/IMS/IMS_App_Service.asmx/Find_Model_Detail

    private void Find_Model_Detail(String ModelID,String WorkID) {

        RequestQueue mQueue = Volley.newRequestQueue(this);

        String Path = GetServiceData.ServicePath + "/Find_Model_Detail?ModelID=" + ModelID + "&WorkID=" + WorkID;

        GetServiceData.getString(Path, mQueue, new GetServiceData.VolleyCallback() {
            @Override
            public void onSuccess(JSONObject result) {



                ModelData_Detail(result);
            }
        });

    }


    private void NewIssue() {
        Bundle bundle = new Bundle();

        bundle.putString("ModelID", Project_Item.GetModelID());

        bundle.putString("ModelName", Project_Item.GetName());

        Intent intent = new Intent();

        intent = new Intent(this, NewIssue.class);

        intent.putExtras(bundle);

        startActivity(intent);

        finish();

    }

    private void ItemClick(int Position) {
        if (Project_Item != null) {
            Bundle bundle = new Bundle();

            bundle.putString("ModelID", Project_Item.GetModelID());

            bundle.putString("ModelName", Project_Item.GetName());

            Intent intent = new Intent();

            switch (Position) {
                case 0:
                    intent = new Intent(this, IssueList.class);

                    intent.putExtras(bundle);

                    startActivity(intent);

                    finish();
                    break;
                case 1:
                    intent = new Intent(this, ProjectSpec.class);

                    intent.putExtras(bundle);

                    startActivity(intent);

                    finish();
                    break;
                case 2:
                    intent = new Intent(this, Project_Member.class);

                    intent.putExtras(bundle);

                    startActivity(intent);

                    finish();
                    break;
                case 3:

                    NewIssue();
                    break;
            }


        }

    }

    private void SpecClick() {
        // 建立啟動另一個Activity元件需要的Intent物件
        // 建構式的第一個參數：「this」
        // 建構式的第二個參數：「Activity元件類別名稱.class」
        Intent intent = new Intent(this, ProjectSpec.class);
        // 呼叫「startActivity」，參數為一個建立好的Intent物件
        // 這行敘述執行以後，如果沒有任何錯誤，就會啟動指定的元件
        startActivity(intent);

    }

    private void MemberClick() {
        // 建立啟動另一個Activity元件需要的Intent物件
        // 建構式的第一個參數：「this」
        // 建構式的第二個參數：「Activity元件類別名稱.class」
        Intent intent = new Intent(this, Project_Member.class);
        // 呼叫「startActivity」，參數為一個建立好的Intent物件
        // 這行敘述執行以後，如果沒有任何錯誤，就會啟動指定的元件
        startActivity(intent);

    }

}
