package intraza.com.interfaz.datos;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by JuanIgnacio on 27/06/2015 on  09:47.
 */
public class ListaRuteros extends ArrayList<DatosRuteroSincro> implements Serializable{


    public ListaRuteros() {
    }

    @Override
    public boolean add(DatosRuteroSincro object) {
        return super.add(object);
    }

    @Override
    public void add(int index, DatosRuteroSincro object) {
        super.add(index, object);
    }

    @Override
    public DatosRuteroSincro get(int index) {
        return super.get(index);
    }
}
