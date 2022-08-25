package com.rein.android.ReynTestApp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import ru.evotor.framework.core.IntegrationActivity;
import ru.evotor.framework.core.IntegrationException;
import ru.evotor.framework.core.IntegrationManagerCallback;
import ru.evotor.framework.core.IntegrationManagerFuture;
import ru.evotor.framework.core.action.command.open_receipt_command.OpenSellReceiptCommand;
import ru.evotor.framework.core.action.event.receipt.changes.position.PositionAdd;
import ru.evotor.framework.core.action.event.receipt.changes.receipt.SetExtra;
import ru.evotor.framework.core.action.event.receipt.payment.system.result.PaymentSystemPaymentOkResult;
import ru.evotor.framework.payment.PaymentType;
import ru.evotor.framework.receipt.ExtraKey;
import ru.evotor.framework.receipt.Measure;
import ru.evotor.framework.receipt.Position;

public class MainActivity extends IntegrationActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle(getApplicationContext().getPackageName());

        findViewById(R.id.btnOpenReceipt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openReceipt();
            }
        });
    }
    public void openReceipt() {
        //Дополнительное поле для позиции. В списке наименований расположено под количеством и выделяется синим цветом
        Set<ExtraKey> set = new HashSet<>();
        set.add(new ExtraKey(null, null, "Тест Зубочистки"));
        //Создание списка товаров чека
        List<PositionAdd> positionAddList = new ArrayList<>();
        positionAddList.add(
                new PositionAdd(
                        Position.Builder.newInstance(
                                        //UUID позиции
                                        UUID.randomUUID().toString(),
                                        //UUID товара
                                        "a627cfd2-d22c-47a2-a5a6-88ad1ecbcaa5",
                                        //Наименование
                                        "Зубочистки",
                                        new Measure("кг", 2, 0),
                                        BigDecimal.valueOf(200),
                                        BigDecimal.ONE
                                        //Добавление цены с учетом скидки на позицию. Итог = price - priceWithDiscountPosition
                                ).setPriceWithDiscountPosition(new BigDecimal(100))
                                .setExtraKeys(set).build()
                )
        );

        //Дополнительные поля в чеке для использования в приложении
        JSONObject object = new JSONObject();
        try {
            object.put("someSuperKey", "AWESOME RECEIPT OPEN");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        SetExtra extra = new SetExtra(object);

        //Открытие чека продажи. Передаются: список наименований, дополнительные поля для приложения
        new OpenSellReceiptCommand(positionAddList, extra, null).process(MainActivity.this, new IntegrationManagerCallback() {
            @Override
            public void run(IntegrationManagerFuture future) {
                try {
                    IntegrationManagerFuture.Result result = future.getResult();
                    if (result.getType() == IntegrationManagerFuture.Result.Type.OK) {
                        startActivity(new Intent("evotor.intent.action.payment.SELL"));
                    }
                } catch (IntegrationException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
