package com.rein.android.ReynTestApp.printGroups;

import android.content.Context;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import ru.evotor.framework.core.action.event.receipt.changes.position.SetPrintGroup;
import ru.evotor.framework.core.action.event.receipt.print_group.PrintGroupRequiredEventResult;
import ru.evotor.framework.receipt.Position;
import ru.evotor.framework.receipt.PrintGroup;
import ru.evotor.framework.receipt.Receipt;
import ru.evotor.framework.receipt.ReceiptApi;

class MoveAllPositionToNonFiscalUseCase {
    private Context mContext;

    public MoveAllPositionToNonFiscalUseCase(Context context) {
        mContext = context;
    }

    PrintGroup firstReceipt = new PrintGroup(
            //Идентифискатор печатной группы (чека покупателя).
            "1",
            //Тип чека, например, кассовый чек.
            PrintGroup.Type.INVOICE,
            //Наименование покупателя.
            "OOO Vector",
            //ИНН покупателя.
            "606053449439",
            //Адрес покупателя.
            "12, 3k2, Dark street, Nsk, Russia",
                /*
                Система налогообложения, которая применялась при расчёте.
                Смарт-терминал печатает чеки с указанной системой налогообложения, если она попадает в список разрешённых систем. В противном случае смарт-терминал выбирает систему налогообложения, заданную по умолчанию.
                */
            null,
            //Указывает на необходимость печати чека.
            true,
            //Реквизиты покупателя.
            null,
            null);

    public PrintGroupRequiredEventResult moveAllPositionsToNonFiscal(String action) {
        try {
            List<Position> positions = ReceiptApi.getReceipt(mContext, Receipt.Type.SELL).getPositions();
            List<String> firstPurchaserPositions = new ArrayList<>();
            for (Position pos:
                 positions) {
                firstPurchaserPositions.add(pos.getUuid());
            }
            SetPrintGroup firstPrintGroup = new SetPrintGroup(firstReceipt, new ArrayList<>(), firstPurchaserPositions);
            final List<SetPrintGroup> setAllPurchaserReceipts = Arrays.asList(firstPrintGroup);

            return new PrintGroupRequiredEventResult(null, setAllPurchaserReceipts);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
