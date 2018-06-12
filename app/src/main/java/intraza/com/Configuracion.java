package intraza.com;

import android.content.Context;
import android.database.Cursor;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Properties;

import intraza.com.interfaz.datosDB.TablaConfiguracion;
import intraza.com.interfaz.task.utils.AssetsPropertyReader;
import intraza.com.interfaz.datosDB.AdaptadorBD;

/**
 * Clase utilizada para recuperar los parametros de configuracion, de un fichero
 */
public class Configuracion {

    private static volatile Configuracion instance;

    private Properties p;
    private Properties d;
    private String uri;

    private Configuracion() {
    }

    public static Configuracion getInstance() {
        if (instance == null) {
            synchronized (Configuracion.class) {
                if (instance == null) {
                    instance = new Configuracion();
                }
            }
        }

        return instance;
    }


    public static final String NOMBRE_PARAMETRO_ULTIMA_FECHA_SINCRONIZACION = "ULTIMA_FECHA_SINCRONIZACION";
    public static final String VALOR_PARAMETRO_ULTIMA_FECHA_SINCRONIZACION = "--/--/-- --:--:--";


    public static final String PROPERTIES = "intraza.properties";
    public static final String DATE_PROPERTIES = "date.properties";

    //Para invocar a los WS de sincronizacion con InTraza
    private static final String NOMBRE_WS_REST_TOTALES = "/totales";
    private static final String NOMBRE_WS_REST_ARTICULO = "/articulos";
    private static final String NOMBRE_WS_REST_CLIENTE = "/clientes";
    private static final String NOMBRE_WS_REST_RUTERO_TOTAL = "/ruteros_total";
    private static final String NOMBRE_WS_REST_RUTERO = "/ruteros";
    private static final String NOMBRE_WS_REST_RUTERO_FRACCIONADO = "/ruteros_fraccionados";
    private static final String NOMBRE_WS_REST_RUTERO_FRACCIONADO_TARIFA = "/ruteros_fraccionados_tarifa";
    private static final String NOMBRE_WS_REST_RUTERO_TOTAL_FRACCIONADO = "/ruteros_total_fraccionados";
    private static final String NOMBRE_WS_REST_RUTERO_TARIFA_CLIENTE = "/rutero_tarifa_cliente";
    private static final String NOMBRE_WS_REST_RUTERO_TARIFA_DEFECTO = "/rutero_tarifa_defecto";
    private static final String NOMBRE_WS_REST_RUTERO_DATOS = "/rutero_datos";
    //Para invocar a los WS de envio de informacion a InTraza
    private static final String NOMBRE_WS_REST_PREPEDIDO = "/prepedido";

    /**
     * Devuelve la ultima fecha en que se sincronizo la BD de la tablet con la BD de InTraza.
     *
     * @param context Context
     * @return String con la fecha de sincronización en formato DD/MM/YYYY hh:mm:ss
     */


    public void preparaPropiedades(final Context context) {
        AssetsPropertyReader assetsPropertyReader = new AssetsPropertyReader(context);
        p = assetsPropertyReader.getProperties(PROPERTIES);
        uri = p.getProperty("URI_WEB_SERVICES_SINCRONIZACION");
    }

    /**
     * Devuelve la ultima fecha en que se sincronizo la BD de la tablet con la BD de InTraza.
     *
     * @param context Context
     * @return String con la fecha de sincronizaci�n en formato DD/MM/YYYY hh:mm:ss
     */

    public static String dameUltimaFechaSincronizacion(final Context context) {
        String fecha = VALOR_PARAMETRO_ULTIMA_FECHA_SINCRONIZACION;
        final AdaptadorBD db = new AdaptadorBD(context);
        final Cursor cursorParametro;

        db.abrir();

        cursorParametro = db.obtenerParametroConfiguracion(NOMBRE_PARAMETRO_ULTIMA_FECHA_SINCRONIZACION);

        //Si tenemos resultado de la consulta...
        if (cursorParametro.moveToFirst()) {
            fecha = cursorParametro.getString(TablaConfiguracion.POS_CAMPO_VALOR);
        }

        db.cerrar();

        return fecha;
    }

    /**
     * Pone la fecha actual (en formato DD/MM/YYYY hh:mm:ss) como la ultima fecha de sincronizacion
     *
     * @param context Context
     */

    public static void ponFechaActualComoUltimaFechaSincronizacion(final Context context) {
        final AdaptadorBD db = new AdaptadorBD(context);
        db.abrir();
        db.actualizarParametroConfiguracion(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", new Locale("es", "ES")).format(new Date()));
        db.cerrar();
    }

    /**
     * Devuelve el timeout en segundos de los Web Service REST de sincronizacion con InTraza
     *
     * @return el timeout en segundos
     */

    public int dameTimeoutWebServices() {
        return Integer.parseInt(p.getProperty("TIMEOUT_WEB_SERVICES_SINCRONIZACION"));
    }

    public String dameUriWebServicesSincronizacion() {
        return uri;
    }

    public String dameUriWebServicesSincronizacionTotalRegistros() {
        return dameUriWebServicesSincronizacion() + NOMBRE_WS_REST_TOTALES;
    }

    public String dameUriWebServicesSincronizacionArticulo() {
        return dameUriWebServicesSincronizacion() + NOMBRE_WS_REST_ARTICULO;
    }

    public String dameUriWebServicesSincronizacionCliente() {
        return dameUriWebServicesSincronizacion() + NOMBRE_WS_REST_CLIENTE;
    }

    public String dameUriWebServicesSincronizacionRuteroTotal() {
        return dameUriWebServicesSincronizacion() + NOMBRE_WS_REST_RUTERO_TOTAL;
    }

    public String dameUriWebServicesSincronizacionRutero() {
        return dameUriWebServicesSincronizacion() + NOMBRE_WS_REST_RUTERO;
    }

    public String dameUriWebServicesSincronizacionRuteroFraccinado(final int start, final int end) {
        return dameUriWebServicesSincronizacion() + NOMBRE_WS_REST_RUTERO_FRACCIONADO + "?start=" + start + "&end=" + end;
    }

    public String dameUriWebServicesSincronizacionRuteroFraccinadoTotal(final int start, final int end) {
        return dameUriWebServicesSincronizacion() + NOMBRE_WS_REST_RUTERO_TOTAL_FRACCIONADO + "?start=" + start + "&end=" + end;
    }

    public String dameUriWebServicesSincronizacionRuteroDatos(final int idCliente, final String codigoArticulo) {
        return dameUriWebServicesSincronizacion() + NOMBRE_WS_REST_RUTERO_DATOS + "?idCliente=" + idCliente + "&codigoArticulo=" + codigoArticulo;
    }


    public String dameUriWebServicesSincronizacionRuteroFraccionado(final int start, final int end) {
        return dameUriWebServicesSincronizacion() + NOMBRE_WS_REST_RUTERO_FRACCIONADO + "?start=" + start + "&end=" + end;
    }

    public String dameUriWebServicesSincronizacionRuteroFraccionadoTarifa(final int start, final int end) {
        return dameUriWebServicesSincronizacion() + NOMBRE_WS_REST_RUTERO_FRACCIONADO_TARIFA + "?start=" + start + "&end=" + end;
    }

    public String dameUriWebServicesSincronizacionRuteroTotalFraccionado(final int start, final int end) {
        return dameUriWebServicesSincronizacion() + NOMBRE_WS_REST_RUTERO_TOTAL_FRACCIONADO + "?start=" + start + "&end=" + end;
    }

    public String dameUriWebServicesSincronizacionRutero(final int idCliente) {
        return dameUriWebServicesSincronizacion() + NOMBRE_WS_REST_RUTERO + "?idCliente=" + idCliente;
    }

    public String dameUriWebServicesSincronizacionRuteroTarifaCliente(final int idCliente, final String codigoArticulo) {
        return dameUriWebServicesSincronizacion() + NOMBRE_WS_REST_RUTERO_TARIFA_CLIENTE + "?idCliente=" + idCliente + "&codigoArticulo=" + codigoArticulo;
    }

    public String dameUriWebServicesSincronizacionRuteroTarifaDefecto(final String codigoArticulo) {
        return dameUriWebServicesSincronizacion() + NOMBRE_WS_REST_RUTERO_TARIFA_DEFECTO + "?codigoArticulo=" + codigoArticulo;
    }

    public String dameUriWebServicesEnvioPrepedido() {
        return dameUriWebServicesSincronizacion() + NOMBRE_WS_REST_PREPEDIDO;
    }


    /**
     * Devuelve si esta permitido o no crear una linea de pedido con precio 0
     *
     * @return true si esta permitido o false en caso contrario
     */

    public boolean estaPermitidoLineasPedidoConPrecio0() {
        return !"SI".equals(p.getProperty("PERMITIR_PRECIO_0").toUpperCase());
    }


    /**
     * Devuelve si esta permitido o no crear una linea de pedido con precio
     *
     * @return true si esta permitido o false en caso contrario
     */

    public int dameNumDiasAntiguedadMarcaTarifaDefecto() {
        return Integer.parseInt(p.getProperty("NUM_DIAS_ANTIGUEDAD_MARCA_TARIFA_DEFECTO"));
    }

    /**
     * Devuelve el usuario para la validacion con lo WS Rest
     *
     * @return el usuario o null sino se puede recuperar
     */

    public String dameUsuarioWS() {
        return p.getProperty("USUARIO_WS");
    }

    /**
     * Devuelve el password para la validacion con lo WS Rest
     *
     * @return el usuario o null sino se puede recuperar
     */

    public String damePasswordWS() {
        return p.getProperty("PASSWORD_WS");
    }

    /**
     * Devuelve si esta permitido o no sincronizar con intraza cuando hay pedidos pendientes de enviar
     *
     * @return true si esta permitido o false en caso contrario
     */

    public boolean estaPermitidoSincronizacionConPedidosPendientes() {
        return "SI".equals(p.getProperty("PERMITIR_SINCRONIZAR_CON_PEDIDOS_PENDIENTES").toUpperCase());
    }

    /**
     * Devuelve si el codigo usado para indicar las lineas de rutero ocultas
     *
     * @return codigo de linea de rutero oculta
     */

    public int dameCodigoStatusLineasRuteroOcultas() {
        return Integer.parseInt(p.getProperty("CODIGO_STATUS_LINEA_RUTERO_OCULTA"));
    }

    public int dameIntervaloDividirRutero() {
        return Integer.parseInt(p.getProperty("INTERVALO_DIVISOR_RUTERO"));
    }
}
