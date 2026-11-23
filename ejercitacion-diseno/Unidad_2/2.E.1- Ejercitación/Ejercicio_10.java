import java.lang.reflect.Constructor;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Ejercicio_10 {
public static void main(String[] args) throws InterruptedException {
  AuditorSeguridad auditor = AuditorSeguridad.obtenerInstancia();

System.out.println("=== 1. Los módulos registran acciones críticas ===");
    ModuloTransferencias moduloTransferencias = new ModuloTransferencias();
    ModuloPrivilegios moduloPrivilegios = new ModuloPrivilegios();
    moduloTransferencias.transferir("SES-001", "ana", 50000);
    moduloPrivilegios.cambiarPrivilegio("SES-002", "luis", "ADMINISTRADOR");
    moduloTransferencias.transferir("SES-001", "ana", 1200);

System.out.println();
System.out.println("=== 2. Cantidad de registros y traza completa ===");
 System.out.println("Cantidad de registros: " + auditor.getCantidadRegistros());
 List<RegistroAuditoria> traza = auditor.obtenerTraza();
    for (int i = 0; i < traza.size(); i++) {
System.out.println(traza.get(i).descripcion());
    }

System.out.println();
System.out.println("=== 3. La traza no se puede modificar ===");
  try {
traza.remove(0);
System.out.println("ERROR: se pudo borrar un registro");
} catch (UnsupportedOperationException e) {
System.out.println("No se puede borrar un registro de la traza");
    }
    try {
traza.add(new RegistroAuditoria(99, "SES-999", "intruso", "ACCION FALSA", LocalDateTime.now()));
System.out.println("ERROR: se pudo agregar un registro");
 } catch (UnsupportedOperationException e) {
System.out.println("No se puede agregar un registro desde afuera");
}
System.out.println("Cantidad de registros: " + auditor.getCantidadRegistros());

System.out.println();
System.out.println("=== 4. Cuatro operadores registran eventos al mismo tiempo ===");
    OperadorHilo operador1 = new OperadorHilo("SES-101", "marta", 250);
    OperadorHilo operador2 = new OperadorHilo("SES-102", "pedro", 250);
    OperadorHilo operador3 = new OperadorHilo("SES-103", "sofia", 250);
    OperadorHilo operador4 = new OperadorHilo("SES-104", "tomas", 250);
    operador1.start();
    operador2.start();
    operador3.start();
    operador4.start();
    operador1.join();
    operador2.join();
    operador3.join();
    operador4.join();
System.out.println("Registros esperados: 1003");
System.out.println("Registros guardados: " + auditor.getCantidadRegistros());
 if (auditor.verificarIntegridad()) {
System.out.println("Resultado: CORRECTO (numeración continua y orden cronológico)");
 } else {
System.out.println("Resultado: ERROR (la traza tiene problemas)");
}

System.out.println();
System.out.println("=== 5. Protección contra la duplicación ===");
    AuditorSeguridad auditor1 = AuditorSeguridad.obtenerInstancia();
    AuditorSeguridad auditor2 = AuditorSeguridad.obtenerInstancia();
 if (auditor1 == auditor2) {
System.out.println("auditor1 y auditor2 son el mismo objeto");
 } else {
System.out.println("auditor1 y auditor2 son objetos distintos");
 }
    intentarCrearConReflexion();
System.out.println("Cantidad de registros: " + auditor1.getCantidadRegistros());
  }

  // intenta saltarse el constructor privado usando reflexión
  private static void intentarCrearConReflexion() {
    try {
      Constructor<AuditorSeguridad> constructor = AuditorSeguridad.class.getDeclaredConstructor();
      constructor.setAccessible(true);
      constructor.newInstance();
      System.out.println("ERROR: se pudo crear un segundo auditor");
    } catch (Exception e) {
      Throwable causa = e.getCause();
      if (causa != null) {
        System.out.println("Intento por reflexión bloqueado: " + causa.getMessage());
      } else {
        System.out.println("Intento por reflexión bloqueado");
      }
    }
  }
}

// registro de auditoría. Es inmutable: todos sus atributos son final y no tiene métodos para modificarlos, así que una vez creado no puede cambiar.

final class RegistroAuditoria {

  private final int numero;
  private final String idSesion;
  private final String operador;
  private final String accion;
  private final LocalDateTime fechaHora;

  public RegistroAuditoria(int numero, String idSesion, String operador, String accion, LocalDateTime fechaHora) {
    this.numero = numero;
    this.idSesion = idSesion;
    this.operador = operador;
    this.accion = accion;
    this.fechaHora = fechaHora;
  }

  public int getNumero() {
    return numero;
  }

  public LocalDateTime getFechaHora() {
    return fechaHora;
  }

  public String descripcion() {
    DateTimeFormatter formato = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    return "#" + numero + " [" + fechaHora.format(formato) + "] sesión " + idSesion
        + " | operador: " + operador + " | " + accion;
  }
}

 // Clase Singleton: el único auditor de seguridad de la aplicación. Es final (no se puede heredar) y no implementa Serializable ni Cloneable, así que tampoco se puede duplicar copiándola.
 
final class AuditorSeguridad {

  // unica instancia de la clase. Se crea al cargar la clase (es segura para varios hilos)
  private static final AuditorSeguridad instancia = new AuditorSeguridad();

  private List<RegistroAuditoria> traza;
  private int contadorRegistros;
  // cerrojo de lectura/escritura: varios hilos pueden leer a la vez,pero solo uno a la vez puede escribir.
  private ReentrantReadWriteLock cerrojo;

  // constructor privado con protección contra reflexión: si ya existe una instancia, lanza un error en vez de crear otra.
  private AuditorSeguridad() {
    if (instancia != null) {
      throw new IllegalStateException("Ya existe un auditor, no se puede crear otro");
    }
    traza = new ArrayList<RegistroAuditoria>();
    contadorRegistros = 0;
    cerrojo = new ReentrantReadWriteLock();
  }

  // devuelve siempre el mismo objeto
  public static AuditorSeguridad obtenerInstancia() {
    return instancia;
  }
  // agrega un evento al final de la traza.
  // El número y la fecha se asignan dentro del bloqueo de escritura,por eso el orden cronológico es estricto.
  public void registrarEvento(String idSesion, String operador, String accion) {
    cerrojo.writeLock().lock();
    try {
      contadorRegistros++;
      RegistroAuditoria registro = new RegistroAuditoria(contadorRegistros, idSesion, operador, accion, LocalDateTime.now());
      traza.add(registro);
    } finally {
      cerrojo.writeLock().unlock();
    }
  }

  public int getCantidadRegistros() {
    cerrojo.readLock().lock();
    try {
      return traza.size();
    } finally {
      cerrojo.readLock().unlock();
    }
  }
  // devuelve una copia de la traza que no se puede modificar
  public List<RegistroAuditoria> obtenerTraza() {
    cerrojo.readLock().lock();
    try {
      List<RegistroAuditoria> copia = new ArrayList<RegistroAuditoria>(traza);
      return Collections.unmodifiableList(copia);
    } finally {
      cerrojo.readLock().unlock();
    }
  }

  // comprueba que los números sean consecutivos (1, 2, 3...) y que ninguna fecha sea anterior a la del registro previo
  public boolean verificarIntegridad() {
    cerrojo.readLock().lock();
    try {
      for (int i = 0; i < traza.size(); i++) {
        RegistroAuditoria actual = traza.get(i);
        if (actual.getNumero() != i + 1) {
          return false;
        }
        if (i > 0) {
          RegistroAuditoria anterior = traza.get(i - 1);
          if (actual.getFechaHora().isBefore(anterior.getFechaHora())) {
            return false;
          }
        }
      }
      return true;
    } finally {
      cerrojo.readLock().unlock();
    }
  }
}
// módulo de transferencias: cada transferencia queda auditada
class ModuloTransferencias {
  public void transferir(String idSesion, String operador, int monto) {
    AuditorSeguridad auditor = AuditorSeguridad.obtenerInstancia();
    auditor.registrarEvento(idSesion, operador, "TRANSFERENCIA de $" + monto);
    System.out.println("[Transferencias] " + operador + " realizó una transferencia de $" + monto);
  }
}
// módulo de privilegios: cada cambio de privilegios queda auditado
class ModuloPrivilegios {
  public void cambiarPrivilegio(String idSesion, String operador, String nuevoRol) {
    AuditorSeguridad auditor = AuditorSeguridad.obtenerInstancia();
    auditor.registrarEvento(idSesion, operador, "CAMBIO DE PRIVILEGIO a " + nuevoRol);
    System.out.println("[Privilegios] " + operador + " cambió su privilegio a " + nuevoRol);
  }
}
// simula un operador que realiza muchas acciones desde su sesión
class OperadorHilo extends Thread {

  private String idSesion;
  private String operador;
  private int cantidadAcciones;

  public OperadorHilo(String idSesion, String operador, int cantidadAcciones) {
    this.idSesion = idSesion;
    this.operador = operador;
    this.cantidadAcciones = cantidadAcciones;
  }

  public void run() {
    AuditorSeguridad auditor = AuditorSeguridad.obtenerInstancia();
    for (int i = 1; i <= cantidadAcciones; i++) {
      auditor.registrarEvento(idSesion, operador, "ACCION CRITICA " + i);
    }
  }
}