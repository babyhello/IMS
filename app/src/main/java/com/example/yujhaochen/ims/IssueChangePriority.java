package com.example.yujhaochen.ims;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.zfdang.multiple_images_selector.ImagesSelectorActivity;
import com.zfdang.multiple_images_selector.SelectorSettings;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IssueChangePriority extends AppCompatActivity {

    static final int REQUEST_VIDEO_CAPTURE = 1;

    static final int REQUEST_IMAGE_CAPTURE = 2;

    static final int REQUEST_Voice_CAPTURE = 3;

    static final int REQUEST_Photo_CAPTURE = 4;

    private static final String WRITE_EXTERNAL_STORAGE = "android.permission.WRITE_EXTERNAL_STORAGE";
    private static final String READ_EXTERNAL_STORAGE = "android.permission.READ_EXTERNAL_STORAGE";
    private static final int REQUEST_EXTERNAL_STORAGE = 1;

    private File ImageFile;

    private File VideoFile;

    private File VoiceFile;

    private NewIssueFileAdapter mListAdapter;

    public String Reason;
    private ListView list;
    private EditText filterText = null;
    ArrayAdapter<String> adapter = null;
    //private static final String TAG = "CityList";
    public static List<List_Item> _DataList = new ArrayList<List_Item>();
    String[] DataStringArray;
    public String SelectValue;
    public String SelectText;
    private Context mContext;
    private ArrayList<String> mMultiPhotoPath = new ArrayList<>();
    private ListView lsv_main;

    private String IssueID;
    private String StatusType;

    List<NewIssueFile_Item> NewIssueFile_List = new ArrayList<NewIssueFile_Item>();
    private RequestQueue mQueue;
    private int FileProcessCount = 0;
    private String Priority;
    private String Author;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_issue_change_owner);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        mContext = this;

        Bundle Bundle = getIntent().getExtras();

        IssueID = Bundle.getString("IssueID");

        StatusType = Bundle.getString("StatusType");

        Priority = Bundle.getString("Priority");

        Author = Bundle.getString("Author");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        toolbar.setTitle("Priority");

        DataStringArray = new String[_DataList.size()];

        for (int i = 0; i < _DataList.size(); i++) {
            DataStringArray[i] = _DataList.get(i).GetText();
        }

        adapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_list_item_single_choice, DataStringArray);

        lsv_main = (ListView) findViewById(R.id.listView);

        lsv_main.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                String NewPosition = String.valueOf(position + 1);

                new AlertDialog.Builder(IssueChangePriority.this)
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

        final EditText txt_Reason = (EditText) findViewById(R.id.txt_Reason);

        TextView txt_Close_Finish = (TextView) findViewById(R.id.txt_Close_Finish);

        txt_Close_Finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                v.setVisibility(View.GONE);

                Change_Issue_Priority(IssueID, SelectValue);

                String CommentTitle = "@Issue Priority Change";

                String CommentText = "◎Issue Priority Change： 『" + PriorityConvert(Priority) + "』change to 『" + PriorityConvert(SelectValue) + "』";

                CommentText += "Reason: 『" + txt_Reason.getText().toString() + "』\n";

                C_Comment_Insert(CommentText, IssueID);

                List<String> WorkID_List = new ArrayList<String>();

                WorkID_List.add(0, Author);

                AppClass.Send_Notification(WorkID_List, CommentTitle, CommentText, "IssueInfo", "IssueInfo", IssueID, mContext);
            }
        });

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


                Intent intent = new Intent(mContext, ImagesSelectorActivity.class);

                intent.putExtra(SelectorSettings.SELECTOR_MAX_IMAGE_NUMBER, 5);

                intent.putExtra(SelectorSettings.SELECTOR_MIN_IMAGE_SIZE, 100000);

                intent.putExtra(SelectorSettings.SELECTOR_SHOW_CAMERA, false);

                startActivityForResult(intent, REQUEST_Photo_CAPTURE);

            }
        });

        Lnl_Microphone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                int permission = ActivityCompat.checkSelfPermission(mContext, android.Manifest.permission.RECORD_AUDIO);

                if (permission != PackageManager.PERMISSION_GRANTED) {
                    // 無權限，向使用者請求
                    ActivityCompat.requestPermissions(
                            IssueChangePriority.this,
                            new String[]{android.Manifest.permission.RECORD_AUDIO}, 1
                    );


                } else {
                    VoiceFile = configFileName("P", ".3gp");

                    Intent recordIntent = new Intent(mContext, IssueVoiceRecord.class);

                    recordIntent.putExtra("fileName", VoiceFile.getAbsolutePath());

                    startActivityForResult(recordIntent, REQUEST_Voice_CAPTURE);

                }

            }
        });

        ImageButton Btn_Priority1 = (ImageButton) findViewById(R.id.Btn_Priority1);
        ImageButton Btn_Priority2 = (ImageButton) findViewById(R.id.Btn_Priority2);
        ImageButton Btn_Priority3 = (ImageButton) findViewById(R.id.Btn_Priority3);
        Btn_Priority1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Set_CheckButton("1");
            }
        });


        Btn_Priority2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Set_CheckButton("2");
            }
        });


        Btn_Priority3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Set_CheckButton("3");
            }
        });


        //預設給limitation
        Set_CheckButton(Priority);
    }

    private void Set_CheckButton(String Value) {
        SelectValue = Value;
        ImageButton Btn_Priority1 = (ImageButton) findViewById(R.id.Btn_Priority1);
        ImageButton Btn_Priority2 = (ImageButton) findViewById(R.id.Btn_Priority2);
        ImageButton Btn_Priority3 = (ImageButton) findViewById(R.id.Btn_Priority3);

        Btn_Priority1.setBackgroundResource(R.mipmap.btn_change_priop1_nor);
        Btn_Priority2.setBackgroundResource(R.mipmap.btn_change_priop2_nor);
        Btn_Priority3.setBackgroundResource(R.mipmap.btn_change_priop3_nor);


        switch (Value) {
            case "1":

                Btn_Priority1.setBackgroundResource(R.mipmap.btn_change_priop1_sel);
                break;

            case "2":

                Btn_Priority2.setBackgroundResource(R.mipmap.btn_change_priop2_sel);
                break;
            case "3":

                Btn_Priority3.setBackgroundResource(R.mipmap.btn_change_priop3_sel);
                break;
        }


    }

    private AdapterView.OnItemClickListener listViewOnItemClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            NewIssueFileAdapter NewIssueFileAdapter = (NewIssueFileAdapter) parent.getAdapter();

            NewIssueFile_Item NewIssueFile_Item = (NewIssueFile_Item) NewIssueFileAdapter.getItem(position);


        }
    };

    public void Set_Data_List(List<List_Item> DataList) {
        _DataList = DataList;

        DataStringArray = new String[DataList.size()];

        for (int i = 0; i < DataList.size(); i++) {
            DataStringArray[i] = DataList.get(i).GetText();
        }

        adapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_list_item_single_choice, DataStringArray);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {

                SelectValue = _DataList.get(position).GetValue();

                SelectText = _DataList.get(position).GetText();

                //onClick(v);
            }
        });
    }

    private void Close_Issue(final String IssueID, final String WorkID, final String CloseType) {


        if (!TextUtils.isEmpty(IssueID)) {


            if (mQueue == null) {
                mQueue = Volley.newRequestQueue(mContext);
            }

            Map<String, String> map = new HashMap<String, String>();
            map.put("IssueNo", IssueID);
            map.put("WorkID", WorkID);
            map.put("CloseType", CloseType);

            String Path = GetServiceData.ServicePath + "/Close_Issue";

            GetServiceData.SendPostRequest(Path, mQueue, new GetServiceData.VolleyStringCallback() {
                @Override
                public void onSendRequestSuccess(String result) {

                    // AppClass.AlertMessage("Close Issue Success", mContext);


                }

                @Override
                public void onSendRequestError(String result) {
                    Log.w("NotificationSuccess", result);
                }

            }, map);


        } else {
            //AppClass.AlertMessage("Close Issue Error", mContext);
        }


    }

    private void C_Comment_Insert(String Comment, String IssueID) {


        String WorkID = UserData.WorkID;

        if (!TextUtils.isEmpty(Comment)) {

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


                    try {

                        JSONObject obj = new JSONObject(result);

                        JSONArray UserArray = new JSONArray(obj.getString("Key"));

                        if (UserArray.length() > 0) {

                            JSONObject IssueData = UserArray.getJSONObject(0);

                            String CommentNo = String.valueOf(IssueData.getInt("CommentNo"));

                            UpdateIssueFile(UserData.WorkID, CommentNo);
                        }

                    } catch (Throwable t) {

                    }


                }

                @Override
                public void onSendRequestError(String result) {

                }

            }, map);

        }


    }

    private File configFileName(String prefix, String extension) {

        String fileName = FileUtil.getUniqueFileName();

        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);

        return new File(dir, prefix + fileName + extension);

    }
//
//    public void IssuePriorityChange() {
//        List<List_Item> PriorityList = new ArrayList<List_Item>();
//
//        PriorityList.add(0, new List_Item("Critical (P1)", "1"));
//
//        PriorityList.add(1, new List_Item("Major (P2)", "2"));
//
//        PriorityList.add(2, new List_Item("Minor (P3)", "3"));
//
//        final Alert_Search_Dialog DataDialog = new Alert_Search_Dialog(mContext, "Select Issue Priority", PriorityList);
//
//        DataDialog.SetOnDialog_Finish_Listener(new Alert_Search_Dialog.Dialog_Finish_Listener() {
//            @Override
//            public void Finished() {
//
//                if (!TextUtils.isEmpty(DataDialog.SelectValue)) {
//
//                    Change_Issue_Priority(IssueID, DataDialog.SelectValue);
//
//                    String CommentTitle = "@Issue Priority Change";
//
//                    String CommentText = "◎Issue Priority Change： 『" + PriorityConvert(Issue_Priotity) + "』change to 『" + PriorityConvert(DataDialog.SelectValue) + "』";
//
//                    CommentText += "Reason: 『" + DataDialog.GetReason() + "』\n";
//
//                    C_Comment_Insert(CommentText);
//
//                    List<String> WorkID_List = new ArrayList<String>();
//
//                    WorkID_List.add(0, Author);
//
//                    AppClass.Send_Notification(WorkID_List, CommentTitle, CommentText, "IssueInfo", "IssueInfo", IssueID, mContext);
//
//                }
//            }
//
//            @Override
//            public void Cancel() {
//
//            }
//
//        });
//
//        DataDialog.setCancelable(false);
//
//        DataDialog.show();
//    }

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

    private void UpdateIssueFile(String F_Keyin, String F_Master_ID) {


        if (mQueue == null) {
            mQueue = Volley.newRequestQueue(this);
        }

        NewIssueFile_List = mListAdapter.getAllitem();

        List<UploadImage> UploadImage_List = new ArrayList<UploadImage>();

        int i = 0;

        if (NewIssueFile_List.size() == 0) {

            finish();

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

    private void Upload_Issue_File(String F_Keyin, String F_Master_ID, String File) {

        if (mQueue == null) {
            mQueue = Volley.newRequestQueue(this);
        }

        String Path = GetServiceData.ServicePath + "/Upload_Issue_File?F_Keyin=" + F_Keyin + "&F_Master_ID=" + F_Master_ID + "&F_Master_Table=C_Comment&File=" + File;


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

    public void UploadIssueFile_File(String UPLOAD_URL, RequestQueue mQueue, File file, String stringPart) {

        MultipartRequest MultiPart = new MultipartRequest(UPLOAD_URL,
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        FileProcessCount++;

                        if (FileProcessCount == mListAdapter.getCount()) {
                            finish();
                        }
                    }


                },
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        FileProcessCount++;

                        if (FileProcessCount == mListAdapter.getCount()) {

                            finish();

                        }

                    }
                }, file, "");


        mQueue.add(MultiPart);
    }

    private void GoCamera() {

        int permission = ActivityCompat.checkSelfPermission(mContext, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // 無權限，向使用者請求
            ActivityCompat.requestPermissions(
                    IssueChangePriority.this,
                    new String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE}, REQUEST_EXTERNAL_STORAGE
            );


        } else {

            permission = ActivityCompat.checkSelfPermission(mContext, android.Manifest.permission.CAMERA);

            if (permission != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(
                        IssueChangePriority.this,
                        new String[]{android.Manifest.permission.CAMERA}, REQUEST_IMAGE_CAPTURE
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


    public String GetReason() {
        EditText txt_Reason = (EditText) findViewById(R.id.txt_Reason);

        return txt_Reason.getText().toString();
    }


    private TextWatcher filterTextWatcher = new TextWatcher() {

        public void afterTextChanged(Editable s) {
        }

        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {
            adapter.getFilter().filter(s);
        }
    };

    private void dispatchTakeVideoIntent() {


        int permission = ActivityCompat.checkSelfPermission(mContext, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // 無權限，向使用者請求
            ActivityCompat.requestPermissions(
                    IssueChangePriority.this,
                    new String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE}, REQUEST_EXTERNAL_STORAGE
            );
        } else {

            permission = ActivityCompat.checkSelfPermission(mContext, android.Manifest.permission.CAMERA);

            if (permission != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(
                        IssueChangePriority.this,
                        new String[]{android.Manifest.permission.CAMERA}, REQUEST_IMAGE_CAPTURE
                );

            } else {

                Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);

                if (takeVideoIntent.resolveActivity(mContext.getPackageManager()) != null) {

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


    private File VideoFileName(String prefix, String extension) {

        String fileName = FileUtil.getUniqueFileName();

        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);

        return new File(dir, prefix + fileName + extension);

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {


        if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK) {


            if (VideoFile.exists()) {

                AddVideoItem(VideoFile.getAbsolutePath());

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
                Log.e("Exception", e.getMessage(), e);
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
}
