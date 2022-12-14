package com.plugin;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.Log;

import br.com.gertec.gedi.GEDI;
import br.com.gertec.gedi.enums.GEDI_PRNTR_e_Status;
import br.com.gertec.gedi.exceptions.GediException;
import br.com.gertec.gedi.interfaces.IGEDI;
import br.com.gertec.gedi.interfaces.IPRNTR;
import br.com.gertec.gedi.structs.GEDI_PRNTR_st_StringConfig;

public class Printer {

    private IPRNTR iPrint = null;
    private IGEDI iGedi;
    private final String IMPRESSORA_ERRO = "Impressora com erro.";
    private GEDI_PRNTR_e_Status status;
    private static boolean isPrintInit = false;
    private GEDI_PRNTR_st_StringConfig stringConfig;
    private ConfigPrint configPrint = new ConfigPrint();
    private Typeface typeface;
    private Context context;


    public Printer(Context context){
        this.context = context;
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

    public boolean isImpressoraOK(){

        if(status.getValue() == 0 ){
            return true;
        }
        return false;
    }

    public String getStatusImpressora() throws GediException {
        try {
            ImpressoraInit();
            this.status = this.iPrint.Status();
        } catch (GediException e) {
            throw new GediException(e.getErrorCode());
        }

        return traduzStatusImpressora(this.status);
    }

    public void imprimeTexto(String texto) throws Exception {
        getStatusImpressora();
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
     * M??todo que faz a finalizacao do objeto iPrint
     *
     * @throws GediException = retorno o c??digo do erro.
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
     * M??todo que faz o avan??o de linhas ap??s uma impress??o.
     *
     * @param linhas = N??mero de linhas que dever ser pulado ap??s a impress??o.
     *
     * @throws GediException = retorna o c??digo do erro.
     *
     * @apiNote = Esse m??todo n??o deve ser chamado dentro de um FOR ou WHILE,
     * o n??mero de linhas deve ser sempre passado no atributo do m??todo.
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

    public void confgPrint(boolean isItalic,boolean isSublinhado,boolean isNegrito,int fontSize,String font,String alinhamento){
        this.configPrint.setItalico(isItalic);
        this.configPrint.setSublinhado(isSublinhado);
        this.configPrint.setNegrito(isNegrito);
        this.configPrint.setTamanho(fontSize);
        this.configPrint.setFonte(font);
        this.configPrint.setAlinhamento(alinhamento);
        this.setConfigImpressao(this.configPrint);
    }

    /**
     * M??todo que recebe a configura????o para ser usada na impress??o
     * @param config  = Classe {@link ConfigPrint} que cont??m toda a configura????o
     *                  para a impress??o
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

    //Metodos Auxiliares
    /**
     * M??todo que faz a tradu????o do status atual da impressora.
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
     * M??todo que faz a inicializa????o da impressao
     *
     * @throws GediException = retorno o c??digo do erro.
     *
     * */
    private void ImpressoraInit() throws GediException {
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
     * M??todo privado que faz a impress??o do texto.
     *
     * @param texto = Texto que ser?? impresso
     *
     * @throws GediException = retorna o c??digo do erro
     *
     * */
    private boolean sPrintLine(String texto) throws Exception {
        //Print Data
        try {
            ImpressoraInit();
            this.iPrint.DrawStringExt(this.stringConfig, texto);
            this.avancaLinha(configPrint.getAvancaLinhas());
            return true;
        } catch (GediException e) {
            throw new GediException(e.getErrorCode());
        }
    }

}
