package intraza.com.interfaz.datosDB;

/**
 * @author JLZS
 * <p/>
 * Define la tabla "rutero" en la BD
 */

public class TablaRutero {
    public static final String NOMBRE_TABLA = "rutero";
    public static final String SQL_BORRA_TABLA = "drop table " + NOMBRE_TABLA + ";";
    public static final String KEY_CAMPO_ID = "id";
    public static final int POS_KEY_CAMPO_ID = 0;
    public static final String KEY_CAMPO_ID_ARTICULO = "id_art";
    public static final int POS_KEY_CAMPO_ID_ARTICULO = 1;
    public static final String KEY_CAMPO_CODIGO_ARTICULO = "codigo_art";
    public static final int POS_KEY_CAMPO_CODIGO_ARTICULO = 2;
    public static final String KEY_CAMPO_ID_CLIENTE = "id_cliente";
    public static final int POS_KEY_CAMPO_ID_CLIENTE = 3;
    public static final String CAMPO_FECHA_ULTIMA_COMPRA = "fecha_ultima_compra";
    public static final int POS_CAMPO_FECHA_ULTIMA_COMPRA = 4;
    public static final String CAMPO_UNIDADES_ULTIMA_COMPRA = "unidades_ultima_compra";
    public static final int POS_CAMPO_UNIDADES_ULTIMA_COMPRA = 5;
    public static final String CAMPO_CANTIDAD_ULTIMA_COMPRA = "cantidad_ultima_compra";
    public static final int POS_CAMPO_CANTIDAD_ULTIMA_COMPRA = 6;
    public static final String CAMPO_UNIDADES_TOTAL_ANIO = "unidades_total_anio";
    public static final int POS_CAMPO_UNIDADES_TOTAL_ANIO = 7;
    public static final String CAMPO_CANTIDAD_TOTAL_ANIO = "cantidad_total_anio";
    public static final int POS_CAMPO_CANTIDAD_TOTAL_ANIO = 8;
    public static final String CAMPO_TARIFA_ULTIMA_COMPRA = "tarifa_ultima_compra";
    public static final int POS_CAMPO_TARIFA_ULTIMA_COMPRA = 9;
    public static final String CAMPO_TARIFA_CLIENTE = "tarifa_cliente";
    public static final int POS_CAMPO_TARIFA_CLIENTE = 10;
    public static final String CAMPO_OBSERVACIONES = "observaciones";
    public static final int POS_CAMPO_OBSERVACIONES = 11;
    public static final String SQL_CREA_TABLA = "create table " + NOMBRE_TABLA + " \n" +
            "( \n" +
            KEY_CAMPO_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, \n" +
            KEY_CAMPO_ID_ARTICULO + " INTEGER NOT NULL, \n" +
            KEY_CAMPO_CODIGO_ARTICULO + " NVARCHAR(10) NOT NULL, \n" +
            KEY_CAMPO_ID_CLIENTE + " INTEGER NOT NULL, \n" +
            CAMPO_FECHA_ULTIMA_COMPRA + " TEXT NOT NULL, \n" +
            CAMPO_UNIDADES_ULTIMA_COMPRA + " INTEGER NOT NULL, \n" +
            CAMPO_CANTIDAD_ULTIMA_COMPRA + " REAL NOT NULL, \n" +
            CAMPO_UNIDADES_TOTAL_ANIO + " INTEGER NOT NULL, \n" +
            CAMPO_CANTIDAD_TOTAL_ANIO + " REAL NOT NULL, \n" +
            CAMPO_TARIFA_ULTIMA_COMPRA + " REAL NOT NULL, \n" +
            CAMPO_TARIFA_CLIENTE + " REAL NOT NULL, \n" +
            CAMPO_OBSERVACIONES + " TEXT NOT NULL, \n" +
            "FOREIGN KEY(" + KEY_CAMPO_ID_ARTICULO + ") REFERENCES " + TablaArticulo.NOMBRE_TABLA + "(" + TablaArticulo.KEY_CAMPO_ID_ARTICULO + "), \n" +
            "FOREIGN KEY(" + KEY_CAMPO_CODIGO_ARTICULO + ") REFERENCES " + TablaArticulo.NOMBRE_TABLA + "(" + TablaArticulo.KEY_CAMPO_CODIGO_ARTICULO + "), \n" +
            "FOREIGN KEY(" + KEY_CAMPO_ID_CLIENTE + ") REFERENCES " + TablaCliente.NOMBRE_TABLA + "(" + TablaCliente.KEY_CAMPO_ID_CLIENTE + ") \n" +
            ");";
    public static final int NUM_CAMPOS = 12;
}
