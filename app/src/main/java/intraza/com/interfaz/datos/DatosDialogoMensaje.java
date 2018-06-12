package intraza.com.interfaz.datos;

import android.os.Parcel;
import android.os.Parcelable;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Almacena los datos que configuran el dialogo mensaje
 */

@Data
@AllArgsConstructor(access = AccessLevel.PUBLIC)
public class DatosDialogoMensaje implements Parcelable {

    public static final Creator<DatosDialogoMensaje> CREATOR = new Creator<DatosDialogoMensaje>() {
        public DatosDialogoMensaje createFromParcel(final Parcel in) {
            return new DatosDialogoMensaje(in);
        }

        public DatosDialogoMensaje[] newArray(final int size) {
            return new DatosDialogoMensaje[size];
        }
    };
    private String activity;
    private String titulo;
    private String informacion;

    // example constructor that takes a Parcel and gives you an object populated with it's values
    private DatosDialogoMensaje(final Parcel in) {
        //You must do this in the same order you put them in (that is, in a FIFO approach)
        activity = in.readString();
        titulo = in.readString();
        informacion = in.readString();
    }

    public void setInformacion(final String informacion) {
        this.informacion = informacion;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(final Parcel out, final int flags) {
        out.writeString(activity);
        out.writeString(titulo);
        out.writeString(informacion);
    }
}
