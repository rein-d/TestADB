package com.rein.android.ReynTestApp;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import androidx.annotation.Nullable;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import ru.evotor.framework.core.IntegrationService;
import ru.evotor.framework.core.action.event.receipt.payment.system.PaymentSystemProcessor;
import ru.evotor.framework.core.action.event.receipt.payment.system.event.PaymentSystemEvent;
import ru.evotor.framework.core.action.event.receipt.payment.system.event.PaymentSystemPaybackCancelEvent;
import ru.evotor.framework.core.action.event.receipt.payment.system.event.PaymentSystemPaybackEvent;
import ru.evotor.framework.core.action.event.receipt.payment.system.event.PaymentSystemSellCancelEvent;
import ru.evotor.framework.core.action.event.receipt.payment.system.event.PaymentSystemSellEvent;
import ru.evotor.framework.core.action.processor.ActionProcessor;

public class MyPaymentService extends IntegrationService {
    public static final String TAG = "PaymentService";
    public static final String EXTRA_NAME_OPERATION = "EXTRA_NAME_OPERATION";
    public static final String SUM = "ReceiptSum";

    @Nullable
    @Override
    protected Map<String, ActionProcessor> createProcessors() {
        Map<String, ActionProcessor> map = new HashMap<>();

        map.put(PaymentSystemEvent.NAME_ACTION,
                new PaymentSystemProcessor() {
                    // Операция продажи товара
                    @Override
                    public void sell(String s, PaymentSystemSellEvent paymentSystemSellEvent, Callback callback) {
                        Log.e(TAG, "sell " + paymentSystemSellEvent);
                        //Передадим тип операции с  в Activity
                        Intent intent = new Intent(MyPaymentService.this, MyPaymentActivity.class);
                        intent.putExtra(SUM, paymentSystemSellEvent.getSum().movePointRight(2).intValue());
                        intent.putExtra(EXTRA_NAME_OPERATION, "sell");
                        try {
                            callback.startActivity(intent);
                        } catch (RemoteException exc) {
                            exc.printStackTrace();
                        }
                    }

                    //Отмена продажи
                    @Override
                    public void sellCancel(String s, PaymentSystemSellCancelEvent paymentSystemSellCancelEvent, Callback callback) {
                        Log.e(TAG, "sellCancel " + paymentSystemSellCancelEvent);
                        //Передадим тип операции в Activity
                        Intent intent = new Intent(MyPaymentService.this, MyPaymentActivity.class);
                        intent.putExtra(EXTRA_NAME_OPERATION, "sellCancel");
                        try {
                            callback.startActivity(intent);
                        } catch (RemoteException exc) {
                            exc.printStackTrace();
                        }
                    }

                    //Операция возврата
                    @Override
                    public void payback(String s, PaymentSystemPaybackEvent paymentSystemPaybackEvent, Callback callback) {
                        Log.e(TAG, "payback " + paymentSystemPaybackEvent);
                        //Передадим тип операции в Activity
                        Intent intent = new Intent(MyPaymentService.this, MyPaymentActivity.class);
                        intent.putExtra(EXTRA_NAME_OPERATION, "payback");
                        try {
                            callback.startActivity(intent);
                        } catch (RemoteException exc) {
                            exc.printStackTrace();
                        }
                    }

                    //Отмена возврата
                    @Override
                    public void paybackCancel(String s, PaymentSystemPaybackCancelEvent paymentSystemPaybackCancelEvent, Callback callback) {
                        Log.e(TAG, "paybackCancel " + paymentSystemPaybackCancelEvent);
                        //Передадим тип операции в Activity
                        Intent intent = new Intent(MyPaymentService.this, MyPaymentActivity.class);
                        intent.putExtra(EXTRA_NAME_OPERATION, "paybackCancel");
                        try {
                            callback.startActivity(intent);
                        } catch (RemoteException exc) {
                            exc.printStackTrace();
                        }
                    }
                });
        return map;
    }
}