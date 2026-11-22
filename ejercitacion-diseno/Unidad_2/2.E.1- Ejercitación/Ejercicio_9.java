import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
public class Ejercicio_9 {

public static void main(String[] args) throws InterruptedException {
ColectorMetricas colector = ColectorMetricas.obtenerInstancia();

System.out.println("=== 1. Distintos módulos registran métricas ===");
    ModuloLogin moduloLogin = new ModuloLogin();
    ModuloPedidos moduloPedidos = new ModuloPedidos();
    moduloLogin.iniciarSesion(40);
    moduloLogin.iniciarSesion(60);
    moduloPedidos.crearPedido(120);
System.out.println("Métricas registradas: " + colector.getCantidadMetricas());

System.out.println();
System.out.println("=== 2. Exportar el resumen de las métricas ===");
System.out.println(colector.exportarResumen());

System.out.println();
System.out.println("=== 3. Cuatro interceptores registran métricas al mismo tiempo ===");
    Interceptor interceptor1 = new Interceptor(1000);
    Interceptor interceptor2 = new Interceptor(1000);
    Interceptor interceptor3 = new Interceptor(1000);
    Interceptor interceptor4 = new Interceptor(1000);
    interceptor1.start();
    interceptor2.start();
    interceptor3.start();
    interceptor4.start();
    interceptor1.join();
    interceptor2.join();
    interceptor3.join();
    interceptor4.join();
System.out.println("Peticiones esperadas: 4000");
System.out.println("Peticiones registradas: " + colector.getContador("peticiones_http"));
if (colector.getContador("peticiones_http") == 4000) {
      System.out.println("Resultado: CORRECTO (no se perdió ningún dato)");
 } else {
System.out.println("Resultado: ERROR (se perdieron datos)");
 }

System.out.println();
System.out.println("=== 4. Control de capacidad: se alcanzó el límite de métricas ===");
System.out.println("Límite de métricas: " + ColectorMetricas.LIMITE_METRICAS);
    colector.incrementarContador("metrica_extra");
    colector.registrarLatencia("latencia_extra", 15);
System.out.println("Métricas registradas: " + colector.getCantidadMetricas());
    colector.incrementarContador("logins");
System.out.println("Las métricas que ya existían siguen funcionando: logins = " + colector.getContador("logins"));

System.out.println();
System.out.println("=== 5. Limpiar las métricas ===");
    colector.limpiarMetricas();
System.out.println("Métricas registradas: " + colector.getCantidadMetricas());
    colector.incrementarContador("metrica_extra");
System.out.println("Ahora sí se puede registrar una nueva: " + colector.getCantidadMetricas() + " métrica");

System.out.println();
System.out.println("=== 6. Verificar que es una única instancia ===");
    ColectorMetricas colector1 = ColectorMetricas.obtenerInstancia();
    ColectorMetricas colector2 = ColectorMetricas.obtenerInstancia();
if (colector1 == colector2) {
System.out.println("colector1 y colector2 son el mismo objeto");
} else {
System.out.println("colector1 y colector2 son objetos distintos");
    }
  }
}

// guarda los datos de una métrica de latencia (sin guardar cada medición, para ahorrar memoria)
class MetricaLatencia {

  private long cantidad;
  private long total;
  private long minimo;
  private long maximo;

  public MetricaLatencia() {
    cantidad = 0;
    total = 0;
    minimo = Long.MAX_VALUE;
    maximo = 0;
  }
  // synchronized: solo bloquea esta métrica, no todo el colector
  public synchronized void registrar(long milisegundos) {
    cantidad++;
    total = total + milisegundos;
    if (milisegundos < minimo) {
      minimo = milisegundos;
    }
    if (milisegundos > maximo) {
      maximo = milisegundos;
    }
  }

  public synchronized String getResumen() {
    long promedio = total / cantidad;
    return "cantidad " + cantidad + ", promedio " + promedio + " ms, mínimo " + minimo + " ms, máximo " + maximo + " ms";
  }
}


 //Singleton: un único colector de métricas para toda la aplicación.

class ColectorMetricas {

  // límite de métricas distintas (es chico para poder probarlo en el ejercicio)
  public static final int LIMITE_METRICAS = 6;

  // unica instancia de la clase. Se crea al cargar la clase, así obtenerInstancia()
  // no necesita bloquear a los hilos cada vez que se la pide.
  private static final ColectorMetricas instancia = new ColectorMetricas();

  // estructuras seguras para varios hilos (no hace falta bloquear todo el colector)
  private ConcurrentHashMap<String, AtomicLong> contadores;
  private ConcurrentHashMap<String, MetricaLatencia> latencias;

  // se usa solo para crear métricas nuevas, así no se pasa del límite
  private Object candado;

  // constructor privado: no se puede usar "new" desde otras clases
  private ColectorMetricas() {
    contadores = new ConcurrentHashMap<String, AtomicLong>();
    latencias = new ConcurrentHashMap<String, MetricaLatencia>();
    candado = new Object();
  }

  // devuelve siempre el mismo objeto
  public static ColectorMetricas obtenerInstancia() {
    return instancia;
  }

  //suma 1 a un contador. Si el contador no existe, se crea.
  public void incrementarContador(String nombre) {
    AtomicLong contador = contadores.get(nombre);
    if (contador == null) {
      contador = crearContador(nombre);
    }
    if (contador != null) {
      contador.incrementAndGet();
    }
  }
  // registra el tiempo de una operación (en milisegundos)
  public void registrarLatencia(String nombre, long milisegundos) {
    MetricaLatencia latencia = latencias.get(nombre);
    if (latencia == null) {
      latencia = crearLatencia(nombre);
    }
    if (latencia != null) {
      latencia.registrar(milisegundos);
    }
  }
  // devuelve un resumen con todas las métricas acumuladas hasta el momento
  public String exportarResumen() {
    String resumen = "----- Resumen de métricas -----\n";
    resumen = resumen + "Contadores:\n";
    Map<String, AtomicLong> contadoresOrdenados = new TreeMap<String, AtomicLong>(contadores);
    for (String nombre : contadoresOrdenados.keySet()) {
      resumen = resumen + "  " + nombre + " = " + contadoresOrdenados.get(nombre).get() + "\n";
    }
    resumen = resumen + "Latencias:\n";
    Map<String, MetricaLatencia> latenciasOrdenadas = new TreeMap<String, MetricaLatencia>(latencias);
    for (String nombre : latenciasOrdenadas.keySet()) {
      resumen = resumen + "  " + nombre + " -> " + latenciasOrdenadas.get(nombre).getResumen() + "\n";
    }
    resumen = resumen + "-------------------------------";
    return resumen;
  }
  // borra todas las métricas para liberar memoria
  public void limpiarMetricas() {
    synchronized (candado) {
      contadores.clear();
      latencias.clear();
    }
    System.out.println("[Colector] Se limpiaron las métricas");
  }

  public long getContador(String nombre) {
    AtomicLong contador = contadores.get(nombre);
    if (contador == null) {
      return 0;
    }
    return contador.get();
  }

  public int getCantidadMetricas() {
    return contadores.size() + latencias.size();
  }
  // crea un contador nuevo, solo si no se alcanzó el límite
  private AtomicLong crearContador(String nombre) {
    synchronized (candado) {
      AtomicLong contador = contadores.get(nombre);
      if (contador == null) {
        if (getCantidadMetricas() >= LIMITE_METRICAS) {
          System.out.println("[Colector] Límite alcanzado, se descarta la métrica " + nombre);
          return null;
        }
        contador = new AtomicLong(0);
        contadores.put(nombre, contador);
      }
      return contador;
    }
  }
  // crea una métrica de latencia nueva, solo si no se alcanzó el límite
  private MetricaLatencia crearLatencia(String nombre) {
    synchronized (candado) {
      MetricaLatencia latencia = latencias.get(nombre);
      if (latencia == null) {
        if (getCantidadMetricas() >= LIMITE_METRICAS) {
          System.out.println("[Colector] Límite alcanzado, se descarta la métrica " + nombre);
          return null;
        }
        latencia = new MetricaLatencia();
        latencias.put(nombre, latencia);
      }
      return latencia;
    }
  }
}

// módulo de login: registra cuántos inicios de sesión hubo y cuánto tardaron
class ModuloLogin {
  public void iniciarSesion(long tiempo) {
    ColectorMetricas colector = ColectorMetricas.obtenerInstancia();
    colector.incrementarContador("logins");
    colector.registrarLatencia("latencia_login", tiempo);
  }
}

// módulo de pedidos: registra cuántos pedidos se crearon y cuánto tardaron
class ModuloPedidos {
  public void crearPedido(long tiempo) {
    ColectorMetricas colector = ColectorMetricas.obtenerInstancia();
    colector.incrementarContador("pedidos");
    colector.registrarLatencia("latencia_pedidos", tiempo);
  }
}
// simula un interceptor de red que registra muchas peticiones
class Interceptor extends Thread {

  private int cantidadPeticiones;

  public Interceptor(int cantidadPeticiones) {
    this.cantidadPeticiones = cantidadPeticiones;
  }

  public void run() {
    ColectorMetricas colector = ColectorMetricas.obtenerInstancia();
    for (int i = 0; i < cantidadPeticiones; i++) {
      colector.incrementarContador("peticiones_http");
      colector.registrarLatencia("latencia_http", 10 + (i % 5) * 10);
    }
  }
}