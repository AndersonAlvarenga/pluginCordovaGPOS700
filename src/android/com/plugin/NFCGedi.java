package com.plugin;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import br.com.gertec.gedi.GEDI;
import br.com.gertec.gedi.interfaces.ICL;
import br.com.gertec.gedi.structs.GEDI_CL_st_ISO_PollingInfo;

public class NFCGedi extends Activity {
    private static final String TAG = NFCGedi.class.getSimpleName();
    ICL icl = null;
    GEDI_CL_st_ISO_PollingInfo pollingInfo;
    private NfcAdapter nfcAdapter;
    private TextView text;
    private int id;
    private static int Contagem = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String package_name = getApplication().getPackageName();
        setContentView(getApplication().getResources().getIdentifier("nfcgedi", "layout", package_name));
        id = getResources().getIdentifier("textIdCartao","id",package_name);
        text = (TextView)findViewById(id);
        new Thread(new Runnable() {
            @Override
            public void run() {
                GEDI.init(getParent());
            }
        }).start();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Liga o Sensor da NFC
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        try {
            icl = GEDI.getInstance().getCL();
            // Inicializa a Leitura da NFC segura
            boolean isConect = false;
            icl.PowerOn();
            pollingInfo = new GEDI_CL_st_ISO_PollingInfo();
            // Tempo que será aguardado para fazer a leitura


            for(int x = 0; x<3;x++){
                text.setText("Insira o Cartão.\nTentativa"+(x+1)+" de 3.");
                try{
                    pollingInfo = icl.ISO_Polling(5000);
                    isConect = true;
                    break;
                }catch (Exception e){
                    text.setText("Insira o Cartão.\nTentativa 1 de 3.");
                }
            }

            icl.PowerOff();
            if(isConect){
                LerCard();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.d(TAG, "onResume");

        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        IntentFilter ndefDetected = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        IntentFilter techDetected = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
        IntentFilter idDetected = new IntentFilter((NfcAdapter.EXTRA_ID));

        IntentFilter[] nfcIntentFilter = new IntentFilter[]{techDetected,tagDetected,ndefDetected, idDetected};
        Intent newIntent = new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0,newIntent , 0);
        if(nfcAdapter!= null)
            nfcAdapter.enableForegroundDispatch(this, pendingIntent, nfcIntentFilter, null);

    }

    public String LerCard(){

        String mensagem;
        // Pega o Id do Cartão em Bytes
        mensagem = LerDados(pollingInfo.abUID);
        Contagem += 1;
        text.setText("Contador: " + Contagem +  "\nID Cartão: " + mensagem);
        
        return mensagem;
    }

    public String LerDados(byte[] dados) {

        long result = 0;

        if (dados == null) return "";

        for (int i = dados.length - 1; i >= 0; --i) {
            result <<= 8;
            result |= dados[i] & 0x0FF;
        }
        Log.d(TAG, "ID Cartão: " + Long.toString(result));
        Log.d(TAG, "ID Cartão HEX: " + bytesToHex(dados));
        
        return Long.toString(result);
    }

    private static String bytesToHex(byte[] hashInBytes) {

        StringBuilder sb = new StringBuilder();
        for (byte b : hashInBytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
    private void closeIntent(){
        finish();
    }

    public String onICL(){
        try {
            nfcAdapter = NfcAdapter.getDefaultAdapter(this);
            icl = GEDI.getInstance().getCL();
            icl.PowerOn();
        }catch (Exception e){
            return e.getMessage();
        }
        return "OK";
    }
    public String offICL(){
        try {
            icl.PowerOff();
        }catch (Exception e ){
            return e.getMessage();
        }

    }
    public String lerCartao(){
        pollingInfo = new GEDI_CL_st_ISO_PollingInfo();
        pollingInfo = icl.ISO_Polling(5000);
        return LerCard();
    }
}
