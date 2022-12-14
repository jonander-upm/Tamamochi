package es.upm.miw.tamamochi.domain.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

public class ServiceRestarter extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("Broadcast Listened", "Service tried to stop");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(new Intent(context, TamamochiPollingService.class));
            context.startForegroundService(new Intent(context, TamamochiNotificationManager.class));
        } else {
            context.startService(new Intent(context, TamamochiPollingService.class));
            context.startService(new Intent(context, TamamochiNotificationManager.class));
        }
    }
}
