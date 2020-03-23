package com.rein.android.ReynTestApp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import ru.evotor.devices.commons.ConnectionWrapper;
import ru.evotor.devices.commons.Constants;
import ru.evotor.devices.commons.DeviceServiceConnector;
import ru.evotor.devices.commons.exception.DeviceServiceException;
import ru.evotor.devices.commons.printer.PrinterDocument;
import ru.evotor.devices.commons.printer.printable.PrintableText;
import ru.evotor.devices.commons.services.IPrinterServiceWrapper;
import ru.evotor.devices.commons.services.IScalesServiceWrapper;

public class PrintActivity extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, Intent intent) {

        DeviceServiceConnector.startInitConnections(context.getApplicationContext());
        DeviceServiceConnector.addConnectionWrapper(new ConnectionWrapper() {


            @Override
            public void onPrinterServiceConnected(IPrinterServiceWrapper printerService) {

                MyTask printTask = new MyTask();
                printTask.execute();

            }

            @Override
            public void onPrinterServiceDisconnected() {
            }

            @Override
            public void onScalesServiceConnected(IScalesServiceWrapper scalesService) {

            }

            @Override
            public void onScalesServiceDisconnected() {

            }
        });
    }

    class MyTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            try {
                DeviceServiceConnector.getPrinterService().printDocument(
                        Constants.DEFAULT_DEVICE_INDEX,
                        new PrinterDocument(
                                new PrintableText("Тестовый Текст Для СЕРЕГИ!!!!! 19.03.2020")));
            } catch (DeviceServiceException exc) {
                exc.printStackTrace();
            }
            DeviceServiceConnector.startDeinitConnections();
            return null;
        }
    }
}