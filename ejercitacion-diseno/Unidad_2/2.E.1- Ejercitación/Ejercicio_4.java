import java.util.HashMap;
public class Ejercicio_4 {
 public static void main(String[] args) {
System.out.println("--- 1. Creación de la caché (por primera vez) ---");
System.out.println("Todavía no se pidió la caché, no existe en memoria");
CacheServicio cache = CacheServicio.obtenerInstancia();

System.out.println();
System.out.println("--- 2. Guardar perfiles en la caché ---");
        ModuloPerfiles moduloPerfiles = new ModuloPerfiles();
moduloPerfiles.cargarPerfil("usuario1", "Julieta Benavides- Administradora");
moduloPerfiles.cargarPerfil("usuario2", "Marisol Rivera - Cliente");
moduloPerfiles.cargarPerfil("usuario1", "Otro perfil");

System.out.println();
System.out.println("--- 3. Otro módulo recupera los datos de la misma caché ---");
ModuloSesiones moduloSesiones = new ModuloSesiones();
        moduloSesiones.iniciarSesion("usuario1");
        moduloSesiones.iniciarSesion("usuario9");

System.out.println();
System.out.println("--- 4. Verificar si existe una clave --- ");
System.out.println("¿Existe usuario2? " + cache.existe("usuario2"));
System.out.println("¿Existe usuario9? " + cache.existe("usuario9"));

System.out.println();
System.out.println("--- 5. Actualizar datos ---");
     cache.actualizar("usuario2", "Agustin Corts - Cliente Premium");
      cache.actualizar("usuario9", "Perfil que no existe");
System.out.println("Ahora usuario2 es: " + cache.obtener("usuario2"));

System.out.println();
System.out.println("--- 6. Eliminar datos ---");
    cache.eliminar("usuario1");
     cache.eliminar("usuario1");
System.out.println("Elementos en la caché: " + cache.cantidadElementos());

System.out.println();
System.out.println("--- 7. Verificar que es una única instancia ---");
    CacheServicio cache1 = CacheServicio.obtenerInstancia();
     CacheServicio cache2 = CacheServicio.obtenerInstancia();
    if (cache1 == cache2) {
System.out.println("cache1 y cache2 son el mismo objeto");
} else {
System.out.println("cache1 y cache2 son objetos distintos");
}
System.out.println("Contenido actual de la caché:");
cache1.mostrarContenido();
}
}
//---------------------------------------------------------------------------------------
/*
 * Clase Singleton: una única caché en memoria compartida por toda la aplicación.
 */
class CacheServicio {

    // Única instancia de la clase. Empieza en null: se crea solo cuando se la pide.
    private static CacheServicio instancia;

    // La estructura de datos es privada: nadie de afuera puede tocarla directamente
    private HashMap<String, String> datos;

    // Constructor privado: no se puede usar "new" desde otras clases
    private CacheServicio() {
        datos = new HashMap<String, String>();
        System.out.println("[Cache] Se creó la caché en memoria");
    }

    // Crea la caché la primera vez y después devuelve siempre la misma
    public static synchronized CacheServicio obtenerInstancia() {
        if (instancia == null) {
            instancia = new CacheServicio();
        }
        return instancia;
    }

    // Guarda un elemento nuevo. Si la clave ya existe, no lo pisa.
    public synchronized void guardar(String clave, String valor) {
        if (!claveValida(clave)) {
            return;
        }
        if (datos.containsKey(clave)) {
            System.out.println("[Cache] La clave " + clave + " ya existe, use actualizar()");
        } else {
            datos.put(clave, valor);
            System.out.println("[Cache] Se guardó " + clave);
        }
    }

    // Devuelve el valor de una clave (o null si no existe)
    public synchronized String obtener(String clave) {
        return datos.get(clave);
    }

    // Cambia el valor de una clave que ya existe
    public synchronized void actualizar(String clave, String nuevoValor) {
        if (datos.containsKey(clave)) {
            datos.put(clave, nuevoValor);
            System.out.println("[Cache] Se actualizó " + clave);
        } else {
            System.out.println("[Cache] No se puede actualizar, la clave " + clave + " no existe");
        }
    }

    // Elimina un elemento
    public synchronized void eliminar(String clave) {
        if (datos.containsKey(clave)) {
            datos.remove(clave);
            System.out.println("[Cache] Se eliminó " + clave);
        } else {
            System.out.println("[Cache] No se puede eliminar, la clave " + clave + " no existe");
        }
    }
    // Verifica si una clave está en la caché
    public synchronized boolean existe(String clave) {
        return datos.containsKey(clave);
    }

    public synchronized int cantidadElementos() {
        return datos.size();
    }

    // Muestra todo lo que hay guardado (sin entregar el HashMap a nadie)
    public synchronized void mostrarContenido() {
        for (String clave : datos.keySet()) {
            System.out.println("  " + clave + " -> " + datos.get(clave));
        }
    }

    // Validación interna: la clave no puede ser nula ni estar vacía
    private boolean claveValida(String clave) {
        if (clave == null || clave.trim().equals("")) {
            System.out.println("[Cache] La clave no es válida");
            return false;
        }
        return true;
    }
}

// Módulo que carga perfiles de usuario en la caché
class ModuloPerfiles {
    public void cargarPerfil(String usuario, String perfil) {
        CacheServicio cache = CacheServicio.obtenerInstancia();
        cache.guardar(usuario, perfil);
    }
}

// Módulo que busca primero en la caché antes de consultar al servidor
class ModuloSesiones {
    public void iniciarSesion(String usuario) {
        CacheServicio cache = CacheServicio.obtenerInstancia();
        String perfil = cache.obtener(usuario);
        if (perfil != null) {
            System.out.println("[Sesiones] Perfil encontrado en la caché: " + perfil);
        } else {
            System.out.println("[Sesiones] " + usuario + " no está en la caché, habría que consultar al servidor");
        }
    }
}
