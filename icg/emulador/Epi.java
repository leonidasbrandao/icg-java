/**
 * 
 * iMath - http://www.matematica.br
 * 
 * <p>Title: iCG - Interactive Computer (Computador Gaveteiro Interativo)</p>
 * 
 * <p>Description: emulator (run the "binary" code of iCG)</p>
 *                 manage the EPI (address of next instruction in Portuguese)
 * 
 * <p>Copyright: Copyleft (c) 2003</p>
 * <p>Company: LInE - http://www.ime.usp.br/line</p>
 * 
 * @author Leônidas de Oliveira Brandão, Heitor, Newton, Paulo

 * @version 1.0: 2012-05-21 (identation and comments); 2006-04-02 (first version from an undergradeate course MAC323 - in 2003)
 * 
 * @see icg.emulador.iCGEmulator; icg.emulador.Epi; icg.emulador.Memoria
 * 
 **/

package icg.emulador;

class Epi {

  private int X, Y; // where is the instruction in memory

  public void setXY (int x, int y) {
    X = x;
    Y = y;
    }

  public int getX () {
    return X;
    }

  public int getY () {
    return Y;
    }

  }
