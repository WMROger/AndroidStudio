package com.example.heavymetals.Models.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
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

import com.example.heavymetals.Home_LandingPage.MainActivity;
import com.example.heavymetals.Models.ExerciseResponse;
import com.example.heavymetals.R;
import com.example.heavymetals.network.ApiService;
import com.example.heavymetals.network.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class WorkoutAdapter extends RecyclerView.Adapter<WorkoutAdapter.WorkoutViewHolder> {

    private List<Workout> workoutList;
    private OnWorkoutClickListener listener;
    private boolean fromTracker;  // To differentiate between "Add to Tracker" and "View Workout"
    private Context context;
    private String userEmail;

    public WorkoutAdapter(List<Workout> workoutList, OnWorkoutClickListener listener, boolean fromTracker, Context context, String userEmail) {
        this.workoutList = workoutList;
        this.listener = listener;
        this.fromTracker = fromTracker;
        this.context = context;
        this.userEmail = userEmail;
    }

    @NonNull
    @Override
    public WorkoutViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.workout_item, parent, false);
        return new WorkoutViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkoutViewHolder holder, int position) {
        Workout workout = workoutList.get(position);
        holder.workoutTitle.setText(workout.getTitle());

        // Fetch exercise count for the workout and ensure exercises are loaded
        fetchExerciseCountForWorkout(workout, holder);

        // Set button text based on whether it's from tracker or not
        if (fromTracker) {
            holder.viewWorkoutButton.setText("Add to Tracker");
        } else {
            holder.viewWorkoutButton.setText("View Workout");
        }

        // Handle button click
        holder.viewWorkoutButton.setOnClickListener(v -> {
            if (fromTracker) {
                // Add workout to tracker with proper exercise fetching
                fetchAndAddToTracker(workout);
            } else {
                listener.onViewWorkoutClick(workout);
            }
        });

        // Handle delete button click
        holder.deleteWorkoutButton.setOnClickListener(v -> new AlertDialog.Builder(v.getContext())
                .setTitle("Delete Workout")
                .setMessage("Are you sure you want to delete this workout?")
                .setPositiveButton("Yes", (dialog, which) -> listener.onWorkoutDeleted(workout))
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .create()
                .show());
    }

    @Override
    public int getItemCount() {
        return workoutList.size();
    }

    public void updateWorkouts(List<Workout> newWorkoutList) {
        this.workoutList.clear();
        this.workoutList.addAll(newWorkoutList);
        notifyDataSetChanged();
    }

    // Fetch exercise count and exercises for a workout
    private void fetchExerciseCountForWorkout(Workout workout, WorkoutViewHolder holder) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String sessionToken = sharedPreferences.getString("auth_token", null);

        if (sessionToken == null) {
            Toast.makeText(context, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        Retrofit retrofit = RetrofitClient.getClient(context);
        ApiService apiService = retrofit.create(ApiService.class);
        Call<ExerciseResponse> call = apiService.getExercises(sessionToken, workout.getWorkoutId());

        call.enqueue(new Callback<ExerciseResponse>() {
            @Override
            public void onResponse(Call<ExerciseResponse> call, Response<ExerciseResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    List<AdaptersExercise> exercises = response.body().getExercises();
                    workout.setExercises(exercises); // Set the exercises in the workout object
                    holder.exerciseCount.setText("Exercises: " + exercises.size());
                } else {
                    holder.exerciseCount.setText("Exercises: 0");
                    Toast.makeText(context, "Failed to load exercises", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ExerciseResponse> call, Throwable t) {
                holder.exerciseCount.setText("Exercises: 0");
                Toast.makeText(context, "Error loading exercises: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Ensure exercises are fetched before adding to tracker
    private void fetchAndAddToTracker(Workout workout) {
        // If the exercises are already fetched, add to tracker
        if (workout.getExercises() != null) {
            addToTracker(workout);
        } else {
            // Fetch exercises from server before adding to tracker
            SharedPreferences sharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
            String sessionToken = sharedPreferences.getString("auth_token", null);

            if (sessionToken == null) {
                Toast.makeText(context, "User not logged in", Toast.LENGTH_SHORT).show();
                return;
            }

            Retrofit retrofit = RetrofitClient.getClient(context);
            ApiService apiService = retrofit.create(ApiService.class);
            Call<ExerciseResponse> call = apiService.getExercises(sessionToken, workout.getWorkoutId());

            call.enqueue(new Callback<ExerciseResponse>() {
                @Override
                public void onResponse(Call<ExerciseResponse> call, Response<ExerciseResponse> response) {
                    if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                        List<AdaptersExercise> exercises = response.body().getExercises();
                        workout.setExercises(exercises); // Set the exercises in the workout object
                        addToTracker(workout); // Now that exercises are fetched, add to tracker
                    } else {
                        Toast.makeText(context, "Failed to load exercises for this workout.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ExerciseResponse> call, Throwable t) {
                    Toast.makeText(context, "Error fetching exercises: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    // Add workout to tracker and redirect to ProgressFragment
    private void addToTracker(Workout workout) {
        if (workout.getExercises() == null || workout.getExercises().isEmpty()) {
            Toast.makeText(context, "This workout has no exercises to add.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Log the selected workout
        Log.d("WorkoutAdapter", "Adding to tracker: Workout Title: " + workout.getTitle() + ", Exercises: " + workout.getExercises().size());

        // Save workout details to SharedPreferences for tracking
        SharedPreferences sharedPreferences = context.getSharedPreferences("SelectedWorkout", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("workout_title", workout.getTitle());
        editor.putInt("exercise_count", workout.getExercises().size());
        editor.apply();

        // Notify the user
        Toast.makeText(context, "Workout added to tracker!", Toast.LENGTH_SHORT).show();

        // Redirect to the ProgressFragment via MainActivity
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra("showProgressFragment", true);
        intent.putExtra("workout_title", workout.getTitle());
        intent.putExtra("exercise_count", workout.getExercises().size());
        context.startActivity(intent);
    }

    // ViewHolder class
    public static class WorkoutViewHolder extends RecyclerView.ViewHolder {
        TextView workoutTitle, deleteWorkoutButton, exerciseCount;
        Button viewWorkoutButton;

        public WorkoutViewHolder(@NonNull View itemView) {
            super(itemView);
            workoutTitle = itemView.findViewById(R.id.workoutTitle);
            viewWorkoutButton = itemView.findViewById(R.id.viewWorkoutButton);
            deleteWorkoutButton = itemView.findViewById(R.id.Delete_txt_view);
            exerciseCount = itemView.findViewById(R.id.exerciseCount);
        }
    }

    public interface OnWorkoutClickListener {
        void onViewWorkoutClick(Workout workout);
        void onWorkoutDeleted(Workout workout);
    }
}
