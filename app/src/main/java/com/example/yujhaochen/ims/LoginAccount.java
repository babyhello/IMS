package com.example.yujhaochen.ims;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class LoginAccount extends Activity {

    private File ImageFile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_account);


        Button button = (Button) findViewById(R.id.Btn_Login_Account);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {



              GoToPassWord();

            }
        });


        EditText txt_Login_Account = (EditText) findViewById(R.id.txt_Login_Account);

        txt_Login_Account.setOnKeyListener(new View.OnKeyListener() {

            public boolean onKey(View v, int keyCode, KeyEvent event) {

                System.out.println(keyCode);
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER))
                {
                    GoToPassWord();
                    return true;
                }
                return false;
            }
        });

    }

    private void GoToMainTab()
    {

    }

    private void TestUpload()
    {


    }

    private final BroadcastReceiver AsyncTaskForPostFileReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //顯示上傳結束
            System.out.println(context);
        }
    };

    public String saveBitmap(Bitmap bmp) {

        //String Path = "";
        String _time = "";
        Calendar cal = Calendar.getInstance();
        int millisecond = cal.get(Calendar.MILLISECOND);
        int second = cal.get(Calendar.SECOND);
        int minute = cal.get(Calendar.MINUTE);
        int hourofday = cal.get(Calendar.HOUR_OF_DAY);
        _time = "image_" + hourofday + "" + minute + "" + second + ""
                + millisecond + ".png";
        String file_path = Environment.getExternalStorageDirectory()
                .getAbsolutePath();
        try {
            File dir = new File(file_path);
            if (!dir.exists())
                dir.mkdirs();
            File file = new File(dir, _time);
            FileOutputStream fOut = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, fOut);
            fOut.flush();
            fOut.close();
            Toast.makeText(getApplicationContext(),
                    "Image has been saved in KidsPainting folder",
                    Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Log.e("error in saving image", e.getMessage());
        }

        return file_path;
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }

    private File configFileName(String prefix, String extension) {
        String fileName = FileUtil.getUniqueFileName();

        return new File(FileUtil.getExternalStorageDir(FileUtil.APP_DIR),
                prefix + fileName + extension);
    }


    private void GoCamera() {
        Intent intentCamera =
                new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // 照片檔案名稱
        File pictureFile = configFileName("P", ".jpg");

        ImageFile = pictureFile;
        Uri uri = Uri.fromFile(pictureFile);
        // 設定檔案名稱
        intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        // 啟動相機元件
        startActivityForResult(intentCamera, 0);
    }




    String mCurrentPhotoPath;

    private String createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return mCurrentPhotoPath;
    }

    private void GoToPassWord() {

        //DBHelper a = new DBHelper(this);

        UserDB UserDB = new UserDB(this);

        //如果進來程式有資料的話就不用再登入
        if (UserDB.getCount() > 0)
        {
            System.out.println(UserDB.getCount());

            UserData UserData = new UserData();

            UserData = UserDB.getAll().get(0);

            System.out.println("fuck"+UserData.WorkID);

            Intent intent = new Intent(this, MainTab.class);

            startActivity(intent);
        }
        else
        {
            EditText txt_Login_Account = (EditText) findViewById(R.id.txt_Login_Account);

            String Account = txt_Login_Account.getText().toString();

            System.out.println("AccountErr" + Account);
            if (!Account.matches("")) {

                Bundle bundle = new Bundle();

                bundle.putString("Account", Account);

                Intent intent = new Intent(this, LoginPassword.class);

                intent.putExtras(bundle);

                startActivity(intent);
            } else

            {
                AppClass.AlertMessage("Please Key In Outlook ID", this);

            }
        }



    }

}
