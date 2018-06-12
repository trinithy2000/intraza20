package intraza.com.interfaz.dialogos;

import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.speech.RecognizerIntent;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FocusChange;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.ColorRes;
import org.androidannotations.annotations.res.StringRes;

import java.util.ArrayList;
import java.util.GregorianCalendar;

import intraza.com.interfaz.datosDB.TablaObservacion;
import intraza.com.Configuracion;
import intraza.com.Constantes;
import intraza.com.interfaz.datos.DatosLineaPedido;
import intraza.com.interfaz.datosDB.AdaptadorBD;

import intraza.com.R.layout;
import intraza.com.R.id;
import intraza.com.R;
import intraza.com.interfaz.pantallas.PantallaRutero_;

/**
 * Activity que solicita al usuario los datos del nuevo pedido.
 *
 * @author JLZS
 */
@SuppressWarnings("CyclicClassDependency")
@EFragment(layout.dialogo_datos_linea_pedido)
public class DialogoDatosLineaPedido extends DialogPadreFragment {

    @StringRes
    String INFO_SIN_FECHA_ANTERIOR_LINEA_PEDIDO;

    @StringRes
    String MENSAJE_DATOS_INCLUIR_SELECCIONES;

    @StringRes
    String CADENA_PREFIJO_PRECIO_TOTAL;

    @StringRes
    String AVISO_DATOS_NUEVO_LP_CANTIDAD_0;

    @StringRes
    String AVISO_DATOS_NUEVO_LP_TARIFA_CLIENTE_0;

    @StringRes
    String MENSAJE_DATOS_QUITAR_SELECCIONES;

    @StringRes
    String MENSAJE_DATOS_NUEVO_LP_ELIMINADOS;

    @StringRes
    String MENSAJE_DATOS_NUEVO_LP_CLONAR_ACEPTADOS;

    @StringRes
    String MENSAJE_DATOS_NUEVO_LP_ACEPTADOS;

    @StringRes
    String totalAnyo;

    @StringRes
    String ultimaCantidad;

    @StringRes
    String ultimaTarifa;

    @StringRes
    String tarifaLista;

    @StringRes
    String pedidoDia;

    @ColorRes
    int colorTextoArticuloCongelado;

    @ColorRes
    int colorFilaDatoAnterior;

    @ColorRes
    int colorMedidaArticuloPorDefecto;

    @ViewById
    EditText cantidadKgNuevoLP;

    @ViewById
    EditText cantidadUdNuevoLP;

    @ViewById
    EditText tarifaNuevoLP;

    // Widgeds que contienen los datos que debe introducir el usuario para la nueva linea de pedido
    @ViewById
    EditText observacionesLP;

    @ViewById
    CheckBox checkFijarTarifaLP;

    @ViewById
    CheckBox checkSuprimirTarifaLP;

    @ViewById
    CheckBox checkFijarObservacionesLP;

    @ViewById
    TextView infoDatosAnterioresLP;

    @ViewById
    TextView datoCantidadAnteriorLP;

    @ViewById
    TextView textoTarifaListaLP;

    @ViewById
    TextView textoPrecioTotalLP;

    @ViewById
    TextView datoTarifaAnteriorLP;

    @ViewById
    TextView textoCantidadKgNuevoLP;

    @ViewById
    TextView textoCantidadUdNuevoLP;

    @ViewById
    TextView datoLP;

    @ViewById
    Button aceptarBotonDialogoNuevoLP;

    @ViewById
    Button clonarBotonDialogoNuevoLP;

    @ViewById
    Button eliminarBotonDialogoNuevoLP;

    @ViewById
    RelativeLayout listaCheckBoxObservacionesLP;

    @FragmentArg("DATOS_LINEA_PEDIDO")
    DatosLineaPedido datosLineaPedido;

    @FragmentArg("OBSERVACIONES_DEFECTO")
    String observacionesDefecto;

    @FragmentArg("DIALOGO_RETORNA_RESULTADO")
    int DIALOGO_RETORNA_RESULTADO;

    //Almacena los datos de la linea de pedido que se recibio como entrada al subdialogo, se usa para comprobar si ha cambiado algun dato
    //y por tanto esta pendiente de guardar
    private DatosLineaPedido datosLineaPedidoOriginal;


    @Override
    public void onStart() {
        super.onStart();
        DisplayMetrics displaymetrics = new DisplayMetrics();
        this.getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int dWidth = displaymetrics.widthPixels;

        Dialog d = getDialog();
        if (d != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = (int) (dWidth / 2f);
            d.getWindow().setLayout(width, height);
        }
    }


    @AfterViews
    void init() {
        setStyle(STYLE_NO_TITLE, intraza.com.R.style.MY_DIALOG);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);


        //Tenemos que crear una nueva instancia del objeto, ya que sino se referencia al mismo objeto
        datosLineaPedidoOriginal = new DatosLineaPedido(datosLineaPedido.getIdArticulo(),datosLineaPedido.getCodArticulo(), datosLineaPedido.getArticulo(),
                datosLineaPedido.getMedida(), datosLineaPedido.isCongelado(), datosLineaPedido.getUltimaFecha(),
                datosLineaPedido.getUltimaUnidades(), datosLineaPedido.getUltimaCantidad(), datosLineaPedido.getUnidadesTotalAnio(), datosLineaPedido.getCantidadTotalAnio(),
                datosLineaPedido.getUltimaTarifa(), datosLineaPedido.getCantidadKg(), datosLineaPedido.getCantidadUd(),
                datosLineaPedido.getTarifaCliente(), datosLineaPedido.getTarifaLista(),
                datosLineaPedido.getFechaCambioTarifaLista(), datosLineaPedido.getObservaciones(), datosLineaPedido.isFijarTarifa(), datosLineaPedido.isSuprimirTarifa(), false, false, false);

        //Actulizamos los widget de la pantalla con los datos, para mostrarselos al usuario
        //En caso de ser un codigo de articulo que indica que es un clon, al usuario solo se le muestra el codigo del articulo del original del clon
        //por eso se hace el split
        if (this.datosLineaPedido.isCongelado()) {
            datoLP.setTextColor(colorTextoArticuloCongelado);
        }
        datoLP.setText(this.datosLineaPedido.getCodArticulo().split(Constantes.CARACTER_OBLIGATORIO_MARCA_CLON_CODIGO_ARTICULO)[0] + " - "
                + this.datosLineaPedido.getArticulo() + ponerMarcaCongelado(this.datosLineaPedido.isCongelado()));

        if (this.datosLineaPedido.getUltimaFecha().equals(Constantes.SIN_FECHA_ANTERIOR_LINEA_PEDIDO)) {
            infoDatosAnterioresLP.setText(INFO_SIN_FECHA_ANTERIOR_LINEA_PEDIDO);
        } else {
            infoDatosAnterioresLP.setText(pedidoDia + this.datosLineaPedido.getUltimaFecha() + ".");
        }
        infoDatosAnterioresLP.setTextColor(colorFilaDatoAnterior);
        datoCantidadAnteriorLP.setText(ultimaCantidad + Constantes.formatearFloat3Decimales.format(this.datosLineaPedido.getUltimaCantidad()) + Constantes.SEPARADOR_CANTIDAD_TOTAL_ANIO
                + totalAnyo + Constantes.formatearFloat2Decimales.format(this.datosLineaPedido.getCantidadTotalAnio()));
        datoCantidadAnteriorLP.setTextColor(colorFilaDatoAnterior);
        datoTarifaAnteriorLP.setText(ultimaTarifa + Constantes.formatearFloat2Decimales.format(this.datosLineaPedido.getUltimaTarifa()) + Constantes.EURO
                + Constantes.SEPARADOR_MEDIDA_TARIFA + this.datosLineaPedido.getMedida());
        datoTarifaAnteriorLP.setTextColor(colorFilaDatoAnterior);

        cantidadKgNuevoLP.setText(Constantes.formatearFloat3Decimales.format(this.datosLineaPedido.getCantidadKg()));
        cantidadUdNuevoLP.setText(Integer.valueOf(this.datosLineaPedido.getCantidadUd()).toString());
        tarifaNuevoLP.setText(Constantes.formatearFloat2Decimales.format(this.datosLineaPedido.getTarifaCliente()));
        checkFijarTarifaLP.setChecked(this.datosLineaPedido.isFijarTarifa());
        checkSuprimirTarifaLP.setChecked(this.datosLineaPedido.isSuprimirTarifa());
        textoTarifaListaLP.setText(tarifaLista + ponerMarcaCambioTarifaListaReciente(this.datosLineaPedido.getFechaCambioTarifaLista())
                + Constantes.formatearFloat2Decimales.format(this.datosLineaPedido.getTarifaLista()) + Constantes.EURO + Constantes.SEPARADOR_MEDIDA_TARIFA + this.datosLineaPedido.getMedida());
        textoPrecioTotalLP.setText(CADENA_PREFIJO_PRECIO_TOTAL + Constantes.formatearFloat2Decimales.format(this.datosLineaPedido.getPrecio()) + Constantes.EURO);
        observacionesLP.setText(this.datosLineaPedido.getObservaciones());
        checkFijarObservacionesLP.setChecked(this.datosLineaPedido.isFijarObservaciones());

        //Creamos los checkBox de las observaciones
        creaCheckBoxObservacionesBD();

        //Si tenemos datos anteriores en la linea de pedido habilitamos el boton eliminar y clonar
        if (0 < this.datosLineaPedido.getCantidadKg() || 0 < this.datosLineaPedido.getCantidadUd()) {
            eliminarBotonDialogoNuevoLP.setEnabled(true);
            clonarBotonDialogoNuevoLP.setEnabled(true);
        }

        //Segun la medida del articulo, se pone el foco en el EditText de Kg o Ud y cambiamos el TextView de color para indicar que es la medida por defecto al usuario
        if (datosLineaPedido.getMedida().equals(Constantes.KILOGRAMOS)) {
            cantidadKgNuevoLP.requestFocus();
            textoCantidadKgNuevoLP.setTextColor(colorMedidaArticuloPorDefecto);
        } else {
            cantidadUdNuevoLP.requestFocus();
            textoCantidadUdNuevoLP.setTextColor(colorMedidaArticuloPorDefecto);
        }
    }

    @Click(R.id.checkFijarTarifaLP)
    void fijarTarifaClick() {
        if (checkFijarTarifaLP.isChecked()) {
            checkSuprimirTarifaLP.setChecked(false);
            checkSuprimirTarifaLP.setEnabled(false);
        } else {
            checkSuprimirTarifaLP.setEnabled(true);
        }
    }

    @Click(R.id.checkSuprimirTarifaLP)
    void suprimirTarifaClick() {
        if (checkSuprimirTarifaLP.isChecked()) {
            checkFijarTarifaLP.setChecked(false);
            checkFijarTarifaLP.setEnabled(false);
        } else {
            checkFijarTarifaLP.setEnabled(true);
        }
    }

    //Tenemos que quitar las observaciones que se hubieran añadido con los checkbox, es decir, ponemos las observaciones por defecto

    @Click(id.botonQuitarObservacionesLP)
    void ObservacionesBtnQuitarClick() {
        //Actualizamos el widget con las observaciones por defecto si no estan ya puestas
        if (!observacionesDefecto.trim().equals(observacionesLP.getText().toString().trim())) {
            observacionesLP.setText(observacionesDefecto);

            Toast.makeText(getActivity().getBaseContext(), MENSAJE_DATOS_QUITAR_SELECCIONES, Toast.LENGTH_SHORT).show();
        }

        //Al poner las observaciones por defecto, no tiene sentido que este chequeado el fijarlas
        checkFijarObservacionesLP.setChecked(false);

        //Guardamos las observaciones en los datos de la linea de pedido para no perderlos
        datosLineaPedido.setObservaciones(observacionesLP.getText().toString().trim());
    }


    //Tenemos que añadir en las observaciones, los checkbox que esten seleccionados
    @Click(id.botonIncluirObservacionesLP)
    void ObservacionesBtnClick() {
        boolean seHanIncluido = false;

        //Recorremos todos los checkBox y añadimos el que este chequeado
        for (int i = 0; i < listaCheckBoxObservacionesLP.getChildCount(); i++) {
            CheckBox observacionLista = (CheckBox) listaCheckBoxObservacionesLP.getChildAt(i);
            if (observacionLista.isChecked()) {
                seHanIncluido = true;

                String observaciones = observacionesLP.getText().toString().trim();
                String observacion = observacionLista.getText().toString();

                //Añadimos el "." al final de la observación sino viene, para que quede mas claro.
                if (!observacion.endsWith(".")) {
                    observacion += ".";
                }

                if (observaciones.length() > 0) {
                    //Añadimos la observacion a las observaciones, insertandola con una linea nueva.
                    observaciones += " " + observacion;
                } else {
                    observaciones += observacion;
                }

                //Actualizamos el widget
                observacionesLP.setText(observaciones);
            }
            //Deschequeamos el CheckBox, pues ya hemos añadido la linea de comentario
            ((CheckBox) listaCheckBoxObservacionesLP.getChildAt(i)).setChecked(false);
        }

        if (seHanIncluido) {
            Toast.makeText(getActivity().getBaseContext(), MENSAJE_DATOS_INCLUIR_SELECCIONES, Toast.LENGTH_SHORT).show();
        }

        //Guardamos las observaciones en los datos de la linea de pedido para no perderlos
        datosLineaPedido.setObservaciones(observacionesLP.getText().toString().trim());
    }


    @FocusChange
    void tarifaNuevoLPFocusChanged(boolean hasFocus) {
        if (!hasFocus) {
            if (tarifaNuevoLP.getText().toString().length() == 0) {
                tarifaNuevoLP.setText("0");
            }

            datosLineaPedido.setTarifaCliente(Float.parseFloat(tarifaNuevoLP.getText().toString().replace(',', '.')));
            textoPrecioTotalLP.setText(CADENA_PREFIJO_PRECIO_TOTAL + Constantes.formatearFloat2Decimales.format(datosLineaPedido.getPrecio()) + Constantes.EURO);
        }
    }

    //Definimos evento del EditText que recoge la cantidad de la linea de pedido, cuando pierde el foco, para recalcular el precio total
    @FocusChange
    void cantidadKgNuevoLPFocusChanged(boolean hasFocus) {
        if (!hasFocus) {
            if (cantidadKgNuevoLP.getText().toString().length() == 0
                    || cantidadKgNuevoLP.getText().toString().equals("-")
                    || cantidadKgNuevoLP.getText().toString().equals("0")
                    || cantidadKgNuevoLP.getText().toString().equals("-0")) {
                cantidadKgNuevoLP.setText("-1");
            }
            datosLineaPedido.setCantidadKg(Float.parseFloat(cantidadKgNuevoLP.getText().toString().replace(',', '.')));
            textoPrecioTotalLP.setText(CADENA_PREFIJO_PRECIO_TOTAL + Constantes.formatearFloat2Decimales.format(datosLineaPedido.getPrecio()) + Constantes.EURO);
        }else if (cantidadKgNuevoLP.getText().toString().length() == 0
                || cantidadKgNuevoLP.getText().toString().equals("-1")){
            cantidadKgNuevoLP.setText("");
        }
    }


//Definimos evento del EditText que recoge la cantidad de la linea de pedido, cuando pierde el foco, para recalcular el precio total

    @FocusChange
    void cantidadUdNuevoLPFocusChanged(boolean hasFocus) {
        if (!hasFocus) {
            if (cantidadUdNuevoLP.getText().toString().length() == 0
                    || cantidadUdNuevoLP.getText().toString().equals("-")
                    || cantidadUdNuevoLP.getText().toString().equals("-0")) {
                cantidadUdNuevoLP.setText("0");
            }

            datosLineaPedido.setCantidadUd(Integer.parseInt(cantidadUdNuevoLP.getText().toString()));
            textoPrecioTotalLP.setText(CADENA_PREFIJO_PRECIO_TOTAL + Constantes.formatearFloat2Decimales.format(datosLineaPedido.getPrecio()) + Constantes.EURO);
        }
    }

    /**
     * Muestra una marca al final de la descripcion del articulo en caso que sea congelado
     *
     * @param esCongelado es congelado
     * @return la marca a poner o cadena vacia en caso de no llevar marca
     */

    private String ponerMarcaCongelado(boolean esCongelado) {
        return esCongelado ? Constantes.MARCA_CONGELADO : "";
    }

    /**
     * Comprueba si hay que poner la marca de cambio reciente en la tarifa por defecto.
     * Las fechas en la BD de la tablet se guardan como DD/MM/YYYY
     *
     * @param fecha de cambio de la tarifa por defecto
     * @return la marca si hay que poner la marca, cadena vacia en cualquier otro caso
     */
    private String ponerMarcaCambioTarifaListaReciente(String fecha) {
        String marca = "";
        String[] fechaDescompuesta;
        GregorianCalendar calendarFechaCaducidadCambioReciente;

        if (fecha != null && !fecha.trim().equals("null") && !fecha.trim().equals("")) {
            fechaDescompuesta = fecha.substring(0,10).split(Constantes.SEPARADOR_FECHA);
            //A la fecha actual le sumamos los dias configurados para que se muestre la marca de cambio de tarifa por defecto,
            //asi obtenemos la fecha en que caduca la marca
            calendarFechaCaducidadCambioReciente = new GregorianCalendar(Integer.parseInt(fechaDescompuesta[2]), Integer.parseInt(fechaDescompuesta[1]), Integer.parseInt(fechaDescompuesta[0]));
            calendarFechaCaducidadCambioReciente.add(GregorianCalendar.DAY_OF_YEAR, Configuracion.getInstance().dameNumDiasAntiguedadMarcaTarifaDefecto());

            //Comprobamos si la marca ha caducado
            if (calendarFechaCaducidadCambioReciente.after(new GregorianCalendar())) {
                marca = Constantes.MARCA_TARIFA_DEFECTO_CAMBIADA_RECIENTEMENTE;
            }
        }

        return marca;
    }

    @Click(R.id.aceptarBotonDialogoNuevoLP)
    void clickAceptar() {
        onClickButton(false);
    }

    @Click(R.id.clonarBotonDialogoNuevoLP)
    void clickClonar() {
        onClickButton(true);
    }

    /**
     * Devuelve el tratamiento del evento OnClick cuando se han introducido los datos de la linea de pedido y se quieren guardar, ya sea al ACEPTAR o al CLONAR
     *
     * @param esClon indica si es un clon del articulo
     */

    private void onClickButton(final boolean esClon) {
        if (cantidadKgNuevoLP.getText().toString().length() == 0
                || cantidadKgNuevoLP.getText().toString().equals("-")
                || cantidadKgNuevoLP.getText().toString().equals("0")
                || cantidadKgNuevoLP.getText().toString().equals("-0")) {
            cantidadKgNuevoLP.setText("-1");
        }

        if (cantidadUdNuevoLP.getText().toString().length() == 0
                || cantidadUdNuevoLP.getText().toString().equals("-")
                || cantidadUdNuevoLP.getText().toString().equals("-0")) {
            cantidadUdNuevoLP.setText("0");
        }

        //La tarifa solo adminte numeros positivos
        if (tarifaNuevoLP.getText().toString().length() == 0) {
            tarifaNuevoLP.setText("0");
        }

        //Primero guardamos y recalculamos los datos
        datosLineaPedido.setCantidadKg(Float.parseFloat(cantidadKgNuevoLP.getText().toString().replace(',', '.')));
        datosLineaPedido.setCantidadUd(Integer.parseInt(cantidadUdNuevoLP.getText().toString()));
        datosLineaPedido.setTarifaCliente(Float.parseFloat(tarifaNuevoLP.getText().toString().replace(',', '.')));
        datosLineaPedido.setFijarTarifa(checkFijarTarifaLP.isChecked());
        datosLineaPedido.setSuprimirTarifa(checkSuprimirTarifaLP.isChecked());
        datosLineaPedido.setObservaciones(observacionesLP.getText().toString().trim());
        datosLineaPedido.setFijarObservaciones(checkFijarObservacionesLP.isChecked());

        //No se puede aceptar los datos si la cantidad es 0, por defecto la cantidad en KG es -1
        if (datosLineaPedido.getCantidadKg() == -1 && datosLineaPedido.getCantidadUd() == 0) {
            toastMensajeError(AVISO_DATOS_NUEVO_LP_CANTIDAD_0);
        }
        //No se puede aceptar los datos si la tarifa del cliente es 0 a no ser que este configurado para que si los acepte
        else if (datosLineaPedido.getTarifaCliente() == 0 && !Configuracion.getInstance().estaPermitidoLineasPedidoConPrecio0()) {
            toastMensajeError(AVISO_DATOS_NUEVO_LP_TARIFA_CLIENTE_0);
        } else {
            returnDatosNuevoLP(esClon);
        }
    }

    /**
     * Crea la lista de chekBox, obteniendo las datos de la lista de la BD
     */
    private void creaCheckBoxObservacionesBD() {

        RelativeLayout.LayoutParams paramsRelativeLayout;
        AdaptadorBD db = new AdaptadorBD(getActivity());
        int idViewAnterior = listaCheckBoxObservacionesLP.getId();
        //Consultamos todas las observaciones y creamos con  cada una de ellas un checkBox
        db.abrir();

        Cursor cursorObservaciones = db.obtenerTodasLasObservacionesPrepedidoItem();

        if (cursorObservaciones.moveToFirst()) {
            do {
                CheckBox checkBox = new CheckBox(getActivity());
                //Hay que usar el RelativeLayout del padre que contiene al CheckBox
                paramsRelativeLayout = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                paramsRelativeLayout.addRule(RelativeLayout.BELOW, idViewAnterior);
                idViewAnterior = cursorObservaciones.getInt(TablaObservacion.POS_KEY_CAMPO_ID_OBSERVACION);
                checkBox.setId(idViewAnterior);
                checkBox.setLayoutParams(paramsRelativeLayout);
                checkBox.setText(cursorObservaciones.getString(TablaObservacion.POS_CAMPO_DESCRIPCION));
                listaCheckBoxObservacionesLP.addView(checkBox);
            } while (cursorObservaciones.moveToNext());
        }

        db.cerrar();
    }

    /**
     * - Devuelve a la activity que lo solicito los datos el nuevo pedido, introducidos por el usuario.
     * - Indica que le ejecución de la activity ha sido OK.
     * - Finaliza la activity.
     *
     * @param esClon indica si es un clon del articulo
     */

    private void returnDatosNuevoLP(boolean esClon) {

        //Comprobamos si hay algun cambio en los datos de la linea de pedido
        if (!this.datosLineaPedido.equals(this.datosLineaPedidoOriginal)) {
            this.datosLineaPedido.setHayCambiosSinGuardar(true);
        }
//Le pasamos los datos de la linea de pedido a la activity principal en el intent y si tenemos que clonar o no
        Intent intent = PantallaRutero_.intent(this).extra("DATOS_LINEA_PEDIDO", datosLineaPedido).extra("ES_CLON", esClon).get();
        ((PantallaRutero_) getActivity()).onActivityResult(DIALOGO_RETORNA_RESULTADO, Activity.RESULT_OK, intent);

        if (!esClon) {
            Toast.makeText(getActivity().getBaseContext(), MENSAJE_DATOS_NUEVO_LP_ACEPTADOS, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity().getBaseContext(), MENSAJE_DATOS_NUEVO_LP_CLONAR_ACEPTADOS, Toast.LENGTH_SHORT).show();
        }
        dismiss();
    }

    /**
     * - Devuelve a la activity que lo solicito los datos el nuevo pedido, introducidos por el usuario.
     * - Indica que le ejecución de la activity ha sido OK.
     * - Finaliza la activity.
     */


    @Click(R.id.eliminarBotonDialogoNuevoLP)
    void eliminarDatosNuevoLP() {
        Intent intent = PantallaRutero_.intent(this).get();
        ((PantallaRutero_) getActivity()).onActivityResult(DIALOGO_RETORNA_RESULTADO, Activity.RESULT_FIRST_USER, intent);
        Toast.makeText(getActivity().getBaseContext(), MENSAJE_DATOS_NUEVO_LP_ELIMINADOS, Toast.LENGTH_SHORT).show();
        dismiss();
    }

    /**
     * Termina la activity
     */
    @Click(R.id.cancelarBotonDialogoNuevoLP)
    void cancelarDialogo() {
        dismiss();
    }

    void log(String text) {
        Log.d("DialogoDatosNuevoPedido", text);
    }

    @Click(id.botonDictarObservacionesLP)
    void dictarObservaciones() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "es-ES");
        try {
            startActivityForResult(intent, 1);
        } catch (ActivityNotFoundException a) {
            Toast t = Toast.makeText(getActivity().getApplicationContext(),
                    "Opps! Your device doesn't support Speech to Text",
                    Toast.LENGTH_SHORT);
            t.show();
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1: {
                if (resultCode == Activity.RESULT_OK && null != data) {
                    ArrayList<String> text = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    observacionesLP.append(text.get(0));
                }
                break;
            }
        }
    }
}