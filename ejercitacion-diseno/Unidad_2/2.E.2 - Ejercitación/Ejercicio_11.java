
public class Ejercicio_11 {

  public static void main(String[] args) {

    ModuloPrestamos moduloPrestamos = new ModuloPrestamos();
    ModuloConsultas moduloConsultas = new ModuloConsultas();

    System.out.println("---- 1. Configuración inicial vista desde los dos módulos ----");
    moduloPrestamos.mostrarConfiguracion();
    moduloConsultas.mostrarConfiguracion();

    System.out.println();
    System.out.println("---- 2. Se modifica la configuración ----");
    ConfiguracionBiblioteca configuracion = ConfiguracionBiblioteca.obtenerInstancia();
    configuracion.setNombreBiblioteca("Biblioteca del ITU");
    configuracion.setMaximoLibrosPorSocio(5);
    configuracion.setPlazoDevolucionDias(20);
    System.out.println("Los dos módulos muestran los valores nuevos:");
    moduloPrestamos.mostrarConfiguracion();
    moduloConsultas.mostrarConfiguracion();

    System.out.println();
    System.out.println("---- 3. Valores inválidos (se rechazan) ----");
    configuracion.setNombreBiblioteca("   ");
    configuracion.setMaximoLibrosPorSocio(0);
    configuracion.setPlazoDevolucionDias(-5);
    System.out.println("La configuración no cambió:");
    moduloConsultas.mostrarConfiguracion();

    System.out.println();
    System.out.println("---- 4. El módulo de préstamos usa la configuración ----");
    moduloPrestamos.registrarPrestamo("Maribel", 4);
    moduloPrestamos.registrarPrestamo("Guadalupe", 5);

    System.out.println();
    System.out.println("---- 5. Verificar que ambos módulos usan el mismo objeto ----");
    ConfiguracionBiblioteca configuracionPrestamos = moduloPrestamos.getConfiguracion();
    ConfiguracionBiblioteca configuracionConsultas = moduloConsultas.getConfiguracion();
    if (configuracionPrestamos == configuracionConsultas) {
      System.out.println("Préstamos y Consultas usan el mismo objeto de configuración");
    } else {
      System.out.println("Préstamos y Consultas usan objetos distintos");
    }
    if (configuracionPrestamos == ConfiguracionBiblioteca.obtenerInstancia()) {
      System.out.println("Y es el mismo que devuelve obtenerInstancia()");
    }
    System.out.println("Referencia Préstamos: " + System.identityHashCode(configuracionPrestamos));
    System.out.println("Referencia Consultas: " + System.identityHashCode(configuracionConsultas));

    System.out.println();
    System.out.println("---- 6. Constructor privado ----");
    System.out.println("No se puede hacer new ConfiguracionBiblioteca() desde otra clase");
  }
}
 // Clase Singleton: guarda la configuración general de la biblioteca.
 // Todos los módulos usan este mismo objeto.

class ConfiguracionBiblioteca {
// unica instancia de la clase
  private static ConfiguracionBiblioteca instancia;
  // atributos privados
  private String nombreBiblioteca;
  private int maximoLibrosPorSocio;
  private int plazoDevolucionDias;
  // constructor privado: no se puede usar "new" desde otras clases
  private ConfiguracionBiblioteca() {
    nombreBiblioteca = "Biblioteca San Martin";
    maximoLibrosPorSocio = 3;
    plazoDevolucionDias = 15;
  }
  // devuelve siempre el mismo objeto
  public static ConfiguracionBiblioteca obtenerInstancia() {
    if (instancia == null) {
      instancia = new ConfiguracionBiblioteca();
    }
    return instancia;
  }
  public String getNombreBiblioteca() {
    return nombreBiblioteca;
  }
  public int getMaximoLibrosPorSocio() {
    return maximoLibrosPorSocio;
  }
  public int getPlazoDevolucionDias() {
    return plazoDevolucionDias;
  }

  // Rechaza nombres vacíos
  public void setNombreBiblioteca(String nombreBiblioteca) {
    if (nombreBiblioteca == null || nombreBiblioteca.trim().equals("")) {
      System.out.println("[Configuración] El nombre no puede estar vacío, se mantiene: " + this.nombreBiblioteca);
    } else {
      this.nombreBiblioteca = nombreBiblioteca;
      System.out.println("[Configuración] Nombre cambiado a: " + nombreBiblioteca);
    }
  }

  // Rechaza valores menores o iguales a cero
  public void setMaximoLibrosPorSocio(int maximoLibrosPorSocio) {
    if (maximoLibrosPorSocio <= 0) {
      System.out.println("[Configuración] El máximo de libros debe ser mayor a cero, se mantiene: " + this.maximoLibrosPorSocio);
    } else {
      this.maximoLibrosPorSocio = maximoLibrosPorSocio;
      System.out.println("[Configuración] Máximo de libros cambiado a: " + maximoLibrosPorSocio);
    }
  }

  // Rechaza valores menores o iguales a cero
  public void setPlazoDevolucionDias(int plazoDevolucionDias) {
    if (plazoDevolucionDias <= 0) {
      System.out.println("[Configuración] El plazo debe ser mayor a cero, se mantiene: " + this.plazoDevolucionDias);
    } else {
      this.plazoDevolucionDias = plazoDevolucionDias;
      System.out.println("[Configuración] Plazo de devolución cambiado a: " + plazoDevolucionDias + " días");
    }
  }
}
// módulo que gestiona los préstamos
class ModuloPrestamos {

  private ConfiguracionBiblioteca configuracion;

  public ModuloPrestamos() {
    configuracion = ConfiguracionBiblioteca.obtenerInstancia();
  }

  public ConfiguracionBiblioteca getConfiguracion() {
    return configuracion;
  }

  public void mostrarConfiguracion() {
    System.out.println("[Préstamos] " + configuracion.getNombreBiblioteca()
        + " | máximo " + configuracion.getMaximoLibrosPorSocio() + " libros"
        + " | plazo " + configuracion.getPlazoDevolucionDias() + " días");
  }

  // Controla que el socio no supere el máximo de libros
  public void registrarPrestamo(String socio, int librosActuales) {
    if (librosActuales < configuracion.getMaximoLibrosPorSocio()) {
      System.out.println("[Préstamos] Préstamo aprobado para " + socio + ". Debe devolverlo en "
          + configuracion.getPlazoDevolucionDias() + " días");
    } else {
      System.out.println("[Préstamos] Préstamo rechazado para " + socio + ": ya tiene "
          + librosActuales + " libros (máximo " + configuracion.getMaximoLibrosPorSocio() + ")");
    }
  }
}
// módulo que atiende las consultas
class ModuloConsultas {

  private ConfiguracionBiblioteca configuracion;

  public ModuloConsultas() {
    configuracion = ConfiguracionBiblioteca.obtenerInstancia();
  }

  public ConfiguracionBiblioteca getConfiguracion() {
    return configuracion;
  }

  public void mostrarConfiguracion() {
    System.out.println("[Consultas] " + configuracion.getNombreBiblioteca()
        + " | máximo " + configuracion.getMaximoLibrosPorSocio() + " libros"
        + " | plazo " + configuracion.getPlazoDevolucionDias() + " días");
  }
}