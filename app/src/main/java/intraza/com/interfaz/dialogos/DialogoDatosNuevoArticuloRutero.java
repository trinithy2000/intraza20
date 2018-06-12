package intraza.com.interfaz.dialogos;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.util.DisplayMetrics;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;

import org.androidannotations.annotations.AfterTextChange;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.ItemSelect;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.StringRes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import intraza.com.interfaz.datosDB.TablaArticulo;
import intraza.com.interfaz.datosDB.TablaRutero;
import intraza.com.Configuracion;
import intraza.com.Constantes;
import intraza.com.interfaz.datos.DatosLineaPedido;
import intraza.com.interfaz.datosDB.AdaptadorBD;

import intraza.com.R.layout;
import intraza.com.R.id;
import intraza.com.R.style;
import intraza.com.interfaz.pantallas.PantallaRutero_;

/**
 * Activity que solicita al usuario los datos del nuevo pedido.
 */
@SuppressWarnings({"CyclicClassDependency", "LocalCanBeFinal"})
@EFragment(layout.dialogo_datos_nuevo_articulo_rutero)
public class DialogoDatosNuevoArticuloRutero extends DialogPadreFragment {

    @FragmentArg("ARRAY_ARTICULOS_EN_RUTERO")
    String[] articulosYaEnRutero;

    @FragmentArg("ID_CLIENTE")
    int cliente;

    @FragmentArg("DIALOGO_DATOS_NUEVO_PEDIDO_RETORNO")
    int DIALOGO_DATOS_NUEVO_PEDIDO_RETORNO;

    @StringRes
    String TITULO_SIN_ARTICULOS_NUEVOS;

    @StringRes
    String INFORMACION_SIN_ARTICULOS_NUEVOS;

    @StringRes
    String AVISO_DATOS_DNAR_ARTICULO_NO_VALIDO;

    @ViewById
    AutoCompleteTextView ReferenciaDNAR;

    @ViewById
    AutoCompleteTextView articuloDNAR;

    @ViewById
    CheckBox checkFijarArticuloDNAR;

    @ViewById
    Spinner spinnerInvisibleClienteDNAR;

    @ViewById
    TextView tituloDM;

    @ViewById
    TextView informacionDM;

    //Almacena los datos de la nueva linea de pedido
    private DatosLineaPedido datosLineaPedido;
    //Almacena los articulo y referencias que hay en la BD y que por tanto puede indicar el usuario
    private ArrayList<String> articuloArrayList;
    //Almacena en cada posicion un array de String de 2 elementos, guardaremos en la posicion 0 el codigo del articulo y en la 1 el nombre del articulo
    //el orden corresponde al de  los ArrayList, asi cuando el usuario selecciona un elemento del ArrayList, tenemos sus datos y nos evitamos una
    //consulta a la BD
    private List<String[]> listaDatosArticulos;
    //Almacena los codigos de los articulos que ya estan en la pantalla del rutero del cliente y que por tanto no se deben mostar al usuario
    //para que los vuelva a incluir
//Para evitar que se cree un bucle recursivo
    private boolean estaDeshabilitadoEventoTextChanged;

    @Override
    public void onStart() {
        super.onStart();

        DisplayMetrics displaymetrics = new DisplayMetrics();
        this.getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int dWidth = displaymetrics.widthPixels;
        Dialog d = getDialog();
        if (d != null) {
            int width = (int) (dWidth / 1.3f);
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;
            d.getWindow().setLayout(width, height);
        }
    }


    @AfterViews
    void init() {
        setStyle(STYLE_NO_TITLE, style.MY_DIALOG);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        ArrayAdapter<String> adapter;

        //Obtenemos los datos de los articulos que se pueden añadir al rutero del cliente
        listaDatosArticulos = dameArticulosParaRuteroBD(cliente);

        //Segun haya articulos o no, mostramos un mensaje o la pantalla de alta del nuevo articulo
        if (!listaDatosArticulos.isEmpty()) {
            //Se obtienen los datos para el TextView AutoComplete y Spinner
            adapter = new ArrayAdapter<>(getActivity(), layout.list_item, dameArticulos(listaDatosArticulos));
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            spinnerInvisibleClienteDNAR.setAdapter(adapter);

            adapter = new ArrayAdapter<>(getActivity(), layout.list_item, dameArticulos(listaDatosArticulos));
            articuloDNAR.setAdapter(adapter);

            adapter = new ArrayAdapter<>(getActivity(), layout.list_item, dameReferencias(listaDatosArticulos));
            ReferenciaDNAR.setAdapter(adapter);
            //Por defecto el checkBox de fijar articulo, se chequea
            checkFijarArticuloDNAR.setChecked(true);

        } else {
            getActivity().setContentView(layout.dialogo_mensaje);
            //Se actualizan los widget del layout con los datos del mensaje
            tituloDM.setText(TITULO_SIN_ARTICULOS_NUEVOS);
            informacionDM.setText(INFORMACION_SIN_ARTICULOS_NUEVOS);
        }
    }

    @ItemSelect(id.spinnerInvisibleClienteDNAR)
    public void myListItemSelected(final boolean selected, final int selectedItem) {
        if (selected) {
            ReferenciaDNAR.setText(listaDatosArticulos.get(selectedItem)[0]);
            ReferenciaDNAR.dismissDropDown();
            articuloDNAR.setText(listaDatosArticulos.get(selectedItem)[1]);
            articuloDNAR.dismissDropDown();
        }
    }


    @AfterTextChange(id.ReferenciaDNAR)
    void afterNombreTextChanged() {
        final String nombreArticulo;

        if (!estaDeshabilitadoEventoTextChanged) {
            estaDeshabilitadoEventoTextChanged = true;
            nombreArticulo = dameNombreArticuloSegunReferencia(ReferenciaDNAR.getText().toString().trim());
            if (null != nombreArticulo) {
                articuloDNAR.setText(nombreArticulo);
                articuloDNAR.dismissDropDown();
            }
            estaDeshabilitadoEventoTextChanged = false;
        }
    }

    @AfterTextChange(id.articuloDNAR)
    void afterReferenciaTextChanged() {
        final String referenciaArticulo;

        if (!estaDeshabilitadoEventoTextChanged) {
            estaDeshabilitadoEventoTextChanged = true;
            referenciaArticulo = dameReferenciaArticuloSegunNombre(articuloDNAR.getText().toString().trim());
            if (null != referenciaArticulo) {
                ReferenciaDNAR.setText(referenciaArticulo);
                ReferenciaDNAR.dismissDropDown();
            }
            estaDeshabilitadoEventoTextChanged = false;
        }
    }


    @Click(id.aceptarBotonDialogoDNAR)
    void aceptarDialogoDNAR() {
        final float tarifaDefecto;
        float tarifaCliente;
        String nombreArticulo;
        int idArticulo;

        //Comprobamos si el articulo seleccionado es valido
        if (esArticuloValido(articuloDNAR.getText().toString())) {
            tarifaDefecto = consultaTarifaDefectoArticuloEnBD(ReferenciaDNAR.getText().toString());
            tarifaCliente = consultaTarifaClienteArticuloEnBD(ReferenciaDNAR.getText().toString(), cliente);
            tarifaCliente = ((0 == Float.compare(-1f, tarifaCliente)) ? tarifaDefecto : tarifaCliente);
            idArticulo = consultaIdArticulporCodigo(ReferenciaDNAR.getText().toString());
            nombreArticulo = articuloDNAR.getText().toString();

            //Si le hemos puesto el sufijo que indica congelado para informar al comercial, se lo quitamos
            if (nombreArticulo.endsWith(Constantes.MARCA_CONGELADO)) {
                nombreArticulo = nombreArticulo.substring(0, nombreArticulo.length() - Constantes.MARCA_CONGELADO.length());
            }

            datosLineaPedido = new DatosLineaPedido(idArticulo, ReferenciaDNAR.getText().toString(), nombreArticulo, consultaMedidaArticuloEnBD(ReferenciaDNAR.getText().toString()),
                    consultaCongeladoArticuloEnBD(ReferenciaDNAR.getText().toString()), Constantes.SIN_FECHA_ANTERIOR_LINEA_PEDIDO,
                    0, -1f, 0, 0f, 0f, -1f, 0, tarifaCliente, tarifaDefecto, consultaFechaCambioTarifaDefectoArticuloEnBD(ReferenciaDNAR.getText().toString()), "",
                    false, false, false, false, false);

            //Chequeamos si hay que fijar el articulo en la BD de intraza, para los futuros ruteros
            if (checkFijarArticuloDNAR.isChecked()) {
                datosLineaPedido.setFijarArticulo(true);
            }
            returnDatosDNAR();
        } else {
            toastMensajeError(AVISO_DATOS_DNAR_ARTICULO_NO_VALIDO);
        }
    }

    @Click(id.botonAbreSpinnerDNAR)
    void performClick() {
        spinnerInvisibleClienteDNAR.performClick();
    }

    /**
     * Dado un codigo de articulo, obtiene su medida para la linea de pedidos
     *
     * @param codArticulo String
     * @return la medida para la linea de pedido.
     */
    private String consultaMedidaArticuloEnBD(final String codArticulo) {
        String medida = Constantes.KILOGRAMOS;
        final AdaptadorBD db = new AdaptadorBD(getActivity());
        final Cursor cursorArticulos;

        db.abrir();
        cursorArticulos = db.obtenerArticulo(codArticulo);
        if (cursorArticulos.moveToFirst() && "0".equals(cursorArticulos.getString(TablaArticulo.POS_CAMPO_ES_KG))) {
            medida = Constantes.UNIDADES;
        }
        db.cerrar();
        return medida;
    }

    /**
     * Dado un codigo de articulo, obtiene si es congelado o no
     *
     * @param codArticulo String
     * @return la medida para la linea de pedido.
     */
    private boolean consultaCongeladoArticuloEnBD(final String codArticulo) {
        final boolean esCongelado;
        final AdaptadorBD db = new AdaptadorBD(getActivity());
        final Cursor cursorArticulos;

        db.abrir();
        cursorArticulos = db.obtenerArticulo(codArticulo);
        esCongelado = cursorArticulos.moveToFirst() && "1".equals(cursorArticulos.getString(TablaArticulo.POS_CAMPO_ES_CONGELADO));
        db.cerrar();

        return esCongelado;
    }

    /**
     * Dado un codigo de articulo, obtiene su tarifa por defecto para la linea de pedidos
     *
     * @param codArticulo String
     * @return La tarifa por defecto
     */
    private float consultaTarifaDefectoArticuloEnBD(final String codArticulo) {
        float tarifaDefecto = 0;
        final AdaptadorBD db = new AdaptadorBD(getActivity());
        final Cursor cursorArticulos;

        db.abrir();
        cursorArticulos = db.obtenerArticulo(codArticulo);
        if (cursorArticulos.moveToFirst()) {
            tarifaDefecto = cursorArticulos.getFloat(TablaArticulo.POS_CAMPO_TARIFA_DEFECTO);
        }
        db.cerrar();

        return tarifaDefecto;
    }

    private int consultaIdArticulporCodigo(final String codArticulo) {
        int id = 0;
        final AdaptadorBD db = new AdaptadorBD(getActivity());
        final Cursor cursorArticulos;

        db.abrir();
        cursorArticulos = db.obtenerArticulo(codArticulo);
        if (cursorArticulos.moveToFirst()) {
            id = cursorArticulos.getInt(TablaArticulo.POS_KEY_CAMPO_ID_ARTICULO);
        }
        db.cerrar();

        return id;
    }

    /**
     * Dado un codigo de articulo y un cliente, obtiene la tarifa de cliente del rutero
     *
     * @param codArticulo String
     * @param idCliente   int
     * @return La tarifa cliente
     */
    private float consultaTarifaClienteArticuloEnBD(final String codArticulo, final int idCliente) {
        float tarifaCliente = -1;
        final AdaptadorBD db = new AdaptadorBD(getActivity());
        final Cursor cursorRutero;

        db.abrir();
        cursorRutero = db.obtenerRutero(codArticulo, idCliente);
        if (cursorRutero.moveToFirst()) {
            tarifaCliente = cursorRutero.getFloat(TablaRutero.POS_CAMPO_TARIFA_CLIENTE);
        }
        db.cerrar();
        return tarifaCliente;
    }

    /**
     * Dado un codigo de articulo, obtiene su fecha de cambio de tarifa por defecto para la linea de pedidos
     *
     * @param codArticulo String
     * @return La fecha de cambio de tarifa por defecto
     */
    private String consultaFechaCambioTarifaDefectoArticuloEnBD(final String codArticulo) {
        String fechaCambioTarifaDefecto = null;
        final AdaptadorBD db = new AdaptadorBD(getActivity());
        final Cursor cursorArticulos;

        db.abrir();
        cursorArticulos = db.obtenerArticulo(codArticulo);
        if (cursorArticulos.moveToFirst()) {
            fechaCambioTarifaDefecto = cursorArticulos.getString(TablaArticulo.POS_CAMPO_FECHA_CAMBIO_TARIFA_DEFECTO);
        }
        db.cerrar();

        return fechaCambioTarifaDefecto;
    }

    /**
     * Obtiene los articulos que no estan en el rutero del cliente y que por tanto pueden ser incluidos en el mismo.
     *
     * @param idCliente int
     * @return Vector de String[] con los datos de los articulos, cada elemento es un array de String de 2 posiciones, en la 0 se guarda el codigo del
     * articulo y en la 1 la descripcion del mismo.
     */
    private List<String[]> dameArticulosParaRuteroBD(final int idCliente) {
        final List<String[]> articulosObtenidos = new ArrayList<>();
        final AdaptadorBD db = new AdaptadorBD(getActivity());
        final Cursor cursorArticulos;
        String[] datosArticulo;

        //Primero lo inicializamos a vacio
        datosArticulo = new String[2];
        datosArticulo[0] = "";
        datosArticulo[1] = "";
        articulosObtenidos.add(datosArticulo);

        db.abrir();

        cursorArticulos = db.obtenerArticulosNoEnRuteroCliente(idCliente);

        if (cursorArticulos.moveToFirst()) {
            do {
                datosArticulo = new String[2];
                datosArticulo[0] = cursorArticulos.getString(TablaArticulo.POS_KEY_CAMPO_CODIGO).trim();
                datosArticulo[1] = cursorArticulos.getString(TablaArticulo.POS_CAMPO_NOMBRE).trim() +
                        ((1 == cursorArticulos.getInt(TablaArticulo.POS_CAMPO_ES_CONGELADO)) ? Constantes.MARCA_CONGELADO : "");
                //Si el articulo no esta en la pantalla de rutero, damos la posibilidad de que sea incluido
                if (!estaArticuloYaEnPantallaRutero(datosArticulo[0])) {
                    articulosObtenidos.add(datosArticulo);
                }
            } while (cursorArticulos.moveToNext());
        }

        db.cerrar();

        return articulosObtenidos;
    }

    /**
     * Comprube si el articulo esta ya en la pantalla de rutero.
     *
     * @param codArticulo String
     * @return true si el articulo ya esta en la pantalla de rutero o false en cualquier otro caso
     */
    private boolean estaArticuloYaEnPantallaRutero(final String codArticulo) {
        boolean esta = false;

        for (final String anArticulosYaEnRutero : articulosYaEnRutero) {
            if (anArticulosYaEnRutero.equals(codArticulo)) {
                esta = true;
                break;
            }
        }
        return esta;
    }

    /**
     * Busca el nombre de un articulo dada su referencia.
     *
     * @param referencia del articulo a buscar
     * @return el nombre del articulo o null si no hay ningun articulo para la referencia dada
     */
    private String dameNombreArticuloSegunReferencia(final String referencia) {

        String nombreArticulo = null;
        if (!"".equals(referencia.trim())) {
            for (String[] lista : listaDatosArticulos)
                if (lista[0].equals(referencia)) {
                    nombreArticulo = lista[1];
                    break;
                }
        }
        return nombreArticulo;
    }

    /**
     * Busca la referencia de un articulo dado su nombre.
     *
     * @param nombre del articulo a buscar
     * @return la referencia del articulo o null si no hay ningun articulo para el nombre dado
     */
    private String dameReferenciaArticuloSegunNombre(final String nombre) {
        String referenciaArticulo = null;

        if (!"".equals(nombre.trim())) {
            for (String[] lista : listaDatosArticulos)
                if (lista[1].equals(nombre)) {
                    referenciaArticulo = lista[0];
                    break;
                }
        }
        return referenciaArticulo;
    }

    /**
     * Devuelve un objeto ArrayList<String> con los articulos, QUE NO ESTAN EN EL RUTERO y que el usuario puede incluir.
     * <p/>
     * Vector con los nombres de los articulos, cada elemento es un array de String en la posicion 0 se almacena el codigo del articulo y en la 1 la descripcion
     * del mismo
     *
     * @return ArrayList con la lista de articulos que el usuario puede a�adir
     */
    private ArrayList<String> dameArticulos(final List<String[]> articulos) {

        articuloArrayList = new ArrayList<>();
        for (String[] lista : articulos) {
            articuloArrayList.add(lista[1]);
        }
        //Ordenamos el array list
        Collections.sort(articuloArrayList);
        return articuloArrayList;
    }

    /**
     * Devuelve un objeto ArrayList<String> con las referencias de los articulos, QUE NO ESTAN EN EL RUTERO y que el usuario puede incluir.
     * <p/>
     * Vector con los datos de los articulos, cada elemento es un array de String en la posicion 0 se almacena el codigo del articulo y en la 1 la descripcion
     * del mismo
     *
     * @return ArrayList con la referencia de los articulos que el usuario puede a�adir
     */
    private ArrayList<String> dameReferencias(final List<String[]> articulos) {

        final ArrayList<String> referenciasArrayList = new ArrayList<>();
        for (String[] lista : articulos) {
            referenciasArrayList.add(lista[0]);
        }
        //Ordenamos el array list
        Collections.sort(referenciasArrayList);
        return referenciasArrayList;
    }

    /**
     * Comprueba si el articulo pasado como parametro, es uno de los articulos de la lista.
     *
     * @param articulo a comprobar si pertenece a la lista.
     * @return true si el articulo pertenece a la lista de articulos o false en caso contrario.
     */
    private boolean esArticuloValido(final String articulo) {

        boolean resultado = false;
        //Comprobamos si el articulo es valido siempre que contenga una cadena, sino sera invalido, este caso se puede dar si el usuario ha metido
        //una referencia de articulo no valida
        if (!articulo.trim().isEmpty()) {
            resultado = articuloArrayList.contains(articulo);
        }
        return resultado;
    }

    /**
     * - Devuelve a la activity que lo solicito los datos de la nueva linea de pedido, introducidos por el usuario.
     * - Indica que le ejecuci�n de la activity ha sido OK.
     * - Finaliza la activity.
     */
    private void returnDatosDNAR() {
        Intent intent = PantallaRutero_.intent(this).extra("DATOS_LINEA_PEDIDO", datosLineaPedido).get();
        ((PantallaRutero_) getActivity()).onActivityResult(DIALOGO_DATOS_NUEVO_PEDIDO_RETORNO, Activity.RESULT_OK, intent);
        dismiss();
    }

    /**
     * Termina la activity
     */
    @Click({id.cancelarBotonDialogoDNAR, id.aceptarDM})
    void cancelarDialogo() {
        dismiss();
    }

}