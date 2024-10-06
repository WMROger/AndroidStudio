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
import com.example.heavymetals.Models.ExerciseResponse;
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
        holder.workoutTitle.setText(workout.getTitle());

        // Fetch exercises for this workout
        fetchExerciseCountForWorkout(workout, holder);

        // Set up the view and delete button click listeners
        holder.viewWorkoutButton.setOnClickListener(v -> listener.onViewWorkoutClick(workout));
        holder.deleteWorkoutButton.setOnClickListener(v -> listener.onWorkoutDeleted(workout));
    }


    private void fetchExerciseCountForWorkout(Workout workout, WorkoutViewHolder holder) {
        // Fetch the session token from SharedPreferences or wherever you store it
        SharedPreferences sharedPreferences = holder.itemView.getContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String sessionToken = sharedPreferences.getString("auth_token", null);

        if (sessionToken == null) {
            Toast.makeText(holder.itemView.getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        // Make the API call to fetch exercises for this workout
        Retrofit retrofit = RetrofitClient.getClient(holder.itemView.getContext());
        ApiService apiService = retrofit.create(ApiService.class);
        Call<ExerciseResponse> call = apiService.getExercises(sessionToken, workout.getWorkoutId());

        call.enqueue(new Callback<ExerciseResponse>() {
            @Override
            public void onResponse(Call<ExerciseResponse> call, Response<ExerciseResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    int exerciseCount = response.body().getExerciseCount();  // Get the exercise count from the API
                    holder.exerciseCount.setText("Exercises: " + exerciseCount);  // Update the TextView with the count
                } else {
                    holder.exerciseCount.setText("Exercises: 0");  // Set to 0 if failed
                    Toast.makeText(holder.itemView.getContext(), "Failed to load exercises", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ExerciseResponse> call, Throwable t) {
                holder.exerciseCount.setText("Exercises: 0");  // Handle error
                Toast.makeText(holder.itemView.getContext(), "Error loading exercises: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
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
            deleteWorkoutButton = itemView.findViewById(R.id.Delete_txt_view);
            exerciseCount = itemView.findViewById(R.id.exerciseCount);  // Initialize this

        }
    }

    public interface OnWorkoutClickListener {
        void onViewWorkoutClick(Workout workout);
        void onWorkoutDeleted(Workout workout);
    }
}
