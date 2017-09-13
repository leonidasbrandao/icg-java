/**
 * 
 * iMath - http://www.matematica.br
 * 
 * <p>Title: iCG - Interactive Computer (Computador Gaveteiro Interativo)</p>
 * 
 * <p>Description: compile (produce "binary" code to iCG)</p>
 *                 read a file and return a buffer
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: LInE - http://www.ime.usp.br/line</p>
 * 
 * @author Leônidas de Oliveira Brandão, Francisco
 * @version 2012-05-21 (identation and comments; printStackTrace()); 2004-08-05 (first version)
 * 
 * @see icg.compilador.CompilerBaseClass
 * 
 */

package icg.compilador;


import java.io.*;

public class LeArq {

  static public FileReader arq;
  static public BufferedReader buffer;

  static public BufferedReader buffer(String arquivo) {
    try {
      arq = new FileReader(arquivo);
    } catch (java.io.FileNotFoundException e) {
      e.printStackTrace();
      }
    buffer = new BufferedReader(arq);
    return buffer;
    }

  // Devolve uma linha do buffer.
  static public String linha (BufferedReader buff) {

    String aux = null;

    try {
      aux = buff.readLine();
    } catch (java.io.IOException e) {
      e.printStackTrace();
      }

    return aux;
    }

  // Devolve uma linha do buffer.
  static public String tudo (BufferedReader buff) {

    String aux = new String();
    String t = null;
    int i = 1;

    t = LeArq.linha(buff);

    while (t != null) {
      aux = aux.concat(t);
      aux = aux.concat("\n");
      t = LeArq.linha(buff);
      }

    return aux;
    }

  }
