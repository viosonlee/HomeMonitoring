package lee.vioson.homemonitoring.thread;

import android.annotation.TargetApi;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;

import java.io.IOException;
import java.util.List;
import java.util.TimerTask;

import lee.vioson.homemonitoring.MainActivity;
import lee.vioson.homemonitoring.service.RecordService;
import lee.vioson.homemonitoring.utils.PowerTool;
import lee.vioson.homemonitoring.utils.SDSpaseUtil;


/**
 * Author:李烽
 * Date:2016-01-19
 * FIXME
 * Todo
 */
public class RecordThread extends Thread {
    private static final int TASK_TIME = 10 * 60 * 1000;
    //    private static final int TASK_TIME = 10 * 1000;
    public static final String START_RECORD_ACTION = "lee.vioson.homemonitoring.thread.start_record";
    private MediaRecorder mediaRecorder;
    private SurfaceHolder surfaceHolder;
    private Camera camera;
    private Context context;
    private boolean isFocus = false;

    public RecordThread(Context context, SurfaceHolder surfaceHolder) {
        this.context = context;
        this.surfaceHolder = surfaceHolder;
    }

    @Override
    public void run() {
        super.run();
        /**
         * 开始录像
         */
        initRecorder();
        startRecord();

    }

    private void backToMain() {
        new Handler(context.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                toMain();
            }
        }, 500);
    }

    private void toMain() {
        Intent mainIntent = new Intent(((Service) context).getBaseContext(), MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ((Service) context).getApplication().startActivity(mainIntent);
    }

    private void sendBroadcast() {
        Intent intent = new Intent();
        intent.setAction(START_RECORD_ACTION);
        context.sendBroadcast(intent);
    }

    private void startRecord() {
        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
        } catch (IOException | IllegalStateException e) {
            e.printStackTrace();
        }
        sendBroadcast();
        backToMain();
        SDSpaseUtil.checkSpace();
//        PowerTool.acquireWakeLock(context, ((RecordService) context).wakeLock);
    }

    private void initRecorder() {
        mediaRecorder = new MediaRecorder();
        if (camera == null)
            camera = getCamera();
        if (camera != null) {
            mediaRecorder.setCamera(camera);
        }
        // 设置录制视频源为Camera(相机)
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        // 设置录制文件质量，格式，分辨率之类，这个全部包括了
        // 设置录制完成后视频的封装格式THREE_GPP为3gp.MPEG_4为mp4
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        // 设置录制的视频编码h263 h264
        mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.MPEG_4_SP);

        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
//         设置视频录制的分辨率。必须放在设置编码和格式的后面，否则报错
        mediaRecorder.setVideoSize(1280, 720);
//        CamcorderProfile profile = CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH);
//        mediaRecorder.setProfile(profile);
        // 设置录制的视频帧率。必须放在设置编码和格式的后面，否则报错

//        mediaRecorder.setVideoFrameRate(30);

        mediaRecorder.setVideoEncodingBitRate(5 * 1280 * 720);

        mediaRecorder.setMaxDuration(TASK_TIME);

        mediaRecorder.setPreviewDisplay(surfaceHolder.getSurface());

        mediaRecorder.setOutputFile(SDSpaseUtil.getFilePath());

        mediaRecorder.setOnInfoListener(new MediaRecorder.OnInfoListener() {
            @Override
            public void onInfo(MediaRecorder mr, int what, int extra) {
                if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED) {
                    actionHandler.sendEmptyMessage(ACTION_CODE);
                }
            }
        });
    }

    public void stopRecord() {
        if (mediaRecorder != null) {
            try {
                mediaRecorder.stop();
                mediaRecorder.release();
                mediaRecorder = null;
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        }
//        PowerTool.releaseWakeLock(((RecordService) context).wakeLock);
    }

    public void releaseCamera() {
        if (camera != null) {
            camera.release();
            camera = null;
        }
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    private Camera getCamera() {
        Camera camera = null;
        try {
            camera = Camera.open(0);
//            setCameraParams(camera);
            camera.setDisplayOrientation(90);//摄像图旋转90度
//            camera.setPreviewDisplay(surfaceHolder);
//            camera.startPreview();
//            if (isFocus) {
//                camera.autoFocus(null);
//            }
            camera.unlock();
        } catch (Exception e) {
            // 打开摄像头错误
            Log.i("info", "打开摄像头错误");
        }
        return camera;
    }

    public void setCameraParams(Camera camera) {
        if (camera != null) {
            Camera.Parameters params = camera.getParameters();
            List<String> list = params.getSupportedFocusModes();
            if (list.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
                isFocus = true;
                params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
            }
            params.set("orientation", "portrait");
            camera.setParameters(params);
        }
    }

    private ActionHandler actionHandler = new ActionHandler();
    private static final int ACTION_CODE = 0;

    class ActionHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case ACTION_CODE:
                    stopRecord();
                    initRecorder();
                    startRecord();
                    break;
                default:
                    break;
            }
        }
    }
}
