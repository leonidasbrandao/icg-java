/**
 * 
 * iMath - http://www.matematica.br
 * 
 * <p>Title: iCG - Interactive Computer (Computador Gaveteiro Interativo)</p>
 * 
 * <p>Description: help in compile (expressions)</p>
 * 
 * <p>Copyright: Copyleft (c) 2003</p>
 * <p>Company: LInE - http://www.ime.usp.br/line</p>
 * 
 * @author Leônidas de Oliveira Brandão
 * @version 2012-05-21 (indentation, comments); 2004-08-05
 * 
 * @see    CompilerBaseClass.java; Leia.java; Elementos.java; Expression.java;
 */

package icg.compilador;

class Exp {
  int pos;
  String oper;
  int tipo;

  public Exp (int p) {
    pos = p;
    oper = "";
    tipo = 0;
    }

  public Exp (String op) {
    pos = 0;
    oper = op;
    tipo = 1;
    }

  }
