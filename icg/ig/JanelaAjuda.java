/**
 * 
 * iMath - http://www.matematica.br
 * 
 * <p>Title: iCG - Interactive Computer (Computador Gaveteiro Interativo)</p>
 * 
 * <p>Description: Janela com informações sobre a sintaxe da linguagem alto-nível do iCG e como compilar
 * Janela para entrar um texto na área de desenho (usando o Medida)
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
 * @author Leônidas de Oliveira Brandão
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
  public JanelaAjuda (String msgTitulo, String [] textLines, boolean visible) { // agora é "static" em BarraDeDesenho
    super(msgTitulo, textLines, visible); // new Frame(),true);
    }

  }
