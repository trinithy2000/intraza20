package intraza.com.interfaz.task;

import java.util.ArrayList;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Almacena los datos que definen un pedido en la BD
 */
@Data
@AllArgsConstructor(access = AccessLevel.PUBLIC)
public class JsonPedido {
    private int idPedido;
    private int idCliente;
    private String cliente;
    private int diaFechaPedido;
    private int mesFechaPedido;
    private int anioFechaPedido;
    private int diaFechaEntrega;
    private int mesFechaEntrega;
    private int anioFechaEntrega;
    private String observaciones;
    private boolean fijar;
    private int descuento;
    private ArrayList<JsonLineaPedido> lineasPedido;
}
