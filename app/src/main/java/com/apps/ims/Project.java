package com.apps.ims;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Project.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Project#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Project extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ListView lsv_main;
    private ProjectAdapter mListAdapter;

    private Boolean flag_loading;
    List<Project_Item> Project_List = new ArrayList<Project_Item>();

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ProgressDialog pDialog;

    private OnFragmentInteractionListener mListener;
    private RequestQueue mQueue;

    public Project() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Project.
     */
    // TODO: Rename and change types and number of parameters
    public Project newInstance(String param1, String param2) {
        Project fragment = new Project();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);


        return fragment;
    }

    private AdapterView.OnItemClickListener listViewOnItemClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            ProjectAdapter ProjectAdapter = (ProjectAdapter) parent.getAdapter();

            Project_Item Project_Item = (Project_Item) ProjectAdapter.getItem(position);

            GoToProjectInfo(Project_Item);
        }
    };


    private void GoToProjectInfo(Project_Item Project_Item) {
        if (!UserData.WorkID.matches("")) {

            Insert_Forcus_Data(UserData.WorkID, "", Project_Item.ModelID);

        }

        ProjectInfo.Project_Item = Project_Item;

        Intent intent = new Intent(getActivity(), ProjectInfo.class);

        startActivity(intent);
    }

    private void Insert_Forcus_Data(String F_Keyin, String F_Owner, String F_PM_ID) {

        if (mQueue == null) {
            mQueue = Volley.newRequestQueue(getActivity());
        }
        String Path = GetServiceData.ServicePath + "/Insert_Focus_Model" + "?F_Keyin=" + F_Keyin + "&F_Owner=" + F_Owner + "&F_PM_ID=" + F_PM_ID;

        GetServiceData.getString(Path, mQueue, new GetServiceData.VolleyCallback() {
            @Override
            public void onSuccess(JSONObject result) {

                //PMData_Mapping(result);
            }
        });

    }

    @Override
    public void onResume() {
        if (!UserData.WorkID.matches("")) {
            GetPM_Data(UserData.WorkID);
        }

        super.onResume();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    private void NewIssue(Project_Item Project_Item) {
        Bundle bundle = new Bundle();

        bundle.putString("ModelID", Project_Item.GetModelID());

        bundle.putString("ModelName", Project_Item.GetName());

        Intent intent = new Intent();

        intent = new Intent(getContext(), NewIssue.class);

        intent.putExtras(bundle);

        startActivity(intent);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(com.apps.ims.R.layout.fragment_project, container, false);
        //宣告 ListView 元件
        lsv_main = (ListView) v.findViewById(com.apps.ims.R.id.listView);

        //lsv_main.setOnItemClickListener(listViewOnItemClickListener);

//        lsv_main.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//            @Override
//            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int index, long l) {
//
//                NewIssue(Project_List.get(index ));
//
//                return false;
//            }
//        });

        flag_loading = false;
        ProgressBar footer = new ProgressBar(getContext());
        lsv_main.addFooterView(footer);

        lsv_main.setOnScrollListener(new AbsListView.OnScrollListener() {

            public void onScrollStateChanged(AbsListView view, int scrollState) {


            }

            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {

                Log.w("firstVisibleItem", String.valueOf(firstVisibleItem));
                Log.w("visibleItemCount", String.valueOf(visibleItemCount));
                Log.w("totalItemCount", String.valueOf(totalItemCount));
                if (firstVisibleItem + visibleItemCount == totalItemCount && totalItemCount != 0) {
                    if (flag_loading == false) {
                        flag_loading = true;
                        //additems();
                    }
                }
            }
        });

        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(com.apps.ims.R.id.refresh_layout);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefreshLayout.setRefreshing(false);

                if (!UserData.WorkID.matches("")) {
                    GetPM_Data(UserData.WorkID);
                }
            }
        });

        //System.out.println(UserData.WorkID);

        if (!UserData.WorkID.matches("")) {
            GetPM_Data(UserData.WorkID);
        }
        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        Activity a;

        if (context instanceof Activity) {
            a = (Activity) context;
        }

    }

    private void GetPM_Data(String WorkID) {

        if (mQueue == null) {
            mQueue = Volley.newRequestQueue(getActivity());
        }

        String Path = GetServiceData.ServicePath + "/Find_Project_List?WorkID=" + WorkID;

        GetServiceData.getString(Path, mQueue, new GetServiceData.VolleyCallback() {
            @Override
            public void onSuccess(JSONObject result) {

                PMData_Mapping(result);
            }
        });

    }

    private void PMData_Mapping(JSONObject result) {
        try {
            Project_List.clear();

            JSONArray UserArray = new JSONArray(result.getString("Key"));

            for (int i = 0; i < UserArray.length(); i++) {
                JSONObject ModelData = UserArray.getJSONObject(i);

                String ModelID = String.valueOf(ModelData.getInt("ModelID"));

                String ModelName = ModelData.getString("ModelName");

                String ModelPic = ModelData.getString("ModelPic");

                //String CloseRate = String.valueOf(ModelData.getDouble("CloseRate"));

                String Model_Focus = ModelData.getString("Model_Focus");

                String Read = String.valueOf(ModelData.getInt("Read"));

                Boolean Model_Focus_Type = false;

                Project_List.add(i, new Project_Item(ModelID, ModelName, ModelPic, "", Model_Focus, Read));
            }


            if (Project_List.size() > 0) {
                // ListView 中所需之資料參數可透過修改 Adapter 的建構子傳入
                mListAdapter = new ProjectAdapter(getActivity(), Project_List);

                //設定 ListView 的 Adapter
                lsv_main.setAdapter(mListAdapter);
            }
        } catch (JSONException ex) {
//            Toast.makeText(Project.this,ex.getMessage(), Toast.LENGTH_LONG).show();
            Log.w("Error", ex.toString());
        }


    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
//    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
