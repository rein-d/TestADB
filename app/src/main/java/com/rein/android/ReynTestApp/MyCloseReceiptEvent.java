package com.rein.android.ReynTestApp;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import ru.evotor.framework.core.action.event.receipt.receipt_edited.ReceiptClosedEvent;
import ru.evotor.framework.core.action.event.receipt.receipt_edited.ReceiptOpenedEvent;
import ru.evotor.framework.kkt.api.KktApi;
import ru.evotor.framework.receipt.FiscalReceipt;
import ru.evotor.framework.receipt.Receipt;
import ru.evotor.framework.receipt.ReceiptApi;
import ru.evotor.query.Cursor;

public class MyCloseReceiptEvent extends BroadcastReceiver {

    private static final String TAG = "ReceiptCloseShenenignas";
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Bundle bundle = intent.getExtras();
        if (action.equals("evotor.intent.action.receipt.sell.RECEIPT_CLOSED")) {
//            Cursor<FiscalReceipt> receipts = ReceiptApi.getFiscalReceipts(context, ReceiptClosedEvent.create(bundle).getReceiptUuid());
//                Log.d(TAG, "regNumber: " + receipts.getValue().getKktRegistrationNumber());
                Log.d(TAG, "KKRRegNumber: " + KktApi.receiveKktRegNumber(context));
        } else if (action.equals("evotor.intent.action.receipt.sell.OPENED")) {
            Log.d(TAG, "uuid" + ReceiptOpenedEvent.create(bundle).getReceiptUuid());//Открытие чека продажи.
        }

    }
}
