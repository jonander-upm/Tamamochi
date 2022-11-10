package es.upm.miw.tamamochi.domain.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.lifecycle.LifecycleService;
import androidx.lifecycle.Observer;

import java.util.List;

import es.upm.miw.tamamochi.MainActivity;
import es.upm.miw.tamamochi.R;
import es.upm.miw.tamamochi.domain.model.CharacterStatus;
import es.upm.miw.tamamochi.domain.model.TamamochiViewModel;

public class TamamochiNotificationManager extends LifecycleService {
    static final String CHANNEL_ID = "12345";
    static final int NOTIFICATION_ID = 12345;

    NotificationManagerCompat nm;
    NotificationCompat.Builder nBuilder;

    TamamochiViewModel tamamochiViewModel;

    private final TamamochiNotificationManager.LocalBinder mBinder = new TamamochiNotificationManager.LocalBinder();

    public class LocalBinder extends Binder {
        public TamamochiNotificationManager getService() {
            return TamamochiNotificationManager.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        super.onBind(intent);
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        setupStatusNotifications();
    }

    private void setupStatusNotifications() {
        Intent tapIntent = new Intent(this, MainActivity.class);
        tapIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, tapIntent, PendingIntent.FLAG_IMMUTABLE);
        nm = NotificationManagerCompat.from(getApplicationContext());
        nBuilder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(getString(R.string.txtNotifAssistanceNeededTitle))
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Status Notification Channel",
                    NotificationManager.IMPORTANCE_DEFAULT);
            nm.createNotificationChannel(channel);
            nBuilder.setChannelId(CHANNEL_ID);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        tamamochiViewModel = TamamochiViewModel.getInstance();
        setupForegroundService();
        setupListeners();
        return Service.START_STICKY;
    }

    private void setupForegroundService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String NOTIFICATION_CHANNEL_ID = "permanence";
            String channelName = "Background Service";
            NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
            chan.setLightColor(Color.BLUE);
            chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            assert manager != null;
            manager.createNotificationChannel(chan);

            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
            Notification notification = notificationBuilder.setOngoing(true)
                    .setContentTitle(getString(R.string.txtAppRunningInBackground))
                    .setPriority(NotificationCompat.PRIORITY_MIN)
                    .setCategory(Notification.CATEGORY_SERVICE)
                    .build();
            startForeground(2, notification);
        } else {
            startForeground(1, new Notification());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction("restartservice");
        broadcastIntent.setClass(this, ServiceRestarter.class);
        this.sendBroadcast(broadcastIntent);
    }

    public void setupListeners() {
        tamamochiViewModel.getCharacterStatusList().observe(this, new Observer<List<CharacterStatus>>() {
            @Override
            public void onChanged(List<CharacterStatus> characterStatusList) {
                for(CharacterStatus statusItem : characterStatusList) {
                    nBuilder.setContentText(getString(statusItem.getIssueStringId()));
                    nm.notify(NOTIFICATION_ID, nBuilder.build());
                }
            }
        });
    }
}
