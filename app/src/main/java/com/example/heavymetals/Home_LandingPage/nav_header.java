package com.example.heavymetals.Home_LandingPage;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.heavymetals.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class nav_header extends AppCompatActivity {

    TextView usernameTextView;
    private String email;  // Assuming you retrieve email from login or saved state

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nav_header);

        usernameTextView = findViewById(R.id.Menu_User_Firstname);

        // Retrieve email from SharedPreferences instead of getIntent()
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        email = sharedPreferences.getString("user_email", null);  // Get the email

        if (email != null) {
            // Fetch and display the username
            fetchUsername(email);

        } else {
            Toast.makeText(this, "Email not found in shared preferences", Toast.LENGTH_SHORT).show();
        }
    }

    // Reusable method for handling the response
    public void onResponse(String response) {
        Log.d("Response", "Server Response: " + response);
        try {
            JSONObject jsonResponse = new JSONObject(response);
            if (jsonResponse.getBoolean("success")) {
                String firstName = jsonResponse.getString("first_name");
                String lastName = jsonResponse.getString("last_name");
                String fullName = firstName + " " + lastName;

                // Set username in TextView
                usernameTextView.setText(fullName);
                Log.d("Response", "User: " + fullName);
            } else {
                Toast.makeText(nav_header.this, "User not found", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("Response", "Error parsing JSON");
        }
    }

    private void fetchUsername(String email) {
        if (email == null) {
            Toast.makeText(this, "Email is null", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d("nav_header", "Email received: " + email);

        String url = "https://heavymetals.scarlet2.io/HeavyMetals/get_username.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Instead of handling response here, pass it to the reusable onResponse method
                        onResponse(response);
                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Toast.makeText(nav_header.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("email", email);  // Send the user's email to the PHP script
                Log.d("nav_header", "Params sent: " + params.toString());
                return params;
            }
        };

        // Add the request to the RequestQueue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}
