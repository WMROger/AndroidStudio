package com.example.heavymetals.network;

import android.content.Context;
import android.content.SharedPreferences;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;

public class RetrofitClient {
    private static final String BASE_URL = "https://heavymetals.scarlet2.io/";
    private static Retrofit retrofit;

    // This method retrieves the Retrofit instance with the Authorization header interceptor
    public static Retrofit getClient(Context context) {
        if (retrofit == null) {
            // Create an OkHttpClient with an Interceptor to add the Authorization header
            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
            httpClient.addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request originalRequest = chain.request();

                    // Retrieve the auth_token from SharedPreferences
                    SharedPreferences sharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
                    String authToken = sharedPreferences.getString("auth_token", null);

                    if (authToken != null) {
                        // Add the Authorization header to the request
                        Request newRequest = originalRequest.newBuilder()
                                .header("Authorization", "Bearer " + authToken) // Add the token here
                                .build();
                        return chain.proceed(newRequest);
                    }

                    // If no token, proceed with the original request
                    return chain.proceed(originalRequest);
                }
            });

            // Build Retrofit with OkHttpClient (with interceptor) and Gson converter
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(httpClient.build())  // Attach the OkHttpClient with the interceptor
                    .addConverterFactory(GsonConverterFactory.create())  // Converts JSON into Java objects
                    .build();
        }
        return retrofit;
    }
}
