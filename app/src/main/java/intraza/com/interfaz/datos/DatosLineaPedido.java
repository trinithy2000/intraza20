package intraza.com.interfaz.datos;

import android.os.Parcel;
import android.os.Parcelable;

import intraza.com.Constantes;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@EqualsAndHashCode(exclude = {"fechaCambioTarifaLista", "hayCambiosSinGuardar"})
public class DatosLineaPedido implements Parcelable {

    public static final Creator<DatosLineaPedido> CREATOR = new Creator<DatosLineaPedido>() {
        public DatosLineaPedido createFromParcel(final Parcel in) {
            return new DatosLineaPedido(in);
        }

        public DatosLineaPedido[] newArray(final int size) {
            return new DatosLineaPedido[size];
        }
    };
    private int idArticulo;
    private String codArticulo;
    private String articulo;
    private String medida = Constantes.UNIDADES + Constantes.SEPARADOR_CANTIDAD_TOTAL_ANIO + Constantes.KILOGRAMOS;
    private boolean congelado;
    private String ultimaFecha;
    private int ultimaUnidades = -1;
    private float ultimaCantidad = -1;
    private int unidadesTotalAnio;
    private float cantidadTotalAnio;
    private float ultimaTarifa;
    private float cantidadKg;
    private int cantidadUd;

    private float tarifaCliente;
    private float tarifaLista;
    private String fechaCambioTarifaLista;
    private String observaciones;

    private boolean fijarTarifa;
    private boolean suprimirTarifa;
    private boolean fijarArticulo;
    private boolean fijarObservaciones;
    //Indica si estos datos se han guardado en la bd de la tablet
    private boolean hayCambiosSinGuardar;

    // example constructor that takes a Parcel and gives you an object populated with it's values
    private DatosLineaPedido(final Parcel in) {
        //You must do this in the same order you put them in (that is, in a FIFO approach)
        idArticulo= in.readInt();
        codArticulo = in.readString();
        articulo = in.readString();
        medida = in.readString();
        congelado = (0 != in.readInt());
        ultimaFecha = in.readString();
        ultimaUnidades = in.readInt();
        ultimaCantidad = in.readFloat();
        ultimaTarifa = in.readFloat();
        cantidadKg = in.readFloat();
        cantidadUd = in.readInt();
        unidadesTotalAnio = in.readInt();
        cantidadTotalAnio = in.readFloat();
        tarifaCliente = in.readFloat();
        tarifaLista = in.readFloat();
        fechaCambioTarifaLista = in.readString();
        observaciones = in.readString();
        fijarTarifa = (0 != in.readInt());
        suprimirTarifa = (0 != in.readInt());
        fijarArticulo = (0 != in.readInt());
        fijarObservaciones = (0 != in.readInt());
        hayCambiosSinGuardar = (0 != in.readInt());
    }

    public float getPrecio() {
        return ((medida.equals(Constantes.KILOGRAMOS) && (0 != Float.compare(-1, cantidadKg))) ? cantidadKg : cantidadUd)
                * tarifaCliente;
    }

    // ********************************************************************************************
    // Codigo para implementar Parcelable y poder pasar el objeto en el Intent a la siguiente Activity
    // ********************************************************************************************

    public int describeContents() {
        return 0;
    }

    // write your object's data to the passed-in Parcel
    public void writeToParcel(final Parcel out, final int flags) {
        out.writeInt(idArticulo);
        out.writeString(codArticulo);
        out.writeString(articulo);
        out.writeString(medida);
        out.writeInt(congelado ? 1 : 0);
        out.writeString(ultimaFecha);
        out.writeInt(ultimaUnidades);
        out.writeFloat(ultimaCantidad);
        out.writeFloat(ultimaTarifa);
        out.writeFloat(cantidadKg);
        out.writeInt(cantidadUd);
        out.writeInt(unidadesTotalAnio);
        out.writeFloat(cantidadTotalAnio);
        out.writeFloat(tarifaCliente);
        out.writeFloat(tarifaLista);
        out.writeString(fechaCambioTarifaLista);
        out.writeString(observaciones);
        out.writeInt(fijarTarifa ? 1 : 0);
        out.writeInt(suprimirTarifa ? 1 : 0);
        out.writeInt(fijarArticulo ? 1 : 0);
        out.writeInt(fijarObservaciones ? 1 : 0);
        out.writeInt(hayCambiosSinGuardar ? 1 : 0);
    }
}
