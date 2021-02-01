package ddwu.mobile.finalproject.ma01_20180943.Show;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import ddwu.mobile.finalproject.ma01_20180943.R;

public class MyBroadcastReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        String title = intent.getStringExtra("title");

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, context.getString(R.string.CHANNEL_ID))
                .setSmallIcon(R.drawable.ic_stat_name)
                .setContentTitle("공연 '" + title + "' 종료일")
                .setContentText("오늘 공연이 종료됩니다.")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        int notificationId = 100;

        notificationManager.notify(notificationId, builder.build());
    }
}
