package intraza.com.interfaz.pantallas;

import android.app.Activity;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.res.ColorRes;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import intraza.com.interfaz.datos.DatosLineaPedido;
import intraza.com.Constantes;

import intraza.com.R.color;
/**
 * Created by JuanIgnacio on 02/04/2015 on  15:34 on  15:34.
 */
@EActivity
public abstract class PantallaPadre extends Activity {

    @ColorRes(color.colorFilaClaro)
    int colorClaro;
    @ColorRes(color.colorTextoFilaClaro)
    int textoClaro;
    @ColorRes(color.colorFilaOscuro)
    int colorOscuro;
    @ColorRes(color.colorTextoFilaOscuro)
    int textoOscuro;
    @ColorRes(color.colorFilaDatoIntroducido)
    int datoIntroducido;
    @ColorRes(color.colorTextoSinValorPedido)
    int datoSinValor;

    int dameColorFila(Integer number) {
        return (number % 2 == 0) ? colorClaro : colorOscuro;
    }

    int dameColorTextoFila(Integer number) {
        return (number % 2 != 0) ? textoClaro : textoOscuro;
    }

    HttpURLConnection getHttpURLConnection(final int segundosTimeout, final String urlWebServiceRest, final String json) throws IOException {

        final URL url = new URL(urlWebServiceRest);
        final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(segundosTimeout * 1000);
        conn.setRequestMethod("POST");
        conn.setDoInput(true);
        conn.setDoOutput(true);
        conn.setFixedLengthStreamingMode(json.getBytes().length);
        conn.setRequestProperty("Content-Type", "application/json;charset=iso-8859-1");
        return conn;
    }

    /**
     * Muestra una marca al lado de la tarifa de cliente, en caso de que el usuario haya indicado que la quiere fijar en intraza
     *
     * @param ponerMarca xd
     * @return la marca a poner o cadena vacia en caso de no llevar marca
     */
    String ponerMarcaFijarTarifa(final boolean ponerMarca) {
        return ponerMarca ? Constantes.MARCA_FIJAR_TARIFA : "";
    }

    /**
     * Muestra una marca al lado del articulo, en caso de que el usuario lo haya creado nuevo en el rutero e indicado que lo quiere fijar en intraza
     *
     * @param ponerMarca s
     * @return la marca a poner o cadena vacia en caso de no llevar marca
     */
    String ponerMarcaFijarArticulo(final boolean ponerMarca) {
        return ponerMarca ? Constantes.MARCA_FIJAR_ARTICULO : "";
    }

    /**
     * Muestra una marca al final de la descripcion del articulo en caso que sea congelado
     *
     * @param esCongelado s
     * @return la marca a poner o cadena vacia en caso de no llevar marca
     */
    String ponerMarcaCongelado(final boolean esCongelado) {
        return esCongelado ? Constantes.MARCA_CONGELADO : "";

    }

    int getColorDato(final DatosLineaPedido linea) {
        return ((0 != Float.compare(-1f, linea.getCantidadKg())) || (0 != linea.getCantidadUd())) ? datoIntroducido : datoSinValor;
    }
}
