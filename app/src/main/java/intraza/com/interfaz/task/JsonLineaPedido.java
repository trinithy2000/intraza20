package intraza.com.interfaz.task;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Almacena los datos que definen una linea de pedido en la BD
 *
 * @author JLZS
 */
@Data
@AllArgsConstructor(access = AccessLevel.PUBLIC)
public class JsonLineaPedido {

    private int idPrepedido;
    private int idArticulo;
    private String nombreArticulo;
    private float cantidadKg;
    private int cantidadUd;
    private float precio;
    private String observaciones;
    private boolean fijarPrecio;
    private boolean suprimirPrecio;
    private boolean fijarArticulo;
    private boolean fijarObservaciones;
}
