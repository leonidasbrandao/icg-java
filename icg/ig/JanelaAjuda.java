/**
 * 
 * iMath - http://www.matematica.br
 * 
 * <p>Title: iCG - Interactive Computer (Computador Gaveteiro Interativo)</p>
 * 
 * <p>Description: Janela com informa��es sobre a sintaxe da linguagem alto-n�vel do iCG e como compilar
 * Janela para entrar um texto na �rea de desenho (usando o Medida)
 *
 * Chamado por: 
 *
 * +------------------------------------------------------+
 * | iCG                                    <logo iCG?>   |
 * +------------------------------------------------------+
 * | Digite na janela abaixo sua frase                    |
 * +------------------------------------------------------+
 * |                        <OK>                          |
 * +------------------------------------------------------+
 * 
 * <p>Copyright: Copyleft (c) 2003</p>
 * <p>Company: LInE - http://www.ime.usp.br/line</p>
 * 
 * @author Le�nidas de Oliveira Brand�o
 * 
 * @version: 08/06/2012 (primeira versao no iCG); vindo do iGeom de 30/08/2004-25/09/2004
 *
 * @see icg/ig/ImagePanel.java <top Panel with logo image of iCG>
 * 
 **/

package icg.ig;

import icg.msg.Bundle;
import icg.configuracoes.Configuracao;

public class JanelaAjuda extends JanelaDialogo {

  //
  public JanelaAjuda (String msgTitulo, String [] textLines, boolean visible) { // agora � "static" em BarraDeDesenho
    super(msgTitulo, textLines, visible); // new Frame(),true);
    }

  }
