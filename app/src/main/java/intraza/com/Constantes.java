package intraza.com;

import java.text.DecimalFormat;

/**
 * Clase que define las constantes usadas en distintos puntos de la aplicacion
 *
 * @author JLZS
 */
@SuppressWarnings("ALL")
public class Constantes {
    // Indica el orden de los valores al mostrarlos en la tabla de rutero
    public static final int INTERVALO = 3000;
    public static final int INTERVALO_TARIFAS = 1000;
    public static final int COLUMNA_REFERENCIA_LP = 1;
    public static final int COLUMNA_ARTICULO_LP = 2;
    public static final int COLUMNA_FECHA_ULTIMO_LP = 3;
    public static final int COLUMNA_CANTIDAD_ULTIMO_LP = 4;
    public static final int COLUMNA_CANTIDAD_NUEVO_KG_LP = 5;
    public static final int COLUMNA_TARIFA_NUEVO_LP = 6;
    public static final int COLUMNA_TARIFA_LISTA_LP = 7;
    public static final int COLUMNA_DATOS_LP = 8;
    public static final int COLUMNA_DATOS_INICIALES_LP = 9;
    public static final int COLUMNAS_TOTALES_LP = 9;

    // Indica el orden de los valores al mostrarlos en la tabla de pedidos
    public static final int COLUMNA_ID_PEDIDO_P = 1;
    public static final int COLUMNA_CLIENTE_P = 2;
    public static final int COLUMNA_FECHA_PEDIDO_P = 3;
    public static final int COLUMNA_FECHA_ENTREGA_P = 4;
    public static final int COLUMNA_PRECIO_TOTAL_P = 5;
    public static final int COLUMNA_PENDIENTE_ENVIAR_P = 6;
    public static final int COLUMNA_SUPRIMIR_P = 7;
    public static final int COLUMNAS_TOTALES_P = 7;
    public static final int UNO = 1;
    // Para indicar el formato de los numeros
    public static final DecimalFormat formatearFloat2Decimales = FormatoNumeros.dameFormato2Decimales();
    public static final DecimalFormat formatearFloat3Decimales = FormatoNumeros.dameFormato3Decimales();

    public static final String EURO = " \u20AC";
    public static final String SEPARADOR_MEDIDA_TARIFA = " / ";
    public static final String SEPARADOR_FECHA = "-";
    public static final String MARCA_FIJAR_TARIFA = "*";
    public static final String MARCA_FIJAR_ARTICULO = "*";
    public static final String MARCA_CONGELADO = " (CONGELADO)";
    public static final String MARCA_TARIFA_DEFECTO_CAMBIADA_RECIENTEMENTE = "*";
    public static final String SEPARADOR_CANTIDAD_TOTAL_ANIO = " / ";
    public static final String MARCA_KILOGRAMOS = " Kg";
    public static final String MARCA_TONELADAS = " T";
    public static final String MARCA_UNIDADES = " xMil";

     // Cadena cuando que se muestra en los datos del nuevo pedido, cuando este no ha sido introducido
    public static final String DATOS_NUEVO_PEDIDO_SIN_INTRODUCIR = "????";

    // Indica el texto a mostrar en el boton de comentarios
    public static final String SIN_COMENTARIOS = "  +\n...";
    public static final String CON_COMENTARIOS = "\n...";

    // Usada en el dialogo/activity que pide los datos del nuevo pedido
    public static final String KILOGRAMOS = "KG";
    public static final String UNIDADES = "UD";

    // Para inicializar los datos de la linea de pedido, cuando no tenemos un pedido anterior
    public static final String SIN_FECHA_ANTERIOR_LINEA_PEDIDO = "---";

    // Usada para los botones de pedido
    public static final String PREFIJO_TEXTO_FECHA_PEDIDO = "FECHA PEDIDO: ";
    public static final String PREFIJO_TEXTO_ID_PEDIDO = "ID. PEDIDO: ";
    public static final String PREFIJO_TEXTO_PRECIO_TOTAL = "PRECIO TOTAL: ";
    public static final String PREFIJO_BOTON_FECHA_ENTREGA = "FECHA ENTREGA: ";
    public static final String PREFIJO_BOTON_CLIENTE = "";
    public static final String PASSWORD_CONFIG = "1";

    //Cuando se crea un articulo que es clon, se le a?ade esto seguido del numero de clon al codigo del articulo para diferenciarlo del articulo
    //del que ha sido clonado
    public static final String CARACTER_OBLIGATORIO_MARCA_CLON_CODIGO_ARTICULO = "_";
    public static final String MARCA_CLON_CODIGO_ARTICULO = Constantes.CARACTER_OBLIGATORIO_MARCA_CLON_CODIGO_ARTICULO + "clon" + Constantes.CARACTER_OBLIGATORIO_MARCA_CLON_CODIGO_ARTICULO;

}
