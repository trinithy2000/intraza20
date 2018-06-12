package intraza.com.interfaz.datosDB;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Almacena los datos que definen un parametro de configuracion en la BD
 */
@Data
@AllArgsConstructor(access = AccessLevel.PUBLIC)
public class ParametroConfiguracionBD {
    private String nombre;
    private String valor;
    private String descripcion;
}
