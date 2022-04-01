package com.rein.android.ReynTestApp;

import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

import ru.evotor.pushNotifications.PushNotificationReceiver;

public class PushReceiver extends PushNotificationReceiver {

    @Override
    public void onReceivePushNotification(final Context context, Bundle data, long messageId) {
        //...получение push-уведомления.
        Toast.makeText(context, data.getString("1111") + " " + data.getString("qqqq")
                + " " + messageId, Toast.LENGTH_SHORT).show();
    }
}