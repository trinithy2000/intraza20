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
public class DatosRuteroSincroSimple implements Serializable {

    String codigoArticulo;
    Integer idCliente;
    Integer unidadesTotalAnio;
    Double pesoTotalAnio;
    Double precioCliente;
}
