package com.example.heavymetals.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.heavymetals.R;

public class WorkoutReminderReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // Trigger the workout reminder notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "workout_notifications")
                .setSmallIcon(R.drawable.human_icon)  // Set your icon
                .setContentTitle("Workout Reminder")
                .setContentText("You have workouts to complete. Let's get to work!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(1, builder.build());

        Log.d("WorkoutReminderReceiver", "Workout reminder notification sent.");
    }
}
