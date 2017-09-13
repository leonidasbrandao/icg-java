/**
 * 
 * iMath - http://www.matematica.br
 * 
 * <p>Title: iCG - Interactive Computer (Computador Gaveteiro Interativo)</p>
 * 
 * <p>Description: help in compile (produce "binary" code to iCG)</p>
 * 
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: LInE - http://www.ime.usp.br/line</p>
 * @author Leônidas de Oliveira Brandão
 * @version 2012-05-21 (added 'read', 'write' - 'leia', 'escreva', void em Configuracao); 2008-10-02 (command moved to 'Configuracao': cmd_if, cmd_else, cmd_leia, cmd_escreva); 2004-08-25
 * 
 */

package icg.compilador;

import icg.configuracoes.Configuracao;

import icg.msg.Bundle;

public class Elemento {

  Object obj;
  int tipo;

  public static int NUMERO = 0;
  public static int COMANDOS = 1; //
  public static int VARIAVEL = 2;
  public static int OUTROS = 3;   // símbolos
  public static int INVALIDO = -1;
  /* -1: invalido
      0: constante;
      1: operador, op. logico, (, ), {, }, etc...
      2: identificador */

  // Identify lexical item: is it command?
  public Elemento (String str) {
    int i;
    char c;

    obj = str;
    try {
      i = Integer.parseInt(str);
      tipo = NUMERO;
      if (Configuracao.debugOptionAL) System.out.println("[Elemento!Elemento(String)] <"+str+"> é contante: "+NUMERO);
    }
    catch (NumberFormatException e) {
      if (ehReservado(str)) {
         if (Configuracao.debugOptionAL) System.out.println("[Elemento!Elemento(String)] <"+str+"> é reservada: "+COMANDOS);
         tipo = COMANDOS; //OUTROS;
         }
      else {
        c = str.charAt(0);
        c = Character.toLowerCase(c);
        if (c >= '0' && c <= '9') {
           if (Configuracao.debugOptionAL) System.out.println("[Elemento!Elemento(String)] <"+str+"> tipo inválido: "+INVALIDO);
           tipo = INVALIDO;
           }
        else if (c >= 'a' && c <= 'z') {
          // => Primeiro caracter nao eh invalido...
          //System.out.println("[icg.compilador.Elemento] tipo variável");
          tipo = VARIAVEL;
          for (i = 1; i < str.length(); i++) {
            c = str.charAt(i);
            c = Character.toLowerCase(c);
            // Procurando caracteres invalidos...
            if (! ( (c >= '0' && c <= '9') || (c >= 'a' && c <= 'z'))) {
               tipo = INVALIDO;
               if (Configuracao.debugOptionAL) System.out.println("[Elemento!Elemento(String)] <"+str+"> tipo inválido: "+INVALIDO);	       
               break;
               }
            }
          if (Configuracao.debugOptionAL) System.out.println("[Elemento!Elemento(String)] <"+str+"> é identificador: "+VARIAVEL);	   
          }

        else if (str.length() <= 2) {
          // => operadores, op. logicos, {, }, etc...
          tipo = OUTROS;
          if (Configuracao.debugOptionAL) System.out.println("[Elemento!Elemento(String)] <"+str+"> é símbolo (\"outros\"): "+OUTROS);
          }
        else {
          tipo = INVALIDO;
          if (Configuracao.debugOptionAL) System.out.println("[Elemento!Elemento(String)] <"+str+"> tipo inválido: "+INVALIDO);
	  }
        }
      }
    }


  private boolean ehReservado (String s) {
    //System.out.println("[icg.compilador.Elemento!ehReservado] s = "+s);
    if (s.equals(Bundle.msg("cmdIf"))) {
       return true;
       }
    if (s.equals(Bundle.msg("cmdElse"))) { // command 'else'
      return true;
      }
    if (s.equals(Bundle.msg("cmdWhile"))) { // command 'while'
      return true;
      }
    if (s.equals(Bundle.msg("cmdRead"))) { // command 'read'
      return true;
      }
    if (s.equals(Bundle.msg("cmdWrite"))) { // command 'write'
      return true;
      }
    return false;
  }


  public int tipo () {
    return tipo;
  }

  public String obj () {
    return (String) obj;
  }
}
