package com.apps.ims;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

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
 * {@link MyIssue.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MyIssue#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyIssue extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private ListView lsv_main;
    private IssueAdapter mListAdapter;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    List<Issue_Item> Issue_List = new ArrayList<Issue_Item>();
    private OnFragmentInteractionListener mListener;
    private RequestQueue mQueue;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        Activity a;

        if (context instanceof Activity) {
            a = (Activity) context;
        }

    }

    public MyIssue() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MyIssue.
     */
    // TODO: Rename and change types and number of parameters
    public static MyIssue newInstance(String param1, String param2) {
        MyIssue fragment = new MyIssue();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
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

        View v = inflater.inflate(com.apps.ims.R.layout.fragment_my_issue, container, false);

        lsv_main = (ListView) v.findViewById(com.apps.ims.R.id.listView);

        lsv_main.setOnItemClickListener(listViewOnItemClickListener);

        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(com.apps.ims.R.id.refresh_layout);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                System.out.println("MyIssueRefresh");
                mSwipeRefreshLayout.setRefreshing(false);


                if (!UserData.WorkID.matches("")) {


                    Find_My_Issue(UserData.WorkID, "300");
                }
            }
        });

        if (!UserData.WorkID.matches("")) {


            Find_My_Issue(UserData.WorkID, "300");
        }

        return v;
    }

    private AdapterView.OnItemClickListener listViewOnItemClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            IssueAdapter IssueAdapter = (IssueAdapter) parent.getAdapter();

            Issue_Item Issue_Item = (Issue_Item) IssueAdapter.getItem(position);

            GoIssueInfo(Issue_Item.GetID());
        }
    };

    private void GoIssueInfo(String IssueID) {

        Bundle bundle = new Bundle();

        bundle.putString("IssueID", IssueID);
        // 建立啟動另一個Activity元件需要的Intent物件
        // 建構式的第一個參數：「this」
        // 建構式的第二個參數：「Activity元件類別名稱.class」
        Intent intent = new Intent(getActivity(), IssueInfo.class);

        intent.putExtras(bundle);
        // 呼叫「startActivity」，參數為一個建立好的Intent物件
        // 這行敘述執行以後，如果沒有任何錯誤，就會啟動指定的元件
        startActivity(intent);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }
//
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

    private void Find_My_Issue(String WorkID, String DateRange) {

        if (mQueue == null) {
            mQueue = Volley.newRequestQueue(getActivity());
        }
        String Path = GetServiceData.ServicePath + "/Find_My_Issue_List?F_Keyin=" + WorkID + "&DateRange=" + DateRange;

        GetServiceData.getString(Path, mQueue, new GetServiceData.VolleyCallback() {
            @Override
            public void onSuccess(JSONObject result) {


                IssueListDataMapping(result);
            }
        });
    }

    private void IssueListDataMapping(JSONObject result) {
        try {

            Issue_List.clear();

            JSONArray UserArray = new JSONArray(result.getString("Key"));

            for (int i = 0; i < UserArray.length(); i++) {

                JSONObject ModelData = UserArray.getJSONObject(i);

                String F_SeqNo = String.valueOf(ModelData.getInt("F_SeqNo"));

                String Model = ModelData.getString("Model");

                String F_Subject = ModelData.getString("F_Subject");

                String F_CreateDate = AppClass.ConvertDateString(ModelData.getString("F_CreateDate"));

                String F_Priority = ModelData.getString("F_Priority");

                String CommentRead = String.valueOf(ModelData.getInt("CommentRead"));

                String Read = String.valueOf(ModelData.getInt("Read"));

                String F_Owner = String.valueOf(ModelData.getString("F_Owner"));

                String Status_Display = String.valueOf(ModelData.getString("Status_Display"));

                F_Subject = F_Subject.replace("\n", "");

                Issue_List.add(i, new Issue_Item(F_SeqNo, Model, F_Subject, F_CreateDate, F_Priority, CommentRead, Read, F_Owner, Status_Display));

            }

            if (Issue_List.size() > 0) {  // ListView 中所需之資料參數可透過修改 Adapter 的建構子傳入
                mListAdapter = new IssueAdapter(getActivity(), Issue_List, "MyIssue");

                //設定 ListView 的 Adapter
                lsv_main.setAdapter(mListAdapter);

            }


        } catch (JSONException ex) {
            System.out.println(ex);
        }
    }

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
