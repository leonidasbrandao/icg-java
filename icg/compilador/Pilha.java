/**
 * 
 * iMath - http://www.matematica.br
 * 
 * <p>Title: iCG - Interactive Computer (Computador Gaveteiro Interativo)</p>
 * 
 * <p>Description: compile (produce "binary" code to iCG)</p>
 *                 my implementation of a stack...
 * 
 * <p>Copyright: Copyleft (c) 2003</p>
 * <p>Company: LInE - http://www.ime.usp.br/line</p>
 * 
 * @author Leônidas de Oliveira Brandão, Francisco
 * @version 2012-05-21 (identation and comments); 2004-08-05 (first version from an undergradeate course MAC323 - in 2003)
 * 
 * @see icg.compilador.CompilerBaseClass, icg.compilador.Elementos, icg.compilador.Itens, icg.compilador.LeArq
 * 
 **/

package icg.compilador;

import icg.util.ListaLigada;

public class Pilha {

    ListaLigada pilha; //LinkedList pilha;

    public Pilha () {
      pilha = new ListaLigada();//LinkedList();
      }

    void empilha ( Object o ) {
      pilha.addLast( o );
      }

    Object desempilha () {
      if (pilha!=null)
         return pilha.removeLast();
      else return null;
      }

  }

