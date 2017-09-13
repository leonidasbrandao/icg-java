/**
 * 
 * iMath - http://www.matematica.br
 * 
 * <p>Title: iCG - Interactive Computer (Computador Gaveteiro Interativo)</p>
 * 
 * <p>Description: help in compile
 *    used to construct expressions
 * </p>
 * 
 * <p>Copyright: Copyleft (c) 2003</p>
 * <p>Company: LInE - http://www.ime.usp.br/line</p>
 * 
 * @author Leônidas de Oliveira Brandão</p>
 * @version 2012-05-21 (identation, comments); 2005-08-18
 * 
 * @see    CompilerBaseClass.java; Leia.java; Elementos.java; Exp.java;
 * 
 **/

package icg.compilador;

class Expressao {

  Exp [] exp = new Exp[10];
  int tam = 0;

  public Expressao () {
    }

  public void add (int pos) {
    exp[tam] = new Exp(pos);
    tam++;
    }

  public void add (String op) {
    exp[tam] = new Exp(op);
    tam++;
    }

  }
