package com.example.heavymetals.Home_LandingPage;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.heavymetals.R;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class ProfileFragment extends Fragment {

    private TextView firstNameTextView;
    private TextView emailTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize the TextViews
        firstNameTextView = view.findViewById(R.id.Menu_User_Firstname);
        emailTextView = view.findViewById(R.id.Menu_User_Email);

        // Fetch user email from SharedPreferences
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String userEmail = sharedPreferences.getString("loggedInUser", null);
        if (userEmail != null) {
            // Fetch user details from the database
            fetchUserDetails(userEmail);
        } else {
            Toast.makeText(getActivity(), "No logged-in user found.", Toast.LENGTH_SHORT).show();
        }

        return view;
    }

    // Fetch the user details from the server (asynchronous network request)
    private void fetchUserDetails(String email) {
        new Thread(() -> {
            try {
                URL url = new URL("https://heavymetals.scarlet2.io/HeavyMetals/get_username.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);

                // Send the POST data with the actual user email
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                String postData = "email=" + URLEncoder.encode(email, "UTF-8");
                writer.write(postData);
                writer.flush();
                writer.close();
                os.close();

                // Get the response
                InputStream is = conn.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                // Parse the response JSON
                JSONObject jsonResponse = new JSONObject(response.toString());
                boolean success = jsonResponse.getBoolean("success");
                if (success) {
                    String firstName = jsonResponse.getString("first_name");
                    String lastName = jsonResponse.getString("last_name");

                    // Update the UI with the user's name on the main thread
                    getActivity().runOnUiThread(() -> {
                        firstNameTextView.setText(firstName + " " + lastName);
                        emailTextView.setText(email);
                    });
                } else {
                    String message = jsonResponse.getString("message");
                    getActivity().runOnUiThread(() ->
                            Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show());
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
