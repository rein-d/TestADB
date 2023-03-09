package com.rein.android.ReynTestApp.printGroups;

import android.content.Context;
import android.os.RemoteException;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.NotNull;

import ru.evotor.framework.core.action.event.receipt.print_group.PrintGroupRequiredEvent;
import ru.evotor.framework.core.action.event.receipt.print_group.PrintGroupRequiredEventProcessor;
import ru.evotor.framework.core.action.event.receipt.print_group.PrintGroupRequiredEventResult;

class SetPrintGroupProcessor extends PrintGroupRequiredEventProcessor {
    private Context mContext;

    public SetPrintGroupProcessor(Context context) {
        mContext = context;
    }

    @Override
    public void call(@NonNull @NotNull String action, @NonNull @NotNull PrintGroupRequiredEvent event, @NonNull @NotNull Callback callback) {

       PrintGroupRequiredEventResult result = new MoveAllPositionToNonFiscalUseCase(mContext).moveAllPositionsToNonFiscal(action);

        try {
            if (result == null) {
                callback.skip();
            } else {
                callback.onResult(result);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
