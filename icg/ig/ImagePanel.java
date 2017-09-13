/**
 * 
 * iMath - http://www.matematica.br
 * 
 * <p>Title: iCG - Interactive Computer (Computador Gaveteiro Interativo)</p>
 * 
 * <p>Description: A Panel with an Image</p>
 *
 * <p>Company: LInE - http://www.ime.usp.br/line</p>
 * 
 * @author Leônidas de Oliveira Brandão
 * @version: 08/06/2012 (first version to iCG)
 * @description 
 *
 * @see A Panel with an Image
 * 
 **/

package icg.ig;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Panel;

public class ImagePanel extends Panel {

  // IMPORTANTE: dimensions must be the same of the image logo
  private int widthImgLogo = 25, heightImgLogo = 21;

  Image imgLogoICG; // image logo
  private Color bgColor; // background color - if presented

  // When called in Flowlayout the dimension could be automatic generated
  public ImagePanel (Image img) {
    this.imgLogoICG = img;
    }

  // When called with BorderLayout it is necessary to set the dimension in ImagePanel
  // To be called for Panel with full width (requires width in order to produce a correct image)
  ImagePanel (Image img, Color bgColor, int widthImgLogo, int heightImgLogo) {
    this.imgLogoICG = img;
    this.bgColor = bgColor;
    this.widthImgLogo = widthImgLogo;
    this.heightImgLogo = heightImgLogo;
    setLayout(null); setBounds(0,0, widthImgLogo, heightImgLogo);
    }

  public void paint (Graphics gr) {
    // int wLogoPanel = imgLogoICG.getWidth(this), hLogoPanel = imgLogoICG.getHeight(this); // test
    this.setSize(widthImgLogo, heightImgLogo);
    if (bgColor!=null)
       gr.setColor(bgColor); // security... (since the image has transparent background)
    gr.fillRect(0, 0, widthImgLogo, heightImgLogo); // fill the background
    gr.drawImage(imgLogoICG, 0, 0, this); // put the image on top
    }

  }
