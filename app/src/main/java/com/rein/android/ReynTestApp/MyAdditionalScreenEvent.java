package com.rein.android.ReynTestApp;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import ru.evotor.framework.receipt.formation.event.DiscountScreenAdditionalItemsEvent;
import ru.evotor.framework.receipt.formation.event.handler.service.SellIntegrationService;


public class MyAdditionalScreenEvent extends SellIntegrationService {

    @Nullable
    @Override
    public Void handleEvent(@NonNull DiscountScreenAdditionalItemsEvent event) {
        String TAG = "MyApp";
        Log.d(TAG, "test");

        return super.handleEvent(event);
    }
}
