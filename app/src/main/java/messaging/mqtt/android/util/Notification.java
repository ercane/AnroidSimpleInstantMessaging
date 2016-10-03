package messaging.mqtt.android.util;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import messaging.mqtt.android.R;
import messaging.mqtt.android.act.ConversationActivity;
import messaging.mqtt.android.database.DbEntryService;

public class Notification {

    private static int NOTIFICATION_ID = 123;

    public static void createNotification(Context context) {
        // Now invoke the Notification Service
        String notifService = Context.NOTIFICATION_SERVICE;
        NotificationManager mgr =
                (NotificationManager) context.getSystemService(notifService);
        mgr.cancel(NOTIFICATION_ID);

        int size = 0;
        size = DbEntryService.getUnreadNumber();

        android.app.Notification n =
                new android.app.Notification(R.drawable.ic_forum_white_24dp, "",
                        System.currentTimeMillis());

        PendingIntent i = PendingIntent.getActivity(context, 0,
                new Intent(context, ConversationActivity.class),
                0);
        n.setLatestEventInfo(context, "New Message", "There are " + size + " new message", i);
        n.number = size;
        n.flags |= android.app.Notification.FLAG_AUTO_CANCEL;
        n.flags |= android.app.Notification.DEFAULT_SOUND;
        n.flags |= android.app.Notification.DEFAULT_VIBRATE;
        n.ledARGB = 0xff0000ff;
        n.flags |= android.app.Notification.FLAG_SHOW_LIGHTS;


        mgr.notify(NOTIFICATION_ID, n);
    }

    public static void clearNotification(Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(NOTIFICATION_ID);
    }
}
