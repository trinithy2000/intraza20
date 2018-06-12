package intraza.com.interfaz.datosDB;

/**
 * @author JLZS
 *         <p/>
 *         Define la tabla "observacion" en la BD
 */

public class TablaObservacion {
    public static final String NOMBRE_TABLA = "observacion";
    public static final String SQL_BORRA_TABLA = "drop table " + NOMBRE_TABLA + ";";
    public static final String KEY_CAMPO_ID_OBSERVACION = "id_observacion";
    public static final int POS_KEY_CAMPO_ID_OBSERVACION = 0;
    public static final String CAMPO_TIPO = "tipo";
    public static final int POS_CAMPO_TIPO = 1;
    public static final String CAMPO_DESCRIPCION = "descripcion";
    public static final int POS_CAMPO_DESCRIPCION = 2;
    public static final int NUM_CAMPOS = 3;
    //Define los tipos del enumerado del campo TIPO de observacion
    public static final int TIPO_PREPEDIDO = 1;
    public static final int TIPO_PREPEDIDO_ITEM = 2;
    public static final String SQL_CREA_TABLA = "create table " + NOMBRE_TABLA + " \n" +
            "( \n" +
            KEY_CAMPO_ID_OBSERVACION + " INTEGER PRIMARY KEY NOT NULL, \n" +
            CAMPO_TIPO + " enum(" + TIPO_PREPEDIDO + "," + TIPO_PREPEDIDO_ITEM + ") DEFAULT 1 NOT NULL, \n" +
            CAMPO_DESCRIPCION + " TEXT NOT NULL \n" +
            ");";
}
