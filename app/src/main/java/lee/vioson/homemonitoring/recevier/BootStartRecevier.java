package lee.vioson.homemonitoring.recevier;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import lee.vioson.homemonitoring.MainActivity;
import lee.vioson.homemonitoring.service.RecordService;

/**
 * Author:李烽
 * Date:2016-01-18
 * FIXME
 * Todo
 */
public class BootStartRecevier extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
//        Intent i = new Intent(context, MainActivity.class);
//        context.startActivity(i);
        Intent intent1 = new Intent(context.getApplicationContext(), RecordService.class);
        context.getApplicationContext().startService(intent1);
    }
}
