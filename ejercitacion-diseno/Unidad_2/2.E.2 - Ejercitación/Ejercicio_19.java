import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
public class Ejercicio_19 {
public static void main(String[] args) throws InterruptedException {

String[] sectores = {"Recepción", "Facturación", "Archivo"};
int cantidadHilos = 10;
int documentosPorHilo = 4;

System.out.println(" 1-Diez hilos envían documentos al mismo tiempo ");
HiloSector[] hilos = new HiloSector[cantidadHilos];
for (int i = 0; i < cantidadHilos; i++) {
hilos[i] = new HiloSector(i + 1, sectores[i % 3], documentosPorHilo);
}
for (int i = 0; i < cantidadHilos; i++) {
hilos[i].start();
}
for (int i = 0; i < cantidadHilos; i++) {
hilos[i].join();
}
GestorImpresion gestor = GestorImpresion.obtenerInstancia();
System.out.println("Hilos: " + cantidadHilos);
System.out.println("Documentos esperados: " + (cantidadHilos * documentosPorHilo));
System.out.println("Documentos en la cola: " + gestor.getCantidadPendientes());

System.out.println();
System.out.println(" 2-Todos los hilos usaron la misma instancia ");
boolean todasIguales = true;
for (int i = 0; i < cantidadHilos; i++) {
if (hilos[i].getGestor() != gestor) {
todasIguales = false;
}
}
if (todasIguales) {
System.out.println("Los 10 hilos y el main usaron el mismo gestor de impresión");
} else {
System.out.println("ERROR: hay gestores distintos");
}

System.out.println();
System.out.println(" 3-Se procesan los documentos en el orden de la cola ");
gestor.procesarTodos();

System.out.println();
System.out.println(" 4-Verificar que cada documento se procesó una sola vez ");
List<TrabajoImpresion> procesados = gestor.obtenerProcesados();
System.out.println("Documentos procesados: " + procesados.size());
System.out.println("Documentos pendientes: " + gestor.getCantidadPendientes());
boolean ordenCorrecto = true;
for (int i = 0; i < procesados.size(); i++) {
if (procesados.get(i).getId() != i + 1) {
ordenCorrecto = false;
}
}
if (ordenCorrecto && procesados.size() == cantidadHilos * documentosPorHilo) {
System.out.println("Resultado: CORRECTO (se procesaron en orden del 1 al " + procesados.size()
+ ", sin perder ni repetir ninguno)");
} else {
System.out.println("Resultado: ERROR (hay documentos perdidos, repetidos o desordenados)");
}

System.out.println();
System.out.println(" 5-Informe de trabajos procesados ");
gestor.emitirInforme();

System.out.println();
System.out.println(" 6-Documentos inválidos y cola vacía ");
gestor.encolar("", "Archivo", 5);
gestor.encolar("informe.pdf", "Archivo", 0);
gestor.encolar("informe.pdf", "   ", 3);
System.out.println("Documentos en la cola: " + gestor.getCantidadPendientes());
gestor.procesarSiguiente();
}
}

// un trabajo de impresión. Es inmutable: sus atributos son final.
final class TrabajoImpresion {

private final int id;
private final String nombre;
private final String sector;
private final int paginas;

public TrabajoImpresion(int id, String nombre, String sector, int paginas) {
this.id = id;
this.nombre = nombre;
this.sector = sector;
this.paginas = paginas;
}
public int getId() {
return id;
}
public String getNombre() {
return nombre;
}
public String getSector() {
return sector;
}
public int getPaginas() {
return paginas;
}
}
/*
* Clase Singleton: un único gestor de impresión para todos los sectores.
*
* Hay dos tipos de seguridad ante concurrencia y no son lo mismo:
* 1) Crear la instancia: se resuelve creándola al cargar la clase (atributo static final),
*    así dos hilos nunca pueden crear dos gestores.
* 2) Usar la cola: se resuelve con synchronized en cada método que toca la cola.
*    Que la instancia sea única no alcanza para proteger los datos que guarda.
*/
class GestorImpresion {
// unica instancia de la clase. Se crea una sola vez, cuando se carga la clase.
private static final GestorImpresion instancia = new GestorImpresion();

private Queue<TrabajoImpresion> cola;
private List<TrabajoImpresion> procesados;
private int contadorIds;
// constructor privado: no se puede usar "new" desde otras clases
private GestorImpresion() {
cola = new LinkedList<TrabajoImpresion>();
procesados = new ArrayList<TrabajoImpresion>();
contadorIds = 0;
}
// devuelve siempre el mismo objeto (no hace falta synchronized: la instancia ya está creada)
public static GestorImpresion obtenerInstancia() {
return instancia;
}
// agrega un documento al final de la cola y le asigna un identificador único.
// es synchronized: el número y la posición en la cola se asignan juntos.
public synchronized int encolar(String nombre, String sector, int paginas) {
if (nombre == null || nombre.trim().equals("")) {
System.out.println("[Gestor] Documento rechazado: falta el nombre");
return -1;
}
if (sector == null || sector.trim().equals("")) {
System.out.println("[Gestor] Documento rechazado: falta el sector");
return -1;
}
if (paginas <= 0) {
System.out.println("[Gestor] Documento rechazado: la cantidad de páginas debe ser positiva (" + paginas + ")");
return -1;
}
contadorIds++;
cola.add(new TrabajoImpresion(contadorIds, nombre, sector, paginas));
return contadorIds;
}

// saca el primer documento de la cola, simula la impresión y lo guarda como procesado
public synchronized void procesarSiguiente() {
TrabajoImpresion trabajo = cola.poll();
if (trabajo == null) {
System.out.println("[Impresora] No hay documentos para imprimir");
return;
}
System.out.println("[Impresora] Imprimiendo #" + trabajo.getId() + " " + trabajo.getNombre()
+ " (" + trabajo.getSector() + ", " + trabajo.getPaginas() + " páginas)");
procesados.add(trabajo);
}
// procesa todos los documentos que hay en la cola, uno por uno
public synchronized void procesarTodos() {
while (!cola.isEmpty()) {
procesarSiguiente();
}
}
public synchronized int getCantidadPendientes() {
return cola.size();
}
// devuelve una copia de los trabajos procesados que no se puede modificar
public synchronized List<TrabajoImpresion> obtenerProcesados() {
List<TrabajoImpresion> copia = new ArrayList<TrabajoImpresion>(procesados);
return Collections.unmodifiableList(copia);
}
// muestra un informe con los trabajos procesados: total y detalle por sector
public synchronized void emitirInforme() {
int totalPaginas = 0;
List<String> sectores = new ArrayList<String>();
for (int i = 0; i < procesados.size(); i++) {
totalPaginas = totalPaginas + procesados.get(i).getPaginas();
if (!sectores.contains(procesados.get(i).getSector())) {
sectores.add(procesados.get(i).getSector());
}
}
Collections.sort(sectores);

System.out.println("Documentos procesados: " + procesados.size());
System.out.println("Páginas impresas: " + totalPaginas);
for (int i = 0; i < sectores.size(); i++) {
int documentos = 0;
int paginas = 0;
for (int j = 0; j < procesados.size(); j++) {
if (procesados.get(j).getSector().equals(sectores.get(i))) {
documentos++;
paginas = paginas + procesados.get(j).getPaginas();
}
}
System.out.println("  " + sectores.get(i) + ": " + documentos + " documentos, " + paginas + " páginas");
}
}
}
// simula un sector que envía documentos a la impresora desde un hilo
class HiloSector extends Thread {

private int numero;
private String sector;
private int cantidadDocumentos;
private GestorImpresion gestor;

public HiloSector(int numero, String sector, int cantidadDocumentos) {
this.numero = numero;
this.sector = sector;
this.cantidadDocumentos = cantidadDocumentos;
}

public GestorImpresion getGestor() {
return gestor;
}

public void run() {
gestor = GestorImpresion.obtenerInstancia();
for (int i = 1; i <= cantidadDocumentos; i++) {
int paginas = 1 + (numero + i) % 5;
gestor.encolar("documento_h" + numero + "_" + i + ".pdf", sector, paginas);
}
}
}
