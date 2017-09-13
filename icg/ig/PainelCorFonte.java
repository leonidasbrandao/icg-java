/**
 * 
 * iMath - http://www.matematica.br
 * 
 * <p>Title: iCG - Interactive Computer (Computador Gaveteiro Interativo)</p>
 * 
 * <p>Description: Panel com possibilidades para mudança de fonte (tamanho e tipo) e formato dela (negrito, itálico)
 *                 Chamada em: icg.iCG ('Frame frameEdicaoFonte', 'trataEdicao(int tipo)')
 * 
 * <p>Copyright: Copyleft (c) 2003</p>
 * <p>Company: LInE - http://www.ime.usp.br/line</p>
 * 
 * @author Leônidas de Oliveira Brandão
 * 
 * @version: 2012-05-22 (comments, identation); 2006-03-28; 2004-09-25 
 *
 * @see icg.emulador.iCGEmulator; icg.compilador.CompilerPanel; icg.iCG
 * 
 **/


package icg.ig; 

import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Checkbox;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Choice;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextField;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;

import icg.compilador.CompilerPanel;
import icg.emulador.EmulatorMainPanel;


public class PainelCorFonte extends Panel {

  private static int altura = 70; // essa altura é importante, se for maior que 50, em minha tela, fica sobre o TextFieldo c/ txt

  private Object emuladorOUcompilador = null; //- static 
  private static int tipoItem; // tipo da fonte, 0 ou 1 (resultado depende do valor de "emuladorOUcompilador"

  private PainelCorFonte painelCorFonte; //- static
  private static boolean        eh_tamanho = false; // Truque: o AWT reduz tamanho do TextField ao mudar tam.

  private Choice escolheTamanho = new Choice(); // tamanho da fonte 8, 9, 10, 11, 12, 14 //- static

  private Choice escolheTipo   = new Choice();    // normal, itálico ou negrito //- static
  private static final String
      tipoNormal    = "Normal", //-
      tipoNegrito   = "Negrito",
      tipoItalico   = "Italico",
      tipoArial     = "Arial",
      tipoDialog    = "Dialog",
      tipoHelvetica = "Helvetica",
      tipoSans      = "Sans-serif";

  private Checkbox checkNegrito  = new Checkbox(tipoNegrito), //- static
                   checkItalico  = new Checkbox(tipoItalico);

  private void defTamanhosTipos () {
      escolheTamanho.add( "8");
      escolheTamanho.add( "9");
      escolheTamanho.add("10");
      escolheTamanho.add("12");
      escolheTamanho.add("14");

      escolheTipo.add(tipoArial);
      escolheTipo.add(tipoDialog);
      escolheTipo.add(tipoHelvetica);
      escolheTipo.add(tipoSans);
      }

  Dimension dimTextModelToFont; // truque para contornar um erro do AWT, que difere o tamanho "textModelToFont.getPreferredSize()"
                                // de "textModelToFont.getSize()", dando preferência ao primeiro !!!


   private void atualizaFonte (String strFonte, int tipoFonte, int tamFonte) { //- static
     Font fonteAtual = null; 
     fonte = new Font(strFonte, tipoFonte, tamFonte); // fonte.getSize());
     textModelToFont.setFont(fonte);
     painelCorFonte.remove(textModelToFont); // para não ter problema de reduzir o núm. de caracteres da caixa de texto
     painelCorFonte.add(textModelToFont,BorderLayout.SOUTH); // adiciona novamente
     fonteAtual = fonte;
     try {
       if (emuladorOUcompilador instanceof EmulatorMainPanel) //EmuladorApplet)
          ((EmulatorMainPanel)emuladorOUcompilador).setFonte(tipoItem, fonte); // pega o fonte
       else 
          ((CompilerPanel)emuladorOUcompilador).setFonte(fonte); // pega o fonte
     } catch (java.lang.Exception e) {
          System.out.println("[PainelCorFonte] Erro "+e+"\n"+
                             "                 emuladorOUcompilador="+emuladorOUcompilador);
          }
     }

  // Listener para mudança de fonte
  public void muda () { //-  static
        String nomeFonte = escolheTipo.getSelectedItem();
        int tipoFonte    = (checkNegrito.getState() ? Font.BOLD : 0) +
                           (checkItalico.getState() ? Font.ITALIC : 0);
        int tamFonte     = Integer.parseInt((String)escolheTamanho.getSelectedItem());
        // System.out.println("mudaFonte: "+nomeFonte);
        atualizaFonte(nomeFonte, tipoFonte, tamFonte);
        }

  // Listener para mudança de fonte
  ItemListener mudaFonte = new ItemListener() {
      public void itemStateChanged (ItemEvent evt) {
        eh_tamanho = false; // Truque: o AWT reduz tamanho do TextField ao mudar tam.
        muda(); if (1==1) return;
        String nomeFonte = (String)evt.getItem();
        int tipoFonte = (checkNegrito.getState() ? Font.BOLD : 0) +
                        (checkItalico.getState() ? Font.ITALIC : 0);
        // System.out.println("mudaFonte: "+nomeFonte);
        atualizaFonte(fonte.getName(), tipoFonte, fonte.getSize());
        }};
  // Listener para mudança de tipo fonte: PLAIN, BOLD, ITALIC
  ItemListener mudaTipoFonte = new ItemListener() {
      public void itemStateChanged (ItemEvent evt) {
        eh_tamanho = false; // Truque: o AWT reduz tamanho do TextField ao mudar tam.
        muda(); if (1==1) return;
        String tipoFonte = (String)evt.getItem();
        int tipo = 0;
        if (tipoFonte.equals(tipoNormal)) tipo=Font.PLAIN; // "PLAIN"
        else if (tipoFonte.equals(tipoNegrito)) tipo=Font.BOLD; // "BOLD"
        else if (tipoFonte.equals(tipoItalico)) tipo=Font.ITALIC; // "ITALIC"
        atualizaFonte(fonte.getName(), tipo, fonte.getSize());
        }};

  // Listener para mudança de tamanho de fonte
  ItemListener mudaTamFonte = new ItemListener() {
      public void itemStateChanged (ItemEvent evt) { 
        eh_tamanho = true; // Truque: o AWT reduz tamanho do TextField ao mudar tam.
        muda(); if (1==1) return;
        int tamFonte = Integer.parseInt((String)evt.getItem());
        atualizaFonte(fonte.getName(), fonte.getStyle(), tamFonte);
        }};


  private static TextField textModelToFont       = new TextField(" "); //

  private static Color cor; // default
  private static Font fonte = new Font (tipoHelvetica, Font.BOLD, 10);

  private Label labelTamanho = new Label("Tamanho "); //- static (caso contrário => não entra o rótulo em 'reload')

  private Label labelTipo = new Label("Tipo "); //- static (caso contrário => não entra o rótulo em 'reload')

  private static Color fundo_menu_secundario = new Color(60, 90, 150);
  private static Color fundo = Color.white;

  public static String getTextModelToFont () { return textModelToFont.getText(); } // From: 
  public static Font getFonte () { return fonte; }

  public int tamanho () { //- static
    return escolheTamanho.getSelectedIndex();
    }
  public int tipo () { //- static
    return escolheTipo.getSelectedIndex();
    }

  // Pega configurações da fonte atual e coloca nessas opções
  private void defineOpcoes (Font fonteAtual) { //- static
    // Pega "name"
    if (fonteAtual==null) { 
       escolheTipo.select(0); 
       checkNegrito.setState(false); checkItalico.setState(false); // style=plain
       escolheTamanho.select(3);
       return; 
       }

    String fonteNome = fonteAtual.getName();

    if (fonteNome.equals(tipoArial))
       escolheTipo.select(0); 
    else
    if (fonteNome.equals(tipoDialog))
       escolheTipo.select(1); 
    else
    if (fonteNome.equals(tipoHelvetica))
       escolheTipo.select(2); 
    else
    if (fonteNome.equals(tipoSans))
       escolheTipo.select(3); 
    else
       escolheTipo.select(0); 

    // Pega "style" 
    switch (fonteAtual.getStyle()) {
      case 0: checkNegrito.setState(false); checkItalico.setState(false); // style=plain
              break;
      case 1: checkNegrito.setState( true); checkItalico.setState(false); // style=bold
              break;
      case 2: checkNegrito.setState(false); checkItalico.setState( true); // style=italic
              break;
      case 3: checkNegrito.setState( true); checkItalico.setState( true); // style=bolditalic
              break;
      default: checkNegrito.setState(false); checkItalico.setState(false); // style=plain
      }

    // Pega "size" 
    switch (fonteAtual.getSize()) {
      case  8: escolheTamanho.select(0); break;
      case  9: escolheTamanho.select(1); break;
      case 10: escolheTamanho.select(2); break;
      case 12: escolheTamanho.select(3); break;
      case 14: escolheTamanho.select(4); break;
      default: escolheTamanho.select(3); break;
      }
    }


  public PainelCorFonte (Object obj, int tipoItem, String texto) {
    Font fonteAtual; 
    this.emuladorOUcompilador = obj;
    this.tipoItem             = tipoItem; // tipo da fonte

    if (obj instanceof EmulatorMainPanel)                        // tipoItem=0 => "memoria[][]": memória
       fonteAtual = ((EmulatorMainPanel)obj).getFonte(tipoItem); // tipoItem=1 => "txtExecucao": código executável
    else fonteAtual = ((CompilerPanel)obj).getFonte();           // tipoItem=0 => "": código fonte/objeto e mensagens
                                                              // tipoItem=1 => "": idem

    setBackground(fundo);
    setForeground(fundo_menu_secundario);

    setFont(new Font ("Helvetica", Font.BOLD, 10)); //Font.PLAIN, 10)); //

    defTamanhosTipos(); //-
    defineOpcoes(fonteAtual); //

    painelCorFonte = this;

    escolheTamanho.addItemListener(mudaTamFonte);
    escolheTipo.addItemListener(mudaTipoFonte);

    checkNegrito.addItemListener(mudaTipoFonte);
    checkItalico.addItemListener(mudaTipoFonte);

    GridBagLayout gbl = new GridBagLayout();
    setLayout(new BorderLayout());

    GridBagConstraints gbc = new GridBagConstraints();

    PainelCentral pAux = new PainelCentral(); // truque para conseguir que os "botões" não estraguem as margens
    pAux.setLayout(gbl);      // do "drawRect" no "paint" abaixo

    gbc.anchor = GridBagConstraints.WEST; // ajusta à esquerda
    add(pAux, labelTamanho,   gbc, 1, 0, 1, 1);
    add(pAux, escolheTamanho, gbc, 1, 1, 1, 1);
    add(pAux, labelTipo,      gbc, 2, 0, 1, 1);
    add(pAux, escolheTipo,    gbc, 2, 1, 1, 1);
    add(pAux, checkNegrito,   gbc, 3, 0, 1, 1);
    add(pAux, checkItalico,   gbc, 3, 1, 1, 1);

    gbc.weightx = 1;                         // importante para conseguir textModelToFont ocupar toda a área de visualização
    gbc.fill = GridBagConstraints.HORIZONTAL; // importante para conseguir textModelToFont ocupar toda a área de visualização
    add(textModelToFont,BorderLayout.SOUTH);

    gbc.anchor = GridBagConstraints.CENTER; // ajusta central (para não estragar o "drawRect" gerador dos bordos)
    gbc.fill = GridBagConstraints.NONE;     // idem
    add(pAux,BorderLayout.CENTER);

    textModelToFont.setEnabled(true); // the user can enter any texto in order to test the encoding font... 
    textModelToFont.setEditable(false); // - duvida: vale a pena? nao parece que digita para entradas...?
    textModelToFont.setText(texto);
    textModelToFont.setBackground( fundo_menu_secundario ); //
    textModelToFont.setForeground( Color.white );           //

    } // public PainelCorFonte(Object obj, int tipoItem, String texto)


  // posiciona "c" no painel
  public static void add (Panel cPai, Component c, GridBagConstraints gbc, int x, int y, int larg, int alt) {
    gbc.gridx      = x;    // posição x, horizontal
    gbc.gridy      = y;    // posição y, vertical
    gbc.gridwidth  = larg; // quantas posições c ocupara na horizontal
    gbc.gridheight = alt;  // quantas posições c ocupara na vertical
    cPai.add(c,gbc);
    }

  class PainelCentral extends Panel {
    public void paint (Graphics gr) {
      Dimension dim = this.getSize();
      setSize(dim.width, altura);
      gr.setColor(Color.black);
      gr.drawRect(1,0,dim.width-3,dim.height-1);
      }
    }

  }
