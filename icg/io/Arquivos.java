/**
 * 
 * iMath - http://www.matematica.br
 * 
 * <p>Title: iCG - Interactive Computer (Computador Gaveteiro Interativo)</p>
 * 
 * <p>Description: static methods to manage files</p>
 * 
 * <p>Copyleft (c) 2003</p>
 * <p>LInE - http://www.ime.usp.br/line</p>
 * 
 * @author Leônidas de Oliveira Brandão
 * 
 * @version 1.0: 02/04/2006
 *
 * @see icg/iCG.java
 * 
 **/

package icg.io;

import java.applet.Applet;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;
import java.util.StringTokenizer;

import icg.configuracoes.Configuracao;
import icg.msg.Bundle;

public class Arquivos {

  private static final String
    ICGADDRESS = "# iCG: " + Configuracao.strEndereco, // # iCG: http://www.matematica.br/icg
    VERSION    = "[ .: version: " + Configuracao.versao + " :. ]";

  // sufix for the auxiliary file to the HTML file - see 'EmulatorBaseClass.actionPerforStoreFileSession(...)'
  public static final String SUFIX_AUX_HTML = "_icg2html.icg";

  // Verify if the "fileName" is a valid file (readable)
  public static boolean isFile (String fileName) {
      File arq;
      try {
        arq = new File(fileName);
        return arq.isFile();
      } catch (Exception e) {
        return false;
        }
      }


  // Get the file name decomposition, an array with: initial name and the extension 'icg' or 'html'
  // Return: String[2] (eventually with nulls)
  public static String [] getExtension (String fileName) {
    String [] vetFileNames = { null, null };
    if (fileName==null || fileName.length()==0)
       return vetFileNames;
    int lastDot = fileName.lastIndexOf('.'); // find the last '.'
    int sizeOf = fileName.length();
    if (lastDot<0) { // there are none extension
       vetFileNames[0] = fileName; // initial name
       return vetFileNames;
       }
    if (sizeOf < lastDot + 3) { // there are no valid extension
       vetFileNames[0] = fileName.substring(0,sizeOf); // initial name
       return vetFileNames;
       }
    vetFileNames[0] = fileName.substring(0,lastDot); // initial name
     //sizeOf<lastDot?sizeOf:lastDot); // initial name
    String strExt = fileName.substring(lastDot+1,sizeOf); // extension
    if (strExt.equalsIgnoreCase("icg"))
       vetFileNames[1] = "icg";
    if (strExt.equalsIgnoreCase("html"))
       vetFileNames[1] = "html";
    //System.err.println("Arquivos.getExtension: " + fileName + " (" + vetFileNames[0] + "," + vetFileNames[1] + ")");
    return vetFileNames;
    } // String [] getExtension(String fileName)


  // Read a file 'diretorio/nome'
  // Return: the file content, if correct; or return null, if not a valid file
  // From: icg.emulador.EmulatorBaseClass.abrir_actionPerformed(...)
  public static String readFromFileDir (String nome, String diretorio) {
    // "Aguarde leitura do arquivo...");
    // try { String str00="";System.out.println(str00.charAt(3)); } catch (Exception e) { e.printStackTrace(); }

    FileReader arquivo = null;
    String dir, strArq="";
    // usados para acertar "path" e nome de arquivo, separadamente (assim, nas abas, dá p/ colocar apenas o 'nome final')
    // String vetNomes[]=igeom.util.Sistema.limpaNomeArquivo(nome); // devolve nome do arquivo (em [0]) e diretório (em [1])
    // nome = igeom.util.Sistema.limpaNomeArquivo(nome);

    String nome_arquivo = nome;
    try {
      arquivo = new FileReader(nome);
      // arquivo = new FileReader(diretorio+igeom.util.Sistema.sep+nome);
      BufferedReader buffer = new BufferedReader(arquivo);
      String linha;
      while ((linha = buffer.readLine())!=null) strArq += linha+"\n";
    } catch (IOException e) {
      String [] param = new String[1];
      String msg;
      int tam;
      param[0] = nome_arquivo;
      msg = Bundle.msgComVar("msgArqInexistente","OBJ",param);
      System.out.println("[Arquivos] erro na leitura do arquivo " + nome + " " + e.toString ()+": "+msg);
      System.out.println("arquivo = " + arquivo);
      //e.printStackTrace();
      return null;
      }
    return strArq;
    } // readFromFileDir(String nome, String diretorio)

  //_ Return a String with the iCG content to the iAssignment
  //_ @param appletILM the initial "applet" (the iCG "applet")
  //_ @param ilmURL the URL of the file (in the same server with this iCG.jar)
  //_ @return <file content> or <null if ilmURL is invalid>
  public static String getMAFile (Applet appletILM, String ilmURL) { // javax.swing.JApplet appletILM
    return readFromURL(appletILM, ilmURL);
    }

  // Read file under an URL
  public static String readFromURL (java.applet.Applet applet, String strURL) {
   // Permite receber URL
   java.io.InputStream inputStream = null;
   java.net.URL endURL = null;
   // String strer = "";
   java.lang.StringBuffer stringbuffer = null;
   try { //
     endURL = new java.net.URL(strURL); // se for URL
     // System.out.println("[Sistema.readFromURL: 1 "+strURL+" -> "+endURL+" -> "+endURL);
   } catch (java.net.MalformedURLException e) {
     try { // se falhou, tente completar com endereço base do applet e completar com nome de arquivo
       // applet.getDocumentBase().toString() = "http://localhost/igeom/exemplo.html"
       System.err.println("[GravadorLeitor.readFromURL: failed while reading '"+strURL+"' - results in URL="+endURL);
       endURL = new java.net.URL(applet.getCodeBase().toString()+strURL); // se for URL
       // endURL = new java.net.URL(applet.getCodeBase().toString()+File.separator+strURL+strURL); // se for URL
       // System.out.println("[Sistema.readFromURL: 2 "+strURL+" -> "+endURL);
     } catch (java.net.MalformedURLException ue) {
       System.err.println("[GravadorLeitor.readFromURL: failed while reading '"+strURL+"' - it also results in URL="+endURL);
       // ue.printStackTrace();
       return "";
       } 
     } 
   try {
     inputStream = endURL.openStream();
     java.io.InputStreamReader inputstreamreader = new java.io.InputStreamReader(inputStream);
     java.io.StringWriter stringwriter = new java.io.StringWriter();
     int i = 8192;
     char[] cs = new char[i];
     try {
       for (;;) {
           int i_11_ = inputstreamreader.read(cs, 0, i);
           if (i_11_ == -1)
              break;
           stringwriter.write(cs, 0, i_11_);
           }
       stringwriter.close();
       inputStream.close();
     } catch (java.io.IOException ioexception) {
       System.err.println("Erro: leitura URL: "+strURL+": "+ioexception); //throw Trace.error(34, ioexception.getMessage());
       // ioexception.printStackTrace();
       }
     return stringwriter.toString();
   } catch (java.io.IOException ioe) {
     System.out.println("Erro: leitura URL: "+strURL+" -> "+endURL+": "+ioe.toString());
     // ioe.printStackTrace();
     }
   return "";
   } // static String readFromURL(java.applet.Applet applet, String strURL)

  public static String readFileFromArg (String [] args) {
   String diretorio = "", nome_arq = null, str_temp, strContent = null;
   String strDebug = "";
   int ind = -1;
   for (int i=0; i<args.length; i++) {
      str_temp = args[i];
      if (Arquivos.isFile(str_temp)) {
         if (icg.iCG.debugTrace)
            strDebug = "\n " + Bundle.msg("msgMenuAbrir")+": "+str_temp; // "Abrir arquivo..." + " ? arquivo v?lido");
         nome_arq = str_temp;
         ind = i;
         break;
         }
      else {
         if (icg.iCG.debugTrace)
            strDebug = "\n " + i + ": "+str_temp+" is NOT a valid file";
         }
      }
   // System.out.println("[Arquivos.readFileFromArg] ind=" + ind);

   if (ind>-1) { // I found a valid file name
      //_ File nomeArq = new File(nome_arq);
      //_ if (!nomeArq.isAbsolute()) { nomeArq = new File(".",nome_arq); diretorio = nomeArq.getAbsolutePath();
      //_    System.out.println("[Arquivos.readFileFromArg] 1 nomeArq=" + nomeArq + ", diretorio=" + diretorio);
      //_    }
      //_ if (!nomeArq.isFile()) {
      //_    nomeArq = new File(nome_arq + ".icg");
      //_    if (!nomeArq.isAbsolute()) { diretorio = nomeArq.getAbsolutePath(); nomeArq = new File(".",nome_arq); }
      //_    }

      strContent = readFromFileDir(nome_arq, diretorio);
      if (icg.iCG.debugTrace)
         System.out.println("[Arquivos.readFileFromArg] file='" + nome_arq + "', directory='" + diretorio +"'" + strDebug);

      } // if (ind>-1)

    return strContent;

    } // static String readFileFromArg(String [] args)

   
  //________________________________________________________________________________________________________
  public static String readFromFileTag (String fileName) {
    StringTokenizer str;
    FileReader inStream = null;
    StringTokenizer st;
    StringBuffer dados = null;
    StringBuffer sb = new StringBuffer("");
    try {
      inStream = new FileReader(fileName);
      int ch = 0;
      while ( (ch = inStream.read()) != -1) { sb.append( (char) ch); }
      str = new StringTokenizer(sb.toString(), " \"=", true);
      String s = str.nextToken();
      while (str.hasMoreTokens() && !s.equals("value")) { s = str.nextToken(); }
      while (str.hasMoreTokens() && !s.equals("\"")) { s = str.nextToken(); }
      dados = new StringBuffer(100);
      int j = 0;
      while (str.hasMoreTokens() && !s.equals("\"")) {
        dados.append(s);
        s = str.nextToken();
        }
      inStream.close();
    } catch (IOException ioe) {
      System.err.println("Arquivos.readFromFile: Error in reading file " + fileName + ": " + ioe.toString());
      return null;
      }
    return dados.toString();
    } // String readFromFileTag(String fileName)


  // Store in iCG file, if *.icg or as HTML, if *.html
  // From: 'icg/emulador/EmulatorBaseClass.java:grava(String,String)'
  // return:  1, OK; 0, impossible to write;
  //         -1, there exist a file with this name; -2, there exists the auxiliary file
  //         -3, empty name; -4, invalid extensions
  public static int storeICGorHTML (String strContent, String fileName) {
    String [] vetFileNames = getExtension(fileName); // cant return just null (but could be {null,null})
    String strFirst = null, strExt = null;
    strFirst = vetFileNames[0]; // name previous to the '.'
    strExt = vetFileNames[1]; // extension

    if (strFirst==null) {
       System.err.println("Arquivos.storeICGorHTML: deve ser providenciado um nome para registrar esta sessao");
       return -3;
       }
    if (strExt==null) {
       System.err.println("Arquivos.storeICGorHTML: " + fileName + ", extensao invalida (" + strExt + ")");
       return -4;
       }

    if (strExt.equalsIgnoreCase("html"))
       return export2HTML(strContent, strFirst, strExt);
    return storeICG(strContent, strFirst + "." + strExt); // ensure lower-case letters (minuscule) for the extension
    }
   

  // Store the iCG file
  // return: 1, OK; 0, impossible to write; -1, there exist a file with this name
  public static int storeICG (String strContent, String fileName) {
    try {

      //TBImproved: for while if there exist a file with this name, return...
      if (isFile(fileName)) {
         System.out.println("Sorry, I am not writing over an existent file '"+fileName+"'");
         return -1;
         }

      FileWriter outStream = new FileWriter(fileName);
      outStream.write(strContent);
      outStream.close();
      return 1;
      }
    catch (Exception e) {
      System.err.println("Arquivos.storeICG: Error: the iCG file couldn't be registered as " + fileName + "!: " + e.toString());
      return 0;
      }
    }

  // Export to HTML page with content in an auxiliary file 'fileName_icg2html.icg'
  // return: 1, OK; 0, impossible to write; -1, there exist a file with this name; -2, there existe the auxiliary file
  public static int export2HTML (String strContent, String fileName, String fileNameExtension) {
    String fileNameHTML = fileName + "." + fileNameExtension;
    String fileNameICG = fileName + SUFIX_AUX_HTML; // fileName + "_icg2html.icg"
    int resp = 1; // if writen the 2 files successfully
    try {

      // Verify if there exist a file under this name
      // Write auxiliary file 'fileName_icg_2_html.icg'

      //TBImproved: for while if there exist a file with this name, return...
      if (isFile(fileName)) {
         System.out.println("Sorry, I am not writing over an existent file '"+fileName+"'");
         return -1;
         }

      if (storeICG(strContent, fileNameICG)!=1) {
         System.err.println("Arquivos.export2HTML: Error: the auxiliary file " + fileNameICG + " could not be stored! The HTML will not work..");
         resp = -2; // error to write auxiliary file
         }

      FileWriter outStream = new FileWriter(fileNameHTML);
      outStream.write("<html>\n");
      outStream.write("<head>\n");
      outStream.write(" <title>iCG : http://www.matematica.br/icg</title>\n");
      outStream.write("</head>\n");
      outStream.write("<body>\n");
      outStream.write(" <h2>iCG : interactive Computer Emulator</h2>\n");
      outStream.write(" <applet codebase=\".\" code=\"icg.iCG.class\" archive=\"iCG.jar\"\n");
      outStream.write("  width =\"600\" height=\"400\" hspace=\"0\" vspace=\"0\" align=\"top\" name=\"iCG iLM\"\n");
      outStream.write("  alt =\"You need to enable Java to see this iCG activity\">\n");

      // Here comes the URL iCG file
      outStream.write(" <param name=\"iLM_PARAM_Assignment\" value=\"" + fileNameICG + "\"/>\n");

      outStream.write("</applet>\n");
      outStream.write("\n<br/><br/><font size=1>Automatic generated by: iCG<br/>"+
                      "An Interactive Learning Module (iLM) of LInE-USP<br/>"+
                      "<a href=\"http://www.matematica.br/icg\">http://www.matematica.br/icg</a></font>\n");
      outStream.write("\n</html>");
      outStream.close();
      return resp;
      }
    catch (Exception e) {
      System.err.println("Arquivos.export2HTML: Error: the HTML file couldn't be registered as " + fileNameHTML + "!: " + e.toString());
      return 0;
      }
    }
  //________________________________________________________________________________________________________

  //-----
  // iCG XML file format

  /*
  These are prepared by the teacher ("exercise"):
  <Statement>Algoritmo que some valores ate digitar zero</statement>  | qualquer texto, filtrando '<statement>' e  '</statement>'
  <Type>exercise</Type>                                               | 'exercise' ou 'example'
  <Hint>Note que seu programa deve funcionar com {-1,-2,-4,0}</Hint>  | qualquer texto, filtrando '<Hint>' e  '</Hint>'
                                                                      | pode haver um numero arbitrario de dicas
  <Template>...7b20453a202d31203220...b768b7f1efcc3602</Template>     | qualquer string hexadecimal
  <Evaluation>2012/02/23 20:30:23 - 0.5 </Evaluation>                 | para cada clique em avaliacao 'aaaa/mm/dd hh:mm:ss - 0|0.5|1'
  <Comment>foi dificil entender...</Comment>                          | qualquer comentario para o aluno efetuar

  These are provided by the student ("exercise answer"):
  <Object>[the student main answer - in machine language]</Object>    | the student answer that is used in automatic evaluation
  <Code>[the student code, if presented]</Code>                       | if student coded in "high level" language
  */
  private static String strContent =
    "# iCG: http://www.matematica.br/icg\n" +
    "[ .: version: 0.8.2 :. ]\n" +
    "[ 03/07/2011 23:23:37leo; ]\n" +
    "[ /home/leo/projetos/icomb; /home/leo; /usr/local/ibm/jre;  ]\n" +
    "<Statement>Algoritmo que some valores ate digitar zero</Statement>\n" +
    "<Type>exercise</Type>\n" +
    "<Hint>Note que seu programa deve funcionar com {-1,-2,-4,0}</Hint>\n" +
    "<Template>...7b20453a202d31203220...b768b7f1efcc3602</Template>\n" +
    "<Evaluation>2012/02/23 20:30:23 - 0.5 </Evaluation>\n" +
    "<Comment>foi dificil entender...</Comment>\n" +
    "<Object>[the student main answer - in machine language]</Object>\n" +
    "<Code>[the student code, if presented]</Code>";

  private static final String
    tagStatement = "Statement", tagType = "Type", tagHint = "Hint",                  // provided by the teacher (exercise)
    tagTemplate = "Template", tagEvaluation = "Evaluation", tagComment = "Comment",  //
    tagObject = "Object", tagCode = "Code"; // provided by the student (answer)

  private static final int
    lengthStatement = tagStatement.length(), lengthType = tagType.length(), lengthHint = tagHint.length(),
    lengthTemplate = tagTemplate.length(), lengthEvaluation = tagEvaluation.length(), lengthComment = tagComment.length(),
    lengthObject = tagObject.length(), lengthCode = tagCode.length();

  // Get data (like "Mon Jun 18 23:06:56 BRT 2012")
  public static String dataICG () {
    return new java.util.Date().toString(); //.toGMTString(); // java.util.Date.toGMTString();
    }

  // iCG header format
  public static String getHeader (java.applet.Applet applet) {
    String strAux, strHeader = ICGADDRESS + "\n" + VERSION; // # iCG: http://www.matematica.br/icg

    if (!icg.iCG.ehApplet()) {
       Properties Prop = System.getProperties(); //
       //java/util/Date: toGMTString() // d mon yyyy hh:mm:ss GMT
       String strDate = dataICG();
       strAux = strHeader + "\n" + 
                "[ Date=" + strDate + " - User= " + Prop.getProperty("user.name") + " - Language= " + Prop.getProperty("user.language") + " ]\n" +
                "[ Java=" +  Prop.getProperty("java.version") + "; " + Prop.getProperty("java.vendor") + " ]\n" +
                "[ Sysem=" +  Prop.getProperty("os.name") + "; " + Prop.getProperty("os.version") + "; " + Prop.getProperty("os.arch") + " ]\n" +
                "[ Path=" +  Prop.getProperty("user.dir") + "; " + Prop.getProperty("user.home") + " ]\n";
       }
    else
    if (applet!=null) {
       String strDate = new java.util.Date().toGMTString(); // java.util.Date.toGMTString();
       strAux = ICGADDRESS + "\n" + VERSION + "\n" +
                "[ Date=" + strDate + " - Language= " + applet.getParameter("lang") + " ]\n" +
                "[ WWW= " + applet.getDocumentBase() + " ]\n";
       // strAux = applet.getAppletInfo();
       }
    else
       strAux = "<>";
    return strAux;
    }

  public static void listICGproperties (Properties iCGproperties) {
    String strStatement=null, strType=null, strHint=null, strTemplate=null, strEvaluation=null, strComment=null,
           strObject=null, strCode=null;
    strStatement = iCGproperties.getProperty(tagStatement);
    strType = iCGproperties.getProperty(tagType);
    strHint = iCGproperties.getProperty(tagHint);
    strTemplate = iCGproperties.getProperty(tagTemplate);
    strEvaluation = iCGproperties.getProperty(tagEvaluation); // evaluations in iCG session
    strComment = iCGproperties.getProperty(tagComment);
    strObject = iCGproperties.getProperty(tagObject);
    strCode = iCGproperties.getProperty(tagCode);
    System.out.println("listProperties:\n Statement=" + strStatement + "\n Type=" + strType + "\n Hint=" + strHint + "\n Template=" +
                       strTemplate + "\n Evaluation=" + strEvaluation + "\n Comment=" + strComment + "\n Object=" +
                       strObject + "\n Code=" + strCode);
    }

  public static String getXML (java.applet.Applet icgApplet, Properties iCGproperties) {
    String strICGfile = getHeader(icgApplet),
           strStatement = iCGproperties.getProperty(tagStatement),
           strType = iCGproperties.getProperty(tagType),
           strHint = iCGproperties.getProperty(tagHint),
           strTemplate = iCGproperties.getProperty(tagTemplate),
           strEvaluation = iCGproperties.getProperty(tagEvaluation),
           strComment = iCGproperties.getProperty(tagComment),
           strObject = iCGproperties.getProperty(tagObject),
           strCode = iCGproperties.getProperty(tagCode);

    if (strStatement!=null && !strStatement.equals("null")) // .trim().length()>0
       strICGfile += "<Statement>" + strStatement + "</Statement>\n";
    if (strType!=null && !strType.equals("null")) // .trim().length()>0
       strICGfile += "<Type>" + strType + "</Type>\n";
    if (strHint!=null && !strHint.equals("null")) // .trim().length()>0
       strICGfile += "<Hint>" + strHint + "</Hint>\n";
    if (strTemplate!=null && !strTemplate.equals("null")) // .trim().length()>0
       strICGfile += "<Template>" + strTemplate + "</Template>\n";
    if (strEvaluation!=null && !strEvaluation.equals("null")) // .trim().length()>0
       strICGfile += "<Evaluation>" + strEvaluation + "</Evaluation>\n";
    if (strComment!=null && !strComment.equals("null")) // .trim().length()>0
       strICGfile += "<Comment>" + strComment + "</Comment>\n";
    if (strObject!=null && !strObject.equals("null")) // .trim().length()>0
       strICGfile += "<Object>" + strObject + "</Object>\n";
    if (strCode!=null && !strCode.equals("null")) // .trim().length()>0
       strICGfile += "<Code>" + strCode + "</Code>";

    return strICGfile.trim(); //
    } // String getXML(iCG icgApplet, Properties iCGproperties)

  // Get the content inside a tag and put it in the Properties: <tag>Any string without the tag mark</tag>
  // Tag exists: return its intial position on 'stringContent'
  // Otherwise : return -1
  public static int getPropertyValue (Properties iCGproperties, String stringContent, String strTagName) {
    int intTag1, intTag2, lengthTag;
    String strTagValue = null;
    intTag1 = stringContent.indexOf("<"+strTagName+">"); // search for "<tag>"
    if (intTag1>0) {
       lengthTag = strTagName.length();
       intTag2 = stringContent.indexOf("</"+strTagName+">", intTag1+1); // search for "</tag>"
       //T System.out.println("getProperties(): intTag1="+intTag1+", intTag2="+intTag2+", lengthTag="+lengthTag);
       strTagValue = stringContent.substring(intTag1+lengthTag+2, intTag2);
       iCGproperties.put(strTagName, strTagValue); // key, value
       }
    return intTag1; // -1 => this tag doesn't exist in the 'stringContent'
    }

  public static Properties getProperties (String strICGContent) {
    Properties iCGproperties = new Properties();
    //T_ String strStatement=null, strType=null, strHint=null, strTemplate=null, strEvaluation=null, strComment=null,
    //T_        strObject=null, strCode=null;
    int len1 = ICGADDRESS.length(); // iCG mark length
    strICGContent = strICGContent.trim(); // clean string

    if (strICGContent==null || strICGContent.length()==0)
       return null;

    if (!strICGContent.substring(0,len1).equals(ICGADDRESS)) {
       System.err.println("icg.io.Arquivos: Erro: arquivo nao foi gerado pelo iCG?\n" + strICGContent);
       return null;
       }

    int indStatement1=-1, indType1=-1, indHint1=-1, indTemplate1=-1, indEvaluation1=-1, indComment1=-1, indObject1=-1, indCode1=-1;//
    //T_ int indStatement2=-1, indType2=-1, indHint2=-1, indTemplate2=-1, indEvaluation2=-1, indComment2=-1, indObject2=-1, indCode2=-1;//

    //_ Teacher: search for tag "<Statement>*</Statement>"
    indStatement1 = getPropertyValue(iCGproperties, strICGContent, tagStatement);
    //T_ indStatement1 = strICGContent.indexOf("<"+tagStatement+">"); // search for "<Statement>"
    //T_ if (indStatement1>0) {
    //T_    indStatement2 = strICGContent.indexOf("</"+tagStatement+">", indStatement1+1); // search for "</Statement>"
    //T_    //T System.out.println("getProperties(): indStatement1="+indStatement1+", indStatement2="+indStatement2+", lengthStatement="+lengthStatement);
    //T_    strStatement = strICGContent.substring(indStatement1+lengthStatement+2, indStatement2);
    //T_    iCGproperties.put(tagStatement, strStatement); // key, value
    //T_    }

    //_ Teacher: search for tag "<Type>*</Type>"
    indType1 = getPropertyValue(iCGproperties, strICGContent, tagType);

    //_ Teacher: search for tag "<Hint>*</Hint>"
    indHint1 = getPropertyValue(iCGproperties, strICGContent, tagHint);

    //_ Teacher: search for tag "<Template>*</Template>"
    indTemplate1 = getPropertyValue(iCGproperties, strICGContent, tagTemplate);

    //_ Teacher: search for tag "<Evaluation>*</Evaluation>"
    indEvaluation1 = getPropertyValue(iCGproperties, strICGContent, tagEvaluation);

    //_ Teacher: search for tag "<Comment>*</Comment>"
    indComment1 = getPropertyValue(iCGproperties, strICGContent, tagComment);

    //_ Student: search for tag "<Object>*</Object>"
    indObject1 = getPropertyValue(iCGproperties, strICGContent, tagObject);

    //_ Student: search for tag "<Code>*</Code>"
    indCode1 = getPropertyValue(iCGproperties, strICGContent, tagCode);

    return iCGproperties;
    } // Properties getProperties(String strICGContent)

  //-----

  public static void main (String[] args) {
    System.out.println("icg.io.Arquivos: testando...");
    System.out.println("icg.io.Arquivos.getHeader(Applet): " + getHeader(null));
    // getProperties("# iCG: http://www.matematica.br/icg\n<Statement>Algoritmo que some valores ate digitar zero</Statement>");
    // Properties iCGproperties = getProperties(strContent);
    // listICGproperties(iCGproperties);
    // testProperties(); // a single test to verify if Hashtable 'put' for the same 'key' effectively change the value
    // getXML(iCG icgApplet, Properties iCGproperties); // 
    }

  // A single test to verify if Hashtable 'put' for the same 'key' effectively change the value
  private static void testProperties () {
    Properties prop = new Properties();
    prop.put("key", "value1");
    System.out.println("#Properties="+prop.size());
    prop.list(System.out);
    prop.put("key", "value2"); // use the same key: Hashtable will put this value over the last one
    System.out.println("\n#Properties="+prop.size());
    prop.list(System.out); // listProperties(prop);
    }

  }
