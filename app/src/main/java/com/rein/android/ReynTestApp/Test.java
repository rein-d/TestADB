package com.rein.android.ReynTestApp;


import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import ru.evotor.devices.commons.printer.printable.IPrintable;
import ru.evotor.devices.commons.printer.printable.PrintableBarcode;
import ru.evotor.devices.commons.printer.printable.PrintableText;
import ru.evotor.framework.core.IntegrationService;
import ru.evotor.framework.core.action.event.receipt.changes.receipt.print_extra.SetPrintExtra;
import ru.evotor.framework.core.action.event.receipt.print_extra.PrintExtraRequiredEvent;
import ru.evotor.framework.core.action.event.receipt.print_extra.PrintExtraRequiredEventProcessor;
import ru.evotor.framework.core.action.event.receipt.print_extra.PrintExtraRequiredEventResult;
import ru.evotor.framework.core.action.processor.ActionProcessor;

import ru.evotor.framework.receipt.ExtraKey;
import ru.evotor.framework.receipt.Position;
import ru.evotor.framework.receipt.Receipt;
import ru.evotor.framework.receipt.ReceiptApi;
import ru.evotor.framework.receipt.print_extras.PrintExtraPlace;
import ru.evotor.framework.receipt.print_extras.PrintExtraPlacePositionAllSubpositionsFooter;
import ru.evotor.framework.receipt.print_extras.PrintExtraPlacePositionFooter;
import ru.evotor.framework.receipt.print_extras.PrintExtraPlacePrintGroupHeader;
import ru.evotor.framework.receipt.print_extras.PrintExtraPlacePrintGroupSummary;
import ru.evotor.framework.receipt.print_extras.PrintExtraPlacePrintGroupTop;
import ru.evotor.framework.receipt.print_extras.PrintExtraPlaceType;

/**
 * Печать внутри кассового чека продажи
 * В манифесте добавить права <uses-permission android:name="ru.evotor.permission.receipt.printExtra.SET" />
 * В манифесте для сервиса указать:
 * - печать внутри кассового чека продажи <action android:name="evo.v2.receipt.sell.printExtra.REQUIRED" />
 * Штрихкод должен быть с контрольной суммой
 */

/*
Receipt(header=Header(uuid=7d2a1b38-d5e1-4a64-89e0-4a8f0a77a027, number=null, type=SELL, date=null, clientEmail=null, clientPhone=null, extra={}), printDocuments=[PrintReceipt(printGroup=null, positions=[Position{uuid='522fbf3b-7b45-4275-8c4f-36182c05cb11', productUuid='dc263ee3-0782-4f7a-ab17-b6a197c1b1ce', productCode='null', productType=NORMAL, name='Позиция по свободной цене', measureName='шт', measurePrecision=0, taxNumber=NO_VAT, price=200, priceWithDiscountPosition=200, quantity=1, barcode='null', mark='null', alcoholByVolume=0, alcoholProductKindCode=0, tareVolume=0, extraKeys=[], subPositions=[], attributes=null, settlementMethod=ru.evotor.framework.receipt.position.SettlementMethod$FullSettlement@f6b5f15, agentRequisites=null}], payments={}, changes={}, discounts={522fbf3b-7b45-4275-8c4f-36182c05cb11=0}), PrintReceipt(printGroup=ru.evotor.framework.receipt.PrintGroup@d2675efa, positions=[], payments={Payment(uuid='50644d3b-8dda-42e5-9d47-1faf98bcd682', value=200, paymentSystem=PaymentSystem{paymentType=ELECTRON, userDescription='Банковская карта', paymentSystemId='ru.evotor.paymentSystem.cashless.base'}, paymentPerformer=ru.evotor.framework.component.PaymentPerformer@8208fe94, purposeIdentifier=null, accountId=null, accountUserDescription=Банковская карта, identifier=null)=200}, changes={Payment(uuid='50644d3b-8dda-42e5-9d47-1faf98bcd682', value=200, paymentSystem=PaymentSystem{paymentType=ELECTRON, userDescription='Банковская карта', paymentSystemId='ru.evotor.paymentSystem.cashless.base'}, paymentPerformer=ru.evotor.framework.component.PaymentPerformer@8208fe94, purposeIdentifier=null, accountId=null, accountUserDescription=Банковская карта, identifier=null)=0}, discounts={522fbf3b-7b45-4275-8c4f-36182c05cb11=0})])
D: [LogViewerHelper:to:22]: uuid = 7d2a1b38-d5e1-4a64-89e0-4a8f0a77a027
D: [LogViewerHelper:to:22]: MyPrintService JSONException = No value for descT
 */

public class Test extends IntegrationService {
    private final String TAG = Test.class.getSimpleName();

    private String descT;
    private String descB;

    @Nullable
    @Override
    protected Map<String, ActionProcessor> createProcessors() {

        Map<String, ActionProcessor> map = new HashMap<>();
        // Получить текущие значения из заказа
        //logViewerHelper.to("createProcessors ");

//Получаем текущий открытый чек.
        Receipt receipt = ReceiptApi.getReceipt(Test.this, Receipt.Type.SELL);
        //logViewerHelper.to("receipt = "+receipt);
        if (receipt == null) return map;
        Toast.makeText(Test.this, "getHeader = " + receipt.getHeader().getExtra(), Toast.LENGTH_LONG).show();
     //   logViewerHelper.to("getHeader = " + receipt.getHeader().getExtra());
        try {
            if (receipt.getHeader().getExtra() != null) {
                JSONObject jsonObject = new JSONObject(receipt.getHeader().getExtra());
                if (jsonObject != null) {
                    descT = jsonObject.getString("descT");
                    descB = jsonObject.getString("descB");
                  //  logViewerHelper.to("descT = " + descT + " descB = " + descB);
                    //Toast.makeText(MyPrintService.this, "uuid = " + uuid + " descT = " + descT, Toast.LENGTH_LONG).show();
                }
            }
        } catch (JSONException e) {
         //   logViewerHelper.to("MyPrintService JSONException = " + e.getMessage());
            //e.printStackTrace();
        }

        map.put(
                PrintExtraRequiredEvent.NAME_SELL_RECEIPT,
                new PrintExtraRequiredEventProcessor() {
                    @Override
                    public void call(@NotNull String s, @NotNull PrintExtraRequiredEvent printExtraRequiredEvent, @NotNull Callback callback) {
                        List<SetPrintExtra> setPrintExtras = new ArrayList<>();
                        setPrintExtras.add(new SetPrintExtra(
                                //Метод, который указывает место, где будут распечатаны данные.
                                //Данные печатаются после клише и до текста “Кассовый чек”
                                new PrintExtraPlacePrintGroupTop(null),
                                //Массив данных, которые требуется распечатать.
                                new IPrintable[]{
                                        //Простой текст
                                        new PrintableText(descT + " \n \n"),
                                }
                        ));

                        //Добавляем к каждой позиции чека продажи необходимые данные

                        setPrintExtras.add(new SetPrintExtra(
                                //Метод, который указывает место, где будут распечатаны данные.
                                new PrintExtraPlacePrintGroupSummary(null),
                                //Массив данных, которые требуется распечатать.
                                new IPrintable[]{
                                        //Наименование организации, Адрес, телефон, email
                                        new PrintableText("\n\n" + descB)
                                }
                        ));

                        try {
                            Bundle result = new PrintExtraRequiredEventResult(setPrintExtras).toBundle();
                          //  logViewerHelper.to("MyPrintService result = " + result.toString());
                            // TODO Получить событие
                            callback.onResult(result);

                        } catch (RemoteException exc) {
                            //logViewerHelper.to("MyPrintService err = " + exc.toString());
                            //exc.printStackTrace();
                        }
                    }
                }
        );
        return map;
    }
}

