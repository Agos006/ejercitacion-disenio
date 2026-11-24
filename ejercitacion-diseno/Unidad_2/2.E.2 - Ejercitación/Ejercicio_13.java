import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
public class Ejercicio_13 {
public static void main(String[] args) {
   PantallaPersonal pantallaPersonal = new PantallaPersonal();
   PantallaConsulta pantallaConsulta = new PantallaConsulta();
System.out.println("   1. Catálogo inicial ");
    pantallaConsulta.mostrarCatalogo();
System.out.println();
System.out.println(" 2. El personal agrega libros (la consulta los ve sin recargar) ");
    pantallaPersonal.agregarLibro("L001", "Rayuela", "Julio Cortázar", 3);
    pantallaPersonal.agregarLibro("L002", "Don Quijote de la Mancha", "Miguel de Cervantes", 2);
    pantallaPersonal.agregarLibro("L003", "Orgullo y prejuicio", "Jane Austen", 4);
    pantallaConsulta.mostrarCatalogo();

    System.out.println();
    System.out.println(" 3. Buscar libros por título o autor ");
    pantallaConsulta.buscar("orgullo");
    pantallaConsulta.buscar("Austen");
    pantallaConsulta.buscar("quijote");

System.out.println();
System.out.println(" 4. El personal actualiza los ejemplares");
    pantallaPersonal.actualizarEjemplares("L002", 5);
System.out.println("La consulta ve la cantidad nueva:");
    pantallaConsulta.buscar("clasicos");

System.out.println();
System.out.println(" 5. Datos inválidos (q se rechazan) ");
    pantallaPersonal.agregarLibro("L001", "Otro libro", "Otro autor", 1);
    pantallaPersonal.agregarLibro("L004", "   ", "Alguien", 1);
    pantallaPersonal.agregarLibro("L005", "Bestiario", "Julio Cortázar", -2);
    pantallaPersonal.actualizarEjemplares("L003", -1);
    pantallaPersonal.actualizarEjemplares("L999", 4);
System.out.println("Libros en el catálogo (no cambió): " + CatalogoLibros.obtenerInstancia().getCantidadTitulos());

System.out.println();
System.out.println(" 6. La colección encapsulada ");
    List<Libro> libros = CatalogoLibros.obtenerInstancia().listarLibros();
    try {
      libros.remove(0);
System.out.println("ERROR: se pudo borrar un libro");
    } catch (UnsupportedOperationException e) {
System.out.println("No se puede borrar un libro desde la consulta");
    }
    try {
      libros.add(new Libro("L777", "Libro falso", "Intruso", 1));
System.out.println("ERROR: se pudo agregar un libro");
    } catch (UnsupportedOperationException e) {
System.out.println("No se puede agregar un libro sin pasar por el catálogo");
    }

System.out.println();
System.out.println("=== 7. Verificar que es una única instancia ===");
    CatalogoLibros catalogoPersonal = pantallaPersonal.getCatalogo();
    CatalogoLibros catalogoConsulta = pantallaConsulta.getCatalogo();
    if (catalogoPersonal == catalogoConsulta) {
System.out.println("Las dos pantallas usan el mismo catálogo");
    } else {
System.out.println("Las pantallas usan catálogos distintos");
    }
  }
}


// Un libro del catálogo. Es inmutable: sus atributos son final, así que para cambiar la cantidad hay que pasar por el catálogo.

final class Libro {
  private final String codigo;
  private final String titulo;
  private final String autor;
  private final int cantidad;

  public Libro(String codigo, String titulo, String autor, int cantidad) {
    this.codigo = codigo;
    this.titulo = titulo;
    this.autor = autor;
    this.cantidad = cantidad;
  }

  public String getCodigo() {
    return codigo;
  }
  public String getTitulo() {
    return titulo;
  }
  public String getAutor() {
    return autor;
  }
  public int getCantidad() {
    return cantidad;
  }
  public String texto() {
    return "[" + codigo + "] " + titulo + " - " + autor + " (" + cantidad + " ejemplares)";
  }
}
// Clase Singleton: un único catálogo de libros compartido por las dos pantallas.

class CatalogoLibros {
  // unica instancia de la clase
  private static CatalogoLibros instancia;
  // la lista es privada: nadie de afuera puede modificarla directamente
  private List<Libro> libros;
  // constructor privado: no se puede usar "new" desde otras clases
  private CatalogoLibros() {
    libros = new ArrayList<Libro>();
  }
  // devuelve siempre el mismo objeto
  public static CatalogoLibros obtenerInstancia() {
    if (instancia == null) {
      instancia = new CatalogoLibros();
    }
    return instancia;
  }
 // agrega un libro. Rechaza códigos repetidos o vacíos, títulos vacíos y cantidades negativas.
  public void agregarLibro(String codigo, String titulo, String autor, int cantidad) {
    if (codigo == null || codigo.trim().equals("")) {
      System.out.println("[Catálogo] No se agregó: el código no puede estar vacío");
      return;
    }
    if (buscarPosicion(codigo) != -1) {
      System.out.println("[Catálogo] No se agregó: ya existe un libro con el código " + codigo);
      return;
    }
    if (titulo == null || titulo.trim().equals("")) {
      System.out.println("[Catálogo] No se agregó: el título no puede estar vacío");
      return;
    }
    if (cantidad < 0) {
      System.out.println("[Catálogo] No se agregó: la cantidad no puede ser negativa (" + cantidad + ")");
      return;
    }
    libros.add(new Libro(codigo, titulo, autor, cantidad));
    System.out.println("[Catálogo] Libro agregado: " + titulo);
  }
  // actualiza la cantidad de ejemplares de un libro
  public void actualizarCantidad(String codigo, int nuevaCantidad) {
    int posicion = buscarPosicion(codigo);
    if (posicion == -1) {
      System.out.println("[Catálogo] No se actualizó: no existe el código " + codigo);
      return;
    }
    if (nuevaCantidad < 0) {
      System.out.println("[Catálogo] No se actualizó: la cantidad no puede ser negativa (" + nuevaCantidad + ")");
      return;
    }
    Libro libroViejo = libros.get(posicion);
    Libro libroNuevo = new Libro(libroViejo.getCodigo(), libroViejo.getTitulo(), libroViejo.getAutor(), nuevaCantidad);
    libros.set(posicion, libroNuevo);
    System.out.println("[Catálogo] Ejemplares de \"" + libroViejo.getTitulo() + "\" actualizados a " + nuevaCantidad);
  }
  // busca los libros cuyo título o autor contengan el texto (sin distinguir mayúsculas)
  public List<Libro> buscar(String texto) {
    List<Libro> encontrados = new ArrayList<Libro>();
    String textoBuscado = texto.toLowerCase();
    for (int i = 0; i < libros.size(); i++) {
      Libro libro = libros.get(i);
      if (libro.getTitulo().toLowerCase().contains(textoBuscado)
          || libro.getAutor().toLowerCase().contains(textoBuscado)) {
        encontrados.add(libro);
      }
    }
    return Collections.unmodifiableList(encontrados);
  }

  // te devuelve una copia de la lista que no se puede modificar
  public List<Libro> listarLibros() {
    List<Libro> copia = new ArrayList<Libro>(libros);
    return Collections.unmodifiableList(copia);
  }

  public int getCantidadTitulos() {
    return libros.size();
  }
// Devuelve la posición de un libro según su código (o -1 si no existe)
  private int buscarPosicion(String codigo) {
    for (int i = 0; i < libros.size(); i++) {
      if (libros.get(i).getCodigo().equals(codigo)) {
        return i;
      }
    }
    return -1;
  }
}
// Pantalla que usa el personal para cargar y actualizar libros
class PantallaPersonal {

  private CatalogoLibros catalogo;

  public PantallaPersonal() {
    catalogo = CatalogoLibros.obtenerInstancia();
  }

  public CatalogoLibros getCatalogo() {
    return catalogo;
  }

  public void agregarLibro(String codigo, String titulo, String autor, int cantidad) {
    catalogo.agregarLibro(codigo, titulo, autor, cantidad);
  }

  public void actualizarEjemplares(String codigo, int nuevaCantidad) {
    catalogo.actualizarCantidad(codigo, nuevaCantidad);
  }
}
// Pantalla que usan los socios para consultar el catálogo
class PantallaConsulta {

  private CatalogoLibros catalogo;

  public PantallaConsulta() {
    catalogo = CatalogoLibros.obtenerInstancia();
  }

  public CatalogoLibros getCatalogo() {
    return catalogo;
  }

  public void mostrarCatalogo() {
    List<Libro> libros = catalogo.listarLibros();
    System.out.println("[Consulta] Catálogo (" + libros.size() + " títulos):");
    for (int i = 0; i < libros.size(); i++) {
      System.out.println("  " + libros.get(i).texto());
    }
  }

  public void buscar(String texto) {
    List<Libro> encontrados = catalogo.buscar(texto);
    System.out.println("[Consulta] Búsqueda \"" + texto + "\": " + encontrados.size() + " resultado(s)");
    for (int i = 0; i < encontrados.size(); i++) {
      System.out.println("  " + encontrados.get(i).texto());
    }
  }
}
