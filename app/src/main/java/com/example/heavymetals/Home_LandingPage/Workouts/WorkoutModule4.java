package com.example.heavymetals.Home_LandingPage.Workouts;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.Button;
import android.widget.TextView;

import com.example.heavymetals.Models.Adapters.Exercise;
import com.example.heavymetals.Models.Adapters.WorkoutAdapter;
import com.example.heavymetals.Models.Adapters.Workout;
import com.example.heavymetals.R;

import java.util.ArrayList;
import java.util.List;

public class WorkoutModule4 extends AppCompatActivity {

    private RecyclerView recyclerView;
    private WorkoutAdapter workoutAdapter;
    private List<Workout> workoutList;
    private Button buttonAddSchedule;
    private TextView workoutTextView, backTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_module4);

        recyclerView = findViewById(R.id.recyclerViewWorkouts);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Get the workout data passed from the previous activity
        Workout workout = (Workout) getIntent().getSerializableExtra("workout");

        if (workout != null) {
            // Create a list containing this one workout
            workoutList = new ArrayList<>();
            workoutList.add(workout);

            // Initialize the adapter with a list of one workout and set it to RecyclerView
            workoutAdapter = new WorkoutAdapter(workoutList, this::onWorkoutViewClicked);
            recyclerView.setAdapter(workoutAdapter);
        }
    }

    private void onWorkoutViewClicked(Workout workout) {
        // Pass the entire list of Exercise objects to the WorkoutDetailActivity
        Intent intent = new Intent(this, WorkoutDetailActivity.class);
        intent.putExtra("exercises", (ArrayList<Exercise>) workout.getExercises());  // Pass the exercises list
        startActivity(intent);
    }

}
