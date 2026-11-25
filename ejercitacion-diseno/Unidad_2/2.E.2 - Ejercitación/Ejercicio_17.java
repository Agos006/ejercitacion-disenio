import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Ejercicio_17 {

  public static void main(String[] args) {

    Terminal terminal1 = new Terminal("Terminal 1");
    Terminal terminal2 = new Terminal("Terminal 2");
    Terminal terminal3 = new Terminal("Terminal 3");
    Terminal[] terminales = {terminal1, terminal2, terminal3};
    GeneradorNumeros generador = GeneradorNumeros.obtenerInstancia();

    System.out.println("=== 1. Estado inicial ===");
    System.out.println("Último número emitido: " + generador.getUltimoNumero());

    System.out.println();
    System.out.println("=== 2. Cada terminal pide un número ===");
    terminal1.solicitarNumero();
    terminal2.solicitarNumero();
    terminal3.solicitarNumero();

    System.out.println();
    System.out.println("=== 3. Consultar el último número sin incrementar el contador ===");
    System.out.println("Último número emitido: " + generador.getUltimoNumero());
    System.out.println("Último número emitido: " + generador.getUltimoNumero());
    System.out.println("Números emitidos hasta ahora: " + generador.getCantidadEmitidos());

    System.out.println();
    System.out.println("=== 4. Las terminales piden números alternadamente ===");
    for (int i = 4; i <= 21; i++) {
      Terminal terminal = terminales[(i - 1) % 3];
      terminal.solicitarNumeroEnSilencio();
    }
    System.out.println("Solicitudes realizadas en total: " + generador.getCantidadEmitidos());
    System.out.println("Último número emitido: " + generador.getUltimoNumero());

    System.out.println();
    System.out.println("=== 5. Historial de números entregados ===");
    List<NumeroEmitido> historial = generador.obtenerHistorial();
    for (int i = 0; i < historial.size(); i++) {
      System.out.println("  Número " + historial.get(i).getNumero() + " -> " + historial.get(i).getTerminal());
    }

    System.out.println();
    System.out.println("=== 6. Verificar que no hay números repetidos ===");
    boolean consecutivos = true;
    for (int i = 0; i < historial.size(); i++) {
      if (historial.get(i).getNumero() != i + 1) {
        consecutivos = false;
      }
    }
    if (consecutivos) {
      System.out.println("Resultado: CORRECTO (los " + historial.size() + " números son consecutivos y no se repite ninguno)");
    } else {
      System.out.println("Resultado: ERROR (hay números repetidos o salteados)");
    }
    for (int i = 0; i < terminales.length; i++) {
      System.out.println(terminales[i].getNombre() + " entregó " + contarNumeros(historial, terminales[i].getNombre()) + " números");
    }

    System.out.println();
    System.out.println("=== 7. Verificar que es una única instancia ===");
    if (terminal1.getGenerador() == terminal2.getGenerador()
        && terminal2.getGenerador() == terminal3.getGenerador()) {
      System.out.println("Las tres terminales usan el mismo generador");
    } else {
      System.out.println("Las terminales usan generadores distintos");
    }
    System.out.println("El contador es privado: las terminales solo pueden pedir un número, no cambiarlo");
  }

  // Cuenta cuántos números entregó una terminal
  private static int contarNumeros(List<NumeroEmitido> historial, String terminal) {
    int cantidad = 0;
    for (int i = 0; i < historial.size(); i++) {
      if (historial.get(i).getTerminal().equals(terminal)) {
        cantidad++;
      }
    }
    return cantidad;
  }
}

/*
 * Un número entregado y la terminal que lo pidió. Es inmutable: sus atributos son final.
 */
final class NumeroEmitido {

  private final int numero;
  private final String terminal;

  public NumeroEmitido(int numero, String terminal) {
    this.numero = numero;
    this.terminal = terminal;
  }

  public int getNumero() {
    return numero;
  }

  public String getTerminal() {
    return terminal;
  }
}

/*
 * Clase Singleton: un único generador de números para las tres terminales.
 * El contador es privado, por eso solo se puede avanzar con generarNumero().
 */
class GeneradorNumeros {

  // Única instancia de la clase
  private static GeneradorNumeros instancia;

  // El contador es privado: las terminales no pueden modificarlo directamente
  private int ultimoNumero;
  private List<NumeroEmitido> historial;

  // Constructor privado: no se puede usar "new" desde otras clases
  private GeneradorNumeros() {
    ultimoNumero = 0;
    historial = new ArrayList<NumeroEmitido>();
  }

  // Devuelve siempre el mismo objeto
  public static GeneradorNumeros obtenerInstancia() {
    if (instancia == null) {
      instancia = new GeneradorNumeros();
    }
    return instancia;
  }

  // Entrega el siguiente número (empezando en 1) y guarda qué terminal lo pidió
  public int generarNumero(String terminal) {
    ultimoNumero++;
    historial.add(new NumeroEmitido(ultimoNumero, terminal));
    return ultimoNumero;
  }

  // Devuelve el último número emitido sin incrementar el contador
  public int getUltimoNumero() {
    return ultimoNumero;
  }

  public int getCantidadEmitidos() {
    return historial.size();
  }

  // Devuelve una copia del historial que no se puede modificar
  public List<NumeroEmitido> obtenerHistorial() {
    List<NumeroEmitido> copia = new ArrayList<NumeroEmitido>(historial);
    return Collections.unmodifiableList(copia);
  }
}

// Terminal de la recepción que entrega números de atención
class Terminal {

  private String nombre;
  private GeneradorNumeros generador;

  public Terminal(String nombre) {
    this.nombre = nombre;
    generador = GeneradorNumeros.obtenerInstancia();
  }

  public String getNombre() {
    return nombre;
  }

  public GeneradorNumeros getGenerador() {
    return generador;
  }

  // Pide un número y lo muestra
  public void solicitarNumero() {
    int numero = generador.generarNumero(nombre);
    System.out.println("[" + nombre + "] Entregó el número " + numero);
  }

  // Pide un número sin mostrar nada
  public void solicitarNumeroEnSilencio() {
    generador.generarNumero(nombre);
  }
}