package com.plugin;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.graphics.Typeface;

import br.com.gertec.gedi.GEDI;
import br.com.gertec.gedi.interfaces.IGEDI;
import br.com.gertec.gedi.exceptions.GediException;
import com.plugin.ConfigPrint;


public class Printer {

    private IGEDI iGedi = null;
    private final String IMPRESSORA_ERRO = "Impressora com erro.";
    private static boolean isPrintInit = false;
    private IPRNTR iPrint = null;
    private GEDI_PRNTR_st_StringConfig stringConfig;
    private GEDI_PRNTR_st_PictureConfig pictureConfig;
    private GEDI_PRNTR_e_Status status;
    private ConfigPrint configPrint;
    private Typeface typeface;

    public Printer(Context context){
        new Thread(() -> {
            GEDI.init(context);
            this.iGedi = GEDI.getInstance(context);
            this.iPrint = this.iGedi.getPRNTR();
            try {
                new Thread().sleep(250);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

    }

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