package com.rein.android.ReynTestApp.printGroups;


import android.os.RemoteException;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.evotor.framework.component.PaymentPerformer;
import ru.evotor.framework.core.IntegrationService;
import ru.evotor.framework.core.action.event.receipt.payment.PaymentSelectedEvent;
import ru.evotor.framework.core.action.event.receipt.payment.PaymentSelectedEventProcessor;
import ru.evotor.framework.core.action.event.receipt.payment.PaymentSelectedEventResult;
import ru.evotor.framework.core.action.processor.ActionProcessor;
import ru.evotor.framework.payment.PaymentPurpose;
import ru.evotor.framework.payment.PaymentSystem;
import ru.evotor.framework.payment.PaymentType;




public class SplitPaymentService extends IntegrationService {
    @Nullable
    @Override
    protected Map<String, ActionProcessor> createProcessors() {
        /*
        Указываем установленное на смарт-терминале приложение или его компонент, которое исполнит платежи.
        Исполнителя платежа также можно указать внутри метода call следущим образом: event.getPaymentPurpose().getPaymentPerformer();
         */
        PaymentPerformer paymentPerformerApplicationOrComponent = new PaymentPerformer(
                //Объект с описанием платёжной системы, которое использует приложение, исполняющее платёж.
                new PaymentSystem(PaymentType.ELECTRON, "Some description", "41138179-5106-4084-8ea1-17039ea9bf6y"),
                //Пакет, в котором расположен компонент, исполняющий платёж.
                "com.rein.android.ReynTestApp",
                //Название компонента, исполняющего платёж.
                "SplitPaymentService",
                //Идентификатор уникальный идентификатор приложения, исполняющего платёж.
                "31138179-5106-4084-8ea1-17039ea9bf6e",
                //Название приложения, исполняющего платёж
                "TestReyn");
        PaymentPerformer paymentPerformerApplicationOrComponent2 = new PaymentPerformer(
                //Объект с описанием платёжной системы, которое использует приложение, исполняющее платёж.
                new PaymentSystem(PaymentType.ADVANCE, "Some description", "41138179-5106-4084-8ea1-17039ea9bf6y"),
                //Пакет, в котором расположен компонент, исполняющий платёж.
                "com.rein.android.ReynTestApp",
                //Название компонента, исполняющего платёж.
                "SplitPaymentService",
                //Идентификатор уникальный идентификатор приложения, исполняющего платёж.
                "31138179-5106-4084-8ea1-17039ea9bf6e",
                //Название приложения, исполняющего платёж
                "TestReyn");

        //Создаём платежи для нескольких юридических лиц и добавляем их в список.
        PaymentPurpose firstLegalEntityPayment = new PaymentPurpose(
                //Идентификатор платежа.
                "32dc62ce-9583-41ad-b88c-ac16a6fdf1ae",
                //Идентификатор платёжной системы. Устаревший параметр.
                null,
                //Установленное на смарт-терминале приложение или его компонент, выполняющее платёж.
                paymentPerformerApplicationOrComponent,
                //Сумма платежа.
                new BigDecimal(5000),
                "Payment account identifier1",
                //Сообщение для пользователя.
                "Your payment has proceeded successfully.");

        PaymentPurpose firstLegalEntityPayment2 = new PaymentPurpose(
                //Идентификатор платежа.
                "0a31820d-0885-49e1-ada1-467616fb61f9",
                //Идентификатор платёжной системы. Устаревший параметр.
                null,
                //Установленное на смарт-терминале приложение или его компонент, выполняющее платёж.
                paymentPerformerApplicationOrComponent2,
                //Сумма платежа.
                new BigDecimal(5000),
                "Payment account identifier2",
                //Сообщение для пользователя.
                "Your payment has proceeded successfully.");

        final List<PaymentPurpose> listOfAllPayments = Arrays.asList(firstLegalEntityPayment, firstLegalEntityPayment2);

        //Создаём обработчик события выбора оплаты.
        PaymentSelectedEventProcessor yourEventProcessor = new PaymentSelectedEventProcessor() {
            @Override
            public void call(@NonNull String action, @NonNull PaymentSelectedEvent event, @NonNull Callback callback) {
                /*
                Все методы функции обратного вызова могут вернуть исключение RemoteException, которое необходимо правильно обработать.
                Например, с помощью конструкции try {} catch () {}.
                 */
                try {
                    /*
                    Вы также можете воспользоваться другими методами функции обратного вызова.
                    Например, запустить операцию с помощью startActivity(Intent intent) или отреагировть на ошибку с помощью одного из методов onError().
                     */
                    callback.onResult(new PaymentSelectedEventResult(
                            //Добавляем дополнительные данные в чек.
                            null,
                            listOfAllPayments));
                } catch (RemoteException exception) {
                    exception.printStackTrace();
                }
            }
        };

        //Создаём и возвращаем в смарт-терминал результат обработки события в виде коллекиции пар "Событие":"Обработчик события".
        Map<String, ActionProcessor> eventProcessingResult = new HashMap<>();
        eventProcessingResult.put(PaymentSelectedEvent.NAME_SELL_RECEIPT, yourEventProcessor);

        return eventProcessingResult;
    }
}