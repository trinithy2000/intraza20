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
import org.androidannotations.annotations.res.StringRes;

import intraza.com.Configuracion;
import intraza.com.InTrazaActivity_;
import intraza.com.R.layout;
import intraza.com.R.id;

/**
 * Activity que solicita al usuario la confirmacion para realizar la sincronizacion con la BD de InTraza
 * crear un pedido nuevo.
 */

@SuppressWarnings("CyclicClassDependency")
@EFragment(layout.dialogo_confirmacion_sincronizar)
public class DialogoConfirmacionSincronizar extends DialogPadreFragment {

    @ViewById
    TextView textoMensajeAvisoDCS;

    @StringRes
    String MENSAJE_SINCRO_UNO;

    @StringRes
    String MENSAJE_SINCRO_DOS;

    @FragmentArg
    int DIALOGO_RETORNA_RESULTADO;

    private Configuracion config;

    @Override
    public void onStart() {
        super.onStart();

        DisplayMetrics displaymetrics = new DisplayMetrics();
        this.getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int dWidth = displaymetrics.widthPixels;
        Dialog d = getDialog();
        if (d != null) {
            int width = (int)(dWidth/1.5f) ;
            int height =  ViewGroup.LayoutParams.WRAP_CONTENT;
            d.getWindow().setLayout(width, height);
        }
    }

    @AfterViews
    void init() {

        setStyle(STYLE_NO_TITLE, intraza.com.R.style.MY_DIALOG);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        final String mensaje = MENSAJE_SINCRO_UNO + Configuracion.getInstance().dameUltimaFechaSincronizacion(getActivity()) + ".\n\n" + MENSAJE_SINCRO_DOS;
        textoMensajeAvisoDCS.setText(mensaje);
    }

    /**
     * - Devuelve a la activity que lo solicito que el usuario ha pulsado el boton CANCELAR.
     * - Finaliza la activity.
     */
    @Click(id.continuarBotonDCS)
    void returnContinuar() {
        Intent intent = InTrazaActivity_.intent(this).get();
        ((InTrazaActivity_) getActivity()).onActivityResult(DIALOGO_RETORNA_RESULTADO, Activity.RESULT_OK, intent);
        dismiss();
    }

    /**
     * Termina la activity
     */
    @Click(id.cancelarBotonDCS)
    void cancelarDialogo() {
        dismiss();
    }
}