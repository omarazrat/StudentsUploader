/*
 *  StudentsUploader
 *  Cargador de datos de estudiantes a formularios
 *  Este programa fue diseñado para la Santa Iglesia Gnostica Universal, por Nestor Arias -nestor_arias@hotmail.com
 */
package oa.variabilis.web.utils;

import java.util.ResourceBundle;
import lombok.Data;

/**
 * Asistente para archivos de propiedades
 * @author nesto
 */
@Data
public class RBHelper {
    private String prefix="";
    private static ResourceBundle rb = ResourceBundle.getBundle("global");
    private static RBHelper instance = new RBHelper();
    
    public RBHelper() {
    }
    public RBHelper(String prefix){
        this.prefix=prefix;
    }

    public final String getString(String key) {
        return rb.getString(prefix+key);
    }
    
    public static String sgetString(String key){
        return instance.getString(key);
    }
}
