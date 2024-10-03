package com.example.heavymetals.network;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Calendar;

public class ScheduleDailyNotification {

    public void scheduleDailyNotification(Context context) {
        // Set the time for the notification (e.g., 8 AM)
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 8);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        Intent intent = new Intent(context, WorkoutReminderReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE // Use FLAG_IMMUTABLE for Android 12+ compatibility
        );

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);


        if (alarmManager != null) {
            alarmManager.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    60000,                                //AlarmManager.INTERVAL_DAY,
                    pendingIntent
            );
            Log.d("ScheduleNotification", "Daily notification scheduled at 8 AM.");
        }
    }
}
