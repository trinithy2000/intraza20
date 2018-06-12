package intraza.com.interfaz.dialogos;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.util.DisplayMetrics;
import android.view.ViewGroup;
import android.view.Window;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;

import intraza.com.R.layout;
import intraza.com.R.id;
import intraza.com.R;
import intraza.com.interfaz.pantallas.PantallaRutero_;

/**
 * @author JuanIgnacio
 *         <p/>
 *         Activity que solicita al usuario la confirmacion de la accion a tomar cuando
 *         hay datos pendientes de guardar en el pedido y solicita
 *         crear un pedido nuevo.
 */
@SuppressWarnings("CyclicClassDependency")
@EFragment(layout.dialogo_aviso_datos_sin_guardar)
public class DialogoAvisoDatosSinGuardar extends DialogPadreFragment {

    /**
     * - Devuelve a la activity que lo solicito que el usuario ha pulsado el boton SI.
     * - Finaliza la activity.
     */
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
            int width = (int) (dWidth / 1.5f);
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;
            d.getWindow().setLayout(width, height);
        }
    }

    @AfterViews
    void init() {
        setStyle(STYLE_NO_TITLE, R.style.MY_DIALOG);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
    }


    @Click(id.siBotonAvisoDatosSinGuardar)
    void returnSi() {
        Intent intent = PantallaRutero_.intent(this).get();
        ((PantallaRutero_) getActivity()).onActivityResult(DIALOGO_RETORNA_RESULTADO, Activity.RESULT_OK, intent);
        dismiss();
    }

    /**
     * - Devuelve a la activity que lo solicito que el usuario ha pulsado el boton NO.
     * - Finaliza la activity.
     */
    @Click(id.noBotonAvisoDatosSinGuardar)
    void returnNo() {
        Intent intent = PantallaRutero_.intent(this).get();
        ((PantallaRutero_) getActivity()).onActivityResult(DIALOGO_RETORNA_RESULTADO, Activity.RESULT_FIRST_USER, intent);
        dismiss();
    }

    /**
     * Termina la activity
     */
    @Click(id.cancelarBotonAvisoDatosSinGuardar)
    void cancelarDialogo() {
        dismiss();
    }
}