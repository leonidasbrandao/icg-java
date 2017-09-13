/**
 * 
 * iMath - http://www.matematica.br
 * 
 * <p>Title: iCG - Interactive Computer (Computador Gaveteiro Interativo)</p>
 * 
 * <p>Description: emulator (run the "binary" code of iCG)</p>
 *                 configurantions constants to color and fonts
 * 
 * <p>Copyright: Copyleft (c) 2003</p>
 * <p>Company: LInE - http://www.ime.usp.br/line</p>
 * 
 * @author Leônidas de Oliveira Brandão, Heitor, Newton, Paulo

 * @version 1.0: 2012-05-21 (identation and comments); 2008-10-02 (an import); 2006-04-02 (first version from an undergradeate course MAC323 - in 2003)
 * 
 * @see icg.emulador.EmulatorBaseClass; icg.emulador.Epi; icg.emulador.Memoria
 * 
 **/

package icg.emulador;

import java.awt.*;
import java.awt.event.*;

import icg.configuracoes.Configuracao;
import icg.iCG;
import icg.ig.Botao;
import icg.ig.JanelaAjuda;
import icg.ig.TrataImage;
import icg.msg.Bundle;

public class EmulatorMainPanel extends Panel {

  // (x0,y0) definido originalmente me icg/iCG
  public static final int x0 = iCG.x0, y0 = iCG.y0, // coordenadas iniciais para colocar painéis na janela principal

          xExec    = iCG.xExec,    yExec    = iCG.yExec   ,   // jan. para digitar código executável (x0+513, y0+ 48, 65,   185)
          xOk      = iCG.xOk     , yOk      = iCG.yOk     ,
        //xEm      = iCG.xEm     , yEm      = iCG.yEm     , 
        //xEmPasso = iCG.xEmPasso, yEmPasso = iCG.yEmPasso,
          xEnt     = iCG.xEnt    , yEnt     = iCG.yEnt    ,  // textUserInput - xEnt,  yEnt, largEnt, altEnt
          xSai     = iCG.xSai    , ySai     = iCG.ySai    ,  // labelInstrComm - largSai=99, altSai=21
          xInst    = iCG.xInst   , yInst    = iCG.yInst   ,  // labelInstrValue - largInst = 159, altInst = 23 // x0+109, y0+  8, 159, 23 
          xIExec   = iCG.xIExec  , yIExec   = iCG.yIExec  ,  // labelInstruction - largIExec = 73, altIExec = 15
          xTEnt    = iCG.xTEnt   , yTEnt    = iCG.yTEnt   ,  // labelInputComm (x0+212, y0+ 10, 0, 0)
          xTAC     = iCG.xTAC    , yTAC     = iCG.yTAC    ,  // labelAcumulator (x0+407, y0+ 41, 76, 17)
          xTtAC    = iCG.xTtAC   , yTtAC    = iCG.yTtAC   ,  // labelAcumValue 
          xBL      = iCG.xBL     , yBL      = iCG.yBL     ,  // painelNumLinhasMem (x0+  8, y0+ 55, 22, 179)
          xBC      = iCG.xBC     , yBC      = iCG.yBC     ,  // painelColLinhasMem (x0+ 29, y0+ 40, 459, 15)
          xLACC    = iCG.xLACC   , yLACC    = iCG.yLACC   ,  // lacc (x0+407, y0+  9, 76, 17)
          xLnh     = iCG.xLnh    , yLnh     = iCG.yLnh    ,  // painelNumLinhasCod // num. linhas ao lado "jan. de código"
        //xAtual   = iCG.xAtual  , yAtual   = iCG.yAtual  ,  // botaoAtualiza

          xEnd    = iCG.xEnd,     yEnd     = iCG.yEnd,    // label com endereço do iCG
          xEnv    = iCG.xEnv,     yEnv     = iCG.yEnv,
          xEm     = iCG.xEm,      yEm      = iCG.yEm,
          xEmPasso= iCG.xEmPasso, yEmPasso = iCG.yEmPasso,
          xAtual  = iCG.xAtual,   yAtual   = iCG.yAtual;

  private static int // larguras e alturas de botões
          //E altBotao = iCG.altBotao, //Eclipse - nao usado...?

          largExec    = iCG.largExec, altExec = iCG.altExec, // jan. para digitar código executável
          largTEnt    = iCG.largTEnt, altTEnt = iCG.altTEnt, // labelInputComm
          largTAC     = iCG.largTAC , altTAC  = iCG.altTAC , // labelAcumulator
          largTtAC    = iCG.largTtAC, altTtAC = iCG.altTtAC, // labelAcumValue
          largBL      = iCG.largBL  , altBL   = iCG.altBL  , // painelNumLinhasMem
          largBC      = iCG.largBC  , altBC   = iCG.altBC  , // painelColLinhasMem
          largLACC    = iCG.largLACC, altLACC = iCG.altLACC, // labelOutput

          largEnv     = iCG.largEnv,     altEnv     = iCG.altBotao,  //  81; 28
          largEm      = iCG.largEm,      altEm      = iCG.altBotao,  //  81; 28
          largEmPasso = iCG.largEmPasso, altEmPasso = iCG.altBotao,  // 109; 28

          largEnt     = iCG.largEnt  , altEnt     = iCG.altBotao,  // textUserInput - xEnt,  yEnt, largEnt, altEnt
          largSai     = iCG.largSai  , altSai     = iCG.altBotao,  // labelInstrComm
          largInst    = iCG.largInst , altInst    = iCG.altBotao,  // x0+109, y0+  8, 159, 23 labelInstrValue
          largIExec   = iCG.largIExec, altIExec   = iCG.altIExec,  // labelInstruction

          largAtual   = iCG.largAtual,   altAtual   = iCG.altBotao,  //  81; 28
          largOk      = iCG.largOk,      altOk      = iCG.altBotao,  //  50; 28

          largEnd     = iCG.largEnd, altEnd  =  iCG.altEnd,      // rótulo com end. do iCG (380 15)
          largLnh     = iCG.largLnh, altLnh  = iCG.altLnh; // painelNumLinhasCod (22,184)

  //T_ private static void iniciaEmPanel () { // está abaixo, em "Emulador_Panel(EmulatorBaseClass)" "//+-+ iniciaEmPanel();"
  //T_         altBotao = 28;
  //T_         largEnv     = iCG.largEnv;     altEnv     = iCG.altBotao,  //  81; 28
  //T_         largEm      = iCG.largEm;      altEm      = iCG.altBotao,  //  81; 28
  //T_         largEmPasso = iCG.largEmPasso; altEmPasso = iCG.altBotao,  // 109; 28
  //T_         largEnt     = iCG.largEnt  ,   altEnt     = iCG.altBotao,  // textUserInput
  //T_         largSai     = iCG.largSai  ,   altSai     = iCG.altBotao,  // labelInstrComm
  //T_         largInst    = iCG.largInst ,   altInst    = iCG.altBotao,  // x0+109, y0+  8, 159, 23 labelInstrValue
  //T_         largIExec   = iCG.largIExec,   altIExec   = iCG.altIExec,  // labelInstruction
  //T_         largAtual   = iCG.largAtual;   altAtual   = iCG.altBotao;  //  81; 28
  //T_         largOk      = iCG.largOk;      altOk      = iCG.altBotao;  //  50; 28
  //T_   }


  Label label_iCG_Address = new Label("["+Configuracao.versao+"] iCG - http://www.matematica.br/programas/icg");

  Panel painelMemoria         = new Panel();     // painel com as 100 células de memória
  Panel painelInferior        = new Panel();     // EmulatorBaseClass(String,boolean) invoca este campo

  // Vários destes componentes são alterados no EmulatorBaseClass (alterar aqui, precisa revisar lá)
  TextArea textInputExecCode;                    // area para digitar instrucoes para execucao (codigo de maquina)

  //---- DB
  // AWT: técnica de DOUBLE BUFFERING evita "flicker" e aqui reduz falhas em mostrar os botoes
  Image offscreen  = null;
  Graphics offgraphics = null;

  // Button: to enter numbers (used in several methods of EmulatorBaseClass)
  private TextField textUserInput = new TextField(Bundle.msg("inputText"));

  // Button: to register data entrance (from "EmulatorBaseClass.ok_actionPerformed(ActionEvent)")
  private Button botaoOk     = new Button(); // but an ENTER after data entrance also register it

  private Label
    labelInstrComm        = new Label(),     // "caixa" que conterá todas as saídas (comando '8ee')
    labelOutput           = new Label(),     // para texto de saída, em "EmulatorBaseClass!reseta()" e "EmulatorBaseClass!"execInst(boolean)"
    labelInstrValue       = new Label(),
    labelInstruction      = new Label(),
    labelInputComm        = new Label(),
    labelAcumulator       = new Label(),     // fixo "Acumulador"
    labelAcumValue        = new Label();     // valor atual do acumulador

  Panel painelNumLinhasMem    = new Panel();
  Panel painelColLinhasMem    = new Panel();
  GridLayout gridLayout1      = new GridLayout();
  GridLayout gridLayout3      = new GridLayout();
  Panel painelNumLinhasCod    = new Panel();
  GridLayout gridLayout2      = new GridLayout();
  GridLayout gridLayout4      = new GridLayout();

  // Vem de EmulatorBaseClass
  // Botao botoes[] tem que ser passado para EmulatorBaseClass -> Emulador_Panel na ordem:
  Botao botoes[]; // para botões que são passados via iCG -> EmulatorBaseClass -> Emulador_Panel
  Botao botaoCompilador, // botoes[0]
        botaoEmulador  , // botoes[1]
        botaoGabarito  , // botoes[2]
        botaoEnviar    , // botoes[3]
        botaoRoda      , // botoes[4]
        botaoRodaPP    , // botoes[5]
        botaoAtualiza  , // botoes[6]
        botaoInfo      , // botoes[7]
        botaoSobre     , // botoes[8]
        botaoAjuda     ; // botoes[9]

  // Allow to load buttons 'botaoCompilador, botaoGabarito, botaoEnviar
  private boolean allowLoadCompiler, loadButtonBuildExerc, loadButtonExercEvaluate;

  private EmulatorBaseClass emulatorBaseClass;

  // icg/emulador/EmulatorBaseClass.java: EmulatorBaseClass (iCG icgPrincipal,...); setCodigo(String strCode); actionPerforOpenFileSession(ActionEvent e);
  // icg/iCG.java: setAllContents()
  public void setTextInputExecCode (String value) {
    textInputExecCode.setText(value); 
    //try { String str00="";System.out.println(str00.charAt(3)); } catch (Exception e) { e.printStackTrace(); }
    }
  protected void setEnabledBotaoOk (boolean value) { botaoOk.setEnabled(value); }
  protected Button getBotaoOk () { return botaoOk; }

  // icg.emulador.EmulatorBaseClass.java: metodos utilizados para definir layout do Emulador
  protected void setTextUserInput (String value) {
    textUserInput.setText(value);
    }
  protected String getTextUserInput () { return textUserInput.getText().trim(); }
  protected void setEnabledTextUserInput (boolean value) { // EmulatorBaseClass.public void execInst(boolean passoapasso)
    // System.out.println("[EmulatorMainPanel.setEnabledTextUserInput] value="+value);
    if (value) {
       textUserInput.setText(""); // clear the last data input
       textUserInput.setEnabled(true); // in order to show focus on it
       textUserInput.requestFocus();
       } // para que usuário não precise clicar na TextField p/ ganhar foco
    textUserInput.setEnabled(value);
    }
  protected void setLabelInstrValue (String value) { labelInstrValue.setText(value); }
  protected void setLabelOutPut (String value) { labelOutput.setText(value); } // EmulatorBaseClass.java
  protected void setLabelInstrComm (String value) { labelInstrComm.setText(value); } // EmulatorBaseClass.java
  protected Label getLabelOutPut () { return labelOutput; } // EmulatorBaseClass.java: construtor
  protected void setLabelAcumValue (String value) { labelAcumValue.setText(value); } // EmulatorBaseClass.java
  // Ver tb src/icg/emulador/EmulatorBaseClass.java
  // 'Label Instrucao_Executada, instrucao,       text_Saida,     labelSaida,  texto_Entrada,  tituloAcumulador, texto_Acumulador' ->
  // 'Label labelInstruction,    labelInstrValue, labelInstrComm, labelOutput, labelInputComm, labelAcumulator,  labelAcumValue'

  private void setLabelFonts () {
    labelInstruction.setFont(Configuracao.ftBold10);    // "Instrucao: N"                     - new Rectangle(xIExec, yIExec, largIExec, altIExec)
    labelInstrValue.setFont(Configuracao.ftPlain10);    //   <saida de "Instrucao">           - new Rectangle(xInst, yInst, largInst, altInst)
    labelInstrComm.setFont(Configuracao.ftPlain10);     //     <comentario sobre "Instrucao"> - new Rectangle(xSai,  ySai, largSai, altSai)
    labelOutput.setFont(Configuracao.ftPlain10);        //       <area para mostrar a saidas> - new Rectangle(xLACC, yLACC, largLACC, altLACC)

    textUserInput.setFont(Configuracao.ftBold10);       // <entrada de usuario> 
    labelInputComm.setFont(Configuracao.ftPlain10);     //   <comentario sobre ent. usuario>  - new Rectangle(xTEnt, yTEnt, largTEnt, altTEnt)

    labelAcumulator.setFont(Configuracao.ftBold10);     // "Acumulador: "                     - new Rectangle(xTAC, yTAC, largTAC, altTAC)
    labelAcumValue.setFont(Configuracao.ftPlain10);     //   <saida do "Acumulador">          - new Rectangle(xTtAC, yTtAC, largTtAC, altTtAC)
    }


  public EmulatorMainPanel (EmulatorBaseClass emuladorApplet, Botao botoes[],
                            boolean allowLoadCompiler, boolean loadButtonBuildExerc, boolean loadButtonExercEvaluate) {
    this.emulatorBaseClass = emuladorApplet;
    this.botoes = botoes;
    this.allowLoadCompiler = allowLoadCompiler; // botaoCompilador -  if true => allow the button to load iCG Compiler
    this.loadButtonBuildExerc = loadButtonBuildExerc; // botaoGabarito
    this.loadButtonExercEvaluate = loadButtonExercEvaluate; // botaoEnviar

    try {
      montaPainelPrincipal();
      }
    catch (Exception ex) {
      System.err.println("Erro: ao tentar montar painel principal do emulador: "+emuladorApplet);
      ex.printStackTrace();
      }
    }


  public String getTextInputExecCode () { return textInputExecCode.getText(); } // texto com instruções para execução

  // Chamado em: iCG.iniciaCG()
  // Usa: PainelCorFonte(Object obj, int tipoItem, String texto) {
  public void setFonte (int contI, Font fonte) {
    // System.out.println("[Emulador_Panel!setFonte] fonte="+fonte);
    if (contI==0) // é emulador
       for (int i=0; i<10; i++)
           for (int j=0; j<10; j++) 
               emulatorBaseClass.memoria[i][j].setFont(fonte); //Configuracao.fonteCodigo);
    else // é compilador
       textInputExecCode.setFont(fonte);
    }
  public Font getFonte (int tipoItem) {
    if (tipoItem==0) // tipoItem=0 => "memoria[][]": memória
       return  emulatorBaseClass.memoria[0][0].getFont(); //Configuracao.fonteCodigo);
    else          // tipoItem=1 => "textInputExecCode": código executável
       return textInputExecCode.getFont();
    }


  //HH
  // Build the "second line": main buttons <Emulator, Compiler, ..., About, Help>
  private void buildMainButtons () {
    // Passei para 'EmulatorMainPanel.java': this.botaoCompilador, this.botaoEmulador, this.botaoSobre, this.botaoAjuda
    // botaoEmulador - botaoCompilador - botaoGabarito - botaoEnviar - botaoRoda - botaoRodaPP - botaoAtualiza - botaoSobre - botaoAjuda

    this.botaoEmulador.setBounds(new Rectangle(iCG.xEmul,iCG.yEmul, TrataImage.ALTURA,TrataImage.LARGURA)); //
    this.botaoCompilador.setBounds(new Rectangle(iCG.xComp, iCG.yComp, TrataImage.ALTURA,TrataImage.LARGURA)); //
    this.botaoGabarito.setBounds(new Rectangle(iCG.xGab,iCG.yGab, TrataImage.ALTURA,TrataImage.LARGURA)); //
    this.botaoEnviar.setBounds(new Rectangle(iCG.xEnv,iCG.yEnv, TrataImage.ALTURA,TrataImage.LARGURA));
    this.botaoRoda.setBounds(new Rectangle(iCG.xEm,iCG.yEm, TrataImage.ALTURA,TrataImage.LARGURA)); //
    this.botaoRodaPP.setBounds(new Rectangle(iCG.xEmPasso,iCG.yEmPasso, TrataImage.ALTURA,TrataImage.LARGURA)); //
    this.botaoAtualiza.setBounds(new Rectangle(iCG.xAtual,iCG.yAtual, TrataImage.ALTURA,TrataImage.LARGURA)); //
    this.botaoSobre.setBounds(new Rectangle(iCG.xSobre,iCG.ySobre, TrataImage.ALTURA,TrataImage.LARGURA)); // iCG
    this.botaoAjuda.setBounds(new Rectangle(iCG.xAjudaE,iCG.yAjudaE, TrataImage.ALTURA,TrataImage.LARGURA));

    // iCG
    this.botaoEmulador.setFont(Configuracao.fonteBotao);
    this.botaoEmulador.setBackground(Configuracao.corFundo1);
    this.botaoEmulador.setForeground(Configuracao.corFrente1);
    this.botaoEmulador.setLocale(java.util.Locale.getDefault());

    //HH iCG
    // if (allowLoadCompiler) // definido em 'icg.iCG.iniciaCG()'
    this.botaoCompilador.setBackground(Configuracao.corFundo1);  // cor de fundo do botão botaoCompilador
    this.botaoCompilador.setFont(Configuracao.fonteBotao); //(Configuracao.fonteDN12);        // fonte do botão botaoCompilador
    this.botaoCompilador.setForeground(Configuracao.corFrente1); //

    //HH iCG
    // if (loadButtonBuildExerc) // precisa generalizar para parametro 'ilm_param_authoring'
    this.botaoGabarito.setBackground(Configuracao.corFundo1);
    this.botaoGabarito.setFont(Configuracao.fonteBotao);
    this.botaoGabarito.setForeground(Configuracao.corFrente1);

    //HH iCG
    // if (loadButtonExercEvaluate) //HH
    // Button "Sent answer" - "Enviar" - deveria passar para "Avaliar resposta" !!!! ????
    this.botaoEnviar.setBackground(Configuracao.corFundo1);  //
    this.botaoEnviar.setFont(Configuracao.fonteBotao);       //
    this.botaoEnviar.setForeground(Configuracao.corFrente1); //

    this.botaoRoda.setBackground(Configuracao.corFundo1);    //
    this.botaoRoda.setFont(Configuracao.fonteBotao); // fonte botão superior
    this.botaoRoda.setForeground(Color.white);
    this.botaoRoda.setLocale(java.util.Locale.getDefault());

    this.botaoRodaPP.setBackground(Configuracao.corFundo1);    //botaoRodaPP.setBackground(Color.black);
    this.botaoRodaPP.setFont(Configuracao.fonteBotao); // fonte botão superior
    this.botaoRodaPP.setForeground(Color.white);

    this.botaoAtualiza.setForeground(Color.white);
    this.botaoAtualiza.setFont(Configuracao.fonteBotao); // fonte botão superior
    this.botaoAtualiza.setBackground(Configuracao.corFundo1); //

    // Botão de ajuda sobre o Emulador: instruções, como atualizar memória, como emular, emular passo a passo
    this.botaoAjuda.setBackground(new Color(0, 111, 194));
    this.botaoAjuda.setForeground(Color.white);
    this.botaoAjuda.setFont(Configuracao.fonteBotao2); // Configuracao.ftBold12); //

    this.add(botaoEmulador, null); // Common to Emulator-Compiler
    if (allowLoadCompiler) // definido em 'icg.iCG.iniciaCG()'
       this.add(botaoCompilador, null); // Common to Emulator-Compiler
    if (loadButtonExercEvaluate)
       this.add(botaoEnviar,null);
    if (loadButtonBuildExerc) // precisa generalizar para parametro 'ilm_param_authoring'
       this.add(botaoGabarito,null);
    this.add(botaoRoda, null);
    this.add(botaoRodaPP, null);
    this.add(botaoAtualiza, null);
    this.add(botaoSobre, null); // Common to Emulator-Compiler
    this.add(botaoAjuda, null); // Common to Emulator-Compiler

    } // private void buildMainButtons()


  // Add to EmulatorMainPanel all buttons that are common to Emulator-Compiler
  public void addEmulatorButtons () { // icg.iCG.setBotaoEmulador() - when moving from Compiler to Emulator
    this.add(botaoEmulador, null); // Common to Emulator-Compiler
    if (allowLoadCompiler) // definido em 'icg.iCG.iniciaCG()'
       this.add(botaoCompilador, null); // Common to Emulator-Compiler
    this.add(botaoSobre, null); // Common to Emulator-Compiler
    this.add(botaoAjuda, null); // Common to Emulator-Compiler
    } // public void addEmulatorButtons()


  // Constroi o painel geral do Emulador
  void montaPainelPrincipal () throws Exception {

    if (botoes==null) { // in case it came here from 'icg.iCG.acaoEnviar(): emulatorBaseClass.execInst(false)'
       // this means that the Emulator layout could not be changed
       return;
       }

    this.setLayout(null);
    textInputExecCode = new TextArea("", 6, 12); // TextArea com o código que pode ser digitado

    // Origem em 'icg.iCG.iniciaCG()'
    this.botaoCompilador = botoes[0];
    this.botaoEmulador   = botoes[1];
    this.botaoGabarito   = botoes[2];
    this.botaoEnviar     = botoes[3];
    this.botaoRoda       = botoes[4];
    this.botaoRodaPP     = botoes[5];
    this.botaoAtualiza   = botoes[6];
    this.botaoInfo       = botoes[7];
    this.botaoSobre      = botoes[8];
    this.botaoAjuda      = botoes[9];
    buildMainButtons(); // load and positioning of these buttons in EmulatorMainPanel

    painelMemoria.setBackground(Configuracao.corAzulEscuro1); // cor que define as linhas/colunas separando células de memória
    painelMemoria.setFont(Configuracao.fonteDN10);//ftPlain8);//fonteCodigo); //new java.awt.Font("Serif", 0, 12));
    painelMemoria.setLocale(java.util.Locale.getDefault());
    painelMemoria.setBounds (new Rectangle(iCG.xPainelMem, iCG.yPainelMem, iCG.lPainelMem, iCG.aPainelMem));
    //                                    (x,              y,              larg,           alt)
    gridLayout1.setColumns(10);
    gridLayout1.setHgap(2);
    gridLayout1.setRows(10);
    gridLayout1.setVgap(2);

    painelMemoria.setLayout(gridLayout1);

    // Cor de fundo do painel que recebe o AC, a caixa para 'entradas', a caixa para 'saída' e msg de instrução executada
    // área c/ "instrução executada", "saída" e "entrada de dados"
    painelInferior.setBackground(Configuracao.corFundoPainel_AC_Ent_Sai); // corFundoPainel_AC_Ent_Sai = new Color(145, 166, 255));
    painelInferior.setBounds(new Rectangle(iCG.xPainelInf,iCG.yPainelInf,iCG.lPainelInf,iCG.aPainelInf));
    painelInferior.setLayout(null);

    // Caixa de texto para entrada do código:
    textInputExecCode.setText(""); // texto de entrada para execução
    textInputExecCode.setBounds(new Rectangle(xExec, yExec, largExec, altExec)); // tamanho e posição da janela para digitar código
    //                                        xExec, yExec: sao coord. sup. esq. de onde vai a janela
    //                                                      largExec, altExec:  largura e altura da janela    
    textInputExecCode.setBackground(Configuracao.corFundoEntradas); // cor que define as linhas/colunas separando células de memória
    textInputExecCode.setForeground(Configuracao.corFrente2);     //

    textInputExecCode.setFont(Configuracao.ftBold11);             // vide método "setLinha(Panel,int,int)"
    textInputExecCode.setColumns(6);
    textInputExecCode.setEditable(true);
    textInputExecCode.setEnabled(true);
    textInputExecCode.setLocale(java.util.Locale.getDefault());
    textInputExecCode.setRows(10);
    textInputExecCode.setSelectionEnd(10);
    textInputExecCode.setSelectionStart(0);

    // "dispositivo de entrada": campo para entrada de dados
    textUserInput.setBounds(new Rectangle(xEnt,  yEnt, largEnt, altEnt)); // System.out.println("[Emulador_Panel.montaPainelPrincipal: xEnt="+xEnt+", yEnt="+yEnt+", largEnt="+largEnt+", altEnt="+altEnt);
    textUserInput.setForeground(Configuracao.corFrente2); //
    textUserInput.setBackground(Configuracao.corFundoEntradas); //
    textUserInput.addActionListener(new ActionListener() { // ao teclar ENTER => dispare o "botaoOk.ok_actionPerformed(e)
      public void actionPerformed(ActionEvent ev) {
        emulatorBaseClass.ok_actionPerformed(ev); // a açao do "botaoOK" está no "EmulatorBaseClass.ok_actionPerformed(...)"
        }
      });

    botaoOk.setBounds(new Rectangle(xOk,  yOk,  largOk, altOk)); // botao para sinalizar "dado digitado, processe"
    botaoOk.setBackground(Configuracao.corAzulEscuro1); //
    botaoOk.setFont(Configuracao.fonteBotao); // fonte botão superior
    botaoOk.setForeground(Configuracao.corFrente1); //
    botaoOk.setLabel("OK");

    // 03/06/2012 - Java 4 applet o 'textUserInput' esta entrando muito grande, apesar de registrar largura OK de 65...
    //System.out.println("EMP: textUserInput: "+xEnt+", "+yEnt+", "+largEnt+", "+altEnt);    
    //System.out.println("EMP: botaoOk: "+xOk+", "+yOk+", "+largOk+", "+altOk);

//HH Eliminei para 'buildMainButtons()': botaoRoda, botaoRodaPP

    // Comentário sobre a instrução executada
    labelInstrComm.setBounds(new Rectangle(xSai,  ySai, largSai, altSai)); //(x,y, l,a): (x,y) coord. de posição, (l,a) larg. e alt.
    labelInstrComm.setText("<- " + Bundle.msg("emulAboutCommand")); // "sobre comandos"

    // Label para texto de saída: definido/alterado em EmulatorBaseClass
    labelOutput.setBackground(Color.white);             // texto de saída - dispositivo de saída
    labelOutput.setForeground(Configuracao.corFrente2); //

    // Sobre instrução "executada": caixa explicativa
    labelInstrValue.setBounds(new Rectangle(xInst, yInst, largInst, altInst));
    labelInstrValue.setAlignment(Label.LEFT);                 // "caixa" com instrução em execução, nem formato de montagem
    labelInstrValue.setBackground(Configuracao.corFrente1);   // branco
    labelInstrValue.setForeground(Configuracao.corFrente2);   // "black"
    labelInstrValue.setText("<" + Bundle.msg("emulinstruction") + ">"); // instrução

    // Texto descritivo da instrução executada
    labelInstruction.setBounds(new Rectangle(xIExec, yIExec, largIExec, altIExec));
    labelInstruction.setForeground(Configuracao.corFrente2); // preto
    labelInstruction.setText(Bundle.msg("emulInstruction") + ": "); // "Instrução: "

    labelInputComm.setBounds(new Rectangle(xTEnt, yTEnt, largTEnt, altTEnt));

    labelAcumulator.setText(Bundle.msg("emulAcumulator")); // "Acumulador"  - rótulo fixo, identificando AC
    labelAcumulator.setBounds(new Rectangle(xTAC, yTAC, largTAC, altTAC));
    //                                      X,  Y,  L,  A: posicao (X,Y), largura L e altura A

    labelAcumValue.setText("<" + Bundle.msg("emulValueAC") + ">"); // "valor AC" - é redefinido em "EmulatorBaseClass!EmulatorBaseClass(String,boolean)"
    labelAcumValue.setBounds(new Rectangle(xTtAC, yTtAC, largTtAC, altTtAC)); //
    labelAcumValue.setBackground(Configuracao.corFrente1);   // branco
    labelAcumValue.setForeground(Configuracao.corFrente2);   // "black"

    painelNumLinhasMem.setBounds(new Rectangle(xBL, yBL, largBL, altBL));
    painelNumLinhasMem.setBackground(Configuracao.corFundo3); //
    painelNumLinhasMem.setForeground(Color.white);
    painelNumLinhasMem.setLocale(java.util.Locale.getDefault());
    painelNumLinhasMem.setLayout(gridLayout4);

    painelColLinhasMem.setBounds(new Rectangle(xBC, yBC, largBC, altBC)); // posição, largura e altura da numeração coluna de memória
    painelColLinhasMem.setBackground(Configuracao.corFundo2); //
    painelColLinhasMem.setForeground(Color.white);
    painelColLinhasMem.setLayout(gridLayout3);

    // Definido/alterado em EmulatorBaseClass
    labelOutput.setText("labelSaida");
    labelOutput.setBounds(new Rectangle(xLACC, yLACC, largLACC, altLACC)); // este é o Label que de fato conterá as saídas de comandos "8EE"

    this.setBackground(Configuracao.corAzulClaro);
    this.setFont(Configuracao.fonteDN10); //
    this.setForeground(Configuracao.corFrente2); //

    gridLayout3.setColumns(10);
    gridLayout3.setHgap(2);
    gridLayout3.setVgap(2);

    painelNumLinhasCod.setBounds(new Rectangle(xLnh, yLnh, largLnh, altLnh)); // numeração de linhas ao lado de "janela de código"
    painelNumLinhasCod.setBackground(Configuracao.corAzulClaro); //
    painelNumLinhasCod.setForeground(Color.white);
    painelNumLinhasCod.setLayout(gridLayout2);

    gridLayout2.setColumns(0);
    gridLayout2.setHgap(2);
    gridLayout2.setRows(13);
    gridLayout2.setVgap(0);
    gridLayout4.setHgap(2);
    gridLayout4.setRows(10);
    gridLayout4.setVgap(2);

    painelInferior.add(labelInputComm, null);
    painelInferior.add(labelOutput, null);     //-? não parece estar em uso...
    painelInferior.add(labelInstrValue, null);
    painelInferior.add(labelInstruction, null);
    painelInferior.add(textUserInput, null);
    painelInferior.add(botaoOk, null);
    painelInferior.add(labelInstrComm, null);
    painelInferior.add(labelAcumulator, null);
    painelInferior.add(labelAcumValue, null);

    this.add(painelNumLinhasCod, null);
    this.add(textInputExecCode, null); // janela para digitar o código a ser executado
    this.add(painelNumLinhasMem, null);
    this.add(painelColLinhasMem, null);
    this.add(painelMemoria, null);
    this.add(painelInferior, null);
    setBorderText(painelNumLinhasMem, painelColLinhasMem);
    setLinha(painelNumLinhasCod, 0, Configuracao.numLinhasCodPermitidas); // número de linhas na janela para digitar códigos

    // Era definido em iCG.iniciaCG(): painelEmulador.setBounds(new Rectangle(xPainelCompEmul,yPainelCompEmul,lPainelCompEmul,aPainelCompEmul)); //
    this.setBounds(new Rectangle(iCG.xPainelCompEmul, iCG.yPainelCompEmul, iCG.lPainelCompEmul, iCG.aPainelCompEmul)); //

    this.setLabelFonts();

    } //  void montaPainelPrincipal() throws Exception


  public void msgInstrucaoExecutada (int c0, int c1, String msg) {
    //labelInstruction.setText("Executada [  ]");
    String texto = labelInstruction.getText(); // Fixa, do tipo "Executada [  ]"

    if (botoes==null) { // in case it came here from 'icg.iCG.acaoEnviar(): emulatorBaseClass.execInst(false)'
       // this means that the Emulator layout could not be changed
       return;
       }

    texto = texto.substring(0,11) + c0 + c1 + "]";
    labelInstruction.setText(texto);
    labelInstrValue.setText(msg);
    }


  // Chamado em: icg.ig.Botao -> agora vai direto no EmulatorBaseClass, no lugar do antigo 'emuladorApplet.emular_actionPerformed(e)'
  private void emular_actionPerformed (ActionEvent e) {
    }

  // Coloca na memória o texto em "TextArea textInputExecCode": chamado em icg.ig.Botao
  public void acaoAtualiza () {
    // Coloca na memória o texto em "TextArea textInputExecCode"
    String texto = textInputExecCode.getText();
    emulatorBaseClass.atualiza(texto); // atualiza posições de memória do emuladorApplet
    // ATTENTION: do not use 'emulatorBaseClass.atualizaMemoria(texto);', the first is more complete, cleaning importante variables
    }
  //E private void atualiza_actionPerformed (ActionEvent e) {
  //E  String texto = textInputExecCode.getText();
  //E  // No " EmulatorBaseClass.atualizaMemoria(String)", faze-se um "epi.setXY(0,0)" - passa para a primeira posição de memória
  //E  emuladorApplet.atualiza(texto); // atualiza posições de memória do emuladorApplet
  //E  }


  // Coloca contador de linhas na lateral esquerda da janela do código fonte (em "TextArea textInputExecCode")
  public void setLinha (Panel linha, int inicio, int fim) {
    int count;
    Label [] label_Linha = new Label [fim];
    String str;
    for (count = inicio; count < fim; count++) {
        str = (new Integer(count + 1)).toString();
        if (str.length()<2) str = " "+str+"___";
        else  str += "___";
        label_Linha[count] = new Label();
        label_Linha[count].setForeground(Color.white);
        label_Linha[count].setBackground(Configuracao.corFundo2); // 
        label_Linha[count].setText(str);
        label_Linha[count].setFont(Configuracao.ftBold10); // fonte 
        linha.add(label_Linha[count], null);
        }
    }


  // Define bordos da memória do emuladorApplet
  // Coloca contador de linha na lateral esquerda do Emulador
  public void setBorderText (Panel lado, Panel cima) {
    int count;
    Label[] label_Cima = new Label[10];
    Label[] label_Lado = new Label[10];
    for (count = 0; count < 10; count++) {
      label_Lado[count] = new Label();
      label_Lado[count].setBackground(Configuracao.corFundo2); // margens laterais com números das colunas
      label_Lado[count].setFont(Configuracao.fonteDN11); // this.setFont(new java.awt.Font("Dialog", 0, 10));
      label_Lado[count].setText( (new Integer(count)).toString());
      lado.add(label_Lado[count], null);

      label_Cima[count] = new Label();
      label_Cima[count].setBackground(Configuracao.corFundo2); // margens laterais com números das linhas
      label_Cima[count].setFont(Configuracao.fonteDN11);
      label_Cima[count].setText("      " + (new Integer(count)).toString());
      cima.add(label_Cima[count], null);
      }
    } // public void setBorderText(Panel lado, Panel cima)

  //---- DB
  public void pinta () {
    Graphics gr = null;
    if (offscreen != null) { // primeiro "paint" entra antes de construir primeira "offscreen"
       gr = offscreen.getGraphics();
       if (gr!=null) //H
          offgraphics = gr;
       paint(gr);
       }
    } //  void pinta()

  //---- DB
  private void copy2DoubleBuffer (Graphics gr) { // copy to 'offScreen' and draw the image
    // copia tudo na tela
    if (gr==null) {
       return; // selecionar botao primario => deselecionar outro botao primario => faz 'repaint' dele, que esta vazio => cai aqui
       }
    else
       gr.drawImage(offscreen,0,0,this);
    } // void copy2DoubleBuffer(Graphics gr)


  // Método para desenhar bordas para gerar barra superior de botões 
  // Emulador_Panel e CompilerPanel
  public void paint (Graphics gr) {
    Dimension tamanho;
    int       l=1; // largura das linhas de botões
    try {
      tamanho = this.getSize(); // size()
      int posX = iCG.xEmul, posY = iCG.yEmul; // posição do botaoComp
      //T Dimension dim = textUserInput.getSize();
      //T System.out.println("EMP: textUserInput: "+dim.width+", "+dim.height);

      //---- DB
      if (offscreen == null) {
         // double buffering techniche
         offscreen = createImage(tamanho.width, tamanho.height);
         }
      offgraphics = offscreen.getGraphics(); //H
      gr = offscreen.getGraphics(); // pega último buffer "gráfico"
      //---- DB

      // Desenha um retângulo dentro do qual estão os botões (este ficará sob os botões)
      gr.setColor(Configuracao.corFundoBarraBt); // 
      gr.fillRect(posX-2, posY-2, tamanho.width-24, Configuracao.ALTURA_BARRAS+2); //
      gr.setColor(Configuracao.corLinhaBarraBt); //Color.white);
      gr.drawRect(posX-2, posY-2, tamanho.width-24, Configuracao.ALTURA_BARRAS+2); //
      gr.setColor(Color.black);
      gr.drawRect(1, 1, tamanho.width-2, tamanho.height-3); //

      // painelInferior: Rectangle(iCG.xPainelInf,iCG.yPainelInf,iCG.lPainelInf,iCG.aPainelInf)
      gr.setColor(Color.black);
      gr.drawRect(iCG.xPainelInf-1,iCG.yPainelInf-1,iCG.lPainelInf+2,iCG.aPainelInf+2);

      // desenha borda em barra de mensagem: iCG.labelMensagem
      gr.drawRect(iCG.leX-1, iCG.leY-1, iCG.leL+2, iCG.leA+2);

      /*
      Este não adianta, pois após este 'paint' é pintada a corde fundo do painelInferior que sobrepõem-se ao 'paint'...
      // Desenha borda em cada um de:
      // labelInstruction: (xIExec, yIExec, largIExec, altIExec) <- texto "Executada [ ]" -> "Instrução "
      // labelInstrValue: (xInst, yInst, largInst, altInst)
      // labelOutput: (xLACC, yLACC, largLACC, altLACC) // este é o Label que conterá as saídas de comandos "8EE"
      // labelInstrComm: (xSai,  ySai, largSai, altSai) //(x,y, l,a): (x,y) coord. de posição, (l,a) larg. e alt.
      // labelAcumValue: (xTtAC, yTtAC, largTtAC, altTtAC)
      gr.drawRect(xIExec-1, yIExec-1, largIExec+2, altIExec+2);
      gr.drawRect(xLACC -1, yLACC -1, largLACC +2, altLACC +2);
      gr.drawRect(xLACC -1, yLACC -1, largLACC +2, altLACC +2);
      gr.drawRect(xSai  -1,  ySai -1, largSai  +2, altSai  +2);
      gr.drawRect(xTtAC -1, yTtAC -1, largTtAC +2, altTtAC +2);
      */

    } catch (Exception ex) {
      System.err.println("[EmulatorMainPanel.paint()] Error: " + ex.toString());
      }

    //---- DB
    copy2DoubleBuffer(this.getGraphics()); // copy to 'offScreen' and draw the image

    } // paint()


  //T // Para testes...
  //T private EmulatorMainPanel () {
  //T   try {
  //T     botoes = new Botao[6];
  //T     montaPainelPrincipal();
  //T     }
  //T   catch (Exception ex) {
  //T     System.err.println("Erro: ao tentar montar painel principal do emulador: "+ex);
  //T     ex.printStackTrace();
  //T     }
  //T   }

  //T public static void main (String[] args) {
  //T    EmulatorMainPanel emp = new EmulatorMainPanel();
  //T    Frame frame;                                                                                                              
  //T    frame = new Frame();
  //T    frame.addWindowListener(new WindowAdapter() {
  //T    public void windowClosing(WindowEvent evt) { System.exit(0); }
  //T       });
  //T    frame.setSize(Configuracao.WIDTH, Configuracao.HEIGTH);
  //T    frame.setVisible(true);
  //T    frame.add(emp);
  //T    }
   

  }
