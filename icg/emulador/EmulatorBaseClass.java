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
 * @author Leônidas de Oliveira Brandão, Heitor, Newton, Paulo

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

  public static boolean erroPara = false; // só p/ testes, p/ abortar laço em "execInst()" se der erro em "case 8"

  boolean listaPassos = true; // para listar na tela cada instrução executada, com println

  boolean ehApplet = false; // isStandalone = false;

  // Allow to load buttons 'botaoCompilador, botaoGabarito, botaoEnviar
  private boolean allowLoadCompiler, loadButtonBuildExerc, loadButtonExercEvaluate;

  boolean inserindo = false;
  boolean ativo = false;
  boolean passoapasso;
  String paramProgram  = "";  // se for applet, poderá conter o programa (código de máquina)
  // paramEmulador = "";  // se for "emulador", então carregará apenas o emulador

  // Componente exclusivos de aplicativo
  Button buttonOpenFile = new Button();
  Button buttonSaveFile = new Button();
  TextField textFileName2Open = new TextField();

  int posMemX,  posMemY;      // posição de memória em execução ("reseta()", "zera" estas variáveis)

  StringTokenizer st;
  int [] inst = new int[4];

  EmulatorCelMemory[][] memoria = new EmulatorCelMemory[10][10];  // as 100 posições de memória: de 00 a 99
  EmulatorCelMemory acumulador = new EmulatorCelMemory("000000"); // acumulador

  int numInstrPrograma = 0;   // número de instruções válidas no programa (utilizado por exemplo, p/ definir início da "pilha de execução")

  public Epi epi = new Epi(); // acesso: Emulador_panel!emular_actionPerformed(ActionEvent e)

  int instExecucao = 1;

  iCG icgPrincipal; // reference to the initial class 'icg.iCG'

  EmulatorMainPanel emulatorMainPanel; // graphical complements for iCG Emulator

  Botao botoes[]; // para botões vindo de iCG

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

       // Inicia a caixa de código de programa (à direita): ao clicar em "Atualizar" seu conteúdo vai para a memória
       if (codigoProgram == "") emulatorMainPanel.setTextInputExecCode(" ");
       else emulatorMainPanel.setTextInputExecCode(codigoProgram);

       // Trata entrada de dados: botão "OK"
       emulatorMainPanel.getBotaoOk().addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(ActionEvent e) {
           ok_actionPerformed(e);
           }
         });
       emulatorMainPanel.setEnabledTextUserInput(false);
       emulatorMainPanel.setEnabledBotaoOk(false); // desabilita o botão "OK"

       textFileName2Open.setBounds(new Rectangle(iCG.xEdFt+iCG.largLabel+4*iCG.largEdFt+10,iCG.yEdFt, 2*iCG.largEdFt,30));
       textFileName2Open.setBackground(Color.white);  // cor de fundo do botão botaoCompilador
       textFileName2Open.setFont(Configuracao.fonteBotao2); // fonte

       buttonSaveFile.setLabel(Bundle.msg("emulStore")); // "Gravar"
       buttonSaveFile.setBounds(new Rectangle(iCG.xEdFt+iCG.largLabel+2*iCG.largEdFt+10,iCG.yEdFt, iCG.largEdFt,iCG.altEdFt));//
       buttonSaveFile.setBackground(Configuracao.corFundo1);  // cor de fundo do botão botaoCompilador
       buttonSaveFile.setForeground(Color.white);             // 
       buttonSaveFile.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(ActionEvent e) {
           actionPerforStoreFileSession(e);
           }
         });
       buttonOpenFile.setLabel(Bundle.msg("emulOpen")); // "Abrir"
       buttonOpenFile.setBounds(new Rectangle(iCG.xEdFt+iCG.largLabel+3*iCG.largEdFt+10,iCG.yEdFt, iCG.largEdFt,iCG.altEdFt));//
       buttonOpenFile.setBackground(Configuracao.corFundo1);  // cor de fundo do botão botaoCompilador
       buttonOpenFile.setForeground(Color.white);             // 
       buttonOpenFile.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(ActionEvent e) {
           actionPerforOpenFileSession(e);
           }
         });

       emulatorMainPanel.setLabelAcumValue("<" + Bundle.msg("emulValueAC") + ">"); // "valor AC" - to be used to show AC value
       emulatorMainPanel.setLabelOutPut("<" + Bundle.msg("emulOutput") + ">");     // "saída" - label para texto de saída
       emulatorMainPanel.setFont(Configuracao.fonteBotao2); // font

       if (!ehApplet) { // <- era 'isStandalone'
          emulatorMainPanel.add(buttonSaveFile, null);
          emulatorMainPanel.add(buttonOpenFile, null);
          emulatorMainPanel.add(textFileName2Open, null);
          }

       emulatorMainPanel.painelInferior.add(emulatorMainPanel.getLabelOutPut(), null); // emulatorMainPanel.setLabelOutPut("<saída>") -> label para texto de saída

       }
    catch (Exception e) {
       System.err.println("Erro: Emulador via Applet: problemas na contrução do painel!");
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

  // Para lista conteúdo da memário
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


  // Atualiza memória de acordo com texto digitado no "EmulatorMainPanel.txtExecucao"
  // - Em: EmulatorMainPanel.emular_actionPerformed(...)" -> icg/emulador/atualiza(String codObjeto) -> atualizaMemoria(String)
  public void atualizaMemoria (String texto) {
    String separadores = " \n" + (char)13;
    StringTokenizer stAM = new StringTokenizer(texto, separadores, false); // " \n"
    // System.out.println("icg/emulador/EmulatorBaseClass: ");
    numInstrPrograma = 0;

    epi.setXY(0,0); // passa para a primeira posição de memória     
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
                  String msgAux = Bundle.msg("emulLoadedInstr") + " " + numInstrPrograma + Bundle.msg("emulinstructions"); // "foram carregadas "+numInstrPrograma+" instruções"
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
               emulatorMainPanel.setLabelInstrValue(errorMsg); // "instrução inválido ("+strInstr+")");
               System.err.println("Error in EmulatorBaseClass: " + errorMsg); // "[emul/EA]: atualização de memória: instrução inválido ("+strInstr+")");
               }
             //System.out.print(" "+memoria[i][j].conteudo);

             } // if (stAM!=null && stAM.hasMoreTokens())

          } // for (j=0; j<10; j++)
      //System.out.println();
      } // for (i=0; i<10; i++)

    } //  void atualizaMemoria(String texto)


  // Constrói string com código vindo em "st", coloca mudança de linha (para o "emulatorMainPanel.txtExecucao")
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
        // Agora só carrega o programa, via parâmetro (ou arquivo?), na área de código (emulatorMainPanel.txtExecucao), não na memória
        // vide montaProgramaInicial()
        //if (st!=null && st.hasMoreTokens()) { // faltava verificar se st!=null
        //   memoria[i][j] = new Memoria(st.nextToken());
        //}
        //else {
           memoria[i][j] = new EmulatorCelMemory("000000");
        // }
        //modificação de estado
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
    iCG.testandoGabarito = false; // para garantir não pegar dados de gabarito
    execInst(false);
    }


  // Coloca na memória o número digitado após clicar me "OK"
  // Chamado em: também é invocado em "EmulatorMainPanel.montaPainelPrincipal()", ao dar um ENTER no
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
         emulatorMainPanel.setLabelInstrValue(Bundle.msg("emulInvalidNum")); // "número inválido"
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
         emulatorMainPanel.setLabelInstrValue(Bundle.msg("emulInvalidNum")); // "número inválido"
         }
       emulatorMainPanel.botaoRoda.setEnabled(true);
       emulatorMainPanel.botaoRodaPP.setEnabled(true);
       emulatorMainPanel.setTextUserInput(" ");
       emulatorMainPanel.setEnabledTextUserInput(false);
       emulatorMainPanel.setEnabledBotaoOk(false);

       //-if (epi.getY() == 9) {
       //-  memoria[epi.getX()][epi.getY()].setBackground(Configuracao.corFundoMem1); //(Color.magenta);
       //-  epi.setXY(epi.getX() + 1, 0);
       //-  memoria[epi.getX()][epi.getY()].setBackground(corFundoMemAtual); // cor de fundo para instrução atual
       //-}
       //-else {
       //-  memoria[epi.getX()][epi.getY()].setBackground(Configuracao.corFundoMem1); //(Color.magenta);
       //-  epi.setXY(epi.getX(), epi.getY() + 1);
       //-  memoria[epi.getX()][epi.getY()].setBackground(corFundoMemAtual); // cor de fundo para instrução atual
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
    boolean acresepi = true; // a menos de desvios, "6EE" ou "9EE", faça após execução da instrução "EPI <- EPI + 1"
    int epiX, epiY; // para armazenar o endereço da instrução em execução (obtido em "epi.getX()" e "epi.getY()")
    //Color corAnterior;

    //-System.out.print("\n\n[EmulatorBaseClass]: ");

    EmulatorMainPanel emulatorMainPanelTesting = this.emulatorMainPanel;  // truque para o caso de teste de exercício

    EmulatorCelMemory[][] memoria   = this.memoria; // se não for teste de avaliação, mantenha memória real
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
    //--1 emulatorMainPanel.botaoRoda.setLabel("Pára"); // pare execução
    //--1 emulatorMainPanel.botaoRoda.setEnabled(true);

    do {

      // Problema: como interromper em caso de 'loop infinito' ?
      //--2 try {Thread.sleep(5);}  // delay 5 msec between updates
      //--2 catch (InterruptedException e){};   // would catch the moue click

      inserindo = false;

      try {
        posMemX = epiX = epi.getX();
        posMemY = epiY = epi.getY();

        inst = memoria[epiX][epiY].getInstrucao(); // este é para pegar sem a resposta do aluno

        if (inst == null) {
           String msgAux = Bundle.msg("emulErrorInvInstr") + " \"memoria["+epiX+"]["+epiY+"]\"="+memoria[epiX][epiY]; // "Não era instrução válida! Em \"memoria["+epiX+"]["+epiY+"]\"="+memoria[epiX][epiY]
           emulatorMainPanelTesting.setLabelInstrValue(msgAux);
           System.out.println(msgAux);
           acresepi = true;
           finaliza = true;
           }
        else {
           //[29/08/2004] desativei, pois "acrescepi=true" sempre, a menos de instruções "6EE" e "9EE" 

           int aux = 0;
           String strAC; // auxiliar para atualizar texto que lista valor do acumulador AC
           acresepi = true;
           try {
           switch (inst[0]) {

             case 0:
               //emulatorMainPanel.setLabelInstrValue("0 -> AC = cEE");
               emulatorMainPanelTesting.msgInstrucaoExecutada(epiX,epiY,"0 -> AC = cEE");

               if (inst[1] == 0 && inst[2] == 0) { // encontrou instrução de final de programa
                 finaliza = true;
                 //memoria[epiX][epiY].setBackground(Color.white);
                 epi.setXY(0, 0);
                 //memoria[epiX][epiY].setBackground(Configuracao.corAzulClaro); //(Color.blue);
                 emulatorMainPanelTesting.setLabelInstrValue("<" + Bundle.msg("emulEndProgramm") + ">"); // final de programa
                 acresepi = false;

                 // Está gerando gabarito a partir da emulação, finalize mais um bloco
                 if (iCG.ehGeraGabarito) {
                    iCG.strGabEntradasSaidas(iCG.strGabEntradasSaidas() + iCG.montaGabaritoDosVetores());
                    this.reseta(); // 03/06/2012: estava no estatico 'iCG.motaGabaritoHTML(...)'
                    this.atualizaMemoria(this.getCodigo()); // 03/06/2012: estava no estatico 'iCG.motaGabaritoHTML(...)'
                    iCG.listaGabEntradas( new ListaLigada()); // começa novamente...
                    iCG.listaGabSaidas(   new ListaLigada());
                    }
                 }
               else {
                 if (inst[1] == -1) { // Truque: 0-k => AC <- k : para atribuir constantes
                    strAC = memoria[epiX][epiY].getValor() + "";
                    acumulador.setText(strAC);
                    }
                 else
                 if (inst[1] == -2) { // Truque: 0*EE => AC <- c(cEE)K : para indireção
                    // pega o valor da gaveta (epiX,epiY): endereço EE
                    int end = memoria[epiX][epiY].getValor(), eX = end/10, eY = end%10;
                    end = memoria[eX][eY].getValor();         eX = end/10; eY = end%10;
                    // pega o valor da gaveta apontada por (eX,eY): endereço eXeY
                    strAC = memoria[eX][eY].getValor() + ""; // memoria[eX][eY].getValor() + "";
                    acumulador.setText(strAC);
                    }
                 else {
                    strAC = memoria[inst[1]][inst[2]].getValor() + "";
                    acumulador.setText(strAC);
                    }
	      
                 // Não, "labelSaida" será apenas para comando "8ee"
                 // emulatorMainPanel.setLabelOutPut(acumulador.getValor() + ""); // emulatorMainPanel.setLabelOutPut("<saída>") é label para texto de saída
                 emulatorMainPanelTesting.setLabelAcumValue(strAC); // emulatorMainPanel.setLabelOutPut("<saída>") é label para texto de saída
                 }
               break;

             case 1:
               //emulatorMainPanel.setLabelInstrValue("1 -> EE = AC");
               emulatorMainPanelTesting.msgInstrucaoExecutada(epiX,epiY,"1 -> EE = AC");
               // Pode ser que EE esteja na "pilha de execução" (neste caso continuará com o fundo branco...)
               if (inst[1] == -1) {
                  memoria[inst[2]][inst[3]].setText(memoria[epiX][epiY].getValor() + "");
                  }
               else
               if (inst[1] == -2) { // Truque: 0*EE => AC <- c(cEE)K : para indireção
                  // pega o valor da gaveta (epiX,epiY): endereço EE
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
               emulatorMainPanelTesting.setLabelAcumValue(aux+""); // é label para texto de saída
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
               emulatorMainPanelTesting.setLabelAcumValue(aux+""); // é label para texto de saída
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
               emulatorMainPanelTesting.setLabelAcumValue(aux+""); // é label para texto de saída
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
                  emulatorMainPanelTesting.setLabelInstrComm("Divisão por zero");
                  System.out.println("Divisão por zero");
                  if (iCG.testandoGabarito) erroPara = true;
                  finaliza = true; // aborta execução do programa!
                  }
               acumulador.setText(aux+"");
               emulatorMainPanelTesting.setLabelAcumValue(aux+""); // é label para texto de saída
               break;

             case 6:
               emulatorMainPanelTesting.msgInstrucaoExecutada(epiX,epiY,"6 -> cAC>0, EPI <- EE");
               if (acumulador.getValor() > 0) {
                  epi.setXY(inst[1], inst[2]);
                  acresepi = false; // não deixa acrescentar +1 ao EPI
                  }
               //else { // este já é o "default"
               //   acresepi = true;
               //   }
               break;

             case 7: // O valor é pego de fato no "botaoOK", em "emulatorMainPanel.botaoOk"->"ok_actionPerformed(ActionEvent)"
                     // o núm. digitado é inserido em "iCG.listaGabEntradas"
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
                      emulatorMainPanelTesting.setLabelInstrValue("número inválido");
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
                  emulatorMainPanelTesting.setEnabledTextUserInput(true); // makes 'emulatorMainPanelTesting.txtEntrada.requestFocus()' - para que usuário não precise clicar na TextField p/ ganhar foco
                  emulatorMainPanelTesting.setEnabledBotaoOk(true);
                  emulatorMainPanelTesting.setLabelInstrComm("<- digite um valor");
                  }
               break;

             case 8: // O valor de saída é inserido em "iCG.listaGabSaidas"
               // Saida: ---
               emulatorMainPanelTesting.msgInstrucaoExecutada(epiX,epiY,"8 -> saida = cEE");
               String strSaida = memoria[inst[1]][inst[2]].getText();
               emulatorMainPanelTesting.setLabelInstrComm("Saída ->"); //
               emulatorMainPanelTesting.setLabelOutPut(strSaida); // emulatorMainPanel.setLabelOutPut("<saída>") é label para texto de saída

               if (iCG.ehGeraGabarito) {
                  iCG.addListaGabSaidas(strSaida); //).add(strSaida);
                  }
               else
               if (iCG.testandoGabarito) {
                  String str1 = "";
                  iCG.numSaidasAlunoInc(); // incrementa de 1 o total de saídas na resposta do aluno
                  try { // pega no atual vetor de saídas a atual saída
                    str1 = (String) iCG.vetSaidas[iCG.atualVetEntSai()].elementAt(iCG.iSai());
                  } catch (java.lang.Exception e) {
                      // gabarito = "{ E: N N }, { S: N } { E: N N N }, { S: N N }"
                      System.out.println("[EA!exec. saídas()] Erro no tratamento do vetor de saídas: o gabarito do professor não previa esta saída ("+strSaida+")");
                      System.out.println("                    Até aqui foram "+iCG.numSaidasAluno()+" saídas, o máximo era "+
                                         iCG.vetSaidas[iCG.atualVetEntSai()].size()+"!");

		      // Neste ponto podería colocar uma interrupção do atual lote de teste, pois se a saída do aluno
                      // tem mais que a do professor, então ERRO!
		      // Isso está no iCG.

                      }
                  if (str1.equals(strSaida)) iCG.contAcertosSaiInc(); // 'strSaida': saída do aluno
                  else
                  if (Configuracao.listaGabarito) {
                     System.out.println("\n[EmulatorBaseClass] --- saídas "+iCG.atualVetEntSai()+" diferentes: gab="+str1+" alu="+strSaida);
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
               acresepi = false; // não deixe somar +1 ao EPI
               break;
             }	

          // Terminou de executar, marque a cor da última como "já executada"
          // Se a cor de fundo atual é "Configuracao.corFundoMemAtual", então pinte com "Configuracao.corAzulClaro"
          // Senão, faça o contrário
           Color corAtual = memoria[epiX][epiY].getBackground();
	   if (corAtual==Configuracao.corFundoMemAtual) { // Configuracao.corAzulClaro) {
              memoria[epiX][epiY].setBackground(Configuracao.corAzulClaro); // marca como "em execução"
              }
           else {
              memoria[epiX][epiY].setBackground(Configuracao.corFundoMemAtual); //corAzulClaro); // marca como "em execução"
              }

           } catch (java.lang.Exception e) {
              System.out.println("[EmulatorBaseClass!execInst(boolean)] inst="+inst+" memória=("+epiX+","+epiY+"): " + e.toString());
              //e.printStackTrace();
              }

          if (acresepi) { // se precisa incrementar EPI
             if (epiY == 9) { // muda de linha na tabela de memória (redefina EPI
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
    atualizaMemoria(codObjeto); // atualiza posições de memória do emuladorApplet
    this.icgPrincipal.setCodeEdited(); // clearEvaluation(); // mark as 'not evaluated' ('icg.iCG.valorExercicio = -111;')
    }

  // Após clicar em "Atualiza", deve-se fazer: epi.setXY(0,0); // passa para a primeira posição de memória
  // em "EmulatorMainPanel.atualiza_actionPerformed(ActionEvent)"
  // Chamado em: icg.ig.Botao
  public void acaoEmularPP () {
    ativo = true;
    iCG.testandoGabarito = false; // para garantir não pegar dados de gabarito
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
       // AtenÃ§Ã£o: com o arquivo '$OBJ' Ã© preciso estarem no mesmo diretÃ³rio/pasta o 'iCG.jar' e o '$OBJ'
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
       // emulatorMainPanel.setTextInputExecCode(strContentFileICG); // coloca na área de código para execução
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
    emulatorMainPanel.setLabelOutPut("<" + Bundle.msg("emulOutput") + ">"); // "<saída>" - label para texto de saída

    emulatorMainPanel.setEnabledBotaoOk(false);
    passoapasso = false;
    emulatorMainPanel.setLabelInstrComm("");
    } // public void reseta()


  } // class EmulatorBaseClass
