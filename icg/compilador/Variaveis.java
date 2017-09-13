/**
 * 
 * iMath - http://www.matematica.br
 * 
 * <p>Title: iCG - Interactive Computer (Computador Gaveteiro Interativo)</p>
 * 
 * <p>Description: compile (produce "binary" code to iCG)</p>
 *                 construct variables
 * 
 * <p>Copyright: Copyleft (c) 2003</p>
 * <p>Company: LInE - http://www.ime.usp.br/line</p>
 * 
 * @author Leônidas de Oliveira Brandão
 * @version 2012-05-21 (identation and comments; max var. in Configuracao); 2004-08-05 (first version from an undergradeate course MAC323 - in 2003)
 * 
 * @see icg.compilador.CompilerBaseClass, icg.compilador.Elementos, icg.compilador.Itens, icg.compilador.Var 
 * 
 **/

package icg.compilador;

import icg.configuracoes.Configuracao;

public class Variaveis {

  public Var [] var = new Var[Configuracao.maxVariables];
  private int pos = 0;

  public Variaveis () {
    }

  public int size () {
    return pos;
    }

  public void add (String n, int v, int p) {
    var[pos] = new Var(n, v, p);
    pos++;
    }

  public int existe (String n) {
    int i;
    for (i = 0; i < pos; i++) {
      if (var[i].nome.equals(n)) {
        return i;
        }
      }
    return -1;
    }

  public String posMemoria (String n) {
    String str;
    int index = existe(n);
    try {
      if (index == -1) {
        return "";
        }
      str = new Integer(var[index].posicao).toString();
      if (str.length() == 1) {
        str = new String("0").concat(str);
        }
      return str;
      }
    catch (Exception e) {
      // System.err.println("Error...");
      return "";
      }
    }

  }
