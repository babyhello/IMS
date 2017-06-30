package com.apps.ims;


import android.content.Context;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.List;


public class VideoPreview extends SurfaceView implements SurfaceHolder.Callback {

    private final static String TAG = VideoPreview.class.getSimpleName();

    private Camera mCamera = null;
    private SurfaceHolder mHolder = null;
    private MediaRecorder mMediaRecorder = null;


    private int mCameraId = 0;
    private String mFilePath = null;
    private boolean isRecording = false;
    private static final int BACK_FACING_CAMERA = 0;


    private MediaRecorder.OnErrorListener errorListener = new MediaRecorder.OnErrorListener() {
        @Override
        public void onError(MediaRecorder mr, int what, int extra) {
            Log.e(TAG, "onError");
            Log.e(TAG, "what: " + what);
            Log.e(TAG, "extra: " + extra);
        }
    };

    private MediaRecorder.OnInfoListener infoListener = new MediaRecorder.OnInfoListener() {

        @Override
        public void onInfo(MediaRecorder mr, int what, int extra) {
            Log.e(TAG, "onInfo");
            Log.e(TAG, "what: " + what);
            Log.e(TAG, "extra: " + extra);
        }
    };


    public VideoPreview(Context context) {
        super(context);
        Log.i(TAG, "VideoRecordView(Context context)");
        init(context);
    }

    public VideoPreview(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    /*  PRIVATE AREA   */
    private void init(Context context) {
        Log.i(TAG, "init");
        setCamera();
        mHolder = getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

    }

    public void StartPrevCamera() {
        mCamera.startPreview();
    }


    private void setCamera() {
        Log.i(TAG, "setCamera");
        if (Camera.getNumberOfCameras() < 1) return;

        if (!useFrontCamera()) {
            if (!useBackCamera()) {
            }
        }
        setCameraParameter();
    }

    public void mTakePicture(Camera.PictureCallback mPicture) {

        mCamera.takePicture(null, null, mPicture);
    }


    private boolean useFrontCamera() {
        Log.i(TAG, "useFrontCamera");
        if (mCamera != null) mCamera.release();
        int numberOfCameras = Camera.getNumberOfCameras();
        if (numberOfCameras < 1) return false;
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();

        for (int i = 0; i < numberOfCameras; i++) {
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                try {
                    mCamera = Camera.open(i);
                    mCameraId = i;
                    Log.d(TAG, "Found Front Camera, ID: " + mCameraId);
                    break;
                } catch (RuntimeException e) {
                    Log.e(TAG, e.getMessage());
                    e.printStackTrace();
                    mCamera = null;
                    return false;
                }
            }
        }
        if (mCamera == null) {
            if (numberOfCameras > 1) {
                try {
                    mCamera = Camera.open(numberOfCameras - 1);
                    mCameraId = numberOfCameras - 1;
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                    e.printStackTrace();
                    mCamera = null;
                    return false;
                }
            } else {
                return false;
            }
        }
        return true;
    }

    private boolean useBackCamera() {
        Log.i(TAG, "useBackCamera");
        if (mCamera != null) mCamera.release();
        int numberOfCameras = Camera.getNumberOfCameras();
        if (numberOfCameras < 1) return false;

        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        for (int i = 0; i < numberOfCameras; i++) {
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                try {
                    mCamera = Camera.open(i);
                    mCameraId = i;
                    Log.d(TAG, "Found Back Camera, ID: " + mCameraId);
                    break;
                } catch (RuntimeException e) {
                    e.printStackTrace();
                    mCamera = null;
                    return false;
                }
            }
        }
        return true;
    }

    private boolean prepareVideoRecorde() {
        Log.i(TAG, "prepareVideoRecorde");
        if (mCamera == null) return false;
        if (mMediaRecorder == null) mMediaRecorder = new MediaRecorder();

        mCamera.unlock();
        mMediaRecorder.setCamera(mCamera);

        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

        mMediaRecorder.setProfile(CamcorderProfile.get(mCameraId, CamcorderProfile.QUALITY_HIGH));
        mMediaRecorder.setOutputFile(mFilePath);
        mMediaRecorder.setPreviewDisplay(mHolder.getSurface());

        mMediaRecorder.setOnErrorListener(errorListener);
        mMediaRecorder.setOnInfoListener(infoListener);
        setVideoOrientation();

        try {
            mMediaRecorder.prepare();
        } catch (Exception e) {
            Log.e(TAG, "Error starting MediaRecorder prepare: " + e.getMessage());
            e.printStackTrace();
            mMediaRecorder.release();
            return false;
        }
        return true;
    }


    private void setVideoOrientation() {
        if (mCameraId == Camera.CameraInfo.CAMERA_FACING_BACK) {
            mMediaRecorder.setOrientationHint(90);
        } else {
            mMediaRecorder.setOrientationHint(270);
        }
    }

    // Different devices may have different camera capabilities
    private void setCameraParameter() {
        if (mCamera == null) return;
        Camera.Parameters parameters = mCamera.getParameters();
        parameters.setRecordingHint(true);

        mCamera.setDisplayOrientation(90);
        parameters.set("orientation", "portrait");
        parameters.setRotation(90);

        List<String> focusModes = parameters.getSupportedFocusModes();
        for (int i = 0; i < focusModes.size(); i++) {
            if (focusModes.get(i).equalsIgnoreCase(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
                break;
            }
        }

        List<String> sceneModes = parameters.getSupportedSceneModes();
        for (int i = 0; !(sceneModes == null) && (i < sceneModes.size()); i++) {
            if (sceneModes.get(i).equalsIgnoreCase(Camera.Parameters.SCENE_MODE_AUTO)) {
                parameters.setSceneMode(Camera.Parameters.SCENE_MODE_AUTO);
                break;
            }
        }

        List<String> whiteBalance = parameters.getSupportedWhiteBalance();
        for (int i = 0; !(whiteBalance == null) && (i < whiteBalance.size()); i++) {
            if (whiteBalance.get(i).equalsIgnoreCase(Camera.Parameters.WHITE_BALANCE_AUTO)) {
                parameters.setWhiteBalance(Camera.Parameters.WHITE_BALANCE_AUTO);
                break;
            }
        }

        mCamera.setParameters(parameters);
    }
    /*  PRIVATE AREA   */

    /*  PUBLIC AREA   */
    public void setFilePath(String sFilePath) {
        mFilePath = sFilePath;
    }

    //TODO errorcode
    public boolean switchCamera() {
        Log.i(TAG, "switchCamera");
        if (isRecording) {
            if (!stopRecord()) {
                return false;
            }
        }

        try {
            mCamera.stopPreview();
        } catch (Exception e) {
            Log.e(TAG, "Error stoping camera preview: " + e.getMessage());
            e.printStackTrace();
            return false;
        }

        releaseCamera();

        if (mCameraId == BACK_FACING_CAMERA) {
            if (!useFrontCamera()) {
                return false;
            }
        } else {
            if (!useBackCamera()) {
                return false;
            }
        }
        if (mCamera == null) return false;
        setCameraParameter();

        Camera.Parameters parameters = mCamera.getParameters();
        parameters.setRecordingHint(true);
        mCamera.setParameters(parameters);
        mCamera.setDisplayOrientation(90);

        try {
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();
        } catch (Exception e) {
            Log.e(TAG, "Error staring camera preview: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean startRecord() {
        Log.i(TAG, "startRecord");
        if (isRecording) return true;

        if (!prepareVideoRecorde()) {
            releaseMediaRecorder();
            return false;
        }
        mMediaRecorder.start();
        isRecording = true;
        return true;
    }

    public boolean stopRecord() {
        Log.i(TAG, "stopRecord");
        if (!isRecording) return true;
        /*假若按下開始 - 結束 之間 未超過1秒, 會crash, 原因應該是因為 MediaRecord Server 尚未收到資料. 加入下列兩行可解此問題*/
        mMediaRecorder.setOnErrorListener(null);
        mMediaRecorder.setPreviewDisplay(null);

        mMediaRecorder.stop();
        releaseMediaRecorder();
        isRecording = false;
        return true;
    }

    public boolean isRecording() {
        return isRecording;
    }

    public void releaseCamera() {
        Log.i(TAG, "releaseCamera");
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }

    public void releaseMediaRecorder() {
        Log.i(TAG, "releaseMediaRecorder");
        if (mMediaRecorder != null) {
            mMediaRecorder.reset();
            mMediaRecorder.release();
            mMediaRecorder = null;
        }
        if (mCamera != null) mCamera.lock();
    }
    /*  PUBLIC AREA   */


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.i(TAG, "surfaceCreated");
        try {
            if (mCamera == null)
                setCamera();


            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();
        } catch (Exception e) {
            Log.e(TAG, "Error setting camera preview: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.i(TAG, "surfaceChanged");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.i(TAG, "surfaceDestroyed");
        releaseMediaRecorder();
        releaseCamera();
    }
}