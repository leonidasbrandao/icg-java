/**
 * 
 * iMath - http://www.matematica.br
 * 
 * <p>Title: iCG - Interactive Computer (Computador Gaveteiro Interativo)</p>
 * 
 * <p>Description: this a didatic software to explain to the newbie what is a Computer, how to program it</p>
 * 
 * @author Leônidas de Oliveira Brandão
 * @version icg.configucoes.Configuracao.Versao
 * @description Take care of connection with LMS server (like Moodle with iAssignment)
 * 
 */

package icg.util;

import java.awt.Frame;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.util.StringTokenizer;

import icg.msg.Bundle;
import icg.iCG;

public class EnviaWeb {

  // Criar uma janela para gerar exercicio
  public static boolean janelaCriada;
  private boolean enviarMensagem = false; // avisar o gravador leitor que esta enviando mensangem e nao codificar o gabarito

  private static String respostaStr = null;

  private static String contraExemplo; // armazena o contra exemplo do exercicio ou apenas uma solucao

  // Para selecionar botões para exercícios
  Frame frameBotoes;    

  // Elimina \n e \f de string trocando-os por ','
  // o POST só pegaria a primeira linha se houvesse quebra (resolucao1.php)
  private static String eliminaQuebras (String str) {
    String strNova = "";
    char c;
    for (int i=0; i<str.length(); i++) {
        c = str.charAt(i);
        if (c=='\n' || c=='\f') strNova += " "; //", ";
        else strNova += c;
        }
    return strNova;
    }


  // Envia conteúdo String de script pela Web
  // Chamado em: Tratamouse.trataMenuSecundario(Botao botao, int acao): "if (acao == CaixaFerramenta.ENVIAR_SCRIPT)"
  public static void enviarResultadoExercicio () {
    // Versão "antiga" para enviar "script" está em "AreaDeDesenho.enviarScript(java.net.URL codebase)"
    if (respostaStr == null) {
       System.out.println("[E] tentando enviar resultado, mas \"script\" está vazio");
       return;
       }
    //String str_scr = BarraDeDesenho.area_de_desenho.script.monta_string_gravar();
    contraExemplo = "";
    enviarResultadoExercicio(0, respostaStr, ""); //str_scr); 
    }


  // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
  // Envia resultado do exercício para o servido utilizando método de POST

  // No PHP: "resolucao1.php"
  //  $r=mysql_query(
  //    "INSERT INTO $TABELA_ALUNO_EXERCICIO (id_aluno,id_exercicio, arquivo,     n_tentativas, valor,  completo) VALUES
  //                                         ($usuario,$cod_exer,    '$resposta', $tentativas,  $valor, $completo)");

   /*
    *   Exercícios:
    *
    *   paramGabarito    : entrada do gabarito para mostrar no HTML   | [16/08/2005]
    *   paramAluno       : uma resposta do aluno para mostrar no HTML | ainda a implementar (neste caso 'pegue 'paramGaberito' p/ apresentar')
    *   paramInfo        : parâmetro: 'string' com info. genéricas, p.e., p/ guardar URL (e devolver via envWebInfo)
    * 
    *   envWebInfo       : dados auxiliares, recebidos via 'paramInfo'
    *   envWebValor      : resultado da avaliação, 
    *   envWebArquivo    : arquivo com resposta
    *   envWebGeoResp    : contra-exemplo GEO
    *   envWebGeoOuvidor : dados sobre operação iGeom "seleção/ação"
   */

  // From: icg.iCG.void acaoEnviar()
  public static void enviarResultadoExercicio (int resultado, String strResp, String strGabarito) {
    URL               url;
    URLConnection     urlConn;
    DataOutputStream  printout;
    //DataInputStream   input;
    BufferedReader input;
    String content = "";
    String gabEntradas = "",
           gabSaidas   = "";

    respostaStr = strResp;

    //if (iCG.listaGabEntradas()!=null) {
    //   gabEntradas = iCG.listaGabEntradas().listaElementos();
    //   System.out.println("[E] Gabarito entradas:\n "+gabEntradas);
    //   }
    //if (iCG.listaGabSaidas()!=null) {
    //   gabSaidas = iCG.listaGabSaidas().listaElementos();
    //   System.out.println("[E] Gabarito saídas  :\n "+gabSaidas);
    //   }

    // "str_param_nome_scr" é definido em "IGeomApplet.init()", com "str_param_nome_scr = getParameter("SCRIPTPOST")"
    // ou seja, vem do "param" do HTML
    System.out.println("[E] enviar resultado exercício: "+iCG.str_param_end_post);
    System.out.println("[E] enviar resultado: envWebInfo="+iCG.str_param_info);
    // System.out.println("[E] strResp="+strResp+" montaGabarito(eliminaQuebras(strResp))="+
    //                         iCG.montaGabarito(eliminaQuebras(strResp)));
    // strResp: tem a resposta do aluno!

    System.out.println("[E] resultado="+resultado+"\nstrGabarito="+strGabarito+" "); // é aqui que vai a resposta do aluno!
    // System.out.println("[E] dados enviados: ||"+resultado+"|| ||"+strResp+"|| ||"+strGabarito+"||");

    try {

      if (iCG.str_param_end_post=="") iCG.str_param_end_post = "http://milanesa.ime.usp.br/mac118/recebe-teste.php";

      url = new URL(iCG.str_param_end_post);
      System.out.println("[E] endereço é: "+iCG.str_param_end_post);
      urlConn = url.openConnection();
      //urlConn.connect();
      urlConn.setDoInput(true);
      urlConn.setDoOutput(true);
      urlConn.setUseCaches(false);
      urlConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
      printout = new DataOutputStream(urlConn.getOutputStream ());

      // No PHP:
      // valor   = $envWebValor    : valor
      // arquivo = '$envWebArquivo': resposta do aluno

      content = 
              "envWebValor="    + URLEncoder.encode (""+resultado) + // resultado=1 => exercício correto
              "&envWebArquivo= "    + URLEncoder.encode(strResp);  // resposta do aluno!

      // paramInfo        : parâmetro: 'string' com info. genéricas, p.e., p/ guardar URL (e devolver via envWebInfo)
      // envWebInfo       : dados auxiliares, recebidos via 'paramInfo'
      if (iCG.str_param_info!="" && iCG.str_param_info!=null)
         content += "&envWebInfo="    + URLEncoder.encode(iCG.str_param_info); //

      // "iCG.montaGabarito(eliminaQuebras(strResp))" precisa que as "ListaLigada"'s,
      // "iCG.listaGabEntradas()" e "iCG.listaGabSaidas()", estejam prontas

      System.out.println("[E] dados enviados: ||"+resultado+"|| ||"+strResp+"|| ||"+strGabarito+"||");
      System.out.println("[E] dados enviados:"+content);

      printout.writeBytes(content);
      //System.out.println("[E] enviado: "+content);
      printout.flush();
      printout.close();
                  
      // Carrega o endereco de uma pagina retornada pelo servidor
      //  input = new DataInputStream (urlConn.getInputStream ());

      //_ input =new BufferedReader(new InputStreamReader(urlConn.getInputStream ()));
      //_ String str;
      //_ //str = input.readUTF(); //input.readLine(); está depreciado no 1.1
      //_ str = input.readLine();
      //_ System.out.println("[Exercicio] endereco de rotorno: "+str);
      //_ input.close ();
      //_ // Carrega um HTML, invoca um CGI ou um PHP
      //_ URL novaURL = new URL(str);
      //_ // Mostra saída do URL usando o navegador (ao clicar no "voltar", retorna ao applet)
      //_ icg.iCG.appletICG.getAppletContext().showDocument(novaURL);
      //_ System.out.println("[Exercicio] após : \"icg.iCG.appletICG.getAppletContext().showDocument(novaURL);\""+icg.iCG.appletICG);

      // Tenta pegar uma URL devolvida pelo servidor que processou a msg acima enviada
      //
      String str = "";
      try {
        input = new BufferedReader(new InputStreamReader(urlConn.getInputStream())); // endereço de onde tenta pegar URL
        str = input.readLine(); // tenta pegar a página devolvida pelo PHP chamado em "IGeomApplet.str_param_end_post" (via "echo" ou "print")
        System.out.println("[Exerc] endereco de rotorno: "+str+".");
        input.close();
        // Carrega um HTML, invoca um CGI ou um PHP
        URL novaURL = new URL(str);
        // se NÃO for página para professor enviar exercício, então pegue a página de resposta indicada num "echo" ao final da página "IGeomApplet.str_param_end_post"
        //-0- if (! (IGeomApplet.str_param_envWebProf!=null && IGeomApplet.str_param_envWebProf.equals("EnvWebPROFESSOR")) ) {
        //-0-    // Carrega um HTML, invoca um CGI ou um PHP
        //-0-    URL novaURL = new URL(str);
        //-0-    // Mostra saída do URL usando o navegador (ao clicar no voltar: retorna ao applet)
        //-0-    areaDesenho.igeomApplet.getAppletContext().showDocument(novaURL);
        //-0-    }
        // Mostra saída do URL usando o navegador (ao clicar no "voltar", retorna ao applet)
        icg.iCG.appletICG.getAppletContext().showDocument(novaURL);
        System.out.println("[Exercicio] após : \"icg.iCG.appletICG.getAppletContext().showDocument(novaURL);\""+icg.iCG.appletICG);
        return;
      } catch (Exception e) {
          String strUrlConn = urlConn!=null ? "urlConn.getURL()="+urlConn.getURL()+" - urlConn.toString()="+urlConn.toString() : "urlConn=null";
          System.out.println("[E] Problemas ao conetar com o servidor: " + e);
          System.out.println("    ICG.str_param_end_post="+iCG.str_param_end_post);
          System.out.println("    "+strUrlConn);
          System.out.println("    str = <"+str+">");
          System.out.println("[Exercicio] Erro! após : \"icg.iCG.appletICG.getAppletContext().showDocument(novaURL);\""+icg.iCG.appletICG);
          e.printStackTrace();
          return;
          }

    } catch (Exception e) {
        System.out.println("[E] Problemas ao conetar ou receber dados: " + e);
        System.out.println("    iCG.str_param_end_post="+iCG.str_param_end_post);
        //System.out.println("    respostaStr="+respostaStr);
        System.out.println("    respostaStr="+eliminaQuebras(strResp));  // 'strResp': solução do aluno
        System.out.println("    &gabarito=" + URLEncoder.encode(iCG.montaGabarito(eliminaQuebras(strResp))));
        System.out.println("    gabarito=" + iCG.montaGabarito(eliminaQuebras(strResp)));
        }
     
  }


}
