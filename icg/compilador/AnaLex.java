/**
 * 
 * iMath - http://www.matematica.br
 * 
 * <p>Title: iCG - Interactive Computer (Computador Gaveteiro Interativo)</p>
 * 
 * <p>Description: help in compile
 *    lexical analyser of iCG, build lexical items, storing them in a "Vector"
 * </p>
 * 
 * <p>Copyright: Copyleft (c) 2003</p>
 * <p>Company: LInE - http://www.ime.usp.br/line</p>
 * 
 * @author Leônidas de Oliveira Brandão (colaboration of  Marcio Teruo Akyama)</p>
 * @version 2012-05-21 (added 'read', 'write' - 'leia', 'escreva', void em Configuracao); 2008-10-02
 * 
 * @see    CompilerBaseClass.java; Leia.java; Elementos.java;
 * 
 **/


package icg.compilador;

import java.lang.*;
import java.util.*;

import icg.configuracoes.Configuracao;

public class AnaLex {

  // Vide "catch (java.lang.ClassCastException)" em "CompilerBaseClass.C()"

  // See 'icg/configuracoes/Configuracao.debugOptionAL' auxiliary messages to trace lexical analyser

  private final static boolean imprimeItem = false; // para imprimir itens a medida que insere no vetor "itensLex"

  public static Vector itensLex;

  // Método que recebe em s o código fonte do programa a ser analisado
  // Monta um Vector com os itens léxicos presentes na string "s"
  static Vector constroiTokens (String linhaStr) {
    int tamLnh = linhaStr.length();
    // if (Configuracao.debugOptionAL) System.out.println("[icg.compilador.AnaLex!constroiTokens(String)]  --- início: código="+linhaStr);
    if (Configuracao.debugOptionAL) System.out.println("[icg] Constrói 'token' via analisador léxico  --- início: código="+linhaStr);

    itensLex = new Vector();
    Elemento elem;
    int posicao,
        posicao0 = -1, // auxiliar para evitar "loops" infinito
        num_itens = 0; // auxiliar
    String atomo,ss,p;
    char proximo;
    char simbolo;
    char c_; // para pegar 'linhaStr.charAt(posicao)'
    // int i_;  // tem algum problema com o char de ASCII 13 (não o reconhece no Windows)
    posicao = 0;

    // if (Configuracao.debugOptionAL) System.out.println(" linhaStr="+linhaStr+" linhaStr.length()="+tamLnh+"\n---\n");
    while (posicao != tamLnh) {
        c_ = linhaStr.charAt(posicao);
        //if (posicao>20) return null; if (Configuracao.debugOptionAL) System.out.println(c_);
        // i_ = c_;//        if (i_==13) { c_ = '\n'; i_ = c_; }
        atomo = "";

        if (posicao0==posicao) { // por algum motivo no Windows o último caractere é de ASCII 13
           int i_ = c_;          // precisa deste truque!
           if (Configuracao.debugOptionAL) System.out.println("\nErro: ficou parado em "+posicao+", "+c_+" -> ASCII="+i_);
           // if (Configuracao.debugOptionAL) System.out.println("\nErro: ficou parado em "+posicao+", "+c_+"="+i_+" tipo(c_)="+Character.getType(c_));
           // break;
           if (posicao >= tamLnh-1) { if (Configuracao.debugOptionAL) System.out.println("[AL!constroiTokens] "+posicao+" "+linhaStr.charAt(tamLnh-1)); break; }
           posicao++; c_ = linhaStr.charAt(posicao);
           continue;
           }
        else posicao0=posicao;
        // --- elimina brancos
        while (posicao < tamLnh-1 && 
          (c_==' ' || c_=='\n' || c_=='\f') ) { posicao++; c_ = linhaStr.charAt(posicao); }

        //- if (Configuracao.debugOptionAL) System.out.println("[AnaLex] item \'"+atomo+"\' posicao="+posicao+" itensLex.size()="+itensLex.size());

	if (posicao>=tamLnh) return itensLex; // para evitar erro: linhas "branco" ao final do programa

        // --- caracteres especiais, operadores aritméticos, lógicos e relacionais
        proximo = c_;
        if (Configuracao.ehSimboloEspecial(proximo+"")) {
           simbolo = proximo;
	   posicao++; // Abaixo: c_ = linhaStr.charAt(posicao);
           if (posicao >= tamLnh) {
              atomo += simbolo;
              if (atomo!="") { num_itens++; itensLex.addElement(atomo); }
              //- if (Configuracao.debugOptionAL) System.out.println("[AnaLex] item \'"+atomo+"\' posicao="+posicao+" itensLex.size()="+itensLex.size());
	      if (imprimeItem) System.out.println(num_itens+": "+atomo);
              return itensLex; 
              }
           c_ = linhaStr.charAt(posicao);
        // --- != >= <= == (Estava considerando que ); era expressão, mas já foi corrigido.)

           String auxS = simbolo + ""; // veja se os 2 caracteres formam um símb., senão fique c/ o prim.
           auxS += c_ + ""; // 
           // if (EstaNoArray(c_, Configuracao.espec_simbol) && c_!=';') {
           if (Configuracao.ehSimboloEspecial(auxS) && c_!=';') {
    	      atomo = auxS;
              posicao++; c_ = linhaStr.charAt(posicao);
              //- System.out.print("[AnaLex] caractere \'"+c_+"\' atomo="+atomo);
    	      }
           else {
    	      atomo += simbolo;	     
    	      }
           num_itens++;
           itensLex.addElement(atomo);
           //- if (Configuracao.debugOptionAL) System.out.println("[AnaLex] caractere \'"+c_+"\' atomo="+atomo);
           }
        else {
        // --- identificador
           if (Character.isLetter(proximo)) {
    	      atomo = "";
    	      do {
    		atomo += proximo;
    		posicao++; c_ = linhaStr.charAt(posicao);
    		proximo = c_;
    	        }
	      while (Character.isLetter(proximo) || Character.isDigit(proximo));
              num_itens++;	      
    	      itensLex.addElement(atomo);
    	      }
	   else {
        // --- número
    	      if (Character.isDigit(proximo)) {
		 //if (Configuracao.debugOptionAL) System.out.println("É número: ("+posicao+","+proximo+")");
    	         atomo = "";
    	         do {
    		    atomo += proximo; // anexa atual caractere q/ compõe o número
    		    posicao++; c_ = linhaStr.charAt(posicao);
    		    proximo = c_;
                    //System.out.print(proximo);
    		    }
		 while (Character.isDigit(proximo));
                 num_itens++;		 
    		 itensLex.addElement(atomo);
		 //if (Configuracao.debugOptionAL) System.out.println(" = "+atomo);
    	         }
      	      //else erro
    	      } // if (Character.isDigit(proximo))
        }

      if (imprimeItem) System.out.println(num_itens+": "+atomo);
      // if (posicao==12) return itensLex; // apelação p/ quebrar o "loop infinito"
      // if (num_itens>14) return itensLex; // apelação...

    } // while (posicao != tamLnh)
    return itensLex;
  }
    
  //verifica se o caractere "proximo" está no array "s"
  static boolean EstaNoArray (char proximo, char[] s){
    int i;
    boolean estah;
    estah = false;
    for (i=0; i<s.length; i++) {
        if (proximo==s[i]) return true; // estah = true;
        }
    return estah;
    }

  }
