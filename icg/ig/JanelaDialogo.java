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
 * | String [] messages (to TextArea)                     |
 * +------------------------------------------------------+
 * |                        <OK>                          |
 * +------------------------------------------------------+
 * 
 * <p>Copyleft (c) 2003</p>
 * <p>LInE - http://www.ime.usp.br/line</p>
 * 
 * @author Leônidas de Oliveira Brandão
 * 
 * @version: 08/07/2012 (fix key listener to botaoOK); 08/06/2012 (first version in iCG); 30/08/2004-25/09/2004 (origin in iGeom)
 *
 * @see icg/ig/ImagePanel.java <top Panel with logo image of iCG>
 * 
 **/

package icg.ig;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Image;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.*; //

import icg.msg.Bundle;
import icg.configuracoes.Configuracao;

public class JanelaDialogo extends Dialog { //v2 implements KeyListener

  private static final String LOGOICG = "logo_icg.gif";

  private static final Color
          corTextoJanela = Color.white, // cor das letras "Geom. Int. na Internet"...
          corTexto       = Color.black, // cor das letras da frase
          corFundoBordos = Configuracao.corAzulClaro,
          corFundoTexto  = Color.white; // cor do fundo da área em digita-se o texto

  private static final Font
          fonteJanela = new Font ("Helvetica", Font.BOLD, 10),
          fonteEndereco = new Font ("Courier", Font.BOLD, 8);

  private static final int
          largura = 340, // largura e altura da janela de criação de texto
          altura  = 170;

  private Image imgLogo = TrataImage.trataImagem(false, LOGOICG);

  private Frame janTexto;
  private TextArea textAreaWithMessages = null; // used whenever there are text lines
  private Panel painelImg = null;

  private Panel painel_princ,  painel_fundo;
  private TopPanel painel_topo;

  private Label msgTitulo;
  private Button botaoOk;

  // Get an array of String and returns one String with '\n' breaks
  public static String lines2String (String [] textLines) {
    String strLines = "";
    int count = textLines.length;
    if (textLines==null) return "";
    strLines = textLines[0];
    for (int i__=1; i__<count; i__++)
        strLines += "\n" + textLines[i__];
    return strLines;
    }

  // From: JanelaAjuda.java
  public JanelaDialogo (String msgTitulo, String [] textLines, boolean visible) {
    super(new Frame("iCG :: LInE"), false); // "true" pode deixar usuario confuso - parecer travar iCG
    //System.out.println("JanelaDialogo 1\n\n\n");

    janTexto = new Frame("iCG :: LInE"); //
    this.setTitle("iCG :: LInE");

    preparaJanela(msgTitulo, textLines);
    this.setVisible(visible);
    }

  //
  public JanelaDialogo (String msgTitulo, String [] textLines) {
    super(new Frame("iCG :: LInE"), false); // "true" pode deixar usuario confuso - parecer travar iCG
    //System.out.println("JanelaDialogo 2\n\n\n");

    janTexto = new Frame("iCG :: LInE"); //
    this.setTitle("iCG :: LInE");

    preparaJanela(msgTitulo, textLines);
    this.setVisible(true);
    }

  // Builds the window interface
  private void preparaJanela (String msgTitulo, String [] textLines) {
    this.msgTitulo = new Label(msgTitulo);
    // Image im  = Toolkit.getDefaultToolkit().getImage("igeom/img/igeom.gif"),
    addWindowListener( new WindowAdapter() { // substitui o "deprecated" WINDOW_DESTROY
        public void windowClosing(WindowEvent e) { dispose(); }
      });

    setBackground(JanelaDialogo.corFundoBordos); // cor do fundo das letras e bordos
    setForeground(JanelaDialogo.corTextoJanela); // cor do texto do botão OK

    painel_topo = new TopPanel(imgLogo);
    painel_princ= new Panel();
    painel_fundo= new Panel();
    painel_princ.setLayout(new BorderLayout());

    if (textLines!=null) { //
       // System.out.println("[JD.preparaJanela] painelImg="+painelImg+" "+textAreaWithMessages.getText());
       textAreaWithMessages = new TextArea(lines2String(textLines), 70, textLines.length+2); // used whenever there are text lines
       textAreaWithMessages.setEditable(false);
       textAreaWithMessages.setBackground(JanelaDialogo.corFundoTexto);
       textAreaWithMessages.setForeground(JanelaDialogo.corTexto);
       // textAreaWithMessages.requestFocus(); - let to the 'botaoOK'
       painel_princ.add(textAreaWithMessages); //
       setSize(JanelaDialogo.largura, JanelaDialogo.altura + (textLines.length+1)*20); // vem do IGeom.java: 380,565
       }
    else {
       java.awt.FontMetrics fm = getFontMetrics(fonteJanela); // owner.getFont());
       int size = fm.stringWidth(msgTitulo);
       // System.out.println("JanelaDialogo.preparaJanela: "+size+", "+JanelaDialogo.largura+" : "+msgTitulo);
       if (size<JanelaDialogo.largura)
	  size = JanelaDialogo.largura;
       this.setSize(size+15, JanelaDialogo.altura);
       }
    setFont(fonteJanela);

    botaoOk = new Button("OK"); botaoOk.setSize(10,20);
    botaoOk.setFont(fonteJanela);
    botaoOk.setBackground(JanelaDialogo.corFundoBordos);
    botaoOk.addActionListener( new ActionListener() {
         public void actionPerformed(ActionEvent e) {
	    _botaoOk(); }
         });

    // botaoOk.addFocusListener(this); - need focus process?
    //v2 botaoOk.addKeyListener(this); - version 2
    botaoOk.addKeyListener(new KeyAdapter() {
       // public void keyTyped (KeyEvent evt) { System.out.println("preparaJanela.keyPressed: "+ evt); }
       public synchronized void keyPressed (KeyEvent keyevent) {
         int keyCode = keyevent.getKeyCode(); //System.out.println("keyPressed: "+ paramString());
         if (keyCode == KeyEvent.VK_ENTER || keyCode == KeyEvent.VK_ESCAPE) {	       
            _botaoOk();
           }
        } });

    painel_fundo.add(botaoOk);

    this.setLayout(new BorderLayout());

    this.add(painel_topo, BorderLayout.NORTH);
    this.add(painel_princ, BorderLayout.CENTER);
    this.add(painel_fundo, BorderLayout.SOUTH);

    }

  // Override java.awt.Component.setVisible(boolean)
  public void setVisible (boolean visible) {
    //try { String str00="";System.out.println(str00.charAt(3)); } catch (Exception e) { e.printStackTrace(); }count++;
    super.setVisible(visible);
    if (visible) {
       botaoOk.setEnabled(true);
       botaoOk.requestFocus();
       }
    }

  private void _botaoOk () {
    atualizar();
    dispose();
    }


  // Só pega o conteúdo da TextArea se usuário cliar em "OK"
  synchronized public void atualizar () { // para pegar o texto digitado no TextArea e definir o "Texto"
    // botão "OK" dispara isso
    }

  //v2 public synchronized void keyPressed (KeyEvent keyevent) {
  //v2   int keyCode = keyevent.getKeyCode();    //System.out.println("keyPressed: "+ paramString());
  //v2   if ( keyCode == KeyEvent.VK_ENTER) { //  KeyEvent.VK_ESCAPE
  //v2      if (keyevent.getSource() instanceof Button) {
  //v2         Button botao_ativo = (Button) keyevent.getSource();
  //v2         if ( botao_ativo == botaoOk ) { _botaoOk(); }
  //v2         }       }    }
  //v2 public void keyTyped (KeyEvent keyevent) {
  //v2   int keyCode = keyevent.getKeyCode();
  //v2   char ch = keyevent.getKeyChar();
  //v2   int i = ch; // System.out.println("keyTyped: keycode=" + keyCode + "KeyChar=" + ch + "=" + i);
  //v2   if ( ch == 27) { // e ESCAPE
  //v2      //System.out.println("keyTyped: é ESC? " + keyCode);
  //v2      dispose();
  //v2      }    }
  //v2 public final void keyReleased(KeyEvent keyevent) {
  //v2   // System.out.println("KeyReleased: "+keyevent.getKeyCode());
  //v2   }


  // public void focusGained (FocusEvent focusevent) { System.out.println("focusGained: ganhou foco"); }
  // public void focusLost (FocusEvent focusevent) {    }

  /**
    * Top Panel
    **/
  protected class TopPanel extends Panel {

    private Label Ltexto1;
    private ImagePanel imgLogoPanel; // Top Panel with an image (logo iCG)

    protected TopPanel (Image img) {
      Panel panelCentral  = new Panel(),
            panelHor1 = new Panel(), // has 'ImagePanel(img)'
            panelHor2 = new Panel();
      //Test: Frame frame = new Frame("teste"); frame.add(new ImagePanel(img)); frame.setSize(150,130); frame.setVisible(true);
      this.imgLogoPanel = new ImagePanel(img, corFundoBordos, 25, 21); // = Toolkit.getDefaultToolkit().getImage("igeom/img/logo-igeom12.gif");

      String Msg1 = new String("iCG"),
             Msg2 = new String("http://www.matematica.br/icg");

      this.Ltexto1 = new Label( Msg1 );
      TextField Ttexto2 = new TextField( Msg2,31 );  //
      Ttexto2.setEditable(false);
      Ttexto2.setFont(fonteEndereco);

      setFont(fonteJanela);

      // Restante das cores na classe "JanelaEspere"
      setForeground(JanelaDialogo.corTextoJanela); // cor das letras "Geom. Int. na Internet"...

      setLayout(new BorderLayout());
      panelCentral.setLayout(new BorderLayout());
      panelHor1.setLayout(new BorderLayout()); // has 'ImagePanel(img)'
      panelHor2.setLayout(new BorderLayout());

      panelHor1.add(this.Ltexto1, BorderLayout.WEST); // iCG
      panelHor1.add(this.imgLogoPanel, BorderLayout.EAST); // new ImagePanel(img)
      panelHor2.add(Ttexto2,    BorderLayout.CENTER);
      panelHor2.add(msgTitulo,  BorderLayout.SOUTH);

      panelCentral.add(panelHor1, BorderLayout.NORTH);
      panelCentral.add(panelHor2, BorderLayout.SOUTH);

      add(panelCentral);
      }

    } // public class TopPanel extends Panel 


  } // public class JanelaDialogo extends Dialog implements KeyListener
