/*
 * Truque para pegar local das imagens a partir de "TrataImage.pegaImagem(str_imagem)"
 *  image = (Toolkit.getDefaultToolkit().getImage(
 *            (trataClasse != null ? trataClasse : (trataClasse = pegaClasse(ComponentImage))).getResource(str_imagem)));
 */

package icg.imgs;

import java.lang.Class;

public class LocalizacaoImagens {
  public String nome = this.getClass().getName();
  }

