package intraza.com.interfaz.pantallas;

import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.text.InputFilter;
import android.text.InputFilter.LengthFilter;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.CheckedChange;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.ColorRes;
import org.androidannotations.annotations.res.DimensionPixelSizeRes;
import org.androidannotations.annotations.res.StringRes;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import intraza.com.Configuracion;
import intraza.com.interfaz.datos.DatosConsultaPedidos;
import intraza.com.interfaz.datosDB.PedidoBD;
import intraza.com.interfaz.datosDB.TablaArticulo;
import intraza.com.interfaz.datosDB.TablaCliente;
import intraza.com.interfaz.datosDB.TablaPrepedidoItem;
import intraza.com.interfaz.dialogos.DialogoConfirmacionBorrarPedidos_;
import intraza.com.interfaz.dialogos.DialogoConfirmacionEnviarPedidos_;
import intraza.com.interfaz.dialogos.DialogoDatosConsultaPedidos_;
import intraza.com.interfaz.dialogos.DialogoMensaje_;
import intraza.com.interfaz.task.JsonLineaPedido;
import intraza.com.interfaz.task.utils.MyBase64;
import intraza.com.Constantes;
import intraza.com.interfaz.datos.DatosDialogoMensaje;
import intraza.com.interfaz.datosDB.AdaptadorBD;
import intraza.com.interfaz.datosDB.LineaPedidoBD;
import intraza.com.interfaz.datosDB.TablaPrepedido;
import intraza.com.interfaz.task.JsonPedido;

import static android.view.View.OnClickListener;
import static android.widget.CheckBox.INVISIBLE;
import static android.widget.CompoundButton.OnCheckedChangeListener;

import intraza.com.R.id;
import intraza.com.R.layout;
import intraza.com.R.color;
import intraza.com.R;

@EActivity(layout.lista_pedidos)
public class PantallaListaPedidos extends PantallaPadre {
    //Codigos de los subdialogos que se usan en la Activity
    private static final int DIALOGO_MENSAJE_SIN_PEDIDOS_CONSULTA = 0;
    private static final int DIALOGO_CONFIRMACION_BORRAR_PEDIDOS = 1;
    private static final int DIALOGO_CONFIRMACION_ENVIAR_PEDIDOS = 2;
    private static final int DIALOGO_MODIFICAR_PEDIDO = 3;
    private static final int DIALOGO_PIDE_DATOS_CONSULTA_PEDIDOS = 4;
    private static final int RETORNA_CANCELAR = 5;


    //Almacena los datos de la consulta de los pedidos a presentar en pantalla
    @Extra("DATOS_CONSULTA_PEDIDOS")
    DatosConsultaPedidos datosConsultaPedidos;

    //Almacena los pedidos obtenidos en la consulta a la BD
    private List<PedidoBD> pedidosBD;

    //Contendra todos los TextView que forman la tabla de pedidos del cliente y los datos de la linea de pedido, la key sera el ID de la View
    private Hashtable<Integer, Object> viewsTablaPedidos;

    @StringRes
    String MENSAJE_PANTALLA_NINGUN_PEDIDO_SELECCIONADO;

    @StringRes
    String TITULO_ALERT_ERROR_ENVIO_PREPEDIDOS;

    @StringRes
    String MENSAJE_ALERT_ERROR_ENVIO_PREPEDIDOS;

    @StringRes
    String TITULO_ALERT_ENVIO_PREPEDIDOS_CORRECTO;

    @StringRes
    String MENSAJE_ALERT_ENVIO_PREPEDIDOS_CORRECTO;

    @StringRes
    String TITULO_SIN_PEDIDOS_CONSULTA;

    @StringRes
    String MENSAJE_PANTALLA_PEDIDOS_BORRADOS;

    @StringRes
    String ERROR_BORRAR_DATOS_PEDIDO;

    @StringRes
    String SPINNER_TODO;

    @StringRes
    String INFORMACION_SIN_PEDIDOS_CONSULTA;

    @StringRes
    String aceptar_M;

    @ColorRes(color.Blue_Ivy)
    int resaltoAzul;

    @DimensionPixelSizeRes
    int widthColPendienteEnviar;

    @DimensionPixelSizeRes
    int heightFilaDatosTabla;

    @DimensionPixelSizeRes
    int textSizeFilaDatosTabla;

    @DimensionPixelSizeRes
    int widthColSuprimir;

    @DimensionPixelSizeRes
    int widthColIdPedido;

    @DimensionPixelSizeRes
    int widthColCliente;

    @DimensionPixelSizeRes
    int widthColFechaPedido;

    @DimensionPixelSizeRes
    int widthColPrecioTotal;

    @DimensionPixelSizeRes
    int widthColFechaEntrega;

    @DimensionPixelSizeRes
    int anchoFilaPedido;

    //El widget que forma la tabla de pedidos en pantalla
    @ViewById(id.pedidosTableP)
    TableLayout tablaPedidos;

    @ViewById(id.checkColumnaEnviar)
    CheckBox checkColEnviar;

    @ViewById(id.checkColumnaSuprimir)
    CheckBox checkColSuprimir;

    @ViewById(id.botonNuevaConsultaPLP)
    Button botonNuevaConsultaPLP;

    @ViewById(id.botonEnviarPLP)
    Button botonEnviarPLP;

    @ViewById(id.botonBorrarPLP)
    Button botonBorrarPLP;

    @ViewById
    ScrollView scrollTablaP;

    @ViewById
    TextView columnaPedidoLP;
    @ViewById
    TextView columnaClienteLP;
    @ViewById
    TextView columnaFechaLP;
    @ViewById
    TextView columnaFechaEntregaLP;
    @ViewById
    TextView columnaPrecioTotalLP;
    @ViewById
    CheckBox checkColumnaEnviar;
    @ViewById
    CheckBox checkColumnaSuprimir;

    private Configuracion config;
    private String usuario;
    private String passw;
    private Integer sincro;

    int dWidth;
    int lineRow = 1;

    @AfterViews
    void init() {

        config = Configuracion.getInstance();
        config.preparaPropiedades(this);
        this.usuario = Configuracion.getInstance().dameUsuarioWS();
        this.passw = Configuracion.getInstance().damePasswordWS();
        this.sincro = Configuracion.getInstance().dameTimeoutWebServices();

        //Inicializamos las variables miembro necesarias
        viewsTablaPedidos = new Hashtable<>();
        calculaLongitudCabecera();
        pedidosBD = consultaPedidosBD(datosConsultaPedidos);

        //Si no hemos obtenido ningun pedido en la consulta, informamos al usuario y le devolvemos a la pantalla principal.
        if (pedidosBD.isEmpty()) {
            subdialogoMensajeSinPedidos();
        } else {
            //Mostramos la lista de pedidos en la pantalla
            cargaPedidosEnPantalla();

            //Si hay id pedido en la consulta es que solo hay una linea de pedido a mostrar, por lo que hacemos invisibles los checkBox de la cabecera
            if (datosConsultaPedidos.hayDatoIdPedido()) {
                checkColEnviar.setVisibility(INVISIBLE);
                checkColSuprimir.setVisibility(INVISIBLE);
            }
        }
    }

    void calculaLongitudCabecera() {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        dWidth = displaymetrics.widthPixels;
        columnaPedidoLP.setWidth((dWidth * 15) / 128);
        columnaClienteLP.setWidth((dWidth * 33) / 128);
        columnaFechaLP.setWidth((dWidth * 19) / 128);
        columnaFechaEntregaLP.setWidth((dWidth * 19) / 128);
        columnaPrecioTotalLP.setWidth((dWidth * 18) / 128);
        checkColumnaEnviar.setWidth((dWidth * 12) / 128);
        checkColumnaSuprimir.setWidth((dWidth * 12) / 128);
    }


    @CheckedChange(id.checkColumnaEnviar)
    void checkedChangeOnColumnaEnviarCheckBox(final boolean isChecked) {
        if (isChecked) {
            //Desmarcamos el check de la cabecera de la tabla de borrar
            checkColSuprimir.setChecked(false);
        }
        if (!viewsTablaPedidos.isEmpty()) {
            for (int i = 1; i <= (viewsTablaPedidos.size() / Constantes.COLUMNAS_TOTALES_P); i++) {
                setCheckBoxEnviar((CheckBox) viewsTablaPedidos.get(calculaIdView(i, Constantes.COLUMNA_PENDIENTE_ENVIAR_P)), isChecked);
            }
        }
    }

    @CheckedChange(id.checkColumnaSuprimir)
    void checkedChangeOnColumnaSuprimirCheckBox(final boolean isChecked) {
        if (isChecked) {
            //Desmarcamos el check de la cabecera de la tabla de envio
            checkColEnviar.setChecked(false);
        }
        if (!viewsTablaPedidos.isEmpty()) {
            //En todos los pedidos chequeamos el borrado y deschequeamos el envio por si estubiera marcado
            for (int i = 1; i <= (viewsTablaPedidos.size() / Constantes.COLUMNAS_TOTALES_P); i++) {
                setCheckBoxBorrar((CheckBox) viewsTablaPedidos.get(calculaIdView(i, Constantes.COLUMNA_SUPRIMIR_P)), isChecked);
            }
        }
    }


    /**
     * Deshabilita o no, todo los eventos onClick de la activity para evitar ejecutar dos click seguidos
     *
     * @param habilita booleano
     */
    private void habilitaClickEnActivity(final boolean habilita) {
        botonNuevaConsultaPLP.setClickable(habilita);
        botonEnviarPLP.setClickable(habilita);
        botonBorrarPLP.setClickable(habilita);

        for (int i = 1; i <= (viewsTablaPedidos.size() / Constantes.COLUMNAS_TOTALES_P); i++) {
            ((TextView) viewsTablaPedidos.get(calculaIdView(i, Constantes.COLUMNA_ID_PEDIDO_P))).setClickable(habilita);
        }
    }

    /**
     * Cambia el checkBox enviar en la view del pedido y actualiza el valor en el pedido
     *
     * @param checkBox checkBox
     * @param checked  para la propiedad checked checkBox
     */
    private void setCheckBoxEnviar(final CheckBox checkBox, final boolean checked) {
        checkBox.setChecked(checked);
        dameDatosPedido(checkBox).setCheckBoxEnviar(checked);
    }

    /**
     * Cambia el checkBox borrar en la view del pedido y actualiza el valor en el pedido
     *
     * @param checkBox checkBox
     * @param checked  para la propiedad checked checkBox
     */
    private void setCheckBoxBorrar(final CheckBox checkBox, final boolean checked) {
        checkBox.setChecked(checked);
        dameDatosPedido(checkBox).setCheckBoxBorrar(checked);
    }

    /**
     * Consultamos en la BD de la tablet los pedidos
     *
     * @param dcp de la consulta
     * @return un Vector con los datos de los pedidos consultados
     */
    private List<PedidoBD> consultaPedidosBD(final DatosConsultaPedidos dcp) {
        final List<PedidoBD> pedidosConsultados = new ArrayList<>();
        List<LineaPedidoBD> lineasPedido;
        PedidoBD pedido;
        final AdaptadorBD db = new AdaptadorBD(this);
        final Cursor cursorPedidos;
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


        //Contiene los datos de una fecha descompuesta, es decir, para una fecha DD-MM-YYYY, contiene 3 elementos,
        //en la posicion 0 tiene DD, en la posicion 1 tiene MM y en la posicion 2 tiene YYYY.
        String[] fechaDescompuesta;

        db.abrir();

        // Hay que obtener los datos del pedido segun su ID
        if (dcp.hayDatoIdPedido()) {
            cursorPedidos = db.obtenerPrepedidoConDatosCliente(dcp.getIdPedido());
        } else {
            //DEPENDIENDO SEA UNA CONSULTA DE TODOS O UN CLIENTE
            cursorPedidos = dcp.getCliente().equals(SPINNER_TODO) ?
                    db.obtenerTodosLosPrepedidosConDatosCliente() :
                    db.obtenerTodosLosPrepedidosDelClienteConDatosCliente(dcp.getIdCliente());
        }

        //Si tenemos resultado de la consulta...
        if (cursorPedidos.moveToFirst()) {
            //Recorremos el cursor para obtener los datos de los pedidos recuperados
            do {
                idPrepedidoP = cursorPedidos.getInt(TablaPrepedido.POS_KEY_CAMPO_ID_PREPEDIDO);
                idClienteP = cursorPedidos.getInt(TablaPrepedido.POS_CAMPO_ID_CLIENTE);
                clienteP = cursorPedidos.getString(TablaPrepedido.NUM_CAMPOS + TablaCliente.POS_CAMPO_NOMBRE_CLIENTE);
                fechaDescompuesta = cursorPedidos.getString(TablaPrepedido.POS_CAMPO_FECHA_PREPEDIDO).split(Constantes.SEPARADOR_FECHA);
                diaFechaPedidoP = Integer.parseInt(fechaDescompuesta[0]);
                mesFechaPedidoP = Integer.parseInt(fechaDescompuesta[1]);
                anioFechaPedidoP = Integer.parseInt(fechaDescompuesta[2]);
                fechaDescompuesta = cursorPedidos.getString(TablaPrepedido.POS_CAMPO_FECHA_ENTREGA).split(Constantes.SEPARADOR_FECHA);
                diaFechaEntregaP = Integer.parseInt(fechaDescompuesta[0]);
                mesFechaEntregaP = Integer.parseInt(fechaDescompuesta[1]);
                anioFechaEntregaP = Integer.parseInt(fechaDescompuesta[2]);
                observacionesP = cursorPedidos.getString(TablaPrepedido.POS_CAMPO_OBSERVACIONES);
                fijarObservacionesP = 1 == cursorPedidos.getInt(TablaPrepedido.POS_CAMPO_FIJAR_OBSERVACIONES);

                //Ahora para cada pedido recuperamos sus lineas de pedido
                lineasPedido = new ArrayList<>();
                cursorLineasPedido = db.obtenerTodosLosPrepedidosItemDelPrepedido(idPrepedidoP);

                if (cursorLineasPedido.moveToFirst()) {
                    //Recorremos el cursor para obtener los datos de las lineas de pedido
                    anyadirLineasDePedido(lineasPedido, db, cursorLineasPedido);
                }

                pedido = new PedidoBD(idPrepedidoP, idClienteP, clienteP, diaFechaPedidoP, mesFechaPedidoP,
                        anioFechaPedidoP, diaFechaEntregaP, mesFechaEntregaP, anioFechaEntregaP, observacionesP, fijarObservacionesP, lineasPedido);
                pedidosConsultados.add(pedido);

            } while (cursorPedidos.moveToNext());
        }

        db.cerrar();

        //Toast.makeText(getBaseContext(), Constantes.MENSAJE_PANTALLA_CONSULTA_PEDIDOS_TERMINADA, Toast.LENGTH_SHORT).show();

        return pedidosConsultados;
    }


    private void anyadirLineasDePedido(final List<LineaPedidoBD> lineasPedido, final AdaptadorBD db, final Cursor cursorLineasPedido) {
        Cursor cursorArticulo;
        String codArticuloLP;
        String observacionesLP;
        int idPrepedidoLP;
        int idArticuloLP;
        int cantidadUdLP;
        float cantidadKgLP;
        float tarifaClienteLP;
        boolean fijarTarifaLP;
        boolean suprimirTarifaLP;
        boolean fijarArticuloLP;
        boolean fijarObservacionesLP;
        boolean esMedidaKgLP;
        boolean esCongeladoLP;
        do {
            idPrepedidoLP = cursorLineasPedido.getInt(TablaPrepedidoItem.POS_CAMPO_ID_PREPEDIDO);
            idArticuloLP = cursorLineasPedido.getInt(TablaPrepedidoItem.POS_CAMPO_ID_ARTICULO);
            codArticuloLP = cursorLineasPedido.getString(TablaPrepedidoItem.POS_CAMPO_CODIGO_ARTICULO);
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
            esCongeladoLP = (1 == cursorArticulo.getInt(TablaArticulo.POS_CAMPO_ES_CONGELADO));
            lineasPedido.add(new LineaPedidoBD(fijarTarifaLP, suprimirTarifaLP, fijarArticuloLP, fijarObservacionesLP, idPrepedidoLP, idArticuloLP, codArticuloLP, esMedidaKgLP, esCongeladoLP,
                    cantidadKgLP, cantidadUdLP, tarifaClienteLP, observacionesLP));
        } while (cursorLineasPedido.moveToNext());
    }

    /**
     * Muestra un mensaje al usuario indicando que no se ha obtenido ningun pedido como resultado de la consulta
     */
    private void subdialogoMensajeSinPedidos() {

        final DatosDialogoMensaje datosMensaje;
        datosMensaje = new DatosDialogoMensaje("intraza.com.interfaz.PantallaListaPedidos_",
                TITULO_SIN_PEDIDOS_CONSULTA, INFORMACION_SIN_PEDIDOS_CONSULTA);
        DialogoMensaje_.intent(this).extra("DATOS_MENSAJE", datosMensaje).startForResult(DIALOGO_MENSAJE_SIN_PEDIDOS_CONSULTA);
    }


    @OnActivityResult(DIALOGO_MENSAJE_SIN_PEDIDOS_CONSULTA)
    void onResult(final int resultCode) {
        if (RESULT_OK == resultCode) {
            //Si no hay pedidos, no hay nada que mostar, finalizamos la activity, para volver a la activity principal
            finish();
        }
        habilitaClickEnActivity(true);
    }

    @OnActivityResult(DIALOGO_CONFIRMACION_BORRAR_PEDIDOS)
    void onResultDos(final int resultCode, final Intent data) {
        if (RESULT_OK == resultCode) {
            //Borramos en todas las lineas de pedido y volvemos a mostrar la lista de pedidos en pantalla
            borrarPedidos(data.getExtras().getStringArray("ARRAY_ID_PEDIDOS"));
            Toast.makeText(getBaseContext(), MENSAJE_PANTALLA_PEDIDOS_BORRADOS, Toast.LENGTH_SHORT).show();
            //Deschequeamos el checkBox de la cabecera borrar
            checkColSuprimir.setChecked(false);
        }
        habilitaClickEnActivity(true);
    }

    @OnActivityResult(DIALOGO_CONFIRMACION_ENVIAR_PEDIDOS)
    void onResultTres(final int resultCode, final Intent data) {
        if (RESULT_OK == resultCode) {
            //Recuperamos los IDs de los pedidos a enviar a intraza
            final String[] idPedidos = data.getExtras().getStringArray("ARRAY_ID_PEDIDOS");
            //Enviamos los pedidos a intraza
            final TareaEnviaPrepedidos enviaPrepedidos = new TareaEnviaPrepedidos(this, idPedidos);
            enviaPrepedidos.execute();
            //Deschequeamos el checkBox de la cabecera enviar
            checkColEnviar.setChecked(false);

            //Cuando finalice la tarea, se invocara al metodo "finalTareaEnviaPedidos", que borrara los pedidos enviados correctamente a intraza
        }
        habilitaClickEnActivity(true);
    }

    @OnActivityResult(DIALOGO_MODIFICAR_PEDIDO)
    void onResultCuatro() {
        pedidosBD = consultaPedidosBD(datosConsultaPedidos);
        //Si no hemos obtenido ningun pedido en la consulta, informamos al usuario y le devolvemos a la pantalla principal.
        if (pedidosBD.isEmpty()) {
            subdialogoMensajeSinPedidos();
        } else {
            //Mostramos la lista de pedidos en la pantalla
            cargaPedidosEnPantalla();
        }
        habilitaClickEnActivity(true);
    }


    @OnActivityResult(DIALOGO_PIDE_DATOS_CONSULTA_PEDIDOS)
    void onResultCinco(final int resultCode, final Intent data) {
        if (RESULT_OK == resultCode) {
            //Obtenemos del intent los datos de la consulta
            datosConsultaPedidos = data.getParcelableExtra("DATOS_CONSULTA_PEDIDOS");
            pedidosBD = consultaPedidosBD(datosConsultaPedidos);
            //Si no hemos obtenido ningun pedido en la consulta, informamos al usuario y le devolvemos a la pantalla principal.
            if (pedidosBD.isEmpty()) {
                subdialogoMensajeSinPedidos();
            } else {
                //Mostramos la lista de pedidos en la pantalla
                cargaPedidosEnPantalla();
            }
        }
        habilitaClickEnActivity(true);
    }

    @OnActivityResult(RETORNA_CANCELAR)
    void onResultseisCinco(final int resultCode, final Intent data) {
        habilitaClickEnActivity(true);
    }

    /**
     * Este metodo es ejecutado cuando termina la tarea que envio de pedidos a intraza. Se encarga de borrar de la BD de la tablet, los pedidos enviados
     * a intraza.
     *
     * @param listaPedidosEnviados ArrayList<String>
     */
    private void finalTareaEnviaPedidos(final ArrayList<String> listaPedidosEnviados) {
        borrarPedidos(listaPedidosEnviados.toArray(new String[listaPedidosEnviados.size()]));
    }

    /**
     * Borra los pedidos y lineas de pedido de la BD y de la tabla de pedidos mostrada en pantalla
     *
     * @param idPedidos array con los id pedidos a borrar
     */
    private void borrarPedidos(final String... idPedidos) {
        final AdaptadorBD db = new AdaptadorBD(this);

        db.abrir();
        //Borramos los pedidos de la lista de pedidos
        for (final String idPedido : idPedidos) {
            //Primero borramos los datos del pedido de la BD y si va bien los borramos de la tabla mostrada en pantalla
            if (!db.borrarPrepedidoConLosPrepedidosItem(Integer.parseInt(idPedido))) {
                toastMensajeError(ERROR_BORRAR_DATOS_PEDIDO + idPedido + ".");
            } else {
                borraPedidoSegunId(Integer.parseInt(idPedido));
            }
        }
        db.cerrar();
        //Refrescamos la pantalla
        cargaPedidosEnPantalla();
    }

    /**
     * Carga en la pantalla los pedidos del cliente
     */
    private void cargaPedidosEnPantalla() {
        //Primero borramos las filas que puediera haber previamente en la tabla y todas las views de la tabla
        tablaPedidos.removeAllViews();
        viewsTablaPedidos.clear();

        for (int i = 0; i < pedidosBD.size(); i++) {
            insertaLineaPedidoEnTabla(pedidosBD.get(i));
        }
        //Posicionamos la tabla de pedidos en pantalla, al princio del scroll
        scrollTablaP.scrollTo(0, 0);
    }

    /**
     * Inserta una fila en la pantalla en la tabla de pedidos
     *
     * @param pedido pedido a insertar
     */
    void insertaLineaPedidoEnTabla(final PedidoBD pedido) {
        final int colorTextoFila = dameColorTextoFila(lineRow);
        final int colorFila = dameColorFila(lineRow++);
        final TableRow filaP = new TableRow(this);

        filaP.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, anchoFilaPedido));

        filaP.addView(creaVistaIdPedido(String.valueOf(pedido.getIdPedido()), resaltoAzul, colorTextoFila));
        filaP.addView(creaVistaCliente(String.valueOf(pedido.getIdPedido()), pedido.getCliente(), colorFila, colorTextoFila));
        filaP.addView(creaVistaFechaPedido(String.valueOf(pedido.getIdPedido()), pedido.getFechaPedido(), colorFila, colorTextoFila));
        filaP.addView(creaVistaFechaEntrega(String.valueOf(pedido.getIdPedido()), pedido.getFechaEntrega(), colorFila, colorTextoFila));
        filaP.addView(creaVistaPrecioTotal(String.valueOf(pedido.getIdPedido()), pedido.getPrecioTotal(), colorFila, colorTextoFila));
        filaP.addView(creaVistaEnviar(String.valueOf(pedido.getIdPedido()), pedido.isCheckBoxEnviar(), colorFila));
        filaP.addView(creaVistaSuprimir(String.valueOf(pedido.getIdPedido()), pedido.isCheckBoxBorrar(), colorFila));

        //Insertamos en la tabla de pedidos de la pantalla el nuevo pedido
        tablaPedidos.addView(filaP, new TableLayout.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
    }

    /**
     * Crea un widget para mostrar el dato IdPedido en pantalla
     *
     * @param idPedido       String
     * @param colorFila      int
     * @param colorTextoFila int
     * @return View
     */
    private View creaVistaIdPedido(final String idPedido, final int colorFila, final int colorTextoFila) {

        final int ident = dameIdViewNuevo(Constantes.COLUMNA_ID_PEDIDO_P);
        final int width = (dWidth * 15) / 128;

        final TextView datoIdPedido = getTextView(colorFila, colorTextoFila, ident, width);

        datoIdPedido.setText(idPedido);
        //Guardamos el idPedido asociado con la view
        datoIdPedido.setContentDescription(idPedido);
        datoIdPedido.setClickable(true);
        //noinspection CyclicClassDependency
        datoIdPedido.setOnClickListener(new OnClickListener() {
            public void onClick(final View v) {
                habilitaClickEnActivity(false);
                subdialogoModificarPedido(v);
            }
        });

        //Guardamos la nueva vista para asi poder consultarla posteriormente
        viewsTablaPedidos.put(datoIdPedido.getId(), datoIdPedido);

        return datoIdPedido;
    }

    /**
     * Crea un widget para mostrar los datos del cliente en pantalla
     *
     * @param idPedido       String
     * @param cliente        String
     * @param colorFila      int
     * @param colorTextoFila int
     * @return View
     */
    private View creaVistaCliente(final String idPedido, final String cliente, final int colorFila, final int colorTextoFila) {

        final int ident = dameIdViewNuevo(Constantes.COLUMNA_CLIENTE_P);
        final int width = (dWidth * 33) / 128;
        final TextView datoCliente = getTextView(colorFila, colorTextoFila, ident, width);

        //El ancho maximo es de 35 caracteres
        final InputFilter[] filterArray = new InputFilter[1];
        filterArray[0] = new LengthFilter(35);
        datoCliente.setFilters(filterArray);

        //Ponemos el dato
        datoCliente.setText(cliente);
        //Guardamos el idPedido asociado con la view
        datoCliente.setContentDescription(idPedido);
        //Guardamos la nueva vista para asi poder consultarla posteriormente
        viewsTablaPedidos.put(datoCliente.getId(), datoCliente);
        return datoCliente;
    }

    /**
     * Crea un widget para mostrar el dato fechaPedido en pantalla
     *
     * @param idPedido       String
     * @param fecha          String
     * @param colorFila      int
     * @param colorTextoFila int
     * @return View
     */
    private View creaVistaFechaPedido(final String idPedido, final String fecha, final int colorFila, final int colorTextoFila) {

        final int ident = dameIdViewNuevo(Constantes.COLUMNA_FECHA_PEDIDO_P);
        final int width = (dWidth * 19) / 128;
        final TextView datoFechaPedido = getTextView(colorFila, colorTextoFila, ident, width);

        datoFechaPedido.setText(fecha);
        //Guardamos el idPedido asociado con la view
        datoFechaPedido.setContentDescription(idPedido);
        //Guardamos la nueva vista para asi poder consultarla posteriormente
        viewsTablaPedidos.put(datoFechaPedido.getId(), datoFechaPedido);
        return datoFechaPedido;
    }

    /**
     * Crea un widget para mostrar el dato fechaEntrega en pantalla
     *
     * @param idPedido       idPedido
     * @param fecha          fecha
     * @param colorFila      int
     * @param colorTextoFila int
     * @return View
     */
    private View creaVistaFechaEntrega(final String idPedido, final String fecha, final int colorFila, final int colorTextoFila) {

        final int ident = dameIdViewNuevo(Constantes.COLUMNA_FECHA_ENTREGA_P);
        final int width = (dWidth * 19) / 128;
        final TextView datoFechaEntrega = getTextView(colorFila, colorTextoFila, ident, width);

        datoFechaEntrega.setText(fecha);
        datoFechaEntrega.setContentDescription(idPedido);
        viewsTablaPedidos.put(datoFechaEntrega.getId(), datoFechaEntrega);
        return datoFechaEntrega;
    }

    /**
     * Crea un widget para mostrar un boton que al pulsar muestre el dato observaciones en pantalla
     *
     * @param idPedido       String
     * @param precioTotal    float
     * @param colorFila      int
     * @param colorTextoFila int
     * @return View
     */
    private View creaVistaPrecioTotal(final String idPedido, final float precioTotal, final int colorFila, final int colorTextoFila) {

        final int ident = dameIdViewNuevo(Constantes.COLUMNA_PRECIO_TOTAL_P);
        final int width = (dWidth * 18) / 128;
        final TextView datoPrecioTotal = getTextView(colorFila, colorTextoFila, ident, width);

        datoPrecioTotal.setText(Constantes.formatearFloat2Decimales.format(precioTotal) + Constantes.EURO);
        datoPrecioTotal.setContentDescription(idPedido);
        viewsTablaPedidos.put(datoPrecioTotal.getId(), datoPrecioTotal);
        return datoPrecioTotal;
    }

    private TextView getTextView(final int colorFila, final int colorTextoFila, final int ident, final int width) {
        final TextView datoPrecioTotal = new TextView(this);
        datoPrecioTotal.setId(ident);
        datoPrecioTotal.setGravity(Gravity.CENTER);
        datoPrecioTotal.setHeight(anchoFilaPedido);
        datoPrecioTotal.setWidth(width);
        datoPrecioTotal.setTextSize(textSizeFilaDatosTabla);
        datoPrecioTotal.setBackgroundColor(colorFila);
        datoPrecioTotal.setTextColor(colorTextoFila);
        datoPrecioTotal.setMaxLines(1);
        return datoPrecioTotal;
    }

    /**
     * Crea un widget para mostrar en pantalla que el pedido si esta pendiente de enviar a intraza
     *
     * @param idPedido  String
     * @param checked   boolean
     * @param colorFila int
     * @return View
     */
    private View creaVistaEnviar(final String idPedido, final boolean checked, final int colorFila) {

        final int idView = dameIdViewNuevo(Constantes.COLUMNA_PENDIENTE_ENVIAR_P);
        final int width = (dWidth * 12) / 128;
        final CheckBox checkBoxEnviar = new CheckBox(this);

        checkBoxEnviar.setBackgroundColor(colorFila);
        checkBoxEnviar.setWidth(width);
        checkBoxEnviar.setHeight(anchoFilaPedido);
        checkBoxEnviar.setContentDescription(idPedido);
        checkBoxEnviar.setId(idView);
        checkBoxEnviar.setGravity(Gravity.CENTER);
        checkBoxEnviar.setChecked(checked);

        checkBoxEnviar.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            public void onCheckedChanged(final CompoundButton buttonView, final boolean isChecked) {
                if (isChecked) {
                    setCheckBoxEnviar((CheckBox) dameViewSegunId(buttonView.getId()), true);
                    setCheckBoxBorrar((CheckBox) dameViewSegunId(buttonView.getId() + new Integer(1)), false);
                } else {
                    setCheckBoxEnviar((CheckBox) dameViewSegunId(buttonView.getId()), false);
                }
            }
        });
        viewsTablaPedidos.put(checkBoxEnviar.getId(), checkBoxEnviar);

        return checkBoxEnviar;
    }

    /**
     * Crea un widget para crear un boton que al pulsar muestra un dialogo para confirmar el borrado del pedido
     *
     * @param idPedido  String
     * @param checked   boolean
     * @param colorFila int
     * @return View
     */
    private View creaVistaSuprimir(final String idPedido, final boolean checked, final int colorFila) {

        final int idView = dameIdViewNuevo(Constantes.COLUMNA_SUPRIMIR_P);
        final int width = (dWidth * 12) / 128;
        final CheckBox checkBoxSuprimir = new CheckBox(this);

        checkBoxSuprimir.setBackgroundColor(colorFila);
        checkBoxSuprimir.setWidth(width);
        checkBoxSuprimir.setHeight(anchoFilaPedido);
        checkBoxSuprimir.setContentDescription(idPedido);
        checkBoxSuprimir.setId(idView);
        checkBoxSuprimir.setGravity(Gravity.CENTER);
        checkBoxSuprimir.setChecked(checked);

        checkBoxSuprimir.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            public void onCheckedChanged(final CompoundButton buttonView, final boolean isChecked) {
                if (isChecked) {
                    setCheckBoxBorrar((CheckBox) dameViewSegunId(buttonView.getId()), true);
                    setCheckBoxEnviar((CheckBox) dameViewSegunId(buttonView.getId() - new Integer(1)), false);
                } else {
                    setCheckBoxBorrar((CheckBox) dameViewSegunId(buttonView.getId()), false);
                }
            }
        });

        //Guardamos la nueva vista el widget que nos interesa para asi poder consultarlo posteriormente
        viewsTablaPedidos.put(checkBoxSuprimir.getId(), checkBoxSuprimir);
        return checkBoxSuprimir;
    }

    /**
     * Muestra el subdialogo que permite realizar una nueva consulta
     */
    @Click(id.botonNuevaConsultaPLP)
    void subdialogoDatosConsultaPedidos() {
        habilitaClickEnActivity(false);
        DialogoDatosConsultaPedidos_.builder()
                .arg("DIALOGO_PIDE_DATOS", DIALOGO_PIDE_DATOS_CONSULTA_PEDIDOS)
                .build()
                .show(getFragmentManager(), null);
    }

    /**
     * Borra los pedidos seleccionados por el usuario, previa a la confirmaci�n del usuario, que se solicita en una nueva activity
     */
    @Click(id.botonBorrarPLP)
    void subdialogoBorrarPedidos() {

        PedidoBD datosPedido;
        final List<String> idPedidos = new ArrayList<>();
        final List<String> clientePedidos = new ArrayList<>();
        final String[] arrayIdPedidos;
        final String[] arrayClientePedidos;
        habilitaClickEnActivity(false);

        //Recorremos los checkBox de borrado, para obtener los datos de los pedidos a borrar y los guardamos en 2 vectores uno para el idPedido
        //y otra para el cliente
        for (int i = 1; i <= (viewsTablaPedidos.size() / Constantes.COLUMNAS_TOTALES_P); i++) {
            if (((CheckBox) viewsTablaPedidos.get(calculaIdView(i, Constantes.COLUMNA_SUPRIMIR_P))).isChecked()) {
                //Localizamos los datos del pedido, si no hay, no invocamos el subdialogo
                datosPedido = dameDatosPedido((View) viewsTablaPedidos.get(calculaIdView(i, Constantes.COLUMNA_SUPRIMIR_P)));

                if (null != datosPedido) {
                    idPedidos.add(String.valueOf(datosPedido.getIdPedido()));
                    clientePedidos.add(datosPedido.getCliente());
                }
            }
        }

        //Si no tenemos ningun pedido que borrar no invocamos a la activity
        if (!idPedidos.isEmpty()) {
            //Formamos los array a pasar en el bundle
            arrayIdPedidos = new String[idPedidos.size()];
            arrayClientePedidos = new String[clientePedidos.size()];

            for (int i = 0; i < idPedidos.size(); i++) {
                arrayIdPedidos[i] = idPedidos.get(i);
                arrayClientePedidos[i] = clientePedidos.get(i);
            }

            DialogoConfirmacionBorrarPedidos_.builder()
                    .arg("ARRAY_ID_PEDIDOS", arrayIdPedidos)
                    .arg("ARRAY_CLIENTE_PEDIDOS", arrayClientePedidos)
                    .arg("DIALOGO_RETORNA_RESULTADO", DIALOGO_CONFIRMACION_BORRAR_PEDIDOS)
                    .build()
                    .show(getFragmentManager(), null);
        } else {
            Toast.makeText(getBaseContext(), MENSAJE_PANTALLA_NINGUN_PEDIDO_SELECCIONADO, Toast.LENGTH_SHORT).show();
            habilitaClickEnActivity(true);
        }
    }

    /**
     * Envia los pedidos seleccionados por el usuario, previa a la confirmaci�n del usuario, que se solicita en una nueva activity
     */
    @Click(id.botonEnviarPLP)
    void subdialogoEnviarPedidos() {

        PedidoBD datosPedido;
        final List<String> idPedidos = new ArrayList<>();
        final List<String> clientePedidos = new ArrayList<>();
        final String[] arrayIdPedidos;
        final String[] arrayClientePedidos;
        habilitaClickEnActivity(false);

        //Recorremos los checkBox de Envio, para obtener los datos de los pedidos a enviar y los guardamos en 2 vectores uno para el idPedido
        //y otra para el cliente
        for (int i = 1; i <= (viewsTablaPedidos.size() / Constantes.COLUMNAS_TOTALES_P); i++) {
            if (((CheckBox) viewsTablaPedidos.get(calculaIdView(i, Constantes.COLUMNA_PENDIENTE_ENVIAR_P))).isChecked()) {
                //Localizamos los datos del pedido, si no hay, no invocamos el subdialogo
                datosPedido = dameDatosPedido((View) viewsTablaPedidos.get(calculaIdView(i, Constantes.COLUMNA_PENDIENTE_ENVIAR_P)));

                if (null != datosPedido) {
                    idPedidos.add(String.valueOf(datosPedido.getIdPedido()));
                    clientePedidos.add(datosPedido.getCliente());
                }
            }
        }

        //Si no tenemos ningun pedido que enviar no invocamos a la activity
        if (!idPedidos.isEmpty()) {
            //Formamos los array a pasar en el bundle
            arrayIdPedidos = new String[idPedidos.size()];
            arrayClientePedidos = new String[clientePedidos.size()];

            for (int i = 0; i < idPedidos.size(); i++) {
                arrayIdPedidos[i] = idPedidos.get(i);
                arrayClientePedidos[i] = clientePedidos.get(i);
            }

            DialogoConfirmacionEnviarPedidos_.builder()
                    .arg("ARRAY_ID_PEDIDOS", arrayIdPedidos)
                    .arg("ARRAY_CLIENTE_PEDIDOS", arrayClientePedidos)
                    .arg("DIALOGO_RETORNA_RESULTADO", DIALOGO_CONFIRMACION_ENVIAR_PEDIDOS)
                    .build().show(getFragmentManager(), null);
        } else {
            Toast.makeText(getBaseContext(), MENSAJE_PANTALLA_NINGUN_PEDIDO_SELECCIONADO, Toast.LENGTH_SHORT).show();
            habilitaClickEnActivity(true);
        }
    }

    /**
     * Permite modificar el prepedido
     *
     * @param v View
     */
    private void subdialogoModificarPedido(final View v) {
        //Localizamos los datos del pedido, si no hay, no invocamos el subdialogo
        if (null != dameDatosPedido(v)) {
            //Toast.makeText(getBaseContext(), Constantes.MENSAJE_PANTALLA_CARGAR_PEDIDO, Toast.LENGTH_SHORT).show();
            //Pasamos los datos del pedido para que sean modificado
            PantallaRutero_.intent(this).extra("PANTALLA_MODIFICACION", true)
                    .extra("ID_PEDIDO", dameDatosPedido(v).getIdPedido())
                    .startForResult(DIALOGO_MODIFICAR_PEDIDO);
        }
    }

    /**
     * Dada una vista nos devuelve los datos del pedido asociados a la misma
     *
     * @param v View
     * @return los datos del pedidos, o null en caso de que no se hayan encontrado
     */
    private PedidoBD dameDatosPedido(final View v) {
        //En el contentDescription de la view tenemos guardado el idPedido
        return dameDatosPedidoSegunId(Integer.parseInt(v.getContentDescription().toString()));
    }

    /**
     * Devuelve los datos del pedido, con el identificador pasado como parametro
     *
     * @param idPedido int
     * @return datos del pedido, o null, si no hay ningun pedido con ese identificador
     */
    private PedidoBD dameDatosPedidoSegunId(final int idPedido) {

        for (final PedidoBD pedido : pedidosBD) {
            if (pedido.getIdPedido() == idPedido) {
                return pedido;
            }
        }
        return null;
    }

    /**
     * Borra uno de los pedidos de la lista, segun su id pedido
     *
     * @param idPedido int
     */
    private void borraPedidoSegunId(final int idPedido) {
        //Recorremos la lista de pedidos hasta localizar el pedido a borrar y lo borramos
        for (final PedidoBD pedido : pedidosBD) {
            if (pedido.getIdPedido() == idPedido) {
                pedidosBD.remove(pedido);
                break;
            }
        }
    }

    /**
     * Busca una vista de la tabla de pedidos, dato su id.
     *
     * @param idView id de la vista
     * @return la View buscada
     */
    private View dameViewSegunId(final int idView) {
        return (View) viewsTablaPedidos.get(idView);
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
        return ((tablaPedidos.getChildCount() + 1) * 100) + columnaTabla;
    }

    /**
     * Devuelve el id view de un widget de la tabla de pedidos
     *
     * @param fila         de la tabla, empieza a partir de la 1
     * @param columnaTabla de la tabla, empieza a partir de la 1
     * @return el ID de la view calculada
     */
    private int calculaIdView(final int fila, final int columnaTabla) {
        return (fila * 100) + columnaTabla;
    }

    /**
     * Muestra un mensaje de error toast con el formato correspondiente.
     *
     * @param mensaje de error a mostrar
     */
    private void toastMensajeError(final String mensaje) {
        final LayoutInflater inflater = getLayoutInflater();
        final View layout = inflater.inflate(R.layout.toast_error, (ViewGroup) findViewById(id.toast_layout_root));

        final TextView text = layout.findViewById(id.text);
        text.setText(mensaje);

        final Toast toast = new Toast(getApplicationContext());
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }

    /**
     * Invoca un WebService REST enviando los datos con un JSON.
     *
     * @param segundosTimeout   para recibir una respuesta
     * @param urlWebServiceRest URL de invocacion al Web Service
     * @param pedido            datos del prepedido a enviar
     * @return Una cadena con el resultado de la invocacion al Web Service
     */
    private String invocaWebServiceHttp(final int segundosTimeout, final String urlWebServiceRest, final JsonPedido pedido) throws Exception {
        Log.d("Sincronizacion", "TRAZA - URL (" + urlWebServiceRest + ") timeout (" + segundosTimeout + ")");

        final ObjectMapper mapper = new ObjectMapper();
        final String json = mapper.writeValueAsString(pedido);

        final HttpURLConnection conn = getHttpURLConnection(segundosTimeout, urlWebServiceRest, json);
        conn.setReadTimeout(segundosTimeout * 1000);
        conn.setRequestProperty("X-Requested-With", "XMLHttpRequest");

        final OutputStream os = new BufferedOutputStream(conn.getOutputStream());
        os.write(json.getBytes());
        //clean up
        os.flush();

        final BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        final StringBuilder sb = new StringBuilder();

        String line;
        while (null != (line = reader.readLine())) {
            sb.append(line).append("\n");
        }
        os.close();
        reader.close();

        return sb.toString();
}

    /**
     * Invoca un WebService REST enviando los datos con un JSON.
     *
     * @param segundosTimeout   para recibir una respuesta
     * @param urlWebServiceRest URL de invocacion al Web Service
     * @param pedido            datos del prepedido a enviar
     * @return Una cadena con el resultado de la invocacion al Web Service
     */
    private String invocaWebServiceHttps(final int segundosTimeout, final String urlWebServiceRest, final JsonPedido pedido) throws Exception {
        String result = "";

        Log.d("Sincronizacion", "TRAZA - URL (" + urlWebServiceRest + ") segundo timeout (" + segundosTimeout + ")");
        final X509TrustManager tm = new X509TrustManager() {
            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            @Override
            public void checkServerTrusted(final X509Certificate[] paramArrayOfX509Certificate, final String paramString) throws CertificateException {
            }

            @Override
            public void checkClientTrusted(final X509Certificate[] paramArrayOfX509Certificate, final String paramString) throws CertificateException {
            }
        };
        final SSLContext ctx = SSLContext.getInstance("TLS");
        ctx.init(null, new TrustManager[]{tm}, null);
        final URL url = new URL(urlWebServiceRest);
        String encoding = MyBase64.encode(usuario + ":" + passw);
        final HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setConnectTimeout(segundosTimeout * 1000);
        connection.setReadTimeout(segundosTimeout * 1000);
        connection.setSSLSocketFactory(ctx.getSocketFactory());
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setHostnameVerifier(new HostnameVerifier() {

                                           @Override
                                           public boolean verify(final String paramString, final SSLSession paramSSLSession) {
                                               return true;
                                           }
                                       }
        );
        connection.setRequestProperty("Authorization", "Basic " + encoding);
        connection.setRequestProperty("Content-Type", "application/json");
        final ObjectMapper mapper = new ObjectMapper();
        final String jsonPedido = mapper.writeValueAsString(pedido);
        final OutputStream os = connection.getOutputStream();
        os.write(jsonPedido.getBytes());
        os.flush();
        final InputStream content = connection.getInputStream();
        final BufferedReader in = new BufferedReader(new InputStreamReader(content));
        String line;
        while (null != (line = in.readLine())) {
            result += line;
        }
        return result;
    }

    public class TareaEnviaPrepedidos extends AsyncTask<Void, Void, Void> {
        private final int maximoValorBarraProgreso = 100;
        private final ProgressDialog dialog;
        private final Context contexto;
        private final String[] idPedidos;

        //Indica si se ha producido un error durante el proceso de envio de los prepedidos
        private boolean hayErrorEnvioPrepedido;

        //Almacena el identificador de los prepedidos que ha sido enviado correctamente a intraza
        private ArrayList<String> listaPrepedidosEnviados = new ArrayList<>();

        public TareaEnviaPrepedidos(final Context contexto, final String[] idPedidos) {
            this.contexto = contexto;
            this.idPedidos = idPedidos;
            dialog = new ProgressDialog(contexto);
        }

        protected void onPreExecute() {
            dialog.setMessage("Enviando pedidos...");
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            dialog.setProgress(0);
            dialog.setMax(maximoValorBarraProgreso);
            dialog.show();
        }

        protected Void doInBackground(final Void... args) {
            try {
                listaPrepedidosEnviados = enviaPrepedidos(idPedidos);

                //Si el numero de prepedidos enviados es menor que el que se indico que se queria enviar, es porque se ha producido un error el enviar todos
                //o alguno de los prepedidos
                if (listaPrepedidosEnviados.size() < idPedidos.length) {
                    throw new Exception("Error al enviar datos de prepedidos a InTraza");
                }
            } catch (final Exception e) {
                hayErrorEnvioPrepedido = true;

                Log.d("EnviarPrepedidos", "TRAZA - Excepcion (" + e.getMessage() + ")");
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(final Void success) {
            if (hayErrorEnvioPrepedido) {
                alertaMensajeError();
            } else {
                alertaMensajeSincronizacionCorrecta();
            }
            //Ejecutamos el metodo de la activity, cuando la tarea ya ha terminado
            finalTareaEnviaPedidos(listaPrepedidosEnviados);
            dialog.dismiss();
        }

        private void alertaMensajeError() {
            final Builder popup = new Builder(contexto);
            popup.setTitle(TITULO_ALERT_ERROR_ENVIO_PREPEDIDOS);
            popup.setMessage(MENSAJE_ALERT_ERROR_ENVIO_PREPEDIDOS);
            popup.setPositiveButton(aceptar_M, null);
            popup.show();
        }

        private void alertaMensajeSincronizacionCorrecta() {
            final Builder popup = new Builder(contexto);
            popup.setTitle(TITULO_ALERT_ENVIO_PREPEDIDOS_CORRECTO);
            popup.setMessage(MENSAJE_ALERT_ENVIO_PREPEDIDOS_CORRECTO);
            popup.setPositiveButton(aceptar_M, null);
            popup.show();
        }

        /**
         * Envia los prepedidos a intraza:
         * 1 - Obtiene los datos de los prepedidos a enviar de la BD, mediante un cursor.
         * 2 - Cada pedido del cursor se envia individualmente a InTraza.
         * 3 - Se hace el tratamiento correspondiente, segun el prepedido se haya enviado correctamente o no.
         *
         * @param idPrepedidos con los idPrepedidos a enviar a intraza
         * @return Lista de idPrepedidos que han sido enviados correctamente
         * @throws Exception
         */
        private ArrayList<String> enviaPrepedidos(final String... idPrepedidos) throws Exception {
            final ArrayList<String> prepedidosEnviados = new ArrayList<>();
            final AdaptadorBD db = new AdaptadorBD(contexto);
            Cursor cursorPedidos;
            Cursor cursorLineasPedido;
            JsonPedido jsonPedido;
            float incrementoParcial = 0;
            int incrementoTotal = 0;
            JSONObject jsonResultado;

            db.abrir();

            if (0 < idPrepedidos.length) {
                //noinspection IntegerDivisionInFloatingPointContext
                incrementoParcial = 100 / idPrepedidos.length;
            }

            for (int i = 0; i < idPrepedidos.length; i++) {
                //Obtenemos los datos del pedido
                cursorPedidos = db.obtenerPrepedidoConDatosCliente(Integer.parseInt(idPrepedidos[i]));
                //Obtenemos los datos de la linea de pedido
                cursorLineasPedido = db.obtenerTodosLosPrepedidosItemDelPrepedido(Integer.parseInt(idPrepedidos[i]));
                //Creamos un objeto con los datos del pedido a enviar al WS
                jsonPedido = creaObjetoJsonPedido(cursorPedidos, cursorLineasPedido, db);
                //Invocamos al WS para que introducir los datos del pedido en InTraza

                jsonResultado = new JSONObject(invocaWebServiceHttp(sincro, config.dameUriWebServicesEnvioPrepedido(), jsonPedido));
                //Comprobamos si el envio fue correo
                if (0 != jsonResultado.getInt("codigoError")) {
                    Log.d("EnviarPrepedidos", "TRAZA - ERROR al enviar los datos del prepedido (" + idPedidos[i] + ") (" + jsonResultado.getString("descripcionError") + ")");
                } else {
                    //Los pedidos enviados luego seran borrados por la activity de la BD de la tablet, para ello guardamos sus id's
                    prepedidosEnviados.add(idPedidos[i]);
                }
                //Actualizamos la barra de progreso
                dialog.incrementProgressBy(Float.valueOf(incrementoParcial).intValue());
                incrementoTotal += Float.valueOf(incrementoParcial).intValue();
                dialog.incrementProgressBy(100 - incrementoTotal);
            }

            db.cerrar();
            return prepedidosEnviados;
        }

        /**
         * Dado 2 cursores de la BD con la informacion de un pedido y sus lineas de pedidos asociadas, devuelve un objeto con los datos del mismo.
         * Para cada linea de pedido se debe obtener el nombre del articulo de la BD
         *
         * @param cursorPedido       cursor
         * @param cursorLineasPedido cursor
         * @param db                 adaptadorBD
         * @return objeto con los datos del pedido y sus lineas de pedido
         */
        private JsonPedido creaObjetoJsonPedido(final Cursor cursorPedido, final Cursor cursorLineasPedido, final AdaptadorBD db) {
            JsonPedido jsonPedido = null;
            final ArrayList<JsonLineaPedido> jsonLineasPedido;
            //Datos para pedido
            final int idPrepedidoP;
            final int idClienteP;
            final String clienteP;
            final int diaFechaPedidoP;
            final int mesFechaPedidoP;
            final int anioFechaPedidoP;
            final int diaFechaEntregaP;
            final int mesFechaEntregaP;
            final int anioFechaEntregaP;
            final String observacionesP;
            final boolean fijarObservacionesP;
            final int descuentoEspecialP;
            //Datos para la linea de pedido
            int idPrepedidoLP;
            int idArticuloLP;
            String codArticuloLP;
            float cantidadKgLP;
            int cantidadUdLP;
            float tarifaClienteLP;
            String observacionesLP;
            boolean fijarTarifaLP;
            boolean suprimirTarifaLP;
            boolean fijarArticuloLP;
            boolean fijarObservacionesLP;
            Cursor cursorArticulo;
            String nombreArticuloLP;

            //Contiene los datos de una fecha descompuesta, es decir, para una fecha DD-MM-YYYY, contiene 3 elementos,
            //en la posicion 0 tiene DD, en la posicion 1 tiene MM y en la posicion 2 tiene YYYY.
            String[] fechaDescompuesta;

            //Si tenemos resultado de la consulta...
            if (cursorPedido.moveToFirst()) {
                //Obtenemos los datos del pedido
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
                descuentoEspecialP = cursorPedido.getInt(TablaPrepedido.POS_CAMPO_DESCUENTO_ESPECIAL);

                //Ahora obtenemos los datos de las lineas de pedido
                jsonLineasPedido = new ArrayList<>();

                if (cursorLineasPedido.moveToFirst()) {
                    //Recorremos el cursor para obtener los datos de las lineas de pedido
                    do {
                        idPrepedidoLP = cursorLineasPedido.getInt(TablaPrepedidoItem.POS_CAMPO_ID_PREPEDIDO);
                        //Por si esta clonado, le quitamos la marca
                        idArticuloLP = cursorLineasPedido.getInt(TablaPrepedidoItem.POS_CAMPO_ID_ARTICULO);
                        codArticuloLP = cursorLineasPedido.getString(TablaPrepedidoItem.POS_CAMPO_CODIGO_ARTICULO).split(Constantes.CARACTER_OBLIGATORIO_MARCA_CLON_CODIGO_ARTICULO)[0];
                        cantidadKgLP = cursorLineasPedido.getFloat(TablaPrepedidoItem.POS_CAMPO_CANTIDAD_KG);
                        cantidadUdLP = cursorLineasPedido.getInt(TablaPrepedidoItem.POS_CAMPO_CANTIDAD_UD);
                        tarifaClienteLP = cursorLineasPedido.getFloat(TablaPrepedidoItem.POS_CAMPO_PRECIO);
                        observacionesLP = cursorLineasPedido.getString(TablaPrepedidoItem.POS_CAMPO_OBSERVACIONES);
                        fijarTarifaLP = 1 == cursorLineasPedido.getInt(TablaPrepedidoItem.POS_CAMPO_FIJAR_PRECIO);
                        suprimirTarifaLP = 1 == cursorLineasPedido.getInt(TablaPrepedidoItem.POS_CAMPO_SUPRIMIR_PRECIO);
                        fijarArticuloLP = 1 == cursorLineasPedido.getInt(TablaPrepedidoItem.POS_CAMPO_FIJAR_ARTICULO);
                        fijarObservacionesLP = 1 == cursorLineasPedido.getInt(TablaPrepedidoItem.POS_CAMPO_FIJAR_OBSERVACIONES);

                        //Obtenemos el nombre del articulo
                        //Por si esta clonado, le quitamos la marca
                        cursorArticulo = db.obtenerArticulo(codArticuloLP.split(Constantes.CARACTER_OBLIGATORIO_MARCA_CLON_CODIGO_ARTICULO)[0]);
                        cursorArticulo.moveToFirst();
                        nombreArticuloLP = cursorArticulo.getString(TablaArticulo.POS_CAMPO_NOMBRE);
                        jsonLineasPedido.add(new JsonLineaPedido(idPrepedidoLP, idArticuloLP, nombreArticuloLP,
                                cantidadKgLP, cantidadUdLP, tarifaClienteLP, observacionesLP, fijarTarifaLP,
                                suprimirTarifaLP, fijarArticuloLP, fijarObservacionesLP));
                    } while (cursorLineasPedido.moveToNext());
                }
                jsonPedido = new JsonPedido(idPrepedidoP, idClienteP, clienteP, diaFechaPedidoP, mesFechaPedidoP, anioFechaPedidoP, diaFechaEntregaP, mesFechaEntregaP, anioFechaEntregaP, observacionesP, fijarObservacionesP, descuentoEspecialP, jsonLineasPedido);
            }
            return jsonPedido;
        }
    }
}
