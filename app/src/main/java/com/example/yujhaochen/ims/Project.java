package com.example.yujhaochen.ims;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;


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

    List<Project_Item> Project_List = new ArrayList<Project_Item>();

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

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
    public  Project newInstance(String param1, String param2) {
        Project fragment = new Project();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);




        return fragment;
    }

    private AdapterView.OnItemClickListener listViewOnItemClickListener
            = new AdapterView.OnItemClickListener()
    {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
        {
            ProjectAdapter ProjectAdapter = (ProjectAdapter)parent.getAdapter();

            Project_Item Project_Item = (Project_Item)ProjectAdapter.getItem(position);

            GoToProjectInfo(Project_Item);
        }
    };

    private void GoToProjectInfo(Project_Item Project_Item)
    {

        ProjectInfo.Project_Item = Project_Item;

        Intent intent = new Intent(getActivity(), ProjectInfo.class);

        startActivity(intent);
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_project, container, false);
        //宣告 ListView 元件
        lsv_main = (ListView)v.findViewById(R.id.listView);

        lsv_main.setOnItemClickListener(listViewOnItemClickListener);

        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.refresh_layout);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefreshLayout.setRefreshing(false);

                if (!UserData.WorkID.matches(""))
                {
                    GetPM_Data(UserData.WorkID);
                }
            }
        });

        if (!UserData.WorkID.matches(""))
        {
            GetPM_Data(UserData.WorkID);
        }
        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        Activity a;

        if (context instanceof Activity){
            a=(Activity) context;
        }

    }

    private void GetPM_Data(String WorkID) {

        RequestQueue mQueue = Volley.newRequestQueue(getActivity());

        String Path = GetServiceData.ServicePath + "/Find_Project_List?WorkID=" + WorkID;

        GetServiceData.getString(Path, mQueue, new GetServiceData.VolleyCallback() {
            @Override
            public void onSuccess(JSONObject result) {

                PMData_Mapping(result);
            }
        });

    }

    private void PMData_Mapping(JSONObject result)
    {
        try
        {
            Project_List.clear();

            JSONArray UserArray = new JSONArray(result.getString("Key"));

            for (int i = 0;i< UserArray.length();i++)
            {
                JSONObject ModelData =  UserArray.getJSONObject(i);

                String ModelID = String.valueOf(ModelData.getInt("ModelID"));

                String ModelName = ModelData.getString("ModelName");

                String ModelPic = ModelData.getString("ModelPic");

                String CloseRate = String.valueOf(ModelData.getDouble("CloseRate"));

                Project_List.add(i,new Project_Item(ModelID,ModelName,ModelPic,CloseRate));
            }


           if(Project_List.size() >0)
           {
               // ListView 中所需之資料參數可透過修改 Adapter 的建構子傳入
               mListAdapter = new ProjectAdapter(getActivity(), Project_List);

               //設定 ListView 的 Adapter
               lsv_main.setAdapter(mListAdapter);
           }
        }
        catch (JSONException ex)
        {
            
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