package intraza.com.interfaz.datosDB;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Almacena los datos que definen una linea de pedido en la BD
 */

@Data
@AllArgsConstructor(access = AccessLevel.PUBLIC)
public class LineaPedidoBD {
    private boolean fijarPrecio;
    private boolean suprimirPrecio;
    private boolean fijarArticulo;
    private boolean fijarObservaciones;
    private int idPrepedido;
    private int idArticulo;
    private String codArticulo;
    private boolean esMedidaEnKg;
    private boolean esCongelado;
    private float cantidadKg = -1;
    private int cantidadUd;
    private float precio;
    private String observaciones;
}
