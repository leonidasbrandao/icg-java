/*
 * 
 * iMath - http://www.matematica.br
 * 
 * @description Auxiliary tools to define language using I18 scheme. For while there are 2 languagens ('en' for 'en_US' and 'BR' for 'pt_BR')
 *              "defLocale()" é utilizado para definir a lingua e país a ser utilizada, p.e., "lingua<-BR" => use "Messages_BR.properties"
 * 
 * @author Leônidas de Oliveira Brandão
 * @see ./icg/iCG.java, ./Messages*.properties
 * 
 * Restrições de uso
 * O código fonte deste programa pode ser utilizado dentro do projeto iMática, mas
 * não deve ser distribuido. Qualquer dúvida sobre uso entre em contato com o coordenador
 * do projeto iMatica: http://www.matematica.br/
 *
 */

package icg.msg;

import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.net.URL;
import java.io.FileInputStream;
import java.io.BufferedReader;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;


import icg.iCG;

public class Bundle {

  public static ResourceBundle bundle;
  public static int cont=0; // apenas para testes

  public static String endereco = "Messages"; //"igeom.msg.Messages"; // "Messages";//
  public static String lingua="", pais="";

  static Properties Prop = new Properties();
  static String ConfigName;

  // Para armazenar todos as "chaves" ("keys") e seus valores
  // usado no caso de precisar definir "manualmente" o sistema de mensagens

  static Locale currentLocale; // variável que guardará o "local", lingua e país


  // Decompõe: 'lang=pt_BR' em String("pt","BR")
  public static boolean decompoeConfig (String str) {
    // System.out.println("[Bundle.decompoeConfig] inicio: str="+str);
    if (str==null)
       return false;
    StringTokenizer tokens = new StringTokenizer(str,"=");
    String item;

    // System.out.println("[Bundle.decompoeConfig] #tokens="+tokens.countTokens());
    if (tokens.hasMoreTokens()) {
       item = tokens.nextToken();
       // System.out.println("[Bundle.decompoeConfig] item="+item);
       if (item==null) return false;
       if (item.equals("lang") && tokens.hasMoreTokens()) {
          // get "pt_BR"
          item = tokens.nextToken();
          if (item.length()>2) {
             lingua = item.substring(0,2); // like "pt_BR" (or "pt")
             if (item.length()>4)
                pais = item.substring(3,5); // get "BR" in "pt_BR"
             return true;
             }
          else {
             // é da forma: 'pt'
             lingua = item.substring(0,2); //
             // System.out.println("[Bundle.decompoeConfig] lingua="+lingua+" [pais="+pais+"]");
             return true;
             }
          }
       else
       if (item.equals("bg") && tokens.hasMoreTokens()) {
          // pegou 'pt_BR'
          item = tokens.nextToken();
          if (item!=null && item.equals("contrast1")) // Bundle.msg("contraste")
             ;// definido contraste - ainda NAO implementado!!!
          }
       else { // problema: veio 'lang='
          return false; // new String[2];
          }
       }
       return false; // new String[2];
    } // boolean decompoeConfig(String str)


  // iCG aplicativo: define lingua, tem prioridade sobre outros métodos
  //                 "java -jar iCG.jar" OR "java icg.iCG lang=es"
  // Arquivo: 'icg.lang' define a lingua (conteúdo: "lang=pt", "lang=en" ou "lang=es")
  // Parameters: lang=pt; lang=en; lang=es - 'lingua', 'pais' ou 'lang' (nesta ordem) -> param name='lang' value="pt" ou "en" ou "es" (default: "pt")
  // Called by: icg/iCG.java: void main(String [] args)
  public static void setConfig (String [] args) {
    lingua = pais = "";
    int i = -1;
    if (args!=null && args.length>0) {
       String item;
       for (i=0; i<args.length; i++) {
           item = args[i].toLowerCase().trim(); // tokens.nextToken().toLowerCase();
           try {
             if (decompoeConfig(item)) {
                // System.out.println(" <- OK");
                }
           } catch (Exception e) { System.err.println("Erro: leitura de parametros para configuracao: "+e);
             e.printStackTrace(); }
           }
       }
    // System.err.println("Bundle.java: setConfig(String []): lingua="+lingua+" [pais="+pais+"]");
    if (lingua==null || lingua=="") // evita sobrescrever definicao de 'igeom.cfg'
       lingua = "pt"; // default
    if (lingua.equals("pt") && pais=="")
       pais = "BR";
    try {
      Locale loc = new Locale(lingua,pais);
      // System.err.println("Bundle.java: setConfig(String []): loc="+loc);
      Locale.setDefault(loc);
      currentLocale = loc;
    } catch (Exception e2) { e2.printStackTrace(); }
    System.out.println("Bundle.sefConfig: lingua="+lingua+", pais="+pais);
    } // static void setConfig(String [] args)


  // Define "Locale" - atualmente está forçando para usar "Messages_BR.properties"
  public static Locale defLocale () {
    //try { String str="";System.out.println(str.charAt(3)); } catch (Exception e) { e.printStackTrace(); }
    System.out.println("Bundle.defLocale: 1: lingua="+lingua+", pais="+pais);
    Locale currentLocale =  Locale.getDefault(); //
    if (!iCG.ehApplet()) {
       if (lingua==null) lingua = currentLocale.getLanguage();
       if (pais  ==null) pais   = currentLocale.getCountry();
       System.out.println("Bundle.defLocale: 2: lingua="+lingua+", pais="+pais+", locale="+currentLocale);
       }

    String strLang = lingua;
    if (strLang!="") {
      if (pais!="")
         strLang += "_"+pais;
      }
    if (strLang!="") {
       if (strLang.equalsIgnoreCase("pt_BR"))
          strLang = "_pt_BR";
       else
          strLang = "_en_US";
       }

    // Define the language
    try {
         currentLocale = new Locale(strLang); // lingua+pais);
         bundle = ResourceBundle.getBundle("Messages" + strLang); //"Messages_en", null); //"Messages" + strLang);
    } catch (java.lang.Exception e) { // SecurityException ?
         System.out.println("Error: erro de segurança... terei que usar o Messages"+lingua+pais+".properties");
         e.printStackTrace();
         }

    return currentLocale;
    } // static Locale defLocale()


  public static void lingua_pais () {
    if (iCG.str_param_lingua!="") lingua = iCG.str_param_lingua;
    else lingua = "pt";
    if (iCG.str_param_pais  !="") pais   = iCG.str_param_pais  ;
    else pais = "BR";
    }

  // From: icg.iCG.main(String[])
  public static void loadMessages () {
    try {  // tem que existir o arq. "Messsage.properties, senão erro no "inicia"
           // tive que colocar na "raiz" para o "codebase="."" poder pegar e evitar erro em Netscape...\
           // http://www.javaworld.com/javaworld/jw-01-1999/jw-01-internationalize-p3.html

      bundle = ResourceBundle.getBundle("Messages", currentLocale);
      System.out.println("Bundle.loadMessages(): lingua="+lingua+", pais="+pais+", locale="+currentLocale);


    } catch (java.util.MissingResourceException mre) {
      // Couldn't read it as a resource bundle.  Maybe this is a Netscape 4.x browser client
      // which erroneously munges the properties file name, and doesn't use the prescribed
      // fallback pattern? Try to read the correct language manually instead
      lingua_pais();

      try {
          //Correto: "igeom/msg/Messages_"+currentLocale+".properties"); => erro segurança
          String str="";
          if (currentLocale!=null) str = "_"+currentLocale;
          java.net.URL source;
          try {
            //*System.out.println("[Bundle] 1: Messages"+str+".properties");
            source = new java.net.URL(iCG.codebase,"Messages"+str+".properties"); 
          } catch (Exception e) {
            //*System.out.println("[Bundle] 2: Messages"+str+".properties");
            source = new java.net.URL(iCG.codebase,"Messages.properties"); 
            }
      }
      catch (Exception e) {
       //- System.out.println("Bundle: "+e);
       }
       } catch (Exception e) {
           bundle = null;
           //- System.out.println("[Bundle] Messages.properties inexistente "+endereco+" "+e);
           }
        //bundle = Global.bundle();
     } //  static void loadMessages()


  public static boolean loadProperties (String filename) {
    ConfigName=filename;
    try {
       FileInputStream in=new FileInputStream(filename);
       Prop=new Properties();
       Prop.load(in);
       in.close();
       }
    catch (Exception e) {
       Prop=new Properties();
       return false;
       }
    return true;
  }
  public static void loadProperties (String dir, String filename) {
    try {
       Properties p=System.getProperties();
       ConfigName=dir+p.getProperty("file.separator")+filename;
       loadProperties(ConfigName);
       }
    catch (Exception e) {
       Prop=new Properties();
       }
    }
  public static void loadPropertiesInHome (String filename) {
    try {
      Properties p=System.getProperties();
      loadProperties(p.getProperty("user.home"),filename);
      }
    catch (Exception e) {
      Prop=new Properties();
      }
    }


  // If reading our strings is not possible using a ResourceBundle,
  // then attempt to read them manually from a file
  // Se não foi possível ler msg usando "ResourceBundle", tente ler manualmente

  //Netscape Communications Corporation -- Java 1.1.5
  //Reading language as a ResourceBundle failed. Attempting to read the file manually.
  //java.util.MissingResourceException: can't find resource for Messages_pt_BR
  //Tente ler do arquivo: http://milanesa.ime.usp.br/~leo/igeom/igeom/msg/Messages_pt_BR.properties
  //Bundle: netscape.security.AppletSecurityException: security.class from local disk trying to access url: http://milanesa.ime.usp.br/~leo/igeom/igeom/msg/Messages_pt_BR.properties
  //Bundle!inicia: resourcebundle==null


  // Initiate...
  public static String inicia (ResourceBundle resourcebundle, String stringMsg, String stringErro) {
    try {
        if (resourcebundle!=null) 
           stringMsg = resourcebundle.getString(stringMsg); // tem que existir o arq. "igeom/msg/Messsage.properties
        else 
           ;//- System.out.println("Bundle!inicia: resourcebundle==null");
    } catch (java.util.MissingResourceException missingresourceexception) {
        stringMsg = stringErro;
    }
    return stringMsg;
    }


  // Processa mensagens com variáveis, do tipo "Arquivo $ARQ$ foi gravado com sucesso"
  //
  public static String msgComVar (String strTexto, String strVar, String [] strValor) {
    // pega string com "$ARQ" no meio da string devolvida por "Bundle.msg()"
    java.util.StringTokenizer strToken = new java.util.StringTokenizer(Bundle.msg(strTexto), "$");  //, true);
    String strFinal = "";
    int prox = 0;
    while (strToken.hasMoreTokens()) {
        String strAux = strToken.nextToken();
        if (strAux.equals(strVar)) {
            strFinal = strFinal+strValor[prox++]; // concatena com valor da variável (troca "strVal" por "strValor"
            }
        else {
            strFinal = strFinal+strAux;   // concatena com o texto
            }
        }
    return strFinal;
    }


  public static String msg (String stringMsg, String strVar, String strValor) {
    String [] strValor1 = new String[1];
    strValor1[0] = strValor;
    return msgComVar( inicia(bundle, stringMsg, stringMsg), strVar, strValor1 );
    }

  
  public static String msg (String stringMsg) {
    return inicia(bundle, stringMsg, stringMsg);
    }


  }

