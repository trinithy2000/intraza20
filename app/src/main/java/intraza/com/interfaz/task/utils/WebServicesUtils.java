package intraza.com.interfaz.task.utils;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.view.WindowManager;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.UiThread;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import intraza.com.Configuracion;
import intraza.com.Constantes;
import intraza.com.interfaz.datos.DatosLineaPedido;
import intraza.com.interfaz.datos.DatosRuteroSincro;
import intraza.com.interfaz.datos.DatosRuteroSincroSimple;
import intraza.com.interfaz.datos.ListaRuteros;
import intraza.com.interfaz.datosDB.AdaptadorBD;

/**
 * Created by JuanIgnacio on 17/05/2015 on  12:57.
 */
@SuppressWarnings("ALL")
@EBean
public class WebServicesUtils {

    private final Context contexto;
    private ProgressDialog dialog;
    private float totalesRuteros;
    private String charSet;
    private Integer sincro;
    private String usuario;
    private String passw;
    private Configuracion config;

    public WebServicesUtils(final Context contexto) {
        this.contexto = contexto;
        config = Configuracion.getInstance();
        config.preparaPropiedades(contexto);
        this.sincro = Configuracion.getInstance().dameTimeoutWebServices();
        this.usuario = Configuracion.getInstance().dameUsuarioWS();
        this.passw = Configuracion.getInstance().damePasswordWS();
    }

    /**
     * Invoca un WebService REST.
     *
     * @param segundosTimeout   segundos
     * @param urlWebServiceRest la URL de invocacion al Web Service
     * @return Una cadena con el resultado de la invocacion al Web Service
     */
    public String invocaWebServiceHttp(final int segundosTimeout, final String urlWebServiceRest) throws Exception {
        Log.d("Sincronizacion", "TRAZA - URL (" + urlWebServiceRest + ") timeout (" + segundosTimeout + ")");

        final HttpURLConnection conn = getHttpURLConnection(segundosTimeout, urlWebServiceRest);
        final BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        final StringBuilder sb = new StringBuilder();

        String line;
        while (null != (line = reader.readLine())) {
            sb.append(line).append("\n");
        }
        reader.close();
        return sb.toString();
    }

    /**
     * Invoca un WebService REST.
     *
     * @param segundosTimeout la URL de invocacion al Web Service
     * @return Una cadena con el resultado de la invocacion al Web Service
     */
    private String invocaWebServiceHttps(final int segundosTimeout, final String urlWebServiceRest) throws Exception {
        String result = "";
        HttpsURLConnection connection = null;
        InputStream content = null;
        Reader reader = null;
        StringBuffer sb = null;
        try {
            Log.d("Sincronizacion", "TRAZA - URL (" + urlWebServiceRest
                    + ") segundo timeout (" + segundosTimeout + ")");

            X509TrustManager tm = new X509TrustManager() {
                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                @Override
                public void checkServerTrusted(
                        X509Certificate[] paramArrayOfX509Certificate,
                        String paramString) throws CertificateException {
                }

                @Override
                public void checkClientTrusted(
                        X509Certificate[] paramArrayOfX509Certificate,
                        String paramString) throws CertificateException {
                }
            };

            SSLContext ctx = SSLContext.getInstance("TLS");
            ctx.init(null, new TrustManager[]{tm}, null);
            URL url = new URL(urlWebServiceRest);
            String encoding = MyBase64.encode(usuario + ":" + passw);
            connection = (HttpsURLConnection) url.openConnection();
            connection.setConnectTimeout(segundosTimeout * 1000);
            connection.setReadTimeout(segundosTimeout * 1000);
            connection.setSSLSocketFactory(ctx.getSocketFactory());
            connection.setHostnameVerifier(new HostnameVerifier() {

                @Override
                public boolean verify(String paramString, SSLSession paramSSLSession) {
                    return true;
                }
            });
            // connection.setRequestProperty("Authorization", "Basic " + encoding);
            connection.setRequestProperty("accept", "application/json;charset=ISO-8859-1");
            try {
                content = (InputStream) connection.getInputStream();
                reader = new InputStreamReader(content);
                final char[] buf = new char[4 * 1024];
                int read;
                sb = new StringBuffer();
                while ((read = reader.read(buf)) > 0) {
                    sb.append(buf, 0, read);
                }
                result = sb.toString();
                reader.close();
            } catch (Exception ex) {
                if (sb != null)
                    sb = null;
                if (reader != null) {
                    reader.close();
                    reader = null;
                }
                if (content != null) {
                    content.close();
                    content = null;
                }
                if (connection != null)
                    connection.disconnect();
                invocaWebServiceHttps(segundosTimeout, urlWebServiceRest, true);
                Log.e("###3exception", ex.getMessage());
            }
        } catch (Exception e) {
            if (sb != null)
                sb = null;
            if (reader != null) {
                reader.close();
                reader = null;
            }
            if (content != null) {
                content.close();
                content = null;
            }
            if (connection != null)
                connection.disconnect();
            invocaWebServiceHttps(segundosTimeout, urlWebServiceRest, true);
            Log.e("###3exception", e.getMessage());
        } finally {
            if (sb != null)
                sb = null;
            if (reader != null) {
                reader.close();
                reader = null;
            }
            if (content != null) {
                content.close();
                content = null;
            }
            if (connection != null)
                connection.disconnect();
        }
        return result;
    }



    /**
     * Invoca un WebService REST.
     *
     * @param segundosTimeout la URL de invocacion al Web Service
     * @return Una cadena con el resultado de la invocacion al Web Service
     */
    private String invocaWebServiceHttpDos(final int segundosTimeout, final String urlWebServiceRest) throws Exception {
        String result = "";
        HttpURLConnection connection = null;
        InputStream content = null;
        Reader reader = null;
        StringBuffer sb = null;
        try {
            Log.d("Sincronizacion", "TRAZA - URL (" + urlWebServiceRest
                    + ") segundo timeout (" + segundosTimeout + ")");
            URL url = new URL(urlWebServiceRest);
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(segundosTimeout * 1000);
            connection.setReadTimeout(segundosTimeout * 1000);
            connection.setRequestProperty("accept", "application/json;charset=ISO-8859-1");
            try {
                content = (InputStream) connection.getInputStream();
                reader = new InputStreamReader(content);
                final char[] buf = new char[4 * 1024];
                int read;
                sb = new StringBuffer();
                while ((read = reader.read(buf)) > 0) {
                    sb.append(buf, 0, read);
                }
                result = sb.toString();
                reader.close();
            } catch (Exception ex) {
                if (sb != null)
                    sb = null;
                if (reader != null) {
                    reader.close();
                    reader = null;
                }
                if (content != null) {
                    content.close();
                    content = null;
                }
                if (connection != null)
                    connection.disconnect();
                invocaWebServiceHttps(segundosTimeout, urlWebServiceRest, true);
                Log.e("###3exception", ex.getMessage());
            }
        } catch (Exception e) {
            if (sb != null)
                sb = null;
            if (reader != null) {
                reader.close();
                reader = null;
            }
            if (content != null) {
                content.close();
                content = null;
            }
            if (connection != null)
                connection.disconnect();
            invocaWebServiceHttps(segundosTimeout, urlWebServiceRest, true);
            Log.e("###3exception", e.getMessage());
        } finally {
            if (sb != null)
                sb = null;
            if (reader != null) {
                reader.close();
                reader = null;
            }
            if (content != null) {
                content.close();
                content = null;
            }
            if (connection != null)
                connection.disconnect();
        }
        return result;
    }





    /**
     * Invoca un WebService REST.
     *
     * @param segundosTimeout la URL de invocacion al Web Service
     * @return Una cadena con el resultado de la invocacion al Web Service
     */
    private String invocaWebServiceHttps(final int segundosTimeout, final String urlWebServiceRest, boolean repite) throws Exception {
        String result = "";
        HttpsURLConnection connection = null;
        InputStream content = null;
        Reader reader = null;
        StringBuffer sb = null;
        try {
            Log.d("Sincronizacion", "TRAZA - URL (" + urlWebServiceRest
                    + ") segundo timeout (" + segundosTimeout + ")");

            X509TrustManager tm = new X509TrustManager() {
                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                @Override
                public void checkServerTrusted(
                        X509Certificate[] paramArrayOfX509Certificate,
                        String paramString) throws CertificateException {
                }

                @Override
                public void checkClientTrusted(
                        X509Certificate[] paramArrayOfX509Certificate,
                        String paramString) throws CertificateException {
                }
            };

            SSLContext ctx = SSLContext.getInstance("TLS");
            ctx.init(null, new TrustManager[]{tm}, null);
            URL url = new URL(urlWebServiceRest);
            String encoding = MyBase64.encode(usuario + ":" + passw);
            connection = (HttpsURLConnection) url.openConnection();
            connection.setConnectTimeout(segundosTimeout * 1000);
            connection.setReadTimeout(segundosTimeout * 1000);
            connection.setSSLSocketFactory(ctx.getSocketFactory());
            connection.setHostnameVerifier(new HostnameVerifier() {

                @Override
                public boolean verify(String paramString, SSLSession paramSSLSession) {
                    return true;
                }
            });
            connection.setRequestProperty("Authorization", "Basic " + encoding);
            connection.setRequestProperty("accept", "application/json;charset=ISO-8859-1");
            try {
                content = (InputStream) connection.getInputStream();
                reader = new InputStreamReader(content);
                final char[] buf = new char[4 * 1024];
                int read;
                sb = new StringBuffer();
                while ((read = reader.read(buf)) > 0) {
                    sb.append(buf, 0, read);
                }
                result = sb.toString();
                reader.close();
            } catch (Exception ex) {
                Log.e("###3exception", ex.getMessage());
            }
        } catch (Exception e) {
            Log.e("###3exception", e.getMessage());
        } finally {
            if (sb != null)
                sb = null;
            if (reader != null) {
                reader.close();
                reader = null;
            }
            if (content != null) {
                content.close();
                content = null;
            }
            if (connection != null)
                connection.disconnect();
        }
        return result;
    }

    /**
     * Invoca un WebService REST.
     *
     * @param segundosTimeout la URL de invocacion al Web Service
     * @return Una cadena con el resultado de la invocacion al Web Service
     */
    private String invocaWebServiceHttpsPost(final int segundosTimeout, final String urlWebServiceRest, List<DatosRuteroSincro> lista) throws Exception {
        String result = "";
        HttpsURLConnection connection = null;
        InputStream content = null;
        Reader reader = null;
        StringBuffer sb = null;

        try {
            Log.d("Sincronizacion", "TRAZA - URL (" + urlWebServiceRest
                    + ") segundo timeout (" + segundosTimeout + ")");

            X509TrustManager tm = new X509TrustManager() {
                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                @Override
                public void checkServerTrusted(
                        X509Certificate[] paramArrayOfX509Certificate,
                        String paramString) throws CertificateException {
                }

                @Override
                public void checkClientTrusted(
                        X509Certificate[] paramArrayOfX509Certificate,
                        String paramString) throws CertificateException {
                }
            };

            SSLContext ctx = SSLContext.getInstance("TLS");
            ctx.init(null, new TrustManager[]{tm}, null);
            URL url = new URL(urlWebServiceRest);
            String encoding = MyBase64.encode(usuario + ":" + passw);
            connection = (HttpsURLConnection) url.openConnection();
            connection.setConnectTimeout(segundosTimeout * 1000);
            connection.setReadTimeout(segundosTimeout * 1000);
            connection.setSSLSocketFactory(ctx.getSocketFactory());
            connection.setHostnameVerifier(new HostnameVerifier() {

                @Override
                public boolean verify(String paramString, SSLSession paramSSLSession) {
                    return true;
                }
            });
            connection.setRequestProperty("Authorization", "Basic " + encoding);
            connection.setRequestProperty("accept", "application/json;charset=ISO-8859-1");
            connection.setRequestMethod("POST");


            try {
                content = (InputStream) connection.getInputStream();
                reader = new InputStreamReader(content);
                final char[] buf = new char[4 * 1024];
                int read;
                sb = new StringBuffer();
                while ((read = reader.read(buf)) > 0) {
                    sb.append(buf, 0, read);
                }
                result = sb.toString();
                reader.close();
            } catch (Exception ex) {
                Log.e("###3exception", ex.getMessage());
            }
        } catch (Exception e) {
            Log.e("###3exception", e.getMessage());
        } finally {
            if (sb != null)
                sb = null;
            if (reader != null) {
                reader.close();
                reader = null;
            }
            if (content != null) {
                content.close();
                content = null;
            }
            if (connection != null)
                connection.disconnect();
        }
        return result;
    }


    private HttpURLConnection getHttpURLConnection(final int segundosTimeout, final String urlWebServiceRest) throws IOException {


        final URL url = new URL(urlWebServiceRest);
        final HttpURLConnection conn = (HttpURLConnection) url.openConnection(Proxy.NO_PROXY);
        conn.setConnectTimeout(segundosTimeout * 100);
        conn.setReadTimeout(segundosTimeout * 10000);
        conn.setRequestMethod("POST");
        conn.setDoInput(true);
        conn.setDoOutput(true);
        conn.setRequestProperty("accept", "application/json;charset=ISO-8859-1");
        return conn;
    }

    /**
     * Obtenemos los registros totales a sincronizar de articulos, clientes,
     * ruteros y observaciones.
     *
     * @throws Exception
     */
    public void registrosTotalesParaSincronizar() throws Exception {
        final JSONObject jsonTotales;

        final Integer sincro = config.dameTimeoutWebServices();
        final String totales = invocaWebServiceHttpDos(sincro, config.dameUriWebServicesSincronizacionTotalRegistros());
        Log.d("totales", totales);
        jsonTotales = new JSONObject(totales);


        // Chequeamos que la ejecucion fue correcta
        if (-1 == jsonTotales.getInt("totalRuteros")) {
            throw new Exception(
                    "Se ha producido una excepcion en la tarea de sincronizacion al recuperar los registros totales a sincronizar.");
        }
        // El resultado lo almacenamos en variables globales
        totalesRuteros = jsonTotales.getInt("totalRuteros");
    }

    /**
     * Sincroniza los datos de clientes de la BD de intraza con los de la
     * tablet: 1 - Obtiene los datos de intraza invocando a un WebService REST
     * que devuelve un JSON. 2 - Borra todos los registros de clientes de la BD
     * de tablet 3 - Procesa el JSON obtenido almacenando los datos de los
     * clientes en la BD de la tablet.
     *
     * @throws Exception
     */
    public void sincronizaClientes() throws JSONException {
        final AdaptadorBD db = new AdaptadorBD(contexto);
        final JSONArray jsonArrayClientes;
        JSONObject jsonCliente = null;

        try {
            db.abrir();
            final String clientes = invocaWebServiceHttpDos(sincro, config.dameUriWebServicesSincronizacionCliente());
            Log.d("clientes", clientes);
            jsonArrayClientes = new JSONArray(clientes);

            if (0 < jsonArrayClientes.length()) {
                db.borrarTodosLosClientes();

                final float totales = jsonArrayClientes.length();

                for (int i = 0; i < jsonArrayClientes.length(); i++) {
                    jsonCliente = jsonArrayClientes.getJSONObject(i);

                    try {
                        String cliente = Utils.obtainDecodedData(jsonCliente.getString("nombreCliente"));

                        db.insertarCliente(jsonCliente.getInt("idCliente"), cliente,
                                jsonCliente.getString("descripcion"), jsonCliente.getString("telefono"));
                    } catch (Exception ex) {
                        Log.d("execpcion", ex.getMessage());
                    }
                    if (0 != i) {
                        final int resultado = (int) ((i * 100) / totales);
                        actualizarEstadoDescarga(resultado);
                    }
                    Log.d("<##clientes##>-", i + "");
                }
            } else {
                throw new Exception("Se ha producido una excepcion en la tarea de sincronizacion al recuperar los clientes.");
            }
        } catch (final Exception e) {
        } finally {
            db.cerrar();
            dialog.dismiss();
            dialog = null;
        }
    }

    /**
     * Sincroniza los datos de articulos de la BD de intraza con los de la
     * tablet: 1 - Obtiene los datos de intraza invocando a un WebService REST
     * que devuelve un JSON. 2 - Borra todos los registros de articulos de la BD
     * de tablet 3 - Procesa el JSON obtenido almacenando los datos de los
     * articulos en la BD de la tablet.
     *
     * @param incrementoTabla incremento total de la tabla en la barra de progreso
     * @throws Exception
     */
    public void sincronizaArticulos() throws JSONException {
        final AdaptadorBD db = new AdaptadorBD(contexto);
        float incrementoActual;
        final int incrementoTotal = 0;
        final JSONArray jsonArrayArticulos;
        JSONObject jsonArticulo = null;

        try {

            db.abrir();
            final String articulos = invocaWebServiceHttpDos(sincro, config.dameUriWebServicesSincronizacionArticulo());
            Log.d("articulos", articulos);
            jsonArrayArticulos = new JSONArray(articulos);

            if (0 < jsonArrayArticulos.length()) {
                db.borrarTodosLosArticulos();
                final float totales = jsonArrayArticulos.length();

                for (int i = 0; i < jsonArrayArticulos.length(); i++) {
                    jsonArticulo = jsonArrayArticulos.getJSONObject(i);
                    String name = Utils.obtainDecodedData(jsonArticulo.getString("name"));
                    //noinspection NumericCastThatLosesPrecision

                    try {
                        db.insertarArticulo(
                                jsonArticulo.getInt("id"),
                                jsonArticulo.getString("codigo"),
                                name,
                                jsonArticulo.getBoolean("esKg"),
                                jsonArticulo.getBoolean("esCongelado"),
                                (float) jsonArticulo.getDouble("tarifa"),
                                jsonArticulo.getString("fechaTarifa"));
                    } catch (Exception ex) {
                        Log.d("execpcion", ex.getMessage());
                    }
                    if (0 != i) {
                        final int resultado = (int) ((i * 100) / totales);
                        actualizarEstadoDescarga(resultado);
                    }
                    Log.d("<##articulos##>-", i + "");
                }
            } else {
                throw new Exception("Se ha producido una excepcion en la tarea de sincronizacion al recuperar los articulos.");
            }
        } catch (final Exception e) {

        } finally {
            db.cerrar();
            dialog.dismiss();
            dialog = null;
        }
    }

    /**
     * Sincroniza los datos de ruteros de la BD de intraza con los de la tablet:
     * 1 - Obtiene los datos de intraza invocando a un WebService REST que
     * devuelve un JSON. 2 - Borra todos los registros de ruteros de la BD de
     * tablet 3 - Procesa el JSON obtenido almacenando los datos de los ruteros
     * en la BD de la tablet.
     *
     * @param incrementoTabla incremento total de la tabla en la barra de progreso
     * @throws Exception
     */
    public void sincronizaRuterosDatos() throws JSONException {

        final AdaptadorBD db = new AdaptadorBD(contexto);
        float incrementoActual;
        final int incrementoTotal = 0;

        JSONObject jsonRutero = null;
        Map<Integer, Integer> intervalos = Utils.generaIntervalos(totalesRuteros);
        ListaRuteros listaRuteros = new ListaRuteros();
        Iterator it = intervalos.entrySet().iterator();

        try {
            Log.d("Sincronizacion", "TRAZA - Sincronizacion");
            String ruteros = "";
            int contador = 0;
            db.abrir();
            db.borrarTodosLosRuteros();
            while (it.hasNext()) {
                Map.Entry e = (Map.Entry) it.next();
                ruteros = invocaWebServiceHttpDos(sincro, config.dameUriWebServicesSincronizacionRutero());
                final JSONArray jsonArrayRuteros = new JSONArray(ruteros);

                if (0 < jsonArrayRuteros.length()) {

                    for (int i = 0; i < jsonArrayRuteros.length(); i++) {
                        jsonRutero = (JSONObject) jsonArrayRuteros.get(i);
                        String observacionesItem = Utils.obtainDecodedData(jsonRutero.getString("observacionesItem"));
                        try {
                            db.insertarRutero(
                                    jsonRutero.getInt("idArticulo"),
                                    jsonRutero.getString("codigoArticulo"),
                                    jsonRutero.getInt("idCliente"),
                                    jsonRutero.getString("fechaPedido"),
                                    jsonRutero.getInt("unidades"),
                                    jsonRutero.getDouble("peso"),
                                    jsonRutero.getInt("unidadesTotalAnio"),
                                    jsonRutero.getDouble("pesoTotalAnio"),
                                    jsonRutero.getDouble("precio"),
                                    jsonRutero.getDouble("precioCliente"),
                                    observacionesItem);
                        } catch (Exception ex) {
                            Log.d("execpcion", ex.getMessage());
                        }
                        if (0 != contador) {
                            final int resultado = (int) ((contador * 100) / totalesRuteros);
                            actualizarEstadoDescarga(resultado);
                        }
                        Log.d("<##ruteros##>-", i + "");
                        contador++;
                    }
                    contador = (int) e.getValue();
                }
            }
        } catch (final Exception e) {
            throw new JSONException(e.getLocalizedMessage());
        } finally {
            db.cerrar();
            dialog.dismiss();
            dialog = null;
        }
    }


    public void procesaTarifasPesos() throws JSONException {

        Map<Integer, Integer> intervalos = Utils.generaIntervalos(totalesRuteros, Constantes.INTERVALO_TARIFAS);
        Iterator it = intervalos.entrySet().iterator();
        List<DatosRuteroSincroSimple> lista = new ArrayList<>();
        final AdaptadorBD db = new AdaptadorBD(contexto);
        db.abrir();

        JSONObject jsonResultadoDR = null;
        int contador = 0;
        String ruteros = "";

        String codArticulo = "";
        int idCliente = 0;
        int unidadesTotalAnio = 0;
        double cantidadTotalAnio = 0d;
        double tarifaCliente = 0d;

        try {
            Log.d("Sincronizacion", "TRAZA - Sincronizacion");

            while (it.hasNext()) {
                Map.Entry e = (Map.Entry) it.next();
                try {
                    ruteros = invocaWebServiceHttpDos(sincro, config.dameUriWebServicesSincronizacionRuteroFraccionadoTarifa((int) e.getKey(), (int) e.getValue()));
                } catch (Exception ex) {
                    Log.e("## Excepcion ##", ex.getMessage());
                }

                final JSONArray jsonArrayRuteros = new JSONArray(ruteros);

                if (0 < jsonArrayRuteros.length()) {
                    for (int i = 0; i < jsonArrayRuteros.length(); i++) {
                        JSONObject jsonTarifas = (JSONObject) jsonArrayRuteros.get(i);

                        idCliente = jsonTarifas.getInt("cliente");
                        codArticulo = jsonTarifas.getString("codigo");
                        unidadesTotalAnio = jsonTarifas.getInt("pesoTotalAnio");
                        tarifaCliente = jsonTarifas.getDouble("tarifaCliente");
                        cantidadTotalAnio = jsonTarifas.getDouble("pesoTotalAnio");

                        db.actualizarRutero(codArticulo, idCliente, unidadesTotalAnio, cantidadTotalAnio, tarifaCliente);
                        jsonTarifas = null;
                        if (0 != contador) {
                            final int resultado = (int) ((contador * 100) / totalesRuteros);
                            actualizarEstadoDescarga(resultado);
                        }
                        contador++;
                    }
                    contador = (int) e.getValue();
                }
            }
        } catch (final Exception e) {
            throw new JSONException(e.getLocalizedMessage());
        } finally {
            db.cerrar();
            dialog.dismiss();
            dialog = null;
        }

    }

    public void sincronizaRuterosTotal() throws Exception {

        final AdaptadorBD db = new AdaptadorBD(contexto);
        float incrementoActual;
        final int incrementoTotal = 0;
        JSONObject jsonRutero = null;

        Map<Integer, Integer> intervalos = Utils.generaIntervalos(totalesRuteros, Constantes.INTERVALO_TARIFAS);
        Iterator it = intervalos.entrySet().iterator();
        db.abrir();
        db.borrarTodosLosRuteros();

        try {
            Log.d("Sincronizacion", "TRAZA - Sincronizacion");
            String ruteros = "";
            int contador = 0;
            while (it.hasNext()) {
                Map.Entry e = (Map.Entry) it.next();
                try {
                    ruteros = invocaWebServiceHttpDos(sincro, config.dameUriWebServicesSincronizacionRuteroTotal());
                } catch (Exception ex) {
                    Log.e("## Excepcion ##", ex.getMessage());
                }

                final JSONArray jsonArrayRuteros = new JSONArray(ruteros);

                if (0 < jsonArrayRuteros.length()) {

                    for (int i = 0; i < jsonArrayRuteros.length(); i++) {
                        jsonRutero = (JSONObject) jsonArrayRuteros.get(i);

                        String observacionesItem = Utils.obtainDecodedData(jsonRutero.getString("observacionesItem"));

                        db.insertarRutero(
                                jsonRutero.getInt("idArticulo"),
                                jsonRutero.getString("codigoArticulo"),
                                jsonRutero.getInt("idCliente"),
                                jsonRutero.getString("fechaPedido"),
                                jsonRutero.getInt("unidades"),
                                jsonRutero.getDouble("peso"),
                                jsonRutero.getInt("unidadesTotalAnio"),
                                jsonRutero.getDouble("pesoTotalAnio"),
                                jsonRutero.getDouble("precio"),
                                jsonRutero.getDouble("precioCliente"),
                                observacionesItem);

                        if (0 != contador) {
                            final int resultado = (int) ((contador * 100) / totalesRuteros);
                            actualizarEstadoDescarga(resultado);
                        }
                        contador++;

                    }
                }
                contador = (int) e.getValue();
            }
        } catch (final Exception e) {
            throw new JSONException(e.getLocalizedMessage());
        } finally {
            db.cerrar();
            dialog.dismiss();
            dialog = null;

        }
    }

    public void sincronizaRuterosDatosLista(List<DatosLineaPedido> lista,
                                            int codigoCliente) throws Exception {

        final AdaptadorBD db = new AdaptadorBD(contexto);
        float incrementoActual;
        final int incrementoTotal = 0;
        JSONObject jsonRutero = null;
        db.abrir();

        try {
            Log.d("Sincronizacion", "TRAZA - Sincronizacion");
            final Integer sincro = config.dameTimeoutWebServices();
            String ruteros = "";
            int contador = 0;
            String codigoArticulo = "";

            for (DatosLineaPedido dato : lista) {
                codigoArticulo = dato.getCodArticulo();
                ruteros = invocaWebServiceHttpDos(sincro, config.dameUriWebServicesSincronizacionRuteroDatos((int) codigoCliente, codigoArticulo));

                jsonRutero = new JSONObject(ruteros);

                db.actualizarRutero(codigoArticulo,
                        codigoCliente,
                        jsonRutero.getInt("unidadesTotalAnio"),
                        jsonRutero.getDouble("pesoTotalAnio"),
                        jsonRutero.getDouble("tarifaCliente"));

                if (0 != contador) {
                    final int resultado = (int) ((contador * 100) / lista.size());
                    actualizarEstadoDescarga(resultado);
                }
                contador++;
                Log.e("####### Sincronizacion ######", "TRAZA - " + codigoArticulo + ", " + codigoCliente + "###########");
            }
        } catch (final Exception e) {
            Log.e("####### Excepcion ######", e.getLocalizedMessage());
            e.printStackTrace();
        } finally {
            db.cerrar();
            dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            dialog.dismiss();
            dialog = null;
        }
    }

    @UiThread
    void actualizarEstadoDescarga(final Integer progreso) {
        dialog.setProgress(progreso);
    }

    public void setDialog(final ProgressDialog dialog) {
        this.dialog = dialog;
    }

    public Dialog getDialog() {
        return this.dialog;
    }
}