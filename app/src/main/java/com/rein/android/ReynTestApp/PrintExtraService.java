package com.rein.android.ReynTestApp;


import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.RemoteException;
import android.widget.Toast;

import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.evotor.devices.commons.printer.printable.IPrintable;
import ru.evotor.devices.commons.printer.printable.PrintableText;
import ru.evotor.framework.core.IntegrationService;
import ru.evotor.framework.core.action.event.receipt.changes.receipt.print_extra.SetPrintExtra;
import ru.evotor.framework.core.action.event.receipt.print_extra.PrintExtraRequiredEvent;
import ru.evotor.framework.core.action.event.receipt.print_extra.PrintExtraRequiredEventProcessor;
import ru.evotor.framework.core.action.event.receipt.print_extra.PrintExtraRequiredEventResult;
import ru.evotor.framework.core.action.processor.ActionProcessor;
import ru.evotor.framework.receipt.print_extras.PrintExtraPlacePrintGroupHeader;
import ru.evotor.framework.receipt.print_extras.PrintExtraPlacePrintGroupTop;


public class PrintExtraService extends IntegrationService {

    private Bitmap getBitmapFromAsset(String fileName) {
        AssetManager assetManager = getAssets();
        InputStream stream = null;
        try {
            stream = assetManager.open(fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return BitmapFactory.decodeStream(stream);
    }


    @Nullable
    @Override
    protected Map<String, ActionProcessor> createProcessors() {
        Toast.makeText(getApplicationContext(), "!!!PRINT!!!", Toast.LENGTH_LONG);
        Map<String, ActionProcessor> map = new HashMap<>();
        PrintExtraRequiredEventProcessor put = (PrintExtraRequiredEventProcessor) map.put(
                PrintExtraRequiredEvent.NAME_SELL_RECEIPT,
                new PrintExtraRequiredEventProcessor() {
                    @Override
                    public void call(String s, PrintExtraRequiredEvent printExtraRequiredEvent, Callback callback) {
                        List<SetPrintExtra> setPrintExtras = new ArrayList<SetPrintExtra>();
                        setPrintExtras.add(new SetPrintExtra(
                                new PrintExtraPlacePrintGroupTop(null),
                                new IPrintable[]{
                                        new PrintableText("SomeText"),
                                       // new PrintableImage(getBitmapFromAsset("ic_launcher.png"))
                                }
                        ));
                        setPrintExtras.add(new SetPrintExtra(
                                new PrintExtraPlacePrintGroupHeader(null),
                                new IPrintable[]{
                                        new PrintableText("SomeText"),
                                       // new PrintableBarcode("1234567", PrintableBarcode.BarcodeType.CODE39)
                                }
                        ));

                        try {
                            callback.onResult(new PrintExtraRequiredEventResult(setPrintExtras).toBundle());
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }

                    }
                }
        );
        return map;
    }
}