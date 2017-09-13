/**
 * 
 * iMath - http://www.matematica.br
 * 
 * <p>Title: iCG - Interactive Computer (Computador Gaveteiro Interativo)</p>
 * 
 * <p>Description: thi iCG Emulator (run the "binary" code of iCG)</p>
 *                 configurantions constants to color and fonts
 *                 the graphical components are in EmulatorMainPanel
 * 
 * <p>Copyright: Copyleft (c) 2003</p>
 * <p>Company: LInE - http://www.ime.usp.br/line</p>
 * 
 * @author Le�nidas de Oliveira Brand�o, Heitor, Newton, Paulo

 * @version 1.0: 2012-05-21 (identation and comments); 2008-10-02 (an import); 2006-04-02 (first version from an undergradeate course MAC323 - in 2003)
 * 
 * @see icg.emulador.EmulatorMainPanel; icg.emulador.Epi; icg.emulador.EmulatorCelMemory
 * 
 **/

package icg.emulador;

import java.util.StringTokenizer;
import java.awt.*;
import java.awt.event.*;

import icg.configuracoes.Configuracao;
import icg.iCG;
import icg.msg.Bundle;
import icg.ig.Botao;
import icg.ig.JanelaDialogo;
import icg.io.Arquivos;
import icg.util.ListaLigada;

public class EmulatorBaseClass {

  public static boolean erroPara = false; // s� p/ testes, p/ abortar la�o em "execInst()" se der erro em "case 8"

  boolean listaPassos = true; // para listar na tela cada instru��o executada, com println

  boolean ehApplet = false; // isStandalone = false;

  // Allow to load buttons 'botaoCompilador, botaoGabarito, botaoEnviar
  private boolean allowLoadCompiler, loadButtonBuildExerc, loadButtonExercEvaluate;

  boolean inserindo = false;
  boolean ativo = false;
  boolean passoapasso;
  String paramProgram  = "";  // se for applet, poder� conter o programa (c�digo de m�quina)
  // paramEmulador = "";  // se for "emulador", ent�o carregar� apenas o emulador

  // Componente exclusivos de aplicativo
  Button buttonOpenFile = new Button();
  Button buttonSaveFile = new Button();
  TextField textFileName2Open = new TextField();

  int posMemX,  posMemY;      // posi��o de mem�ria em execu��o ("reseta()", "zera" estas vari�veis)

  StringTokenizer st;
  int [] inst = new int[4];

  EmulatorCelMemory[][] memoria = new EmulatorCelMemory[10][10];  // as 100 posi��es de mem�ria: de 00 a 99
  EmulatorCelMemory acumulador = new EmulatorCelMemory("000000"); // acumulador

  int numInstrPrograma = 0;   // n�mero de instru��es v�lidas no programa (utilizado por exemplo, p/ definir in�cio da "pilha de execu��o")

  public Epi epi = new Epi(); // acesso: Emulador_panel!emular_actionPerformed(ActionEvent e)

  int instExecucao = 1;

  iCG icgPrincipal; // reference to the initial class 'icg.iCG'

  EmulatorMainPanel emulatorMainPanel; // graphical complements for iCG Emulator

  Botao botoes[]; // para bot�es vindo de iCG

  // From: icg.iCG.actionUpdate()
  // public EmulatorMainPanel getEmulatorMainPanel () { return emulatorMainPanel;} - abaixo substituindo o antigo 'Panel getPanel(): return emulatorMainPanel'
  // From: icg.iCG.acaoEnviar()
  public String getTextInputExecCode () { return emulatorMainPanel.getTextInputExecCode(); }

  // Construct the Panel
  // Botao botoes[] tem que ser passado para EmulatorBaseClass -> EmulatorMainPanel na ordem:
  //       botaoGabarito=botoes[0], botaoRoda=botoes[1], botaoRodaPP=botoes[2], botaoAtualiza=botoes[3]
  //       botaoInfo=botoes[4], botaoAjuda=botoes[5], botaoCompila = botoes[6]
  public EmulatorBaseClass (iCG icgPrincipal, Botao botoes[], String paramProgram, boolean ehApplet,
                            boolean allowLoadCompiler, boolean loadButtonBuildExerc, boolean loadButtonExercEvaluate) {
    //- System.out.println("[icg.emulador.EmulatorBaseClass] paramProgram="+paramProgram);
    this.icgPrincipal = icgPrincipal; //L 
    this.botoes = botoes;
    this.paramProgram = paramProgram;
    this.ehApplet = ehApplet; // isStandalone;
    this.allowLoadCompiler = allowLoadCompiler; // botaoCompilador -  if true => allow the button to load iCG Compiler
    this.loadButtonBuildExerc = loadButtonBuildExerc; // botaoGabarito
    this.loadButtonExercEvaluate = loadButtonExercEvaluate; // botaoEnviar

    String codigoProgram = "";

    try {
       // botaoGabarito=botoes[0], botaoRoda=botoes[1], botaoRodaPP=botoes[2], botaoAtualiza=botoes[3]
       emulatorMainPanel = new EmulatorMainPanel(this, botoes, allowLoadCompiler, loadButtonBuildExerc, loadButtonExercEvaluate);
       }
    catch (Exception e) {
       System.err.println("Erro: EmulatorBaseClass.java: problema ao tentar criar o painel principal...");
       e.printStackTrace();
       }

    try {
       if (ehApplet && paramProgram!=null) { // <- era 'isStandalone'
          st = new StringTokenizer(paramProgram, " ", false);
          if (st!=null) codigoProgram = montaProgramaInicial(st);
          }
       }
    catch (Exception e) {
       System.err.println("Erro: EmulatorBaseClass.java: em Applet, problemas na montagem inicial do programa");
       e.printStackTrace();
       }

    try  {

       // Inicia a caixa de c�digo de programa (� direita): ao clicar em "Atualizar" seu conte�do vai para a mem�ria
       if (codigoProgram == "") emulatorMainPanel.setTextInputExecCode(" ");
       else emulatorMainPanel.setTextInputExecCode(codigoProgram);

       // Trata entrada de dados: bot�o "OK"
       emulatorMainPanel.getBotaoOk().addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(ActionEvent e) {
           ok_actionPerformed(e);
           }
         });
       emulatorMainPanel.setEnabledTextUserInput(false);
       emulatorMainPanel.setEnabledBotaoOk(false); // desabilita o bot�o "OK"

       textFileName2Open.setBounds(new Rectangle(iCG.xEdFt+iCG.largLabel+4*iCG.largEdFt+10,iCG.yEdFt, 2*iCG.largEdFt,30));
       textFileName2Open.setBackground(Color.white);  // cor de fundo do bot�o botaoCompilador
       textFileName2Open.setFont(Configuracao.fonteBotao2); // fonte

       buttonSaveFile.setLabel(Bundle.msg("emulStore")); // "Gravar"
       buttonSaveFile.setBounds(new Rectangle(iCG.xEdFt+iCG.largLabel+2*iCG.largEdFt+10,iCG.yEdFt, iCG.largEdFt,iCG.altEdFt));//
       buttonSaveFile.setBackground(Configuracao.corFundo1);  // cor de fundo do bot�o botaoCompilador
       buttonSaveFile.setForeground(Color.white);             // 
       buttonSaveFile.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(ActionEvent e) {
           actionPerforStoreFileSession(e);
           }
         });
       buttonOpenFile.setLabel(Bundle.msg("emulOpen")); // "Abrir"
       buttonOpenFile.setBounds(new Rectangle(iCG.xEdFt+iCG.largLabel+3*iCG.largEdFt+10,iCG.yEdFt, iCG.largEdFt,iCG.altEdFt));//
       buttonOpenFile.setBackground(Configuracao.corFundo1);  // cor de fundo do bot�o botaoCompilador
       buttonOpenFile.setForeground(Color.white);             // 
       buttonOpenFile.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(ActionEvent e) {
           actionPerforOpenFileSession(e);
           }
         });

       emulatorMainPanel.setLabelAcumValue("<" + Bundle.msg("emulValueAC") + ">"); // "valor AC" - to be used to show AC value
       emulatorMainPanel.setLabelOutPut("<" + Bundle.msg("emulOutput") + ">");     // "sa�da" - label para texto de sa�da
       emulatorMainPanel.setFont(Configuracao.fonteBotao2); // font

       if (!ehApplet) { // <- era 'isStandalone'
          emulatorMainPanel.add(buttonSaveFile, null);
          emulatorMainPanel.add(buttonOpenFile, null);
          emulatorMainPanel.add(textFileName2Open, null);
          }

       emulatorMainPanel.painelInferior.add(emulatorMainPanel.getLabelOutPut(), null); // emulatorMainPanel.setLabelOutPut("<sa�da>") -> label para texto de sa�da

       }
    catch (Exception e) {
       System.err.println("Erro: Emulador via Applet: problemas na contru��o do painel!");
       e.printStackTrace();
       }

    try  {
       criaPanel(); // monta painel com matriz 10x10 representando a memoria
       }
    catch (Exception e) {
       System.err.println("Erro: Emulador via applet: problemas ao tentar criar o painel!");
       e.printStackTrace();
       }

    } // EmulatorBaseClass(iCG icgPrincipal, Botao botoes[], String paramProgram, boolean ehApplet)


  // Internacionalization
  void changeI18Texts () {
     buttonSaveFile.setLabel(Bundle.msg("emulStore")); // "Gravar"
     buttonOpenFile.setLabel(Bundle.msg("emulOpen")); // "Abrir"
     }
   

  // From: icg.iCG.setBotaoEmulador()
  public EmulatorMainPanel getEmulatorMainPanel () { // 03/06/2012 'Panel getPanel()'
    return (emulatorMainPanel);
    }


  // Set the "object code" in Emulator code area
  // From: icg.iCG.setBotaoEmulador()
  public void setCodigo (String strCode) {
    emulatorMainPanel.setTextInputExecCode( strCode );
    // Used in: 'acaoEnviar()' and 'getEvaluation()'
    // Do not mark 'iCG.codeEdited=true', since it comes here from load a file...
    // if (strCode!=null && strCode.trim().length()>0)
    //   this.icgPrincipal.setCodeEdited(); // student does editition in the programm
    // System.out.println("[EmulatorBaseClass!setCodigo] " + strCode);
    // try { String str00="";System.out.println(str00.charAt(3)); } catch (Exception e) { e.printStackTrace(); }
    }

  //
  public String getCodigo () {
    return emulatorMainPanel.getTextInputExecCode();
    }

  // Para lista conte�do da mem�rio
  public void listaMemoria () {
    System.out.println("[EmulatorBaseClass!listaMemoria]: ");
    String str;
    int i, j;
    for (i = 0; i < 10; i++) {
      for (j = 0; j < 10; j++) {
          str = memoria[i][j].conteudo;
          for (int k=str.length(); k<6; k++) str+=" ";
          System.out.print(" "+str);
          }
      System.out.println();
      }
    // System.out.println("AC = "+emulatorMainPanel.labelAcumValueOD.getText()+" EPI = "+epi.getX()+epi.getY()); //
    System.out.println("AC = "+acumulador.getValor()+" EPI = "+epi.getX()+epi.getY()); //
    System.out.println(" --- ");
    }


  // Called from: 'icg.iCG.acaoEnviar()'
  public boolean hasCodeInMemory () { // atualizaMemoria(String)
    // if position (0,0) has "000000" this means that the memory is empty or the first instructions is STOP
    return (memoria[0][0] != null && !memoria[0][0].getText().trim().equalsIgnoreCase("000000"));
    }


  // Atualiza mem�ria de acordo com texto digitado no "EmulatorMainPanel.txtExecucao"
  // - Em: EmulatorMainPanel.emular_actionPerformed(...)" -> icg/emulador/atualiza(String codObjeto) -> atualizaMemoria(String)
  public void atualizaMemoria (String texto) {
    String separadores = " \n" + (char)13;
    StringTokenizer stAM = new StringTokenizer(texto, separadores, false); // " \n"
    // System.out.println("icg/emulador/EmulatorBaseClass: ");
    numInstrPrograma = 0;

    epi.setXY(0,0); // passa para a primeira posi��o de mem�ria     
    int i, j;
    for (i=0; i<10; i++) {
      for (j=0; j<10; j++) {
          memoria[i][j].setText("000000"); // inicialmente, zere valor
          memoria[i][j].setBackground(Configuracao.corFrente1);

          if (stAM!=null && stAM.hasMoreTokens()) { // has item to the memory?

             String strInstr = stAM.nextToken();
             try {
               memoria[i][j].setText(strInstr);
               memoria[i][j].setBackground(Configuracao.corFundoMem2);//corAzulClaro); //
               numInstrPrograma++;
               if (strInstr.equals("000")) { // final de programa para
                  String msgAux = Bundle.msg("emulLoadedInstr") + " " + numInstrPrograma + Bundle.msg("emulinstructions"); // "foram carregadas "+numInstrPrograma+" instru��es"
                  emulatorMainPanel.setLabelInstrValue(msgAux);
                  for (j++; j<10; j++) // completa linha atual
                      memoria[i][j].setBackground(Configuracao.corFrente1); //

                  // Complete with blank and return
                  for (i++ ; i<10; i++)
                      for (j=0; j<10; j++) {
                          //System.out.print("("+i+","+j+") ");
                          memoria[i][j].setText("000000"); // inicialmente, zere valor
		          memoria[i][j].setBackground(Configuracao.corFrente1); //
                          }
                  return;

                  }
             } catch (Exception expt) {
               expt.printStackTrace();
               String errorMsg = Bundle.msg("emulInvalidInstruction") + " ("+strInstr+")";
               emulatorMainPanel.setLabelInstrValue(errorMsg); // "instru��o inv�lido ("+strInstr+")");
               System.err.println("Error in EmulatorBaseClass: " + errorMsg); // "[emul/EA]: atualiza��o de mem�ria: instru��o inv�lido ("+strInstr+")");
               }
             //System.out.print(" "+memoria[i][j].conteudo);

             } // if (stAM!=null && stAM.hasMoreTokens())

          } // for (j=0; j<10; j++)
      //System.out.println();
      } // for (i=0; i<10; i++)

    } //  void atualizaMemoria(String texto)


  // Constr�i string com c�digo vindo em "st", coloca mudan�a de linha (para o "emulatorMainPanel.txtExecucao")
  private String montaProgramaInicial (StringTokenizer st) {
    String codProg = "";
    while (st.hasMoreTokens()) { // faltava verificar se st!=null
      codProg += st.nextToken() + "\n";
      }
    return codProg;
    }


  // Monta as celulas de memoria em uma matriz 10 x 10
  public void criaPanel () {
    emulatorMainPanel.painelMemoria.removeAll();
    int pos = 0;
    int i, j;
    for (i = 0; i < 10; i++) {
      for (j = 0; j < 10; j++) {
        // Agora s� carrega o programa, via par�metro (ou arquivo?), na �rea de c�digo (emulatorMainPanel.txtExecucao), n�o na mem�ria
        // vide montaProgramaInicial()
        //if (st!=null && st.hasMoreTokens()) { // faltava verificar se st!=null
        //   memoria[i][j] = new Memoria(st.nextToken());
        //}
        //else {
           memoria[i][j] = new EmulatorCelMemory("000000");
        // }
        //modifica��o de estado
        //fica feio com essa opcao no linux
        //e funciona da mesma forma
        //memoria[i][j].setEnabled(false);
        memoria[i][j].setForeground(Configuracao.corFrente2);// Configuracao.corFundo3); //Color.black);
        memoria[i][j].setFont(Configuracao.fonteCodigo); // para caber 
        if (memoria[i][j].valor != 0) {
          if (instExecucao == 1) {
             memoria[i][j].setBackground(Configuracao.corFundoMem1); // (Color.orange);
             }
          else {
             memoria[i][j].setBackground(Configuracao.corFundoMem2); // (Color.green);
             }
          }
        else {
          instExecucao = 0;
          memoria[i][j].setBackground(Configuracao.corFrente1); //Color.white);
          }
        emulatorMainPanel.painelMemoria.add(memoria[i][j], null);
        }
   
      }
    epi.setXY(0, 0);
    memoria[epi.getX()][epi.getY()].setBackground(Configuracao.corAzulClaro); //Color.blue);
    }

  //-- Problema: como interromper em caso de 'loop infinito' ?
  // Chamado em: icg.ig.Botao
  public void acaoEmular () {
    // int i=epi.getX(), j=epi.getY();
    // System.out.println("[EmulatorBaseClass!ok_actionPerformed] (i,j)=("+i+","+j+") e="+e);
    epi.setXY(0, 0);
    ativo = true;
    iCG.testandoGabarito = false; // para garantir n�o pegar dados de gabarito
    execInst(false);
    }


  // Coloca na mem�ria o n�mero digitado ap�s clicar me "OK"
  // Chamado em: tamb�m � invocado em "EmulatorMainPanel.montaPainelPrincipal()", ao dar um ENTER no
  //             "textInput"
  void ok_actionPerformed (ActionEvent e) {
    if (!inserindo) {
       String strEntrada = "-";
       emulatorMainPanel.setEnabledTextUserInput(false);
       emulatorMainPanel.setEnabledBotaoOk(false);
       emulatorMainPanel.botaoRoda.setEnabled(true);
       emulatorMainPanel.botaoRodaPP.setEnabled(true);
       int i = posMemX, j = posMemY;
       //System.out.println("[EmulatorBaseClass!ok_actionPerformed] (i,j)=("+i+","+j+")");
       try {
         strEntrada = emulatorMainPanel.getTextUserInput();
         //iCG.addListaGabEntradas(strEntrada); //).add(strEntrada); // lista para seq. de entradas em exercicios
         memoria[i][j].setText(strEntrada);
         }
       catch (NumberFormatException a) {
         emulatorMainPanel.setLabelInstrValue(Bundle.msg("emulInvalidNum")); // "n�mero inv�lido"
         }
       emulatorMainPanel.setTextUserInput(" ");
       memoria[i][j].setBackground(Configuracao.corFundoMem1); //(Color.magenta);
       System.out.println("[EmulatorBaseClass!ok_actionPerformed(ActionEvent)] "+strEntrada);
       }
    else {
       try {
         //System.out.println("[EmulatorBaseClass!ok_actionPerformed] (inst1,inst2)=("+inst[1]+","+inst[2]+")");
         String strEntrada = emulatorMainPanel.getTextUserInput();
         if (iCG.ehGeraGabarito) {
            //-System.out.println("[EmulatorBaseClass!ok_actionPerformed] <inserindo> entrada: "+strEntrada);
            iCG.addListaGabEntradas(strEntrada); //).add(strEntrada); // lista para seq. de entradas em exercicios
            }
         //iCG.addListaGabEntradas(strEntrada); //).add(strEntrada); // lista para seq. de entradas em exercicios

         memoria[inst[1]][inst[2]].setText(strEntrada); //emulatorMainPanel.getTextInput());
         memoria[inst[1]][inst[2]].setBackground(Configuracao.corFundoMem3); // (Color.red);
         }
       catch (NumberFormatException a) {
         emulatorMainPanel.setLabelInstrValue(Bundle.msg("emulInvalidNum")); // "n�mero inv�lido"
         }
       emulatorMainPanel.botaoRoda.setEnabled(true);
       emulatorMainPanel.botaoRodaPP.setEnabled(true);
       emulatorMainPanel.setTextUserInput(" ");
       emulatorMainPanel.setEnabledTextUserInput(false);
       emulatorMainPanel.setEnabledBotaoOk(false);

       //-if (epi.getY() == 9) {
       //-  memoria[epi.getX()][epi.getY()].setBackground(Configuracao.corFundoMem1); //(Color.magenta);
       //-  epi.setXY(epi.getX() + 1, 0);
       //-  memoria[epi.getX()][epi.getY()].setBackground(corFundoMemAtual); // cor de fundo para instru��o atual
       //-}
       //-else {
       //-  memoria[epi.getX()][epi.getY()].setBackground(Configuracao.corFundoMem1); //(Color.magenta);
       //-  epi.setXY(epi.getX(), epi.getY() + 1);
       //-  memoria[epi.getX()][epi.getY()].setBackground(corFundoMemAtual); // cor de fundo para instru��o atual
       //-}

       emulatorMainPanel.setLabelInstrComm("<- " + Bundle.msg("emulAboutCommand")); // "sobre comandos"

       if (!passoapasso) {
          execInst(false);
          }
       }
    } // void ok_actionPerformed(ActionEvent e)


  // Chamado em: this.acaoEmularPP (estava em EmulatorMainPanel!emular_actionPerformed(ActionEvent e))
  //             this!emular_actionPerformed(ActionEvent e)
  public void execInst (boolean passoapasso) {
    this.passoapasso = passoapasso;
    boolean finaliza = false;
    boolean acresepi = true; // a menos de desvios, "6EE" ou "9EE", fa�a ap�s execu��o da instru��o "EPI <- EPI + 1"
    int epiX, epiY; // para armazenar o endere�o da instru��o em execu��o (obtido em "epi.getX()" e "epi.getY()")
    //Color corAnterior;

    //-System.out.print("\n\n[EmulatorBaseClass]: ");

    EmulatorMainPanel emulatorMainPanelTesting = this.emulatorMainPanel;  // truque para o caso de teste de exerc�cio

    EmulatorCelMemory[][] memoria   = this.memoria; // se n�o for teste de avalia��o, mantenha mem�ria real
    if (iCG.testandoGabarito) {	
       if (Configuracao.listaGabarito) {
          System.out.println("\n - EPI=("+epi.getX()+","+epi.getY()+")");
          }

       // Trick: since we use a single instance o 'botoes', a new 'EmulatorMainPanel' avoid the evaluation output in 'AC' or 'EPI'
       // Do not use 'botoes' since it could remove them from the visible panel of Emulator - don't use 'new ...(this, botoes, ...)'
       emulatorMainPanelTesting = new EmulatorMainPanel(this, null, allowLoadCompiler, loadButtonBuildExerc, loadButtonExercEvaluate);

       memoria = new EmulatorCelMemory[10][10];
       // Trick: it is also necessary to make a memory copy
       for (int i=0; i<10; i++) {
           for (int j=0; j<10; j++) {
               memoria[i][j] = this.memoria[i][j];
               }
           }
       }


    //-- Problema: como interromper em caso de 'loop infinito' ?
    // EmulatorMainPanel ! emular_actionPerformed(ActionEvent e) -> EmulatorBaseClass!emular_actionPerformed(e);
    // "epi.getX()==0" e "epi.getY()==5" e "e=java.awt.event.ActionEvent[ACTION_PERFORMED,cmd=Emular] on button0"
    //--1 String msgBotaoEmular = emulatorMainPanel.botaoRoda.getLabel();
    //--1 emulatorMainPanel.botaoRoda.setLabel("P�ra"); // pare execu��o
    //--1 emulatorMainPanel.botaoRoda.setEnabled(true);

    do {

      // Problema: como interromper em caso de 'loop infinito' ?
      //--2 try {Thread.sleep(5);}  // delay 5 msec between updates
      //--2 catch (InterruptedException e){};   // would catch the moue click

      inserindo = false;

      try {
        posMemX = epiX = epi.getX();
        posMemY = epiY = epi.getY();

        inst = memoria[epiX][epiY].getInstrucao(); // este � para pegar sem a resposta do aluno

        if (inst == null) {
           String msgAux = Bundle.msg("emulErrorInvInstr") + " \"memoria["+epiX+"]["+epiY+"]\"="+memoria[epiX][epiY]; // "N�o era instru��o v�lida! Em \"memoria["+epiX+"]["+epiY+"]\"="+memoria[epiX][epiY]
           emulatorMainPanelTesting.setLabelInstrValue(msgAux);
           System.out.println(msgAux);
           acresepi = true;
           finaliza = true;
           }
        else {
           //[29/08/2004] desativei, pois "acrescepi=true" sempre, a menos de instru��es "6EE" e "9EE" 

           int aux = 0;
           String strAC; // auxiliar para atualizar texto que lista valor do acumulador AC
           acresepi = true;
           try {
           switch (inst[0]) {

             case 0:
               //emulatorMainPanel.setLabelInstrValue("0 -> AC = cEE");
               emulatorMainPanelTesting.msgInstrucaoExecutada(epiX,epiY,"0 -> AC = cEE");

               if (inst[1] == 0 && inst[2] == 0) { // encontrou instru��o de final de programa
                 finaliza = true;
                 //memoria[epiX][epiY].setBackground(Color.white);
                 epi.setXY(0, 0);
                 //memoria[epiX][epiY].setBackground(Configuracao.corAzulClaro); //(Color.blue);
                 emulatorMainPanelTesting.setLabelInstrValue("<" + Bundle.msg("emulEndProgramm") + ">"); // final de programa
                 acresepi = false;

                 // Est� gerando gabarito a partir da emula��o, finalize mais um bloco
                 if (iCG.ehGeraGabarito) {
                    iCG.strGabEntradasSaidas(iCG.strGabEntradasSaidas() + iCG.montaGabaritoDosVetores());
                    this.reseta(); // 03/06/2012: estava no estatico 'iCG.motaGabaritoHTML(...)'
                    this.atualizaMemoria(this.getCodigo()); // 03/06/2012: estava no estatico 'iCG.motaGabaritoHTML(...)'
                    iCG.listaGabEntradas( new ListaLigada()); // come�a novamente...
                    iCG.listaGabSaidas(   new ListaLigada());
                    }
                 }
               else {
                 if (inst[1] == -1) { // Truque: 0-k => AC <- k : para atribuir constantes
                    strAC = memoria[epiX][epiY].getValor() + "";
                    acumulador.setText(strAC);
                    }
                 else
                 if (inst[1] == -2) { // Truque: 0*EE => AC <- c(cEE)K : para indire��o
                    // pega o valor da gaveta (epiX,epiY): endere�o EE
                    int end = memoria[epiX][epiY].getValor(), eX = end/10, eY = end%10;
                    end = memoria[eX][eY].getValor();         eX = end/10; eY = end%10;
                    // pega o valor da gaveta apontada por (eX,eY): endere�o eXeY
                    strAC = memoria[eX][eY].getValor() + ""; // memoria[eX][eY].getValor() + "";
                    acumulador.setText(strAC);
                    }
                 else {
                    strAC = memoria[inst[1]][inst[2]].getValor() + "";
                    acumulador.setText(strAC);
                    }
	      
                 // N�o, "labelSaida" ser� apenas para comando "8ee"
                 // emulatorMainPanel.setLabelOutPut(acumulador.getValor() + ""); // emulatorMainPanel.setLabelOutPut("<sa�da>") � label para texto de sa�da
                 emulatorMainPanelTesting.setLabelAcumValue(strAC); // emulatorMainPanel.setLabelOutPut("<sa�da>") � label para texto de sa�da
                 }
               break;

             case 1:
               //emulatorMainPanel.setLabelInstrValue("1 -> EE = AC");
               emulatorMainPanelTesting.msgInstrucaoExecutada(epiX,epiY,"1 -> EE = AC");
               // Pode ser que EE esteja na "pilha de execu��o" (neste caso continuar� com o fundo branco...)
               if (inst[1] == -1) {
                  memoria[inst[2]][inst[3]].setText(memoria[epiX][epiY].getValor() + "");
                  }
               else
               if (inst[1] == -2) { // Truque: 0*EE => AC <- c(cEE)K : para indire��o
                  // pega o valor da gaveta (epiX,epiY): endere�o EE
                  int end = memoria[inst[2]][inst[3]].getValor(), eX = end/10, eY = end%10;;
                  memoria[eX][eY].setText(acumulador.getValor() + "");
                  }
               else {
                  memoria[inst[1]][inst[2]].setText(acumulador.getValor() + "");
                  }
               break;

             case 2:
               //obs caso passa do limite
               emulatorMainPanelTesting.msgInstrucaoExecutada(epiX,epiY,"2 -> AC = AC + cEE");
               aux = acumulador.getValor() + memoria[inst[1]][inst[2]].getValor();
               if (aux > 999999) {
                  aux = -99999 + (aux % 999999);
                  }
               else if (aux < -99999) {
                  aux = 999999 + (aux % 99999);
                  }
               acumulador.setText(aux + "");
               emulatorMainPanelTesting.setLabelAcumValue(aux+""); // � label para texto de sa�da
               break;

             case 3:
               //obs caso passa do limite
               emulatorMainPanelTesting.msgInstrucaoExecutada(epiX,epiY,"3 -> AC = AC - cEE");
               aux = acumulador.getValor() - memoria[inst[1]][inst[2]].getValor();
               if (aux > 999999) {
                  aux = -99999 + aux % 999999;
                  }
               else if (aux < -99999) {
                  aux = 999999 + aux % 99999;
                  }
               acumulador.setText(aux + "");
               emulatorMainPanelTesting.setLabelAcumValue(aux+""); // � label para texto de sa�da
               break;

             case 4:
               //obs caso passa do limite
               emulatorMainPanelTesting.msgInstrucaoExecutada(epiX,epiY,"4 -> AC = AC * cEE");
               aux = acumulador.getValor() * memoria[inst[1]][inst[2]].getValor();
               if (aux > 999999) {
                  aux = -99999 + aux % 999999;
                  }
               else if (aux < -99999) {
                  aux = 999999 + aux % 99999;
                  }
               acumulador.setText(aux + "");
               emulatorMainPanelTesting.setLabelAcumValue(aux+""); // � label para texto de sa�da
               break;

             case 5:
               //emulatorMainPanelTesting.setLabelInstrValue("5 -> AC = AC / cEE");
               emulatorMainPanelTesting.msgInstrucaoExecutada(epiX,epiY,"5 -> AC = AC / cEE");
               int divisor;
               divisor = memoria[inst[1]][inst[2]].getValor();
               if (divisor != 0) {
                  aux = acumulador.getValor() / divisor;
                  }
               else {
                  emulatorMainPanelTesting.setLabelInstrComm("Divis�o por zero");
                  System.out.println("Divis�o por zero");
                  if (iCG.testandoGabarito) erroPara = true;
                  finaliza = true; // aborta execu��o do programa!
                  }
               acumulador.setText(aux+"");
               emulatorMainPanelTesting.setLabelAcumValue(aux+""); // � label para texto de sa�da
               break;

             case 6:
               emulatorMainPanelTesting.msgInstrucaoExecutada(epiX,epiY,"6 -> cAC>0, EPI <- EE");
               if (acumulador.getValor() > 0) {
                  epi.setXY(inst[1], inst[2]);
                  acresepi = false; // n�o deixa acrescentar +1 ao EPI
                  }
               //else { // este j� � o "default"
               //   acresepi = true;
               //   }
               break;

             case 7: // O valor � pego de fato no "botaoOK", em "emulatorMainPanel.botaoOk"->"ok_actionPerformed(ActionEvent)"
                     // o n�m. digitado � inserido em "iCG.listaGabEntradas"
               emulatorMainPanelTesting.msgInstrucaoExecutada(epiX,epiY,"7 -> EE = leitura teclado");
               if (iCG.testandoGabarito) {

       	          // E se o algoritmo do aluno entrar em "loop" ?? 
       	          // precisa ao menos de um "finaliza = true;"
       	          // Mas como identificar ??

                  String str1 = "", //(String) iCG.vetEntradas[iCG.atualVetEntSai()].elementAt(iCG.iEnt()),
                         strEntrada = "";
                  int i = inst[1], j = inst[2]; // posMemX, j = posMemY;
                  try {
                    str1 = (String) iCG.vetEntradas[iCG.atualVetEntSai()].elementAt(iCG.iEnt());
                    if (Configuracao.listaGabarito) {
                       System.out.println("[EA!execInst] "+iCG.atualVetEntSai()+" --- entrada ---- Memoria["+i+","+j+"] <- "+
                                                           str1+"\nvetEntradas["+iCG.atualVetEntSai()+"]:");
                       }

                    try {
                      strEntrada = str1; // emulatorMainPanel.getTextInput();
                      memoria[i][j].setText(strEntrada);
                      }
                    catch (NumberFormatException a) {
                      emulatorMainPanelTesting.setLabelInstrValue("n�mero inv�lido");
                      }
                    }
                  catch (Exception e) {
                    System.out.println("[AP!exec] erro, iCG.vetEntradas[iCG.atualVetEntSai()]="+
                                       iCG.vetEntradas[iCG.atualVetEntSai()]+" iCG.iEnt()="+iCG.iEnt()); //
                    if (iCG.vetEntradas[iCG.atualVetEntSai()]!=null && iCG.vetEntradas[iCG.atualVetEntSai()].size()>=iCG.iEnt()) 
                       return;
                    }
                  iCG.iEntInc();
                  // Entrada: ---
                  }
	       else {
                  inserindo = true;
                  // acresepi = false;
                  emulatorMainPanelTesting.botaoRoda.setEnabled(false);
                  emulatorMainPanelTesting.botaoRodaPP.setEnabled(false);
                  emulatorMainPanelTesting.setEnabledTextUserInput(true); // makes 'emulatorMainPanelTesting.txtEntrada.requestFocus()' - para que usu�rio n�o precise clicar na TextField p/ ganhar foco
                  emulatorMainPanelTesting.setEnabledBotaoOk(true);
                  emulatorMainPanelTesting.setLabelInstrComm("<- digite um valor");
                  }
               break;

             case 8: // O valor de sa�da � inserido em "iCG.listaGabSaidas"
               // Saida: ---
               emulatorMainPanelTesting.msgInstrucaoExecutada(epiX,epiY,"8 -> saida = cEE");
               String strSaida = memoria[inst[1]][inst[2]].getText();
               emulatorMainPanelTesting.setLabelInstrComm("Sa�da ->"); //
               emulatorMainPanelTesting.setLabelOutPut(strSaida); // emulatorMainPanel.setLabelOutPut("<sa�da>") � label para texto de sa�da

               if (iCG.ehGeraGabarito) {
                  iCG.addListaGabSaidas(strSaida); //).add(strSaida);
                  }
               else
               if (iCG.testandoGabarito) {
                  String str1 = "";
                  iCG.numSaidasAlunoInc(); // incrementa de 1 o total de sa�das na resposta do aluno
                  try { // pega no atual vetor de sa�das a atual sa�da
                    str1 = (String) iCG.vetSaidas[iCG.atualVetEntSai()].elementAt(iCG.iSai());
                  } catch (java.lang.Exception e) {
                      // gabarito = "{ E: N N }, { S: N } { E: N N N }, { S: N N }"
                      System.out.println("[EA!exec. sa�das()] Erro no tratamento do vetor de sa�das: o gabarito do professor n�o previa esta sa�da ("+strSaida+")");
                      System.out.println("                    At� aqui foram "+iCG.numSaidasAluno()+" sa�das, o m�ximo era "+
                                         iCG.vetSaidas[iCG.atualVetEntSai()].size()+"!");

		      // Neste ponto poder�a colocar uma interrup��o do atual lote de teste, pois se a sa�da do aluno
                      // tem mais que a do professor, ent�o ERRO!
		      // Isso est� no iCG.

                      }
                  if (str1.equals(strSaida)) iCG.contAcertosSaiInc(); // 'strSaida': sa�da do aluno
                  else
                  if (Configuracao.listaGabarito) {
                     System.out.println("\n[EmulatorBaseClass] --- sa�das "+iCG.atualVetEntSai()+" diferentes: gab="+str1+" alu="+strSaida);
                     System.out.println("memoria["+inst[1]+"]["+inst[2]+"].getText() = "+memoria[inst[1]][inst[2]].getText());
                     //listaMemoria();
                     erroPara = true;
                     }

                  iCG.iSaiInc();
                  }
               break;

             case 9:
               emulatorMainPanelTesting.msgInstrucaoExecutada(epiX,epiY,"9 -> EPI = EE");
               epi.setXY(inst[1], inst[2]);
               acresepi = false; // n�o deixe somar +1 ao EPI
               break;
             }	

          // Terminou de executar, marque a cor da �ltima como "j� executada"
          // Se a cor de fundo atual � "Configuracao.corFundoMemAtual", ent�o pinte com "Configuracao.corAzulClaro"
          // Sen�o, fa�a o contr�rio
           Color corAtual = memoria[epiX][epiY].getBackground();
	   if (corAtual==Configuracao.corFundoMemAtual) { // Configuracao.corAzulClaro) {
              memoria[epiX][epiY].setBackground(Configuracao.corAzulClaro); // marca como "em execu��o"
              }
           else {
              memoria[epiX][epiY].setBackground(Configuracao.corFundoMemAtual); //corAzulClaro); // marca como "em execu��o"
              }

           } catch (java.lang.Exception e) {
              System.out.println("[EmulatorBaseClass!execInst(boolean)] inst="+inst+" mem�ria=("+epiX+","+epiY+"): " + e.toString());
              //e.printStackTrace();
              }

          if (acresepi) { // se precisa incrementar EPI
             if (epiY == 9) { // muda de linha na tabela de mem�ria (redefina EPI
                epi.setXY(epi.getX()+1, 0); // epi.setXY(epi.getX() + 1, 0);
                }
             else {
                epi.setXY(epi.getX(), epi.getY()+1); // epi.setXY(epi.getX(), epi.getY() + 1);
                }
             }

          } // desativei: if (!acresepi) {

      }
      catch (IndexOutOfBoundsException e) {
        finaliza = true;
        }
    }
    while (!passoapasso && !finaliza && !inserindo);

    //--1 emulatorMainPanel.botaoRoda.setLabel(msgBotaoEmular);
    //--1 emulatorMainPanel.botaoRoda.setEnabled(true);

    } // void execInst(boolean passoapasso)


  // Chamado em: EmulatorMainPanel.emular_actionPerformed(...)"
  public void atualiza (String codObjeto) {
    reseta();
    atualizaMemoria(codObjeto); // atualiza posi��es de mem�ria do emuladorApplet
    this.icgPrincipal.setCodeEdited(); // clearEvaluation(); // mark as 'not evaluated' ('icg.iCG.valorExercicio = -111;')
    }

  // Ap�s clicar em "Atualiza", deve-se fazer: epi.setXY(0,0); // passa para a primeira posi��o de mem�ria
  // em "EmulatorMainPanel.atualiza_actionPerformed(ActionEvent)"
  // Chamado em: icg.ig.Botao
  public void acaoEmularPP () {
    ativo = true;
    iCG.testandoGabarito = false; // para garantir n�o pegar dados de gabarito
    execInst(true);
    }

  // Register the iCG file
  // If extension is: 'icg' => save as *.icg; 'html' => save as *.html an *_icg2html.html
  private void actionPerforStoreFileSession (ActionEvent e) {
    // EmulatorInOutput.grava(memoria, textFileName2Open.getText());
    // String getSession (): Arquivos.getXML(this, iCGproperties); 
    String strContent = icgPrincipal.getSession();
    String fileName = textFileName2Open.getText(); // get the file name to be generated
    int resp = Arquivos.storeICGorHTML(strContent, fileName);
    String [] vetFileNames = Arquivos.getExtension(fileName);
    String strMsg = null, strError = null,
           fileFirstName = vetFileNames[0], fileExt = vetFileNames[1];

    switch (resp) { //
      case 1: // written with success
        strMsg = Bundle.msg("msgArqGravadoSucesso");
        break;
      case 0: // Impossible to write this file (permission problem? there is not "room enought"?)
        // I could not write a file named $OBJ$! It could be: permission problem, lack of space,... 
        strMsg = strError = Bundle.msgComVar("msgFileNotWrite","OBJ", new String [] { fileName });
        break;
      case -1: // There exist a file with this name
        // There is a file named $OBJ$! Please, choose another name
        strMsg = strError = Bundle.msgComVar("msgFilePreviousFile","OBJ", new String [] { fileName });
        break;
      case -2: // There exist a file with the name of the auxiliary file to the HTML
        // The HTML file '$OBJ$' was generated, but not its auxiliary! Attention, I used the previous auxiliary!
        strMsg = strError = Bundle.msgComVar("msgFilePreviousFileAux","OBJ", new String [] { fileName, fileFirstName+Arquivos.SUFIX_AUX_HTML });
        System.err.println("Attention: I am using the previous auxiliary "+fileFirstName+"."+fileExt+"\n"+
                           "Perhaps you should change its name, "+fileName+", or erase the old "+fileFirstName+"."+fileExt);
        break;
      case -3: // Empty name...
        // You should provide a name in order to store this session in a file
        strMsg = strError = Bundle.msg("msgFileNameStoreEmpty");
        break;
      case -4: //
        // The extension is invalid or empty
        strMsg = strError = Bundle.msgComVar("msgFileNameExtInvalid","OBJ", new String [] { fileName, fileExt });
        break;
      }
    if (strError!=null) {
       new JanelaDialogo(strError, null);
       }
    else if (fileExt.equalsIgnoreCase("html")) { // success in file HTML - advertise the user about the auxiliary file
       // Attention: the user must keep the auxiliary file 'fileFirstName_icg2html.icg' with the HTML file
       // Atenção: com o arquivo '$OBJ' é preciso estarem no mesmo diretório/pasta o 'iCG.jar' e o '$OBJ'
       String strMsg2 = Bundle.msgComVar("msgFileHTMLhasAux","OBJ", new String [] { fileName, fileFirstName+Arquivos.SUFIX_AUX_HTML });
       new JanelaDialogo(strMsg2, null); // Arquivos.SUFIX_AUX_HTML = "_icg2html.icg"
       }
    else {
       new JanelaDialogo(strMsg, null); // success in storage of an *.icg file
       }
     
    icgPrincipal.setMensagem(strMsg);
    System.out.println(strMsg);

    } // void actionPerforStoreFileSession(ActionEvent e)

  // try { String str00="";System.out.println(str00.charAt(3)); } catch (Exception e00) { e00.printStackTrace(); }

  // Click in the button 'abrir'
  private void actionPerforOpenFileSession (ActionEvent e) {
    boolean erro = false;
    String strContentFileICG = "<" + Bundle.msg("emulEmpty") + ">", strExerc; // "<vazio>"
    String fileName;
    int intFormat;
    reseta();
    try {
      fileName = textFileName2Open.getText().toString().trim();
      String strError = null;
      if (fileName==null || fileName.length()==0) { // You should provide a name in order to open a file
         strError = Bundle.msg("msgFileNameOpenEmpty");
         }
      else if (!Arquivos.isFile(fileName)) { // I am sorry, I could not find a file name '$OBJ$'
         strError = Bundle.msgComVar("msgFileNotFound","OBJ", new String [] { fileName });
         }
      if (strError!=null) {
         new JanelaDialogo(strError, null);
         return;
         }

      strContentFileICG = Arquivos.readFromFileDir(fileName,""); //
      if (strContentFileICG=="" || strContentFileICG.trim()=="") {
         strError = Bundle.msg("msgFileIsEmpty");
         new JanelaDialogo(strError, null);
         return;
         }
      intFormat = icgPrincipal.setProperties(strContentFileICG); // define all iCG itens: Statement, Type, Hint, Template, ...: 0 => OK; -1 => Error

      // To be implemented: must define a protocol for a single file with
      if (intFormat<0) { // it is not in iCG XML format, try for old version (only with the content for tag 'Template')
         strExerc = this.icgPrincipal.decodeTemplate(strContentFileICG);
         if (strExerc!=null ) // it is an exercise in old format
            System.err.println("[icg.emulador.actionPerforOpenFileSession] It is an exercise in old format, with answer template (presented bellow)\n");
         else
            System.err.println("[icg.emulador.actionPerforOpenFileSession] It is not an iCG file...\n"); // + strExerc
         if (iCG.testandoGabarito)
            System.err.println(strExerc);
         new JanelaDialogo(Bundle.msg("errFileNotiCG"), null);
         }

      System.out.println("[EmulatorBaseClass] open file " + fileName + ":\n" + strContentFileICG);
    } catch (Exception exc) {
      exc.printStackTrace();
      System.err.println("[EA] abrir: "+strContentFileICG+":"+exc);
      icgPrincipal.setMensagem(Bundle.msg("msgArqInvalido")); // 
      erro = true;
      }
    if (!erro) {
       // emulatorMainPanel.setTextInputExecCode(strContentFileICG); // coloca na �rea de c�digo para execu��o
       icgPrincipal.setAllContents(); // let 'icg.iCG' define all contents, in Emulator and in Compiler
       }
    } // void actionPerforOpenFileSession(ActionEvent e)

  public void reseta () {
    acumulador.setText("000000");
    ativo = false;
    emulatorMainPanel.setTextUserInput(""); // field to user input data
    emulatorMainPanel.setEnabledTextUserInput(false);

    emulatorMainPanel.botaoRoda.setEnabled(true);
    emulatorMainPanel.botaoRodaPP.setEnabled(true);

    epi.setXY(0, 0);
    posMemX = posMemY = 0;
    inserindo = false;
    emulatorMainPanel.setLabelInstrValue("");
    emulatorMainPanel.setLabelOutPut("<" + Bundle.msg("emulOutput") + ">"); // "<sa�da>" - label para texto de sa�da

    emulatorMainPanel.setEnabledBotaoOk(false);
    passoapasso = false;
    emulatorMainPanel.setLabelInstrComm("");
    } // public void reseta()


  } // class EmulatorBaseClass
