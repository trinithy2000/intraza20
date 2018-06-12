package intraza.com.interfaz.pantallas;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.text.InputFilter;
import android.text.InputFilter.LengthFilter;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.ColorRes;
import org.androidannotations.annotations.res.DimensionPixelSizeRes;
import org.androidannotations.annotations.res.DrawableRes;
import org.androidannotations.annotations.res.StringRes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.List;

import intraza.com.interfaz.datos.DatosPedido;
import intraza.com.interfaz.datosDB.TablaArticulo;
import intraza.com.interfaz.datosDB.TablaCliente;
import intraza.com.interfaz.datosDB.TablaPrepedidoItem;
import intraza.com.interfaz.datosDB.TablaRutero;
import intraza.com.interfaz.dialogos.DialogoAvisoDatosSinGuardar_;
import intraza.com.interfaz.dialogos.DialogoDatosLineaPedido_;
import intraza.com.interfaz.dialogos.DialogoDatosNuevoArticuloRutero_;
import intraza.com.interfaz.dialogos.DialogoDatosPedido_;
import intraza.com.interfaz.task.utils.ReferenceComparator;
import intraza.com.interfaz.task.utils.Utils;
import intraza.com.interfaz.task.utils.WebServicesUtils;
import intraza.com.Configuracion;
import intraza.com.Constantes;
import intraza.com.interfaz.datos.DatosLineaPedido;
import intraza.com.interfaz.datosDB.AdaptadorBD;
import intraza.com.interfaz.datosDB.LineaPedidoBD;
import intraza.com.interfaz.datosDB.TablaPrepedido;
import intraza.com.interfaz.task.utils.ArticleComparator;
import intraza.com.interfaz.task.utils.DateComparator;

import intraza.com.R.layout;
import intraza.com.R.id;
import intraza.com.R;

import static android.view.View.OnClickListener;


@EActivity(layout.rutero)
public class PantallaRutero extends PantallaPadre {
    //Codigos de los subdialogos que se usan en la Activity
    private static final int DIALOGO_PIDE_DATOS_LINEA_PEDIDO = 0;
    private static final int DIALOGO_PIDE_DATOS_NUEVO_PEDIDO = 1;
    private static final int DIALOGO_PIDE_DATOS_PEDIDO = 2;
    private static final int DIALOGO_AVISO_DATOS_SIN_GUARDAR = 3;
    private static final int DIALOGO_AVISO_DATOS_SIN_GUARDAR_TECLA_ATRAS = 4;
    private static final int DIALOGO_PIDE_DATOS_NUEVO_ARTICULO_RUTERO = 5;

    @Extra("DATOS_PEDIDO")
    DatosPedido datosPedido;
    @Extra("ID_PEDIDO")
    int idPedido;
    @Extra("DATOS_LINEA_PEDIDO")
    DatosLineaPedido datosLineaPedido;
    @Extra("ES_CLON")
    boolean esClon;
    @Extra("PANTALLA_MODIFICACION")
    boolean esPantallaModificacionPedidoDos;
    @StringRes
    String TITULO_ALERT_ERROR_NO_SINCRONIZAR_PEDIDOS_PENDIENTES;
    @StringRes
    String MENSAJE_ALERT_ERROR_NO_SINCRONIZAR_PEDIDOS_PENDIENTES;
    @StringRes
    String Aceptar_M;
    @StringRes
    String MENSAJE_DATOS_PEDIDO_GUARDADOS;
    @StringRes
    String ERROR_GUARDAR_DATOS_PEDIDO;
    @StringRes
    String MENSAJE_PEDIDO_SIN_LINEAS_PEDIDO;
    @StringRes
    String pg_ruteros_RW;
    @StringRes
    String sincronizando;
    @ColorRes
    int colorFilaDatoIntroducido;
    @ColorRes
    int colorFilaDatoAnterior;
    @ColorRes
    int colorTextoBotonDeshabilitadoTablaP;
    @ColorRes
    int colorTextoSinValorPedido;
    @ColorRes
    int colorTextoArticuloCongelado;
    @ColorRes
    int colorBotonPedidos;
    @DimensionPixelSizeRes
    int anchoFilaPedido;
    @DimensionPixelSizeRes
    int heightFilaDatosTabla;
    @DimensionPixelSizeRes
    int widthColReferencia;
    @DimensionPixelSizeRes
    int textSizeFilaDatosTabla;
    @DimensionPixelSizeRes
    int widthColArticulo;
    @DimensionPixelSizeRes
    int widthColFechaAnterior;
    @DimensionPixelSizeRes
    int widthColTarifaLista;
    @DimensionPixelSizeRes
    int widthColTarifaAnterior;
    @DimensionPixelSizeRes
    int widthColCantidadKg;
    @DimensionPixelSizeRes
    int widthColCantidadAnterior;
    @DimensionPixelSizeRes
    int widthColTarifa;
    @DimensionPixelSizeRes
    int widthColBoton;

    @DrawableRes
    Drawable tabla_celda_button;

    //El widget que forma la tabla en pantalla
    @ViewById
    TableLayout ruteroTableLayout;

    @ViewById
    RelativeLayout marcoP;

    @ViewById
    Button botonClienteP;
    @ViewById
    Button botonFechaEntregaP;
    @ViewById
    Button botonObservacionesP;
    @ViewById
    Button botonGuardarP;
    @ViewById
    Button botonSincronizacionP;

    @ViewById
    ToggleButton botonOcultarRuteroP;

    @ViewById
    TextView textoFechaP;
    @ViewById
    TextView textoIdP;
    @ViewById
    TextView textoPrecioTotalP;
    @ViewById
    TextView columnaReferenciaP;
    @ViewById
    TextView columnaArticuloP;
    @ViewById
    Button botonNuevoArticuloP;
    @ViewById
    TextView columnaUltimaFechaP;
    @ViewById
    TextView columnaCantidadAnteriorP;
    @ViewById
    TextView columnaKilogramosP;
    @ViewById
    TextView columnaTarifaP;
    @ViewById
    TextView columnaTarifaListaP;

    boolean esPantallaModificacionPedido;

    private boolean esInverso;

    int maxLines = 2;

    private int lineNumber = 1;

    ProgressDialog mDialog;

    private boolean message;

    //Contendra todos los TextView que forman la tabla de linea de pedidos de cliente y los datos de la linea de pedido, la key sera el ID de la View
    private Hashtable<Integer, Object> viewsTablaRutero;

    //Almacena los datos de las lineas de pedido que se reflejan en la pantalla del rutero
    private List<DatosLineaPedido> lineasPedido;

    //Almacena los datos de las lineas de pedido que hay en la BD, se usa para una modificiacion de pedido
    private List<LineaPedidoBD> lineasPedidoBD;

    //Almacena los datos de las lineas de pedido que el usuario ha querido suprimir
    private List<DatosLineaPedido> lineasPedidoSuprimir;

    //Almacena las observaciones por defecto para el pedido configuradas en intraza
    private String observacionesDefectoPedido;

    private View viewLineaRutero;

    private int dWidth;

    private Configuracion config;

    Handler handler = new Handler(Looper.getMainLooper()) {

        @Override
        public void handleMessage(Message msg) {
            PantallaRutero.this.alertaMensajeSincronizacionCorrecta();
        }
    };

    public void alertaMensajeSincronizacionCorrecta() {
        String mensaje = MENSAJE_ALERT_ERROR_NO_SINCRONIZAR_PEDIDOS_PENDIENTES + " \n Para el Cliente --> " + datosPedido.getCliente();
        final AlertDialog.Builder popup = new AlertDialog.Builder(this);
        popup.setTitle(TITULO_ALERT_ERROR_NO_SINCRONIZAR_PEDIDOS_PENDIENTES);
        popup.setMessage(mensaje);
        popup.setPositiveButton(Aceptar_M, null);
        popup.show();
        botonSincronizacionP.setEnabled(false);
        botonSincronizacionP.setTextColor(colorTextoBotonDeshabilitadoTablaP);
    }

    DialogInterface.OnDismissListener postExecute = new OnDismissListener() {

        @Override
        public void onDismiss(DialogInterface dialogInterface) {
            if (!message) {
                lineNumber = 1;
                cargaRuteroEnPantalla(datosPedido.getIdCliente());
                message = false;
            }
            botonSincronizacionP.setEnabled(false);
            botonSincronizacionP.setTextColor(colorTextoBotonDeshabilitadoTablaP);
            habilitaClickEnActivity(true);
        }
    };

    @AfterViews
    void init() {

        config = Configuracion.getInstance();
        config.preparaPropiedades(this);
        calculaLongitudCabecera();
        //Inicializamos las variables miembro necesarias
        lineasPedido = new ArrayList<>();
        lineasPedidoSuprimir = new ArrayList<>();
        viewsTablaRutero = new Hashtable<>();
        esInverso = false;
        message = false;

        //Si no hay datos de pedido, es porque esta activiy se inicio para modificar o consultar los datos de un pedido desde la pantalla de pedidos
        if (null == datosPedido) {
            marcoP.setBackgroundColor(colorBotonPedidos);
            datosPedido = consultaDatosPedidoBD(idPedido);
            esPantallaModificacionPedido = esPantallaModificacionPedidoDos;
        }
        cargaDatosPedido();

        //Si estamos en una pantalla de modificacion de pedido, no permitimos cambiar de cliente
        if (esPantallaModificacionPedido) {
            botonClienteP.setEnabled(false);
            botonClienteP.setTextColor(colorTextoBotonDeshabilitadoTablaP);
            botonSincronizacionP.setEnabled(false);
            botonSincronizacionP.setTextColor(colorTextoBotonDeshabilitadoTablaP);
        }
        //Cargamos el rutero
        cargaRuteroEnPantalla(datosPedido.getIdCliente());
        if (esPantallaModificacionPedido) {
            datosPedido.setHayDatosPedidoSinGuardar(false);
        }
    }

    void calculaLongitudCabecera() {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        dWidth = displaymetrics.widthPixels;
        columnaReferenciaP.setWidth((dWidth * 11) / 100);
        columnaArticuloP.setWidth((dWidth * 18) / 100);
        botonNuevoArticuloP.setWidth((dWidth * 4) / 100);
        columnaUltimaFechaP.setWidth((dWidth * 15) / 100);
        columnaCantidadAnteriorP.setWidth((dWidth * 16) / 100);
        columnaKilogramosP.setWidth((dWidth * 12) / 100);
        columnaTarifaP.setWidth((dWidth * 12) / 100);
        columnaTarifaListaP.setWidth((dWidth * 12) / 100);
    }

    @Click(id.botonClienteP)
    void cargaSubdialogoDatos() {
        if (!esPantallaModificacionPedido) {
            // Comprueba si hay que mostrar un aviso al usuario de perdida de informacion al cargar el nuevo rutero. Este se mostrara cuando tengamos
            // datos sin guardar en el pedido y contenga lineas de pedido, ya que no tiene sentido guardar un pedido sin lineas de pedido.
            if (datosPedido.isHayDatosPedidoSinGuardar() && datosPedido.isHayLineasPedido()) {
                subdialogoAvisoDatosSinGuardar();
            } else {
                subdialogoDatosNuevoPedido();
            }
        }
    }

    /**
     * Cuando se pulse la tecla "atras", si hay cambios pendientes el usuario debe indicar si quiere o no guardarlos
     */

    @Override
    public boolean onKeyDown(final int keyCode, @NonNull final KeyEvent event) {
        // Comprueba si hay que mostrar un aviso al usuario de perdida de informacion al salir de la pantalla. Este se mostrara cuando tengamos
        // datos sin guardar en el pedido y contenga lineas de pedido, ya que no tiene sentido guardar un pedido sin lineas de pedido.
        if ((KeyEvent.KEYCODE_BACK == keyCode) && datosPedido.isHayDatosPedidoSinGuardar() && datosPedido.isHayLineasPedido()) {
            subdialogoAvisoDatosSinGuardarTeclaAtras();
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    /**
     * Guarda en la BD de la tablet los datos del pedido y las lineas de pedido, pendientes de guardar.
     */
    @Click(id.botonGuardarP)
    void guardarDatosPedidoBD() {
        boolean hayError;
        final AdaptadorBD db = new AdaptadorBD(this);
        int descuentoEspecial = 0;
        db.abrir();

        // Guardamos los datos generales del pedido
        hayError = !db.guardaPrepedido(datosPedido.getIdPedido(), datosPedido.getIdCliente(),
                datosPedido.getFechaEntrega(), datosPedido.getFechaPedido(),
                datosPedido.getObservaciones(), datosPedido.isFijarObservaciones(), descuentoEspecial);

        if (!hayError) {
            // Recorremos las lineas de pedido que se hayan suprimido para borrarlas
            for (DatosLineaPedido dato : lineasPedidoSuprimir) {
                //En esta caso no controlamos que haya error, ya que si el usuario borra una linea de pedido que no estaba previamente en
                //la BD se produciria un error
                db.borrarPrepedidoItem(datosPedido.getIdPedido(), dato.getCodArticulo());
            }

            //Limpiamos el vector que almacena las lineas de pedido a borrar
            lineasPedidoSuprimir = new ArrayList<>();

            // Recorremos las lineas de pedido para guardar en la BD aquellas que tengan algun cambio pendiente de guardar
            for (int i = 1; i <= (viewsTablaRutero.size() / Constantes.COLUMNAS_TOTALES_LP); i++) {
                final DatosLineaPedido datosLineaPedido = (DatosLineaPedido) viewsTablaRutero.get((i * 100) + Constantes.COLUMNA_DATOS_LP);

                if (datosLineaPedido.isHayCambiosSinGuardar()) {

                    hayError = !db.guardaPrepedidoItem(datosPedido.getIdPedido(), datosLineaPedido.getIdArticulo(), datosLineaPedido.getCodArticulo(),
                            datosLineaPedido.getCantidadKg(), datosLineaPedido.getCantidadUd(),
                            datosLineaPedido.getTarifaCliente(), datosLineaPedido.getObservaciones(),
                            datosLineaPedido.isFijarTarifa(), datosLineaPedido.isSuprimirTarifa(), datosLineaPedido.isFijarArticulo(), datosLineaPedido.isFijarObservaciones());

                    if (!hayError) {
                        //Indicamos que esa linea de pedidos no tiene cambios pendientes ya que los hemos guardado en la BD
                        ((DatosLineaPedido) viewsTablaRutero.get((i * 100) + Constantes.COLUMNA_DATOS_LP)).setHayCambiosSinGuardar(false);
                    } else {
                        //En caso de error dejamos de hacer cosas en la BD
                        break;
                    }
                }
            }
        }

        db.cerrar();

        //Segun se haya producido error o no, hacemos las acciones correspondientes
        if (!hayError) {
            Toast.makeText(getBaseContext(), MENSAJE_DATOS_PEDIDO_GUARDADOS, Toast.LENGTH_SHORT).show();
            //El boton guardar se habilita solo cuando hay cambios pendientes de guardar
            inhabilitaBotonGuardar();
        } else {
            toastMensajeError();
        }

    }

    /**
     * Inhabilita el boton guardar e indica en los datos del pedido que no hay cambios pendiente de guardar
     */
    private void inhabilitaBotonGuardar() {
        botonGuardarP.setEnabled(false);
        //Indicamos que el pedido tiene guardados todos los cambios
        datosPedido.setHayDatosPedidoSinGuardar(false);
    }


    void habilitaBotonGuardar() {
        if (datosPedido.isHayLineasPedido()) {
            botonGuardarP.setEnabled(true);
            //Indicamos que el pedido tiene guardados todos los cambios
            datosPedido.setHayDatosPedidoSinGuardar(true);
        }
    }

    /**
     * Comprueba si hay que ocultar o mostrar las lineas de rutero en la pantalla
     *
     * @param v vista del boton que indica si se debe o no ocultar las lineas de rutero
     */
    @Click(id.botonOcultarRuteroP)
    void compruebaBotonOcultarRutero(final View v) {
        if (((ToggleButton) v).isChecked()) {
            ocultarLineasRutero();
        } else {
            mostrarLineasRutero();
        }
    }

    /**
     * Oculta de la pantalla las lineas de rutero, de forma que solo se muestran las lineas de pedido
     */
    private void ocultarLineasRutero() {
        final List<DatosLineaPedido> lineasRuteroPedido = new ArrayList<>();
        final List<DatosLineaPedido> lineasIniRuteroPedido = new ArrayList<>();

        //Eliminamos todas las fila en pantalla
        ruteroTableLayout.removeAllViews();

        //Obtenemos los datos de la linea de pedido que nos interesan, recorremos el rutero para obtener las lineas de pedido introducidas por el usuario,
        //el numero de filas es el numero de elementos / numeroColumnas
        for (int i = 1; i <= (viewsTablaRutero.size() / Constantes.COLUMNAS_TOTALES_LP); i++) {
            final DatosLineaPedido datosLineaPedido = (DatosLineaPedido) viewsTablaRutero.get((i * 100) + Constantes.COLUMNA_DATOS_LP);
            if (chekCantidad(datosLineaPedido)) {
                lineasRuteroPedido.add(datosLineaPedido);
                lineasIniRuteroPedido.add((DatosLineaPedido) viewsTablaRutero.get((i * 100) + Constantes.COLUMNA_DATOS_INICIALES_LP));
            }
        }
        //Una vez obtenidos los datos limpiamos, la HashTable que guarda las view de la pantalla y los datos de la linea de pedido
        viewsTablaRutero.clear();
        //Mostramos en pantalla las lineas de pedido
        muestraRutero(lineasRuteroPedido, lineasIniRuteroPedido);
    }

    private boolean chekCantidad(final DatosLineaPedido datosLineaPedido) {
        return (0 != Float.compare(-1f, datosLineaPedido.getCantidadKg())) || (0 != datosLineaPedido.getCantidadUd());
    }

    /**
     * Muestra en pantalla tanto las lineas de rutero como las de pedido
     */
    private void mostrarLineasRutero() {
        final List<DatosLineaPedido> lineasRuteroPedido = new ArrayList<>();
        final List<DatosLineaPedido> lineasIniRuteroPedido = new ArrayList<>();
        boolean estaLineaIncluida;

        //Eliminamos todas la filas en pantalla
        ruteroTableLayout.removeAllViews();

        //Recorremos las lineas de pedido del rutero, cogiendo los datos de la linea del pedido, si es el caso
        for (DatosLineaPedido dato : lineasPedido) {
            //Obtenemos los datos de la linea de pedido que nos interesan, recorremos el rutero para obtener las lineas de pedido introducidas por el usuario,
            //el numero de filas es el numero de elementos / numeroColumnas
            estaLineaIncluida = false;
            for (int i = 1; i <= (viewsTablaRutero.size() / Constantes.COLUMNAS_TOTALES_LP); i++) {
                final DatosLineaPedido datosLineaPedido = (DatosLineaPedido) viewsTablaRutero.get((i * 100) + Constantes.COLUMNA_DATOS_LP);
                //Si la referencia del articulo es la misma, y tiene datos en la linea de pedido los cogemos
                if (dato.getCodArticulo().equals(datosLineaPedido.getCodArticulo()) && (chekCantidad(datosLineaPedido))) {
                    lineasRuteroPedido.add(datosLineaPedido);
                    lineasIniRuteroPedido.add((DatosLineaPedido) viewsTablaRutero.get((i * 100) + Constantes.COLUMNA_DATOS_INICIALES_LP));
                    //Indicamos que ya se ha incluido la linea en el rutero y salimos del for, pues ya hemos terminado de buscar
                    estaLineaIncluida = true;
                    break;
                }
            }
            //Si la linea no tenia datos de pedido, incluimos los datos recuperados de la BD
            if (!estaLineaIncluida) {
                lineasRuteroPedido.add(dato);
                lineasIniRuteroPedido.add(dato);
            }
        }
        //Una vez obtenidos los datos limpiamos, la hashtable que guarda las view de la pantalla y los datos de la linea de pedido
        viewsTablaRutero.clear();
        //Mostramos en pantalla las lineas de pedido
        muestraRutero(lineasRuteroPedido, lineasIniRuteroPedido);
    }

    /**
     * Abre el subdialogo que solicita los datos del pedido
     */
    @Click({id.botonFechaEntregaP, id.botonObservacionesP})
    void subdialogoDatosPedido() {
        DialogoDatosPedido_.builder()
                .arg("DATOS_PEDIDO", datosPedido)
                .arg("OBSERVACIONES_DEFECTO", observacionesDefectoPedido)
                .arg("DIALOGO_PIDE_DATOS", DIALOGO_PIDE_DATOS_PEDIDO)
                .build().show(getFragmentManager(), null);
    }

    /**
     * Abre el subdialogo que solicita los datos del pedido
     */
    private void subdialogoDatosNuevoPedido() {
        DialogoDatosPedido_.builder()
                .arg("DIALOGO_PIDE_DATOS", DIALOGO_PIDE_DATOS_NUEVO_PEDIDO)
                .build().show(getFragmentManager(), null);
    }

    /**
     * Abre el subdialogo que solicita los datos de un articulo nuevo para al rutero
     */
    @Click(id.botonNuevoArticuloP)
    void subdialogoDatosNuevoArticuloRutero() {
        final String[] codigosArticulosRutero;

        //Creamos un array con los articulos que ya estan en el rutero de la pantalla, para que no se puedan incluir
        codigosArticulosRutero = new String[viewsTablaRutero.size() / Constantes.COLUMNAS_TOTALES_LP];
        for (int i = 1; i <= (viewsTablaRutero.size() / Constantes.COLUMNAS_TOTALES_LP); i++) {
            final DatosLineaPedido datosLineaRutero = (DatosLineaPedido) viewsTablaRutero.get((i * 100) + Constantes.COLUMNA_DATOS_INICIALES_LP);
            codigosArticulosRutero[i - 1] = datosLineaRutero.getCodArticulo();
        }
        //Para indicar al subdialogo el id cliente del rutero
        DialogoDatosNuevoArticuloRutero_.builder()
                .arg("ID_CLIENTE", datosPedido.getIdCliente())
                .arg("ARRAY_ARTICULOS_EN_RUTERO", codigosArticulosRutero)
                .arg("DIALOGO_DATOS_NUEVO_PEDIDO_RETORNO", DIALOGO_PIDE_DATOS_NUEVO_ARTICULO_RUTERO)
                .build().show(getFragmentManager(), null);
    }

    /**
     * Abre el subdialogo que solicita la confirmaci�n del usuario cuando tenemos datos en el pedido sin guardar
     */
    private void subdialogoAvisoDatosSinGuardar() {
        DialogoAvisoDatosSinGuardar_.builder()
                .arg("DIALOGO_RETORNA_RESULTADO", DIALOGO_AVISO_DATOS_SIN_GUARDAR)
                .build()
                .show(getFragmentManager(), null);
    }

    /**
     * Abre el subdialogo que solicita la confirmación del usuario cuando tenemos datos en el pedido sin guardar y se pulsa la tecla de ir atras
     */
    private void subdialogoAvisoDatosSinGuardarTeclaAtras() {
        DialogoAvisoDatosSinGuardar_.builder()
                .arg("DIALOGO_RETORNA_RESULTADO", DIALOGO_AVISO_DATOS_SIN_GUARDAR_TECLA_ATRAS)
                .build()
                .show(getFragmentManager(), null);
    }

    /**
     * Carga los datos del pedido en la pantalla
     */
    private void cargaDatosPedido() {
        String clienteParaBoton = datosPedido.getCliente();

        textoFechaP.setText(Constantes.PREFIJO_TEXTO_FECHA_PEDIDO + datosPedido.getFechaPedido());
        textoIdP.setText(Constantes.PREFIJO_TEXTO_ID_PEDIDO + datosPedido.getIdPedido());
        textoPrecioTotalP.setText(Constantes.PREFIJO_TEXTO_PRECIO_TOTAL + Constantes.formatearFloat2Decimales.format(datosPedido.getPrecioTotal()) + Constantes.EURO);
        botonFechaEntregaP.setText(Constantes.PREFIJO_BOTON_FECHA_ENTREGA + datosPedido.getFechaEntrega());

        if (29 < clienteParaBoton.length()) {
            clienteParaBoton = clienteParaBoton.substring(0, 29);
        }

        botonClienteP.setText(Constantes.PREFIJO_BOTON_CLIENTE + clienteParaBoton);

    }

    /**
     * Cargamos el rutero en pantalla, para ello primero consultamos los datos del cliente en la BD, para obener el rutero
     * y luego lo mostramos en la tabla de rutero
     */
    private void cargaRuteroEnPantalla(final int idCliente) {
        //El boton guardar se habilita solo cuando hay cambios pendientes de guardar
        inhabilitaBotonGuardar();
        //Ponemos en off el boton de ocultar la lineas de rutero
        botonOcultarRuteroP.setChecked(false);
        //Eliminamos todas la filas en pantalla y la HashTable que contiene las view de los datos en pantalla
        ruteroTableLayout.removeAllViews();
        viewsTablaRutero.clear();
        lineasPedido.clear();

        //Consultamos los datos del rutero en la BD y los mostramos en pantalla, actualizando en pantalla el precio total del pedido
        if (esPantallaModificacionPedido) {
            //Si es una modificacion de pedido, tenemos que inicializar el rutero con los datos de las lineas de pedido que teniamos en la BD
            inicializaRuteroConLineasPedido(consultaDatosRuteroBD(idCliente), lineasPedidoBD);
        } else {
            inicializaRutero(consultaDatosRuteroBD(idCliente));
        }
        refrescaPrecioTotalPedido();
        habilitaBotonGuardar();
    }

    /**
     * Obtenemos mediante una consulta a la BD de la tablet, los datos del pedido
     *
     * @return Vector de DatosLineaPedido
     */
    private DatosPedido consultaDatosPedidoBD(final int idPedido) {
        DatosPedido datosPedido = null;
        final AdaptadorBD db = new AdaptadorBD(this);
        final Cursor cursorPedido;
        Cursor cursorLineasPedido;
        //Datos para pedido
        int idPrepedidoP;
        int idClienteP;
        String clienteP;
        int diaFechaPedidoP;
        int mesFechaPedidoP;
        int anioFechaPedidoP;
        int diaFechaEntregaP;
        int mesFechaEntregaP;
        int anioFechaEntregaP;
        String observacionesP;
        boolean fijarObservacionesP;
        int descuentoEspecial;

        //Constiene los datos de una fecha descompuesta, es decir, para una fecha DD-MM-YYYY, contiene 3 elementos,
        //en la posicion 0 tiene DD, en la posicion 1 tiene MM y en la posicion 2 tiene YYYY.
        String[] fechaDescompuesta;

        db.abrir();

        cursorPedido = db.obtenerPrepedidoConDatosCliente(idPedido);

        //Si tenemos resultado de la consulta...
        if (cursorPedido.moveToFirst()) {
            //Recorremos el cursor para obtener los datos de los pedidos recuperados
            do {
                idPrepedidoP = cursorPedido.getInt(TablaPrepedido.POS_KEY_CAMPO_ID_PREPEDIDO);
                idClienteP = cursorPedido.getInt(TablaPrepedido.POS_CAMPO_ID_CLIENTE);
                clienteP = cursorPedido.getString(TablaPrepedido.NUM_CAMPOS + TablaCliente.POS_CAMPO_NOMBRE_CLIENTE);
                fechaDescompuesta = cursorPedido.getString(TablaPrepedido.POS_CAMPO_FECHA_PREPEDIDO).split(Constantes.SEPARADOR_FECHA);
                diaFechaPedidoP = Integer.parseInt(fechaDescompuesta[0]);
                mesFechaPedidoP = Integer.parseInt(fechaDescompuesta[1]);
                anioFechaPedidoP = Integer.parseInt(fechaDescompuesta[2]);
                fechaDescompuesta = cursorPedido.getString(TablaPrepedido.POS_CAMPO_FECHA_ENTREGA).split(Constantes.SEPARADOR_FECHA);
                diaFechaEntregaP = Integer.parseInt(fechaDescompuesta[0]);
                mesFechaEntregaP = Integer.parseInt(fechaDescompuesta[1]);
                anioFechaEntregaP = Integer.parseInt(fechaDescompuesta[2]);
                observacionesP = cursorPedido.getString(TablaPrepedido.POS_CAMPO_OBSERVACIONES);
                fijarObservacionesP = 1 == cursorPedido.getInt(TablaPrepedido.POS_CAMPO_FIJAR_OBSERVACIONES);

                descuentoEspecial = cursorPedido.getInt(TablaPrepedido.POS_CAMPO_DESCUENTO_ESPECIAL);
                //Guardamos en una variable global las observaciones por defecto del pedido, ya que la tenemos que enviar al subdialogo que solicita los datos
                //del pedido al usuario
                observacionesDefectoPedido = cursorPedido.getString(TablaPrepedido.NUM_CAMPOS + TablaCliente.POS_CAMPO_OBSERVACIONES_PREPEDIDO);
                //Ahora para cada pedido recuperamos sus lineas de pedido
                lineasPedidoBD = new ArrayList<>();
                cursorLineasPedido = db.obtenerTodosLosPrepedidosItemDelPrepedido(idPrepedidoP);

                if (cursorLineasPedido.moveToFirst()) {
                    //Recorremos el cursor para obtener los datos de las lineas de pedido
                    addLineasDePedidoBD(db, cursorLineasPedido);
                }

                datosPedido = new DatosPedido(idPrepedidoP, idClienteP, clienteP, diaFechaPedidoP, mesFechaPedidoP,
                        anioFechaPedidoP, diaFechaEntregaP, mesFechaEntregaP,
                        anioFechaEntregaP, 0f, observacionesP, fijarObservacionesP, descuentoEspecial, false, false);

            } while (cursorPedido.moveToNext());
        }

        db.cerrar();

        return datosPedido;
    }

    private void addLineasDePedidoBD(final AdaptadorBD db, final Cursor cursorLineasPedido) {
        int idPrepedidoLP;
        String codArticuloLP;
        int idArticuloLP;
        float cantidadKgLP;
        int cantidadUdLP;
        float tarifaClienteLP;
        String observacionesLP;
        boolean fijarTarifaLP;
        boolean suprimirTarifaLP;
        boolean fijarArticuloLP;
        boolean fijarObservacionesLP;
        Cursor cursorArticulo;
        boolean esMedidaKgLP;
        boolean esCongeladoLP;
        do {
            idPrepedidoLP = cursorLineasPedido.getInt(TablaPrepedidoItem.POS_CAMPO_ID_PREPEDIDO);
            codArticuloLP = cursorLineasPedido.getString(TablaPrepedidoItem.POS_CAMPO_CODIGO_ARTICULO);
            idArticuloLP = cursorLineasPedido.getInt(TablaPrepedidoItem.POS_CAMPO_ID_ARTICULO);
            cantidadKgLP = cursorLineasPedido.getFloat(TablaPrepedidoItem.POS_CAMPO_CANTIDAD_KG);
            cantidadUdLP = cursorLineasPedido.getInt(TablaPrepedidoItem.POS_CAMPO_CANTIDAD_UD);
            tarifaClienteLP = cursorLineasPedido.getFloat(TablaPrepedidoItem.POS_CAMPO_PRECIO);
            observacionesLP = cursorLineasPedido.getString(TablaPrepedidoItem.POS_CAMPO_OBSERVACIONES);
            fijarTarifaLP = 1 == cursorLineasPedido.getInt(TablaPrepedidoItem.POS_CAMPO_FIJAR_PRECIO);
            suprimirTarifaLP = 1 == cursorLineasPedido.getInt(TablaPrepedidoItem.POS_CAMPO_SUPRIMIR_PRECIO);
            fijarArticuloLP = 1 == cursorLineasPedido.getInt(TablaPrepedidoItem.POS_CAMPO_FIJAR_ARTICULO);
            fijarObservacionesLP = 1 == cursorLineasPedido.getInt(TablaPrepedidoItem.POS_CAMPO_FIJAR_OBSERVACIONES);

            //Hacemos una consulta al articulo para obtener su medida por defecto
            //Por si esta clonado, le quitamos la marca
            cursorArticulo = db.obtenerArticulo(codArticuloLP.split(Constantes.CARACTER_OBLIGATORIO_MARCA_CLON_CODIGO_ARTICULO)[0]);

            esMedidaKgLP = 1 == cursorArticulo.getInt(TablaArticulo.POS_CAMPO_ES_KG);
            //Comprobamos si el articulo es congelado
            esCongeladoLP = 1 == cursorArticulo.getInt(TablaArticulo.POS_CAMPO_ES_CONGELADO);

            lineasPedidoBD.add(new LineaPedidoBD(fijarTarifaLP, suprimirTarifaLP, fijarArticuloLP, fijarObservacionesLP, idPrepedidoLP, idArticuloLP, codArticuloLP, esMedidaKgLP, esCongeladoLP, cantidadKgLP, cantidadUdLP,
                    tarifaClienteLP, observacionesLP));

        } while (cursorLineasPedido.moveToNext());
    }

    /**
     * Obtenemos mediante una consulta a la BD de la tablet, las lineas de pedido que forman el rutero del cliente
     *
     * @param idCliente del rutero a obtener
     * @return Vector de DatosLineaPedido
     */
    private List<DatosLineaPedido> consultaDatosRuteroBD(final int idCliente) {
        final List<DatosLineaPedido> lineasPedidoRutero = new ArrayList<>();
        final AdaptadorBD db = new AdaptadorBD(this);
        final Cursor cursorRutero;

        //Obtenemos las lineas de rutero del cliente
        db.abrir();

        cursorRutero = db.obtenerTodosLosRuterosDelClienteConArticulo(idCliente);

        //Recorremos el cursor con las lineas de pedido de la BD para formar las lineas de pedido del rutero a mostrar en pantalla
        if (cursorRutero.moveToFirst()) {
            do {
                String idArticuloAux = cursorRutero.getString(TablaRutero.POS_KEY_CAMPO_ID_ARTICULO);
                final int idArticulo = Integer.parseInt(TextUtils.isEmpty(idArticuloAux) ? "0" : idArticuloAux);

                final String codArticulo = cursorRutero.getString(TablaRutero.POS_KEY_CAMPO_CODIGO_ARTICULO);
                final String articulo = cursorRutero.getString(TablaRutero.NUM_CAMPOS + TablaArticulo.POS_CAMPO_NOMBRE);
                String medida = Constantes.KILOGRAMOS;

                if ("0".equals(cursorRutero.getString(TablaRutero.NUM_CAMPOS + TablaArticulo.POS_CAMPO_ES_KG))) {
                    medida = Constantes.UNIDADES;
                }

                boolean esCongelado = false;

                if ("1".equals(cursorRutero.getString(TablaRutero.NUM_CAMPOS + TablaArticulo.POS_CAMPO_ES_CONGELADO))) {
                    esCongelado = true;
                }

                final String ultimaFecha = cursorRutero.getString(TablaRutero.POS_CAMPO_FECHA_ULTIMA_COMPRA);
                String ultimaUnidadesAux = cursorRutero.getString(TablaRutero.POS_CAMPO_UNIDADES_ULTIMA_COMPRA);
                final int ultimaUnidades = Integer.parseInt(TextUtils.isEmpty(ultimaUnidadesAux) ? "0" : ultimaUnidadesAux);
                String ultimaCantidadAux = cursorRutero.getString(TablaRutero.POS_CAMPO_CANTIDAD_ULTIMA_COMPRA);
                final float ultimaCantidad = Float.parseFloat(TextUtils.isEmpty(ultimaCantidadAux) ? "0" : ultimaCantidadAux);
                String unidadesTotalAnioAux = cursorRutero.getString(TablaRutero.POS_CAMPO_UNIDADES_TOTAL_ANIO);
                final int unidadesTotalAnio = Integer.parseInt(TextUtils.isEmpty(unidadesTotalAnioAux) ? "0" : unidadesTotalAnioAux);
                String cantidadTotalAnioAux = cursorRutero.getString(TablaRutero.POS_CAMPO_CANTIDAD_TOTAL_ANIO);
                final float cantidadTotalAnio = Float.parseFloat(TextUtils.isEmpty(cantidadTotalAnioAux) ? "0" : cantidadTotalAnioAux);
                String ultimaTarifaAux = cursorRutero.getString(TablaRutero.POS_CAMPO_TARIFA_ULTIMA_COMPRA);
                final float ultimaTarifa = Float.parseFloat(TextUtils.isEmpty(ultimaTarifaAux) ? "0" : ultimaTarifaAux);

                final float cantidadKg = -1f;
                final int cantidadUd = 0;
                String tarifaClienteAux = cursorRutero.getString(TablaRutero.POS_CAMPO_TARIFA_CLIENTE);
                final float tarifaCliente = Float.parseFloat(TextUtils.isEmpty(tarifaClienteAux) ? "0" : tarifaClienteAux);
                String tarifaListaAux = cursorRutero.getString(TablaRutero.NUM_CAMPOS + TablaArticulo.POS_CAMPO_TARIFA_DEFECTO);
                final float tarifaLista = Float.parseFloat(TextUtils.isEmpty(tarifaListaAux) ? "0" : tarifaListaAux);

                final String fechaCambioTarifaLista = cursorRutero.getString(TablaRutero.NUM_CAMPOS + TablaArticulo.POS_CAMPO_FECHA_CAMBIO_TARIFA_DEFECTO);
                final String observaciones = cursorRutero.getString(TablaRutero.POS_CAMPO_OBSERVACIONES);

                lineasPedidoRutero.add(new DatosLineaPedido(idArticulo, codArticulo, articulo, medida, esCongelado, ultimaFecha, ultimaUnidades,
                        ultimaCantidad, unidadesTotalAnio, cantidadTotalAnio, ultimaTarifa, cantidadKg, cantidadUd,
                        tarifaCliente, tarifaLista, fechaCambioTarifaLista, observaciones, false, false, false, false, false));
            } while (cursorRutero.moveToNext());
        }

        db.cerrar();
        return lineasPedidoRutero;
    }

    /**
     * Presenta la pantalla inicial con los datos el rutero inicializados con los datos de las lineas de pedido que hay en la BD
     *
     * @param lineasRutero   Vector
     * @param lineasPedidoBD Vector
     */
    private void inicializaRuteroConLineasPedido(final List<DatosLineaPedido> lineasRutero, final List<LineaPedidoBD> lineasPedidoBD) {
        final List<DatosLineaPedido> lineasRuteroConPedido = new ArrayList<>();
        DatosLineaPedido datosInicialesRutero;
        DatosLineaPedido datosLineaPedido;
        LineaPedidoBD lineaPedidoBD;

        if (!lineasPedidoBD.isEmpty()) {
            //Indicamos en el pedido que hay lineas de pedido
            datosPedido.setHayLineasPedido(true);
        }

        //Primero ponemos las lineas de pedido de articulos que no estuvieran en el rutero
        for (LineaPedidoBD dato : lineasPedidoBD) {
            if (!estaArticuloEnRutero(lineasRutero, dato.getCodArticulo())) {
                //Creamos una linea de rutero inicial para que se incluya esta linea de pedido en el rutero de pantalla.
                datosInicialesRutero = inicializaLineaRuteroConDatosArticuroBD(dato.getCodArticulo());

                //Si fuera nulo es que el articulo no existe por que se borro en una sincrnozacion con intraza, inicializamos los datos
                //para que al menos se muestre la linea de pedido
                if (null == datosInicialesRutero) {
                    datosInicialesRutero = new DatosLineaPedido(dato.getIdArticulo(), dato.getCodArticulo(), "", "", false, Constantes.SIN_FECHA_ANTERIOR_LINEA_PEDIDO,
                            0, 0f, 0, 0f, -1f, 0f, 0, 0f, 0f, "", "", false, false, false, false, false);
                }

                //Lo insertamos en la posicion 0 del vector para que asi las lineas de pedido sin rutero aparezcan las primera en pantalla
                lineasRutero.add(0, datosInicialesRutero);
            }
        }

        //Recorremos las linea de rutero, para comprobar con cada una, si tenemos datos de linea de pedido, y si asi es, inicializarlos
        for (DatosLineaPedido dato : lineasRutero) {
            //Se debe crear un nuevo objeto, sino se referencia al mismo
            datosLineaPedido = new DatosLineaPedido(
                    dato.getIdArticulo(),
                    dato.getCodArticulo(),
                    dato.getArticulo(), dato.getMedida(),
                    dato.isCongelado(), dato.getUltimaFecha(),
                    dato.getUltimaUnidades(), dato.getUltimaCantidad(),
                    dato.getUnidadesTotalAnio(), dato.getCantidadTotalAnio(),
                    dato.getUltimaTarifa(), dato.getCantidadKg(),
                    dato.getCantidadUd(), dato.getTarifaCliente(),
                    dato.getTarifaLista(), dato.getFechaCambioTarifaLista(),
                    dato.getObservaciones(), false, false, false, false, false);

            lineaPedidoBD = buscaLineaPedidoBD(lineasPedidoBD, datosLineaPedido.getCodArticulo());

            if (null != lineaPedidoBD) {
                //Si tenemos datos de pedido para esta linea de rutero, los cogemos
                datosLineaPedido.setMedida(lineaPedidoBD.isEsMedidaEnKg() ? Constantes.KILOGRAMOS : Constantes.UNIDADES);
                datosLineaPedido.setCongelado(lineaPedidoBD.isEsCongelado());
                datosLineaPedido.setCantidadKg(lineaPedidoBD.getCantidadKg());
                datosLineaPedido.setCantidadUd(lineaPedidoBD.getCantidadUd());
                datosLineaPedido.setTarifaCliente(lineaPedidoBD.getPrecio());
                datosLineaPedido.setObservaciones(lineaPedidoBD.getObservaciones());
                datosLineaPedido.setFijarTarifa(lineaPedidoBD.isFijarPrecio());
                datosLineaPedido.setSuprimirTarifa(lineaPedidoBD.isSuprimirPrecio());
                datosLineaPedido.setFijarArticulo(lineaPedidoBD.isFijarArticulo());
                datosLineaPedido.setFijarObservaciones(lineaPedidoBD.isFijarObservaciones());
            }
            lineasRuteroConPedido.add(datosLineaPedido);
        }
        muestraRuteroModificacion(lineasRuteroConPedido, lineasRutero);
    }

    /**
     * Inicializa una linea de rutero con los datos de un articulo en la BD
     *
     * @param codArticulo String
     * @return los datos de la linea de pedido iniciales.
     */
    private DatosLineaPedido inicializaLineaRuteroConDatosArticuroBD(final String codArticulo) {
        DatosLineaPedido dlp = null;
        final AdaptadorBD db = new AdaptadorBD(this);
        final Cursor cursorArticulos;
        String medida = Constantes.KILOGRAMOS;

        db.abrir();

        //Hacemos el split por si el codigo del articulo tiene el indicador de clon, sino no se obtendran los datos del articulo de la BD
        cursorArticulos = db.obtenerArticulo(codArticulo.split(Constantes.CARACTER_OBLIGATORIO_MARCA_CLON_CODIGO_ARTICULO)[0]);

        if (cursorArticulos.moveToFirst()) {
            do {
                if ("0".equals(cursorArticulos.getString(TablaArticulo.POS_CAMPO_ES_KG))) {
                    medida = Constantes.UNIDADES;
                }

                dlp = new DatosLineaPedido(Integer.parseInt(cursorArticulos.getString(TablaArticulo.POS_KEY_CAMPO_ID_ARTICULO)), codArticulo, cursorArticulos.getString(TablaArticulo.POS_CAMPO_NOMBRE), medida, false, Constantes.SIN_FECHA_ANTERIOR_LINEA_PEDIDO,
                        0, 0f, 0, 0f, -1f, 0f, 0, 0f, 0f, "", "", false, false, false, false, false);
            } while (cursorArticulos.moveToNext());
        }

        db.cerrar();
        return dlp;
    }


    /**
     * Comprueba si un articulo esta en el rutero del cliente.
     *
     * @param lineasRutero Vector
     * @param codArticulo  String
     * @return true si el articulo esta en el rutero, false en cualquier otro caso
     */
    private boolean estaArticuloEnRutero(final List<DatosLineaPedido> lineasRutero, final String codArticulo) {

        for (final DatosLineaPedido pedido : lineasRutero) {
            if (pedido.getCodArticulo().equals(codArticulo)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Busca en las lineas de pedido, los datos de la linea de pedido del articulo
     *
     * @param lineasPedidoBD de pedido
     * @param codArticulo    cuya linea de pedido queremos buscar
     * @return los datos de la linea de pedido o null si no se ha encontrado ninguna
     */
    private LineaPedidoBD buscaLineaPedidoBD(final List<LineaPedidoBD> lineasPedidoBD, final String codArticulo) {

        for (final LineaPedidoBD pedido : lineasPedidoBD) {
            if (pedido.getCodArticulo().equals(codArticulo)) {
                return pedido;
            }
        }
        return null;
    }

    /**
     * Presenta la pantalla inicial con los datos el rutero
     *
     * @param lineasRutero Vector
     */
    private void inicializaRutero(final List<DatosLineaPedido> lineasRutero) {
        for (DatosLineaPedido dato : lineasRutero) {
            //Se debe crear un nuevo objeto, sino se referencia al mismo
            final DatosLineaPedido datosLineaIniRutero = new DatosLineaPedido(
                    dato.getIdArticulo(),
                    dato.getCodArticulo(), dato.getArticulo(),
                    dato.getMedida(), dato.isCongelado(),
                    dato.getUltimaFecha(), dato.getUltimaUnidades(),
                    dato.getUltimaCantidad(), dato.getUnidadesTotalAnio(),
                    dato.getCantidadTotalAnio(), dato.getUltimaTarifa(),
                    dato.getCantidadKg(), dato.getCantidadUd(),
                    dato.getTarifaCliente(), dato.getTarifaLista(),
                    dato.getFechaCambioTarifaLista(), dato.getObservaciones(), false, false, false, false, false);
            insertaLineaInicialPedidoEnRutero(dato, datosLineaIniRutero);
        }
        if (lineasRutero.isEmpty()) {
            botonSincronizacionP.setEnabled(false);
            botonSincronizacionP.setTextColor(colorTextoBotonDeshabilitadoTablaP);
        }
        //Posicionamos la tabla de rutero en pantalla, al princio del scroll
        findViewById(id.scrollTablaLineasPedidoP).scrollTo(0, 0);
    }

    /**
     * Crea los datos de pedido, en la linea de rutero asociada a la view pasada como parametro
     *
     * @param v                view          de la fial al que pertenece la columna
     * @param datosLineaPedido DATOS_LINEA_PEDIDO
     */
    private void creaLineaPedido(final View v, final DatosLineaPedido datosLineaPedido) {
        guardaDatosLineaPedidoRuteroEnView(v, datosLineaPedido);

        //Indicamos en el pedido que hay lineas de pedido
        datosPedido.setHayLineasPedido(true);

        //Refrescamos los datos en la pantalla del rutero
        ponDatoNuevoPedidoFilaEnColumna(v, Constantes.COLUMNA_CANTIDAD_NUEVO_KG_LP, Constantes.formatearFloat3Decimales.format(datosLineaPedido.getCantidadKg()), colorFilaDatoIntroducido);
        ponDatoNuevoPedidoFilaEnColumna(v, Constantes.COLUMNA_TARIFA_NUEVO_LP, ponerMarcaFijarTarifa(datosLineaPedido.isFijarTarifa()) + Constantes.formatearFloat2Decimales.format(datosLineaPedido.getTarifaCliente()) + Constantes.EURO + Constantes.SEPARADOR_MEDIDA_TARIFA + datosLineaPedido.getMedida(), colorFilaDatoIntroducido);

        //Refrescamos el precio total del pedido, recalculandolo segun las lineas del pedido del rutero
        refrescaPrecioTotalPedido();
    }

    /**
     * Presenta en pantalla las lineas de pedido que conponen el rutero, cuando es una pantalla de modificacion de pedido
     *
     * @param lineasRutero    Vector
     * @param lineasIniRutero Vector
     */
    private void muestraRuteroModificacion(final List<DatosLineaPedido> lineasRutero, final List<DatosLineaPedido> lineasIniRutero) {
        for (int i = 0; i < lineasRutero.size(); i++) {
            insertaLineaInicialPedidoEnRutero(lineasRutero.get(i), lineasIniRutero.get(i));
        }
    }

    /**
     * Presenta en pantalla las lineas de pedido que conponen el rutero
     *
     * @param lineasRutero
     * @param lineasIniRutero
     */
    private void muestraRutero(final List<DatosLineaPedido> lineasRutero, final List<DatosLineaPedido> lineasIniRutero) {
        for (int i = 0; i < lineasRutero.size(); i++) {
            insertaLineaPedidoEnRutero(lineasRutero.get(i), lineasIniRutero.get(i));
        }
    }

    /**
     * Inserta una fila en la pantalla de inicializacion, ya sea de rutero o de linea de pedido
     *
     * @param lineaRutero    DATOS_LINEA_PEDIDO
     * @param lineaIniRutero DATOS_LINEA_PEDIDO
     */
    private void insertaLineaInicialPedidoEnRutero(final DatosLineaPedido lineaRutero, final DatosLineaPedido lineaIniRutero) {
        final int colorTextoFila = dameColorTextoFila(lineNumber);
        final int colorFila = dameColorFila(lineNumber++);
        final TableRow filaLP = new TableRow(this);

        filaLP.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
        filaLP.setBackgroundColor(colorFila);

        filaLP.addView(creaVistaReferencia(lineaRutero.getCodArticulo()));
        filaLP.addView(creaVistaArticulo(lineaRutero.getArticulo(), lineaRutero.isCongelado(), lineaRutero.isFijarArticulo(), colorTextoFila));
        filaLP.addView(creaVistaFechaAnterior(lineaRutero.getUltimaFecha()));
        filaLP.addView(creaVistaCantidadAnterior(lineaRutero.getUltimaUnidades(), lineaRutero.getUltimaCantidad(), lineaRutero.getUnidadesTotalAnio(), lineaRutero.getCantidadTotalAnio(), lineaRutero.getMedida()));

        //Comprobamos si tenemos datos de la linea de pedido
        filaLP.addView(creaVistaCantidadKg(lineaRutero.getCantidadKg(), lineaRutero.getCantidadUd(), getColorDato(lineaRutero)));
        filaLP.addView(creaVistaTarifaCliente(lineaRutero.getTarifaCliente(), lineaRutero.getMedida(), lineaRutero.isFijarTarifa(), getColorDato(lineaRutero)));
        filaLP.addView(creaVistaTarifaLista(lineaRutero.getTarifaLista(), lineaRutero.getFechaCambioTarifaLista(), lineaRutero.getMedida()));

        //Guardamos en la fila de rutero los datos de la linea de pedido
        guardaDatosLineaPedidoRutero(lineaRutero, Constantes.COLUMNA_DATOS_LP);
        //Guardamos en la fila de rutero los datos de la linea de pedido como iniciales, pues los necesitaremos tener
        guardaDatosLineaPedidoRutero(lineaIniRutero, Constantes.COLUMNA_DATOS_INICIALES_LP);
        //Guardamos los datos de esta linea de pedidos
        lineasPedido.add(lineaIniRutero);
        //Insertamos en la tabla de rutero de la pantalla la nueva linea de pedido
        ruteroTableLayout.addView(filaLP, new TableLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
    }

    /**
     * Inserta una fila en la pantalla, ya sea de rutero o de linea de pedido
     *
     * @param lineaRutero    DATOS_LINEA_PEDIDO
     * @param lineaIniRutero DATOS_LINEA_PEDIDO
     */
    private void insertaLineaPedidoEnRutero(final DatosLineaPedido lineaRutero, final DatosLineaPedido lineaIniRutero) {
        final int colorTextoFila = dameColorTextoFila(lineNumber);
        final int colorFila = dameColorFila(lineNumber++);
        final TableRow filaLP = new TableRow(this);


        filaLP.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, anchoFilaPedido));
        filaLP.setBackgroundColor(colorFila);

        filaLP.addView(creaVistaReferencia(lineaRutero.getCodArticulo()));
        filaLP.addView(creaVistaArticulo(lineaRutero.getArticulo(), lineaRutero.isCongelado(), lineaRutero.isFijarArticulo(), colorTextoFila));
        filaLP.addView(creaVistaFechaAnterior(lineaRutero.getUltimaFecha()));
        filaLP.addView(creaVistaCantidadAnterior(lineaRutero.getUltimaUnidades(), lineaRutero.getUltimaCantidad(), lineaRutero.getUnidadesTotalAnio(), lineaRutero.getCantidadTotalAnio(), lineaRutero.getMedida()));

        filaLP.addView(creaVistaCantidadKg(lineaRutero.getCantidadKg(), lineaRutero.getCantidadUd(), getColorDato(lineaRutero)));
        //Obtenemos el dato de la tarifa cliente
        filaLP.addView(creaVistaTarifaCliente(lineaRutero.getTarifaCliente(), lineaRutero.getMedida(), lineaRutero.isFijarTarifa(), getColorDato(lineaRutero)));
        //Obtenemos el dato de la tarifa lista
        filaLP.addView(creaVistaTarifaLista(lineaRutero.getTarifaLista(), lineaRutero.getFechaCambioTarifaLista(), lineaRutero.getMedida()));
        //Guardamos en la fila de rutero los datos de la linea de pedido
        guardaDatosLineaPedidoRutero(lineaRutero, Constantes.COLUMNA_DATOS_LP);
        //Guardamos en la fila de rutero los datos de la linea de pedido como iniciales, pues los necesitaremos tener
        guardaDatosLineaPedidoRutero(lineaIniRutero, Constantes.COLUMNA_DATOS_INICIALES_LP);
        //Insertamos en la tabla de rutero de la pantalla la nueva linea de pedido
        ruteroTableLayout.addView(filaLP, new TableLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
    }

    /**
     * Crea la celda De Referencia
     *
     * @param referencia
     * @return
     */
    private View creaVistaReferencia(final String referencia) {
        final TextView datoReferencia = new TextView(this);

        datoReferencia.setId(dameIdViewNuevo(Constantes.COLUMNA_REFERENCIA_LP));
        datoReferencia.setGravity(Gravity.CENTER);
        datoReferencia.setHeight(anchoFilaPedido);
        datoReferencia.setWidth((dWidth * 11) / 100);
        datoReferencia.setTextSize(textSizeFilaDatosTabla);
        datoReferencia.setTextColor(colorFilaDatoAnterior);
        //En caso de ser un codigo de articulo que indica que es un clon, al usuario solo se le muestra el codigo del articulo del original del clon
        datoReferencia.setText(referencia.split(Constantes.CARACTER_OBLIGATORIO_MARCA_CLON_CODIGO_ARTICULO)[0]);
        datoReferencia.setMaxLines(maxLines);
        //Guardamos la nueva vista para asi poder consultarla posteriormente
        viewsTablaRutero.put(datoReferencia.getId(), datoReferencia);

        return datoReferencia;
    }

    /**
     * Crea la vista de
     *
     * @param articulo
     * @param esCongelado
     * @param fijarArticulo
     * @param colorTextoFila
     * @return
     */

    private View creaVistaArticulo(final String articulo, final boolean esCongelado, final boolean fijarArticulo, final int colorTextoFila) {
        final TextView datoArticulo = new TextView(this);

        datoArticulo.setId(dameIdViewNuevo(Constantes.COLUMNA_ARTICULO_LP));
        datoArticulo.setGravity(Gravity.START);
        datoArticulo.setHeight(anchoFilaPedido);
        datoArticulo.setWidth((dWidth * 22) / 100);
        datoArticulo.setTextSize(textSizeFilaDatosTabla);
        datoArticulo.setTextColor(esCongelado ? colorTextoArticuloCongelado : colorTextoFila);
        datoArticulo.setMaxLines(maxLines);
        //El ancho maximo es de 22 caracteres
        final InputFilter[] filterArray = new InputFilter[1];
        filterArray[0] = new LengthFilter(50);
        datoArticulo.setFilters(filterArray);

        //Ponemos el dato
        datoArticulo.setText(ponerMarcaFijarArticulo(fijarArticulo) + articulo + ponerMarcaCongelado(esCongelado));
        datoArticulo.setClickable(true);

        datoArticulo.setOnClickListener(new OnClickListener() {
            public void onClick(final View v) {
                pideDatosLineaPedido(v);
            }
        });

        //Guardamos la nueva vista para asi poder consultarla posteriormente
        viewsTablaRutero.put(datoArticulo.getId(), datoArticulo);
        return datoArticulo;
    }

    /**
     * Celda de la fecha anterior
     *
     * @param fecha
     * @return
     */
    private View creaVistaFechaAnterior(final String fecha) {
        final TextView datoFechaAnterior = new TextView(this);
        String fechaaux = fecha;
        if (fechaaux.length() >= 10) {
            fechaaux = fecha.substring(0, 10);
            fechaaux = Utils.formatFecha(fechaaux, false);
        }
        datoFechaAnterior.setId(dameIdViewNuevo(Constantes.COLUMNA_FECHA_ULTIMO_LP));
        datoFechaAnterior.setGravity(Gravity.CENTER);
        datoFechaAnterior.setHeight(anchoFilaPedido);
        datoFechaAnterior.setWidth((dWidth * 15) / 100);
        datoFechaAnterior.setTextSize(textSizeFilaDatosTabla);
        datoFechaAnterior.setTextColor(colorFilaDatoAnterior);
        datoFechaAnterior.setText(fechaaux);
        datoFechaAnterior.setMaxLines(maxLines);

        //Guardamos la nueva vista para asi poder consultarla posteriormente
        viewsTablaRutero.put(datoFechaAnterior.getId(), datoFechaAnterior);

        return datoFechaAnterior;
    }

    private View creaVistaCantidadAnterior(final int unidades, final float cantidad, final int unidadesTotalAnio, final float cantidadTotalAnio, final String medida) {
        final TextView datoCantidadAnterior = new TextView(this);
        final String cantidadTotalFormateada;
        final String unidadesTotalFormateada;

        datoCantidadAnterior.setId(dameIdViewNuevo(Constantes.COLUMNA_CANTIDAD_ULTIMO_LP));
        datoCantidadAnterior.setGravity(Gravity.CENTER);
        datoCantidadAnterior.setWidth((dWidth * 16) / 100);
        datoCantidadAnterior.setTextSize(textSizeFilaDatosTabla);
        datoCantidadAnterior.setTextColor(colorFilaDatoAnterior);
        datoCantidadAnterior.setMaxLines(maxLines);
        datoCantidadAnterior.setHeight(anchoFilaPedido);

        //Si la cantidad total es mayor de 1000 se pasa a toneladas
        cantidadTotalFormateada = (1000 < cantidadTotalAnio) ?
                (Constantes.formatearFloat2Decimales.format(cantidadTotalAnio / 1000) + Constantes.MARCA_TONELADAS) :
                Constantes.formatearFloat2Decimales.format(cantidadTotalAnio) + Constantes.MARCA_KILOGRAMOS;

        unidadesTotalFormateada = (1000 < unidadesTotalAnio) ?
                (Constantes.formatearFloat2Decimales.format(unidadesTotalAnio / 1000) + Constantes.MARCA_UNIDADES) : Constantes.formatearFloat2Decimales.format(unidadesTotalAnio);

        datoCantidadAnterior.setText(medida.equalsIgnoreCase(Constantes.KILOGRAMOS) ? (Constantes.formatearFloat3Decimales.format(cantidad) + Constantes.MARCA_KILOGRAMOS + Constantes.SEPARADOR_CANTIDAD_TOTAL_ANIO + cantidadTotalFormateada) :
                (unidades + Constantes.SEPARADOR_CANTIDAD_TOTAL_ANIO + unidadesTotalFormateada));

        //Guardamos la nueva vista para asi poder consultarla posteriormente
        viewsTablaRutero.put(datoCantidadAnterior.getId(), datoCantidadAnterior);
        return datoCantidadAnterior;
    }

    private View creaVistaCantidadKg(final float cantidadKg, final int cantidadUd, final int colorTextoFila) {
        final TextView datoCantidad = new TextView(this);

        datoCantidad.setId(dameIdViewNuevo(Constantes.COLUMNA_CANTIDAD_NUEVO_KG_LP));
        datoCantidad.setGravity(Gravity.CENTER);
        datoCantidad.setHeight(anchoFilaPedido);
        datoCantidad.setWidth((dWidth * 12) / 100);
        datoCantidad.setTextSize(textSizeFilaDatosTabla);
        datoCantidad.setTextColor(colorTextoFila);
        datoCantidad.setMaxLines(maxLines);
        String kilos = (0 != Float.compare(-1f, cantidadKg)) ? Constantes.formatearFloat3Decimales.format(cantidadKg) : "0";
        String unidades = (-1 != cantidadUd) ? cantidadUd + "" : "0";
        datoCantidad.setText(unidades + Constantes.UNIDADES + "/" + kilos + Constantes.KILOGRAMOS);
        datoCantidad.setCursorVisible(false);
        datoCantidad.setClickable(true);
        datoCantidad.setOnClickListener(new OnClickListener() {
            public void onClick(final View v) {
                pideDatosLineaPedido(v);
            }
        });

        //Guardamos la nueva vista para asi poder consultarla posteriormente
        viewsTablaRutero.put(datoCantidad.getId(), datoCantidad);

        return datoCantidad;
    }

    private View creaVistaTarifaCliente(final float tarifa, final String medida, final boolean fijarTarifa, final int colorTextoFila) {
        final TextView datoTarifa = new TextView(this);

        datoTarifa.setId(dameIdViewNuevo(Constantes.COLUMNA_TARIFA_NUEVO_LP));
        datoTarifa.setGravity(Gravity.CENTER);
        datoTarifa.setHeight(anchoFilaPedido);
        datoTarifa.setWidth((dWidth * 12) / 100);
        datoTarifa.setTextSize(textSizeFilaDatosTabla);
        datoTarifa.setTextColor(colorTextoFila);
        log("TRAZA - tarifa (" + tarifa + ") - tarifa formateada (" + Constantes.formatearFloat2Decimales.format(tarifa) + ")");
        datoTarifa.setText(ponerMarcaFijarTarifa(fijarTarifa) + Constantes.formatearFloat2Decimales.format(tarifa) + Constantes.EURO + Constantes.SEPARADOR_MEDIDA_TARIFA + medida);
        datoTarifa.setMaxLines(maxLines);

        datoTarifa.setClickable(true);
        datoTarifa.setOnClickListener(new OnClickListener() {
            public void onClick(final View v) {
                pideDatosLineaPedido(v);
            }
        });

        //Guardamos la nueva vista para asi poder consultarla posteriormente
        viewsTablaRutero.put(datoTarifa.getId(), datoTarifa);

        return datoTarifa;
    }

    private View creaVistaTarifaLista(final float tarifaLista, final String fechaCambioTarifaLista, final String medida) {
        final TextView datoTarifaLista = new TextView(this);

        datoTarifaLista.setId(dameIdViewNuevo(Constantes.COLUMNA_TARIFA_LISTA_LP));
        datoTarifaLista.setGravity(Gravity.CENTER);
        datoTarifaLista.setHeight(anchoFilaPedido);
        datoTarifaLista.setWidth((dWidth * 12) / 10);
        datoTarifaLista.setTextSize(textSizeFilaDatosTabla);
        datoTarifaLista.setTextColor(colorFilaDatoAnterior);
        datoTarifaLista.setText(ponerMarcaCambioTarifaListaReciente(fechaCambioTarifaLista) + Constantes.formatearFloat2Decimales.format(tarifaLista) + Constantes.EURO + Constantes.SEPARADOR_MEDIDA_TARIFA + medida);
        datoTarifaLista.setMaxLines(maxLines);

        //Guardamos la nueva vista para asi poder consultarla posteriormente
        viewsTablaRutero.put(datoTarifaLista.getId(), datoTarifaLista);
        return datoTarifaLista;
    }

    /**
     * Comprueba si hay que poner la marca de cambio reciente en la tarifa por defecto.
     * Las fechas en la BD de la tablet se guardan como DD/MM/YYYY
     *
     * @param fecha de cambio de la tarifa por defecto
     * @return la marca si hay que poner la marca, cadena vacia en cualquier otro caso
     */
    private String ponerMarcaCambioTarifaListaReciente(final String fecha) {
        String marca = "";
        final String[] fechaDescompuesta;
        final GregorianCalendar calendarFechaCaducidadCambioReciente;
        if ((null != fecha) && !"null".equals(fecha.trim()) && !"".equals(fecha.trim())) {
            fechaDescompuesta = fecha.substring(0, 10).split(Constantes.SEPARADOR_FECHA);
            //A la fecha actual le sumamos los dias configurados para que se muestre la marca de cambio de tarifa por defecto,
            //asi obtenemos la fecha en que caduca la marca
            calendarFechaCaducidadCambioReciente = new GregorianCalendar(Integer.parseInt(fechaDescompuesta[2]), Integer.parseInt(fechaDescompuesta[1]), Integer.parseInt(fechaDescompuesta[0]));
            calendarFechaCaducidadCambioReciente.add(GregorianCalendar.DAY_OF_YEAR, config.dameNumDiasAntiguedadMarcaTarifaDefecto());
            //Comprobamos si la marca ha caducado
            if (calendarFechaCaducidadCambioReciente.after(new GregorianCalendar())) {
                marca = Constantes.MARCA_TARIFA_DEFECTO_CAMBIADA_RECIENTEMENTE;
            }
        }
        return marca;
    }

    /**
     * Para guardar los datos del pedido asociados con los datos presentados en pantalla
     *
     * @param datos   DATOS_LINEA_PEDIDO
     * @param columna de la tabla
     */
    private void guardaDatosLineaPedidoRutero(final DatosLineaPedido datos, final int columna) {
        //Guardamos la informacion en la tablaHash para asi poder consultarla posteriormente
        viewsTablaRutero.put(dameIdViewNuevo(columna), datos);

        //Tambien actualiza los datos en el Vector de pedidos
        for (int i = 0; i < lineasPedido.size(); i++) {
            if (lineasPedido.get(i).getCodArticulo().equals(datos.getCodArticulo())) {
                lineasPedido.remove(i);
                lineasPedido.add(i, datos);
                break;
            }
        }
    }

    private void pideDatosLineaPedido(final View v) {
        //Guardamos la vista que ha seleccionado el usuario para solicitar los datos del pedido
        viewLineaRutero = v;
        DialogoDatosLineaPedido_.builder()
                .arg("DATOS_LINEA_PEDIDO", dameDatosLineaPedidoRuteroEnView(v, Constantes.COLUMNA_DATOS_LP))
                .arg("OBSERVACIONES_DEFECTO", dameDatosLineaPedidoRuteroEnView(v, Constantes.COLUMNA_DATOS_INICIALES_LP).getObservaciones())
                .arg("DIALOGO_RETORNA_RESULTADO", DIALOGO_PIDE_DATOS_LINEA_PEDIDO)
                .build()
                .show(getFragmentManager(), null);
    }

    /**
     * Dado un codigo de articulo, obtiene el numero de clones que tiene el articulo
     *
     * @param codArticulo String
     * @return numero de clones del articulo
     */
    private int dameSiguienteNumeroDeClonParaElArticulo(final String codArticulo) {
        int numMayorClon = 0;

        //Recorremos todas la lineas de pedido para buscar el numero de clon mayor del articulo
        for (DatosLineaPedido dato : lineasPedido) {
            if (dato.getCodArticulo().startsWith(codArticulo.split(Constantes.CARACTER_OBLIGATORIO_MARCA_CLON_CODIGO_ARTICULO)[0] + Constantes.MARCA_CLON_CODIGO_ARTICULO)) {
                if (Integer.parseInt(dato.getCodArticulo().split(Constantes.CARACTER_OBLIGATORIO_MARCA_CLON_CODIGO_ARTICULO)[2]) > numMayorClon) {
                    numMayorClon = Integer.parseInt(dato.getCodArticulo().split(Constantes.CARACTER_OBLIGATORIO_MARCA_CLON_CODIGO_ARTICULO)[2]);
                }
            }
        }

        //El siguiente indice sera el mayor actual + 1
        return numMayorClon + 1;
    }

    @OnActivityResult(DIALOGO_PIDE_DATOS_LINEA_PEDIDO)
    void onResult(final int resultCode, final Intent data) {
        if (RESULT_OK == resultCode) {
            //Obtenemos los datos de la linea de pedido
            final DatosLineaPedido datosLineaPedido = data.getParcelableExtra("DATOS_LINEA_PEDIDO");
            //Recuperamos el valor que nos indica si este articulo es o no un clon
            final boolean esClon = data.getBooleanExtra("ES_CLON", false);

            if (esClon) {
                //Tomamos los datos iniciales de la linea de pedido para guardados en la linea del rutero del clon
                final DatosLineaPedido datosIniLineaPedido = dameDatosLineaPedidoRuteroEnView(viewLineaRutero, Constantes.COLUMNA_DATOS_INICIALES_LP);

                //Tenemos que crear una copia del objeto para que no se referencia al de la linea de pedido de la que se clono
                final DatosLineaPedido datosIniLineaPedidoClon = new DatosLineaPedido(
                        datosIniLineaPedido.getIdArticulo(),
                        datosIniLineaPedido.getCodArticulo(),
                        datosIniLineaPedido.getArticulo(),
                        datosIniLineaPedido.getMedida(),
                        datosIniLineaPedido.isCongelado(),
                        datosIniLineaPedido.getUltimaFecha(),
                        datosIniLineaPedido.getUltimaUnidades(),
                        datosIniLineaPedido.getUltimaCantidad(),
                        datosIniLineaPedido.getUnidadesTotalAnio(),
                        datosIniLineaPedido.getCantidadTotalAnio(),
                        datosIniLineaPedido.getUltimaTarifa(),
                        datosIniLineaPedido.getCantidadKg(),
                        datosIniLineaPedido.getCantidadUd(),
                        datosIniLineaPedido.getTarifaCliente(),
                        datosIniLineaPedido.getTarifaLista(),
                        datosIniLineaPedido.getFechaCambioTarifaLista(),
                        datosIniLineaPedido.getObservaciones(), false, false, false, false, false);

                //Le a�adimos un sufijo de clon al codigo del articulo para que se trate como un articulo independiente, aunque este sufijo
                //no se le muestra al usuario, es algo interno
                final String codigoArticuloClon = datosLineaPedido.getCodArticulo().split(Constantes.CARACTER_OBLIGATORIO_MARCA_CLON_CODIGO_ARTICULO)[0] + Constantes.MARCA_CLON_CODIGO_ARTICULO + dameSiguienteNumeroDeClonParaElArticulo(datosLineaPedido.getCodArticulo());
                datosLineaPedido.setCodArticulo(codigoArticuloClon);
                datosIniLineaPedidoClon.setCodArticulo(codigoArticuloClon);
                //Guardamos los datos como una nueva linea de pedido y posteriormente la insertamos en pantalla
                lineasPedido.add(datosLineaPedido);
                insertaLineaPedidoEnRutero(datosLineaPedido, datosIniLineaPedidoClon);
            } else {
                //Guardamos los datos en la linea de rutero asociada
                guardaDatosLineaPedidoRuteroEnView(viewLineaRutero, datosLineaPedido);
                //Refrescamos los datos en la pantalla del rutero
                ponDatoNuevoPedidoFilaEnColumna(viewLineaRutero, Constantes.COLUMNA_CANTIDAD_NUEVO_KG_LP, datosLineaPedido.getCantidadUd() + Constantes.SEPARADOR_MEDIDA_TARIFA + Constantes.formatearFloat3Decimales.format(datosLineaPedido.getCantidadKg()), colorFilaDatoIntroducido);
                ponDatoNuevoPedidoFilaEnColumna(viewLineaRutero, Constantes.COLUMNA_TARIFA_NUEVO_LP, ponerMarcaFijarTarifa(datosLineaPedido.isFijarTarifa()) + Constantes.formatearFloat2Decimales.format(datosLineaPedido.getTarifaCliente()) + Constantes.EURO + Constantes.SEPARADOR_MEDIDA_TARIFA + datosLineaPedido.getMedida(), colorFilaDatoIntroducido);
            }

            //Si hay cambios que guardar habilitamos el boton
            if (datosLineaPedido.isHayCambiosSinGuardar()) {
                //Indicamos en el pedido que hay lineas de pedido
                datosPedido.setHayLineasPedido(true);
                habilitaBotonGuardar();
            }
            //Refrescamos el precio total del pedido, recalculandolo segun las lineas del pedido del rutero
            refrescaPrecioTotalPedido();
        }

        //Cuando el usuario pulsa el boton ELIMINAR, para eliminar los datos que introdujo anteriormente de la linea de pedido
        if (RESULT_FIRST_USER == resultCode) {
            //Hay un cambio que hay que guardar
            habilitaBotonGuardar();
            //Tomamos como datos de la linea de pedido los iniciales guardados en la linea del rutero
            final DatosLineaPedido datosIniLineaPedido = dameDatosLineaPedidoRuteroEnView(viewLineaRutero, Constantes.COLUMNA_DATOS_INICIALES_LP);
            //Indicamos que hay un cambio sin guardar ya que el registro de la linea de pedido, se debe borrar de la BD
            //datosIniLineaPedido.setHayCambiosSinGuardar(true);
            //Almacenamos en el vector que indica las lineas de pedido a suprimir
            //Si la linea de pedido tiene cantidad 0 es porque teniamos cambios de la linea de pedido que hay que borrar de la BD
            lineasPedidoSuprimir.add(datosIniLineaPedido);
            guardaDatosLineaPedidoRuteroEnView(viewLineaRutero, datosIniLineaPedido);
            //Refrescamos los datos en la pantalla del rutero
            ponDatoNuevoPedidoFilaEnColumna(viewLineaRutero, Constantes.COLUMNA_CANTIDAD_NUEVO_KG_LP, Constantes.DATOS_NUEVO_PEDIDO_SIN_INTRODUCIR, colorTextoSinValorPedido);
            ponDatoNuevoPedidoFilaEnColumna(viewLineaRutero, Constantes.COLUMNA_TARIFA_NUEVO_LP, Constantes.formatearFloat2Decimales.format(datosIniLineaPedido.getTarifaCliente()) + Constantes.EURO + Constantes.SEPARADOR_MEDIDA_TARIFA + datosIniLineaPedido.getMedida(), colorTextoSinValorPedido);
            //Refrescamos el precio total del pedido, recalculandolo segun las lineas del pedido del rutero
            refrescaPrecioTotalPedido();

            //Si esta pulsado el boton que indica ocultar las lineas de rutero en la pantalla, la refrescamos para eliminar esta linea de pedido
            if (botonOcultarRuteroP.isChecked()) {
                ocultarLineasRutero();
            }

            //Comprobamos si nos hemos quedado sin lineas de pedido
            if (!hayLineasPedido()) {
                datosPedido.setHayLineasPedido(false);
                Toast.makeText(getBaseContext(), MENSAJE_PEDIDO_SIN_LINEAS_PEDIDO, Toast.LENGTH_SHORT).show();
            }
        }

        //Cuando el usuario pulsa el boton CANCELAR, en una ventana de solicitud de datos de linea de pedido de un nuevo articulo
        if (RESULT_CANCELED == resultCode) {
            //Si esta pulsado el boton que indica ocultar las lineas de rutero en la pantalla, la refrescamos para eliminar esta linea de pedido
            //sino, estaria sin contener datos
            if (botonOcultarRuteroP.isChecked()) {
                ocultarLineasRutero();
            }
        }
    }

    @OnActivityResult(DIALOGO_PIDE_DATOS_NUEVO_PEDIDO)
    void onResultDos(final int resultCode, final Intent data) {
        if (RESULT_OK == resultCode) {
            //Toast.makeText(getBaseContext(), Constantes.MENSAJE_PANTALLA_CARGAR_RUTERO, Toast.LENGTH_SHORT).show();
            //Obtenemos los datos del pedido
            datosPedido = data.getParcelableExtra("DATOS_PEDIDO");
            //Actualizamos la pantalla con los datos del nuevo pedido
            cargaDatosPedido();
            cargaRuteroEnPantalla(datosPedido.getIdCliente());
        }
    }

    @OnActivityResult(DIALOGO_PIDE_DATOS_PEDIDO)
    void onResultTres(final int resultCode, final Intent data) {
        if (RESULT_OK == resultCode) {
            //Obtenemos los datos del pedido
            datosPedido = data.getParcelableExtra("DATOS_PEDIDO");
            //Si hay cambios que guardar y tenemos lineas de pedido, habilitamos el boton
            if (datosPedido.isHayDatosPedidoSinGuardar() && datosPedido.isHayLineasPedido()) {
                habilitaBotonGuardar();
            }
            cargaDatosPedido();
        }
    }


    @OnActivityResult(DIALOGO_AVISO_DATOS_SIN_GUARDAR)
    void onResultCuatro(final int resultCode, final Intent data) {
        if (RESULT_OK == resultCode) {
            guardarDatosPedidoBD();
            subdialogoDatosNuevoPedido();
        }
        if (RESULT_FIRST_USER == resultCode) {
            subdialogoDatosNuevoPedido();
        }
    }


    @OnActivityResult(DIALOGO_AVISO_DATOS_SIN_GUARDAR_TECLA_ATRAS)
    void onResultCinco(final int resultCode, final Intent data) {
        //Cuando el usuario pulsa el boton SI, indicando que quiere guardar los datos del pedido antes de salir
        if (RESULT_OK == resultCode) {
            //Guardamos los datos del pedido
            guardarDatosPedidoBD();
        }

        //Cuando el usuario pulsa el boton SI o NO, indicando que quiere o no guardar los datos despues se finaliza la activity, a no ser
        //que haya querido cancelar la operacion de salir de la activity
        if (RESULT_CANCELED != resultCode) {
            //Finalizamos la Activity, pues se pulso la tecla atras
            finish();
        }
    }


    @OnActivityResult(DIALOGO_PIDE_DATOS_NUEVO_ARTICULO_RUTERO)
    void onResultSeis(final int resultCode, final Intent data) {
        //Cuando el usuario pulsa el boton ACEPTAR, para crear un nueva linea de pedido con un nuevo articulo
        if (RESULT_OK == resultCode) {
            //Obtenemos los datos de la nueva linea de pedido y creamos una nueva linea de pedido en la pantalla del rutero
            final DatosLineaPedido datosLineaPedido = data.getParcelableExtra("DATOS_LINEA_PEDIDO");
            //Guardamos los datos de esta linea de pedido y posteriormente la insertamos en pantalla
            lineasPedido.add(datosLineaPedido);
            insertaLineaPedidoEnRutero(datosLineaPedido, datosLineaPedido);
            //Abrimos el subdialogo con los datos de la nueva linea de pedido, para que los modifique el usuario
            pideDatosLineaPedido((View) viewsTablaRutero.get(((viewsTablaRutero.size() / Constantes.COLUMNAS_TOTALES_LP) * 100) + Constantes.COLUMNA_CANTIDAD_NUEVO_KG_LP));
        }
    }

    /**
     * Comprueba si el pedido tiene alguna linea de pedido con datos.
     *
     * @return true si hay alguna linea de pedido con datos, false en cualquier otro caso
     */
    private boolean hayLineasPedido() {
        boolean resultado = false;

        //Recorremos el rutero para comprobar si hay alguna linea de pedido con datos, en el momento que encontremos una salimos
        for (int i = 1; (viewsTablaRutero.size() / Constantes.COLUMNAS_TOTALES_LP) >= i; i++) {
            if (chekCantidad(((DatosLineaPedido) viewsTablaRutero.get((i * 100) + Constantes.COLUMNA_DATOS_LP)))) {
                resultado = true;
                break;
            }
        }
        return resultado;
    }


    /**
     * Dada una vista y un numero de columna, devuelve el dato de la fila de la tabla de pedidos de cliente, donde esta la vista y que ocupa
     * la columna indicada
     *
     * @param v       view          de la fial al que pertenece la columna
     * @param columna la columna que ocupa la view, para la cual queremos obtener su ID.
     * @return el dato buscado
     */
    private String dameDatoFilaEnColumna(final View v, final int columna) {
        final TextView textViewBuscado = (TextView) viewsTablaRutero.get(calculaIdView(v, columna));
        return textViewBuscado.getText().toString();
    }

    /**
     * Dada una vista, devuelve la informacion adicional al pedido de la tabla de pedidos de cliente, donde esta la vista.
     * la columna indicada
     *
     * @param v       view          de la fial al que pertenece la columna
     * @param columna la columna que ocupa la view, para la cual queremos obtener su ID.
     * @return objeto Bundle que contiene la informacion
     */
    private DatosLineaPedido dameDatosLineaPedidoRuteroEnView(final View v, final int columna) {
        return (DatosLineaPedido) viewsTablaRutero.get(calculaIdView(v, columna));
    }

    /**
     * Dada una vista de una linea del rutero, guarda los datos de la linea de pedido asociados
     *
     * @param v     view          de la fial al que pertenece la columna
     * @param datos linea de pedido
     */
    private void guardaDatosLineaPedidoRuteroEnView(final View v, final DatosLineaPedido datos) {
        viewsTablaRutero.put(calculaIdView(v, Constantes.COLUMNA_DATOS_LP), datos);
    }

    /**
     * Dada una vista y un numero de columna, devuelve el dato de la fila de la tabla de pedidos de cliente, donde esta la vista y que ocupa
     * la columna indicada
     *
     * @param v       view          de la fial al que pertenece la columna
     * @param columna la columna que ocupa la view, para la cual queremos obtener su ID.
     * @return el dato buscado
     */
    private int dameColorTextoDatoFilaEnColumna(final View v, final int columna) {

        final TextView textViewBuscado = (TextView) viewsTablaRutero.get(calculaIdView(v, columna));
        return textViewBuscado.getTextColors().getDefaultColor();
    }

    /**
     * Dada una vista y un numero de columna, pone un dato del nuevo pedido en la fila de la tabla de pedidos de cliente, en la fila donde esta
     * la vista y que ocupa la columna indicada
     *
     * @param v       view          de la fial al que pertenece la columna
     * @param columna la columna que ocupa la view, para la cual queremos obtener su ID.
     * @param dato    String
     * @param idColor int
     */
    private void ponDatoNuevoPedidoFilaEnColumna(final View v, final int columna, final String dato, final int idColor) {

        final TextView textViewBuscado = (TextView) viewsTablaRutero.get(calculaIdView(v, columna));
        textViewBuscado.setText(dato);
        textViewBuscado.setTextColor(idColor);
    }

    /**
     * Actualiza el precio total en el widget de la pantalla, recalculandolo
     */
    private void refrescaPrecioTotalPedido() {
        float precioTotal = 0;

        //Recorremos el rutero para acumular el precio de las lineas de pedido introducidas por el usuario, el numero de filas es el numero de elementos / numeroColumnas
        for (int i = 1; i <= (viewsTablaRutero.size() / Constantes.COLUMNAS_TOTALES_LP); i++) {
            final DatosLineaPedido datosLineaPedido = (DatosLineaPedido) viewsTablaRutero.get((i * 100) + Constantes.COLUMNA_DATOS_LP);
            precioTotal += datosLineaPedido.getPrecio();
        }

        datosPedido.setPrecioTotal(precioTotal);
        textoPrecioTotalP.setText(Constantes.PREFIJO_TEXTO_PRECIO_TOTAL + Constantes.formatearFloat2Decimales.format(datosPedido.getPrecioTotal()) + Constantes.EURO);
    }

    /**
     * Devuelve el ID de la view, que sera uno de los datos de la tabla. El ID debe ser entero y sera de la forma
     * x..xy, donde "x...x" indica la fila e "y" la columna, el numero de filas no se sabe cual sera, pero el numero de
     * columnas es fijo, siempre sera de 1 a 11. Asi para una vista con ID 2604, la fila sera "26" y la columna "4".
     *
     * @param columnaTabla, la columna que ocupa la view, para la cual queremos obtener su ID.
     * @return el ID de la view calculada
     */
    private int dameIdViewNuevo(final int columnaTabla) {
        return ((ruteroTableLayout.getChildCount() + 1) * 100) + columnaTabla;
    }

    /**
     * Devuelve el ID de la view, que sera uno de los datos de la tabla. El ID debe ser entero y sera de la forma
     * x..xy, donde "x...x" indica la fila e "y" la columna, el numero de filas no se sabe cual sera, pero el numero de
     * columnas es fijo, siempre sera de 1 a 11. Asi para una vista con ID 2604, la fila sera "26" y la columna "4".
     *
     * @param v       view          de la fial al que pertenece la columna
     * @param columna la columna que ocupa la view, para la cual queremos obtener su ID.
     * @return el ID de la view de la columna
     */
    private int calculaIdView(final View v, final int columna) {
        return ((v.getId() / new Integer(10)) * 10) + columna;
    }


    /**
     * Muestra un mensaje de error toast con el formato correspondiente.
     */
    private void toastMensajeError() {
        final LayoutInflater inflater = getLayoutInflater();
        final View layout = inflater.inflate(R.layout.toast_error,
                (ViewGroup) findViewById(id.toast_layout_root));

        final TextView text = layout.findViewById(id.text);
        text.setText(ERROR_GUARDAR_DATOS_PEDIDO);

        final Toast toast = new Toast(getApplicationContext());
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }

    private void log(final String text) {
        Log.d("PantallaRutero", text);
    }


    @Click(id.columnaArticuloP)
    void ordenarPorArticulo() {
        inhabilitaBotonGuardar();
        botonOcultarRuteroP.setChecked(false);
        ruteroTableLayout.removeAllViews();
        viewsTablaRutero.clear();

        List<DatosLineaPedido> lista = new ArrayList<>();
        lista.addAll(lineasPedido);

        lineasPedido.clear();

        if (esInverso) {
            esInverso = false;
            Collections.sort(lista, Collections.reverseOrder(new ArticleComparator()));
        } else {
            Collections.sort(lista, new ArticleComparator());
            esInverso = true;
        }

        inicializaRutero(lista);
        refrescaPrecioTotalPedido();
        habilitaBotonGuardar();
    }


    @Click(id.columnaUltimaFechaP)
    void ordenarPorFecha() {
        inhabilitaBotonGuardar();
        botonOcultarRuteroP.setChecked(false);
        ruteroTableLayout.removeAllViews();
        viewsTablaRutero.clear();

        List<DatosLineaPedido> lista = new ArrayList<>();
        lista.addAll(lineasPedido);
        lineasPedido.clear();

        if (esInverso) {
            esInverso = false;
            Collections.sort(lista, Collections.reverseOrder(new DateComparator()));
        } else {
            Collections.sort(lista, new DateComparator());
            esInverso = true;
        }
        inicializaRutero(lista);
        refrescaPrecioTotalPedido();
        habilitaBotonGuardar();
    }


    @Click(id.columnaReferenciaP)
    void ordenarPorReferencia() {
        inhabilitaBotonGuardar();
        botonOcultarRuteroP.setChecked(false);
        ruteroTableLayout.removeAllViews();
        viewsTablaRutero.clear();

        List<DatosLineaPedido> lista = new ArrayList<>();
        lista.addAll(lineasPedido);
        lineasPedido.clear();

        if (esInverso) {
            esInverso = false;
            Collections.sort(lista, Collections.reverseOrder(new ReferenceComparator()));
        } else {
            Collections.sort(lista, new ReferenceComparator());
            esInverso = true;
        }
        inicializaRutero(lista);
        refrescaPrecioTotalPedido();
        habilitaBotonGuardar();
    }


    private boolean hayPedidosPendientes() {
        final AdaptadorBD db = new AdaptadorBD(this);
        db.abrir();
        final Cursor cursorPedidos = db.obtenerTodosLosPrepedidosDelCliente(datosPedido.getIdCliente());
        final boolean hayPendientes = (0 < cursorPedidos.getCount());
        db.cerrar();
        return hayPendientes;
    }

    @Click(id.botonSincronizacionP)
    @Background
    void sincronizarRuteros() {
        habilitaClickEnActivity(false);
        if (!hayPedidosPendientes()) {
            final WebServicesUtils wsUtils = new WebServicesUtils(this);
            cargaVentanaDialogo(pg_ruteros_RW, wsUtils);
            try {
                wsUtils.sincronizaRuterosDatosLista(consultaDatosRuteroBD(datosPedido.getIdCliente()), datosPedido.getIdCliente());
            } catch (Exception e) {
                Log.e("#### Excepcion", e.getLocalizedMessage() + "#######");
            }
        } else {
            message = true;
            Message msg = handler.obtainMessage();
            msg.sendToTarget();
        }
    }

    /**
     * Deshabilita o no, todo los eventos onClick de la activity para evitar ejecutar dos click seguidos
     */
    private void habilitaClickEnActivity(boolean habilita) {
        botonFechaEntregaP.setClickable(habilita);
        botonClienteP.setClickable(habilita);
        botonObservacionesP.setClickable(habilita);
        botonGuardarP.setClickable(habilita);
        botonNuevoArticuloP.setClickable(habilita);
        botonSincronizacionP.setClickable(habilita);
        for (int i = 1; i <= this.viewsTablaRutero.size() / Constantes.COLUMNAS_TOTALES_LP; i++) {
            ((TextView) this.viewsTablaRutero.get(((i) * 100) + Constantes.COLUMNA_ARTICULO_LP)).setClickable(habilita);
            ((TextView) this.viewsTablaRutero.get(((i) * 100) + Constantes.COLUMNA_CANTIDAD_NUEVO_KG_LP)).setClickable(habilita);
            ((TextView) this.viewsTablaRutero.get(((i) * 100) + Constantes.COLUMNA_TARIFA_NUEVO_LP)).setClickable(habilita);
        }
    }


    @UiThread
    void cargaVentanaDialogo(final String proceso, final WebServicesUtils wsUtils) {
        mDialog = new ProgressDialog(this);
        mDialog.setMessage(sincronizando + proceso + "...");
        mDialog.setCancelable(false);
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mDialog.setProgress(0);
        mDialog.setMax(100);
        mDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        wsUtils.setDialog(mDialog);
        mDialog.setOnDismissListener(postExecute);
        mDialog.show();
    }
}
