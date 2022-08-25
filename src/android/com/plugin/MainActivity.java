package com.plugin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;

import java.util.List;

import br.com.gertec.gedi.GEDI;
import br.com.gertec.gedi.enums.GEDI_CL_e_MF_KeyType;
import br.com.gertec.gedi.enums.GEDI_INFO_e_ControlNumber;
import br.com.gertec.gedi.enums.GEDI_LED_e_Id;
import br.com.gertec.gedi.enums.GEDI_MSR_e_Status;
import br.com.gertec.gedi.enums.GEDI_PRNTR_e_Alignment;
import br.com.gertec.gedi.enums.GEDI_PRNTR_e_BarCodeType;
import br.com.gertec.gedi.enums.GEDI_PRNTR_e_Status;
import br.com.gertec.gedi.enums.GEDI_SMART_e_Slot;
import br.com.gertec.gedi.enums.GEDI_SMART_e_Status;
import br.com.gertec.gedi.enums.GEDI_SMART_e_Voltage;
import br.com.gertec.gedi.exceptions.GediException;
import br.com.gertec.gedi.interfaces.IAUDIO;
import br.com.gertec.gedi.interfaces.ICL;
import br.com.gertec.gedi.interfaces.ICLOCK;
import br.com.gertec.gedi.interfaces.ICRYPT;
import br.com.gertec.gedi.interfaces.IGEDI;
import br.com.gertec.gedi.interfaces.IINFO;
import br.com.gertec.gedi.interfaces.IKBD;
import br.com.gertec.gedi.interfaces.IKMS;
import br.com.gertec.gedi.interfaces.ILED;
import br.com.gertec.gedi.interfaces.IMSR;
import br.com.gertec.gedi.interfaces.IPM;
import br.com.gertec.gedi.interfaces.IPRNTR;
import br.com.gertec.gedi.interfaces.ISMART;
import br.com.gertec.gedi.structs.GEDI_CLOCK_st_RTC;
import br.com.gertec.gedi.structs.GEDI_CL_st_ISO_PollingInfo;
import br.com.gertec.gedi.structs.GEDI_CL_st_MF_Key;
import br.com.gertec.gedi.structs.GEDI_KBD_st_Info;
import br.com.gertec.gedi.structs.GEDI_KMS_st_Control;
import br.com.gertec.gedi.structs.GEDI_KMS_st_Data;
import br.com.gertec.gedi.structs.GEDI_KMS_st_PINBlock;
import br.com.gertec.gedi.structs.GEDI_MSR_st_LastErrors;
import br.com.gertec.gedi.structs.GEDI_MSR_st_Tracks;
import br.com.gertec.gedi.structs.GEDI_PRNTR_st_BarCodeConfig;
import br.com.gertec.gedi.structs.GEDI_PRNTR_st_PictureConfig;
import br.com.gertec.gedi.structs.GEDI_PRNTR_st_StringConfig;

public class MainActivity extends CordovaPlugin {
    



///////------------------------------------------------------------------------------------

    public static final int BLACKLINE_MIN = 90;
    ScrollView main_scrollview;
    Button pinpadButton0, pinpadButton1, pinpadButton2,
            pinpadButton3, pinpadButton4, pinpadButton5,
            pinpadButton6, pinpadButton7, pinpadButton8,
            pinpadButton9, pinpadCancel, pinpadConfirm, pinpadClear;

    Button btnIINFO, btnIPRNTR, btnILED, btnIAUDIO, btnICRYPT, btnICLOCK, btnIKBD,
            btnICL, btnIKMS, btnIPM, btnIMSR, btnISMART;

    //  GEDI VAR
    private IGEDI iGedi;
    private IINFO gInfo;
    private IPRNTR iPrntr;
    private ILED iLed;
    private IAUDIO iAudio;
    private ICRYPT iCrypt;
    private ICLOCK iClock;
    private IKBD iKbd;
    private ICL iCl;
    private IKMS iKms;
    private IMSR iMsr;
    private IPM iPM;
    private ISMART iSmart;

    // ETC
    private int iPaperUsage;
    private Handler handler = new Handler();
    private GEDI_PRNTR_e_Status printStatus;
    private PinPadFragment pinPadFragment;


    ProgressDialog progressDialog;
    AlertDialog alerta;
    AlertDialog.Builder builder;
    private StringBuilder sb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        main_scrollview = findViewById(R.id.main_scrollview);

        pinpadButton0 = findViewById(R.id.ppcButton0);
        pinpadButton1 = findViewById(R.id.ppcButton1);
        pinpadButton2 = findViewById(R.id.ppcButton2);
        pinpadButton3 = findViewById(R.id.ppcButton3);
        pinpadButton4 = findViewById(R.id.ppcButton4);
        pinpadButton5 = findViewById(R.id.ppcButton5);
        pinpadButton6 = findViewById(R.id.ppcButton6);
        pinpadButton7 = findViewById(R.id.ppcButton7);
        pinpadButton8 = findViewById(R.id.ppcButton8);
        pinpadButton9 = findViewById(R.id.ppcButton9);
        pinpadCancel = findViewById(R.id.ppcButtonCancel);
        pinpadConfirm = findViewById(R.id.ppcButtonConfirm);
        pinpadClear = findViewById(R.id.ppcButtonClear);


        btnIAUDIO = findViewById(R.id.buttonIAUDIO);
        btnIINFO = findViewById(R.id.buttonIINFO);
        btnICLOCK = findViewById(R.id.buttonICLOCK);
        btnIKBD = findViewById(R.id.buttonIKBD);
        btnILED = findViewById(R.id.buttonILED);
        btnICRYPT = findViewById(R.id.buttonICRYPT);
        btnIPRNTR = findViewById(R.id.buttonIPRNTR);
        btnIKMS = findViewById(R.id.buttonIKMS);
        btnICL = findViewById(R.id.buttonICL);
        btnIPM = findViewById(R.id.buttonIPM);
        btnIMSR = findViewById(R.id.buttonIMSR);
        btnISMART = findViewById(R.id.buttonISMART);


        progressDialog = new ProgressDialog(MainActivity.this);
        builder = new AlertDialog.Builder(this);


        System.out.println("----------------------------");
        System.out.println("Aplicação: infoGEDI");

        try {

            GEDI.init(getApplicationContext());
            System.out.println("GEDI.init\t\t\t- OK");

        } catch (Exception ex) {
            System.out.println("GEDI.init\t\t\t- FAIL");
            System.out.println("Erro: " + ex.getMessage());

        }


    }
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private GertecPrinter gertecPrinter;

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        this.webView = webView;
        gertecPrinter = new GertecPrinter(cordova.getActivity().getApplicationContext());
        gertecPrinter.setConfigImpressao(configPrint);
    }

    public MainActivity() {
        super();
    }


    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        Context context = cordova.getActivity().getApplicationContext();
        this.callbackContext = callbackContext;
        intent = null;

        if (action.equals("checarImpressora")) {
            onCreate(savedInstanceState);
            return printStatus();
        }

        return false; // Returning false results in a "MethodNotFound" error.
    }
    //------------------------------------------------------------------------------------------------------------

    private IGEDI setGediInstance() {
        try {
            iGedi = GEDI.getInstance();
            System.out.println("GEDI.getInstance\t\t\t- OK");
            return GEDI.getInstance();

        } catch (Exception ex) {
            System.out.println("GEDI.getInstance\t\t\t- FAIL");
        }
        return null;
    }


    //    IPRNTR
    private String printStatus() {
        try {
            printStatus = iPrntr.Status();
            switch (printStatus) {
                case OK:
                    return "STATUS: " + "A impressora está pronta para uso.";
                    break;
                case OUT_OF_PAPER:
                    return "STATUS: " + "A impressora está sem papel ou com tampa aberta.";
                    break;
                case OVERHEAT:
                    return "STATUS: " + "A impressora está superaquecida.";
                    break;
                case UNKNOWN_ERROR:
                    return "STATUS: " + "Valor padrão para erros não mapeados.";
                    break;
            }
        } catch (Exception ex) {
            return "iPrntr.Status\t\t\t- FAIL";
        }
    }

    private void printPaperReset() {
        try {
            iPrntr.ResetPaperUsage();

            System.out.println("iPrntr.ResetPaperUsage\t\t\t- OK");
        } catch (Exception ex) {
            System.out.println("iPrntr.ResetPaperUsage\t\t\t- FAIL");
        }
    }

    private void printPaperUsage() {
        try {
            iPaperUsage = iPrntr.GetPaperUsage();
            System.out.println("iPrntr.GetPaperUsage: " + iPaperUsage);

            System.out.println("iPrntr.GetPaperUsage\t\t\t- OK");
        } catch (Exception ex) {
            System.out.println("iPrntr.GetPaperUsage\t\t\t- FAIL");
        }
    }


    private void printBarCode(GEDI_PRNTR_e_BarCodeType barCodeType) {
        try {


            GEDI_PRNTR_st_BarCodeConfig config;
            switch (barCodeType) {


                case QR_CODE:
                    config = new GEDI_PRNTR_st_BarCodeConfig();
                    config.barCodeType = barCodeType;
                    config.height = 100;
                    config.width = 150;

                    break;

                default:
                    config = new GEDI_PRNTR_st_BarCodeConfig();
                    config.barCodeType = barCodeType;

                    config.height = 100;
                    config.width = 100;

                    break;
            }

            iPrntr.DrawBarCode(config, "TEXTO");

            System.out.println("iPrntr.DrawBarCode\t\t\t- OK");
        } catch (Exception ex) {
            System.out.println("iPrntr.DrawBarCode\t\t\t- FAIL");
        }
    }

    private void printImage() {
        try {
            Paint paint = new Paint();
            paint.setTextSize(10);

            GEDI_PRNTR_st_PictureConfig config = new GEDI_PRNTR_st_PictureConfig();
            config.alignment = GEDI_PRNTR_e_Alignment.CENTER;
            config.height = 100;
            config.width = 100;
            config.offset = 10;


            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.gertec);
            Bitmap bitmapReduzido = Bitmap.createScaledBitmap(bitmap, 100, 100, true);
            iPrntr.DrawPictureExt(config, bitmapReduzido);

            System.out.println("iPrntr.DrawPictureExt\t\t\t- OK");
        } catch (Exception ex) {
            System.out.println("iPrntr.DrawPictureExt\t\t\t- FAIL");
        }
    }

    private void printString(String value) {
        try {
            Paint paint = new Paint();
            paint.setTextSize(10);

            GEDI_PRNTR_st_StringConfig config = new GEDI_PRNTR_st_StringConfig();
            config.lineSpace = 1;
            config.offset = 1;
            config.paint = paint;

            iPrntr.DrawStringExt(config, value);

            System.out.println("iPrntr.DrawStringExt\t\t\t- OK");
        } catch (Exception ex) {
            System.out.println("iPrntr.DrawStringExt\t\t\t- FAIL");
        }
    }

    private void printBlankLine(int height) {
        try {
            iPrntr.DrawBlankLine(height);
            System.out.println("iPrntr.DrawBlankLine\t\t\t- OK");
        } catch (Exception ex) {
            System.out.println("iPrntr.DrawBlankLine\t\t\t- FAIL");
        }
    }

    private void printOutput() {
        try {
            iPrntr.Output();
            System.out.println("iPrntr.Output\t\t\t- OK");
        } catch (Exception ex) {
            System.out.println("iPrntr.Output\t\t\t- FAIL");
        }
    }

    private void printInit() {
        try {
            iPrntr.Init();
            System.out.println("iPrntr.Init\t\t\t- OK");
        } catch (Exception ex) {
            System.out.println("iPrntr.Init\t\t\t- FAIL");
        }
    }

    //    ICRYPT
    private byte[] cryptRandomBytes(int qtd) {
        try {
            byte[] rng_crypted = iCrypt.RNG(qtd);
            System.out.println("iCrypt.RNG\t\t\t- OK");
            return rng_crypted;
        } catch (GediException e) {
            System.out.println("iCrypt.RNG\t\t\t- FAIL");
        }

        return null;
    }

    //    IAUDIO
    private void audioBeep() {
        try {
            iAudio.Beep();

            btnIAUDIO.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check_24dp, 0, 0, 0);
            System.out.println("iAudio.Beep\t\t\t- OK");
        } catch (Exception e) {
            System.out.println("iAudio.Beep\t\t\t- FAIL");
            btnIAUDIO.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_close_24dp, 0, 0, 0);
        }
    }

    //    ICLOCK
    private String formatGertecRTC(GEDI_CLOCK_st_RTC gRTC) {
        byte hora = gRTC.bHour;
        byte minuto = gRTC.bMinute;
        byte segundo = gRTC.bSecond;

        byte semana = gRTC.bDoW;
        String strSemana = null;
        switch (semana) {
            case 0:
                strSemana = "SEG";
                break;
            case 1:
                strSemana = "TER";
                break;
            case 2:
                strSemana = "QUA";
                break;
            case 3:
                strSemana = "QUI";
                break;
            case 4:
                strSemana = "SEX";
                break;
            case 5:
                strSemana = "SAB";
                break;
            case 6:
                strSemana = "DOM";
                break;

        }

        byte dia = gRTC.bDay;
        byte mes = gRTC.bMonth;
        byte ano = gRTC.bYear;

        System.out.printf("ICLOCK Time: %d:%d:%d\n", hora, minuto, segundo);

        System.out.printf("ICLOCK Date: %d/%d/%d - %s\n", dia, mes, ano, strSemana);


        return String.format("Time:\t%d:%d:%d\n" +
                "Date:\t%d/%d/%d - %s", hora, minuto, segundo, dia, mes, ano, strSemana);
    }

    //    ICL
    private void cl_PowerOn() {
        try {
            iCl.PowerOn();
            System.out.println("iCl.PowerOn\t\t\t- OK");
        } catch (GediException gedi_e_ret) {
            System.out.println("iCl.PowerOn\t\t\t- FAIL (GEDI) - " + gedi_e_ret.getErrorCode().name());
        } catch (Exception e) {
            System.out.println("iCl.PowerOn\t\t\t- FAIL - " + e.getMessage());
        }
    }

    private void cl_PowerOff() {
        try {
            iCl.PowerOff();
            System.out.println("iCl.PowerOff\t\t\t- OK");
        } catch (GediException gedi_e_ret) {
            System.out.println("iCl.PowerOff\t\t\t- FAIL (GEDI) - " + gedi_e_ret.getErrorCode().name());
        } catch (Exception e) {
            System.out.println(" iCl.PowerOff\t\t\t- FAIL - " + e.getMessage());
        }
    }

    //    ISMART
    private void smartCardWarmResetEMV() {
        try {
            for (GEDI_SMART_e_Slot c : GEDI_SMART_e_Slot.values()) {
                iSmart.WarmResetEMV(c, GEDI_SMART_e_Voltage.VOLTAGE_1_8V);
                System.out.printf("iSmart.WarmResetEMV: %s\t\t\t- OK\n", c);
            }
        } catch (GediException gedi_e_ret) {
            System.out.println("iSmart.WarmResetEMV\t\t\t- FAIL (GEDI) - " + gedi_e_ret.getErrorCode().name());
        } catch (Exception e) {
            System.out.println("iSmart.WarmResetEMV\t\t\t- FAIL - " + e.getMessage());
        }
    }

    private void smartCardResetEMV() {
        try {
            for (GEDI_SMART_e_Slot c : GEDI_SMART_e_Slot.values()) {
                iSmart.ResetEMV(c, GEDI_SMART_e_Voltage.VOLTAGE_1_8V);
                System.out.printf("iSmart.ResetEMV: %s\t\t\t- OK\n", c);
            }
        } catch (GediException gedi_e_ret) {
            System.out.println("iSmart.ResetEMV\t\t\t- FAIL (GEDI) - " + gedi_e_ret.getErrorCode().name());
        } catch (Exception e) {
            System.out.println("iSmart.ResetEMV\t\t\t- FAIL - " + e.getMessage());
        }
    }

    private void smartCardPowerOff() {
        try {

            for (GEDI_SMART_e_Slot c : GEDI_SMART_e_Slot.values()) {
                iSmart.PowerOff(c);
                System.out.printf("iSmart.PowerOff: %s\t\t\t- OK\n", c);
            }

        } catch (GediException gedi_e_ret) {
            System.out.println("iSmart.PowerOff\t\t\t- FAIL (GEDI) - " + gedi_e_ret.getErrorCode().name());
        } catch (Exception e) {
            System.out.println("iSmart.PowerOff\t\t\t- FAIL - " + e.getMessage());
        }
    }

    private void smartCardStatus() {
        sb = new StringBuilder();
        for (GEDI_SMART_e_Slot c : GEDI_SMART_e_Slot.values()) {

            try {

                GEDI_SMART_e_Status status = iSmart.Status(c);
                final String r = String.format("iSmart - Status - %s:\t%s\n", c, status);
                System.out.printf(r);

                sb.append(r);


            } catch (GediException gedi_e_ret) {
                System.out.println("iSmart.Status\t\t\t- FAIL (GEDI) - " + gedi_e_ret.getErrorCode().name());
            } catch (Exception e) {
                System.out.println("iSmart.Status\t\t\t- FAIL - " + e.getMessage());
            }
        }
    }

    //    ILED
    private void ledOff() {
        System.out.println("ILed - Desligado");
        for (GEDI_LED_e_Id c : GEDI_LED_e_Id.values()) {

            if (c.equals(GEDI_LED_e_Id.GEDI_LED_ID_CONTACTLESS_RED) ||
                    c.equals(GEDI_LED_e_Id.GEDI_LED_ID_CONTACTLESS_GREEN) ||
                    c.equals(GEDI_LED_e_Id.GEDI_LED_ID_CONTACTLESS_ORANGE) ||
                    c.equals(GEDI_LED_e_Id.GEDI_LED_ID_CONTACTLESS_BLUE)) {
                try {
                    iLed.Set(c, false);
                    System.out.println("iLed.Set - " + c + ":\t\t\t- OK");
                } catch (GediException e) {
                    System.out.println("iLed.Set - " + c + ":\t\t\t- FAIL -- " + e.getErrorCode().name());
                } catch (Exception e) {
                    System.out.println("iLed.Set - \t\t\t- FAIL - " + e.getMessage());
                }
            }
        }
    }

    private void ledOn() {
        System.out.println("ILed - Ligado");
        for (GEDI_LED_e_Id c : GEDI_LED_e_Id.values()) {

            if (c.equals(GEDI_LED_e_Id.GEDI_LED_ID_CONTACTLESS_RED) ||
                    c.equals(GEDI_LED_e_Id.GEDI_LED_ID_CONTACTLESS_GREEN) ||
                    c.equals(GEDI_LED_e_Id.GEDI_LED_ID_CONTACTLESS_ORANGE) ||
                    c.equals(GEDI_LED_e_Id.GEDI_LED_ID_CONTACTLESS_BLUE)) {

                try {
                    iLed.Set(c, true);
                    System.out.println("iLed.Set - " + c + ":\t- OK");
                    btnILED.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check_24dp, 0, 0, 0);

                } catch (GediException e) {
                    System.out.println("iLed.Set - " + c + ":\t- FAIL -- " + e.getErrorCode().name());
                    btnILED.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_close_24dp, 0, 0, 0);

                } catch (Exception e) {
                    System.out.println("iLed.Set - \t- FAIL - " + e.getMessage());
                    btnILED.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_close_24dp, 0, 0, 0);

                }
            }


        }
    }

    //    BUTTON
    public void btnILED(View view) {

        try {
            iLed = GEDI.getInstance().getLED();

            if (iLed == null) {
                throw new GediException(10800);
            }

            System.out.println("getLED\t- OK");
        } catch (GediException e) {
            System.out.println("getLED\t- FAIL (GEDI) - " + e.getErrorCode().name());
            btnILED.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_close_24dp, 0, 0, 0);

            return;
        } catch (Exception e) {
            System.out.println("getLED\t- FAIL");
            btnILED.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_close_24dp, 0, 0, 0);

            return;
        }

        ledOn();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ledOff();


    }                                                            // LED

    public void btnIAUDIO(View view) {
        try {
            iAudio = GEDI.getInstance().getAUDIO();

            System.out.println("getAUDIO\t\t\t- OK");
        } catch (Exception e) {
            System.out.println("getAUDIO\t\t\t- FAIL");
        }

        audioBeep();
    }                                                          // AUDIO

    public void btnICRYPT(View view) {
        try {
            iCrypt = GEDI.getInstance().getCRYPT();
            System.out.println("getCRYPT\t\t\t- OK");
            btnICRYPT.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check_24dp, 0, 0, 0);

        } catch (Exception e) {
            System.out.println("getCRYPT\t\t\t- FAIL");
            btnICRYPT.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_close_24dp, 0, 0, 0);

        }

        byte[] cryptRndBytes = cryptRandomBytes(100);

        System.out.print("Bytes Aleatórios:\t");
        for (byte b : cryptRndBytes) {
            System.out.print(b);
        }
        System.out.println(" ");
    }                                                          // CRYPT

    public void btnIINFO(View view) {

        try {
            gInfo = GEDI.getInstance().getINFO();
            System.out.println("getINFO\t\t\t- OK");

        } catch (Exception e) {
            System.out.println("getINFO\t\t\t- FAIL");

        }
        sb = new StringBuilder();
        for (GEDI_INFO_e_ControlNumber c : GEDI_INFO_e_ControlNumber.values()) {
            try {

                String value = gInfo.ControlNumberGet(c);
                System.out.printf("%s:\t\t%s\n", c, value);
                sb.append(c + "\t" + value + "\n");
                btnIINFO.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check_24dp, 0, 0, 0);

            } catch (GediException gedi_e_ret) {
                btnIINFO.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_close_24dp, 0, 0, 0);
                System.out.println("gInfo.ControlNumberGet\t\t\t- FAIL (GEDI) - " + gedi_e_ret.getErrorCode().name());

            } catch (Exception e) {
                btnIINFO.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_close_24dp, 0, 0, 0);
                System.out.println("gInfo.ControlNumberGet\t\t\t- FAIL");
            }
        }


        builder.setTitle("GEDI IINFO");
        builder.setMessage(sb.toString());

        alerta = builder.create();
        alerta.show();

    }                                                           // Informações

    public void btnIPRNTR(View view) {
        iPrntr = GEDI.getInstance().getPRNTR();

        printPaperUsage();

        printStatus();
        if (printStatus == GEDI_PRNTR_e_Status.OK) {


            printInit();
            printString("TEXTO");
            printBlankLine(BLACKLINE_MIN);
            printOutput();

            printInit();
            printImage();
            printBlankLine(BLACKLINE_MIN);
            printOutput();
            printPaperUsage();

            printInit();
            printString("PDF_417");
            printBarCode(GEDI_PRNTR_e_BarCodeType.PDF_417);
            printBlankLine(BLACKLINE_MIN);

            printString("QR_CODE");
            printBarCode(GEDI_PRNTR_e_BarCodeType.QR_CODE);
            printBlankLine(BLACKLINE_MIN);

            printOutput();
            printPaperUsage();

            printPaperUsage();
            printPaperReset();
            printPaperUsage();


        }

    }                                                          // Impressora

    public void btnICLOCK(View view) {
        try {
            iClock = GEDI.getInstance().getCLOCK();
            System.out.println("getCLOCK\t\t\t- OK");
        } catch (Exception e) {
            System.out.println("getCLOCK\t\t\t- FAIL");
        }

        try {
            GEDI_CLOCK_st_RTC gRTC = iClock.RTCFGet();

            System.out.println("iClock.RTCFGet\t\t\t- OK");

            builder.setTitle("GEDI ICLOCK");
            builder.setMessage(formatGertecRTC((gRTC)));

            alerta = builder.create();
            alerta.show();
            btnICLOCK.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check_24dp, 0, 0, 0);

        } catch (Exception e) {
            System.out.println("iClock.RTCFGet\t\t\t- FAIL");
            btnICLOCK.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_close_24dp, 0, 0, 0);

        }


    }                                                          // RTC - TIMESTAMP

    public void btnIKBD(View view) {
//        pinPadFragment = new PinPadFragment();
//
//        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//        transaction.replace(R.id.frame_kbd, pinPadFragment);
//        transaction.commit();

//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                handler.post(new Runnable() {
//                    @Override
//                    public void run() {
//
//                        checkInflateButtonsPinPad();
//                    }
//                });
//            }
//        }).start();


        handler.post(new Runnable() {
            @Override
            public void run() {
                main_scrollview.post(new Runnable() {
                    public void run() {
                        main_scrollview.fullScroll(ScrollView.FOCUS_DOWN);
                    }
                });
            }
        });

        try {
            iKbd = GEDI.getInstance().getKBD();
            System.out.println("getKBD\t\t\t- OK");
        } catch (Exception e) {
            System.out.println("getKBD\t\t\t- FAIL");
        }

        try {

            GEDI_KBD_st_Info gKeyboard = new GEDI_KBD_st_Info(pinpadButton0, pinpadButton1,
                    pinpadButton2, pinpadButton3, pinpadButton4, pinpadButton5, pinpadButton6,
                    pinpadButton7, pinpadButton8, pinpadButton9, pinpadCancel, pinpadClear,
                    pinpadConfirm, MainActivity.this);


            iKbd.Set(gKeyboard);

            System.out.println("iKbd.Set\t\t\t- OK");
            btnIKBD.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check_24dp, 0, 0, 0);

        } catch (Exception e) {
            System.out.println("iKbd.Set\t\t\t- FAIL - " + e.getMessage());
            btnIKBD.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_close_24dp, 0, 0, 0);

        }


    }                                                            // Keyboard PinPAD

    public void btnICL(View view) {

        try {
            iCl = GEDI.getInstance(getApplicationContext()).getCL();

            System.out.println("getCL\t\t\t- OK");

        } catch (Exception e) {
            System.out.println("getCL\t\t\t- FAIL - " + e.getMessage());
        }

        progressDialog.setTitle("ICL");
        progressDialog.setIcon(R.drawable.ic_contactless);

        new Thread(new Runnable() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.show();
                    }
                });
            }
        }).run();


        cl_PowerOn();
        cl_PowerOff();

        final GEDI_CL_st_ISO_PollingInfo[] pollingInfo = new GEDI_CL_st_ISO_PollingInfo[1];
        final GEDI_CL_st_MF_Key key = new GEDI_CL_st_MF_Key();

        new Thread(new Runnable() {
            @Override
            public void run() {

                for (int i = 1; i <= 3; i++) {

                    final int aux = i;
                    try {

                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.setMessage("Aguardando Contactless...\nTentativa: " + aux + "/3");
                                System.out.println("Aguardando Contactless - Tentativa: " + aux + "/3");
                            }
                        });

                        pollingInfo[0] = iCl.ISO_Polling(5 * 1000);

                        System.out.println("iCl.ISO_Polling\t\t\t- OK");
                        System.out.printf("iCl.ISO_Polling - peType: %s\n", pollingInfo[0].peType);
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                btnICL.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check_24dp, 0, 0, 0);
                            }
                        });

                        byte[] abUID = pollingInfo[0].abUID;

                        String UID = arrayBytesToString(abUID);
                        System.out.println("iCl.PollingInfo UID: " + UID);

                        key.abValue = new byte[]{0xf, 0xf, 0xf, 0xf};
                        key.abValue = new byte[]{0x0f, 0x1a, 0x2c, 0x33}; //Cartão Gertec
                        key.abValue = new byte[]{(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF}; // Cartão Cliente


                        System.out.println("iCl.MF_BlockREAD: BEGIN");

                        key.eType = GEDI_CL_e_MF_KeyType.KEY_A;
                        byte[] blockInfo = null;

                        for (i = 0; i < 130; i += 4) {
                            try {
                                iCl.MF_Authentication(i, key, key.abValue);


                                blockInfo = GEDI.getInstance(getApplicationContext()).getCL().MF_BlockRead(i);
                            } catch (GediException e) {
                                if (e.toString().contains("252")) {
                                    System.out.println("iCl.GEDI Exception - Senha Errada!!!! - " + e);

                                } else {
                                    System.out.println("iCl.read error: " + e);
                                }
                                e.printStackTrace();
                            }
                            if (blockInfo != null)
                                System.out.println("iCl.PollingInfo MF_BlockRead[" + String.format("%03d", i) + "]: " + arrayBytesToString(blockInfo));
                            blockInfo = null;
                        }
                        System.out.println("iCl.MF_BlockREAD: END");
                        System.out.println("iCl.");


                        System.out.println("iCl.WRITE");
                        iCl.MF_Authentication(112, key, key.abValue);
                        iCl.MF_BlockWrite(112, hexStringToByteArray("bcde"));

                        iCl.MF_Authentication(116, key, key.abValue);
                        iCl.MF_BlockWrite(116, hexStringToByteArray("ffddd"));

                        System.out.println("iCl.MF_BlockREAD: BEGIN");
                        for (i = 0; i < 130; i += 4) {
                            try {
                                iCl.MF_Authentication(i, key, key.abValue);
                                blockInfo = GEDI.getInstance(getApplicationContext()).getCL().MF_BlockRead(i);
                            } catch (GediException e) {
                                if (e.toString().contains("252")) {
                                    System.out.println("iCl.GEDI Exception - Senha Errada!!!!");

                                } else {
                                    System.out.println("iCl.read error: " + e);
                                }
                                e.printStackTrace();
                            }
                            if (blockInfo != null)
                                System.out.println("iCl.PollingInfo MF_BlockRead[" + String.format("%03d", i) + "]: " + arrayBytesToString(blockInfo));
                            blockInfo = null;
                        }
                        System.out.println("iCl.MF_BlockREAD: END");
                        System.out.println("iCl.");
                        System.out.println("iCl.END");
                        progressDialog.dismiss();
                        return;

                    } catch (GediException gedi_e_ret) {
                        System.out.println("iCl.ISO_Polling\t\t\t- FAIL (GEDI) - " + gedi_e_ret.getErrorCode().name());
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                btnICL.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_close_24dp, 0, 0, 0);
                            }
                        });

                    } catch (Exception e) {
                        System.out.println("iCl.ISO_Polling\t\t\t- FAIL - " + e.getMessage());
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                btnICL.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_close_24dp, 0, 0, 0);
                            }
                        });

                    }
                }
                progressDialog.dismiss();
            }

        }).start();


    }                                                             // Contactless

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }


    public static String arrayBytesToString(byte[] bValues) {

        StringBuilder sbValues = new StringBuilder();
        for (byte b : bValues) {
            sbValues.append(String.format("%02X ", b).replace(" ", ""));
        }
        return sbValues.toString();
    }

    public void btnIKMS(View view) {

        try {
            iKms = GEDI.getInstance().getKMS();
            System.out.println("getKMS\t\t\t- OK");
        } catch (Exception e) {
            System.out.println("getKMS\t\t\t- FAIL");
        }


        try {
            GEDI_KMS_st_Control gedi_kms_st_control = new GEDI_KMS_st_Control();

            String acClearPIN = gedi_kms_st_control.acClearPIN;

            boolean boPlaintextPIN = true;
            GEDI_KMS_st_Data gedi_kms_st_data = new GEDI_KMS_st_Data();

            byte[] abICV = gedi_kms_st_data.abICV;

            List<GEDI_KMS_st_PINBlock> list = null;

            GEDI_KMS_st_PINBlock gedi_kms_st_pinBlock = new GEDI_KMS_st_PINBlock();

            list.add(gedi_kms_st_pinBlock);

            iKms.CapturePINBlock(gedi_kms_st_control, boPlaintextPIN, gedi_kms_st_data, list);


            System.out.println("iKms.CapturePINBlock\t\t\t- OK (GEDI)");
            btnIKMS.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check_24dp, 0, 0, 0);

        } catch (GediException gedi_e_ret) {
            System.out.println("iKms.CapturePINBlock\t\t\t- FAIL (GEDI) - " + gedi_e_ret.getErrorCode().name());
            btnIKMS.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_close_24dp, 0, 0, 0);

        } catch (Exception e) {
            System.out.println("iKms.CapturePINBlock\t\t\t- FAIL - " + e.getMessage());
            btnIKMS.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_close_24dp, 0, 0, 0);

        }
    }

    public void btnIMSR(View view) {
        try {
            iMsr = GEDI.getInstance().getMSR();
            System.out.println("getMSR\t\t\t- OK");
        } catch (Exception e) {
            System.out.println("getMSR\t\t\t- FAIL");
        }


        try {
            GEDI_MSR_st_Tracks read = iMsr.Read();

            byte[] abTk1Buf = read.abTk1Buf;
            byte[] abTk2Buf = read.abTk2Buf;
            byte[] abTk3Buf = read.abTk3Buf;


            System.out.println("byte[]: " + abTk1Buf.toString() + "\n" +
                    "byte[]: " + abTk2Buf.toString() + "\n" +
                    "byte[]: " + abTk3Buf.toString());

            System.out.println("iMsr.Read\t\t\t- OK");
            btnIMSR.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_close_24dp, 0, 0, 0);

        } catch (GediException get_e_ret) {
            System.out.println("iMsr.Read\t\t\t- FAIL - " + get_e_ret.getErrorCode());
            btnIMSR.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_close_24dp, 0, 0, 0);

        } catch (Exception e) {
            System.out.println("iMsr.Read\t\t\t- FAIL - " + e.getMessage());
            btnIMSR.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_close_24dp, 0, 0, 0);

        }


        try {
            GEDI_MSR_st_LastErrors gedi_msr_st_lastErrors = iMsr.LastErrorGet();


            int peTk1ErrValue = gedi_msr_st_lastErrors.peTk1Err.getValue();
            int peTk2ErrValue = gedi_msr_st_lastErrors.peTk2Err.getValue();
            int peTk3ErrValue = gedi_msr_st_lastErrors.peTk3Err.getValue();


            System.out.printf("Track 1: [%d] - %s\nTrack 2: [%d] - %s\nTrack 3: [%d] - %s\n",
                    peTk1ErrValue, GEDI_MSR_e_Status.valueOf(peTk1ErrValue).toString(),
                    peTk2ErrValue, GEDI_MSR_e_Status.valueOf(peTk2ErrValue).toString(),
                    peTk3ErrValue, GEDI_MSR_e_Status.valueOf(peTk3ErrValue).toString());

            System.out.println("LastErrorGet\t\t\t- OK");

            iMsr = null;
        } catch (Exception e) {
            System.out.println("LastErrorGet\t\t\t- FAIL");
        }


    }                                                            // Leitor de Cartão Magnético

    public void btnIPM(View view) {


        PackageManager packageManager = getPackageManager();
        List<ApplicationInfo> listAppPackageName = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);
//
//        for (ApplicationInfo ap : listAppPackageName) {
//            String appPackageName = ap.packageName;
//            System.out.printf("PackageName: %s", appPackageName);
//        }


        try {
            iPM = GEDI.getInstance().getPM();
            System.out.println("getPM\t\t\t- OK");
            btnIPM.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check_24dp, 0, 0, 0);
        } catch (Exception e) {
            System.out.println("getPM\t\t\t- FAIL");
            btnIPM.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_close_24dp, 0, 0, 0);
        }

//        try {
//            iPM.ApDefaultSet(getString(R.string.app_name));
//            System.out.println("iPM.ApDefaultSet\t\t\t- OK");
//        } catch (GediException gedi_e_ret) {
//            System.out.println("iPM.ApDefaultSet\t\t\t- FAIL (GEDI) - " + gedi_e_ret.getErrorCode().name());
//        } catch (Exception e) {
//            System.out.println("iPM.ApDefaultSet\t\t\t- FAIL");
//        }


//        for (ApplicationInfo ap : listAppPackageName) {
//            final String appPackageName = ap.packageName;
//
//            if (!appPackageName.equals("gpos700.gertec.com.infogedi")) {
//
//                try {
//                    iPM.ApDelete(appPackageName);
//                    System.out.printf("PackageName: %s\t", appPackageName);
//                    System.out.println("iPM.ApDelete\t\t\t- OK");
//                } catch (GediException gedi_e_ret) {
//                    System.out.println("iPM.ApDelete\t\t\t- FAIL (GEDI) - " + gedi_e_ret.getErrorCode().name());
//                } catch (Exception e) {
//                    System.out.println("iPM.ApDelete\t\t\t- FAIL - " + e.getMessage());
//                }
//
//            }
//        }


//
//        try {
//            String appPackageName = "br.com.xxx.app";
//
//            IEnums xxx  = GEDI_e_Ret.valueOf(0);
//
//
//
//            iPM.UpdateFromFile(appPackageName, name);
//
//
//        } catch (GediException gedi_e_ret) {
//            System.out.println("iPM.ApDelete\t\t\t- FAIL (GEDI) - " + gedi_e_ret.getErrorCode().name());
//        } catch (Exception e) {
//            System.out.println("iPM.ApDelete\t\t\t- FAIL - " + e.getMessage());
//        }

    }                                                             // Program Manager

    public void btnISMART(View view) {

        progressDialog = new ProgressDialog(MainActivity.this);

        progressDialog.setTitle("ISMART");
        progressDialog.setMessage("INSIRA UM CARTÃO...");
        System.out.println("Aguardando cartão...");

        progressDialog.setIcon(R.drawable.ic_card);

        try {
            iSmart = GEDI.getInstance().getSMART();
            System.out.println("getSMART\t\t\t- OK");
        } catch (Exception e) {
            System.out.println("getSMART\t\t\t- FAIL");
        }

        smartCardPowerOff();


        try {

            boolean isThread = true;

            new Thread(new Runnable() {
                @Override
                public void run() {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.show();
                        }
                    });
                }
            }).start();


            while (isThread == true) {
                if (iSmart.Status(GEDI_SMART_e_Slot.USER) == GEDI_SMART_e_Status.PRESENT) {
                    isThread = false;
                    progressDialog.dismiss();
                }

            }
            System.out.println("iSmart.Status\t\t\t- OK");
            btnISMART.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check_24dp, 0, 0, 0);

        } catch (GediException gedi_e_ret) {
            System.out.println("iSmart.Status\t\t\t- FAIL (GEDI) - " + gedi_e_ret.getErrorCode().name());
            btnISMART.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_close_24dp, 0, 0, 0);

        } catch (Exception e) {
            System.out.println("iSmart.Status\t\t\t- FAIL - " + e.getMessage());
            btnISMART.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_close_24dp, 0, 0, 0);

        }

        smartCardStatus();
        handler.post(new Runnable() {
            @Override
            public void run() {
                progressDialog.dismiss();
                builder.setTitle("GEDI ISMART");
                builder.setMessage(sb.toString());

                alerta = builder.create();
                alerta.show();
            }
        });


//        smartCardResetEMV();
//        smartCardWarmResetEMV();


    }                                                          // Smart Card Reader

    private void gediVersionGet() {
        iGedi = GEDI.getInstance(getApplicationContext());
        String v = iGedi.VersionGet();

        System.out.println("GEDI Version: " + v);


        builder.setTitle("GEDI Version");
        builder.setMessage(v);

        alerta = builder.create();
        alerta.show();


    }                                                             // GEDI Version


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);


    }

    //    MENU
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_app_default, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.main_menu_VersionGet:
                gediVersionGet();
                break;
            case R.id.main_menu_teste:

                break;
        }


        return super.onOptionsItemSelected(item);
    }


}
