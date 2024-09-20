package com.example.heavymetals.Home_LandingPage;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.heavymetals.R;

import org.json.JSONException;
import org.json.JSONObject;

public class nav_header extends Fragment {

    private static final String TAG = "nav_header";
    private String url = "https://heavymetals.scarlet2.io/HeavyMetals/email_retrieve.php"; // Use HTTPS and ensure URL is correct

    private TextView Username, Email;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.nav_header, container, false);

        Username = view.findViewById(R.id.Menu_User_Firstname);
        Email = view.findViewById(R.id.Menu_User_Email);

        // Retrieve the email passed from MainActivity
        Bundle arguments = getArguments();
        if (arguments != null) {
            String userEmail = arguments.getString("user_email", "Default Email");

            // Set the email in the TextView
            Email.setText(userEmail);
        }

        return view;
    }


    private void fetchUserData() {
        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "Response: " + response.toString()); // Log the full response

                        try {
                            if (response.has("error")) {
                                Log.e(TAG, "Error: " + response.getString("error"));
                                return;
                            }

                            String firstName = response.optString("first_name", "Default Name");
                            String email = response.optString("email", "Default Email");

                            // Debug logs
                            Log.d(TAG, "Setting Username to: " + firstName);
                            Log.d(TAG, "Setting Email to: " + email);

                            if (Username != null && Email != null) {
                                // Set the data in the TextViews
                                Username.setText(firstName);
                                Email.setText(email);
                            } else {
                                Log.e(TAG, "TextViews are null. Unable to update.");
                            }
                        } catch (JSONException e) {
                            Log.e(TAG, "Error parsing JSON response", e);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.networkResponse != null) {
                    Log.e(TAG, "HTTP Status Code: " + error.networkResponse.statusCode);
                }
                Log.e(TAG, "Error: " + error.toString());
                error.printStackTrace();
            }
        });

        requestQueue.add(jsonObjectRequest);
    }
}
