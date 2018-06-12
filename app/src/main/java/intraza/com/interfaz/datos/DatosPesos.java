package intraza.com.interfaz.datos;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created by JuanIgnacio on 02/06/2015 on  19:11.
 */
@Data
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@EqualsAndHashCode(exclude = "iskg")
public class DatosPesos {
    private int id;
    private String codigo;
    private int idCliente;
    private boolean iskg;
    private float pesototal;
    private int unidadestotales;
}
