package intraza.com;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;


class FormatoNumeros {
    private static final char SIMBOLO_DECIMAL = '.';

    private static final String FORMATO_2_DECIMALES = "####.##";
    private static final String FORMATO_3_DECIMALES = "####.###";

    public static DecimalFormat dameFormato2Decimales() {
        final DecimalFormatSymbols simbolosDecimales = new DecimalFormatSymbols();
        simbolosDecimales.setDecimalSeparator(FormatoNumeros.SIMBOLO_DECIMAL);
        return new DecimalFormat(FormatoNumeros.FORMATO_2_DECIMALES, simbolosDecimales);
    }

    public static DecimalFormat dameFormato3Decimales() {
        final DecimalFormatSymbols simbolosDecimales = new DecimalFormatSymbols();
        simbolosDecimales.setDecimalSeparator(FormatoNumeros.SIMBOLO_DECIMAL);

        return new DecimalFormat(FormatoNumeros.FORMATO_3_DECIMALES, simbolosDecimales);
    }
}
