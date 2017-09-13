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
 * @see icg.compilador.CompilerBaseClass, icg.compilador.Elementos, icg.compilador.Itens, icg.compilador.Variaveis 
 * 
 **/

package icg.compilador;

class Var {

  public String nome;
  public int valor;
  public int posicao;

  public Var (String n, int v, int p) {
    nome = n;
    valor = v;
    posicao = p;
    }

  public String nome () {
    return this.nome;
    }

  public String valor () {
    String str = new Integer(valor).toString();
    if (str.length() == 0) {
      str = new String("000").concat(str);
      }
    else if (str.length() == 1) {
      str = new String("00").concat(str);
      }
    else if (str.length() == 2) {
      str = new String("0").concat(str);
      }
    else {
      str = new String(str);
      }
    return str;
    }

  public int posicao () {
    return this.posicao;
    }

  }
