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

    private static final String TAG = "MyApp123";

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
                        setPrintExtras.add(new SetPrintExtra(
                                //Метод, который указывает место, где будут распечатаны данные.
                                //Данные печатаются после клише и до текста “Кассовый чек”
                                new PrintExtraPlacePrintGroupTop(null),
                                //Массив данных, которые требуется распечатать.
                                new IPrintable[]{
                                        //Простой текст
                                        new PrintableText("Proin eget tortor risus. Nulla quis lorem ut libero malesuada feugiat. Proin eget tortor risus."),
                                        //Штрихкод с контрольной суммой если она требуется для выбранного типа штрихкода
                                        new PrintableBarcode("4750232005910", PrintableBarcode.BarcodeType.EAN13),
                                        //Изображение
                                        //new PrintableImage(getBitmapFromAsset("ic_launcher.png"))
                                }
                        ));
                        setPrintExtras.add(new SetPrintExtra(
                                //Данные печатаются после текста “Кассовый чек”, до имени пользователя
                                new PrintExtraPlacePrintGroupHeader(null),
                                new IPrintable[]{
                                        new PrintableBarcode("4750232005910", PrintableBarcode.BarcodeType.EAN13),
                                        new PrintableText("Proin eget tortor risus. Nulla quis lorem ut libero malesuada feugiat. Proin eget tortor risus.")
                                }
                        ));
                        //Добавляем к каждой позиции чека продажи необходимые данные
                        Receipt r = ReceiptApi.getReceipt(MyPrintService.this, Receipt.Type.SELL);
                        if (r != null) {
                            setPrintExtras.add(new SetPrintExtra(
                                    //Данные печатаются после итога и списка оплат, до текста “всего оплачено”
                                    new PrintExtraPlacePrintGroupSummary(null),
                                    new IPrintable[]{
                                            new PrintableText("EXTRA:" + r.getHeader().getExtra()),
                                            new PrintableBarcode("4750232005910", PrintableBarcode.BarcodeType.EAN13),
                                            new PrintableText("Proin eget tortor risus. Nulla quis lorem ut libero malesuada feugiat. Proin eget tortor risus.")
                                    }
                            ));
                            for (Position p : r.getPositions()) {
                                List<ExtraKey> list = new ArrayList<>(p.getExtraKeys());
                                setPrintExtras.add(new SetPrintExtra(
                                        //Данные печатаются в позиции в чеке, до подпозиций
                                        new PrintExtraPlacePositionFooter(p.getUuid()),
                                        new IPrintable[]{
                                                new PrintableBarcode("4750232005910", PrintableBarcode.BarcodeType.EAN13),
                                                new PrintableText("UUID:" + p.getUuid() + "\n EXTRA:" + (list.size() > 0 ? list.get(0).getDescription() : ""))
                                        }
                                ));
                                setPrintExtras.add(new SetPrintExtra(
                                        //Данные печатаются в позиции в чеке, после всех подпозиций
                                        new PrintExtraPlacePositionAllSubpositionsFooter(p.getUuid()),
                                        new IPrintable[]{
                                                new PrintableBarcode("4750232005910", PrintableBarcode.BarcodeType.EAN13),
                                                new PrintableText("<Текст>\n" + p.getUuid() + "\n<Текст>")
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
}