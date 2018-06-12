package intraza.com.interfaz.dialogos;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.util.DisplayMetrics;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.ViewById;

import intraza.com.R.layout;
import intraza.com.R.id;
import intraza.com.R;
import intraza.com.interfaz.pantallas.PantallaListaPedidos_;

/**
 * @author JuanIgnacio
 *         <p/>
 *         Activity que solicita al usuario la confirmacion de la accion a tomar cuando hay
 *         datos pendientes de guardar en el pedido y solicita
 *         crear un pedido nuevo.
 */
@EFragment(layout.dialogo_confirmacion_borrar_pedidos)
public class DialogoConfirmacionBorrarPedidos extends DialogPadreFragment {

    @ViewById
    TextView textoScrollMensajeAvisoDCBP;

    @FragmentArg("ARRAY_ID_PEDIDOS")
    String[] idPedidos;

    @FragmentArg("ARRAY_CLIENTE_PEDIDOS")
    String[] clientePedidos;

    @FragmentArg
    int DIALOGO_RETORNA_RESULTADO;

    @Override
    public void onStart() {
        super.onStart();

        DisplayMetrics displaymetrics = new DisplayMetrics();
        this.getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int dWidth = displaymetrics.widthPixels;
        Dialog d = getDialog();
        if (d != null) {
            int width = (int) (dWidth / 1.8f);
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;
            d.getWindow().setLayout(width, height);
        }
    }


    @AfterViews
    void init() {
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.MY_DIALOG);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        String mensajeAviso = "";

        for (int i = 0; i < idPedidos.length; i++) {
            mensajeAviso += idPedidos[i] + " - " + clientePedidos[i] + (i != (idPedidos.length - 1) ? "\n" : "");
        }
        textoScrollMensajeAvisoDCBP.setText(mensajeAviso);
    }

    /**
     * - Devuelve a la activity que lo solicito que el usuario ha pulsado el boton SI.
     * - Finaliza la activity.
     */
    @Click(id.siBotonDCBP)
    void returnSi() {
        Intent intent = PantallaListaPedidos_.intent(this).extra("ARRAY_ID_PEDIDOS", idPedidos).get();
        ((PantallaListaPedidos_) getActivity()).onActivityResult(DIALOGO_RETORNA_RESULTADO, Activity.RESULT_OK, intent);
        dismiss();
    }

    /**
     * Termina la activity
     */
    @Click(id.noBotonDCBP)
    void cancelarDialogo() {
        Intent intent = PantallaListaPedidos_.intent(this).get();
        ((PantallaListaPedidos_) getActivity()).onActivityResult(5, Activity.RESULT_CANCELED, intent);
        dismiss();
    }
}