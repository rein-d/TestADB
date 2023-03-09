package com.rein.android.ReynTestApp;


import static ru.evotor.framework.kkt.api.KktApi.receiveKktSerialNumber;
import static ru.evotor.framework.receipt.TaxationSystem.COMMON;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import ru.evotor.framework.component.PaymentPerformer;
import ru.evotor.framework.component.PaymentPerformerApi;
import ru.evotor.framework.core.Error;
import ru.evotor.framework.core.IntegrationActivity;
import ru.evotor.framework.core.IntegrationAppCompatActivity;
import ru.evotor.framework.core.IntegrationException;
import ru.evotor.framework.core.IntegrationManagerCallback;
import ru.evotor.framework.core.IntegrationManagerFuture;
import ru.evotor.framework.core.action.command.open_receipt_command.OpenSellReceiptCommand;
import ru.evotor.framework.core.action.command.print_receipt_command.PrintCorrectionIncomeReceiptCommand;
import ru.evotor.framework.core.action.command.print_receipt_command.PrintReceiptCommandResult;
import ru.evotor.framework.core.action.command.print_receipt_command.PrintSellReceiptCommand;
import ru.evotor.framework.core.action.command.print_z_report_command.PrintZReportCommand;
import ru.evotor.framework.core.action.command.print_z_report_command.PrintZReportCommandResult;
import ru.evotor.framework.core.action.event.receipt.changes.position.PositionAdd;

import ru.evotor.framework.core.action.event.receipt.changes.receipt.SetExtra;
import ru.evotor.framework.fs.api.FsApi;

import ru.evotor.framework.kkt.api.DocumentRegistrationCallback;
import ru.evotor.framework.kkt.api.DocumentRegistrationException;
import ru.evotor.framework.kkt.api.KktApi;

import ru.evotor.framework.navigation.NavigationApi;
import ru.evotor.framework.payment.PaymentSystem;
import ru.evotor.framework.payment.PaymentType;
import ru.evotor.framework.receipt.DocumentType;
import ru.evotor.framework.receipt.ExtraKey;
import ru.evotor.framework.receipt.FiscalReceipt;
import ru.evotor.framework.receipt.Measure;
import ru.evotor.framework.receipt.Payment;
import ru.evotor.framework.receipt.Position;
import ru.evotor.framework.receipt.PrintGroup;
import ru.evotor.framework.receipt.Purchaser;
import ru.evotor.framework.receipt.PurchaserType;
import ru.evotor.framework.receipt.Receipt;
import ru.evotor.framework.receipt.ReceiptApi;
import ru.evotor.framework.receipt.SettlementType;
import ru.evotor.framework.receipt.TaxationSystem;
import ru.evotor.framework.receipt.correction.CorrectionType;
import ru.evotor.framework.receipt.formation.api.ReceiptFormationCallback;
import ru.evotor.framework.receipt.formation.api.ReceiptFormationException;
import ru.evotor.framework.receipt.formation.api.SellApi;
import ru.evotor.framework.receipt.position.VatRate;
import ru.evotor.query.Cursor;


public class MainActivity extends IntegrationActivity {
    public static final String TAG = "corp.remotehelp.integration.evotor.ru.servicePack";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "Activity Created");

        findViewById(R.id.PrintSellReceiptButton).setOnClickListener(view -> {
            // m_Activity.startActivityForResult(NavigationApi.createIntentForSellReceiptEdit(true),0);
            openReceiptAndEmail();
        });

        findViewById(R.id.CorrectionButton).setOnClickListener(view -> newCorrectionFFD12());

        findViewById(R.id.OpenSellReceiptButton).setOnClickListener(view -> {
            String KktNumber = receiveKktSerialNumber(getApplicationContext());
            openReceipt();
            //NavigationApi.createIntentForChangeUser();
            //Toast.makeText(MainActivity.this, KktNumber, Toast.LENGTH_LONG).show();


        });

        findViewById(R.id.WebViewButton).setOnClickListener(view -> {

            Log.v(TAG, "Hello this is log");
//            List<TaxationSystem> taxSystems = getFsDoc(getApplicationContext());
//
//            String firstTaxSystem = String.valueOf(taxSystems.get(0));
//
//            if (taxSystems.isEmpty()) {
//                Log.d("fsAPI", "Empty!!");
//                Toast.makeText(this, "Empty!!", Toast.LENGTH_SHORT).show();
//            } else {
//                Log.d("fsAPI", String.valueOf(taxSystems.get(0)));
//                Toast.makeText(this, firstTaxSystem, Toast.LENGTH_SHORT).show();
//            }
//            Cursor<FiscalReceipt> receipts = ReceiptApi.getFiscalReceipts(getApplicationContext(), "1c2353d8-2282-4505-a5d4-23421e0f9469");
//            Log.d(TAG, "Reg Number from receipt: " + receipts.getValue().getKktRegistrationNumber());
//            Log.d(TAG, "KKRRegNumber: " + KktApi.receiveKktRegNumber(getApplicationContext()));

        });

        findViewById(R.id.CloseSessionButton).setOnClickListener(view -> new PrintZReportCommand().process(MainActivity.this, new IntegrationManagerCallback() {
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
        }));


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(getPackageName(), "Activity Destroyed");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.v(getPackageName(), "Activity Paused");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("com.rein.android.ReynTestApp", "Activity Resumed");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.v("com.rein.android.ReynTestApp", "Activity Restarted");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("DestroyTest", "Activity Stopped");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("DestroyTest", "Activity Started");
    }

    public List<TaxationSystem> getFsDoc(Context context) {
        FsApi.GetFsFiscalizationDocumentResult.Success fsDoc = (FsApi.GetFsFiscalizationDocumentResult.Success) FsApi.getLastFsFiscalizationDocument(context);
        return fsDoc.getDocument().getTaxationSystemsList();
    }

    ;


    public void newCorrectionFFD12() {

        Date correctableSettlementDate = Calendar.getInstance().getTime(); //Дата коррекции
        List<Position> positions = new ArrayList<>(); //Список позиций
        HashMap payments = new HashMap<Payment, BigDecimal>(); //Список оплат
        ArrayList<Receipt.PrintReceipt> listDocs = new ArrayList<>(); //Список печатных документов

        //Добавляем позиции в список
        positions.add(
                Position.Builder.newInstance(
                                //UUID позиции
                                UUID.randomUUID().toString(),
                                //UUID товара
                                UUID.randomUUID().toString(),
                                //Наименование
                                UUID.randomUUID().toString(),
                                //Наименование единицы измерения
                                new Measure("шт", 0, 0),
                                //Цена без скидок
                                new BigDecimal(1000),
                                //Количество
                                new BigDecimal(1)
                        )
                        //.setPriceWithDiscountPosition()
                        .build()
        );

        positions.add(
                Position.Builder.newInstance(
                                //UUID позиции
                                UUID.randomUUID().toString(),
                                //UUID товара
                                UUID.randomUUID().toString(),
                                //Наименование
                                UUID.randomUUID().toString(),
                                //Наименование единицы измерения
                                new Measure("шт", 0, 0),
                                //Цена без скидок
                                new BigDecimal(1000),
                                //Количество
                                new BigDecimal(1)
                        )
                        //.setPriceWithDiscountPosition()
                        .build()
        );

        //Создаем оплату
        Payment payment1 = new Payment(
                UUID.randomUUID().toString(),
                new BigDecimal(2000),
                null,
                new PaymentPerformer(
                        new PaymentSystem(PaymentType.ELECTRON, "", UUID.randomUUID().toString()),
                        null,
                        null,
                        null,
                        null
                ),
                null,
                null,
                null
        );
        //Добавляем оплату в список
        payments.put(payment1, new BigDecimal(2000));

        //Создаем печатную группу
        PrintGroup printGroup = new PrintGroup(UUID.randomUUID().toString(),
                PrintGroup.Type.CASH_RECEIPT, null, null, null,
                null, true, null, null);

        //Создаем документ к печати
        Receipt.PrintReceipt printReceipt = new Receipt.PrintReceipt(
                printGroup,
                positions,
                payments,
                new HashMap<Payment, BigDecimal>(),
                new HashMap<String, BigDecimal>()
        );
        //Добавляем документ в список
        listDocs.add(printReceipt);

        //Выполняем команду для печати
        new PrintCorrectionIncomeReceiptCommand(
                listDocs,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                correctableSettlementDate,
                CorrectionType.BY_SELF,
                "тест коррекции"
        ).process(MainActivity.this, new IntegrationManagerCallback() {
            @Override
            public void run(IntegrationManagerFuture future) {
                try {
                    IntegrationManagerFuture.Result result = future.getResult();
                    switch (result.getType()) {
                        case OK:
                            PrintReceiptCommandResult printCorrectionIncomeReceiptResult = PrintReceiptCommandResult.create(result.getData());
                            break;
                        case ERROR:
                            Toast.makeText(MainActivity.this, result.getError().getMessage(), Toast.LENGTH_LONG).show();
                            Error error = result.getError();
                            Log.i(getPackageName(), "e.code: " + error.getCode() + "; e.msg: " + error.getMessage());
                            break;
                    }
                } catch (IntegrationException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    public void Correction() {
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
        Date correctableSettlementDate = Calendar.getInstance().getTime();
        try {
            correctableSettlementDate = format.parse("26.09.2021");
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

        KktApi.registerCorrectionReceipt(getApplicationContext(), SettlementType.OUTCOME,
                COMMON, CorrectionType.BY_SELF, "unknown", "1",
                correctableSettlementDate, new BigDecimal(10).setScale(2, BigDecimal.ROUND_HALF_UP), PaymentType.CASH, VatRate.VAT_20_120,
                "correction", callback);
    }

    public void openReceiptAndEmail() {

        //Дополнительное поле для позиции. В списке наименований расположено под количеством и выделяется синим цветом
        Set<ExtraKey> set = new HashSet<>();
        set.add(new ExtraKey(null, "31138179-5106-4084-8ea1-17039ea9bf6e", "123"));
        //Создание списка товаров чека
        List<Position> list = new ArrayList<>();

        String principalInn = "070704218872";
        List<String> phones = new ArrayList<>();
        phones.add("89631654555");

        // for (int i = 1; i < 2; i++) {
        //позиция 1
        list.add(
                Position.Builder.newInstance(
                                //UUID позиции
                                UUID.randomUUID().toString(),
                                //UUID товара
                                null,
                                //Наименование
                                "Товар",
                                //Наименование единицы измерения
                                new Measure("шт", 0, 0),
                                //Цена без скидок
                                new BigDecimal(1000),
                                //Количество
                                new BigDecimal(1)
                        )
                        //.setSettlementMethod(new SettlementMethod.Lend())
                        //.setAgentRequisites(AgentRequisites.createForAgent(principalInn, phones))
                        //.setMark("1234983784289efuiafa930a940939jfe")
                        .build()

        );
        //}
        HashMap payments = new HashMap<Payment, BigDecimal>();
        //установка способа оплаты
        //1
        Payment payment1 = new Payment(
                UUID.randomUUID().toString(),
                new BigDecimal(1000),
                null,
                new PaymentPerformer(
                        new PaymentSystem(PaymentType.ELECTRON, "", ""),
                        null,
                        null,
                        null,
                        null
                ),
                null,
                null,
                null
        );
        payments.put(payment1, new BigDecimal(1000));

        Purchaser purchaser = new Purchaser("Иванов Иван", null, new Date(),
                DocumentType.PASSPORT_RF, "4511161107", PurchaserType.NATURAL_PERSON);

        PrintGroup printGroup = new PrintGroup(UUID.randomUUID().toString(),
                PrintGroup.Type.CASH_RECEIPT, null, null, null,
                null, true, purchaser, null);
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
                            Log.d("ReynLogs", "payment id: " + payment1.getUuid().toString());
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
        List<PositionAdd> nullPositionAddList = new ArrayList<>();
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
                                        "Услуга",
                                        //Наименование единицы измерения
                                        new Measure("шт", 0, 0),
                                        //Цена без скидок
                                        new BigDecimal(30000),
                                        //Количество
                                        BigDecimal.valueOf(1)
                                        //Добавление цены с учетом скидки на позицию. Итог = price - priceWithDiscountPosition
                                )
                                //.setSettlementMethod(new SettlementMethod.Lend())
                                //.setExtraKeys(set) //Extras
                                //.setAgentRequisites(AgentRequisites.createForAgent("070704218872", Collections.singletonList("79776030448")))
                                //Добавление цены с учетом скидки на позицию. Итог = price - priceWithDiscountPosition
                                //.setPriceWithDiscountPosition(new BigDecimal(20000))
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
//        new OpenSellReceiptCommand(positionAddList, null, null).process(this, future -> {
//
//            try {
//                IntegrationManagerFuture.Result result = future.getResult();
//                if (result.getType() == IntegrationManagerFuture.Result.Type.OK) {
//
//                    startActivity(new Intent("evotor.intent.action.payment.SELL"));
        //startActivity(NavigationApi.createIntentForSellReceiptEdit(false));


        ////////////////////SellAPI////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

//                        PaymentPerformer creditPaymentPerformer = new PaymentPerformer(
//                                new PaymentSystem(
//                                        PaymentType.CREDIT,
//                                        "Кредит",
//                                        "ru.evotor.paymentSystem.credit.base"
//                                ),
//                                null,
//                                null,
//                                null,
//                                "Кредит"
//                        );
//                        SellApi.moveCurrentReceiptDraftToPaymentStage(MainActivity.this, creditPaymentPerformer, new ReceiptFormationCallback() {
//                            @Override
//                            public void onSuccess() {
//                                Toast.makeText(MainActivity.this, "Передаем чек на оплату", Toast.LENGTH_LONG).show();
//                            }
//
//                            @Override
//                            public void onError(ReceiptFormationException e) {
//                                Toast.makeText(MainActivity.this, e.getCode() + " " + e.getMessage(), Toast.LENGTH_LONG).show();
//                            }
//                        });

        //Получаем текущий открытый чек.
        Receipt receipt = ReceiptApi.getReceipt(MainActivity.this, Receipt.Type.SELL);
        if (receipt == null) return;
        //Получаем идентификатор чека.
        String uuid = receipt.getHeader().getUuid();
        if (uuid == null) return;
        //Создаём список всех компонентов, способных выполнить оплату.
        List<PaymentPerformer> paymentPerformers = PaymentPerformerApi.INSTANCE.getAllPaymentPerformers(getPackageManager());
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(MainActivity.this);

        //Показываем пользователю диалоговое окно с возможностью выбрать исполнителя платежа, например, Наличными или Банковской картой.
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.select_dialog_singlechoice);
        for (int i = 0; i < paymentPerformers.size(); i++) {
            arrayAdapter.add(paymentPerformers.get(i).getPaymentSystem().getUserDescription());
        }

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            //По выбору пользователя выполняем оплату и печатаем чек.
            public void onClick(DialogInterface dialog, int which) {
                SellApi.moveCurrentReceiptDraftToPaymentStage(MainActivity.this, paymentPerformers.get(which), new ReceiptFormationCallback() {
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


//                }
//            } catch (IntegrationException e) {
//                e.printStackTrace();
//            }
//        });
    }

    public void trustAllCertificates() {
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        public X509Certificate[] getAcceptedIssuers() {
                            X509Certificate[] myTrustedAnchors = new X509Certificate[0];
                            return myTrustedAnchors;
                        }

                        @Override
                        public void checkClientTrusted(X509Certificate[] certs, String authType) {
                        }

                        @Override
                        public void checkServerTrusted(X509Certificate[] certs, String authType) {
                        }
                    }
            };

            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String arg0, SSLSession arg1) {
                    return true;
                }
            });
        } catch (Exception e) {
        }
    }

   /* @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        View mDecorView = getWindow().getDecorView();
        if (hasFocus) {
            mDecorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }*/
}
