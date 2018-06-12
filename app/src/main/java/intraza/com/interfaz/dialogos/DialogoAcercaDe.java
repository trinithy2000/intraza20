package intraza.com.interfaz.dialogos;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.view.ViewGroup;
import android.view.Window;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.res.StringRes;
import intraza.com.R.layout;
import intraza.com.R.id;
import intraza.com.R.style;

/**
 * Activity que muestra la informacion de "Acerda De" de la aplicacion
 */
@SuppressWarnings("CyclicClassDependency")
@EFragment(layout.dialogo_acerca_de)
public class DialogoAcercaDe extends DialogPadreFragment {

    @StringRes
    String www;

    @StringRes
    String mail;

    @StringRes
    String app_name;

    @StringRes
    String chooseEmail;

    @AfterViews
    void init() {
        setStyle(STYLE_NO_TITLE, style.MY_DIALOG);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
    }

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

    /**
     * Termina la activity
     */
    @Click(id.aceptarAcercaDe)
    void cancelarDialogo() {
        dismiss();
    }

    /**
     * Abre un navegador con la URL indicada en el parametro de entrada
     */
    @Click(id.informacionAcercaDe2)
    void mostrarWeb() {
        final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://" + www));
        startActivity(intent);
    }

    /**
     * Envia un correo a la direccion de correo indicada en el parametro.
     */
    @Click(id.informacionAcercaDe6)
    void enviarCorreo() {

        final String subject = app_name + "Android";
        final String message = "";

        final Intent email = new Intent(Intent.ACTION_SEND);
        email.putExtra(Intent.EXTRA_EMAIL, new String[]{mail});
        email.putExtra(Intent.EXTRA_SUBJECT, subject);
        email.putExtra(Intent.EXTRA_TEXT, message);

        //need this to prompts email client only
        email.setType("message/rfc822");
        startActivity(Intent.createChooser(email, chooseEmail));
    }

}