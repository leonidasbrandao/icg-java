/**
 * 
 * iMath - http://www.matematica.br
 * 
 * <p>Title: iCG - Interactive Computer (Computador Gaveteiro Interativo)</p>
 * 
 * <p>Description: compile (produce "binary" code to iCG)</p>
 *                 read from keyboard an string and return it as a String
 * 
 * <p>Copyright: Copyleft (c) 2003</p>
 * <p>Company: LInE - http://www.ime.usp.br/line</p>
 * 
 * @author Leônidas de Oliveira Brandão, Francisco
 * @version 2012-05-21 (identation and comments; printStackTrace()); 2004-08-05 (first version from an undergradeate course MAC323 - in 2003)
 * 
 * @see icg.compilador.CompilerBaseClass, icg.compilador.Elementos, icg.compilador.Itens, icg.compilador.LeArq
 * 
 */

package icg.compilador;

import java.io.*;

public class Leia {

  static BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

  // Leia line do Teclado.
  public static String readLine () {
    String s = null;
    try {
      s = in.readLine();
    }
    catch (java.io.IOException ioe) {
      System.err.println("readLine: Formato invalido: " + ioe.getMessage());
      ioe.printStackTrace();
      }
    return s;
    }

  }
