package com.rein.android.ReynTestApp;


import android.os.RemoteException;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import ru.evotor.framework.core.IntegrationService;
import ru.evotor.framework.core.action.event.receipt.changes.position.IPositionChange;
import ru.evotor.framework.core.action.event.receipt.changes.position.PositionAdd;
import ru.evotor.framework.core.action.event.receipt.changes.receipt.SetExtra;
import ru.evotor.framework.core.action.event.receipt.discount.ReceiptDiscountEvent;
import ru.evotor.framework.core.action.event.receipt.discount.ReceiptDiscountEventProcessor;
import ru.evotor.framework.core.action.event.receipt.discount.ReceiptDiscountEventResult;
import ru.evotor.framework.core.action.processor.ActionProcessor;
import ru.evotor.framework.receipt.Measure;
import ru.evotor.framework.receipt.Position;


/**
 * Применение скидки на весь чек продажи
 */
public class MyDiscountService extends IntegrationService {
    @Nullable
    @Override
    protected Map<String, ActionProcessor> createProcessors() {
        Map<String, ActionProcessor> map = new HashMap<>();
        map.put(ReceiptDiscountEvent.NAME_SELL_RECEIPT, new ReceiptDiscountEventProcessor() {
            @Override
            public void call(@NonNull String action, @NonNull ReceiptDiscountEvent event, @NonNull Callback callback) {
                try {
                    //Значение скидки на весь чек в рублях или иной валюте
                    BigDecimal discount = new BigDecimal(0); //Ставим 0
                    JSONObject object = new JSONObject();
                    object.put("Extra Discount on Receipt", "AWESOME DISCOUNT");
                    SetExtra extra = new SetExtra(object);
                    List<IPositionChange> listOfChanges = new ArrayList<>();
                    Position PositionToBeAdded = Position.Builder.newInstance(
                            UUID.randomUUID().toString(),
                            UUID.randomUUID().toString(),
                            "Зажигалка",
                            new Measure("шт", 0, 0),
                            new BigDecimal(30),
                            new BigDecimal(1)
                    ).build();
                    listOfChanges.add(new PositionAdd(PositionToBeAdded));
                    callback.onResult(
                            new ReceiptDiscountEventResult(
                                    discount, //скидка будет == 0
                                    extra,
                                    listOfChanges, // добавляем позицию
                                    null
                            ));
                } catch (JSONException | RemoteException e) {
                    e.printStackTrace();
                }
            }
        });
        return map;
    }
}
