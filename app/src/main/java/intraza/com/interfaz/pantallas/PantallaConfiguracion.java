package intraza.com.interfaz.pantallas;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.ColorRes;
import org.androidannotations.annotations.res.DimensionPixelSizeRes;
import org.androidannotations.annotations.res.DrawableRes;
import org.androidannotations.annotations.res.StringRes;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import intraza.com.Configuracion;
import intraza.com.interfaz.datosDB.ParametroConfiguracionBD;
import intraza.com.interfaz.task.utils.AssetsPropertyReader;

import intraza.com.R.layout;
import intraza.com.R.id;
import intraza.com.R.color;

@EActivity(layout.lista_configuracion)
public class PantallaConfiguracion extends PantallaPadre {

    //El widget que forma la tabla de parametros en pantalla

    @StringRes
    String URI_WEB_SERVICES_SINCRONIZACION;

    @StringRes
    String MENSAJE_PARAMETROS_PC_GUARDADOS;

    @StringRes
    String DESCRIPCION_PARAMETRO_TIMEOUT_WEB_SERVICE_SINCRONIZACION;

    @StringRes
    String DESCRIPCION_PARAMETRO_PERMITIR_PRECIO_0;

    @StringRes
    String DESCRIPCION_PARAMETRO_USUARIO_WS;

    @StringRes
    String DESCRIPCION_PARAMETRO_PASSWORD_WS;

    @StringRes
    String DESCRIPCION_PARAMETRO_CODIGO_STATUS_LINEA_RUTERO_OCULTA;

    @StringRes
    String DESCRIPCION_PARAMETRO_PERMITIR_SINCRONIZAR_CON_PEDIDOS_PENDIENTES;

    @StringRes
    String DESCRIPCION_PARAMETRO_INTERVALOS_A_DIVIDIR_RUTERO;

    @StringRes
    String DESCRIPCION_PARAMETRO_NUM_DIAS_ANTIGUEDAD_MARCA_TARIFA_DEFECTO;

    @DimensionPixelSizeRes
    int heightFilaDatosTablaConfiguracion;

    @DimensionPixelSizeRes
    int textSizeFilaDatosTablaConfiguracion;

    @DrawableRes
    Drawable tabla_celda;

    @DrawableRes
    Drawable tabla_celda_button;

    @ColorRes
    int black;

    @ViewById
    protected TableLayout parametrosTableC;

    @ViewById
    protected ScrollView scrollTablaC;

    @ViewById
    protected Button botonGuardarCambiosPC;

    private int displayWidth;

    AssetsPropertyReader assetsPropertyReader;

    @AfterViews
    void init() {

        final DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        displayWidth = displaymetrics.widthPixels;

        //Obtenemos los parametros de configuracion de la BD
        final List<ParametroConfiguracionBD> parametrosBD = consultaParametrosConfiguracionBD();
        //Mostramos la informacion de los parametros en la pantalla
        cargaParametrosEnPantalla(parametrosBD);
        //Para ocultar el teclado virtual
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    /**
     * Consultamos en la BD de la tablet los parametros de configuracion
     *
     * @return un Vector con los datos de los pedidos consultados
     */
    private List<ParametroConfiguracionBD> consultaParametrosConfiguracionBD() {
        final List<ParametroConfiguracionBD> parametrosConsultados = new ArrayList<>();
        assetsPropertyReader = new AssetsPropertyReader(this);
        Properties p = assetsPropertyReader.getProperties(Configuracion.PROPERTIES);
        parametrosConsultados.add(new ParametroConfiguracionBD("TIMEOUT_WEB_SERVICES_SINCRONIZACION",
                p.getProperty("TIMEOUT_WEB_SERVICES_SINCRONIZACION"),
                DESCRIPCION_PARAMETRO_TIMEOUT_WEB_SERVICE_SINCRONIZACION));

        parametrosConsultados.add(new ParametroConfiguracionBD("URI_WEB_SERVICES_SINCRONIZACION", p.getProperty("URI_WEB_SERVICES_SINCRONIZACION"),
                URI_WEB_SERVICES_SINCRONIZACION));

        parametrosConsultados.add(new ParametroConfiguracionBD("PERMITIR_PRECIO_0", p.getProperty("PERMITIR_PRECIO_0"),
                DESCRIPCION_PARAMETRO_PERMITIR_PRECIO_0));

        parametrosConsultados.add(new ParametroConfiguracionBD("NUM_DIAS_ANTIGUEDAD_MARCA_TARIFA_DEFECTO",
                p.getProperty("NUM_DIAS_ANTIGUEDAD_MARCA_TARIFA_DEFECTO"), DESCRIPCION_PARAMETRO_NUM_DIAS_ANTIGUEDAD_MARCA_TARIFA_DEFECTO
        ));

        parametrosConsultados.add(new ParametroConfiguracionBD("USUARIO_WS", p.getProperty("USUARIO_WS"), DESCRIPCION_PARAMETRO_USUARIO_WS));
        parametrosConsultados.add(new ParametroConfiguracionBD("PASSWORD_WS",
                p.getProperty("PASSWORD_WS"), DESCRIPCION_PARAMETRO_PASSWORD_WS));

        parametrosConsultados.add(new ParametroConfiguracionBD("PERMITIR_SINCRONIZAR_CON_PEDIDOS_PENDIENTES",
                p.getProperty("PERMITIR_SINCRONIZAR_CON_PEDIDOS_PENDIENTES"), DESCRIPCION_PARAMETRO_PERMITIR_SINCRONIZAR_CON_PEDIDOS_PENDIENTES
        ));

        parametrosConsultados.add(new ParametroConfiguracionBD("CODIGO_STATUS_LINEA_RUTERO_OCULTA",
                p.getProperty("CODIGO_STATUS_LINEA_RUTERO_OCULTA"),
                DESCRIPCION_PARAMETRO_CODIGO_STATUS_LINEA_RUTERO_OCULTA));

        return parametrosConsultados;
    }

    /**
     * Carga en la pantalla los parametros de configuracion
     */
    private void cargaParametrosEnPantalla(final List<ParametroConfiguracionBD> parametros) {
        for (final ParametroConfiguracionBD parametro : parametros) {
            insertaLineaParametroConfiguracionEnTabla(parametro);
        }
        //Posicionamos la tabla de configuracion en pantalla, al princio del scroll
        scrollTablaC.scrollTo(0, 0);
    }

    /**
     * Inserta una fila en la pantalla en la tabla de configuracion
     *
     * @param parametro ParametroConfiguracionBD
     */
    void insertaLineaParametroConfiguracionEnTabla(final ParametroConfiguracionBD parametro) {

        final TableRow filaP = new TableRow(this);
        final LinearLayout.LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        params.setMargins(4, 4, 4, 4);
        filaP.setLayoutParams(params);
        filaP.setPadding(10, 5, 10, 5);
        filaP.addView(creaVistaDescripcion(parametro.getNombre(), parametro.getDescripcion()), params);
        filaP.addView(creaVistaValor(parametro.getNombre(), parametro.getValor()), params);
        //Insertamos en la tabla de pedidos de la pantalla el nuevo pedido
        parametrosTableC.addView(filaP, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
    }

    /**
     * Crea un widget para mostrar la descripcion de los parametros en pantalla
     *
     * @param nombre      String
     * @param descripcion String
     * @return View la View que muestra el dato con el formato adecuado
     */
    @SuppressLint("NewApi")
    private View creaVistaDescripcion(final String nombre, final String descripcion) {


        final TextView datoDescripcion = new TextView(this);
        datoDescripcion.setGravity(Gravity.START);
        datoDescripcion.setHeight(heightFilaDatosTablaConfiguracion);
        datoDescripcion.setWidth((int) (displayWidth * .58));
        datoDescripcion.setTextSize(textSizeFilaDatosTablaConfiguracion);
        datoDescripcion.setBackground(tabla_celda_button);
        datoDescripcion.setTextColor(black);
        datoDescripcion.setMaxLines(3);
        datoDescripcion.setPadding(2, 2, 2, 2);
        //Guardamos el nombre del parametro para actualizar la BD
        datoDescripcion.setContentDescription(nombre);
        //Ponemos el dato
        datoDescripcion.setText(descripcion);

        return datoDescripcion;
    }

    /**
     * Crea un widget para mostrar el valor de los parametros en pantalla
     *
     * @param nombre String
     * @param valor  String
     * @return la View que muestra el dato con el formato adecuado
     */
    @SuppressLint("NewApi")
    private View creaVistaValor(final String nombre, final String valor) {

        final EditText datoValor = new EditText(this);
        datoValor.setGravity(Gravity.START);
        datoValor.setHeight(heightFilaDatosTablaConfiguracion);
        datoValor.setWidth(((int) (displayWidth * .38)));
        datoValor.setTextSize(textSizeFilaDatosTablaConfiguracion);
        datoValor.setBackground(tabla_celda);
        datoValor.setPadding(2, 2, 2, 2);
        //Guardamos el nombre del parametro para actualizar la BD
        datoValor.setContentDescription(nombre);
        //Ponemos el dato
        datoValor.setText(valor);

        return datoValor;
    }

    /**
     * Guardamos en la BD el valor de los parametros mostrados en pantalla.
     */
    @Click(id.botonGuardarCambiosPC)
    void guardaParametrosConfiguracion() {
        habilitaClickEnActivity(false);
        TableRow filaTablaView;
        EditText valorView;
        TextView descripcionView;

        final List<ParametroConfiguracionBD> parametrosBD = consultaParametrosConfiguracionBD();

        for (int i = 0; i < parametrosTableC.getChildCount(); i++) {
            //Para cada fila obtenemos la descripcion y el valor
            filaTablaView = (TableRow) parametrosTableC.getChildAt(i);
            descripcionView = (TextView) filaTablaView.getChildAt(0);
            valorView = (EditText) filaTablaView.getChildAt(1);
            parametrosBD.get(i).setValor(valorView.getText().toString());
        }

        assetsPropertyReader.setProperties(Configuracion.PROPERTIES, parametrosBD);

        Toast.makeText(getBaseContext(), MENSAJE_PARAMETROS_PC_GUARDADOS, Toast.LENGTH_SHORT).show();
        habilitaClickEnActivity(true);
    }

    /**
     * Deshabilita o no, todos los eventos onClick de la activity para evitar ejecutar dos click seguidos
     *
     * @param habilita boolean
     */
    private void habilitaClickEnActivity(final boolean habilita) {
        botonGuardarCambiosPC.setClickable(habilita);
    }
}
