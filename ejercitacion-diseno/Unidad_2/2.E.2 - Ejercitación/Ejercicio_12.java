import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
public class Ejercicio_12 {
 public static void main(String[] args) {

    SectorPrestamos sectorPrestamos = new SectorPrestamos();
    SectorDevoluciones sectorDevoluciones = new SectorDevoluciones();
    SectorAtencionSocios sectorAtencion = new SectorAtencionSocios();

System.out.println("---- 1. Los tres sectores registran actividades ----");
    sectorPrestamos.prestarLibro("Gonzalo", "Rayuela");
    sectorAtencion.registrarSocio("Franco");
    sectorDevoluciones.devolverLibro("Valentina", "El Aleph");
    sectorPrestamos.prestarLibro("Francisco", "Ficciones");
    sectorAtencion.consultarCatalogo("Valentina");
    sectorDevoluciones.devolverLibro("Valentina", "Rayuela");

    RegistroActividades registro = RegistroActividades.obtenerInstancia();

System.out.println();
System.out.println("---- 2. Historial completo (en orden de incorporación) ----");
    List<Actividad> historial = registro.obtenerHistorial();
    for (int i = 0; i < historial.size(); i++) {
System.out.println((i + 1) + ". " + historial.get(i).texto());
    }

System.out.println();
System.out.println("---- 3. Filtrar actividades por sector ----");
    mostrarSector(registro, "Préstamos");
    mostrarSector(registro, "Devoluciones");
    mostrarSector(registro, "Atención a socios");
    mostrarSector(registro, "Administración");

System.out.println();
System.out.println("---- 4. Cantidad total de eventos ----");
System.out.println("Eventos registrados: " + registro.getCantidadEventos());

System.out.println();
System.out.println("---- 5. La colección interna está protegida ----");
  try {
      historial.remove(0);
System.out.println("ERROR: se pudo borrar una actividad");
  } catch (UnsupportedOperationException e) {
System.out.println("No se puede borrar una actividad desde la consulta");
    }
 try {
      historial.clear();
System.out.println("ERROR: se pudo vaciar el historial");
  } catch (UnsupportedOperationException e) {
System.out.println("No se puede vaciar el historial desde la consulta");
  }
    registro.registrar("", "Actividad sin sector");
    registro.registrar("Préstamos", "   ");
System.out.println("Eventos registrados (no cambió): " + registro.getCantidadEventos());

System.out.println();
System.out.println("=== 6. Verificar que es una única instancia ===");
    RegistroActividades registro1 = RegistroActividades.obtenerInstancia();
    RegistroActividades registro2 = RegistroActividades.obtenerInstancia();
  if (registro1 == registro2) {
System.out.println("registro1 y registro2 son el mismo objeto");
   } else {
System.out.println("registro1 y registro2 son objetos distintos");
   }
  if (sectorPrestamos.getRegistro() == sectorDevoluciones.getRegistro()
        && sectorDevoluciones.getRegistro() == sectorAtencion.getRegistro()) {
System.out.println("Los tres sectores usan el mismo registro");
    } else {
      System.out.println("Los sectores usan registros distintos");
    }
  }
// muestra las actividades de un sector
  private static void mostrarSector(RegistroActividades registro, String sector) {
    List<Actividad> actividades = registro.filtrarPorSector(sector);
System.out.println("Sector " + sector + " (" + actividades.size() + " actividades):");
  for (int i = 0; i < actividades.size(); i++) {
System.out.println("  - " + actividades.get(i).texto());
    }
  }
}

// Una actividad registrada. Es inmutable: sus atributos son final, así que una vez creada no se puede modificar.

final class Actividad {

  private final LocalDateTime fechaHora;
  private final String sector;
  private final String descripcion;

  public Actividad(LocalDateTime fechaHora, String sector, String descripcion) {
    this.fechaHora = fechaHora;
    this.sector = sector;
    this.descripcion = descripcion;
  }

  public LocalDateTime getFechaHora() {
    return fechaHora;
  }

  public String getSector() {
    return sector;
  }

  public String getDescripcion() {
    return descripcion;
  }

  // Devuelve la actividad como texto, con la fecha y la hora separadas
  public String texto() {
    DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    DateTimeFormatter formatoHora = DateTimeFormatter.ofPattern("HH:mm:ss");
    return fechaHora.format(formatoFecha) + " " + fechaHora.format(formatoHora)
        + " | " + sector + " | " + descripcion;
  }
}

/*
 * Clase Singleton: un único registro de actividades compartido por toda la biblioteca.
 */
class RegistroActividades {

  // unica instancia de la clase
  private static RegistroActividades instancia;
 // la lista es privada: nadie de afuera puede modificarla directamente
  private List<Actividad> actividades;

  // Constructor privado: no se puede usar "new" desde otras clases
  private RegistroActividades() {
    actividades = new ArrayList<Actividad>();
  }
  // devuelve siempre el mismo objeto
  public static RegistroActividades obtenerInstancia() {
    if (instancia == null) {
      instancia = new RegistroActividades();
    }
    return instancia;
  }

  // Agrega una actividad al final del historial, con la fecha y hora actuales.
  // Rechaza el sector o la descripción vacíos.
  public void registrar(String sector, String descripcion) {
    if (sector == null || sector.trim().equals("")) {
      System.out.println("[Registro] No se registró: el sector no puede estar vacío");
      return;
    }
    if (descripcion == null || descripcion.trim().equals("")) {
      System.out.println("[Registro] No se registró: la descripción no puede estar vacía");
      return;
    }
    Actividad actividad = new Actividad(LocalDateTime.now(), sector, descripcion);
    actividades.add(actividad);
  }

  // devuelve una copia del historial que no se puede modificar
  public List<Actividad> obtenerHistorial() {
    List<Actividad> copia = new ArrayList<Actividad>(actividades);
    return Collections.unmodifiableList(copia);
  }
  // evuelve solo las actividades de un sector
  public List<Actividad> filtrarPorSector(String sector) {
    List<Actividad> filtradas = new ArrayList<Actividad>();
    for (int i = 0; i < actividades.size(); i++) {
      Actividad actividad = actividades.get(i);
      if (actividad.getSector().equalsIgnoreCase(sector)) {
        filtradas.add(actividad);
      }
    }
    return Collections.unmodifiableList(filtradas);
  }
public int getCantidadEventos() {
  return actividades.size();
  }
}
// sector que registra los préstamos
class SectorPrestamos {
private RegistroActividades registro;
public SectorPrestamos() {
  registro = RegistroActividades.obtenerInstancia();
}
public RegistroActividades getRegistro() {
  return registro;
}
public void prestarLibro(String socio, String libro) {
  registro.registrar("Préstamos", "Se prestó \"" + libro + "\" a " + socio);
}
}
// sector que registra las devoluciones
class SectorDevoluciones {
private RegistroActividades registro;

public SectorDevoluciones() {
  registro = RegistroActividades.obtenerInstancia();
}
public RegistroActividades getRegistro() {
  return registro;
}
public void devolverLibro(String socio, String libro) {
  registro.registrar("Devoluciones", socio + " devolvió \"" + libro + "\"");
}
}
// sector que registra la atención a socios
class SectorAtencionSocios {

  private RegistroActividades registro;
public SectorAtencionSocios() {
  registro = RegistroActividades.obtenerInstancia();
}
public RegistroActividades getRegistro() {
  return registro;
}
public void registrarSocio(String socio) {
  registro.registrar("Atención a socios", "Se inscribió al socio " + socio);
}

public void consultarCatalogo(String socio) {
  registro.registrar("Atención a socios", socio + " consultó el catálogo");
}
}