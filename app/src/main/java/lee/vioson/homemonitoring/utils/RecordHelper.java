package lee.vioson.homemonitoring.utils;


import android.content.Context;
import android.media.MediaRecorder;
import android.view.SurfaceHolder;

import java.io.IOException;

/**
 * Author:李烽
 * Date:2016-01-18
 * FIXME
 * Todo 录制操作类
 */
public class RecordHelper {
    private static RecordHelper instance;
    private Context context;

    private RecordHelper(Context context) {
        this.context = context;
    }

    public synchronized static RecordHelper getInstance(Context context) {
        if (instance == null)
            synchronized (RecordHelper.class) {
                if (instance == null)
                    instance = new RecordHelper(context);
            }
        return instance;
    }

    public void startRecord(MediaRecorder mediaRecorder) {
        try {
            // 准备录制
            mediaRecorder.prepare();
            // 开始录制
            mediaRecorder.start();
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (RuntimeException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public  void stopRecord(MediaRecorder mediaRecorder) {
        if (mediaRecorder != null) {
            try {
                // 停止录制
                mediaRecorder.stop();
                // 释放资源
                mediaRecorder.release();
                mediaRecorder = null;
            } catch (IllegalStateException e) {
//                Toast.makeText(this, "IllegalStateException", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void initRecorder(MediaRecorder mediaRecorder, SurfaceHolder surfaceHolder) {
        mediaRecorder = new MediaRecorder();// 创建mediarecorder对象
        // 设置录制视频源为Camera(相机)
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        // 设置录制完成后视频的封装格式THREE_GPP为3gp.MPEG_4为mp4
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        // 设置录制的视频编码h263 h264
        mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.MPEG_4_SP);
        // 设置视频录制的分辨率。必须放在设置编码和格式的后面，否则报错
        mediaRecorder.setVideoSize(1280, 720);
        // 设置录制的视频帧率。必须放在设置编码和格式的后面，否则报错
        mediaRecorder.setVideoFrameRate(30);
        if (surfaceHolder != null)
            mediaRecorder.setPreviewDisplay(surfaceHolder.getSurface());
        // 设置视频文件输出的路径
        mediaRecorder.setOutputFile(SDSpaseUtil.getFilePath());
    }


}
