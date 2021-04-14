package com.rein.android.ReynTestApp;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.google.firebase.crashlytics.FirebaseCrashlytics;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import ru.evotor.framework.component.PaymentPerformer;
import ru.evotor.framework.component.PaymentPerformerApi;
import ru.evotor.framework.core.Error;
import ru.evotor.framework.core.IntegrationActivity;
import ru.evotor.framework.core.IntegrationException;
import ru.evotor.framework.core.IntegrationManagerCallback;
import ru.evotor.framework.core.IntegrationManagerFuture;
import ru.evotor.framework.core.action.command.open_receipt_command.OpenSellReceiptCommand;
import ru.evotor.framework.core.action.command.print_receipt_command.PrintReceiptCommandResult;
import ru.evotor.framework.core.action.command.print_receipt_command.PrintSellReceiptCommand;
import ru.evotor.framework.core.action.command.print_z_report_command.PrintZReportCommand;
import ru.evotor.framework.core.action.command.print_z_report_command.PrintZReportCommandResult;
import ru.evotor.framework.core.action.event.receipt.changes.position.PositionAdd;
import ru.evotor.framework.core.action.event.receipt.changes.position.SetExtra;
import ru.evotor.framework.kkt.api.DocumentRegistrationCallback;
import ru.evotor.framework.kkt.api.DocumentRegistrationException;
import ru.evotor.framework.kkt.api.KktApi;
import ru.evotor.framework.navigation.NavigationApi;
import ru.evotor.framework.payment.PaymentSystem;
import ru.evotor.framework.payment.PaymentType;
import ru.evotor.framework.receipt.ExtraKey;
import ru.evotor.framework.receipt.Payment;
import ru.evotor.framework.receipt.Position;
import ru.evotor.framework.receipt.PrintGroup;
import ru.evotor.framework.receipt.Purchaser;
import ru.evotor.framework.receipt.PurchaserType;
import ru.evotor.framework.receipt.Receipt;
import ru.evotor.framework.receipt.ReceiptApi;
import ru.evotor.framework.receipt.SettlementType;
import ru.evotor.framework.receipt.TaxNumber;
import ru.evotor.framework.receipt.correction.CorrectionType;
import ru.evotor.framework.receipt.formation.api.ReceiptFormationCallback;
import ru.evotor.framework.receipt.formation.api.ReceiptFormationException;
import ru.evotor.framework.receipt.formation.api.SellApi;
import ru.evotor.framework.receipt.position.AgentRequisites;
import ru.evotor.framework.receipt.position.SettlementMethod;
import ru.evotor.framework.receipt.position.VatRate;

import static ru.evotor.framework.kkt.api.KktApi.receiveKktSerialNumber;
import static ru.evotor.framework.receipt.TaxationSystem.COMMON;

public class MainActivity extends IntegrationActivity {
    private Activity m_Activity = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        findViewById(R.id.PrintSellReceiptButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // m_Activity.startActivityForResult(NavigationApi.createIntentForSellReceiptEdit(true),
                //        0);


                openReceiptAndEmail();

            }
        });

        findViewById(R.id.CorrectionButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Correction();

            }
        });
        findViewById(R.id.OpenSellReceiptButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String KktNumber = receiveKktSerialNumber(getApplicationContext());
                openReceipt();
                //NavigationApi.createIntentForChangeUser();
                //Toast.makeText(MainActivity.this, KktNumber, Toast.LENGTH_LONG).show();


            }
        });
        findViewById(R.id.WebViewButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //startActivity(new Intent(MainActivity.this, ActivityWebView.class));
                throw new RuntimeException("This is a crash... AHHHHH!!!!");

            }
        });

        findViewById(R.id.HttpRequestButton).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                Thread thread = new Thread() {
                    @Override
                    public void run() {
                        try {
                            putRequestWithHeaderAndBody();
                        } catch (IOException e) {
                            e.printStackTrace();

                        }
                    }
                };
                thread.start();
            }
        });
        findViewById(R.id.CloseSessionButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new PrintZReportCommand().process(MainActivity.this, new IntegrationManagerCallback() {
                    @Override
                    public void run(IntegrationManagerFuture future) {
                        try {
                            IntegrationManagerFuture.Result result = future.getResult();
                            switch (result.getType()) {
                                case OK:
                                    PrintZReportCommandResult printSellReceiptResult = PrintZReportCommandResult.create(result.getData());
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
        });


    }
    /*


     */

    public void putRequestWithHeaderAndBody() throws IOException {

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://webhook.site/6d428723-d100-476c-9910-13b5853036c2")
                .build();

        Response response = client.newCall(request).execute();

        if (!response.isSuccessful()) {
            throw new IOException("Unexpected response code: " + response);
        }
        System.out.println(response.body().string());
    }


    public void SendHttpRequest() throws UnsupportedEncodingException {

        String text = "";
        BufferedReader reader = null;

        // Send data
        try {

            // Defined URL  where to send data
            URL url = new URL("https://webhook.site/6d428723-d100-476c-9910-13b5853036c2");
            String data = URLEncoder.encode("login", "UTF-8")
                    + ":" + URLEncoder.encode("admin", "UTF-8");
            // Send POST data request

            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(data);
            wr.flush();

            // Get the server response

            reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line = null;

            // Read Server Response
            while ((line = reader.readLine()) != null) {
                // Append server response in string
                sb.append(line + "\n");
            }


            text = sb.toString();
        } catch (Exception ex) {
        }
    }


    public void Correction() {
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
                COMMON, CorrectionType.BY_PRESCRIBED, "unknown", "1",
                correctableSettlementDate, new BigDecimal(10).setScale(2, BigDecimal.ROUND_HALF_UP), PaymentType.CASH, VatRate.VAT_20_120,
                "correction", callback);
    }

    public void openReceiptAndEmail() {

        //Дополнительное поле для позиции. В списке наименований расположено под количеством и выделяется синим цветом
        Set<ExtraKey> set = new HashSet<>();
        set.add(new ExtraKey(null, "31138179-5106-4084-8ea1-17039ea9bf6e", "123"));
        //Создание списка товаров чека
        List<Position> list = new ArrayList<>();
        List<String> phones = new ArrayList<>();
        phones.add("89631654555");
        //позиция 1
        list.add(
                Position.Builder.newInstance(
                        //UUID позиции
                        UUID.randomUUID().toString(),
                        //UUID товара
                        UUID.randomUUID().toString(),
                        //Наименование
                        "123Товар321",
                        //Наименование единицы измерения
                        "шт",
                        //Точность единицы измерения
                        0,
                        //Цена без скидок
                        new BigDecimal(2000),
                        //Количество
                        new BigDecimal(1)
                )

                        .setSettlementMethod(new SettlementMethod.Lend())
                        .setTaxNumber(TaxNumber.VAT_18_118)
                        .setExtraKeys(set) //Установка Extra-информации. Данные будут напечатаны на чеке
                        //Установка цены с учетом скидки:
                        //.setPriceWithDiscountPosition(new BigDecimal(500))
                        .build()
        );


        HashMap payments = new HashMap<Payment, BigDecimal>();
        //установка способа оплаты
        //1
        payments.put(new Payment(
                "32dc62ce-9583-41ad-b88c-ac16a6fdf1ae",
                new BigDecimal(1000),
                null,
                new PaymentPerformer(
                        new PaymentSystem(PaymentType.ADVANCE, "Internet", null),
                        "com.rein.android.ReynTestApp",
                        "название компонента",
                        "app_uuid",
                        "appName"
                ),
                null,
                null,
                null
        ), new BigDecimal(1000));

        //2
        payments.put(new Payment(
                "0a31820d-0885-49e1-ada1-467616fb61f9",
                new BigDecimal(1000),
                null,
                new PaymentPerformer(
                        new PaymentSystem(PaymentType.CASH, "Internet", null),
                        "com.rein.android.ReynTestApp",
                        "название компонента",
                        "app_uuid",
                        "appName"
                ),
                null,
                null,
                null
        ), new BigDecimal(1000));


        Purchaser firstLegalEntity = new Purchaser(
                //Наименование покупателя, например, название организации. Данные сохраняются в теге 1227 фискального документа.
                "Privet",
                //Номер документа покупателя, например, ИНН или номер паспорта иностранного гражданина. Данные сохраняются в теге 1228 фискального документа.
                "606053449439",
                //Тип покупателя, например, юр. лицо. Не сохраняется в фискальном документе.
                PurchaserType.LEGAL_ENTITY);

        PrintGroup printGroup = new PrintGroup(UUID.randomUUID().toString(),
                PrintGroup.Type.CASH_RECEIPT, null, null, "адрессссссс",
                null, true, firstLegalEntity, null);
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
                "+79776020339",
                "room085@gmail.com",
                null,
                null,
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
        set.add(new ExtraKey(null, "31138179-5106-4084-8ea1-17039ea9bf6e", "Абракадабра123987"));

        positionAddList.add(
                new PositionAdd(
                        Position.Builder.newInstance(
                                //UUID позиции
                                UUID.randomUUID().toString(),
                                //UUID товара
                                UUID.randomUUID().toString(),
                                //Наименование
                                "Товар1",
                                //Наименование единицы измерения
                                "кг",
                                //Точность единицы измерения
                                0,
                                //Цена без скидок
                                new BigDecimal(30000),
                                //Количество
                                BigDecimal.valueOf(1,1)
                                //Добавление цены с учетом скидки на позицию. Итог = price - priceWithDiscountPosition
                        )
                                //.setExtraKeys(set) //Extras
                                //.setAgentRequisites(AgentRequisites.createForAgent("070704218872", Collections.singletonList("79776030448")))
                                //Добавление цены с учетом скидки на позицию. Итог = price - priceWithDiscountPosition
                                .setPriceWithDiscountPosition(new BigDecimal(20000))
                                .build()

                )
        );
        positionAddList.add(
                new PositionAdd(
                        Position.Builder.newInstance(
                                //UUID позиции
                                UUID.randomUUID().toString(),
                                //UUID товара
                                UUID.randomUUID().toString(),
                                //Наименование
                                "Товар1",
                                //Наименование единицы измерения
                                "кг",
                                //Точность единицы измерения
                                0,
                                //Цена без скидок
                                new BigDecimal(30000),
                                //Количество
                                BigDecimal.valueOf( 1)
                                //Добавление цены с учетом скидки на позицию. Итог = price - priceWithDiscountPosition
                        )
                                //.setExtraKeys(set) //Extras
                                //.setAgentRequisites(AgentRequisites.createForAgent("070704218872", Collections.singletonList("79776030448")))
                                //Добавление цены с учетом скидки на позицию. Итог = price - priceWithDiscountPosition
                                .setPriceWithDiscountPosition(new BigDecimal(20000))
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
                                        String Receipt_uuid = MyReceipt124.getHeader().getUuid();
                                        Toast.makeText(MainActivity.this, Receipt_uuid, Toast.LENGTH_LONG).show();

                                        Intent intent = new Intent("evotor.intent.action.payment.SELL");

                                               startActivity(intent);
/*

                ////////////////////SellAPI////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


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

                //////////////////////////SellAPI////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
*/

                    }
                } catch (IntegrationException e) {
                    e.printStackTrace();
                }
            }
        });
    }


}
