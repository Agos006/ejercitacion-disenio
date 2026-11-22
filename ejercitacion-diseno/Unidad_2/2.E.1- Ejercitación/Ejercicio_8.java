public class Ejercicio_8 {
 public static void main(String[] args) {
System.out.println("=== 1. El main crea la pasarela una sola vez ===");
    PasarelaPagoReal pasarela = PasarelaPagoReal.obtenerInstancia();

System.out.println();
System.out.println("=== 2. Se le entrega la pasarela a los servicios por el constructor ===");
    ServicioPedidos servicioPedidos = new ServicioPedidos(pasarela);
    ServicioSuscripciones servicioSuscripciones = new ServicioSuscripciones(pasarela);
    servicioPedidos.realizarPedido("Anto", 5000);
    servicioSuscripciones.renovar("Maribel", 1200);
    servicioPedidos.realizarPedido("Agostina", 800);

System.out.println();
System.out.println("=== 3. Se sigue usando una única instancia ===");
    PasarelaPagoReal pasarela1 = PasarelaPagoReal.obtenerInstancia();
    PasarelaPagoReal pasarela2 = PasarelaPagoReal.obtenerInstancia();
 if (pasarela1 == pasarela2) {
      System.out.println("pasarela1 y pasarela2 son el mismo objeto");
  } else {
      System.out.println("pasarela1 y pasarela2 son objetos distintos");
 }
System.out.println("Pagos procesados por la pasarela: " + pasarela1.getCantidadPagos());
System.out.println("(los dos servicios usaron la misma pasarela)");

System.out.println();
System.out.println("=== 4. Pago rechazado por monto inválido ===");
    servicioPedidos.realizarPedido("Pedro", -300);

System.out.println();
System.out.println("=== 5. Prueba con una pasarela falsa (sin pagar de verdad) ===");
    PasarelaFalsa pasarelaFalsa = new PasarelaFalsa();
    ServicioPedidos servicioDePrueba = new ServicioPedidos(pasarelaFalsa);
    servicioDePrueba.realizarPedido("Cliente de prueba", 999);
 if (pasarelaFalsa.getUltimoMonto() == 999) {
System.out.println("Prueba CORRECTA: el servicio envió el monto 999 a la pasarela");
 } else {
System.out.println("Prueba con ERROR");
    }
System.out.println("Pagos de la pasarela real (no cambiaron): " + pasarela1.getCantidadPagos());

System.out.println();
System.out.println("=== 6. Constructor privado ===");
System.out.println("No se puede hacer new PasarelaPagoReal() desde otra clase");
  }
}


//interfaz: define lo que sabe hacer una pasarela de pago,los servicios dependen de esta interfaz, no de una clase concreta.

interface PasarelaPago {
  boolean pagar(String cliente, int monto);
}

 //*Singleton: la pasarela de pago real. Solo puede existir una:Implementa la interfaz PasarelaPago, por eso se puede entregar a cualquier clase que necesite una pasarela.
class PasarelaPagoReal implements PasarelaPago {

  // unica instancia de la clase
  private static PasarelaPagoReal instancia;
private int cantidadPagos;
  // constructor privado: no se puede usar "new" desde otras clases
  private PasarelaPagoReal() {
    cantidadPagos = 0;
    System.out.println("[Pasarela] Se creó la pasarela de pago real");
  }
  // devuelve siempre el mismo objeto
  public static synchronized PasarelaPagoReal obtenerInstancia() {
    if (instancia == null) {
      instancia = new PasarelaPagoReal();
    }
    return instancia;
  }
  // simula un pago. Rechaza los montos que no sean mayores a cero.
  public synchronized boolean pagar(String cliente, int monto) {
    if (monto <= 0) {
      System.out.println("[Pasarela] Pago rechazado de " + cliente + ": monto inválido (" + monto + ")");
      return false;
    }
    cantidadPagos++;
    System.out.println("[Pasarela] Pago #" + cantidadPagos + " aprobado: " + cliente + " pagó $" + monto);
    return true;
  }

  public synchronized int getCantidadPagos() {
    return cantidadPagos;
  }
}

 // Pasarela falsa para probar: no cobra nada, solo guarda el último monto recibido,sirve para probar los servicios sin usar la pasarela real.
 
class PasarelaFalsa implements PasarelaPago {

  private int ultimoMonto;

  public boolean pagar(String cliente, int monto) {
    ultimoMonto = monto;
    System.out.println("[PasarelaFalsa] Pago simulado de " + cliente + " por $" + monto);
    return true;
  }

  public int getUltimoMonto() {
    return ultimoMonto;
  }
}
// servicio que procesa los pedidos. Recibe la pasarela por el constructor.
class ServicioPedidos {

  private PasarelaPago pasarela;

  public ServicioPedidos(PasarelaPago pasarela) {
    this.pasarela = pasarela;
  }

  public void realizarPedido(String cliente, int monto) {
    boolean pagado = pasarela.pagar(cliente, monto);
    if (pagado) {
      System.out.println("[Pedidos] Pedido de " + cliente + " confirmado");
    } else {
      System.out.println("[Pedidos] Pedido de " + cliente + " cancelado");
    }
  }
}
// servicio que renueva suscripciones. También recibe la pasarela por el constructor.
class ServicioSuscripciones {

  private PasarelaPago pasarela;

  public ServicioSuscripciones(PasarelaPago pasarela) {
    this.pasarela = pasarela;
  }

  public void renovar(String cliente, int monto) {
    boolean pagado = pasarela.pagar(cliente, monto);
    if (pagado) {
      System.out.println("[Suscripciones] Suscripción de " + cliente + " renovada");
    } else {
      System.out.println("[Suscripciones] No se pudo renovar la suscripción de " + cliente);
    }
  }
}