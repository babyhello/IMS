package com.apps.ims;

import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;

/**
 * Created by androids on 2016/11/4.
 */
public class UploadImage {
    //参数的名称
    private String mName;
    //文件名
    private String mFileName;
    //文件的 mime，需要根据文档查询
    private String mMime;
    //需要上传的图片资源，因为这里测试为了方便起见，直接把 bigmap 传进来，真正在项目中一般不会这般做，而是把图片的路径传过来，在这里对图片进行二进制转换
    private Bitmap mBitmap;

    public UploadImage(Bitmap mBitmap, String ImageName) {
        this.mBitmap = mBitmap;
        this.mName = ImageName;
    }

    public String getName() {
//        return mName;
//测试，把参数名称写死
        return "uploadimg";
    }

    public String getFileName() {
        //测试，直接写死文件的名字
        return mName;
    }

    //对图片进行二进制转换
    public byte[] getValue() {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        mBitmap.compress(Bitmap.CompressFormat.JPEG, 80, bos);
        return bos.toByteArray();
    }

    //因为我知道是 png 文件，所以直接根据文档查的
    public String getMime() {
        return "image/png";
    }
}
