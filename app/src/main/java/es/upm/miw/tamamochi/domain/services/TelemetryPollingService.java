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
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.List;

import es.upm.miw.tamamochi.MainActivity;
import es.upm.miw.tamamochi.R;
import es.upm.miw.tamamochi.domain.model.Environment;
import es.upm.miw.tamamochi.domain.model.TamamochiViewModel;
import es.upm.miw.tamamochi.domain.model.pojos.measurements.Co2;
import es.upm.miw.tamamochi.domain.model.pojos.measurements.Humidity;
import es.upm.miw.tamamochi.domain.model.pojos.measurements.Light;
import es.upm.miw.tamamochi.domain.model.pojos.measurements.Temperature;
import es.upm.miw.tamamochi.domain.services.device.ISpikeRESTAPIService;
import es.upm.miw.tamamochi.domain.model.CharacterStatus;
import es.upm.miw.tamamochi.domain.model.pojos.measurements.Measurement;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TelemetryPollingService extends Service {
    static final String TAG = "MiW - MeasurementService";

    private TamamochiViewModel tamamochiViewModel;

    private final LocalBinder mBinder = new LocalBinder();

    private ISpikeRESTAPIService apiService;
    private static final String API_LOGIN_POST = "https://thingsboard.cloud/api/auth/"; // Base url to obtain token
    private static final String API_BASE_GET = "https://thingsboard.cloud:443/api/plugins/telemetry/DEVICE/"; // Base url to obtain data
    private static final String TOKEN = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJzdHVkZW50dXBtMjAyMkBnbWFpbC5jb20iLCJ1c2VySWQiOiI4NDg1OTU2MC00NzU2LTExZWQtOTQ1YS1lOWViYTIyYjlkZjYiLCJzY29wZXMiOlsiVEVOQU5UX0FETUlOIl0sImlzcyI6InRoaW5nc2JvYXJkLmNsb3VkIiwiaWF0IjoxNjY4MDg1MDUxLCJleHAiOjE2NjgxMTM4NTEsImZpcnN0TmFtZSI6IlN0dWRlbnQiLCJsYXN0TmFtZSI6IlVQTSIsImVuYWJsZWQiOnRydWUsImlzUHVibGljIjpmYWxzZSwiaXNCaWxsaW5nU2VydmljZSI6ZmFsc2UsInByaXZhY3lQb2xpY3lBY2NlcHRlZCI6dHJ1ZSwidGVybXNPZlVzZUFjY2VwdGVkIjp0cnVlLCJ0ZW5hbnRJZCI6ImUyZGQ2NTAwLTY3OGEtMTFlYi05MjJjLWY3NDAyMTlhYmNiOCIsImN1c3RvbWVySWQiOiIxMzgxNDAwMC0xZGQyLTExYjItODA4MC04MDgwODA4MDgwODAifQ.7R453K4QBkVGfZ4Cv3L7Wy4EPsSkcqpVhshZZFUTtWSTNrRYD6A0E8hikJ8gG6MM9eMiToqBMByBCW9XY3Q66Q";
    private static final String BEARER_TOKEN = "Bearer " + TOKEN;
    private static final String DEVICE_ID = "cf87adf0-dc76-11ec-b1ed-e5d3f0ce866e";
    private static final String USER_THB = "studentupm2022@gmail.com";
    private static final String PASS_THB = "student";

    private Handler handler;
    public static final long DEFAULT_SYNC_INTERVAL = 60 * 1000;

    private final Runnable runnableService = new Runnable() {
        @Override
        public void run() {
            getLastTelemetry().enqueue(new Callback<Measurement>() {
                @Override
                public void onResponse(Call<Measurement> call, Response<Measurement> response) {
                    Measurement lm = response.body();
                    if(lm != null) {
                        Environment env = parseEnvironment(lm);
                        tamamochiViewModel.setEnvironment(env);
                    }
                }

                @Override
                public void onFailure(Call<Measurement> call, Throwable t) {
                    Log.e(TAG, " error message: "+t.getMessage());
                }
            });
            handler.postDelayed(runnableService, DEFAULT_SYNC_INTERVAL);
        }
    };

    private Environment parseEnvironment(Measurement measurement) {
        Temperature temp = measurement.getTemperature().get(0);
        Humidity hum = measurement.getHumidity().get(0);
        Co2 co2 = measurement.getCo2().get(0);
        Light light = measurement.getLight().get(0);
        return Environment.builder()
                .temperature(Double.parseDouble(temp.getValue()))
                .humidity(Double.parseDouble(hum.getValue()))
                .co2(Double.parseDouble(co2.getValue()))
                .light(Double.parseDouble(light.getValue()))
                .build();
    }

    public Call<Measurement> getLastTelemetry() {
        Measurement lm;

        //https://thingsboard.cloud:443/api/plugins/telemetry/DEVICE/{{deviceId}}/values/timeseries?keys=co2&useStrictDataTypes=false
        String keys = "co2,humidity,light,soilTemp1,soilTemp2,temperature";
        String useStrictDataTypes = "false";

        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(httpLoggingInterceptor)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .client(client)
                .baseUrl(API_BASE_GET)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ISpikeRESTAPIService iApi = retrofit.create(ISpikeRESTAPIService.class);
        Log.i(TAG, " request params: |"+ BEARER_TOKEN +"|"+ DEVICE_ID +"|"+keys+"|"+useStrictDataTypes);
        return iApi.getLastTelemetry(BEARER_TOKEN, DEVICE_ID, keys, useStrictDataTypes);
    }

    public class LocalBinder extends Binder {
        public TelemetryPollingService getService() {
            return TelemetryPollingService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
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
    public int onStartCommand(Intent intent, int flags, int startId) {
        tamamochiViewModel = TamamochiViewModel.getInstance();
        handler = new Handler();
        handler.post(runnableService);
        return android.app.Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction("restartservice");
        broadcastIntent.setClass(this, ServiceRestarter.class);
        this.sendBroadcast(broadcastIntent);
    }
}
