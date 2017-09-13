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
 * @author Leônidas de Oliveira Brandão
 * @version 2012-05-21 (added 'read', 'write' - 'leia', 'escreva'); 2008-10-02 
 * 
 */

// Called by: icg.compilador.Compilador.ButtonHandler.actionPerformed(ActionEvent e)

package icg.compilador;

/*
 * Elemento: "estrutura" para cada item léxico
 * Leia    : para leitura da cadeia de carateres (expressão aritmética)
 * Itens   : monta os itens léxico num "Vector" (faz o papel de um Analisador Léxico muito simplificado...)
 *
 */

    /* Grafos sintáticos (ou diagramas sintáticos)
     *
     * [24/08/2004] revisão
     * [26/03/2006] detectado problema de EXPRESSÃO LÓGICA com EXPRESSÃO ARITMÉTICA
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
     *         +--> ID  ---------------------->|           OEE  AC <- cEE   EE endereço var. NUM
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


public class Compila {

    String cadeia;   // para a expressão a ser analisada
    Vector itensLex; // para armazenar itens léxicos: seria 
                     //montado pelo Analisador Léxico (mas aqui vai via StringTockenizer)

    Pilha posicoesTemporarias;
    CodigoObjeto programa;

    ListaLigada listaPosicoes = new ListaLigada(); // lista com todos os comandos inseridos, para trocar ocorrência de '$' (caso de uso da pilha de execução)

    int num_item;    // número do item léxico atual
    int num_linha;       // linha atual do arquivo ( se existir )
    boolean OK;

    String pilhaDeInformacoes = new String("");
    String programaAnalisado = new String( "" );

    static int[] linhas = null;

    static int endPilhaExec = 0; // endereço relativo na pilha de execução, usado em conjunção com o '$', como em "1$n"

    static int numORs = 0; // E(): anote seu número para eventual uso para gerenciar end. dos blocos (p/ desvio final do OR): vetBlocosORs

    /*
      devolve o tipo do item léxico
       Elemento.NUMERO
       Elemento.COMANDOS
       Elemento.VARIAVEL
       Elemento.OUTROS
    */
    private int tipo (int i) {
      //Object obj = itensLex.elementAt(i);
      //if (obj instanceof Elemento)
      //   return ((Elemento)obj).tipo();
      //else return -1;
      try {
        return ((Elemento)itensLex.elementAt(i)).tipo();
      } catch (java.lang.Exception e) {
        System.out.println("[Compila!item(int)] Erro, "+e);
        return -1;
        }

      }


    // devolve o item léxico atual.
    private String item (int i) {
      try {
       return ((Elemento)itensLex.elementAt(i)).obj();
      } catch (java.lang.Exception e) {
        System.out.println("[Compila!item(int)] Erro, "+e);
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

    static private void initLinhas( BufferedReader buff ) {

    Vector v = new Vector();
	Vector st = new Vector();
    //StringTokenizer st;
    String s;
    Integer n;
    int i;
	
    s = LeArq.linha( buff );
    while ( s != null ) {

        //st = new StringTokenizer( s );
        //n = new Integer( st.countTokens() );
	    st = AnaLex.constroiTokens(s);
	    n = new Integer(st.size());
        v.addElement( n );
        s = LeArq.linha( buff );
    }

    // Alocando vetor de linhas...
    linhas = new int[ v.size() ];

    for ( i = 0; i < v.size(); i++ ) {

        n = (Integer) v.elementAt( i );
        linhas[i] = n.intValue();
    }
    }



    public static void main (String[] args) {
    Compila comp;
    BufferedReader arquivo;
    String linhaArquivo;

    if (args.length < 1)
        comp = new Compila();

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
            System.out.println("Erro! Ao tentar ler arquivo "+linhaArquivo);
	    }

    	linhaArquivo = LeArq.tudo( arquivo );
    	//System.out.println( linhaArquivo );
        }

        comp = new Compila( linhaArquivo );
    }

    System.out.println( comp.imprimeProgramaAnalisado() );
    System.out.println( comp.informacoesDeSaida() );

    if ( comp.OK )
       System.out.println( comp.programa );
    }


  public Compila () {

    System.out.print("Digite uma sequencia de comandos\n(help - informações; exit - sair): " );
    cadeia = Leia.readLine();
    
    while (true) {
      posicoesTemporarias = new Pilha();
      programa = new CodigoObjeto( 0 ); // ( 1 ); [04/08/2004]

      if (cadeia.equals("exit"))
         return;
       else
       reconheceCadeia();

       System.out.print("Digite uma sequencia de comandos\n(help - informações; exit - sair): ");
       cadeia = Leia.readLine();
       } 
    }


  public Compila (String cad) {
    if (Configuracao.listaAnaSim) 
       System.out.println("[icg.compilador.Compila.Compila(String)] "+cad);
    num_linha = 0;
    posicoesTemporarias = new Pilha();
    programa = new CodigoObjeto( 0 ); // [04/08/2004]( 1 );
    cadeia = cad;
    reconheceCadeia();
    }
  


  public String informacoesDeSaida () {
    pilhaDeInformacoes += "Compilacao:" + programa.MSG;
    return pilhaDeInformacoes;
    }

  public String imprimeProgramaAnalisado () {
    return programaAnalisado;
    }


  private void reconheceCadeia () {
    int i = 0;  

    // monta vetor com itens léxicos, funciona como o analisador sintatico
    // se ele for == null => erro de sintaxe.
    itensLex = Itens.montaItens( cadeia ); // "cadeia" contém uma "string" com o texto a ser compilado
    //System.out.println("[Compila!reconheceCadeia] 1 - cadeia:\n"+cadeia);
     
    if ( itensLex != null ) {
        //System.out.print("Teste: ");
        // lista itens léxicos, só para conferência
        // Itens.listaItens(itensLex);
       }

    // posiciona para leitura do primeiro item léxico
    num_item = 0;
    programaAnalisado += "\n" + (num_linha + 1) + ". " ;

    if (Configuracao.listaAnaSim) 
       System.out.println("[Compila!reconheceCadeia] cadeia:\n"+cadeia);
    //System.out.println("[Compila!reconheceCadeia] "+num_linha+": "+programaAnalisado);

    if ( itensLex != null  && this.INICIA() ) {
       pilhaDeInformacoes = pilhaDeInformacoes.concat( "\nCadeia reconhecida\n" );
       OK = true;
       }
    else
       pilhaDeInformacoes = pilhaDeInformacoes.concat("\nCadeia NÃO reconhecida\n");
    }


  // Inicia a analise dos comandos C().
  boolean INICIA () {

    while ( true ) {

      //if (num_linha>-1 && linhas!=null)
      //   System.out.println("[Compila!INICIA] "+linhas[num_linha]);
      //else System.out.println("[Compila!INICIA] - "+num_linha+" "+linhas);

      if ( !C() ) {
         pilhaDeInformacoes = pilhaDeInformacoes.concat( "\nLinha " + (num_linha+1) + ". Erro: comando invalido!" );
         return false;
         }


      // Se a cadeia chegou ateh o final verdadeira ela eh reconhecida...
      if ( num_item >= itensLex.size() ) {
         // END
         programa.adicionaComando("000"); // "0-0000" );

         // O "programa.atualizaComandos(posicoes);" serve para trocar $ pela primeira posição de memória disponível, logo
         // deve ser a última coisa a ser feita!!
         programa.atualizaComandos(listaPosicoes);
         if (Configuracao.listaAnaSim) 
            System.out.println("\n[Compila!INICIA] cadeia reconhecida e gerado código com sucesso!");
         return true;
         }
      }
    }



  // copia a lista "posicoes" no final da lista "listaPosicoes"
  void copiaListaPosicoes (ListaLigada posicoes) {
    //No noListaDefinitiva = listaPosicoes.posicaoAtualLista(); // último No na lista definitiva
    No noAux             = posicoes.inicioLista();            // primeiro elemento na lista atual
    while ( noAux != null ) { //iterador.hasNext() ) {
      listaPosicoes.add( noAux.obj() );
      noAux = noAux.proximo();
      }
    }





    // Trata as expressoes entre parenteses.......
  boolean Tpar () {

    if (Configuracao.listaAnaSim) 
        System.out.println("[Compila!Tpar] item "+item(num_item)+" ? "+tipo(num_item));

    if ( tipo(num_item) == Elemento.OUTROS && item(num_item).equals( "(" ) ) { 
        // => '('
        avanca_item();
        if ( !EA() ) { //N
           pilhaDeInformacoes = pilhaDeInformacoes.concat( "\n Erro: esperava um operando após" +
                  		" \'(\' (encontrado \'" + item(num_item) + "\')" );
           return false;
           }

        if ( tipo(num_item) == Elemento.OUTROS && item(num_item).equals( ")" ) ) {
        // => ')'
           avanca_item();
           return true;
           }
        else {
           pilhaDeInformacoes = pilhaDeInformacoes.concat( "\nErro: esperava um \')\' (encontrado \'" + item(num_item) + "\')" );
           return false;
           }
       }
    else {
        pilhaDeInformacoes = pilhaDeInformacoes.concat( "\nErro: esperava um \'(\' ou operando (encontrado \'"+item(num_item)+"\')" );
        return false;
        }
    }


  // Analisa identificadores (variaveis ou numeros).
  boolean ID () {
    if (Configuracao.listaAnaSim) 
       System.out.println("[Compila!ID] item "+item(num_item)+" ? "+tipo(num_item));

    //LinkedList posicoes = new LinkedList();
    ListaLigada posicoes = new ListaLigada();

    if ( tipo(num_item) == Elemento.NUMERO || tipo(num_item) == Elemento.VARIAVEL ) {
        // Se eh operando...
        if (tipo(num_item) == Elemento.VARIAVEL) programa.aloca( item(num_item) );
        posicoes.add( new Integer( programa.getMemoria() ) );
        //System.out.println("[Compila!ID] "+num_item+" -> "+item(num_item));
        programa.empilha( item(num_item) );
        posicoesTemporarias.empilha( posicoes );
        return true;
       }
    else
        return false;
    }



  // **********

  /*
     N EXPLOG ---> ELS --+----------> ou ---> ELS --+-->||  (ou = ||)
     *                   |        ^                 |
     *                   +-->||   |                 |
     *                            +-----------------+
  */
  boolean EXPLOG () {
    if (Configuracao.listaAnaSim)
       System.out.println("[Compila!EXPLOG] Início: EXPLOG := ELSS | ELS ou ELS");
    Vector vetBlocosORs = new Vector(); // para anotar comandos com blocos de OR (||) após processamento total do E()
    boolean algumOR = false;
    try {
      if (ELS()) {
         // ELS || ELS
         while (true) {
            if (tipo(num_item)==Elemento.OUTROS && item(num_item).equals( "||" )) {
               algumOR = true;
               int pM = programa.getMemoria();           // pega endereço da últ. instrução, pM;
               vetBlocosORs.addElement(new Integer(pM)); // anota núm. da instrução ao final para ser substituida
               programa.adicionaComando("6ee");          // 6ee                        ; após todos os ORs troque
               avanca_item();                            //                            ; pelo endereço do últ. bloco
               if (ELS()) {
                  if (Configuracao.listaAnaSim)
                     System.out.println("[Compila!EXPLOG] Reconhecido:  EXPLOG := ELS ou ELS");
                  }
               else {
                  System.out.println("[Compila!EXPLOG] Erro em expressão lógica: esperava o segundo [EXPLOG] de: [ELS || ELS]");
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
            int pM = programa.getMemoria();         // pega endereço da últ. instrução, pM;
            for (int i=0; i<vetBlocosORs.size(); i++) {
                int posInstrOR = ((Integer)vetBlocosORs.elementAt(i)).intValue();
                programa.substituaComando("6"+pM,posInstrOR); // EPI     <- ee     ; pule o próx. cmd (q/ é desvio)
                if (Configuracao.listaAnaSim) 
                   System.out.println("\n[Compila!EXPLOG] substituido \"6ee\" por \"6"+pM+"\"");

                }
            }
         return true;
         }
      else {
         System.out.println("[Compila!EXPLOG] Erro em expressão lógica: esperava [ELS] ou [ELS || ELS]");// ERRO
         return false;
         }
      }
    catch(java.lang.ArrayIndexOutOfBoundsException aobe) {
      pilhaDeInformacoes = pilhaDeInformacoes.concat("\nEXPLOG - Erro: final de cadeia");
      return false;
      }
    }

  /*
     N ELS -+----------> ELSS --------------------+-->||
     *      |                  ^                  |
     *      |                  |                  |
     *      |                  +<-- ELSS <-- e <--+
     *      +-> ! -> ELS -->||
  */
  boolean ELS () {
    if (Configuracao.listaAnaSim)
       System.out.println("[Compila!ELS] Início: ELS := ELSS (e ELSS)* | ! ELS");
    Vector vetBlocosEs = new Vector(); // para anotar comandos com blocos de E (&&) após processamento total do ELS()
    boolean algumE = false;
    try {
      // ! ELS
      if (tipo(num_item)==Elemento.OUTROS && item(num_item).equals( "!" )) {
         if (Configuracao.listaAnaSim)
            System.out.println("[Compila!ELS] Reconhecido '!'");
         avanca_item();
         if (ELS()) { // inverte valor de AC: cAC>0 => AC <- -cAC
                      //                      cAC<0 => AC <- -cAC
                      //                      cAC=0 => AC <- 1
            int pM = programa.getMemoria();             // pega endereço da últ. instrução, pM;
                                                        //                     ; X = cAC
            programa.adicionaComando("6"+(pM+6));       // cAC>0 => vá para R  ; X>0, vá para local onde fará X<-(-X)
            programa.adicionaComando("4--1");           // AC <- - cAC
            programa.adicionaComando("6"+(pM+7));       // cAC>0 => vá para R  ; -X>0, vá para fim (AC já está com -X)
            programa.adicionaComando("0-1");            // AC <- 1             ; X==0, fique com 1
            programa.adicionaComando("9"+(pM+7));       // vá p/ fim           ; 
            programa.adicionaComando("4--1");           // AC <- - cAC         ; X>0, faça AC <- -X
            return true;
            }
         else {
            System.out.println("[Compila!ELS] Erro: esperava ELS após '!'");
            return false;
            }
         }
      if (ELSS()) {
         if (Configuracao.listaAnaSim)
            System.out.println("[Compila!ELS] Reconhecido 'ELSS'");
         while (tipo(num_item)==Elemento.OUTROS && item(num_item).equals( "&&" )) {
            if (Configuracao.listaAnaSim)
               System.out.println("[Compila!ELS] Reconhecido '&&'");
            algumE = true;

	    // Código:
            int pM = programa.getMemoria();             // pega endereço da últ. instrução, pM;
            programa.adicionaComando("6"+(pM+2));       // 6(pM+1)                    ; pule instrução seguinte se EL=v
            vetBlocosEs.addElement(new Integer(pM+1));  // anota núm. da instrução ao final para ser substituida
            programa.adicionaComando("9ee");            // 9ee                        ; após todos os Es troque (fim: EL=f)

            avanca_item();
            if (ELSS()) {
               if (Configuracao.listaAnaSim)
                  System.out.println("[Compila!ELS] Reconhecido 'ELSS'");
               }
            else {
               System.out.println("[Compila!ELS] Erro: esperava mais um 'ELSS'");
               return false;
               }
            } // while (tipo(num_item)==Elemento.OUTROS && item(num_item).equals( "&&" ))
         // Substitua em vetBlocosEs
         if (algumE) {
            int pM = programa.getMemoria();         // pega endereço da últ. instrução, pM;
            for (int i=0; i<vetBlocosEs.size(); i++) {
                int posInstrE = ((Integer)vetBlocosEs.elementAt(i)).intValue();
                programa.substituaComando("9"+pM,posInstrE); //
                if (Configuracao.listaAnaSim) 
                   System.out.println("\n[Compila!EXPLOG] substituido \"9ee\" por \"9"+pM+"\" na pos. memória "+posInstrE);
                }
            }

         return true;
         }
      else {
         System.out.println("[Compila!ELS] Erro: esperava ao menos um ELSS");
         return false;
         }
      }
    catch(java.lang.ArrayIndexOutOfBoundsException aobe) {
      pilhaDeInformacoes = pilhaDeInformacoes.concat("\nEXPLOG - Erro: final de cadeia");
      return false;
      }
    }
    

  // Reconhece: [+|-] T* [+|-|ou] (ou é '||')
  boolean EA () {
    if (Configuracao.listaAnaSim) 
       System.out.println("[Compila!EA] inicio:  [+|-] T* [+|-|ou] (ou é '||')");
    int tipoAnt = -1; // para testar se é possível interpretar como Exp. Lógica (com ||)
    boolean menos_unario = false;
    boolean primeiro_T   = true;
    String codigoParcial = null;
    //LinkedList posicoes = new LinkedList();
    ListaLigada listaCodigosTemp = new ListaLigada();
    boolean algumOR = false;
    try {
        // Menos (mais) unário
        if (tipo(num_item)==Elemento.OUTROS && 
            (item(num_item).equals( "+" ) || item(num_item).equals( "-" )) ) {
           // => é operador unário
   	   //if ( menos_unario ) {
           // Para empilhar o -1 ou 1
           // listaCodigosTemp.add( new Integer( programa.getMemoria() ) );
           // System.out.println("[Compila!EA] unário: "+item(num_item)+" "+tipo(num_item));
           if ( item(num_item).equals( "-" ) )
              //- programa.empilha( String.valueOf( -1 ) );
              menos_unario = true; // após reconhecer T, faça -T (apenas no primeiro T)
           //- else
           //-    programa.empilha( String.valueOf( 1 ) );
           //- codigoParcial = new String( "4" );
           avanca_item();
           }

        // Restante da expressão
        char op = 'X';
        String var = "",
               strPosRelativa = "";
        int posRelativa = 0; // para pilha de execução (guardar cAC)
        while (true) {
           if ( T() ) {
              // se é a primeira vez que reconhece um T aqui, então veja se teve um menos unário (se tiver inverta sinal)
              if (primeiro_T) {
                 if (menos_unario) {
                    programa.adicionaComando("1$"+endPilhaExec);     // $(EE)   <- cAC           ; usa pos. de mem. (mas não registra)
                    programa.adicionaComando("0--1");                // AC      <- -1
                    programa.adicionaComando("4$"+endPilhaExec);     // AC      <- cAC * c$(EE)  ;
                    endPilhaExec--;                                  //                          ; libera últ. posição de memória usada
                    menos_unario = false; // não permita inverter novamente
                    }
                 }
              else  primeiro_T = false;

              if (Configuracao.listaAnaSim) 
                 System.out.println("[Compila!EA] --- reconhecido T(): item=<"+item(num_item)+"> "+tipo(num_item)+" op="+op);

              // op == 'X' => é operador relacional ou lógico (||)
              if (op!='X') { // já ocorreu algum operador antes do último T reconhecido => realize operação
                 if (op == '+') {
                    endPilhaExec--;                                  //                          ; libera últ. posição de memória usada
                    programa.adicionaComando("2$"+(endPilhaExec  )); // AC      <- cAC + c$(EE-1); pega últ. valor na pilha de mem.
                    }
                 else
                 if (op == '-') {
                    // AC <- c$(EE-1) - c$(EE)
                    programa.adicionaComando("1$"+endPilhaExec);     // $(EE)   <- cAC           ; usa pos. de mem. (mas não registra)
                    programa.adicionaComando("0$"+(endPilhaExec-1)); // AC      <- c$(EE-1)
                    programa.adicionaComando("3$"+endPilhaExec);     // AC      <- cAC - c$(EE)
                    endPilhaExec--; //??posRelativa--;               //                          ; libera a últ. pos. de mem. registrada
                    }
                 else
                 if  (op == 'o') { // ???
                    /*
                    endPilhaExec--;
                    programa.adicionaComando("2$"+(endPilhaExec-1)); // AC      <- cAC + c$(EE-1)
                    programa.adicionaComando("1$"+(endPilhaExec));   // $(EE)   <- cAC
                    programa.adicionaComando("4$"+(endPilhaExec));   // AC      <- cAC * c$(EE)
                    int posUltMem = programa.getMemoria();           //                         ; pega endereço da últ. instrução, X
                    programa.adicionaComando("6$"+(posUltMem+4));    // cAC>0,     EPI <- X+4    ; desvia p/ gaveta de endereço X
                    programa.adicionaComando("0-00");                // AC      <- 0             ; AC <- 0 eq. a falso
                    programa.adicionaComando("9$"+(endPilhaExec+5)); // EPI     <- X+5
                    programa.adicionaComando("0-01");                // AC      <- 1             ; AC <- 1 eq. a verdadeiro
                    */ }
		 if (op == '|') { //
		    }
                 else {
                    // ERRO
                    System.out.println("[Compila!EA] Erro em E: esperava um operador \'+\', \'-\' ou \'ou\'");// ERRO
                    }
                 } // if (op!='X')


              if (tipo(num_item)== Elemento.OUTROS && (item(num_item).equals("+") || item(num_item).equals("-"))) {
//N                  (item(num_item).equals("+") || item(num_item).equals("-") || item(num_item).equals("||")) ) {
	         // Ainda tem operadores [+|-|ou], então precisa empilhar este valor
                 // Valor vindo de T fica no AC; colocar cAC numa posição de memória (pilha de execução)
                 tipoAnt = tipo(num_item-1); // Elemento: NUMERO = 0; COMANDOS = 1; VARIAVEL = 2;
                 if ( item(num_item).equals( "+" ) ) {
                    op = '+';
                    // codigoParcial = new String( "2" );
                    programa.adicionaComando("1$"+endPilhaExec);        // $(EE)  <- cAC              ; empilhe cAC
                    endPilhaExec++; // pilha de execução cresceu
                    avanca_item();
                    // programa.empilha( String.valueOf( -1 ) );
                    }
                 else
                 if (item(num_item).equals( "-" ) ) {
                    op = '-';
                    // codigoParcial = new String("3");
                    programa.adicionaComando("1$"+endPilhaExec);        // $(EE)  <- cAC              ; empilhe cAC
                    endPilhaExec++; // pilha de execução cresceu
                    avanca_item();
                    // programa.empilha( String.valueOf( 1 ) );
                    }
// ?? [26/03/2006]
/*
                 else
                 if (tipoAnt!=Elemento.OUTROS) { 
//System.out.println("[Compila!EA] item("+(num_item-1)+")="+item(num_item-1)+" tipoAnt="+tipoAnt); 
return true; }
                 else
                 if (item(num_item).equals( "||" )) {
                    algumOR = true;
                    op = '|';                                 // Anotar comandos com blocos de OR (||) após processamento total do E()
                    int pM = programa.getMemoria();           // pega endereço da últ. instrução, pM;
                    vetBlocosORs.addElement(new Integer(pM)); // anota núm. da instrução ao final para ser substituida
                    programa.adicionaComando("6ee");          // 6ee                        ; após todos os ORs troque
                    avanca_item();                            //                            ; pelo endereço do últ. bloco
                    }
//"6"+numORs
//vetBlocosORs.addElement("6"+numORs); // para anotar comandos com blocos de OR (||) após processamento total do E()
// ??
                 else {
                    // return finaliza(listaCodigosTemp); //true;
                    System.out.println("[Compila!EA]: "+item(num_item)+" <- falta tratar op="+op);
                    }
*/
                 } // if (tipo(num_item)== Elemento.OUTROS && item(num_item).equals( "+" ) || item(num_item).equals( "-" ) )
              else {
                 if (Configuracao.listaAnaSim) 
                    System.out.println("[Compila!EA] --- reconhecido EA():= [+|-] T (+|-): <"+
                                        item(num_item)+"> "+tipo(num_item));
/*
                    // Substitua em vetBlocosORs
                    if (algumOR) {
                       int pM = programa.getMemoria();         // pega endereço da últ. instrução, pM;
                       for (int i=0; i<vetBlocosORs.size(); i++) {
                           int posInstrOR = ((Integer)vetBlocosORs.elementAt(i)).intValue();
                           programa.substituaComando("6"+pM,posInstrOR); // EPI     <- ee     ; pule o próx. cmd (q/ é desvio)
                           }
                       }
*/
                 return true;
                 }
              } // if ( T() )
           else {
              if ( num_item >= itensLex.size() - 1 ) {
                 System.out.println("[Compila!EA] Erro, não reconhecido T "+item(num_item)+" "+tipo(num_item));
                 pilhaDeInformacoes = pilhaDeInformacoes.concat("\nErro: final de cadeia após um operador unário (\'+\' " +
                                      "ou \'-\'), esperava operando");
                 return false;
                 }
              op = 'X';
              }

      	   //avanca_item(); <- só avança em terminais
           } // while (true)
    	
           } catch(java.lang.ArrayIndexOutOfBoundsException aobe) {
             pilhaDeInformacoes = pilhaDeInformacoes.concat("\nE - Erro: final de cadeia");
             aobe.printStackTrace();
             return false;
             }     
    }

  boolean finaliza (ListaLigada posicoes) { 
    // O "programa.atualizaComandos(posicoes);" serve para trocar $ pela primeira posição de memória disponível, logo deve ser a
    // última coisa a ser feita!!
    // reconhecido T
    if ( num_item >= itensLex.size() - 1 ) {
       copiaListaPosicoes(posicoes); // copia a lista "posicoes" no final da lista "listaPosicoes"
       return true;
       }
    
    if ( ! (tipo(num_item)== Elemento.OUTROS && (item(num_item).equals("+") || item(num_item).equals("-")) ) ) {
    // => pode ser um ')', '>'...'==', ';', etc...
    // O "programa.atualizaComandos(posicoes);" serve para trocar $ pela primeira posição de memória disponível, logo deve ser a
    // última coisa a ser feita!!
        copiaListaPosicoes(posicoes); // copia a lista "posicoes" no final da lista "listaPosicoes"
        return true;
        }
    return false;
    }


  //     const : NUMERO
  //     operadores, op logicos, (, {, etc... : OUTROS
  //     variaveis: VARIAVEL
  //                                    Códigos
  //     +------------------------+     1: if (temFator) {
  //     !                    2   |           "1$topo"     ; $topo <- cAC
  // T ----> FATOR ---+---> * --->| 5         "0$(topo-1)" ; AC    <- $(topo-1)
  //              1   |       3   |           if (op=='*') "4$topo"   ; AC <- cAC * $topo
  //                  +---> / --->|           if (op=='/') "5$topo"   ; AC <- cAC / $topo
  //                  |       4   |           if (op=='&') {
  //                  |                +---> && -->|              "4$(topo-1)"  ; 0: ($(topo-1))²
  //                  |                          "6$(X+7)"     ; 1: 
  //                  +--->||                    "0$(topo"     ; 2: 
  //                                             "4$topo"      ; 3: ($topo)²
  //                                             "6$(X+7)"     ; 4:
  //                                             "0-0"         ; 5:
  //                                             "9$(X+8)"     ; 6:
  //                                             "0-1"         ; 7:
  //                                             }
  //                                           topo--; // desempilhe último AC empilhado (que estava em $(topo-1))
  //                                           }
  //                                       else temFator = true;
  //                                    2: op = '*';  3: op = '/';  4: op = '&';
  //                                    5: "1$topo" ;  empilhe atual AC
  //                                       topo++;
  //                                       
  boolean T () {
    if (Configuracao.listaAnaSim) 
       System.out.println("[Compila!T] início ");
    String item_op = null;
    //String codigoParcial = null;
    //ListaLigada posicoes = new ListaLigada();
    char op = 'X';
    int topo = 0; // para pilha de execução, deslocamento relativo
    boolean temFator = false; // para indicar se já ocorreu algum FATOR: ou seja, passou por um operador '*', '/' ou '&&'  
    try {

        while ( true ) {
          //System.out.println("[Compila!T] item <"+item(num_item)+"> ? "+tipo(num_item)+" num_item="+num_item);
          if ( FATOR() ) {
    	     // é operando, só avança em reconhecimento de terminais!!! nunca em não terminais
             //System.out.println("[Compila!T] após FATOR()  <"+item(num_item)+"> num_item="+num_item);
    	     // 'Concatena' os comandos que ID empilhou junto com estes de T.
             if (temFator) { // já passou por algum FATOR() antes do último (logo já tem FATOR op FATOR)
                programa.adicionaComando("1$"+topo);      // empilha AC
                programa.adicionaComando("0$"+(topo-1));  // pega AC anteriormente empilhado
                if (op=='*') programa.adicionaComando("4$"+topo); //   ; AC <- cAC * $topo
                if (op=='/') programa.adicionaComando("5$"+topo); //   ; AC <- cAC / $topo
/*N
                if (op=='&') { //
                   int pM = programa.getMemoria();         // pega endereço da últ. instrução, pM;
                   programa.adicionaComando("4$"+(topo-1));//     ; 0: ($(topo-1))²// 
                   programa.adicionaComando("6$"+(pM+7));  //     ; 1:             // 
                   programa.adicionaComando("0$"+topo);    //     ; 2:             // 
                   programa.adicionaComando("4$"+topo);    //     ; 3: ($topo)²    // 
                   programa.adicionaComando("6$"+(pM+7));  //     ; 4:             // 
                   programa.adicionaComando("0-0");        //     ; 5:             // 
                   programa.adicionaComando("9$"+(pM+8));  //     ; 6:             // 
                   programa.adicionaComando("0-1");        //     ; 7:             //
                   }                              // 
*/
                topo--; // desempilhe último AC empilhado (que estava em $(topo-1));
                }
             else temFator = true;

             item_op = item(num_item);
 
             // Reconhece: [*|/]  X cortei |&&
             if ( tipo(num_item) == Elemento.OUTROS &&
                  (item_op.equals("*") || item_op.equals("/")) ) {
//N                  (item_op.equals("*") || item_op.equals("/") || item_op.equals("&&") ) ) {
                // 5: empilhe atual AC
                programa.adicionaComando("1$"+topo);  // "$topo <- cAC";
                topo++;

               // 2, 3, ou 4: é operador
                if ( item_op.equals( "*" ) ) op = '*';
                else
                if ( item_op.equals( "/" ) ) op = '/';
                else { // N
                     if (Configuracao.listaAnaSim) 
                        System.out.println("[Compila!T] --- reconhecido FATOR <"+item((num_item-1))+"> ");
                     return true; //N
                     }
//N                else                         op = '&'; //N
                avanca_item();	     
                }
             else {
                int n = num_item-1;
                if (Configuracao.listaAnaSim) 
                   System.out.println("[Compila!T] --- reconhecido FATOR <"+item(n)+"> n="+n+" num_item="+num_item);
                //avanca_item(); <- só avança em reconhecimento de terminais!!! nunca em não terminais
                return true;
                }
             }
          else {
             System.out.println("[Compila!T] erro, não encontei um FATOR -> num_item="+num_item);
             //for (int i=0;i<80;i++) System.out.print(".");
             return false;
             }
          //posicoesTemporarias.empilha( posicoes );
          } // while ( true )

    }
    catch (java.lang.ArrayIndexOutOfBoundsException aobe) {
        pilhaDeInformacoes = pilhaDeInformacoes.concat( "\n" + "T - Erro: final de cadeia" );
        aobe.printStackTrace();
        }

    return false;
    }

/*
     * FATOR --+--> NUM --------------------->+--->||     ?    AC <- NUM
     *         |                              |        
     *         +--> ID  --------------------->|           OEE  AC <- cEE   EE endereço var. NUM
     *         |                              | 
     *         +-->  (  ---> [ELSS] ---> ) --->|      
*/
  boolean FATOR () {
    if (Configuracao.listaAnaSim) 
       System.out.println("[Compila!FATOR] inicio "+item(num_item)+" "+tipo(num_item));
    if ( tipo(num_item) == Elemento.NUMERO) { 
       if (Configuracao.listaAnaSim) 
           System.out.println("[Compila!FATOR] --- reconhecido NUMERO <"+item(num_item-1)+"> "+tipo(num_item-1));
       programa.adicionaComando("0-"+item(num_item));  // AC <- N
       avanca_item();
       return true;
       }
    else
    if ( tipo(num_item) == Elemento.VARIAVEL ) { 
       //System.out.println("[Compila!FATOR] --- reconhecido IDENT <"+item(num_item)+"> "+tipo(num_item)+" num_item="+num_item);
       // coloca var. numa posição de memória
       String var = item(num_item);
       programa.aloca(var); // aloca espaço para a var. ou pega um end. EE já existente
       programa.adicionaComando( "0" + programa.enderecoDaVariavel(var)); // adiciona o comando correspondente AC <- cEE
       avanca_item();
       return true;
       }
    else
    // if ( tipo(num_item) == Elemento.OUTROS && item(num_item).equals("(") ) { 
    if ( item(num_item).equals("(") ) {  // o '(' não está com valor "OUTROS"!!
       if (Configuracao.listaAnaSim) 
          System.out.println("[Compila!FATOR] reconhecida <"+item(num_item)+"> "+tipo(num_item));
       avanca_item();
       if ( ELSS() ) {
          if (Configuracao.listaAnaSim) 
             System.out.println("[Compila!FATOR] reconhecida ELSS "+item(num_item)+" "+tipo(num_item));
          //if (tipo(num_item) == Elemento.OUTROS && item(num_item).equals(")") ) { 
          if ( item(num_item).equals(")") ) { 
             //System.out.println("[Compila!FATOR] --- reconhecido \"( ELSS )\" <"+item(num_item)+"> "+tipo(num_item));
             avanca_item(); // só terminal avança
             return true;
             }
          else {
             System.out.println("[Compila!FATOR] --- NÃO reconhecido o fecha de \"(ELSS)\" "+item(num_item)+" "+tipo(num_item));
             return false;
             }
          }
       else {
          System.out.println("[Compila!FATOR] NÃO ELSS após um \"( "+item(num_item)+" "+tipo(num_item));
          return false;
          }
       } // if ( tipo(num_item) == Elemento.OUTROS && item(num_item).equals("(") )
    else
    if ( tipo(num_item) == Elemento.OUTROS && item(num_item).equals("!") ) { 
       if (Configuracao.listaAnaSim) 
          System.out.println("[Compila!FATOR] --- reconhecido ! <"+item(num_item)+"> "+tipo(num_item));
       avanca_item();
       if ( FATOR() ) {
          //System.out.println("[Compila!FATOR] --- reconhecida ! FATOR "+item(num_item-1)+" "+tipo(num_item-1));
          return true;
          }
       else {
          System.out.println("[Compila!FATOR] --- NÃO reconhecido FATOR de \"!FATOR\" "+item(num_item-1)+" "+tipo(num_item-1));
          return false;
          } 
       } // if ( tipo(num_item) == Elemento.OUTROS && item(num_item).equals("!") )
    else {
       //System.out.println("[Compila!FATOR]---NÃOreconhecidoNUM|ID|(ELSS)|!FATOR:item="+item(num_item)+"tipo="+tipo(num_item));
       System.out.println("[Compila!FATOR] --- NÃO reconhecido NUM|ID|(ELSS): item="+item(num_item)+" tipo="+tipo(num_item));
       avanca_item(); // ignore este item léxico, pegue o próximo
       return false;
       }  
    }

  // ELSS --> EA --+--> == ---+--> EA -->||    [ E ]                                    ; AC fica c/ res. desta expr.
  //               |          ^                mem0 <- cAC
  //               +--> <= -->|                [ E ]                                    ; AC fica c/ res. desta expr.
  //               ...                         mem1 <- cAC
  //               +-->||                      AC   <- mem0                             ; pega res. da prim. expr.
  //                                           AC   <- cAC - mem1                       ; pega res. da seg. expr.
  //                                           (desvio de acordo c/ operador relacional)
  boolean ELSS () {
    if (Configuracao.listaAnaSim) 
       System.out.println("[Compila!ELSS] inicio "+item(num_item)+" "+tipo(num_item));
    boolean temOpRel = false;
    ListaLigada posicoes;
    String tipoComp; // 0:==; 1:<=; 2:>=; 3:!=; 4:>; 5:<
    int posRelativa = 0; // para pilha de execução (guardar cAC)

    if ( EA() ) { // acabou de calcular primeira expressão aritmética
       if (Configuracao.listaAnaSim) 
          System.out.println("[Compila!ELSS] --- reconhecido EA  <"+item(num_item)+"> "+tipo(num_item));
       //if ( tipo(num_item) == Elemento.OUTROS && item(num_item).equals("==") ) { 
       if ( item(num_item).equals("==") ) { 
          if (Configuracao.listaAnaSim) 
             System.out.println("[Compila!ELSS] --- reconhecido ELSS := \"EA ==\" "+item(num_item)+" tipo="+tipo(num_item));
          temOpRel = true; tipoComp = "=="; //0;
          avanca_item();
          }
       else
       //if ( tipo(num_item) == Elemento.OUTROS && item(num_item).equals("<=") ) { 
       if ( item(num_item).equals("<=") ) { 
          if (Configuracao.listaAnaSim) 
             System.out.println("[Compila!ELSS] --- reconhecido ELSS := \"EA <=\" "+item(num_item)+" tipo="+tipo(num_item));
          temOpRel = true; tipoComp = "<="; //1;
          avanca_item();
          }
       else
       //if ( tipo(num_item) == Elemento.OUTROS && item(num_item).equals(">=") ) { 
       if ( item(num_item).equals(">=") ) { 
          if (Configuracao.listaAnaSim) 
             System.out.println("[Compila!ELSS] --- reconhecido ELSS := \"EA >=\" "+item(num_item)+" tipo="+tipo(num_item));
          temOpRel = true; tipoComp = ">="; //2;
          avanca_item();
          }
       else
       //if ( tipo(num_item) == Elemento.OUTROS && item(num_item).equals("!=") ) { 
       if ( item(num_item).equals("!=") ) { 
          if (Configuracao.listaAnaSim) 
             System.out.println("[Compila!ELSS] --- reconhecido ELSS := \"EA !=\" "+item(num_item)+" tipo="+tipo(num_item));
          temOpRel = true; tipoComp = "!="; //3;
          avanca_item();
          }
       else
       //if ( tipo(num_item) == Elemento.OUTROS && item(num_item).equals(">") ) { 
       if ( item(num_item).equals(">") ) { 
          if (Configuracao.listaAnaSim) 
             System.out.println("[Compila!ELSS] --- reconhecido ELSS := \"EA >\" "+item(num_item)+" tipo="+tipo(num_item));
          temOpRel = true; tipoComp = ">"; //4;
          avanca_item();
          }
       else
       //if ( tipo(num_item) == Elemento.OUTROS && item(num_item).equals("<") ) { 
       if ( item(num_item).equals("<") ) { 
          if (Configuracao.listaAnaSim) 
             System.out.println("[Compila!ELSS] --- reconhecido ELSS := \"EA <\" "+item(num_item)+" tipo="+tipo(num_item));
          temOpRel = true; tipoComp = "<"; //5;
          avanca_item();
          }
       else {
          if (Configuracao.listaAnaSim) 
             System.out.println("[Compila!ELSS] --- reconhecido ELSS := EA  <"+item(num_item)+"> tipo="+tipo(num_item));
          temOpRel = false; 
          tipoComp = "";
          } // 

       if (temOpRel) {
          programa.adicionaComando("1$"+endPilhaExec);              // c$(EE)      <- cAC         ; mem0 <- cAC
          endPilhaExec++; // pilha de execução cresceu

          if (Configuracao.listaAnaSim) 
             System.out.println("[Compila!ELSS] tenta reconhecer nova EA: "+item(num_item)+" "+tipo(num_item));

          //posRelativa++;                                          //                            ; mais uma pos. de mem. usada
          if ( EA() ) { // E OpRel E
             if (Configuracao.listaAnaSim) 
                System.out.println("[Compila!ELSS] --- reconhecido ELSS := \"EA op EA\" "+item(num_item)+" "+tipo(num_item));
             programa.adicionaComando("1$"+endPilhaExec);           // c$(EE)      <- cAC         ; mem0 <- cAC
             // endPilhaExec++; // pilha de execução cresceu

             programa.adicionaComando("0$"+(endPilhaExec-1));       // AC          <- c(EE)       ; AC   <- mem0
             programa.adicionaComando("3$"+(endPilhaExec  ));       // AC          <- cAC-c(EE+1) ; mem1 <- mem0 - mem1
             //System.out.println("[Compila!ELSS] "+": 1$"+(endPilhaExec  )+"\n              "
             //                                   +": 0$"+(endPilhaExec-1)+"\n              "+": 3$"+(endPilhaExec));
             endPilhaExec--; // pilha de execução: libera 1 posição (do primeiro "E", em "E OpRel E")

             completaExprLog(tipoComp); // aqui está o código para o operador relacional

             return true;
             }
          else {
             System.out.println("[Compila!ELSS] --- NÃO reconhecido EA final em ELSS!! "+item(num_item)+" "+tipo(num_item));
             return false;
             }
          }
       else { // if (temOpRel)
          if (Configuracao.listaAnaSim) 
             System.out.println("[Compila!ELSS] --- reconhecido  ELSS := EA "+item(num_item)+" "+tipo(num_item));
          //avanca_item(); <- só avança em terminais
          return true;
          }
       }
    else { // não reconhecido EA()
       System.out.println("[Compila!ELSS] NÃO reconhecido  ELSS := EA [op EA] "+item(num_item)+" "+tipo(num_item));
       return false;
       }
    }

  void completaExprLog (String opR) {
    int tempMem = programa.getMemoria(); // número de instruções até o momento
    if (opR.equals("==")) { // :---: "==" eq. "não(AC²>0)"
       programa.adicionaComando("1$0");                            // F+0          <- cAC        ; 0. m0 <- cAC
       programa.adicionaComando("4$0");                            // AC           <- cAC * c(F) ; 1. AC <- cAC * m0
       programa.adicionaComando("6"+(tempMem+5));                  // cAC>0 => EPI <- X+3        ; 2. cAC > 0 => goto 5
       programa.adicionaComando("0-1");                            // AC           <- 0          ; 3. AC <- 1
       programa.adicionaComando("9"+(tempMem+6));                  // EPI          <- X+4        ; 4. goto 6
       programa.adicionaComando("0-0");                            // AC           <- 1          ; 5. AC <- 0
       }
    else 
    if (opR.equals("<=")) { // :---: "<=" eq. "não(AC>0)"
       programa.adicionaComando("6"+(tempMem+3));                  // cAC>0 => EPI <- X+3        ; 0. cAC > 0 => goto 3
       programa.adicionaComando("0-1");                            // AC           <- 0          ; 1. AC <- 1
       programa.adicionaComando("9"+(tempMem+4));                  // EPI          <- X+4        ; 2. goto 4
       programa.adicionaComando("0-0");                            // AC           <- 1          ; 3. AC <- 0
       }
    else 
    if (opR.equals(">=")) { // :---: ">=" eq. "(AC>0) ou não(AC²>0)"
       programa.adicionaComando("6"+(tempMem+4));                  // cAC>0 => EPI <- X+4        ; 0. cAC > 0 => goto 4
       programa.adicionaComando("1$0");                            // F+0          <- cAC        ; 1. m0 <- cAC
       programa.adicionaComando("4$0");                            // AC           <- cAC * c(F) ; 2. AC <- cAC * m0
       programa.adicionaComando("6"+(tempMem+6));                  // cAC>0 => EPI <- X+6        ; 3. cAC > 0 => goto 6
       programa.adicionaComando("0-1");                            // AC           <- 0          ; 4. AC <- 1
       programa.adicionaComando("9"+(tempMem+7));                  // EPI          <- X+7        ; 5. goto 7
       programa.adicionaComando("0-0");                            // AC           <- 1          ; 6. AC <- 0
       }
    else 
    if (opR.equals("!=")) { // :---:"!=" eq. "AC²>0"
       programa.adicionaComando("1$0");                            // F+0          <- cAC        ; 0. m0 <- cAC
       programa.adicionaComando("4$0");                            // AC           <- cAC * c(F) ; 1. AC <- cAC * m0
       programa.adicionaComando("6"+(tempMem+5));                  // cAC>0 => EPI <- X+5        ; 2. cAC > 0 => goto 5
       programa.adicionaComando("0-0");                            // AC           <- 1          ; 3. AC <- 0
       programa.adicionaComando("9"+(tempMem+6));                  // EPI          <- X+6        ; 4. goto 6
       programa.adicionaComando("0-1");                            // AC           <- 0          ; 5. AC <- 1
       }
    else 
    if (opR.equals(">")) {  // :---: ">"   eq. "AC>0"
       //int tempMem = programa.getMemoria(); // número de instruções até o momento
       programa.adicionaComando("6"+(tempMem+3));                  // cAC>0 => EPI <- X+3        ; 0. cAC > 0 => goto 5
       programa.adicionaComando("0-0");                            // AC           <- 0          ; 1. AC <- 0
       programa.adicionaComando("9"+(tempMem+4));                  // EPI          <- X+4        ; 2. goto 4
       programa.adicionaComando("0-1");                            // AC           <- 1          ; 3. AC <- 1
       }
    else 
    if (opR.equals("<")) {  // :---: "<"   eq. "(-AC)>0"
       //- System.out.println("[Compila!] sinal \'<\': 1$-1; 4$0; 6"+(tempMem+5)+"; 0-0; 9"+(tempMem+6)+"; 0-1");
       programa.adicionaComando("1$0");                            // x0           <- cAC        ; 0. x0 <- cAC
       programa.adicionaComando("0--1");                           // F+0          <- cAC        ; 1. m0 <- -1
       programa.adicionaComando("4$0");                            // AC           <- cAC * c(F) ; 2. AC <- cAC * m0
       programa.adicionaComando("6"+(tempMem+6));                  // cAC>0 => EPI <- X+6        ; 3. cAC > 0 => goto 6
       programa.adicionaComando("0-0");                            // AC           <- 1          ; 4. AC <- 0
       programa.adicionaComando("9"+(tempMem+7));                  // EPI          <- X+7        ; 5. goto 7
       programa.adicionaComando("0-1");                            // AC           <- 0          ; 6. AC <- 1
       }
    else System.out.println("[Compila!completaExprLog] erro, esperava \'==\', \'<=\', \'>=\', \'!=\', \'>\' ou \'>\' entre [E] e [E]");
    }


    // Trata comandos....
  boolean C () { // chamado em: icg.compilador.Compila.item(Compila.java:54)
    if (Configuracao.listaAnaSim) 
       System.out.println("[Compila!C] inicio: <"+item(num_item)+"> "+tipo(num_item)+" == "+Elemento.COMANDOS+" ? <" +
                  ((Elemento)itensLex.elementAt(num_item)).obj()+"> tipo="+((Elemento)itensLex.elementAt(num_item)).tipo());

    try {
    try {
        //if (num_item>-1) //num_linha>-1) // && linhas!=null)
        //   System.out.println("[Compila!C] "+((Elemento)itensLex.elementAt(num_item)).obj+" tipo="+tipo(num_item));

        // se o item lido é um "if"
        if ( tipo(num_item) == Elemento.COMANDOS && item(num_item).equals(Configuracao.cmd_if) ) { // "if"
           //System.out.println("[Compila!C] --- reconhecido C := \"if\": "+item(num_item)+" "+num_item);
           avanca_item(); // mais um terminal reconhecido: "Configuracao.cmd_if"

           if ( !IF() ) { // completa reconhecimento do "if"
      	      pilhaDeInformacoes = pilhaDeInformacoes.concat( "\nErro: Era esperado Expressão lógica." );
    	      return false;
    	      }
           else {
              //if (num_item < itensLex.size())
              //   System.out.println("[Compila!C] é \"if\": "+item(num_item));
              //else System.out.println("[Compila!C] é \"if\": num_item="+num_item+" > "+itensLex.size());
    	      programa.esvaziaAC();
    	      return true;
    	      }	
           }

        // se o item lido é um "while"
        else if ( tipo(num_item) == Elemento.COMANDOS && item(num_item).equals( Configuracao.cmd_while ) ) { // "while"
           //System.out.println("[Compila!C] --- reconhecido C := \""+Configuracao.cmd_while+"\": "+item(num_item)+" "+num_item);
    	   avanca_item(); // mais um terminal reconhecido: "Configuracao.cmd_if"

    	   if ( !WHILE() ) {
    	      pilhaDeInformacoes = pilhaDeInformacoes.concat("\nErro: Era esperado Expressão lógica.");
    	      return false;        	    
    	      }
    	   else {
    	      programa.esvaziaAC();
    	      return true;
    	      }
           }

        // identifica a parte da leitura de variáveis
        else if ( tipo(num_item) == Elemento.COMANDOS && (item(num_item).equals( Bundle.msg("cmdRead") ) ) ) { // "read"/"leia"
           //System.out.println("[Compila!C] --- reconhecido C := \""+Bundle.msg("cmdRead")+"\": "+item(num_item)+" "+num_item);
           avanca_item(); // mais um terminal reconhecido: "Configuracao.cmd_if"

           if ( !IO_LE() ){
    	      pilhaDeInformacoes = pilhaDeInformacoes.concat("\nErro: Parâmetros incorretos.");
    	      return false;
    	      }
    	   else if ( num_item <= itensLex.size() - 1 && tipo(num_item) == Elemento.OUTROS && item(num_item).equals(";")) {
    	      avanca_item();
    	      return true;
    	      }
    	   else {
    	      pilhaDeInformacoes = pilhaDeInformacoes.concat("\nErro: Era esperado \';\'.");
    	      return false;            
    	      }
           }
        
        // identifiva a parte de escrita de variaveis
        else if ( tipo(num_item) == Elemento.COMANDOS && 
                  item(num_item).equals( Bundle.msg("cmdWrite") ) ) { // "write"/"escreve"
          //System.out.println("[Compila!C] --- reconhecido C := \""+Bundle.msg("cmdWrite")+"\": "+item(num_item)+" "+num_item);
          avanca_item(); // mais um terminal reconhecido: "Configuracao.cmd_if"

          if ( !IO_ESCREVA() ) {
    	     pilhaDeInformacoes = pilhaDeInformacoes.concat("\nErro: Parametros incorretos.");
    	     return false;
             }
          else if ( num_item <= itensLex.size() - 1 && tipo(num_item) == Elemento.OUTROS && item(num_item).equals(";")) {
             if (Configuracao.debugOptionAL2)
                System.out.println("[Compila!C] --- reconhecido \""+Bundle.msg("cmdWrite")+"(var)\": "+item(num_item)+" "+num_item);
    	     avanca_item();
    	     return true;
             }
          else {
    	     pilhaDeInformacoes = pilhaDeInformacoes.concat("\nErro: Era esperado \';\'.");
             System.out.println("[Compila!C] ERRO, esperava comando ou \';\' no lugar de \""+item(num_item)+"\": "+num_item);
             System.out.println("[Compila!C]   num_item="+num_item+" itensLex.size()="+itensLex.size()+" "
                                 +tipo(num_item)+"=="+Elemento.OUTROS+" "+item(num_item)+"=="+";");
    	     return false;            
             }
          }
        
        // se é operando
        else if ( tipo(num_item) == Elemento.VARIAVEL ) {
          //if (Configuracao.debugOptionAL2)
          if (Configuracao.listaAnaSim) 
             System.out.println("[Compila!C] --- reconhecido \"var\": "+item(num_item)+" "+num_item);

    	  String var = item(num_item);
          programa.aloca( var );
          avanca_item();
    	
          if ( tipo(num_item) == Elemento.OUTROS && item(num_item).equals( "=" )) {
             //if (Configuracao.debugOptionAL2)
             if (Configuracao.listaAnaSim) 
                System.out.println("[Compila!C] --- reconhecido \"=\": "+item(num_item)+" "+num_item);
     	     avanca_item(); // mais um terminal reconhecido: "Configuracao.cmd_if"

    	     if ( !EA() ) { //N
                pilhaDeInformacoes = pilhaDeInformacoes.concat( "\nErro: Era esperado expressão.");
                return false;
    	        }
    	     else {
                if ( num_item <= itensLex.size() - 1 && tipo(num_item) == Elemento.OUTROS && item(num_item).equals(";")) {
                   programa.ACparaEE( var );
                   programa.esvaziaAC();
                   avanca_item();
                   return true;
                   }
                else {
                   pilhaDeInformacoes = pilhaDeInformacoes.concat("\nErro: Era esperado \';\'.");
                   return false;            
                   }
    	        } // else
    	     } //if ( tipo(num_item) == Elemento.OUTROS && item(num_item).equals( "=" )) {

    	else { //  if ( tipo(num_item) == Elemento.VARIAVEL )
          int n = num_item-1;
    	  //pilhaDeInformacoes = pilhaDeInformacoes.concat("\nErro: Era esperado o operador \'=\'.");
    	  pilhaDeInformacoes = pilhaDeInformacoes.concat("\nErro: Era esperado um comando no lugar de \""+item(n)+"\": item "+n);
          System.out.println("[Compila!C] ERRO, esperava um comando no lugar de \""+item(n)+"\": "+n);
          System.out.println("[Compila!C] num_item="+num_item+" item(num_item)="+item(num_item));
    	  return false;
          }
        } // else if ( tipo(num_item) == Elemento.VARIAVEL ){

        pilhaDeInformacoes = pilhaDeInformacoes.concat("\nErro: Era esperado variável. ");	
        System.out.println("[icg.compilador.Compila!C] erro: \"Erro: era esperado variável.\" em: "+item(num_item));
        return false;

    } catch (java.lang.ArrayIndexOutOfBoundsException aobe) {
        pilhaDeInformacoes = pilhaDeInformacoes.concat("\nC - Erro: final de cadeia");
        aobe.printStackTrace();
        return false;
        }

    } catch(java.lang.ClassCastException cce) {
        // ??????????????????????????????????????????????????????????????????????????????????????????????
        // este erro é provocado principalmente por usar um só vetor, "itensLex", para armazenar o item e também seu
        // tipo, da metade p/ frente, isso é muito "desestruturado"
        pilhaDeInformacoes = pilhaDeInformacoes.concat("\nC - Erro, ClassCastException");
        System.out.println("[Compila!C] Erro, ClassCastException, em tipo com número="+num_item+" "+
                            itensLex.elementAt(num_item)+" "+itensLex.elementAt(itensLex.size()/2+num_item));
        cce.printStackTrace();
        return false;
        }

    }
    
/*
  boolean EXPLOG () {
    int marca;
    boolean nao = false;
    //LinkedList posicoes;
    ListaLigada posicoes;

    try {
      if (Configuracao.listaAnaSim) 
         System.out.println("[Compila!EXPLOG] inicio ");
      // =================================================================================================
      // if (1==1) 
      return ELSS();
      // =================================================================================================
      }
    catch(java.lang.ArrayIndexOutOfBoundsException aobe) {
      pilhaDeInformacoes = pilhaDeInformacoes.concat("\nEXPLOG - Erro: final de cadeia");
      return false;
      }
    }
*/  

    //
    boolean IF () {
    //LinkedList posicoes;
    ListaLigada posicoes;
    int tempMem; // = programa.getMemoria();                 // X número de instruções até o momento

    //if (Configuracao.debugOptionAL)
    if (Configuracao.listaAnaSim) 
       System.out.println("[Compila!IF] início - "+item(num_item)+": "+tipo(num_item));

    try {
      //if (Configuracao.debugOptionAL)
      //   System.out.println("[Compila!IF] "+item(num_item)+"==\'(\' ? "+tipo(num_item));

      if ( tipo(num_item) == Elemento.OUTROS && item(num_item).equals("(") ) {        	
  	 avanca_item();
    	 if ( EXPLOG() ) {
            //System.out.println("[Compila!IF] --- reconhecido EXPLOG em IF() <"+item(num_item)+"> "+tipo(num_item));
    	    //achou uma expressão lógica
    	    if (tipo(num_item) != Elemento.OUTROS || !item(num_item).equals(")") ) {
               //System.out.println("[Compila!IF] erro 1: \"Erro: era esperado \')\'.\" em: "+item(num_item));
               pilhaDeInformacoes = pilhaDeInformacoes.concat("\nErro: era esperado \')\'.");
               return false;
       	       }
    	    else { // achou um ")"
               // Gera código correspondente         |     |
               // ao "if EL", para true e false      +-----+
               //                                     cAC > 0 --------+      X
               //                           +-----------o             |      X+1
               //                           |         +-----+ <-------+      X+2
               //                           |         |true | 
               //                           |         +-----+           ___
               //                           |            o------------+    +
               //                           +-------> +-----+         |    | este bloco só existirá
               //                                     |false|         |    | se houver um "else" (e
               //                                     +-----+         |    | seu bloco)
               //                                     +-----+<--------+  __+
               tempMem = programa.getMemoria();                    // X número de instruções até o momento
               programa.adicionaComando("6"+(tempMem+2));          // cAC>0 => EPI <- X+2        ; X   endereços
               programa.adicionaComando("9ee");                    // EPI          <- ee         ; "ee" vai ser substituido após C()
               // após o cmd C do "true", será preciso voltar a esta instrução em "programa.programaExecutavel.elementAt(tempMem+1)"

               //System.out.println("\n[Compila!IF](1) - - - - - - - - - > "+
               //(tempMem+1)+": "+programa.programaExecutavel().elementAt(tempMem+1)+"\n");

               avanca_item(); // reconhecido "(EXPLOG)"
               //System.out.println("[Compila!IF] ---reconhecido \"if(EXPLOG)\": "+item(num_item)+" "+num_item+" | posicoes="+posicoes);

               if (tipo(num_item) == Elemento.OUTROS && item(num_item).equals("{") ) {
                  //System.out.println("[Compila!IF] --- reconhecido \"if (EXPLOG) {\": "+item(num_item)+" "+num_item);
		  avanca_item();
                  while (true) {
                    if ( C() ) { // gera código do comando C
                       //System.out.println("[Compila!IF] --- reconhecido \"if (EXPLOG) { C();\": "+item(num_item)+" "+num_item);
                       if ( tipo(num_item) == Elemento.OUTROS && item(num_item).equals("}") ) {
                          //System.out.println("[Compila!IF] --- reconhecido \"if (EXPLOG) { C(); }\": "+item(num_item)+" "+num_item);
                          avanca_item();
                          break; // pode ser que este "if" ainda tenha um "else"
              	          }
                       }
                    else {                        	    
                       pilhaDeInformacoes = pilhaDeInformacoes.concat("\nErro (if): era esperado comando.");
                       return false;
                       }
                    } // while
                  }
               else {
                  if ( !C() ) { // gera código do comando C
                     pilhaDeInformacoes = pilhaDeInformacoes.concat("\nErro: era esperado comando.");
                     return false;
                     }
                  }

               // já gerou código do(s) comando(s) C, agora pegue o comando "9ee" inserido acima e substitua-o pelo end. correto
               // após o cmd C do "true", será preciso voltar a esta instrução em "programa.programaExecutavel.elementAt(tempMem+1)"
               int tempMem2 = programa.getMemoria();                // X número de instruções até o momento

               //System.out.println("\n[Compila!IF](2) - - - - - - - - - > "+
               //(tempMem+1)+": "+programa.programaExecutavel().elementAt(tempMem+1)+"\n");
               programa.substituaComando("9"+(tempMem2+1),tempMem+1);// EPI     <- ee     ; pule o próx. cmd (q/ é desvio)
               programa.adicionaComando("9ee");                    // EPI       <- ee     ; "ee" vai ser substituido após ELSE()
                                                                   //                     ; salta p/ após comandos do ELSE()

               boolean jaSubstituido = false;
               if ( tipo(num_item) == Elemento.COMANDOS && item(num_item).equals(Configuracao.cmd_else) ) {
                  avanca_item();
                  if ( ELSE() ) {
                     int tempMem3 = programa.getMemoria();          // X número de instruções até o momento
//System.out.println("\n[Compila!IF](3) - - - - - - - - - - > "+(tempMem2)+": "+programa.programaExecutavel().elementAt(tempMem2)+"\n");
                     programa.substituaComando("9"+(tempMem3),tempMem2);// EPI  <- ee     ; "ee" recebe end. próx. instrução
                     jaSubstituido = true;
                     }
                  }
               if (!jaSubstituido) {
                  programa.substituaComando("9"+(tempMem2+1),tempMem2);// EPI   <- ee      ; "ee" recebe end. próx. instrução
                  }

               return true;
    	       } // else 

    	    } // if ( EXPLOG() )
    	else {
    	    // não encontrou uma expressão lógica.
            System.out.println("[Compila!IF] não é EXPLOG");
    	    pilhaDeInformacoes = pilhaDeInformacoes.concat("\nErro: era esperado expressão lógica.");
    	    return false;        
    	    }
        }
        else {
            System.out.println("[icg.compilador.Compila!IF] erro 2: \"Erro: era esperado \')\'.\" em: "+item(num_item));
            pilhaDeInformacoes = pilhaDeInformacoes.concat("\nErro: era esperado \')\'.");      
            return false;
            }
    }
    catch(java.lang.ArrayIndexOutOfBoundsException aobe) {
        pilhaDeInformacoes = pilhaDeInformacoes.concat("\nIF - Erro: final de cadeia");
        aobe.printStackTrace();
        return false;
        }
  
    } // IF


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
             pilhaDeInformacoes = pilhaDeInformacoes.concat("\nErro: era esperado comando.");
             return false;
       	     }	
          } //while        	
        }
        else {
          if ( !C() ) {
             pilhaDeInformacoes = pilhaDeInformacoes.concat("\nErro: era esperado comando.");
             return false;
             }
          }
    }
    catch (java.lang.ArrayIndexOutOfBoundsException aobe) {            
        pilhaDeInformacoes = pilhaDeInformacoes.concat("\nELSE - Erro: final de cadeia");
        return false;
        }

    return true;    
    }

  
    // Reconhece: "while (EL) { comandos }"
    // Código:  x0 <- cAC                     X atual pos. desta instrução na memória
    //          AC <- cAC * x0
    //          cAC>0 ----------+
    //          ----------------|---+
    //          . <-------------+   |
    //          comandos <------+   |
    //          . <-----------------+

    boolean WHILE () {

    int desvioIncondicional,
        posInicial = programa.getMemoria(),    // número de instruções até o momento (X)
        posFinal;                              //

    try {
        
        if ( tipo(num_item) == Elemento.OUTROS && item(num_item).equals("(") ) {
    	
    	avanca_item();
    	if ( EXPLOG() ) { // traduz a expressão lógica: AC = 0 <=> EL = false
    	   if ( tipo(num_item) != Elemento.OUTROS || !item(num_item).equals(")") ) {                        
              pilhaDeInformacoes = pilhaDeInformacoes.concat("\nErro: era esperado \')\'.");
              return false;
              }
    	   else {
              avanca_item();

              // Supoe que EXP_LOG jah deixou uma lista empilhada.
              // Código:  x0 <- cAC                     X atual pos. desta instrução na memória
              //          AC <- cAC * x0
              //          cAC>0 ----------+
              //          ----------------|---+
              //          . <-------------+   |
              //          comandos <------+   |
              //          . <-----------------+
              int posAtual = programa.getMemoria();    // para saber onde introduziu a inst. "9ee" (em "posAtual+3")
              String str = String.valueOf(posAtual+4); // para pular instrução "9ee"
              if (str.length()<2) str = "0"+str;

              programa.adicionaComando( "1$0");  // $0 <- cAC
              programa.adicionaComando( "4$0");  // AC <- cAC * $0 (cAC²)
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
              	   else {                        	    
              	      pilhaDeInformacoes = pilhaDeInformacoes.concat("\nErro: era esperado comando.");
              	      return false;
              	      }
                   } // while

                 }
              else {
                 if ( !C() ) {
              	    pilhaDeInformacoes = pilhaDeInformacoes.concat("\nErro: era esperado comando.");
              	    return false;
                    }
                 }

              // coloca instrução p/ voltar ao início e substitua o desvio do início do "while" para o final do mesmo
              posFinal = programa.getMemoria();    //
              str = String.valueOf(posInicial);
              if (str.length()<2) str = "0"+str;
              programa.adicionaComando( "9" + str);

              String strF = String.valueOf(posFinal+1); // para pular instrução "9ee"
              //System.out.println("\n ----> posInicial="+posInicial+"  posFinal+1="+(posFinal+1));
              programa.substituaComando("9"+strF,posAtual+3); // substitui a inst. "9ee" introduzida acima
              
              return true;
    	      }
    	}
    	else {
    	    pilhaDeInformacoes = pilhaDeInformacoes.concat("\nErro: era esperado expressão lógica.");
    	    return false;        
    	}
        }
        else {
    	pilhaDeInformacoes = pilhaDeInformacoes.concat("\nErro: era esperado \'(\'.");
    	return false;
        }
        
    } 
    catch(java.lang.ArrayIndexOutOfBoundsException aobe) {            
        pilhaDeInformacoes = pilhaDeInformacoes.concat("\nWHILE - Erro: final de cadeia");
        return false;
    }
    
    }


    boolean IO_LE () {

    try {
        
        if ( tipo(num_item) == Elemento.OUTROS && item(num_item).equals( "(" ) ) {        	
    	avanca_item();
        //System.out.println("[Compila!IO_LE] "+item(num_item);
    	while ( true ) {        	    
    	  if ( tipo(num_item) == Elemento.VARIAVEL ) {
             programa.le( item(num_item) );
             avanca_item();
             if (tipo(num_item) == Elemento.OUTROS && item(num_item).equals(")")) {
                avanca_item();
                return true;
                }
             else if ( tipo(num_item) == Elemento.OUTROS && item(num_item).equals( "," ) )
                // => ha mais parametros na funcao...
                avanca_item();
             else {
                System.out.println("[icg.compilador.Compila!IO_LE] erro: \"Erro: era esperado \')\' ou \',\'\" em: "+item(num_item));
                pilhaDeInformacoes = pilhaDeInformacoes.concat("\nErro: era esperado \')\' ou \',\'.");
                return false;
                }
    	     } // if ( tipo(num_item) == Elemento.VARIAVEL )
    	  else { //
             pilhaDeInformacoes = pilhaDeInformacoes.concat("\nErro: era esperado variável.");
             //System.out.println("[icg.compilador.Compila!IO_LE] erro: \"Erro: era esperado variável.\" em: "+item(num_item));
             return false;
    	     }
          } // while ( true )
    	
        } else {
    	pilhaDeInformacoes = pilhaDeInformacoes.concat("\nErro: era esperado \'(\'.");
    	return false;
        }
    } 
    catch(java.lang.ArrayIndexOutOfBoundsException aobe) {
        pilhaDeInformacoes = pilhaDeInformacoes.concat("\nIO_LE - Erro: final de cadeia");
        return false;
    }

    }

  // Reconhece: ( variavel [, variavel]*)
  boolean IO_ESCREVA () {

    try {
      if ( tipo(num_item) == Elemento.OUTROS && item(num_item).equals( "(" ) ) {
         if (Configuracao.debugOptionAL2) 
         //if (Configuracao.listaAnaSim) 
            System.out.println("[Compila!IO_ESCREVA] --- reconhecido \"(\": "+item(num_item)+" "+num_item);
         avanca_item();
         
         while ( true ) {
           // ainda não dispomos de instrução para carregar constante p/ memória ou AC !!
           if ( tipo(num_item) == Elemento.VARIAVEL ) { // || tipo(num_item) == Elemento.NUMERO ) {
              programa.escreve(item(num_item) ); // gera o código do comando "cmd_escreva" "8EE"
              if (Configuracao.debugOptionAL2)
                 System.out.println("[Compila!IO_ESCREVA] --- reconhecido \"(ident|num\": "+item(num_item)+" "+num_item);
              avanca_item();
              if ( tipo(num_item) == Elemento.OUTROS && item(num_item).equals( ")" ) ) {                            
                 if (Configuracao.debugOptionAL2) 
                    System.out.println("[Compila!IO_ESCREVA] --- reconhecido \")\": "+item(num_item)+" "+num_item);
                 avanca_item();
                 //System.out.println("[Compila!IO_ESCREVA] item atual: "+item(num_item)+" "+num_item);
                 return true;
                 }
              else if ( tipo( num_item) == Elemento.OUTROS && item(num_item).equals( "," ) ) {
                 // => ha mais parametros na funcao...
                 if (Configuracao.debugOptionAL2) 
                    System.out.println("[Compila!IO_ESCREVA] --- reconhecido \",\": "+item(num_item)+" "+num_item);
                 avanca_item();
                 }
              else {
                 if (Configuracao.debugOptionAL2) 
                    System.out.println("[Compila!IO_ESCREVA] Erro: era esperado \')\' ou \',\': "+item(num_item)+" "+num_item);
                 pilhaDeInformacoes = pilhaDeInformacoes.concat("\nErro: era esperado \')\' ou \',\'.");
                 return false;
                 }	   
              } // if ( tipo(num_item) == Elemento.VARIAVEL

           else {
             if (Configuracao.debugOptionAL) 
                System.out.println("[Compila!IO_ESCREVA] Erro: era esperado uma variável, veio "+item(num_item)+" "+num_item);
              pilhaDeInformacoes = pilhaDeInformacoes.concat( "\nErro: era esperado uma variável, veio "+item(num_item));
              return false;
              }
           } // while ( true )
      
         }
      else {  
        pilhaDeInformacoes = pilhaDeInformacoes.concat("\nErro: era esperado \'(\'.");
        return false;
        }
      } 
    catch (java.lang.ArrayIndexOutOfBoundsException aobe) {
        pilhaDeInformacoes = pilhaDeInformacoes.concat("\nIO_ESCREVA - Erro: final de cadeia");
        return false;
        }
    }

} // FIM DA CLASSE
