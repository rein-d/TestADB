package com.rein.android.ReynTestApp;

import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

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

        Toast.makeText(MyAdditionalScreenEvent.this, "Helloнелсенлсвегасгндамдгнамгмгндасгндвскев", Toast.LENGTH_LONG).show();
        return super.handleEvent(event);
    }
}
