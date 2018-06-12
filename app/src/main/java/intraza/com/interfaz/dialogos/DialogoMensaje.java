package intraza.com.interfaz.dialogos;

import android.app.Activity;
import android.content.Intent;
import android.view.Window;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.WindowFeature;

import intraza.com.InTrazaActivity_;
import intraza.com.R.layout;
import intraza.com.R.id;

import intraza.com.interfaz.datos.DatosDialogoMensaje;

/**
 * Activity que muestra al usuario un mensaje cono boton aceptar.
 */

@SuppressWarnings("CyclicClassDependency")
@EActivity(layout.dialogo_mensaje)
@WindowFeature(Window.FEATURE_NO_TITLE)
public class DialogoMensaje extends Activity {

    @ViewById(id.tituloDM)
    TextView titulo;

    @ViewById(id.informacionDM)
    TextView informacion;

    @Extra("DATOS_MENSAJE")
    DatosDialogoMensaje datosMensaje;

    private String activity;

    @AfterViews
    void init() {

        if (null != datosMensaje) {
            //Se actualizan los widget del layout con los datos del mensaje
            titulo.setText(datosMensaje.getTitulo());
            informacion.setText(datosMensaje.getInformacion());
            activity = datosMensaje.getActivity();
        }
    }

    /**
     * - Devuelve a la activity que solicito el mensaje el control.
     * - Indica que le ejecucion de la activity ha sido OK.
     * - Finaliza la activity.
     */
    @Click(id.aceptarDM)
    void returnDM() {
        Intent intent;
        //Le pasamos los datos de la consulta a la activity principal en el intent
        try {
            intent = new Intent(this, Class.forName(activity));
        } catch (final ClassNotFoundException e) {
            intent = InTrazaActivity_.intent(this).get();
        }
        setResult(Activity.RESULT_OK, intent);
        finish();
    }
}