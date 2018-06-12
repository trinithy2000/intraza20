package intraza.com.interfaz.dialogos;


import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.androidannotations.annotations.AfterTextChange;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.StringRes;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

import intraza.com.InTrazaActivity_;
import intraza.com.interfaz.datos.DatosPedido;
import intraza.com.interfaz.datos.DatosDialogoMensaje;
import intraza.com.interfaz.datosDB.AdaptadorBD;
import intraza.com.interfaz.datosDB.TablaCliente;
import intraza.com.interfaz.datosDB.TablaObservacion;

import intraza.com.R.layout;
import intraza.com.R.id;
import intraza.com.R.color;
import intraza.com.interfaz.pantallas.PantallaRutero_;

import static android.widget.AdapterView.OnItemSelectedListener;

/**
 * Activity que solicita al usuario los datos del nuevo pedido.
 */

@EFragment(layout.dialogo_datos_pedido)
public class DialogoDatosPedido extends DialogPadreFragment {
    //Codigos de los subdialogos que se usan en la Activity
    private static final int DIALOGO_MENSAJE_INFO_TELEFONO = 0;

    //Almacena la fecha del sistema, en el momento en que se crea el subdialogo
    private GregorianCalendar calendarFechaSistema;

    //Almacena las observaciones por defecto
    @FragmentArg("OBSERVACIONES_DEFECTO")
    String observacionesDefecto;

    //Almacena los datos del pedido
    @FragmentArg("DATOS_PEDIDO")
    DatosPedido datosPedido;

    @FragmentArg("DIALOGO_PIDE_DATOS")
    int DIALOGO_PIDE_DATOS_NUEVO_PEDIDO;

    @StringRes
    String TITULO_INFO_TELEFONO;

    @StringRes
    String INFORMACION_INFO_TELEFONO;

    @StringRes
    String MENSAJE_TITULO_DIALOGO_NUEVO_PEDIDO;

    @StringRes
    String MENSAJE_TITULO_DIALOGO_MODIFICA_PEDIDO;

    @StringRes
    String MENSAJE_DATOS_INCLUIR_SELECCIONES;

    @StringRes
    String MENSAJE_DATOS_QUITAR_SELECCIONES;

    @StringRes
    String TITULO_ALERT_ERROR_NO_SINCRONIZAR_PEDIDOS_PENDIENTES;

    @StringRes
    String AVISO_DATOS_DCNP_FECHA_NO_VALIDA;

    @StringRes
    String AVISO_DATOS_DCNP_CLIENTE_NO_VALIDO;

    @StringRes
    String MENSAJE_ALERT_ERROR_NO_SINCRONIZAR_PEDIDOS_PENDIENTES;

    @StringRes
    String aceptar_M;

    @ViewById
    Button botonInfoTelefonoDCNP;

    @ViewById
    Button botonAbreSpinnerDCNP;

    @ViewById
    Button botonDictarObservacionesDCNP;

    @ViewById(id.fechaEntregaDCNP)
    DatePicker fechaView;

    @ViewById
    EditText observacionesDCNP;

    @ViewById(id.checkFijarObservacionesDCNP)
    CheckBox fijarObservaciones;

    @ViewById(id.spinnerInvisibleClienteDCNP)
    Spinner spinnerCliente;

    @ViewById(id.clienteDCNP)
    AutoCompleteTextView clienteView;

    @ViewById(id.textoInfoDCNP)
    TextView textoinfo;

    @ViewById
    RelativeLayout listaCheckBoxObservacionesDCNP;

    //Almacena la informacion de telefono
    private String telefono;

    //Almacena los datos del pedido recividos al invocar al subdialogo
    private DatosPedido datosPedidoOriginal;
    //Almacena los clientes que hay en la BD y que por tanto puede indicar el usuario
    private ArrayList<String> clienteArrayList;

    //Almacena en cada posici�n un array de String de 3 elementos, guardaremos en la posicion 0 el id del cliente, en la 1 el nombre del cliente
    //en la posicion 2 las observaciones por defecto y en la posicion 3 el telefono del cliente, el orden corresponde al de clienteArrayList, asi
    //cuando el usuario selecciona un cliente del ArrayList, tenemos sus datos y nos evitamos una consulta a la BD
    private List<String[]> listaDatosClientes;
    //Indica si se ha iniciado el evento del spinner, para no inicializarlo 2 veces
    private boolean eventoSpinnerOnSelectedInicializado;

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
        TimeZone.setDefault(TimeZone.getTimeZone("GMT+1"));
        calendarFechaSistema = (GregorianCalendar) GregorianCalendar.getInstance();

        ArrayAdapter<String> adapter;
        boolean hayInfoTelefono = false;
        //Inicializamos los miembros
        listaDatosClientes = new ArrayList<>();


        //Obtenemos la fecha de maniana, obtenemos la fecha de maniana para inicializar la fecha de entrega y pedido,
        //ya que un pedido se solicita con fecha del dia siguiente a cuando se crea en la tablet
        final GregorianCalendar calendarFechaManiana = (GregorianCalendar) calendarFechaSistema.clone();
        calendarFechaManiana.add(GregorianCalendar.DAY_OF_MONTH, 1);
        calendarFechaManiana.add(GregorianCalendar.MONTH, 1);

        //Inicializarmos los arrayList a utlizar en el ArrayAdapter
        inicializaDatosClientesBD();

        //Si aun no tenemos datos de pedido nos creamos uno con valores por defecto
        if (null == datosPedido) {
            //Se obtienen los datos para el TextView AutoComplete y Spinner
            adapter = new ArrayAdapter<>(getActivity(), layout.list_item, clienteArrayList);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerCliente.setAdapter(adapter);

            adapter = new ArrayAdapter<>(getActivity(), layout.list_item, clienteArrayList);
            clienteView.setAdapter(adapter);
            textoinfo.setText(MENSAJE_TITULO_DIALOGO_NUEVO_PEDIDO);
            int idPedidoTmp = Long.valueOf(new Date().getTime()).intValue();

            //Para controlar que el idPedido sea un numero entero positivo
            if (0 > idPedidoTmp) {
                idPedidoTmp = idPedidoTmp * -1;
            }
            datosPedido = new DatosPedido(idPedidoTmp,
                    0, "",
                    calendarFechaManiana.get(Calendar.DATE),
                    calendarFechaManiana.get(Calendar.MONTH),
                    calendarFechaManiana.get(Calendar.YEAR),
                    calendarFechaManiana.get(Calendar.DATE),
                    calendarFechaManiana.get(Calendar.MONTH),
                    calendarFechaManiana.get(Calendar.YEAR),
                    0f, "", false, 0, false, false);

        } else {
            textoinfo.setText(MENSAJE_TITULO_DIALOGO_MODIFICA_PEDIDO);

            //Obtenemos de los datos del cliente inicializados de la BD las observaciones por defecto
            final String[] datosCliente = dameDatosClientePorId(Integer.toString(datosPedido.getIdCliente()));
            observacionesDefecto = (null != datosCliente) ? datosCliente[2] : null;
            telefono = (null != datosCliente) ? datosCliente[3] : null;
            hayInfoTelefono = true;

            //Se deshabilita el widget que recoge el cliente ya que este dato no se puede modificar
            clienteView.setEnabled(false);
            botonAbreSpinnerDCNP.setEnabled(false);
            botonAbreSpinnerDCNP.setTextColor(getResources().getColor(color.colorTextoBotonDeshabilitadoTablaP));
        }

        //Tenemos que crear una nueva instancia del objeto, ya que sino se referencia al mismo objeto
        datosPedidoOriginal = new DatosPedido(
                datosPedido.getIdPedido(), datosPedido.getIdCliente(),
                datosPedido.getCliente(), datosPedido.getDiaFechaPedido(),
                datosPedido.getMesFechaPedido(), datosPedido.getAnioFechaPedido(),
                datosPedido.getDiaFechaEntrega(), datosPedido.getMesFechaEntrega(),
                datosPedido.getAnioFechaEntrega(), datosPedido.getPrecioTotal(),
                datosPedido.getObservaciones(), datosPedido.isFijarObservaciones(),
                datosPedido.getDescuentoEspecial(), datosPedido.isHayDatosPedidoSinGuardar(),
                datosPedido.isHayLineasPedido());

        //Actualizamos los datos del pedido en la pantalla
        clienteView.setText(datosPedido.getCliente());
        fechaView.init(datosPedido.getAnioFechaEntrega(), datosPedido.getMesFechaEntrega() - 1, datosPedido.getDiaFechaEntrega(), new OnDateChangedListener() {
            @Override
            public void onDateChanged(final DatePicker view, final int year, final int monthOfYear, final int dayOfMonth) {
                // No hacemos nada
            }
        });
        observacionesDCNP.setText(datosPedido.getObservaciones());
        fijarObservaciones.setChecked(datosPedido.isFijarObservaciones());

        //Creamos los checkBox de las observaciones
        creaCheckBoxObservacionesBD();

        if (!hayInfoTelefono) {
            botonInfoTelefonoDCNP.setEnabled(false);
        }
    }

    /**
     * Cuando tengamos un cliente valido actualizamos las observaciones por defecto
     *
     * @see android.text.TextWatcher#afterTextChanged(Editable)
     */

    @AfterTextChange(id.clienteDCNP)
    void afterTextChanged(final Editable s) {
        final String[] datosCliente = dameDatosCliente(clienteView.getText().toString().trim());

        if (null != datosCliente) {
            observacionesDCNP.setText(datosCliente[2]);
            observacionesDefecto = datosCliente[2];
            telefono = datosCliente[3];
            botonInfoTelefonoDCNP.setEnabled(true);
            //Al poner las observaciones por defecto, no tiene sentido que este chequeado el fijarlas
            fijarObservaciones.setChecked(false);
        } else {
            botonInfoTelefonoDCNP.setEnabled(false);
        }
    }

    @Click(id.botonDictarObservacionesDCNP)
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

    @Click(id.botonIncluirObservacionesDCNP)
    void incluirObservaciones() {
        //Tenemos que añadir en las observaciones, los checkbox que esten seleccionados
        boolean seHanIncluido = false;

        //Recorremos todos los checkBox y a�adimos el que este chequeado
        for (int i = 0; i < listaCheckBoxObservacionesDCNP.getChildCount(); i++) {
            final CheckBox observacionLista = (CheckBox) listaCheckBoxObservacionesDCNP.getChildAt(i);

            if (observacionLista.isChecked()) {
                seHanIncluido = true;

                String observaciones = observacionesDCNP.getText().toString();
                String observacion = observacionLista.getText().toString();

                //Añadimos el "." al final de la observacion sino viene, para que quede mas claro.
                observacion += (!observacion.endsWith(".")) ? "." : "";
                observaciones += (!observaciones.isEmpty() ? " " : " ") + observacion;
                //Actualizamos el widget
                observacionesDCNP.setText(observaciones);
            }

            //Deschequeamos el CheckBox, pues ya hemos añadido la linea de comentario
            ((CheckBox) listaCheckBoxObservacionesDCNP.getChildAt(i)).setChecked(false);
        }

        if (seHanIncluido) {
            Toast.makeText(getActivity().getBaseContext(), MENSAJE_DATOS_INCLUIR_SELECCIONES, Toast.LENGTH_SHORT).show();
        }
    }


    @Click(id.botonQuitarObservacionesDCNP)
    void quitarObservaciones() {

        //Actualizamos el widget con las observaciones por defecto si no estan ya puestas
        if (!observacionesDefecto.trim().equals(observacionesDCNP.getText().toString().trim())) {
            //Actualizamos el widget con las observaciones por defecto
            observacionesDCNP.setText(observacionesDefecto);
            Toast.makeText(getActivity().getBaseContext(), MENSAJE_DATOS_QUITAR_SELECCIONES, Toast.LENGTH_SHORT).show();
        }
        //Al poner las observaciones por defecto, no tiene sentido que este chequeado el fijarlas
        fijarObservaciones.setChecked(false);

    }

    @Click(id.aceptarBotonDialogoDCNP)
    void aceptaDialogo() {
        final String[] datosCliente;

        //Segun sea una modificacion de pedido o creacion de nuevo pedido, las comprobaciones seran distintas
        if (clienteView.isEnabled()) {
            //ESTAMOS EN CREACION DE NUEVO PEDIDO

            //Comprobamos si el cliente seleccionado es valido
            if (esClienteValido(clienteView.getText().toString())) {
                //Comprobamos que la fecha de entrega es correcta
                if (esFechaEntregaCorrecta(fechaView)) {
                    //Actualizamos los datos solicitados al usuario en la pantalla
                    datosCliente = dameDatosCliente(clienteView.getText().toString().trim());

                    if (null != datosCliente) {
                        datosPedido.setIdCliente(Integer.parseInt(datosCliente[0]));
                        datosPedido.setCliente(datosCliente[1]);
                        datosPedido.setDiaFechaPedido(fechaView.getDayOfMonth());
                        datosPedido.setMesFechaPedido(fechaView.getMonth() + 1);
                        datosPedido.setAnioFechaPedido(fechaView.getYear());
                        datosPedido.setDiaFechaEntrega(fechaView.getDayOfMonth());
                        datosPedido.setMesFechaEntrega(fechaView.getMonth() + 1);
                        datosPedido.setAnioFechaEntrega(fechaView.getYear());
                        datosPedido.setObservaciones(observacionesDCNP.getText().toString().trim());
                        datosPedido.setFijarObservaciones(fijarObservaciones.isChecked());

                        //Comprobamos si hay algun cambio en los datos del pedido
                        if (!datosPedido.equals(datosPedidoOriginal)) {
                            datosPedido.setHayDatosPedidoSinGuardar(true);
                        }

                        this.returnDatosDCNP();
                    } else {
                        toastMensajeError(AVISO_DATOS_DCNP_CLIENTE_NO_VALIDO);
                    }
                } else {
                    toastMensajeError(AVISO_DATOS_DCNP_FECHA_NO_VALIDA);
                }
            } else {
                toastMensajeError(AVISO_DATOS_DCNP_CLIENTE_NO_VALIDO);
            }
        } else {
            //ESTAMOS EN MODIFICACION DE PEDIDO

            //Comprobamos que la fecha de entrega es correcta
            if (esFechaEntregaCorrecta(fechaView)) {
                datosPedido.setDiaFechaPedido(fechaView.getDayOfMonth());
                datosPedido.setMesFechaPedido(fechaView.getMonth() + 1);
                datosPedido.setAnioFechaPedido(fechaView.getYear());
                datosPedido.setDiaFechaEntrega(fechaView.getDayOfMonth());
                datosPedido.setMesFechaEntrega(fechaView.getMonth() + 1);
                datosPedido.setAnioFechaEntrega(fechaView.getYear());
                datosPedido.setObservaciones(observacionesDCNP.getText().toString().trim());
                datosPedido.setFijarObservaciones(fijarObservaciones.isChecked());

                //Comprobamos si hay algun cambio en los datos del pedido
                if (!datosPedido.equals(datosPedidoOriginal)) {
                    datosPedido.setHayDatosPedidoSinGuardar(true);
                }

                returnDatosDCNP();
            } else {
                toastMensajeError(AVISO_DATOS_DCNP_FECHA_NO_VALIDA);
            }
        }
    }


    @Click(id.botonAbreSpinnerDCNP)
    void abreSpinner() {
        //El evento del spinner solo lo inicializamos una sola vez
        if (!eventoSpinnerOnSelectedInicializado) {
            eventoSpinnerOnSelectedInicializado = true;

            spinnerCliente.setOnItemSelectedListener(new OnItemSelectedListener() {
                @Override
                public void onItemSelected(final AdapterView<?> parent, final View view, final int pos, final long id) {
                    clienteView.setText(listaDatosClientes.get(pos)[1]);
                    clienteView.dismissDropDown();
                    observacionesDCNP.setText(listaDatosClientes.get(pos)[2]);
                    observacionesDefecto = listaDatosClientes.get(pos)[2];
                    telefono = listaDatosClientes.get(pos)[3];
                    botonInfoTelefonoDCNP.setEnabled(true);

                    //Al poner las observaciones por defecto, no tiene sentido que este chequeado el fijarlas
                    fijarObservaciones.setChecked(false);
                }

                @Override
                public void onNothingSelected(final AdapterView<?> arg0) {
                }
            });
        }
        spinnerCliente.performClick();
    }

    @Click(id.botonFechaHoyDCNP)
    void fechaDeHoy() {

        fechaView.init(calendarFechaSistema.get(Calendar.YEAR), calendarFechaSistema.get(Calendar.MONTH), calendarFechaSistema.get(Calendar.DATE), new OnDateChangedListener() {
            @Override
            public void onDateChanged(final DatePicker view, final int year, final int monthOfYear, final int dayOfMonth) {
                // No hacemos nada
            }
        });
    }

    @Click(id.botonFechaLunesDCNP)
    void fechaLunes() {

        final GregorianCalendar calendarFechaLunes = (GregorianCalendar) calendarFechaSistema.clone();
        final int diaSemana = calendarFechaSistema.get(GregorianCalendar.DAY_OF_WEEK);
        if (GregorianCalendar.MONDAY != diaSemana) {
            //Calcula cuantos dias se debe sumar al dia de la semana actual para llegar al Lunes, el 2 es la diferencia entre el Sabado y el Lunes
            calendarFechaLunes.add(GregorianCalendar.DAY_OF_YEAR, ((GregorianCalendar.SATURDAY - diaSemana) + 2) % 7);
        } else {
            //La fecha de hoy es Lunes y queremos el proximo lunes el de la semana que viene
            calendarFechaLunes.add(GregorianCalendar.DAY_OF_YEAR, 7);
        }
        calendarFechaLunes.getTime();
        fechaView.init(calendarFechaLunes.get(Calendar.YEAR), calendarFechaLunes.get(Calendar.MONTH), calendarFechaLunes.get(Calendar.DATE), new OnDateChangedListener() {
            @Override
            public void onDateChanged(final DatePicker view, final int year, final int monthOfYear, final int dayOfMonth) {
                // No hacemos nada
            }
        });
    }


    private void muestraInfoTelefono() {
        final Builder popup = new Builder(getActivity());
        popup.setTitle(TITULO_ALERT_ERROR_NO_SINCRONIZAR_PEDIDOS_PENDIENTES);
        popup.setMessage(MENSAJE_ALERT_ERROR_NO_SINCRONIZAR_PEDIDOS_PENDIENTES);
        popup.setPositiveButton(aceptar_M, null);
        popup.show();
    }

    /**
     * Muestra un mensaje al usuario con la informacion del telefono del cliente seleccionado
     */

    @Click(id.botonInfoTelefonoDCNP)
    void subdialogoMensajeInfoTelefono() {

        final DatosDialogoMensaje datosMensaje = new DatosDialogoMensaje("intraza.com.interfaz.DialogoDatosPedidos_",
                TITULO_INFO_TELEFONO, INFORMACION_INFO_TELEFONO + telefono);
        DialogoMensaje_.intent(this).extra("DATOS_MENSAJE", datosMensaje)
                .startForResult(DIALOGO_MENSAJE_INFO_TELEFONO);
    }

    /**
     * Crea la lista de chekBox, obteniendo las datos de la lista de la BD
     */
    private void creaCheckBoxObservacionesBD() {

        int idViewAnterior = listaCheckBoxObservacionesDCNP.getId();
        final AdaptadorBD db = new AdaptadorBD(getActivity());
        //Consultamos todas las observaciones y creamos con  cada una de ellas un checkBox
        db.abrir();
        final Cursor cursorObservaciones = db.obtenerTodasLasObservacionesPrepedido();

        if (cursorObservaciones.moveToFirst()) {
            do {
                CheckBox checkBox = new CheckBox(getActivity());
                //Hay que usar el RelativeLayout del padre que contiene al CheckBox
                LayoutParams paramsRelativeLayout = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                paramsRelativeLayout.addRule(RelativeLayout.BELOW, idViewAnterior);
                idViewAnterior = cursorObservaciones.getInt(TablaObservacion.POS_KEY_CAMPO_ID_OBSERVACION);
                checkBox.setId(idViewAnterior);
                checkBox.setLayoutParams(paramsRelativeLayout);
                checkBox.setText(cursorObservaciones.getString(TablaObservacion.POS_CAMPO_DESCRIPCION));

                listaCheckBoxObservacionesDCNP.addView(checkBox);

            } while (cursorObservaciones.moveToNext());
        }

        db.cerrar();
    }

    /**
     * Dado un nombre de cliente obtiene los datos guardados en el vector de datos de clientes.
     *
     * @param nombreCliente String
     * @return array de String con los datos del cliente (id, nombre, observaciones)
     */
    private String[] dameDatosCliente(final String nombreCliente) {

        //Recorremos los datos de los clientes para obtener el cliente buscado
        for (final String[] clientes : listaDatosClientes) {
            if (clientes[1].equals(nombreCliente)) {
                return clientes;
            }
        }
        return null;
    }

    /**
     * Dado un id de cliente obtiene los datos guardados en el vector de datos de clientes.
     *
     * @param idCliente String
     * @return array de String con los datos del cliente (id, nombre, observaciones)
     */
    private String[] dameDatosClientePorId(final String idCliente) {

        //Recorremos los datos de los clientes para obtener el cliente buscado
        for (final String[] clientes : listaDatosClientes) {
            if (clientes[0].equals(idCliente)) {
                return clientes;
            }
        }
        return null;
    }

    /**
     * Devuelve un objeto ArrayList<String> con los clientes de la BD.
     */
    private void inicializaDatosClientesBD() {

        final AdaptadorBD db = new AdaptadorBD(getActivity());
        final Cursor cursorClientes;
        String[] datosCliente = {"", "", "", ""};
        clienteArrayList = new ArrayList<>();
        clienteArrayList.add("");
        listaDatosClientes.add(datosCliente);

        //Consultamos todos los clientes y metemos cada uno de ellos en el ArrayList, tambien guardamos sus datos pues los necesitaremos posteriormente
        db.abrir();
        cursorClientes = db.obtenerTodosLosClientes();

        if (cursorClientes.moveToFirst()) {
            do {
                clienteArrayList.add(cursorClientes.getString(TablaCliente.POS_CAMPO_NOMBRE_CLIENTE));
                datosCliente = new String[4];
                datosCliente[0] = cursorClientes.getString(TablaCliente.POS_KEY_CAMPO_ID_CLIENTE).trim();
                datosCliente[1] = cursorClientes.getString(TablaCliente.POS_CAMPO_NOMBRE_CLIENTE).trim();
                datosCliente[2] = cursorClientes.getString(TablaCliente.POS_CAMPO_OBSERVACIONES_PREPEDIDO).trim();
                datosCliente[3] = cursorClientes.getString(TablaCliente.POS_CAMPO_TELEFONO).trim();
                listaDatosClientes.add(datosCliente);
            } while (cursorClientes.moveToNext());
        }
        db.cerrar();
    }

    /**
     * Comprueba si el cliente pasado como parametro, es uno de los clientes de la lista.
     *
     * @param cliente a comprobar si pertenece a la lista.
     * @return true si el cliente pertenece a la lista de clientes o false en caso contrario.
     */
    private boolean esClienteValido(final String cliente) {
        return (!"".equals(cliente.trim())) && clienteArrayList.contains(cliente);
    }

    /**
     * Comprueba si la fecha de entrega es posterior a la fecha del sistema.
     *
     * @param fechaEntrega fecha de entrega.
     * @return true si la fecha de entrega es posterior a la fecha del sistema, false, en caso contrario.
     */
    private boolean esFechaEntregaCorrecta(final DatePicker fechaEntrega) {

        final GregorianCalendar fechaSistema = calendarFechaSistema;
        final GregorianCalendar fecha = new GregorianCalendar(fechaEntrega.getYear(), fechaEntrega.getMonth(), fechaEntrega.getDayOfMonth());
        fechaSistema.set(Calendar.HOUR_OF_DAY, 0);
        fecha.set(Calendar.HOUR_OF_DAY, 1);
        return !(0 > fecha.compareTo(fechaSistema));
    }

    /**
     * - Devuelve a la activity que lo solicito los datos de consulta para el nuevo pedido, introducidos por el usuario.
     * - Indica que le ejecución de la activity ha sido OK.
     * - Finaliza la activity.
     */
    private void returnDatosDCNP() {
        Intent intent;
        if (getActivity() instanceof InTrazaActivity_) {
            intent = InTrazaActivity_.intent(this).extra("DATOS_PEDIDO", datosPedido).get();
            ((InTrazaActivity_) getActivity()).onActivityResult(DIALOGO_PIDE_DATOS_NUEVO_PEDIDO, Activity.RESULT_OK, intent);
        } else if (getActivity() instanceof PantallaRutero_) {
            intent = PantallaRutero_.intent(this).extra("DATOS_PEDIDO", datosPedido).get();
            ((PantallaRutero_) getActivity()).onActivityResult(DIALOGO_PIDE_DATOS_NUEVO_PEDIDO, Activity.RESULT_OK, intent);
        }
        dismiss();
    }

    /**
     * Termina la activity
     */
    @Click(id.cancelarBotonDialogoDCNP)
    void cancelarDialogo() {
        dismiss();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1: {
                if (resultCode == Activity.RESULT_OK && null != data) {
                    ArrayList<String> text = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    observacionesDCNP.append(text.get(0));
                }
                break;
            }
        }
    }
}