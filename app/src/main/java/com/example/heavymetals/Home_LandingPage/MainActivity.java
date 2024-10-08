package com.example.heavymetals.Home_LandingPage;

import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.heavymetals.Home_LandingPage.Profile.ProfileFragment;
import com.example.heavymetals.Home_LandingPage.Tracker.ProgressFragment;
import com.example.heavymetals.Home_LandingPage.Workouts.WorkoutModule1;
import com.example.heavymetals.Login_RegisterPage.AuthenticationActivity;
import com.example.heavymetals.R;
import com.example.heavymetals.network.ScheduleDailyNotification;
import com.google.android.material.navigation.NavigationView;

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

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private TextView emailTextView;
    private TextView firstNameTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Toolbar and DrawerLayout
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);




        ScheduleDailyNotification notificationScheduler = new ScheduleDailyNotification();
        notificationScheduler.scheduleDailyNotification(this);  // `this` refers to the context (in this case, the activity context)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            if (!alarmManager.canScheduleExactAlarms()) {
                // Prompt the user to enable the exact alarm permission via system settings
                Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                startActivity(intent);
            }
        }


        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Set up drawer toggle with toolbar
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.custom_orange));

        // Initialize NavigationView header and TextViews
        View headerView = navigationView.getHeaderView(0);
        emailTextView = headerView.findViewById(R.id.Menu_User_Email);
        firstNameTextView = headerView.findViewById(R.id.Menu_User_Firstname);

        // Retrieve the user email from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String userEmail = sharedPreferences.getString("loggedInUser", null);
        if (userEmail == null) {
            // No logged-in user, redirect to AuthenticationActivity
            redirectToLogin();
            return;  // Stop further execution
        }

        // Set email in Navigation header
        emailTextView.setText(userEmail);

        // Open HomeFragment as default if savedInstanceState is null
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_home);
        }

        // Fetch user details from the server
        fetchUserDetails(userEmail);
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
                    runOnUiThread(() -> {
                        firstNameTextView.setText(firstName + " " + lastName);
                    });
                } else {
                    String message = jsonResponse.getString("message");
                    runOnUiThread(() -> Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show());
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.nav_profile) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment()).commit();
        } else if (itemId == R.id.nav_home) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
        } else if (itemId == R.id.nav_progress) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProgressFragment()).commit();
        } else if (itemId == R.id.nav_exercise) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new WorkoutModule1()).commit();
        } else if (itemId == R.id.nav_settings) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SettingsFragment()).commit();
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }


    private void redirectToLogin() {
        // This should always run on the main thread
        Intent intent = new Intent(MainActivity.this, AuthenticationActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);  // Clear the activity stack
        startActivity(intent);
        finish();  // Close MainActivity
    }


    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        // Save email to savedInstanceState
        String email = emailTextView.getText().toString();
        outState.putString("user_email", email);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
