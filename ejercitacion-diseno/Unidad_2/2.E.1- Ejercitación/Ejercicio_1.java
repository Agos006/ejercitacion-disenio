import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Properties;

public class Ejercicio_1 {
public static void main(String[] args) {
String rutaArchivo = "config.properties";
  try {
System.out.println("=== 1. Carga inicial de la configuración ===");
ConfigurationManager configuracion = ConfigurationManager.obtenerInstancia();
configuracion.cargar(rutaArchivo);
System.out.println("Archivo cargado: " + rutaArchivo);

System.out.println();
System.out.println("=== 2. Consulta de parámetros desde distintos componentes ===");
ComponenteBaseDatos baseDatos = new ComponenteBaseDatos();
ComponenteCorreo correo = new ComponenteCorreo();
baseDatos.conectar();
correo.enviar();

System.out.println();
System.out.println("=== 3. Verificación de instancia única ===");
ConfigurationManager configuracionA = ConfigurationManager.obtenerInstancia();
ConfigurationManager configuracionB = ConfigurationManager.obtenerInstancia();
System.out.println("Referencia A: " + System.identityHashCode(configuracionA));
System.out.println("Referencia B: " + System.identityHashCode(configuracionB));
    if (configuracionA == configuracionB) {
System.out.println("¿Misma instancia? true");
    } else {
System.out.println("¿Misma instancia? false");
}

System.out.println();
System.out.println("=== 4. Segunda carga (se ignora) ===");
configuracionA.cargar(rutaArchivo);
System.out.println("La segunda llamada a cargar() no volvió a leer el archivo");

System.out.println();
    System.out.println("=== 5. Clave que no existe ===");
System.out.println("Sin valor por defecto: " + configuracion.obtener("clave.inexistente"));
    System.out.println("Con valor por defecto: " + configuracion.obtener("clave.inexistente", "N/A"));

System.out.println();
System.out.println("=== 6. Prueba con varios hilos ===");
probarConHilos();

System.out.println();
System.out.println("=== 7. Constructor privado ===");
System.out.println("No se puede hacer new ConfigurationManager() desde otra clase");
System.out.println("Fin del programa");
    } catch (IOException e) {
System.out.println("Error al leer el archivo de configuración: " + e.getMessage());
    } catch (InterruptedException e) {
System.out.println("Error en la prueba con hilos: " + e.getMessage());
}
}
 // Crea varios hilos y verifica que todos obtengan la misma instancia
    private static void probarConHilos() throws InterruptedException {
        int cantidadHilos = 10;
        ConfigurationManager[] instancias = new ConfigurationManager[cantidadHilos];
        Thread[] hilos = new Thread[cantidadHilos];

        for (int i = 0; i < cantidadHilos; i++) {
            final int posicion = i;
            hilos[i] = new Thread(() -> {
                instancias[posicion] = ConfigurationManager.obtenerInstancia();
            });
            hilos[i].start();
        }

        for (int i = 0; i < cantidadHilos; i++) {
            hilos[i].join();
        }

        boolean todasIguales = true;
        for (int i = 1; i < cantidadHilos; i++) {
            if (instancias[i] != instancias[0]) {
                todasIguales = false;
            }
        }

        System.out.println("Se probaron " + cantidadHilos + " hilos");
        if (todasIguales) {
            System.out.println("Resultado: CORRECTO (todos obtuvieron la misma instancia)");
        } else {
            System.out.println("Resultado: ERROR (hay instancias distintas)");
        }
    }
}

/*
 * Clase Singleton: solo puede existir un objeto de esta clase
 * y se accede a él con obtenerInstancia().
 */
class ConfigurationManager {

    // Única instancia de la clase (empieza en null)
    private static ConfigurationManager instancia;

    private Properties propiedades;
    private boolean cargado;

    // Constructor privado: impide crear objetos con "new" desde afuera
    private ConfigurationManager() {
        propiedades = new Properties();
        cargado = false;
    }

    // Método estático que devuelve siempre el mismo objeto
    public static synchronized ConfigurationManager obtenerInstancia() {
        if (instancia == null) {
            instancia = new ConfigurationManager();
        }
        return instancia;
    }

    // Carga el archivo una sola vez. Si ya estaba cargado, no hace nada.
    public synchronized void cargar(String rutaArchivo) throws IOException {
        if (cargado) {
            return;
        }

        File archivo = new File(rutaArchivo);

        // Si el archivo no existe, se crea con valores por defecto
        if (!archivo.exists()) {
            System.out.println("No se encontró " + archivo.getAbsolutePath()
                    + ", se crea con valores por defecto");
            crearArchivoPorDefecto(archivo);
        }

        FileInputStream lector = new FileInputStream(archivo);
        try {
            propiedades.load(lector);
        } finally {
            lector.close();
        }
        cargado = true;
    }

    // Devuelve el valor de una clave (o null si no existe)
    public String obtener(String clave) {
        verificarCarga();
        return propiedades.getProperty(clave);
    }

    // Devuelve el valor de una clave (o el valor por defecto si no existe)
    public String obtener(String clave, String valorPorDefecto) {
        verificarCarga();
        return propiedades.getProperty(clave, valorPorDefecto);
    }

    // Si se consulta antes de cargar, avisa con un error claro
    private void verificarCarga() {
        if (!cargado) {
            throw new IllegalStateException("Primero hay que llamar a cargar()");
        }
    }

    private void crearArchivoPorDefecto(File archivo) throws IOException {
        PrintWriter escritor = new PrintWriter(archivo, "UTF-8");
        try {
            escritor.println("db.url=jdbc:mysql://localhost:3306/miapp");
            escritor.println("db.user=admin");
            escritor.println("mail.host=smtp.ejemplo.com");
            escritor.println("app.nombre=MiAplicacion");
        } finally {
            escritor.close();
        }
    }
}

// Componente que usa la configuración para conectarse a la base de datos
class ComponenteBaseDatos {
    public void conectar() {
        ConfigurationManager configuracion = ConfigurationManager.obtenerInstancia();
        String url = configuracion.obtener("db.url");
        String usuario = configuracion.obtener("db.user");
        System.out.println("[BaseDatos] Conectando a " + url + " con el usuario " + usuario);
    }
}

// Componente que usa la configuración para enviar correos
class ComponenteCorreo {
    public void enviar() {
        ConfigurationManager configuracion = ConfigurationManager.obtenerInstancia();
        String servidor = configuracion.obtener("mail.host", "localhost");
        System.out.println("[Correo] Enviando mensaje por el servidor " + servidor);
    }
}
