package com.plugin;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.nfc.tech.MifareClassic;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import android.app.ProgressDialog;
import android.os.Handler;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaWebView;
import com.plugin.NFCGedi;

import br.com.gertec.gedi.GEDI;
import br.com.gertec.gedi.enums.GEDI_PRNTR_e_Alignment;
import br.com.gertec.gedi.enums.GEDI_PRNTR_e_BarCodeType;
import br.com.gertec.gedi.enums.GEDI_PRNTR_e_Status;
import br.com.gertec.gedi.exceptions.GediException;
import br.com.gertec.gedi.interfaces.IGEDI;
import br.com.gertec.gedi.interfaces.IPRNTR;
import br.com.gertec.gedi.structs.GEDI_PRNTR_st_BarCodeConfig;
import br.com.gertec.gedi.structs.GEDI_PRNTR_st_PictureConfig;
import br.com.gertec.gedi.structs.GEDI_PRNTR_st_StringConfig;
import br.com.gertec.gedi.interfaces.IAUDIO;
import br.com.gertec.gedi.interfaces.ILED;
import br.com.gertec.gedi.interfaces.ICL;
import br.com.gertec.gedi.enums.GEDI_LED_e_Id;
import br.com.gertec.gedi.interfaces.ISMART;
import br.com.gertec.gedi.enums.GEDI_SMART_e_Slot;
import br.com.gertec.gedi.enums.GEDI_SMART_e_Status;
import br.com.gertec.gedi.structs.GEDI_CL_st_ISO_PollingInfo;
import br.com.gertec.gedi.structs.GEDI_CL_st_MF_Key;
import br.com.gertec.gedi.enums.GEDI_CL_e_MF_KeyType;
import br.com.gertec.easylayer.contactless.ContactLessCard;

public class GertecPrinter {
  // Definições
    private final String IMPRESSORA_ERRO = "Impressora com erro.";

    // Statics
    private static boolean isPrintInit = false;

    // Vaviáveis iniciais
    private Activity activity;
    private Context context;

    // Classe de impressão
    private IGEDI iGedi = null;
    private IPRNTR iPrint = null;
    private GEDI_PRNTR_st_StringConfig stringConfig;
    private GEDI_PRNTR_st_PictureConfig pictureConfig;
    private GEDI_PRNTR_e_Status status;
    //Variaval beep
    private IAUDIO iAudio;

    //Variaveis Led
    private ILED iLed;

    //Variaveis Contactless

    ICL icl;

    GEDI_CL_st_ISO_PollingInfo pollingInfo;

    private NfcAdapter nfcAdapter;

    private TextView text;
    private NFCGedi nfcGedi;
    //Variaveis Pagamento
    private ISMART iSmart;
    private StringBuilder sb;

    // Classe de configuração da impressão
    private ConfigPrint configPrint;
    private Typeface typeface;


    /**
    * Método construtor da classe usando o GPOS 700
    * @param c = Context  atual que esta sendo inicializada a class
    **/
    public GertecPrinter(Context c) {
        this.context = c;
        
        startIGEDI();
    }

    /**
    * Método que instância a classe GEDI da lib deve ser usado apenas para o GPOS 700
    *
    * @apiNote = Este mátodo faz a instância da classe GEDI através de uma Thread.
    *            Será sempre chamado na construção da classe.
    *            Não alterar...
    **/
    private void startIGEDI() {
        new Thread(() -> {
            GEDI.init(this.context);
            this.iGedi = GEDI.getInstance(this.context);
            this.iPrint = this.iGedi.getPRNTR();
            try {
                new Thread().sleep(250);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    /**
    * Método que recebe a configuração para ser usada na impressão
    * @param config  = Classe {@link ConfigPrint} que contém toda a configuração
    *                  para a impressão
    * */
    public void setConfigImpressao(ConfigPrint config) {

        this.configPrint = config;

        this.stringConfig = new GEDI_PRNTR_st_StringConfig(new Paint());
        this.stringConfig.paint.setTextSize(configPrint.getTamanho());
        this.stringConfig.paint.setTextAlign(Paint.Align.valueOf(configPrint.getAlinhamento()));
        this.stringConfig.offset = configPrint.getOffSet();
        this.stringConfig.lineSpace = configPrint.getLineSpace();

        switch (configPrint.getFonte()){
            case "NORMAL":
                this.typeface = Typeface.create(configPrint.getFonte(), Typeface.NORMAL );
                break;
            case "DEFAULT":
                this.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL );
                break;
            case "DEFAULT BOLD":
                this.typeface = Typeface.create(Typeface.DEFAULT_BOLD, Typeface.NORMAL );
                break;
            case "MONOSPACE":
                this.typeface = Typeface.create(Typeface.MONOSPACE, Typeface.NORMAL );
                break;
            case "SANS SERIF":
                this.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL );
                break;
            case "SERIF":
                this.typeface = Typeface.create(Typeface.SERIF, Typeface.NORMAL );
                break;
            default:
                this.typeface = Typeface.createFromAsset(this.context.getAssets(), configPrint.getFonte());
        }

        if (this.configPrint.isNegrito() && this.configPrint.isItalico()){
            typeface = Typeface.create(typeface, Typeface.BOLD_ITALIC);
        }else if(this.configPrint.isNegrito()){
            typeface = Typeface.create(typeface, Typeface.BOLD);
        }else if(this.configPrint.isItalico()){
            typeface = Typeface.create(typeface, Typeface.ITALIC);
        }

        if(this.configPrint.isSublinhado()){
            this.stringConfig.paint.setFlags(Paint.UNDERLINE_TEXT_FLAG);
        }

        this.stringConfig.paint.setTypeface(this.typeface);
    }

    /**
     * Método que recebe o atual texto a ser impresso
     * @param texto  = Texto que será impresso.
     *
     * @throws Exception = caso a impressora esteja com erro.
     *
     * */
    public void imprimeTexto(String texto) throws Exception {

        //this.getStatusImpressora();
        try{
            if (!isImpressoraOK()) {
                throw new Exception(IMPRESSORA_ERRO);
            }
            sPrintLine(texto);
        }catch (Exception e){
            throw new Exception(e.getMessage());
        }
    }

    /**
     * Método que recebe o atual texto e o tamanho da fonte que deve ser usado na impressão.
     *
     * @param texto  = Texto que será impresso.
     * @param tamanho = Tamanho da fonte que será usada
     *
     * @throws Exception = caso a impressora esteja com erro.
     *
     * @apiNote = Esse mátodo só altera o tamanho do texto na impressão que for chamado
     * a classe {@link ConfigPrint} não será alterada para continuar sendo usado na impressão da
     * proxíma linha
     *
     * */
    public void imprimeTexto(String texto, int tamanho) throws Exception {

        int tamanhoOld;

        //this.getStatusImpressora();

        try{
            if (!isImpressoraOK()) {
                throw new Exception(IMPRESSORA_ERRO);
            }
            tamanhoOld = this.configPrint.getTamanho();
            this.configPrint.setTamanho(tamanho);
            this.setConfigImpressao(this.configPrint);
            sPrintLine(texto);
            this.configPrint.setTamanho(tamanhoOld);
            this.setConfigImpressao(this.configPrint);
        }catch (Exception e){
            throw new Exception(e.getMessage());
        }
    }

    /**
     * Método que recebe o atual texto e ser o mesmo será impresso em negrito.
     *
     * @param texto  = Texto que será impresso.
     * @param negrito = Caso o texto deva ser impresso em negrito
     *
     * @throws Exception = caso a impressora esteja com erro.
     *
     * @apiNote = Esse mátodo só altera o tamanho do texto na impressão que for chamado
     *      * a classe {@link ConfigPrint} não será alterada para continuar sendo usado na impressão da
     *      * proxíma linha
     *
     * */
    public void imprimeTexto(String texto, boolean negrito) throws Exception {

        boolean negritoOld = false;

        //this.getStatusImpressora();

        try{
            if (!isImpressoraOK()) {
                throw new Exception(IMPRESSORA_ERRO);
            }
            negritoOld = this.configPrint.isNegrito();
            this.configPrint.setNegrito(negrito);
            this.setConfigImpressao(this.configPrint);
            sPrintLine(texto);

            this.configPrint.setNegrito(negritoOld);
            this.setConfigImpressao(this.configPrint);

        }catch (Exception e){
            throw new Exception(e.getMessage());
        }
    }

    /**
     * Método que recebe o atual texto e ser o mesmo será impresso em negrito e/ou itálico.
     *
     * @param texto  = Texto que será impresso.
     * @param negrito = Caso o texto deva ser impresso em negrito
     * @param italico  = Caso o texto deva ser impresso em itálico
     *
     * @throws Exception = caso a impressora esteja com erro.
     *
     * @apiNote = Esse mátodo só altera o tamanho do texto na impressão que for chamado
     *      * a classe {@link ConfigPrint} não será alterada para continuar sendo usado na impressão da
     *      * proxíma linha
     *
     * */
    public void imprimeTexto(String texto, boolean negrito, boolean italico) throws Exception {

        boolean negritoOld = false;
        boolean italicoOld = false;

        //this.getStatusImpressora();

        try{
            if (!isImpressoraOK()) {
                throw new Exception(IMPRESSORA_ERRO);
            }
            negritoOld = this.configPrint.isNegrito();
            italicoOld = this.configPrint.isItalico();
            this.configPrint.setNegrito(negrito);
            this.configPrint.setItalico(italico);
            this.setConfigImpressao(this.configPrint);
            sPrintLine(texto);

            this.configPrint.setNegrito(negritoOld);
            this.configPrint.setItalico(italicoOld);
            this.setConfigImpressao(this.configPrint);

        }catch (Exception e){
            throw new Exception(e.getMessage());
        }
    }
    /**
     * Método que recebe o atual texto e ser o mesmo será impresso em negrito, itálico e/ou  sublinhado.
     *
     * @param texto  = Texto que será impresso.
     * @param negrito = Caso o texto deva ser impresso em negrito
     * @param italico  = Caso o texto deva ser impresso em itálico
     * @param sublinhado   = Caso o texto deva ser impresso em itálico.
     *
     * @throws Exception = caso a impressora esteja com erro.
     *
     * @apiNote = Esse mátodo só altera o tamanho do texto na impressão que for chamado
     *      * a classe {@link ConfigPrint} não será alterada para continuar sendo usado na impressão da
     *      * proxíma linha
     *
     * */
    public void imprimeTexto(String texto, boolean negrito, boolean italico, boolean sublinhado) throws Exception {

        boolean negritoOld = false;
        boolean italicoOld = false;
        boolean sublinhadoOld = false;

        //this.getStatusImpressora();

        try{
            if (!isImpressoraOK()) {
                throw new Exception(IMPRESSORA_ERRO);
            }
            negritoOld = this.configPrint.isNegrito();
            italicoOld = this.configPrint.isItalico();
            sublinhadoOld = this.configPrint.isSublinhado();

            this.configPrint.setNegrito(negrito);
            this.configPrint.setItalico(italico);
            this.configPrint.setSublinhado(sublinhado);
            this.setConfigImpressao(this.configPrint);
            sPrintLine(texto);

            this.configPrint.setNegrito(negritoOld);
            this.configPrint.setItalico(italicoOld);
            this.configPrint.setSublinhado(sublinhadoOld);
            this.setConfigImpressao(this.configPrint);

        }catch (Exception e){
            throw new Exception(e.getMessage());
        }
    }

    /**
     * Método privado que faz a impressão do texto.
     *
     * @param texto = Texto que será impresso
     *
     * @throws GediException = retorna o código do erro
     *
     * */
    private boolean sPrintLine(String texto) throws Exception {
        //Print Data
        try {
            ImpressoraInit();
            this.iPrint.DrawStringExt(this.stringConfig, texto);
            this.avancaLinha(configPrint.getAvancaLinhas());
            //ImpressoraOutput();
            return true;
        } catch (GediException e) {
            throw new GediException(e.getErrorCode());
        }
    }

    /**
     * Método que faz a impressão de imagens
     *
     * @param imagem = Nome da imagem que deve estar na pasta drawable
     *
     * @throws IllegalArgumentException = Argumento passado ilegal
     * @throws GediException = retorna o código do erro.
     *
     * */
    public boolean imprimeImagem( String imagem ) throws GediException {

        int id = 0;
        Bitmap bmp;
        try {

            pictureConfig = new GEDI_PRNTR_st_PictureConfig();

            //Align
            pictureConfig.alignment = GEDI_PRNTR_e_Alignment.valueOf(configPrint.getAlinhamento());

            //Height
            pictureConfig.height = this.configPrint.getiHeight();
            //Width
            pictureConfig.width = this.configPrint.getiWidth();

            if(MainActivity.Model.equals(MainActivity.G700)){
                id = context.getResources().getIdentifier(imagem,"drawable",
                    context.getPackageName());
                bmp = BitmapFactory.decodeResource(context.getResources(),id);
            }else{
                id = this.activity.getApplicationContext().getResources().getIdentifier(
                        imagem,"drawable",
                        this.activity.getApplicationContext().getPackageName());
                bmp = BitmapFactory.decodeResource(this.activity.getApplicationContext().getResources(),id);
            }

            ImpressoraInit();
            this.iPrint.DrawPictureExt(pictureConfig,bmp);
            this.avancaLinha(configPrint.getAvancaLinhas());

            return true;
        }catch (IllegalArgumentException e){
            throw new IllegalArgumentException(e);
        } catch (GediException e) {
            throw new GediException(e.getErrorCode());
        }

    }

    /**
     * Método que faz a impressão de código de barras
     *
     * @param texto = Texto que será usado para a impressão do código de barras
     * @param height  = Tamanho
     * @param width  = Tamanho
     * @param barCodeType  = Tipo do código que será impresso
     *
     * @throws IllegalArgumentException = Argumento passado ilegal
     * @throws GediException = retorna o código do erro.
     *
     * */
    public boolean imprimeBarCode( String texto, int height, int width,  String barCodeType ) throws GediException {

        try {

            GEDI_PRNTR_st_BarCodeConfig barCodeConfig = new GEDI_PRNTR_st_BarCodeConfig();
            //Bar Code Type
            barCodeConfig.barCodeType = GEDI_PRNTR_e_BarCodeType.valueOf(barCodeType);

            //Height
            barCodeConfig.height = height;
            //Width
            barCodeConfig.width = width;

            ImpressoraInit();
            this.iPrint.DrawBarCode(barCodeConfig,texto);
            this.avancaLinha(configPrint.getAvancaLinhas());

            return true;
        }catch (IllegalArgumentException e){
            throw new IllegalArgumentException(e);
        } catch (GediException e) {
            throw new GediException(e.getErrorCode());
        }

    }

    /**
     * Método que faz a impressão de código de barras
     *
     * @param texto = Texto que será usado para a impressão do código de barras
     * @param height  = Tamanho
     * @param width  = Tamanho
     * @param barCodeType  = Tipo do código que será impresso
     *
     * @throws IllegalArgumentException = Argumento passado ilegal
     * @throws GediException = retorna o código do erro.
     *
     * */
    public boolean imprimeBarCodeIMG( String texto, int height, int width,  String barCodeType ) throws GediException, WriterException {

        try {

            MultiFormatWriter multiFormatWriter  = new MultiFormatWriter();
            BitMatrix bitMatrix = multiFormatWriter.encode(texto, BarcodeFormat.valueOf(barCodeType), height, width);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);

            pictureConfig = new GEDI_PRNTR_st_PictureConfig();
            pictureConfig.alignment = GEDI_PRNTR_e_Alignment.valueOf(configPrint.getAlinhamento());

            pictureConfig.height = bitmap.getHeight();
            pictureConfig.width = bitmap.getWidth();

            ImpressoraInit();
            this.iPrint.DrawPictureExt(pictureConfig, bitmap);

            return true;
        }catch (IllegalArgumentException e){
            e.printStackTrace();
            throw new IllegalArgumentException(e);
        } catch (GediException e) {
            e.printStackTrace();
            throw new GediException(e.getErrorCode());
        } catch (WriterException e) {
            e.printStackTrace();
            throw new WriterException(e);
        }

    }

    
    /**
     * Método que faz a inicialização da impressao
     *
     * @throws GediException = retorno o código do erro.
     *
     * */
    public void ImpressoraInit() throws GediException {
        try {
            if( this.iPrint != null && !isPrintInit  ){
                this.iPrint.Init();
                isPrintInit = true;
            }
        } catch (GediException e) {
            e.printStackTrace();
            throw new GediException(e.getErrorCode());
        }
    }

    /**
     * Método que faz a finalizacao do objeto iPrint
     *
     * @throws GediException = retorno o código do erro.
     *
     * */
    public void ImpressoraOutput() throws GediException {
        try {
            if( this.iPrint != null  ){
                this.iPrint.Output();
                isPrintInit = false;
            }
        } catch (GediException e) {
            e.printStackTrace();
            throw new GediException(e.getErrorCode());
        }
    }

    //MetodosTesting
    //Inicio Métodos Contactless EasyLayer

    public String teste(){
        ContactLessCard con = new ContactLessCard.ContactLessCard();
        //byte[] retorno = ContactLessCard.connect(10000);
        return "ok";
    }


    //Fim metodos


    //Inicio Metodos Contactless
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
            nfcAdapter = NfcAdapter.getDefaultAdapter(this.context);

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
        /*key.abValue = new byte[]{0xf, 0xf, 0xf, 0xf};
        key.abValue = new byte[]{0x0f, 0x1a, 0x2c, 0x33}; //Cartão Gertec
        key.abValue = new byte[]{(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF}; // Cartão Cliente
        key.eType = GEDI_CL_e_MF_KeyType.KEY_A;
        byte[] blockInfo = null;*/
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


    //Fim metodos Contactless


    //Metodos ISmart
    public String checkISmart(){
        
        sb = new StringBuilder();
        for (GEDI_SMART_e_Slot cd : GEDI_SMART_e_Slot.values()) {

            try {

                GEDI_SMART_e_Status status = iSmart.Status(cd);
                final String r = "Status: "+status;


                sb.append(r);


            } catch (Exception e) {
                System.out.println("iSmart.Status- FAIL - " + e.getMessage());
            }
        }

        return "SB: "+sb.toString();
    }
    public String setSmartCardPowerOff(){
        try {
            iSmart = GEDI.getInstance().getSMART();
            
        } catch (Exception e) {
            return "getSMART FAIL";
        }

        try {
            for (GEDI_SMART_e_Slot c : GEDI_SMART_e_Slot.values()) {
                try{
                    iSmart.PowerOff(c);
                }catch(Exception e){

                }
            }
        } catch (Exception e) {
            return e.getMessage();
        }  

        
        return "Ativado";
    }



    //Metodo Testados e funcionando corretamente

    //-------------------Metodos Led------------------
     /**
     * Método Acender o led
     *
     * @throws GediException = vai retorno o código do erro.
     *
     * @return String
     *
     * */
    public String led() {

        try {
            iLed = GEDI.getInstance().getLED();

            if (iLed == null) {
                throw new GediException(10800);
            }

        } catch (GediException e) {
            return e.getMessage();
        } catch (Exception e) {
            return e.getMessage();
        }

        ledOn();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            return e.getMessage();
        }

        ledOff();

        return "Ok";
    }  

    private String ledOff() {
        for (GEDI_LED_e_Id c : GEDI_LED_e_Id.values()) {

            if (c.equals(GEDI_LED_e_Id.GEDI_LED_ID_CONTACTLESS_RED) ||
                    c.equals(GEDI_LED_e_Id.GEDI_LED_ID_CONTACTLESS_GREEN) ||
                    c.equals(GEDI_LED_e_Id.GEDI_LED_ID_CONTACTLESS_ORANGE) ||
                    c.equals(GEDI_LED_e_Id.GEDI_LED_ID_CONTACTLESS_BLUE)) {
                try {
                    iLed.Set(c, false);
                } catch (GediException e) {
                    return "iLed.Set - " + c + ":- FAIL -- " + e.getErrorCode().name();
                } catch (Exception e) {
                    return "iLed.Set - \t\t\t- FAIL - " + e.getMessage();
                }
            }
        }
        return "Erro";
    }

    private String ledOn() {
        for (GEDI_LED_e_Id c : GEDI_LED_e_Id.values()) {

            if (c.equals(GEDI_LED_e_Id.GEDI_LED_ID_CONTACTLESS_RED) ||
                    c.equals(GEDI_LED_e_Id.GEDI_LED_ID_CONTACTLESS_GREEN) ||
                    c.equals(GEDI_LED_e_Id.GEDI_LED_ID_CONTACTLESS_ORANGE) ||
                    c.equals(GEDI_LED_e_Id.GEDI_LED_ID_CONTACTLESS_BLUE)) {

                try {
                    iLed.Set(c, true);
                } catch (GediException e) {
                    return "iLed.Set - " + c + ":\t- FAIL -- " + e.getErrorCode().name();

                } catch (Exception e) {
                    return "iLed.Set - \t- FAIL - " + e.getMessage();
                }
            }


        }
        return "Erro";
    }
    //----------------Fim metodos Led------------------
     /**
     * Método que retorna o atual estado da impressora
     *
     * @throws GediException = vai retorno o código do erro.
     *
     * @return String = traduzStatusImpressora()
     *
     * */
    public String getStatusImpressora() throws GediException {
        try {
            ImpressoraInit();
            this.status = this.iPrint.Status();
        } catch (GediException e) {
            throw new GediException(e.getErrorCode());
        }

        return traduzStatusImpressora(this.status);
    }

    /**
     * Metodo para Fazer um beep na impressora
     *
     * @throws GediException = vai retorno o código do erro.
     *
     * @return String 
     *
     * */
    
    public String Beep() throws GediException {

        try {
            iAudio = GEDI.getInstance().getAUDIO();
            try {
                iAudio.Beep();
                return "iAudio.Beep - OK";
            } catch (Exception e) {
                return "iAudio.Beep - FAIL";
            }
            
        } catch (Exception e) {
            return "getAUDIO - FAIL";
        }
    }


    //METODOS AUXILIARES

    /**
     * Método que faz a tradução do status atual da impressora.
     *
     * @param status = Recebe o {@link GEDI_PRNTR_e_Status} como atributo
     *
     * @return String = Retorno o atual status da impressora
     *
     * */
    private String traduzStatusImpressora(GEDI_PRNTR_e_Status status) {
        String retorno;
        switch (status) {
            case OK:
                retorno = "IMPRESSORA OK";
                break;

            case OUT_OF_PAPER:
                retorno = "SEM PAPEL";
                break;

            case OVERHEAT:
                retorno = "SUPER AQUECIMENTO";
                break;

            default:
                retorno = "ERRO DESCONHECIDO";
                break;
        }

        return retorno;
    }

    /**
     * Método que faz o avanço de linhas após uma impressão.
     *
     * @param linhas = Número de linhas que dever ser pulado após a impressão.
     *
     * @throws GediException = retorna o código do erro.
     *
     * @apiNote = Esse método não deve ser chamado dentro de um FOR ou WHILE,
     * o número de linhas deve ser sempre passado no atributo do método.
     *
     * */
    public void avancaLinha(int linhas) throws GediException {
        try {
            if(linhas > 0){
                this.iPrint.DrawBlankLine(linhas);
            }
        } catch (GediException e) {
           throw new GediException(e.getErrorCode());
        }
    }

    /**
     * Método que retorno se a impressora está apta a fazer impressões
     *
     * @return true = quando estiver tudo ok.
     *
     * */
    public boolean isImpressoraOK(){

        if( status.getValue() == 0 ){
            return true;
        }
        return false;
    }


}
