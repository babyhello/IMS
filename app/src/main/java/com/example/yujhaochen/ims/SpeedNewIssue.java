package com.example.yujhaochen.ims;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard;

public class SpeedNewIssue extends AppCompatActivity {

    private Context mcontext;

    UploadProgressBar mUploadProgressBar;

    private ProjectAdapter mListAdapter;

    public static Bitmap ImgBmp;

    public static String ImagePath;

    public static String VideoPath;
    private String IssueID;
    private ProgressDialog pDialog;
    List<Project_Item> Project_List = new ArrayList<Project_Item>();
    private static boolean FromGallary = false;

    private ViewGroup mViewGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mcontext = SpeedNewIssue.this;

        final LayoutInflater inflater = LayoutInflater.from(this);

        View v = inflater.inflate(R.layout.activity_speed_new_issue, null);

        setContentView(v);

        mViewGroup = (ViewGroup) v.getParent();

        pDialog = new ProgressDialog(this);

        UserDB UserDB = new UserDB(SpeedNewIssue.this);

        //如果進來程式有資料的話就不用再登入
        if (UserDB.getCount() > 0) {

            UserData UserData = new UserData();

            UserData = UserDB.getAll().get(0);

        }

        //getSupportActionBar().hide();

        Issue_Init(UserData.WorkID);

        Button btn_Save = (Button) findViewById(R.id.btn_Save);

        btn_Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                Save_IssueData();

            }
        });

        GetPM_Data(UserData.WorkID);

        // Get intent, action and MIME type
        Intent intent = getIntent();

        if (intent != null) {
            String action = intent.getAction();

            String type = intent.getType();

            if (Intent.ACTION_SEND.equals(action) && type != null) {
                if ("text/plain".equals(type)) {
                    handleSendText(intent); // Handle text being sent
                } else if (type.startsWith("image/")) {
                    FromGallary = true;
                    handleSendImage(intent); // Handle single image being sent
                } else if (type.startsWith("video/")) {

                    handleSendVideo(intent); // Handle single image being sent
                }
            } else if (Intent.ACTION_SEND_MULTIPLE.equals(action) && type != null) {
                if (type.startsWith("image/")) {
                    handleSendMultipleImages(intent); // Handle multiple images being sent
                }
            } else {
                ImageView Img_ViewIssuePhoto = (ImageView) findViewById(R.id.Img_ViewIssuePhoto);
                Img_ViewIssuePhoto.setImageBitmap(AppClass.loadBitmap(ImagePath));
            }
        }
    }


    @Override
    public void onBackPressed() {
        if (JCVideoPlayer.backPress()) {
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        JCVideoPlayer.releaseAllVideos();
    }


    private void Save_IssueData() {
        Spinner mSpinner = (Spinner) findViewById(R.id.spinner1);

        int SelectIndex = mSpinner.getSelectedItemPosition();

        Project_Item Project_Item = (Project_Item) mListAdapter.getItem(SelectIndex);

        String ModelID = Project_Item.ModelID;

        UpdateIssue(IssueID, ModelID);


    }


    private void Insert_Forcus_Data(String F_Keyin,String F_Owner,String F_PM_ID) {

        RequestQueue mQueue = Volley.newRequestQueue(SpeedNewIssue.this);

        String Path = GetServiceData.ServicePath + "/Insert_Focus_Model" + "?F_Keyin=" + F_Keyin + "&F_Owner=" + F_Owner + "&F_PM_ID=" + F_PM_ID;

        GetServiceData.getString(Path, mQueue, new GetServiceData.VolleyCallback() {
            @Override
            public void onSuccess(JSONObject result) {

                PMData_Mapping(result);
            }
        });

    }

    private void UpdateIssue(final String IssueID, final String ModelID) {

        EditText txt_Subject = (EditText) findViewById(R.id.txt_Subject);

        String WorkID = UserData.WorkID;

        String Subject = txt_Subject.getText().toString();

        if (Subject != "") {


            RequestQueue mQueue = Volley.newRequestQueue(this);

            Map<String, String> map = new HashMap<String, String>();
            map.put("F_SeqNo", IssueID);
            map.put("F_PM_ID", ModelID);
            map.put("F_Priority", "1");
            map.put("F_Subject", Subject);

            String Path = GetServiceData.ServicePath + "/Issue_Update";

            GetServiceData.SendPostRequest(Path, mQueue, new GetServiceData.VolleyStringCallback() {
                @Override
                public void onSendRequestSuccess(String result) {

                    UpdateIssueFile(UserData.WorkID, IssueID);  //更新附件

                }

            }, map);


        }

        Insert_Forcus_Data(WorkID,"",ModelID);


    }

    private void UpdateIssueFile(final String F_Keyin, final String F_Master_ID) {


        RequestQueue mQueue = Volley.newRequestQueue(this);

        int i = 0;

        String FilePath = "";

        if (!TextUtils.isEmpty(ImagePath)) {
            FilePath = ImagePath;
        } else if (!TextUtils.isEmpty(VideoPath)) {
            FilePath = GoCopyFile(VideoPath, ".mp4");
        }

        final File UploadFile = new File(FilePath);

        UploadFileProgress mUploadFileProgress = new UploadFileProgress(mcontext);

        mUploadFileProgress.Go_Upload(GetServiceData.ServicePath + "/Upload_Issue_File_MultiPart", UploadFile.getName(), FilePath, mViewGroup, new UploadFileListenter() {
            @Override
            public void Success() {
                Upload_Issue_File(F_Keyin,F_Master_ID,UploadFile.getName());
            }

            @Override
            public void Fault() {

            }
        });

    }

    public String GoCopyFile(String OldPath, String FileType) {

        String FilePath = "";

        try {

            File wantfile = new File(OldPath);
            File newfile = configFileName("P", FileType);

            InputStream in = new FileInputStream(wantfile);
            OutputStream out = new FileOutputStream(newfile);
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();

            FilePath = newfile.getAbsolutePath();
        } catch (Exception e) {
            Log.e("copy file error", e.toString());

// TODO: handle exception
        } finally {
            return FilePath;
        }


    }

    private void Upload_Issue_File(String F_Keyin, String F_Master_ID, String File) {

        RequestQueue mQueue = Volley.newRequestQueue(this);

        String Path = GetServiceData.ServicePath + "/Upload_Issue_File?F_Keyin=" + F_Keyin + "&F_Master_ID=" + F_Master_ID + "&F_Master_Table=C_Issue&File=" + File;

        //System.out.println(File);

        GetServiceData.SendRequest(Path, mQueue, new GetServiceData.VolleyStringCallback() {
            @Override
            public void onSendRequestSuccess(String result) {

                pDialog.hide();

                if (FromGallary) {
                    Intent intent = new Intent(SpeedNewIssue.this, MainTab.class);

                    startActivity(intent);

                    finish();
                } else {
                    finish();

                    GoIssueInfo(IssueID);
                }


            }

        });
    }
//
//    private void UpoadFileProgress()
//    {
//
//    }

    private void GoIssueInfo(String IssueID) {
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

    private void Issue_Init(String WorkID) {
        RequestQueue mQueue = Volley.newRequestQueue(this);

        String Path = GetServiceData.ServicePath + "/Issue_Init?F_Keyin=" + WorkID;

        GetServiceData.getString(Path, mQueue, new GetServiceData.VolleyCallback() {
            @Override
            public void onSuccess(JSONObject result) {

                try {


                    JSONArray UserArray = new JSONArray(result.getString("Key"));

                    if (UserArray.length() > 0) {

                        JSONObject IssueData = UserArray.getJSONObject(0);

                        String F_SeqNo = String.valueOf(IssueData.getInt("F_SeqNo"));

                        IssueID = F_SeqNo;

                        //System.out.println("IssueID" + F_SeqNo);
                    }
                } catch (JSONException ex) {

                }

            }
        });
    }

    private void GetPM_Data(String WorkID) {

        RequestQueue mQueue = Volley.newRequestQueue(this);

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

                String Model_Focus = ModelData.getString("Model_Focus");

                String Read = String.valueOf(ModelData.getDouble("Read"));

                Project_List.add(i,new Project_Item(ModelID,ModelName,ModelPic,"",Model_Focus,"0"));
            }


            if (Project_List.size() > 0) {
                // ListView 中所需之資料參數可透過修改 Adapter 的建構子傳入
                mListAdapter = new ProjectAdapter(this, Project_List);

                Spinner mSpinner = (Spinner) findViewById(R.id.spinner1);

                //設定 ListView 的 Adapter
                mSpinner.setAdapter(mListAdapter);
            }
        } catch (JSONException ex) {

        }


    }


    void handleSendText(Intent intent) {
        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (sharedText != null) {
            // Update UI to reflect text being shared
        }
    }

    void handleSendVideo(Intent intent) {
        Uri VideoUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (VideoUri != null) {

            VideoPath = getRealPathFromURI(this, VideoUri);

            JCVideoPlayerStandard jcVideoPlayerStandard = (JCVideoPlayerStandard) findViewById(R.id.videoplayer);
            jcVideoPlayerStandard.setUp(VideoPath
                    , JCVideoPlayerStandard.SCREEN_LAYOUT_NORMAL, "");
            //jcVideoPlayerStandard.thumbImageView.setImage("http://p.qpic.cn/videoyun/0/2449_43b6f696980311e59ed467f22794e792_1/640");

            ImageView Img_ViewIssuePhoto = (ImageView) findViewById(R.id.Img_ViewIssuePhoto);

            Img_ViewIssuePhoto.setVisibility(View.GONE);

        }
    }

    public String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    void handleSendImage(Intent intent) {
        Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (imageUri != null) {
            Uri uri = imageUri;
            //寫log

            //抽象資料的接口
            ContentResolver cr = this.getContentResolver();
            try {
                //由抽象資料接口轉換圖檔路徑為Bitmap
                Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));

                //File myFile = new File(uri.toString());

                File MyNewFile = configFileName("P", ".jpg");


                FileOutputStream out = null;
                try {
                    out = new FileOutputStream(MyNewFile);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 85, out); // bmp is your Bitmap instance
                    // PNG is a lossless format, the compression factor (100) is ignored
                    ImgBmp = bitmap;

                    ImagePath = MyNewFile.getAbsolutePath();

                    ImageView Img_ViewIssuePhoto = (ImageView) findViewById(R.id.Img_ViewIssuePhoto);
                    Img_ViewIssuePhoto.setImageBitmap(AppClass.loadBitmap(ImagePath));


                    JCVideoPlayerStandard jcVideoPlayerStandard = (JCVideoPlayerStandard) findViewById(R.id.videoplayer);



                    jcVideoPlayerStandard.setVisibility(View.GONE);


                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (out != null) {
                            out.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (FileNotFoundException e) {
                Log.e("Exception", e.getMessage(), e);
            }
        }
    }

    private File configFileName(String prefix, String extension) {

        String fileName = FileUtil.getUniqueFileName();

        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);

        return new File(dir, prefix + fileName + extension);

    }

    void handleSendMultipleImages(Intent intent) {
        ArrayList<Uri> imageUris = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
        if (imageUris != null) {
            // Update UI to reflect multiple images being shared
        }
    }
}

