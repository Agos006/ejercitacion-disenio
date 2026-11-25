import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Ejercicio_20 {
public static void main(String[] args) throws InterruptedException {

PuestoRecepcion recepcion = new PuestoRecepcion();
PuestoAutoservicio autoservicio = new PuestoAutoservicio();
AdministradorTurnos administrador = AdministradorTurnos.obtenerInstancia();
LocalDate fecha = LocalDate.of(2026, 11, 25);
String profesional1 = "Dra. Gómez";
String profesional2 = "Dr. Ruiz";

System.out.println(" 1-Se carga la agenda de los profesionales ");
administrador.cargarAgenda(profesional1, fecha, LocalTime.of(9, 0), 6, 30);
administrador.cargarAgenda(profesional2, fecha, LocalTime.of(10, 0), 4, 30);

System.out.println();
System.out.println(" 2-Recepción y autoservicio consultan la agenda inicial ");
recepcion.mostrarAgenda(profesional1, fecha);
autoservicio.mostrarAgenda(profesional1, fecha);

System.out.println();
System.out.println(" 3-Veinticuatro hilos intentan reservar el mismo turno ");
LocalTime horarioDisputado = LocalTime.of(9, 0);
int cantidadHilos = 24;
HiloPaciente[] hilos = new HiloPaciente[cantidadHilos];
for (int i = 0; i < cantidadHilos; i++) {
hilos[i] = new HiloPaciente("Paciente " + (i + 1), profesional1, fecha, horarioDisputado);
}
for (int i = 0; i < cantidadHilos; i++) {
hilos[i].start();
}
for (int i = 0; i < cantidadHilos; i++) {
hilos[i].join();
}
int reservados = 0;
int ocupados = 0;
String ganador = "";
for (int i = 0; i < cantidadHilos; i++) {
if (hilos[i].getResultado().equals("RESERVADO")) {
reservados++;
ganador = hilos[i].getPaciente();
} else if (hilos[i].getResultado().equals("OCUPADO")) {
ocupados++;
}
}
System.out.println("Solicitudes para las " + horarioDisputado + ": " + cantidadHilos);
System.out.println("Reservas exitosas: " + reservados);
System.out.println("Rechazadas por horario ocupado: " + ocupados);
System.out.println("El turno quedó para: " + ganador);
if (reservados == 1 && ocupados == cantidadHilos - 1) {
System.out.println("Resultado: CORRECTO (solo una solicitud consiguió el turno)");
} else {
System.out.println("Resultado: ERROR (se asignó el mismo turno más de una vez)");
}
boolean todasIguales = true;
for (int i = 0; i < cantidadHilos; i++) {
if (hilos[i].getAdministrador() != administrador) {
todasIguales = false;
}
}
if (todasIguales) {
System.out.println("Los 24 hilos y el main usaron el mismo administrador");
} else {
System.out.println("ERROR: hay administradores distintos");
}

System.out.println();
System.out.println(" 4-Los dos componentes ven la agenda actualizada ");
recepcion.mostrarAgenda(profesional1, fecha);
autoservicio.mostrarAgenda(profesional1, fecha);

System.out.println();
System.out.println(" 5-Recepción y autoservicio reservan otros horarios ");
recepcion.reservar("Paciente 51", profesional1, fecha, LocalTime.of(9, 30));
autoservicio.reservar("Paciente 52", profesional1, fecha, LocalTime.of(9, 30));
autoservicio.reservar("Paciente 52", profesional1, fecha, LocalTime.of(10, 0));
recepcion.reservar("Paciente 53", profesional2, fecha, LocalTime.of(10, 0));
recepcion.mostrarAgenda(profesional1, fecha);

System.out.println();
System.out.println(" 6-Cancelaciones ");
System.out.println("Turnos libres antes: " + administrador.getCantidadLibres());
recepcion.cancelar(profesional1, fecha, LocalTime.of(9, 0));
System.out.println("Turnos libres después de cancelar uno reservado: " + administrador.getCantidadLibres());
recepcion.cancelar(profesional1, fecha, LocalTime.of(15, 0));
autoservicio.cancelar(profesional1, fecha, LocalTime.of(10, 30));
System.out.println("Turnos libres después de cancelar turnos inexistentes: " + administrador.getCantidadLibres());
autoservicio.mostrarAgenda(profesional1, fecha);

System.out.println();
System.out.println(" 7-Datos inválidos y horarios que no existen ");
recepcion.reservar("   ", profesional1, fecha, LocalTime.of(11, 0));
autoservicio.reservar("Paciente 60", profesional1, fecha, LocalTime.of(20, 0));
System.out.println("Turnos libres (no cambió): " + administrador.getCantidadLibres());

System.out.println();
System.out.println(" 8-Verificar que es una única instancia ");
if (recepcion.getAdministrador() == autoservicio.getAdministrador()) {
System.out.println("Recepción y autoservicio usan el mismo administrador");
} else {
System.out.println("Recepción y autoservicio usan administradores distintos");
}
}
}

// Un turno de la agenda. Es inmutable: si el paciente es null, el horario está libre.
final class Turno {

private final String profesional;
private final LocalDate fecha;
private final LocalTime horario;
private final String paciente;

public Turno(String profesional, LocalDate fecha, LocalTime horario, String paciente) {
this.profesional = profesional;
this.fecha = fecha;
this.horario = horario;
this.paciente = paciente;
}

public String getProfesional() {
return profesional;
}

public LocalDate getFecha() {
return fecha;
}

public LocalTime getHorario() {
return horario;
}

public String getPaciente() {
return paciente;
}

public boolean estaLibre() {
return paciente == null;
}
}

/*
* Clase Singleton: un único administrador de turnos para recepción y autoservicio.
*
* La creación de la instancia es segura porque se hace al cargar la clase (static final).
* Los métodos que consultan y modifican la agenda son synchronized: la comprobación de
* que el horario está libre y la asignación del turno se hacen juntas (operación atómica).
*/
class AdministradorTurnos {

// Única instancia de la clase. Se crea una sola vez, cuando se carga la clase.
private static final AdministradorTurnos instancia = new AdministradorTurnos();

// La agenda es privada: la clave identifica a cada horario (profesional, fecha y hora)
private Map<String, Turno> agenda;

// Constructor privado: no se puede usar "new" desde otras clases
private AdministradorTurnos() {
agenda = new LinkedHashMap<String, Turno>();
}

// Devuelve siempre el mismo objeto
public static AdministradorTurnos obtenerInstancia() {
return instancia;
}

// Arma la clave que identifica un horario de un profesional en una fecha
private String clave(String profesional, LocalDate fecha, LocalTime horario) {
return profesional + "|" + fecha + "|" + horario;
}

// Carga horarios disponibles para un profesional en una fecha
public synchronized void cargarAgenda(String profesional, LocalDate fecha, LocalTime inicio, int cantidad, int minutos) {
for (int i = 0; i < cantidad; i++) {
LocalTime horario = inicio.plusMinutes(i * minutos);
agenda.put(clave(profesional, fecha, horario), new Turno(profesional, fecha, horario, null));
}
System.out.println("[Administrador] Agenda cargada: " + profesional + ", " + fecha.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
+ ", " + cantidad + " horarios desde las " + inicio);
}

// Reserva un turno. Devuelve: RESERVADO, OCUPADO, NO_EXISTE o DATOS_INVALIDOS.
// Es synchronized: mientras un hilo comprueba y asigna, ningún otro puede entrar.
public synchronized String reservar(String paciente, String profesional, LocalDate fecha, LocalTime horario) {
if (paciente == null || paciente.trim().equals("") || profesional == null || fecha == null || horario == null) {
return "DATOS_INVALIDOS";
}
String claveTurno = clave(profesional, fecha, horario);
Turno turno = agenda.get(claveTurno);
if (turno == null) {
return "NO_EXISTE";
}
if (!turno.estaLibre()) {
return "OCUPADO";
}
agenda.put(claveTurno, new Turno(profesional, fecha, horario, paciente));
return "RESERVADO";
}

// Cancela un turno reservado y libera el horario. Si no hay un turno reservado, no cambia nada.
public synchronized boolean cancelar(String profesional, LocalDate fecha, LocalTime horario) {
String claveTurno = clave(profesional, fecha, horario);
Turno turno = agenda.get(claveTurno);
if (turno == null || turno.estaLibre()) {
return false;
}
agenda.put(claveTurno, new Turno(profesional, fecha, horario, null));
return true;
}

// Devuelve la agenda de un profesional en una fecha (libres y ocupados) en una lista que no se puede modificar
public synchronized List<Turno> obtenerAgenda(String profesional, LocalDate fecha) {
List<Turno> turnos = new ArrayList<Turno>();
for (Turno turno : agenda.values()) {
if (turno.getProfesional().equals(profesional) && turno.getFecha().equals(fecha)) {
turnos.add(turno);
}
}
return Collections.unmodifiableList(turnos);
}

// Cuenta los horarios libres de toda la agenda
public synchronized int getCantidadLibres() {
int libres = 0;
for (Turno turno : agenda.values()) {
if (turno.estaLibre()) {
libres++;
}
}
return libres;
}
}

// Componente de recepción: reserva y cancela turnos
class PuestoRecepcion {

private AdministradorTurnos administrador;

public PuestoRecepcion() {
administrador = AdministradorTurnos.obtenerInstancia();
}

public AdministradorTurnos getAdministrador() {
return administrador;
}

public void reservar(String paciente, String profesional, LocalDate fecha, LocalTime horario) {
String resultado = administrador.reservar(paciente, profesional, fecha, horario);
System.out.println("[Recepción] Reservar " + horario + " con " + profesional + " para " + paciente + ": " + resultado);
}

public void cancelar(String profesional, LocalDate fecha, LocalTime horario) {
boolean cancelado = administrador.cancelar(profesional, fecha, horario);
if (cancelado) {
System.out.println("[Recepción] Turno de las " + horario + " cancelado, el horario quedó libre");
} else {
System.out.println("[Recepción] No hay un turno reservado a las " + horario + ", la agenda no cambió");
}
}

public void mostrarAgenda(String profesional, LocalDate fecha) {
List<Turno> turnos = administrador.obtenerAgenda(profesional, fecha);
System.out.println("[Recepción] Agenda de " + profesional + ":");
for (int i = 0; i < turnos.size(); i++) {
if (turnos.get(i).estaLibre()) {
System.out.println("  " + turnos.get(i).getHorario() + " libre");
} else {
System.out.println("  " + turnos.get(i).getHorario() + " ocupado (" + turnos.get(i).getPaciente() + ")");
}
}
}
}

// Componente de autoservicio: reserva y cancela turnos
class PuestoAutoservicio {

private AdministradorTurnos administrador;

public PuestoAutoservicio() {
administrador = AdministradorTurnos.obtenerInstancia();
}

public AdministradorTurnos getAdministrador() {
return administrador;
}

public void reservar(String paciente, String profesional, LocalDate fecha, LocalTime horario) {
String resultado = administrador.reservar(paciente, profesional, fecha, horario);
System.out.println("[Autoservicio] Reservar " + horario + " con " + profesional + " para " + paciente + ": " + resultado);
}

public void cancelar(String profesional, LocalDate fecha, LocalTime horario) {
boolean cancelado = administrador.cancelar(profesional, fecha, horario);
if (cancelado) {
System.out.println("[Autoservicio] Turno de las " + horario + " cancelado, el horario quedó libre");
} else {
System.out.println("[Autoservicio] No hay un turno reservado a las " + horario + ", la agenda no cambió");
}
}

public void mostrarAgenda(String profesional, LocalDate fecha) {
List<Turno> turnos = administrador.obtenerAgenda(profesional, fecha);
System.out.println("[Autoservicio] Agenda de " + profesional + ":");
for (int i = 0; i < turnos.size(); i++) {
if (turnos.get(i).estaLibre()) {
System.out.println("  " + turnos.get(i).getHorario() + " libre");
} else {
System.out.println("  " + turnos.get(i).getHorario() + " ocupado (" + turnos.get(i).getPaciente() + ")");
}
}
}
}

// Simula un paciente que intenta reservar un turno desde un hilo
class HiloPaciente extends Thread {

private String paciente;
private String profesional;
private LocalDate fecha;
private LocalTime horario;
private String resultado;
private AdministradorTurnos administrador;

public HiloPaciente(String paciente, String profesional, LocalDate fecha, LocalTime horario) {
this.paciente = paciente;
this.profesional = profesional;
this.fecha = fecha;
this.horario = horario;
resultado = "";
}

public String getPaciente() {
return paciente;
}

public String getResultado() {
return resultado;
}

public AdministradorTurnos getAdministrador() {
return administrador;
}

public void run() {
administrador = AdministradorTurnos.obtenerInstancia();
resultado = administrador.reservar(paciente, profesional, fecha, horario);
}
}