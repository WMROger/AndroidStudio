package com.example.heavymetals.Models.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.heavymetals.R;

import java.util.List;

public class ExercisesAdapter extends RecyclerView.Adapter<ExercisesAdapter.ExerciseViewHolder> {
    private List<AdaptersExercise> exercises;

    public ExercisesAdapter(List<AdaptersExercise> exercises) {
        this.exercises = exercises;
    }

    @NonNull
    @Override
    public ExerciseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.exercise_item, parent, false);
        return new ExerciseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExerciseViewHolder holder, int position) {
        AdaptersExercise exercise = exercises.get(position);
        holder.exerciseName.setText(exercise.getName());
        holder.setsText.setText("Sets: " + exercise.getSets());
        holder.repsText.setText("Reps: " + exercise.getReps());
    }

    @Override
    public int getItemCount() {
        return exercises.size();
    }

    public void updateExercises(List<AdaptersExercise> exercises) {
        this.exercises = exercises;
        notifyDataSetChanged();
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
}

