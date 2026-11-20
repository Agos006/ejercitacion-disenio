import java.util.LinkedList;
import java.util.Queue;
public class Ejercicio_7 {
 public static void main(String[] args) throws InterruptedException {

ColaImpresion colaImpresion = ColaImpresion.obtenerInstancia();
System.out.println("=== 1. Cola de impresión ===");
    colaImpresion.encolar("contrato.pdf", "Ana");
    colaImpresion.encolar("factura.docx", "Luis");
System.out.println("Documentos pendientes: " + colaImpresion.getCantidadPendientes());

System.out.println();
System.out.println("=== 2. Imprimir el siguiente documento ===");
    colaImpresion.imprimirSiguiente();
System.out.println("Documentos pendientes: " + colaImpresion.getCantidadPendientes());

System.out.println();
System.out.println("=== 3. Tres terminales envían documentos al mismo tiempo ===");
    Terminal terminal1 = new Terminal("Valentina", 3);
    Terminal terminal2 = new Terminal("Sofía", 3);
    Terminal terminal3 = new Terminal("Guadalupe", 3);
    terminal1.start();
    terminal2.start();
    terminal3.start();
    terminal1.join();
    terminal2.join();
    terminal3.join();
System.out.println("Documentos pendientes: " + colaImpresion.getCantidadPendientes());

System.out.println();
System.out.println("=== 4. Imprimir todos los documentos en orden de llegada ===");
    colaImpresion.imprimirTodos();

System.out.println();
System.out.println("=== 5. Intentar imprimir con la cola vacía ===");
    colaImpresion.imprimirSiguiente();

System.out.println();
System.out.println("=== 6. Verificar que es una única instancia y que no se perdió nada ===");
    ColaImpresion cola1 = ColaImpresion.obtenerInstancia();
    ColaImpresion cola2 = ColaImpresion.obtenerInstancia();
    if (cola1 == cola2) {
      System.out.println("cola1 y cola2 son el mismo objeto");
    } else {
      System.out.println("cola1 y cola2 son objetos distintos");
    }
    System.out.println("Documentos en la cola: " + cola1.getTotalEncolados());
    System.out.println("Documentos impresos: " + cola1.getTotalImpresos());
    if (cola1.getTotalEncolados() == cola1.getTotalImpresos()) {
      System.out.println("Resultado: CORRECTO (no se perdió ningún documento)");
    } else {
      System.out.println("Resultado: ERROR (hay documentos perdidos)");
    }
  }
}

// Representa un documento con sus datos (metadatos)
class Documento {

  private int numero;
  private String nombreArchivo;
  private String autor;

  public Documento(int numero, String nombreArchivo, String autor) {
    this.numero = numero;
    this.nombreArchivo = nombreArchivo;
    this.autor = autor;
  }

  public int getNumero() {
    return numero;
  }

  public String getNombreArchivo() {
    return nombreArchivo;
  }

  public String getAutor() {
    return autor;
  }
}

/*
 * Clase Singleton: una única cola de impresión para toda la oficina.
 * La cola y los métodos para encolar y desencolar están solo en esta clase.
 */
class ColaImpresion {

  // Única instancia de la clase
  private static ColaImpresion instancia;

  // La cola es privada: nadie de afuera puede tocarla directamente
  private Queue<Documento> cola;
  private int contadorNumeros;
  private int totalEncolados;
  private int totalImpresos;

  // Constructor privado: no se puede usar "new" desde otras clases
  private ColaImpresion() {
    cola = new LinkedList<Documento>();
    contadorNumeros = 0;
    totalEncolados = 0;
    totalImpresos = 0;
  }

  // Devuelve siempre el mismo objeto
  public static synchronized ColaImpresion obtenerInstancia() {
    if (instancia == null) {
      instancia = new ColaImpresion();
    }
    return instancia;
  }

  // Agrega un documento al final de la cola.
  // Es synchronized para que dos hilos no modifiquen la cola al mismo tiempo.
  public synchronized void encolar(String nombreArchivo, String autor) {
    contadorNumeros++;
    Documento documento = new Documento(contadorNumeros, nombreArchivo, autor);
    cola.add(documento);
    totalEncolados++;
    System.out.println("[Cola] Documento #" + documento.getNumero() + " encolado: "
        + nombreArchivo + " (autor: " + autor + ")");
  }

  // Saca el primer documento de la cola y simula su impresión
  public synchronized void imprimirSiguiente() {
    Documento documento = cola.poll();
    if (documento == null) {
      System.out.println("[Impresora] No hay documentos para imprimir");
    } else {
      System.out.println("[Impresora] Imprimiendo documento #" + documento.getNumero() + ": "
          + documento.getNombreArchivo() + " (autor: " + documento.getAutor() + ")");
      totalImpresos++;
    }
  }

  // Imprime todos los documentos que están en la cola, uno por uno
  public synchronized void imprimirTodos() {
    while (!cola.isEmpty()) {
      imprimirSiguiente();
    }
  }

  public synchronized int getCantidadPendientes() {
    return cola.size();
  }

  public synchronized int getTotalEncolados() {
    return totalEncolados;
  }

  public synchronized int getTotalImpresos() {
    return totalImpresos;
  }
}

// Simula una terminal de usuario que envía documentos a imprimir
class Terminal extends Thread {

  private String autor;
  private int cantidadDocumentos;

  public Terminal(String autor, int cantidadDocumentos) {
    this.autor = autor;
    this.cantidadDocumentos = cantidadDocumentos;
  }

  public void run() {
    ColaImpresion colaImpresion = ColaImpresion.obtenerInstancia();
    for (int i = 1; i <= cantidadDocumentos; i++) {
      colaImpresion.encolar("informe" + i + "_" + autor + ".pdf", autor);
    }
  }
}