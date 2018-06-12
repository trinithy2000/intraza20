package intraza.com.interfaz.dialogos;

import android.app.Dialog;
import android.app.DialogFragment;
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
/**
 * Activity que muestra al usuario las observaciones del pedido con boton aceptar.
 */

@SuppressWarnings("CyclicClassDependency")
@EFragment(layout.dialogo_muestra_observaciones_pedido)
public class DialogoMuestraObservacionesPedido extends DialogPadreFragment {

    @ViewById(id.tituloDMOP)
    TextView titulo;

    @ViewById(id.observacionesDMOP)
    TextView observaciones;

    @FragmentArg("ID_PEDIDO")
    String tituloExtra;

    @FragmentArg("OBSERVACIONES")
    String observacionesExtra;

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
        //Se actualizan los widget del layout con los datos
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.MY_DIALOG);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        titulo.setText(tituloExtra);
        observaciones.setText(observacionesExtra);
        //En el xml que define el widget para mostrar las observaciones, ya se han indicado las propiedades para que no sea editable
    }

    /**
     * - Finaliza la activity.
     */
    @Click(id.aceptarDMOP)
    void finalizar() {
        dismiss();
    }
}