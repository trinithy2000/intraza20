package intraza.com.interfaz.task.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Properties;

import intraza.com.interfaz.datosDB.ParametroConfiguracionBD;

/**
 * Created by JuanIgnacio on 30/06/2015 on  22:37.
 */
public class AssetsPropertyReader {
    private Context context;
    private Properties properties;

    public AssetsPropertyReader(Context context) {
        this.context = context;
        properties = new Properties();
    }

    public Properties getProperties(String FileName) {
        try {
            FileInputStream fis = context.openFileInput(FileName);
            properties.load(fis);
            fis.close();
        } catch (IOException e) {
            try {
                AssetManager assetManager = context.getAssets();
                InputStream inputStream = assetManager.open(FileName);
                properties.load(inputStream);
                OutputStream output = context.openFileOutput(FileName, Context.MODE_PRIVATE);
                properties.store(output, null);
                output.close();
                inputStream.close();
            } catch (IOException es) {
                Log.e("AssetsPropertyReader", es.toString());
            }
        }
        return properties;
    }

    public void setProperties(String FileName, List<ParametroConfiguracionBD> lista) {


        try {
            OutputStream output = context.openFileOutput(FileName, Context.MODE_PRIVATE);

            Properties properties = new Properties();
            for (ParametroConfiguracionBD dato : lista) {
                properties.setProperty(dato.getNombre(), dato.getValor());
            }
            properties.store(output, null);
            output.close();
        } catch (IOException e) {
            Log.e("AssetsPropertyReader", e.toString());
        }
    }

    public void setProperty(String FileName, String value) {
        try {
            FileInputStream fis = context.openFileInput(FileName);
            properties.load(fis);
            OutputStream output = context.openFileOutput(FileName, Context.MODE_PRIVATE);
            properties.setProperty("ULTIMA_FECHA_SINCRONIZACION", value);
            properties.store(output, null);
            output.close();
        } catch (IOException e) {
            Log.e("AssetsPropertyReader", e.toString());
        }
    }


}
