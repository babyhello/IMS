package com.example.yujhaochen.ims;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
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
import android.widget.Toast;
import android.widget.VideoView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
//import com.nguyenhoanglam.imagepicker.activity.ImagePicker;
//import com.nguyenhoanglam.imagepicker.activity.ImagePickerActivity;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard;


public class NewIssue extends AppCompatActivity {

    static final int REQUEST_VIDEO_CAPTURE = 1;

    static final int REQUEST_IMAGE_CAPTURE = 2;

    static final int REQUEST_Voice_CAPTURE = 3;

    static final int REQUEST_Photo_CAPTURE = 4;

    static final String ACTION_SCAN = "com.google.zxing.client.android.SCAN";
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;
    private ProgressDialog pDialog;
    private static final String WRITE_EXTERNAL_STORAGE = "android.permission.WRITE_EXTERNAL_STORAGE";
    private static final String READ_EXTERNAL_STORAGE = "android.permission.READ_EXTERNAL_STORAGE";
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private ListView lsv_main;
    private NewIssueFileAdapter mListAdapter;
    private Camera camera;
    List<NewIssueFile_Item> NewIssueFile_List = new ArrayList<NewIssueFile_Item>();

    private String IssueID;

    private String ModelID;

    private String ModelName;

    private String FilePath;

    private File ImageFile;

    private File VideoFile;

    private File VoiceFile;

    private int FileProcessCount = 0;

    private RequestQueue mQueue;

    //private ArrayList<com.nguyenhoanglam.imagepicker.model.Image> SelectImages =  new ArrayList<>();;

    private void initData() {

        Bundle Bundle = this.getIntent().getExtras();

        ModelID = Bundle.getString("ModelID");

        ModelName = Bundle.getString("ModelName");


        final ImageView Img_IssueAuthor = (ImageView) findViewById(R.id.Img_IssueAuthor);

        TextView txt_NewIssue_Author = (TextView) findViewById(R.id.txt_NewIssue_Author);

        //TextView txt_NewIssue_ModelName = (TextView) findViewById(R.id.txt_NewIssue_ModelName);

        EditText txt_NewIssue_Subjectv = (EditText) findViewById(R.id.txt_NewIssue_Subject);

        if (Bundle.getString("Subject") != null) {
            txt_NewIssue_Subjectv.setText(Bundle.getString("Subject"));
        }

        Glide
                .with(NewIssue.this)
                .load(GetServiceData.ServicePath + "/Get_File?FileName=" + "//172.16.111.114/File/SDQA/Code/Admin/" + UserData.WorkID + ".jpg")
                .asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.mipmap.progress_image)
                .into(new SimpleTarget<Bitmap>(100, 100) {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {

                        Img_IssueAuthor.setImageBitmap(resource);

                    }
                });

        txt_NewIssue_Author.setText(UserData.EName);

        //txt_NewIssue_ModelName.setText(ModelName);

        Toolbar mActionBarToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mActionBarToolbar);
        getSupportActionBar().setTitle(ModelName);

        //setTitle(ModelName);
        //取得新的一筆newissue 序號
        Issue_Init(UserData.WorkID);

    }

    private void GoIssueInfo(String IssueID) {

        this.finish();

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

    private void checkCarema() {
        try {
            IntentIntegrator scanIntegrator = new IntentIntegrator(this);

            scanIntegrator.initiateScan();

        } catch (ActivityNotFoundException anfe) {
            System.out.println(anfe);
        }
    }

    @Override
    public void onResume() {


        super.onResume();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Discard this issue ?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();

                            finish();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    });
            builder.create().show();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_new_issue);

        pDialog = new ProgressDialog(this);

//        RelativeLayout NewIssue_Attachment = (RelativeLayout) findViewById(R.id.NewIssue_Attachment);
//
//        NewIssue_Attachment.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View arg0) {
//
//                //新增照片
//                GoCamera();
//                //checkCarema();
//                //dispatchTakeVideoIntent();
//
//            }
//        });

        LinearLayout Lnl_Picture = (LinearLayout) findViewById(R.id.Lnl_Picture);
        LinearLayout Lnl_Camera = (LinearLayout) findViewById(R.id.Lnl_Camera);
        LinearLayout Lnl_Microphone = (LinearLayout) findViewById(R.id.Lnl_Microphone);
        LinearLayout Lnl_Photo = (LinearLayout) findViewById(R.id.Lnl_Photo);




        Lnl_Picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                //新增照片
                GoCamera();

            }
        });

        Lnl_Camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                dispatchTakeVideoIntent();

            }
        });

        Lnl_Photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
// 建立 "選擇檔案 Action" 的 Intent
                Intent intent = new Intent( Intent.ACTION_PICK );

                // 過濾檔案格式
                intent.setType( "image/*" );

                // 建立 "檔案選擇器" 的 Intent  (第二個參數: 選擇器的標題)
                Intent destIntent = Intent.createChooser( intent, "Choose Photo" );

                // 切換到檔案選擇器 (它的處理結果, 會觸發 onActivityResult 事件)
                startActivityForResult( destIntent, REQUEST_Photo_CAPTURE );

            }
        });

        Lnl_Microphone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                int permission = ActivityCompat.checkSelfPermission(NewIssue.this, Manifest.permission.RECORD_AUDIO);

                if (permission != PackageManager.PERMISSION_GRANTED) {
                    // 無權限，向使用者請求
                    ActivityCompat.requestPermissions(
                            NewIssue.this,
                            new String[]{Manifest.permission.RECORD_AUDIO}, 1
                    );


                } else {
                    VoiceFile = configFileName("P", ".3gp");

                    Intent recordIntent = new Intent(NewIssue.this, IssueVoiceRecord.class);

                    recordIntent.putExtra("fileName", VoiceFile.getAbsolutePath());

                    startActivityForResult(recordIntent, REQUEST_Voice_CAPTURE);

                }

            }
        });
//
//        ImageView Img_IssueInfo_AddPhoto = (ImageView) findViewById(R.id.Img_IssueInfo_AddPhoto);
//
//        Img_IssueInfo_AddPhoto.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View arg0) {
//
//                //新增照片
//                GoCamera();
//
//            }
//        });
//        ImageView Img_IssueInfo_AddCamera = (ImageView) findViewById(R.id.Img_IssueInfo_AddCamera);
//
//        Img_IssueInfo_AddCamera.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View arg0) {
//
//                dispatchTakeVideoIntent();
//
//            }
//        });
//
//
//
//        ImageView Img_IssueInfo_AddVoice = (ImageView) findViewById(R.id.Img_IssueInfo_AddVoice);
//
//        Img_IssueInfo_AddVoice.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View arg0) {
//
//                VoiceFile = configFileName("P", ".3gp");
//
//                Intent recordIntent = new Intent(NewIssue.this, IssueVoiceRecord.class);
//
//                recordIntent.putExtra("fileName", VoiceFile.getAbsolutePath());
//
//                startActivityForResult(recordIntent, REQUEST_Voice_CAPTURE);
//
//            }
//        });

        TextView txt_NewIssue_Finish = (TextView) findViewById(R.id.txt_NewIssue_Finish);

        txt_NewIssue_Finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                UpdateIssue(IssueID, ModelID);


            }
        });


        lsv_main = (ListView) findViewById(R.id.listView);

        lsv_main.setOnItemClickListener(listViewOnItemClickListener);

        //QRCodeScann();
        lsv_main.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                String NewPosition = String.valueOf(position + 1);

                new AlertDialog.Builder(NewIssue.this)
                        .setTitle("want to delele?")
                        .setMessage("Want to delete " + NewPosition + " item?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mListAdapter.removeItem(position);
                                mListAdapter.notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .show();

                return false;
            }
        });

        NewIssueFile_List.clear();

        // ListView 中所需之資料參數可透過修改 Adapter 的建構子傳入
        mListAdapter = new NewIssueFileAdapter(this, NewIssueFile_List);

        //設定 ListView 的 Adapter
        lsv_main.setAdapter(mListAdapter);

        initData();

        setupUI(this.findViewById(android.R.id.content));


//        // Check if no view has focus:
//        View view = this.getCurrentFocus();
//        if (view != null) {
//            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
//            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
//        }
    }


    private void Issue_Init(String WorkID) {
        if (mQueue == null) {
            mQueue = Volley.newRequestQueue(this);
        }

        String Path = GetServiceData.ServicePath + "/Issue_Init?F_Keyin=" + WorkID;
        System.out.println(WorkID);
        GetServiceData.getString(Path, mQueue, new GetServiceData.VolleyCallback() {
            @Override
            public void onSuccess(JSONObject result) {

                try {


                    JSONArray UserArray = new JSONArray(result.getString("Key"));

                    if (UserArray.length() > 0) {

                        JSONObject IssueData = UserArray.getJSONObject(0);

                        String F_SeqNo = String.valueOf(IssueData.getInt("F_SeqNo"));

                        IssueID = F_SeqNo;


                    }
                } catch (JSONException ex) {

                    Log.w("New Issue EXception",ex.toString());

                }

            }
        });
    }


    private void UpdateIssue(final String IssueID, final String ModelID) {



        EditText txt_NewIssue_Subject = (EditText) findViewById(R.id.txt_NewIssue_Subject);

        String WorkID = UserData.WorkID;

        String Subject = txt_NewIssue_Subject.getText().toString().trim();



        if (!TextUtils.isEmpty(Subject)) {

            final TextView txt_NewIssue_Finish = (TextView) findViewById(R.id.txt_NewIssue_Finish);

            txt_NewIssue_Finish.setVisibility(View.GONE);

            pDialog.setMessage("Uploading...");

            pDialog.show();

            if (mQueue == null) {
                mQueue = Volley.newRequestQueue(this);
            }

            Log.w("Subject", Subject);

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

                    //txt_NewIssue_Finish.setVisibility(View.VISIBLE);
                }

                @Override
                public void onSendRequestError(String result) {
                    //txt_NewIssue_Finish.setVisibility(View.VISIBLE);
                }

            }, map);


        }
        else
        {
            AppClass.AlertMessage("Please Keyin Subject",NewIssue.this);
        }


    }


    public void setupUI(View view) {

        // Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    hideSoftKeyboard(NewIssue.this);
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

    private void UpdateIssueFile(String F_Keyin, String F_Master_ID) {


        if (mQueue == null) {
            mQueue = Volley.newRequestQueue(this);
        }

        NewIssueFile_List = mListAdapter.getAllitem();

        List<UploadImage> UploadImage_List = new ArrayList<UploadImage>();

        int i = 0;

        if (NewIssueFile_List.size() == 0)
        {
            pDialog.hide();

            GoIssueInfo(IssueID);
        }
        else
        {
            for (NewIssueFile_Item FileItem : NewIssueFile_List) {

                File _IssueFile;

                //Toast.makeText(NewIssue.this,String.valueOf(NewIssueFile_List.size()) , Toast.LENGTH_SHORT).show();

                switch (FileItem.GetFileType()) {
                    case Image:

                        //Log.w("FileType",FileItem.GetFileType().toString());
                        Upload_Issue_File(F_Keyin, F_Master_ID, FileItem.GetImageName());

                        _IssueFile = new File(FileItem.GetImagePath());

                        UploadIssueFile_File(GetServiceData.ServicePath + "/Upload_Issue_File_MultiPart", mQueue, _IssueFile, "");
                        break;
                    case Video:
                        File VideoFileUpload = new File(FileItem.GetVideoPath());

                        Upload_Issue_File(F_Keyin, F_Master_ID, VideoFileUpload.getName());

                        UploadIssueFile_File(GetServiceData.ServicePath + "/Upload_Issue_File_MultiPart", mQueue, VideoFileUpload, "");

                        break;
                    case Voice:
                        _IssueFile = new File(FileItem.GetVoicePath());

                        Upload_Issue_File(F_Keyin, F_Master_ID, _IssueFile.getName());

                        UploadIssueFile_File(GetServiceData.ServicePath + "/Upload_Issue_File_MultiPart", mQueue, _IssueFile, "");
                        break;

                }

                i++;

            }
        }




    }


    public void UploadIssueFile_File(String UPLOAD_URL, RequestQueue mQueue, File file, String stringPart){

        MultipartRequest MultiPart = new MultipartRequest(UPLOAD_URL,
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        FileProcessCount++;

                        Log.w("FileProcessCount", String.valueOf(FileProcessCount));

                        Log.w("mListAdapter", String.valueOf(mListAdapter.getCount()));
                        if (FileProcessCount == mListAdapter.getCount()) {
                            pDialog.hide();

                            GoIssueInfo(IssueID);
                        }
                    }


                },
                new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                FileProcessCount ++;

                Log.w("FileProcessCount",String.valueOf(FileProcessCount));

                Log.w("mListAdapter",String.valueOf(mListAdapter.getCount()));
                if (FileProcessCount == mListAdapter.getCount() )
                {
                    pDialog.hide();

                    GoIssueInfo(IssueID);
                }

            }
        },file,"");


        mQueue.add(MultiPart);
    }

    private void Upload_Issue_File(String F_Keyin, String F_Master_ID, String File) {

        if (mQueue == null) {
            mQueue = Volley.newRequestQueue(this);
        }

        String Path = GetServiceData.ServicePath + "/Upload_Issue_File?F_Keyin=" + F_Keyin + "&F_Master_ID=" + F_Master_ID + "&F_Master_Table=C_Issue&File=" + File;


        GetServiceData.SendRequest(Path, mQueue, new GetServiceData.VolleyStringCallback() {
            @Override
            public void onSendRequestSuccess(String result) {


            }

            @Override
            public void onSendRequestError(String result) {
                //Log.w("NotificationSuccess",result);
            }

        });
    }

    private void QRCodeScann() {
        CodeScanner.CodeReaderListener codeReaderListener = new CodeScanner.CodeReaderListener() {
            @Override
            public void codeReadResult(final String type, final String data) {
                // type就是條碼的類型，EAN、ISBN、QRCode...等等
                // data就是條碼的內容，為任意字串
                // 若掃描未完成，type和data的值將為null
            }
        };

        CodeScanner cs = new CodeScanner(this, codeReaderListener);

    }

    private AdapterView.OnItemClickListener listViewOnItemClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            NewIssueFileAdapter NewIssueFileAdapter = (NewIssueFileAdapter) parent.getAdapter();

            NewIssueFile_Item NewIssueFile_Item = (NewIssueFile_Item) NewIssueFileAdapter.getItem(position);


        }
    };

    private Bitmap compressImage(Bitmap image) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中

        int options = 100;

        while (baos.toByteArray().length / 20480 > 100) {//循环判断如果压缩后图片是否大于100kb,大于继续压缩

            baos.reset();//重置baos即清空baos

            options -= 10;//每次都减少10

            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中

        }

        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中

        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片


        return bitmap;

    }


    private void AddImageItem(Bitmap Image, String ImageName, String ImagePath) {

        mListAdapter.addItem(new NewIssueFile_Item(Image, ImageName, ImagePath, "", "", NewIssueFile_Item.FileType.Image));

        mListAdapter.notifyDataSetChanged();

    }

    private void AddVideoItem(String VideoPath) {

        mListAdapter.addItem(new NewIssueFile_Item(null, "", "", VideoPath, "", NewIssueFile_Item.FileType.Video));

        mListAdapter.notifyDataSetChanged();

    }

    private void AddVoiceItem(String VoicePath) {

        mListAdapter.addItem(new NewIssueFile_Item(null, "", "", "", VoicePath, NewIssueFile_Item.FileType.Voice));

        mListAdapter.notifyDataSetChanged();

    }

    private void GoCamera() {

        int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // 無權限，向使用者請求
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE}, REQUEST_EXTERNAL_STORAGE
            );


        } else {

            permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);

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

    private File configFileName(String prefix, String extension) {

        String fileName = FileUtil.getUniqueFileName();

        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);

        return new File(dir, prefix + fileName + extension);

    }


    private File VideoFileName(String prefix, String extension) {

        String fileName = FileUtil.getUniqueFileName();

        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);

        return new File(dir, prefix + fileName + extension);

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {




        if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK) {


            if (VideoFile.exists()) {

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


            if (ImageFile.exists()) {

                Bitmap photo = FileUtil.FilePathGetBitMap(ImageFile.getAbsolutePath());

                Bitmap minibm = ThumbnailUtils.extractThumbnail(photo, 1024, 800);

                AddImageItem(minibm, ImageFile.getName(), ImageFile.getAbsolutePath());

                ImageFile = null;
            }


        }
        else if (requestCode == REQUEST_Voice_CAPTURE && resultCode == RESULT_OK) {


            if (VoiceFile.exists()) {

                AddVoiceItem(VoiceFile.getAbsolutePath());

                VoiceFile = null;
            }


        }
        else if (requestCode == REQUEST_Photo_CAPTURE && resultCode == RESULT_OK)
        {

            Uri uri = data.getData();
            //抽象資料的接口
            ContentResolver cr = this.getContentResolver();
            try {
                //由抽象資料接口轉換圖檔路徑為Bitmap
                Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));

                File MyNewFile = configFileName("P", ".jpg");



                FileOutputStream out = null;

                try {


                    out = new FileOutputStream(MyNewFile);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 85, out); // bmp is your Bitmap instance
                    ImageFile = MyNewFile;

                    if (ImageFile.exists()) {

                        Log.w("HavePhtoto","HavePhtoto");

                        Bitmap photo = FileUtil.FilePathGetBitMap(ImageFile.getAbsolutePath());

                        Bitmap minibm = ThumbnailUtils.extractThumbnail(photo, 1024, 800);

                        AddImageItem(minibm, ImageFile.getName(), ImageFile.getAbsolutePath());

                        ImageFile = null;
                    }



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
        else if (resultCode == RESULT_OK) {

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

    public void uploadImg(List<UploadImage> File_List) {

        PostUploadRequest PostUploadRequest = new PostUploadRequest(GetServiceData.ServicePath + "/Upload_Issue_File_MultiPart", File_List, new Response.Listener<String>() {
            @Override
            public void onResponse(String jsonObject) {
                //listener.onResponse(jsonObject);
                System.out.println(jsonObject);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                //listener.onErrorResponse(volleyError);
                System.out.println(volleyError);
            }
        });

        if (mQueue == null) {
            mQueue = Volley.newRequestQueue(this);
        }

        mQueue.add(PostUploadRequest);


    }

    private void dispatchTakeVideoIntent() {


        int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // 無權限，向使用者請求
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE}, REQUEST_EXTERNAL_STORAGE
            );
        } else {

            permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);

            if (permission != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.CAMERA}, REQUEST_IMAGE_CAPTURE
                );

            } else {

                Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);

                if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {

                    File _VideoFile = VideoFileName("P", ".mp4");

                    VideoFile = _VideoFile;

                    Uri uri = Uri.fromFile(_VideoFile);
                    // 設定檔案名稱
                    takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);

                    startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
                }
            }


        }
    }

}

