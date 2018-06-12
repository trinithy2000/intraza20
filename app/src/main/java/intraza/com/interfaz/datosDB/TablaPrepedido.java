package intraza.com.interfaz.datosDB;

/**
 * @author JLZS
 *         <p/>
 *         Define la tabla "prepedido" en la BD
 */

public class TablaPrepedido {
    public static final String NOMBRE_TABLA = "prepedido";
    public static final String SQL_BORRA_TABLA = "drop table " + NOMBRE_TABLA + ";";
    public static final String KEY_CAMPO_ID_PREPEDIDO = "id_prepedido";
    public static final int POS_KEY_CAMPO_ID_PREPEDIDO = 0;
    public static final String CAMPO_ID_CLIENTE = "id_cliente";
    public static final int POS_CAMPO_ID_CLIENTE = 1;
    public static final String CAMPO_FECHA_PREPEDIDO = "fecha_prepedido";
    public static final int POS_CAMPO_FECHA_PREPEDIDO = 2;
    public static final String CAMPO_FECHA_ENTREGA = "fecha_entrega";
    public static final int POS_CAMPO_FECHA_ENTREGA = 3;
    public static final String CAMPO_OBSERVACIONES = "observaciones";
    public static final int POS_CAMPO_OBSERVACIONES = 4;
    public static final String CAMPO_FIJAR_OBSERVACIONES = "fijar_observaciones";
    public static final int POS_CAMPO_FIJAR_OBSERVACIONES = 5;
    public static final String CAMPO_DESCUENTO_ESPECIAL = "descuento_especial";
    public static final String SQL_CREA_TABLA = "create table " + NOMBRE_TABLA + " \n" +
            "( \n" +
            KEY_CAMPO_ID_PREPEDIDO + " INTEGER PRIMARY KEY NOT NULL, \n" +
            CAMPO_ID_CLIENTE + " INTEGER NOT NULL, \n" +
            CAMPO_FECHA_PREPEDIDO + " TEXT NOT NULL, \n" +
            CAMPO_FECHA_ENTREGA + " TEXT NOT NULL, \n" +
            CAMPO_OBSERVACIONES + " TEXT NULL, \n" +
            CAMPO_FIJAR_OBSERVACIONES + " BOOLEAN DEFAULT FALSE, \n" +
            CAMPO_DESCUENTO_ESPECIAL + " INTEGER NOT NULL DEFAULT 0, \n" +
            "FOREIGN KEY(" + CAMPO_ID_CLIENTE + ") REFERENCES " + TablaCliente.NOMBRE_TABLA + "(" + TablaCliente.KEY_CAMPO_ID_CLIENTE + ") \n" +
            ");";
    public static final int POS_CAMPO_DESCUENTO_ESPECIAL = 6;
    public static final int NUM_CAMPOS = 7;
}
