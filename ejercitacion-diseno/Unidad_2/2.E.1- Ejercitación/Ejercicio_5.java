public class Ejercicio_5 {
public static void main(String[] args) {
 System.out.println("=== 1. Obtener el controlador del dispositivo ===");
    ControladorHardware controlador = ControladorHardware.obtenerInstancia();
    System.out.println("Estado inicial: " + controlador.leerEstado());

    System.out.println();
    System.out.println("=== 2. Enviar un comando con el dispositivo desconectado ===");
    ModuloBrazoRobotico moduloBrazo = new ModuloBrazoRobotico();
    ModuloCinta moduloCinta = new ModuloCinta();
    moduloBrazo.moverBrazo();

    System.out.println();
    System.out.println("=== 3. Conectar el dispositivo ===");
    controlador.conectar();
    System.out.println("Estado: " + controlador.leerEstado());

    System.out.println();
    System.out.println("=== 4. El brazo robótico envía un comando ===");
    moduloBrazo.moverBrazo();
    System.out.println("Estado: " + controlador.leerEstado());

    System.out.println();
    System.out.println("=== 5. La cinta intenta enviar un comando mientras el dispositivo está ocupado ===");
    moduloCinta.activarCinta();

    System.out.println();
    System.out.println("=== 6. El brazo termina y la cinta puede enviar su comando ===");
    controlador.finalizarComando();
    moduloCinta.activarCinta();
    controlador.finalizarComando();
    System.out.println("Estado: " + controlador.leerEstado());

    System.out.println();
    System.out.println("=== 7. Verificar que es una única instancia ===");
    ControladorHardware controlador1 = ControladorHardware.obtenerInstancia();
    ControladorHardware controlador2 = ControladorHardware.obtenerInstancia();
    if (controlador1 == controlador2) {
      System.out.println("controlador1 y controlador2 son el mismo objeto");
    } else {
      System.out.println("controlador1 y controlador2 son objetos distintos");
    }
    System.out.println("Comandos enviados en total: " + controlador1.getCantidadComandos());
    controlador1.desconectar();
  }
}
/*
 * Clase Singleton: representa el puerto serial físico, que es único.
 * Es "final" para que nadie pueda heredar de ella y crear otra versión.
 */
final class ControladorHardware {
// Única instancia de la clase
  private static ControladorHardware instancia;

  private String puerto;
  private boolean conectado;
  private boolean ocupado;
  private String comandoActual;
  private int cantidadComandos;
  // Constructor privado: no se puede usar "new" desde otras clases
  private ControladorHardware() {
    puerto = "COM3";
    conectado = false;
    ocupado = false;
    comandoActual = "";
    cantidadComandos = 0;
  }
// Devuelve siempre el mismo objeto
  public static synchronized ControladorHardware obtenerInstancia() {
    if (instancia == null) {
      instancia = new ControladorHardware();
    }
    return instancia;
  }

  // Simula la conexión con el dispositivo
  public synchronized void conectar() {
    if (conectado) {
      System.out.println("[Hardware] El puerto " + puerto + " ya estaba conectado");
    } else {
      conectado = true;
      System.out.println("[Hardware] Dispositivo conectado en el puerto " + puerto);
    }
  }
// Simula la desconexión del dispositivo
  public synchronized void desconectar() {
    conectado = false;
    ocupado = false;
    System.out.println("[Hardware] Dispositivo desconectado");
  }
// Valida si se puede enviar una nueva instrucción tiene que estar conectado y no estar ejecutando otro comando
  public synchronized boolean estaDisponible() {
    return conectado && !ocupado;
  }
// Simula el envío de un comando. Solo se envía si el dispositivo está disponible.
  public synchronized boolean enviarComando(String comando) {
    if (!estaDisponible()) {
      System.out.println("[Hardware] No se pudo enviar \"" + comando + "\": el dispositivo no está disponible");
      return false;
    }
    ocupado = true;
    comandoActual = comando;
    cantidadComandos++;
    System.out.println("[Hardware] Comando enviado al puerto " + puerto + ": " + comando);
    return true;
  }

// Simula que el dispositivo terminó de ejecutar el comando y queda libre
  public synchronized void finalizarComando() {
    if (ocupado) {
      System.out.println("[Hardware] El dispositivo terminó de ejecutar: " + comandoActual);
      ocupado = false;
      comandoActual = "";
    }
  }
 // Lee el estado operacional actual
  public synchronized String leerEstado() {
    if (!conectado) {
      return "DESCONECTADO";
    } else if (ocupado) {
      return "OCUPADO (ejecutando " + comandoActual + ")";
    } else {
      return "LISTO";
    }
  }
public synchronized int getCantidadComandos() {
    return cantidadComandos;
  }
}
// Módulo que controla el brazo robótico
class ModuloBrazoRobotico {
  public void moverBrazo() {
    ControladorHardware controlador = ControladorHardware.obtenerInstancia();
    if (controlador.estaDisponible()) {
      controlador.enviarComando("MOVER_BRAZO 90");
    } else {
      System.out.println("[Brazo] El dispositivo no está disponible, no se envía el comando");
    }
  }
}
// Módulo que controla la cinta transportadora
class ModuloCinta {
  public void activarCinta() {
    ControladorHardware controlador = ControladorHardware.obtenerInstancia();
    if (controlador.estaDisponible()) {
      controlador.enviarComando("ACTIVAR_CINTA");
    } else {
      System.out.println("[Cinta] El dispositivo no está disponible, no se envía el comando");
    }
  }
}