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
@EqualsAndHashCode(exclude = "precioDefecto")
public class DatosTarifas {

    private int id;
    private String idArticulo;
    private int idCliente;
    private Double precioDefecto;
}
