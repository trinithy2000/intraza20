package intraza.com.interfaz.datosDB;

/**
 * @author JLZS
 *         <p/>
 *         Define la tabla "configuracion" en la BD
 */

@SuppressWarnings("ALL")
public class TablaConfiguracion {
    public static final String NOMBRE_TABLA = "configuracion";
    public static final String SQL_BORRA_TABLA = "drop table " + TablaConfiguracion.NOMBRE_TABLA + ";";
    public static final String KEY_CAMPO_NOMBRE_PARAMETRO = "nombre_parametro";
    public static final int POS_KEY_CAMPO_NOMBRE_PARAMETRO = 0;
    public static final String CAMPO_VALOR = "valor";
    public static final int POS_CAMPO_VALOR = 1;
    public static final String CAMPO_DESCRIPCION = "descripcion";
    public static final int POS_CAMPO_DESCRIPCION = 2;
    public static final String CAMPO_ES_EDITABLE = "esEditable";
    public static final String SQL_CREA_TABLA = "create table " + TablaConfiguracion.NOMBRE_TABLA + " \n" +
            "( \n" +
            TablaConfiguracion.KEY_CAMPO_NOMBRE_PARAMETRO + " TEXT PRIMARY KEY NOT NULL, \n" +
            TablaConfiguracion.CAMPO_VALOR + " TEXT NOT NULL, \n" +
            TablaConfiguracion.CAMPO_DESCRIPCION + " TEXT NOT NULL, \n" +
            TablaConfiguracion.CAMPO_ES_EDITABLE + " BOOLEAN DEFAULT true \n" +
            ");";
    public static final int POS_CAMPO_ES_EDITABLE = 3;
    public static final int NUM_CAMPOS = 4;
}
