package com.rein.android.ReynTestApp;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.net.ssl.HttpsURLConnection;

import ru.evotor.framework.component.PaymentPerformer;
import ru.evotor.framework.core.Error;
import ru.evotor.framework.core.IntegrationActivity;
import ru.evotor.framework.core.IntegrationException;
import ru.evotor.framework.core.IntegrationManagerCallback;
import ru.evotor.framework.core.IntegrationManagerFuture;
import ru.evotor.framework.core.action.command.open_receipt_command.OpenSellReceiptCommand;
import ru.evotor.framework.core.action.command.print_receipt_command.PrintReceiptCommandResult;
import ru.evotor.framework.core.action.command.print_receipt_command.PrintSellReceiptCommand;
import ru.evotor.framework.core.action.event.receipt.changes.position.PositionAdd;
import ru.evotor.framework.core.action.event.receipt.changes.position.SetExtra;
import ru.evotor.framework.kkt.api.DocumentRegistrationCallback;
import ru.evotor.framework.kkt.api.DocumentRegistrationException;
import ru.evotor.framework.kkt.api.KktApi;
import ru.evotor.framework.payment.PaymentSystem;
import ru.evotor.framework.payment.PaymentType;
import ru.evotor.framework.receipt.ExtraKey;
import ru.evotor.framework.receipt.Payment;
import ru.evotor.framework.receipt.Position;
import ru.evotor.framework.receipt.PrintGroup;
import ru.evotor.framework.receipt.Receipt;
import ru.evotor.framework.receipt.ReceiptApi;
import ru.evotor.framework.receipt.SettlementType;
import ru.evotor.framework.receipt.TaxNumber;
import ru.evotor.framework.receipt.correction.CorrectionType;
import ru.evotor.framework.receipt.position.SettlementMethod;
import ru.evotor.framework.receipt.position.VatRate;

import static android.content.ContentValues.TAG;
import static ru.evotor.framework.receipt.TaxationSystem.SIMPLIFIED_INCOME;

public class MainActivity extends IntegrationActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        findViewById(R.id.button).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){

                openReceiptAndEmail();

            }
        });

        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){

                Correction();

            }
        });
        findViewById(R.id.button4).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){

                openReceipt();

            }
        });
        findViewById(R.id.button3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, ActivityWebView.class));
            }
        });

        findViewById(R.id.button6).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String path = "https://webhook.site/e50dde35-94d6-40e1-966b-8045565845ac";
                Thread thread = new Thread(new Runnable(){
                    @Override
                    public void run() {
                        try {
                            //Your code goes here
                            URL url = new URL(path);
                            HttpsURLConnection c = (HttpsURLConnection)url.openConnection();
                            c.setRequestMethod("POST");
                            c.setReadTimeout(10000);
                            c.connect();
                        } catch (Exception e) {
                            Log.e(TAG, e.getMessage());
                        }
                    }
                });
                thread.start();
            }
        });



    }
    /*


*/
    public void Correction () {
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
        Date correctableSettlementDate = Calendar.getInstance().getTime();
        try {
            correctableSettlementDate = format.parse("26.09.2019");
        } catch (Exception e) {

        }

        DocumentRegistrationCallback callback = null;
        try {
            callback = new DocumentRegistrationCallback() {
                @Override
                public void onError(@NotNull DocumentRegistrationException e) {

                }

                @Override
                public void onSuccess(@Nullable UUID uuid) {


                }
            };
        } catch (Exception e) {
            e.printStackTrace();
        }

        KktApi.registerCorrectionReceipt(getApplicationContext(), SettlementType.INCOME,
                SIMPLIFIED_INCOME, CorrectionType.BY_PRESCRIBED, "basis", "1",
                correctableSettlementDate, new BigDecimal(10).setScale(2, BigDecimal.ROUND_HALF_UP), PaymentType.CASH, VatRate.VAT_20_120,
                "correction", callback);
    }
    public void openReceiptAndEmail() {
        //Дополнительное поле для позиции. В списке наименований расположено под количеством и выделяется синим цветом
        Set<ExtraKey> set = new HashSet<>();
        set.add(new ExtraKey(null, null, "Тест "));
        //Создание списка товаров чека
        List<Position> list = new ArrayList<>();
        //позиция 1
        list.add(
                Position.Builder.newInstance(
                        //UUID позиции
                        UUID.randomUUID().toString(),
                        //UUID товара
                        UUID.randomUUID().toString(),
                        //Наименование
                        "Тестовый1",
                        //Наименование единицы измерения
                        "шт",
                        //Точность единицы измерения
                        0,
                        //Цена без скидок
                        new BigDecimal(50000 ),
                        //Количество
                        new BigDecimal(1)
                ).setSettlementMethod(new SettlementMethod.FullSettlement()).setTaxNumber(TaxNumber.VAT_18_118).build()
        );

        HashMap payments = new HashMap<Payment, BigDecimal>();
        //установка способа оплаты
        //1
        payments.put(new Payment(
                UUID.randomUUID().toString(),
                new BigDecimal(50000 ),
                null,
                new PaymentPerformer(
                        new PaymentSystem(PaymentType.CASH, "Оплата", "My"),
                        "имя пакета",
                        "название компонента",
                        "app_uuid",
                        "appName"
                ),
                null,
                null,
                null
        ), new BigDecimal(50000 ));

        PrintGroup printGroup = new PrintGroup(UUID.randomUUID().toString(),
                PrintGroup.Type.CASH_RECEIPT, null, null, null, null, false, null);
        Receipt.PrintReceipt printReceipt = new Receipt.PrintReceipt(
                printGroup,
                list,
                payments,
                new HashMap<Payment, BigDecimal>(),
                new HashMap<String, BigDecimal>()
        );

        ArrayList<Receipt.PrintReceipt> listDocs = new ArrayList<>();
        listDocs.add(printReceipt);


        new PrintSellReceiptCommand(
                listDocs,
                null,
                null,
                "d.reyn@evotor.ru",
                null
        ).process(MainActivity.this, new IntegrationManagerCallback() {
            @Override
            public void run(IntegrationManagerFuture future) {
                try {
                    IntegrationManagerFuture.Result result = future.getResult();
                    switch (result.getType()) {
                        case OK:
                            PrintReceiptCommandResult printSellReceiptResult = PrintReceiptCommandResult.create(result.getData());
                            break;
                        case ERROR:
                            Toast.makeText(MainActivity.this, result.getError().getMessage(), Toast.LENGTH_LONG).show();
                            Error error = result.getError();
                            Log.i(getPackageName(), "e.code: " + error.getCode() + "; e.msg: " + error.getMessage());
                            //alertMainValue(error.getMessage());
                            break;
                    }
                } catch (IntegrationException e) {

                    e.printStackTrace();
                }
            }
        });


    }

    public void openReceipt() {
        //Создание списка товаров чека
        List<PositionAdd> positionAddList = new ArrayList<>();

        Set<ExtraKey> set = new HashSet<>();
        set.add(new ExtraKey(null, null, "Тест Зубочистки"));

        positionAddList.add(
                new PositionAdd(
                        Position.Builder.newInstance(
                                //UUID позиции
                                "241e9344-ef50-46bc-9ce2-443c38b649e5",
                                //UUID товара
                                UUID.randomUUID().toString(),
                                //Наименование
                                "Товар1",
                                //Наименование единицы измерения
                                "шт",
                                //Точность единицы измерения
                                0,
                                //Цена без скидок
                                new BigDecimal(30000),
                                //Количество
                                BigDecimal.valueOf( 1)
                                //Добавление цены с учетом скидки на позицию. Итог = price - priceWithDiscountPosition
                        ).setExtraKeys(set)
                                .build()
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
        new OpenSellReceiptCommand(positionAddList, extra).process(MainActivity.this, new IntegrationManagerCallback() {
            @Override
            public void run(IntegrationManagerFuture future) {

                try {
                    IntegrationManagerFuture.Result result = future.getResult();
                    if (result.getType() == IntegrationManagerFuture.Result.Type.OK) {

                        Receipt MyReceipt124 = ReceiptApi.getReceipt(MainActivity.this, Receipt.Type.SELL);
                        MyReceipt124.getPrintDocuments();
                                startActivity(new Intent("evotor.intent.action.payment.SELL"));

/*
                        Receipt MyReceipt124 = ReceiptApi.getReceipt(MainActivity.this, Receipt.Type.SELL);
                        String uuid = MyReceipt124.getHeader().getUuid();

                        final List<PaymentPerformer> paymentPerformers123 = PaymentPerformerApi.INSTANCE.getAllPaymentPerformers(getPackageManager());
                        AlertDialog.Builder builderSingle = new AlertDialog.Builder(MainActivity.this);

                        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.select_dialog_singlechoice);
                        for (int i = 0; i < paymentPerformers123.size(); i++) {
                            arrayAdapter.add(paymentPerformers123.get(i).getPaymentSystem().getUserDescription());
                        }

                        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                            @Override
                            //По выбору пользователя выполняем оплату и печатаем чек.
                            public void onClick(DialogInterface dialog, int which) {
                                SellApi.moveCurrentReceiptDraftToPaymentStage(MainActivity.this, paymentPerformers123.get(which), new ReceiptFormationCallback() {
                                    @Override
                                    public void onSuccess() {
                                        Toast.makeText(MainActivity.this, "Оплата прошла успешно", Toast.LENGTH_LONG).show();
                                    }

                                    @Override
                                    public void onError(ReceiptFormationException e) {
                                        Toast.makeText(MainActivity.this, e.getCode() + " " + e.getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        });
                        builderSingle.show();

 */
                    }
                } catch (IntegrationException e) {
                    e.printStackTrace();
                }
            }
        });
    }


}
