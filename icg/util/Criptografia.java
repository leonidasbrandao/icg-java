/**
 * 
 * iMath - http://www.matematica.br
 * 
 * <p>Title: iCG - Interactive Computer (Computador Gaveteiro Interativo)</p>
 * 
 * <p>Description: this a didatic software to explain to the newbie what is a Computer, how to program it</p>
 * 
 * <p>Copyright: Copyleft (c) 2003</p>
 * <p>Company: LInE - http://www.ime.usp.br/line</p>
 * @author Leônidas de Oliveira Brandão
 * @version 
 * 
 */

package icg.util;

import java.util.Random; 
import java.util.StringTokenizer;

public class Criptografia extends Random {

  private static final int lixo = 50;

  private static final char[] vetorHexa = {'0' , '1' , '2' , '3' ,'4' , '5' , '6' , '7' , '8' , '9' , 'a' , 'b' ,'c' , 'd' , 'e' , 'f'   }; 

  public static int nextInt2 (int n, Random m) {
    if (n<=0)
       throw new IllegalArgumentException("n must be positive");

    int var = m.nextInt();
    if (var < 0)
       var = -var;
    var = var % n;//pegar um numero entre 0 e n (inclusive)
    return var;
    }


  public static String criaHexa ( byte[] b ) {
    StringBuffer sb = new StringBuffer( b.length * 2 );
    for ( int i=0 ; i<b.length ; i++ ) {
        sb.append( vetorHexa [ ( b[ i] & 0xf0 ) >>> 4 ] );
        sb.append( vetorHexa [ b[ i] & 0x0f ] );
        }
    return sb.toString();
    }


  public static byte[] converteHexaParaByte (String strHexa2byte) {
    int stringLength;
    strHexa2byte = strHexa2byte.trim();
    stringLength = strHexa2byte.length();
    
    //if ( (stringLength & 0x1) != 0 ) strHexa2byte = strHexa2byte.substring(0,strHexa2byte.length()-1); // tenta sem últ. caractere

    if ( (stringLength & 0x1) != 0 ) {
       //throw new IllegalArgumentException("  converteHexaParaByte requires an even number of hex characters" );
       String strErr = "stringLength=" + stringLength + " converteHexaParaByte precisa de um numero impar de caracteres hexadecimais\n" +
                       "line=|"+strHexa2byte+"|";
       throw new IllegalArgumentException(strErr);
       }
    
    byte [] b = new byte[ stringLength / 2 ];
    for (int i=0 ,j= 0; i< stringLength; i+= 2,j ++ ) {
        int high= valorByte(strHexa2byte.charAt ( i ));
        int low = valorByte( strHexa2byte.charAt ( i+1 ) );
        b[ j ] = (byte ) ( ( high << 4 ) | low );
        }
    return b;
    }



  private static int valorByte ( char c ) {
    if ( '0' <= c && c <= '9' ) {
       return c - '0';
       }
    else if ( 'a' <= c && c <= 'f' ) {
       return c - 'a' + 0xa;
       }
    else if ( 'A' <= c && c <= 'F' ) {
       return c - 'A' + 0xa;
       }
    else {
       throw new IllegalArgumentException ( "Invalid hex character: " + c );
       }
    }

  
  public static String criptografa (String s, boolean eh_applet) {
    String saida = "";
    Random n = new Random();
    int i;

    // Se quiser lista na tela a conteúdo do arquivo de forma não "criptografada"
    //System.out.println("[Criptografia!criptografa]: "+s);

    //temos que verificar se nao vai dar problema de configuracao
    //cada plataforma pega o seu chartset ... para nao acontecer isso
    //devemos usar as linhas abaixo no lugar dos correspodentes no codigo.
    //String(byte[] bytes, String charsetName)->US-ASCII
    //getBytes(String charsetName)

    nextInt2(16, n);
    for (i = 0; i < lixo; i++)
       saida += vetorHexa[nextInt2(16, n)];     //inserindo lixo no comeco
    saida += criaHexa(s.getBytes());                
    for (i = 0; i< lixo; i++)
       saida += vetorHexa[nextInt2(16, n)];        // inserindo lixo no fim
    //if (eh_applet) // se deixar aqui, ao "descriptografar" precisa eliminar o último caractere
    //    saida += '!';
    return saida;
    }

  public static String descriptografa (String strToBeConverted, boolean eh_applet) {
    int i;
    String saida1 = "";
    String str, linha;

    // CAUTION: this is important when using 'Arquivo.getMAFile(...)' - reading file through Web
    //          Erase final marks, like '\n'
    strToBeConverted = strToBeConverted.trim();
    // System.out.println("[Criptografia!descriptografa] lixo="+lixo+" strToBeConverted.length()="+strToBeConverted.length()+"line=|"+strToBeConverted+"|");

    // Para remover lixos do começo e do fim
    int dif = strToBeConverted.length() - lixo;
    if (lixo<=dif) saida1 = strToBeConverted.substring(lixo,dif); // (strToBeConverted.length()-lixo));
    else {
        System.out.println("[C!d] "+strToBeConverted.length()+", "+lixo+" -> "+dif); //("[Criptografia!descriptografa] lixo="+lixo+" strToBeConverted.length()="+strToBeConverted.length()); 
        saida1 = strToBeConverted;
        }

    byte [] saida = converteHexaParaByte(saida1);
    str = new String(saida);
    if (eh_applet) {
       saida1 = "";
       
       //- System.out.println("[Criptografia!descriptografa]: código decodificado");
       StringTokenizer st = new StringTokenizer(str, "\n");
       //- int conta_linha = 0;
       while (st.hasMoreTokens()) {
          linha = st.nextToken(); 
          saida1 += linha;
          //-     saida1 += "!\n"; // no iCG não precisa
          //-     conta_linha++;
          //-     System.out.println(conta_linha+":  "+linha);
          }
       return saida1;
       }
    //System.out.println("[Criptografia!descriptografa]: código decodificado: \n"+str);
    return str;
    } // static String descriptografa (String s, boolean eh_applet)


  public static boolean eh_exercicio (String strToBeTested) {
    int i;
    if (strToBeTested==null) { System.out.println("[Cript] str vazia "+strToBeTested+"  "+vetorHexa); return false; }
    for (i = 0; i < vetorHexa.length; i++)
       if (strToBeTested.charAt(0) == vetorHexa[i])
          return true;
    return false;
    }

  }
