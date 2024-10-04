package com.example.heavymetals.Models.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.heavymetals.Models.Exercise;
import com.example.heavymetals.R;
import com.example.heavymetals.network.ApiService;
import com.example.heavymetals.network.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


import com.example.heavymetals.Models.Adapters.WorkoutApi;



public class WorkoutAdapter extends RecyclerView.Adapter<WorkoutAdapter.WorkoutViewHolder> {

    private List<Workout> workoutList;
    private OnWorkoutClickListener listener;

    public WorkoutAdapter(List<Workout> workoutList, OnWorkoutClickListener listener) {
        this.workoutList = workoutList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public WorkoutViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.workout_item, parent, false);
        return new WorkoutViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkoutViewHolder holder, int position) {
        // Get the workout at this position
        Workout workout = workoutList.get(position);

        // Set the workout title and ID
        holder.workoutTitle.setText(workout.getTitle() + " (ID: " + workout.getWorkoutId() + ")");

        // Get the number of exercises in the workout and handle null case
        List<AdaptersExercise> exercises = workout.getExercises();
        int exerciseCount = (exercises != null) ? exercises.size() : 0;  // Check if exercises is null, if so set count to 0

        // Update the TextView with the exercise count
        holder.exerciseCount.setText("Exercises: " + exerciseCount);

        // Set up the view and delete button click listeners
        holder.viewWorkoutButton.setOnClickListener(v -> listener.onViewWorkoutClick(workout));
        holder.deleteWorkoutButton.setOnClickListener(v -> listener.onWorkoutDeleted(workout));
    }




    @Override
    public int getItemCount() {
        return workoutList.size();
    }

    public static class WorkoutViewHolder extends RecyclerView.ViewHolder {
        TextView workoutTitle, deleteWorkoutButton, exerciseCount;
        Button viewWorkoutButton;

        public WorkoutViewHolder(@NonNull View itemView) {
            super(itemView);
            workoutTitle = itemView.findViewById(R.id.workoutTitle);
            viewWorkoutButton = itemView.findViewById(R.id.viewWorkoutButton);
            deleteWorkoutButton = itemView.findViewById(R.id.Deletetxtview);
            exerciseCount = itemView.findViewById(R.id.exerciseCount);  // Initialize this

        }
    }

    public interface OnWorkoutClickListener {
        void onViewWorkoutClick(Workout workout);
        void onWorkoutDeleted(Workout workout);
    }
}
