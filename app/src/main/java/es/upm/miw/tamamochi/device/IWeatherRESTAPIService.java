package es.upm.miw.tamamochi.device;

import es.upm.miw.tamamochi.domain.model.pojos.weather.ExternalWeather;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface IWeatherRESTAPIService {
    @GET("weather")
    Call<ExternalWeather> getCurrentWeather(@Query("lat") Double lat, @Query("lon") Double lon, @Query("appid") String appid);
}
