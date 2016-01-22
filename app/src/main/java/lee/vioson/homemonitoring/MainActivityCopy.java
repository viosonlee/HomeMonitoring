package lee.vioson.homemonitoring;

import android.app.Activity;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import lee.vioson.homemonitoring.thread.RecordThread;
import lee.vioson.homemonitoring.utils.PowerTool;

/**
 * Author:李烽
 * Date:2016-01-19
 * FIXME
 * Todo
 */
public class MainActivityCopy extends Activity implements SurfaceHolder.Callback {
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
//    private PowerManager.WakeLock wakeLock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.flow_window);
        surfaceView = (SurfaceView) findViewById(R.id.surfaceview_video);
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

//        wakeLock = ((PowerManager) getSystemService(POWER_SERVICE)).newWakeLock
//                (PowerManager.SCREEN_DIM_WAKE_LOCK, getClass().getCanonicalName());
    }

    @Override
    protected void onResume() {
        super.onResume();
//        PowerTool.acquireWakeLock(this, wakeLock);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        PowerTool.releaseWakeLock(wakeLock);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        this.surfaceHolder = holder;
        RecordThread thread = new RecordThread(this, surfaceHolder);
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
