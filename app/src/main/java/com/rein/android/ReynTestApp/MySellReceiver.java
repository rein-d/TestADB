package com.rein.android.ReynTestApp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import ru.evotor.framework.receipt.Payment;
import ru.evotor.framework.receipt.Receipt;
import ru.evotor.framework.receipt.ReceiptApi;
import ru.evotor.framework.receipt.event.ReceiptCompletedEvent;
import ru.evotor.framework.receipt.event.ReceiptCreatedEvent;
import ru.evotor.framework.receipt.event.handler.receiver.SellReceiptBroadcastReceiver;
import ru.evotor.framework.users.UserApi;

public class MySellReceiver extends SellReceiptBroadcastReceiver {
    public static final String TAG = "sellOperation";


    @Override
    protected void handleReceiptCompletedEvent(@NotNull Context context, @NotNull ReceiptCompletedEvent event) {
        super.handleReceiptCompletedEvent(context, event);
        Receipt receiptClosed = ReceiptApi.getReceipt(context, event.getReceiptUuid());

        Log.d(TAG, "Чек "+receiptClosed.getHeader().getUuid().toString()+" успешно проведен");
        Log.d(TAG, "Кассир с UUID: "+ UserApi.getAuthenticatedUser(context).getUuid()+" пробил этот чек");

    }
}