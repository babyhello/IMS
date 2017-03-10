package com.example.yujhaochen.ims;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class IssueList extends Activity {

    private SwipeRefreshLayout mSwipeRefreshLayout;
    List<Issue_Item> Issue_List = new ArrayList<Issue_Item>();
    private ListView lsv_main;
    private IssueAdapter mListAdapter;

    private String ModelName;

    private String ModelID;

    private ProgressDialog pDialog;

    private Boolean flag_loading = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_issue_list);
        pDialog = new ProgressDialog(this);
        //View v = inflater.inflate(R.layout.fragment_my_issue, container, false);
        //宣告 ListView 元件
        lsv_main = (ListView)findViewById(R.id.listView);

        lsv_main.setOnItemClickListener(listViewOnItemClickListener);

        Bundle Bundle = this.getIntent().getExtras();

        ModelID = Bundle.getString("ModelID");

        ModelName = Bundle.getString("ModelName");
        String WorkID = UserData.WorkID;

        if (!TextUtils.isEmpty(WorkID)  )
        {
            Find_Issue_List(ModelID,WorkID);
        }




        TextView txt_IssueList_ModelName= (TextView)findViewById(R.id.txt_IssueList_ModelName);

        txt_IssueList_ModelName.setText(ModelName);

        ImageView Img_New_Issue= (ImageView)findViewById(R.id.Img_New_Issue);

        Img_New_Issue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                NewIssue();
            }
        });

        mSwipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.refresh_layout);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefreshLayout.setRefreshing(false);

                String WorkID = UserData.WorkID;

                if (!TextUtils.isEmpty(WorkID)  )
                {
                    Find_Issue_List(ModelID,WorkID);
                }

            }
        });

    }

    @Override
    public void onResume()
    {
        String WorkID = UserData.WorkID;

        if (!TextUtils.isEmpty(WorkID)  )
        {
            Find_Issue_List(ModelID,WorkID);
        }

        super.onResume();
    }

    private void NewIssue()
    {
        Bundle bundle = new Bundle();

        bundle.putString("ModelID", ModelID);

        bundle.putString("ModelName", ModelName);

        Intent intent = new Intent();

        intent = new Intent(this, NewIssue.class);

        intent.putExtras(bundle);

        startActivity(intent);

    }

    private void Find_Issue_List(String PM_ID,String F_Keyin) {

        pDialog.setMessage("Loading...");

        pDialog.show();

        RequestQueue mQueue = Volley.newRequestQueue(this);

        String Path = GetServiceData.ServicePath + "/Find_Issue_List_Advantage?PM_ID=" + PM_ID +"&F_Keyin=" + F_Keyin;

        GetServiceData.getString(Path, mQueue, new GetServiceData.VolleyCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                IssueDataMapping(result);

                pDialog.hide();
            }
        });

    }

    private void IssueDataMapping(JSONObject result)
    {
        try
        {
            Issue_List.clear();

            JSONArray UserArray = new JSONArray(result.getString("Key"));

            for (int i = 0;i< UserArray.length();i++)
            {
                JSONObject IssueData =  UserArray.getJSONObject(i);

                String F_SeqNo = IssueData.getString("F_SeqNo");

                String F_Owner = IssueData.getString("F_Owner");

                String F_Status = IssueData.getString("F_Status");

                String F_Status_Display = IssueData.getString("F_Status_Display");

                String F_Priority = IssueData.getString("F_Priority");

                String F_Subject = IssueData.getString("F_Subject");

                String Issue_Author = IssueData.getString("Issue_Author");

                String Issue_Owner = IssueData.getString("Issue_Owner");

                String F_CreateDate =AppClass.ConvertDateString(IssueData.getString("F_CreateDate"));

                String Read = String.valueOf(IssueData.getInt("Read"));

                String CommentRead = String.valueOf(IssueData.getInt("CommentRead"));

                Issue_List.add(i,new Issue_Item("00" + F_SeqNo,ModelName,F_Subject,F_CreateDate,F_Priority,CommentRead,Read));
            }

            // ListView 中所需之資料參數可透過修改 Adapter 的建構子傳入
            mListAdapter = new IssueAdapter(this, Issue_List,"ProjectList");

            //設定 ListView 的 Adapter
            lsv_main.setAdapter(mListAdapter);


        }
        catch (JSONException ex)
        {

        }
    }

    private AdapterView.OnItemClickListener listViewOnItemClickListener
            = new AdapterView.OnItemClickListener()
    {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
        {
            IssueAdapter IssueAdapter = (IssueAdapter)parent.getAdapter();

            Issue_Item Issue_Item = (Issue_Item)IssueAdapter.getItem(position);

            GoIssueInfo(Issue_Item.GetID());
        }
    };

    private void GoIssueInfo(String IssueID)
    {

        Bundle bundle = new Bundle();

        bundle.putString("IssueID", IssueID);
        // 建立啟動另一個Activity元件需要的Intent物件
        // 建構式的第一個參數：「this」
        // 建構式的第二個參數：「Activity元件類別名稱.class」
        Intent intent = new Intent(this, IssueInfo.class);

        intent.putExtras(bundle);
        // 呼叫「startActivity」，參數為一個建立好的Intent物件
        // 這行敘述執行以後，如果沒有任何錯誤，就會啟動指定的元件
        startActivity(intent);
    }
}
