import java.time.LocalTime;

public class Ejercicio_16 {
public static void main(String[] args) {
System.out.println("1_Se crean los módulos, pero todavía nadie pidió la configuración");
    Recepcion recepcion = new Recepcion();
    Consultorios consultorios = new Consultorios();
System.out.println("Instancias creadas hasta ahora: " + ConfiguracionHospital.getInstanciasCreadas());

System.out.println();
System.out.println("2_Primera consulta: recién ahora se crea la configuración");
    recepcion.mostrarParametros();
System.out.println("Instancias creadas: " + ConfiguracionHospital.getInstanciasCreadas());

System.out.println();
System.out.println("3_ Consultas posteriores: no se crean instancias nuevas");
    consultorios.mostrarParametros();
    recepcion.mostrarParametros();
    ConfiguracionHospital.obtenerInstancia();
System.out.println("Instancias creadas: " + ConfiguracionHospital.getInstanciasCreadas());
    if (recepcion.getConfiguracion() == consultorios.getConfiguracion()) {
System.out.println("Recepción y Consultorios usan el mismo objeto");
    } else {
System.out.println("Recepción y Consultorios usan objetos distintos");
    }

System.out.println();
System.out.println("4_ Se modifican los parámetros");
    ConfiguracionHospital configuracion = ConfiguracionHospital.obtenerInstancia();
    configuracion.setNombreHospital("Hospital Municipal San Martín");
    configuracion.setHorario(LocalTime.of(7, 0), LocalTime.of(20, 0));
    configuracion.setDuracionTurnoMinutos(20);
System.out.println("Los dos modulos muestran los valores nuevos:");
    recepcion.mostrarParametros();
    consultorios.mostrarParametros();
    recepcion.calcularTurnosPorDia();

System.out.println();
System.out.println("5_ Valores inválidos (se rechazan)");
    configuracion.setDuracionTurnoMinutos(0);
    configuracion.setDuracionTurnoMinutos(-10);
    configuracion.setHorario(LocalTime.of(18, 0), LocalTime.of(9, 0));
    configuracion.setHorario(LocalTime.of(8, 0), LocalTime.of(8, 0));
    configuracion.setNombreHospital("   ");
System.out.println("La configuración no cambió:");
    consultorios.mostrarParametros();

System.out.println();
System.out.println(" 6_ Constructor privado");
System.out.println("No se puede hacer new ConfiguracionHospital() desde otra clase");
System.out.println("Instancias creadas en total: " + ConfiguracionHospital.getInstanciasCreadas());
  }
}
 // Singleton con inicialización diferida: la configuración se crea recién la primera vez que alguien la pide.
class ConfiguracionHospital {
// unica instancia de la clase. Empieza en null: todavía no se creó.
  private static ConfiguracionHospital instancia;
// cuenta cuántas veces se construyó la clase (para poder comprobar que es una sola)
private static int instanciasCreadas = 0;
  private String nombreHospital;
  private LocalTime horaApertura;
  private LocalTime horaCierre;
  private int duracionTurnoMinutos;
// constructor privado: no se puede usar "new" desde otras clases
  private ConfiguracionHospital() {
    nombreHospital = "Hospital General";
    horaApertura = LocalTime.of(8, 0);
    horaCierre = LocalTime.of(18, 0);
    duracionTurnoMinutos = 30;
    instanciasCreadas++;
System.out.println("[Configuración] Se creó la configuración del hospital");
  }
// crea la configuración solo la primera vez que se pide.
// despues devuelve la que ya existe.
public static ConfiguracionHospital obtenerInstancia() {
    if (instancia == null) {
      instancia = new ConfiguracionHospital();
    }
    return instancia;
  }
public static int getInstanciasCreadas() {
    return instanciasCreadas;
  }
public String getNombreHospital() {
    return nombreHospital;
  }
 public LocalTime getHoraApertura() {
    return horaApertura;
  }
  public LocalTime getHoraCierre() {
    return horaCierre;
  }
  public int getDuracionTurnoMinutos() {
    return duracionTurnoMinutos;
  }
 // rechaza los nombres vacíos
public void setNombreHospital(String nombreHospital) {
    if (nombreHospital == null || nombreHospital.trim().equals("")) {
System.out.println("[Configuración] El nombre no puede estar vacío, se mantiene: " + this.nombreHospital);
    } else {
      this.nombreHospital = nombreHospital;
System.out.println("[Configuración] Nombre cambiado a: " + nombreHospital);
    }
  }
// el horario de cierre tiene que ser posterior al de apertura
  public void setHorario(LocalTime apertura, LocalTime cierre) {
    if (apertura == null || cierre == null) {
      System.out.println("[Configuración] Falta la hora de apertura o de cierre");
    } else if (!cierre.isAfter(apertura)) {
      System.out.println("[Configuración] El cierre (" + cierre + ") debe ser posterior a la apertura ("
          + apertura + "), se mantiene " + horaApertura + " a " + horaCierre);
    } else {
      horaApertura = apertura;
      horaCierre = cierre;
System.out.println("[Configuración] Horario cambiado a: " + apertura + " a " + cierre);
    }
  }
// la duración tiene que ser mayor a cero
public void setDuracionTurnoMinutos(int duracionTurnoMinutos) {
    if (duracionTurnoMinutos <= 0) {
System.out.println("[Configuración] La duración del turno debe ser positiva, se mantiene: "
          + this.duracionTurnoMinutos + " minutos");
    } else {
      this.duracionTurnoMinutos = duracionTurnoMinutos;
System.out.println("[Configuración] Duración del turno cambiada a: " + duracionTurnoMinutos + " minutos");
    }
  }
}
// módulo de recepción. No guarda la configuración: la pide cada vez que la necesita,por eso la configuración no se crea hasta que se usa por primera vez.
class Recepcion {

  public ConfiguracionHospital getConfiguracion() {
    return ConfiguracionHospital.obtenerInstancia();
  }
  public void mostrarParametros() {
    ConfiguracionHospital configuracion = ConfiguracionHospital.obtenerInstancia();
System.out.println("[Recepción] " + configuracion.getNombreHospital()
        + " | horario " + configuracion.getHoraApertura() + " a " + configuracion.getHoraCierre()
        + " | turnos de " + configuracion.getDuracionTurnoMinutos() + " minutos");
  }
// Calcula cuántos turnos entran en un día con la configuración actual
  public void calcularTurnosPorDia() {
    ConfiguracionHospital configuracion = ConfiguracionHospital.obtenerInstancia();
    int minutosAbierto = (configuracion.getHoraCierre().getHour() * 60 + configuracion.getHoraCierre().getMinute())
        - (configuracion.getHoraApertura().getHour() * 60 + configuracion.getHoraApertura().getMinute());
    int turnos = minutosAbierto / configuracion.getDuracionTurnoMinutos();
System.out.println("[Recepción] Con esta configuración entran " + turnos + " turnos por día");
  }
}
// módulo de consultorios
class Consultorios {
public ConfiguracionHospital getConfiguracion() {
    return ConfiguracionHospital.obtenerInstancia();
  }
public void mostrarParametros() {
    ConfiguracionHospital configuracion = ConfiguracionHospital.obtenerInstancia();
System.out.println("[Consultorios] " + configuracion.getNombreHospital()
        + " | horario " + configuracion.getHoraApertura() + " a " + configuracion.getHoraCierre()
        + " | turnos de " + configuracion.getDuracionTurnoMinutos() + " minutos");
  }
}