package com.plugin;

import android.content.Context;
import br.com.gertec.gedi.GEDI;
import br.com.gertec.gedi.interfaces.IGEDI;
import br.com.gertec.gedi.interfaces.ISMART;
import br.com.gertec.gedi.enums.GEDI_SMART_e_Slot;
import br.com.gertec.gedi.enums.GEDI_SMART_e_Voltage;
import br.com.gertec.gedi.enums.GEDI_SMART_e_Status;
import br.com.gertec.gedi.exceptions.GediException;

public class ISmart {
    private ISMART iSmart;
    private StringBuilder sb;
    private GEDI_SMART_st_ResetInfo resetEMV;
    private GEDI_SMART_st_ResetInfo warmResetEMV;
    private IGEDI iGedi = null;
    private int contadorCartao = 10;

    public ISmart(Context context){
        new Thread(() -> {
            GEDI.init(context);
            this.iGedi = GEDI.getInstance(context);
            this.iSmart = GEDI.getInstance().getSMART();
            try {
                new Thread().sleep(250);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
    public void powerOff() {
        try {
            this.iSmart.PowerOff(GEDI_SMART_e_Slot.USER);
        } catch (GediException e) {
            e.printStackTrace();
        }
    }
    public GEDI_SMART_e_Status statusGedi(GEDI_SMART_e_Slot eSlot) {
        try {
            return iSmart.Status(eSlot);
        } catch (GediException e) {
            e.printStackTrace();
            return null;
        }
    }
    public void gedi_SmartCard() {
        try {

            try {
                for (String a = statusGedi(GEDI_SMART_e_Slot.USER).toString(); !a.equals("PRESENT") && this.contadorCartao > 0; a = statusGedi(GEDI_SMART_e_Slot.USER).toString()) {
                }
                this.resetEMV = iSmart.ResetEMV(GEDI_SMART_e_Slot.USER, GEDI_SMART_e_Voltage.VOLTAGE_5V);
                this.warmResetEMV = iSmart.WarmResetEMV(GEDI_SMART_e_Slot.USER, GEDI_SMART_e_Voltage.VOLTAGE_5V);
            } catch (GediException e) {
                e.printStackTrace();
            }

        } catch (Exception e2) {
            e2.printStackTrace();
        }

    }

    public static String byteArrayToHexString(byte[] in) {
        StringBuilder builder = new StringBuilder();
        if (in != null) {
            byte[] var2 = in;
            int var3 = in.length;
            for (int var4 = 0; var4 < var3; var4++) {
                builder.append(String.format("%02X", new Object[]{Byte.valueOf(var2[var4])}));
            }
        }
        return builder.toString();
    }

    public String getCard(){
        powerOff();
        gedi_SmartCard();
        return byteArrayToHexString(resetEMV.abATR);
    }
}
