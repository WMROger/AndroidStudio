package com.example.heavymetals.Models.Adapters;

import static android.app.PendingIntent.getActivity;

import android.app.AlertDialog;
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
import com.example.heavymetals.Models.ExerciseResponse;
import com.example.heavymetals.R;
import com.example.heavymetals.network.ApiService;
import com.example.heavymetals.network.RetrofitClient;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


import com.example.heavymetals.Models.Adapters.WorkoutApi;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


public class WorkoutAdapter extends RecyclerView.Adapter<WorkoutAdapter.WorkoutViewHolder> {

    private List<Workout> workoutList;
    private OnWorkoutClickListener listener;
    private boolean fromTracker;
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
        fetchExerciseCountForWorkout(workout, holder);

        if (fromTracker) {
            holder.viewWorkoutButton.setText("Add to Tracker");
        } else {
            holder.viewWorkoutButton.setText("View Workout");
        }

        holder.viewWorkoutButton.setOnClickListener(v -> listener.onViewWorkoutClick(workout));
        holder.deleteWorkoutButton.setOnClickListener(v -> new AlertDialog.Builder(v.getContext())
                .setTitle("Delete Workout")
                .setMessage("Are you sure you want to delete this workout?")
                .setPositiveButton("Yes", (dialog, which) -> listener.onWorkoutDeleted(workout))
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .create()
                .show());
    }

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
                    int exerciseCount = response.body().getExerciseCount();
                    holder.exerciseCount.setText("Exercises: " + exerciseCount);
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

    @Override
    public int getItemCount() {
        return workoutList.size();
    }

    public void updateWorkouts(List<Workout> newWorkoutList) {
        this.workoutList.clear();
        this.workoutList.addAll(newWorkoutList);
        notifyDataSetChanged();
    }

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

    private void loadTrackedWorkouts() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("TrackerData", Context.MODE_PRIVATE);
        String trackerJson = sharedPreferences.getString("tracker_" + userEmail, "");

        if (!trackerJson.isEmpty()) {
            Gson gson = new Gson();
            Type workoutListType = new TypeToken<ArrayList<Workout>>() {}.getType();
            List<Workout> trackedWorkouts = gson.fromJson(trackerJson, workoutListType);
            updateWorkouts(trackedWorkouts);
        } else {
            Toast.makeText(context, "No workouts tracked yet!", Toast.LENGTH_SHORT).show();
        }
    }
}
