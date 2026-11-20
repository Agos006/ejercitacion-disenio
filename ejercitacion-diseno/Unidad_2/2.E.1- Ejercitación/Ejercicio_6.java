public class Ejercicio_6 {

  public static void main(String[] args) {

    GestorTemas gestor = GestorTemas.obtenerInstancia();
    VistaPrincipal vistaPrincipal = new VistaPrincipal();
    VistaPerfil vistaPerfil = new VistaPerfil();
    VistaConfiguracion vistaConfiguracion = new VistaConfiguracion();

    System.out.println("---- 1. Tema inicial de la aplicación ----");
    System.out.println("Tema actual: " + gestor.getTemaActual());
    vistaPrincipal.mostrar();
    vistaPerfil.mostrar();
    vistaConfiguracion.mostrar();

    System.out.println();
    System.out.println("---- 2. El usuario cambia a tema oscuro desde Configuración ----");
    vistaConfiguracion.elegirTema("Oscuro");
    vistaPrincipal.mostrar();
    vistaPerfil.mostrar();
    vistaConfiguracion.mostrar();

    System.out.println();
    System.out.println("---- 3. Alternar entre claro y oscuro ---");
    gestor.alternarTema();
    vistaPrincipal.mostrar();
    vistaPerfil.mostrar();

    System.out.println();
    System.out.println("---- 4. Intentar poner un tema que no existe ----");
    vistaConfiguracion.elegirTema("Azul");
    System.out.println("El tema sigue siendo: " + gestor.getTemaActual());

    System.out.println();
    System.out.println("---- 5. Verificar que es una única instancia ----");
    GestorTemas gestor1 = GestorTemas.obtenerInstancia();
    GestorTemas gestor2 = GestorTemas.obtenerInstancia();
    if (gestor1 == gestor2) {
      System.out.println("gestor1 y gestor2 son el mismo objeto");
    } else {
      System.out.println("gestor1 y gestor2 son objetos distintos");
    }
    System.out.println("Cantidad de cambios de tema: " + gestor1.getCantidadCambios());
  }
}

/*
 * Clase Singleton: guarda el tema visual de toda la aplicación.
 * Todas las vistas consultan este mismo objeto, por eso el estado no se divide.
 */
class GestorTemas {

  // Única instancia de la clase
  private static GestorTemas instancia;

  // Los atributos son privados: solo se cambian con los métodos de esta clase
  private String temaActual;
  private String colorFondo;
  private String colorTexto;
  private int cantidadCambios;

  // Constructor privado: no se puede usar "new" desde otras clases
  private GestorTemas() {
    cantidadCambios = 0;
    aplicarColores("Claro");
  }

  // Devuelve siempre el mismo objeto
  public static synchronized GestorTemas obtenerInstancia() {
    if (instancia == null) {
      instancia = new GestorTemas();
    }
    return instancia;
  }

  // Cambia el tema. Solo acepta "Claro" u "Oscuro".
  public synchronized void cambiarTema(String tema) {
    if (tema.equals("Claro") || tema.equals("Oscuro")) {
      aplicarColores(tema);
      cantidadCambios++;
      System.out.println("[Temas] Tema cambiado a " + temaActual);
    } else {
      System.out.println("[Temas] El tema " + tema + " no existe, solo hay Claro y Oscuro");
    }
  }

  // Pasa de claro a oscuro, o de oscuro a claro
  public synchronized void alternarTema() {
    if (temaActual.equals("Claro")) {
      cambiarTema("Oscuro");
    } else {
      cambiarTema("Claro");
    }
  }

  public synchronized String getTemaActual() {
    return temaActual;
  }

  public synchronized String getColorFondo() {
    return colorFondo;
  }

  public synchronized String getColorTexto() {
    return colorTexto;
  }

  public synchronized int getCantidadCambios() {
    return cantidadCambios;
  }

  // Método privado: es la única forma de modificar los colores
  private void aplicarColores(String tema) {
    temaActual = tema;
    if (tema.equals("Claro")) {
      colorFondo = "#FFFFFF";
      colorTexto = "#000000";
    } else {
      colorFondo = "#121212";
      colorTexto = "#FFFFFF";
    }
  }
}

// Vista principal de la aplicación
class VistaPrincipal {
  public void mostrar() {
    GestorTemas gestor = GestorTemas.obtenerInstancia();
    System.out.println("[Vista Principal] fondo " + gestor.getColorFondo() + " | texto " + gestor.getColorTexto());
  }
}

// Vista del perfil del usuario
class VistaPerfil {
  public void mostrar() {
    GestorTemas gestor = GestorTemas.obtenerInstancia();
    System.out.println("[Vista Perfil] fondo " + gestor.getColorFondo() + " | texto " + gestor.getColorTexto());
  }
}

// Vista donde el usuario elige el tema
class VistaConfiguracion {
  public void elegirTema(String tema) {
    GestorTemas gestor = GestorTemas.obtenerInstancia();
    gestor.cambiarTema(tema);
  }

  public void mostrar() {
    GestorTemas gestor = GestorTemas.obtenerInstancia();
    System.out.println("[Vista Configuración] fondo " + gestor.getColorFondo() + " | texto " + gestor.getColorTexto());
  }
}