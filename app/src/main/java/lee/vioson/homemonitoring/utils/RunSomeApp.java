package lee.vioson.homemonitoring.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import java.util.List;

/**
 * Author:李烽
 * Date:2016-01-20
 * FIXME
 * Todo 启动其他apk的app
 */
public class RunSomeApp {
    public static void runApp(Context context, String packageName) {
        PackageInfo info;
        try {
            info = context.getPackageManager().getPackageInfo(packageName, 0);
            Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
            resolveIntent.setPackage(info.packageName);
            PackageManager packageManager = context.getPackageManager();
            List<ResolveInfo> apps = packageManager.queryIntentActivities(resolveIntent, 0);
            ResolveInfo resolveInfo = apps.iterator().next();
            if (resolveInfo != null) {
                packageName = resolveInfo.activityInfo.packageName;
                String className = resolveInfo.activityInfo.name;
                Intent intent = new Intent(Intent.ACTION_MAIN);
                ComponentName cn = new ComponentName(packageName, className);
                intent.setComponent(cn);
                context.startActivity(intent);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }
}
