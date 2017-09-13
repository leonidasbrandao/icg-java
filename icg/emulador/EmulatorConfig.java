/**
 * 
 * iMath - http://www.matematica.br
 * 
 * <p>Title: iCG - Interactive Computer (Computador Gaveteiro Interativo)</p>
 * 
 * <p>Description: emulator (run the "binary" code of iCG)</p>
 *                 configurantions constants to color and fonts
 * 
 * <p>Copyright: Copyleft (c) 2003</p>
 * <p>Company: LInE - http://www.ime.usp.br/line</p>
 * 
 * @author Leônidas de Oliveira Brandão
 * @version 1.0: 2012-05-21 (identation and comments); 2005-09-01 (changed 'texto_Entrada_Label_Background_Color'); 2004-08-05 (first version from an undergradeate course MAC323 - in 2003)
 * 
 * @see icg.emulador.iCGEmulator; icg.emulador.Emulador_Panel; icg.emulador.Epi; icg.emulador.Memoria
 * 
 **/

package icg.emulador;

import java.awt.*;

public class EmulatorConfig {

  static Color execuçao_textArea_Background_Color = Color.blue;
  static Color execuçao_textArea_Foreground_Color = Color.black;
  static Font execuçao_textArea_Font = new java.awt.Font("Serif", 0, 12);

  static Color ok_Button_Background_Color = Color.blue;
  static Color ok_Button_Foreground_Color = Color.black;
  static Font ok_Button_Font = new java.awt.Font("Serif", 0, 12);

  static Color emular_Button_Background_Color = Color.blue;
  static Color emular_Button_Foreground_Color = Color.black;
  static Font emular_Button_Font = new java.awt.Font("Serif", 0, 12);

  static Color emularPasso_Button_Background_Color = Color.blue;
  static Color emularPasso_Button_Foreground_Color = Color.black;
  static Font emularPasso_Button_Font = new java.awt.Font("Serif", 0, 12);

  static Color entrada_TextField_Background_Color = Color.blue;
  static Color entrada_TextField_Foreground_Color = Color.black;
  static Font entrada_TextField_Font = new java.awt.Font("Serif", 0, 12);

  static Color text_Saida_Label_Background_Color = Color.blue;
  static Color text_Saida_Label_Foreground_Color = Color.black;
  static Font text_Saida_Label_Font = new java.awt.Font("Serif", 0, 12);

  static Color instrucao_Label_Background_Color = Color.blue;
  static Color instrucao_Label_Foreground_Color = Color.black;
  static Font instrucao_Label_Font = new java.awt.Font("Serif", 0, 12);

  static Color Instrucao_Executada_Label_Background_Color = Color.blue;
  static Color Instrucao_Executada_Label_Foreground_Color = Color.black;
  static Font Instrucao_Executada_Label_Font = new java.awt.Font("Serif", 0, 12);

  static Color texto_Entrada_Label_Background_Color = Color.white; // Color.blue;
  static Color texto_Entrada_Label_Foreground_Color = Color.black;
  static Font texto_Entrada_Label_Font = new java.awt.Font("Serif", 0, 12);

  static Color texto_Acumulador_Label_Background_Color = Color.blue;
  static Color texto_Acumulador_Label_Foreground_Color = Color.black;
  static Font texto_Acumulador_Label_Font = new java.awt.Font("Serif", 0, 12);


  static Color borda_lado_Panel_Background_Color = Color.blue;
  static Color borda_lado_Panel_Foreground_Color = Color.black;

  static Color borda_cima_Panel_Background_Color = Color.blue;
  static Color borda_cima_Panel_Foreground_Color = Color.black;

  static Color Center_West_Background_Color = Color.blue;
  static Font Center_West_Font = new java.awt.Font("Serif", 0, 12);
  static Color South_Background_Color = new Color(145, 166, 255);

  }
