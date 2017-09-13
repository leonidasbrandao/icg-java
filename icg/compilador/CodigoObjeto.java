/**
 * 
 * iMath - http://www.matematica.br
 * 
 * <p>Title: iCG - Interactive Computer (Computador Gaveteiro Interativo)</p>
 * 
 * <p>Description: help in compile
 *    Usado junto com o CompilerBaseClass.java, cria o codigo objeto enquanto o programa eh analisado.
 *    Example code with 12 lines: 0 799 / 1 798 / 2 797 / 3 099 / 4 110 / 5 098 / 6 210 / 7 110 / 8 097 / 9 210 / 10 196 / 11 896 / 12 0-0000
 *    Clean version:
            799
            798
            797
            099
            110
            098
            210
            110
            097
            210
            196
            896
            0-0000

 * </p>
 * 
 * <p>Copyright: Copyleft (c) 2003</p>
 * <p>Company: LInE - http://www.ime.usp.br/line</p>
 * 
 * @author Leônidas de Oliveira Brandão (colaboration of  Marcio Teruo Akyama)</p>
 * @version 2012-05-21 (added 'read', 'write' - 'leia', 'escreva', void em Configuracao); 2008-10-02 
 * 
 * @see    CompilerBaseClass.java; Leia.java; Elementos.java;
 * 
*/

package icg.compilador;

import java.util.Vector;
import java.util.Hashtable;

import icg.configuracoes.Configuracao;
import icg.util.ListaLigada;
import icg.util.No;

public class CodigoObjeto {

    // Auxiliary messages to trace lexical analyser
    // See 'icg.configuracoes.Configuracao.debugOptionBinary' - must be 'true'
   
    //private HashMap variaveis; // tabela de indices para os nomes das vars.
    private Hashtable variaveis; // tabela de indices para os nomes das vars.
    //private ArrayList programaExecutavel; // programa inteiro.
    private Vector programaExecutavel; // programa inteiro.
    private boolean acumuladorCheio;
    private int ponteiroDaMemoria; // local das variáveis na memoria.
    private int contador;          // posições relativas na "pilha de execução".

    // Alterado em "adicionaComando(String comando)" e "CodigoObjeto(int ini)" (com "memoria = ini;")
    private int memoria;           // posições na memória (número de instruções válidas na memória)

    String MSG; //Guarda as mensagens de alerta durante a compilação.

    // Usadas para substituicao na pilha de execucao e nos desvios.
    final char posicaoSimbolica = '$';
    final String V = new String( "@v" );
    final String F = new String( "@f" );


    public Vector programaExecutavel () { // programa inteiro
      return programaExecutavel;
      }
   
   
    // Chamado em: icg.compilador.CompilerBaseClass.<init>(CompilerBaseClass.java:181) ou (CompilerBaseClass.java:198)
    public CodigoObjeto (int ini) { //
        //if (Configuracao.debugOptionBinary) System.out.println("[CodigoObjeto!CodigoObjeto] ini="+ini+" ");//+programaExecutavel.elementAt(0));
	variaveis = new Hashtable();//HashMap();
	programaExecutavel = new Vector(100); //ArrayList( 100 );
	MSG = new String();
	ponteiroDaMemoria = 99;
	memoria = ini;
	contador = 0;
	for ( ; ini > 0; ini-- )
	    //programaExecutavel.add( new String( "000000" ) );
	    programaExecutavel.addElement( new String( "000000" ) );
        }

    void aloca (String nome) {
        if ( !variaveis.containsKey( nome ) ) {
           if (Configuracao.debugOptionBinary)  System.out.println("[CodigoObjeto!aloca(String)] nome="+nome+"  "+String.valueOf(ponteiroDaMemoria));
	   variaveis.put( nome, String.valueOf(ponteiroDaMemoria) );
	   ponteiroDaMemoria--;
	   if ( ponteiroDaMemoria < memoria )
	       MSG += "\nAtencao: variaveis alocadas sobre o programa. " + "Variavel: " + nome ;
	   }
        }

    void esvaziaAC () {
	acumuladorCheio = false;
        }

    public String toString () {
	int i;
	String s = new String( " " );
	for ( i = 0; i < programaExecutavel.size(); i++ ) {
	    s += "\n" + String.valueOf( i ) + ". " +
	    //(String) programaExecutavel.get( i );
	    (String) programaExecutavel.elementAt( i );
	    }
	return s;
        }

    public String codigo () {
	int i;
	String s = new String( "" );
	for ( i = 0; i < programaExecutavel.size(); i++ ) {
	    //s += (String) programaExecutavel.get( i ) + "\n";
	    s += (String) programaExecutavel.elementAt( i ) + "\n";
	    }
	return s;
        }

    String enderecoDaVariavel (String nome) {
	return (String) variaveis.get( nome );
        }

    boolean jaFoiAlocada (String nome) {
	return variaveis.containsKey( nome );
        }

    int getMemoria() {
	return memoria;
        }

     
  // Substitua um comando já adicionado: para o Vector "programaExecutavel"
  // Usado para completar instrução de desvio de "if EL { ... } else { ... }" (e acertar end. de final de bloco ORs em E())
  void substituaComando (String comando, int pos) {
    if (Configuracao.debugOptionBinary)  System.out.println("[CodigoObjeto!substituaComando] -------> " + programaExecutavel.size() + " comando = " + comando);
    if ( pos > ponteiroDaMemoria || pos < 0)
       MSG += "\nAtenção: o código objeto não cabe na memória. Posição da instrução: " + memoria;
    else
       programaExecutavel.setElementAt(comando,pos);
    }


  // Adiciona todos os comandos gerados aqui: para o Vector "programaExecutavel"
  void adicionaComando (String comando) {
    if (Configuracao.debugOptionBinary) {
       //T if (memoria==3)try { String str00="";System.out.println(str00.charAt(3)); } catch (Exception e) { e.printStackTrace(); }
       System.out.println("[CodigoObjeto!adicionaComando] -------> " + memoria+"="+programaExecutavel.size()+ " comando = "+comando);
       }

    //if (comando.length()<3) { // complete com '0'        <----- não está precisando disso!
    //   comando = comando.charAt(0) + "0" + comando.charAt(1);
    //   }

    programaExecutavel.addElement( comando );
    //if (Configuracao.debugOptionBinary) System.out.println("[CodigoObjeto!adicionaComando(String comando)] "+memoria+": "+comando);
    memoria++;
    if ( memoria > ponteiroDaMemoria )
       MSG += "\nAtencao: o codigo objeto nao cabe na memoria. " +	"Posicao da instrucao: " + memoria;
       }

    // Só deve ser chamado uma vez, após todos os comandos serem gerados
    // Procura em todos os comandos do programa aqueles que tem alguma posição simbólica (p.e. uma futura posição na pilha de execução)
    // Estes comandos fazem parte da pilha de execução
    // forma do codigo -> 'codigo' + posicao simbolica
    // void atualizaComandos( LinkedList indices ) {
    void atualizaComandos (ListaLigada indices) { // chamado em: icg.compilador.CompilerBaseClass.E(CompilerBaseClass.java:516)
      String s1, s2, s3 = null;
      Integer posicao;
      int numInstrucoes = memoria;
      int posInicial    = memoria + 1; // programaExecutavel.size(); // primeira posição disponível de memória é "pos"
      int pos, posComplemento;
      Vector novoProgramaExecutavel = new Vector();

      if (Configuracao.debugOptionBinary)  System.out.println("\n\n[CodigoObjeto!atualizaComandos] número total de instruções = " + numInstrucoes);
      // para gerar erro: System.out.println(s3.charAt(2));
      
      char c = '-';
      for (int i=0; i<numInstrucoes; i++) {
          s1 = (String)programaExecutavel.elementAt(i);
          if (s1.charAt(1)=='$') { // é posição de memória que precisa ser trocada
             c = '*';
             s2 = s1.substring(2, s1.length()); // copia complemento do endereço, após posição 1 (com o '$')
             posComplemento = Integer.parseInt(s2);
             pos = posInicial + posComplemento + 1;      // posição inical para "pilha de execução"
             s3 = "" + pos;                              // converte para string
             if (s3.length()<2) s3 = "0" + s3.charAt(0); // endereço com um só dígito, complete com '0' à esquerda
             s2 = s1.charAt(0) + s3;                     // posição final como string
             novoProgramaExecutavel.addElement(s2);
             }
          else {
             c = '-';
             novoProgramaExecutavel.addElement(s1);
	     }

          if (Configuracao.debugOptionBinary) System.out.println(c+": "+novoProgramaExecutavel.elementAt(i));
          }
      programaExecutavel = novoProgramaExecutavel;
      //for (int i=0; i<pos; i++) {
      //    programaExecutavel = novoProgramaExecutavel;
      //    }
      
/*    programaExecutavel.addElement( comando );
      //ListIterator iterador = indices.listIterator( 0 );
      No no = indices.inicioLista();
      int p, indicePilhaExecucao ;
      indicePilhaExecucao = ponteiroDaMemoria;
      while ( no != null ) { //iterador.hasNext() ) {
          //posicao = (Integer) iterador.next();
          posicao = (Integer) no.obj();
          //s = (String) programaExecutavel.get( posicao.intValue() );
          s = (String) programaExecutavel.elementAt( posicao.intValue() );
          p = s.indexOf( posicaoSimbolica );
          if ( p == -1 ) continue;
          s1 = s.substring( s.indexOf( posicaoSimbolica ) + 1, s.length() );
          try {
      	p = Integer.parseInt( s1 );
      	p = indicePilhaExecucao - p;
          }
          catch( NumberFormatException e ) {
      	MSG += "\nOoops, problemas com o pilha de execucao!";
          }
          s = s.substring( 0, s.indexOf( posicaoSimbolica ) ) + String.valueOf( p );
      
          //programaExecutavel.set( posicao.intValue(), s );
          //iterador.remove();
          no = no.proximo();
          }
*/
        }

    //void atualizaDesvios( ListIterator iterador, String operando,
    void atualizaDesvios (No iterador, String operando, boolean nao) {
	String s, s1;
	String busca = new String();
	Integer posicao;
	int p;
	if ( operando == null ) {
	   // quer dizer que chegou no fim do EXP_LOG
	   String busca2;
	   if ( nao ) {
	      busca = F;
	      busca2 = V;
	      }
	   else {
	      busca = V;
	      busca2 = F;
	      }
	   while ( iterador != null ) { // iterador.hasNext() ) {
	     posicao = (Integer) iterador.obj(); //iterador.next();
	     //s = (String) programaExecutavel.get( posicao.intValue() );
	     s = (String) programaExecutavel.elementAt( posicao.intValue() );
	     p = s.indexOf( busca );
	     if ( p == -1 ) {
	         // => que com certeza existe o 'busca2'...
	         p = s.indexOf( busca2 );
	         s1 = s.substring( 0, p );
	         s1 = s1.concat( F );
	         p = s.indexOf( "+1" );
	         if ( p != -1 )
	     	s1 = s1.concat( "+1" );
	         //programaExecutavel.set( posicao.intValue(), s1 );
	         continue;
	         }
	     s1 = s.substring( 0, p );
	     s1 = s1.concat( String.valueOf( memoria ) );
	     //programaExecutavel.set( posicao.intValue(), s1 );
	     //iterador.remove();
	     iterador.proximo();
	     }
	   }
	else if ( operando.equals( "&&" ) ) {
	    if ( nao )
		busca = F;
	    else
		busca = V;
	}
	else if ( operando.equals( "||" ) ) {
	    if ( nao )
		busca = V;
	    else
		busca = F;
	}
	while ( iterador != null ) { // iterador.hasNext() ) {
	    posicao = (Integer) iterador.obj(); //iterador.next();
	    //s = (String) programaExecutavel.get( posicao.intValue() );
	    s = (String) programaExecutavel.elementAt( posicao.intValue() );
	    p = s.indexOf( busca );
	    if ( p == -1 )
		continue;
	    s1 = s.substring( 0, p );
	    p = s.indexOf( "+1" );
	    if ( p == -1 )
		s1 = s1.concat( String.valueOf( memoria ) );
	    else
		s1 = s1.concat( String.valueOf( memoria + 1 ) );
	    //programaExecutavel.set( posicao.intValue(), s1 );
	    //iterador.remove();
	    iterador.proximo();
	}
        }


    /* Quando este metodo eh chamado:
       
       PILHA_EXEC ->    | EXP2 | - (topo da pilha == AC)
                        | EXP1 |
			| etc  |
			| ...  |
    */

    //LinkedList aplicaComparacao ( String a ) {
    ListaLigada aplicaComparacao ( String a ) {

	ListaLigada l = new ListaLigada(); //LinkedList l = new LinkedList();
	String comando;

	// EXP2 - EXP1
	l.add( new Integer( memoria ) );
	SUB();

	if ( a.equals( "<" ) ) {
	    // AC > 0 desvia para V + 1 ???
	    l.add( new Integer( memoria ) );
	    adicionaComando( new String( "6" + V + "+1" ) ); // V = "@v"
	    // senao desvia para F
	    l.add( new Integer( memoria ) );
	    adicionaComando( new String( "9" + F ) );
	}
	else if ( a.equals( ">" ) ) {
	    // EMPILHA -1
	    l.add( new Integer( memoria ) );
	    empilha( String.valueOf( -1 ) );
	    // MUL
	    l.add( new Integer( memoria ) );
	    MUL();
	    // AC > 0 desvia para V
	    l.add( new Integer( memoria ) );
	    adicionaComando( new String( "6" + V + "+1" ) );
	    // senao desvia para F
	    l.add( new Integer( memoria ) );
	    adicionaComando( new String( "9" + F ) );
	}
	else if ( a.equals( "<=" ) ) {
	    // EMPILHA -1
	    l.add( new Integer( memoria ) );
	    empilha( String.valueOf( -1 ) );
	    // MUL
	    l.add( new Integer( memoria ) );
	    MUL();
	    // AC > 0 desvia para F
	    l.add( new Integer( memoria ) );
	    adicionaComando( new String( "6" + F ) );
	}
	else if ( a.equals( ">=" ) ) {
	    // AC > 0 desvia para F
	    l.add( new Integer( memoria ) );
	    adicionaComando( new String( "6" + F ) );
	}
	else if ( a.equals( "==" ) ) {
	    // AC > 0 desvia para F
	    l.add( new Integer( memoria ) );
	    adicionaComando( new String( "6" + F ) );
	    // EMPILHA -1
	    l.add( new Integer( memoria ) );
	    empilha( String.valueOf( -1 ) );
	    // MUL
	    l.add( new Integer( memoria ) );
	    MUL();
	    // AC > 0 desvia para F
	    l.add( new Integer( memoria ) );
	    adicionaComando( new String( "6" + F ) );
	}
	else {
	    // AC > 0 desvia para V
	    l.add( new Integer( memoria ) );
	    adicionaComando( new String( "6" + V  + "+1" ) );
	    // EMPILHA -1
	    l.add( new Integer( memoria ) );
	    empilha( String.valueOf( -1 ) );
	    // MUL
	    l.add( new Integer( memoria ) );
	    MUL();
	    // AC > 0 desvia para V
	    l.add( new Integer( memoria ) );
	    adicionaComando( new String( "6" + V + "+1" ) );
	    // senao desvia para F
	    l.add( new Integer( memoria ) );
	    adicionaComando( new String( "9" + F ) );
	}

	// atualiza os comandos que usam a pilha de execucao.
	atualizaComandos( l );

	// devolve os comandos que precisam ser atualizados em relacao
	// ao desvio.
	return l;
        }
	    

    void empilha (String objeto) {
	if ( acumuladorCheio ) {
	    //ACparaEE( posicaoSimbolica + String.valueOf( contador++ ) ); // esta solução era para armazenar variáveis após a última instrução
            String str = posicaoSimbolica + String.valueOf(contador++); 
            //if (Configuracao.debugOptionBinary) System.out.println("[CodigoObjeto!empilha] contador="+contador+" -> "+str);
	    ACparaEE(str);
            }
	else
	    acumuladorCheio = true;
	if ( ponteiroDaMemoria - contador < memoria  )
	    MSG += "\nAtencao: Estouro da pilha. Posicao da instrucao: " + memoria + ".";
	EEparaAC( objeto );
        }

    // EE <- cAC
    void ACparaEE (String EE) { // chamado em: icg.compilador.CodigoObjeto.empilha(CodigoObjeto.java:317) <- icg.compilador.CompilerBaseClass.ID(CompilerBaseClass.java:343)
	String comando = null;
        //if (Configuracao.debugOptionBinary) System.out.println("[CodigoObjeto!ACparaEE] <EE="+EE+">"); // System.out.println(comando.charAt(1));
	if ( jaFoiAlocada( EE ) )
	    comando = new String( "1" + enderecoDaVariavel( EE ) );
	else
	    comando = new String( "1" + EE );
	adicionaComando( comando );
        }

    // AC <- cEE
    // Aqui tb esta a parte sobrecarregada: AC <- 11, por exemplo.
    void EEparaAC( String EE ) {
	String comando = new String( "" );
	if ( jaFoiAlocada( EE ) )
	    comando = new String( "0" + enderecoDaVariavel( EE ) );
	else
	    comando = new String( "0-" + EE );
	adicionaComando( comando );
        }

    void MUL () {
	String comando;
	contador--; //- a última posição da pilha de execução pode ser liberada
	comando = new String( "4" + posicaoSimbolica + String.valueOf( contador ) );
	adicionaComando( comando );
        }

    void SUB () {
	String comando;
	contador--; //- a última posição da pilha de execução pode ser liberada
	comando = new String( "3" + posicaoSimbolica + String.valueOf( contador ) );
	adicionaComando( comando );
        }

  // Codigo de imprimir o conteudo da variavel EE na saida padrao.
  void escreve (String EE) {
    String comando;
    if (Configuracao.debugOptionBinary) 
       System.out.print("[CodigoObjeto!escreve(String)] " + programaExecutavel.size() + " EE = " + EE);

    if ( !jaFoiAlocada( EE ) ) { // a "impressão" propriamente dita está em "EmuladorApplet.master.lacc.setText(String)"
       MSG += "\nAtencao: variavel nao inicializada: " + EE;
       aloca( EE );
       }
    comando = new String( "8" + enderecoDaVariavel( EE ) );
    if (Configuracao.debugOptionBinary)  System.out.println("  comando=" + comando);
    adicionaComando( comando );
    }

    // Codigo de ler do teclado e armazenar em EE.
    void le (String EE) {
	String comando;
	aloca( EE );
	comando = new String( "7" + enderecoDaVariavel( EE ) );
	adicionaComando( comando );
        }

    void aplicaCodigo (String comando) {
      contador--; //- a última posição da pilha de execução pode ser liberada
      //if (Configuracao.debugOptionBinary) System.out.println("[CodigoObjeto!aplicaCodigo] "+comando + posicaoSimbolica + String.valueOf( contador )); // +"|"+contador+"|" );
      adicionaComando( comando + posicaoSimbolica + String.valueOf( contador ) );
      }

  }
