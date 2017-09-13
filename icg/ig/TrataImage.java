/* 
 * TrataImage
 * 
 * @author Leônidas de Oliveira Brandão
 * @version 30/03/2006 (versão incial: iGeom 0: 23/08/2003)
 * Para gerar tela de abertura do iGeom
 *
 */

package icg.ig;

import java.awt.Component;
import java.awt.Frame;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import java.util.Hashtable;

//import IGeomApplet;
import icg.iCG;

public class TrataImage { //

 // public static final int ALTURA = iCG.aBt, LARGURA = iCG.lBt; // altura e largura dos botões
 public static final int ALTURA = iCG.altBt, LARGURA = iCG.largBt; // altura e largura dos botões

 private static boolean eh_applet; //

 private static String msgErroPC   = "[TI] 1"; //"[TrataImage!pegaClasse]";
 private static String msgErroPCpI = "[TI] 2"; //"[TrataImage!pegaImagem]";

 public static Frame frameImg; //
 public static MediaTracker mediaTracker; //
 public static Hashtable hash_img = new Hashtable(); //
 public static Class trataClasse; // ze

 // O diretório em que está o arquivo "LocalizacaoImagens.java" não pode mudar!!
 //private static String ComponentImage = "igeom.gifs.LocalizacaoImagens"; // truque para usar classe para pegar diretório
 private static String ComponentImage = (new icg.imgs.LocalizacaoImagens()).nome; // truque para usar classe para pegar diretório
 
 // devem ser estáticos, uma só vez
 static {
   new Hashtable();
   Toolkit.getDefaultToolkit();
   }


 // pega a classe de nome "str_classe"
 public static Class pegaClasse (String str_classe) { //
   try {
       Class classe = Class.forName(str_classe);
       //System.out.println("pegaClasse: "+str_classe+" -> "+classe);
       return classe;
       //return Class.forName(str_classe);
   } catch (ClassNotFoundException classnotfoundexception) {
       System.out.println(msgErroPC+": classe não encontrada "+str_classe);
       throw new NoClassDefFoundError(classnotfoundexception.getMessage());
   }
 }

 //-
 public static void eh_applet (boolean eh) {  eh_applet =  eh; }
 // System.out.println("[TI!eh_applet] eh_applet="+eh_applet); }

 // Entra primeiro aqui
 // Chamado em: icg.ig.JanelaDialogo
 public static Image pegaImagem (boolean ehBotao, String str_imagem) { //
   Image image = null;
   boolean erro = false;

   //System.out.print("TI!pegaImagem "+ str_imagem);
   try {

   if (eh_applet) {
       try {

         InputStream inputstream
             = (trataClasse != null ? trataClasse : (trataClasse = pegaClasse(ComponentImage))).getResourceAsStream(str_imagem);
         //System.err.println(" inputstream=" + inputstream);
         byte[] is = new byte[inputstream.available()];
         inputstream.read(is);
         image = Toolkit.getDefaultToolkit().createImage(is);
         inputstream.close();
         // System.out.println("[TI!pegaImagem] str_imagem="+str_imagem+" inputstream="+inputstream+" image="+image);
       } catch (Exception e) { // IOException ioexception
           System.err.println(msgErroPC+": não foi possível ler a imagem " + str_imagem);
           erro = true;
           //ioexception.printStackTrace();
           }
      }
   if (!eh_applet || erro) {
      image = (Toolkit.getDefaultToolkit().getImage(
                (trataClasse != null ? trataClasse : (trataClasse = pegaClasse(ComponentImage))).getResource(str_imagem)));
      }

   if (ehBotao)
      return image.getScaledInstance(ALTURA, LARGURA, 0);
   //System.out.println("[TI!pegaImagem] str_imagem="+str_imagem+" erro="+erro+" image="+image);

   return image;
   } catch (java.lang.NullPointerException npe) {
       //System.out.println(msgErroPCpI+": erro para ler imagem "+str_imagem+" - "+image); //npe);

       //System.out.println(":: erro para ler imagem "+str_imagem+" - "+image + " - " + trataImagemComoStream(str_imagem)); //npe);
       // esta chamada de "trataImagemComoStream(str_imagem)" também gera erro...

       //npe.printStackTrace();
       return image;
       //System.out.println(msgErroPCpI+": erro para ler imagen "+str_imagem+" - tenta via getResourceAsStream");//+npe);
       //return trataImagemComoStream(str_imagem);
       }

 }


 // Também não consegui fazer funcionar no Netscape...
 private static Image trataImagemComoStream (iCG icg, String str_arquivo) {
   Component component = icg;
   InputStream is;
   ByteArrayOutputStream baos;
   Image img1 = null;
   //String codebase_str;
   //codebase_str = ((java.applet.Applet)component).getCodeBase().toString();
   //System.out.print  ("[TrataImagem!trataImagemComoStream] codebase_str="+codebase_str+" "); 
   str_arquivo = "icg/imgs/"+str_arquivo;
   System.out.print  ("[TrataImagem!trataImagemComoStream] str_arquivo="+str_arquivo+" "); 

   trataClasse = pegaClasse(ComponentImage);

   try {
     //is = component.getClass().getResourceAsStream(str_arquivo); //codebase_str); //
     is = trataClasse.getClass().getResourceAsStream(str_arquivo); //codebase_str); //
     System.out.print  ("trataClasse="+trataClasse+" is="+is+" ");

     baos = new ByteArrayOutputStream();

     int c;
     while ((c = is.read()) >= 0)
           baos.write(c);
     img1 = component.getToolkit().createImage(baos.toByteArray());

     System.out.println(" OK1 "+img1);

   } catch (Exception e) { // (IOException e) {
     System.out.println("[TrataImagem!trataImagemComoStream] erro "+e);
     //e.printStackTrace();
     }

   System.out.println(" OK2 "+img1);

   return img1;
   }

 // Chamado em: icg.iCG
 public static Image trataImagem (boolean ehBotao, String str_imagem) { //
   Image image;
   //System.out.println("TI: trataImagem "+ str_imagem);
   try {
     image = pegaImagem(ehBotao,str_imagem); // monta o hash com nomes das imagens
     return image;
   } catch (java.lang.NullPointerException npe) {
     System.err.println(msgErroPC+": imagem vazia, "+ str_imagem);
     return null;
   }
 }
 
 /*
 public static Image devolveImagem (String str_imagem) { //
   return trataImagem(nomeImagem(str_imagem));
 }
 
 public static String nomeImagem (String str_imagem) { // 
   String nomeImgStr
       = (str_imagem.substring(0, str_imagem.lastIndexOf("/") + 1) + "img-" + str_imagem.substring(str_imagem.lastIndexOf("/") + 1));
   return nomeImgStr;
 }
 */
   
}
