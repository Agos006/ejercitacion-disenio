public class Ejercicio_2 {
 public static void main(String[] args) {

System.out.println("=== 1. Abrir la conexión ===");
 GestorConexion gestor = GestorConexion.obtenerInstancia();
   gestor.abrirConexion();

System.out.println();
System.out.println("=== 2. Los módulos usan la misma conexión ===");
ModuloClientes moduloClientes = new ModuloClientes();
ModuloProductos moduloProductos = new ModuloProductos();
        moduloClientes.mostrarClientes();
        moduloProductos.cambiarPrecio();

System.out.println();
System.out.println("=== 3. Verificar que es una única instancia ===");
        GestorConexion gestor1 = GestorConexion.obtenerInstancia();
        GestorConexion gestor2 = GestorConexion.obtenerInstancia();
    if (gestor1 == gestor2) {
System.out.println("gestor1 y gestor2 son el mismo objeto");
    } else {
    System.out.println("gestor1 y gestor2 son objetos distintos");
}

System.out.println();
System.out.println("=== 4. Intentar abrir la conexión otra vez ===");
gestor2.abrirConexion();

System.out.println();
System.out.println("=== 5. Estado de la conexión ===");
System.out.println("Estado: " + gestor.getEstado());
gestor.cerrarConexion();
System.out.println("Estado: " + gestor2.getEstado());
moduloClientes.mostrarClientes();

System.out.println();
System.out.println("Fin del programa");
}
}
/*
 * solo hay un solo gestor y conexion que no es real es simulada
 */
class GestorConexion {

private static GestorConexion instancia;

    private boolean conectada;

    // privado no se puede usar "new" desde otras clases
private GestorConexion() {
conectada = false;
}
 // devuelve siempre el mismo objeto
public static GestorConexion obtenerInstancia() {
if (instancia == null) {
instancia = new GestorConexion();
}
return instancia;
}
// simula abrir la conexión con la base de datos
public void abrirConexion() {
if (conectada) {
System.out.println("La conexión ya estaba abierta, se usa la misma");
} else {
conectada = true;
System.out.println("Conexión abierta con la base de datos");
}
}
 // simula cerrar la conexión
 public void cerrarConexion() {
    if (conectada) {
    conectada = false;
System.out.println("Conexión cerrada");
} else {
System.out.println("No hay ninguna conexión abierta");
}
}
// Simula una consulta (SELECT)
public void ejecutarConsulta(String consulta) {
    if (conectada) {
System.out.println("La consulta fue: " + consulta);
    } else {
System.out.println("No se puede ejecutar la consulta, la conexión está cerrada");
}
}
//simula una actualización (INSERT, UPDATE o DELETE)
public void ejecutarActualizacion(String actualizacion) {
    if (conectada) {
System.out.println("Actualización ejecutada: " + actualizacion);
} else {
System.out.println("No se puede ejecutar la actualización, la conexión está cerrada");
}
}
 // devuelve el estado de la conexión como texto
public String getEstado() {
    if (conectada) {
return "CONECTADA";
} else {
return "CERRADA";
}
}
}
// módulo que trabaja con los clientes
class ModuloClientes {
    public void mostrarClientes() {
        GestorConexion gestor = GestorConexion.obtenerInstancia();
        gestor.ejecutarConsulta("SELECT * FROM clientes");
    }
}
// módulo que trabaja con los productos
class ModuloProductos {
    public void cambiarPrecio() {
        GestorConexion gestor = GestorConexion.obtenerInstancia();
        gestor.ejecutarActualizacion("UPDATE productos SET precio = 1500 WHERE id = 7");
    }
}