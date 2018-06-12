package intraza.com.interfaz.datosDB;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import intraza.com.Configuracion;
import intraza.com.R.string;


/**
 * Created by JuanIgnacio on 02/07/2015 on  19:55.
 */
public class DataBaseHelper extends SQLiteOpenHelper {

    private static String DB_PATH = "";
    private static String DB_NAME = "InTraza";// Database name

    private Context contexto;

    public DataBaseHelper(Context context) {
        super(context, DB_PATH + DB_NAME, null, 1);// 1? its Database Version

        if (android.os.Build.VERSION.SDK_INT >= 4.2) {
            DB_PATH = context.getApplicationInfo().dataDir + "/databases/";
        } else {
            DB_PATH = "/data/data/" + context.getPackageName() + "/databases/";
        }
        contexto=context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TablaConfiguracion.SQL_CREA_TABLA);

        ContentValues valoresIniciales = new ContentValues();
        valoresIniciales.put(TablaConfiguracion.KEY_CAMPO_NOMBRE_PARAMETRO, Configuracion.NOMBRE_PARAMETRO_ULTIMA_FECHA_SINCRONIZACION);
        valoresIniciales.put(TablaConfiguracion.CAMPO_VALOR, Configuracion.VALOR_PARAMETRO_ULTIMA_FECHA_SINCRONIZACION);
        valoresIniciales.put(TablaConfiguracion.CAMPO_DESCRIPCION, contexto.getApplicationContext().getResources().getText(string.DESCRIPCION_PARAMETRO_ULTIMA_FECHA_SINCRONIZACION).toString());
        db.insert(TablaConfiguracion.NOMBRE_TABLA, null, valoresIniciales);

        db.execSQL(TablaArticulo.SQL_CREA_TABLA);
        db.execSQL(TablaCliente.SQL_CREA_TABLA);
        db.execSQL(TablaPrepedido.SQL_CREA_TABLA);
        db.execSQL(TablaPrepedidoItem.SQL_CREA_TABLA);
        db.execSQL(TablaRutero.SQL_CREA_TABLA);
        db.execSQL(TablaObservacion.SQL_CREA_TABLA);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String TAG = "DataBaseHelper";
        Log.w(TAG, "Actualizando base de datos de version " + oldVersion + " a "
                + newVersion + ", lo que destruira todos los viejos datos");
        db.execSQL(TablaObservacion.SQL_CREA_TABLA);
        db.execSQL(TablaRutero.SQL_CREA_TABLA);
        db.execSQL(TablaPrepedidoItem.SQL_CREA_TABLA);
        db.execSQL(TablaPrepedido.SQL_CREA_TABLA);
        db.execSQL(TablaCliente.SQL_CREA_TABLA);
        db.execSQL(TablaConfiguracion.SQL_CREA_TABLA);
        onCreate(db);
    }
}