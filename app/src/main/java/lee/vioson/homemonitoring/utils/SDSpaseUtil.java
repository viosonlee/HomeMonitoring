package lee.vioson.homemonitoring.utils;

import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Author:李烽
 * Date:2016-01-18
 * FIXME
 * Todo
 */
public class SDSpaseUtil {
    private static final String TAG = "SDSpaseUtil";
    private static final long MIN_SPACE_SIZE = 120;
    private static String dirName = "/ARecode";
    private static final String SUFFIX = ".mp4";
    private static final String DATE_FORMAT = "yyyyMMddHHmmss";

    public static void checkSpace() {
        getAvailableExternalMemorySize();
        getAvailableInternalMemorySize();
        getTotalExternalMemorySize();
        getTotalInternalMemorySize();
        Log.i(TAG, "内部可用存储空间是：" + Long.toString(getAvailableInternalMemorySize() / (1024 * 1024)));
        Log.i(TAG, "内部总共存储空间是：" + Long.toString(getTotalInternalMemorySize() / (1024 * 1024)));
        Log.i(TAG, "外部可用存储空间是：" + Long.toString(getAvailableExternalMemorySize() / (1024 * 1024)));
        Log.i(TAG, "外部总共存储空间是：" + Long.toString(getTotalExternalMemorySize() / (1024 * 1024)));
        if (getAvailableExternalMemorySize() / (1024 * 1024) <= MIN_SPACE_SIZE)
            deleteCache();
    }

    private static void deleteCache() {
        new Thread(new MyThread()).start();
    }

    static class MyThread implements Runnable {
        @Override
        public void run() {
            File fileDir = new File(getDirPath());
            if (fileDir.exists() && fileDir.isDirectory()) {
                File videos[] = fileDir.listFiles();
                for (int i = videos.length - 1; i >= 0; i--) {
                    File file = videos[i];
//                    long l = file.lastModified();
                    String name = file.getName();
                    name = name.substring(0, 13);
                    Log.i(TAG, name);
                    long l = strToDate(name);
                    long now = System.currentTimeMillis();
                    long d = now - l;
                    long h = d / (1000 * 60 * 60);
                    Log.i(TAG, "l:" + l + "\nnow:" + now + "\nd:" + d + "\nh:" + h);

                    if (h >= 3) {
                        Log.i(TAG, "delete:" + name);
                        file.delete();
                    }
                }
            }
        }
    }

    private static String getDirPath() {
        return getSDPath() + dirName;
    }

    public static long getAvailableInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return availableBlocks * blockSize;
    }

    public static long getTotalInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        return totalBlocks * blockSize;
    }

    public static boolean externalMemoryAvailable() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    public static long getAvailableExternalMemorySize() {
        if (externalMemoryAvailable()) {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();
            long availableBlocks = stat.getAvailableBlocks();
            return availableBlocks * blockSize;
        } else {
            return -1;
        }
    }

    public static long getTotalExternalMemorySize() {
        if (externalMemoryAvailable()) {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();
            long totalBlocks = stat.getBlockCount();
            return totalBlocks * blockSize;
        } else {
            return -1;
        }
    }

    public static String getFilePath() {

        String path = getSDPath() + dirName + "/" + getDate() + SUFFIX;
        File dir = new File(getSDPath() + dirName);
        if (!dir.exists())
            dir.mkdirs();
        return path;
    }

    /**
     * 使用时间对录像起名
     *
     * @return
     */
    private static String getDate() {
        Calendar ca = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        return sdf.format(ca.getTimeInMillis());
    }

    private static long strToDate(String time) {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        Date dt2 = new Date();
        try {
            dt2 = sdf.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dt2.getTime();
    }

    /**
     * 获取SD path
     *
     * @return
     */
    private static String getSDPath() {
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();// 获取跟目录
            // Toast.makeText(this,sdDir.toString(),Toast.LENGTH_LONG).show();
            return sdDir.toString();
        }

        return null;
    }
}
