/**
 *
 * iMath - http://www.matematica.br
 *
 * <p>Title: iCG - Interactive Computer (Computador Gaveteiro Interativo)</p>
 *
 * <p>Description: compile (produce "binary" code to iCG)</p>
 *
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: LInE - http://www.ime.usp.br/line</p>
 *
 * @author Le�nidas de Oliveira Brand�o
 * @version 2012-05-21 (added 'read', 'write' - 'leia', 'escreva'); 2008-10-02
 *
 */

// Called by: icg.compilador.CompilerPanel.ButtonHandler.actionPerformed(ActionEvent e)

package icg.compilador;

/*
 * Elemento: "estrutura" para cada item l�xico
 * Leia    : para leitura da cadeia de carateres (express�o aritm�tica)
 * Itens   : monta os itens l�xico num "Vector" (faz o papel de um Analisador L�xico muito simplificado...)
 *
 */

    /* Grafos sint�ticos (ou diagramas sint�ticos)
     *
     * [24/08/2004] revis�o
     * [26/03/2006] detectado problema de EXPRESS�O L�GICA com EXPRESS�O ARITM�TICA
     *              if (a<b || flag==1)
     *                    ^ ^
     *                     vai tentar juntar, qdo deveria finalizar "a<b" como EXP
     *              tentar resolve anotando em E() que se trata de EA ou EL
     * [27/03/2006] novas tentativas de diagramas
     *
     * Anterior                    Novo
     * ------------------------+-----------------------------------------------------------
     * EXPLOG ---> EXP --->||  =>  EXPLOG -+-> EXPLOG ---> ou ---> EXPLOG --->||  (ou = ||)
     *                                     |...
     *
     * EXP ---> [E] ...        =>  ELSS ---> [EA] ...
     *
     * E   ---> ...            =>  EA  --->
     *
     *
     * C --+--> if --------> IF ---------------------->||
     *     |                                       ^
     *     +--> while -----> WHILE --------------->|
     *     |                                       |
     *     +--> leia ------> IO_LE --------------->|
     *     |                                       |
     *     +--> escreva ---> IO_ESCREVA ---------->|
     *     |                                       |
     *     +--> id --------> = ------------> E --->+
     *
     *
     * IF ---> ( ---> EXPLOG ---> ) --+--> C ---> ELSE -+->||
     *                                |                 |
     *                                |                 +->||
     *                                |
     *                                +--> { ---> C ---> } -+-> ELSE --->||
     *                                                      |
     *                                                      +->||
     *
     N EXPLOG ---> ELS --+--> ou ---> ELS --->||  (ou = ||)
     *                   |
     *                   +-->||
     *
     N ELS -+----------> ELSS --------------------+-->||
     *      |                  ^                  |
     *      |                  |                  |
     *      |                  +<-- ELSS <-- e <--+
     *      +-> ! -> ELS -->||
     *
     * ELSS ---> [ EA ] --+--> == --->+---> [ EA ] -->||  X era EXP
     *                    |           ^
     *                    +--> <= --->|
     *                    |           |
     *                    +--> >= --->|
     *                    |           |
     *                    +--> != --->|
     *                    |           |
     *                    +--> >  --->|
     *                    |           |
     *                    +--> <  --->+
     *                    |
     *                    +-->||
     *
     *                    +<-----------------------+
     *          1         !       3            4   ^
     * EA --+-> + --+-----+---> [ T ] --+---> + ---+    X era E
     *      |       ^                   |      5   ^
     *      +-> - ->|                   +---> - ---+
     *      |    2  |                   |      6   |
     *      +-------+                   |                X cortei: ---> ou --+ (||)
     *                                  |
     *                                  +--->|| 7
     *
     *
     *    +----------------------------+
     *    !                            |
     * T ---> [ FATOR ] ---+---> * --->+
     *                     |           |
     *                     +---> / --->+
     *                     |           |
     *                     |                            X cortei: +---> e --->+ (&&)
     *                     |
     *                     +--->||
     *
     * FATOR --+--> NUM ---------------------->+--->||     ?    AC <- NUM
     *         |                               |
     *         +--> ID  ---------------------->|           OEE  AC <- cEE   EE endere�o var. NUM
     *         |                               |
     *         +-->  (  ---> [ELSS] ---> ) --->|
     *                                            X cortei |                             |
     *                                                     +--> ! [FATOR] --------------->+
     *
     *
     */

import java.io.*;
import java.util.StringTokenizer;
import java.util.Vector;

import icg.configuracoes.Configuracao;
import icg.msg.Bundle;
import icg.util.ListaLigada;
import icg.util.No;


public class CompilerBaseClass {

  String cadeia;   // para a express�o a ser analisada
  Vector itensLex; // para armazenar itens l�xicos: seria
                   //montado pelo Analisador L�xico (mas aqui vai via StringTockenizer)

  Pilha posicoesTemporarias;
  CodigoObjeto programa;

  ListaLigada listaPosicoes = new ListaLigada(); // lista com todos os comandos inseridos, para trocar ocorr�ncia de '$' (caso de uso da pilha de execu��o)

  int num_item;   // n�mero do item l�xico atual
  int num_linha;  // linha atual do arquivo ( se existir )
  boolean OK;

  String pilhaDeInformacoes = new String(""); // messages about the compilation process
  String programaAnalisado = new String( "" );

  static int[] linhas = null;

  static int endPilhaExec = 0; // endere�o relativo na pilha de execu��o, usado em conjun��o com o '$', como em "1$n"

  static int numORs = 0; // E(): anote seu n�mero para eventual uso para gerenciar end. dos blocos (p/ desvio final do OR): vetBlocosORs


  // Return the lexical item type:
  //   Elemento.NUMERO
  //   Elemento.COMANDOS
  //   Elemento.VARIAVEL
  //   Elemento.OUTROS
  private int tipo (int i) {
    //Object obj = itensLex.elementAt(i);
    //if (obj instanceof Elemento)
    //   return ((Elemento)obj).tipo();
    //else return -1;
    try {
      return ((Elemento)itensLex.elementAt(i)).tipo();
    } catch (java.lang.Exception e) {
      System.err.println("[CompilerBaseClass!item(int)] Erro, "+e);
      return -1;
      }
    }


  // Return the current lexical item
  private String item (int i) {
    try {
     return ((Elemento)itensLex.elementAt(i)).obj();
    } catch (java.lang.Exception e) {
      System.err.println("[CompilerBaseClass!item(int)] Erro, "+e);
      return "";
      }
    }


  private void item () {
    programaAnalisado += item(num_item)+" ";
    }

  // Avanca para o proximo item lexico...
  private void avanca_item () {
    item();
    if ( linhas != null ) {
       linhas[ num_linha ]--;
       while ( num_linha < linhas.length && linhas[ num_linha ] == 0 ) {
         num_linha++;
         programaAnalisado += "\n" + (num_linha + 1) + ". " ; // ????
         }
       }
    num_item++;
    }

  private void listItensUntil (int n) {
    int max_item = itensLex==null ? 0 : itensLex.size();
    if (max_item<n)
       System.err.println("listItensUntil: Error, #itens=" + max_item + " < " + n);
    // List other itens, until 'num_item'
    for (int i_=0; i_<max_item; i_++)
        System.out.println(" " + i_ + ": "+item(i_));
    }


  // Read from a 'BufferedReader' to build the internal 'String [] linhas'
  private static void initLinhas (BufferedReader buff ) {
    Vector v = new Vector();
    Vector st = new Vector();
    //StringTokenizer st;
    String s;
    Integer n;
    int i;

    s = LeArq.linha( buff );
    while ( s != null ) {
      st = AnaLex.constroiTokens(s); // build Vector with itens
      n = new Integer(st.size());
      v.addElement( n );
      s = LeArq.linha( buff );
      }

    // Alocando vetor de linhas...
    linhas = new int[ v.size() ]; // internal global variable

    for ( i=0; i<v.size(); i++ ) {
      n = (Integer) v.elementAt( i );
      linhas[i] = n.intValue();
      }
    } // private static void initLinhas(BufferedReader buff)


  // To test...
  public static void main (String[] args) {
    CompilerBaseClass comp;
    BufferedReader arquivo;
    String linhaArquivo;

    if (args.length < 1)
      comp = new CompilerBaseClass();
    else {
      linhaArquivo = args[0];
      System.out.println (" teste" + linhaArquivo);
      arquivo = LeArq.buffer( linhaArquivo );

      if ( arquivo != null ) {
        try {
          //Se existe um arquivo...
          arquivo.mark( 3000 );
          initLinhas( arquivo );

          arquivo.reset();
        } catch( IOException e ) {
          System.err.println("Erro! Ao tentar ler arquivo "+linhaArquivo);
          }

        linhaArquivo = LeArq.tudo( arquivo );
        //System.out.println( linhaArquivo );
        }

      comp = new CompilerBaseClass( linhaArquivo );
      } // else

    System.out.println( comp.imprimeProgramaAnalisado() );
    System.out.println( comp.informacoesDeSaida() );

    if ( comp.OK )
      System.out.println( comp.programa );
    }


  public CompilerBaseClass () {

    System.out.print("Digite uma sequencia de comandos\n(help - informa��es; exit - sair): " );
    cadeia = Leia.readLine();

    while (true) {
      posicoesTemporarias = new Pilha();
      programa = new CodigoObjeto( 0 ); // ( 1 ); [04/08/2004]

      if (cadeia.equals("exit"))
        return;
      else
        reconheceCadeia();

      System.out.print("Digite uma sequencia de comandos\n(help - informa��es; exit - sair): ");
      cadeia = Leia.readLine();
      }
    }


  public CompilerBaseClass (String cad) {
    if (Configuracao.listaAnaSim)
      System.out.println("[icg.compilador.CompilerBaseClass.Compila(String)] "+cad);
    num_linha = 0;
    posicoesTemporarias = new Pilha();
    programa = new CodigoObjeto( 0 ); // [04/08/2004]( 1 );
    cadeia = cad;
    reconheceCadeia();
    }



  public String informacoesDeSaida () {
    pilhaDeInformacoes += Bundle.msg("msgCompComp") + ":" + programa.MSG;
    return pilhaDeInformacoes;
    }

  public String imprimeProgramaAnalisado () {
    return programaAnalisado;
    }


  private void reconheceCadeia () {
    int i = 0;

    // monta vetor com itens l�xicos, funciona como o analisador sintatico
    // se ele for == null => erro de sintaxe.
    itensLex = Itens.montaItens( cadeia ); // "cadeia" cont�m uma "string" com o texto a ser compilado
    //System.out.println("[CompilerBaseClass!reconheceCadeia] 1 - cadeia:\n"+cadeia);

    if ( itensLex != null ) {
      //System.out.print("Teste: ");
      // lista itens l�xicos, s� para confer�ncia
      // Itens.listaItens(itensLex);
      }

    // posiciona para leitura do primeiro item l�xico
    num_item = 0;
    programaAnalisado += "\n" + (num_linha + 1) + ". " ;

    //System.out.println("[CompilerBaseClass!reconheceCadeia] "+num_linha+": "+programaAnalisado);
    if (Configuracao.listaAnaSim)
      System.out.println("[CompilerBaseClass!reconheceCadeia] cadeia:\n"+cadeia);
    if ( itensLex != null  && this.INICIA() ) {
      pilhaDeInformacoes = pilhaDeInformacoes.concat( "\n" + Bundle.msg("msgCompOK") + "\n" ); // Cadeia reconhecida com sucesso
      OK = true;
      }
    else
      pilhaDeInformacoes = pilhaDeInformacoes.concat("\n" + Bundle.msg("msgCompErrNotOK") + "\n"); // Falha no reconhecimento da cadeia
    }


  // Inicia a analise dos comandos C().
  boolean INICIA () {
    while ( true ) {

      //if (num_linha>-1 && linhas!=null)
      //   System.out.println("[CompilerBaseClass!INICIA] "+linhas[num_linha]);
      //else System.out.println("[CompilerBaseClass!INICIA] - "+num_linha+" "+linhas);

      if ( !C() ) {
        // Linha " + (num_linha+1) + ". Erro: comando invalido!
        String [] strMsgs =  { "" + (num_linha+1) };
        pilhaDeInformacoes = pilhaDeInformacoes.concat( "\n" + Bundle.msgComVar("msgCompErrInvComL", "OBJ", strMsgs) );
        return false;
        }

      // Se a cadeia chegou ateh o final verdadeira ela eh reconhecida...
      if ( num_item >= itensLex.size() ) {
        // END
        programa.adicionaComando("000"); // "0-0000" );

        // O "programa.atualizaComandos(posicoes);" serve para trocar $ pela primeira posi��o de mem�ria dispon�vel, logo
        // deve ser a �ltima coisa a ser feita!!
        programa.atualizaComandos(listaPosicoes);
        if (Configuracao.listaAnaSim)
          System.out.println("\n[CompilerBaseClass!INICIA] cadeia reconhecida e gerado c�digo com sucesso!");
        return true;
        }
      } // while ( true )
    }


  // copia a lista "posicoes" no final da lista "listaPosicoes"
  void copiaListaPosicoes (ListaLigada posicoes) {
    //No noListaDefinitiva = listaPosicoes.posicaoAtualLista(); // �ltimo No na lista definitiva
    No noAux             = posicoes.inicioLista();            // primeiro elemento na lista atual
    while ( noAux != null ) { //iterador.hasNext() ) {
      listaPosicoes.add( noAux.obj() );
      noAux = noAux.proximo();
      }
    }


  // Trata as expressoes entre parenteses.......
  boolean Tpar () {
    if (Configuracao.listaAnaSim)
      System.out.println("[CompilerBaseClass!Tpar] item "+item(num_item)+" ? "+tipo(num_item));

    if ( tipo(num_item) == Elemento.OUTROS && item(num_item).equals( "(" ) ) {
      // => '('
      avanca_item();
      if ( !EA() ) { //N
	 // Erro: esperava um operando ap�s  \'(\' (encontrado \'" + item(num_item) + "\')"
         String [] strMsgs = { "" + item(num_item) }; //
         pilhaDeInformacoes = pilhaDeInformacoes.concat( "\n" + Bundle.msgComVar("msgCompErrOpenParTpar","OBJ",strMsgs) );
         return false;
         }

      if ( tipo(num_item) == Elemento.OUTROS && item(num_item).equals( ")" ) ) {
      // => ')'
         avanca_item();
         return true;
         }
      else { // Erro: esperava um \')\' (encontrado \'<item(num_item)>\')
         String [] strMsgs = { "" + item(num_item) }; //
         pilhaDeInformacoes = pilhaDeInformacoes.concat( "\n" + Bundle.msgComVar("msgCompErrCloseParTpar","OBJ",strMsgs) );
         return false;
         }
      }
    else { // Erro: esperava um \'(\' ou operando (encontrado \'<item(num_item)>\')
      String [] strMsgs = { "" + item(num_item) }; //
      pilhaDeInformacoes = pilhaDeInformacoes.concat( "\n" + Bundle.msgComVar("msgCompErrParOrOperTpar","OBJ",strMsgs) );
      return false;
      }
    } // boolean Tpar ()


  // Analisa identificadores (variaveis ou numeros).
  boolean ID () {
    if (Configuracao.listaAnaSim)
      System.out.println("[CompilerBaseClass!ID] item "+item(num_item)+" ? "+tipo(num_item));

    //LinkedList posicoes = new LinkedList();
    ListaLigada posicoes = new ListaLigada();

    if ( tipo(num_item) == Elemento.NUMERO || tipo(num_item) == Elemento.VARIAVEL ) {
      // Se eh operando...
      if (tipo(num_item) == Elemento.VARIAVEL)
        programa.aloca( item(num_item) );
      posicoes.add( new Integer( programa.getMemoria() ) );
      //System.out.println("[CompilerBaseClass!ID] "+num_item+" -> "+item(num_item));
      programa.empilha( item(num_item) );
      posicoesTemporarias.empilha( posicoes );
      return true;
      }
    else
      return false;
    }


  /*
   N EXPLOG ---> ELS --+----------> ou ---> ELS --+-->||  (ou = ||)
   *                   |        ^                 |
   *                   +-->||   |                 |
   *                            +-----------------+
  */
  boolean EXPLOG () {
    if (Configuracao.listaAnaSim)
      System.out.println("[CompilerBaseClass!EXPLOG] In�cio: EXPLOG := ELSS | ELS ou ELS");
    Vector vetBlocosORs = new Vector(); // para anotar comandos com blocos de OR (||) ap�s processamento total do E()
    boolean algumOR = false;
    try {
      if (ELS()) {
        // ELS || ELS
        while (true) {
          if (tipo(num_item)==Elemento.OUTROS && item(num_item).equals( "||" )) {
             algumOR = true;
             int pM = programa.getMemoria();           // pega endere�o da �lt. instru��o, pM;
             vetBlocosORs.addElement(new Integer(pM)); // anota n�m. da instru��o ao final para ser substituida
             programa.adicionaComando("6ee");          // 6ee                        ; ap�s todos os ORs troque
             avanca_item();                            //                            ; pelo endere�o do �lt. bloco
             if (ELS()) {
                if (Configuracao.listaAnaSim)
                   System.out.println("[CompilerBaseClass!EXPLOG] Reconhecido:  EXPLOG := ELS ou ELS");
                }
             else {
                System.out.println("[CompilerBaseClass!EXPLOG] Erro em express�o l�gica: esperava o segundo [EXPLOG] de: [ELS || ELS]");
                return false;
                }
             } // if (tipo(num_item)==Elemento.OUTROS && item(num_item).equals( "||" ))
          else
             break;
          if ( num_item >= itensLex.size() ) {
             // END
             programa.adicionaComando("000"); // "0-0000" );
             //
             programa.atualizaComandos(listaPosicoes);
             }

          } // while (true)

        // Substitua em vetBlocosORs
        if (algumOR) {
          int pM = programa.getMemoria();         // pega endere�o da �lt. instru��o, pM;
          for (int i=0; i<vetBlocosORs.size(); i++) {
              int posInstrOR = ((Integer)vetBlocosORs.elementAt(i)).intValue();
              programa.substituaComando("6"+pM,posInstrOR); // EPI     <- ee     ; pule o pr�x. cmd (q/ � desvio)
              if (Configuracao.listaAnaSim)
                 System.out.println("\n[CompilerBaseClass!EXPLOG] substituido \"6ee\" por \"6"+pM+"\"");
              }
          }
        return true;
        } // if (ELS())
      else {
        System.out.println("[CompilerBaseClass!EXPLOG] Erro em express�o l�gica: esperava [ELS] ou [ELS || ELS]");// ERRO
        return false;
        }
    } catch (java.lang.ArrayIndexOutOfBoundsException aobe) {
        pilhaDeInformacoes = pilhaDeInformacoes.concat("\n"+Bundle.msg("msgCompErrELfinal")+": "+aobe.toString()); // EXPLOG - Erro: final de cadeia
        return false;
        }
    } // boolean EXPLOG()


  /*
   N ELS -+----------> ELSS --------------------+-->||
   *      |                  ^                  |
   *      |                  |                  |
   *      |                  +<-- ELSS <-- e <--+
   *      +-> ! -> ELS -->||
  */
  boolean ELS () {
    if (Configuracao.listaAnaSim)
      System.out.println("[CompilerBaseClass!ELS] In�cio: ELS := ELSS (e ELSS)* | ! ELS");
    Vector vetBlocosEs = new Vector(); // para anotar comandos com blocos de E (&&) ap�s processamento total do ELS()
    boolean algumE = false;
    try {
      // ! ELS
      if (tipo(num_item)==Elemento.OUTROS && item(num_item).equals( "!" )) {
        if (Configuracao.listaAnaSim)
          System.out.println("[CompilerBaseClass!ELS] Reconhecido '!'");
        avanca_item();
        if (ELS()) { // inverte valor de AC: cAC>0 => AC <- -cAC
                     //                      cAC<0 => AC <- -cAC
                     //                      cAC=0 => AC <- 1
          int pM = programa.getMemoria();             // pega endere�o da �lt. instru��o, pM;
                                                      //                     ; X = cAC
          programa.adicionaComando("6"+(pM+6));       // cAC>0 => v� para R  ; X>0, v� para local onde far� X<-(-X)
          programa.adicionaComando("4--1");           // AC <- - cAC
          programa.adicionaComando("6"+(pM+7));       // cAC>0 => v� para R  ; -X>0, v� para fim (AC j� est� com -X)
          programa.adicionaComando("0-1");            // AC <- 1             ; X==0, fique com 1
          programa.adicionaComando("9"+(pM+7));       // v� p/ fim           ;
          programa.adicionaComando("4--1");           // AC <- - cAC         ; X>0, fa�a AC <- -X
          return true;
          }
        else {
          System.out.println("[CompilerBaseClass!ELS] Erro: esperava ELS ap�s '!'");
          return false;
          }
        }
      if (ELSS()) {
        if (Configuracao.listaAnaSim)
          System.out.println("[CompilerBaseClass!ELS] Reconhecido 'ELSS'");
        while (tipo(num_item)==Elemento.OUTROS && item(num_item).equals( "&&" )) {
          if (Configuracao.listaAnaSim)
             System.out.println("[CompilerBaseClass!ELS] Reconhecido '&&'");
          algumE = true;

          // C�digo:
          int pM = programa.getMemoria();             // pega endere�o da �lt. instru��o, pM;
          programa.adicionaComando("6"+(pM+2));       // 6(pM+1)                    ; pule instru��o seguinte se EL=v
          vetBlocosEs.addElement(new Integer(pM+1));  // anota n�m. da instru��o ao final para ser substituida
          programa.adicionaComando("9ee");            // 9ee                        ; ap�s todos os Es troque (fim: EL=f)

          avanca_item();
          if (ELSS()) {
             if (Configuracao.listaAnaSim)
                System.out.println("[CompilerBaseClass!ELS] Reconhecido 'ELSS'");
             }
          else {
             System.out.println("[CompilerBaseClass!ELS] Erro: esperava mais um 'ELSS'");
             return false;
             }
          } // while (tipo(num_item)==Elemento.OUTROS && item(num_item).equals( "&&" ))

        // Substitua em vetBlocosEs
        if (algumE) {
          int pM = programa.getMemoria();         // pega endere�o da �lt. instru��o, pM;
          for (int i=0; i<vetBlocosEs.size(); i++) {
              int posInstrE = ((Integer)vetBlocosEs.elementAt(i)).intValue();
              programa.substituaComando("9"+pM,posInstrE); //
              if (Configuracao.listaAnaSim)
                 System.out.println("\n[CompilerBaseClass!EXPLOG] substituido \"9ee\" por \"9"+pM+"\" na pos. mem�ria "+posInstrE);
              }
          }

        return true;
        } // if (ELSS())
      else {
        System.out.println("[CompilerBaseClass!ELS] Erro: esperava ao menos um ELSS");
        return false;
        }
    } catch (java.lang.ArrayIndexOutOfBoundsException aobe) {
       pilhaDeInformacoes = pilhaDeInformacoes.concat("\n"+Bundle.msg("msgCompErrELfinal")+": "+aobe.toString()); // EXPLOG - Erro: final de cadeia
       return false;
       }
    } // boolean ELS()


  // Reconhece: [+|-] T* [+|-|ou] (ou � '||')
  boolean EA () {
    if (Configuracao.listaAnaSim)
      System.out.println("[CompilerBaseClass!EA] inicio:  [+|-] T* [+|-|ou] (ou � '||')");
    int tipoAnt = -1; // para testar se � poss�vel interpretar como Exp. L�gica (com ||)
    boolean menos_unario = false;
    boolean primeiro_T   = true;
    String codigoParcial = null;
    //LinkedList posicoes = new LinkedList();
    ListaLigada listaCodigosTemp = new ListaLigada();
    boolean algumOR = false;
    try {
      // Menos (mais) un�rio
      if (tipo(num_item)==Elemento.OUTROS &&
          (item(num_item).equals( "+" ) || item(num_item).equals( "-" )) ) {
         // => � operador un�rio
         //if ( menos_unario ) {
         // Para empilhar o -1 ou 1
         // listaCodigosTemp.add( new Integer( programa.getMemoria() ) );
         // System.out.println("[CompilerBaseClass!EA] un�rio: "+item(num_item)+" "+tipo(num_item));
         if ( item(num_item).equals( "-" ) )
            //- programa.empilha( String.valueOf( -1 ) );
            menos_unario = true; // ap�s reconhecer T, fa�a -T (apenas no primeiro T)
         //- else
         //-    programa.empilha( String.valueOf( 1 ) );
         //- codigoParcial = new String( "4" );
         avanca_item();
         }

      // Restante da express�o
      char op = 'X';
      String var = "",
             strPosRelativa = "";
      int posRelativa = 0; // para pilha de execu��o (guardar cAC)
      while (true) {
         if ( T() ) {
            // se � a primeira vez que reconhece um T aqui, ent�o veja se teve um menos un�rio (se tiver inverta sinal)
            if (primeiro_T) {
               if (menos_unario) {
                  // Atencao ao uso de 'topo' gerenciado em 'T()', 'endPilhaExec' gerenciado em 'EA(),ELSS()': usados em 'EA()' e 'T()'
                  programa.adicionaComando("1$"+endPilhaExec+topo);     // $(EE)   <- cAC           ; usa pos. de mem. (mas n�o registra)
                  programa.adicionaComando("0--1");                     // AC      <- -1
                  programa.adicionaComando("4$"+endPilhaExec+topo);     // AC      <- cAC * c$(EE)  ;
                  endPilhaExec--;                                  //                          ; libera �lt. posi��o de mem�ria usada
                  menos_unario = false; // n�o permita inverter novamente
                  }
               }
            else  primeiro_T = false;

            if (Configuracao.listaAnaSim)
               System.out.println("[CompilerBaseClass!EA] --- reconhecido T(): item=<"+item(num_item)+"> "+tipo(num_item)+" op="+op);

            // op == 'X' => � operador relacional ou l�gico (||)
            if (op!='X') { // j� ocorreu algum operador antes do �ltimo T reconhecido => realize opera��o
               if (op == '+') {
                  // Atencao ao uso de 'topo' gerenciado em 'T()', 'endPilhaExec' gerenciado em 'EA(),ELSS()': usados em 'EA()' e 'T()'
                  endPilhaExec--;                                  //                          ; libera �lt. posi��o de mem�ria usada
                  programa.adicionaComando("2$"+endPilhaExec+topo); // AC      <- cAC + c$(EE-1); pega �lt. valor na pilha de mem.
                  }
               else
               if (op == '-') {
                  // Atencao ao uso de 'topo' gerenciado em 'T()', 'endPilhaExec' gerenciado em 'EA(),ELSS()': usados em 'EA()' e 'T()'
                  // AC <- c$(EE-1) - c$(EE)
                  programa.adicionaComando("1$"+endPilhaExec+topo);     // $(EE)   <- cAC           ; usa pos. de mem. (mas n�o registra)
                  programa.adicionaComando("0$"+(endPilhaExec-1)+topo); // AC      <- c$(EE-1)
                  programa.adicionaComando("3$"+endPilhaExec+topo);     // AC      <- cAC - c$(EE)
                  endPilhaExec--; //??posRelativa--;               //                          ; libera a �lt. pos. de mem. registrada
                  }
               else
               if  (op == 'o') { // ???
                  /*
                  endPilhaExec--;
                  programa.adicionaComando("2$"+(endPilhaExec-1)); // AC      <- cAC + c$(EE-1)
                  programa.adicionaComando("1$"+(endPilhaExec));   // $(EE)   <- cAC
                  programa.adicionaComando("4$"+(endPilhaExec));   // AC      <- cAC * c$(EE)
                  int posUltMem = programa.getMemoria();           //                         ; pega endere�o da �lt. instru��o, X
                  programa.adicionaComando("6$"+(posUltMem+4));    // cAC>0,     EPI <- X+4    ; desvia p/ gaveta de endere�o X
                  programa.adicionaComando("0-00");                // AC      <- 0             ; AC <- 0 eq. a falso
                  programa.adicionaComando("9$"+(endPilhaExec+5)); // EPI     <- X+5
                  programa.adicionaComando("0-01");                // AC      <- 1             ; AC <- 1 eq. a verdadeiro
                  */ }
               if (op == '|') { //
                    }
               else {
                  // ERRO
                  System.out.println("[CompilerBaseClass!EA] Erro em E: esperava um operador \'+\', \'-\' ou \'ou\'");// ERRO
                  }
               } // if (op!='X')

             if (tipo(num_item)== Elemento.OUTROS && (item(num_item).equals("+") || item(num_item).equals("-"))) {
                //N (item(num_item).equals("+") || item(num_item).equals("-") || item(num_item).equals("||")) ) {
                // Ainda tem operadores [+|-|ou], ent�o precisa empilhar este valor
                // Valor vindo de T fica no AC; colocar cAC numa posi��o de mem�ria (pilha de execu��o)
                tipoAnt = tipo(num_item-1); // Elemento: NUMERO = 0; COMANDOS = 1; VARIAVEL = 2;
                if ( item(num_item).equals( "+" ) ) {
                  op = '+';
                  // Atencao ao uso de 'topo' gerenciado em 'T()', 'endPilhaExec' gerenciado em 'EA(),ELSS()': usados em 'EA()' e 'T()'
                  programa.adicionaComando("1$"+endPilhaExec+topo);        // $(EE)  <- cAC              ; empilhe cAC
                  endPilhaExec++; // pilha de execu��o cresceu
                  avanca_item();
                  // programa.empilha( String.valueOf( -1 ) );
                  }
                else
                if (item(num_item).equals( "-" ) ) {
                  op = '-';
                  // Atencao ao uso de 'topo' gerenciado em 'T()', 'endPilhaExec' gerenciado em 'EA(),ELSS()': usados em 'EA()' e 'T()'
                  programa.adicionaComando("1$"+endPilhaExec+topo);        // $(EE)  <- cAC              ; empilhe cAC
                  endPilhaExec++; // pilha de execu��o cresceu
                  avanca_item();
                  // programa.empilha( String.valueOf( 1 ) );
                  }
                } // if (tipo(num_item)== Elemento.OUTROS && item(num_item).equals( "+" ) || item(num_item).equals("-"))
             else {
                if (Configuracao.listaAnaSim)
                  System.out.println("[CompilerBaseClass!EA] --- reconhecido EA():= [+|-] T (+|-): <"+
                                      item(num_item)+"> "+tipo(num_item));
                return true;
                }
           } // if ( T() )
         else {
           if ( num_item >= itensLex.size() - 1 ) {
             // Erro: final de cadeia ap�s um operador un�rio (\'+\' ou \'-\'), esperava operando
             System.out.println("[CompilerBaseClass!EA] Erro, n�o reconhecido T "+item(num_item)+" "+tipo(num_item));
             pilhaDeInformacoes = pilhaDeInformacoes.concat("\n" + Bundle.msg("msgCompErrEmptyAfterOper"));
             return false;
             }
           op = 'X';
           }

         //avanca_item(); <- s� avan�a em terminais
         } // while (true)
       } catch(java.lang.ArrayIndexOutOfBoundsException aobe) {
         // Erro: final de cadeia apos Exp. Arit.
         String msgErr = Bundle.msg("msgCompErrEAend")+": "+aobe.toString(); //
         System.out.println("[CompilerBaseClass!EA] \n" + msgErr );
         pilhaDeInformacoes = pilhaDeInformacoes.concat("\n" + msgErr);
         aobe.printStackTrace();
         return false;
         }

    } // boolean EA()


  boolean finaliza (ListaLigada posicoes) {
    // O "programa.atualizaComandos(posicoes);" serve para trocar $ pela primeira posi��o de mem�ria dispon�vel, logo deve ser a
    // �ltima coisa a ser feita!!
    // reconhecido T
    if ( num_item >= itensLex.size() - 1 ) {
      copiaListaPosicoes(posicoes); // copia a lista "posicoes" no final da lista "listaPosicoes"
      return true;
      }

    if ( ! (tipo(num_item)== Elemento.OUTROS && (item(num_item).equals("+") || item(num_item).equals("-")) ) ) {
      // => pode ser um ')', '>'...'==', ';', etc...
      // O "programa.atualizaComandos(posicoes);" serve para trocar $ pela primeira posi��o de mem�ria dispon�vel, logo deve ser a
      // �ltima coisa a ser feita!!
      copiaListaPosicoes(posicoes); // copia a lista "posicoes" no final da lista "listaPosicoes"
      return true;
      }
    return false;
    }


  // Atencao ao uso de 'topo' gerenciado em 'T()', 'endPilhaExec' gerenciado em 'EA(),ELSS()': usados em 'EA()' e 'T()'
  //
  //     const : NUMERO
  //     operadores, op logicos, (, {, etc... : OUTROS
  //     variaveis: VARIAVEL
  //                                    C�digos
  //     +------------------------+     1: if (temFator) {
  //     !                    2   |           "1$topo"     ; $topo <- cAC
  // T ----> FATOR ---+---> * --->| 5         "0$(topo-1)" ; AC    <- $(topo-1)
  //              1   |       3   |           if (op=='*') "4$topo"   ; AC <- cAC * $topo
  //                  +---> / --->|           if (op=='/') "5$topo"   ; AC <- cAC / $topo
  //                  |       4   |           if (op=='&') {
  //                  |                +---> && -->|              "4$(topo-1)"  ; 0: ($(topo-1))�
  //                  |                          "6$(X+7)"     ; 1:
  //                  +--->||                    "0$(topo"     ; 2:
  //                                             "4$topo"      ; 3: ($topo)�
  //                                             "6$(X+7)"     ; 4:
  //                                             "0-0"         ; 5:
  //                                             "9$(X+8)"     ; 6:
  //                                             "0-1"         ; 7:
  //                                             }
  //                                           topo--; // desempilhe �ltimo AC empilhado (que estava em $(topo-1))
  //                                           }
  //                                       else temFator = true;
  //                                    2: op = '*';  3: op = '/';  4: op = '&';
  //                                    5: "1$topo" ;  empilhe atual AC
  //                                       topo++;
  //
  int topo = 0; // para pilha de execu��o, deslocamento relativo
  boolean T () {
    if (Configuracao.listaAnaSim)
      System.out.println("[CompilerBaseClass!T] in�cio ");
    String item_op = null;
    //String codigoParcial = null;
    //ListaLigada posicoes = new ListaLigada();
    char op = 'X';
    boolean temFator = false; // para indicar se j� ocorreu algum FATOR: ou seja, passou por um operador '*', '/' ou '&&'

    try {

      while ( true ) {
        //System.out.println("[CompilerBaseClass!T] topo="+topo+", item <"+item(num_item)+"> ? "+tipo(num_item)+" num_item="+num_item);
        if ( FATOR() ) {
          // � operando, s� avan�a em reconhecimento de terminais!!! nunca em n�o terminais
          //System.out.println("[CompilerBaseClass!T] ap�s FATOR(): temFator=" + temFator +"  <"+item(num_item)+"> num_item="+num_item);
          // 'Concatena' os comandos que ID empilhou junto com estes de T.
          if (temFator) { // j� passou por algum FATOR() antes do �ltimo (logo j� tem FATOR op FATOR)
             // Atencao ao uso de 'topo' gerenciado em 'T()', 'endPilhaExec' gerenciado em 'EA(),ELSS()': usados em 'EA()' e 'T()'
             programa.adicionaComando("1$"+endPilhaExec+topo);      // empilha AC
             programa.adicionaComando("0$"+endPilhaExec+(topo-1));  // pega AC anteriormente empilhado
             if (op=='*') programa.adicionaComando("4$"+endPilhaExec+topo); //   ; AC <- cAC * $topo
             if (op=='/') programa.adicionaComando("5$"+endPilhaExec+topo); //   ; AC <- cAC / $topo
             // atencao ao uso de 'topo' gerenciado em 'T()'
             topo--; // desempilhe �ltimo AC empilhado (que estava em $(topo-1));
             }
          else temFator = true;

          item_op = item(num_item);

          // Reconhece: [*|/]  X cortei |&&
          if ( tipo(num_item) == Elemento.OUTROS &&
               (item_op.equals("*") || item_op.equals("/")) ) {
             // 5: empilhe atual AC
             // Atencao ao uso de 'topo' gerenciado em 'T()', 'endPilhaExec' gerenciado em 'EA(),ELSS()': usados em 'EA()' e 'T()'
             programa.adicionaComando("1$"+endPilhaExec+topo);  // "$topo <- cAC";
             topo++; // atencao ao uso de 'topo' gerenciado em 'T()'

             // 2, 3, ou 4: � operador
             if ( item_op.equals( "*" ) ) op = '*';
             else
             if ( item_op.equals( "/" ) ) op = '/';
             else { // N
               if (Configuracao.listaAnaSim)
                  System.out.println("[CompilerBaseClass!T] --- reconhecido FATOR <"+item((num_item-1))+"> ");
                  return true; //N
                  }
             avanca_item();
             }
           else {
             int n = num_item-1;
             if (Configuracao.listaAnaSim)
                System.out.println("[CompilerBaseClass!T] --- reconhecido FATOR <"+item(n)+"> n="+n+" num_item="+num_item);
             //avanca_item(); <- s� avan�a em reconhecimento de terminais!!! nunca em n�o terminais
             return true;
             }
           }
         else {
           System.out.println("[CompilerBaseClass!T] erro, n�o encontei um FATOR -> num_item="+num_item);
           //for (int i=0;i<80;i++) System.out.print(".");
           return false;
           }
         //posicoesTemporarias.empilha( posicoes );
         } // while ( true )

    } catch (java.lang.ArrayIndexOutOfBoundsException aobe) {
      //  Erro: final de cadeia ap�s fator de Express�o Aritm�tica
      String msgErr = Bundle.msg("msgCompErrEAendT")+": "+aobe.toString(); //
      System.out.println("[CompilerBaseClass!EA] \n" + msgErr );
      pilhaDeInformacoes = pilhaDeInformacoes.concat("\n" + msgErr); // "T - Erro: final de cadeia"
      aobe.printStackTrace();
      }

    return false;
    } // boolean T()


  /*
   * FATOR --+--> NUM --------------------->+--->||     ?    AC <- NUM
   *         |                              |
   *         +--> ID  --------------------->|           OEE  AC <- cEE   EE endere�o var. NUM
   *         |                              |
   *         +-->  (  ---> [ELSS] ---> ) --->|
   */
  boolean FATOR () {
    if (Configuracao.listaAnaSim)
      System.out.println("[CompilerBaseClass!FATOR] inicio "+item(num_item)+" "+tipo(num_item));
    if ( tipo(num_item) == Elemento.NUMERO) {
      if (Configuracao.listaAnaSim)
         System.out.println("[CompilerBaseClass!FATOR] --- reconhecido NUMERO <"+item(num_item-1)+"> "+tipo(num_item-1));
      programa.adicionaComando("0-"+item(num_item));  // AC <- N
      avanca_item();
      return true;
      }
    else
    if ( tipo(num_item) == Elemento.VARIAVEL ) {
      //System.out.println("[CompilerBaseClass!FATOR] --- reconhecido IDENT <"+item(num_item)+"> "+tipo(num_item)+" num_item="+num_item);
      // coloca var. numa posi��o de mem�ria
      String strItemVar = item(num_item);
      programa.aloca(strItemVar); // aloca espa�o para a var. ou pega um end. EE j� existente
      programa.adicionaComando( "0" + programa.enderecoDaVariavel(strItemVar)); // adiciona o comando correspondente AC <- cEE
      avanca_item();
      return true;
      }
    else
    if ( item(num_item).equals("(") ) {  // o '(' n�o est� com valor "OUTROS"!!
      if (Configuracao.listaAnaSim)
        System.out.println("[CompilerBaseClass!FATOR] reconhecida <"+item(num_item)+"> "+tipo(num_item));
      avanca_item();
      if ( ELSS() ) {
        if (Configuracao.listaAnaSim)
           System.out.println("[CompilerBaseClass!FATOR] reconhecida ELSS "+item(num_item)+" "+tipo(num_item));
        //if (tipo(num_item) == Elemento.OUTROS && item(num_item).equals(")") ) {
        if ( item(num_item).equals(")") ) {
           //System.out.println("[CompilerBaseClass!FATOR] --- reconhecido \"( ELSS )\" <"+item(num_item)+"> "+tipo(num_item));
           avanca_item(); // s� terminal avan�a
           return true;
           }
        else {
           System.out.println("[CompilerBaseClass!FATOR] --- N�O reconhecido o fecha de \"(ELSS)\" "+item(num_item)+" "+tipo(num_item));
           return false;
           }
        }
      else {
        System.out.println("[CompilerBaseClass!FATOR] N�O ELSS ap�s um \"( "+item(num_item)+" "+tipo(num_item));
        return false;
        }
      } // if ( tipo(num_item) == Elemento.OUTROS && item(num_item).equals("(") )
    else
    if ( tipo(num_item) == Elemento.OUTROS && item(num_item).equals("!") ) {
      if (Configuracao.listaAnaSim)
        System.out.println("[CompilerBaseClass!FATOR] --- reconhecido ! <"+item(num_item)+"> "+tipo(num_item));
      avanca_item();
      if ( FATOR() ) {
        //System.out.println("[CompilerBaseClass!FATOR] --- reconhecida ! FATOR "+item(num_item-1)+" "+tipo(num_item-1));
        return true;
        }
      else {
        System.out.println("[CompilerBaseClass!FATOR] --- N�O reconhecido FATOR de \"!FATOR\" "+item(num_item-1)+" "+tipo(num_item-1));
        return false;
        }
      } // if ( tipo(num_item) == Elemento.OUTROS && item(num_item).equals("!") )
    else {
      //System.out.println("[CompilerBaseClass!FATOR]---N�OreconhecidoNUM|ID|(ELSS)|!FATOR:item="+item(num_item)+"tipo="+tipo(num_item));
      System.out.println("[CompilerBaseClass!FATOR] --- N�O reconhecido NUM|ID|(ELSS): item="+item(num_item)+" tipo="+tipo(num_item));
      avanca_item(); // ignore este item l�xico, pegue o pr�ximo
      return false;
      }
    } // boolean FATOR()

  // ELSS --> EA --+--> == ---+--> EA -->||    [ E ]                                    ; AC fica c/ res. desta expr.
  //               |          ^                mem0 <- cAC
  //               +--> <= -->|                [ E ]                                    ; AC fica c/ res. desta expr.
  //               ...                         mem1 <- cAC
  //               +-->||                      AC   <- mem0                             ; pega res. da prim. expr.
  //                                           AC   <- cAC - mem1                       ; pega res. da seg. expr.
  //                                           (desvio de acordo c/ operador relacional)
  boolean ELSS () {
    if (Configuracao.listaAnaSim)
      System.out.println("[CompilerBaseClass!ELSS] inicio "+item(num_item)+" "+tipo(num_item));
    boolean temOpRel = false;
    ListaLigada posicoes;
    String tipoComp; // 0:==; 1:<=; 2:>=; 3:!=; 4:>; 5:<
    int posRelativa = 0; // para pilha de execu��o (guardar cAC)

    if ( EA() ) { // acabou de calcular primeira express�o aritm�tica
      if (Configuracao.listaAnaSim)
        System.out.println("[CompilerBaseClass!ELSS] --- reconhecido EA  <"+item(num_item)+"> "+tipo(num_item));
      //if ( tipo(num_item) == Elemento.OUTROS && item(num_item).equals("==") ) {
      if ( item(num_item).equals("==") ) {
        if (Configuracao.listaAnaSim)
           System.out.println("[CompilerBaseClass!ELSS] --- reconhecido ELSS := \"EA ==\" "+item(num_item)+" tipo="+tipo(num_item));
        temOpRel = true; tipoComp = "=="; //0;
        avanca_item();
        }
      else
      if ( item(num_item).equals("<=") ) {
        if (Configuracao.listaAnaSim)
           System.out.println("[CompilerBaseClass!ELSS] --- reconhecido ELSS := \"EA <=\" "+item(num_item)+" tipo="+tipo(num_item));
        temOpRel = true; tipoComp = "<="; //1;
        avanca_item();
        }
      else
      if ( item(num_item).equals(">=") ) {
        if (Configuracao.listaAnaSim)
           System.out.println("[CompilerBaseClass!ELSS] --- reconhecido ELSS := \"EA >=\" "+item(num_item)+" tipo="+tipo(num_item));
        temOpRel = true; tipoComp = ">="; //2;
        avanca_item();
        }
      else
      if ( item(num_item).equals("!=") ) {
        if (Configuracao.listaAnaSim)
           System.out.println("[CompilerBaseClass!ELSS] --- reconhecido ELSS := \"EA !=\" "+item(num_item)+" tipo="+tipo(num_item));
        temOpRel = true; tipoComp = "!="; //3;
        avanca_item();
        }
      else
      if ( item(num_item).equals(">") ) {
        if (Configuracao.listaAnaSim)
           System.out.println("[CompilerBaseClass!ELSS] --- reconhecido ELSS := \"EA >\" "+item(num_item)+" tipo="+tipo(num_item));
        temOpRel = true; tipoComp = ">"; //4;
        avanca_item();
        }
      else
      if ( item(num_item).equals("<") ) {
        if (Configuracao.listaAnaSim)
           System.out.println("[CompilerBaseClass!ELSS] --- reconhecido ELSS := \"EA <\" "+item(num_item)+" tipo="+tipo(num_item));
        temOpRel = true; tipoComp = "<"; //5;
        avanca_item();
        }
      else {
        if (Configuracao.listaAnaSim)
           System.out.println("[CompilerBaseClass!ELSS] --- reconhecido ELSS := EA  <"+item(num_item)+"> tipo="+tipo(num_item));
        temOpRel = false;
        tipoComp = "";
        } //

      if (temOpRel) {
        programa.adicionaComando("1$"+endPilhaExec);              // c$(EE)      <- cAC         ; mem0 <- cAC
        endPilhaExec++; // pilha de execu��o cresceu
        //N Atencao ao uso de 'topo' gerenciado em 'T()', 'endPilhaExec' gerenciado em 'EA(),ELSS()': usados em 'EA()' e 'T()'
        //N precisa? topo++;

        if (Configuracao.listaAnaSim)
           System.out.println("[CompilerBaseClass!ELSS] tenta reconhecer nova EA: "+item(num_item)+" "+tipo(num_item));

        //posRelativa++;                                          //                            ; mais uma pos. de mem. usada
        if ( EA() ) { // E OpRel E
           if (Configuracao.listaAnaSim)
              System.out.println("[CompilerBaseClass!ELSS] --- reconhecido ELSS := \"EA op EA\" "+item(num_item)+" "+tipo(num_item));
           programa.adicionaComando("1$"+endPilhaExec);           // c$(EE)      <- cAC         ; mem0 <- cAC
           // endPilhaExec++; // pilha de execu��o cresceu

           programa.adicionaComando("0$"+(endPilhaExec-1));       // AC          <- c(EE)       ; AC   <- mem0
           programa.adicionaComando("3$"+(endPilhaExec  ));       // AC          <- cAC-c(EE+1) ; mem1 <- mem0 - mem1
           //System.out.println("[CompilerBaseClass!ELSS] "+": 1$"+(endPilhaExec  )+"\n              "
           //                                   +": 0$"+(endPilhaExec-1)+"\n              "+": 3$"+(endPilhaExec));
           endPilhaExec--; // pilha de execu��o: libera 1 posi��o (do primeiro "E", em "E OpRel E")

           completaExprLog(tipoComp); // aqui est� o c�digo para o operador relacional

           return true;
           }
        else {
           System.out.println("[CompilerBaseClass!ELSS] --- N�O reconhecido EA final em ELSS!! "+item(num_item)+" "+tipo(num_item));
           return false;
           }
        } // if (temOpRel)
      else {
        if (Configuracao.listaAnaSim)
           System.out.println("[CompilerBaseClass!ELSS] --- reconhecido  ELSS := EA "+item(num_item)+" "+tipo(num_item));
        //avanca_item(); <- s� avan�a em terminais
        return true;
        }
      } // if ( EA() )

    else { // n�o reconhecido EA()
      System.out.println("[CompilerBaseClass!ELSS] N�O reconhecido  ELSS := EA [op EA] "+item(num_item)+" "+tipo(num_item));
      return false;
      }
    } // boolean ELSS()


  void completaExprLog (String opR) {
    int tempMem = programa.getMemoria(); // n�mero de instru��es at� o momento
    if (opR.equals("==")) { // :---: "==" eq. "n�o(AC�>0)"
      programa.adicionaComando("1$0");                            // F+0          <- cAC        ; 0. m0 <- cAC
      programa.adicionaComando("4$0");                            // AC           <- cAC * c(F) ; 1. AC <- cAC * m0
      programa.adicionaComando("6"+(tempMem+5));                  // cAC>0 => EPI <- X+3        ; 2. cAC > 0 => goto 5
      programa.adicionaComando("0-1");                            // AC           <- 0          ; 3. AC <- 1
      programa.adicionaComando("9"+(tempMem+6));                  // EPI          <- X+4        ; 4. goto 6
      programa.adicionaComando("0-0");                            // AC           <- 1          ; 5. AC <- 0
      }
    else
    if (opR.equals("<=")) { // :---: "<=" eq. "n�o(AC>0)"
      programa.adicionaComando("6"+(tempMem+3));                  // cAC>0 => EPI <- X+3        ; 0. cAC > 0 => goto 3
      programa.adicionaComando("0-1");                            // AC           <- 0          ; 1. AC <- 1
      programa.adicionaComando("9"+(tempMem+4));                  // EPI          <- X+4        ; 2. goto 4
      programa.adicionaComando("0-0");                            // AC           <- 1          ; 3. AC <- 0
      }
    else
    if (opR.equals(">=")) { // :---: ">=" eq. "(AC>0) ou n�o(AC�>0)"
      programa.adicionaComando("6"+(tempMem+4));                  // cAC>0 => EPI <- X+4        ; 0. cAC > 0 => goto 4
      programa.adicionaComando("1$0");                            // F+0          <- cAC        ; 1. m0 <- cAC
      programa.adicionaComando("4$0");                            // AC           <- cAC * c(F) ; 2. AC <- cAC * m0
      programa.adicionaComando("6"+(tempMem+6));                  // cAC>0 => EPI <- X+6        ; 3. cAC > 0 => goto 6
      programa.adicionaComando("0-1");                            // AC           <- 0          ; 4. AC <- 1
      programa.adicionaComando("9"+(tempMem+7));                  // EPI          <- X+7        ; 5. goto 7
      programa.adicionaComando("0-0");                            // AC           <- 1          ; 6. AC <- 0
      }
    else
    if (opR.equals("!=")) { // :---:"!=" eq. "AC�>0"
      programa.adicionaComando("1$0");                            // F+0          <- cAC        ; 0. m0 <- cAC
      programa.adicionaComando("4$0");                            // AC           <- cAC * c(F) ; 1. AC <- cAC * m0
      programa.adicionaComando("6"+(tempMem+5));                  // cAC>0 => EPI <- X+5        ; 2. cAC > 0 => goto 5
      programa.adicionaComando("0-0");                            // AC           <- 1          ; 3. AC <- 0
      programa.adicionaComando("9"+(tempMem+6));                  // EPI          <- X+6        ; 4. goto 6
      programa.adicionaComando("0-1");                            // AC           <- 0          ; 5. AC <- 1
      }
    else
    if (opR.equals(">")) {  // :---: ">"   eq. "AC>0"
      //int tempMem = programa.getMemoria(); // n�mero de instru��es at� o momento
      programa.adicionaComando("6"+(tempMem+3));                  // cAC>0 => EPI <- X+3        ; 0. cAC > 0 => goto 5
      programa.adicionaComando("0-0");                            // AC           <- 0          ; 1. AC <- 0
      programa.adicionaComando("9"+(tempMem+4));                  // EPI          <- X+4        ; 2. goto 4
      programa.adicionaComando("0-1");                            // AC           <- 1          ; 3. AC <- 1
      }
    else
    if (opR.equals("<")) {  // :---: "<"   eq. "(-AC)>0"
      //- System.out.println("[CompilerBaseClass!] sinal \'<\': 1$-1; 4$0; 6"+(tempMem+5)+"; 0-0; 9"+(tempMem+6)+"; 0-1");
      programa.adicionaComando("1$0");                            // x0           <- cAC        ; 0. x0 <- cAC
      programa.adicionaComando("0--1");                           // F+0          <- cAC        ; 1. m0 <- -1
      programa.adicionaComando("4$0");                            // AC           <- cAC * c(F) ; 2. AC <- cAC * m0
      programa.adicionaComando("6"+(tempMem+6));                  // cAC>0 => EPI <- X+6        ; 3. cAC > 0 => goto 6
      programa.adicionaComando("0-0");                            // AC           <- 1          ; 4. AC <- 0
      programa.adicionaComando("9"+(tempMem+7));                  // EPI          <- X+7        ; 5. goto 7
      programa.adicionaComando("0-1");                            // AC           <- 0          ; 6. AC <- 1
      }
    else System.out.println("[CompilerBaseClass!completaExprLog] erro, esperava \'==\', \'<=\', \'>=\', \'!=\', \'>\' ou \'>\' entre [E] e [E]");

    } // void completaExprLog(String opR)


  /*
   * Search for "Nonterminals" (commmands like "if" and "while")
   */
  boolean C () { // chamado em: icg.compilador.CompilerBaseClass.item(CompilerBaseClass.java:54)
    if (Configuracao.listaAnaSim)
      System.out.println("[CompilerBaseClass!C] inicio: <"+item(num_item)+"> "+tipo(num_item)+" == "+Elemento.COMANDOS+" ? <" +
                 ((Elemento)itensLex.elementAt(num_item)).obj()+"> tipo="+((Elemento)itensLex.elementAt(num_item)).tipo());

    try {
    try {

      // se o item lido � um "if"
      if ( tipo(num_item) == Elemento.COMANDOS && item(num_item).equals(Bundle.msg("cmdIf")) ) { // "if"
         avanca_item(); // another "Terminal" recognized: "if"

         if ( !IF() ) { // "Nonterminal" item recognition failed: it not a "(EL)", i.e., it is not a "logical expression" for "if (EL)"
           pilhaDeInformacoes = pilhaDeInformacoes.concat( "\n" + Bundle.msg("msgCompErrELexpect")); // Erro: Era esperado Express�o l�gica.
           return false;
           }
         else {
           programa.esvaziaAC();
           return true;
           }
         }

       // se o item lido � um "while"
       else if ( tipo(num_item) == Elemento.COMANDOS && item(num_item).equals( Bundle.msg("cmdWhile") ) ) { // "while"
         avanca_item(); // another "Terminal" recognized: "while"

         if ( !WHILE() ) { // "Nonterminal" item recognition failed: it not a "logical expression" for "while"
            pilhaDeInformacoes = pilhaDeInformacoes.concat( "\n" + Bundle.msg("msgCompErrELexpect")); // Erro: Era esperado Express�o l�gica.
            return false;
            }
         else {
            programa.esvaziaAC();
            return true;
            }
         }

       // identifica a parte da leitura de vari�veis
       else if ( tipo(num_item) == Elemento.COMANDOS && (item(num_item).equals( Bundle.msg("cmdRead") ) ) ) { // "read"/"leia"
         avanca_item(); // another "Terminal" recognized: "read"

         if ( !IO_LE() ) { // "Nonterminal" item recognition failed: it not a "variable" for "read(...)"
            pilhaDeInformacoes = pilhaDeInformacoes.concat("\n" + Bundle.msg("msgCompErrParamVar")); // Erro: Par�metros incorretos, era esperada uma vari�vel
            return false;
            }
         else if ( num_item <= itensLex.size() - 1 && tipo(num_item) == Elemento.OUTROS && item(num_item).equals(";")) {
            avanca_item();
            return true;
            }
         else {
            pilhaDeInformacoes = pilhaDeInformacoes.concat("\n" + Bundle.msg("msgCompErrCommEnd")); // Erro: Era esperado \';\' apos comando
            return false;
             }
         }

       // identifiva a parte de escrita de variaveis
       else if (tipo(num_item) == Elemento.COMANDOS &&
                item(num_item).equals( Bundle.msg("cmdWrite") ) ) { // "write"/"escreve"
         //System.out.println("[CompilerBaseClass!C] --- reconhecido C := \""+Bundle.msg("cmdWrite")+"\": "+item(num_item)+" "+num_item);
         avanca_item(); // another "Terminal" recognized: "write"

         if ( !IO_ESCREVA() ) { // "Nonterminal" item recognition failed: it not a "variable" for "write(...)"
            pilhaDeInformacoes = pilhaDeInformacoes.concat("\n" + Bundle.msg("msgCompErrParamVar")); // Erro: Par�metros incorretos, era esperada uma vari�vel
            return false;
            }
         else if ( num_item <= itensLex.size() - 1 && tipo(num_item) == Elemento.OUTROS && item(num_item).equals(";")) {
            if (Configuracao.debugOptionAL2)
              System.out.println("[CompilerBaseClass!C] --- reconhecido \""+Bundle.msg("cmdWrite")+"(var)\": "+item(num_item)+" "+num_item);
            avanca_item();
            return true;
            }
         else {
            pilhaDeInformacoes = pilhaDeInformacoes.concat("\n" + Bundle.msg("msgCompErrCommEnd")); // Erro: Era esperado \';\' apos comando
            System.out.println("[CompilerBaseClass!C] ERRO, esperava comando ou \';\' no lugar de \""+item(num_item)+"\": "+num_item);
            System.out.println("[CompilerBaseClass!C]   num_item="+num_item+" itensLex.size()="+itensLex.size()+" "
                               +tipo(num_item)+"=="+Elemento.OUTROS+" "+item(num_item)+"=="+";");
            return false;
            }
         }

       // se � operando
       else if ( tipo(num_item) == Elemento.VARIAVEL ) {
         //if (Configuracao.debugOptionAL2)
         if (Configuracao.listaAnaSim)
           System.out.println("[CompilerBaseClass!C] --- reconhecido \"var\": "+item(num_item)+" "+num_item);

         String strItemVar = item(num_item);
         programa.aloca( strItemVar );
         avanca_item();

         if ( tipo(num_item) == Elemento.OUTROS && item(num_item).equals( "=" )) {
           if (Configuracao.listaAnaSim)
              System.out.println("[CompilerBaseClass!C] --- reconhecido \"=\": "+item(num_item)+" "+num_item);
           avanca_item(); // another "Terminal" recognized: "=" (attribution)

           if ( !EA() ) { //"Nonterminal" item recognition failed: it not a "arithmetic expression" for <atribution> "="
              pilhaDeInformacoes = pilhaDeInformacoes.concat("\n" + Bundle.msg("msgCompErrExpectEA")); // Erro: Era esperada uma express�o aritmetica (para variavel a esquerda)
              return false;
              }
           else {
             if ( num_item <= itensLex.size() - 1 && tipo(num_item) == Elemento.OUTROS && item(num_item).equals(";")) {
                programa.ACparaEE( strItemVar );
                programa.esvaziaAC();
                avanca_item();
                return true;
                }
             else {
                pilhaDeInformacoes = pilhaDeInformacoes.concat("\n" + Bundle.msg("msgCompErrCommEnd")); // Erro: Era esperado \';\' apos comando
                return false;
                }
              } // else
           } //if  ( tipo(num_item) == Elemento.OUTROS && item(num_item).equals( "=" ))

         else { //  if ( tipo(num_item) == Elemento.VARIAVEL )
           int numAux = num_item-1;
           String [] strMsgs = { item(numAux), ""+numAux };
           String msgErr = Bundle.msg("msgCompErrExpectComm") + "! " + // Erro: Era esperado um comando
                           Bundle.msgComVar("msgCompErrFound","OBJ",strMsgs); // Encontrei um \"<item(n)>"\ (no item <n>)
           pilhaDeInformacoes = pilhaDeInformacoes.concat("\n" + msgErr);
           System.out.println("[CompilerBaseClass!C] " + msgErr);
           System.out.println("[CompilerBaseClass!C] num_item="+num_item+" item(num_item)="+item(num_item));
           listItensUntil(num_item); // list all itens until item 'num_item'
           return false;
           }

         } // else if ( tipo(num_item) == Elemento.VARIAVEL )


        pilhaDeInformacoes = pilhaDeInformacoes.concat("\n" + Bundle.msg("msgCompErrExpectVar")); // Erro: era esperada uma vari�vel       
        System.out.println("[icg.compilador.CompilerBaseClass!C] " + Bundle.msg("msgCompErrExpectVar") + "in: "+item(num_item));
        return false;

      } catch (java.lang.ArrayIndexOutOfBoundsException aobe) {
        pilhaDeInformacoes = pilhaDeInformacoes.concat("\n" + Bundle.msg("msgCompErrNotExpectFinal")); // Erro: nao era esperado o final de cadeia
        System.err.println("C: " + Bundle.msg("msgCompErrNotExpectFinal"));
        aobe.printStackTrace();
        return false;
        }

     } catch (java.lang.ClassCastException cce) {
       // ??????????????????????????????????????????????????????????????????????????????????????????????
       // este erro � provocado principalmente por usar um s� vetor, "itensLex", para armazenar o item e tamb�m seu
       // tipo, da metade p/ frente, isso � muito "desestruturado"
       String strAux = Bundle.msg("msgCompErrExpectTypeObj"); // Erro: confusao entre tipo de objeto e o objeto em si...
       pilhaDeInformacoes = pilhaDeInformacoes.concat("\n" + strAux);
       System.out.println("[CompilerBaseClass!C] " + strAux + " in type with number="+num_item+" "+
                          itensLex.elementAt(num_item)+" "+itensLex.elementAt(itensLex.size()/2+num_item));
       cce.printStackTrace();
       return false;
       }

    } // boolean C()


  // Search for a "NomTerminal" to complete an "if": search for "(EL)"
  boolean IF () {
    ListaLigada posicoes;
    int tempMem; //

    if (Configuracao.listaAnaSim)
      System.out.println("[CompilerBaseClass!IF] in�cio - "+item(num_item)+": "+tipo(num_item));

    try {

     if ( tipo(num_item) == Elemento.OUTROS && item(num_item).equals("(") ) {
        avanca_item();
        if ( EXPLOG() ) {
          //achou uma express�o l�gica
          if (tipo(num_item) != Elemento.OUTROS || !item(num_item).equals(")") ) {
            // Erro: era esperado \')\' apos expresao logica
            pilhaDeInformacoes = pilhaDeInformacoes.concat("\n" + Bundle.msg("msgCompErrExpectCloseParEL"));
            return false;
            }
          else { // achou um ")"
            // Gera c�digo correspondente         |     |
            // ao "if EL", para true e false      +-----+
            //                                     cAC > 0 --------+      X
            //                           +-----------o             |      X+1
            //                           |         +-----+ <-------+      X+2
            //                           |         |true |
            //                           |         +-----+           ___
            //                           |            o------------+    +
            //                           +-------> +-----+         |    | este bloco s� existir�
            //                                     |false|         |    | se houver um "else" (e
            //                                     +-----+         |    | seu bloco)
            //                                     +-----+<--------+  __+
            tempMem = programa.getMemoria();                    // X n�mero de instru��es at� o momento
            programa.adicionaComando("6"+(tempMem+2));          // cAC>0 => EPI <- X+2        ; X   endere�os
            programa.adicionaComando("9ee");                    // EPI          <- ee         ; "ee" vai ser substituido ap�s C()
            // ap�s o cmd C do "true", ser� preciso voltar a esta instru��o em "programa.programaExecutavel.elementAt(tempMem+1)"

            avanca_item(); // reconhecido "(EXPLOG)"
            //System.out.println("[CompilerBaseClass!IF] ---reconhecido \"if(EXPLOG)\": "+item(num_item)+" "+num_item+" | posicoes="+posicoes);

            if (tipo(num_item) == Elemento.OUTROS && item(num_item).equals("{") ) {
               //System.out.println("[CompilerBaseClass!IF] --- reconhecido \"if (EXPLOG) {\": "+item(num_item)+" "+num_item);
               avanca_item();
               while (true) {
                 if ( C() ) { // gera c�digo do comando C
                    //System.out.println("[CompilerBaseClass!IF] --- reconhecido \"if (EXPLOG) { C();\": "+item(num_item)+" "+num_item);
                    if ( tipo(num_item) == Elemento.OUTROS && item(num_item).equals("}") ) {
                       //System.out.println("[CompilerBaseClass!IF] --- reconhecido \"if (EXPLOG) { C(); }\": "+item(num_item)+" "+num_item);
                       avanca_item();
                       break; // pode ser que este "if" ainda tenha um "else"
                       }
                    }
                 else { // Erro (if): era esperado comando
                    pilhaDeInformacoes = pilhaDeInformacoes.concat("\n" + Bundle.msg("msgCompErrExpectComm"));
                    return false;
                    }
                 } // while
               }
             else {
               if ( !C() ) { // gera c�digo do comando C
                  int numAux = num_item-1;
                  String [] strMsgs = { item(numAux), ""+numAux };
                  String msgErr = Bundle.msgComVar("msgCompErrExpectComm","OBJ",strMsgs); // Erro: Era esperado um comando no lugar de \"<item(n)>\": item <n>
                  pilhaDeInformacoes = pilhaDeInformacoes.concat("\n" + msgErr);
                  return false;
                  }
               }

             // j� gerou c�digo do(s) comando(s) C, agora pegue o comando "9ee" inserido acima e substitua-o pelo end. correto
             // ap�s o cmd C do "true", ser� preciso voltar a esta instru��o em "programa.programaExecutavel.elementAt(tempMem+1)"
             int tempMem2 = programa.getMemoria();                // X n�mero de instru��es at� o momento

             //System.out.println("\n[CompilerBaseClass!IF](2) - - - - - - - - - > "+
             //(tempMem+1)+": "+programa.programaExecutavel().elementAt(tempMem+1)+"\n");
             programa.substituaComando("9"+(tempMem2+1),tempMem+1);// EPI     <- ee     ; pule o pr�x. cmd (q/ � desvio)
             programa.adicionaComando("9ee");                    // EPI       <- ee     ; "ee" vai ser substituido ap�s ELSE()
                                                                 //                     ; salta p/ ap�s comandos do ELSE()

             boolean jaSubstituido = false;
             if ( tipo(num_item) == Elemento.COMANDOS && item(num_item).equals( Bundle.msg("cmdElse") ) ) {
                avanca_item();
                if ( ELSE() ) {
                   int tempMem3 = programa.getMemoria();          // X n�mero de instru��es at� o momento
                   programa.substituaComando("9"+(tempMem3),tempMem2);// EPI  <- ee     ; "ee" recebe end. pr�x. instru��o
                   jaSubstituido = true;
                   }
                }
             if (!jaSubstituido) {
                programa.substituaComando("9"+(tempMem2+1),tempMem2);// EPI   <- ee      ; "ee" recebe end. pr�x. instru��o
                }

             return true;
             } // else

           } // if ( EXPLOG() )
         else {
           // n�o encontrou uma express�o l�gica.
           System.out.println("[CompilerBaseClass!IF] n�o � EXPLOG");
           pilhaDeInformacoes = pilhaDeInformacoes.concat( "\n" + Bundle.msg("msgCompErrELexpect")); // Erro: Era esperado Express�o l�gica
           return false;
           }
        } // if ( tipo(num_item) == Elemento.OUTROS && item(num_item).equals("(") )
      else {
        System.out.println("[icg.compilador.CompilerBaseClass!IF] erro 2: \"Erro: era esperado \')\'.\" em: "+item(num_item));
        // Erro: era esperado \')\' apos expresao logica
        pilhaDeInformacoes = pilhaDeInformacoes.concat("\n" + Bundle.msg("msgCompErrExpectCloseParEL"));
        return false;
        }
    } catch (java.lang.ArrayIndexOutOfBoundsException aobe) {
      pilhaDeInformacoes = pilhaDeInformacoes.concat("\n" + Bundle.msg("msgCompErrNotExpectFinal")); // Erro: nao era esperado o final de cadeia
      System.err.println("IF: " + Bundle.msg("msgCompErrNotExpectFinal"));
      aobe.printStackTrace();
      return false;
      }

    } // boolean IF()


  boolean ELSE () {
    try {
    if ( tipo(num_item) == Elemento.OUTROS && item(num_item).equals("{") ) {
      avanca_item();
      while (true) {
        if ( C() ) {
           if (tipo(num_item) == Elemento.OUTROS && item(num_item).equals("}") ) {
              avanca_item();
              break;
              }
           }
        else {
           pilhaDeInformacoes = pilhaDeInformacoes.concat("\n" + Bundle.msg("msgCompErrExpectComm") + "!"); // Erro: Era esperado um comando
           return false;
           }
        } //while
      }
      else {
        if ( !C() ) {
           pilhaDeInformacoes = pilhaDeInformacoes.concat("\n" + Bundle.msg("msgCompErrExpectComm") + "!"); // Erro: Era esperado um comando
           return false;
           }
        }
    } catch (java.lang.ArrayIndexOutOfBoundsException aobe) { // Erro: nao era esperado o final de cadeia
      pilhaDeInformacoes = pilhaDeInformacoes.concat("\n" + Bundle.msg("msgCompErrNotExpectFinal"));
      System.err.println("ELSE: " + Bundle.msg("msgCompErrNotExpectFinal"));
      aobe.printStackTrace();
      return false;
      }

    return true;
    } // boolean ELSE()


  // Reconhece: "while (EL) { comandos }"
  // C�digo:  x0 <- cAC                     X atual pos. desta instru��o na mem�ria
  //          AC <- cAC * x0
  //          cAC>0 ----------+
  //          ----------------|---+
  //          . <-------------+   |
  //          comandos <------+   |
  //          . <-----------------+

  boolean WHILE () {
    int desvioIncondicional,
        posInicial = programa.getMemoria(),    // n�mero de instru��es at� o momento (X)
        posFinal;                              //

    try {
      if ( tipo(num_item) == Elemento.OUTROS && item(num_item).equals("(") ) {
        avanca_item();
        if ( EXPLOG() ) { // traduz a express�o l�gica: AC = 0 <=> EL = false
           if ( tipo(num_item) != Elemento.OUTROS || !item(num_item).equals(")") ) {
	     // Erro: esperava um \')\' ap�s uma Express�o L�gica
             pilhaDeInformacoes = pilhaDeInformacoes.concat("\n" + Bundle.msg("msgCompErrExpectCloseParEL"));
             return false;
             }
           else {
             avanca_item();

             // Supoe que EXP_LOG jah deixou uma lista empilhada.
             // C�digo:  x0 <- cAC                     X atual pos. desta instru��o na mem�ria
             //          AC <- cAC * x0
             //          cAC>0 ----------+
             //          ----------------|---+
             //          . <-------------+   |
             //          comandos <------+   |
             //          . <-----------------+
             int posAtual = programa.getMemoria();    // para saber onde introduziu a inst. "9ee" (em "posAtual+3")
             String str = String.valueOf(posAtual+4); // para pular instru��o "9ee"
             if (str.length()<2) str = "0"+str;

             programa.adicionaComando( "1$0");  // $0 <- cAC
             programa.adicionaComando( "4$0");  // AC <- cAC * $0 (cAC�)
             programa.adicionaComando( "6"+str);
             programa.adicionaComando( "9ee");  //             instr. end. "posAtual+3": vai ser substituido mais tarde
             // System.out.println("\n ----> posAtual+2="+(posAtual+2));
             if ( tipo(num_item) == Elemento.OUTROS && item(num_item).equals("{") ) {
               avanca_item();

               while( true ) {  // traduz comandos subordinados ao "while"
                 if ( C() ) {
                    if ( tipo(num_item) == Elemento.OUTROS && item(num_item).equals("}") ) {
                       avanca_item();
                       break;
                       }
                    }
                 else { // Erro: Era esperado um comando
                    pilhaDeInformacoes = pilhaDeInformacoes.concat("\n" + Bundle.msg("msgCompErrExpectComm") + "!");
                    return false;
                    }
                 } // while

               }
             else {
               if ( !C() ) { // Erro: Era esperado um comando
                  pilhaDeInformacoes = pilhaDeInformacoes.concat("\n" + Bundle.msg("msgCompErrExpectComm") + "!");
                  return false;
                  }
               }

             // coloca instru��o p/ voltar ao in�cio e substitua o desvio do in�cio do "while" para o final do mesmo
             posFinal = programa.getMemoria();    //
             str = String.valueOf(posInicial);
             if (str.length()<2)
                str = "0"+str;
             programa.adicionaComando( "9" + str);

             String strF = String.valueOf(posFinal+1); // para pular instru��o "9ee"
             //System.out.println("\n ----> posInicial="+posInicial+"  posFinal+1="+(posFinal+1));
             programa.substituaComando("9"+strF,posAtual+3); // substitui a inst. "9ee" introduzida acima

             return true;
             }
           } // if ( EXPLOG() )
         else {
           pilhaDeInformacoes = pilhaDeInformacoes.concat( "\n" + Bundle.msg("msgCompErrELexpect")); // Erro: Era esperado Express�o l�gica
           return false;
           }
         } // if ( tipo(num_item) == Elemento.OUTROS && item(num_item).equals("(") )
       else {
         // Erro: esperava um \'(\' ou operando (encontrado \'<item(num_item)>\')
         String [] strMsgs = { "" + item(num_item) }; //
         pilhaDeInformacoes = pilhaDeInformacoes.concat( "\n" + Bundle.msgComVar("msgCompErrParOrOperTpar","OBJ",strMsgs) );
         return false;
         }

     } catch (java.lang.ArrayIndexOutOfBoundsException aobe) { // Erro: nao era esperado o final de cadeia
       pilhaDeInformacoes = pilhaDeInformacoes.concat("\n" + Bundle.msg("msgCompErrNotExpectFinal"));
       System.err.println("WHILE: " + Bundle.msg("msgCompErrNotExpectFinal"));
       return false;
       }

     } // boolean WHILE()


  boolean IO_LE () {
    try {

      if ( tipo(num_item) == Elemento.OUTROS && item(num_item).equals( "(" ) ) {
        avanca_item();
        //System.out.println("[CompilerBaseClass!IO_LE] "+item(num_item);
        while ( true ) {
          if ( tipo(num_item) == Elemento.VARIAVEL ) {
            programa.le( item(num_item) );
            avanca_item();
            if (tipo(num_item) == Elemento.OUTROS && item(num_item).equals(")")) {
              avanca_item();
              return true;
              }
            else
            if ( tipo(num_item) == Elemento.OUTROS && item(num_item).equals( "," ) ) // => ha mais parametros na funcao...
              avanca_item();
            else { // Erro: era esperado \')\' ou \',\'
              String strErr = Bundle.msg("msgCompErrExpectCloseParOrComma");
              System.out.println("[icg.compilador.CompilerBaseClass!IO_LE] " + strErr + " in: "+item(num_item));
              pilhaDeInformacoes = pilhaDeInformacoes.concat("\n" + strErr);
              return false;
              }
            } // if ( tipo(num_item) == Elemento.VARIAVEL )
          else { // Erro: era esperada uma vari�vel
            pilhaDeInformacoes = pilhaDeInformacoes.concat("\n" + Bundle.msg("msgCompErrExpectVar"));
            //System.out.println("[icg.compilador.CompilerBaseClass!IO_LE] erro: \"Erro: era esperado vari�vel.\" em: "+item(num_item));
            return false;
            }
          } // while ( true )

        } else { // Erro: era esperado \'(\'
          String strErr = Bundle.msg("msgCompErrExpectOpenPar");
          System.out.println("[icg.compilador.CompilerBaseClass!IO_LE] " + strErr + " in: "+item(num_item));
          pilhaDeInformacoes = pilhaDeInformacoes.concat("\n" + strErr);
          return false;
          }
      } catch (java.lang.ArrayIndexOutOfBoundsException aobe) { // Erro: nao era esperado o final de cadeia
        pilhaDeInformacoes = pilhaDeInformacoes.concat("\n" + Bundle.msg("msgCompErrNotExpectFinal"));
        System.err.println("IO_LE: " + Bundle.msg("msgCompErrNotExpectFinal"));
        return false;
        }

    } // boolean IO_LE()


  // Reconhece: ( variavel [, variavel]*)
  boolean IO_ESCREVA () {

    try {
      if ( tipo(num_item) == Elemento.OUTROS && item(num_item).equals( "(" ) ) {
        if (Configuracao.debugOptionAL2)
          System.out.println("[CompilerBaseClass!IO_ESCREVA] --- reconhecido \"(\": "+item(num_item)+" "+num_item);
        avanca_item();

        while ( true ) {
          // ainda n�o dispomos de instru��o para carregar constante p/ mem�ria ou AC !!
          if ( tipo(num_item) == Elemento.VARIAVEL ) { // || tipo(num_item) == Elemento.NUMERO ) {
            programa.escreve(item(num_item) ); // gera o c�digo do comando "cmd_escreva" "8EE"
            if (Configuracao.debugOptionAL2)
               System.out.println("[CompilerBaseClass!IO_ESCREVA] --- reconhecido \"(ident|num\": "+item(num_item)+" "+num_item);
            avanca_item();
            if ( tipo(num_item) == Elemento.OUTROS && item(num_item).equals( ")" ) ) {
               if (Configuracao.debugOptionAL2)
                  System.out.println("[CompilerBaseClass!IO_ESCREVA] --- reconhecido \")\": "+item(num_item)+" "+num_item);
               avanca_item();
               //System.out.println("[CompilerBaseClass!IO_ESCREVA] item atual: "+item(num_item)+" "+num_item);
               return true;
               }
            else if ( tipo( num_item) == Elemento.OUTROS && item(num_item).equals( "," ) ) {
               // => ha mais parametros na funcao...
               if (Configuracao.debugOptionAL2)
                  System.out.println("[CompilerBaseClass!IO_ESCREVA] --- reconhecido \",\": "+item(num_item)+" "+num_item);
               avanca_item();
               }
            else { // Erro: era esperado \')\' ou \',\'
               String strErr = Bundle.msg("msgCompErrExpectCloseParOrComma");
               if (Configuracao.debugOptionAL2)
                  System.out.println("[CompilerBaseClass!IO_ESCREVA] " + strErr + item(num_item)+" "+num_item);
               pilhaDeInformacoes = pilhaDeInformacoes.concat("\n" + strErr);
               return false;
               }
            } // if ( tipo(num_item) == Elemento.VARIAVEL

          else {
            int numAux = num_item-1;
            String [] strMsgs = { item(numAux), ""+numAux };
            String msgErr = Bundle.msg("msgCompErrExpectVar") + "! " + // Erro: era esperada uma vari�vel
                            Bundle.msgComVar("msgCompErrFound","OBJ",strMsgs); // Encontrei um \"<item(n)>"\ (no item <n>)
            pilhaDeInformacoes = pilhaDeInformacoes.concat("\n" + msgErr);
            if (Configuracao.debugOptionAL) { 
              System.out.println("[CompilerBaseClass!IO_ESCREVA] " + msgErr);
              listItensUntil(numAux); // list all itens until item 'num_item'
              }
            return false;
            }
          } // while ( true )

        } // if ( tipo(num_item) == Elemento.OUTROS && item(num_item).equals( "(" ) )
      else { // Erro: era esperado \'(\'
        String strErr = Bundle.msg("msgCompErrExpectOpenPar");
        if (Configuracao.debugOptionAL)
           System.out.println("[icg.compilador.CompilerBaseClass!IO_ESCREVA] " + strErr + " in: "+item(num_item));
        pilhaDeInformacoes = pilhaDeInformacoes.concat("\n" + strErr);
        return false;
        }
    } catch (java.lang.ArrayIndexOutOfBoundsException aobe) { // Erro: nao era esperado o final de cadeia
       pilhaDeInformacoes = pilhaDeInformacoes.concat("\n" + Bundle.msg("msgCompErrNotExpectFinal"));
       System.err.println("IO_ESCREVA: " + Bundle.msg("msgCompErrNotExpectFinal"));
       return false;
       }

    } // boolean IO_ESCREVA()

  } // public class CompilerBaseClass
