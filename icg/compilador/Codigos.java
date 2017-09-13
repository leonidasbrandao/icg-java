package icg.compilador;

import java.io.*;
import java.util.Vector;
import java.util.Stack; // deprecated, opção icg/compilador/Stack.java

import icg.configuracoes.Configuracao;

public class Codigos {

  static int op;
  static Stack pilha_unario = new Stack(), pilha_op = new Stack();

  int[] memoria = new int[100];
  int mp = 89; // memory pointer
  int sp = 99; // stack pointer
  public Stack programa;
  Vector comandos = new Vector();
  Variaveis vars;

  public Codigos() {
    vars = new Variaveis();
    programa = new Stack();
    while (!programa.isEmpty()) {
      programa.pop();
      }
    }

  private Vector pegaExpressao (Vector c, int pos) {
    Vector inFixa = new Vector();
    String item = ( (Elemento) c.elementAt(pos)).obj();
    System.out.print("Expressão Infixa: ");
    while (!item.equals(";")) {
      inFixa.addElement(new Elemento(item));
      System.out.print(item);
      pos++;
      item = ( (Elemento) c.elementAt(pos)).obj();
      }
    System.out.print("\n");
    return inFixa;
    }

  public void imprimePrograma () {
    while (!programa.isEmpty()) {
      System.out.println(programa.pop().toString());
      }
    }

  public void imprimePrograma (String arquivo) {
    String codigo;
    try {
      PrintWriter out = new PrintWriter(new FileWriter(arquivo));
      while (!programa.isEmpty()) {
        codigo = programa.pop().toString();
        out.println(codigo);
        }
      out.close();
      }
    catch (Exception e) {
      System.out.println(e);
      }
    }

  public void geraLista (Vector c, int inicio) {
    int i = 0;
    while (i < c.size()) {
      i = listaDeComandos(c, i);
      comandos.removeAllElements();
      }

    // armazena os espaços da memória q não foram utilizados
    // até a ultima instrução
    for (i = programa.size() - 1; i < 90 - vars.size(); i++) {
      programa.push("000");

      // armazena o espaço para as variáveis
      }
    int cont = 90 - vars.size();
    for (i = cont; i < 90; i++) {
      programa.push(vars.var[i - cont].valor());

      // armazena o espaço da pilha
      }
    for (i = 90; i < 100; i++) {
      programa.push("000");
      }
    }

  private int listaDeComandos (Vector c, int inicio) {
    int num_item = inicio;
    int mem;
    if (num_item < c.size()) {
      String item = new String( ( (Elemento) c.elementAt(inicio)).obj());
      Elemento e = new Elemento(item);
      // Verifica se é uma atribuição
      if (e.tipo == e.VARIAVEL) {
        num_item = trataAtribuicao(c, num_item);
        }

      // Verifica se é uma leitura do teclado
      else if (e.obj().equals(Configuracao.cmd_leia)) { //"le")) {
        num_item = trataLe(c, num_item);
        }

      // Verifica se é uma saida na tela
      else if (e.obj().equals(Configuracao.cmd_escreva)) { //""escreve")) {
        num_item = trataEscreve(c, num_item);
        }

      // Verifica se é um if
      else if (e.obj().equals(Configuracao.cmd_if)) { //""if")) {
        num_item = trataIf(c, num_item);
        }

      // Verifica se é um while
      else if (e.obj().equals(Configuracao.cmd_while)) { //""while")) {
        num_item = trataWhile(c, num_item);
        }
      // Verifica se é um else
      else if (e.obj().equals(Configuracao.cmd_else)) { //""else")) {

        }
      else {
        num_item++;

        }
      for (int i = 0; i < comandos.size(); i++) {
        programa.push(comandos.elementAt(i));
        }
      }

    return num_item;
    }

  private int trataIf (Vector c, int pos) {
    String str = new String(";");
    String item;
    Vector expLog = new Vector();
    Vector exp = new Vector();
    String opL = new String("");
    Elemento e;
    int i;
    //encontra o final do while
    //procurar por um ";", mas se encontrar um "{"
    //deve se procurar um "  }"

    expLog = pegaExpLog(c, pos);
    pos++; // pula o "if"
    pos++; // pula o "("
    i = 0;
    while (i < expLog.size()) {
      // existe pelo menos duas expressões:
      // exp op_logigo exp
      e = (Elemento) expLog.elementAt(i);

      // se o item não é um operador lógico adiciona o item a expressão
      if (!ehCompLogico(e.obj())) {
        exp.addElement(e);
        }
      else {
        trataExpressao(exp); // o Acumulador tem o resultado da expressão
        comandos.addElement(new String("1").concat(new Integer(sp).toString()));
        sp--;
        //push

        pos = pos + exp.size();
        pos++; // avança a posicao do op.
        exp.removeAllElements(); //limpa a expressão lógica

        if (opL.equals("")) { //significa q é exp do lado esquerdo
          opL = e.obj();
          }
        else { // é a expressão do lado direito
          trataExpressao(exp); // o Acumulador tem o resultado da expressão
          comandos.addElement(new String("1").concat(new Integer(sp).toString()));
          sp--;
          //push
          //sp = opl_Maior (sp);
              /* aqui ja foram avaliadas duas expressoes e seus valores estao na pilha
           * agora é preciso avaliar a expressao logica e empilhar o valor*/

          if (opL.equals(">")) {
            opl_Maior(sp);
            }
          else if (opL.equals(Configuracao.cmd_geq)) {//>=
            opl_MaiorIg(sp);
            }
          else if (opL.equals("<")) {
            opl_Menor(sp);
            }
          else if (opL.equals(Configuracao.cmd_leq)) {//<=
            opl_MenorIG(sp);
            }
          else if (opL.equals(Configuracao.cmd_eq)) {//==
            opl_Igual(sp);
            }
          else if (opL.equals(Configuracao.cmd_neq)) {//!=
            opl_NIgual(sp);
            }
          // le o proximo item
          e = (Elemento) expLog.elementAt(i);
          opL = "";
          }
        }
      i++;
      }
    // primeiro item depois da exp lógica
    pos = pos + i - 1;
    item = ( (Elemento) c.elementAt(pos)).obj();
    while (!item.equals(str)) {
      if (item.equals("{")) {
        str = "  }";
        pos++; // avanca o item da {
        }
      pos = listaDeComandos(c, pos);
      item = ( (Elemento) c.elementAt(pos)).obj();
      }
    return pos;
    }

  private Vector pegaExpLog (Vector c, int pos) {
    Stack p = new Stack();
    Vector exp = new Vector();
    int inicio = pos + 1;
    // para expressões lógicas o pegaExpressão é diferente.
    // este baseia-se em operadores lógicos, no "||", ou no "&&" ou  no ultimo ")"
    pos++; //avança um item "("
    p.push(c.elementAt(pos));
    pos++;
    while (!p.isEmpty()) {
      if ( ( (Elemento) c.elementAt(pos)).obj().equals("(")) {
        p.push(c.elementAt(pos));
        }
      else if ( ( (Elemento) c.elementAt(pos)).obj().equals(")")) {
        p.pop();
        }
      pos++;
      }
    for (int i = inicio + 1; i < pos - 1; i++) {
      exp.addElement(c.elementAt(i));
      }
    return exp;
    }

  private int trataWhile (Vector c, int pos) {
    String str = new String(";");
    String item;
    Vector expLog = new Vector();
    Vector exp = new Vector();
    String opL = new String("");
    Elemento e;
    int i;
    //encontra o final do while
    //procurar por um ";", mas se encontrar um "{"
    //deve se procurar um "  }"

    expLog = pegaExpLog(c, pos);
    pos++; // pula o "while"
    pos++; // pula o "("
    i = 0;
    while (i < expLog.size()) {
      // existe pelo menos duas expressões:
      // exp op_logigo exp
      e = (Elemento) expLog.elementAt(i);

      // se o item não é um operador lógico adiciona o item a expressão
      if (!ehCompLogico(e.obj())) {
        exp.addElement(e);
        }
      else {
        trataExpressao(exp); // o Acumulador tem o resultado da expressão
        comandos.addElement(new String("1").concat(new Integer(sp).toString()));
        sp--;
        //push

        pos = pos + exp.size();
        pos++; // avança a posicao do op.
        exp.removeAllElements(); //limpa a expressão lógica

        if (opL.equals("")) { //significa q é exp do lado esquerdo
          opL = e.obj();
          }
        else { // é a expressão do lado direito
          trataExpressao(exp); // o Acumulador tem o resultado da expressão
          comandos.addElement(new String("1").concat(new Integer(sp).toString()));
          sp--;
          //push
          //sp = opl_Maior (sp);
              /* aqui ja foram avaliadas duas expressoes e seus valores estao na pilha
           * agora é preciso avaliar a expressao logica e empilhar o valor*/

          if (opL.equals(">")) {
            opl_Maior(sp);
            }
          else if (opL.equals(">=")) {
            opl_MaiorIg(sp);
            }
          else if (opL.equals("<")) {
            opl_Menor(sp);
            }
          else if (opL.equals("<=")) {
            opl_MenorIG(sp);
            }
          else if (opL.equals("==")) {
            opl_Igual(sp);
            }
          else if (opL.equals("!=")) {
            opl_NIgual(sp);
            }
          // le o proximo item
          e = (Elemento) expLog.elementAt(i);

          opL = "";
          }
        }
      i++;
      }
    // primeiro item depois da exp lógica
    pos = pos + i - 1;
    item = ( (Elemento) c.elementAt(pos)).obj();
    while (!item.equals(str)) {
      if (item.equals("{")) {
        str = "  }";
        pos++; // avanca o item da {
        }
      pos = listaDeComandos(c, pos);
      item = ( (Elemento) c.elementAt(pos)).obj();
      }
    return pos;
    }

  private int trataAtribuicao (Vector c, int pos) {
    int fim_exp;
    Vector inFixa;
    String item = new String( ( (Elemento) c.elementAt(pos)).obj());
    if (vars.existe(item) == -1) {
      vars.add(item, 0, mp);
      mp--;
      }

    pos++; // o proximo item é um "="
    pos++; // a partir daqui começa uma expressão. O seu final é na pos do ";"
    inFixa = pegaExpressao(c, pos);
    pos = pos + inFixa.size();
    trataExpressao(inFixa);
    comandos.addElement(new String("1").concat(vars.posMemoria(item)));
    // [variavel] = AC
    return pos;
    }

  private void trataExpressao (Vector inFixa) {
    Vector posFixa;
    Elemento e;
    int index;
    posFixa = toPosFixa(inFixa);
    System.out.print("Expressão Posfixa: ");
    for (int i = 0; i < posFixa.size(); i++) {
      e = (Elemento) posFixa.elementAt(i);
      if (e.tipo != Elemento.OUTROS) {
        index = vars.existe(e.obj());
        if (index == -1) {
          try {
            int j = Integer.parseInt(e.obj()); // é número
            vars.add(e.obj(), j, mp);
            }
          catch (Exception exc) {
            vars.add(e.obj(), 0, mp);
            }
          mp--;
          }
        comandos.addElement(new String("0").concat(vars.posMemoria(e.obj())));
        // AC = [variavel]

        comandos.addElement(new String("1").concat(new Integer(sp).toString()));
        sp--;
        // push
        }
      else { // é operador
        if (e.obj().equals("*")) {
          sp++;
          comandos.addElement(new String("0").concat(new Integer(sp).toString()));
          // pop

          sp++;
          comandos.addElement(new String("4").concat(new Integer(sp).toString()));
          //AC = AC * pop

          comandos.addElement(new String("1").concat(new Integer(sp).toString()));
          sp--;
          // push
          }

        else if (e.obj().equals("/")) {
          sp++;
          sp++;
          comandos.addElement(new String("0").concat(new Integer(sp).toString()));
          //pop
          comandos.addElement(new String("5").concat(new Integer(sp - 1).
              toString()));
          //AC = pop / Ac

          comandos.addElement(new String("1").concat(new Integer(sp).toString()));
          sp--;
          //push
          }

        else if (e.obj().equals("+")) {
          sp++;
          comandos.addElement(new String("0").concat(new Integer(sp).toString()));
          // pop

          sp++;
          comandos.addElement(new String("2").concat(new Integer(sp).toString()));
          //AC = AC * pop

          comandos.addElement(new String("1").concat(new Integer(sp).toString()));
          sp--;
          // push

          }

        else { // -
          sp++;
          sp++;
          //pop
          comandos.addElement(new String("0").concat(new Integer(sp).toString()));
          //pop

          comandos.addElement(new String("3").concat(new Integer(sp - 1).
              toString()));
          //AC = pop - Ac

          comandos.addElement(new String("1").concat(new Integer(sp).toString()));
          sp--;
          //push
          }
        }
      System.out.print(e.obj());
      }
    sp++;
    comandos.addElement(new String("0").concat(new Integer(sp).toString()));
    //pop

    System.out.print("\n");
    }

  private int trataLe (Vector v, int pos) {
    int index;
    String item;
    // o proximo item léxico é um "("
    pos++;

    // posiciona no proximo item (q deve ser uma variável
    pos++;
    item = ( (Elemento) v.elementAt(pos)).obj();

    index = vars.existe(item);
    if (index == -1) {
      vars.add(item, 0, mp);
      mp--;
      }

    comandos.addElement(new String("7").concat(vars.posMemoria(item)));

    // pega o proximo item
    pos++;
    item = ( (Elemento) v.elementAt(pos)).obj();

    // toda vez q for encontrada uma "," então existe uma nova var
    while (item.equals(",")) {
      pos++; //posiciona na prox variavel
      item = ( (Elemento) v.elementAt(pos)).obj();

      index = vars.existe(item);
      if (index == -1) {
        vars.add(item, 0, mp);
        mp--;
        }
      comandos.addElement(new String("7").concat(vars.posMemoria(item)));

      //pega o proximo item
      pos++;
      item = ( (Elemento) v.elementAt(pos)).obj();
      }
    pos++; // le o ")"

    return pos;
    }

  private int trataEscreve (Vector v, int pos) {
    int index;
    String item;
    // o proximo item léxico é um "("
    pos++;

    // posiciona no proximo item (q deve ser uma variável
    pos++;
    item = ( (Elemento) v.elementAt(pos)).obj();

    index = vars.existe(item);
    if (index == -1) {
      if (Integer.getInteger(item) != null) { // é número
        vars.add(item, Integer.getInteger(item).intValue(), mp);
        }
      else {
        vars.add(item, 0, mp);
        }
      mp--;
      }
    comandos.addElement(new String("8").concat(vars.posMemoria(item)));

    // pega o proximo item
    pos++;
    item = ( (Elemento) v.elementAt(pos)).obj();

    // toda vez q for encontrada uma "," então existe uma nova var
    while (item.equals(",")) {
      pos++; //posiciona na prox variavel
      item = ( (Elemento) v.elementAt(pos)).obj();

      index = vars.existe(item);
      if (index == -1) {
        if (Integer.getInteger(item) != null) { // é número
          vars.add(item, Integer.getInteger(item).intValue(), mp);
          }
        else {
          vars.add(item, 0, mp);
          }
        mp--;
        }

      comandos.addElement(new String("8").concat(vars.posMemoria(item)));

      //pega o proximo item
      pos++;
      item = ( (Elemento) v.elementAt(pos)).obj();
      }
    pos++; // le o ")"
    return pos;
    }

  public static Vector toPosFixa (Vector inFixa) {
    String item;
    Stack operador = new Stack();
    Stack expressao = new Stack();
    Vector posFixa = new Vector();
    int i, j, pre1, pre2;

    for (i = 0; i < inFixa.size(); i++) {
      item = ( (Elemento) inFixa.elementAt(i)).obj();
      j = precedencia(item);
      if (j == 3) { //se é operando
        expressao.push( (Object) item);
        }
      else if (item.compareTo("(") == 0) {
        operador.push( (Object) "(");
        }
      else if (item.compareTo(")") == 0) {
        while (operador.peek().toString().compareTo("(") != 0) {
          expressao.push(operador.pop());
          }
        operador.pop();
        }
      else { // é um operador
        while ( (!operador.isEmpty()) &&
               (precedencia(operador.peek().toString()) < j) &&
               operador.peek().toString().compareTo("(") != 0) {
          expressao.push(operador.pop());
          }
        operador.push( (Object) item);
        }
      }
    while (!operador.isEmpty()) {
      expressao.push(operador.pop());

      }
    while (!expressao.isEmpty()) {
      posFixa.addElement(new Elemento(expressao.peek().toString()));
      expressao.pop();
    //expressao.removeElementAt(0);
      }
    //System.out.println(aux);

    return (posFixa);
    }

  public static boolean isOperador (String op) {
    return (precedencia(op) != 3);
    }

  public static int precedencia (String op) {
    if ( (op.compareTo("/") == 0) || (op.compareTo("*") == 0) ||
        (op.compareTo("(") == 0) || (op.compareTo(")") == 0)) {
      return (0);
      }
    else if (op.compareTo("+") == 0) {
      return (1);
      }
    else if (op.compareTo("-") == 0) {
      return (2);
      }
    else {
      return (3); //não é operador
      }
    }

  private boolean ehCompLogico (String item) {
    if (item.equals(">") || item.equals(">=") || item.equals("<") ||
        item.equals("<=") || item.equals("==") || item.equals("!=")) {
      return true;
      }
    return false;
    }

  /* A = 1.a expressap, B = 2.a expressao
   * se opl == ">" devo testar A - B > 0 => AC > 0 . OK
   * parte que avalia <, >, <=, >=, ==
   */

  private void opl_MenorIG (int sp) {
    int spaux = sp + 2; /*sp é a primeira pos vazia, sp + 2 = A*/
    comandos.addElement(new String("0").concat(new Integer(spaux).toString()));
    //pop
    comandos.addElement(new String("3").concat(new Integer(spaux - 1).toString()));
    //AC <= A - B
    comandos.addElement(new String("1").concat(new Integer(spaux).toString()));
    //push np lugar aonde estava o A
    return;
    }

  private void opl_MaiorIg (int sp) {
    int spaux = sp + 1; /*sp é a primeira pos vazia, sp + 1 = B*/
    comandos.addElement(new String("0").concat(new Integer(spaux).toString()));
    //pop
    comandos.addElement(new String("3").concat(new Integer(++spaux).toString()));
    //AC <= B - A
    comandos.addElement(new String("1").concat(new Integer(spaux).toString()));
    //push np lugar aonde estava o A
    return;
    }

  private void opl_Igual (int sp) {
    int spaux = sp + 2; /*pos do A*/
    comandos.addElement(new String("0").concat(new Integer(spaux).toString()));
    //pop
    comandos.addElement(new String("3").concat(new Integer(spaux - 1).toString()));
    //AC <= A - B
    comandos.addElement(new String("6").concat(new Integer(comandos.size() + 3).
                                               toString()));
    //desvio
    comandos.addElement(new String("0").concat(new Integer(spaux - 1).toString()));
    //pop
    comandos.addElement(new String("3").concat(new Integer(spaux).toString()));
    //AC <= B - A
    comandos.addElement(new String("1").concat(new Integer(spaux).toString()));
    //push AC
    return;
    }

  private void opl_Maior (int sp) {
    int spaux = sp + 2;
    comandos.addElement(new String("0").concat(new Integer(spaux - 1).toString()));
    //pop B
    comandos.addElement(new String("3").concat(new Integer(spaux).toString()));
    //AC <= B - A
    comandos.addElement(new String("6").concat(new Integer(comandos.size() + 3).
                                               toString()));
    //desvio
    comandos.addElement(new String("0").concat(new Integer(spaux).toString()));
    //pop A
    comandos.addElement(new String("3").concat(new Integer(spaux - 1).toString()));
    //AC <= A - B
    comandos.addElement(new String("1").concat(new Integer(spaux).toString()));
    //push AC
    return;
    }

  private void opl_Menor (int sp) {
    int spaux = sp + 2;
    comandos.addElement(new String("0").concat(new Integer(spaux).toString()));
    //pop A
    comandos.addElement(new String("3").concat(new Integer(spaux - 1).toString()));
    //AC <= A - B
    comandos.addElement(new String("6").concat(new Integer(comandos.size() + 3).
                                               toString()));
    //desvio
    comandos.addElement(new String("0").concat(new Integer(spaux - 1).toString()));
    //pop B
    comandos.addElement(new String("3").concat(new Integer(spaux).toString()));
    //AC <= B - A
    comandos.addElement(new String("1").concat(new Integer(spaux).toString()));
    //push AC
    return;
    }

  private void opl_NIgual(int sp) {
    int spaux = sp + 2; /*pos do A*/
    comandos.addElement(new String("0").concat(new Integer(spaux).toString()));
    //pop
    comandos.addElement(new String("3").concat(new Integer(spaux - 1).toString()));
    //AC <= A - B
    comandos.addElement(new String("6").concat(new Integer(comandos.size() + 6).
                                               toString()));
    //desvio
    comandos.addElement(new String("0").concat(new Integer(spaux - 1).toString()));
    //pop
    comandos.addElement(new String("3").concat(new Integer(spaux).toString()));
    //AC <= B - A
    comandos.addElement(new String("1").concat(new Integer(spaux).toString()));
    //push AC
    comandos.addElement(new String("6").concat(new Integer(comandos.size() + 2).
                                               toString()));
    //desvio
    comandos.addElement(new String("0").concat(new Integer(0 - 0001).toString()));
    //AC <= !0
    comandos.addElement(new String("1").concat(new Integer(spaux).toString()));
    //push Ac
    return;
    }

  }
