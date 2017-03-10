package com.example.yujhaochen.ims;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.provider.SyncStateContract;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.ServerResponse;
import net.gotev.uploadservice.UploadInfo;
import net.gotev.uploadservice.UploadService;
import net.gotev.uploadservice.UploadStatusDelegate;

import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.content.ContentValues.TAG;

/**
 * Created by yujhaochen on 2017/1/5.
 */
public class UploadFileProgress implements UploadStatusDelegate {

    private Dialog UploadDialog;

    private UploadFileListenter mListenter = new UploadFileListenter()
    {

        @Override
        public void Success() {
            // TODO Auto-generated method stub
        }

        @Override
        public void Fault() {
            // TODO Auto-generated method stub
        }

    };

    Context mcontext;

    public ViewGroup container;

    class UploadProgressViewHolder {
        View itemView;

        @BindView(R.id.uploadTitle)
        TextView uploadTitle;
        @BindView(R.id.uploadProgress)
        ProgressBar progressBar;

        String uploadId;

        UploadProgressViewHolder(View view, String filename) {
            itemView = view;
            ButterKnife.bind(this, itemView);
            uploadTitle = (TextView)itemView.findViewById(R.id.uploadTitle) ;
            progressBar = (ProgressBar)itemView.findViewById(R.id.uploadProgress) ;
            progressBar.setMax(100);
            progressBar.setProgress(0);

//            uploadTitle.setText(getString(R.string.upload_progress, filename));
        }

//        @OnClick(R.id.cancelUploadButton)
//        void onCancelUploadClick() {
//            if (uploadId == null)
//                return;
//
//            UploadService.stopUpload(uploadId);
//        }
    }

    private Map<String, UploadProgressViewHolder> uploadProgressHolders = new HashMap<>();

    private void logSuccessfullyUploadedFiles(List<String> files) {

        for (String file : files) {
            Log.e(TAG, "Success:" + file);
        }
    }
    @Override
    public void onProgress(UploadInfo uploadInfo) {
        Log.i(TAG, String.format(Locale.getDefault(), "ID: %1$s (%2$d%%) at %3$.2f Kbit/s",
                uploadInfo.getUploadId(), uploadInfo.getProgressPercent(),
                uploadInfo.getUploadRate()));
        logSuccessfullyUploadedFiles(uploadInfo.getSuccessfullyUploadedFiles());

        if (uploadProgressHolders.get(uploadInfo.getUploadId()) == null)
            return;

        uploadProgressHolders.get(uploadInfo.getUploadId())
                .progressBar.setProgress(uploadInfo.getProgressPercent());

        uploadProgressHolders.get(uploadInfo.getUploadId())
                .uploadTitle.setText(uploadInfo.getProgressPercent() + "%");
    }


    @Override
    public void onError(UploadInfo uploadInfo, Exception exception) {
        Log.e(TAG, "Error with ID: " + uploadInfo.getUploadId() + ": "
                + exception.getLocalizedMessage(), exception);
        logSuccessfullyUploadedFiles(uploadInfo.getSuccessfullyUploadedFiles());

//        if (uploadProgressHolders.get(uploadInfo.getUploadId()) == null)
//            return;
//
//        container.removeView(uploadProgressHolders.get(uploadInfo.getUploadId()).itemView);
//        uploadProgressHolders.remove(uploadInfo.getUploadId());
        UploadDialog.hide();
    }

    @Override
    public void onCompleted(UploadInfo uploadInfo, ServerResponse serverResponse) {
        Log.i(TAG, String.format(Locale.getDefault(),
                "ID %1$s: completed in %2$ds at %3$.2f Kbit/s. Response code: %4$d, body:[%5$s]",
                uploadInfo.getUploadId(), uploadInfo.getElapsedTime() / 1000,
                uploadInfo.getUploadRate(), serverResponse.getHttpCode(),
                serverResponse.getBodyAsString()));
        logSuccessfullyUploadedFiles(uploadInfo.getSuccessfullyUploadedFiles());
        for (Map.Entry<String, String> header : serverResponse.getHeaders().entrySet()) {
            Log.i("Header", header.getKey() + ": " + header.getValue());
        }

        Log.e(TAG, "Printing response body bytes");
        byte[] ba = serverResponse.getBody();
        for (int j = 0; j < ba.length; j++) {
            Log.e(TAG, String.format("%02X ", ba[j]));
        }
        mListenter.Success();
//        if (uploadProgressHolders.get(uploadInfo.getUploadId()) == null)
//            return;
//
//        container.removeView(uploadProgressHolders.get(uploadInfo.getUploadId()).itemView);
//        uploadProgressHolders.remove(uploadInfo.getUploadId());
        UploadDialog.hide();
    }

    @Override
    public void onCancelled(UploadInfo uploadInfo) {
        Log.i(TAG, "Upload with ID " + uploadInfo.getUploadId() + " is cancelled");
        logSuccessfullyUploadedFiles(uploadInfo.getSuccessfullyUploadedFiles());

//        if (uploadProgressHolders.get(uploadInfo.getUploadId()) == null)
//            return;
//
//        container.removeView(uploadProgressHolders.get(uploadInfo.getUploadId()).itemView);
//        uploadProgressHolders.remove(uploadInfo.getUploadId());
        UploadDialog.hide();
    }

    public void Go_Upload(final String ServerPath,final String FileName,final String FilePath,ViewGroup mViewGroup,final UploadFileListenter Listenter)
    {
        container = mViewGroup;

        mListenter = Listenter;

        final String[] filesToUploadArray = FilePath.split(",");

        for (String fileToUploadPath : filesToUploadArray) {
            try {
                final String filename = getFilename(fileToUploadPath);

                MultipartUploadRequest req = new MultipartUploadRequest(mcontext, ServerPath)
                        .addFileToUpload(fileToUploadPath, "Data").addParameter("FileName", FileName)

                        .setMaxRetries(2);


                String uploadID = req.setDelegate(this).startUpload();

                addUploadToList(uploadID, filename);

                // these are the different exceptions that may be thrown
            } catch (FileNotFoundException exc) {
                showToast(exc.getMessage());
            } catch (IllegalArgumentException exc) {
                showToast("Missing some arguments. " + exc.getMessage());
            } catch (MalformedURLException exc) {
                showToast(exc.getMessage());
            }
        }
    }

    public UploadFileProgress(Context context) {

        mcontext = context;
    }

    private void addUploadToList(String uploadID, String filename) {

        AlertDialog.Builder builder = new AlertDialog.Builder(mcontext);
        //builder.setTitle("Update");

        final LayoutInflater inflater = LayoutInflater.from(mcontext);

        View uploadProgressView = inflater.inflate(R.layout.view_upload_progress, null);

        UploadProgressViewHolder viewHolder = new UploadProgressViewHolder(uploadProgressView, filename);

        viewHolder.uploadId = uploadID;

        uploadProgressHolders.put(uploadID, viewHolder);

        builder.setView(uploadProgressView);

        UploadDialog = builder.create();

        UploadDialog.show();

//        final LayoutInflater inflater = LayoutInflater.from(mcontext);
//
//        View uploadProgressView = inflater.inflate(R.layout.view_upload_progress, null);
//
//        UploadProgressViewHolder viewHolder = new UploadProgressViewHolder(uploadProgressView, filename);
//
//        viewHolder.uploadId = uploadID;
//
//        container.addView(viewHolder.itemView, 0);
//
//        uploadProgressHolders.put(uploadID, viewHolder);
    }

    private void showToast(String message) {
        Toast.makeText(mcontext, message, Toast.LENGTH_LONG).show();
    }

    private String getFilename(String filepath) {
        if (filepath == null)
            return null;

        final String[] filepathParts = filepath.split("/");

        return filepathParts[filepathParts.length - 1];
    }

//    public UploadFileProgress(Context context, String path, String name,String WebSitePath, final UploadFileListenter listenter) {
//        //Uploading code
//        try {
//            String uploadId = UUID.randomUUID().toString();
//
//            new MultipartUploadRequest(context, uploadId, WebSitePath)
//                    .addFileToUpload(path, "Data") //Adding file
//                    .addParameter("FileName", name) //Adding text parameter to the request
//                    .setMaxRetries(2)
//                    .setDelegate(new UploadStatusDelegate() {
//                        @Override
//                        public void onProgress(UploadInfo uploadInfo) {
//                            listenter.onProgress(uploadInfo.getUploadedBytes(),uploadInfo.getTotalBytes());
//                        }
//
//
//
//                        @Override
//                        public void onError(UploadInfo uploadInfo, Exception exception) {
//                            listenter.Fault();
//                        }
//
//                        @Override
//                        public void onCompleted(UploadInfo uploadInfo, ServerResponse serverResponse) {
//                            listenter.Success();
//                        }
//
//                        @Override
//                        public void onCancelled(UploadInfo uploadInfo) {
//
//                        }
//                    })
//                    .startUpload(); //Starting the upload
//
//
//        } catch (Exception exc) {
//            Toast.makeText(context, exc.getMessage(), Toast.LENGTH_SHORT).show();
//            Log.d("", "error = " + exc.getMessage());
//        }
//    }

}
