package lee.vioson.homemonitoring;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import lee.vioson.homemonitoring.service.RecordService;
import lee.vioson.homemonitoring.thread.RecordThread;
import lee.vioson.homemonitoring.utils.PowerTool;
import lee.vioson.homemonitoring.utils.RunSomeApp;

/**
 * Author:李烽
 * Date:2016-01-19
 * FIXME
 * Todo
 */
public class MainActivity extends Activity implements View.OnClickListener {
    private Intent intent;
    //    private PowerManager.WakeLock wakeLock;
    boolean b = true;
    boolean stop = false;
    private Button stopBtn, lightBtn, finishBtn;
    private RecordBCReceiver recordBCReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        intent = new Intent(this, RecordService.class);
        startService(intent);

        stopBtn = (Button) findViewById(R.id.stop_btn);
//        lightBtn = (Button) findViewById(R.id.off_screen_btn);
        finishBtn = (Button) findViewById(R.id.finish_btn);
        stopBtn.setOnClickListener(this);
//        lightBtn.setOnClickListener(this);
        finishBtn.setOnClickListener(this);
        findViewById(R.id.root_layout).setOnClickListener(this);
//        wakeLock = ((PowerManager) getSystemService(POWER_SERVICE)).newWakeLock
//                (PowerManager.PARTIAL_WAKE_LOCK, getClass().getCanonicalName());
        recordBCReceiver = new RecordBCReceiver();

        PowerTool.checkScreenBrightness(this);

//        setScreenBritness(1);

        startBaiDuYun();
    }

    private void startBaiDuYun() {
        //唤醒百度云
        RunSomeApp.runApp(this, "com.baidu.netdisk");
    }


    public void stop() {
        Log.i("info", "stop");
        if (stop) {
            stop = false;
            startService(intent);
            stopBtn.setText("停止");
        } else {
            stop = true;
            stopService(intent);
            stopBtn.setText("开始");
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter intentFilter = new IntentFilter(RecordThread.START_RECORD_ACTION);
        registerReceiver(recordBCReceiver, intentFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(recordBCReceiver);
//        PowerTool.releaseWakeLock(wakeLock);
        PowerTool.setScreenBritness(this, 100);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.stop_btn:
                stop();
                break;
//            case R.id.off_screen_btn:
//                testClick();
//                break;
            case R.id.finish_btn:
                if (stop)
                    finish();
                else {
                    stop();
                    finish();
                }
                break;
            case R.id.root_layout:
                if (b) {
                    PowerTool.setScreenBritness(this, 1f);
                    b = false;
                } else {
                    PowerTool.setScreenBritness(this, 100f);
                    b = true;
                }
//
                break;
            default:
                break;
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

    class RecordBCReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(RecordThread.START_RECORD_ACTION)) {
                startBaiDuYun();
                PowerTool.setScreenBritness(MainActivity.this, 1f);
                b = false;
            }

        }
    }
}
