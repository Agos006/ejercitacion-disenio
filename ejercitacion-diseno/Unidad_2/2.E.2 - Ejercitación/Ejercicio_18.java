import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Ejercicio_18 {
public static void main(String[] args) {

SectorDeposito deposito = new SectorDeposito();
SectorConsultorios consultorios = new SectorConsultorios();
SectorLaboratorio laboratorio = new SectorLaboratorio();
RegistroMovimientos registro = RegistroMovimientos.obtenerInstancia();

System.out.println(" 1-El depósito carga el saldo inicial con movimientos de entrada ");
deposito.recibirInsumo("GAS-01", 100, LocalDate.of(2026, 11, 1));
deposito.recibirInsumo("JER-05", 200, LocalDate.of(2026, 11, 1));
deposito.recibirInsumo("ALC-02", 40, LocalDate.of(2026, 11, 2));

System.out.println();
System.out.println(" 2-Los tres sectores registran movimientos ");
consultorios.consumirInsumo("GAS-01", 30, LocalDate.of(2026, 11, 3));
consultorios.consumirInsumo("JER-05", 80, LocalDate.of(2026, 11, 3));
laboratorio.consumirInsumo("GAS-01", 20, LocalDate.of(2026, 11, 4));
laboratorio.consumirInsumo("JER-05", 40, LocalDate.of(2026, 11, 4));
laboratorio.consumirInsumo("ALC-02", 15, LocalDate.of(2026, 11, 4));
deposito.recibirInsumo("GAS-01", 50, LocalDate.of(2026, 11, 5));

System.out.println();
System.out.println(" 3-Historial completo ");
List<Movimiento> historial = registro.obtenerHistorial();
for (int i = 0; i < historial.size(); i++) {
System.out.println((i + 1) + ". " + historial.get(i).texto());
}

System.out.println();
System.out.println(" 4-Filtrar por insumo y por sector ");
mostrarLista("Movimientos del insumo JER-05", registro.filtrarPorInsumo("JER-05"));
mostrarLista("Movimientos del sector Consultorios", registro.filtrarPorSector("Consultorios"));

System.out.println();
System.out.println(" 5-Saldo de cada insumo ");
System.out.println("GAS-01: " + registro.calcularSaldo("GAS-01"));
System.out.println("JER-05: " + registro.calcularSaldo("JER-05"));
System.out.println("ALC-02: " + registro.calcularSaldo("ALC-02"));

System.out.println();
System.out.println(" 6-Movimientos inválidos (se rechazan) ");
System.out.println("Movimientos registrados antes: " + registro.getCantidadMovimientos());
consultorios.consumirInsumo("ALC-02", 30, LocalDate.of(2026, 11, 6));
consultorios.consumirInsumo("GAS-77", 5, LocalDate.of(2026, 11, 6));
laboratorio.consumirInsumo("JER-05", 0, LocalDate.of(2026, 11, 6));
laboratorio.consumirInsumo("JER-05", -10, LocalDate.of(2026, 11, 6));
deposito.recibirInsumo("   ", 10, LocalDate.of(2026, 11, 6));
System.out.println("Movimientos registrados después: " + registro.getCantidadMovimientos());
System.out.println("Saldo de ALC-02 (no cambió): " + registro.calcularSaldo("ALC-02"));

System.out.println();
System.out.println(" 7-Los movimientos no se pueden editar ni eliminar ");
try {
historial.remove(0);
System.out.println("ERROR: se pudo borrar un movimiento");
} catch (UnsupportedOperationException e) {
System.out.println("No se puede borrar un movimiento desde el código cliente");
}
try {
historial.set(0, historial.get(1));
System.out.println("ERROR: se pudo reemplazar un movimiento");
} catch (UnsupportedOperationException e) {
System.out.println("No se puede reemplazar un movimiento desde el código cliente");
}

System.out.println();
System.out.println(" 8-Verificar que es una única instancia ");
if (deposito.getRegistro() == consultorios.getRegistro()
&& consultorios.getRegistro() == laboratorio.getRegistro()) {
System.out.println("Los tres sectores usan el mismo registro");
} else {
System.out.println("Los sectores usan registros distintos");
}
}
// muestra una lista de movimientos con un título
private static void mostrarLista(String titulo, List<Movimiento> movimientos) {
System.out.println(titulo + " (" + movimientos.size() + "):");
for (int i = 0; i < movimientos.size(); i++) {
System.out.println("  " + movimientos.get(i).texto());
}
}
}
// tipos de movimiento que admite el registro
enum TipoMovimiento {
ENTRADA,
SALIDA
}

//Un movimiento de insumos. Es inmutable: sus atributos son final y no tiene métodos para modificarlos, por eso un movimiento registrado no se puede editar.
final class Movimiento {

private final String codigoInsumo;
private final TipoMovimiento tipo;
private final int cantidad;
private final String sector;
private final LocalDate fecha;

public Movimiento(String codigoInsumo, TipoMovimiento tipo, int cantidad, String sector, LocalDate fecha) {
this.codigoInsumo = codigoInsumo;
this.tipo = tipo;
this.cantidad = cantidad;
this.sector = sector;
this.fecha = fecha;
}
public String getCodigoInsumo() {
return codigoInsumo;
}
public TipoMovimiento getTipo() {
return tipo;
}
public int getCantidad() {
return cantidad;
}
public String getSector() {
return sector;
}
public String texto() {
return fecha.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + " | " + tipo + " | "
+ codigoInsumo + " x " + cantidad + " | " + sector;
}
}

// Clase Singleton: un único registro de movimientos de insumos para todos los sectores.
class RegistroMovimientos {

// unica instancia de la clase
private static RegistroMovimientos instancia;

// la lista es privada: nadie de afuera puede modificarla directamente
private List<Movimiento> movimientos;

// constructor privado: no se puede usar "new" desde otras clases
private RegistroMovimientos() {
movimientos = new ArrayList<Movimiento>();
}

// devuelve siempre el mismo objeto
public static RegistroMovimientos obtenerInstancia() {
if (instancia == null) {
instancia = new RegistroMovimientos();
}
return instancia;
}

// registra un movimiento. Controla los datos y, en las salidas, que alcance el saldo.
public void registrar(String codigoInsumo, TipoMovimiento tipo, int cantidad, String sector, LocalDate fecha) {
if (codigoInsumo == null || codigoInsumo.trim().equals("")) {
System.out.println("[Registro] Movimiento rechazado: falta el código del insumo");
return;
}
if (sector == null || sector.trim().equals("")) {
System.out.println("[Registro] Movimiento rechazado: falta el sector");
return;
}
if (tipo == null || fecha == null) {
System.out.println("[Registro] Movimiento rechazado: faltan el tipo o la fecha");
return;
}
if (cantidad <= 0) {
System.out.println("[Registro] Movimiento rechazado: la cantidad debe ser positiva (" + cantidad + ")");
return;
}
if (tipo == TipoMovimiento.SALIDA) {
int saldo = calcularSaldo(codigoInsumo);
if (cantidad > saldo) {
System.out.println("[Registro] Salida rechazada de " + codigoInsumo + ": se piden " + cantidad
+ " y el saldo es " + saldo);
return;
}
}
movimientos.add(new Movimiento(codigoInsumo, tipo, cantidad, sector, fecha));
System.out.println("(Registro)" + tipo + " registrada: " + codigoInsumo + " x " + cantidad + " (" + sector + ")");
}

// devuelve una copia del historial que no se puede modificar
public List<Movimiento> obtenerHistorial() {
List<Movimiento> copia = new ArrayList<Movimiento>(movimientos);
return Collections.unmodifiableList(copia);
}

// devuelve los movimientos de un insumo
public List<Movimiento> filtrarPorInsumo(String codigoInsumo) {
List<Movimiento> filtrados = new ArrayList<Movimiento>();
for (int i = 0; i < movimientos.size(); i++) {
if (movimientos.get(i).getCodigoInsumo().equals(codigoInsumo)) {
filtrados.add(movimientos.get(i));
}
}
return Collections.unmodifiableList(filtrados);
}

// devuelve los movimientos de un sector
public List<Movimiento> filtrarPorSector(String sector) {
List<Movimiento> filtrados = new ArrayList<Movimiento>();
for (int i = 0; i < movimientos.size(); i++) {
if (movimientos.get(i).getSector().equalsIgnoreCase(sector)) {
filtrados.add(movimientos.get(i));
}
}
return Collections.unmodifiableList(filtrados);
}

// calcula el saldo de un insumo: suma las entradas y resta las salidas
public int calcularSaldo(String codigoInsumo) {
int saldo = 0;
for (int i = 0; i < movimientos.size(); i++) {
Movimiento movimiento = movimientos.get(i);
if (movimiento.getCodigoInsumo().equals(codigoInsumo)) {
if (movimiento.getTipo() == TipoMovimiento.ENTRADA) {
saldo = saldo + movimiento.getCantidad();
} else {
saldo = saldo - movimiento.getCantidad();
}
}
}
return saldo;
}

public int getCantidadMovimientos() {
return movimientos.size();
}
}
// sector depósito: recibe insumos (entradas)
class SectorDeposito {
private RegistroMovimientos registro;
public SectorDeposito() {
registro = RegistroMovimientos.obtenerInstancia();
}
public RegistroMovimientos getRegistro() {
return registro;
}
public void recibirInsumo(String codigoInsumo, int cantidad, LocalDate fecha) {
registro.registrar(codigoInsumo, TipoMovimiento.ENTRADA, cantidad, "Depósito", fecha);
}
}

// sector consultorios: consume insumos (salidas)
class SectorConsultorios {
private RegistroMovimientos registro;
public SectorConsultorios() {
registro = RegistroMovimientos.obtenerInstancia();
}
public RegistroMovimientos getRegistro() {
return registro;
}
public void consumirInsumo(String codigoInsumo, int cantidad, LocalDate fecha) {
registro.registrar(codigoInsumo, TipoMovimiento.SALIDA, cantidad, "Consultorios", fecha);
}
}

// sector laboratorio: consume insumos (salidas)
class SectorLaboratorio {
private RegistroMovimientos registro;
public SectorLaboratorio() {
registro = RegistroMovimientos.obtenerInstancia();
}
public RegistroMovimientos getRegistro() {
return registro;
}
public void consumirInsumo(String codigoInsumo, int cantidad, LocalDate fecha) {
registro.registrar(codigoInsumo, TipoMovimiento.SALIDA, cantidad, "Laboratorio", fecha);
}
}