package com.example.yujhaochen.ims;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
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

public class IssueInfo extends Activity {
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
    private ProgressDialog pDialog;
    private File ImageFile;

    private File VideoFile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_issue_info);

        pDialog = new ProgressDialog(this);
        //View v = inflater.inflate(R.layout.fragment_my_issue, container, false);
        //宣告 ListView 元件
        lsv_main = (ListView) findViewById(R.id.listView);

        lsv_main.setOnItemClickListener(listViewOnItemClickListener);

        Bundle Bundle = this.getIntent().getExtras();

        IssueID = Bundle.getString("IssueID");

        Issue_Get(IssueID);

        Find_Issue_Comment(IssueID);

        Issue_File_List(IssueID);

        //mRecyclerView = (RecyclerView) findViewById(R.id.Rcy_IssueFile);

        ImageView Img_IssueAuthor = (ImageView) findViewById(R.id.Img_IssueAuthor);

        ImageView Img_IssueInfo_Send = (ImageView) findViewById(R.id.Img_IssueInfo_Send);

        Img_IssueInfo_Send.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                C_Comment_Insert();
            }
        });

        ImageView Img_IssueInfo_AddPhoto = (ImageView) findViewById(R.id.Img_IssueInfo_AddPhoto);

        Img_IssueInfo_AddPhoto.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                GoCamera();
            }
        });

        GetServiceData.GetUserPhoto(UserData.WorkID, Img_IssueAuthor);


        RelativeLayout Top_Banner = (RelativeLayout) findViewById(R.id.Top_Banner);

        TextView txt_IssueInfo_No = (TextView) findViewById(R.id.txt_IssueInfo_No);

        txt_IssueInfo_No.setText("#" + IssueID);

        Top_Banner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                GoToIssue_Gallery();
            }
        });

        mSwipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.refresh_layout);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefreshLayout.setRefreshing(false);

                Issue_Get(IssueID);

                Find_Issue_Comment(IssueID);

                //Issue_File_List(IssueID);
            }
        });

        setupUI(this.findViewById(android.R.id.content));
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {


        System.out.println(requestCode);

        if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK) {


            if (VideoFile.exists()) {
                System.out.println("Video Request" + requestCode);

                AddVideoItem(VideoFile.getAbsolutePath());

//                VideoView Vdo_Issue_File = (VideoView)findViewById(R.id.videoView);
//
//                Vdo_Issue_File.setVideoURI(Uri.parse(VideoFile.getAbsolutePath()));

                // System.out.println("Video Path" + NewIssueFile_List.get(position).GetVideoPath());
                VideoFile = null;
            }

        }
        // 如果照片檔案存在
        else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            System.out.println("Image Request" + requestCode);
            if (ImageFile.exists()) {

                Bitmap photo = FileUtil.FilePathGetBitMap(ImageFile.getAbsolutePath());

                Bitmap minibm = ThumbnailUtils.extractThumbnail(photo, 1024, 800);

                AddImageItem(minibm, ImageFile.getName(), ImageFile.getAbsolutePath());

                ImageFile = null;
            }


        } else if (resultCode == RESULT_OK) {

            IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

            if (scanningResult != null) {
                String scanContent = scanningResult.getContents();
                String scanFormat = scanningResult.getFormatName();
                System.out.println(scanContent);
                System.out.println(scanFormat);

            } else {





            }


        }

    }

    private void GoToIssue_Gallery() {


        //IssueGallery.ImageViewList = ImageViewList;

        //photo_gallery.images = ImageViewList;

        Bundle bundle = new Bundle();

        bundle.putString("IssueID", IssueID);

        Intent intent = new Intent();

        intent = new Intent(this, photo_gallery.class);

        intent.putExtras(bundle);

        startActivity(intent);

    }

    private void GoCamera() {

        int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);




        if (permission != PackageManager.PERMISSION_GRANTED) {
            // 無權限，向使用者請求
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE}, REQUEST_EXTERNAL_STORAGE
            );

            System.out.println("Storage");
        } else {

            permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);

            if (permission != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(
                        this,
                        new String[]{ Manifest.permission.CAMERA}, REQUEST_IMAGE_CAPTURE
                );

            }
            else
            {
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

    private void Upload_Issue_File(String F_Keyin, String F_Master_ID, String File) {

        RequestQueue mQueue = Volley.newRequestQueue(this);

        String Path = GetServiceData.ServicePath + "/Upload_Issue_File?F_Keyin=" + F_Keyin + "&F_Master_ID=" + F_Master_ID + "&F_Master_Table=C_Comment&File=" + File;

        //System.out.println(File);

        GetServiceData.SendRequest(Path, mQueue, new GetServiceData.VolleyStringCallback() {
            @Override
            public void onSendRequestSuccess(String result) {

                //System.out.println("Test");
            }

        });
    }

    private File configFileName(String prefix, String extension) {

        String fileName = FileUtil.getUniqueFileName();

        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);

        return new File(dir, prefix + fileName + extension);

    }

    private void UpdateIssueFile(String F_Keyin, String F_Master_ID) {

        pDialog.setMessage("Uploading...");

        pDialog.show();

        RequestQueue mQueue = Volley.newRequestQueue(this);

        NewIssueFile_List = mListAdapter.getAllitem();

        List<UploadImage> UploadImage_List = new ArrayList<UploadImage>();

        int i = 0;

        for (NewIssueFile_Item FileItem : NewIssueFile_List) {

            //UploadImage_List.add(i,new UploadImage(FileItem.GetImageBitMap(),FileItem.GetImageName()));

            switch (FileItem.GetFileType())
            {
                case Image:
                    Upload_Issue_File(F_Keyin, F_Master_ID, FileItem.GetImageName());

                    File ImageFileUpload = new File(FileItem.GetImagePath());

                    GetServiceData.uploadImage(GetServiceData.ServicePath + "/Upload_Issue_File_MultiPart", mQueue, ImageFileUpload, "");
                    break;
                case Video:

                    File VideoFileUpload = new File(FileItem.GetVideoPath());

                    Upload_Issue_File(F_Keyin, F_Master_ID, VideoFileUpload.getName());

                    GetServiceData.uploadImage(GetServiceData.ServicePath + "/Upload_Issue_File_MultiPart", mQueue, VideoFileUpload, "");

                    //System.out.println(VideoFileUpload.getName());
                    break;
            }

            i++;

        }

        pDialog.hide();

        GoIssueInfo(IssueID);

    }

    private void C_Comment_Insert() {

        EditText txt_Comment = (EditText) findViewById(R.id.txt_Comment);

        String WorkID = UserData.WorkID;

        String Comment = txt_Comment.getText().toString();

        if (Comment != "") {

            Map<String, String> map = new HashMap<String, String>();
            map.put("F_Keyin", WorkID);
            map.put("F_Master_Table", "C_Issue");
            map.put("F_Master_ID",IssueID);
            map.put("F_Comment", Comment);

            RequestQueue mQueue = Volley.newRequestQueue(this);

//            String Path = GetServiceData.ServicePath + "/C_Comment_Insert?F_Keyin=" + WorkID + "&F_Master_Table=C_Issue&F_Master_ID=" + IssueID + "&F_Comment=" + Comment;

            String Path = GetServiceData.ServicePath + "/C_Comment_Insert";


            GetServiceData.SendPostRequest(Path, mQueue, new GetServiceData.VolleyStringCallback() {
                @Override
                public void onSendRequestSuccess(String result) {

                    System.out.println("Test");

                    Find_Issue_Comment(IssueID);

                    EditText txt_Comment = (EditText) findViewById(R.id.txt_Comment);

                    txt_Comment.setText("");
                }

            },map);

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

        pDialog.setMessage("Downloading json...");
        pDialog.show();

        RequestQueue mQueue = Volley.newRequestQueue(this);

        String Path = GetServiceData.ServicePath + "/Issue_File_List?F_SeqNo=" + Issue_ID;

        GetServiceData.getString(Path, mQueue, new GetServiceData.VolleyCallback() {
            @Override
            public void onSuccess(JSONObject result) {

                pDialog.hide();

                IssueInfoFile_ListMapping(result);
            }
        });

    }

    private void IssueInfoFile_ListMapping(JSONObject result) {
        try {
            IssueFile_List.clear();

            JSONArray UserArray = new JSONArray(result.getString("Key"));

            if(UserArray.length() > 0 )
            {
                RelativeLayout Top_Banner = (RelativeLayout) findViewById(R.id.Top_Banner);

                Top_Banner.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {

                        GoToIssue_Gallery();
                    }
                });
            }
            else
            {
                ImageView Img_IssueInfo_Gallery = (ImageView) findViewById(R.id.Img_IssueInfo_Gallery);

                Img_IssueInfo_Gallery.setVisibility(View.GONE);
            }

//            for (int i = 0; i < UserArray.length(); i++) {
//
//                JSONObject IssueData = UserArray.getJSONObject(i);
//
//                String F_DownloadFilePath = IssueData.getString("F_DownloadFilePath");
//
//                String F_FileName = IssueData.getString("F_FileName");
//
//                String F_CreateDate = IssueData.getString("F_CreateDate");
//
//                if(!(F_DownloadFilePath.contains("http://") || F_DownloadFilePath.contains("https://")))
//                {
//                    F_DownloadFilePath = "http:" + F_DownloadFilePath;
//
//                }
//
//                IssueFile_List.add(i, new IssueFile_Item(F_DownloadFilePath, "", "", ""));
//
//                //ImageView IV = new ImageView(this);
//
//                //GetServiceData.GetImageByImageLoad(F_DownloadFilePath, IV);
//
//
//
//                Image image = new Image();
//                image.setName(F_FileName);
//
//                image.setSmall(F_DownloadFilePath);
//                image.setMedium(F_DownloadFilePath);
//                image.setLarge(F_DownloadFilePath);
//                image.setTimestamp(F_CreateDate);
//
//                ImageViewList.add(i, image);
//            }

        } catch (JSONException ex) {

        }


    }


    private void Issue_Get(String Issue_ID) {

        RequestQueue mQueue = Volley.newRequestQueue(this);

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

                String F_SeqNo = String.valueOf(IssueData.getInt("F_SeqNo"));

                String F_Owner_en = IssueData.getString("F_Owner_en");

                String Issue_OwnerEN = IssueData.getString("Issue_OwnerEN");

                String F_ModelName = IssueData.getString("F_ModelName");

                String F_Priority = IssueData.getString("F_Priority");

                String F_CreateDate = AppClass.ConvertDateString(IssueData.getString("F_CreateDate"));

                String F_Subject = IssueData.getString("F_Subject");

                TextView txt_IssueInfo_Author = (TextView) findViewById(R.id.txt_IssueInfo_Author);

                TextView txt_IssueInfo_Owner = (TextView) findViewById(R.id.txt_IssueInfo_Owner);

                TextView txt_IssueInfo_ProjectName = (TextView) findViewById(R.id.txt_IssueInfo_ProjectName);

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

                txt_IssueInfo_ProjectName.setText(F_ModelName);

                txt_Issue_Subject.setText(F_Subject);

                Img_IssuePriority.setImageResource(AppClass.PriorityImage(F_Priority));

            }
        } catch (JSONException ex) {

        }


    }

    private void Find_Issue_Comment(String Issue_ID) {

        pDialog.setMessage("Loading...");

        pDialog.show();

        RequestQueue mQueue = Volley.newRequestQueue(this);

        String Path = GetServiceData.ServicePath + "/Find_Issue_Comment?Issue_ID=" + Issue_ID;

        GetServiceData.getString(Path, mQueue, new GetServiceData.VolleyCallback() {
            @Override
            public void onSuccess(JSONObject result) {

                IssueCommentMapping(result);

                pDialog.hide();
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

                String F_CreateDate = AppClass.ConvertDateString(IssueData.getString("F_CreateDate"));

                String F_SeqNo = String.valueOf(IssueData.getInt("F_SeqNo"));

                String F_Comment = IssueData.getString("F_Comment");

                WorkNote_List.add(i, new WorkNote_Item(F_Owner_en, F_Keyin, F_CreateDate, F_Comment, "", ""));
            }


            // ListView 中所需之資料參數可透過修改 Adapter 的建構子傳入
            mListAdapter = new WorkNoteAdapter(this, WorkNote_List);

            //設定 ListView 的 Adapter
            lsv_main.setAdapter(mListAdapter);
        } catch (JSONException ex) {

        }


    }
}
