package es.upm.miw.tamamochi.domain.services.device;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class TokenInterceptor implements Interceptor {

    private String TOKEN = "";

    public TokenInterceptor(String TOKEN) {
        this.TOKEN = TOKEN;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {

        Request newRequest = chain.request().newBuilder()
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .header("Authorization","Bearer "+TOKEN)
                .build();

        return chain.proceed(newRequest);
    }
}