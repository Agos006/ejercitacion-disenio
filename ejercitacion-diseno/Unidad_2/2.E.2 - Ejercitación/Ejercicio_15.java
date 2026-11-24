import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Ejercicio_15 {
public static void main(String[] args) throws InterruptedException {

System.out.println("=== 1. Treinta y cinco puestos piden un lugar al mismo tiempo ===");
System.out.println("Lugares en la sala: " + GestorReservas.CAPACIDAD);
System.out.println("(el gestor todavía no existe: lo crean los propios hilos)");
    int cantidadPuestos = 35;
    PuestoAtencion[] puestos = new PuestoAtencion[cantidadPuestos];
    for (int i = 0; i < cantidadPuestos; i++) {
      puestos[i] = new PuestoAtencion("socio" + (i + 1));
    }
    for (int i = 0; i < cantidadPuestos; i++) {
      puestos[i].start();
    }
    for (int i = 0; i < cantidadPuestos; i++) {
      puestos[i].join();
    }

    GestorReservas gestor = GestorReservas.obtenerInstancia();

    int aceptadas = 0;
    int sinLugares = 0;
    for (int i = 0; i < cantidadPuestos; i++) {
      if (puestos[i].getResultado().equals("ACEPTADA")) {
        aceptadas++;
      } else if (puestos[i].getResultado().equals("SIN_LUGARES")) {
        sinLugares++;
      }
    }
System.out.println("Solicitudes: " + cantidadPuestos);
System.out.println("Reservas aceptadas: " + aceptadas);
System.out.println("Rechazadas por falta de lugares: " + sinLugares);
System.out.println("Reservas activas: " + gestor.getCantidadReservas());
System.out.println("Lugares disponibles: " + gestor.getLugaresDisponibles());
    if (gestor.getCantidadReservas() <= GestorReservas.CAPACIDAD
        && gestor.getLugaresDisponibles() >= 0
        && aceptadas == GestorReservas.CAPACIDAD) {
System.out.println("Resultado: CORRECTO (nunca se pasó de 10 reservas)");
    } else {
System.out.println("Resultado: ERROR (se aceptaron más reservas que lugares)");
    }

System.out.println();
System.out.println("=== 2. Todos los puestos usaron la misma instancia ===");
    boolean todasIguales = true;
    for (int i = 0; i < cantidadPuestos; i++) {
      if (puestos[i].getGestor() != gestor) {
        todasIguales = false;
      }
    }
    if (todasIguales) {
System.out.println("Los 35 puestos y el main usaron el mismo gestor");
    } else {
System.out.println("ERROR: hay gestores distintos");
    }

System.out.println();
System.out.println("=== 3. Reserva duplicada ===");
    String socioConReserva = gestor.getReservasActivas().get(0); System.out.println("Se intenta reservar otra vez para " + socioConReserva + ": " + gestor.reservar(socioConReserva));
System.out.println("Reservas activas: " + gestor.getCantidadReservas());

System.out.println();
System.out.println("=== 4. Reserva con la sala llena ===");
System.out.println("Se intenta reservar para socio100: " + gestor.reservar("socio100"));
System.out.println("Lugares disponibles: " + gestor.getLugaresDisponibles());

System.out.println();
System.out.println("=== 5. Cancelaciones ===");
    boolean cancelada = gestor.cancelar(socioConReserva);
System.out.println("Se cancela la reserva de " + socioConReserva + ": " + cancelada);
System.out.println("Lugares disponibles: " + gestor.getLugaresDisponibles());
System.out.println("Se intenta reservar para socio100: " + gestor.reservar("socio100"));
System.out.println("Lugares disponibles: " + gestor.getLugaresDisponibles());
    boolean inexistente = gestor.cancelar("socio999");
System.out.println("Se cancela una reserva que no existe (socio999): " + inexistente);
  System.out.println("Lugares disponibles (no aumentó): " + gestor.getLugaresDisponibles());

System.out.println();
System.out.println("=== 6. Reservas activas ===");
    List<String> reservas = gestor.getReservasActivas();
System.out.println("Cantidad: " + reservas.size() + " | Lugares disponibles: " + gestor.getLugaresDisponibles());
    for (int i = 0; i < reservas.size(); i++) {
System.out.println("  " + reservas.get(i));
    }
  }
}

//Clase Singleton: un único gestor de reservas para toda la sala de lectura.
 //Todos los métodos que usan los cupos son synchronized, así solo un hilo a la vez puede comprobar la disponibilidad y registrar la reserva.
class GestorReservas {
// Cantidad de lugares de la sala
  public static final int CAPACIDAD = 10;
// Única instancia de la clase
  private static GestorReservas instancia;
// Socios que tienen una reserva activa (un socio no puede estar dos veces)
  private Set<String> reservasActivas;
// Constructor privado: no se puede usar "new" desde otras clases
  private GestorReservas() {
    reservasActivas = new HashSet<String>();
  }
// Devuelve siempre el mismo objeto. Es synchronized para que dos hilos no creen dos gestores al mismo tiempo.
  public static synchronized GestorReservas obtenerInstancia() {
    if (instancia == null) {
      instancia = new GestorReservas();
    }
    return instancia;
  }

  // intenta reservar un lugar. La comprobación y el registro se hacen juntos, sin que otro hilo pueda meterse en el medio (operación atómica).
  // Devuelve: ACEPTADA, DUPLICADA, SIN_LUGARES o SOCIO_INVALIDO.
  public synchronized String reservar(String socio) {
    if (socio == null || socio.trim().equals("")) {
      return "SOCIO_INVALIDO";
    }
    if (reservasActivas.contains(socio)) {
      return "DUPLICADA";
    }
    if (reservasActivas.size() >= CAPACIDAD) {
      return "SIN_LUGARES";
    }
    reservasActivas.add(socio);
    return "ACEPTADA";
  }

  // cancela la reserva de un socio. Devuelve false si no tenía una reserva.
  public synchronized boolean cancelar(String socio) {
    return reservasActivas.remove(socio);
  }
  // los lugares disponibles se calculan a partir de las reservas, así nunca quedan negativos
  public synchronized int getLugaresDisponibles() {
    return CAPACIDAD - reservasActivas.size();
  }
  public synchronized int getCantidadReservas() {
    return reservasActivas.size();
  }
  // devuelve una copia ordenada de las reservas que no se puede modificar
  public synchronized List<String> getReservasActivas() {
    List<String> copia = new ArrayList<String>(reservasActivas);
    Collections.sort(copia);
    return Collections.unmodifiableList(copia);
  }
}
// simula un puesto de atención que recibe la solicitud de un socio
class PuestoAtencion extends Thread {

  private String socio;
  private GestorReservas gestor;
  private String resultado;

  public PuestoAtencion(String socio) {
    this.socio = socio;
    resultado = "";
  }
  public void run() {
    gestor = GestorReservas.obtenerInstancia();
    resultado = gestor.reservar(socio);
  }
  public String getResultado() {
    return resultado;
  }
  public GestorReservas getGestor() {
    return gestor;
  }
}