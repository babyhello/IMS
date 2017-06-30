package com.apps.ims;

import android.net.Uri;
import android.webkit.MimeTypeMap;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;


public class UploadFile {

    private Uri Uri;

    private File file;

    private String Path;

    private String MimeType;

    public UploadFile(String Path) {
        this.Path = Path;

        this.Uri = Uri.parse(Path);
    }

    public String GetMimeType() {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(Path);
        if (extension != null) {
            MimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }

        return MimeType;
    }

    public byte[] GetByteValue() {
        Uri uri = Uri.parse(Path);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        FileInputStream fis;
        try {
            fis = new FileInputStream(new File(Uri.getPath()));
            byte[] buf = new byte[1024];
            int n;
            while (-1 != (n = fis.read(buf)))
                baos.write(buf, 0, n);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return baos.toByteArray();
    }


    public String GetName() {

        return "UploadFile";
    }

    public String GetFileName() {

        String fileName = Path.substring(Path.lastIndexOf('/') + 1);

        return fileName;
    }
}
