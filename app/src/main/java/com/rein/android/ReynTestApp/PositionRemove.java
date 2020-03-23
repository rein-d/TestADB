package com.rein.android.ReynTestApp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import ru.evotor.framework.core.action.event.receipt.position_edited.PositionRemovedEvent;

public class PositionRemove extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Bundle bundle = intent.getExtras();
        Log.e("TAG", action);
        if (action.equals("evotor.intent.action.cashDrawer.OPEN")) {
            PositionRemovedEvent.create(bundle);
        }

    }
}
