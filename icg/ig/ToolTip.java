/**
 * 
 * iMath - http://www.matematica.br
 * 
 * <p>Title: iCG - Interactive Computer (Computador Gaveteiro Interativo)</p>
 * 
 * <p>Description: PopUp with information about buttons or choice
 * 
 * <p>Copyright: Copyleft (c) 2003</p>
 * <p>Company: LInE - http://www.ime.usp.br/line</p>
 * 
 * @author Alexandre Eiseman Kundrát, Leônidas de Oliveira Brandão
 * 
 * @version 21/05/2012 (versão incial: iComb)
 *
 * @see icg.emulador.iCGEmulator; icg.compilador.CompilerPanel; icg.ig.Botao (launches ToolTip)
 * 
 **/

package icg.ig;

import java.awt.*;
import java.applet.*;
import java.awt.event.*;
import java.util.StringTokenizer;

import icg.msg.Bundle;

public class ToolTip extends Canvas {

  private static final Color BACK_COLOR = new Color(200,220,220), LINE_COLOR = new Color(10,10,10);
  private static final Font FONT = new Font("Helvetica",Font.PLAIN,11);
  private static final int DY = 5; // margem para bordo inferior
  private static final int DX = 5; // margem para bordo esquerdo
  private static final int LINE_HEIGHT = 15; // distancia entre linhas de PopUp (que tenha mais de 1 linha)

  protected String tip;
  protected Component owner;
  
  private Container mainContainer;
  private LayoutManager mainLayout;
  
  private boolean shown;
  
  private final int VERTICAL_OFFSET = 30;
  private int TIP_HEIGHT;
  private int TIP_WIDTH;
  private int line_height;
  private int NUMBER_OF_LINES;
  private String[] tipArray;
  
  public ToolTip (String tip, Component owner) {
    this.tip = tip;
    this.owner = owner;
    owner.addMouseListener(new MAdapter());
    setBackground(BACK_COLOR);
    setFont(FONT);
    }


  public void paint (Graphics g) {
    g.setColor(LINE_COLOR);
    g.drawRect(0,0, getSize().width-1, getSize().height-1);
    g.setColor(Color.BLACK);
    int pos = 15;
    for (int i = 0; i < tipArray.length; i++) { // se texto tem mais de uma linha
      g.drawString(tipArray[i], 3, pos );
      pos += LINE_HEIGHT;
      }
    }

  // Add the message to this tool tip - using internacionalization by Bundle.msg(...)
  private void addToolTip () {
    String tipMessage = Bundle.msg(tip); // get the actual msg
    // System.out.println(" ---> addToolTip: "+tip+" -> "+ tipMessage);
		   
    mainContainer.setLayout(null);

    FontMetrics fm = getFontMetrics(this.getFont()); //owner.getFont());
    StringTokenizer st = new StringTokenizer(tipMessage,"\n");
    NUMBER_OF_LINES = st.countTokens();
    tipArray = new String[NUMBER_OF_LINES];
    int i=0;
    TIP_WIDTH = 0;
    while (st.hasMoreTokens()) { // se tiver mais de uma linha => fique com a maior
      tipArray[i] = st.nextToken();
      int size = fm.stringWidth(tipArray[i]);
      //System.out.println(" ---> addToolTip: "+tipArray[i]+" size="+size+" TIP_WIDTH="+TIP_WIDTH);
      if (size>TIP_WIDTH)
        TIP_WIDTH = size;
      i++;
      }

    line_height = fm.getHeight();
    TIP_HEIGHT = (line_height)*(NUMBER_OF_LINES);

    setSize(TIP_WIDTH + DX , TIP_HEIGHT + DY); // 

    int mainC_locationX = mainContainer.getLocationOnScreen().x, 
        mainC_locationY = mainContainer.getLocationOnScreen().y, 
        mainC_sizeWidth = mainContainer.getSize().width;

    setLocation((owner.getLocationOnScreen().x - mainC_locationX) , 
                (owner.getLocationOnScreen().y - mainC_locationY + VERTICAL_OFFSET));

    // correction, whole tool tip must be visible 
    if (mainC_sizeWidth < getLocation().x + getSize().width) {
      setLocation(mainC_sizeWidth - getSize().width, getLocation().y);
      }

    setVisible(false);
    mainContainer.add(this, 0);
    setVisible(true);
    mainContainer.validate();
    repaint();
    shown = true;
    }

  // Trick: when a popup is lanched by choice and is used the keyboard, the popup could remain in the screen
  //        force to dismiss the last popup tool tip (in case it lost focus)
  private static ToolTip staticToolTip = null;

  public void removeLastToolTip () {
    ToolTip tt = staticToolTip;
    if (tt==null) { // first time gets here
      staticToolTip = this;
      return;
      }
    //T String str0 = tt.tip!=null ? (tt.tip.length()>10 ? tt.tip.substring(0,10):tt.tip) : "<>";
    //T System.out.println("ToolTip.removeLastToolTip(): "+str0+" - "+shown);
    if (tt.shown) try {
      tt.mainContainer.remove(0);
      tt.mainContainer.setLayout(mainLayout);
      tt.mainContainer.validate();
    } catch(Exception e) { System.err.println("ToolTip.removeLastToolTip"); } // e.printStackTrace();
    else tt.setVisible(false);
    tt.shown = false;
    staticToolTip = this; // change the last popup
    }

  private void removeToolTip () {
    if (shown) {
      mainContainer.remove(0);
      mainContainer.setLayout(mainLayout);
      mainContainer.validate();
      }
    shown = false;
    }

  private void findMainContainer () {
    Container parent = owner.getParent();
    while (true) {
     if ((parent instanceof Applet) || (parent instanceof Frame)) {
       mainContainer = parent;
       break;
       }
     else {
       parent = parent.getParent();
       }
     }
    mainLayout = mainContainer.getLayout();
    }

  class MAdapter extends MouseAdapter {
    public void mouseEntered (MouseEvent me) {
      findMainContainer();
      removeLastToolTip(); // force to remove the last popup
      addToolTip();
      }
    public void mouseExited (MouseEvent me) {
      removeToolTip();
      }
    public void mousePressed (MouseEvent me) {
      removeToolTip();
      }
    }

  }
