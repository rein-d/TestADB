package com.rein.android.ReynTestApp;

import android.content.Intent;
import android.os.RemoteException;
import android.support.annotation.NonNull;

import org.jetbrains.annotations.Nullable;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.evotor.framework.core.IntegrationService;
import ru.evotor.framework.core.action.event.receipt.changes.position.IPositionChange;
import ru.evotor.framework.core.action.event.receipt.changes.position.SetExtra;
import ru.evotor.framework.core.action.event.receipt.discount.ReceiptDiscountEvent;
import ru.evotor.framework.core.action.event.receipt.discount.ReceiptDiscountEventProcessor;
import ru.evotor.framework.core.action.event.receipt.discount.ReceiptDiscountEventResult;
import ru.evotor.framework.core.action.processor.ActionProcessor;

public class MyDiscountService extends IntegrationService {
    @Nullable
    @Override
    protected Map<String, ActionProcessor> createProcessors() {
        Map<String, ActionProcessor> map = new HashMap<>();
        map.put(ReceiptDiscountEvent.NAME_SELL_RECEIPT, new ReceiptDiscountEventProcessor() {
            @Override
            public void call(@NonNull String action, @NonNull ReceiptDiscountEvent event, @NonNull Callback callback){
                try {
                    callback.startActivity(new Intent(getApplicationContext(), ActivityWebView.class));
                    //Значение скидки на весь чек в рублях или иной валюте
                    BigDecimal discount = new BigDecimal(10);
                    JSONObject object = new JSONObject();
                    object.put("someSuperKey", "AWESOME DISCOUNT");
                    SetExtra extra = new SetExtra(object);
                    List<IPositionChange> listOfChanges = new ArrayList<>();
                        callback.onResult(
                                new ReceiptDiscountEventResult(
                                        discount,
                                        extra,
                                        listOfChanges
                                ));
                    } catch (JSONException | RemoteException e) {
                    e.printStackTrace();

                }


            }
        });
        return map;
    }
}