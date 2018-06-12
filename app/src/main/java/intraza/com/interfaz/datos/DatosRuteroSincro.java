package intraza.com.interfaz.datos;

import java.io.Serializable;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Created by JuanIgnacio on 20/05/2015 on  19:38.
 */
@Data
@AllArgsConstructor(access = AccessLevel.PUBLIC)
public class DatosRuteroSincro implements Serializable {

    String codigoArticulo;
    Integer idCliente;
    String fechaPedido;
    Integer unidades;
    Double peso;
    Integer unidadesTotalAnio;
    Double pesoTotalAnio;
    Double precio;
    Double precioCliente;
    String observacionesItem;
    Integer status;

}
