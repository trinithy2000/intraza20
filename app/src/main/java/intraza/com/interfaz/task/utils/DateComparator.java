package intraza.com.interfaz.task.utils;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

import intraza.com.interfaz.datos.DatosLineaPedido;

/**
 * Created by JuanIgnacio on 09/06/2015 on  16:58.
 */

public class DateComparator implements Comparator<DatosLineaPedido>, Serializable {

    DateFormat df;

    @Override
    public int compare(DatosLineaPedido s1, DatosLineaPedido s2) {

        String fechaUno = s1.getUltimaFecha();
        String fechaDos = s2.getUltimaFecha();

        if ((fechaUno != null && !fechaUno.equals("---") && !fechaUno.equals("")) &&
                (fechaDos != null && !fechaDos.equals("---") && !fechaDos.equals(""))) {
            df = new SimpleDateFormat("dd-MM-yyyy",new Locale("es"));
            Date d1 = null;
            Date d2 = null;
            try {
                d1 = df.parse(fechaUno);
                d2 = df.parse(fechaDos);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return d1 != null ? d1.compareTo(d2) : 0;
        }
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        return false;
    }
}
