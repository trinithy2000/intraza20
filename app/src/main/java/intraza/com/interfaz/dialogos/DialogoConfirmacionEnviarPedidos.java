package intraza.com.interfaz.dialogos;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.util.DisplayMetrics;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import android.widget.ToggleButton;

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
 * Activity que solicita al usuario la confirmaci�n de la acci�n a tomar cuando hay datos pendientes de guardar en el pedido y solicita
 * crear un pedido nuevo.
 *
 * @author JLZS
 */
@EFragment(layout.dialogo_confirmacion_enviar_pedidos)
public class DialogoConfirmacionEnviarPedidos extends DialogPadreFragment {
    //Id y cliente de los pedidos a enviar

    @ViewById
    TextView textoScrollMensajeAvisoDCEP;

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
            int width = (int) (dWidth / 1.7f);
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;
            d.getWindow().setLayout(width, height);
        }
    }

    @AfterViews
    void init() {
        setStyle(STYLE_NO_TITLE, R.style.MY_DIALOG);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        String mensajeAviso = "";
        //Formamos el mensaje de aviso a mostar, mostramos la informacion de todos los pedidos a enviar
        for (int i = 0; i < idPedidos.length; i++) {
            mensajeAviso += idPedidos[i] + " - " + clientePedidos[i];
            mensajeAviso += (i != (idPedidos.length - 1)) ? "\n" : "";
        }
        textoScrollMensajeAvisoDCEP.setText(mensajeAviso);
    }

    /**
     * - Devuelve a la activity que lo solicito que el usuario ha pulsado el boton SI.
     * - Finaliza la activity.
     */
    @Click(id.siBotonDCEP)
    void returnSi() {
        Intent intent = PantallaListaPedidos_.intent(this).extra("ARRAY_ID_PEDIDOS", idPedidos)
                .extra("USAR_WIFI", true).get();
        ((PantallaListaPedidos_) getActivity()).onActivityResult(DIALOGO_RETORNA_RESULTADO, Activity.RESULT_OK, intent);
        dismiss();
    }

    /**
     * Termina la activity
     */
    @Click(id.noBotonDCEP)
    void cancelarDialogo() {
        Intent intent = PantallaListaPedidos_.intent(this).get();
        ((PantallaListaPedidos_) getActivity()).onActivityResult(5, Activity.RESULT_CANCELED, intent);
        dismiss();
    }
}