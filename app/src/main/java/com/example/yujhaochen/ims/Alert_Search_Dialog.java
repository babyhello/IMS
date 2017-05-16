package com.example.yujhaochen.ims;

import android.*;
import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.zfdang.multiple_images_selector.ImagesSelectorActivity;
import com.zfdang.multiple_images_selector.SelectorSettings;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class Alert_Search_Dialog extends Dialog implements View.OnClickListener {

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

    public interface Dialog_Finish_Listener {

        void Finished();

        void Cancel();

    }

    private Dialog_Finish_Listener mDialog_Finish_Listener;
    public String Reason;
    private ListView list;
    private EditText filterText = null;
    ArrayAdapter<String> adapter = null;
    //private static final String TAG = "CityList";
    List<List_Item> _DataList = new ArrayList<List_Item>();
    String[] DataStringArray;
    public String SelectValue;
    public String SelectText;
    private Context mContext;
    private ArrayList<String> mMultiPhotoPath = new ArrayList<>();
    public Alert_Search_Dialog(Context context, String Title, List<List_Item> DataList) {

        super(context);
        mContext = context;
        this._DataList = DataList;

        setContentView(R.layout.alert_search_dialog);

        DisplayMetrics metrics = context.getResources().getDisplayMetrics();

        int screenWidth = (int) (metrics.widthPixels * 0.9);

        int screenHeight = (int) (metrics.heightPixels * 0.78);

        getWindow().setLayout(screenWidth, screenHeight);

        this.setTitle(Title);


        filterText = (EditText) findViewById(R.id.EditBox);
        filterText.addTextChangedListener(filterTextWatcher);
        list = (ListView) findViewById(R.id.List);
        DataStringArray = new String[DataList.size()];

        for (int i = 0; i < DataList.size(); i++) {
            DataStringArray[i] = DataList.get(i).GetText();
        }

        adapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_single_choice, DataStringArray);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {

                SelectValue = _DataList.get(position).GetValue();

                SelectText = _DataList.get(position).GetText();

                //onClick(v);
            }
        });

        final EditText txt_Reason = (EditText) findViewById(R.id.txt_Reason);

        txt_Reason.setBackgroundResource(R.drawable.edittextnormalcolor);

        Button Btn_Finish = (Button) findViewById(R.id.Btn_Finish);

        Btn_Finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (!TextUtils.isEmpty(SelectValue)) {
                    if (!TextUtils.isEmpty(txt_Reason.getText().toString())) {
                        if (mDialog_Finish_Listener != null) {
                            mDialog_Finish_Listener.Finished();

                            dismiss();
                        }
                    } else {
                        txt_Reason.setBackgroundResource(R.drawable.edittextwarningcolor);
                    }

                }


            }
        });

        Button Btn_Cancel = (Button) findViewById(R.id.Btn_Cancel);

        Btn_Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mDialog_Finish_Listener != null) {

                    mDialog_Finish_Listener.Cancel();

                    dismiss();
                }


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


//// 建立 "選擇檔案 Action" 的 Intent
//                Intent intent = new Intent( Intent.ACTION_PICK );
//
//                // 過濾檔案格式
//                intent.setType( "image/*" );
//
//                // 建立 "檔案選擇器" 的 Intent  (第二個參數: 選擇器的標題)
//                Intent destIntent = Intent.createChooser( intent, "Choose Photo" );
//
//                // 切換到檔案選擇器 (它的處理結果, 會觸發 onActivityResult 事件)
//                startActivityForResult( destIntent, REQUEST_Photo_CAPTURE );


// start multiple photos selector
                Intent intent = new Intent(mContext, ImagesSelectorActivity.class);
// max number of images to be selected
                intent.putExtra(SelectorSettings.SELECTOR_MAX_IMAGE_NUMBER, 5);
// min size of image which will be shown; to filter tiny images (mainly icons)
                intent.putExtra(SelectorSettings.SELECTOR_MIN_IMAGE_SIZE, 100000);
// show camera or not
                intent.putExtra(SelectorSettings.SELECTOR_SHOW_CAMERA, false);
// pass current selected images as the initial value
//                intent.putStringArrayListExtra(SelectorSettings.SELECTOR_INITIAL_SELECTED_LIST, mMultiPhotoPath);
// start the selector
                // mContext.startActivityForResult(intent, REQUEST_Photo_CAPTURE);

                getOwnerActivity().startActivityForResult(intent, REQUEST_Photo_CAPTURE);

            }
        });

        Lnl_Microphone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                int permission = ActivityCompat.checkSelfPermission(mContext, android.Manifest.permission.RECORD_AUDIO);

                if (permission != PackageManager.PERMISSION_GRANTED) {
                    // 無權限，向使用者請求
                    ActivityCompat.requestPermissions(
                            getOwnerActivity(),
                            new String[]{android.Manifest.permission.RECORD_AUDIO}, 1
                    );


                } else {
                    VoiceFile = configFileName("P", ".3gp");

                    Intent recordIntent = new Intent(mContext, IssueVoiceRecord.class);

                    recordIntent.putExtra("fileName", VoiceFile.getAbsolutePath());

                    getOwnerActivity().startActivityForResult(recordIntent, REQUEST_Voice_CAPTURE);

                }

            }
        });



    }

    private File configFileName(String prefix, String extension) {

        String fileName = FileUtil.getUniqueFileName();

        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);

        return new File(dir, prefix + fileName + extension);

    }

    private void GoCamera() {

        int permission = ActivityCompat.checkSelfPermission(mContext, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // 無權限，向使用者請求
            ActivityCompat.requestPermissions(
                    getOwnerActivity(),
                    new String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE}, REQUEST_EXTERNAL_STORAGE
            );


        } else {

            permission = ActivityCompat.checkSelfPermission(mContext, android.Manifest.permission.CAMERA);

            if (permission != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(
                        getOwnerActivity(),
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
                getOwnerActivity().startActivityForResult(intentCamera, REQUEST_IMAGE_CAPTURE);
            }
        }


    }

    public void SetOnDialog_Finish_Listener(Dialog_Finish_Listener eventListener) {
        mDialog_Finish_Listener = eventListener;
    }


    public String GetReason() {
        EditText txt_Reason = (EditText) findViewById(R.id.txt_Reason);

        return txt_Reason.getText().toString();
    }

    @Override
    public void onClick(View v) {
        dismiss();
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

    @Override
    public void onStop() {
        filterText.removeTextChangedListener(filterTextWatcher);
    }

    private void dispatchTakeVideoIntent() {


        int permission = ActivityCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // 無權限，向使用者請求
            ActivityCompat.requestPermissions(
                    getOwnerActivity(),
                    new String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE}, REQUEST_EXTERNAL_STORAGE
            );
        } else {

            permission = ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA);

            if (permission != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(
                        getOwnerActivity(),
                        new String[]{Manifest.permission.CAMERA}, REQUEST_IMAGE_CAPTURE
                );

            } else {

                Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);

                if (takeVideoIntent.resolveActivity(mContext.getPackageManager()) != null) {

                    File _VideoFile = VideoFileName("P", ".mp4");

                    VideoFile = _VideoFile;

                    Uri uri = Uri.fromFile(_VideoFile);
                    // 設定檔案名稱
                    takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);

                    getOwnerActivity().startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
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