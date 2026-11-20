import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Ejercicio_3 {

public static void main(String[] args) {
File archivoAnterior = new File("registro.log");
archivoAnterior.delete();
System.out.println("=== 1. Uso del logger desde el main ===");
    Logger logger = Logger.obtenerInstancia();
    logger.info("La aplicación se inició correctamente");

System.out.println();
System.out.println("=== 2. Distintos módulos usan el mismo logger ===");
ModuloUsuarios moduloUsuarios = new ModuloUsuarios();
ModuloPagos moduloPagos = new ModuloPagos();
moduloUsuarios.iniciarSesion("agos");
moduloPagos.realizarPago(5000);
moduloPagos.realizarPago(15000);
moduloPagos.realizarPago(-200);

System.out.println();
System.out.println("=== 3. Verificar que es una única instancia ===");
Logger logger1 = Logger.obtenerInstancia();
Logger logger2 = Logger.obtenerInstancia();
    if (logger1 == logger2) {
System.out.println("logger1 y logger2 son el mismo objeto");
    } else {
System.out.println("logger1 y logger2 son objetos distintos");
}
logger2.info("La aplicación finalizó");
    System.out.println();
    System.out.println("=== 4. Contenido del archivo registro.log ===");
mostrarArchivo("registro.log");
}
 // se lee el archivo y lo muestra por pantalla
private static void mostrarArchivo(String rutaArchivo) {
try {
BufferedReader lector = new BufferedReader(new FileReader(rutaArchivo));
String linea = lector.readLine();
    while (linea != null) {
System.out.println(linea);
linea = lector.readLine();
}
lector.close();
} catch (IOException e) {
System.out.println("No se pudo leer el archivo: " + e.getMessage());
}
}
}
 //Clase Singleton: un único logger que escribe todos los mensajes en la consola y en el archivo registro.log.//
class Logger {
    private static Logger instancia;
private String archivoLog;
 // Constructor privado: no se puede usar "new" desde otras clases
 private Logger() {
     archivoLog = "registro.log";
}
// va a devvolver siempre el mismo objeto
  public static synchronized Logger obtenerInstancia() {
        if (instancia == null) {
instancia = new Logger();}
    return instancia;
 }
// registra un mensaje informativo
 public void info(String mensaje) {
    registrar("INFO", mensaje);
    }
 // registra una advertencia
 public void warning(String mensaje) {
   registrar("WARNING", mensaje);
 }

// registra un error
    public void error(String mensaje) {
  registrar("ERROR", mensaje);
 }
// Arma la línea con la fecha y hora, y la escribe.
// Es synchronized para que dos hilos no escriban al mismo tiempo.
    private synchronized void registrar(String nivel, String mensaje) {
        LocalDateTime ahora = LocalDateTime.now();
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String fechaHora = ahora.format(formato);

        String linea = "[" + fechaHora + "] [" + nivel + "] " + mensaje;

        // Se muestra por consola
        System.out.println(linea);

 // Se guarda en el archivo (true = agrega al final sin borrar lo anterior)
        try {
            FileWriter escritor = new FileWriter(archivoLog, true);
            escritor.write(linea + "\n");
            escritor.close();
        } catch (IOException e) {
            System.out.println("No se pudo escribir en el archivo: " + e.getMessage());
        }
    }
}

// módulo que trabaja con los usuarios
class ModuloUsuarios {
    public void iniciarSesion(String usuario) {
        Logger logger = Logger.obtenerInstancia();
        logger.info("El usuario " + usuario + " inició sesión");
    }
}

// módulo que trabaja con los pagos
class ModuloPagos {
    public void realizarPago(int monto) {
        Logger logger = Logger.obtenerInstancia();
        if (monto > 10000) {
            logger.warning("Pago de monto muy alto: $" + monto);
        } else if (monto > 0) {
            logger.info("Pago realizado por $" + monto);
        } else {
            logger.error("Monto inválido: " + monto);
        }
    }
}