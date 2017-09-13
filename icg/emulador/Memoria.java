/**
 * 
 * iMath - http://www.matematica.br
 * 
 * <p>Title: iCG - Interactive Computer (Computador Gaveteiro Interativo)</p>
 * 
 * <p>Description: emulator (run the "binary" code of iCG)</p>
 *                 mamory of the iCG
 * 
 * <p>Copyright: Copyleft (c) 2003</p>
 * <p>Company: LInE - http://www.ime.usp.br/line</p>
 * 
 * @author Leônidas de Oliveira Brandão, Heitor, Newton, Paulo

 * @version 1.0: 2012-05-21 (identation and comments); 2006-03-16 (additional 'try/catch'); 2005-10-17 (first version from an undergradeate course MAC323 - in 2003)
 * 
 * @see icg.emulador.iCGEmulator; icg.emulador.Epi; icg.emulador.Memoria
 * 
 **/

package icg.emulador;

import java.awt.*;

class Memoria extends Label {

  public int valor;
  String conteudo;

  public Memoria () {
    }

  public Memoria (String s) throws NumberFormatException {
    setText(s);
    }

  public void setText (String conteudo) throws NumberFormatException {
    if (conteudo.length() > 6) {
       conteudo = conteudo.substring(0, 6);
       }
    try {
    if (conteudo.length() > 2) {
       // 0-EE ou 0*EE
       if (conteudo.charAt(0) == '0') {
          // 0-EE: AC <- EE : para atribuição de constantes
          if (conteudo.charAt(1) == '-') {
             valor = Integer.parseInt(conteudo.substring(2, conteudo.length()));
             }
          else
          // 0*EE: 
          // 0*EE: AC <- c(cEE) : para indereção (apontadores)
          if (conteudo.charAt(1) == '*') {
             valor = Integer.parseInt(conteudo.substring(2, conteudo.length()));
             }
          }
       else

       if (conteudo.charAt(0) == '1' && (conteudo.charAt(1) == '-' || conteudo.charAt(1) == '*') ) {

          //1-11-9
          if (conteudo.length() >= 4) {

          // 1*EE: 
             if (conteudo.charAt(1) == '*') {
               valor = Integer.parseInt(conteudo.substring(2, conteudo.length()));
               }
             else // 
             if (conteudo.charAt(2) == '-') { // é negativo, instrução tipo '1--EE' => erro
               valor = Integer.parseInt(conteudo.substring(0, conteudo.length()));
               }
             else 
               valor = Integer.parseInt(conteudo.substring(4, conteudo.length()));

             }
          else
            valor = Integer.parseInt(conteudo.substring(0, conteudo.length()));

          } // if (conteudo.charAt(0) == '1' && conteudo.charAt(1) == '-')

      else {
          // if (conteudo.charAt(1)=='*')
          // System.out.println("[Memoria] "+conteudo+" #conteudo="+conteudo.length());
          valor = Integer.parseInt(conteudo); // e se 'conteudo' não tiver um inteiro ???
	  }

      } // if (conteudo.length() > 2)
    else 
      valor = Integer.parseInt(conteudo);
    } catch (Exception exp) {
      // [16/03/2006]
      // Vale a pena acrescentar um WarningDialog aqui ?
      System.err.println("[Memória] Erro: não foi possíve convertar '"+conteudo+"' para int: " + exp);
      }
    this.conteudo = conteudo;
    super.setText(this.conteudo);
    } // setTex(String conteudo)


  public int getValor () {
    return valor;
    }

  public String getConteudo () {
    return conteudo;
    }

  public int [] getInstrucao () {
    int[] aux = new int[4];
    if (conteudo.length() >= 3 && conteudo.charAt(0) != '-') {
      aux[0] = conteudo.charAt(0) - '0';
      // Truque: 0-k => AC <- k : para atribuir constantes
      if (conteudo.charAt(1) == '-') {
        aux[1] = -1;
        }
      else
      // Truque: 0*EE => AC <- c(cEE)K : para indireção
      if (conteudo.charAt(1) == '*') {
        aux[1] = -2;
        }
      else {
        aux[1] = conteudo.charAt(1) - '0';
        }
      aux[2] = conteudo.charAt(2) - '0';
      if (conteudo.length() >= 4) {
        aux[3] = conteudo.charAt(3) - '0';
        }
      return aux;
      }
    return null;
    // possivel trocar por aux = 0,0,0 e entao
    // considerara fim de programa

    }

  // Test
  public static void main (String args[]) {
    Memoria m = new Memoria();
    m.setText("123456");
    int[] i = m.getInstrucao();
    System.out.println(m.valor);
    }

  }
