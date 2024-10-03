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

    public interface OnWorkoutClickListener {
        void onViewWorkoutClick(Workout workout);
        void onWorkoutDeleted(Workout workout);  // Make sure this matches the method in WorkoutModule4
    }


    public WorkoutAdapter(List<Workout> workouts, OnWorkoutClickListener listener) {
        this.workoutList = workouts;
        this.listener = listener;
    }


    @NonNull
    @Override
    public WorkoutViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.workout_item, parent, false);
        return new WorkoutViewHolder(view);
    }

    public void onBindViewHolder(@NonNull WorkoutViewHolder holder, int position) {
        Workout workout = workoutList.get(position);

        // Check if the workout ID is correctly set
        Log.d("WorkoutAdapter", "Binding workout title: " + workout.getTitle() + " with ID: " + workout.getWorkoutId());

        holder.workoutTitle.setText(workout.getTitle());

        // Set the click listener to open workout details
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onViewWorkoutClick(workout);  // Passing the correct workout object
            }
        });
    }

    @Override
    public int getItemCount() {
        return workoutList.size();
    }
    public static class ExerciseViewHolder extends RecyclerView.ViewHolder {
        TextView exerciseName, setsText, repsText;

        public ExerciseViewHolder(@NonNull View itemView) {
            super(itemView);
            exerciseName = itemView.findViewById(R.id.exercise_name);
            setsText = itemView.findViewById(R.id.set_value);
            repsText = itemView.findViewById(R.id.reps_edit_text);
        }
    }




    static class WorkoutViewHolder extends RecyclerView.ViewHolder {
        TextView workoutTitle;
        Button viewWorkoutButton;
        TextView deleteWorkout;  // Ensure the delete button is set

        public WorkoutViewHolder(@NonNull View itemView) {
            super(itemView);
            workoutTitle = itemView.findViewById(R.id.workoutTitle);
            viewWorkoutButton = itemView.findViewById(R.id.viewWorkoutButton);
            deleteWorkout = itemView.findViewById(R.id.Deletetxtview);  // Initialize the delete button
        }
    }

    // Method to delete workout from the server
    private void deleteWorkoutFromServer(Context context, int workoutId, Runnable onSuccess) {
        Retrofit retrofit = RetrofitClient.getClient(context);
        WorkoutApi workoutApi = retrofit.create(WorkoutApi.class);

        SharedPreferences sharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String sessionToken = sharedPreferences.getString("auth_token", "");

        // Call the API to delete the workout
        Call<Void> call = workoutApi.deleteWorkout(workoutId, sessionToken);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    // Ensure the success callback is triggered only if the response is successful
                    onSuccess.run();
                    Log.d("WorkoutAdapter", "Workout deleted successfully from server");
                } else {
                    // Server deletion failed, provide feedback via Toast
                    Toast.makeText(context, "Failed to delete workout: " + response.message(), Toast.LENGTH_SHORT).show();
                    Log.e("WorkoutAdapter", "Server error: " + response.code() + " - " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                // Handle failure scenario
                Toast.makeText(context, "Error deleting workout: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("WorkoutAdapter", "Network error: " + t.getMessage(), t);
            }
        });
    }

}
