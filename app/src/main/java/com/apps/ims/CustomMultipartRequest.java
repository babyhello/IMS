package com.apps.ims;


import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.webkit.MimeTypeMap;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;

import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;

/*
Ver : 1.0
Last Updated : 27-Nov-2015
This class handles the multipart api calls
 */

public class CustomMultipartRequest extends Request<JSONObject> {


    private final Response.Listener<JSONObject> mListener;
    private final Map<String, File> mFilePartData;
    private final Map<String, String> mStringPart;
    private final Map<String, String> mHeaderPart;

    private MultipartEntityBuilder mEntityBuilder = MultipartEntityBuilder.create();
    private HttpEntity mHttpEntity;
    private Context mContext;


    public CustomMultipartRequest(int method, Context mContext, String url, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener, Map<String, File> mFilePartData, Map<String, String> mStringPart, Map<String, String> mHeaderPart) {
        super(method, url, errorListener);
        mListener = listener;
        this.mFilePartData = mFilePartData;
        this.mStringPart = mStringPart;
        this.mHeaderPart = mHeaderPart;
        this.mContext = mContext;
        mEntityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        buildMultipartFileEntity();
        //buildMultipartTextEntity();
        mHttpEntity = mEntityBuilder.build();
    }

    public static String getMimeType(Context context, String url) {
        Uri uri = Uri.fromFile(new File(url));
        String mimeType = null;
        if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            ContentResolver cr = context.getContentResolver();
            mimeType = cr.getType(uri);
        } else {
            String fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri
                    .toString());
            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                    fileExtension.toLowerCase());
        }
        System.out.println(mimeType);

        return mimeType;


    }

    private void buildMultipartFileEntity() {
        for (Map.Entry<String, File> entry : mFilePartData.entrySet()) {
            try {

                String key = entry.getKey();
                File file = entry.getValue();
                String mimeType = getMimeType(mContext, file.toString());
                System.out.println(mimeType);
                mEntityBuilder.addBinaryBody(key, file, ContentType.create(mimeType), file.getName());

                System.out.println(key);
                System.out.println(file);
                System.out.println(ContentType.create(mimeType));
                System.out.println(file.getName());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void buildMultipartTextEntity() {

        for (Map.Entry<String, String> entry : mStringPart.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (key != null && value != null)
                mEntityBuilder.addTextBody(key, value);
        }
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return mHeaderPart;
    }


    @Override
    public String getBodyContentType() {
        return mHttpEntity.getContentType().getValue();
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            mHttpEntity.writeTo(bos);
        } catch (IOException e) {
            VolleyLog.e("IOException writing to ByteArrayOutputStream");
        }
        return bos.toByteArray();
    }

    @Override
    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
        try {
            String jsonString = new String(response.data,
                    HttpHeaderParser.parseCharset(response.headers));
            return Response.success(new JSONObject(jsonString),
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JSONException je) {
            return Response.error(new ParseError(je));
        }
    }

    @Override
    protected void deliverResponse(JSONObject response) {
        mListener.onResponse(response);
    }

}