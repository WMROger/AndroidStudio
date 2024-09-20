package com.example.heavymetals.Home_LandingPage;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.heavymetals.Models.API;
import com.example.heavymetals.Models.CurrentTimeResponse;
import com.example.heavymetals.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HomeFragment extends Fragment {

    private TextView textViewCurrentTime;
    private API apiService;
    private final Handler handler = new Handler();
    private Runnable runnable;

    // Replace onCreate with onCreateView for fragment initialization
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        textViewCurrentTime = view.findViewById(R.id.textDate); // Ensure this ID exists in your fragment_home.xml

        // Initialize Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://heavymetals.scarlet2.io/HeavyMetals/miscellaneous/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(API.class);

        // Fetch the time from server once and start local updating
        getCurrentTimeFromServer();

        return view; // Return the view for the fragment
    }

    private void getCurrentTimeFromServer() {
        Call<CurrentTimeResponse> call = apiService.getCurrentTime();
        call.enqueue(new Callback<CurrentTimeResponse>() {
            @Override
            public void onResponse(Call<CurrentTimeResponse> call, Response<CurrentTimeResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String serverTime = response.body().getCurrentTime();
                    // Set the text to your TextView
                    textViewCurrentTime.setText(serverTime);
                    // Start local time updates if needed
                    startLocalTimeUpdate(serverTime);
                } else {
                    Toast.makeText(getActivity(), "Failed to retrieve time", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CurrentTimeResponse> call, Throwable t) {
                Log.e("API Error", t.getMessage(), t);
                Toast.makeText(getActivity(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void startLocalTimeUpdate(String serverTime) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm a", Locale.US);

        try {
            Date parsedServerTime = dateFormat.parse(serverTime);
            if (parsedServerTime != null) {
                final long[] serverTimeMillis = {parsedServerTime.getTime()};

                runnable = new Runnable() {
                    @Override
                    public void run() {
                        serverTimeMillis[0] += 1000; // Increment by 1 second
                        Date updatedTime = new Date(serverTimeMillis[0]);
                        String currentTime = dateFormat.format(updatedTime);
                        textViewCurrentTime.setText(currentTime); // Update TextView
                        handler.postDelayed(this, 1000); // Repeat every second
                    }
                };

                handler.post(runnable); // Start the update
            } else {
                Log.e("Time Parse Error", "Parsed time is null.");
            }
        } catch (Exception e) {
            Log.e("Time Parse Error", e.getMessage());
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Stop the updates when the fragment is destroyed
        handler.removeCallbacks(runnable);
    }
}
