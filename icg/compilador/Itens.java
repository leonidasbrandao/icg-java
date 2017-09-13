/**
 * 
 * iMath - http://www.matematica.br
 * 
 * <p>Title: iCG - Interactive Computer (Computador Gaveteiro Interativo)</p>
 * 
 * <p>Description: help in compile processes, this class buid the lexical itens, storing them in a "Vector" (used by 'icg.compilador.Analex')
 *    Items to be used in arithmetical expressions {constants , + , - , * , / }
 * </p>
 * 
 * <p>Copyright: Copyleft (c) 2003</p>
 * <p>Company: LInE - http://www.ime.usp.br/line</p>
 * 
 * @author Le�nidas de Oliveira Brand�o</p>
 * @version 2012-05-21 (identation, commente); 2004-08-20 (initial version from didatical example I construct to undergraduation course MAC323 in 2003)
 * 
 * @see    CompilerBaseClass.java; Leia.java; Elementos.java; Exp.java; Expression.java;
 * 
 **/

package icg.compilador;

import java.util.Vector;

import icg.configuracoes.Configuracao;

public class Itens {

  // Analisador Lexico.
  static Vector montaItens (String cadeia) {
    // if (Configuracao.debugOptionAL)  System.out.println("[icg.compilador.Itens! montaItens]\n"+cadeia);
    Vector itensLex = new Vector();
    Vector tipoLex = new Vector();
    // express�o tem que estar separada por brancos

    // ALTERACAO!!!!!!!!!!
    //StringTokenizer st = new StringTokenizer(cadeia, " \n\t");
    
    Elemento elem;
    String str;
    int conta = 0, limite;
    itensLex = AnaLex.constroiTokens(cadeia); 
    limite = itensLex.size();  // itensLex.size() est� sendo alterado    
    //-
    if (Configuracao.debugOptionAL)  System.out.println("[icg.compilador.Itens! montaItens]  --- in�cio: itensLex.size()="+limite);

    //while (st.hasMoreTokens()) {
    for (conta=0; conta<limite; conta++) { // itensLex.size() est� sendo alterado, n�o pode ser usado aqui!
      try {
        str = itensLex.elementAt(conta).toString();
        elem = new Elemento(str);
        //if (Configuracao.debugOptionAL)  System.out.println("[icg.compilador.Itens!montaItens] "+conta+" "+str+" "+conta+"<"+itensLex.size()+"?");
      } catch (java.lang.ClassCastException cce) {
        System.err.println("[icg.compilador.Itens!montaItens] "+conta+" "+itensLex.elementAt(conta)+" n�o � do tipo Elemento <- erro: " + cce.toString());
        continue;
        }

      //-if (Configuracao.debugOptionAL)  System.out.println(" "+str+" - "+elem.tipo());

      if (elem.tipo() == Elemento.INVALIDO) {
         System.err.println("Erro: '" + str + "' variavel com caractere " + "invalido!");
         return null;
         }

      tipoLex.addElement(elem);
      //System.out.print(str+" ");
      }
    //if (Configuracao.debugOptionAL)  System.out.println("\nN�mero de itens l�xicos: "+conta);

    //-if (Configuracao.debugOptionAL)  System.out.println("[icg.compilador.Itens! montaItens]  --- fim");
    return tipoLex;
    }

  // Para testar se est� montando os itens l�xicos corretamente
  static void listaItens(Vector itensLex) {
    // para o caso de querer testar se o AL est� pegando os tipos corretos

    if (itensLex == null) {
       return;
       }

    for (int i = 0; i < itensLex.size(); i++) {
        System.out.print( ( (Elemento) itensLex.elementAt(i)).obj() + " ");
        }
    System.out.println("\nN�mero de itens l�xicos: " + itensLex.size());
    }

  }
