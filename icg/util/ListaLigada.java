package icg.util;

//import 

public class ListaLigada {

 private No inicioLista = null;
 private No posicaoAtualLista = null;
 private int tamanhoLista = 0;

 public No inicioLista () { return inicioLista; }

 public String montaListaElementos () {
   No no = inicioLista;
   String str = "";
   //System.out.println("[ListaLigada] conteúdo\n <");
   while (no != null) {
     //System.out.print(" " + no.obj().toString());
     str += no.obj().toString();
     no = no.proximo();
     }
   //System.out.println(str+" >");
   return str;
   }

 public void listaElementos () {
   No no = inicioLista;
   System.out.println("[ListaLigada] conteúdo\n <");
   //
   while (no != null) {
     System.out.print(" " + no.obj().toString());
     no = no.proximo();
     }
   System.out.println(" >");
   }

 public No encontraAnterior (No atual) {
   No no = inicioLista, noAnterior;
   //
   while (no != null && no != atual) {
     noAnterior = no;
     no = no.proximo();
     }
   return no;
   }

 public void add (Object obj) {
   if (posicaoAtualLista == null) {
      posicaoAtualLista = new No(obj); // cria novo início de lista
      inicioLista = posicaoAtualLista;
      tamanhoLista = 1;
      }
   else {
      No no = new No(obj);
      posicaoAtualLista.proximo(no); // liga novo nó na última posição da lista
      posicaoAtualLista = no;        // este é atualmente o novo último da lista
      tamanhoLista++;
      }
   }

 public void addLast (Object obj) {
   add(obj);
   }

 public void addAll (ListaLigada listaConcat) {
   if (posicaoAtualLista == null) {
      if (listaConcat==null) { System.out.println("[ListaLigada!addAll] erro, listaConcat nula"); return; }
      posicaoAtualLista = listaConcat.posicaoAtualLista; // cria novo início de lista
      inicioLista = listaConcat.inicioLista;
      tamanhoLista = listaConcat.tamanhoLista;
      }
   else {
      if (listaConcat==null) { System.out.println("[ListaLigada!addAll] erro no else, listaConcat nula"); return; }
      No no = listaConcat.inicioLista(), no0 = null;
      while (no != null) {
	if (no==no0) return; // Truque: para evitar laço infinito!
        //System.out.print(".");
        no0 = no;
        posicaoAtualLista.proximo(no); // liga novo nó na última posição da lista
        posicaoAtualLista = no;        // este é atualmente o novo último da lista
        tamanhoLista++;
        }
      }
   }

 public Object removeLast () {
   Object obj;
   No anterior = encontraAnterior(posicaoAtualLista);
   if (anterior != inicioLista) {
      if (anterior==null || anterior.proximo()==null) return null;
      obj = anterior.proximo().obj();
      anterior.proximo(null);
      tamanhoLista--;
      }
   else {
      if (inicioLista!=null)
         obj = inicioLista.obj(); // pode retornar null aqui
      else return null;
      tamanhoLista = tamanhoLista>0 ? tamanhoLista-1 : 0;
      }
   return obj;
   }

}
