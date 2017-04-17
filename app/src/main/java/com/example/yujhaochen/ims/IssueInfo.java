package com.example.yujhaochen.ims;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.*;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.TabLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.firebase.messaging.RemoteMessage;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IssueInfo extends AppCompatActivity {
    static final int REQUEST_VIDEO_CAPTURE = 1;

    static final int REQUEST_IMAGE_CAPTURE = 2;
    private static final String WRITE_EXTERNAL_STORAGE = "android.permission.WRITE_EXTERNAL_STORAGE";
    private static final String READ_EXTERNAL_STORAGE = "android.permission.READ_EXTERNAL_STORAGE";
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ListView lsv_main;
    private WorkNoteAdapter mListAdapter;
    List<WorkNote_Item> WorkNote_List = new ArrayList<WorkNote_Item>();
    List<IssueFile_Item> IssueFile_List = new ArrayList<IssueFile_Item>();
    private RecyclerView mRecyclerView;
    private IssueFileAdapter mAdapter;
    public ArrayList<Image> ImageViewList = new ArrayList<Image>();
    private String IssueID;
    private String ModelID;
    private ProgressDialog pDialog;
    private File ImageFile;

    private File VideoFile;

    private Animator mCurrentAnimator;

    private Menu mMenu;

    private String Status_Display = "";

    private String Author = "";

    private String AuthorNameEN = "";

    private String AuthorNameCN = "";

    private String Owner = "";

    private String OwnerNameEN = "";

    private String OwnerNameCN = "";

    private String Issue_Priotity = "";

    // The system "short" animation time duration, in milliseconds. This
    // duration is ideal for subtle animations or animations that occur
    // very frequently.
    private int mShortAnimationDuration;

    private RequestQueue mQueue;

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_issue_info);

        mContext = IssueInfo.this;

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        toolbar.setTitle("My Title");
        //getActionBar().hide();

        pDialog = new ProgressDialog(mContext);
        //View v = inflater.inflate(R.layout.fragment_my_issue, container, false);
        //宣告 ListView 元件
        lsv_main = (ListView) findViewById(R.id.listView);


        //lsv_main.setOnItemClickListener(listViewOnItemClickListener);

        Bundle Bundle = getIntent().getExtras();

        IssueID = Bundle.getString("IssueID");

        if (TextUtils.isEmpty(IssueID)) {
            String idOffer = "";
            Intent startingIntent = getIntent();
            if (startingIntent != null) {

                if (startingIntent.getStringExtra("key").contains("IssueInfo")) {

                    idOffer = startingIntent.getStringExtra("value"); // Retrieve the id


                    showRecordingNotification(idOffer);
                }
            }

            IssueID = idOffer;
        }

        Issue_Get(IssueID);

        Find_Issue_Comment(IssueID);

        Issue_File_List(IssueID);

        String WorkID = UserData.WorkID;

        if (!TextUtils.isEmpty(WorkID)) {
            Insert_Issue_Read(IssueID, WorkID);
        }


        mRecyclerView = (RecyclerView) findViewById(R.id.Rcy_IssueFile);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);

        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerView.setLayoutManager(layoutManager);

        ImageView Img_IssueAuthor = (ImageView) findViewById(R.id.Img_IssueAuthor);

        ImageView Img_IssueInfo_Send = (ImageView) findViewById(R.id.Img_IssueInfo_Send);

        Img_IssueInfo_Send.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                EditText txt_Comment = (EditText) findViewById(R.id.txt_Comment);

                String Comment = txt_Comment.getText().toString();

                C_Comment_Insert(Comment);
            }
        });

        ImageView Img_IssueInfo_AddPhoto = (ImageView) findViewById(R.id.Img_IssueInfo_AddPhoto);

        Img_IssueInfo_AddPhoto.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                GoCamera();
            }
        });


        TextView txt_IssueInfo_No = (TextView) findViewById(R.id.txt_IssueInfo_No);

        txt_IssueInfo_No.setText("#" + IssueID);


        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh_layout);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                if (mSwipeRefreshLayout != null) {
                    mSwipeRefreshLayout.setRefreshing(false);

//                    Issue_Get(IssueID);
//
//                    Find_Issue_Comment(IssueID);
                }


                //Issue_File_List(IssueID);
            }
        });

        setupUI(findViewById(android.R.id.content));


//        RelativeLayout Top_Banner = (RelativeLayout) findViewById(R.id.Top_Banner);
//
//        Top_Banner.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View arg0) {
//
//                GoToIssue_Gallery();
//            }
//        });


    }

    @Override
    public void onPause() {
        super.onPause();

        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setRefreshing(false);
            mSwipeRefreshLayout.destroyDrawingCache();
            mSwipeRefreshLayout.clearAnimation();
        }
    }

    @Override
    public void onResume() {
        Issue_Get(IssueID);

        Find_Issue_Comment(IssueID);

        Issue_File_List(IssueID);

        super.onResume();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            if (ImageFile.exists()) {


                ImageView image = new ImageView(mContext);

                Bitmap photo = FileUtil.FilePathGetBitMap(ImageFile.getAbsolutePath());

                Bitmap minibm = ThumbnailUtils.extractThumbnail(photo, 1024, 800);

                image.setImageBitmap(minibm);

                AlertDialog.Builder builder =
                        new AlertDialog.Builder(mContext).
                                setMessage("Use this photo?").
                                setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        String WorkID = UserData.WorkID;

                                        Bitmap photo = FileUtil.FilePathGetBitMap(ImageFile.getAbsolutePath());


                                        UpdateIssueFile(WorkID, IssueID, ImageFile.getAbsolutePath(), ImageFile.getName());

                                        Find_Issue_Comment(IssueID);

                                        ImageFile = null;

                                        dialog.dismiss();
                                    }
                                }).
                                setView(image);
                builder.create().show();


            }


        }

    }

    private void showRecordingNotification(String IssueID) {


    }

    private void GoToIssue_Gallery() {


        //IssueGallery.ImageViewList = ImageViewList;

        //photo_gallery.images = ImageViewList;

        Bundle bundle = new Bundle();

        bundle.putString("IssueID", IssueID);

        Intent intent = new Intent();

        intent = new Intent(mContext, photo_gallery.class);

        intent.putExtras(bundle);

        startActivity(intent);

    }

    private void Insert_Issue_Read(String F_Master_ID, String F_Keyin) {


        if (mQueue == null) {
            mQueue = Volley.newRequestQueue(mContext);
        }


        String Path = GetServiceData.ServicePath + "/Insert_Issue_Read_Advantage?F_Master_ID=" + F_Master_ID + "&F_Master_Table=" + "C_Issue" + "&F_Read=" + "1" + "&F_Keyin=" + F_Keyin;

        //System.out.println(File);

        GetServiceData.SendRequest(Path, mQueue, new GetServiceData.VolleyStringCallback() {
            @Override
            public void onSendRequestSuccess(String result) {

                //System.out.println("Test");
            }

            @Override
            public void onSendRequestError(String result) {
                //Log.w("NotificationSuccess",result);
            }
        });
    }

    private void GoCamera() {

        int permission = ActivityCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE);


        if (permission != PackageManager.PERMISSION_GRANTED) {
            // 無權限，向使用者請求
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE}, REQUEST_EXTERNAL_STORAGE
            );

            System.out.println("Storage");
        } else {

            permission = ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA);

            if (permission != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.CAMERA}, REQUEST_IMAGE_CAPTURE
                );

            } else {
                System.out.println("CAMERA");

                Intent intentCamera =
                        new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                // 照片檔案名稱
                File pictureFile = configFileName("P", ".jpg");

                ImageFile = pictureFile;

                Uri uri = Uri.fromFile(pictureFile);
                // 設定檔案名稱
                intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                // 啟動相機元件
                startActivityForResult(intentCamera, REQUEST_IMAGE_CAPTURE);
            }
        }


    }

    private void Upload_Comment_File(String F_Keyin, String F_Master_ID, String File) {

        if (mQueue == null) {
            mQueue = Volley.newRequestQueue(mContext);
        }

        String Path = GetServiceData.ServicePath + "/Issue_Comment_File_Insert?F_Keyin=" + F_Keyin + "&F_Master_ID=" + F_Master_ID + "&File=" + File;

        //System.out.println(File);

        GetServiceData.SendRequest(Path, mQueue, new GetServiceData.VolleyStringCallback() {
            @Override
            public void onSendRequestSuccess(String result) {

                //System.out.println("Test");
            }

            @Override
            public void onSendRequestError(String result) {
                //Log.w("NotificationSuccess",result);
            }
        });
    }

    private File configFileName(String prefix, String extension) {

        String fileName = FileUtil.getUniqueFileName();

        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);

        return new File(dir, prefix + fileName + extension);

    }

    private void UpdateIssueFile(String F_Keyin, String F_Master_ID, String FilePath, String FileName) {

//        pDialog.setMessage("Uploading...");
//
//        pDialog.show();

        if (mQueue == null) {
            mQueue = Volley.newRequestQueue(mContext);
        }

        Upload_Comment_File(F_Keyin, F_Master_ID, FileName);

        File ImageFileUpload = new File(FilePath);

        GetServiceData.uploadImage(GetServiceData.ServicePath + "/Upload_Issue_File_MultiPart", mQueue, ImageFileUpload, "");

    }

    private void C_Comment_Insert(String Comment) {



        String WorkID = UserData.WorkID;

        if (!TextUtils.isEmpty(Comment)) {

            final ImageView Img_IssueInfo_Send = (ImageView) findViewById(R.id.Img_IssueInfo_Send);

            Img_IssueInfo_Send.setVisibility(View.GONE);

            Map<String, String> map = new HashMap<String, String>();
            map.put("F_Keyin", WorkID);
            map.put("F_Master_Table", "C_Issue");
            map.put("F_Master_ID", IssueID);
            map.put("F_Comment", Comment);

            if (mQueue == null) {
                mQueue = Volley.newRequestQueue(mContext);
            }


            String Path = GetServiceData.ServicePath + "/C_Comment_Insert";


            GetServiceData.SendPostRequest(Path, mQueue, new GetServiceData.VolleyStringCallback() {
                @Override
                public void onSendRequestSuccess(String result) {


                    Find_Issue_Comment(IssueID);

                    EditText txt_Comment = (EditText) findViewById(R.id.txt_Comment);

                    txt_Comment.setText("");

                    Img_IssueInfo_Send.setVisibility(View.VISIBLE);
                }

                @Override
                public void onSendRequestError(String result) {


                    Find_Issue_Comment(IssueID);

                    EditText txt_Comment = (EditText) findViewById(R.id.txt_Comment);

                    txt_Comment.setText("");

                    Img_IssueInfo_Send.setVisibility(View.VISIBLE);
                }

            }, map);

        }


    }


    private AdapterView.OnItemClickListener listViewOnItemClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        }
    };

    public void setupUI(View view) {

        // Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    hideSoftKeyboard(IssueInfo.this);
                    return false;
                }
            });
        }

        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupUI(innerView);
            }
        }
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
    }

    private void Issue_File_List(String Issue_ID) {

//        pDialog.setMessage("Loading...");
//        pDialog.show();

        if (mQueue == null) {
            mQueue = Volley.newRequestQueue(mContext);
        }

        String Path = GetServiceData.ServicePath + "/Issue_File_List?F_SeqNo=" + Issue_ID;

        GetServiceData.getString(Path, mQueue, new GetServiceData.VolleyCallback() {
            @Override
            public void onSuccess(JSONObject result) {

//                pDialog.hide();

                IssueInfoFile_ListMapping(result);
            }
        });

    }

    private void IssueInfoFile_ListMapping(JSONObject result) {
        try {
            IssueFile_List.clear();

            JSONArray IssueInfoFileArray = new JSONArray(result.getString("Key"));

            if (IssueInfoFileArray.length() > 0) {


                for (int i = 0; i < IssueInfoFileArray.length(); i++) {

                    JSONObject IssueData = IssueInfoFileArray.getJSONObject(i);

                    String F_DownloadFilePath = GetServiceData.ServicePath + "/Get_File?FileName=" + IssueData.getString("F_DownloadFilePath");

                    //if (F_DownloadFilePath.contains())

                    IssueFile_List.add(i, new IssueFile_Item(F_DownloadFilePath, "", "", ""));


                }


                mAdapter = new IssueFileAdapter(mContext, IssueFile_List);


                mRecyclerView.setAdapter(mAdapter);

            } else {
                LinearLayout RecycleView = (LinearLayout) findViewById(R.id.Lie_IssueFile);

                RecycleView.setVisibility(View.GONE);

                //TextView txt_IssueInfo_Gallery = (TextView) findViewById(R.id.txt_IssueInfo_Gallery);

                //txt_IssueInfo_Gallery.setVisibility(View.GONE);
            }


        } catch (JSONException ex) {

        }


    }


    private void Issue_Get(String Issue_ID) {

        if (mQueue == null) {
            mQueue = Volley.newRequestQueue(mContext);
        }

        String Path = GetServiceData.ServicePath + "/Issue_Get?F_SeqNo=" + Issue_ID;

        GetServiceData.getString(Path, mQueue, new GetServiceData.VolleyCallback() {
            @Override
            public void onSuccess(JSONObject result) {

                IssueInfoMapping(result);
            }
        });

    }


    private void IssueInfoMapping(JSONObject result) {
        try {
            WorkNote_List.clear();

            JSONArray UserArray = new JSONArray(result.getString("Key"));

            if (UserArray.length() > 0) {

                JSONObject IssueData = UserArray.getJSONObject(0);

                AuthorNameEN = IssueData.getString("F_Owner_en");

                AuthorNameCN = IssueData.getString("F_Owner_cn");

                Owner = IssueData.getString("F_RespGroup");

                OwnerNameEN = IssueData.getString("Issue_OwnerEN");

                OwnerNameCN = IssueData.getString("Issue_Owner");

                String F_SeqNo = String.valueOf(IssueData.getInt("F_SeqNo"));

                String F_Owner_en = IssueData.getString("F_Owner_en");

                String Issue_OwnerEN = IssueData.getString("Issue_OwnerEN");

                String F_ModelName = IssueData.getString("F_ModelName");

                String F_Priority = IssueData.getString("F_Priority");

                Issue_Priotity = F_Priority;

                Status_Display = IssueData.getString("F_Status_Display");

                String F_CreateDate = AppClass.ConvertDateString(IssueData.getString("F_CreateDate"));

                String F_Subject = IssueData.getString("F_Subject");

                Author = IssueData.getString("F_Keyin");

                ModelID = IssueData.getString("F_PM_ID");

                F_Subject = AppClass.stripHtml(F_Subject);

                TextView txt_IssueInfo_Author = (TextView) findViewById(R.id.txt_IssueInfo_Author);

                TextView txt_IssueInfo_Owner = (TextView) findViewById(R.id.txt_IssueInfo_Owner);

                //TextView txt_IssueInfo_ProjectName = (TextView) findViewById(R.id.txt_IssueInfo_ProjectName);

                ImageView Img_IssuePriority = (ImageView) findViewById(R.id.Img_IssuePriority);

                TextView txt_IssueInfo_Date = (TextView) findViewById(R.id.txt_IssueInfo_Date);

                TextView txt_Issue_Subject = (TextView) findViewById(R.id.txt_IssueInfo_Subject);

                txt_IssueInfo_Author.setText(F_Owner_en);

                if (Issue_OwnerEN == null || Issue_OwnerEN == "null") {
                    txt_IssueInfo_Owner.setText("");
                } else {
                    txt_IssueInfo_Owner.setText(Issue_OwnerEN);
                }


                txt_IssueInfo_Date.setText(F_CreateDate);

                //txt_IssueInfo_ProjectName.setText("MS-" + F_ModelName);

                Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

                toolbar.setTitle("MS-" + F_ModelName);

                txt_Issue_Subject.setText(F_Subject);

                Img_IssuePriority.setImageResource(AppClass.PriorityImage(F_Priority));

                final ImageView Img_IssueAuthor = (ImageView) findViewById(R.id.Img_IssueAuthor);

//                Glide
//                        .with(IssueInfo.this)
//                        .load(GetServiceData.ServicePath + "/Get_File?FileName=" + "//172.16.111.114/File/SDQA/Code/Admin/" + Author + ".jpg")
//                        .asBitmap()
//                        .diskCacheStrategy(DiskCacheStrategy.ALL)
//                        .placeholder(R.mipmap.progress_image)
//                        .into(new SimpleTarget<Bitmap>(100,100) {
//                            @Override
//                            public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {
//
//                                Img_IssueAuthor.setImageBitmap(resource);
//
//                            }
//                        });

                invalidateOptionsMenu();

            }
        } catch (JSONException ex) {

        }


    }

    private String PriorityConvert(String Priority) {
        String PriorityDisplayText = "";

        switch (Priority) {
            case "1":
                PriorityDisplayText = "Critical (P1)";
                break;
            case "2":
                PriorityDisplayText = "Major (P2)";
                break;
            case "3":
                PriorityDisplayText = "Minor (P3)";
                break;

        }

        return PriorityDisplayText;
    }

    public void IssuePriorityChange() {
        List<List_Item> PriorityList = new ArrayList<List_Item>();

        PriorityList.add(0, new List_Item("Critical (P1)", "1"));

        PriorityList.add(1, new List_Item("Major (P2)", "2"));

        PriorityList.add(2, new List_Item("Minor (P3)", "3"));

        final Alert_Search_Dialog DataDialog = new Alert_Search_Dialog(mContext, "Select Issue Priority", PriorityList);

        DataDialog.SetOnDialog_Finish_Listener(new Alert_Search_Dialog.Dialog_Finish_Listener() {
            @Override
            public void Finished() {

                if (!TextUtils.isEmpty(DataDialog.SelectValue)) {

                    Change_Issue_Priority(IssueID, DataDialog.SelectValue);

                    String CommentTitle = "@Issue Priority Change";

                    String CommentText = "◎Issue Priority Change： 『" + PriorityConvert(Issue_Priotity) + "』change to 『" + PriorityConvert(DataDialog.SelectValue) + "』";

                    CommentText += "Reason: 『" + DataDialog.GetReason() + "』\n";

                    C_Comment_Insert(CommentText);

                    List<String> WorkID_List = new ArrayList<String>();

                    WorkID_List.add(0, Author);

                    AppClass.Send_Notification(WorkID_List, CommentTitle, CommentText, "IssueInfo", "IssueInfo", IssueID, mContext);

                }
            }
        });


        DataDialog.show();
    }

    public void IssueOwnerChange(String PM_ID) {
        if (mQueue == null) {
            mQueue = Volley.newRequestQueue(mContext);
        }

        String Path = GetServiceData.ServicePath + "/Find_Model_Member?PM_ID=" + PM_ID;

        GetServiceData.getString(Path, mQueue, new GetServiceData.VolleyCallback() {
            @Override
            public void onSuccess(JSONObject result) {

                try {

                    List<List_Item> Member_List = new ArrayList<List_Item>();

                    JSONArray UserArray = new JSONArray(result.getString("Key"));

                    for (int i = 0; i < UserArray.length(); i++) {
                        JSONObject ModelData = UserArray.getJSONObject(i);

                        String MemberName = ModelData.getString("FullName");

                        String WorkID = ModelData.getString("F_ID");

                        Member_List.add(i, new List_Item(MemberName, WorkID));
                    }

                    final Alert_Search_Dialog DataDialog = new Alert_Search_Dialog(mContext, "Select Issue Owner", Member_List);

                    DataDialog.SetOnDialog_Finish_Listener(new Alert_Search_Dialog.Dialog_Finish_Listener() {
                        @Override
                        public void Finished() {

                            if (!TextUtils.isEmpty(DataDialog.SelectValue)) {

                                Change_Issue_Owner(IssueID, DataDialog.SelectValue);

                                String CommentTitle = "@Issue Owner Change";

                                String CommentText = "◎Issue Owner Change： 『" + AuthorNameCN + " " + AuthorNameEN + "』change to 『" + DataDialog.SelectText + "』";

                                CommentText += "Reason: 『" + DataDialog.GetReason() + "』\n";

                                C_Comment_Insert(CommentText);

                                List<String> WorkID_List = new ArrayList<String>();

                                WorkID_List.add(0, DataDialog.SelectValue);

                                WorkID_List.add(1, Author);

                                AppClass.Send_Notification(WorkID_List, CommentTitle, CommentText, "IssueInfo", "IssueInfo", IssueID, mContext);
                            }
                        }
                    });

                    DataDialog.show();

                } catch (JSONException ex) {

                }
            }
        });


    }


    private void Find_Issue_Comment(String Issue_ID) {

//        pDialog.setMessage("Loading...");
//
//        pDialog.show();

        if (mQueue == null) {
            mQueue = Volley.newRequestQueue(mContext);
        }

        String Path = GetServiceData.ServicePath + "/Find_Issue_Comment?Issue_ID=" + Issue_ID;

        GetServiceData.getString(Path, mQueue, new GetServiceData.VolleyCallback() {
            @Override
            public void onSuccess(JSONObject result) {

                IssueCommentMapping(result);

                //pDialog.hide();
            }
        });

    }

    private void IssueCommentMapping(JSONObject result) {
        try {
            WorkNote_List.clear();

            JSONArray UserArray = new JSONArray(result.getString("Key"));

            for (int i = 0; i < UserArray.length(); i++) {
                JSONObject IssueData = UserArray.getJSONObject(i);

                String ID = String.valueOf(IssueData.getInt("ID"));

                String F_Keyin = IssueData.getString("F_Keyin");

                String F_Owner = IssueData.getString("F_Owner");

                String F_Owner_cn = IssueData.getString("F_Owner_cn");

                String F_Owner_en = IssueData.getString("F_Owner_en");

                String F_CreateDate = AppClass.ConvertLongDateString(IssueData.getString("F_CreateDate"));

                String F_SeqNo = String.valueOf(IssueData.getInt("F_SeqNo"));

                String F_Comment = IssueData.getString("F_Comment");

                F_Comment = AppClass.stripHtml(F_Comment);

                String CommentFile = IssueData.getString("Comment_File");

                WorkNote_List.add(i, new WorkNote_Item(F_Owner_en, F_Keyin, F_CreateDate, F_Comment, "", CommentFile));
            }

            // ListView 中所需之資料參數可透過修改 Adapter 的建構子傳入
            mListAdapter = new WorkNoteAdapter(mContext, WorkNote_List);

            //mListAdapter.notifyDataSetChanged();
            //設定 ListView 的 Adapter
            lsv_main.setAdapter(mListAdapter);


        } catch (JSONException ex) {

        }


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        mMenu = menu;


        if (!Status_Display.equals("3") && Author.equals(UserData.WorkID)) {
            getMenuInflater().inflate(R.menu.menu_issue, menu);
        }


        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.CloseIssue) {
            AlertDialog.Builder alert = new AlertDialog.Builder(
                    mContext);
            alert.setTitle("Close Issue!!");
            alert.setMessage("Are you sure to close issue");
            alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {

                    Log.w("IssueID", IssueID);

                    Close_Issue(IssueID);

                }
            });
            alert.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {

                    dialog.dismiss();
                }
            });

            alert.show();
        } else if (id == R.id.IssueOwner) {
            IssueOwnerChange(ModelID);
        } else if (id == R.id.IssuePriotiry) {
            IssuePriorityChange();
        }

        return super.onOptionsItemSelected(item);
    }

    private void Change_Issue_Owner(final String IssueID, final String WorkID) {


        if (!TextUtils.isEmpty(IssueID) && !TextUtils.isEmpty(WorkID)) {

            if (mQueue == null) {
                mQueue = Volley.newRequestQueue(mContext);
            }

            Map<String, String> map = new HashMap<String, String>();
            map.put("IssueID", IssueID);
            map.put("WorkID", WorkID);

            String Path = GetServiceData.ServicePath + "/Change_Issue_Owner";

            GetServiceData.SendPostRequest(Path, mQueue, new GetServiceData.VolleyStringCallback() {
                @Override
                public void onSendRequestSuccess(String result) {

                    Issue_Get(IssueID);

                    Find_Issue_Comment(IssueID);

                    AppClass.AlertMessage("Change Owner Success", mContext);
                }

                @Override
                public void onSendRequestError(String result) {
                    Log.w("NotificationSuccess", result);
                }

            }, map);


        } else {
            AppClass.AlertMessage("Change Owner Error", mContext);
        }


    }

    private void Change_Issue_Priority(final String IssueID, final String Priority) {


        if (!TextUtils.isEmpty(IssueID) && !TextUtils.isEmpty(Priority)) {

            if (mQueue == null) {
                mQueue = Volley.newRequestQueue(mContext);
            }

            Map<String, String> map = new HashMap<String, String>();
            map.put("IssueID", IssueID);
            map.put("Priority", Priority);

            String Path = GetServiceData.ServicePath + "/Change_Issue_Priority";

            GetServiceData.SendPostRequest(Path, mQueue, new GetServiceData.VolleyStringCallback() {
                @Override
                public void onSendRequestSuccess(String result) {

                    Issue_Get(IssueID);

                    AppClass.AlertMessage("Change Priority Success", mContext);
                }

                @Override
                public void onSendRequestError(String result) {
                    //Log.w("NotificationSuccess",result);
                }

            }, map);


        } else {
            AppClass.AlertMessage("Change Priority Error", mContext);
        }


    }

    private void Close_Issue(final String IssueID) {


        if (!TextUtils.isEmpty(IssueID)) {


            if (mQueue == null) {
                mQueue = Volley.newRequestQueue(mContext);
            }

            Map<String, String> map = new HashMap<String, String>();
            map.put("IssueNo", IssueID);

            String Path = GetServiceData.ServicePath + "/Close_Issue";

            GetServiceData.SendPostRequest(Path, mQueue, new GetServiceData.VolleyStringCallback() {
                @Override
                public void onSendRequestSuccess(String result) {

                    AppClass.AlertMessage("Close Issue Success", mContext);
                }

                @Override
                public void onSendRequestError(String result) {
                    Log.w("NotificationSuccess", result);
                }

            }, map);


        } else {
            AppClass.AlertMessage("Close Issue Error", mContext);
        }


    }

}
