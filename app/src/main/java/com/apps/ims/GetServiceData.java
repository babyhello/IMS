package com.apps.ims;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

//import org.apache.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by androids on 2016/10/24.
 */
public class GetServiceData {

    //正式機
    public static String ServicePath = "http://wtsc.msi.com.tw/IMS/IMS_App_Service.asmx";

    //測試機
    //public static String ServicePath = "http://172.16.111.111:200/IMS_App_Service.asmx";

    public static String getUrlFromImgTag(String imgTag) {
        String url = null;

        Pattern p = Pattern.compile("src='[^']*'", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(imgTag);
        if (m.find()) {
            url = imgTag.substring(m.start() + 5, m.end() - 1);
        }

        return url;
    }

    public static void getString(String Url, RequestQueue mQueue, final VolleyCallback callback) {

        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, Url,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        callback.onSuccess(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        );

        int socketTimeout = 30000;

        getRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        mQueue.add(getRequest);
    }


    public static void SendPostRequest(String Url, RequestQueue mQueue, final VolleyStringCallback callback, final Map<String, String> map) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                callback.onSendRequestSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                callback.onSendRequestError(error.getMessage());

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
//                Map<String, String> map = new HashMap<String, String>();
//                map.put("params1", "value1");
//                map.put("params2", "value2");
                return map;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        mQueue.add(stringRequest);
    }


    public static void SendRequest(String Url, RequestQueue mQueue, final VolleyStringCallback callback) {
        StringRequest stringRequest = new StringRequest(Url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        callback.onSendRequestSuccess(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                callback.onSendRequestError(error.getMessage());

                Log.e("TAG", error.getMessage(), error);
            }
        });
        mQueue.add(stringRequest);
    }

    public static void getImage(String Url, RequestQueue mQueue, final ImageView Img) {
        ImageRequest request = new ImageRequest(Url,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap bitmap) {
                        Img.setImageBitmap(bitmap);
                    }
                }, 0, 0, null,
                new Response.ErrorListener() {
                    public void onErrorResponse(VolleyError error) {
                        // mImageViㄌew.setImageResource(R.drawable.image_load_error);
                    }
                });
    }

    public static String encodeURIComponent(String Url) {

        try {

            String ReplaceURL = Url.substring(Url.lastIndexOf('/') + 1, Url.length());


            String EncodeURL = "";

            if (ReplaceURL != "") {
                EncodeURL = URLEncoder.encode(ReplaceURL, "utf-8");
            }

            Url = Url.replace(ReplaceURL, EncodeURL);

        } catch (IOException e) {
            System.out.print(e);
        }

        return Url;
    }

    public static void GetImageByImageLoad(String Url, final ImageView Img) {

        if (!(Url.contains("http://") || Url.contains("https://"))) {
            Url = "http:" + Url;

        }

        try {

            String ReplaceURL = Url.substring(Url.lastIndexOf('/') + 1, Url.length());


            String EncodeURL = "";

            if (ReplaceURL != "") {
                EncodeURL = URLEncoder.encode(ReplaceURL, "utf-8");
            }

            Url = Url.replace(ReplaceURL, EncodeURL);

        } catch (IOException e) {
            System.out.print(e);
        }


        RequestQueue mQueue = AppController.getInstance().getRequestQueue();

        ImageLoader imageLoader = new ImageLoader(mQueue, new BitmapCache() {
            @Override
            public void putBitmap(String url, Bitmap bitmap) {

                Img.setImageBitmap(bitmap);

            }

            @Override
            public Bitmap getBitmap(String url) {
                return null;
            }
        });


        ImageLoader.ImageListener listener = ImageLoader.getImageListener(Img,
                com.apps.ims.R.mipmap.progress_image, com.apps.ims.R.mipmap.progress_image);

        imageLoader.get(Url, listener);
    }


    public static void GetImageByImageLoad(String Url, final ImageView Img, int DefaultImage, int ErrorImage) {


        Url = ServicePath + "/Get_File?FileName=" + Url;

        if (!(Url.contains("http://") || Url.contains("https://"))) {
            Url = "http:" + Url;

        }
        RequestQueue mQueue = AppController.getInstance().getRequestQueue();

        ImageLoader imageLoader = new ImageLoader(mQueue, new BitmapCache() {
            @Override
            public void putBitmap(String url, Bitmap bitmap) {

                Img.setImageBitmap(bitmap);

            }

            @Override
            public Bitmap getBitmap(String url) {
                return null;
            }
        });


        ImageLoader.ImageListener listener = ImageLoader.getImageListener(Img,
                DefaultImage, ErrorImage);

        imageLoader.get(Url, listener);
    }

    public static void GetUserPhoto(Context mcontext, String WorkID, final ImageView Img) {

        Glide.with(mcontext)
                .load(GetServiceData.ServicePath + "/Get_File?FileName=" + "//172.16.111.114/File/SDQA/Code/Admin/" + WorkID + ".jpg")
                .centerCrop()
                .placeholder(com.apps.ims.R.mipmap.avatar_man)
                .crossFade()
                .into(Img);

    }

    public interface VolleyCallback {

        void onSuccess(JSONObject result);

    }

    public interface VolleyImageCallback {

        void onSuccess(Bitmap ImgBitMap);
    }

    public interface VolleyStringCallback {

        void onSendRequestSuccess(String response);

        void onSendRequestError(String response);
    }

    public static void uploadImage(String UPLOAD_URL, RequestQueue mQueue, File file, String stringPart) {

        MultipartRequest MultiPart = new MultipartRequest(UPLOAD_URL,
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }


                },
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                    }
                }, file, "");


        mQueue.add(MultiPart);
    }

    public static void UploadImageVolley(String UPLOAD_URL, File file, RequestQueue mQueue, Context mContext) {
        //Auth header
        Map<String, String> mHeaderPart = new HashMap<>();
        mHeaderPart.put("Content-Type", "multipart/form-data;");
        //mHeaderPart.put("access_token", accessToken);

//File part
        Map<String, File> mFilePartData = new HashMap<>();

        mFilePartData.put("Files", file);
        //mFilePartData.put("file", new File(mFilePath));

//String part
        Map<String, String> mStringPart = new HashMap<>();
//        mStringPart.put("profile_id","1");
//        mStringPart.put("imageType", "ProfileImage");

        CustomMultipartRequest mCustomRequest = new CustomMultipartRequest(Request.Method.POST, mContext, UPLOAD_URL, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                //listener.onResponse(jsonObject);
                //System.out.println(jsonObject);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                //listener.onErrorResponse(volleyError);
                System.out.println(volleyError);
            }
        }, mFilePartData, mStringPart, mHeaderPart);

        mQueue.add(mCustomRequest);
    }


    public static String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    public static void UploadFile(String FilePath, String Uploadurl) {
        try {
            // Set your file path here
            FileInputStream fstrm = new FileInputStream(FilePath);
            //System.out.println(FilePath);
            // Set your server page url (and the file title/description)
            HttpFileUpload hfu = new HttpFileUpload(Uploadurl, "Test", "Test");

            hfu.Send_Now(fstrm);

        } catch (FileNotFoundException e) {
            System.out.println(e);
        }
    }

    public static void uploadImageByte(String UPLOAD_URL, RequestQueue mQueue, final Bitmap bitmap, final String FileName) {
        //Showing the progress dialog
        //final ProgressDialog loading = ProgressDialog.show(,"Uploading...","Please wait...",false,false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, UPLOAD_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        //System.out.println(s);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {

                        } else if (error instanceof AuthFailureError) {
                            System.out.println("1");
                        } else if (error instanceof ServerError) {
                            System.out.println("2");
                        } else if (error instanceof NetworkError) {
                            System.out.println("3");
                        } else if (error instanceof ParseError) {
                            System.out.println("4");
                        }
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //Converting Bitmap to String
                String image = getStringImage(bitmap);
                //System.out.println(image);
                //Getting Image Name
                String name = FileName;

                //Creating parameters
                Map<String, String> params = new Hashtable<String, String>();

                //Adding parameters
                params.put("Data", image);
                params.put("FileName", name);

                //returning parameters
                return params;
            }
        };

        mQueue.add(stringRequest);
    }


    /**
     * 獲取軟件版本號
     *
     * @param context
     * @return
     */
    private static int getVersionCode(Context context) {
        int versionCode = 0;
        try {
            // 獲取軟件版本號，對應AndroidManifest.xml下android:versionCode
            versionCode = context.getPackageManager().getPackageInfo("com.apps.ims", 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    public static void isUpdate(final Context context) {

        RequestQueue mQueue = Volley.newRequestQueue(context);

        String Path = GetServiceData.ServicePath + "/Get_Android_Version";

        GetServiceData.getString(Path, mQueue, new GetServiceData.VolleyCallback() {
            @Override
            public void onSuccess(JSONObject result) {

                try {

                    JSONArray UserArray = new JSONArray(result.getString("Key"));

                    if (UserArray.length() > 0) {

                        UpdateManager mUpdateManager;

                        int versionCode = getVersionCode(context);

                        JSONObject IssueData = UserArray.getJSONObject(0);

                        String Version = String.valueOf(IssueData.getInt("Version"));

                        String Path = String.valueOf(IssueData.getInt("Path"));

                        if (Version != String.valueOf(versionCode)) {
                            mUpdateManager = new UpdateManager(context, Path);

                            mUpdateManager.checkUpdateInfo();
                        }

                    }
                } catch (JSONException ex) {

                }

            }
        });

        //return VersionCheck;
    }
}









