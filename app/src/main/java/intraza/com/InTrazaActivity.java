package intraza.com;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.StringRes;

import intraza.com.interfaz.datos.DatosConsultaPedidos;
import intraza.com.interfaz.datos.DatosPedido;
import intraza.com.interfaz.dialogos.DialogoAcercaDe_;
import intraza.com.interfaz.dialogos.DialogoConfirmacionSincronizar_;
import intraza.com.interfaz.dialogos.DialogoDatosConsultaPedidos_;
import intraza.com.interfaz.dialogos.DialogoDatosPedido_;
import intraza.com.interfaz.pantallas.PantallaConfiguracion_;
import intraza.com.interfaz.pantallas.PantallaListaPedidos_;
import intraza.com.interfaz.pantallas.PantallaRutero_;
import intraza.com.interfaz.task.utils.WebServicesUtils;
import intraza.com.interfaz.datosDB.AdaptadorBD;

import intraza.com.R.layout;
import intraza.com.R.id;

/**
 * Activity principal.
 */

@EActivity(layout.main)
public class InTrazaActivity extends Activity {
    // Codigos de los subdialogos que se usan en la Activity
    private static final int DIALOGO_PIDE_DATOS_NUEVO_PEDIDO = 0;
    private static final int PANTALLA_RUTERO = 1;
    private static final int DIALOGO_PIDE_DATOS_CONSULTA_PEDIDOS = 2;
    private static final int PANTALLA_LISTA_PEDIDOS = 3;
    private static final int PANTALLA_CONFIRMACION_SINCRONIZAR = 4;
    private static final int REDIRECCIONA_A_PANTALLA_RUTERO = 5;
    private static final int RETORNA_CANCELEAR = 6;

    public static final String MESSAGE_PEDIDOS = "DATOS_CONSULTA_PEDIDOS";

    @Extra("DATOS_PEDIDO")
    DatosPedido datosPedido;

    @StringRes
    String TITULO_ALERT_ERROR_NO_SINCRONIZAR_PEDIDOS_PENDIENTES;

    @StringRes
    String MENSAJE_ALERT_ERROR_NO_SINCRONIZAR_PEDIDOS_PENDIENTES;

    @StringRes
    String MENSAJE_ALERT_SINCRONIZACION_CORRECTA;

    @StringRes
    String TITULO_ALERT_SINCRONIZACION_CORRECTA;

    @StringRes
    String TITULO_ALERT_ERROR_SINCRONIZAR;

    @StringRes
    String articulos;

    @StringRes
    String errores;

    @StringRes
    String sincronizando;

    @StringRes
    String Aceptar_M;

    @StringRes
    String pg_clientes;

    @StringRes
    String pg_articulos;

    @StringRes
    String pg_ruteros_R;

    @StringRes
    String pg_tarifas_R;

    @StringRes
    String pg_pesosUnidades_R;

    @StringRes
    String pg_ruteros_W;

    @ViewById
    Button botonMainAcercaDe;

    @ViewById
    Button botonMainConfiguracion;

    @ViewById
    Button botonMainSincronizar;

    @ViewById
    Button botonMainPedidos;

    @ViewById
    Button botonMainRutero;

    @ViewById
    EditText textoPassword;

    ProgressDialog mDialog;

    private Configuracion config;

    @AfterViews
    void init() {
        config = Configuracion.getInstance();
        config.preparaPropiedades(this);
    }

    /**
     * Deshabilita o no, todos los eventos onClick de la activity para evitar ejecutar dos click seguidos
     *
     * @param habilita d
     */
    private void habilitaClickEnActivity(final boolean habilita) {
        findViewById(id.botonMainRutero).setClickable(habilita);
        findViewById(id.botonMainPedidos).setClickable(habilita);
        findViewById(id.botonMainSincronizar).setClickable(habilita);
        findViewById(id.botonMainAcercaDe).setClickable(habilita);
        findViewById(id.botonMainConfiguracion).setClickable(habilita);
    }


    @Click(id.botonMainRutero)
    void subdialogoDatosNuevoPedido() {
        DialogoDatosPedido_.builder()
                .arg("DIALOGO_PIDE_DATOS", REDIRECCIONA_A_PANTALLA_RUTERO)
                .build()
                .show(getFragmentManager(), null);
    }

    private void pantallaRutero(final DatosPedido datosPedido) {
        PantallaRutero_.intent(this).datosPedido(datosPedido).startForResult(PANTALLA_RUTERO);
    }

    @Click(id.botonMainPedidos)
    void subdialogoDatosConsultaPedidos() {
        DialogoDatosConsultaPedidos_.builder()
                .arg("DIALOGO_RETORNA_RESULTADO", DIALOGO_PIDE_DATOS_CONSULTA_PEDIDOS)
                .build()
                .show(getFragmentManager(), null);
    }

    private void pantallaListaPedidos(final DatosConsultaPedidos datosConsultaPedidos) {
        PantallaListaPedidos_.intent(this)
                .extra("DATOS_CONSULTA_PEDIDOS", datosConsultaPedidos)
                .startForResult(PANTALLA_LISTA_PEDIDOS);
    }

    @Click(id.botonMainSincronizar)
    void subdialogoConfirmacionSincronizar() {
        boolean permitidaSincronizacion = true;

        //Si no esta permitida la sincronizacion si hay lineas de pedido pendientes, lo comprobamos
        if (!config.estaPermitidoSincronizacionConPedidosPendientes()) {
            if (hayPedidosPendientes()) {
                permitidaSincronizacion = false;

                final Builder popup = new Builder(this);
                popup.setTitle(TITULO_ALERT_ERROR_NO_SINCRONIZAR_PEDIDOS_PENDIENTES);
                popup.setMessage(MENSAJE_ALERT_ERROR_NO_SINCRONIZAR_PEDIDOS_PENDIENTES);
                popup.setPositiveButton(Aceptar_M, null);
                popup.show();
            }
        }

        if (permitidaSincronizacion) {
            DialogoConfirmacionSincronizar_.builder()
                    .arg("DIALOGO_RETORNA_RESULTADO", PANTALLA_CONFIRMACION_SINCRONIZAR)
                    .build()
                    .show(getFragmentManager(), null);
        }
    }

    /**
     * Comprueba mediante una consulta a la BD si existen pedidos pendientes de enviar a InTraza.
     *
     * @return true en caso de existir pedido pendiente, false en cualquier otro caso
     */
    private boolean hayPedidosPendientes() {

        final AdaptadorBD db = new AdaptadorBD(this);
        db.abrir();
        final Cursor cursorPedidos = db.obtenerTodosLosPrepedidos();
        final boolean hayPendientes = (0 < cursorPedidos.getCount());
        db.cerrar();
        return hayPendientes;
    }

    @Click(id.botonMainAcercaDe)
    void subdialogoAcercaDe() {
        habilitaClickEnActivity(false);
        DialogoAcercaDe_.builder().build().show(getFragmentManager(), null);
        habilitaClickEnActivity(true);
    }

    @Click(id.botonMainConfiguracion)
    void subdialogoConfiguracion() {

        final LayoutInflater li = LayoutInflater.from(this);
        final View promptsView = li.inflate(layout.dialogo_password, null);
        final Builder alertDialogBuilder = new Builder(this);
        alertDialogBuilder.setView(promptsView);

        final EditText userInput = promptsView.findViewById(intraza.com.R.id.textoPassword);

        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new OnClickListener() {
                            public void onClick(final DialogInterface dialog, final int id) {
                                final String passw = userInput.getText().toString();
                                if (passw.equals(Constantes.PASSWORD_CONFIG)) {
                                    cargaDialogoConfiguracion();
                                    dialog.cancel();
                                    dialog.dismiss();
                                }
                            }
                        }
                );

        // create alert dialog
        final AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();


    }

    void cargaDialogoConfiguracion() {
        habilitaClickEnActivity(false);
        PantallaConfiguracion_.intent(this).start();
        habilitaClickEnActivity(true);
    }

    @OnActivityResult(DIALOGO_PIDE_DATOS_NUEVO_PEDIDO)
    void onResultUno(final int resultCode, final Intent data) {
        if (Activity.RESULT_OK == resultCode) {
            final DatosPedido datosPedido = data.getParcelableExtra("DATOS_PEDIDO");
            pantallaRutero(datosPedido);
        }
        habilitaClickEnActivity(true);
    }

    @OnActivityResult(DIALOGO_PIDE_DATOS_CONSULTA_PEDIDOS)
    void onResultDos(final int resultCode, final Intent data) {
        if (Activity.RESULT_OK == resultCode) {
            final DatosConsultaPedidos datosConsultaPedidos = data.getParcelableExtra("DATOS_CONSULTA_PEDIDOS");
            pantallaListaPedidos(datosConsultaPedidos);
        }
        habilitaClickEnActivity(true);
    }

    @OnActivityResult(PANTALLA_CONFIRMACION_SINCRONIZAR)
    @Background
    void onResultTres(final int resultCode, final Intent data) {
        habilitaClickEnActivity(false);
        final WebServicesUtils wsUtils = new WebServicesUtils(this);

        Configuracion.ponFechaActualComoUltimaFechaSincronizacion(this);

        //Cuando el usuario pulsa el boton SINCRONIZAR hacemos la sincronizacion con los datos de InTraza
        if (Activity.RESULT_OK == resultCode) {
            try {

                wsUtils.registrosTotalesParaSincronizar();

                this.cargaVentanaDialogo(pg_clientes, wsUtils);
                wsUtils.sincronizaClientes();

                this.cargaVentanaDialogo(pg_articulos, wsUtils);
                wsUtils.sincronizaArticulos();

                this.cargaVentanaDialogo(pg_ruteros_R, wsUtils);
                wsUtils.sincronizaRuterosDatos();

                ejecutaRuteros();
            } catch (final Exception ex) {
                Log.d("Sincronizacion", "TRAZA - Excepcion (" + ex.getMessage() + ")");
                habilitaClickEnActivity(true);
            }
        }
        habilitaClickEnActivity(true);
    }

    @Background
    void ejecutaRuteros() {
        try {
            final WebServicesUtils wsUtils = new WebServicesUtils(this);
            wsUtils.registrosTotalesParaSincronizar();
            cargaVentanaDialogo(pg_ruteros_W, wsUtils);
            wsUtils.sincronizaRuterosTotal();

        } catch (final Exception ex) {
            Log.d("Sincronizacion", "TRAZA - Excepcion (" + ex.getMessage() + ")");
            habilitaClickEnActivity(true);
            alertaMensajeSincronizacionInCorrecta(ex.getMessage());
        }
        postExecute();
    }

    @OnActivityResult(REDIRECCIONA_A_PANTALLA_RUTERO)
    void onResultCinco(final int resultCode, final Intent data) {
        if (Activity.RESULT_OK == resultCode) {
            final DatosPedido datosPedido = data.getParcelableExtra("DATOS_PEDIDO");
            pantallaRutero(datosPedido);
        }
        habilitaClickEnActivity(true);
    }

    @OnActivityResult(RETORNA_CANCELEAR)
    void onResultSeis(final int resultCode, final Intent data) {
        habilitaClickEnActivity(true);
    }

    @UiThread
    void postExecute() {
        mDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        Configuracion.ponFechaActualComoUltimaFechaSincronizacion(this);
        alertaMensajeSincronizacionCorrecta();
    }

    void alertaMensajeSincronizacionCorrecta() {
        final Builder popup = new Builder(this);
        popup.setTitle(TITULO_ALERT_SINCRONIZACION_CORRECTA);
        popup.setMessage(MENSAJE_ALERT_SINCRONIZACION_CORRECTA);
        popup.setPositiveButton(Aceptar_M, null);
        popup.show();
    }

    void alertaMensajeSincronizacionInCorrecta(String error) {
        final Builder popup = new Builder(this);
        popup.setTitle(TITULO_ALERT_ERROR_SINCRONIZAR);
        popup.setMessage(error);
        popup.setPositiveButton(Aceptar_M, null);
        popup.show();
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
        mDialog.show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if ((mDialog != null) && mDialog.isShowing())
            mDialog.dismiss();
        mDialog = null;
    }
}