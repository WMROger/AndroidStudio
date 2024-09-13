package com.example.navigationdrawer.Heavy_Metals.Login_RegisterPage.RegisterPage;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import android.os.AsyncTask;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.navigationdrawer.Heavy_Metals.Login_RegisterPage.LoginActivity;
import com.example.navigationdrawer.R;

public class RegisterActivity extends AppCompatActivity {

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        EditText Signup_Email = findViewById(R.id.Signup_Email);
        EditText Signup_Password = findViewById(R.id.Signup_Password);
        EditText Signup_PasswordConfirmation = findViewById(R.id.Signup_PasswordConfirmation);
        Button btnDone = findViewById(R.id.btnDone);
        TextView SignupLoginTxt = findViewById(R.id.SignUpLoginTxt);






        //Done button function
        btnDone.setOnClickListener(v -> {
            // Replace "new_username" and "new_password" with actual user input values
            String username = Signup_Email.getText().toString().trim();
            String password = Signup_Password.getText().toString().trim();
            String passwordConfirmation = Signup_PasswordConfirmation.getText().toString().trim();

            // Check if passwords match
            if (!password.equals(passwordConfirmation)) {
                Toast.makeText(RegisterActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            } else if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(RegisterActivity.this, "Username and password cannot be empty", Toast.LENGTH_SHORT).show();
            } else {
                // Start the registration task
                new RegisterUserTask(this, username, password).execute();
            }
        });

        //Login button/text function
        SignupLoginTxt.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        SignupLoginTxt.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    SignupLoginTxt.setTextColor(getResources().getColor(R.color.black));
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    SignupLoginTxt.setTextColor(getResources().getColor(R.color.white));
                    break;
            }
            return false;
        });


    }

    public class RegisterUserTask extends AsyncTask<Void, Void, String> {
        private String username, password;
        private static final String REGISTER_URL = "https://heavymetals.scarlet2.io/register_user.php";

        public RegisterUserTask(RegisterActivity registerActivity, String username, String password) {
            this.username = username;
            this.password = password;
        }

        @Override
        protected String doInBackground(Void... voids) {
            String result = "";
            try {
                URL url = new URL(REGISTER_URL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setDoInput(true);

                String postData = "username=" + URLEncoder.encode(username, "UTF-8") +
                        "&password=" + URLEncoder.encode(password, "UTF-8");

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(postData);
                writer.flush();
                writer.close();
                os.close();

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    result += line;
                }
                reader.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            // Handle response from the PHP script
            System.out.println(result);
        }
    }

}
