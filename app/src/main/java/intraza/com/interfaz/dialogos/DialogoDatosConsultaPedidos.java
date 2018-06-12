package intraza.com.interfaz.dialogos;


import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.database.Cursor;
import android.text.Editable;
import android.util.DisplayMetrics;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;

import org.androidannotations.annotations.AfterTextChange;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.ItemSelect;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.StringRes;

import java.util.ArrayList;
import java.util.List;

import intraza.com.InTrazaActivity_;
import intraza.com.interfaz.datos.DatosConsultaPedidos;
import intraza.com.interfaz.datosDB.AdaptadorBD;
import intraza.com.interfaz.datosDB.TablaCliente;

import intraza.com.R.layout;
import intraza.com.R.id;
import intraza.com.interfaz.pantallas.PantallaRutero_;

/**
 * Activity que solicita al usuario los datos para consultar los pedidos.
 *
 * @author JLZS
 */
@SuppressWarnings("CyclicClassDependency")
@EFragment(layout.dialogo_datos_consulta_pedidos)
public class DialogoDatosConsultaPedidos extends DialogPadreFragment {

    @StringRes
    String SPINNER_NINGUNO;

    @StringRes
    String SPINNER_TODO;

    @StringRes
    String AVISO_DATOS_DCP_CLIENTE_NO_VALIDO;

    @ViewById(id.clienteDCP)
    AutoCompleteTextView clienteView;

    @ViewById(id.spinnerInvisibleClienteDCP)
    Spinner spinnerCliente;

    @FragmentArg
    int DIALOGO_RETORNA_RESULTADO;

    //Almacena la posicion del elemento de listaDatosClientes que esta seleccionado, 0 es TODOS
    private int posClienteSeleccionado;
    //Almacena los datos de la consulta
    private DatosConsultaPedidos datosConsultaPedidos;
    //Almacena los clientes que hay en la BD y que por tanto puede indicar el usuario
    private ArrayList<String> clienteArrayList;

    //Almacena en cada posicion un array de String de 2 elementos, guardaremos en la posicion 0 el id del cliente, en la 1 el nombre del cliente,
    //el orden corresponde al de clienteArrayList, asi cuando el usuario selecciona un cliente del ArrayList, tenemos sus datos y nos evitamos
    //una consulta a la BD
    private List<String[]> listaDatosClientes;

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
        setStyle(DialogFragment.STYLE_NO_TITLE, intraza.com.R.style.MY_DIALOG);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        ArrayAdapter<String> adapter;
        //Inicializamos los miembros
        listaDatosClientes = new ArrayList<>();
        //Inicializarmos los arrayList a utlizar en el ArrayAdapter
        inicializaDatosClientesBD();
        //Se obtienen los datos para los TextView AutoComplete y Spinner
        adapter = new ArrayAdapter<>(getActivity(), layout.list_item, clienteArrayList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCliente.setAdapter(adapter);
        adapter = new ArrayAdapter<>(getActivity(), layout.list_item, clienteArrayList);
        clienteView.setAdapter(adapter);
    }

    @ItemSelect(id.spinnerInvisibleClienteDCP)
    void listItemSelected(final boolean somethingSelected, final String item) {
        clienteView.setText(somethingSelected ? (item.equals(SPINNER_NINGUNO) ? "" : item) : "");
    }

    @AfterTextChange(id.clienteDCP)
    public void afterTextChanged(final Editable s) {
        posClienteSeleccionado = damePosDatosCliente();
    }


    @Click(id.consultarBotonDialogoDCP)
    void consultar() {

        final int idPedido = 0;
        int idCliente = 0;
        String nombreCliente = clienteView.getText().toString();

        if (nombreCliente != null) {
            //Comprobamos si el cliente seleccionado es valido
            if ("".equals(nombreCliente.trim()) || !isClienteValido(nombreCliente.trim()))
                toastMensajeError(AVISO_DATOS_DCP_CLIENTE_NO_VALIDO);
            else {
                if (null != listaDatosClientes.get(posClienteSeleccionado)) {
                    idCliente = Integer.parseInt(listaDatosClientes.get(posClienteSeleccionado)[0]);
                }
                nombreCliente = clienteView.getText().toString().trim();
                datosConsultaPedidos = new DatosConsultaPedidos(idPedido, idCliente, nombreCliente);
                returnDatosDCP();
            }
        }
    }

    /**
     * Dado un nombre de cliente obtiene la posicion que ocupan sus datos en el vector de datos de clientes, esta posicion corresponde con la posici�n
     * en el spinner
     *
     * @return array de String con los datos del cliente (id, nombre, observaciones)
     */
    private int damePosDatosCliente() {

        int posDatosCliente = 0;
        //Recorremos los datos de los clientes para obtener el cliente buscado
        for (int i = 0; i < listaDatosClientes.size(); i++) {
            if (listaDatosClientes.get(i)[1].equals(clienteView.getText().toString().trim())) {
                posDatosCliente = i;
                break;
            }
        }
        return posDatosCliente;
    }

    /**
     * Devuelve un ArrayList<String> con los clientes.
     */
    private void inicializaDatosClientesBD() {
        clienteArrayList = new ArrayList<>();
        final AdaptadorBD db = new AdaptadorBD(getActivity());
        final Cursor cursorClientes;
        String[] datosCliente;

        //Consultamos todos los clientes y metemos cada uno de ellos en el ArrayList, tambien guardamos sus datos pues los necesitaremos posteriormente
        db.abrir();

        cursorClientes = db.obtenerTodosLosClientes();

        if (cursorClientes.moveToFirst()) {
            // Primero incluimos las opciones de "ninguno" y "TODOS", por defecto se seleccionan todos los clientes
            clienteArrayList.add(SPINNER_TODO);
            datosCliente = new String[2];
            datosCliente[0] = "11111"; //Este valor nos da igual nunca se va ha utilizar
            datosCliente[1] = SPINNER_TODO;
            listaDatosClientes.add(datosCliente);

            do {
                clienteArrayList.add(cursorClientes.getString(TablaCliente.POS_CAMPO_NOMBRE_CLIENTE).trim());
                datosCliente = new String[2];
                datosCliente[0] = cursorClientes.getString(TablaCliente.POS_KEY_CAMPO_ID_CLIENTE).trim();
                datosCliente[1] = cursorClientes.getString(TablaCliente.POS_CAMPO_NOMBRE_CLIENTE).trim();
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
    private boolean isClienteValido(final String cliente) {
        return clienteArrayList.contains(cliente);
    }

    /**
     * - Devuelve a la activity que lo solicito los datos de consulta para el nuevo pedido, introducidos por el usuario.
     * - Indica que le ejecucion de la activity ha sido OK.
     * - Finaliza la activity.
     */
    private void returnDatosDCP() {

        //Enviamos los datos del pedido a la activity que solicito el subdialogo
        Intent intent;
        if (getActivity() instanceof InTrazaActivity_) {
            intent = InTrazaActivity_.intent(this).extra("DATOS_CONSULTA_PEDIDOS", datosConsultaPedidos).get();
            ((InTrazaActivity_) getActivity()).onActivityResult(DIALOGO_RETORNA_RESULTADO, Activity.RESULT_OK, intent);
        } else if (getActivity() instanceof PantallaRutero_) {
            intent = PantallaRutero_.intent(this).extra("DATOS_CONSULTA_PEDIDOS", datosConsultaPedidos).get();
            ((PantallaRutero_) getActivity()).onActivityResult(DIALOGO_RETORNA_RESULTADO, Activity.RESULT_OK, intent);
        }
        dismiss();
    }

    /**
     * Termina la activity
     */

    @Click(id.cancelarBotonDialogoDCP)
    void cancelarDialogo() {
        //Enviamos los datos del pedido a la activity que solicito el subdialogo
        Intent intent;
        if (getActivity() instanceof InTrazaActivity_) {
            intent = InTrazaActivity_.intent(this).extra("DATOS_CONSULTA_PEDIDOS", datosConsultaPedidos).get();
            ((InTrazaActivity_) getActivity()).onActivityResult(6, Activity.RESULT_OK, intent);
        } else if (getActivity() instanceof PantallaRutero_) {
            intent = PantallaRutero_.intent(this).extra("DATOS_CONSULTA_PEDIDOS", datosConsultaPedidos).get();
            ((PantallaRutero_) getActivity()).onActivityResult(5, Activity.RESULT_OK, intent);
        }
        dismiss();
    }

    @Click(id.botonAbreSpinnerDCP)
    void abreSpin() {
        spinnerCliente.performClick();
    }
}
