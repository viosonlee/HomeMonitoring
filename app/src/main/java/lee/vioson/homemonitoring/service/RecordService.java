package lee.vioson.homemonitoring.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;

import lee.vioson.homemonitoring.R;
import lee.vioson.homemonitoring.thread.RecordThread;
import lee.vioson.homemonitoring.utils.PowerTool;
import lee.vioson.homemonitoring.utils.RunSomeApp;

/**
 * Author:李烽
 * Date:2016-01-19
 * FIXME
 * Todo
 */
public class RecordService extends Service implements SurfaceHolder.Callback {
    private SurfaceView surfaceView;
    private WindowManager mWindowManager;
    private SurfaceHolder surfaceHolder;
    private View view;
    private RecordThread thread;
    public PowerManager.WakeLock wakeLock;
    private boolean isBig = false;

    public RecordService() {
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Context mContext = this;
        view = LayoutInflater.from(mContext).inflate(R.layout.flow_window, null);
        surfaceView = (SurfaceView) view.findViewById(R.id.surfaceview_video);
        final ViewGroup.LayoutParams lp = surfaceView.getLayoutParams();

        surfaceView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isBig) {
                    isBig = false;
                    lp.width = RecordService.this.getResources().getDimensionPixelOffset(R.dimen.flow_window_width);
                    lp.height = RecordService.this.getResources().getDimensionPixelOffset(R.dimen.flow_window_height);
                    surfaceView.setLayoutParams(lp);
                } else {
                    isBig = true;
                    lp.width = 450;
                    lp.height = 750;
                    surfaceView.setLayoutParams(lp);
                }
            }
        });
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        wakeLock = ((PowerManager) getSystemService(POWER_SERVICE)).newWakeLock
                (PowerManager.SCREEN_BRIGHT_WAKE_LOCK, getClass().getCanonicalName());
        PowerTool.acquireWakeLock(this, wakeLock);

        mWindowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        layoutParams.format = PixelFormat.RGBA_8888;
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        layoutParams.gravity = Gravity.LEFT | Gravity.TOP;
        layoutParams.x = 0;
        layoutParams.y = 0;
        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        mWindowManager.addView(view, layoutParams);

    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (surfaceView != null)
            mWindowManager.removeView(view);
        thread.stopRecord();
        thread.releaseCamera();
        PowerTool.releaseWakeLock(wakeLock);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        this.surfaceHolder = holder;
        thread = new RecordThread(this, surfaceHolder);
        thread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        this.surfaceHolder = holder;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        surfaceHolder = null;
        surfaceView = null;
    }
}
