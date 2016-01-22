package lee.vioson.homemonitoring.utils;

import android.app.Activity;
import android.content.Context;
import android.os.PowerManager;
import android.provider.Settings;
import android.view.WindowManager;

/**
 * Author:李烽
 * Date:2016-01-18
 * FIXME
 * Todo
 */
public class PowerTool {
    public static void acquireWakeLock(Context context, PowerManager.WakeLock wakeLock) {
        if (wakeLock == null) {
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            wakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, context.getClass().getCanonicalName());
        }
        wakeLock.acquire();
    }

    public static void releaseWakeLock(PowerManager.WakeLock wakeLock) {
        if (wakeLock != null && wakeLock.isHeld()) {
            wakeLock.release();
            wakeLock = null;
        }
    }

    public static void checkScreenBrightness(Context context) {
        //先关闭系统的亮度自动调节
        try {
            if (android.provider.Settings.System.getInt(context.getContentResolver(),
                    android.provider.Settings.System.SCREEN_BRIGHTNESS_MODE)
                    == android.provider.Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC) {
                android.provider.Settings.System.putInt(context.getContentResolver(),
                        android.provider.Settings.System.SCREEN_BRIGHTNESS_MODE,
                        android.provider.Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
            }
        } catch (Settings.SettingNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public static void setScreenBritness(Activity context, float brightness) {
        //不让屏幕全暗
        if (brightness <= 0) {
            brightness = 0;
        }
        //设置当前activity的屏幕亮度
        WindowManager.LayoutParams lp = context.getWindow().getAttributes();
        //0到1,调整亮度暗到全亮
        lp.screenBrightness = brightness / 255f;
        lp.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON| WindowManager.LayoutParams.FLAG_FULLSCREEN;
        context.getWindow().setAttributes(lp);

//        //保存为系统亮度方法1
//        android.provider.Settings.System.putInt(context.getContentResolver(),
//                android.provider.Settings.System.SCREEN_BRIGHTNESS,
//                brightness);

        //保存为系统亮度方法2
//        Uri uri = android.provider.Settings.System.getUriFor("screen_brightness");
//        android.provider.Settings.System.putInt(getContentResolver(), "screen_brightness", brightness);
//        // resolver.registerContentObserver(uri, true, myContentObserver);
//        getContentResolver().notifyChange(uri, null);

    }
}
