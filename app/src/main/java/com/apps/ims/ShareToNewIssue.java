package com.apps.ims;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

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
import com.zfdang.multiple_images_selector.ImagesSelectorActivity;
import com.zfdang.multiple_images_selector.SelectorSettings;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


//import com.nguyenhoanglam.imagepicker.activity.ImagePicker;
//import com.nguyenhoanglam.imagepicker.activity.ImagePickerActivity;


public class ShareToNewIssue extends AppCompatActivity {

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

    private Context mContext;
    private String IssueID;

    private String ModelID;

    private String ModelName;

    private String FilePath;

    private File ImageFile;

    private File VideoFile;

    private File VoiceFile;

    private int FileProcessCount = 0;

    private RequestQueue mQueue;

    private ArrayList<String> mMultiPhotoPath = new ArrayList<>();

    private static boolean FromGallary = false;

    public static String ImagePath;

    List<Project_Item> Project_List = new ArrayList<Project_Item>();

    private ProjectAdapter mProjectListAdapter;

    private void initData() {

        Bundle Bundle = this.getIntent().getExtras();

        EditText txt_NewIssue_Subjectv = (EditText) findViewById(R.id.txt_NewIssue_Subject);

        if (Bundle != null) {
            if (Bundle.getString("Subject") != null) {
                txt_NewIssue_Subjectv.setText(Bundle.getString("Subject"));
            }
        }


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


        // TODO Auto-generated method stub
        super.onResume();

//        answer_et.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                InputMethodManager imm = (InputMethodManager)getSystemService(
//                        Context.INPUT_METHOD_SERVICE);
//                imm.hideSoftInputFromWindow(edit_text.getWindowToken(), 0);
//            }
//        }, 100);
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

        mContext = this;

        UserDB UserDB = new UserDB(ShareToNewIssue.this);

        //如果進來程式有資料的話就不用再登入
        if (UserDB.getCount() > 0) {

            UserData UserData = new UserData();

            UserData = UserDB.getAll().get(0);

        } else {
            Intent intent = new Intent(ShareToNewIssue.this, LoginAccount.class);
            startActivity(intent);
            ShareToNewIssue.this.finish();
        }

        setContentView(R.layout.activity_share_newissue);

        pDialog = new ProgressDialog(this);

        View view = this.getCurrentFocus();

        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }


        Toolbar mActionBarToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mActionBarToolbar);
        getSupportActionBar().setTitle("Select Project");

        LinearLayout Lnl_Picture = (LinearLayout) findViewById(R.id.Lnl_Picture);
        LinearLayout Lnl_Camera = (LinearLayout) findViewById(R.id.Lnl_Camera);
        LinearLayout Lnl_Microphone = (LinearLayout) findViewById(R.id.Lnl_Microphone);
        LinearLayout Lnl_Photo = (LinearLayout) findViewById(R.id.Lnl_Photo);
        LinearLayout Lnl_Contact = (LinearLayout) findViewById(R.id.Lnl_Contact);
        final LinearLayout Lnl_Model = (LinearLayout) findViewById(R.id.Lnl_Model);

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


// start multiple photos selector
                Intent intent = new Intent(ShareToNewIssue.this, ImagesSelectorActivity.class);
// max number of images to be selected
                intent.putExtra(SelectorSettings.SELECTOR_MAX_IMAGE_NUMBER, 5);
// min size of image which will be shown; to filter tiny images (mainly icons)
                intent.putExtra(SelectorSettings.SELECTOR_MIN_IMAGE_SIZE, 100000);
// show camera or not
                intent.putExtra(SelectorSettings.SELECTOR_SHOW_CAMERA, false);
// pass current selected images as the initial value
//                intent.putStringArrayListExtra(SelectorSettings.SELECTOR_INITIAL_SELECTED_LIST, mMultiPhotoPath);
// start the selector
                startActivityForResult(intent, REQUEST_Photo_CAPTURE);


            }
        });

        Lnl_Microphone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                int permission = ActivityCompat.checkSelfPermission(ShareToNewIssue.this, Manifest.permission.RECORD_AUDIO);

                if (permission != PackageManager.PERMISSION_GRANTED) {
                    // 無權限，向使用者請求
                    ActivityCompat.requestPermissions(
                            ShareToNewIssue.this,
                            new String[]{Manifest.permission.RECORD_AUDIO}, 1
                    );


                } else {
                    VoiceFile = configFileName("P", ".3gp");

                    Intent recordIntent = new Intent(ShareToNewIssue.this, IssueVoiceRecord.class);

                    recordIntent.putExtra("fileName", VoiceFile.getAbsolutePath());

                    startActivityForResult(recordIntent, REQUEST_Voice_CAPTURE);

                }

            }
        });

        //HelpDeskFunction();

        Lnl_Contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                HelpDeskFunction();


            }
        });

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

                new AlertDialog.Builder(ShareToNewIssue.this)
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
                ImageView Img_ViewIssuePhoto = (ImageView) findViewById(com.apps.ims.R.id.Img_ViewIssuePhoto);

                if (ImagePath != null) {
                    Img_ViewIssuePhoto.setImageBitmap(AppClass.loadBitmap(ImagePath));
                }

            }
        }

    }

    private void HelpDeskFunction() {
        final LinearLayout Lnl_Model = (LinearLayout) findViewById(R.id.Lnl_Model);

        ModelID = "12895";

        Toolbar mActionBarToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mActionBarToolbar);
        getSupportActionBar().setTitle("Help Desk");

        Lnl_Model.setVisibility(View.GONE);
    }

    private void GetPM_Data(String WorkID) {

        if (mQueue == null) {
            mQueue = Volley.newRequestQueue(ShareToNewIssue.this);
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

                String Model_Focus = ModelData.getString("Model_Focus");

                String Read = String.valueOf(ModelData.getDouble("Read"));

                Project_List.add(i, new Project_Item(ModelID, ModelName, ModelPic, "", Model_Focus, "0"));
            }


            if (Project_List.size() > 0) {
                // ListView 中所需之資料參數可透過修改 Adapter 的建構子傳入
                mProjectListAdapter = new ProjectAdapter(this, Project_List);

                Spinner mSpinner = (Spinner) findViewById(com.apps.ims.R.id.spinner1);


                //設定 ListView 的 Adapter
                mSpinner.setAdapter(mProjectListAdapter);


                mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                        ModelID = Project_List.get(position).GetModelID();

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });


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

    void handleSendImage(Intent intent) {
        Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (imageUri != null) {
            Uri uri = imageUri;
            //寫log

            //抽象資料的接口
            ContentResolver cr = this.getContentResolver();
            try {

                ImagePath = getRealPathFromURI(this, uri);

                String filename = ImagePath.substring(ImagePath.lastIndexOf("/") + 1);

                Bitmap photo = FileUtil.FilePathGetBitMap(ImagePath);

                Bitmap minibm = ThumbnailUtils.extractThumbnail(photo, 1024, 800);

                AddImageItem(minibm, filename, ImagePath);

            } catch (Exception e) {
                Log.e("Exception", e.getMessage(), e);
            }
        }
    }

    void handleSendVideo(Intent intent) {
        Uri VideoUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (VideoUri != null) {

            AddVideoItem(getRealPathFromURI(this, VideoUri));

        }
    }

    void handleSendMultipleImages(Intent intent) {
        ArrayList<Uri> imageUris = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
        if (imageUris != null) {
            // Update UI to reflect multiple images being shared
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

    private void Issue_Init(String WorkID) {
        if (mQueue == null) {
            mQueue = Volley.newRequestQueue(this);
        }

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


                    }
                } catch (JSONException ex) {

                    Log.w("New Issue EXception", ex.toString());

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

            if (!pDialog.isShowing()) {
                pDialog.show();
            }


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


        } else {
            AppClass.AlertMessage("Please Keyin Subject", ShareToNewIssue.this);
        }


    }


    public void setupUI(View view) {

        // Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    hideSoftKeyboard(ShareToNewIssue.this);
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

        if (NewIssueFile_List.size() == 0) {
            pDialog.hide();

            GoIssueInfo(IssueID);
        } else {
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


    public void UploadIssueFile_File(String UPLOAD_URL, RequestQueue mQueue, File file, String stringPart) {

        MultipartRequest MultiPart = new MultipartRequest(UPLOAD_URL,
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        FileProcessCount++;

//                        Log.w("FileProcessCount", String.valueOf(FileProcessCount));
//
//                        Log.w("mListAdapter", String.valueOf(mListAdapter.getCount()));
                        if (FileProcessCount == mListAdapter.getCount()) {
                            pDialog.hide();

                            GoIssueInfo(IssueID);
                        }
                    }


                },
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        FileProcessCount++;

//                Log.w("FileProcessCount",String.valueOf(FileProcessCount));
//
//                Log.w("mListAdapter",String.valueOf(mListAdapter.getCount()));
                        if (FileProcessCount == mListAdapter.getCount()) {
                            pDialog.hide();

                            GoIssueInfo(IssueID);
                        }

                    }
                }, file, "");


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

    private void AddListImageItem(ArrayList<String> ImagePathList) {

        for (String ImagePath : ImagePathList) {
            Bitmap photo = FileUtil.FilePathGetBitMap(ImagePath);

            Bitmap minibm = ThumbnailUtils.extractThumbnail(photo, 1024, 800);

            mListAdapter.addItem(new NewIssueFile_Item(minibm, ImagePath.substring(ImagePath.lastIndexOf("/") + 1), ImagePath, "", "", NewIssueFile_Item.FileType.Image));
        }

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


                Intent intentCamera =
                        new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                // 照片檔案名稱
                File pictureFile = configFileName("P", ".jpg");

                ImageFile = pictureFile;

                Uri uri = AppClass.GetFileURI(mContext,pictureFile,intentCamera);
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


        } else if (requestCode == REQUEST_Voice_CAPTURE && resultCode == RESULT_OK) {


            if (VoiceFile.exists()) {

                AddVoiceItem(VoiceFile.getAbsolutePath());

                VoiceFile = null;
            }


        } else if (requestCode == REQUEST_Photo_CAPTURE && resultCode == RESULT_OK) {

            try {

                mMultiPhotoPath = data.getStringArrayListExtra(SelectorSettings.SELECTOR_RESULTS);

                assert mMultiPhotoPath != null;

                AddListImageItem(mMultiPhotoPath);

            } catch (Exception e) {

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

                    Uri uri = AppClass.GetFileURI(mContext,_VideoFile,takeVideoIntent);
                    // 設定檔案名稱
                    takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);

                    startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
                }
            }


        }
    }

}

