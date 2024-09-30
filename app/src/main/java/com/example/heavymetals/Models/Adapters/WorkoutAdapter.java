package com.example.heavymetals.Models.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.heavymetals.R;

import java.util.List;

public class WorkoutAdapter extends RecyclerView.Adapter<WorkoutAdapter.WorkoutViewHolder> {
    private List<Workout> workoutList;
    private OnWorkoutClickListener listener;

    // Modify the interface to include a method for deletion
    public interface OnWorkoutClickListener {
        void onViewWorkoutClick(Workout workout);  // Existing click to view a workout
        void onWorkoutDeleted();  // New method to notify the activity of a deletion
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

    @Override
    public void onBindViewHolder(@NonNull WorkoutViewHolder holder, int position) {
        Workout workout = workoutList.get(position);
        holder.workoutTitle.setText(workout.getTitle());  // Display workout name
        holder.exerciseCount.setText("Exercises: " + workout.getExerciseCount());

        // Handle view workout click
        holder.viewWorkoutButton.setOnClickListener(v -> listener.onViewWorkoutClick(workout));

        // Handle delete workout click
        holder.deleteWorkout.setOnClickListener(v -> {
            // Remove the workout from the list
            workoutList.remove(position);
            notifyItemRemoved(position);  // Notify RecyclerView about the item removal
            notifyItemRangeChanged(position, workoutList.size());  // Update remaining items' positions

            // Notify the activity that a workout was deleted
            listener.onWorkoutDeleted();  // This will call the method in the activity to save the deletion
        });
    }

    @Override
    public int getItemCount() {
        return workoutList.size();
    }

    static class WorkoutViewHolder extends RecyclerView.ViewHolder {
        TextView workoutTitle;
        TextView exerciseCount;
        Button viewWorkoutButton;
        TextView deleteWorkout;  // Add this to handle deletion

        public WorkoutViewHolder(@NonNull View itemView) {
            super(itemView);
            workoutTitle = itemView.findViewById(R.id.workoutTitle);
            exerciseCount = itemView.findViewById(R.id.exerciseCount);
            viewWorkoutButton = itemView.findViewById(R.id.viewWorkoutButton);
            deleteWorkout = itemView.findViewById(R.id.Deletetxtview);  // Initialize the delete TextView
        }
    }
}
