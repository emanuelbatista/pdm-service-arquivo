package br.edu.ifpb.pdm;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;

import br.edu.ifpb.pdm.brodcast.ListReceiver;
import br.edu.ifpb.pdm.contrat.Notification;

public class MainActivity extends AppCompatActivity implements Notification{

    private ArrayAdapter<String> listViewAdapter;
    private Intent service;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //
        listViewAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1);
        ListView listView=(ListView)findViewById(R.id.list);
        listView.setAdapter(listViewAdapter);
        //
        IntentFilter intentFilter=new IntentFilter("br.edu.ifpb.pdm.LIST_RECEIVER");
        registerReceiver(new ListReceiver(this),intentFilter);
    }

    public void inciar(View v){
        service=new Intent("br.edu.ifpb.pdm.ARQUIVO_SERVICE");
        startService(service);

    }

    public void parar(View v){
        stopService(service);
        listViewAdapter.clear();
    }

    @Override
    public void run(List<String> list) {
        for (String value:list){
            listViewAdapter.add(value);
        }
    }
}
