/**
 * 
 * iMath - http://www.matematica.br
 * 
 * <p>Title: iCG - Interactive Computer (Computador Gaveteiro Interativo)</p>
 * 
 * <p>Description: buttons with main options
 * 
 * <p>Copyright: Copyleft (c) 2003</p>
 * <p>Company: LInE - http://www.ime.usp.br/line</p>
 * 
 * @author Leônidas de Oliveira Brandão
 * 
 * @version 30/03/2006 (versão incial: iGeom)
 *
 * @see icg.emulador.EmulatorBaseClass; icg.compilador.CompilerPanel, icg.ig.ToolTip (to lauche msg to the graphical buttons)
 * 
 **/

package icg.ig; 

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.applet.*;

import icg.configuracoes.Configuracao;
import icg.iCG;
import icg.msg.Bundle;

public class Botao extends Canvas implements MouseListener { //

  private iCG icg;

  private static String msgEpaint = "[Bt!paint]";

  //---- DB
  // AWT: técnica de DOUBLE BUFFERING evita "flicker" e aqui reduz falhas em mostrar os botoes
  Image offscreen  = null;
  Graphics offgraphics = null;

  public void setForeground (Color cor) { super.setForeground(cor); }

  private boolean selecionado;

  private static Botao botaoAtual = null; // define variável com última ação, usado no TrataMouse
  public static Botao botaoAtual () { return botaoAtual; }

  //___ popup menu
  private static ToolTip staticToolTip = null;
  private static final boolean ehPrimario = true; // para caso de botoes primarios x secundarios
  private static final Color COR_FUNDO = new Color(255, 241, 168); //240,240,0); // cor bordo
  private static final int DELTAx = 10; //8; // deslocamento vertical adicional
  private static final int DELTAy = 10; //5; // deslocamento horizontal adicional: para mostrar msg inteira
  private static final int ALTURA_LINHA = 15; // altura de cada linha de msg (msg grandes podem ter \n)
  private final int BOTAO_OFFSET = 30; // primario => acima do bota; secundario => abaixo do botao
  private int PU_HEIGHT;
  private int PU_WIDTH;
  private int LINE_HEIGHT;
  private int NUMBER_OF_LINES;
  private String[] msgArray; // para poder mostrar msg grandes: colocar \n para quebrá-la
//  private PopMenu menu = null;
  private Component owner = null;
  private Container mainContainer;
  private static Botao estePopUp = null; // truque para remover PopUp sobrando, via Botao ou via AreaDeDesenho
  private boolean mostrePopUp;

  private static final Color COR_PADRAO = Color.white;
  private static final Color COR_SELECIONADO = Color.red;

  public static final Color gray1 = new Color(138,138,138); // para degradê de cinzas:
  public static final Color gray2 = new Color(148,148,148); // do 1 para o 3, vai ficando mais
  public static final Color gray3 = new Color(158,158,158); // claro
  // darkGray  =  64  64  64
  // gray      = 128 128 128
  // lightGray = 192 192 192
  public static final Color branco1 = new Color(245,245,245); // para degradê de cinzas:
  public static final Color branco2 = new Color(235,235,235); // do 1 para o 3, vai ficando mais
  public static final Color branco3 = new Color(225,225,225); // claro

  private static int tamX = 50; // para o caso de erro de leitura das imagens
  private static int tamY = 50;

  private String nome;   // nome do botão
  private int acao = -1; // identificador da ação associada ao botão

  private Image img;
  private boolean abaixado,
                  ativado,
                  botao_de_baixo;
  private int modo;
  private boolean botaoPainel = false; // também usado para dizer que não é para adicionar "listener" (útil em Exercicio)

  public static Dimension imgTamanho = new Dimension(30, Configuracao.ALTURA_BARRAS);

  public Image img () { return img; }
  public int acao () { return acao; }

  // Chamado em igeom/botoes/PainelMenus!BotaoMenus(BotaoMenus [] botoesMenus,int inicio)
  // para tentar evitar erro em algum navegador que produza "this.img==null"
  public void img (Image img) { this.img = img; }

  public String nome () { return nome; }


  // Chamado em: igeom.ig.PainelBotoes: para exercícios
  public void abaixado (boolean bool) { this.abaixado = bool; }


  // Chamado em: igeom.ig.PainelBotoes: para exercícios
  public Botao (iCG icg, String nome, int Acao, Image img) {
    // também usado para dizer que não é para adicionar "listener"
    // Se "botaoPainel==true", então vem de JanelaExercicio => reduzir!
    this.icg = icg;
    botaoPainel = true;
    this.completa(nome,Acao,img,true);
    this.abaixado = true;
    }


  public Botao (iCG icg, String nome, int Acao, Image img, boolean tipo_botao) {
    this.icg = icg;
    this.completa(nome,Acao,img,true);
    }

   
  public void completa (String nome, int Acao, Image img, boolean tipo_botao) {
    addMouseListener(this);
    this.botao_de_baixo = tipo_botao; // para posicionar corretamente o balão "popup": precisa saber se é botão de baixo
    this.nome = nome;
    this.acao = Acao;
    this.img  = img;
    this.modo = -1;
    abaixado  = false;
    ativado   = true;
    setBackground(COR_PADRAO);
    this.selecionado = false;

    //___ popup menu
    String tooltip = nome;
    if (tooltip != null && !tooltip.trim().equals("")) {
       staticToolTip = new ToolTip(tooltip, this);
       }
    //staticErrTool = tooltip;
    //___ popup menu


    // Se "botaoPainel==true", então vem de JanelaExercicio => reduzir!
    if (!botaoPainel) this.setBounds(34, 67, 32, 32);
    else this.setBounds(2*34/3, 2*67/3, 2*32/3, 2*32/3);

    }

  //---- DB
  public void pinta () {
    Graphics gr = null;
    if (offscreen != null) { // primeiro "paint" entra antes de construir primeira "offscreen"
       gr = offscreen.getGraphics();
       if (gr!=null) //H
          offgraphics = gr;
       paint(gr);
       }
    } //  void pinta()

  //---- DB
  private void copy2DoubleBuffer (Graphics gr) { // copy to 'offScreen' and draw the image
    // copia tudo na tela
    if (gr==null) {
       return; // selecionar botao primario => deselecionar outro botao primario => faz 'repaint' dele, que esta vazio => cai aqui
       }
    else
       gr.drawImage(offscreen,0,0,this);
    } // void copy2DoubleBuffer(Graphics gr)



  //---- DB
  public void paint (Graphics gr) {
    Dimension tamanho,
              imgTamanho = Botao.imgTamanho;
    int       l=1; // largura das linhas de botões
    try {

    tamanho = this.getSize(); // size()

    // Se "botaoPainel==true", então vem de JanelaExercicio => reduzir!
    if (botaoPainel) {  // vem de JanelaExercicio => reduza imagem
       tamanho = new Dimension(tamanho.width, tamanho.height);
       imgTamanho = new Dimension(20, 20);
       }

    if (img!=null)  { // Em Netscape 4 dá erro de permissão p/ carregar imagens
       // Se "botaoPainel==true", então vem de JanelaExercicio => reduzir!
       if (!botaoPainel) imgTamanho.setSize(img.getWidth(this), img.getHeight(this)); // 
       }
    else {
       //- System.out.println(msgEpaint+": "+img);
       return; //imgTamanho = new Dimension(img.getWidth(this), img.getHeight(this));
       }

    //---- DB
    if (offscreen == null) {
       // double buffering techniche
       offscreen = createImage(tamanho.width, tamanho.height);
       }
    offgraphics = offscreen.getGraphics(); //H
    gr = offscreen.getGraphics(); // pega último buffer "gráfico"
    //---- DB

    // [ABAIXADO]
    if (selecionado) { // quando o botão está abaixado, sem o mouse "clicado" sobre ele
        gr.setColor(Color.lightGray);
        gr.fillRect(0, 0, tamanho.width, tamanho.height); // preenche o fundo todo com um cinza claro

        gr.drawImage(img, tamanho.width/2 - imgTamanho.width/2,
       		          tamanho.height/2 - imgTamanho.height/2, this);

	// Se quiser um outro tipo de efeito no Botao, use apenas as duas linhas seguintes:
        //- gr.draw3DRect(1, 1, tamanho.width-3, tamanho.height-3,false);
        //- gr.draw3DRect(2, 2, tamanho.width-5, tamanho.height-5,true);
        // comente o restante

        //       (1)
        //     +-----+
        // (2) |     | (4)
        //     |     |
        //     +-----+
        //       (3)

        gr.setColor(Color.white);
        gr.drawRect(0, 0, tamanho.width-1, tamanho.height-1); // preenche o bordo todo com branco

      // (1) Sombra horizontal superior (escura)
        gr.setColor(Color.gray);
        gr.drawLine(1, 1, tamanho.width-2, 1); //
        gr.setColor(gray1);
        gr.drawLine(2, 2, tamanho.width-2, 2);
        gr.setColor(gray2);
        gr.drawLine(3, 3, tamanho.width-2, 3);

      // (2) Sombra vertical esquerda (escura)
        gr.setColor(Color.gray);
        gr.drawLine(1, 1, 1, tamanho.height-2); 
        gr.setColor(gray1);
        gr.drawLine(2, 2, 2, tamanho.height-2); // reforça linha vertical de sombra
        gr.setColor(gray2);
        gr.drawLine(3, 3, 3, tamanho.height-2); // reforça linha vertical de sombra

      // (3) Claro horizontal inferior (clara)
        // gr.setColor(Color.white);
        // gr.drawLine(l, tamanho.height-l, tamanho.width-l, tamanho.height-l);
        // gr.setColor(branco1);
        // gr.drawLine(2, tamanho.height-2, tamanho.height-2, tamanho.height-2); // reforça linha vertical de sombra

      // (4) Claro vertical direita (clara)
        // gr.setColor(Color.white);
        // gr.drawLine(tamanho.width-0, 0, tamanho.width-0, tamanho.width-0); //
        // gr.setColor(branco1);
        // gr.drawLine(tamanho.width-1, l, tamanho.width-1, tamanho.width-l); //

        //---- DB
        copy2DoubleBuffer(this.getGraphics()); // copy to 'offScreen' and draw the image

        return;    
        }

    // [ABAIXADO]
    if (abaixado) { // quando mouse está "clicado" sobre o botão
        gr.setColor(Color.white);
        gr.fillRect(0, 0, tamanho.width, tamanho.height);
        gr.drawImage(img, tamanho.width/2 - imgTamanho.width/2,
    		          tamanho.height/2 - imgTamanho.height/2, this);
        gr.setColor(Color.gray);
        gr.drawLine(0, 0, tamanho.width-l, 0);                // sombra horizontal
        gr.drawLine(0, 0, 0,               tamanho.height-l); // sombras verticais
        gr.drawLine(1, 0, 1,               tamanho.height-l); // 
        //gr.drawLine(2, 0, 2,               tamanho.height-l); // 
	//        gr.drawLine(0, 0, 0, tamanho.width-l);
        }

    // [LEVANTADO]
    else { // quando o botão não está "clicado" (está levantado)
        gr.setColor(Color.lightGray);
        gr.fillRect(0, 0, tamanho.width, tamanho.height);
        gr.drawImage(img, tamanho.width/2 - imgTamanho.width/2,
         		  tamanho.height/2 - imgTamanho.height/2, this);
        gr.fillRect(0, 0, tamanho.width, tamanho.height);
        // java.lang.InternalError: obsolete interface used
	//  at sun.java2d.NullSurfaceData.getRaster(NullSurfaceData.java:78)
	//  ...
	//  at icg.ig.Botao.paint(Botao.java:248)
	//  ...
        gr.drawImage(img, tamanho.width/2 - imgTamanho.width/2,
    		          tamanho.height/2 - imgTamanho.height/2, this);

        gr.setColor(Color.white);
        gr.drawLine(0, 0, tamanho.width-l, 0);
        gr.drawLine(0, 0, 0, tamanho.height-l);
        gr.setColor(Color.gray);
        gr.drawLine(tamanho.width-l, tamanho.height-l, l, tamanho.height-l);
        gr.drawLine(tamanho.width-l, tamanho.height-l, tamanho.width-l, l);  // tinha erro // linha hor. sombra
        }

    } catch (java.lang.Exception npe) {
        //  System.err.println(msgEpaint+": "+img);
        }

    //---- DB
    copy2DoubleBuffer(this.getGraphics()); // copy to 'offScreen' and draw the image

    } // public void paint(Graphics gr)


  public void setImage (Image img) {
    this.img = img;
    }

 // TrataMouse.textoArea é quem "distribui" o tratamento de cliques nos botões, vê se é primário ou secundário
 public void mouseClicked (java.awt.event.MouseEvent me) {
   // sempre que clica sobre botão cai aqui
   // Se "botaoPainel==true", então vem de JanelaExercicio => reduzir!
   if (!botaoPainel) {
      // MSGCOMPILA; MSGEMULADOR; MSGENVIAR; MSGGABARITO; MSGEMULAR; MSGPASSO; MSGATUALIZA; MSGMEMORIA; MSGCODIGO
       if (acao==Configuracao.MSGCOMPILADOR)   // muda para ambiente de compilação
	  icg.setBotaoCompilador();
       else
       if (acao==Configuracao.MSGEMULADOR)     // muda para ambiente de compilação
	  icg.setBotaoEmulador();
       else
       if (acao==Configuracao.MSGENVIAR) {     // enviar solução do exercício e seu resultado
          icg.acaoEnviar();
          }
       else
       if (acao==Configuracao.MSGGABARITO) {   // dispara anotador de gabarito
          icg.acaoGabarito();
          }
       else
       if (acao==Configuracao.MSGEMULAR) {     // emula código objeto
          icg.getEmulatorBaseClass().acaoEmular(); // EmulatorBaseClass (estava em Emulador_Panel)
          }
       else
       if (acao==Configuracao.MSGPASSO) {      // emular passo-a-passo
          icg.getEmulatorBaseClass().acaoEmularPP(); // EmulatorBaseClass
          }
       else
       if (acao==Configuracao.MSGAJUDA) {      // ajuda emulador/compilador
          icg.acaoAjuda(); //
          }
       else
       if (acao==Configuracao.MSGSOBRE) {      // sobre o iCG
          icg.acaoSobre(); // 
          }
       else
       if (acao==Configuracao.MSGATUALIZA) {   // atualiza código na memória do emulador
          icg.actionUpdate(); // getEmulatorBaseClass().getEmulatorMainPanel().acaoAtualiza(); // em iCG (icg/emulador/EmulatorMainPanel.java)
          }
       else
       if (acao==Configuracao.MSGCOMPILA) {    // compila código fonte
          icg.painelCompilador().acaoCompila(); // em icg.compilador.CompilerPanel
          }
      }
   else // {PainelBotoes.botaoClicado(this); this.repaint(); }
      System.out.println("[Bt!mouseClicked]: sem ação... "+me);
   }


  public void mouseReleased (java.awt.event.MouseEvent me) {          
    //System.out.println("mouseUp?  "+me);         
    if (!botaoPainel) {
       if (ativado) {
          abaixado = false;
          pinta(); //DB_ repaint();
	  }
       else selecione();
       }
    }


  public void mousePressed (java.awt.event.MouseEvent me) {
    // sempre que clica sobre botão cai aqui

    //PopUpThread.estah_sobreNovoBotao = false;
    //PopUpThread.eh_novoTempo=false;
    //PopUpGerencia.threadPopUP.stop();
    if (!botaoPainel)
    if (ativado) {
       if (!abaixado) {
          abaixado = true;
          pinta(); //DB_ repaint();
          }
       }
	//return false;
    }
  

  public void mouseEntered (java.awt.event.MouseEvent evt) {
    Point p = this.getLocation();
    int x = p.x, y = p.y;

    if (this == evt.getComponent()) {
       if (this.acao!=-1)
          icg.setMensagem(Configuracao.mensagem[this.acao]);
       else
          icg.setMensagem(Bundle.msg(this.nome));
       }

    } // mouseEntered(java.awt.event.MouseEvent evt)


  public void mouseExited (java.awt.event.MouseEvent me) {
    //R PopUpThread.estah_sobreNovoBotao = false; // anota que não está mais sobre botão, para apagar balão da tela
    //___ popup menu
    //R removePopUp(); //HH remove a msg da janela
    }

  public boolean estaAtivado () {
    return ativado;
    }

  public boolean estaAbaixado () {
    return abaixado;
    }

  public void setEnabled (boolean tf) {
    if (tf == true) {
        ativado = true;
        abaixado = false;
        pinta(); //DB_ repaint();
	}
    else {
        ativado = false;
        abaixado = false;
        pinta(); //DB_ repaint();
	}
    }

  public int getMode () {
    return modo;
    }

  public boolean selecionado () {
    return selecionado;
    }

  public void selecione () {
    this.selecionado = true;
    botaoAtual = this; // define último botão clicado, usado no TrataMouse    
    pinta(); //DB_ repaint();
    }

  // Chamado em:
  public void deselecione () {
    this.selecionado = false;
    pinta(); //DB_ repaint();
    }


   
  }

