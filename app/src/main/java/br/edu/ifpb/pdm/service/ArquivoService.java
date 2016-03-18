package br.edu.ifpb.pdm.service;

import android.app.Service;
import android.content.Intent;
import android.os.*;
import android.widget.Toast;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import br.edu.ifpb.pdm.util.DataList;
import br.edu.ifpb.pdm.util.PreferenceList;

public class ArquivoService extends Service {

    private RestTemplate restTemplate;
    private String token;
    private ServiceHandler mServiceHandler;
    private DataList dataList;
    private PreferenceList preferenceList;
    private Timer timer;

    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            preferenceList = new PreferenceList(getBaseContext());
            dataList = new DataList(getBaseContext());
            List<String> linhas = iniciar();
            enviarResultado(linhas);
            //
            verificarMudanca();
        }
    }

    public ArquivoService() {
        this.restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());


    }


    @Override
    public void onCreate() {
        // Criando a thread responsavel pela execucao da tarefa
        HandlerThread thread = new HandlerThread(" ServiceStartArguments ",
                android.os.Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();
// Obtendo o Looper da thread e passando como parametro para o Handler
        Looper mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Toast.makeText(this, " Service Started", Toast.LENGTH_LONG).show();

        Message msg = mServiceHandler.obtainMessage();
        msg.arg1 = startId;
        msg.setData(intent.getExtras());
        mServiceHandler.sendMessage(msg);

        return START_STICKY;
    }

    private List<String> iniciar() {
        return oneAcessProcesar();
    }

    private List<String> oneAcessProcesar(){
        return dataList.findAll();
    }


    private List<String> processarArquivo() {
        Integer offset=preferenceList.getOffset();
        String linha = lerLinhaArquivo(offset);
        List<String> linhasArquivo = new ArrayList<>();
        while (linha != null) {
            linhasArquivo.add(linha);
            offset++;
            linha = lerLinhaArquivo(offset);
        }
        preferenceList.setOffset(offset);
        return linhasArquivo;
    }


    private String lerLinhaArquivo(int posicao) {
        String url = "http://192.168.56.1:8080/arquivo?a=um&o={offset}";
        List<String> strings = restTemplate.getForObject(url, List.class, posicao);
        if(strings.isEmpty()){
            return null;
        }
        return strings.get(0);
    }

    private String md5(final String s) {
        final String MD5 = "MD5";
        try {
            // Create MD5 Hash
            MessageDigest digest = MessageDigest
                    .getInstance(MD5);
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    //

    private void verificarMudanca() {
        token = gerarToken();

        timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                String novoToken = newToken();
                if (!novoToken.equals(token)) {
                    token = novoToken;
                    List<String> novasLinhas = processarArquivo();
                    enviarResultado(novasLinhas);
                    preferenceList.setHash(token);
                    dataList.save(novasLinhas);
                }
            }
        };
        timer.schedule(timerTask, 0, 2 * 60 * 1000);
    }

    private String gerarToken() {
        String token = preferenceList.getHash();
        if (token == null) {
            String url = "http://192.168.56.1:8080/arquivo?a=todos";
            List<String> linhas = restTemplate.getForObject(url, List.class);
            //
            token = md5(linhas.toString());
            preferenceList.setHash(token);
        }
        return token;
    }

    private String newToken(){
        String url = "http://192.168.56.1:8080/arquivo?a=todos";
        List<String> linhas = restTemplate.getForObject(url, List.class);
        //
        return md5(linhas.toString());
    }

    private void enviarResultado(List<String> linhas) {
        Intent intent = new Intent("br.edu.ifpb.pdm.LIST_RECEIVER");
        intent.putStringArrayListExtra("list", (ArrayList<String>) linhas);
        sendBroadcast(intent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "Distruiu serviço", Toast.LENGTH_LONG).show();
        timer.cancel();
    }
}
