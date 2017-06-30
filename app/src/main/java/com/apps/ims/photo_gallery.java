package com.apps.ims;

import android.app.ProgressDialog;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.R;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class photo_gallery extends FragmentActivity {

    private String TAG = com.apps.ims.MainActivity.class.getSimpleName();
    private static final String endpoint = "http://api.androidhive.info/json/glide.json";
    public ArrayList<Image> images;
    private ProgressDialog pDialog;
    private GalleryAdapter mAdapter;
    private RecyclerView recyclerView;
    private String IssueID;
    private RequestQueue mQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.apps.ims.R.layout.activity_photo_gallery);

        recyclerView = (RecyclerView) findViewById(com.apps.ims.R.id.recycler_view);

        pDialog = new ProgressDialog(this);
        images = new ArrayList<>();
        mAdapter = new GalleryAdapter(getApplicationContext(), images);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getApplicationContext(), 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        Bundle Bundle = this.getIntent().getExtras();

        IssueID = Bundle.getString("IssueID");

        recyclerView.addOnItemTouchListener(new GalleryAdapter.RecyclerTouchListener(getApplicationContext(), recyclerView, new GalleryAdapter.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("images", images);
                bundle.putInt("position", position);

                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

                //FragmentManager MG = getSupportFragmentManager();
                SlideshowDialogFragment newFragment = SlideshowDialogFragment.newInstance();
                newFragment.setArguments(bundle);

                newFragment.show(ft, "slideshow");
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        Issue_File_List(IssueID);
    }

    private void fetchImages() {

        pDialog.setMessage("Downloading Photo...");
        pDialog.show();

        JsonArrayRequest req = new JsonArrayRequest(endpoint,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, response.toString());
                        pDialog.hide();

                        images.clear();
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject object = response.getJSONObject(i);
                                Image image = new Image();
                                image.setName(object.getString("name"));

                                JSONObject url = object.getJSONObject("url");
                                image.setSmall(url.getString("small"));
                                image.setMedium(url.getString("medium"));
                                image.setLarge(url.getString("large"));
                                image.setTimestamp(object.getString("timestamp"));

                                images.add(image);

                            } catch (JSONException e) {
                                Log.e(TAG, "Json parsing error: " + e.getMessage());
                            }
                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error: " + error.getMessage());
                pDialog.hide();
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(req);
    }

    private void Issue_File_List(String Issue_ID) {

        pDialog.setMessage("Downloading Photo...");
        pDialog.show();


        if (mQueue == null) {
            mQueue = Volley.newRequestQueue(this);
        }
        String Path = GetServiceData.ServicePath + "/Issue_File_List?F_SeqNo=" + Issue_ID;

        GetServiceData.getString(Path, mQueue, new GetServiceData.VolleyCallback() {
            @Override
            public void onSuccess(JSONObject result) {


                IssueInfoFile_ListMapping(result);

                mAdapter.notifyDataSetChanged();

                pDialog.hide();
            }
        });

    }

    private void IssueInfoFile_ListMapping(JSONObject result) {
        try {
            images.clear();

            JSONArray UserArray = new JSONArray(result.getString("Key"));

            int index = 0;

            for (int i = 0; i < UserArray.length(); i++) {

                JSONObject IssueData = UserArray.getJSONObject(i);

                String F_DownloadFilePath = GetServiceData.ServicePath + "/Get_File?FileName=" + IssueData.getString("F_DownloadFilePath");

                String F_FileName = IssueData.getString("F_FileName");

                String F_CreateDate = IssueData.getString("F_CreateDate");

                if (!(F_DownloadFilePath.contains("http://") || F_DownloadFilePath.contains("https://"))) {
                    F_DownloadFilePath = "http:" + F_DownloadFilePath;

                }

                if ((F_DownloadFilePath.toLowerCase().contains("jpg") || F_DownloadFilePath.toLowerCase().contains("png") || F_DownloadFilePath.toLowerCase().contains("jpeg"))) {
                    Image image = new Image();
                    image.setName(F_FileName);

                    image.setSmall(F_DownloadFilePath);
                    image.setMedium(F_DownloadFilePath);
                    image.setLarge(F_DownloadFilePath);
                    image.setTimestamp(F_CreateDate);

                    images.add(index, image);

                    index++;
                }

            }

        } catch (JSONException ex) {

        }


    }

}

