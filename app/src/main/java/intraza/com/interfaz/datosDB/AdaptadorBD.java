package intraza.com.interfaz.datosDB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import intraza.com.Configuracion;

/**
 * @author JLZS
 * <p/>
 * Clase de acceso a la BD
 */
public class AdaptadorBD {

    private final DataBaseHelper DBHelper;
    private SQLiteDatabase db;

    public AdaptadorBD(final Context contexto) {
        DBHelper = new DataBaseHelper(contexto);
    }

    /**
     * Abre la BD.
     *
     * @throws SQLException
     */
    public void abrir() throws SQLException {
        //Abre la BD y si la BD no existe llama al metodo onCreate
        db = DBHelper.getWritableDatabase();
    }

    /**
     * Cierra la BD.
     */
    public void cerrar() {
        DBHelper.close();
    }


    /**
     * Inserta un articulo en la tabla de articulos.
     *
     * @param codigo                   d
     * @param nombre                   d
     * @param esKg                     d
     * @param esCongelado              d
     * @param tarifaDefecto            d
     * @param fechaCambioTarifaDefecto d
     */
    public void insertarArticulo(final int id, final String codigo, final String nombre, final boolean esKg, final boolean esCongelado,
                                 final float tarifaDefecto, final String fechaCambioTarifaDefecto) {
        final ContentValues valoresIniciales = new ContentValues();

        valoresIniciales.put(TablaArticulo.KEY_CAMPO_ID_ARTICULO, id);
        valoresIniciales.put(TablaArticulo.KEY_CAMPO_CODIGO_ARTICULO, codigo);
        valoresIniciales.put(TablaArticulo.CAMPO_NOMBRE, nombre);
        valoresIniciales.put(TablaArticulo.CAMPO_ES_KG, esKg);
        valoresIniciales.put(TablaArticulo.CAMPO_ES_CONGELADO, esCongelado);
        valoresIniciales.put(TablaArticulo.CAMPO_TARIFA_DEFECTO, tarifaDefecto);
        valoresIniciales.put(TablaArticulo.CAMPO_FECHA_CAMBIO_TARIFA_DEFECTO, fechaCambioTarifaDefecto);
        try {
            db.insertOrThrow(TablaArticulo.NOMBRE_TABLA, null, valoresIniciales);
        } catch (Exception ex) {
        }
    }

    ///////////////////////////////////////////
    //
    //   TABLA ARTICULO
    //
    ///////////////////////////////////////////

    /**
     * Borra un articulo en concreto de la tabla de articulos
     *
     * @param codigo del articulo a borrar
     * @return true si ha ido bien o false en caso de error
     */
    public boolean borrarArticulo(final String codigo) {
        return 0 < db.delete(TablaArticulo.NOMBRE_TABLA, TablaArticulo.KEY_CAMPO_CODIGO_ARTICULO + " like '" + codigo + "'", null);
    }

    /**
     * Borra todos los articulos
     */
    public void borrarTodosLosArticulos() {
        db.delete(TablaArticulo.NOMBRE_TABLA, null, null);
    }

    /**
     * Recupera todos los articulos de la tabla de articulos que no estan en el rutero del cliente
     *
     * @param idCliente d
     * @return Cursor
     */
    public Cursor obtenerArticulosNoEnRuteroCliente(final int idCliente) {
        return db.query(TablaArticulo.NOMBRE_TABLA,
                new String[]{TablaArticulo.KEY_CAMPO_ID_ARTICULO, TablaArticulo.KEY_CAMPO_CODIGO_ARTICULO, TablaArticulo.CAMPO_NOMBRE, TablaArticulo.CAMPO_ES_KG, TablaArticulo.CAMPO_ES_CONGELADO, TablaArticulo.CAMPO_TARIFA_DEFECTO, TablaArticulo.CAMPO_FECHA_CAMBIO_TARIFA_DEFECTO},
                TablaArticulo.KEY_CAMPO_CODIGO_ARTICULO + " NOT IN (SELECT " + TablaRutero.KEY_CAMPO_CODIGO_ARTICULO + " FROM " + TablaRutero.NOMBRE_TABLA + " WHERE " + TablaRutero.KEY_CAMPO_ID_CLIENTE + " = " + idCliente + ")",
                null, null, null, TablaArticulo.CAMPO_NOMBRE);
    }

    /**
     * Recupera todos los articulos de la tabla de articulos
     *
     * @return Cursor
     */
    public Cursor obtenerTodosLosArticulos() {
        return db.query(TablaArticulo.NOMBRE_TABLA,
                new String[]{TablaArticulo.KEY_CAMPO_ID_ARTICULO, TablaArticulo.KEY_CAMPO_CODIGO_ARTICULO, TablaArticulo.CAMPO_NOMBRE, TablaArticulo.CAMPO_ES_KG, TablaArticulo.CAMPO_ES_CONGELADO, TablaArticulo.CAMPO_TARIFA_DEFECTO, TablaArticulo.CAMPO_FECHA_CAMBIO_TARIFA_DEFECTO},
                null, null, null, null, null);
    }

    /**
     * Recupera un articulo en concreto
     *
     * @param codigo del articulo a recuperar
     * @return Cursor
     * @throws SQLException
     */
    public Cursor obtenerArticulo(final String codigo) throws SQLException {
        final Cursor mCursor = db.query(true, TablaArticulo.NOMBRE_TABLA,
                new String[]{TablaArticulo.KEY_CAMPO_ID_ARTICULO, TablaArticulo.KEY_CAMPO_CODIGO_ARTICULO, TablaArticulo.CAMPO_NOMBRE, TablaArticulo.CAMPO_ES_KG, TablaArticulo.CAMPO_ES_CONGELADO, TablaArticulo.CAMPO_TARIFA_DEFECTO, TablaArticulo.CAMPO_FECHA_CAMBIO_TARIFA_DEFECTO},
                TablaArticulo.KEY_CAMPO_CODIGO_ARTICULO + " like '" + codigo + "'",
                null, null, null, null, null);
        if (null != mCursor) {
            mCursor.moveToFirst();
        }

        return mCursor;
    }

    /**
     * Actualiza los datos de un articulo en concreto
     *
     * @param codigo                   d
     * @param nombre                   d
     * @param esKg                     d
     * @param esCongelado              d
     * @param tarifaDefecto            d
     * @param fechaCambioTarifaDefecto d
     * @return true si los datos se han actualizado correctamente, o false en caso contrario
     */
    public boolean actualizarArticulo(final String codigo, final String nombre, final boolean esKg, final boolean esCongelado, final float tarifaDefecto, final String fechaCambioTarifaDefecto) {
        final ContentValues args = new ContentValues();

        args.put(TablaArticulo.KEY_CAMPO_CODIGO_ARTICULO, codigo);
        args.put(TablaArticulo.CAMPO_NOMBRE, nombre);
        args.put(TablaArticulo.CAMPO_ES_KG, esKg);
        args.put(TablaArticulo.CAMPO_ES_CONGELADO, esCongelado);
        args.put(TablaArticulo.CAMPO_TARIFA_DEFECTO, tarifaDefecto);
        args.put(TablaArticulo.CAMPO_FECHA_CAMBIO_TARIFA_DEFECTO, fechaCambioTarifaDefecto);

        return 0 < db.update(TablaArticulo.NOMBRE_TABLA, args, TablaArticulo.KEY_CAMPO_CODIGO_ARTICULO + " like '" + codigo + "'", null);
    }

    /**
     * Inserta un cliente en la tabla de clientes.
     *
     * @param id          d
     * @param nombre      d
     * @param descripcion d
     * @param telefono    d
     */
    public void insertarCliente(final int id, final String nombre, final String descripcion, final String telefono) {
        final ContentValues valoresIniciales = new ContentValues();

        valoresIniciales.put(TablaCliente.KEY_CAMPO_ID_CLIENTE, id);
        valoresIniciales.put(TablaCliente.CAMPO_NOMBRE_CLIENTE, nombre);
        valoresIniciales.put(TablaCliente.CAMPO_DESCRIPCION, descripcion);
        valoresIniciales.put(TablaCliente.CAMPO_TELEFONO, telefono);
        db.insert(TablaCliente.NOMBRE_TABLA, null, valoresIniciales);
    }

    ///////////////////////////////////////////
    //
    //   TABLA CLIENTE
    //
    ///////////////////////////////////////////

    /**
     * Borra un cliente en concreto de la tabla de clientes
     *
     * @param id del cliente a borrar
     * @return true si ha ido bien o false en caso de error
     */
    public boolean borrarCliente(final int id) {
        return 0 < db.delete(TablaCliente.NOMBRE_TABLA, TablaCliente.KEY_CAMPO_ID_CLIENTE + " = " + id, null);
    }

    /**
     * Borra todos los clientes de la tabla cliente
     */
    public void borrarTodosLosClientes() {
        db.delete(TablaCliente.NOMBRE_TABLA, null, null);
    }

    /**
     * Recupera todos los clientes de la tabla de clientes
     *
     * @return Cursor
     */
    public Cursor obtenerTodosLosClientes() {
        return db.query(TablaCliente.NOMBRE_TABLA,
                new String[]{TablaCliente.KEY_CAMPO_ID_CLIENTE, TablaCliente.CAMPO_NOMBRE_CLIENTE, TablaCliente.CAMPO_DESCRIPCION, TablaCliente.CAMPO_TELEFONO},
                null, null, null, null, TablaCliente.CAMPO_NOMBRE_CLIENTE);
    }

    /**
     * Recupera un cliente en concreto
     *
     * @param id del cliente a recuperar
     * @return Cursor
     * @throws SQLException
     */
    public Cursor obtenerCliente(final int id) throws SQLException {
        final Cursor mCursor = db.query(true, TablaCliente.NOMBRE_TABLA,
                new String[]{TablaCliente.KEY_CAMPO_ID_CLIENTE, TablaCliente.CAMPO_NOMBRE_CLIENTE, TablaCliente.CAMPO_DESCRIPCION, TablaCliente.CAMPO_TELEFONO},
                TablaCliente.KEY_CAMPO_ID_CLIENTE + " = " + id,
                null, null, null, null, null);
        if (null != mCursor) {
            mCursor.moveToFirst();
        }

        return mCursor;
    }

    /**
     * Actualiza los datos de un cliente en concreto
     *
     * @param id          d
     * @param nombre      d
     * @param descripcion d
     * @param telefono    d
     * @return true si los datos se han actualizado correctamente, o false en caso contrario
     */
    public boolean actualizarCliente(final int id, final String nombre, final String descripcion, final String telefono) {
        final ContentValues args = new ContentValues();

        args.put(TablaCliente.KEY_CAMPO_ID_CLIENTE, id);
        args.put(TablaCliente.CAMPO_NOMBRE_CLIENTE, nombre);
        args.put(TablaCliente.CAMPO_DESCRIPCION, descripcion);
        args.put(TablaCliente.CAMPO_TELEFONO, telefono);

        return 0 < db.update(TablaCliente.NOMBRE_TABLA, args, TablaCliente.KEY_CAMPO_ID_CLIENTE + " = " + id, null);
    }

    /**
     * Guarda los datos de un prepedido en la BD, para ello primero lo intenta actualizar y si falla hace un insert de los datos.
     *
     * @param idPrepedido        d
     * @param idCliente          d
     * @param fechaEntrega       d
     * @param fechaPrepedido     d d
     * @param observaciones      d d
     * @param fijarObservaciones d
     * @param descuentoEspecial  d
     * @return true si los datos se han guardado correctamente o false en caso contrario.
     */
    public boolean guardaPrepedido(final int idPrepedido, final int idCliente, final String fechaEntrega, final String fechaPrepedido, final String observaciones, final boolean fijarObservaciones, final int descuentoEspecial) {
        boolean resultado = actualizarPrepedido(idPrepedido, idCliente, fechaEntrega, fechaPrepedido, observaciones, fijarObservaciones, descuentoEspecial);

        if (!resultado) {
            resultado = insertarPrepedido(idPrepedido, idCliente, fechaEntrega, fechaPrepedido, observaciones, fijarObservaciones, descuentoEspecial);
        }

        return resultado;
    }

    ///////////////////////////////////////////
    //
    //   TABLA PREPEDIDO
    //
    ///////////////////////////////////////////

    /**
     * Inserta un prepedido.
     *
     * @param idPrepedido        id                 pepedido
     * @param idCliente          id                 cliente
     * @param fechaEntrega       fecha              entrega
     * @param fechaPrepedido     fecha              prepedid
     * @param observaciones      String
     * @param fijarObservaciones boolean
     * @param descuentoEspecial  int
     * @return true si ha ido bien, o false en caso de error
     */
    private boolean insertarPrepedido(final int idPrepedido, final int idCliente, final String fechaEntrega, final String fechaPrepedido, final String observaciones, final boolean fijarObservaciones, final int descuentoEspecial) {
        final ContentValues valoresIniciales = new ContentValues();

        valoresIniciales.put(TablaPrepedido.KEY_CAMPO_ID_PREPEDIDO, idPrepedido);
        valoresIniciales.put(TablaPrepedido.CAMPO_ID_CLIENTE, idCliente);
        valoresIniciales.put(TablaPrepedido.CAMPO_FECHA_ENTREGA, fechaEntrega);
        valoresIniciales.put(TablaPrepedido.CAMPO_FECHA_PREPEDIDO, fechaPrepedido);
        valoresIniciales.put(TablaPrepedido.CAMPO_OBSERVACIONES, observaciones);
        valoresIniciales.put(TablaPrepedido.CAMPO_FIJAR_OBSERVACIONES, fijarObservaciones);
        valoresIniciales.put(TablaPrepedido.CAMPO_DESCUENTO_ESPECIAL, descuentoEspecial);

        return -1 != db.insert(TablaPrepedido.NOMBRE_TABLA, null, valoresIniciales);
    }

    /**
     * Borra un prepedido en concreto
     *
     * @param id del prepedido a borrar
     * @return true si ha ido bien o false en caso de error
     */
    public boolean borrarPrepedido(final int id) {
        return 0 < db.delete(TablaPrepedido.NOMBRE_TABLA, TablaPrepedido.KEY_CAMPO_ID_PREPEDIDO + " = " + id, null);
    }

    /**
     * Borra un prepedido en concreto con sus linea de prepedido
     *
     * @param id del prepedido a borrar
     * @return true si ha ido bien o false en caso de error
     */
    public boolean borrarPrepedidoConLosPrepedidosItem(final int id) {
        final boolean borrarPrepedidosItem;
        final boolean borrarPrepedido;

        borrarPrepedidosItem = borrarTodosLosPrepedidoItemDelPrepedido(id);

        borrarPrepedido = 0 < db.delete(TablaPrepedido.NOMBRE_TABLA, TablaPrepedido.KEY_CAMPO_ID_PREPEDIDO + " = " + id, null);

        return borrarPrepedidosItem && borrarPrepedido;
    }

    /**
     * Recupera todos los prepedidos de la tabla de prepedido
     *
     * @return Cursor
     */
    public Cursor obtenerTodosLosPrepedidos() {
        return db.query(TablaPrepedido.NOMBRE_TABLA,
                new String[]{TablaPrepedido.KEY_CAMPO_ID_PREPEDIDO, TablaPrepedido.CAMPO_ID_CLIENTE, TablaPrepedido.CAMPO_FECHA_ENTREGA,
                        TablaPrepedido.CAMPO_FECHA_PREPEDIDO, TablaPrepedido.CAMPO_OBSERVACIONES, TablaPrepedido.CAMPO_FIJAR_OBSERVACIONES, TablaPrepedido.CAMPO_DESCUENTO_ESPECIAL},
                null, null, null, null, null);
    }

    /**
     * Recupera todos los prepedidos de la tabla de prepedido para un cliente
     * <p/>
     * id cliente de los prepedidos que se quiere obtener
     *
     * @return Cursor
     */
    public Cursor obtenerTodosLosPrepedidosDelCliente(final int idCliente) {
        return db.query(TablaPrepedido.NOMBRE_TABLA,
                new String[]{TablaPrepedido.KEY_CAMPO_ID_PREPEDIDO, TablaPrepedido.CAMPO_ID_CLIENTE, TablaPrepedido.CAMPO_FECHA_ENTREGA,
                        TablaPrepedido.CAMPO_FECHA_PREPEDIDO, TablaPrepedido.CAMPO_OBSERVACIONES, TablaPrepedido.CAMPO_FIJAR_OBSERVACIONES, TablaPrepedido.CAMPO_DESCUENTO_ESPECIAL},
                TablaPrepedido.CAMPO_ID_CLIENTE + " = " + idCliente,
                null, null, null, null, null);
    }

    /**
     * Recupera un prepedido en concreto
     *
     * @param idPrepedido codigo del prepedido a recuperar
     * @return Cursor
     * @throws SQLException
     */
    public Cursor obtenerPrepedido(final int idPrepedido) throws SQLException {
        final Cursor mCursor = db.query(true, TablaPrepedido.NOMBRE_TABLA,
                new String[]{TablaPrepedido.KEY_CAMPO_ID_PREPEDIDO, TablaPrepedido.CAMPO_ID_CLIENTE, TablaPrepedido.CAMPO_FECHA_ENTREGA,
                        TablaPrepedido.CAMPO_FECHA_PREPEDIDO, TablaPrepedido.CAMPO_OBSERVACIONES, TablaPrepedido.CAMPO_FIJAR_OBSERVACIONES, TablaPrepedido.CAMPO_DESCUENTO_ESPECIAL},
                TablaPrepedido.KEY_CAMPO_ID_PREPEDIDO + " = " + idPrepedido,
                null, null, null, null, null);
        if (null != mCursor) {
            mCursor.moveToFirst();
        }

        return mCursor;
    }

    /**
     * Recupera todos los prepedidos de la tabla de prepedido y los datos de los clientes asociados
     *
     * @return Cursor
     */
    public Cursor obtenerTodosLosPrepedidosConDatosCliente() {
        //La select, devolvera 8 campos 5 de la tabla prepedido + 3 de la tabla cliente
        final String select = "SELECT * FROM " + TablaPrepedido.NOMBRE_TABLA + " p INNER JOIN " + TablaCliente.NOMBRE_TABLA + " c ON p." + TablaPrepedido.CAMPO_ID_CLIENTE + "=c." + TablaCliente.KEY_CAMPO_ID_CLIENTE + " ORDER BY p." + TablaPrepedido.CAMPO_FECHA_PREPEDIDO + ", p." + TablaPrepedido.KEY_CAMPO_ID_PREPEDIDO;

        return db.rawQuery(select, new String[]{});
    }

    /**
     * Recupera todos los prepedidos de la tabla de prepedido para un cliente y los datos de los clientes asociados
     * <p/>
     * id cliente de los prepedidos que se quiere obtener
     *
     * @return Cursor
     */
    public Cursor obtenerTodosLosPrepedidosDelClienteConDatosCliente(final int idCliente) {
        final String select = "SELECT * FROM " + TablaPrepedido.NOMBRE_TABLA + " p INNER JOIN " + TablaCliente.NOMBRE_TABLA + " c ON p." + TablaPrepedido.CAMPO_ID_CLIENTE + "=c." + TablaCliente.KEY_CAMPO_ID_CLIENTE + " WHERE p." + TablaPrepedido.CAMPO_ID_CLIENTE + "=? ORDER BY p." + TablaPrepedido.CAMPO_FECHA_PREPEDIDO + ", p." + TablaPrepedido.KEY_CAMPO_ID_PREPEDIDO;

        return db.rawQuery(select, new String[]{String.valueOf(idCliente)});
    }

    /**
     * Recupera un prepedido en concreto y los datos de los clientes asociados
     *
     * @param idPrepedido codigo del prepedido a recuperar
     * @return Cursor
     * @throws SQLException
     */
    public Cursor obtenerPrepedidoConDatosCliente(final int idPrepedido) throws SQLException {
        final String select = "SELECT * FROM " + TablaPrepedido.NOMBRE_TABLA + " p INNER JOIN " + TablaCliente.NOMBRE_TABLA + " c ON p." + TablaPrepedido.CAMPO_ID_CLIENTE + "=c." + TablaCliente.KEY_CAMPO_ID_CLIENTE + " WHERE p." + TablaPrepedido.KEY_CAMPO_ID_PREPEDIDO + "=?";

        return db.rawQuery(select, new String[]{String.valueOf(idPrepedido)});
    }

    /**
     * Actualiza los datos de un prepedido en concreto
     *
     * @param idPrepedido        id                 prepedido
     * @param idCliente          id                 cliente
     * @param fechaEntrega       fecha              entrega
     * @param fechaPrepedido     fecha              prepedido
     * @param observaciones      String
     * @param fijarObservaciones boolean
     * @param descuentoEspecial  int
     * @return true si los datos se han actualizado correctamente, o false en caso contrario
     */
    private boolean actualizarPrepedido(final int idPrepedido, final int idCliente, final String fechaEntrega, final String fechaPrepedido, final String observaciones, final boolean fijarObservaciones, final int descuentoEspecial) {
        final ContentValues args = new ContentValues();

        args.put(TablaPrepedido.KEY_CAMPO_ID_PREPEDIDO, idPrepedido);
        args.put(TablaPrepedido.CAMPO_ID_CLIENTE, idCliente);
        args.put(TablaPrepedido.CAMPO_FECHA_ENTREGA, fechaEntrega);
        args.put(TablaPrepedido.CAMPO_FECHA_PREPEDIDO, fechaPrepedido);
        args.put(TablaPrepedido.CAMPO_OBSERVACIONES, observaciones);
        args.put(TablaPrepedido.CAMPO_FIJAR_OBSERVACIONES, fijarObservaciones);
        args.put(TablaPrepedido.CAMPO_DESCUENTO_ESPECIAL, descuentoEspecial);

        return 0 < db.update(TablaPrepedido.NOMBRE_TABLA, args, TablaPrepedido.KEY_CAMPO_ID_PREPEDIDO + " = " + idPrepedido, null);
    }

    /**
     * Guardamos en la BD un prepedido Item (linea de pedido), para ello hacemos primero un update y si da un error hacemos un insert.
     *
     * @param idPrepedido        d
     * @param codArticulo        d
     * @param cantidadKg         d
     * @param cantidadUd         d
     * @param precio             d
     * @param observaciones      d
     * @param fijarPrecio        d
     * @param suprimirPrecio     d
     * @param fijarArticulo      d
     * @param fijarObservaciones d
     * @return true si se ha guardado correctamente y false en caso contrario.
     */
    public boolean guardaPrepedidoItem(final int idPrepedido, final int idArticulo, final String codArticulo,
                                       final float cantidadKg, final int cantidadUd, final float precio, final String observaciones, final boolean fijarPrecio, final boolean suprimirPrecio, final boolean fijarArticulo, final boolean fijarObservaciones) {
        boolean resultado = actualizarPrepedidoItem(idPrepedido, idArticulo, codArticulo, cantidadKg, cantidadUd, precio, observaciones, fijarPrecio, suprimirPrecio, fijarArticulo, fijarObservaciones);

        if (!resultado) {
            resultado = insertarPrepedidoItem(idPrepedido, idArticulo, codArticulo, cantidadKg, cantidadUd, precio, observaciones, fijarPrecio, suprimirPrecio, fijarArticulo, fijarObservaciones);
        }

        return resultado;
    }

    ///////////////////////////////////////////
    //
    //   TABLA PREPEDIDO ITEM
    //
    ///////////////////////////////////////////

    /**
     * Inserta un prepedido item.
     *
     * @param idPrepedido        id            prepedido
     * @param codArticulo        codigo        articulo
     * @param cantidadKg         kg
     * @param cantidadUd         ud
     * @param precio             precio
     * @param observaciones      observaciones
     * @param fijarPrecio        fijar         tarifa
     * @param suprimirPrecio     suprimir      tarifa
     * @param fijarArticulo      fijar         articulo
     * @param fijarObservaciones fijar         observaciones
     * @return true si ha ido bien, o false en caso de error
     */
    private boolean insertarPrepedidoItem(final int idPrepedido, final int idArticulo, final String codArticulo,
                                          final float cantidadKg, final int cantidadUd, final float precio, final String observaciones, final boolean fijarPrecio, final boolean suprimirPrecio, final boolean fijarArticulo, final boolean fijarObservaciones) {
        final ContentValues valoresIniciales = new ContentValues();

        valoresIniciales.put(TablaPrepedidoItem.KEY_CAMPO_ID_PREPEDIDO, idPrepedido);
        valoresIniciales.put(TablaPrepedidoItem.KEY_CAMPO_ID_ARTICULO, idArticulo);
        valoresIniciales.put(TablaPrepedidoItem.KEY_CAMPO_CODIGO_ARTICULO, codArticulo);
        valoresIniciales.put(TablaPrepedidoItem.CAMPO_CANTIDAD_KG, cantidadKg);
        valoresIniciales.put(TablaPrepedidoItem.CAMPO_CANTIDAD_UD, cantidadUd);
        valoresIniciales.put(TablaPrepedidoItem.CAMPO_PRECIO, precio);
        valoresIniciales.put(TablaPrepedidoItem.CAMPO_OBSERVACIONES, observaciones);
        valoresIniciales.put(TablaPrepedidoItem.CAMPO_FIJAR_PRECIO, fijarPrecio);
        valoresIniciales.put(TablaPrepedidoItem.CAMPO_SUPRIMIR_PRECIO, suprimirPrecio);
        valoresIniciales.put(TablaPrepedidoItem.CAMPO_FIJAR_ARTICULO, fijarArticulo);
        valoresIniciales.put(TablaPrepedidoItem.CAMPO_FIJAR_OBSERVACIONES, fijarObservaciones);

        return -1 != db.insert(TablaPrepedidoItem.NOMBRE_TABLA, null, valoresIniciales);
    }

    /**
     * Borra un prepedido item en concreto
     *
     * @param idPrepedido id     del prepedido a borrar
     * @param codArticulo codigo articulo a borrar
     */
    public void borrarPrepedidoItem(final int idPrepedido, final String codArticulo) {
        db.delete(TablaPrepedidoItem.NOMBRE_TABLA,
                TablaPrepedidoItem.KEY_CAMPO_ID_PREPEDIDO + " = " + idPrepedido + " and " + TablaPrepedidoItem.KEY_CAMPO_CODIGO_ARTICULO + " like '" + codArticulo + "'",
                null);
    }

    /**
     * Borra todos los prepedidos item de un prepedido en concreto
     *
     * @param idPrepedido id del prepedido cuyos prepedidos item queremos borrar
     * @return true si ha ido bien o false en caso de error
     */
    private boolean borrarTodosLosPrepedidoItemDelPrepedido(final int idPrepedido) {
        boolean resultado = true;

        //Antes de borrar comprobamos si existen lineas de prepedido para el pedido, ya que sino se interpretaria como un error por devolver false
        if (0 < obtenerTodosLosPrepedidosItemDelPrepedido(idPrepedido).getCount()) {
            resultado = 0 < db.delete(TablaPrepedidoItem.NOMBRE_TABLA,
                    TablaPrepedidoItem.KEY_CAMPO_ID_PREPEDIDO + " = " + idPrepedido,
                    null);
        }

        return resultado;
    }

    /**
     * Recupera todos los prepedidos item
     *
     * @return Cursor
     */
    public Cursor obtenerTodosLosPrepedidosItem() {
        return db.query(TablaPrepedidoItem.NOMBRE_TABLA,
                new String[]{TablaPrepedidoItem.KEY_CAMPO_ID_PREPEDIDO, TablaPrepedidoItem.KEY_CAMPO_ID_ARTICULO,TablaPrepedidoItem.KEY_CAMPO_CODIGO_ARTICULO,
                        TablaPrepedidoItem.CAMPO_CANTIDAD_KG, TablaPrepedidoItem.CAMPO_CANTIDAD_UD, TablaPrepedidoItem.CAMPO_PRECIO,
                        TablaPrepedidoItem.CAMPO_OBSERVACIONES, TablaPrepedidoItem.CAMPO_FIJAR_PRECIO, TablaPrepedidoItem.CAMPO_SUPRIMIR_PRECIO, TablaPrepedidoItem.CAMPO_FIJAR_ARTICULO, TablaPrepedidoItem.CAMPO_FIJAR_OBSERVACIONES},
                null, null, null, null, null);
    }

    /**
     * Recupera todos los prepedidos item de un prepedido
     *
     * @param idPrepedido predido cusos prepedido item se desea obtener
     * @return Cursor
     */
    public Cursor obtenerTodosLosPrepedidosItemDelPrepedido(final int idPrepedido) {
        return db.query(TablaPrepedidoItem.NOMBRE_TABLA,
                new String[]{TablaPrepedidoItem.KEY_CAMPO_ID_PREPEDIDO, TablaPrepedidoItem.KEY_CAMPO_ID_ARTICULO, TablaPrepedidoItem.KEY_CAMPO_CODIGO_ARTICULO,
                        TablaPrepedidoItem.CAMPO_CANTIDAD_KG, TablaPrepedidoItem.CAMPO_CANTIDAD_UD, TablaPrepedidoItem.CAMPO_PRECIO,
                        TablaPrepedidoItem.CAMPO_OBSERVACIONES, TablaPrepedidoItem.CAMPO_FIJAR_PRECIO, TablaPrepedidoItem.CAMPO_SUPRIMIR_PRECIO, TablaPrepedidoItem.CAMPO_FIJAR_ARTICULO, TablaPrepedidoItem.CAMPO_FIJAR_OBSERVACIONES},
                TablaPrepedidoItem.KEY_CAMPO_ID_PREPEDIDO + " = " + idPrepedido,
                null, null, null, null);
    }

    /**
     * Recupera un prepedido item en concreto
     *
     * @param idPrepedido id     del prepedido a recuperar
     * @param codArticulo codigo del articulo a borrar
     * @return Cursor
     * @throws SQLException
     */
    public Cursor obtenerPrepedidoItem(final int idPrepedido, final String codArticulo) throws SQLException {
        final Cursor mCursor = db.query(true, TablaPrepedidoItem.NOMBRE_TABLA,
                new String[]{TablaPrepedidoItem.KEY_CAMPO_ID_PREPEDIDO, TablaPrepedidoItem.KEY_CAMPO_ID_ARTICULO, TablaPrepedidoItem.KEY_CAMPO_CODIGO_ARTICULO,
                        TablaPrepedidoItem.CAMPO_CANTIDAD_KG, TablaPrepedidoItem.CAMPO_CANTIDAD_UD, TablaPrepedidoItem.CAMPO_PRECIO,
                        TablaPrepedidoItem.CAMPO_OBSERVACIONES, TablaPrepedidoItem.CAMPO_FIJAR_PRECIO, TablaPrepedidoItem.CAMPO_SUPRIMIR_PRECIO, TablaPrepedidoItem.CAMPO_FIJAR_ARTICULO, TablaPrepedidoItem.CAMPO_FIJAR_OBSERVACIONES},
                TablaPrepedidoItem.KEY_CAMPO_ID_PREPEDIDO + " = " + idPrepedido + " and " + TablaPrepedidoItem.KEY_CAMPO_CODIGO_ARTICULO + " like '" + codArticulo + "'",
                null, null, null, null, null);
        if (null != mCursor) {
            mCursor.moveToFirst();
        }

        return mCursor;
    }

    /**
     * Actualiza los datos de un prepedido item en concreto
     *
     * @param idPrepedido        id            prepedido
     * @param codArticulo        codigo        articulo
     * @param cantidadKg         kg
     * @param cantidadUd         ud
     * @param precio             precio
     * @param observaciones      obs
     * @param fijarPrecio        fijar         tarifa
     * @param suprimirPrecio     suprimir      tarifa
     * @param fijarArticulo      fijar         articulo
     * @param fijarObservaciones fijar         observaciones
     * @return true si los datos se han actualizado correctamente, o false en caso contrario
     */
    private boolean actualizarPrepedidoItem(final int idPrepedido, final int idArticulo, final String codArticulo,
                                            final float cantidadKg, final int cantidadUd, final float precio, final String observaciones, final boolean fijarPrecio, final boolean suprimirPrecio, final boolean fijarArticulo, final boolean fijarObservaciones) {
        final ContentValues args = new ContentValues();

        args.put(TablaPrepedidoItem.KEY_CAMPO_ID_PREPEDIDO, idPrepedido);
        args.put(TablaPrepedidoItem.KEY_CAMPO_ID_ARTICULO, idArticulo);
        args.put(TablaPrepedidoItem.KEY_CAMPO_CODIGO_ARTICULO, codArticulo);
        args.put(TablaPrepedidoItem.CAMPO_CANTIDAD_KG, cantidadKg);
        args.put(TablaPrepedidoItem.CAMPO_CANTIDAD_UD, cantidadUd);
        args.put(TablaPrepedidoItem.CAMPO_PRECIO, precio);
        args.put(TablaPrepedidoItem.CAMPO_OBSERVACIONES, observaciones);
        args.put(TablaPrepedidoItem.CAMPO_FIJAR_PRECIO, fijarPrecio);
        args.put(TablaPrepedidoItem.CAMPO_SUPRIMIR_PRECIO, suprimirPrecio);
        args.put(TablaPrepedidoItem.CAMPO_FIJAR_ARTICULO, fijarArticulo);
        args.put(TablaPrepedidoItem.CAMPO_FIJAR_OBSERVACIONES, fijarObservaciones);

        return 0 < db.update(TablaPrepedidoItem.NOMBRE_TABLA, args,
                TablaPrepedidoItem.KEY_CAMPO_ID_PREPEDIDO + " = " + idPrepedido + " and " + TablaPrepedidoItem.KEY_CAMPO_CODIGO_ARTICULO + " like '" + codArticulo + "'",
                null);
    }

    /**
     * Inserta un rutero.
     *
     * @param codArticulo       codigo        articulo
     * @param idCliente         id            cliente
     * @param ultimaFecha       fecha         ultima compra
     * @param ultimaUnidades    unidades      ultima compra
     * @param ultimaCantidad    tarifa        cliente
     * @param unidadesTotalAnio unidades      total_anio
     * @param cantidadTotalAnio cantidad      ultima compra
     * @param ultimaTarifa      tarifa        ultima compra
     * @param tarifaCliente     cantidad      total_anio
     * @param observaciones     s
     */
    public String insertarRutero(final int idArticulo, final String codArticulo, final int idCliente,
                                 final String ultimaFecha, final int ultimaUnidades, final double ultimaCantidad, final int unidadesTotalAnio, final double cantidadTotalAnio, final double ultimaTarifa,
                                 final double tarifaCliente, final String observaciones) {
        final ContentValues valoresIniciales = new ContentValues();
        valoresIniciales.put(TablaRutero.KEY_CAMPO_ID_ARTICULO, idArticulo);
        valoresIniciales.put(TablaRutero.KEY_CAMPO_CODIGO_ARTICULO, codArticulo);
        valoresIniciales.put(TablaRutero.KEY_CAMPO_ID_CLIENTE, idCliente);
        valoresIniciales.put(TablaRutero.CAMPO_FECHA_ULTIMA_COMPRA, ultimaFecha);
        valoresIniciales.put(TablaRutero.CAMPO_UNIDADES_ULTIMA_COMPRA, ultimaUnidades);
        valoresIniciales.put(TablaRutero.CAMPO_CANTIDAD_ULTIMA_COMPRA, ultimaCantidad);
        valoresIniciales.put(TablaRutero.CAMPO_UNIDADES_TOTAL_ANIO, unidadesTotalAnio);
        valoresIniciales.put(TablaRutero.CAMPO_CANTIDAD_TOTAL_ANIO, cantidadTotalAnio);
        valoresIniciales.put(TablaRutero.CAMPO_TARIFA_ULTIMA_COMPRA, ultimaTarifa);
        valoresIniciales.put(TablaRutero.CAMPO_TARIFA_CLIENTE, tarifaCliente);
        valoresIniciales.put(TablaRutero.CAMPO_OBSERVACIONES, removeAcentos(observaciones).replaceAll("[^\\dA-Za-z]", ""));
        try {
            db.insert(TablaRutero.NOMBRE_TABLA, null, valoresIniciales);
        } catch (SQLException e) {
            return e.getMessage();
        }
        return "ok";
    }


    ///////////////////////////////////////////
    //
    //   TABLA RUTERO
    //
    ///////////////////////////////////////////

    /**
     * Borra un rutero en concreto
     *
     * @param codArticulo codigo articulo
     * @param idCliente   id     cliente
     * @return true si ha ido bien o false en caso de error
     */
    public boolean borrarRutero(final String codArticulo, final int idCliente) {
        return 0 < db.delete(TablaRutero.NOMBRE_TABLA, TablaRutero.KEY_CAMPO_CODIGO_ARTICULO + " like '" + codArticulo + "' and " +
                TablaRutero.KEY_CAMPO_ID_CLIENTE + " = " + idCliente, null);
    }

    /**
     * Borra todos los ruteros
     */
    public void borrarTodosLosRuteros() {
        db.delete(TablaRutero.NOMBRE_TABLA, null, null);
    }

    /**
     * Recupera todos los ruteros
     *
     * @return Cursor
     */
    public Cursor obtenerTodosLosRuteros() {
        return db.query(TablaRutero.NOMBRE_TABLA,
                new String[]{TablaRutero.KEY_CAMPO_CODIGO_ARTICULO, TablaRutero.KEY_CAMPO_ID_CLIENTE,
                        TablaRutero.CAMPO_FECHA_ULTIMA_COMPRA, TablaRutero.CAMPO_UNIDADES_ULTIMA_COMPRA, TablaRutero.CAMPO_CANTIDAD_ULTIMA_COMPRA, TablaRutero.CAMPO_UNIDADES_TOTAL_ANIO, TablaRutero.CAMPO_CANTIDAD_TOTAL_ANIO,
                        TablaRutero.CAMPO_TARIFA_ULTIMA_COMPRA, TablaRutero.CAMPO_TARIFA_CLIENTE, TablaRutero.CAMPO_OBSERVACIONES},
                null, null, null, null, null);
    }

    /**
     * Recupera todos los ruteros
     *
     * @return Cursor
     */
    public Cursor obtenerTodosLosRuterosSimples() {
        return db.query(TablaRutero.NOMBRE_TABLA,
                new String[]{TablaRutero.KEY_CAMPO_CODIGO_ARTICULO, TablaRutero.KEY_CAMPO_ID_CLIENTE},
                null, null, null, null, null);
    }

    /**
     * Recupera todos los ruteros de un cliente
     *
     * @param idCliente d
     * @return Cursor
     */
    public Cursor obtenerTodosLosRuterosDelClienteConArticulo(final int idCliente) {
        //La select, devolvera 12 campos 10 de la tabla rutero + 2 de la tabla articulo
        final String select = "SELECT * FROM " + TablaRutero.NOMBRE_TABLA + " r INNER JOIN " + TablaArticulo.NOMBRE_TABLA
                + " a ON r." + TablaRutero.KEY_CAMPO_CODIGO_ARTICULO + "=a." + TablaArticulo.KEY_CAMPO_CODIGO_ARTICULO + " WHERE r."
                + TablaRutero.KEY_CAMPO_ID_CLIENTE + "=? ORDER BY a." + TablaArticulo.CAMPO_NOMBRE + " ASC";

        return db.rawQuery(select, new String[]{String.valueOf(idCliente)});
    }

    /**
     * Recupera todos los ruteros de un cliente
     *
     * @param idCliente               d
     * @param statusLineaRuteroOculta status
     * @return Cursor
     */
    public Cursor obtenerTodosLosRuterosDelClienteConArticulo(final int idCliente, final int statusLineaRuteroOculta, String campoOrder, String order) {
        //La select, devolvera 12 campos 10 de la tabla rutero + 2 de la tabla articulo
        final String select = "SELECT * FROM " + TablaRutero.NOMBRE_TABLA + " r INNER JOIN " + TablaArticulo.NOMBRE_TABLA
                + " a ON r." + TablaRutero.KEY_CAMPO_CODIGO_ARTICULO + "=a." + TablaArticulo.KEY_CAMPO_CODIGO_ARTICULO + " WHERE r."
                + TablaRutero.KEY_CAMPO_ID_CLIENTE + "=? ORDER BY " + campoOrder + order;

        return db.rawQuery(select, new String[]{String.valueOf(idCliente)});
    }


    /**
     * Recupera un rutero en concreto
     *
     * @param codArticulo codigo articulo
     * @param idCliente   id     cliente
     * @return Cursor
     * @throws SQLException
     */
    public Cursor obtenerRutero(final String codArticulo, final int idCliente) throws SQLException {
        final Cursor mCursor = db.query(true, TablaRutero.NOMBRE_TABLA,
                new String[]{TablaRutero.KEY_CAMPO_CODIGO_ARTICULO, TablaRutero.KEY_CAMPO_ID_CLIENTE,
                        TablaRutero.CAMPO_FECHA_ULTIMA_COMPRA, TablaRutero.CAMPO_UNIDADES_ULTIMA_COMPRA, TablaRutero.CAMPO_CANTIDAD_ULTIMA_COMPRA,
                        TablaRutero.CAMPO_UNIDADES_TOTAL_ANIO, TablaRutero.CAMPO_CANTIDAD_TOTAL_ANIO, TablaRutero.CAMPO_TARIFA_ULTIMA_COMPRA,
                        TablaRutero.CAMPO_TARIFA_CLIENTE, TablaRutero.CAMPO_OBSERVACIONES},
                TablaRutero.KEY_CAMPO_CODIGO_ARTICULO + " like '" + codArticulo + "' and " + TablaRutero.KEY_CAMPO_ID_CLIENTE + " = " + idCliente,
                null, null, null, null, null);
        if (null != mCursor) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    /**
     * Actualiza los datos de un rutero en concreto
     *
     * @param observaciones d
     * @param status        d
     * @return true si los datos se han actualizado correctamente, o false en caso contrario
     */
    public boolean actualizarRutero(final String codArticulo, final int idCliente,
                                    final String ultimaFecha, final int ultimaUnidades, final double ultimaCantidad,
                                    final int unidadesTotalAnio, final double cantidadTotalAnio, final double ultimaTarifa,
                                    final double tarifaCliente, final String observaciones, final int status) {
        final ContentValues args = new ContentValues();

        args.put(TablaRutero.KEY_CAMPO_CODIGO_ARTICULO, codArticulo);
        args.put(TablaRutero.KEY_CAMPO_ID_CLIENTE, idCliente);
        args.put(TablaRutero.CAMPO_FECHA_ULTIMA_COMPRA, ultimaFecha);
        args.put(TablaRutero.CAMPO_UNIDADES_ULTIMA_COMPRA, ultimaUnidades);
        args.put(TablaRutero.CAMPO_CANTIDAD_ULTIMA_COMPRA, ultimaCantidad);
        args.put(TablaRutero.CAMPO_UNIDADES_TOTAL_ANIO, unidadesTotalAnio);
        args.put(TablaRutero.CAMPO_CANTIDAD_TOTAL_ANIO, cantidadTotalAnio);
        args.put(TablaRutero.CAMPO_TARIFA_ULTIMA_COMPRA, ultimaTarifa);
        args.put(TablaRutero.CAMPO_TARIFA_CLIENTE, tarifaCliente);

        args.put(TablaRutero.CAMPO_OBSERVACIONES, removeAcentos(observaciones).replaceAll("[^\\dA-Za-z]", ""));

        return 0 < db.update(TablaRutero.NOMBRE_TABLA, args, TablaRutero.KEY_CAMPO_CODIGO_ARTICULO + " like '" + codArticulo + "' and " +
                TablaRutero.KEY_CAMPO_ID_CLIENTE + " = " + idCliente, null);
    }

    /**
     * Actualiza los datos de un rutero en concreto
     *
     * @return true si los datos se han actualizado correctamente, o false en caso contrario
     */
    public boolean actualizarRutero(final String codArticulo, final int idCliente, final int unidadesTotalAnio, final double cantidadTotalAnio, final double tarifaCliente) {

        final ContentValues args = new ContentValues();

        args.put(TablaRutero.KEY_CAMPO_CODIGO_ARTICULO, codArticulo);
        args.put(TablaRutero.KEY_CAMPO_ID_CLIENTE, idCliente);
        args.put(TablaRutero.CAMPO_UNIDADES_TOTAL_ANIO, unidadesTotalAnio);
        args.put(TablaRutero.CAMPO_CANTIDAD_TOTAL_ANIO, cantidadTotalAnio);
        args.put(TablaRutero.CAMPO_TARIFA_CLIENTE, tarifaCliente);

        return 0 < db.update(TablaRutero.NOMBRE_TABLA, args, TablaRutero.KEY_CAMPO_CODIGO_ARTICULO + " like '" + codArticulo + "' and " +
                TablaRutero.KEY_CAMPO_ID_CLIENTE + " = " + idCliente, null);
    }

    /**
     * Inserta una observacion en la tabla de observaciones.
     *
     * @param idObservacion d
     * @param tipo          d
     * @param descripcion   d
     */
    public void insertarObservacion(final int idObservacion, final int tipo, final String descripcion) {
        final ContentValues valoresIniciales = new ContentValues();

        valoresIniciales.put(TablaObservacion.KEY_CAMPO_ID_OBSERVACION, idObservacion);
        valoresIniciales.put(TablaObservacion.CAMPO_TIPO, tipo);
        valoresIniciales.put(TablaObservacion.CAMPO_DESCRIPCION, descripcion);
        db.insert(TablaObservacion.NOMBRE_TABLA, null, valoresIniciales);
    }

    ///////////////////////////////////////////
    //
    //   TABLA OBSERVACION
    //
    ///////////////////////////////////////////

    /**
     * Borra una observacion en concreto de la tabla de observaciones
     *
     * @param idObservacion codigo de la observacion a borrar
     * @return true si ha ido bien o false en caso de error
     */
    public boolean borrarObservacion(final int idObservacion) {
        return 0 < db.delete(TablaObservacion.NOMBRE_TABLA, TablaObservacion.KEY_CAMPO_ID_OBSERVACION + " = " + idObservacion, null);
    }

    /**
     * Borra todas las observaciones
     */
    public void borrarTodasLasObservaciones() {
        db.delete(TablaObservacion.NOMBRE_TABLA, null, null);
    }

    /**
     * Recupera todas las observaciones de la tabla de observaciones que son de tipo prepedido
     *
     * @return Cursor
     */
    public Cursor obtenerTodasLasObservacionesPrepedido() {
        return db.query(TablaObservacion.NOMBRE_TABLA,
                new String[]{TablaObservacion.KEY_CAMPO_ID_OBSERVACION, TablaObservacion.CAMPO_TIPO, TablaObservacion.CAMPO_DESCRIPCION},
                TablaObservacion.CAMPO_TIPO + " = " + TablaObservacion.TIPO_PREPEDIDO,
                null, null, null, TablaObservacion.CAMPO_DESCRIPCION, null);
    }

    /**
     * Recupera todas las observaciones de la tabla de observaciones que son de tipo prepedido item
     *
     * @return Cursor
     */
    public Cursor obtenerTodasLasObservacionesPrepedidoItem() {
        return db.query(TablaObservacion.NOMBRE_TABLA,
                new String[]{TablaObservacion.KEY_CAMPO_ID_OBSERVACION, TablaObservacion.CAMPO_TIPO, TablaObservacion.CAMPO_DESCRIPCION},
                TablaObservacion.CAMPO_TIPO + " = " + TablaObservacion.TIPO_PREPEDIDO_ITEM,
                null, null, null, TablaObservacion.CAMPO_DESCRIPCION, null);
    }

    /**
     * Recupera una observacion en concreto
     *
     * @param idObservacion codigo de la observacion a recuperar
     * @return Cursor
     * @throws SQLException
     */
    public Cursor obtenerObservacion(final int idObservacion) throws SQLException {
        final Cursor mCursor = db.query(true, TablaObservacion.NOMBRE_TABLA,
                new String[]{TablaObservacion.KEY_CAMPO_ID_OBSERVACION, TablaObservacion.CAMPO_TIPO, TablaObservacion.CAMPO_DESCRIPCION},
                TablaObservacion.KEY_CAMPO_ID_OBSERVACION + " = " + idObservacion,
                null, null, null, null, null);
        if (null != mCursor) {
            mCursor.moveToFirst();
        }

        return mCursor;
    }

    /**
     * Actualiza los datos de un articulo en concreto
     *
     * @param idObservacion d
     * @param tipo          d d
     * @param descripcion   d
     * @return true si los datos se han actualizado correctamente, o false en caso contrario
     */
    public boolean actualizarObservacion(final int idObservacion, final int tipo, final String descripcion) {
        final ContentValues args = new ContentValues();

        args.put(TablaObservacion.KEY_CAMPO_ID_OBSERVACION, idObservacion);
        args.put(TablaObservacion.CAMPO_TIPO, tipo);
        args.put(TablaObservacion.CAMPO_DESCRIPCION, descripcion);

        return 0 < db.update(TablaObservacion.NOMBRE_TABLA, args, TablaObservacion.KEY_CAMPO_ID_OBSERVACION + " = " + idObservacion, null);
    }

    private void log(final String text) {
        Log.d("AdaptadorBD", text);
    }


    /**
     * Actualiza los datos de un parametro en concreto
     *
     * @param valor d
     */
    public void actualizarParametroConfiguracion(final String valor) {
        final ContentValues args = new ContentValues();
        args.put(TablaConfiguracion.KEY_CAMPO_NOMBRE_PARAMETRO, Configuracion.NOMBRE_PARAMETRO_ULTIMA_FECHA_SINCRONIZACION);
        args.put(TablaConfiguracion.CAMPO_VALOR, valor);
        db.update(TablaConfiguracion.NOMBRE_TABLA, args, TablaConfiguracion.KEY_CAMPO_NOMBRE_PARAMETRO + " like '" + Configuracion.NOMBRE_PARAMETRO_ULTIMA_FECHA_SINCRONIZACION + "'", null);
    }


    /**
     * Recupera un parametro en concreto
     *
     * @param nombre del parametro a recuperar
     * @return Cursor
     * @throws SQLException
     */
    public Cursor obtenerParametroConfiguracion(final String nombre) throws SQLException {
        final Cursor mCursor = db.query(true, TablaConfiguracion.NOMBRE_TABLA,
                new String[]{TablaConfiguracion.KEY_CAMPO_NOMBRE_PARAMETRO, TablaConfiguracion.CAMPO_VALOR, TablaConfiguracion.CAMPO_DESCRIPCION, TablaConfiguracion.CAMPO_ES_EDITABLE},
                TablaConfiguracion.KEY_CAMPO_NOMBRE_PARAMETRO + " like '" + nombre + "'",
                null, null, null, null, null);
        if (null != mCursor) {
            mCursor.moveToFirst();
        }

        return mCursor;
    }

    /**
     * Función que elimina acentos y caracteres especiales de
     * una cadena de texto.
     *
     * @param input
     * @return cadena de texto limpia de acentos y caracteres especiales.
     */
    public static String removeAcentos(String input) {
        // Cadena de caracteres original a sustituir.
        String original = "áàäéèëíìïóòöúùuñÁÀÄÉÈËÍÌÏÓÒÖÚÙÜÑçÇ";
        // Cadena de caracteres ASCII que reemplazarán los originales.
        String ascii = "aaaeeeiiiooouuunAAAEEEIIIOOOUUUNcC";
        String output = input;
        for (int i = 0; i < original.length(); i++) {
            // Reemplazamos los caracteres especiales.
            output = output.replace(original.charAt(i), ascii.charAt(i));
        }//for i
        return output;
    }//remove1
}