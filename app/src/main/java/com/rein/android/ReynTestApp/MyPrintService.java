package com.rein.android.ReynTestApp;


import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.nfc.Tag;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import ru.evotor.devices.commons.DeviceServiceConnector;
import ru.evotor.devices.commons.printer.printable.IPrintable;
import ru.evotor.devices.commons.printer.printable.PrintableBarcode;
import ru.evotor.devices.commons.printer.printable.PrintableImage;
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
import ru.evotor.framework.receipt.print_extras.PrintExtraPlacePositionAllSubpositionsFooter;
import ru.evotor.framework.receipt.print_extras.PrintExtraPlacePositionFooter;
import ru.evotor.framework.receipt.print_extras.PrintExtraPlacePrintGroupHeader;
import ru.evotor.framework.receipt.print_extras.PrintExtraPlacePrintGroupSummary;
import ru.evotor.framework.receipt.print_extras.PrintExtraPlacePrintGroupTop;

import static ru.evotor.devices.commons.Constants.DEFAULT_DEVICE_INDEX;


public class MyPrintService extends IntegrationService {
    /**
     * Получение картинки из каталога asset приложения
     *
     * @param fileName имя файла
     * @return значение типа Bitmap
     */

    private static final String TAG = "MyApp123";
    public static final Locale LOCALE_DEC = Locale.US;
    private DecimalFormat mDecimalFormat = new DecimalFormat("0.00", DecimalFormatSymbols.getInstance(LOCALE_DEC));

    @Nullable
    @Override
    protected Map<String, ActionProcessor> createProcessors() {
        Map<String, ActionProcessor> map = new HashMap<>();
        map.put(
                PrintExtraRequiredEvent.NAME_SELL_RECEIPT,
                new PrintExtraRequiredEventProcessor() {
                    @Override
                    public void call(@NotNull String s, @NotNull PrintExtraRequiredEvent printExtraRequiredEvent, @NotNull Callback callback) {
                        List<SetPrintExtra> setPrintExtras = new ArrayList<>();
                        Receipt MyReceipt124 = ReceiptApi.getReceipt(MyPrintService.this, Receipt.Type.SELL);
                        Log.d(TAG, "Uuid чека: "+MyReceipt124.getHeader().getNumber());

                        setPrintExtras.add(new SetPrintExtra(
                                //Метод, который указывает место, где будут распечатаны данные.
                                //Данные печатаются после клише и до текста “Кассовый чек”
                                new PrintExtraPlacePrintGroupTop(null),
                                //Массив данных, которые требуется распечатать.
                                new IPrintable[]{
                                        //Простой текст
                                        new PrintableText("Proin eget tortor risus. Nulla quis lorem ut libero malesuada feugiat. Proin eget tortor risus."),
                                        //Штрихкод с контрольной суммой если она требуется для выбранного типа штрихкода
                                       // new PrintableBarcode("4750232005910", PrintableBarcode.BarcodeType.EAN13),
                                        //Изображение
                                       // new PrintableImage(getBitmapFromAsset("ic_launcher.png"))
                                }
                        ));
                        setPrintExtras.add(new SetPrintExtra(
                                //Данные печатаются после текста “Кассовый чек”, до имени пользователя
                                new PrintExtraPlacePrintGroupHeader(null),
                                new IPrintable[]{
                                        //new PrintableBarcode("4750232005910", PrintableBarcode.BarcodeType.EAN13),
                                        //new PrintableText("Proin eget tortor risus. Nulla quis lorem ut libero malesuada feugiat. Proin eget tortor risus.")
                                }
                        ));
                        //Добавляем к каждой позиции чека продажи необходимые данные
                        Receipt r = ReceiptApi.getReceipt(MyPrintService.this, Receipt.Type.SELL);
                        if (r != null) {
                            BigDecimal discount = r.getDiscount();
                            if (!BigDecimal.ZERO.equals(discount)) {
                                int width = 0;
                                try {
                                    width = DeviceServiceConnector.getPrinterService().getAllowableSymbolsLineLength(DEFAULT_DEVICE_INDEX);
                                } catch (Throwable e) {

                                }

                                StringBuilder resultDiscount = getString(discount, width, "СКИДКА НА ЧЕК");
                                setPrintExtras.add(new SetPrintExtra(
                                        new PrintExtraPlacePrintGroupSummary(null),
                                        new IPrintable[]{
                                             //  new PrintableText(resultSum.toString()),
                                                new PrintableText(resultDiscount.toString())
                                        }
                                ));
                            }
                        }

                        try {
                            callback.onResult(new PrintExtraRequiredEventResult(setPrintExtras).toBundle());
                        } catch (RemoteException exc) {
                            exc.printStackTrace();
                        }
                    }
                }

        );
        return map;
    }
    @NonNull
    private StringBuilder getString(BigDecimal discount, int width, String text) {
        StringBuilder resultDiscount = new StringBuilder("=")
                .append(mDecimalFormat.format(discount)
                        .replaceAll(",", "."));
        if (width > 0) {
            int spacesLength = width - resultDiscount.length() - text.length();
            for (int i = 0; i < spacesLength; i++) {
                resultDiscount.insert(0, " ");
            }
            resultDiscount.insert(0, text);
        } else {
            resultDiscount.insert(0, text + " ");
        }
        return resultDiscount;
    }

    @NonNull
    private StringBuilder getSumString(double price, int width, String text) {
        StringBuilder resultDiscount = new StringBuilder("=")
                .append(mDecimalFormat.format(price).replaceAll(",", "."));
        if (width > 0) {
            int spacesLength = width - resultDiscount.length() - text.length();
            for (int i = 0; i < spacesLength; i++) {
                resultDiscount.insert(0, " ");
            }
            resultDiscount.insert(0, text);
        } else {
            resultDiscount.insert(0, text + " ");
        }
        return resultDiscount;
    }
}