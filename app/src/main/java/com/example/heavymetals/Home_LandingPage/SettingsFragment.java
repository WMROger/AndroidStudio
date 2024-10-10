package com.example.heavymetals.Home_LandingPage;

import static android.content.Context.MODE_PRIVATE;

import android.app.AlertDialog;
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
    Button Settings_LogoutButton,delete_account_btn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        // Initialize the logout button
        Settings_LogoutButton = view.findViewById(R.id.Settings_LogoutButton);
        delete_account_btn = view.findViewById(R.id.Delete_account); // Assuming this is in your layout

        // Set the click listener for the logout button
        Settings_LogoutButton.setOnClickListener(v -> logoutUser());
        delete_account_btn.setOnClickListener(v -> confirmAccountDeletion());

        return view;
    }
    // Show a confirmation dialog to delete the account
    private void confirmAccountDeletion() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Delete Account")
                .setMessage("Are you sure you want to delete your account? This action cannot be undone.")
                .setPositiveButton("Delete", (dialog, which) -> deleteUserAccount()) // Confirm button
                .setNegativeButton("Cancel", null) // Cancel button
                .show();
    }

    // Delete the user account from the server
    private void deleteUserAccount() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String sessionToken = sharedPreferences.getString("auth_token", null); // Retrieve session token
        String userIdString = sharedPreferences.getString("user_id", null); // Retrieve user ID as a String

        if (sessionToken != null && userIdString != null) {
            try {
                int userId = Integer.parseInt(userIdString); // Convert the user ID to an integer

                // Make a network request to delete the user account
                new Thread(() -> {
                    try {
                        URL url = new URL("https://heavymetals.scarlet2.io/HeavyMetals/profile/api_delete_account.php/"); // URL to your new PHP script
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        conn.setRequestMethod("POST"); // Ensure that POST method is being used
                        conn.setDoOutput(true);         // This is important for POST requests

                        // Send the POST data with the session token and user ID
                        OutputStream os = conn.getOutputStream();
                        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                        String postData = "user_id=" + URLEncoder.encode(String.valueOf(userId), "UTF-8")
                                + "&session_token=" + URLEncoder.encode(sessionToken, "UTF-8");
                        writer.write(postData);
                        writer.flush();
                        writer.close();
                        os.close();

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
                        boolean success = jsonResponse.getBoolean("success");

                        if (success) {
                            requireActivity().runOnUiThread(() -> {
                                Toast.makeText(requireActivity(), "Account deleted successfully.", Toast.LENGTH_SHORT).show();
                                clearSessionAndRedirectToLogin();
                            });
                        } else {
                            String message = jsonResponse.getString("message");
                            requireActivity().runOnUiThread(() -> {
                                Toast.makeText(requireActivity(), "Failed to delete account: " + message, Toast.LENGTH_LONG).show();
                            });
                        }

                    } catch (Exception e) {
                        // Handle any exceptions during the request
                        e.printStackTrace();
                        requireActivity().runOnUiThread(() ->
                                Toast.makeText(requireActivity(), "Error during account deletion request: " + e.getMessage(), Toast.LENGTH_LONG).show()
                        );
                    }
                }).start();

            } catch (NumberFormatException e) {
                // Handle the case where user_id is not a valid integer
                Toast.makeText(requireActivity(), "Invalid user ID format", Toast.LENGTH_SHORT).show();
            }

        } else {
            Toast.makeText(requireActivity(), "Session not found. Please log in again.", Toast.LENGTH_SHORT).show();
            redirectToLogin();
        }
    }



    // Clear session data and redirect to login page after account deletion
    private void clearSessionAndRedirectToLogin() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("UserPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear(); // Clear all saved preferences
        editor.apply();

        // Redirect to login activity
        redirectToLogin();
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