package intraza.com.interfaz.datosDB;

/**
 * @author JLZS
 *         <p/>
 *         Define la tabla "cliente" en la BD
 */

@SuppressWarnings("ALL")
public class TablaCliente {
    public static final String NOMBRE_TABLA = "cliente";
    public static final String SQL_BORRA_TABLA = "drop table " + TablaCliente.NOMBRE_TABLA + ";";
    public static final String KEY_CAMPO_ID_CLIENTE = "id_cliente";
    public static final int POS_KEY_CAMPO_ID_CLIENTE = 0;
    public static final String CAMPO_NOMBRE_CLIENTE = "nombre_cliente";
    public static final int POS_CAMPO_NOMBRE_CLIENTE = 1;
    public static final String CAMPO_DESCRIPCION = "descripcion";
    public static final int POS_CAMPO_OBSERVACIONES_PREPEDIDO = 2;
    public static final String CAMPO_TELEFONO = "telefono";
    public static final String SQL_CREA_TABLA = "create table " + TablaCliente.NOMBRE_TABLA + " \n" +
            "( \n" +
            TablaCliente.KEY_CAMPO_ID_CLIENTE + " INTEGER PRIMARY KEY NOT NULL, \n" +
            TablaCliente.CAMPO_NOMBRE_CLIENTE + " NVARCHAR(50) NOT NULL, \n" +
            TablaCliente.CAMPO_DESCRIPCION + " TEXT NULL, \n" +
            TablaCliente.CAMPO_TELEFONO + " TEXT NULL \n" +
            ");";
    public static final int POS_CAMPO_TELEFONO = 3;
    public static final int NUM_CAMPOS = 4;
}
