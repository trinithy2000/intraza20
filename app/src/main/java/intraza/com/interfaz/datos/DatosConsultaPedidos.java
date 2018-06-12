package intraza.com.interfaz.datos;

import android.os.Parcel;
import android.os.Parcelable;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Almacena los datos que definen un pedido
 */

@Data
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@EqualsAndHashCode(exclude = "cliente")
public class DatosConsultaPedidos implements Parcelable {

    public static final Creator<DatosConsultaPedidos> CREATOR = new Creator<DatosConsultaPedidos>() {
        public DatosConsultaPedidos createFromParcel(final Parcel in) {
            return new DatosConsultaPedidos(in);
        }

        public DatosConsultaPedidos[] newArray(final int size) {
            return new DatosConsultaPedidos[size];
        }
    };

    private int idPedido;
    private int idCliente;
    private String cliente;


    // example constructor that takes a Parcel and gives you an object populated with it's values
    private DatosConsultaPedidos(final Parcel in) {
        //You must do this in the same order you put them in (that is, in a FIFO approach)
        idPedido = in.readInt();
        idCliente = in.readInt();
        cliente = in.readString();
    }

    public boolean hayDatoIdPedido() {
        return (0 != idPedido);
    }

    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel out, final int i) {
        out.writeInt(idPedido);
        out.writeInt(idCliente);
        out.writeString(cliente);
    }
}
