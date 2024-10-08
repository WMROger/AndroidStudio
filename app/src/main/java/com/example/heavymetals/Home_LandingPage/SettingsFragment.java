package com.example.heavymetals.Home_LandingPage;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.heavymetals.Login_RegisterPage.AuthenticationActivity;
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
import android.content.SharedPreferences;


public class SettingsFragment extends Fragment {
    Button Settings_LogoutButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        // Initialize the logout button
        Settings_LogoutButton = view.findViewById(R.id.Settings_LogoutButton);

        // Set the click listener for the logout button
        Settings_LogoutButton.setOnClickListener(v -> logoutUser());

        return view;
    }

    // Log out the user and clear their session
    private void logoutUser() {
        // Retrieve the session token from SharedPreferences
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String sessionToken = sharedPreferences.getString("auth_token", null); // Retrieve the saved session token

        if (sessionToken != null) {
            // Make a network request to logout from the server
            new Thread(() -> {
                try {
                    URL url = new URL("https://heavymetals.scarlet2.io/HeavyMetals/logout.php"); // URL to your logout.php script
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setDoOutput(true);

                    // Send the POST data with the session token
                    OutputStream os = conn.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                    String postData = "session_token=" + URLEncoder.encode(sessionToken, "UTF-8");
                    writer.write(postData);
                    writer.flush();
                    writer.close();
                    os.close();

                    // Log the response code for debugging
                    int responseCode = conn.getResponseCode();
                    if (responseCode != HttpURLConnection.HTTP_OK) {
                        throw new Exception("HTTP error code: " + responseCode);
                    }

                    // Get the response from the server
                    InputStream is = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();

                    // Parse the server response
                    JSONObject jsonResponse = new JSONObject(response.toString());
                    int success = jsonResponse.getInt("success");

                    if (success == 1 || jsonResponse.getString("message").contains("Session token is required")) {
                        // If logout was successful or if the token was already invalid, clear the local session
                        requireActivity().runOnUiThread(() -> {
                            // Clear the local session
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.clear(); // Clear all saved preferences including loggedInUser and auth_token
                            editor.apply();

                            // Inform the user about the logout
                            Toast.makeText(requireActivity(), "Logged out successfully!", Toast.LENGTH_SHORT).show();

                            // Redirect to login
                            redirectToLogin();
                        });
                    } else {
                        // If logout failed on the server, show an error message
                        String message = jsonResponse.getString("message");
                        requireActivity().runOnUiThread(() -> {
                            Toast.makeText(requireActivity(), "Logout failed: " + message, Toast.LENGTH_LONG).show();
                        });
                    }

                } catch (Exception e) {
                    // Handle any exceptions during the request and log them
                    e.printStackTrace();
                    requireActivity().runOnUiThread(() -> Toast.makeText(requireActivity(), "Error during logout request: " + e.getMessage(), Toast.LENGTH_LONG).show());
                }
            }).start();
        } else {
            // If there's no session token, just clear the local session and redirect to login
            requireActivity().runOnUiThread(() -> {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear(); // Clear all saved preferences including loggedInUser and auth_token
                editor.apply();

                // Inform the user about the logout
                Toast.makeText(requireActivity(), "Logged out successfully!", Toast.LENGTH_SHORT).show();

                // Redirect to the AuthenticationActivity
                redirectToLogin();
            });
        }
    }

    // Method to redirect to the AuthenticationActivity (login)
    private void redirectToLogin() {
        Intent intent = new Intent(requireActivity(), AuthenticationActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
