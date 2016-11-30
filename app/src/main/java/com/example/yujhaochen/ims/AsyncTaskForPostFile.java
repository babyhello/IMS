package com.example.yujhaochen.ims;


import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

public class AsyncTaskForPostFile extends AsyncTask<String, Void, Void>{
    private String TAG = "AsyncTaskForPostFile";
    private Context mContext;
    private final String sp = "7dd19a1a5a0d2c";
    private final String end = "\r\n";
    private final String twoHyphens = "--";
    private final String boundary = "---------------------------";
    private final String post_url = GetServiceData.ServicePath + "/Upload_Issue_File_MultiPart";
    private final String input_name = "myfile";

    public AsyncTaskForPostFile(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    protected Void doInBackground(String... params) {
        String file_path = params[0];
        File MediaFile = new File(file_path);
        if(!MediaFile.exists()){
            return null;
        }

        try {
            URL url = new URL(post_url);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setUseCaches(false);
            httpURLConnection.setChunkedStreamingMode(0);	//針對大檔傳輸的設定
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setRequestProperty("Connection", "Keep-Alive");
            httpURLConnection.setRequestProperty("Charset", "UTF-8");
            httpURLConnection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary + sp);

            int bufferSize = 102400;
            FileInputStream fileInputStream = new FileInputStream(file_path);
            BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
            DataOutputStream dataOutputStream = new DataOutputStream(httpURLConnection.getOutputStream());
            dataOutputStream.writeBytes(boundary + twoHyphens + sp + end);
            dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"" + input_name + "\"; filename=\"" + MediaFile.getName() + "\"" + end);
            dataOutputStream.writeBytes(end);
            byte[] buffer = new byte[bufferSize];
            int count = 0;
            while((count = bufferedInputStream.read(buffer)) != -1)
            {
                dataOutputStream.write(buffer, 0 , count);
                dataOutputStream.writeBytes(end);
            }
            bufferedInputStream.close();
            fileInputStream.close();
            dataOutputStream.write((boundary + twoHyphens + sp + twoHyphens + end).getBytes());
            dataOutputStream.flush();

            InputStream inputStream = httpURLConnection.getInputStream();
            inputStream.close();
            dataOutputStream.close();

        } catch (Exception ex) {
            Log.d(TAG, ex.getMessage());
        }

        return null;
    }

//    @Override
//    protected void onPostExecute(Void result) {
//        Intent intent = new Intent("PostFileComplete");
//        this.mContext.sendBroadcast(intent);
//        super.onPostExecute(result);
//    }

}