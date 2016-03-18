package br.edu.ifpb.pdm.brodcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.List;

import br.edu.ifpb.pdm.contrat.Notification;

public class ListReceiver extends BroadcastReceiver {

    private Notification notification;

    public ListReceiver(Notification notification) {
        this.notification = notification;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        List<String> list = intent.getStringArrayListExtra("list");
        notification.run(list);
    }
}
