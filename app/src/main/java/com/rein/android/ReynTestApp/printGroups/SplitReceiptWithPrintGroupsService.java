package com.rein.android.ReynTestApp.printGroups;


import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

import ru.evotor.framework.core.IntegrationService;

import ru.evotor.framework.core.action.event.receipt.print_group.PrintGroupRequiredEvent;
import ru.evotor.framework.core.action.processor.ActionProcessor;


public class SplitReceiptWithPrintGroupsService extends IntegrationService {
    @Nullable
    @Override
    protected Map<String, ActionProcessor> createProcessors() {

        //Создаём и возвращаем в смарт-терминал результат обработки события в виде коллекиции пар "Событие":"Обработчик события".
        Map<String, ActionProcessor> eventProcessingResult = new HashMap<>();
        eventProcessingResult.put(PrintGroupRequiredEvent.NAME_SELL_RECEIPT, new SetPrintGroupProcessor(this));

        return eventProcessingResult;
    }
}