package intraza.com.interfaz.dialogos;

import android.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import intraza.com.R;
import intraza.com.R.id;

/**
 * Created by JuanIgnacio on 18/04/2015 on  18:28.
 */
public abstract class DialogPadreFragment extends DialogFragment {

    /**
     * Muestra un mensaje de error toast con el formato correspondiente.
     *
     * @param mensaje de error a mostrar
     */
    void toastMensajeError(final String mensaje) {
        final LayoutInflater inflater = getActivity().getLayoutInflater();
        final View layout = inflater.inflate(R.layout.toast_error,
                (ViewGroup) getActivity().findViewById(id.toast_layout_root));

        final TextView text = layout.findViewById(id.text);
        text.setText(mensaje);

        final Toast toast = new Toast(getActivity().getApplicationContext());
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }
}
