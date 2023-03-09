package com.rein.android.ReynTestApp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import ru.evotor.framework.core.action.event.receipt.receipt_edited.ReceiptClosedEvent;
import ru.evotor.framework.device.display.DisplayApi;
import ru.evotor.framework.device.display.Displays;
import ru.evotor.framework.receipt.Payment;
import ru.evotor.framework.receipt.Receipt;
import ru.evotor.framework.receipt.ReceiptApi;
import ru.evotor.framework.receipt.event.ReceiptCompletedEvent;
import ru.evotor.framework.receipt.event.ReceiptCreatedEvent;
import ru.evotor.framework.receipt.event.handler.receiver.SellReceiptBroadcastReceiver;
import ru.evotor.framework.users.UserApi;

public class MySellReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Bundle bundle = intent.getExtras();
        Log.e("MySellReceiver", "Data:" + ReceiptClosedEvent.create(bundle).getReceiptUuid());
        if (action.equals("evotor.intent.action.receipt.sell.RECEIPT_CLOSED")){
            Intent activityIntent = new Intent(context,MainActivity.class);
            activityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.getApplicationContext().startActivity(activityIntent , DisplayApi.makeOptionsFor(context.getApplicationContext(), Displays.CUSTOMER));
        }
    }
}