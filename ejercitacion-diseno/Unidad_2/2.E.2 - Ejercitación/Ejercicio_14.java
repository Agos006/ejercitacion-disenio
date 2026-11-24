import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Ejercicio_14 {
public static void main(String[] args) {

Mostrador mostrador1 = new Mostrador("Mostrador 1");
Mostrador mostrador2 = new Mostrador("Mostrador 2");

System.out.println( "  1. El Mostrador 1 presta un ejemplar ");
mostrador1.prestar("RAY-001", "Ana", LocalDate.of(2026, 11, 24));

System.out.println();
System.out.println("  2. El Mostrador 2 intenta prestar el mismo ejemplar ");
    mostrador2.prestar("RAY-001", "Luis", LocalDate.of(2026, 11, 24));

System.out.println();
System.out.println(" 3. El Mostrador 2 presta otro ejemplar del mismo título ");
    mostrador2.prestar("RAY-002", "Luis", LocalDate.of(2026, 11, 24));
    mostrador2.prestar("FIC-001", "Ana", LocalDate.of(2026, 11, 25));

System.out.println();
System.out.println(" 4. Préstamos activos de cada socio ");
    mostrador1.mostrarPrestamosDe("Ana");
    mostrador1.mostrarPrestamosDe("Luis");
    mostrador2.mostrarPrestamosDe("Marta");

System.out.println();
System.out.println(" 5. Una devolución habilita de nuevo el ejemplar ");
    mostrador2.devolver("RAY-001");
    mostrador1.prestar("RAY-001", "Marta", LocalDate.of(2026, 11, 26));
    mostrador2.mostrarPrestamosDe("Ana");
    mostrador2.mostrarPrestamosDe("Marta");

System.out.println();
System.out.println(" 6. Devoluciones y préstamos que se rechazan ");
    AdministradorPrestamos administrador = AdministradorPrestamos.obtenerInstancia();
System.out.println("Préstamos activos antes: " + administrador.getCantidadPrestamosActivos());
    mostrador1.devolver("RAY-003");
    mostrador1.prestar("RAY-003", "   ", LocalDate.of(2026, 11, 26));
System.out.println("Préstamos activos después: " + administrador.getCantidadPrestamosActivos());
    mostrador1.devolver("RAY-002");
    mostrador2.devolver("RAY-002");
System.out.println("Préstamos activos al final: " + administrador.getCantidadPrestamosActivos());

System.out.println();
System.out.println(" 7. Verificar que es una única instancia ");
    if (mostrador1.getAdministrador() == mostrador2.getAdministrador()) {
System.out.println("Los dos mostradores usan el mismo administrador");
    } else {
System.out.println("Los mostradores usan administradores distintos");
    }
  }
}
 // un préstamo registrado. Es inmutable: sus atributos son final.
final class Prestamo {
private final String idEjemplar;
private final String socio;
private final LocalDate fecha;

public Prestamo(String idEjemplar, String socio, LocalDate fecha) {
    this.idEjemplar = idEjemplar;
    this.socio = socio;
    this.fecha = fecha;
  }
public String getIdEjemplar() {
    return idEjemplar;
  }
public String getSocio() {
    return socio;
  }
public LocalDate getFecha() {
    return fecha;
  }
public String getFechaTexto() {
    return fecha.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
  }
}
//  Singleton: un único administrador de préstamos para todos los mostradores.
 // guarda el estado de cada ejemplar físico (identificado con un código único).
class AdministradorPrestamos {

// unica instancia de la clase
  private static AdministradorPrestamos instancia;

// préstamos activos: la clave es el identificador del ejemplar.
// como las claves no se repiten, un ejemplar no puede tener dos préstamos a la vez.
  private Map<String, Prestamo> prestamosActivos;

// constructor privado: no se puede usar "new" desde otras clases
  private AdministradorPrestamos() {
    prestamosActivos = new LinkedHashMap<String, Prestamo>();
  }

// devuelve siempre el mismo objeto
  public static AdministradorPrestamos obtenerInstancia() {
    if (instancia == null) {
      instancia = new AdministradorPrestamos();
    }
    return instancia;
  }
// registra un préstamo, siempre que el ejemplar no esté prestado
  public void registrarPrestamo(String idEjemplar, String socio, LocalDate fecha) {
    if (idEjemplar == null || idEjemplar.trim().equals("")) {
      System.out.println("[Administrador] Préstamo rechazado: falta el identificador del ejemplar");
      return;
    }
    if (socio == null || socio.trim().equals("")) {
      System.out.println("[Administrador] Préstamo rechazado: falta el nombre del socio");
      return;
    }
    if (fecha == null) {
      System.out.println("[Administrador] Préstamo rechazado: falta la fecha");
      return;
    }
    Prestamo prestamoExistente = prestamosActivos.get(idEjemplar);
    if (prestamoExistente != null) {
      System.out.println("[Administrador] Préstamo rechazado: el ejemplar " + idEjemplar
          + " ya está prestado a " + prestamoExistente.getSocio()
          + " desde el " + prestamoExistente.getFechaTexto());
      return;
    }
    prestamosActivos.put(idEjemplar, new Prestamo(idEjemplar, socio, fecha));
    System.out.println("[Administrador] Préstamo registrado: " + idEjemplar + " a " + socio);
  }

// registra una devolución. Si el ejemplar no está prestado, se rechaza sin cambiar nada.
  public void registrarDevolucion(String idEjemplar) {
    Prestamo prestamo = prestamosActivos.get(idEjemplar);
    if (prestamo == null) {
      System.out.println("[Administrador] Devolución rechazada: el ejemplar " + idEjemplar
          + " no tiene un préstamo activo");
      return;
    }
    prestamosActivos.remove(idEjemplar);
    System.out.println("[Administrador] Devolución registrada: " + idEjemplar
        + " (lo tenía " + prestamo.getSocio() + "). El ejemplar queda disponible");
  }

// devuelve los préstamos activos de un socio
  public List<Prestamo> consultarPrestamosDe(String socio) {
    List<Prestamo> prestamosSocio = new ArrayList<Prestamo>();
    for (Prestamo prestamo : prestamosActivos.values()) {
      if (prestamo.getSocio().equals(socio)) {
        prestamosSocio.add(prestamo);
      }
    }
    return Collections.unmodifiableList(prestamosSocio);
  }

  public int getCantidadPrestamosActivos() {
    return prestamosActivos.size();
  }
}
// mostrador de la biblioteca. Hay dos y los dos usan el mismo administrador.
class Mostrador {
private String nombre;
private AdministradorPrestamos administrador;

  public Mostrador(String nombre) {
    this.nombre = nombre;
    administrador = AdministradorPrestamos.obtenerInstancia();
  }
public AdministradorPrestamos getAdministrador() {
    return administrador;
  }
public void prestar(String idEjemplar, String socio, LocalDate fecha) {
    System.out.println("[" + nombre + "] Prestar " + idEjemplar + " a " + socio);
    administrador.registrarPrestamo(idEjemplar, socio, fecha);
  }
public void devolver(String idEjemplar) {
    System.out.println("[" + nombre + "] Devolver " + idEjemplar);
    administrador.registrarDevolucion(idEjemplar);
}
public void mostrarPrestamosDe(String socio) {
    List<Prestamo> prestamos = administrador.consultarPrestamosDe(socio);
System.out.println("[" + nombre + "] Préstamos activos de " + socio + " (" + prestamos.size() + "):");
    for (int i = 0; i < prestamos.size(); i++) {
 System.out.println("  " + prestamos.get(i).getIdEjemplar() + " desde el " + prestamos.get(i).getFechaTexto());
    }
  }
}