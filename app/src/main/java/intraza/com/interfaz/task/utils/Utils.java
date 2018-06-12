package intraza.com.interfaz.task.utils;

import android.content.Context;
import android.util.Base64;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import intraza.com.Constantes;

/**
 * Created by JuanIgnacio on 20/05/2015 on  18:38.
 */
public class Utils {

    public static Map<Integer, Integer> generaIntervalos(float numero) {

        Map<Integer, Integer> intervalos = new LinkedHashMap<>();
        Integer aux = Math.round(numero);
        Integer salto = Constantes.INTERVALO;
        Integer saltoinc = Constantes.UNO;
        Integer inc = salto;
        if (numero < salto) {
            intervalos.put(0, aux);
        } else {
            intervalos.put(0, salto);

            while (saltoinc < aux) {
                saltoinc += inc;
                salto += inc;
                if ((saltoinc) < aux) {
                    if (salto > aux)
                        intervalos.put(saltoinc, aux);
                    else
                        intervalos.put(saltoinc, salto);
                }
            }
        }
        return intervalos;
    }


    public static Map<Integer, Integer> generaIntervalos(float numero, int salto) {

        Map<Integer, Integer> intervalos = new LinkedHashMap<>();
        Integer aux = Math.round(numero);
        Integer saltoinc = Constantes.UNO;
        Integer inc = salto;
        if (numero < salto) {
            intervalos.put(0, aux);
        } else {
            intervalos.put(0, salto);

            while (saltoinc < aux) {
                saltoinc += inc;
                salto += inc;
                if ((saltoinc) < aux) {
                    if (salto > aux)
                        intervalos.put(saltoinc, aux);
                    else
                        intervalos.put(saltoinc, salto);
                }
            }
        }
        return intervalos;
    }


    public static String fileRead(Context context, String file) {

        String line;
        String retorno = "";
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(context.getAssets().open(file)));
            while ((line = br.readLine()) != null) {
                retorno += line;
            }
        } catch (IOException ex) {
            return null;
        }
        return retorno;
    }

    public static String obtainDecodedData(String cadena) {
        String retorno = "";

        byte[] buffer = Base64.decode(cadena, 0);
        for (int idx = 0; idx < buffer.length; idx++) {
            if (buffer[idx] == -64) {
                buffer[idx] = -63;
            } else if (buffer[idx] < -76) {
                buffer[idx] = 39;
            }
        }
        try {
            retorno = new String(buffer, "ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return retorno;
    }

    public static <T> List<List<T>> chopIntoParts(final List<T> ls, final int iParts) {
        final List<List<T>> lsParts = new ArrayList<>();
        final int iChunkSize = ls.size() / iParts;
        int iLeftOver = ls.size() % iParts;
        int iTake;

        for (int i = 0, iT = ls.size(); i < iT; i += iTake) {
            if (iLeftOver > 0) {
                iLeftOver--;

                iTake = iChunkSize + 1;
            } else {
                iTake = iChunkSize;
            }

            lsParts.add(new ArrayList<>(ls.subList(i, Math.min(iT, i + iTake))));
        }
        return lsParts;
    }

    public static String formatFecha(String fecha, boolean escritura) {
        String OLD_FORMAT = "yyyy-MM-dd";
        String NEW_FORMAT = "dd-MM-yyyy";
        String date = fecha;
        if (escritura) {
            NEW_FORMAT = "yyyy-MM-dd";
            OLD_FORMAT = "dd-MM-yyyy";
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(OLD_FORMAT);
            Date d = sdf.parse(fecha);
            sdf.applyPattern(NEW_FORMAT);
            date = sdf.format(d);
        } catch (Exception e) {
        }
        return date;
    }
}

