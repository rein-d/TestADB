package com.rein.android.ReynTestApp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import ru.evotor.framework.core.action.event.receipt.receipt_edited.ReceiptClosedEvent;
import ru.evotor.framework.receipt.Receipt;
import ru.evotor.framework.receipt.ReceiptApi;

public class MyCloseReceiptEvent extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Bundle bundle = intent.getExtras();
        if (action.equals("evotor.intent.action.receipt.sell.RECEIPT_CLOSED")) {
            String uuid = ReceiptClosedEvent.create(bundle).getReceiptUuid();


            Receipt MyReceipt124 = ReceiptApi.getReceipt(context.getApplicationContext(), uuid);
            String object = "";
            object = MyReceipt124.getHeader().getExtra();
            Toast.makeText(context.getApplicationContext(), object, Toast.LENGTH_LONG).show();
        }
    }
}
