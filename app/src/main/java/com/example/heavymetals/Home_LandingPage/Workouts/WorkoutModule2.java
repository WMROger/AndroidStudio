package com.example.heavymetals.Home_LandingPage.Workouts;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.heavymetals.R;

public class WorkoutModule2 extends AppCompatActivity {
    TextView discardTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_module2);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        discardTxt = findViewById(R.id.WM2discard_txt);
        Button addbtn = findViewById(R.id.WM2AddExercisebtn);

        discardTxt.setOnClickListener(V -> {
            // Navigate back to WorkoutModule1

            finish(); // Close WorkoutModule2 activity
        });

        addbtn.setOnClickListener(V -> {
            Intent intent = new Intent (WorkoutModule2.this, WorkoutModule3.class);
            startActivity(intent);
        });
    }
}