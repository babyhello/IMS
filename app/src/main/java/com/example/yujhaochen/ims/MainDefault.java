package com.example.yujhaochen.ims;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

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
import java.io.OutputStreamWriter;

public class MainDefault extends Activity {
    static final int REQUEST_VIDEO_CAPTURE = 1;

    static final int REQUEST_IMAGE_CAPTURE = 2;
    static final String ACTION_SCAN = "com.google.zxing.client.android.SCAN";
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;
    private ProgressDialog pDialog;
    private static final String WRITE_EXTERNAL_STORAGE = "android.permission.WRITE_EXTERNAL_STORAGE";
    private static final String READ_EXTERNAL_STORAGE = "android.permission.READ_EXTERNAL_STORAGE";
    private static final int REQUEST_EXTERNAL_STORAGE = 1;


    ;
    static final int PICK_FROM_GALLERY = 5;
    private File ImageFile;

    private File VideoFile;

    static boolean QR_Code_Checked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_default);

        ImageView IMG_Camera = (ImageView) findViewById(R.id.IMG_Camera);

        IMG_Camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

//                GoCamera();

                Intent CameraIntent = new Intent(MainDefault.this,VideoRecord.class);

                Bundle bundle = new Bundle();

                bundle.putString("FilePath", configFileName("P", ".jpg").getAbsolutePath());

                CameraIntent.putExtras(bundle);

                startActivityForResult(CameraIntent, REQUEST_IMAGE_CAPTURE);

            }
        });

        ImageView IMG_Photo = (ImageView) findViewById(R.id.IMG_Photo);

        IMG_Photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                Intent i = new Intent(Intent.ACTION_PICK, null);
                i.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(i, PICK_FROM_GALLERY);

            }
        });

        ImageView IMG_Search = (ImageView) findViewById(R.id.IMG_Search);

        IMG_Search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                UserDB UserDB = new UserDB(MainDefault.this);

                //如果進來程式有資料的話就不用再登入
                if (UserDB.getCount() > 0) {

                    UserData UserData = new UserData();

                    UserData = UserDB.getAll().get(0);

                    Intent intent = new Intent(MainDefault.this, MainTab.class);

                    startActivity(intent);
                }

            }
        });


        ImageView IMG_QR_Code = (ImageView) findViewById(R.id.IMG_QR_Code);

        IMG_QR_Code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                QR_Code_Scan_Start();


            }
        });
    }

    private void Go_To_New_Issue(String JsonString) {
        try {
            JSONObject QR_Object = new JSONObject(JsonString);

            JSONArray ProjectArray = new JSONArray(QR_Object.getString("Key"));

            if (ProjectArray.length() > 0) {


                JSONObject ProjectData = ProjectArray.getJSONObject(0);

                String Model_ID = String.valueOf(ProjectData.getInt("ModelID"));

                String ModelName = ProjectData.getString("ModelName");

                String Subject = ProjectData.getString("Subject");

                Bundle bundle = new Bundle();

                bundle.putString("ModelID", Model_ID);

                bundle.putString("ModelName", ModelName);

                bundle.putString("Subject", Subject);

                Intent intent = new Intent(MainDefault.this, NewIssue.class);

                intent.putExtras(bundle);

                startActivity(intent);
            }

        } catch (JSONException ex) {

        }


    }

    private void Search_Project(String QR_Content) {

        if (QR_Content.contains("Subject")) {
            Go_To_New_Issue(QR_Content);
        } else {
            RequestQueue mQueue = Volley.newRequestQueue(this);

            String Path = GetServiceData.ServicePath + "/Find_Project_List_Search?ModelName=" + QR_Content;

            GetServiceData.getString(Path, mQueue, new GetServiceData.VolleyCallback() {
                @Override
                public void onSuccess(JSONObject result) {

                    Go_To_Project(result);
                }
            });
        }
    }

    private void Go_To_Project(JSONObject ModelID) {
        try {
            JSONArray ProjectArray = new JSONArray(ModelID.getString("Key"));

            if (ProjectArray.length() > 0) {

                JSONObject ProjectData = ProjectArray.getJSONObject(0);

                String Model_ID = String.valueOf(ProjectData.getInt("ModelID"));

                String ModelName = ProjectData.getString("ModelName");

                Bundle bundle = new Bundle();

                bundle.putString("ModelID", Model_ID);

                bundle.putString("ModelName", ModelName);

                Intent intent = new Intent(MainDefault.this, IssueList.class);

                intent.putExtras(bundle);

                startActivity(intent);
            } else {
                AlertDialog.Builder MyAlertDialog = new AlertDialog.Builder(this);

                MyAlertDialog.setMessage("Project Was Not Found!!!");

                MyAlertDialog.show();
            }

        } catch (JSONException ex) {

        }


//        Bundle bundle = new Bundle();
//
//        bundle.putString("ModelID", scanContent);
//
//        bundle.putString("ModelName", Project_Item.GetName());
//
//        Intent intent = new Intent();
//
//        intent.putExtras(bundle);
//
//        startActivity(intent);
    }

    private void QR_Code_Scan_Start() {

        try {

            QR_Code_Checked = true;

            IntentIntegrator scanIntegrator = new IntentIntegrator(this);

            scanIntegrator.setOrientationLocked(false);

            scanIntegrator.initiateScan();

        } catch (ActivityNotFoundException anfe) {

        }
    }

    private File configFileName(String prefix, String extension) {

        String fileName = FileUtil.getUniqueFileName();

        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);

        return new File(dir, prefix + fileName + extension);

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

                Uri uri = Uri.fromFile(pictureFile);
                // 設定檔案名稱
                intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                // 啟動相機元件
                startActivityForResult(intentCamera, REQUEST_IMAGE_CAPTURE);
            }
        }


    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {


        Log.w("resultCode",String.valueOf(resultCode));
        Log.w("requestCode",String.valueOf(requestCode));
        Log.w("RESULT_OK",String.valueOf(RESULT_OK));

        if (resultCode == RESULT_OK && QR_Code_Checked == true) {

            IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

            if (scanningResult != null) {
                String scanContent = scanningResult.getContents();
                String scanFormat = scanningResult.getFormatName();


                Search_Project(scanContent);


                QR_Code_Checked = false;
            }
        } else if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK) {


            if (VideoFile.exists()) {

                //AddVideoItem(VideoFile.getAbsolutePath());

//                VideoView Vdo_Issue_File = (VideoView)findViewById(R.id.videoView);
//
//                Vdo_Issue_File.setVideoURI(Uri.parse(VideoFile.getAbsolutePath()));

                // System.out.println("Video Path" + NewIssueFile_List.get(position).GetVideoPath());
                VideoFile = null;
            }

        }
        // 如果照片檔案存在
        else if (resultCode == REQUEST_IMAGE_CAPTURE) {


//            if (ImageFile.exists()) {
//
//            }

            Bundle bundleAccount = data.getExtras();



            if(!TextUtils.isEmpty(bundleAccount.getString("FilePath")))
            {
                Log.w("FilePath",bundleAccount.getString("FilePath"));

                Bitmap photo = FileUtil.FilePathGetBitMap(bundleAccount.getString("FilePath"));

                Bitmap minibm = ThumbnailUtils.extractThumbnail(photo, 1024, 800);

                SpeedNewIssue.ImgBmp = photo;

                SpeedNewIssue.ImagePath = bundleAccount.getString("FilePath");

                Intent intent = new Intent(MainDefault.this, SpeedNewIssue.class);

                startActivity(intent);
            }

        } else {
            if (requestCode == PICK_FROM_GALLERY && resultCode == RESULT_OK) {
//取得圖檔的路徑位置
                Uri uri = data.getData();
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
                        SpeedNewIssue.ImgBmp = bitmap;

                        SpeedNewIssue.ImagePath = MyNewFile.getAbsolutePath();


                        Intent intent = new Intent(MainDefault.this, SpeedNewIssue.class);

                        startActivity(intent);
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

//                    try {
//
//                        //copy(myFile,MyNewFile);
//
//
//
//                    } catch (IOException e) {
//                        Log.e("Exception", "File write failed: " + e.toString());
//                    }


                } catch (FileNotFoundException e) {
                    Log.e("Exception", e.getMessage(), e);
                }

            } else if (resultCode == RESULT_OK) {

                IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

                if (scanningResult != null) {
                    String scanContent = scanningResult.getContents();
                    String scanFormat = scanningResult.getFormatName();

                } else {


                }


            }
        }

    }

    public void copy(File src, File dst) throws IOException {
        InputStream in = new FileInputStream(src);
        OutputStream out = new FileOutputStream(dst);

        // Transfer bytes from in to out
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();
    }

}
