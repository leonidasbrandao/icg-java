/**
 * 
 * iMath - http://www.matematica.br
 * 
 * <p>Title: iCG - Interactive Computer (Computador Gaveteiro Interativo)</p>
 * 
 * <p>Description: compile (produce "binary" code to iCG)</p>
 * 
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: LInE - http://www.ime.usp.br/line</p>
 * @author Le�nidas de Oliveira Brand�o
 * @version 2012-05-21 (added 'read', 'write' - 'leia', 'escreva'); 2008-10-02
 * 
 */

package icg.compilador;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Font;
import java.awt.Label;
import java.awt.Panel;
import java.awt.Rectangle;
import java.awt.TextArea;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import icg.iCG;
import icg.msg.Bundle;
import icg.ig.JanelaAjuda;
import icg.ig.Botao;
import icg.ig.TrataImage;
import icg.configuracoes.Configuracao;

public class Compilador extends Panel {

  // private static final String
  //   Bundle.msg("msgCode") - MSGCODFONTE  = "C�digo",
  //   Bundle.msg("msgObject") - MSGCODOBJETO = "Execut�vel",
  //   Bundle.msg("msgEnterCode") - MSGDIGITECOD = "Aqui vir�o as mensagens. Digite seu programa na janela de \"C�digo\"",
  //   Bundle.msg("msgEmptyCode") - MSGERROVAZIO = "C�digo vazio! � preciso digitar seu c�digo fonte.";

  // n�o mais uso, agora vai iCG.labelMensagem
  //- Label labelEndereco = new Label(Configuracao.strEndereco); // Label("http://www.matematica.br/programas/icg");

  public static final int x0 = iCG.x0-10, y0 = iCG.y0; // coordenadas iniciais para colocar pain�is na janela principal

  iCG icg;

  Panel painelCodigos;

  Botao botaoAjuda,
        botaoCompila;

//-  Button botaoCompila, botaoAjuda, botaoAnalisar;
  Button botaoAnalisar;

  TextArea areaCodigoFonte,    // �rea para c�digo fonte
           areaErrosCodigo,    // �rea para erros no c�digo fonte
           areaCodigoObjeto;   // �rea para c�digo objeto

  boolean compilou = false;

  BorderLayout borderLayout1 = new BorderLayout();

  // Botao botoes[] tem que ser passado para EmuladorApplet -> Emulador_Panel na ordem:
  //       botaoGabarito=botoes[0], botaoEmula=botoes[1], botaoEmulaPP=botoes[2], botaoAtualiza=botoes[3]
  //       botaoInfo=botoes[4], botaoAjuda=botoes[5], botaoCompila = botoes[6]
  public Compilador(iCG icg, Botao [] botoes) {
    this.icg = icg;
    this.botaoAjuda   = botoes[5];
    this.botaoCompila = botoes[6];
    System.out.println("[Compilador!Compilador] botaoAjuda.acao="+botaoAjuda.acao());
    try { iniciaCompilador(); } catch (Exception ex) { ex.printStackTrace(); }
    }

  public boolean compilacaoOK () {
    return compilou;
    }

  public String getCodigo () {
    return areaCodigoObjeto.getText();
    }

  // Chamado em: iCG.iniciaCG()
  // Usa: PainelCorFonte(Object obj, int tipoItem, String texto) {
  public void setFonte (Font fonte) {
    // System.out.println("[Compilador!setFonte] fonte="+fonte);
    areaCodigoFonte.setFont(fonte); 
    areaCodigoObjeto.setFont(fonte);
    areaErrosCodigo.setFont(fonte); 
    }
  public Font getFonte () {
    return areaCodigoFonte.getFont();
    }

  void iniciaCompilador () { //throws Exception {

    painelCodigos = new Panel(null);

    // Botoes

    //- Bot�es 'botaoCompila' e 'botaoAjuda' em iCG (incluindo tratamento icg.acaoCompila() -> this.acaoCompila())
    botaoAnalisar = new Button("Analisar");
    try {
      botaoCompila.setBackground(Configuracao.corAzulEscuro1); // new Color(0, 111, 194));
    } catch(Exception e) { System.err.println("Compilador: "+e); }
    try {
      botaoCompila.setForeground(Configuracao.corFrente1); // Color.white
    } catch(Exception e) { System.err.println("Compilador: "+e); }

    // try { System.out.println("Compilador: "+Configuracao.corAzulEscuro1+", "+Configuracao.corFrente1);
    // } catch(Exception e) { System.err.println("Compilador: "+e); }

    botaoCompila.setFont(Configuracao.fonteBotao); //

    botaoCompila.setBounds(new Rectangle(iCG.xCompila,iCG.yCompila, TrataImage.ALTURA,TrataImage.LARGURA));


    // Areas de Texto
    areaCodigoFonte  = new TextArea(Bundle.msg("msgCode"),  70, 60); //
    areaCodigoObjeto = new TextArea(Bundle.msg("msgObject"), 70, 10); // 
    areaErrosCodigo  = new TextArea(Bundle.msg("msgEnterCode"),  5, 80); //

    this.setLayout(null);

    painelCodigos.setBackground(new Color(0, 111, 194)); // janela principal, com c�digos fonte e objeto e msg de erro
    painelCodigos.setForeground(Color.white);
    painelCodigos.setBounds(new Rectangle(iCG.xPainelFC, iCG.yPainelFC,  iCG.lPainelFC, iCG.aPainelFC)); // (x0+  0, y0+  45,  590, 315)
    painelCodigos.setLayout(null);

    areaCodigoFonte.setBackground(new Color(0, 111, 194));
    areaCodigoFonte.setForeground(Color.white);

    areaCodigoFonte.setFont(Configuracao.fonteCodigoFonte); //

    areaCodigoFonte.setBounds(new Rectangle(iCG.xCodFont, iCG.yCodFont,  iCG.lCodFont, iCG.aCodFont));

    areaCodigoObjeto.setBackground(new Color(0, 111, 194)); // janela para c�digo objeto (gerado)
    areaCodigoObjeto.setForeground(Color.white);
    areaCodigoObjeto.setFont(Configuracao.fonteCodigoObjeto); // 

    areaCodigoObjeto.setEditable(false); //setEnabled(false);
    areaCodigoObjeto.setBounds(new Rectangle(iCG.xCodObj, iCG.yCodObj,  iCG.lCodObj, iCG.aCodObj));

    areaErrosCodigo.setBackground(new Color(0, 111, 194)); // janela inferior para msg de erro (ou outras)
    areaErrosCodigo.setForeground(Color.white);
    areaErrosCodigo.setFont(Configuracao.fonteErrosCodigo); // 

    areaErrosCodigo.setEditable(false); //setEnabled(false);
    areaErrosCodigo.setBounds(new Rectangle(iCG.xMsgComp, iCG.yMsgComp,  iCG.lMsgComp, iCG.aMsgComp));

    //x0+150, y0+360,  380,  15
    //- labelEndereco.setBounds(new Rectangle(Configuracao.leX, Configuracao.leY, Configuracao.leL, Configuracao.leA));
    //- labelEndereco.setFont(Configuracao.ftEndereco);

    this.setBackground(Color.black);
    this.setEnabled(true);

    // Adicionando tudo

    painelCodigos.add(areaCodigoFonte, null);
    painelCodigos.add(areaCodigoObjeto, null);
    painelCodigos.add(areaErrosCodigo, null);

    this.add(painelCodigos, null);
    this.setVisible(true);
    }

  // Chamada de iCG
  public void acaoCompila () {
    Compila parteAnalisada;
    String s = areaCodigoFonte.getText();
    // System.out.println("[icg.compilador.Compilador] s="+s);
    if (s==null || s=="" || s.equals(Bundle.msg("msgCode"))) {
       areaErrosCodigo.setText(Bundle.msg("msgEmptyCode"));
       return;
       }
    //- System.out.println("[icg.compilador.Compilador.acaoCompila] ");
    parteAnalisada = new Compila( s );
    //- System.out.println("[icg.compilador.Compilador.acaoCompila] parteAnalisada="+parteAnalisada);
    areaErrosCodigo.setText( "" );
    areaCodigoObjeto.setText( "" );
    
    if (parteAnalisada.OK )
       areaCodigoObjeto.setText( parteAnalisada.programa.codigo() );
    
    compilou = parteAnalisada.OK;
    areaErrosCodigo.setText( parteAnalisada.informacoesDeSaida() );
    }


  class ButtonHandler implements ActionListener {

    public void actionPerformed (ActionEvent e) {
      String s = ( (Button) e.getSource()).getLabel(); //??????
      }

    }

  // M�todo para desenhar bordas para gerar barra superior de bot�es 
  // Emulador_Panel e Compilador
  public void paint (Graphics gr) {
    Dimension tamanho;
    int       l=1; // largura das linhas de bot�es
    try {
      tamanho = this.getSize(); // size()
      int posX = iCG.xEmul, posY = iCG.yEmul; // posi��o do botaoComp

      // Desenha um ret�ngulo dentro do qual est�o os bot�es (este ficar� sob os bot�es)
      gr.setColor(Configuracao.corFundoBarraBt); // 
      gr.fillRect(posX-2, posY-2, tamanho.width-24, Configuracao.ALTURA_BARRAS+2); //
      gr.setColor(Configuracao.corLinhaBarraBt); //Color.white);
      gr.drawRect(posX-2, posY-2, tamanho.width-24, Configuracao.ALTURA_BARRAS+2); //
      gr.setColor(Color.black);
      gr.drawRect(1, 1, tamanho.width-2, tamanho.height-3); //

      // painelInferior: Rectangle(iCG.xPainelInf,iCG.yPainelInf,iCG.lPainelInf,iCG.aPainelInf)
      gr.setColor(Color.black);
      gr.drawRect(iCG.xPainelInf-1,iCG.yPainelInf-1,iCG.lPainelInf+2,iCG.aPainelInf+2);

      // desenha borda em barra de mensagem: iCG.labelMensagem
      gr.drawRect(iCG.leX-1, iCG.leY-1, iCG.leL+2, iCG.leA+2);

      /*
      Este n�o adianta, pois ap�s este 'paint' � pintada a corde fundo do painelInferior que sobrep�em-se ao 'paint'...
      // Desenha borda em cada um de:
      // Instrucao_Executada: (xIExec, yIExec, largIExec, altIExec) <- texto "Executada [ ]" -> "Instru��o "
      // instrucao: (xInst, yInst, largInst, altInst)
      // labelSaida: (xLACC, yLACC, largLACC, altLACC) // este � o Label que conter� as sa�das de comandos "8EE"
      // text_Saida: (xSai,  ySai, largSai, altSai) //(x,y, l,a): (x,y) coord. de posi��o, (l,a) larg. e alt.
      // texto_Acumulador: (xTtAC, yTtAC, largTtAC, altTtAC)
      gr.drawRect(xIExec-1, yIExec-1, largIExec+2, altIExec+2);
      gr.drawRect(xLACC -1, yLACC -1, largLACC +2, altLACC +2);
      gr.drawRect(xLACC -1, yLACC -1, largLACC +2, altLACC +2);
      gr.drawRect(xSai  -1,  ySai -1, largSai  +2, altSai  +2);
      gr.drawRect(xTtAC -1, yTtAC -1, largTtAC +2, altTtAC +2);
      */

    } catch (Exception ex) {
      System.err.println("[EP] Erro");
      }
    } // paint()


  }
