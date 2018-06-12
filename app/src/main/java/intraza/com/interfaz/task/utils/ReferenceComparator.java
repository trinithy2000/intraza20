package intraza.com.interfaz.task.utils;

import java.io.Serializable;
import java.util.Comparator;

import intraza.com.interfaz.datos.DatosLineaPedido;

/**
 * Created by JuanIgnacio on 09/06/2015 on  16:58.
 */

public class ReferenceComparator implements Comparator<DatosLineaPedido>, Serializable {

    @Override
    public int compare(DatosLineaPedido s1, DatosLineaPedido s2) {
        return s1.getCodArticulo().compareToIgnoreCase(s2.getCodArticulo());
   }

    @Override
    public boolean equals(Object o) {
        return false;
    }
}
