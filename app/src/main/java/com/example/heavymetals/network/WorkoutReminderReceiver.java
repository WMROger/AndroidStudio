package com.example.heavymetals.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.heavymetals.R;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class WorkoutReminderReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("WorkoutReminderReceiver", "onReceive called!");

        // Create notification channel if necessary
        createNotificationChannel(context);

        // Trigger the workout reminder notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "workout_notifications")
                .setSmallIcon(R.drawable.human_icon)  // Replace with your app's notification icon
                .setContentTitle("Workout Reminder")
                .setContentText("You have workouts to complete. Let's get to work!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(1, builder.build());

        Log.d("WorkoutReminderReceiver", "Workout reminder notification sent.");
    }


    private void createNotificationChannel(Context context) {
        // Only create the notification channel on Android 8.0 (API level 26) and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Workout Notifications";
            String description = "Notifications for workout reminders";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("workout_notifications", name, importance);
            channel.setDescription(description);

            // Register the channel with the system
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }
}
