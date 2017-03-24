package com.example.yujhaochen.ims;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ProjectSpec extends AppCompatActivity {

    List<Spec_Item> Spec_List = new ArrayList<Spec_Item>();
    private ListView lsv_main;
    private SpecAdapter mListAdapter;
    private ProgressDialog pDialog;
    String ModelName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_spec);
        pDialog = new ProgressDialog(ProjectSpec.this);

        //View v = inflater.inflate(R.layout.fragment_my_issue, container, false);
        //宣告 ListView 元件
        lsv_main = (ListView)findViewById(R.id.listView);

        lsv_main.setOnItemClickListener(listViewOnItemClickListener);



        Bundle bundle = this.getIntent().getExtras();

        String ModelID = bundle.getString("ModelID");

        ModelName = bundle.getString("ModelName");

        if(!ModelID.matches(("")))
        {
            GetPM_SpecData(ModelID);
        }

        setTitle(ModelName);


    }

    private AdapterView.OnItemClickListener listViewOnItemClickListener
            = new AdapterView.OnItemClickListener()
    {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
        {
//            IssueAdapter IssueAdapter = (IssueAdapter)parent.getAdapter();
//
//            GoToIssueInfo(IssueAdapter.getItem(position).);


        }
    };


    private void GetPM_SpecData(String PM_ID) {

        pDialog.setTitle("Loading...");

        pDialog.show();

        RequestQueue mQueue = Volley.newRequestQueue(this);

        String Path = GetServiceData.ServicePath + "/Find_Model_Spec?PM_ID=" + PM_ID;

        GetServiceData.getString(Path, mQueue, new GetServiceData.VolleyCallback() {
            @Override
            public void onSuccess(JSONObject result) {

                SpecDataMapping(result);

                pDialog.hide();
            }
        });

    }

    private void SpecDataMapping(JSONObject result)
    {
        try
        {
            Spec_List.clear();

            JSONArray UserArray = new JSONArray(result.getString("Key"));

            for (int i = 0;i< UserArray.length();i++)
            {
                JSONObject ModelData =  UserArray.getJSONObject(i);

                String F_FieldName = ModelData.getString("F_FieldName");

                String F_FieldValue = ModelData.getString("F_SpecData").replace("<br>","\n");

                Spec_List.add(i,new Spec_Item(F_FieldName,F_FieldValue));
            }

            // ListView 中所需之資料參數可透過修改 Adapter 的建構子傳入
            mListAdapter = new SpecAdapter(this, Spec_List);


            lsv_main.setEmptyView(findViewById(R.id.emptyview));
            //設定 ListView 的 Adapter
            lsv_main.setAdapter(mListAdapter);
        }
        catch (JSONException ex)
        {

        }



    }
}
