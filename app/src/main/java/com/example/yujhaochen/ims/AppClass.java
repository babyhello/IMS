package com.example.yujhaochen.ims;

import android.accessibilityservice.GestureDescription;
import android.animation.Animator;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.ExifInterface;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by yujhaochen on 2016/10/25.
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

        return Date.replace("T"," ");
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
                PriorityImage = R.mipmap.projectlist_ic_issue_priop1;

                return PriorityImage;

            case "2":
                PriorityImage = R.mipmap.projectlist_ic_issue_priop2;

                return PriorityImage;

            case "3":
                PriorityImage = R.mipmap.projectlist_ic_issue_priop3;

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

}
