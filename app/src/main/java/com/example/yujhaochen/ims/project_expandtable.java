package com.example.yujhaochen.ims;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.acl.Group;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.R.attr.width;
import static com.example.yujhaochen.ims.R.id.listView;

public class project_expandtable extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private List<String> GroupItem = new ArrayList<String>();

    private List<Project_Item> Project_List = new ArrayList<Project_Item>();

    private List<Project_Item> Focus_Project_List = new ArrayList<Project_Item>();

    private List<Project_Item> NotFocus_Project_List = new ArrayList<Project_Item>();

    private List<Project_Item> Top_Project_List = new ArrayList<Project_Item>();

    private ArrayList<List<Project_Item>> ProjectGroup_List = new ArrayList<List<Project_Item>>();

    private Context ProjectContext;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    private ExpandableListView elv;

    private String mParam1;

    private String mParam2;

    private ExpandableAdapter viewAdapter;

    private boolean flag_loading = false;

    private ProgressDialog pDialog;

    private int SortType = 0 ;

    public static boolean ResumeFlag = false;

    private boolean Lsv_Collspan = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    // TODO: Rename and change types and number of parameters
    public project_expandtable newInstance(String param1, String param2) {
        project_expandtable fragment = new project_expandtable();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);


        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_project_expandtable, container, false);

        pDialog = new ProgressDialog(getContext());

        ProjectContext = getActivity();

        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.refresh_layout);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefreshLayout.setRefreshing(false);

                if (!UserData.WorkID.matches("")) {
                    GetPM_Data(UserData.WorkID, "1", "25");

                    Log.w("ReloadData", "GetPMDAtat ReFresh");
                }
            }
        });


        if (!UserData.WorkID.matches("")) {
            GetPM_Data(UserData.WorkID, "1", "25");

            Log.w("ReloadData", "GetPMDAtatInit");
        }

        DisplayMetrics metrics = new DisplayMetrics();

        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);


        elv = (ExpandableListView) v.findViewById(R.id.mExpandableListView);

        elv.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
            @Override
            public void onGroupCollapse(int groupPosition) {

                Lsv_Collspan = true;

                //elv.collapseGroup(groupPosition);
            }
        });


        elv.setIndicatorBounds(metrics.widthPixels - GetDipsFromPixel(35), metrics.widthPixels - GetDipsFromPixel(5));

        elv.setOnScrollListener(new AbsListView.OnScrollListener() {

            public void onScrollStateChanged(AbsListView view, int scrollState) {


            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {


                if (firstVisibleItem + visibleItemCount == totalItemCount && totalItemCount != 0) {

                    if (flag_loading == false) {

                        if (Lsv_Collspan) {
                            Lsv_Collspan = false;
                        } else {
                            flag_loading = true;

                            Add_Item(UserData.WorkID, String.valueOf(totalItemCount + 1), String.valueOf(totalItemCount + visibleItemCount + 1));

                            Lsv_Collspan = false;
                        }


                    }
                }
            }
        });


        return v;
    }

    public void Project_Sort()
    {


        AlertDialog.Builder builderSingle = new AlertDialog.Builder(ProjectContext);
        //builderSingle.setIcon(R.drawable.ic_launcher);
        //builderSingle.setTitle("Sort");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(ProjectContext, android.R.layout.select_dialog_singlechoice);

        arrayAdapter.add("Default");

        arrayAdapter.add("Latest Activities");

        builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builderSingle.setSingleChoiceItems(arrayAdapter,SortType, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                SortType = which;

                if (!UserData.WorkID.matches("")) {
                    GetPM_Data(UserData.WorkID, "1", "25");

                    Log.w("ReloadData", "GetPMDAtat Sort");
                }

                dialog.dismiss();
            }
        });



        builderSingle.show();
    }

    @Override
    public void onResume() {

//        if (ResumeFlag)
//        {
//            if (!UserData.WorkID.matches("")) {
//                GetPM_Data(UserData.WorkID, "1", "25");
//
//                Log.w("ReloadData","GetPMDAtat Resume");
//            }
//            ResumeFlag = false;
//        }



        super.onResume();
    }

    public int GetDipsFromPixel(float pixels) {
        // Get the screen's density scale
        final float scale = getResources().getDisplayMetrics().density;
        // Convert the dps to pixels, based on density scale
        return (int) (pixels * scale + 0.5f);
    }

    private void GetPM_Data(String WorkID, String Start, String End) {


//        pDialog.setTitle("Loading...");
//
//        pDialog.show();

        RequestQueue mQueue = Volley.newRequestQueue(ProjectContext);

        String Path = "";

        if (SortType == 0)
        {
            Path = GetServiceData.ServicePath + "/Find_Project_List_By_Start_End?WorkID=" + WorkID + "&Start=" + Start + "&End=" + End;
        }
        else
        {
            Path = GetServiceData.ServicePath + "/Find_Project_LastActive?WorkID=" + WorkID + "&Start=" + Start + "&End=" + End;
        }


        GetServiceData.getString(Path, mQueue, new GetServiceData.VolleyCallback() {
            @Override
            public void onSuccess(JSONObject result) {


                PMData_Mapping(result);

                // pDialog.hide();
            }
        });

    }

    private void Add_Item(String WorkID, String Start, String End) {

        //Toast.makeText(getContext(),"Load More",1).show();



        RequestQueue mQueue = Volley.newRequestQueue(ProjectContext);

        String Path = "";

        if (SortType == 0)
        {
            Path = GetServiceData.ServicePath + "/Find_Project_List_By_Start_End?WorkID=" + WorkID + "&Start=" + Start + "&End=" + End;
        }
        else
        {
            Path = GetServiceData.ServicePath + "/Find_Project_LastActive?WorkID=" + WorkID + "&Start=" + Start + "&End=" + End;
        }

        GetServiceData.getString(Path, mQueue, new GetServiceData.VolleyCallback() {
            @Override
            public void onSuccess(JSONObject result) {

                try {

                    JSONArray UserArray = new JSONArray(result.getString("Key"));

                    for (int i = 0; i < UserArray.length(); i++) {
                        JSONObject ModelData = UserArray.getJSONObject(i);

                        String ModelID = String.valueOf(ModelData.getInt("ModelID"));

                        String ModelName = ModelData.getString("ModelName");

                        String ModelPic = ModelData.getString("ModelPic");

                        String Model_Focus = ModelData.getString("Model_Focus");

                        String Read = String.valueOf(ModelData.getInt("Read"));

                        Boolean Model_Focus_Type = false;

                        viewAdapter.addItem(1, new Project_Item(ModelID, ModelName, ModelPic, "", Model_Focus, Read));

                        viewAdapter.notifyDataSetChanged();

                        flag_loading = false;
                    }

                    for (int i = 0; i < GroupItem.size(); i++) {
                        elv.expandGroup(i);
                    }

                } catch (JSONException ex) {
//            Toast.makeText(Project.this,ex.getMessage(), Toast.LENGTH_LONG).show();
                    Log.w("Error", ex.toString());
                }


                //pDialog.hide();


            }
        });
    }

    private void PMData_Mapping(JSONObject result) {


        try {
            Project_List.clear();

            Top_Project_List.clear();

            Focus_Project_List.clear();

            NotFocus_Project_List.clear();

            ProjectGroup_List.clear();

            GroupItem.clear();

            JSONArray UserArray = new JSONArray(result.getString("Key"));

            for (int i = 0; i < UserArray.length(); i++) {
                JSONObject ModelData = UserArray.getJSONObject(i);

                String ModelID = String.valueOf(ModelData.getInt("ModelID"));

                String ModelName = ModelData.getString("ModelName");

                String ModelPic = ModelData.getString("ModelPic");

                //String CloseRate = String.valueOf(ModelData.getDouble("CloseRate"));

                String Model_Focus = ModelData.getString("Model_Focus");

                String Read = String.valueOf(ModelData.getInt("Read"));

                Project_List.add(i, new Project_Item(ModelID, ModelName, ModelPic, "", Model_Focus, Read));
            }


            for (Project_Item e : Project_List) {

                if (e.GetFocusType().equals("Favorit")) {
                    Focus_Project_List.add(e);
                }
                else if(e.GetFocusType().equals("Default"))
                {
                    Top_Project_List.add(e);
                }
                else {
                    NotFocus_Project_List.add(e);
                }
            }

            Log.w("NotFocus_Project_List", String.valueOf(NotFocus_Project_List.size()));

            ProjectGroup_List.add(Top_Project_List);

            ProjectGroup_List.add(Focus_Project_List);

            ProjectGroup_List.add(NotFocus_Project_List);

            GroupItem.add("Top");

            GroupItem.add("Favorit");

            GroupItem.add("Project");

            viewAdapter = new ExpandableAdapter(ProjectContext, GroupItem, ProjectGroup_List);

            elv.setAdapter(viewAdapter);

            for (int i = 0; i < GroupItem.size(); i++) {
                elv.expandGroup(i);
            }

        } catch (JSONException ex) {
//            Toast.makeText(Project.this,ex.getMessage(), Toast.LENGTH_LONG).show();
            Log.w("Error", ex.toString());
        }


    }

    //自訂的ExpandListAdapter
    class ExpandableAdapter extends BaseExpandableListAdapter {
        private LayoutInflater mLayInf;
        private Context context;
        List<String> groups;
        ArrayList<List<Project_Item>> childs;

        /*
        * 構造函數:
        * 參數1:context物件
        * 參數2:一級清單資料來源
        * 參數3:二級清單資料來源
        */
        public ExpandableAdapter(Context context, List<String> groups, ArrayList<List<Project_Item>> childs) {
            mLayInf = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.groups = groups;
            this.childs = childs;
            this.context = context;
        }


        public Object getChild(int groupPosition, int childPosition) {
            return childs.get(groupPosition).get(childPosition);
        }

        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        //獲取二級清單的View物件
        public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView,
                                 ViewGroup parent) {
            View v = new View(context);

            v = mLayInf.inflate(R.layout.project_item, parent, false);

            final ImageView Img_ProjectImage = (ImageView) v.findViewById(R.id.Img_ProjectImage);

            final Project_Item Project_Item = (Project_Item) getChild(groupPosition, childPosition);

            Img_ProjectImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {

                    if (!UserData.WorkID.matches("")) {

                        Insert_Forcus_Data(UserData.WorkID, "", Project_Item.GetModelID());

                    }

                    ProjectInfo.Project_Item = Project_Item;

                    Intent intent = new Intent(context, ProjectInfo.class);

                    context.startActivity(intent);

                    ResumeFlag = true;
                }
            });


            TextView txt_Project_Name = (TextView) v.findViewById(R.id.txt_Project_Name);

            txt_Project_Name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {

                    if (!UserData.WorkID.matches("")) {

                        Insert_Forcus_Data(UserData.WorkID, "", Project_Item.ModelID);

                    }

                    Bundle bundle = new Bundle();

                    bundle.putString("ModelID", Project_Item.GetModelID());

                    bundle.putString("ModelName", Project_Item.GetName());

                    ProjectInfo.Project_Item = Project_Item;

                    Intent intent = new Intent(context, IssueList.class);

                    intent.putExtras(bundle);

                    context.startActivity(intent);
                }
            });

            txt_Project_Name.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View arg0) {

                    NewIssue(Project_Item);

                    return false;
                }
            });

            TextView txt_Issue_WorkNoteCount = (TextView) v.findViewById(R.id.txt_Issue_WorkNoteCount);

            if (!Project_Item.GetRead().equals("0")) {
                txt_Issue_WorkNoteCount.setVisibility(View.VISIBLE);
            } else {
                txt_Issue_WorkNoteCount.setVisibility(View.GONE);
            }


            String ProjectImagePath = GetServiceData.ServicePath + "/Get_File?FileName=" + Project_Item.GetImage();

            Log.w("ProjectImagePath", ProjectImagePath);

            Glide.with(context)
                    .load(ProjectImagePath)
                    .asBitmap()
                    .placeholder(R.mipmap.logo_dragon)
                    .error(R.mipmap.logo_dragon)
                    .into(new SimpleTarget<Bitmap>(300, 300) {

                        @Override
                        public void onLoadStarted(Drawable placeholder) {
                            Img_ProjectImage.setImageDrawable(placeholder);
                        }

                        @Override
                        public void onResourceReady(Bitmap bitmap, GlideAnimation anim) {


                            BitmapDrawable ob = new BitmapDrawable(getResources(), AppClass.roundCornerImage(bitmap, 150));

                            Img_ProjectImage.setImageBitmap(ob.getBitmap());
                        }
                    });

            txt_Project_Name.setText(Project_Item.GetName());


            return v;
        }


        private void NewIssue(Project_Item Project_Item) {
            Bundle bundle = new Bundle();

            bundle.putString("ModelID", Project_Item.GetModelID());

            bundle.putString("ModelName", Project_Item.GetName());

            Intent intent = new Intent();

            intent = new Intent(context, NewIssue.class);

            intent.putExtras(bundle);

            context.startActivity(intent);


        }

        private void Insert_Forcus_Data(String F_Keyin, String F_Owner, String F_PM_ID) {

            RequestQueue mQueue = Volley.newRequestQueue(context);

            String Path = GetServiceData.ServicePath + "/Insert_Focus_Model" + "?F_Keyin=" + F_Keyin + "&F_Owner=" + F_Owner + "&F_PM_ID=" + F_PM_ID;

            GetServiceData.getString(Path, mQueue, new GetServiceData.VolleyCallback() {
                @Override
                public void onSuccess(JSONObject result) {

                    //PMData_Mapping(result);
                }
            });

        }

        public int getChildrenCount(int groupPosition) {
            if (groups.size() == 0) {
                return 0;
            } else {
                if (childs.size() == 0) {
                    return 0;
                } else {
                    return childs.get(groupPosition).size();
                }


            }
        }

        public Object getGroup(int groupPosition) {


            return groups.get(groupPosition);
        }

        public int getGroupCount() {
            //Log.w("GroupSize",String.valueOf(groups.size()));


            return groups.size();
        }

        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        //獲取一級清單View物件
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            String text = groups.get(groupPosition);
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

//獲取一級清單佈局檔,設置相應元素屬性
            LinearLayout linearLayout = (LinearLayout) layoutInflater.inflate(R.layout.group, null);
            TextView textView = (TextView) linearLayout.findViewById(R.id.group_tv);
            textView.setText(text);


            return linearLayout;
        }

        public boolean hasStableIds() {
            return false;
        }

        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return false;
        }

        public void addItem(int GroupPosition, Project_Item Project_Item) {


            Log.w("FavoritType",Project_Item.GetFocusType());

            if (Project_Item.GetFocusType().equals("Favorit"))
            {
                Focus_Project_List.add(Project_Item);
            }
            else
            {
                NotFocus_Project_List.add(Project_Item);
            }

        }

    }
}

