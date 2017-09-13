/**
 * 
 * iMath - http://www.matematica.br
 * 
 * @author Leônidas de Oliveira Brandão
 * @see    icg/iCG.java
 *
 * Define variáveis estáticas para configuração
 */

package icg.configuracoes;

import java.awt.Color;
import java.awt.Font;

import icg.iCG;
import icg.msg.Bundle;

public class Configuracao {

  // 2.3.0: 07-08/07/2012: acertos: interface para gravar exerc. (automatizado); em JanelaDialogo
  // 2.2.5: 07/07/2012: (nem publiquei) acertos: -1 e nao -111 qdo nao atividade; problema de conversao para POST (perdia tags)
  // 2.2.4: 25/06/2012: acertos: msg de erro (com janela) para abrir arquivos; acerto nomes metodos em iCG, EmulatorBaseClass e Arquivos
  // 2.2.3: 24/06/2012: acertos: larg. JanelaDialogo; msg para gravacao *.icg/*.html
  // 2.2.2: 23/06/2012: acertos: AnaLex de Expressoes; gravar como *.icg ou *.html (cria aux. '*_icg2html.icg'; conf. 'pt_BR' applet; exc. com cod. null; ...
  // 2.2.1: 18/06/2012: acertos em gravacoa de sessao, 'getEvaluation' e 'getAnswer' chamam 'acaoEnviar()'
  // 2.2.0: 17/06/2012: implementado formato "iCG XML" (agora arquivo enviado pode ter gabarito, enunc., codigo obj. e fonte,...)
  // 2.1.3: 12/06/2012: acertos em internacionalizacao de comandos (if, while...)
  // 2.1.2: 11/06/2012: acerto em Criptografia para jogar fora final de linha
  // 2.1.1: 10/06/2012: muitas mudancas (ler arq. de URL, unificada JanelaDialogo, implementado iMA v.1 - getAnswer,getEvaluation)
  // 2.1.0: 03/06/2012: varias alteracoes, vindo desde 29/05/2012 para limpar codigo, acertar layout, internacionalizar mais...
  // 2.0.2: 24/08/2010: permitir entrar botao compilador em aplicativo sem qualquer parâmetro
  public static final String data   = "08/07/2012";
  public static final String versao = "2.3.0";
  // "07/07/2012" | "25/06/2012" | "24/06/2012" | "23/06/2012"
  // "2.2.5"      | "2.2.4"      | "2.2.2"      | "2.2.3"
  // "17/06/2012" | "12/06/2012" | "11/06/2012" | "10/06/2012" | "03/06/2012" | "16/05/2012" | "24/08/2010" | "04/04/2006"
  // "2.2.0"      | "2.1.3"      | "2.1.2"      | "2.1.1"      | "2.1.0"      | "2.0.3"      | "2.0.2"      | "2.0.1"

  // Label de 'barra de mensagens'
  public static int leX = iCG.leX, leY = iCG.leY, leL = iCG.leL, leA = iCG.leA;

  public final static String strEndereco = "http://www.matematica.br/icg";

  // CompilerPanel.java: areaCodigoFonte, areaCodigoObjeto, areaErrosCodigo
  public final static String ENCODING = "ISO-8859-1"; // or "UTF-8"

  // To define iCG standard width and height: from 'icg.iCG.main(...)'
  public static final int
    WIDTH = 600,
    HEIGTH = 420;

  public static final int
    ALTURA_BARRAS=25, // icg.ig.Botao
    NUMMSG = 14,
    MSGCOMPILADOR = 0,
    MSGEMULADOR   = 1,
    MSGENVIAR     = 2,
    MSGGABARITO   = 3,
    MSGEMULAR     = 4,
    MSGPASSO      = 5,
    MSGATUALIZA   = 6,
    MSGINFO       = 7,
    MSGMEMORIA    = 8,
    MSGCODIGO     = 9,
    MSGCOMPILA    =11,
    MSGSOBRE      =12,
    MSGAJUDA      =13;

  public static final String[] mensagem = new String[NUMMSG]; //
  public static void defMsgs () {
    // mensagem[] = Bundle.msg("")
    mensagem[MSGCOMPILADOR ] = Bundle.msg("buttonModelCompile"); // "Usar ambiente para compilação"
    mensagem[MSGEMULADOR   ] = Bundle.msg("buttonModelEmulator"); // "Usar ambiente para emulação"
    mensagem[MSGENVIAR     ] = Bundle.msg("buttonEvaluate"); // "Avaliar e enviar exercício"
    mensagem[MSGGABARITO   ] = Bundle.msg("buttonStartConstructExercise"); // "Iniciar o construtor de gabarito"
    mensagem[MSGEMULAR     ] = Bundle.msg("buttonStartEmul"); // "Iniciar emulação do código"
    mensagem[MSGPASSO      ] = Bundle.msg("buttonEmulStepByStep"); // "Emulação passo-a-passo: próximo passo"
    mensagem[MSGATUALIZA   ] = Bundle.msg("buttonLoadCode"); // "Carrega o código para a memória"
    mensagem[MSGINFO       ] = Bundle.msg("buttonInfo"); // "Informações"
    mensagem[MSGMEMORIA    ] = Bundle.msg("buttonFontMemory"); // "Altera fonte da memória"
    mensagem[MSGCODIGO     ] = Bundle.msg("buttonFontCode"); // "Altera fonte do código"
    mensagem[MSGGABARITO   ] = Bundle.msg("buttonConstructExercise"); // "Constrói gabarito de exercício: anota entradas/saídas";
    mensagem[MSGCOMPILA    ] = Bundle.msg("buttonCompile"); // "CompilerBaseClass: gera código objeto"
    mensagem[MSGSOBRE      ] = Bundle.msg("buttonAbout"); // "Sobre o iCG"
    mensagem[MSGAJUDA      ] = Bundle.msg("buttonHelp"); // "Ajuda"
    }

  public final static boolean
    debugOptionAL      = true,   // list lexical items during compilation - in 'icg.compilador.AnaLex', 'icg.compilador.CompilerBaseClass'
    debugOptionBinary  = true,   // list instructions in 'icg.compilador.CodigoObjeto'
    listaAnaSim        = false,  // para acompanhar a análise sintática
    debugOptionAL2     = false,  // lista tb partes de cada não terminal (para IO_ESCREVA) - in 'icg.compilador.CompilerBaseClass'
    listaGabarito      = true;   // lista msg para depurar gabarito

  public static final Color
     corFundoEntradas = Color.white, // cor de fundo para a entrada de programa no Emulador
     corFundo1 = new Color( 49,  76, 166),
     corFundo2 = new Color( 62,  83, 159),
     corFundo3 = new Color(240, 240, 240),

     corFundoBarraBt = new Color( 75, 95,135), // cor de fundo para 'barra' que conterá os botões (parte de cima do iCG)
     corLinhaBarraBt = new Color(150,170,210), // cor do bordo da 'barra'

     // Cor de fundo do painel que recebe o AC, a caixa para 'entradas', a caixa para 'saída' e msg de instrução executada
     corFundoPainel_AC_Ent_Sai = new Color(101, 182, 255), // (101, 166, 255), // 145, 166, 255),

     corFundoMem1     = new Color( 40, 140, 255), // memória já "executada"
     corFundoMem2     = new Color(240, 240, 100), // memória ainda não "eecutada" (amarelo fosco)
     corFundoMem3     = new Color(140, 255, 140), // memória utilizada para armazenar dados (verde fosco)
     corFundoMemAtual = new Color(140, 240, 255), // cor de fundo da instrução na memória atualmente executada  

     corFrente1 = Color.white,           // o contraste da "corFundo1"
     corFrente2 = Color.black,           // o contraste da "corFundo1"

     // Cores para Compilador:
     // Em 'icg.iCG': deve contratar com 'compilerFg' para cor de fonte sobre o fundo
     compilerBg =  new Color(0, 111, 194),       // fundo geral (sobre o qual estao botoes e outros
     compilerFg =  Color.white,                  // para cor de fonte sobre 'compilerBg'
     // Em 'icg.compilador.CompilerPanel'
     compilerBgCenter =  new Color(0, 100, 150), // fundo sobre os quais estao paineis codigo, mensagem, execucao
     compilerBgCode = new Color(0, 100, 150),    // fundo da area de codigos (a ser compilado)
     compilerBgExec = new Color(0, 100, 150),    // fundo area de codigo executavel
     compilerBgMsgs = new Color(0, 100, 150),    // fundo area de mensagens (usada principalmente para listar erros compilacao)

     corAzulClaro   = new Color( 64, 128, 198), // cor de fundo do applet
     corAzulClaro2  = new Color(128, 156, 225), // cor de fundo do applet
     corAzulEscuro1 = new Color(30,100,180); //( 30, 100, 180); // icg/compilador cor de fundo

  public static final Font
    fonteBotao   = new java.awt.Font("Helvetica", 1, 10),
    fonteBotao2  = new java.awt.Font("Helvetica", 1,  8),

    fonteDN9     = new java.awt.Font("Dialog", 1,  9),
    fonteDN10    = new java.awt.Font("Dialog", 1, 10),
    fonteDN11    = new java.awt.Font("Dialog", 1, 11),
    fonteDN12    = new java.awt.Font("Dialog", 1, 12),

    ftEndereco   = new Font("Helvetica", Font.PLAIN, 10),

    fonteCodigoFonte  = new java.awt.Font("Dialog", 1, 10), // para código fonte
    fonteCodigoObjeto = new java.awt.Font("Dialog", 1, 10), // para erros no código fonte
    fonteErrosCodigo  = new java.awt.Font("Dialog", 1, 10), // para código objeto

    fonteCodigoN = new java.awt.Font("Courier", 1, 9),//10), // BOLD
    fonteCodigo  = new java.awt.Font("Courier", 0, 9);//10); // PLAIN


  // Cor de fundo do painel que recebe o AC, a caixa para 'entradas', a caixa para 'saída' e msg de instrução executada
  // public static Color corFundoPainel_AC_Ent_Sai; // usada em 'Emulador_Panel.painelInferior'


  public static Color
       fundoTopo             = Color.white, // cor para jundo dos topos das janelas JanelaTexto, icg/ig/JanelaAjuda.java
       fundoInvTopo          = Color.black,
       azulMedio             = new Color(53, 106, 160); // = #356AA0 : cor de fundo de icg/ig/JanelaAjuda.java


  public static final Font
        ftPlain8      = new Font ("Helvetica", Font.PLAIN,  8),
        ftPlain10     = new Font ("Helvetica", Font.PLAIN, 10),

        ftBold9       = new Font ("Helvetica", Font.BOLD,   9),
        ftBold10      = new Font ("Helvetica", Font.BOLD,  10),
        ftBold11      = new Font ("Helvetica", Font.BOLD,  11),
        ftBold12      = new Font ("Helvetica", Font.BOLD,  12),

        ftAPlain10    = new Font ("Arial",     Font.PLAIN, 10),
        ftABold10     = new Font ("Arial",     Font.BOLD,  10),

        ftDPlain10    = new Font ("Dialog",    Font.PLAIN, 10),

        fixedPlain10  = new Font ("Courier",   Font.PLAIN, 10); // JanelaAjuda.java


  // Para definir comandos do compilador: icg.compilador.Compila e icg.compilador.Elemento.ehReservado
  // public static String cmd_leia    = "leia";    - now it is found em 'Messages_*.properties': cmdRead
  // public static String cmd_escreva = "escreva"; - cmdwrite
  // public static String cmd_if      = "if";      - cmdIf
  // public static String cmd_else    = "else";    - cmdElse
  // public static String cmd_while   = "while";   - cmdWhile
  public static String cmd_eq      = "==";  
  public static String cmd_leq     = "<=";
  public static String cmd_geq     = ">=";
  public static String cmd_neq     = "!=";

  public static char[] espec_simbol={';','+','-','*','<','>','/','(',')','=','{','}','!','&','|'};

  // Emulador_Panel:
  public static int numLinhasCodPermitidas = 12;

  //  static { if (corFundo1==null) inicia(); }

  public final static int maxVariables = 50; // maximal number of allowed variables (half of memory...) - used in 'icg.compilador.Variaveis'

  public static boolean ehSimboloEspecial (String str) {
    try {
    if (str.length()>2) return false; // não existem símbolos válidos com mais de 2 caracteres
    if (str.length()==2) {
       //System.out.println("[Configuracao.ehSimboloEspecial] str=\""+str+"\"");
       if (str.equals("==")) return true;
       if (str.equals("<=")) return true;
       if (str.equals(">=")) return true;
       if (str.equals("!=")) return true;
       if (str.equals("&&")) return true;
       if (str.equals("||")) return true;
       return false;
       }
    else {
      char c = str.charAt(0);
      // espec_simbol={';','+','-','*','<','>','/','(',')','=','{','}','!','&','|'};
      for (int i=0; i<espec_simbol.length; i++) {
          if (c==espec_simbol[i]) return true; // 
          }
      return false;
      }
    } catch (java.lang.Exception e) {
      System.out.println("[Configuracao.ehSimboloEspecial] Erro, item léxico inexistente: <"+str+">");
      return false;
      }
    }


  }
