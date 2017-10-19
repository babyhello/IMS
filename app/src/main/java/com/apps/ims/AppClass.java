package com.apps.ims;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Environment;
import android.text.Html;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by androids on 2016/10/25.
 */
public class AppClass {

    private static Toast toast;

    /**
     * 從給定的路徑載入圖片，並指定是否自動旋轉方向
     */
    public static Bitmap loadBitmap(String _imgpath) {
        Bitmap bm = BitmapFactory.decodeFile(_imgpath);
//        int digree = 0;
//        ExifInterface exif = null;
//        try {
//            exif = new ExifInterface(_imgpath);
//        } catch (IOException e) {
//            e.printStackTrace();
//            exif = null;
//        }
//        if (exif != null) {
//            //讀取圖片中相機方向資訊
//            int ori = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
//                    ExifInterface.ORIENTATION_UNDEFINED);
//            //計算旋轉角度
//            switch (ori) {
//                case ExifInterface.ORIENTATION_ROTATE_90:
//                    digree = 90;
//                    break;
//                case ExifInterface.ORIENTATION_ROTATE_180:
//                    digree = 180;
//                    break;
//                case ExifInterface.ORIENTATION_ROTATE_270:
//                    digree = 270;
//                    break;
//                default:
//                    digree = 0;
//                    break;
//            }
//        }
//        if (digree != 0) {
//            //旋轉圖片
//            Matrix m = new Matrix();
//            m.postRotate(digree);
//            bm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(),
//                    bm.getHeight(), m, true);
//        }


//        BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inJustDecodeBounds = true;
//// 獲取這個圖片的寬和高
//        Bitmap bitmap = bm; //此時返回bm為空
//        options.inJustDecodeBounds = false;
////計算縮放比
//        int be = (int) (options.outHeight / (float) 200);
//        if (be <= 0)
//            be = 1;
//        options.inSampleSize = be;
////重新讀入圖片，注意這次要把options.inJustDecodeBounds 設為 false哦
//        bitmap = bm;
//        int w = bitmap.getWidth();
//        int h = bitmap.getHeight();

        return bm;
    }

    public static void AlertMessage(String AlertMessage, Context Context) {
        AlertDialog.Builder MyAlertDialog = new AlertDialog.Builder(Context);

        MyAlertDialog.setMessage(AlertMessage);

        MyAlertDialog.show();
    }

    public static String stripHtml(String html) {
        return Html.fromHtml(html).toString();
    }

    public static String ConvertDateString(String Date) {


        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = format.parse(Date);

            format = new SimpleDateFormat("yyyy-MM-dd");

            Date = format.format(date.getTime());

        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return Date;
    }

    public static String ConvertLongDateString(String Date) {


        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        try {
            Date date = format.parse(Date);

            format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

            Date = format.format(date.getTime());

        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return Date.replace("T", " ");
    }

    public static void makeTextAndShow(final Context context, final String text, final int duration) {
        if (toast == null) {
            //如果還沒有用過makeText方法，才使用
            toast = android.widget.Toast.makeText(context, text, duration);
        } else {
            toast.setText(text);
            toast.setDuration(duration);
        }
        toast.show();
    }

    public static int PriorityImage(String PriorityString) {
        int PriorityImage = 0;

        switch (PriorityString) {
            case "1":
                PriorityImage = com.apps.ims.R.mipmap.projectlist_ic_issue_priop1;

                return PriorityImage;

            case "2":
                PriorityImage = com.apps.ims.R.mipmap.projectlist_ic_issue_priop2;

                return PriorityImage;

            case "3":
                PriorityImage = com.apps.ims.R.mipmap.projectlist_ic_issue_priop3;

                return PriorityImage;

        }

        return PriorityImage;
    }


    public static Bitmap roundCornerImage(Bitmap raw, float round) {
        int width = raw.getWidth();
        int height = raw.getHeight();
        Bitmap result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);
        canvas.drawARGB(0, 0, 0, 0);

        final Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.parseColor("#000000"));

        final Rect rect = new Rect(0, 0, width, height);
        final RectF rectF = new RectF(rect);

        canvas.drawRoundRect(rectF, round, round, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(raw, rect, rect, paint);

        return result;
    }

    public static void Send_Notification(String WorkID, String Title, String Tag, String Key, String Value, String Message, Context mContext) {

        RequestQueue mQueue = Volley.newRequestQueue(mContext);

        Map<String, String> map = new HashMap<String, String>();
        map.put("WorkID", WorkID);
        map.put("Title", Title);
        map.put("Message", Message);
        map.put("Tag", Tag);
        map.put("Key", Key);
        map.put("Value", Value);
        String Path = GetServiceData.ServicePath + "/SendPushNotificationDeviceByWorkID";

        GetServiceData.SendPostRequest(Path, mQueue, new GetServiceData.VolleyStringCallback() {
            @Override
            public void onSendRequestSuccess(String result) {

            }

            @Override
            public void onSendRequestError(String result) {

            }
        }, map);
    }

    public static void Send_Notification(List<String> WorkID_List, String Title, String Message, String Tag, String Key, String Value, final Context mContext) {

        RequestQueue mQueue = Volley.newRequestQueue(mContext);

        for (String WorkID : WorkID_List) {

            Map<String, String> map = new HashMap<String, String>();
            map.put("WorkID", WorkID);
            map.put("Title", Title);
            map.put("Message", Message);
            map.put("Tag", Tag);
            map.put("Key", Key);
            map.put("Value", Value);
            String Path = GetServiceData.ServicePath + "/SendPushNotificationDeviceByWorkID";

            GetServiceData.SendPostRequest(Path, mQueue, new GetServiceData.VolleyStringCallback() {
                @Override
                public void onSendRequestSuccess(String result) {


                }

                @Override
                public void onSendRequestError(String result) {

                }
            }, map);
        }


    }

    private static String SaveImageData(Bitmap finalBitmap) {

        String FullPath = "";
        String root = Environment.getExternalStorageDirectory().getAbsolutePath();
        File myDir = new File(root + "/saved_images");

        myDir.mkdirs();

        String fname = java.util.UUID.randomUUID().toString() +".jpg";
        FullPath = myDir.getPath() + "/" + fname;

        File file = new File (myDir, fname);
        if (file.exists ()) file.delete ();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);

            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return FullPath;
    }

    private static void SaveImage(final PhotoData PhotoDataItem,final LaunchPhoto LaunchPhoto,final Boolean UpdateType,Context mContext) {
        RequestQueue mQueue = Volley.newRequestQueue(mContext);


        // Retrieves an image specified by the URL, displays it in the UI.
        ImageRequest request = new ImageRequest(GetServiceData.ServicePath + "/Get_File?FileName=" + PhotoDataItem.photo_downloadPath,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap bitmap) {
                        //mImageView.setImageBitmap(bitmap);

                        String SaveImage = SaveImageData(bitmap);

                        PhotoData.photo_path = SaveImage;

                        if (UpdateType)
                        {
                            LaunchPhoto.update(PhotoDataItem);
                        }
                        else
                        {
                            LaunchPhoto.insert(PhotoDataItem);
                        }


                    }
                }, 0, 0, null,
                new Response.ErrorListener() {
                    public void onErrorResponse(VolleyError error) {
                        // mImageView.setImageResource(R.drawable.image_load_error);
                    }
                });
                // Access the RequestQueue through your singleton class.
        mQueue.add(request);

    }

    public static void Get_Server_All_Image(final Context mContext) {

        RequestQueue mQueue = Volley.newRequestQueue(mContext);

        Map<String, String> map = new HashMap<String, String>();

        String Path = GetServiceData.ServicePath + "/Get_Server_All_Image";

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

        String now = formatter.format(new Date());

        final LaunchPhoto LaunchPhoto = new LaunchPhoto(mContext);

        final List<PhotoData> WebPhotoList = new ArrayList<PhotoData>();

        final List<PhotoData> LocalPhotoList = LaunchPhoto.getAll(now);

        GetServiceData.SendPostRequest(Path, mQueue, new GetServiceData.VolleyStringCallback() {
            @Override
            public void onSendRequestSuccess(String result) {

                try {

                    JSONObject obj = new JSONObject(result);

                    JSONArray ImageArray = new JSONArray(obj.getString("Key"));

                    for (int i = 0; i < ImageArray.length(); i++) {

                        JSONObject ImageData = ImageArray.getJSONObject(i);

                        String Update = ImageData.getString("Update");

                        String Downdate = ImageData.getString("Downdate");

                        String F_AppDownloadFilePath = ImageData.getString("F_AppDownloadFilePath");

                        String Item = String.valueOf(ImageData.getInt("Item"));

                        WebPhotoList.add(new PhotoData(" ", Downdate.replace("T",""), Update.replace("T",""), "", "", F_AppDownloadFilePath, Item));

                    }

                } catch (JSONException ex) {

                }

                boolean CheckAdd = false;

                for (PhotoData PhotoDataItem:WebPhotoList) {
                    CheckAdd = false;

                    for (PhotoData LocalItem:LocalPhotoList) {

                        if (PhotoDataItem.photo_item == LocalItem.photo_downloadPath)
                        {
                            CheckAdd = true;

                            if (PhotoDataItem.photo_downloadPath != LocalItem.photo_downloadPath)
                            {
                                LocalItem.photo_downloadPath = PhotoDataItem.photo_downloadPath;

                                SaveImage(LocalItem,LaunchPhoto,true,mContext);
                            }
                        }
                    }

                    if (!CheckAdd)
                    {
                        SaveImage(PhotoDataItem,LaunchPhoto,true,mContext);
                    }

                }

               // PhotoData PhotoDataItem =  new PhotoData(" ", Downdate, Update, "", "", F_AppDownloadFilePath, Item);
            }

            @Override
            public void onSendRequestError(String result) {

            }
        }, map);



        Log.w("eeeeeeeeeeeeeeeeee",String.valueOf(WebPhotoList.size()));
        Log.w("eeeeeeeeeeeeeeeeee",String.valueOf(WebPhotoList.size()));
    }

}
