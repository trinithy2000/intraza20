package intraza.com.interfaz.datosDB;

import java.util.List;

import lombok.Data;

/**
 * Almacena los datos que definen un pedido en la BD
 */
@Data
public class PedidoBD {
    private int idPedido;
    private int idCliente;
    private String cliente;
    private int diaFechaPedido;
    private int mesFechaPedido;
    private int anioFechaPedido;
    private int diaFechaEntrega;
    private int mesFechaEntrega;
    private int anioFechaEntrega;
    private String observaciones;
    private boolean fijarObservaciones;
    private List<LineaPedidoBD> lineasPedido;

    //Para control de la lista de pedidos mostrada en pantalla
    private boolean checkBoxEnviar;
    private boolean checkBoxBorrar;

    /**
     * Constructor.
     *
     * @param idPedido           Integer
     * @param idCliente          Integer
     * @param cliente            String
     * @param diaFechaPedido     Integer
     * @param mesFechaPedido     Integer
     * @param anioFechaPedido    Integer
     * @param diaFechaEntrega    Integer
     * @param mesFechaEntrega    Integer
     * @param anioFechaEntrega   Integer
     * @param observaciones      String
     * @param fijarObservaciones boolean
     */
    public PedidoBD(final int idPedido, final int idCliente, final String cliente, final int diaFechaPedido, final int mesFechaPedido, final int anioFechaPedido, final int diaFechaEntrega, final int mesFechaEntrega, final int anioFechaEntrega, final String observaciones, final boolean fijarObservaciones,
                    final List<LineaPedidoBD> lineasPedido) {
        this.idPedido = idPedido;
        this.idCliente = idCliente;
        this.cliente = cliente;
        this.diaFechaPedido = diaFechaPedido;
        this.mesFechaPedido = mesFechaPedido;
        this.anioFechaPedido = anioFechaPedido;
        this.diaFechaEntrega = diaFechaEntrega;
        this.mesFechaEntrega = mesFechaEntrega;
        this.anioFechaEntrega = anioFechaEntrega;
        this.observaciones = observaciones;
        this.fijarObservaciones = fijarObservaciones;

        this.lineasPedido = lineasPedido;
        checkBoxEnviar = false;
        checkBoxBorrar = false;
    }

    // ***********************
    // METODOS GETTER Y SETTER
    // ***********************

    public String getFechaPedido() {
        return rellena0(diaFechaPedido) + "/" + rellena0(mesFechaPedido) + "/" + anioFechaPedido;
    }

    public String getFechaEntrega() {
        return rellena0(diaFechaEntrega) + "/" + rellena0(mesFechaEntrega) + "/" + anioFechaEntrega;
    }

    /**
     * El precio total se obtiene de las lineas de pedido
     *
     * @return float
     */
    public float getPrecioTotal() {

        float precioTotal = 0;

        for (final LineaPedidoBD lineaPedidoBD : lineasPedido) {
            precioTotal += (lineaPedidoBD.isEsMedidaEnKg() ? lineaPedidoBD.getCantidadKg() : lineaPedidoBD.getCantidadUd()) * lineaPedidoBD.getPrecio();
        }
        return precioTotal;

    }

    // ******************
    // Metodos auxiliares
    // ******************

    /**
     * Dato un valor entero lo formatea para que tenga 2 digitos justificando a 0 por la izquierda
     *
     * @param valor Integer
     * @return String
     */
    private String rellena0(final int valor) {
        return ((0 <= valor) && (9 >= valor)) ? ("0" + valor) : Integer.toString(valor);
    }

    /**
     * Indica si hay observaciones en el pedido
     *
     * @return true si hay observaciones, o false en caso contrario
     */
    public boolean hayObservaciones() {
        return (null != observaciones) && !"".equals(observaciones.trim());
    }
}
