package es.upm.miw.tamamochi.domain.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
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
import androidx.lifecycle.LifecycleService;
import androidx.lifecycle.Observer;

import java.util.List;

import es.upm.miw.tamamochi.R;
import es.upm.miw.tamamochi.domain.model.Character;
import es.upm.miw.tamamochi.domain.model.CharacterAge;
import es.upm.miw.tamamochi.domain.model.CharacterStatus;
import es.upm.miw.tamamochi.domain.model.TamamochiViewModel;
import es.upm.miw.tamamochi.domain.model.pojos.measurements.AuthorizationBearer;
import es.upm.miw.tamamochi.domain.model.pojos.measurements.Credentials;
import es.upm.miw.tamamochi.domain.services.device.ISpikeRESTAPIService;
import es.upm.miw.tamamochi.domain.model.pojos.measurements.Measurement;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TamamochiPollingService extends LifecycleService {
    static final String TAG = "MiW - MeasurementService";

    private TamamochiViewModel tamamochiViewModel;

    private Character character;

    private final LocalBinder mBinder = new LocalBinder();

    private static final String API_LOGIN_POST = "https://thingsboard.cloud/api/auth/"; // Base url to obtain token
    private static final String API_BASE_GET = "https://thingsboard.cloud:443/api/plugins/telemetry/DEVICE/"; // Base url to obtain data
    private static final String BEARER_TOKEN = "Bearer ";
    private static final String DEVICE_ID = "cf87adf0-dc76-11ec-b1ed-e5d3f0ce866e";
    private static final String USER_THB = "studentupm2022@gmail.com";
    private static final String PASS_THB = "student";

    private String token;

    private Handler handler;
    public static final long DEFAULT_SYNC_INTERVAL = 60 * 1000;

    private final Runnable runnableService = new Runnable() {
        @Override
        public void run() {
            getLastTelemetry();
            handler.postDelayed(runnableService, DEFAULT_SYNC_INTERVAL);
        }
    };

    public void getToken() {
        Credentials credentials = new Credentials();
        credentials.setUsername(USER_THB);
        credentials.setPassword(PASS_THB);

        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(httpLoggingInterceptor)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .client(client)
                .baseUrl(API_LOGIN_POST)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ISpikeRESTAPIService iApi = retrofit.create(ISpikeRESTAPIService.class);
        iApi.postAuthorizationBearer(credentials).enqueue(new Callback<AuthorizationBearer>() {
            @Override
            public void onResponse(Call<AuthorizationBearer> call, Response<AuthorizationBearer> response) {
                AuthorizationBearer authorizationBearer = response.body();
                if(authorizationBearer != null) {
                    token = BEARER_TOKEN + authorizationBearer.getToken();
                }
            }

            @Override
            public void onFailure(Call<AuthorizationBearer> call, Throwable t) {

            }
        });
    }

    public void getLastTelemetry() {
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
        iApi.getLastTelemetry(token, DEVICE_ID, keys, useStrictDataTypes).enqueue(new Callback<Measurement>() {
            @Override
            public void onResponse(Call<Measurement> call, Response<Measurement> response) {
                Measurement lm = response.body();
                if(lm != null) {
                    tamamochiViewModel.setInternalEnvironment(lm);
                    handleLifeDrain(CharacterStatus.getCharacterStatusList(lm));
                } else if(response.code() == 401) {
                    getToken();
                }
            }

            @Override
            public void onFailure(Call<Measurement> call, Throwable t) {
                Log.e(TAG, " error message: "+t.getMessage());
            }
        });
    }

    public class LocalBinder extends Binder {
        public TamamochiPollingService getService() {
            return TamamochiPollingService.this;
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
        getToken();
        tamamochiViewModel = TamamochiViewModel.getInstance();
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
        tamamochiViewModel.getCharacter().observe(this, new Observer<Character>() {
            @Override
            public void onChanged(Character observerCharacter) {
                character = observerCharacter;
            }
        });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        handler = new Handler();
        handler.postDelayed(runnableService, 1000);
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction("restartservice");
        broadcastIntent.setClass(this, ServiceRestarter.class);
        this.sendBroadcast(broadcastIntent);
    }

    private void handleLifeDrain(List<CharacterStatus> characterStatusList) {
        if(character != null) {
            int characterLife = character.getLife();
            if(characterLife > 0 && CharacterAge.getCharacterAge(character.getCharacterBirthDate()) != CharacterAge.DEAD) {
                CharacterAge characterAge = CharacterAge.getCharacterAge(character.getCharacterBirthDate());
                for (CharacterStatus status : characterStatusList) {
                    characterLife -= status.getLifeDrainPerMinute() * characterAge.getDrainMultiplier();
                }
                tamamochiViewModel.setCharacter(character);
            } else {
                characterLife = 0;
                character.setAlive(false);
            }
            character.setLife(characterLife);
            tamamochiViewModel.setCharacter(character);
        }
    }
}
