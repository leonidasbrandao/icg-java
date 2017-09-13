/**
 * 
 * iMath - http://www.matematica.br
 * LInE - Laboratory of Informatics in Education
 * 
 * <p>Title: iCG - Interactive Computer (Computador Gaveteiro Interativo)</p>
 * 
 * <p>Description: this a didatic software to explain to the newbie what is a Computer, how to program it</p>
 * 
 * <p>Copyleft: 2003</p>
 * @author Leônidas de Oliveira Brandão
 * @version icg.configucoes.Configuracao.Versao
 * 
 */

/*
 Compilador:    icg/compilador/CompilerBaseClass.java
 Emular:        icg/emulador/EmulatorMainPanel.java
 */

/*
 *   Exercícios:
 *
 *   paramGabarito    : entrada do gabarito para mostrar no HTML   | [16/08/2005]
 *   paramEnunGabarito: enunciado do exercício (só é lido se 'paramGabarito<>""'
 *   paramAluno       : uma resposta do aluno para mostrar no HTML | ainda a implementar (neste caso 'pegue 'paramGabarito' p/ apresentar')
 *   paramInfo        : parâmetro auxiliar, pode usá-lo para passar a URL (ou outros dados ao applet)
 * 
 *   envWebInfo       : dados auxiliares, recebidos via 'paramInfo' (devolve valor de 'paramInfo')
 * 
 *   envWebValor      : resultado da avaliação, 
 *   envWebArquivo    : arquivo com resposta
 *   envWebGeoResp    : contra-exemplo GEO
 *   envWebGeoOuvidor : dados sobre operação iGeom "seleção/ação"
 * 
 *   se paramGabarito<>"", então não entre botão de "Gabarito" nem de "CompilerPanel"
 */

package icg;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import java.net.URL;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;

import icg.ig.*;
import icg.emulador.*;
import icg.compilador.CompilerPanel;
import icg.msg.Bundle;
import icg.io.Arquivos;
import icg.util.Criptografia;
import icg.util.EnviaWeb;
import icg.util.ListaLigada;
import icg.configuracoes.Configuracao;


public class iCG extends Applet {

  public static iCG appletICG; // usado em "icg.util.EnviaWeb"

  private EmulatorBaseClass emulatorBaseClass; // usado no método q/ compara solução aluno c/ gabarito
  public EmulatorBaseClass getEmulatorBaseClass () { return emulatorBaseClass; } // icg.Botao
  public void actionUpdate () {
    emulatorBaseClass.getEmulatorMainPanel().acaoAtualiza(); // icg.ig.Botao.mouseClicked(...): update memory, load program
    }

  //---- DB
  // AWT: técnica de DOUBLE BUFFERING evita "flicker" e aqui reduz falhas em mostrar os botoes
  Image offscreen  = null;
  Graphics offgraphics = null;

  Botao [] botoes = null; // tera imagem em 'EmulatorMainPanel.botoes'

  // ------======------======------======------======------======------======------======

  public  static final int x0 = 10, // Usado em icg/emulador/Emulador_Panel e icg/compilador/CompilerPanel
                           y0 = 10;
  public  static final int dbx = 80, dby = 26; // dimensões dos botões

  public static int // posições, larguras e alturas de painéis, botões, rótulos ...

  //- Posicoes iniciais dos principais paineis e suas respectivas altura e largura
          xPainelCompEmul = 0, yPainelCompEmul = 0,        lPainelCompEmul = 600, aPainelCompEmul = 389, //(0, 0, 600, 441)

  //- Posição do label com "http://www.matematica.br/programas/icg" ou ("http://www.matematica.br/icg" ?)
          // define configuracoes.Configuracao.leX,leY,leL e leA
          // não + usado p/ 'labelEndereco', agora para 'labelEndereco': barra de mensagem
          leX = x0+5, leY = aPainelCompEmul-25, leL = lPainelCompEmul-25, leA = 18, // labelMensagem

  //- Largura/altura dos botões
          largBt  = 25, altBt  = 25, // usado em icg.ig.TrataImage
          lBt  = 25, aBt  = 25, // usado em icg.ig.TrataImage
          lBtD = 30, aBtD = 10, // computa distância entre botões, altura do início do bordo

  // segunda fila de botões (Emulador: botaoEmular, botaoEmularPP, botaoAtualiza; CompilerPanel: botaoCompila)
          xSFB = x0+4*lBtD,

  // iCG ---
          xEmul     = x0+       5,          yEmul    = y0+ aBtD,   // botão: Emulador       (::botaoEmulador)
          xComp     = x0+5+  lBtD,          yComp    = y0+ aBtD,   // botão: CompilerPanel  (::botaoCompilador)
          xSobre    = lPainelCompEmul-2*lBtD-8, ySobre   = y0+ aBtD,   // botão: Sobre          (::botaoSobre)
          xAjudaE   = lPainelCompEmul-  lBtD-8, yAjudaE  = y0+ aBtD,   // botão: Ajuda          (::botaoAjuda)

  // Compilador ---
          xPainelFC = x0-  7, yPainelFC = y0 + 40, lPainelFC= 595,    aPainelFC = 285, // painel azul completo
          xCodFont  = x0-  7, yCodFont  = y0 -  3, lCodFont = 500,    aCodFont  = 215, // area do cod. fonte
          xCodObj   = x0+492, yCodObj   = y0 -  3, lCodObj  =  93,    aCodObj   = 215, // area do cod. obj. (direita)
          xMsgComp  = x0-  7, yMsgComp  = y0 +210, lMsgComp = 590,    aMsgComp  =  65, // area de msgs (inferior)

          xCompila  = xSFB,   yCompila = y0+ aBtD,                  // botão: CompilerBaseClass (::botaoCompila)

  // Emulador ---
          xGab      = xSFB,            yGab     = y0+ aBtD,         // botão: Enviar        (::botaoEnvia)
          xEnv      = xSFB+lBtD,       yEnv     = y0+ aBtD,         // botão: Gabarito      (::botaoGabarito)
          xEm       = xSFB+2*lBtD,     yEm      = y0+ aBtD,         // botão: Rodar         (::botaoRoda)
          xEmPasso  = xSFB+3*lBtD,     yEmPasso = y0+ aBtD,         // botão: Passo a Passo (::botaoRodaPP)
          xAtual    = xSFB+4*lBtD,     yAtual   = y0+ aBtD,         // botão: Atualiza      (::botaoAtualiza)


          // para o Emulador_Panel

          //- Emulador_Panel.painelMemoria
          xPainelMem = x0+29, yPainelMem = y0+ 55, lPainelMem = 462, aPainelMem = 182,

          //- Emulador_Panel.painelInferior
          xPainelInf = x0+ 9, yPainelInf = y0+238, lPainelInf = 570, aPainelInf =  81,

          //-
          xExec     = x0+513, yExec    = y0+ 48,  // textUserInput: campo para digitar código executável - textInputExecCode
          xOk       = x0+105, yOk      = y0+ 35,  // botaoOk

          xEnt      = x0+  1, yEnt     = y0+ 35,  // textInput (caixa de entrada)

          xIExec    = x0+  1, yIExec   = y0+ 13,  // labelInstruction
          xInst     = x0+107, yInst    = y0+  8 , // msg de instrução
          xSai      = x0+290, ySai     = y0+  8,  // labelInstrComm - alinhado com 'labelAcumulator'

          xLACC     = x0+467, yLACC    = y0+  9,  // labelOutput        - alinhado com 'labelAcumValue'

          xTEnt     = x0+212, yTEnt    = y0+ 10,  // labelInputComm     -
          xTAC      = x0+290, yTAC     = y0+ 41,  // labelAcumulator    - 
          xTtAC     = x0+467, yTtAC    = y0+ 41,  // labelAcumValue     - alinhado com 'labelOutput'

          xBL       = x0+8,   yBL      = y0+ 55,  // borda_lado          -
          xBC       = x0+ 29, yBC      = y0+ 40,  // borda_cima          -
          xLnh      = x0+490, yLnh     = y0+ 49,  // Linha               -
                                                  //       num. de linhas ao lado de "janela de código"

          xEnd      = 140,    yEnd     =  2,      // label com endereço do iCG

  // Emulador_Panel
  // labelInstruction.setBounds(new Rectangle(xIExec, yIExec, largIExec, altIExec)); <- texto "Executada [ ]" -> "Instrução "
  // labelInstrValue.setBounds(new Rectangle(xInst, yInst, largInst, altInst));
  // labelOutput.setBounds(new Rectangle(xLACC, yLACC, largLACC, altLACC)); // este é o Label que conterá as saídas de comandos "8EE"
  // labelInstrComm.setBounds(new Rectangle(xSai,  ySai, largSai, altSai)); //(x,y, l,a): (x,y) coord. de posição, (l,a) larg. e alt.
  // labelAcumValue.setBounds(new Rectangle(xTtAC, yTtAC, largTtAC, altTtAC)); //


  // Edita fonte de:
  // botões de edição de fontes: Emulador -> {memória,código}; Compilador -> {códigos, msg}
  // também usado em "Emulador_Panel" para posicionar o "botaoAjuda"
          largLabel = 140, //
          xEdFt     = x0+ 5,       yEdFt    = aPainelCompEmul - 3*leA - 4, // <- Emulador   : leA -> labelMensagem
          largEdFt  = 55,          altEdFt  = 23, // altBotao - 5
          xEdFtC    = x0+ 5,       yEdFtC   = aPainelCompEmul - 2*leA - 15, // <- Compialdor : leA -> labelMensagem - ver iCG.yPainelFC => CompilerPanel.painelCodigos
          largEdFtC = 55,          altEdFtC = 23, //

          //- associado com o painelInferior
          xGabEnun  = xPainelInf-5, yGabEnun = aPainelCompEmul - 2*leA -5,
          lGabEnun  = lPainelInf-5, aGabEnun = 25;       // para enunciado de exercício

  public static int // larguras e alturas de botões
          altBotao    =  28,
          largEnd     = 380,  altEnd  =  15,      // rótulo com end. do iCG
          largComp    =  93,  altComp = altBotao, //dbx, dby)); //113, 26));
          largEmul    =  75,  altEmul = altBotao, //

          // para o Emulador_Panel ->
          largExec    =  65,  altExec = 185, // textInputExecCode: campo para usuario digitar código executável
          largTEnt    =   0,  altTEnt =   0, // labelInputComm
          largTAC     = 106,  altTAC  =  17, // labelAcumulator - alinhado com 'labelInstrComm', abaixo dele
          largTtAC    =  76,  altTtAC =  17, // labelAcumulator
          largBL      =  22,  altBL   = 179, // painelNumLinhasMem painel (era: borda_lado)
          largBC      = 459,  altBC   =  15, // painelColLinhasMem (era: borda_cima)
          largLACC    =  76,  altLACC =  17, // lacc
          largLnh     =  22,  altLnh  = 184, // painelNumLinhasCod (era: Linha)

          largEnv     =  58,  altEnv     = altBotao,  //  81, 28
          largGab     =  78,  altGab     = altBotao,  //  81, 28

          largEm      =  61,  altEm      = altBotao,  //  81, 28
          largEmPasso = 109,  altEmPasso = altBotao,  // 109, 28

          largEnt     =  75,  altEnt     = altBotao,  // textInput: campo para usuario digitar dados de entrada
          largSai     = 139,  altSai     = altBotao,  // labelInstrComm <comentarios comandos ou saida> - alinhado com 'labelAcumulator'
          largInst    = 159,  altInst    = altBotao,  // x0+109, y0+  8, 159, 23 labelInstrValue
          largIExec   =  96,  altIExec   = 15, // labelInstruction

          largAtual   =  71,  altAtual   = altBotao,  //  81, 28

          largOk      =  50,  altOk      = altBotao;  //  50, 28

  private static boolean ehApplet       = false;
  public static boolean ehApplet() { return ehApplet; } // 30/03/2006: icg/ig/Botao

  private boolean ehEmulador   = true; // ehEmulador==true <=> applet está com o Emulador (e portanto não Compilador)
  public boolean ehEmulador () { return  ehEmulador; }
  public void ehEmulador (boolean bol) { ehEmulador = bol; }


  //
  // iCG content (for extension like in "tabs": use here a vector of Properties)
  private Properties iCGproperties = null; // EXTENSION: use a vector here (e.g., if use tabs, each tab with one Emulator/Compiler)
  public  Properties getICGproperties () { return iCGproperties; }
  private static final String
    tagStatement = "Statement", tagType = "Type", tagHint = "Hint",                  // provided by the teacher (exercise)
    tagTemplate = "Template", tagEvaluation = "Evaluation", tagComment = "Comment",  //
    tagObject = "Object", tagCode = "Code"; // provided by the student (answer)

  // From: icg.emulador.EmulatorBaseClass.abrir_actionPerformed(ActionEvent)
  // Return: -1 => not in iCG format; 0 => it is OK
  public int setProperties (String strAllProperties) {
    iCGproperties = icg.io.Arquivos.getProperties(strAllProperties); // get all iCG itens: Statement, Type, Hint, Template, ...
    if (iCGproperties==null) {
       iCGproperties = new Properties();
       String strErr = Bundle.msg("errFileNotiCG"); // Error: this file seems not be from iCG...
       System.err.println("[iCG.setProperties] " + strErr + "\n" + strAllProperties);
       labelMensagem.setText(strErr);
       return -1;
       }
    return 0;
    }

  // Set fields/areas in Emulator/Compiler with contents loaded from a file
  // From: icg.emulador.EmulatorBaseClass.abrir_actionPerformed(ActionEvent)
  public void setAllContents () {
    if (iCGproperties==null) {
       System.err.println("[iCG.setAllContents] This session is empty!");
       return;
       }

    String
      //TBI strStatement = iCGproperties.getProperty(tagStatement),
      //TBI strType = iCGproperties.getProperty(tagType),
      //TBI strHint = iCGproperties.getProperty(tagHint),
      strTemplate = iCGproperties.getProperty(tagTemplate),
      //TBI strEvaluation = iCGproperties.getProperty(tagEvaluation),
      //TBI strComment = iCGproperties.getProperty(tagComment),
      strObject = iCGproperties.getProperty(tagObject),
      strCode = iCGproperties.getProperty(tagCode);

    if (strTemplate!=null && !strTemplate.equals("null")) // <Template>...</Template>: template answer for automatic evaluation
       str_paramGabarito = strTemplate; //

    if (strObject!=null && !strObject.equals("null")) { // <Object>...</Object>: the student "object code"
       emulatorBaseClass.getEmulatorMainPanel().setTextInputExecCode(strObject); // "object code" from student (to Emulator)
       // makes 'EmulatorBaseClass.getEmulatorMainPanel().setTextInputExecCode(strObject)'
       }

    if (strCode!=null && !strCode.equals("null")) // <Code>...</Code>:  the student "high level code"
       painelCompilador.setSourceCode(strCode); // set "hith level code" in Compiler
    } // public void setAllContents()

  private boolean // define the presentation of some button
    allowLoadCompiler = true,       // botaoCompilador -  if true => allow the button to load iCG Compiler
    loadButtonBuildExerc = true,    // botaoGabarito 
    loadButtonExercEvaluate = true; // botaoEnviar

  //|-------------------------------
  //| To exercise template
  public static boolean ehExercicio    = false;
  private static String strGabEntradasSaidas = "";
  public static void   strGabEntradasSaidas (String str) {  strGabEntradasSaidas = str; }
  public static String strGabEntradasSaidas () { return strGabEntradasSaidas; }

  // Variable to store template criptographed (original form '{ E: 9 8  }, { S: 17  }')
  private static String strCriptTemplate = null;

  // Indicate that the user is building a new exercise template: take note of <inputs x outputs> (iCGEmulator)
  public static boolean ehGeraGabarito = false;

  // public static String strGabarito = ""; // anota entradas e saídas para montar gabarito (definido no "iCGEmulator"?????)
  //|-------------------------------

  public static boolean versionXML = false; // version >= 2.2.0: all data in a single file

  public static String

         str_icg_content,         // the whole string with iCG content
         str_answer_object,       // the student answer with object code (to the emulator) - str_paramPrograma
         str_answer_code,         // the student answer with code in high level language (to the compiler)
         str_answer_session = "", // register in the session any evaluation attempt

         str_param_info,     // parâmetro: 'string' com info. genéricas, pe, p/ guardar URL
         str_paramPrograma,  // parâmetro em applet, poderá conter o programa (código de máquina)
         str_paramProfessor, // parâm. de applet, se igual a "Gabarito", então carregará botão "Gabarito" (p/ construir exerc.)
         str_paramEmulador = null,  // parâmetro em applet para indicar quem será iniciado ("botaoEmulador" => só botaoEmulador, c.c., ambos)
         str_param_end_post = "", //= "http://milanesa.ime.usp.br/mac118",

         str_paramAluno        = "",    // se igual a 'respostaAluno', então mostrar a solução enviada pelo aluno , em 'paramGabarito',
                                        //                             e não carregar botão de envio

         str_paramGabarito     = "", // parâmetro em applet, poderá conter gabarito do professor (deve ficar criptografado)
         str_paramEnunGabarito = "", // parâmetro em applet, se tiver gabarito é exercício, aqui vai o enunciado
         str_paramFeedback     = "", // Parameter 'iLM_PARAM_Feedback': show the evaluation result to the student? (used here in 'acaoEnviar()')

         // Formato: { E: nnnnnn nnnnnn ... nnnnnn }, { S: nnnnnn nnnnnn ... nnnnnn }
         //           +--------- entradas ---------+   +--------- saídas ----------+

         // não é mais este
         // Formato: { xxx xxx xxx ... xxx }, { nnnnnn, nnnnnn, ... nnnnnn }, { nnnnnn, nnnnnn, ... nnnnnn }
         //           +----- programa -----+  +--------- entradas ---------+  +---------- saídas ----------+

         str_param_lingua,
         str_param_pais;


  public static URL codebase; // = new URL("http://milanesa.ime.usp.br/mac118"); // para applet

  //_ static
  private 
  Image imgComp ,
        imgEmul ,
        imgEnv ,
        imgGab ,
        imgEmula ,
        imgEmulaPP ,
        imgAtualiza ,
        imgInfo,
        imgAjuda,
        imgSobre,
        imgCompila;
  private 
  Botao botaoEmulador      ,
        botaoCompilador    ,
        botaoEnviar        ,
        botaoGabarito      ,
        botaoRoda          ,
        botaoRodaPP        ,
        botaoAtualiza      ,
        botaoInfo          ,
        botaoAjuda         ,
        botaoSobre         ,
        botaoCompila       ;

  // para não dar erro de sujeira na imagem, carrega uma só vez, como 'static {...}'
  private void startIconButtons () {  // png não funciona no Java 1
    imgComp     = TrataImage.trataImagem(true,"compilador.gif"); // botao para pegar Compilador
    imgEmul     = TrataImage.trataImagem(true,"emulador.gif");   // botao para pegar interface Emulador
    imgEnv      = TrataImage.trataImagem(true,"exerc-enviar.gif");
    imgGab      = TrataImage.trataImagem(true,"exerc-criar.gif");
    imgEmula    = TrataImage.trataImagem(true,"emula.gif");
    imgEmulaPP  = TrataImage.trataImagem(true,"emulapp.gif");
    imgAtualiza = TrataImage.trataImagem(true,"atualiza.gif");
    imgInfo     = TrataImage.trataImagem(true,"info.gif");
    imgAjuda    = TrataImage.trataImagem(true,"ajuda.gif");
    imgSobre    = TrataImage.trataImagem(true,"sobre.gif");
    imgCompila  = TrataImage.trataImagem(true,"compila.gif"); // 
    }

  // This is the main panel - it contains all other graphical components for iCG Emulator
  private EmulatorMainPanel emulatorMainPanel;

  private Frame frameEdicaoFonte;


  //-  Panel 
  CompilerPanel painelCompilador;
  public CompilerPanel painelCompilador () { return painelCompilador; } // icg.Botao

  public static final String STR_ICG = "iCG - http://www.matematica.br/icg"; // "["+Configuracao.versao+"] "

  static Label labelEdicaoFonte0 = new Label(); // "Edita fonte de" para Emulador
  static Label labelEdicaoFonte2 = new Label(); // "Edita fonte de" para Compilador

  Label labelEnunGabarito = new Label(); // Bundle.msg("labelExerc") + ": ");        // "Exercício" - se tiver Gabarito, então é exercício (este terá enunciado)

  static final Label labelEnderecoTopoE = new Label(STR_ICG); //"iCG - http://www.matematica.br/programas/");
  static final Label labelEnderecoTopoC = new Label(STR_ICG);

  Button botaoFonteMemEmul  = new Button(Bundle.msg("buttonMemory")); // "Memória"  - para Emulador
  Button botaoFonteCodEmul  = new Button(Bundle.msg("buttonCode"));   // "Código"   - para Emulador
  Button botaoFonteComp     = new Button(Bundle.msg("buttonCodes"));  // "Códigos"  - para Compilador

  Label labelMensagem      = new Label("<" + Bundle.msg("barraMsg") + ">"); // "<barra de mensagens para o usuário>"
  public void setMensagem (String str) { labelMensagem.setText(str); } // icg.ig.Botao: ao passar mouse sobre

  // Internationalization
  public void changeI18Texts () {
    labelEdicaoFonte0.setText(Bundle.msg("labelEdit") + ": ");
    labelEdicaoFonte2.setText(Bundle.msg("labelEdit") + ": ");
    labelEnunGabarito.setText(Bundle.msg("labelExerc"));
    botaoFonteMemEmul.setLabel(Bundle.msg("buttonMemory"));
    botaoFonteCodEmul.setLabel(Bundle.msg("buttonCode"));
    botaoFonteComp.setLabel(Bundle.msg("buttonCodes"));
    labelMensagem.setText("<" + Bundle.msg("barraMsg") + ">");
    // Bundle.msg(""));
    }


  // Pegue um valor de parâmetro
  public String getParameter (String key, String def) {
    try {
     return !ehApplet ? System.getProperty(key, def) : (getParameter(key) != null ? getParameter(key) : def);
    } catch (Exception e) { System.err.println("Erro: ao tentar ler parâmetros: "+key+", "+def+": "+e); }
    return "";
    }

  // Get Applet information - information about iCG applet - override 'java/applet/Applet.getAppletInfo()':
  // From: icg/io/Arquivos.java - getHeader(java.applet.Applet applet)
  public String getAppletInfo () {
    //return Bundle.msg("appletInformation"); // "Applet Information"
    String strAux = "iCG: free software by LInE - iMath : http://www.matematica.br : ";
    try {
      return !ehApplet ? strAux + "<" +
        System.getProperty("user.name") + "; " + 
        System.getProperty("user.language") + ">; <" +
        System.getProperty("os.name") + "; " + 
        System.getProperty("os.version") + "; " + 
        System.getProperty("os.arch") + ">; <" + 
        System.getProperty("user.dir") + "; " + 
        System.getProperty("user.home") + ">" : strAux + "<Class=" +
        this.getClass().getName() + ">; <Langauge=" +
        this.getLocale() + ">";
    } catch (Exception e) {
      System.err.println("Error: getAppletInfo(): "+e.toString());
      }
    return strAux + "<>";
    }

  // ------======------======------======------======------======------======------======

  public static boolean debugTrace = true; // use 'true' in iCG testing version...

  // Disparado ao clicar no botão "Enviar" em "iniciaCG()!actionPerformed(ActionEvent)" ("EnviaWeb")
  public static boolean testandoGabarito = true; // usado em "iCGEmulator" para não mostrar saídas, nem pedir dados
                                                 // só para comparar resposta do aluno com gabarito do professor

  // Zeradas ao clicar no botão "Enviar"
  private static int contAcertosEnt = 0, // qde de acertos entre programa do usuário e gabarito, em rel. às entradas
                     contAcertosSai = 0, // qde de acertos entre programa do usuário e gabarito, em rel. às saídas
                     numSaidasAluno = 0; // total de saídas na resposta do aluno
  private static int iEnt = 0, // índice para vetEntradas
                     iSai = 0; // índice para vetSaidas
  public static int  contAcertosEnt () { return contAcertosEnt; }
  public static int  iEnt () { return iEnt; }
  public static int  contAcertosSai () { return contAcertosSai; }
  public static int  iSai () { return iSai; }
  public static void contAcertosEntInc () { contAcertosEnt++; }
  public static void iEntInc () { iEnt++; }
  public static void contAcertosSaiInc () { contAcertosSai++; }
  public static void iSaiInc () { iSai++; }
  public static void numSaidasAlunoInc () { numSaidasAluno++; }
  public static int  numSaidasAluno    () { return numSaidasAluno; }

  public static Vector // para exercícios com gabaritos
         [] vetEntradas; // = null,  // para usar nas comparações com instruções 7EE e 8EE
  public static Vector // para exercícios com gabaritos
         [] vetSaidas  ; // = null,
  public static Vector // para exercícios com gabaritos
         strVetEntradas, // para strings de entradas {"E: N N N ", "E: N N "}
         strVetSaidas;   // para strings de saidas   {"S: N N ", "S: N "}
  public static int contaVetEntradas = 0, contaVetSaidas = 0;

  //  public
  private static int atualVetEntSai; // é um contador utilizado no "iCGEmulator" (isso é importante)
  public static int atualVetEntSai () { return  atualVetEntSai; }

  private static int numEntradas = 0, numSaidas = 0;
  private static ListaLigada listaGabEntradas = null, // lista para seq. de entradas em exercicios
                             listaGabSaidas   = null;   // lista para seq. de saidas em exercicios
  public static  ListaLigada listaGabEntradas () { return listaGabEntradas; }
  public static  void        listaGabEntradas (ListaLigada lst) { listaGabEntradas = lst; } // iCGEmulator
  public static  void        addListaGabEntradas (String str) { 
    if (listaGabEntradas!=null) {
       listaGabEntradas.add(str+" ");
       //System.out.println("[iCG!addListaGabEntradas] inserido "+str+" em listaGabEntradas");
       }
    else System.err.println("[iCG!addListaGabEntradas] erro, listaGabEntradas="+listaGabEntradas);
    }
  public static  ListaLigada listaGabSaidas   () { return listaGabSaidas; }
  public static  void        listaGabSaidas   (ListaLigada lst) { listaGabSaidas   = lst; } // iCGEmulator
  public static  void        addListaGabSaidas (String str) { 
    if (listaGabSaidas!=null) { 
       listaGabSaidas.add(str+" ");
       //System.out.println("[iCG!addListaGabSaidas] inserido "+str+" em listaGabSaidas");
       }
    else System.err.println("[iCG!addListaGabSaidas] erro, listaGabSaidas="+listaGabSaidas);
    }

  public static String listaVector (Vector vet) {
    String strAux = "<";
    for (int i=0; i<vet.size(); i++) {
       strAux += " "+vet.elementAt(i);
       }
    strAux += ">";
    // System.out.println(strAux); // [iCG.listaVector]
    return strAux;
    } // 


  private double valorExercicio = -111; // to get the exercise value of the student - -111 => nothing done
  private boolean codeEdited = false; // indicate if the student did some edition in the code (in 'emulatorBaseClass.getCodigo()')

  // icg.emulador.EmulatorBaseClass.atualiza(EmulatorBaseClass.java:770)
  public void setCodeEdited () {
     // System.out.println("[IA.setCodeEdited] codeEdited="+codeEdited);
     // try { String str00="";System.out.println(str00.charAt(3)); } catch (Exception e) { e.printStackTrace(); }
     this.codeEdited = true;
     this.valorExercicio = -111;
     } // defined in 'icg/emulador/EmulatorBaseClass.java: setCodigo(String s)

  //T static int conta=0;

  // Build content to be send or registered (with the student session)
  public String getSession () {
    System.out.println("[IA.getSession] initial valorExercicio="+this.valorExercicio);
    //T conta++; if (conta>3) {
    //T try { String str00="";System.out.println(str00.charAt(3)); } catch (Exception e) { e.printStackTrace(); }
    //T return ""; }     

    // tagType, tagStatement, tagTemplate, tagHint, tagObject, tagCode, tagComment, tagEvaluation
    // Update iCG properties for file
    String strAnswerEvals = str_answer_session; // preserve any previous evaluation in this exercise
    String strSourceCode = painelCompilador.getSourceCode(); // source code from Compiler
    String strObjectCode = emulatorBaseClass.getCodigo(); // object code from Emulator

    if (iCGproperties==null) // old versions of 'icg' file hasn't any tag => empty
       iCGproperties = new Properties();

    iCGproperties.put(tagEvaluation, strAnswerEvals); // "<Evaluation> ... </Evaluation>"
    iCGproperties.put(tagCode, strSourceCode); // "<Code> ... </Code>"
    iCGproperties.put(tagObject, strObjectCode); // "<Object> ... </Object>"
    //T iCGproperties.list(System.out);
    //TBI iCGproperties.put(tagComment, strComment); // "<Comment> ... </Comment>" - to be implemented "interface to student send comment"
    String strICGproperties = Arquivos.getXML(this, iCGproperties);
    //
System.out.println("[iCG.getSession]\n"+strICGproperties); //
    return strICGproperties;
    } // String getSession()

  // iLM: Pega valor de exercicio
  // Return: -1 => empty solution; 0 => wrong in all test; 0.5 => failed in at least one test; 1 => passed in all tests
  public double getEvaluation () {
    // 
System.out.println("[IA.getEvaluation] valorExercicio="+this.valorExercicio+" codeEdited="+codeEdited);

    // Only evaluate if this new "object code" in the emulator was loaded to the memory without any previous evaluation
    // The 'getAnswer()' could provide this evaluation
    if (this.valorExercicio==-111) { // it is -111 => there was edition in the object code (at least the user 'reload' his programm)
       if (!codeEdited) {
          String strErr = "1: "+Bundle.msg("exercEmptyAnswer");
          // new JanelaDialogo(strErr, null); // if student do not edited the programm => did nothing!
          labelMensagem.setText(strErr); //
          return -111;
          }
       acaoEnviar(); // avalia resposta-aluno, esta define valor da resposta em 'this.valorExercicio'
       }     
    return this.valorExercicio;
    }

  // iLM: Pega conteudo de exercicio
  // return: "-1" => student does nothing; otherwise return the all iCG file
  public String getAnswer () {
    // String str = emulatorBaseClass.getCodigo();

    // Only evaluate if this new "object code" in the emulator was loaded to the memory without any previous evaluation
    // The 'getEvaluation()' could provide this evaluation
    if (this.valorExercicio==-111) { // it is -111 => the student does nothing in iCG... refuse to send empty answer
    // 
System.out.println("[IA.getAnswer] OK: this.valorExercicio="+this.valorExercicio);
       if (!codeEdited) {
          String strErr = "2: "+Bundle.msg("exercEmptyAnswer");
          // new JanelaDialogo(strErr, null); // if student do not edited the programm => did nothing!
          labelMensagem.setText(strErr); //
          return "-1";
          }
       else { // it is not -111 => there was edition in the object code (at least the user 'reload' his programm)
          // 
System.out.println("[IA.getAnswer] OPS!!! this.valorExercicio="+this.valorExercicio);
          acaoEnviar(); // primeiro tem que avaliar resposta-aluno, esta define valor da resposta em 'this.valorExercicio'
          }
       }

    //System.out.println("[IA.getEvaluation] DEPOIS this.valorExercicio="+this.valorExercicio);
    return getSession();
    }

  // Clear the evaluation value: from 'icg.emulator.EmulatorBaseClass.atualiza(String codObjeto)'
  public void clearEvaluation () {
    this.valorExercicio = -111; // this.icgPrincipal.setCodeEdited
    }


  // gabarito = "{ E: N N }, { S: N } { E: N N N }, { S: N N }"
  // From: icg.iCG.acaoEnviar(iCG.java:832) -> icg.iCG.montaGabaritoHTML(iCG.java:495)
  private static void contaVetEntSai (String gabarito) { // conta "E:" e def. <strVetEntradas={"E: N N N", "E: N N "}>
    strVetEntradas = new Vector(); // começa novo vetor para strings de entrada
    strVetSaidas = new Vector(); // começa novo vetor para strings de entrada
    contaVetEntradas = 0;  contaVetSaidas = 0;
    String str="";
    int lim = 0;
    if (Configuracao.listaGabarito) 
       System.out.println("[iCG!contaVetEntSai] gabarito="+gabarito+" lim="+lim);

    if (Criptografia.eh_exercicio(gabarito))
       gabarito = Criptografia.descriptografa(gabarito, ehApplet);
    lim = gabarito.length();

    for (int i=0; i<lim-1; i++) {
        if (gabarito.charAt(i)=='E' && gabarito.charAt(i+1) == ':') {
           contaVetEntradas++;
           str = "E: ";
           i += 2; // pula "E:"
           char c0 = gabarito.charAt(i), c1 = gabarito.charAt(i+1);
           while (i<lim) { // monte "E: N N N"
             if ( ! (c0>47 && c0<59) && c0!=' ' && !(c0=='-' && (c1>47 && c1<59) )) break;
             if (c0=='-' && (c1>47 && c1<59)) {// é número negativo -N
                str += "-"+c1;
                i++;
                }
             else
                str += c0;
             i++;
             if (i<lim-1) {
                c0 = gabarito.charAt(i);
                c1 = gabarito.charAt(i+1);
                }
             else if (i<lim) c0 = gabarito.charAt(i);
             }
           strVetEntradas.addElement(str);
           if (Configuracao.listaGabarito) 
              System.out.println("Inputs: "+contaVetEntradas+" - <"+str+">");
           }
        else
        if (gabarito.charAt(i)=='S' && gabarito.charAt(i+1) == ':') {
           contaVetSaidas++;
           str = "S: ";
           i += 2; // pula "S:"
           char c0 = gabarito.charAt(i), c1 = gabarito.charAt(i+1);
           while (i<lim) { // monte "S: N N"
             if ( ! (c0>47 && c0<59) && c0!=' ' && !(c0=='-' && (c1>47 && c1<59)) ) break;
             if (c0=='-' && (c1>47 && c1<59)) {// é número negativo -N
                str += "-"+c1;
                i++;
                }
             else
                str += c0;
             i++;
             if (i<lim) {
                c0 = gabarito.charAt(i);
                c1 = gabarito.charAt(i+1);
                }
             else if (i<lim) c0 = gabarito.charAt(i);
             }

           strVetSaidas.addElement(str);
           if (Configuracao.listaGabarito) 
              System.out.println("Outputs: "+contaVetSaidas+" - <"+str+">");
           }
        //else System.out.print(gabarito.charAt(i));

        } // for (int i = 0; i<lim-1; i++)
    if (Configuracao.listaGabarito) 
       System.out.println("                contaVetEntradas="+contaVetEntradas+" contaVetSaidas="+contaVetSaidas + "\n-----\n");
    } // static void contaVetEntSai(String gabarito)


  // Monta "{ E: N ... }, { S: N ... }" a partir das "ListaLigada"'s "iCG.listaGabEntradas()" e "iCG.listaGabSaidas()"
  public static String montaGabarito (String strGab) {
    String resp = "{ ";
    resp += strGab + " } ";
    if (iCG.listaGabEntradas()!=null)
       resp += "{ " + iCG.listaGabEntradas().montaListaElementos() + " }";
    if (iCG.listaGabSaidas()!=null)
       resp += "{ " + iCG.listaGabSaidas().montaListaElementos() + " }";
    return resp;
    } // static String montaGabarito(String strGab)


              //__?__ E se o algoritmo do aluno entrar em "loop" ?? 
              //__?__ precisa ao menos de um "finaliza = true;" dentro do "iCGEmulator.execInst(boolean)"
              //__?__ Mas como identificar ??


  // Constrói vetores "vetEntradas" e "vetSaidas" para usar nas comparações com instruções 7EE e 8EE
  // From here in: 'static String montaGabaritoDosVetores()' and 'void acaoEnviar()'
  private static void montaGabaritoHTML (String gabarito) {
    if (gabarito==null || gabarito.length()==0) {
       System.err.println("[iCG.montaGabaritoHTML] Error: there are no exercise template!");
       return;
       }
    //-- já vem "decodificado"
    //_ if (Configuracao.listaGabarito) {
    //_   System.out.println("[iCG!montaGabaritoHTML] origem=||"+gabarito+"||");//+"\n decod.=||"
    //_   }

    // Se precisasse "decodificar": +Criptografia.descriptografa(gabarito,true)+"||");

    numEntradas = 0; // número de entradas solicitadas no programa (compara com "vetEntradas.length")
    numSaidas = 0;   // número de saidas solicitadas no programa (compara com "vetSaidas.length")

    // gabarito = "{ E: N N }, { S: N } { E: N N N }, { S: N N }"
    contaVetEntSai(gabarito); // conta "E:", def. "contaVetEntradas" e "strVetEntradas <- {"E: N N", "E: N N N "}
                              // conta "S:", def. "contaVetSaidas" e "strVetSaidas   <- {"S: N", "S: N N "}
    if (contaVetEntradas!=contaVetSaidas) {
       System.out.println("[iCG!montaGabaritoHTML(String)] erro, "+contaVetEntradas+" != "+contaVetSaidas);
       return ;
       }
    else
    if (Configuracao.listaGabarito) 
       System.out.println(" contaVetEntradas="+contaVetEntradas+" == "+contaVetSaidas+" = contaVetSaidas");

    vetEntradas = new Vector[contaVetEntradas]; //vetEntradas = new Vector();
    vetSaidas   = new Vector[contaVetEntradas]; //vetSaidas = new Vector();
    for (int i=0; i<contaVetEntradas; i++) {
        vetEntradas[i] = new Vector(); //vetEntradas = new Vector();
        vetSaidas  [i] = new Vector(); //vetSaidas = new Vector();
        }
    for (int i=0; i<contaVetEntradas; i++) {
       StringTokenizer st;
       String str, s = "";
       char c = '-';

       try {
         // Entradas
         //str = atributo(gabarito, 'E'); // pega primeiro entradas
         str = atributo((String)strVetEntradas.elementAt(i), 'E'); // pega primeiro entradas
         if (str=="" || str==null) {
            return;
            }
         if (str.charAt(0)==' ') str = str.substring(1,str.length()-1); // se branco, pegue próximo
         //--
         if (Configuracao.listaGabarito) 
            System.out.println("[iCG!montaGabaritoHTML] vetEntradas["+i+"]: <"+str+">"); //
         st = new StringTokenizer(str, " "); // N N
         while (st.hasMoreTokens()) {
           s = st.nextToken();
           if (s!=null) {
              vetEntradas[i].addElement(s); // adiciona elemento ao Vector vetEntradas
              //--
             if (Configuracao.listaGabarito) 
                System.out.print(" addElement("+s+")"); //
              }
           }
         // Saídas
         str = atributo((String)strVetSaidas.elementAt(i), 'S'); // pega primeiro entradas
         //--
         if (Configuracao.listaGabarito) 
            System.out.println("\n[iCG!montaGabaritoHTML] vetSaidas["+i+"]: <"+str+">"); //
         st = new StringTokenizer(str, " "); // N N
         while (st.hasMoreTokens()) {
           s = st.nextToken();
           if (s!=null) {
              vetSaidas[i].addElement(s); // adiciona elemento ao Vector vetSaidas
              //--
              if (Configuracao.listaGabarito) 
                 System.out.print(" addElement("+s+")"); //
              }
           }
       } catch (Exception e) {
         System.err.println("[iCG.montaGabaritoHTML] erro na decodificação do tipo "+c+" de: \n "+ s); //
         // e.printStackTrace();
         //return "";
         }
       } // for (int i=0; i<contaVetEntradas; i++)

    if (Configuracao.listaGabarito)
       System.out.println(""); //
    //E emulatorBaseClass.reseta();
    //E emulatorBaseClass.atualizaMemoria(emulatorBaseClass.getCodigo());

    } // static void montaGabaritoHTML(String gabarito)


  // Deactivate the template registration
  // Whenever: load code to memory
  private void deactivateTemplate (boolean activate) {
     if (botaoGabarito.selecionado()) {
        botaoGabarito.deselecione(); // up this button: desativate "template recording"
        }
     ehGeraGabarito   = false; // not generating template anymore 
     strCriptTemplate =  null; // clear the list of registered <inputs x outputs>
     listaGabEntradas =  null; // list with all inputs
     listaGabSaidas   =  null; // list with all outputs
     }

  // Change status related to "template building"
  // - at any insctruction "7EE": register input in the list "listaGabEntradas"
  // - at any insctruction "8EE": register output in the list "listaGabSaidas"
  // From: icg.iCG.acaoGabarito()
  private void montaGabarito () {
    // ListaLigada listaGabEntradas, listaGabSaidas 
    // System.out.println("[iCG!montaGabarito] ehGeraGabarito="+ehGeraGabarito);
    if (ehGeraGabarito) { // já estava gerando gabarito, termine
       ehGeraGabarito = false; // not generating template anymore 
       listaGabEntradas = null; // lista com todas as entradas
       listaGabSaidas   = null; // lista com todas as saídas
       }
    else {
       //System.out.println("[iCG!montaGabarito()] Agora emule quantas vezes quiser e ao final, clique em "+Bundle.msg("EnviarAvaliar"));
       System.out.println("Agora emule quantas vezes quiser e ao final, clique em "+Bundle.msg("EnviarAvaliar"));
       ehGeraGabarito = true; // anota que está gerando gabarito
                              // indica que está montando gabarito, anote entradas e saídas (iCGEmulator)
       listaGabEntradas = new ListaLigada(); // lista com todas as entradas
       listaGabSaidas   = new ListaLigada(); // lista com todas as saídas
       }
    } // void montaGabarito()


  private static void listaTodasES () {
    if (iCG.listaGabEntradas()==null || iCG.listaGabSaidas()==null) {
       System.out.println("Lista todas ES: listas vazias!");
       return;
       }
    String str;
    str = "{ E:";
    for (int i=0; i<vetEntradas[0].size(); i++)
        str += " "+vetEntradas[0].elementAt(i);
    str = " }, { S:";
    for (int i=0; i<vetSaidas[0].size(); i++)
        str += " "+vetSaidas[0].elementAt(i);
    //System.out.println("Lista todas ES: "+str);
    System.out.println("[iCG.listaTodasES()] Lista todas ES: "+str);
    }

  // Formato não criptografado: { E: 9 8  }, { S: 17  }
  // From: icg.emulador.EmulatorBaseClass.execInst(...)
  public static String montaGabaritoDosVetores () {
    System.out.println("[iCG.montaGabaritoDosVetores()] "); //vetEntradas="+vetEntradas+" vetSaidas="+vetSaidas);
    if (iCG.listaGabEntradas()==null || iCG.listaGabSaidas()==null) return "";
    String str0 = "{ E: "+iCG.listaGabEntradas().montaListaElementos()+" }, "+
                  "{ S: "+iCG.listaGabSaidas()  .montaListaElementos()+" } ";
    montaGabaritoHTML(str0); // build static vectors "vetEntradas" and "vetSaidas" from 'str0'

    //    iCG.listaGabEntradas().add();
    //    iCG.listaGabSaidas().add();
    // addListaGabEntradas(String): em 'icg.emulador.iCGEmulator.ok_actionPerformed(ActionEvent e)'
    //                              if (iCG.ehGeraGabarito) 
    // addListaGabSaidas(String)  : em 'icg.emulador.iCGEmulator.ok_actionPerformed(ActionEvent e)' 
    //                              if (iCG.ehGeraGabarito) iCG.addListaGabSaidas(strSaida); 

    if (strCriptTemplate==null) // WARNING: let this prolix way in order to avoid programming error...
       strCriptTemplate = Criptografia.criptografa(str0,true); // the first list <inputs x outputs>
    else
       strCriptTemplate += Criptografia.criptografa(str0,true); // other list <inputs x outputs>

    // listaTodasES();
    System.out.println("[iCG.montaGabaritoDosVetores()] "+str0+" -> ||"+ strCriptTemplate +"||");
    return str0;
    } // static String montaGabaritoDosVetores()


  public static String atributo (String s, char c) { // c='E' de entradas ou 'S' de saídas
    try {
      StringTokenizer st = new StringTokenizer(s, ",");

      while (st.hasMoreTokens()) {
        s = st.nextToken();
        int i = 0;
        String strA = "";
        char c0;
        if (s!=null && s!="") {
           c0 = s.charAt(0);
           if (c0==' ') s = s.substring(1,s.length()-1);
           c0 = s.charAt(0);
           if (c0=='{') s = s.substring(1,s.length()-1);
           }
        //else c0 = '@';
        //while (s!=null && s!="" && i<s.length()) {// && (c0=='{'||c0==' ') ) {
        //   c0 = s.charAt(i);
        //   if (c0=='{') break;
        //   strA += c0;
        //   i++;
        //   }
        //s = strA;
        //-System.out.print(" <"+s+">");
        StringTokenizer st2 = new StringTokenizer(s, ":");
        // int valor = emInteiro(st2.nextToken());
        String nt2 =  st2.nextToken();
        char valor = nt2.charAt(0);
        if (valor==' ') valor = nt2.charAt(1); // se próximo é branco, pegue o próximo
        if (valor == c) { // c='E' => entrada
           if (st2.hasMoreTokens()) {
              String st_ = st2.nextToken();
              //if (i==FONTE) System.out.println("atributo: tipo "+i+" encontrado: "+st_);
              return st_;
              }
           else {
              return "";// null; //
              }
            }
        }
      //-System.out.println(" fim - vazio! ");
      return ""; // null;//
    } catch (Exception e) {
        System.err.println("[iCG.atributo] erro na decodificação do tipo "+c+" de: \n "+ s); //
        // e.printStackTrace();
        return "";
        }
    } // static String atributo(String s, char c)


  // Load values to the internal variabel (you need 'icg.setAllContents()' to load these value to the Emulator/Compiler fields)
  // From: iCG.loadParameters() and iCG.main(String[])
  protected void loadICGdata (String strContent) {
    iCGproperties = icg.io.Arquivos.getProperties(strContent); // get all iCG itens: Statement, Type, Hint, Template, ...
    //T iCGproperties.list(System.out);
    if (iCGproperties==null) {
       System.err.println("[iCG.loadICGdata] Is this a valid iCG file?\n" + strContent);
       return;
       }

    //TBI str_icg_type = iCGproperties.getProperty(tagType); // type of activity: 'example' ou 'exercise'
    str_paramEnunGabarito = iCGproperties.getProperty(tagStatement); //
    str_paramGabarito = iCGproperties.getProperty(tagTemplate); // <Template>...7b20453a202d31203220...b768b7f1efcc3602</Template>
    //TBI str_icg_hint = iCGproperties.getProperty(tagHint); // any hint to the student in this exercise
    str_answer_object = iCGproperties.getProperty(tagObject); //
    str_answer_code = iCGproperties.getProperty(tagCode); // in 'iniciaCG()', after this, see 'painelCompilador.setCodigo(tagCode);'
    //TBI str_answer_comment = iCGproperties.getProperty(tagComment); // any comment from the student
    str_answer_session = iCGproperties.getProperty(tagEvaluation); // preserve any previous evaluation in this exercise

    // str_paramPrograma = str_answer_object; // old variable - must be changed for 'str_answer_object'
    }


  // HTML: load parameters
  private void loadParameters () {
    //String [] vetParamLang = null;
    String strParamLang = null;
    strParamLang = this.getParameter("lang");
    if (strParamLang!=null && strParamLang!="") {
       if (Bundle.decompoeConfig("lang="+strParamLang)) { // split "pt_BR" in ["pt","BR"] ou "en_US" in ["en","US"]
          Bundle.defLocale(); // no main faz 'Bundle.setConfig(args)' e 'Bundle.loadMessages()'
          }
       }
    else {
       //
       if (Bundle.decompoeConfig("lang=pt_BR")) { // split "pt_BR" in ["pt","BR"] ou "en_US" in ["en","US"]
          Bundle.defLocale(); // no main faz 'Bundle.setConfig(args)' e 'Bundle.loadMessages()'
          }
       }

    try {
      codebase = getCodeBase();
      //codebase = new URL("http://milanesa.ime.usp.br/mac118"); // para applet

      str_param_info      = this.getParameter("paramInfo", "");  // parâmetro: 'string' com info. genéricas, pe, p/ guardar URL

      str_paramPrograma   = this.getParameter("paramPrograma", "");

      // Load the exercise template
      // { E: nnnnnn ... nnnnnn }, { S: nnnnnn ... nnnnnn }

      // The iCG content is in
      // v.2 of iAssign: iLM_PARAM_Assignment is "the URL of the file content"
      // v.1 of iAssign: MA_PARAM_Proposition is "the URL of the file content", if MA_PARAM_PropositionURL not "false"
      //                                      otherwise, is "the content"

      str_paramGabarito = this.getParameter("iLM_PARAM_Assignment", "");    // v.2 of iAssign
      if (str_paramGabarito==null || str_paramGabarito.trim().length()==0) {
         str_paramGabarito = this.getParameter("MA_PARAM_Proposition", ""); // v.1 of iAssign
         }
      if (str_paramGabarito!=null && str_paramGabarito.trim().length()>0) { // v.1 or v.2 of iAssitn
         versionXML = true; // all data in a single file
         System.out.println("[iCG.loadParameters()] URL in " + str_paramGabarito);

         // get the iCG content and decompose it in its particular itens
         str_icg_content = icg.io.Arquivos.getMAFile(this, str_paramGabarito); // get the content under this URL
	 loadICGdata(str_icg_content); // load values to the internal variabel (including Emulator/Compiler fields)

         System.out.println("[iCG.loadParameters()] Statement: " + str_paramEnunGabarito);
         //System.out.println("[iCG.loadParameters()] Template: " + str_paramGabarito);
         System.out.println("[iCG.loadParameters()] Object: " + str_answer_object);
         System.out.println("[iCG.loadParameters()] Code: " + str_answer_code);
         // System.out.println("[iCG.loadParameters()] : " + );
         }
      else { // (str_paramGabarito==null || str_paramGabarito.trim().length()==0)
	 str_paramGabarito = this.getParameter("paramGabarito", "");        // v.0 of iAssign
         }

      if (str_paramEnunGabarito!="") {
         str_paramEnunGabarito  = this.getParameter("paramEnunGabarito", ""); // enunciado do exercício
         ehExercicio = true;
         }
      else {
         str_paramEnunGabarito  = this.getParameter("iLM_PARAM_Assignment", ""); // v.2 of iAssign
         }
      if (str_paramEnunGabarito!=null && str_paramEnunGabarito.trim().length()>0) {
	 // is exercise => get some aditional option
         // From parameter 'iLM_PARAM_Feedback': used here in 'void acaoEnviar()'
         str_paramFeedback = this.getParameter("iLM_PARAM_Feedback", ""); // v.2 of iAssign
         }

      str_paramEmulador   = this.getParameter("emulador", "");

      // Allow teacher to author the exercise => use "botaoGabarito"
      str_paramProfessor  = this.getParameter("professor", ""); // v.0 of iAssign
      if (str_paramProfessor=="")
         str_paramProfessor  = this.getParameter("iLM_PARAM_Authoring", ""); // v.2 of iAssign

      str_param_end_post  = this.getParameter("enderecoPOST",""); // parâmetro: endereço para envio de exercícios é o codebase

      // se paramGabarito<>"", então não entre botão de "Gabarito" nem de "Compilador"

      // Tests...
      System.out.println("[iCG] paramPrograma=" + str_answer_object); // str_paramPrograma
      System.out.println("[iCG] paramGabarito="+str_paramGabarito);
      //DEBUG System.out.println("[iCG] paramGabarito=||"+Criptografia.descriptografa(str_paramGabarito,true)+"||");
      System.out.println("[iCG] paramEnunGabarito="+str_paramEnunGabarito);
      System.out.println("[iCG] emulador="+str_paramEmulador);
      System.out.println("[iCG] professor="+str_paramProfessor);
      System.out.println("[iCG] enderecoPOST="+str_param_end_post);
      System.out.println("[iCG] paramInfo="+str_param_info);
      //System.out.println("[iCG]="+);

      // Se igual a 'respostaAluno', então mostrar a solução enviada pelo aluno , em 'paramGabarito', e não carregar botão de envio
      boolean mostraCodigoAluno = false; // código a ser carregado em 'str_paramGabarito'
      str_paramAluno = this.getParameter("paramAluno");  //
      if (str_paramAluno!=null && str_paramAluno!="" && str_paramAluno.equals("respostaAluno")) {
         // não tente decodificar que é resposta de aluno
         if (str_answer_object==null || str_answer_object=="") // já tem código, esqueça
            str_answer_object = str_paramGabarito;
         mostraCodigoAluno = true;
         }
      // System.out.println("[iCG] ->"+str_paramAluno+"<-"); // <param name='paramAluno' value='respostaAluno'>

      // Para testes...
      // Descodifica a lista "{ E: N ... }, { S: N ... }"
      //String str0 = str_paramGabarito;
      //if (!mostraCodigoAluno && str_paramGabarito!=null && str_paramGabarito!="")
      //   decodeTemplate(str_paramGabarito); // str_paramGabarito = Criptografia.descriptografa(str_paramGabarito,true);

      }
    catch (Exception e) {
      System.err.println("Erro: iCG: "+codebase);
      e.printStackTrace();
      }

    } // private void loadParameters()


  // Para testes
  // If it is an exercise, decode it and define 'str_paramGabarito'
  public String decodeTemplate (String strText) {
    if (strText==null || strText=="")
       return null;
    ehExercicio = true;
  
    str_paramGabarito = Criptografia.descriptografa(strText.trim(),true); // erase eventual '\n' when read from a file (aplicative)
    return str_paramGabarito;
    }


  // Inicia applet
  public void init () {
    //T String strCript = null;

    ehApplet = true;

    TrataImage.eh_applet(true); //

    // recarrega variáveis estáticas de Configuracao
    appletICG = this;

    // load de Messges (in the correct language)
    loadParameters();

    //T try  {
    //T   strCript = Criptografia.criptografa(str_answer_object,true);
    //T } catch (Exception e) {
    //T   System.err.println("Erro: iCG: problema na criptografia:\n"+strCript);
    //T   e.printStackTrace();
    //T   }

    try {
      iniciaCG();
    } catch (Exception e) {
      System.err.println("Erro: iCG: problema ao iniciar o iCG...");
      e.printStackTrace();  
      }

    } // void init()



 // Start/stop the "exercise template" recording: each run it take note of a set "{ E: N ... }, { S: N ... }"
 public void acaoGabarito () { // click in button "botaoGabarito'
   if (ehGeraGabarito) {
      botaoGabarito.deselecione(); // down this button: desativate "template recording"

      // register template in tag Template
      if (strCriptTemplate!=null && strCriptTemplate.length()>0) {
         iCGproperties.put(tagTemplate, strCriptTemplate); // "<Template>...</Template>"
         if (Configuracao.listaGabarito) {
            System.out.println("[iCG!acaoGabarito] Template=||" + strCriptTemplate + "||");
            }
         }
      else { // Sorry, but I have no register of a list of <inputs x outputs>
             // to be used as an exercise template!
         String [] strExplainExerc1 = { Bundle.msg("gabErrorEmpty1"), Bundle.msg("gabErrorEmpty2") };
         new JanelaDialogo(Bundle.msg("gabErTitle"), strExplainExerc1, true); //
         return;
         }

      labelMensagem.setText(Bundle.msg("gabFim")); //
      // The new exercise template is ready to be recorded,
      // please, use the button: 
      String [] strExplainExerc1 = { Bundle.msg("gabEr3Exp1"), Bundle.msg("gabEr3Exp2") + Bundle.msg("msgStore") };
      new JanelaDialogo(Bundle.msg("gabErTitle"), strExplainExerc1, true); // 
      }
   else {
      if (!codeEdited) { // there are no code in Memory => no way to construct a new exercise
         // In order to genarate a new exercise template,
         // it is necessary a program in Memory!
         String [] strExplainExerc1 = { Bundle.msg("gabEr1Exp1"), Bundle.msg("gabEr1Exp2") };
         new JanelaDialogo(Bundle.msg("gabErTitle"), strExplainExerc1, true); // 
         return;
         }
      // Genarate a new exercise template:
      // 1. now you must run your program;
      // 2. enter your datas (those required by your program);
      // 3. when your program ends, you can stop the recording
      //    clicking the same button or star the recording of
      //    a new set of <input x output> to be used as a template
      String [] strExplainExerc1 = { Bundle.msg("gabEr2Exp1"), Bundle.msg("gabEr2Exp2"), Bundle.msg("gabEr2Exp3"), Bundle.msg("gabEr2Exp4"), Bundle.msg("gabEr2Exp5"), Bundle.msg("gabEr2Exp6") };
      new JanelaDialogo(Bundle.msg("gabErTitle"), strExplainExerc1, true); // 

      botaoGabarito.selecione(); // abaixe botão
      labelMensagem.setText(Bundle.msg("gabInicio")); // 
      }
   String str = emulatorBaseClass.getCodigo();
   strGabEntradasSaidas = ""; // vai armazenar todas entradas/saídas p/ geradas
   System.out.println("[Bt!iniciaCG()] Início/fim de montagem de gabarito.");
   montaGabarito(); // define "montaGabarito = true;"
   }


  // Exercise: evaluate the exercise
  // If connected to an LMS (like Moodle with iAssign) send the exercise to the server, using 'icg.util.EnviaWeb'
  // From: icg.ig.Botao.mouseClicked(java.awt.event.MouseEvent) - button inside iCG
  //       icg.iCG.getEvaluation()                              - button external from iCG
  public void acaoEnviar () { // chamada de Botao
    String strObjectCode = emulatorBaseClass.getCodigo(),
           strGabarito2 = ""; // caso "professor" e "str_paramGabarito==""", será o gabarito criptografado

    //System.out.println("[IA.acaoEnviar] codeEdited="+codeEdited);
    if (str_paramGabarito==null || str_paramGabarito.length()==0) {
       labelMensagem.setText(Bundle.msg("exercNotTemplate")); // 
       new JanelaDialogo(Bundle.msg("exercNotTemplate"), null); // Este não é um exercício com avaliação automática
       return;
       }

    if (!codeEdited) { // defined in 'icg/emulador/EmulatorBaseClass.java: setCodigo(String s)
       labelMensagem.setText("3: "+Bundle.msg("exercEmptyAnswer")); 
       // if student do not edited the programm => did nothing!
       try {
         new JanelaDialogo(Bundle.msg("exercEmptyAnswer"), null); // "Atencao: sua resposta esta \"em branco\""
         // try { String str00="";System.out.println(str00.charAt(3)); } catch (Exception e) { e.printStackTrace(); }
       } catch(Exception e) { e.printStackTrace(); }
       //Test new JanelaDialogo(Bundle.msg("exercEmptyAnswer"), new String [] { "texto 1", "texto 2", "texto 3" });
       return;
       }
    //Test else { new JanelaDialogo("Foi editado!", null); if (1==1) return; }

    ehGeraGabarito = false;
    botaoGabarito.deselecione(); // suba botão que anota início de construção de gabarito

    // Apenas se for "applet" para professor, ou seja, "str_paramProfessor="Gabarito""
    // por enquanto isso é feito "NA MÃO", ou seja, o prof. tem q/ gerar o HTML todo e anexar no BD!!!!
    //
    if ( str_paramProfessor!=null && str_paramProfessor!="" && str_paramProfessor.equals("Gabarito") &&
         str_paramGabarito=="") { // ainda não pode haver um gabarito, depois completa com "Enviar" chamando "
       strGabarito2 = iCG.strGabEntradasSaidas; // montaGabaritoDosVetores();
       System.out.println("[iCG!acaoEnviar()] "+str_paramProfessor+": ||"+strGabarito2+"||\n"+
                          "                   ||"+Criptografia.criptografa(strGabarito2,true)+"||");
       }
    //System.out.println("[IA.acaoEnviar()] 4 - str_paramGabarito=" + str_paramGabarito);

    // Apenas se for "applet" para aluno, ou seja, "str_paramGabarito!=null"
    if (str_paramGabarito!=null && str_paramGabarito.length()>0) {
       montaGabaritoHTML(str_paramGabarito);
       //System.out.println("[IA.acaoEnviar()] 5");

       if (vetEntradas!=null && vetEntradas.length>0) {
          if (Configuracao.listaGabarito)
             System.out.println("[iCG!acaoEnviar()] Answer: ||"+str_paramGabarito+"||\n"+
                                "                   #input[0]="+vetEntradas[0].size()+" #output[0]="+vetSaidas[0].size());
          }
       else {
          String strErr = Bundle.msg("errInputEmpty"); // Exercício com problemas: não existe entradas para testes no gabarito
          System.out.println("[iCG] Error: " + strErr);
          System.out.println("[iCG] não há entradas em gabarito, esqueça!");
          labelMensagem.setText(strErr);
          return;
          }

       testandoGabarito = true;
       int contAcertos = 0; // qde de acertos entre programa do usuário e gabarito, em rel. às entradas
       contAcertosSai  = 0; // qde de acertos entre programa do usuário e gabarito, em rel. às saídas
       int resultado   = 0;

       System.out.println("Loop             ----------  #number of testing sets=" + contaVetEntradas);

       if (!emulatorBaseClass.hasCodeInMemory()) { // emulatorBaseClass.getCodigo()
          // strObjectCode==null || strObjectCode.trim().length()==0
          if (str_paramProfessor!="" && str_paramProfessor.trim().equalsIgnoreCase("true")) { // automatically load the content - teacher do not lose time
             emulatorBaseClass.atualizaMemoria(strObjectCode);
             // System.out.println("\n\n[iCG.acaoEnviar()] carregado... --------------------" + strObjectCode);
             }
          }
       if (!emulatorBaseClass.hasCodeInMemory()) { // still empty => warning the user...
          // if student do not edited the programm => did nothing!
          new JanelaDialogo(Bundle.msg("exercEmptyAnswer"), null); // "Atencao: sua resposta esta \"em branco\""
          // System.out.println("[iCG.acaoEnviar()] sem codigo na memoria do Emuldador...");
          return;
          }

       boolean isAuthor = false;
       String strDebugAuthor[], strDAI, strDAO;
       Vector vectorDebugAuthor = new Vector();
       if (str_paramProfessor!=null && str_paramProfessor.length()>0 && str_paramProfessor.trim().equalsIgnoreCase("true")) {
          // it is to a teacher: show everything
          isAuthor = true; // CAUTION: it could show the answer to the student
          }

       // atualVetEntSai é um contador utilizado no "iCGEmulator" (isso é importante)
       for (atualVetEntSai=0; atualVetEntSai<contaVetEntradas; atualVetEntSai++) {
         //Test   System.out.println("\n\n[iCG] atualVetEntSai="+atualVetEntSai+"-------------------------------------------------");
         iEnt = 0;           // índice para vetEntradas[]
         iSai = 0;           // índice para vetSaidas[]

         contAcertosSai = 0; // importante, conta núm. de coincidências de saídas (deve ser = a contaVetSaidas)
         numSaidasAluno = 0; // total de saídas (8ee) do aluno no atual teste

         /* Test
          try {
             if (numSaidasAluno==vetSaidas[atualVetEntSai].size())
             else {
                resultado=0;
                System.out.println("                 ---------- Respostas ERRO: uma instância com mais saídas do aluno que o esperado");
               }
         } catch (Exception expt) {
                resultado=0;
                System.err.println("                 ---------- Respostas ERRO: uma instância com mais saídas do aluno que o esperado");
                System.err.println("                 ---------- atualVetEntSai="+atualVetEntSai+" <- ");
         }*/

         emulatorBaseClass.reseta();        // EPI <- (0,0) importante
         emulatorBaseClass.execInst(false); // define 'iCG.contAcertosSai'

         if (Configuracao.listaGabarito) {
            strDAI = Bundle.msg("exercInputMark") + ": " + listaVector(vetEntradas[atualVetEntSai]); // Entradas:
            strDAO =Bundle.msg("exercOutputMark") + ": " + listaVector(vetSaidas[atualVetEntSai]); // Saidas:
            //Debug
            System.out.println("\n                 ----------  testing set: #"+atualVetEntSai);
            System.out.println(strDAI);
            System.out.println(strDAO);
            }

         String strSituation;
         if (iCG.contAcertosSai()==vetSaidas[atualVetEntSai].size() &&
             numSaidasAluno==vetSaidas[atualVetEntSai].size()       && // se aluno deu mais saídas => erro
             iCG.iEnt()==vetEntradas[atualVetEntSai].size() ) { // tem que coincidir tb o número de entradas
            System.out.println("                 ---------- Resposta CORRETA: <saidas: "+
                               iCG.contAcertosSai()+"="+vetSaidas[atualVetEntSai].size() + "?>  <entradas: "+
                               iCG.iEnt()+"="+vetEntradas[atualVetEntSai].size() + "?>");
            contAcertos++;
            strSituation = Bundle.msg("exercTestCorrect");
            resultado++; // correto - correct
            }
         else {
            System.out.println("                 ---------- Resposta ERRADA: <saidas: "+
                               iCG.contAcertosSai()+"="+vetSaidas[atualVetEntSai].size() + "?>  <entradas: "+
                               iCG.iEnt()+"="+vetSaidas[atualVetEntSai].size() + "?>");
            strSituation = Bundle.msg("exercTestWrong");
            }
         //else { //Teste 
         //   System.out.println("                 ---------- Resposta ERRADA:  "+
         //                       iCG.contAcertosSai()+"!="+vetSaidas[0].size());
         //   //contErrosEnt++;
         //   }
         if (Configuracao.listaGabarito) {
            if (EmulatorBaseClass.erroPara) { // pára só no segundo erro
               EmulatorBaseClass.erroPara=false;
               System.err.println("Erro no gabarito "+atualVetEntSai+" !");
               }
            }

         if (isAuthor) { // CAUTION: it could show the answer to the student
            // it is to a teacher: show everything
            vectorDebugAuthor.addElement("-------------------");
            vectorDebugAuthor.addElement(Bundle.msg("exercAuthorTestNum") + ": " + atualVetEntSai);
            vectorDebugAuthor.addElement(Bundle.msg("exercSituation") + ": " + strSituation);
            vectorDebugAuthor.addElement(strDAI);
            vectorDebugAuthor.addElement(strDAO);
            }

         } // for (atualVetEntSai=0; atualVetEntSai<contaVetEntradas; atualVetEntSai++)

       String codExec = this.emulatorBaseClass.getTextInputExecCode(); //emulatorBaseClass.getEmulatorMainPanel().getTextInputExecCode(); 
       emulatorBaseClass.atualiza(codExec); // atualiza posições de memória do emulatorBaseClass

       String strResult = "";
       System.out.println("                 ---------- Respostas CORRETAS="+contAcertos+
                          " erros="+(contaVetEntradas-contAcertos)+" | resultado="+resultado+" \n");
       // Algoritmo de avaliacao: 'this.valorExercicio'
       // 1.0: tudo certo
       // 0.5: certo, com contra-exemplo
       // 0.0: tudo errado
       if (contAcertos>0) { // ao menos um erro
          if (contAcertos<contaVetEntradas) {
             resultado = 2;
             this.valorExercicio = 0.5;
             System.out.println("                 ---------- Resultado final: alguns erros...\n");
             strResult = Bundle.msg("exercNotCompleteRight");
             }
          else { // passou em todos os testes
             resultado = 1;
             this.valorExercicio = 1.0;
             System.out.println("                 ---------- Resultado final: solução correta!\n");
             strResult = Bundle.msg("exercCompleteRight");
             }
          }
       else { // falhou em TODOS os testes
          resultado = 0;
          this.valorExercicio = 0.0;
          System.out.println("                 ---------- Resultado final: vários erros!\n");
          strResult = Bundle.msg("exercWrong");
          }

       // Register in this session this evaluation - 'getSession()' is used in order to update all properties 'iCGproperties'
       String strDate = new java.util.Date().toGMTString(); //
       str_answer_session = "[date=" + strDate + "; eval= " + this.valorExercicio + "]" + str_answer_session; //

       // From parameter 'iLM_PARAM_Authoring' - v.2 of iAssign
       if (isAuthor) { // CAUTION: it could show the answer to the student
          // it is to a teacher: show everything
          strDebugAuthor = new String[vectorDebugAuthor.size() + 1];
          strDebugAuthor[0] = strResult;
          for (int i__=0; i__<vectorDebugAuthor.size(); i__++)
              strDebugAuthor[i__+1] = (String)vectorDebugAuthor.elementAt(i__);
          //strDebugAuthor = ;
          // String [] strVet = new String [] { strResult, "texto 1", "texto 2", "texto 3" };
          new JanelaDialogo(strResult, strDebugAuthor); //
          labelMensagem.setText(strResult); //
          }
       else
       // From parameter 'iLM_PARAM_Feedback' - v.2 of iAssign
       if (str_paramFeedback==null || str_paramFeedback.trim().length()==0 || str_paramFeedback.equalsIgnoreCase("true")) {
          // student can see the evaluation results
          new JanelaDialogo(strResult, null); // "Atencao: sua resposta esta \"em branco\""
          labelMensagem.setText(strResult); //
          }
       else // In this exercise the results of the evaluation is blocked the teacher
          labelMensagem.setText(Bundle.msg("exercFeedbackBlocked"));

       // V.0 of iAssign: the button to evaluate also send to the server the answer
       // V.2 of iAssign: the LMS takes care of the carry out to the server
       // EnviaWeb.enviarResultadoExercicio(resultado, str, strGabarito2);

       // Volta para configuração inicial: permite funcionar apenas clicando agora no "Emular"
       testandoGabarito = false;
       contAcertosEnt = 0; // qde de acertos entre programa do usuário e gabarito, em rel. às entradas
       contAcertosSai = 0; // qde de acertos entre programa do usuário e gabarito, em rel. às saídas
       iEnt = 0;           // índice para vetEntradas
       iSai = 0;           // índice para vetSaidas
       } // if (str_paramGabarito!=null && str_paramGabarito!="")

    labelMensagem.setText(Bundle.msg("gabFim")); // 

    // V.0 of iAssign: the button to evaluate also send to the server the answer
    // V.2 of iAssign: the LMS takes care of the carry out to the server
    // else // envia sem correção!
    //   EnviaWeb.enviarResultadoExercicio(0, str,""); // último parâm. seria p/ o gabarito do prof. (construção de exerc.)

    // For while - when it is not implemented interface to register a file...
    System.out.println("[iCG.acaoEnviar] file to be recorded\n"+getSession());

    } // void acaoEnviar()


  // iCG Emulator environment
  // Build the component with: <button emulator> <button compiler> |--- space ---| <button > <button help>
  public void setBotaoEmulador () {
    // Desabilita botão "Emulador", habilita botão "Compilador"
    ehEmulador = true;

    // Remove all common to Emulator-Compiler from Compiler
    this.painelCompilador.remove(botaoEmulador); // removeAll()
    this.painelCompilador.remove(botaoCompilador);
    this.painelCompilador.remove(botaoSobre);
    this.painelCompilador.remove(botaoAjuda);

    //A Try to remove panel???
    //A this.remove(this.painelCompilador); // remove all the Compiler panel
    //A this.add(this.emulatorMainPanel, null); // add Emulator panel

    labelMensagem.setText(Bundle.msg("barraMsg")); // volta msg inicial p/ identificar 'barra de msg'
    this.emulatorMainPanel.add(labelMensagem, null);   //-

    botaoEmulador.selecione();       // botaoEmulador.abaixado(false);
    botaoCompilador.deselecione();   // botaoCompilador.abaixado(true);
    botaoEmulador.setEnabled(false); //
    botaoCompilador.setEnabled(true);//

    // Se a janela de troca de fontes estiver aberta, feche-a
    if (frameEdicaoFonte!=null) frameEdicaoFonte.setVisible(false);

    String codigoExecutavel = null;
    codigoExecutavel = emulatorBaseClass.getCodigo(); // try to get "object code" from Emulator area
    if (painelCompilador.compilacaoOK())
       codigoExecutavel = painelCompilador.getCodigo(); // try to get "object code" from Compiler area for "object code"

    // if there is no code, forget it (do not enter in the 'emulatorBaseClass.setCodigo(...)' bellow
    if (codigoExecutavel!=null && codigoExecutavel.trim().length()>0)
       emulatorBaseClass.setCodigo(codigoExecutavel);

    // Rebuild this panel completely
    this.emulatorMainPanel.setEnabled(true);
    this.emulatorMainPanel.setVisible(true); // default is iCG Emulator
    this.painelCompilador.setVisible(false);

    this.emulatorMainPanel.addEmulatorButtons(); // add all common buttons: botaoEmulador, botaoCompilador, botaoSobre, botaoAjuda

    } // void setBotaoEmulador()


  // iCG Compiler environment
  // Build the component with: <button emulator> <button compiler> |--- space ---| <button > <button help>
  public void setBotaoCompilador () {
    ehEmulador = false;

    // Remove all common to Emulator-Compiler from Emulator
    this.emulatorMainPanel.remove(botaoEmulador); // removeAll()
    this.emulatorMainPanel.remove(botaoCompilador);
    this.emulatorMainPanel.remove(botaoSobre);
    this.emulatorMainPanel.remove(botaoAjuda);

    //A Try to remove panel???
    //A this.remove(this.emulatorMainPanel); // remove all the Emulator panel
    //A this.add(this.painelCompilador, null); // remove all the Emulator panel

    botaoEmulador.deselecione();   // botaoEmulador.abaixado(true);
    botaoCompilador.selecione();   // botaoCompilador.abaixado(false);
    botaoEmulador.setEnabled(true);
    botaoCompilador.setEnabled(false);

    // Se a janela de troca de fontes estiver aberta, feche-a
    if (frameEdicaoFonte!=null) frameEdicaoFonte.setVisible(false);

    this.emulatorMainPanel.setVisible(false);
    this.painelCompilador.setVisible(true);
    this.painelCompilador.setEnabled(true);

    // iCG Compiler panel - painelCompilador - load here all the buttons (this implies that they leave emulatorMainPanel)
    this.painelCompilador.add(botaoEmulador, null); // allow to change to the iCG Emulator
    this.painelCompilador.add(botaoCompilador, null);
    this.painelCompilador.add(botaoCompila, null); //-
    this.painelCompilador.add(botaoSobre, null);   //- this is common to Emulator and Compiler
    this.painelCompilador.add(botaoAjuda, null);   //-
    this.painelCompilador.add(labelMensagem, null);   //-

    } // void setBotaoCompilador()


  // Components initialization
  private void iniciaCG () throws Exception {

    //- Load messages to the main buttons
    Configuracao.defMsgs(); // define textos para msgs

    // Attention: para não dar erro de sujeira na imagem, carrega uma só vez, como 'static {...}'
    //            imgComp     = TrataImage.trataImagem(true,"compilador.gif"); // png não funciona no Java 1
    //            ...
    //            imgCompila  = TrataImage.trataImagem(true,"compila.gif"); // 
    startIconButtons(); // para carregar icones de botoes 

    // -- iCG
    //  - botoes principais
    botaoEmulador      = new Botao(this,"Emulador"        , Configuracao.MSGEMULADOR,  imgEmul    ,false); // botao para pegar interface Emulador
    botaoCompilador    = new Botao(this,"Compilador"      , Configuracao.MSGCOMPILADOR,imgComp    ,false); // botao para pegar Compilador
    botaoInfo          = new Botao(this,"Informacoes"     , Configuracao.MSGINFO,      imgInfo    ,false);
    botaoAjuda         = new Botao(this,"Ajuda"           , Configuracao.MSGAJUDA,     imgAjuda   ,false);
    botaoSobre         = new Botao(this,"Sobre"           , Configuracao.MSGSOBRE,     imgSobre   ,false);
    //  - Emulador
    botaoEnviar        = new Botao(this,"Enviar"          , Configuracao.MSGENVIAR,    imgEnv     ,false);
    botaoGabarito      = new Botao(this,"Gabarito"        , Configuracao.MSGGABARITO,  imgGab     ,false);
    botaoRoda          = new Botao(this,"Emula"           , Configuracao.MSGEMULAR,    imgEmula   ,false);
    botaoRodaPP        = new Botao(this,"EmulaPassoPasso" , Configuracao.MSGPASSO,     imgEmulaPP ,false);
    botaoAtualiza      = new Botao(this,"Atualiza"        , Configuracao.MSGATUALIZA,  imgAtualiza,false);
    //  - Compilador
    botaoCompila       = new Botao(this,"Compila"         , Configuracao.MSGCOMPILA,   imgCompila ,false);

    // Component botoes[] tem que ser passado para iCGEmulator -> Emulador_Panel na ordem:
    botoes = new Botao[10]; // tera imagem em 'EmulatorMainPanel.botoes'
    botoes[0] = botaoCompilador;
    botoes[1] = botaoEmulador;
    botoes[2] = botaoGabarito; // 32 x 32 ??
    botoes[3] = botaoEnviar;
    botoes[4] = botaoRoda;
    botoes[5] = botaoRodaPP;
    botoes[6] = botaoAtualiza;
    botoes[7] = botaoInfo; // 32 x 32 ??
    botoes[8] = botaoSobre;
    botoes[9] = botaoAjuda;

    //- Painel para Compilador
    painelCompilador = new CompilerPanel(this, botaoAjuda, botaoCompila);

    this.setLayout(null);
    this.setVisible(true);
    this.setLocation(1, 1);

    //  'icg.emulador.iCGEmulator' usa painel 'icg.emulador.Emulador_Panel'
    // Era: emuladorApplet = new EmuladorApplet
    emulatorBaseClass = new EmulatorBaseClass(
       this, botoes, str_answer_object, ehApplet, allowLoadCompiler, loadButtonExercEvaluate, loadButtonBuildExerc
       );

    changeI18Texts(); // define label/text of 'labelEdicaoFonte0, labelEdicaoFonte2, labelEnunGabarito, botaoFonteMemEmul, botaoFonteCodEmul, botaoFonteComp, labelMensagem'

    int x_ = emulatorBaseClass.getEmulatorMainPanel().getLocation().x, x__=this.getLocation().x;
    System.out.println("\n[icg.iCG.iniciaCG] emulatorBaseClass="+emulatorBaseClass+"->"+x_+" this="+this+"->"+x__); // trace it...

    // Panel: memory, computer emulator
    this.emulatorMainPanel = emulatorBaseClass.getEmulatorMainPanel(); //

    this.emulatorMainPanel.setBounds(new Rectangle(xPainelCompEmul, yPainelCompEmul, lPainelCompEmul, aPainelCompEmul)); // (0, 0, 600, 441)
    this.emulatorMainPanel.setLayout(null);

    this.painelCompilador.setLayout(null);
    this.painelCompilador.setBounds(new Rectangle(xPainelCompEmul, yPainelCompEmul, lPainelCompEmul, aPainelCompEmul)); // (0, 0, 600, 441)

    this.painelCompilador.setBackground(Configuracao.compilerBg); // cor geral de fundo do Compilador
    this.painelCompilador.setForeground(Configuracao.compilerFg); // cor de fontes que devem contrastar
    this.painelCompilador.setVisible(false); // default is iCG Emulator

    // Button to load iCG Emulador

    if (codebase!=null) {
       // Botao "Gabarito"
       if ( (str_paramGabarito==null || str_paramGabarito=="") && 
            str_paramProfessor!=null && str_paramProfessor!="" && str_paramProfessor.equals("Gabarito")) {
          loadButtonExercEvaluate = true; //HH
          }

       } // if (codebase!=null)
    else { // eh aplicativo => deixe construir exercicio
       loadButtonBuildExerc = true; // precisa generalizar para parametro 'ilm_param_authoring'
       }

    // outros botões: Emular", "Passo a passo" e "Atualiza" estão em "icg/emulador/EmuladorPanel"
    if ((str_paramEmulador==null || (str_paramEmulador!=null && !str_paramEmulador.equals("emulador"))) &&
        (str_paramGabarito==null || str_paramGabarito=="")) {
       // Attention: some exercise must avoid student access to the iCG Compiler
       allowLoadCompiler = true;
       }

    // metodo atual: iniciaCG()
    this.setBotaoEmulador();

    // Estão em 'setBotaoEmulador()' (o inverso em 'setBotaoCompilador()')
    labelMensagem.setText(Bundle.msg("barraMsg")); // volta msg inicial p/ identificar 'barra de msg'

    // Topo do iCG - Emulador - versao e endereco iMatica
    labelEnderecoTopoE.setBounds(new Rectangle(xEnd, yEnd, largEnd, altEnd)); //140,  0,  380,  15
    labelEnderecoTopoE.setFont(Configuracao.ftBold9); //
    labelEnderecoTopoE.setForeground(Configuracao.fundoTopo);
    emulatorMainPanel.add(labelEnderecoTopoE,null);

    // Topo do iCG - Compilador - versao e endereco iMatica
    labelEnderecoTopoC.setBounds(new Rectangle(xEnd, yEnd, largEnd, altEnd)); //140,  0,  380,  15
    labelEnderecoTopoC.setFont(Configuracao.ftBold9); //
    labelEnderecoTopoC.setForeground(Configuracao.compilerFg); // deve contratar com cor de fundo 'Configuracao.compilerBg'
    painelCompilador.add(labelEnderecoTopoC,null);

    // Label de 'barra de mensagens' - parte inferior do iCG
    emulatorMainPanel.add(labelMensagem);
    labelMensagem.setBounds(new Rectangle(leX, leY, leL, leA));
    labelMensagem.setFont(Configuracao.ftEndereco);
    labelMensagem.setBackground(Color.white);             // texto de saída - dispositivo de saída
    labelMensagem.setForeground(Configuracao.corFrente2); //

    // Anexar botões para troca de fonte de 
    //  Emulador: memória ou código
    //  Compilador: qualquer dos três, código fonte ou objeto ou mensagens

    // Emulador ---
    labelEdicaoFonte0.setBounds(new Rectangle( xEdFt,  yEdFt,  largLabel, altEdFt));// 81, 28
    labelEdicaoFonte0.setFont(Configuracao.fonteBotao2); //
    labelEdicaoFonte0.setBackground(emulatorMainPanel.getBackground());
    labelEdicaoFonte0.setForeground(Color.white);

    botaoFonteMemEmul.setBounds(new Rectangle( xEdFt+largLabel,  yEdFt,  largEdFt, altEdFt)); //
    botaoFonteMemEmul.setBackground(Configuracao.corFundo1);  // cor de fundo do botão botaoCompilador
    botaoFonteMemEmul.setForeground(Color.white);             // 
    botaoFonteMemEmul.setFont(Configuracao.fonteBotao2);      // font
    botaoFonteMemEmul.addActionListener(new ActionListener() { 
         public void actionPerformed(ActionEvent ev) { trataEdicao(0); }}); // 0 => Memória em Emulador

    botaoFonteCodEmul.setBounds(new Rectangle( xEdFt+largLabel+largEdFt+10, yEdFt,  largEdFt, altEdFt));// o "10" é 
    botaoFonteCodEmul.setFont(Configuracao.fonteBotao2);                                                // espaço
    botaoFonteCodEmul.setBackground(Configuracao.corFundo1);  // cor de fundo do botão botaoCompilador
    botaoFonteCodEmul.setForeground(Color.white);             // 
    botaoFonteCodEmul.addActionListener(new ActionListener() { 
         public void actionPerformed(ActionEvent ev) { trataEdicao(1); }}); // 1 => Código em Emulador

    // Compilador ---
    labelEdicaoFonte2.setBounds(new Rectangle( xEdFtC, yEdFtC,  largLabel, altEdFtC));// 81, 28
    labelEdicaoFonte2.setFont(Configuracao.fonteBotao2); //
    labelEdicaoFonte2.setBackground(Color.white);
    labelEdicaoFonte2.setForeground(Color.black);

    botaoFonteComp.setBounds(new Rectangle( xEdFtC+largLabel,  yEdFtC,  largEdFtC, altEdFtC)); //
    botaoFonteComp.setFont(Configuracao.fonteBotao2); //
    botaoFonteComp.setBackground(Configuracao.corFundo1);  // cor de fundo do botão botaoCompilador
    botaoFonteComp.setForeground(Color.white);             // 
    botaoFonteComp.addActionListener(new ActionListener() { 
         public void actionPerformed(ActionEvent ev) { trataEdicao(2); }}); // 2 => Código Compilador

    if (botaoEmulador.isEnabled()) { // só carrega botões associados ao emulador
       emulatorMainPanel.add(labelEdicaoFonte0, null);
       emulatorMainPanel.add(botaoFonteMemEmul, null);
       emulatorMainPanel.add(botaoFonteCodEmul, null);
       }

    if (ehExercicio) { // Bad interface solution: it is necessary a detachable frame with informations like 'statement'
       // Para enunciado de exercício ("labelEnunGabarito")
       // labelEnunGabarito = new Label("Exercício: "+str_paramEnunGabarito);
       // labelEnunGabarito.setBounds(new Rectangle( xGabEnun,  yGabEnun,  lGabEnun, aGabEnun));//
       // labelEnunGabarito.setForeground(Color.white);
       // emulatorMainPanel.add(labelEnunGabarito, null);
       }

    painelCompilador.add(labelEdicaoFonte2, null);
    painelCompilador.add(botaoFonteComp, null);

    //A Try to remove panel???
    //A if use option under '//A' in 'setBotaoEmulador()' and 'setBotaoCompilador()', then comment both 'this.add(*)' bellow
    // See: 'setBotaoEmulador()' and 'setBotaoCompilador()'
    this.add(emulatorMainPanel, null);
    this.add(painelCompilador, null); //- 03/06/2012 se entra default, nao precisa fazer 'add' de Compilador aqui
    //A leave to 'setBotaoCompilador'() and 'setBotaoEmulador()' add each panel

    buildHelpWindows(); // build the 2 static windows for help to the Emulator and to the Compiler

    // if (allowLoadCompiler)
    // If the file or page has 'tag' "<code>*</code>", load the student programa in the Compiler panel
    if (str_answer_code!=null && str_answer_code.length()>0)
       painelCompilador.setSourceCode(str_answer_code);  // from 'tagCode'

    } // private void iniciaCG()


  void trataEdicao (int tipo) { // 0: memória;  1: código
    frameEdicaoFonte = new Frame(Bundle.msg("editionOfFont")); // "Edição de fontes"
    String strModelFont = Bundle.msg("modelOfFont"); // "O tipo da fonte será como neste exemplo (012345;ãáóôç...)"
    if (tipo==2) // é Compilador
       frameEdicaoFonte.add(new icg.ig.PainelCorFonte(painelCompilador,tipo, strModelFont));
    else         // é Emulador
       frameEdicaoFonte.add(new icg.ig.PainelCorFonte(emulatorBaseClass.getEmulatorMainPanel(),tipo, strModelFont));
    frameEdicaoFonte.setVisible(true);
    frameEdicaoFonte.setSize(350,140); // DIMENSOES: deixar uma constante e permitir escalonamento?
    frameEdicaoFonte.addWindowListener( new WindowAdapter() {
       public void windowClosing(WindowEvent e) { frameEdicaoFonte.dispose(); }
       });
    }


  // Start the applet
  public void start() {
    }

  // Stop the applet
  public void stop() {
    }

  // Destroy the applet
  public void destroy() {
    }

  /*
   * Help windows to Emulator and Compiler 
   */
  private static String [] 
     vecHelpEmulator, // help messages to the Emulator
     vecHelpCompiler, // help messages to the Compiler
     vecAboutICG;     // about the iCG

  private static JanelaAjuda janelaAjudaEmulator, janelaAjudaCompiler, janelaAboutICG;

  private static void buildHelpWindows () {
    vecHelpEmulator = new String [] { // 18
       Bundle.msg("ajudaEmul1"),  Bundle.msg("ajudaEmul2"),  Bundle.msg("ajudaEmul3"),  Bundle.msg("ajudaEmul4"), 
       Bundle.msg("ajudaEmul5"),  Bundle.msg("ajudaEmul6"),  Bundle.msg("ajudaEmul7"),  Bundle.msg("ajudaEmul8"), 
       Bundle.msg("ajudaEmul9"),  Bundle.msg("ajudaEmul10"), Bundle.msg("ajudaEmul11"), Bundle.msg("ajudaEmul12"), 
       Bundle.msg("ajudaEmul13"), Bundle.msg("ajudaEmul14"), Bundle.msg("ajudaEmul15"), Bundle.msg("ajudaEmul16"), 
       Bundle.msg("ajudaEmul17"), Bundle.msg("ajudaEmul18")
       };
    vecHelpCompiler = new String [] { // 10
       Bundle.msg("ajudaComp1"), Bundle.msg("ajudaComp2"), Bundle.msg("ajudaComp3"), Bundle.msg("ajudaComp4"),
       Bundle.msg("ajudaComp5"), Bundle.msg("ajudaComp6"), Bundle.msg("ajudaComp7"), Bundle.msg("ajudaComp8"),
       Bundle.msg("ajudaComp9"), Bundle.msg("ajudaComp10")
       };
    vecAboutICG = new String [] { // 6
       Bundle.msg("msgSobre1"), 
       Bundle.msgComVar(Bundle.msg("msgVersao"),"OBJ", new String [] {Configuracao.versao, Configuracao.data}),
       Bundle.msg("msgSobre2"), Bundle.msg("msgSobre3"), Bundle.msg("msgSobre4"),
       Bundle.msg("msgSobre5")
       };
    janelaAjudaEmulator = new JanelaAjuda(Bundle.msg("msgAjudaEmul"), vecHelpEmulator, false);
    janelaAjudaCompiler = new JanelaAjuda(Bundle.msg("msgAjudaComp"), vecHelpCompiler, false);
    janelaAboutICG = new JanelaAjuda(Bundle.msg("buttonAbout"), vecAboutICG, false);
    }

  // Chamado em: icg.ig.Botao
  public void acaoAjuda () {
    int tipoEouC = 1;
    // if (ehEmulador) tipoEouC = 0;
    // JanelaAjuda.montaJanela(tipoEouC,this); // 0 => Emulador; 1 => Compilador
    if (ehEmulador)
       janelaAjudaEmulator.setVisible(true);
    else
       janelaAjudaCompiler.setVisible(true);
    //JanelaAjuda.janelaAjuda.botaoOK().requestFocus(); // para que usuário não precise clicar p/ ganhar foco
    }

  // Chamado em: icg.ig.Botao - click sobre 'botaoSobre'
  public void acaoSobre () {
    janelaAboutICG.setVisible(true); // icg.ig.JanelaAjuda
    //JanelaAjuda.janelaAjuda.montaJanela(2,this); // 0 => Emulador; 1 => Compilador; 2 => Sobre
    //JanelaAjuda.janelaAjuda.botaoOK().requestFocus(); // para que usuário não precise clicar p/ ganhar foco
    }

  // Chamado em: icg.ig.Botao - clique sobre 'botaoCompila'
  // Chama: icg.compilador.CompilerPanel.acaoCompila()
  public void acaoCompila () {
    painelCompilador.acaoCompila();
    }

  // Get parameter info
  // public String[][] getParameterInfo () { String[][] pinfo = { {"paramPrograma", "String", ""} , }; return pinfo; }


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


  //_   // 
  //_   public void paint (Graphics gr) {
  //_     // try { String str00="";System.out.println(str00.charAt(3)); } catch (Exception e) { e.printStackTrace(); }
  //_     Dimension dimension;
  //_     int       l=1; // largura das linhas de botões
  //_     try {
  //_       dimension = this.getSize(); // size()
  //_       int posX = iCG.xEmul, posY = iCG.yEmul; // posição do botaoComp
  //_       //T Dimension dim = textUserInput.getSize();
  //_       //T System.out.println("iCG.paint(): width="+dimension.width+", "+dimension.height);
  //_       // for (int i__=0; i__<botoes.length; i__++) {
  //_       //   botoes[i__].setVisible(true);
  //_       //   botoes[i__].pinta();
  //_       //System.out.println(" - " + i__ + ": " + botoes[i__].getSize());
  //_       //   }
  //_       //---- DB
  //_       if (offscreen == null) {
  //_          // double buffering techniche
  //_          offscreen = createImage(dimension.width, dimension.height);
  //_          }
  //_       offgraphics = offscreen.getGraphics(); //H
  //_       gr = offscreen.getGraphics(); // pega último buffer "gráfico"
  //_       //---- DB
  //_     } catch (Exception ex) {
  //_       System.err.println("[iCG.paint()] Error: " + ex.toString());
  //_       }
  //_     //---- DB
  //_     copy2DoubleBuffer(this.getGraphics()); // copy to 'offScreen' and draw the image
  //_     } // paint()



  // Main method
  public static void main (String[] args) {
    // if there are parameters like "lang=BR" it implies iCG load the corresponding 'Messages*.properties' file
    Bundle.setConfig(args);

    // load de Messges (in the correct language)
    Bundle.loadMessages();

    appletICG = new iCG();
    Frame frame;
    frame = new Frame();

    frame.addWindowListener(new WindowAdapter() {
           public void windowClosing(WindowEvent evt) { System.exit(0); }
           } );

    frame.setTitle("iCG :: http://www.matematica.br/icg");
    frame.add(appletICG, BorderLayout.CENTER);
    appletICG.ehApplet = false; // 
    TrataImage.eh_applet(false); //

    try {
      appletICG.iniciaCG();
    } catch (Exception e) {
      System.err.println("Erro: iCG: problema ao iniciar o iCG...");
      e.printStackTrace();  
      }

    // All panels built, then try to load a file under 'command line'
    if (args!=null && args.length>0) { // read file from command line
       // System.err.println("[iCG.main] #args=" + args.length + "\n" + Arquivos.readFileFromArg(args));
       String strContent = Arquivos.readFileFromArg(args); //
       if (strContent!=null) {
          // iCGproperties = icg.io.Arquivos.getProperties(strContent); // get all iCG itens: Statement, Type, Hint, Template, ...
          appletICG.loadICGdata(strContent); // load values data
          appletICG.setAllContents(); // load variables
          }
       else {
          System.err.println("Error: I couldn't find a valid iCG in the command line...");
          }
       }

    frame.setSize(Configuracao.WIDTH, Configuracao.HEIGTH);

    Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
    frame.setLocation( (d.width - frame.getSize().width) / 2,
                      (d.height - frame.getSize().height) / 2);
    frame.setVisible(true);
    } // public static void main(String[] args)

  }
