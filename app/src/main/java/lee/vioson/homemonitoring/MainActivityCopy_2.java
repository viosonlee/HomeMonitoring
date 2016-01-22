package lee.vioson.homemonitoring;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;

import java.util.Timer;
import java.util.TimerTask;

import lee.vioson.homemonitoring.utils.PowerTool;
import lee.vioson.homemonitoring.utils.RecordHelper;
import lee.vioson.homemonitoring.utils.SDSpaseUtil;

public class MainActivityCopy_2 extends Activity implements SurfaceHolder.Callback {
    private MediaRecorder mediarecorder;// 录制视频的类
    private SurfaceView surfaceview;// 显示视频的控件
    // 用来显示视频的一个接口，我靠不用还不行，也就是说用mediarecorder录制视频还得给个界面看
    // 想偷偷录视频的同学可以考虑别的办法。。嗯需要实现这个接口的Callback接口
    private SurfaceHolder surfaceHolder;

    private Timer timer;
    private TimerTask task;
    private static final long TASK_TIME = 30 * 60 * 1000;//录制时间

//    private PowerManager.WakeLock wakeLock;

    private RecordHelper helper;
    private HomeKeyEventBroadCastReceiver receiver;
    private boolean homeClick = false;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);// 设置全屏
        // 设置横屏显示
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        // 选择支持半透明模式,在有surfaceview的activity中使用。
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        setContentView(R.layout.main);
        helper = RecordHelper.getInstance(this);
//        wakeLock = ((PowerManager) getSystemService(POWER_SERVICE)).newWakeLock
//                (PowerManager.SCREEN_DIM_WAKE_LOCK, getClass().getCanonicalName());

        receiver = new HomeKeyEventBroadCastReceiver();
        init();
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume");
        homeClick = false;
        super.onResume();
//        PowerTool.acquireWakeLock(this, wakeLock);
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
//        PowerTool.releaseWakeLock(wakeLock);
        unregisterReceiver(receiver);
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause");
        super.onPause();
        if (homeClick)
            stopRun();
//        PowerTool.releaseWakeLock(wakeLock);
    }

    @Override
    protected void onStart() {
        Log.d(TAG, "onStart");
        super.onStart();
        handler = new ActionHandler();

        registerReceiver(receiver, new IntentFilter(
                Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
    }

    private void init() {
        surfaceview = (SurfaceView) this.findViewById(R.id.surfaceview);
        SurfaceHolder holder = surfaceview.getHolder();// 取得holder
        holder.addCallback(this); // holder加入回调接口
        // setType必须设置，要不出错.
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    private void initRecord() {
        mediarecorder = new MediaRecorder();// 创建mediarecorder对象
        // 设置录制视频源为Camera(相机)
        mediarecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        // 设置录制完成后视频的封装格式THREE_GPP为3gp.MPEG_4为mp4
        mediarecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        // 设置录制的视频编码h263 h264
        mediarecorder.setVideoEncoder(MediaRecorder.VideoEncoder.MPEG_4_SP);
        // 设置视频录制的分辨率。必须放在设置编码和格式的后面，否则报错
        mediarecorder.setVideoSize(1280, 720);
        // 设置录制的视频帧率。必须放在设置编码和格式的后面，否则报错
        mediarecorder.setVideoFrameRate(30);
        if (surfaceHolder != null)
            mediarecorder.setPreviewDisplay(surfaceHolder.getSurface());
        // 设置视频文件输出的路径
        mediarecorder.setOutputFile(SDSpaseUtil.getFilePath());
    }

    private ActionHandler handler;
    private static final int ACTION_CODE = 0;

    private void startRun() {
        timer = new Timer(true);
        initRecord();
        task = new TimerTask() {
            @Override
            public void run() {
                handler.sendEmptyMessage(ACTION_CODE);
            }
        };
        timer.schedule(task, 0, TASK_TIME);
    }

    private void stopRun() {
        if (timer != null)
            timer.cancel();
        helper.stopRecord(mediarecorder);


    }

    class HomeKeyEventBroadCastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            homeClick = true;
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
        // 将holder，这个holder为开始在oncreat里面取得的holder，将它赋给surfaceHolder
        surfaceHolder = holder;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // 将holder，这个holder为开始在oncreat里面取得的holder，将它赋给surfaceHolder
        surfaceHolder = holder;
        startRun();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // surfaceDestroyed的时候同时对象设置为null
        surfaceview = null;
        surfaceHolder = null;
        mediarecorder = null;
    }

    private static final String TAG = "tag";

    class ActionHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case ACTION_CODE:
                    helper.stopRecord(mediarecorder);
                    initRecord();
                    SDSpaseUtil.checkSpace();
                    helper.startRecord(mediarecorder);
                    Log.d(TAG, ACTION_CODE + "");
                    break;
            }
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                || keyCode == KeyEvent.KEYCODE_MENU
                || keyCode == KeyEvent.KEYCODE_SEARCH)
            if (event.getAction() == KeyEvent.ACTION_DOWN)
                return true;
        return true;
    }
}

