package es.upm.miw.tamamochi.domain.services;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import java.util.List;

import es.upm.miw.tamamochi.domain.model.Environment;
import es.upm.miw.tamamochi.domain.model.TamamochiViewModel;
import es.upm.miw.tamamochi.domain.services.device.IWeatherRESTAPIService;
import es.upm.miw.tamamochi.domain.model.pojos.weather.ExternalWeather;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ExternalWeatherService extends Service implements LocationListener {
    static final String TAG = "MiW - ExternalWeatherService";
    static final String API_BASE = "https://api.openweathermap.org/data/2.5/";
    static final String API_KEY = "c52e71938484116d72f2b24d8fe540ce";

    private TamamochiViewModel tamamochiViewModel;

    private final ExternalWeatherService.LocalBinder mBinder = new ExternalWeatherService.LocalBinder();

    private Handler handler;
    public static final long DEFAULT_SYNC_INTERVAL = 10 * 1000;

    LocationManager locationManager;
    Location location;

    private final Runnable runnableService = new Runnable() {
        @Override
        public void run() {
            if (location != null) {
                getCurrentWeatherData().enqueue(new Callback<ExternalWeather>() {
                    @Override
                    public void onResponse(Call<ExternalWeather> call, Response<ExternalWeather> response) {
                        ExternalWeather ew = response.body();
                        if (ew != null) {
                            Log.i(TAG, "Current weather: " + ew.getWeather().get(0).getDescription());
                            Environment env = tamamochiViewModel.getEnvironment().getValue();
                            if(env != null) {
                                env.setWeatherIcon(ew.getWeather().get(0).getIcon());
                                tamamochiViewModel.setEnvironment(env);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ExternalWeather> call, Throwable t) {
                        Log.e(TAG, " error message: " + t.getMessage());
                    }
                });
            }
            handler.postDelayed(runnableService, DEFAULT_SYNC_INTERVAL);
        }
    };

    public Call<ExternalWeather> getCurrentWeatherData() {
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(httpLoggingInterceptor)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .client(client)
                .baseUrl(API_BASE)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        IWeatherRESTAPIService iApi = retrofit.create(IWeatherRESTAPIService.class);
        return iApi.getCurrentWeather(location.getLatitude(), location.getLongitude(), API_KEY);
    }

    @Override
    public void onLocationChanged(@NonNull Location newLocation) {
        this.location = newLocation;
    }

    @Override
    public void onLocationChanged(@NonNull List<Location> locations) {
        LocationListener.super.onLocationChanged(locations);
    }

    @Override
    public void onFlushComplete(int requestCode) {
        LocationListener.super.onFlushComplete(requestCode);
    }

    public class LocalBinder extends Binder {
        public ExternalWeatherService getService() {
            return ExternalWeatherService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        tamamochiViewModel = TamamochiViewModel.getInstance();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0L, 0.0F, this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        handler = new Handler();
        handler.post(runnableService);
        return android.app.Service.START_STICKY;
    }
}
