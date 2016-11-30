package com.example.yujhaochen.ims;

import android.graphics.Bitmap;
import android.net.Uri;
import android.webkit.MimeTypeMap;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class PostUploadRequest extends Request<String> {

    /**
     * 正确数据的时候回掉用
     */
    private final Response.Listener<String> mListener;
    /*请求 数据通过参数的形式传入*/
    private List<UploadImage> FileList ;

    private String BOUNDARY = "--------------520-13-14"; //数据分隔线
    private String MULTIPART_FORM_DATA = "multipart/form-data";


    public PostUploadRequest(String url, List<UploadImage> FileList, Response.Listener<String> listener,Response.ErrorListener errorListener) {

        super(Method.POST, url, errorListener);

        this.mListener = listener ;
        setShouldCache(false);
        this.FileList = FileList;
        //设置请求的响应事件，因为文件上传需要较长的时间，所以在这里加大了，设为5秒
        setRetryPolicy(new DefaultRetryPolicy(50000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    /**
     * 这里开始解析数据
     * @param response Response from the network
     * @return
     */


    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response)
    {
        return Response.success("Uploaded", getCacheEntry());
    }

    @Override
    protected void deliverResponse(String response)
    {
        mListener.onResponse(response);
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        ByteArrayOutputStream bos = new ByteArrayOutputStream() ;

        for (UploadImage File : FileList) {
            StringBuffer sb= new StringBuffer() ;
            /*第一行*/
            //`"--" + BOUNDARY + "\r\n"`
            sb.append("--"+BOUNDARY);
            sb.append("\r\n") ;
            /*第二行*/
            //Content-Disposition: form-data; name="参数的名称"; filename="上传的文件名" + "\r\n"
            sb.append("Content-Disposition: form-data;");
            sb.append(" name=\"");
            sb.append(File.getName()) ;
            sb.append("\"") ;
            sb.append("; filename=\"") ;
            sb.append(File.getFileName()) ;
            sb.append("\"");
            sb.append("\r\n") ;
            /*第三行*/
            //Content-Type: 文件的 mime 类型 + "\r\n"
            sb.append("Content-Type: ");
            sb.append(File.getMime()) ;
            sb.append("\r\n") ;
            /*第四行*/
            //"\r\n"
            sb.append("\r\n") ;
            try {
                bos.write(sb.toString().getBytes("utf-8"));
                /*第五行*/
                //文件的二进制数据 + "\r\n"
                bos.write(File.getValue());
                bos.write("\r\n".getBytes("utf-8"));
            } catch (IOException e) {
                e.printStackTrace();
            }


        }

        /*结尾行*/
        //`"--" + BOUNDARY + "--" + "\r\n"`
        String endLine = "--" + BOUNDARY + "--" + "\r\n" ;
        try {
            bos.write(endLine.toString().getBytes("utf-8"));


        } catch (IOException e) {
            e.printStackTrace();
        }
//
        return bos.toByteArray();
    }
    //Content-Type: multipart/form-data; boundary=----------8888888888888
    @Override
    public String getBodyContentType() {
        return MULTIPART_FORM_DATA+"; boundary="+BOUNDARY;
    }
//
//    public class FormImage {
//        //参数的名称
//        private String mName ;
//        //文件名
//        private String mFileName ;
//        //文件的 mime，需要根据文档查询
//        private String mMime ;
//        //需要上传的图片资源，因为这里测试为了方便起见，直接把 bigmap 传进来，真正在项目中一般不会这般做，而是把图片的路径传过来，在这里对图片进行二进制转换
//        private Bitmap mBitmap ;
//
//        public FormImage(Bitmap mBitmap) {
//            this.mBitmap = mBitmap;
//        }
//
//        public String getName() {
////        return mName;
////测试，把参数名称写死
//            return "uploadimg" ;
//        }
//
//        public String getFileName() {
//            //测试，直接写死文件的名字
//            return "test.png";
//        }
//        //对图片进行二进制转换
//        public byte[] getValue() {
//
//            Uri uri = Uri.parse(ImagePath);
//            ByteArrayOutputStream baos = new ByteArrayOutputStream();
//            FileInputStream fis;
//            try {
//                fis = new FileInputStream(new File(data.getPath()));
//                byte[] buf = new byte[1024];
//                int n;
//                while (-1 != (n = fis.read(buf)))
//                    baos.write(buf, 0, n);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            return baos.toByteArray();
//        }
//
//        public byte[] getBytes(InputStream inputStream) throws IOException {
//            ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
//            int bufferSize = 1024;
//            byte[] buffer = new byte[bufferSize];
//
//            int len = 0;
//            while ((len = inputStream.read(buffer)) != -1) {
//                byteBuffer.write(buffer, 0, len);
//            }
//            return byteBuffer.toByteArray();
//        }
//
//        //因为我知道是 png 文件，所以直接根据文档查的
//        public String getMime() {
//
//            String type = null;
//            String extension = MimeTypeMap.getFileExtensionFromUrl(url);
//            if (extension != null) {
//                type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
//            }
//
//            return type;
//    }
//
//}

}