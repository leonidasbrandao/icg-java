package icg.util;

public class No {

  private Object obj;
  private No proximo;

  // Estes apontadores s�o usados na lista de pontos de interse��o 
  // para impedir que fiquem uns sobre outros

  public No ( ) {
    this.obj = null;
    this.proximo = null;
    }

  public No (Object obj) {
    this.obj = obj;
    this.proximo = null;
    }

  public No (Object obj, No proximo) {
    this.obj = obj;
    this.proximo = proximo;
    } 

  public No proximo () {
    return proximo;
    }

  public void proximo (No novo) {
    this.proximo = novo;
    }

  public Object obj () {
    return obj;
    }


  }
