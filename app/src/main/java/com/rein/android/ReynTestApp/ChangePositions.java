package com.rein.android.ReynTestApp;

import android.os.Bundle;
import android.os.RemoteException;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import ru.evotor.IBundlable;
import ru.evotor.framework.common.event.handler.service.IntegrationServiceV2;
import ru.evotor.framework.core.IntegrationService;
import ru.evotor.framework.core.action.event.receipt.before_positions_edited.BeforePositionsEditedEvent;
import ru.evotor.framework.core.action.event.receipt.before_positions_edited.BeforePositionsEditedEventProcessor;
import ru.evotor.framework.core.action.event.receipt.before_positions_edited.BeforePositionsEditedEventResult;
import ru.evotor.framework.core.action.event.receipt.changes.position.IPositionChange;
import ru.evotor.framework.core.action.event.receipt.changes.position.PositionAdd;
import ru.evotor.framework.core.action.event.receipt.changes.position.PositionEdit;
import ru.evotor.framework.core.action.processor.ActionProcessor;
import ru.evotor.framework.receipt.ExtraKey;
import ru.evotor.framework.receipt.Measure;
import ru.evotor.framework.receipt.Position;

public class ChangePositions extends IntegrationServiceV2 {
    @Nullable
    @Override
    public IBundlable onEvent(@NotNull String s, @NotNull Bundle bundle) {
        List<IPositionChange> changes = new ArrayList<>();
        Position PositionToBeAdded = Position.Builder.newInstance(
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                "Зажигалка",
                new Measure("шт", 0, 0),
                new BigDecimal(30),
                new BigDecimal(1)
        ).build();
        changes.add(new PositionAdd(PositionToBeAdded));
        return new BeforePositionsEditedEventResult(changes, null, null);
    }
}
