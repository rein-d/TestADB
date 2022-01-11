package com.rein.android.ReynTestApp;

import android.os.RemoteException;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ru.evotor.framework.core.IntegrationService;
import ru.evotor.framework.core.action.event.receipt.before_positions_edited.BeforePositionsEditedEvent;
import ru.evotor.framework.core.action.event.receipt.before_positions_edited.BeforePositionsEditedEventProcessor;
import ru.evotor.framework.core.action.event.receipt.before_positions_edited.BeforePositionsEditedEventResult;
import ru.evotor.framework.core.action.event.receipt.changes.position.IPositionChange;
import ru.evotor.framework.core.action.event.receipt.changes.position.PositionEdit;
import ru.evotor.framework.core.action.processor.ActionProcessor;
import ru.evotor.framework.receipt.ExtraKey;
import ru.evotor.framework.receipt.Position;

public class ChangePositions extends IntegrationService {
    @Nullable
    @Override
    protected Map<String, ActionProcessor> createProcessors() {
        Map<String, ActionProcessor> map = new HashMap<>();
        map.put(BeforePositionsEditedEvent.NAME_SELL_RECEIPT, new BeforePositionsEditedEventProcessor() {
            @Override
            public void call(@NonNull String action, @NonNull BeforePositionsEditedEvent event, @NonNull Callback callback){
                List<IPositionChange> changes = event.getChanges();
                Set<ExtraKey> someExtraKey = new HashSet<>();
                someExtraKey.add(new ExtraKey(null, "<app_uuid>", "Добавление скиддки к позиции"));

                    for (IPositionChange change : event.getChanges()) {
                        if (change instanceof PositionEdit) {

                           Position position123 = ((PositionEdit) change).getPosition();
                           changes.clear();
                           changes.add(new PositionEdit(
                                   Position.Builder.copyFrom(position123).setExtraKeys(someExtraKey).setPriceWithDiscountPosition(new BigDecimal( 200)).build()));

                        }
                    }


                try {
                    callback.onResult(new BeforePositionsEditedEventResult(changes, null));
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
        );

        return map;
    }

}
