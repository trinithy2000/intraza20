package intraza.com.interfaz.datos;

import android.os.Parcel;
import android.os.Parcelable;

import intraza.com.Constantes;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Almacena los datos que definen un pedido
 */
@Data
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@EqualsAndHashCode(exclude = {"idPedido", "precioTotal", "hayDatosPedidoSinGuardar", "hayLineasPedido"})
public class DatosPedido implements Parcelable {

    public static final Creator<DatosPedido> CREATOR = new Creator<DatosPedido>() {
        public DatosPedido createFromParcel(final Parcel in) {
            return new DatosPedido(in);
        }

        public DatosPedido[] newArray(final int size) {
            return new DatosPedido[size];
        }
    };

    private int idPedido;
    private int idCliente;
    private String cliente;
    private int diaFechaPedido;
    private int mesFechaPedido;
    private int anioFechaPedido;
    private int diaFechaEntrega;
    private int mesFechaEntrega;
    private int anioFechaEntrega;
    private float precioTotal;
    private String observaciones;
    private boolean fijarObservaciones;
    private int descuentoEspecial;
    //Indica si el pedido tiene algun cambio en los datos generales del pedido o en las lineas de pedido sin guardar
    private boolean hayDatosPedidoSinGuardar;
    //Indica si el pedido tiene lineas de pedido, sin lineas de pedido no se permite guardar el pedido
    private boolean hayLineasPedido;

    // example constructor that takes a Parcel and gives you an object populated with it's values
    private DatosPedido(final Parcel in) {
        //You must do this in the same order you put them in (that is, in a FIFO approach)
        idPedido = in.readInt();
        idCliente = in.readInt();
        cliente = in.readString();
        diaFechaPedido = in.readInt();
        mesFechaPedido = in.readInt();
        anioFechaPedido = in.readInt();
        diaFechaEntrega = in.readInt();
        mesFechaEntrega = in.readInt();
        anioFechaEntrega = in.readInt();
        precioTotal = in.readFloat();
        observaciones = in.readString();
        fijarObservaciones = (0 != in.readInt());
        descuentoEspecial = in.readInt();
        hayDatosPedidoSinGuardar = (0 != in.readInt());
        hayLineasPedido = (0 != in.readInt());
    }

    public String getFechaPedido() {
        return rellena0(diaFechaPedido) + Constantes.SEPARADOR_FECHA + rellena0(mesFechaPedido) + Constantes.SEPARADOR_FECHA + anioFechaPedido;
    }

    public String getFechaEntrega() {
        return rellena0(diaFechaEntrega) + Constantes.SEPARADOR_FECHA + rellena0(mesFechaEntrega) + Constantes.SEPARADOR_FECHA + anioFechaEntrega;
    }

    /**
     * Dato un valor entero lo formatea para que tenga 2 digitos justificando a 0 por la izquierda
     *
     * @param valor d
     */
    private String rellena0(final int valor) {

        return ((0 <= valor) && (9 >= valor)) ? ("0" + valor) : Integer.toString(valor);
    }


    // ********************************************************************************************
    // Codigo para implementar Parcelable y poder pasar el objeto en el Intent a la siguiente Activity
    // ********************************************************************************************

    public int describeContents() {
        return 0;
    }

    // write your object's data to the passed-in Parcel
    public void writeToParcel(final Parcel out, final int flags) {
        out.writeInt(idPedido);
        out.writeInt(idCliente);
        out.writeString(cliente);
        out.writeInt(diaFechaPedido);
        out.writeInt(mesFechaPedido);
        out.writeInt(anioFechaPedido);
        out.writeInt(diaFechaEntrega);
        out.writeInt(mesFechaEntrega);
        out.writeInt(anioFechaEntrega);
        out.writeFloat(precioTotal);
        out.writeString(observaciones);
        out.writeInt(fijarObservaciones ? 1 : 0);
        out.writeInt(descuentoEspecial);
        out.writeInt(hayDatosPedidoSinGuardar ? 1 : 0);
        out.writeInt(hayLineasPedido ? 1 : 0);
    }
}
