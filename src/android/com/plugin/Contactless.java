package com.plugin;

import android.content.Context;
import android.nfc.NfcAdapter;
import android.widget.TextView;
import br.com.gertec.gedi.GEDI;
import br.com.gertec.gedi.interfaces.IGEDI;
import br.com.gertec.gedi.interfaces.ICL;
import br.com.gertec.gedi.structs.GEDI_CL_st_ISO_PollingInfo;
import br.com.gertec.gedi.structs.GEDI_CL_st_MF_Key;
import br.com.gertec.gedi.enums.GEDI_CL_e_MF_KeyType;
import br.com.gertec.gedi.exceptions.GediException;

public class Contactless {

    private ICL icl;
    private IGEDI iGedi = null;
    private GEDI_CL_st_ISO_PollingInfo pollingInfo;
    private NfcAdapter nfcAdapter;
    private TextView text;

    public Contactless(Context context){
        new Thread(() -> {
            GEDI.init(context);
            this.iGedi = GEDI.getInstance(context);
            try {
                new Thread().sleep(250);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public String ativarLeituraICL(){
        String retornoOnICl = onICl();
        if(retornoOnICl != "Ativado"){
            return retornoOnICl;
        }
        String retornoLerCartao = lerCartao();
        offICl();
        return retornoLerCartao;
    }

    public String onICl(){
        try {
            icl = GEDI.getInstance().getCL();

        } catch (Exception e) {
            return "getCL - FAIL - " + e.getMessage();
        }

        try {
            icl.PowerOn();
        } catch (Exception e) {
            return "iCl.PowerOn - FAIL - " + e.getMessage();
        }

        return "Ativado";
    }

    public String offICl(){
        try {
            icl = GEDI.getInstance().getCL();

        } catch (Exception e) {
            return "getCL - FAIL - " + e.getMessage();
        }

        try {
            icl.PowerOff();
        } catch (Exception e) {
            return "iCl.PowerOff - FAIL - " + e.getMessage();
        }

        return "Desativado";
    }

    public String lerCartao(){
        pollingInfo = new GEDI_CL_st_ISO_PollingInfo();
        final GEDI_CL_st_MF_Key key = new GEDI_CL_st_MF_Key();
        String erro="";
        String UID;
        try {
            pollingInfo= icl.ISO_Polling(10000);

        }catch (GediException e ){
            return e.getMessage();
        }catch (Exception e) {
            // TODO: handle exception
            return e.getMessage();
        }
        byte[] abUID = pollingInfo.abUID;
        UID = arrayBytesToString(abUID);

        return UID;
    }


    public static String arrayBytesToString(byte[] bValues) {

        StringBuilder sbValues = new StringBuilder();
        for (byte b : bValues) {
            sbValues.append(String.format("%02X ", b).replace(" ", ""));
        }
        return sbValues.toString();
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }
}
